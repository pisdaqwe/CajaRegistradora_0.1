package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.DbPool;
import dtoS.RegistrarVentaExtraRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaItemResultDTO;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;
import enums.MetodoPago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO encargado de registrar una venta completa en una única transacción.
 *
 * Este DAO inserta:
 * - VENTA
 * - VENTA_ITEM
 * - VENTA_ITEM_EXTRA
 * - PAGO
 * - TICKET_JSON
 *
 * IMPORTANTE:
 * - la conexión se abre aquí dentro con DbPool
 * - si falla algo, se hace rollback completo
 * - se usa NOW() en SQL para las fechas críticas
 */
public class VentaRegistroDao {

    private final ObjectMapper objectMapper;

    public VentaRegistroDao() {
        this.objectMapper = new ObjectMapper();
    }

    // =====================================================
    // MÉTODO PRINCIPAL
    // =====================================================

    /**
     * Registra una venta completa en una única transacción.
     */
    public RegistrarVentaResultDTO registrarVentaCompleta(RegistrarVentaRequest request) {
        try (Connection con = DbPool.getConnection()) {
            con.setAutoCommit(false);

            try {
                int idVenta = insertVenta(con, request);

                List<RegistrarVentaItemResultDTO> itemsPersistidos =
                        insertVentaItemsYExtras(con, idVenta, request.getItems());

                int idPago = insertPago(con, idVenta, request);

                int idTicketJson = insertTicketJson(con, idVenta, request);

                con.commit();

                RegistrarVentaResultDTO result = new RegistrarVentaResultDTO();
                result.setIdVenta(idVenta);
                result.setIdPago(idPago);
                result.setIdTicketJson(idTicketJson);
                result.setItemsPersistidos(itemsPersistidos);

                return result;

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Error registrando la venta completa.", e);
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir la conexión para registrar la venta.", e);
        }
    }

    // =====================================================
    // INSERT VENTA
    // =====================================================

    /**
     * Inserta la cabecera de la venta.
     *
     * Tabla: venta
     */
    private int insertVenta(Connection con, RegistrarVentaRequest request) throws SQLException {
        String sql = """
            INSERT INTO venta (
                id_sesion,
                id_usuario,
                fecha_venta,
                total,
                anulada
            ) VALUES (?, ?, NOW(), ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, request.getIdSesion());
            ps.setInt(2, request.getIdUsuario());
            ps.setBigDecimal(3, request.getTotal());
            ps.setBoolean(4, false);

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id de la venta insertada.");
        }
    }

    // =====================================================
    // INSERT ITEMS + EXTRAS
    // =====================================================

    /**
     * Inserta todos los items de la venta y sus extras.
     *
     * Además devuelve la lista de items ya guardados en BD
     * con sus ids reales de venta_item.
     */
    private List<RegistrarVentaItemResultDTO> insertVentaItemsYExtras(
            Connection con,
            int idVenta,
            List<RegistrarVentaItemRequest> items
    ) throws SQLException {

        List<RegistrarVentaItemResultDTO> itemsPersistidos = new ArrayList<>();

        if (items == null || items.isEmpty()) {
            return itemsPersistidos;
        }

        for (RegistrarVentaItemRequest item : items) {
            int idItem = insertVentaItem(con, idVenta, item);

            if (item.getExtras() != null && !item.getExtras().isEmpty()) {
                for (RegistrarVentaExtraRequest extra : item.getExtras()) {
                    insertVentaItemExtra(con, idItem, extra);
                }
            }

            RegistrarVentaItemResultDTO itemResult = new RegistrarVentaItemResultDTO();
            itemResult.setIdItem(idItem);
            itemResult.setIdProducto(item.getIdProducto());

            itemsPersistidos.add(itemResult);
        }

        return itemsPersistidos;
    }

    /**
     * Inserta un item en venta_item.
     */
    private int insertVentaItem(
            Connection con,
            int idVenta,
            RegistrarVentaItemRequest item
    ) throws SQLException {

        String sql = """
            INSERT INTO venta_item (
                id_venta,
                id_producto,
                cantidad,
                precio_unitario,
                subtotal,
                iva,
                descripcion_personalizacion
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, item.getIdProducto());
            ps.setInt(3, item.getCantidad());
            ps.setBigDecimal(4, item.getPrecioUnitario());
            ps.setBigDecimal(5, item.getSubtotal());
            ps.setBigDecimal(6, item.getIva());
            ps.setString(7, item.getDescripcionPersonalizacion());

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del item insertado.");
        }
    }

    /**
     * Inserta un extra en venta_item_extra.
     */
    private void insertVentaItemExtra(
            Connection con,
            int idItem,
            RegistrarVentaExtraRequest extra
    ) throws SQLException {

        String sql = """
            INSERT INTO venta_item_extra (
                id_item,
                id_extra,
                precio_extra,
                nombre_extra
            ) VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            ps.setInt(2, extra.getIdExtra());
            ps.setBigDecimal(3, extra.getPrecioExtra());
            ps.setString(4, extra.getNombreExtra());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // INSERT PAGO
    // =====================================================

    /**
     * Inserta el pago de la venta.
     *
     * Tabla: pago
     */
    private int insertPago(
            Connection con,
            int idVenta,
            RegistrarVentaRequest request
    ) throws SQLException {

        String sql = """
            INSERT INTO pago (
                id_venta,
                metodo,
                monto,
                fecha_pago
            ) VALUES (?, ?, ?, NOW())
        """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setString(2, mapMetodoPago(request.getMetodoPago()));
            ps.setBigDecimal(3, request.getMontoPagado());

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del pago insertado.");
        }
    }

    // =====================================================
    // INSERT TICKET_JSON
    // =====================================================

    /**
     * Inserta el json del ticket.
     *
     * Aquí guardamos también:
     * - nombrePedido
     * - tipoServicio
     *
     * porque todavía no están como columnas propias en la tabla venta.
     */
    private int insertTicketJson(
            Connection con,
            int idVenta,
            RegistrarVentaRequest request
    ) throws SQLException, JsonProcessingException {

        String sql = """
            INSERT INTO ticket_json (
                id_venta,
                json_data,
                ruta_pdf,
                fecha_generacion
            ) VALUES (?, ?, ?, NOW())
        """;

        String jsonData = buildTicketJson(request);

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setString(2, jsonData);
            ps.setString(3, null); // de momento no generamos PDF aquí

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del ticket_json insertado.");
        }
    }

    // =====================================================
    // HELPERS
    // =====================================================

    /**
     * Construye el JSON del ticket completo.
     *
     * Guardamos aquí:
     * - cabecera de la venta
     * - nombrePedido
     * - tipoServicio
     * - método de pago
     * - monto pagado
     * - items
     */
    private String buildTicketJson(RegistrarVentaRequest request) throws JsonProcessingException {
        Map<String, Object> root = new LinkedHashMap<>();

        root.put("idSesion", request.getIdSesion());
        root.put("idUsuario", request.getIdUsuario());
        root.put("nombrePedido", request.getNombrePedido());
        root.put("tipoServicio", request.getTipoServicio() != null ? request.getTipoServicio().name() : null);
        root.put("metodoPago", request.getMetodoPago() != null ? request.getMetodoPago().name() : null);
        root.put("montoPagado", request.getMontoPagado());
        root.put("total", request.getTotal());
        root.put("items", request.getItems());

        return objectMapper.writeValueAsString(root);
    }

    /**
     * Mapea el enum Java al valor esperado por la BD.
     */
    private String mapMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == MetodoPago.TARJETA) {
            return "TARJETA";
        }
        return "EFECTIVO";
    }

    /**
     * Devuelve el id autogenerado de un INSERT.
     */
    private int getGeneratedId(PreparedStatement ps, String errorMessage) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException(errorMessage);
    }
}
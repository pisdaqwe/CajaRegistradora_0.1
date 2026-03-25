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
import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;
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
 * - DESCUENTO DE STOCK_PRODUCTO (si aplica)
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

                // NUEVO: descontar stock de productos si aplica
                descontarStockProductos(con, request);

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
    // DESCUENTO DE STOCK PRODUCTO
    // =====================================================

    private void descontarStockProductos(Connection con, RegistrarVentaRequest request) throws SQLException {
        Map<Integer, Integer> cantidadesPorProducto = agruparCantidadesPorProducto(request.getItems());

        for (Map.Entry<Integer, Integer> entry : cantidadesPorProducto.entrySet()) {
            int idProducto = entry.getKey();
            int cantidadVendida = entry.getValue();

            StockProductoEstado estado = lockStockProducto(con, request.getIdSucursal(), idProducto);

            if (estado == null) {
                throw new IllegalStateException(
                        "No existe configuración de stock para el producto " + idProducto
                                + " en la sucursal " + request.getIdSucursal() + "."
                );
            }

            switch (estado.modoDisponibilidad()) {
                case NO_DISPONIBLE -> throw new IllegalStateException(
                        "El producto '" + estado.nombreProducto() + "' está marcado como NO DISPONIBLE."
                );

                case DISPONIBLE_SIN_CONTROL -> {
                    // No se descuenta stock. Se considera venta válida.
                }

                case DISPONIBLE_CON_CANTIDAD -> {
                    BigDecimal cantidad = BigDecimal.valueOf(cantidadVendida);

                    if (estado.stockActual().compareTo(cantidad) < 0) {
                        throw new IllegalStateException(
                                "No hay stock suficiente para '" + estado.nombreProducto()
                                        + "'. Disponible: " + estado.stockActual().stripTrailingZeros().toPlainString()
                                        + ", requerido: " + cantidadVendida
                        );
                    }

                    descontarStockProducto(con, request.getIdSucursal(), idProducto, cantidad);
                }
            }
        }
    }

    private Map<Integer, Integer> agruparCantidadesPorProducto(List<RegistrarVentaItemRequest> items) {
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();

        if (items == null || items.isEmpty()) {
            return cantidades;
        }

        for (RegistrarVentaItemRequest item : items) {
            int idProducto = item.getIdProducto();
            int cantidad = item.getCantidad() > 0 ? item.getCantidad() : 0;

            cantidades.merge(idProducto, cantidad, Integer::sum);
        }

        return cantidades;
    }

    private StockProductoEstado lockStockProducto(Connection con, int idSucursal, int idProducto) throws SQLException {
        String sql = """
            SELECT
                p.nombre AS nombre_producto,
                sp.modo_disponibilidad,
                sp.stock
            FROM stock_producto sp
            JOIN producto p
                ON p.id_producto = sp.id_producto
            WHERE sp.id_sucursal = ?
              AND sp.id_producto = ?
            FOR UPDATE
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StockProductoEstado(
                            rs.getString("nombre_producto"),
                            ModoDisponibilidadProducto.valueOf(rs.getString("modo_disponibilidad")),
                            rs.getBigDecimal("stock")
                    );
                }
            }
        }

        return null;
    }

    private void descontarStockProducto(
            Connection con,
            int idSucursal,
            int idProducto,
            BigDecimal cantidad
    ) throws SQLException {

        String sql = """
            UPDATE stock_producto
               SET stock = stock - ?
             WHERE id_sucursal = ?
               AND id_producto = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, cantidad);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idProducto);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException(
                        "No se pudo descontar stock para producto=" + idProducto
                                + " en sucursal=" + idSucursal
                );
            }
        }
    }

    // =====================================================
    // INSERT PAGO
    // =====================================================

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
            ps.setString(3, null);

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del ticket_json insertado.");
        }
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private String buildTicketJson(RegistrarVentaRequest request) throws JsonProcessingException {
        Map<String, Object> root = new LinkedHashMap<>();

        root.put("idSesion", request.getIdSesion());
        root.put("idSucursal", request.getIdSucursal());
        root.put("idUsuario", request.getIdUsuario());
        root.put("nombrePedido", request.getNombrePedido());
        root.put("tipoServicio", request.getTipoServicio() != null ? request.getTipoServicio().name() : null);
        root.put("metodoPago", request.getMetodoPago() != null ? request.getMetodoPago().name() : null);
        root.put("montoPagado", request.getMontoPagado());
        root.put("total", request.getTotal());
        root.put("items", request.getItems());

        return objectMapper.writeValueAsString(root);
    }

    private String mapMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == MetodoPago.TARJETA) {
            return "TARJETA";
        }
        return "EFECTIVO";
    }

    private int getGeneratedId(PreparedStatement ps, String errorMessage) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException(errorMessage);
    }

    // =====================================================
    // DTO INTERNO PARA LOCK DE STOCK
    // =====================================================

    private record StockProductoEstado(
            String nombreProducto,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stockActual
    ) {
    }
}
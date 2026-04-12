package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtoS.RegistrarVentaComboItemRequest;
import dtoS.RegistrarVentaComboRequest;
import dtoS.RegistrarVentaDescuentoRequest;
import dtoS.RegistrarVentaExtraRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaItemResultDTO;
import dtoS.RegistrarVentaRequest;
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
 * DAO puro de registro de venta.
 *
 * RESPONSABILIDAD:
 * - insertar datos en BD
 * - no abrir/cerrar transacción completa
 * - no llamar a Services
 *
 * La transacción completa vive ahora en VentaService.
 */
public class VentaRegistroDao {

    private final ObjectMapper objectMapper;

    public VentaRegistroDao() {
        this.objectMapper = new ObjectMapper();
    }

    // =====================================================
    // INSERT VENTA
    // =====================================================

    public int insertVenta(Connection con, RegistrarVentaRequest request) throws SQLException {
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

    public VentaItemsPersistidosResult insertVentaItemsYExtras(
            Connection con,
            int idVenta,
            List<RegistrarVentaItemRequest> items
    ) throws SQLException {

        List<RegistrarVentaItemResultDTO> itemsPersistidos = new ArrayList<>();
        Map<Integer, Integer> ticketIndexToVentaItemId = new LinkedHashMap<>();

        if (items == null || items.isEmpty()) {
            return new VentaItemsPersistidosResult(itemsPersistidos, ticketIndexToVentaItemId);
        }

        for (int ticketItemIndex = 0; ticketItemIndex < items.size(); ticketItemIndex++) {
            RegistrarVentaItemRequest item = items.get(ticketItemIndex);

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

            ticketIndexToVentaItemId.put(ticketItemIndex, idItem);
        }

        return new VentaItemsPersistidosResult(itemsPersistidos, ticketIndexToVentaItemId);
    }

    /**
     * Inserta una línea real de venta.
     *
     * Guarda también snapshot del café seleccionado.
     */
    private int insertVentaItem(Connection con, int idVenta, RegistrarVentaItemRequest item) throws SQLException {
        String sql = """
                INSERT INTO venta_item (
                    id_venta,
                    id_producto,
                    cantidad,
                    precio_unitario,
                    subtotal_bruto,
                    importe_descuento_linea,
                    subtotal_final,
                    subtotal,
                    iva,
                    descripcion_personalizacion,
                    id_tipo_cafe_seleccionado,
                    nombre_tipo_cafe_snapshot,
                    suplemento_tipo_cafe
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, item.getIdProducto());
            ps.setInt(3, item.getCantidad());
            ps.setBigDecimal(4, item.getPrecioUnitario());
            ps.setBigDecimal(5, item.getSubtotalBruto());
            ps.setBigDecimal(6, item.getImporteDescuentoLinea());
            ps.setBigDecimal(7, item.getSubtotalFinal());

            // subtotal legacy = subtotal_final
            ps.setBigDecimal(8, item.getSubtotalFinal());

            ps.setBigDecimal(9, item.getIva());
            ps.setString(10, item.getDescripcionPersonalizacion());

            if (item.getIdTipoCafeSeleccionado() != null) {
                ps.setInt(11, item.getIdTipoCafeSeleccionado());
            } else {
                ps.setNull(11, java.sql.Types.INTEGER);
            }

            ps.setString(12, item.getNombreTipoCafeSnapshot());
            ps.setBigDecimal(13, item.getSuplementoTipoCafe());

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del item insertado.");
        }
    }

    private void insertVentaItemExtra(Connection con, int idItem, RegistrarVentaExtraRequest extra)
            throws SQLException {

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
    // INSERT COMBOS
    // =====================================================

    public void insertVentaCombos(
            Connection con,
            int idVenta,
            List<RegistrarVentaComboRequest> combos,
            Map<Integer, Integer> ticketIndexToVentaItemId
    ) throws SQLException {

        if (combos == null || combos.isEmpty()) {
            return;
        }

        for (RegistrarVentaComboRequest combo : combos) {
            int idVentaCombo = insertVentaCombo(con, idVenta, combo);

            if (combo.getItems() == null || combo.getItems().isEmpty()) {
                continue;
            }

            for (RegistrarVentaComboItemRequest comboItem : combo.getItems()) {
                Integer idItemReal = ticketIndexToVentaItemId.get(comboItem.getTicketItemIndex());

                if (idItemReal == null) {
                    throw new IllegalStateException(
                            "No existe id_item persistido para ticketItemIndex=" + comboItem.getTicketItemIndex()
                    );
                }

                insertVentaComboItem(con, idVentaCombo, idItemReal, comboItem);
            }
        }
    }

    private int insertVentaCombo(Connection con, int idVenta, RegistrarVentaComboRequest combo) throws SQLException {
        String sql = """
                INSERT INTO venta_combo (
                    id_venta,
                    id_combo,
                    nombre_combo,
                    tipo_combo,
                    valor_combo,
                    precio_original,
                    precio_final,
                    ahorro_total,
                    fecha_aplicacion
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, combo.getIdCombo());
            ps.setString(3, combo.getNombreCombo());
            ps.setString(4, combo.getTipoCombo().name());
            ps.setBigDecimal(5, combo.getValorCombo());
            ps.setBigDecimal(6, combo.getPrecioOriginal());
            ps.setBigDecimal(7, combo.getPrecioFinal());
            ps.setBigDecimal(8, combo.getAhorroTotal());

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id de venta_combo insertado.");
        }
    }

    private void insertVentaComboItem(
            Connection con,
            int idVentaCombo,
            int idItem,
            RegistrarVentaComboItemRequest comboItem
    ) throws SQLException {

        String sql = """
                INSERT INTO venta_combo_item (
                    id_venta_combo,
                    id_item,
                    subtotal_original_item,
                    descuento_asignado,
                    subtotal_final_item
                ) VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVentaCombo);
            ps.setInt(2, idItem);
            ps.setBigDecimal(3, comboItem.getSubtotalOriginalItem());
            ps.setBigDecimal(4, comboItem.getDescuentoAsignado());
            ps.setBigDecimal(5, comboItem.getSubtotalFinalItem());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // STOCK PRODUCTO
    // =====================================================

    public void descontarStockProductos(Connection con, RegistrarVentaRequest request) throws SQLException {
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
                    // no se descuenta
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

    private void descontarStockProducto(Connection con, int idSucursal, int idProducto, BigDecimal cantidad)
            throws SQLException {

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
                        "No se pudo descontar stock para producto=" + idProducto + " en sucursal=" + idSucursal
                );
            }
        }
    }

    // =====================================================
    // INSERT PAGO
    // =====================================================

    public int insertPago(Connection con, int idVenta, RegistrarVentaRequest request) throws SQLException {
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

    public int insertTicketJson(
            Connection con,
            int idVenta,
            RegistrarVentaRequest request,
            Map<Integer, Integer> ticketIndexToVentaItemId
    ) throws SQLException, JsonProcessingException {

        String sql = """
                INSERT INTO ticket_json (
                    id_venta,
                    json_data,
                    ruta_pdf,
                    fecha_generacion
                ) VALUES (?, ?, ?, NOW())
                """;

        String jsonData = buildTicketJson(request, ticketIndexToVentaItemId);

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVenta);
            ps.setString(2, jsonData);
            ps.setString(3, null);

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del ticket_json insertado.");
        }
    }

    // =====================================================
    // INSERT DESCUENTO
    // =====================================================

    public void insertVentaDescuento(Connection con, int idVenta, RegistrarVentaDescuentoRequest descuento)
            throws SQLException {

        if (descuento == null) {
            return;
        }

        String sql = """
                INSERT INTO venta_descuento (
                    id_venta,
                    id_descuento,
                    id_usuario_aplica,
                    id_empleado_beneficiario,
                    codigo_introducido,
                    tipo_descuento_aplicado,
                    valor_descuento_aplicado,
                    importe_base,
                    importe_descuento,
                    fecha_aplicacion,
                    observaciones
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, descuento.getIdDescuento());
            ps.setInt(3, descuento.getIdUsuarioAplica());

            if (descuento.getIdEmpleadoBeneficiario() != null) {
                ps.setInt(4, descuento.getIdEmpleadoBeneficiario());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setString(5, descuento.getCodigoIntroducido());
            ps.setString(6, descuento.getTipoDescuentoAplicado());
            ps.setBigDecimal(7, descuento.getValorDescuentoAplicado());
            ps.setBigDecimal(8, descuento.getImporteBase());
            ps.setBigDecimal(9, descuento.getImporteDescuento());
            ps.setString(10, descuento.getObservaciones());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // HELPERS JSON
    // =====================================================

    private String buildTicketJson(
            RegistrarVentaRequest request,
            Map<Integer, Integer> ticketIndexToVentaItemId
    ) throws JsonProcessingException {

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
        root.put("combos", buildTicketJsonCombos(request.getCombos(), ticketIndexToVentaItemId));
        root.put("descuento", buildTicketJsonDescuento(request.getDescuento()));

        return objectMapper.writeValueAsString(root);
    }

    private List<Map<String, Object>> buildTicketJsonCombos(
            List<RegistrarVentaComboRequest> combos,
            Map<Integer, Integer> ticketIndexToVentaItemId
    ) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (combos == null || combos.isEmpty()) {
            return result;
        }

        for (RegistrarVentaComboRequest combo : combos) {
            Map<String, Object> comboMap = new LinkedHashMap<>();
            comboMap.put("idCombo", combo.getIdCombo());
            comboMap.put("nombreCombo", combo.getNombreCombo());
            comboMap.put("tipoCombo", combo.getTipoCombo() != null ? combo.getTipoCombo().name() : null);
            comboMap.put("valorCombo", combo.getValorCombo());
            comboMap.put("precioOriginal", combo.getPrecioOriginal());
            comboMap.put("precioFinal", combo.getPrecioFinal());
            comboMap.put("ahorroTotal", combo.getAhorroTotal());

            List<Map<String, Object>> comboItems = new ArrayList<>();
            if (combo.getItems() != null) {
                for (RegistrarVentaComboItemRequest comboItem : combo.getItems()) {
                    Map<String, Object> itemMap = new LinkedHashMap<>();
                    itemMap.put("ticketItemIndex", comboItem.getTicketItemIndex());
                    itemMap.put("idItem", ticketIndexToVentaItemId.get(comboItem.getTicketItemIndex()));
                    itemMap.put("subtotalOriginalItem", comboItem.getSubtotalOriginalItem());
                    itemMap.put("descuentoAsignado", comboItem.getDescuentoAsignado());
                    itemMap.put("subtotalFinalItem", comboItem.getSubtotalFinalItem());
                    comboItems.add(itemMap);
                }
            }

            comboMap.put("items", comboItems);
            result.add(comboMap);
        }

        return result;
    }

    private Map<String, Object> buildTicketJsonDescuento(RegistrarVentaDescuentoRequest descuento) {
        if (descuento == null) {
            return null;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idDescuento", descuento.getIdDescuento());
        map.put("idUsuarioAplica", descuento.getIdUsuarioAplica());
        map.put("idEmpleadoBeneficiario", descuento.getIdEmpleadoBeneficiario());
        map.put("codigoIntroducido", descuento.getCodigoIntroducido());
        map.put("tipoDescuentoAplicado", descuento.getTipoDescuentoAplicado());
        map.put("valorDescuentoAplicado", descuento.getValorDescuentoAplicado());
        map.put("importeBase", descuento.getImporteBase());
        map.put("importeDescuento", descuento.getImporteDescuento());
        map.put("observaciones", descuento.getObservaciones());
        map.put("nombreDescuento", descuento.getNombreDescuento());
        map.put("origenDescuento", descuento.getOrigenDescuento());
        return map;
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
    // RECORDS INTERNOS
    // =====================================================

    private record StockProductoEstado(
            String nombreProducto,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stockActual
    ) {
    }

    /**
     * Resultado intermedio necesario para que el Service
     * pueda seguir orquestando la venta.
     */
    public static record VentaItemsPersistidosResult(
            List<RegistrarVentaItemResultDTO> itemsPersistidos,
            Map<Integer, Integer> ticketIndexToVentaItemId
    ) {
    }
}
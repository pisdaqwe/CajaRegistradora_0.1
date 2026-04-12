package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.DbPool;
import dtoS.RegistrarDevolucionItemRequest;
import dtoS.RegistrarDevolucionRequest;
import dtoS.RegistrarDevolucionResultDTO;
import dtoS.VentaItemParaDevolucionDTO;
import dtoS.VentaParaDevolucionDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO transaccional encargado de registrar una devolución completa.
 *
 * Esta clase inserta:
 * - DEVOLUCION
 * - DEVOLUCION_ITEM
 * - DEVOLUCION_TICKET_JSON
 *
 * Y ahora también:
 * - repone stock en stock_producto si la línea lo solicita
 *
 * IMPORTANTE:
 * - abre la conexión aquí con DbPool
 * - trabaja con una única transacción
 * - si falla algo, hace rollback completo
 */
public class DevolucionRegistroDao {

    // =====================================================
    // 1) DEPENDENCIAS
    // =====================================================

    private final DevolucionDao devolucionDao;
    private final DevolucionItemDao devolucionItemDao;
    private final DevolucionTicketJsonDao devolucionTicketJsonDao;
    private final StockProductoDao stockProductoDao;
    private final ObjectMapper objectMapper;

    // =====================================================
    // 2) CONSTRUCTOR
    // =====================================================

    public DevolucionRegistroDao(
            DevolucionDao devolucionDao,
            DevolucionItemDao devolucionItemDao,
            DevolucionTicketJsonDao devolucionTicketJsonDao,
            StockProductoDao stockProductoDao
    ) {
        if (devolucionDao == null) {
            throw new IllegalArgumentException("devolucionDao no puede ser null");
        }
        if (devolucionItemDao == null) {
            throw new IllegalArgumentException("devolucionItemDao no puede ser null");
        }
        if (devolucionTicketJsonDao == null) {
            throw new IllegalArgumentException("devolucionTicketJsonDao no puede ser null");
        }
        if (stockProductoDao == null) {
            throw new IllegalArgumentException("stockProductoDao no puede ser null");
        }

        this.devolucionDao = devolucionDao;
        this.devolucionItemDao = devolucionItemDao;
        this.devolucionTicketJsonDao = devolucionTicketJsonDao;
        this.stockProductoDao = stockProductoDao;
        this.objectMapper = new ObjectMapper();
    }

    // =====================================================
    // 3) REGISTRO COMPLETO DE LA DEVOLUCIÓN
    // =====================================================

    public RegistrarDevolucionResultDTO registrarDevolucionCompleta(
            RegistrarDevolucionRequest request,
            VentaParaDevolucionDTO ventaOriginal,
            List<VentaItemParaDevolucionDTO> itemsVenta,
            BigDecimal totalDevuelto
    ) {
        try (Connection con = DbPool.getConnection()) {
            con.setAutoCommit(false);

            try {
                // -------------------------------------------------
                // 1. Insertar cabecera de devolución
                // -------------------------------------------------
                int idDevolucion = devolucionDao.insert(con, request, totalDevuelto);

                // -------------------------------------------------
                // 2. Insertar detalle de líneas devueltas
                // -------------------------------------------------
                insertDevolucionItems(con, idDevolucion, request, itemsVenta);

                // -------------------------------------------------
                // 3. Reponer stock real si procede
                // -------------------------------------------------
                reponerStockSiProcede(con, request, itemsVenta);

                // -------------------------------------------------
                // 4. Generar JSON del ticket de devolución
                // -------------------------------------------------
                String jsonData = buildDevolucionTicketJson(
                        idDevolucion,
                        request,
                        ventaOriginal,
                        itemsVenta,
                        totalDevuelto
                );

                // -------------------------------------------------
                // 5. Guardar ticket JSON de devolución
                // -------------------------------------------------
                devolucionTicketJsonDao.insert(con, idDevolucion, jsonData, null);

                // -------------------------------------------------
                // 6. Commit final
                // -------------------------------------------------
                con.commit();

                RegistrarDevolucionResultDTO result = new RegistrarDevolucionResultDTO();
                result.setIdDevolucion(idDevolucion);
                result.setIdVentaOriginal(request.getIdVentaOriginal());
                result.setImporteTotalDevuelto(totalDevuelto);
                result.setMetodoReembolso(request.getMetodoReembolso());
                result.setTicketGenerado(true);

                return result;

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Error registrando la devolución completa.", e);
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir la conexión para registrar la devolución.", e);
        }
    }

    // =====================================================
    // 4) INSERT DEL DETALLE DE DEVOLUCIÓN
    // =====================================================

    private void insertDevolucionItems(
            Connection con,
            int idDevolucion,
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) throws SQLException {

        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            VentaItemParaDevolucionDTO lineaOriginal = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            devolucionItemDao.insertItem(
                    con,
                    idDevolucion,
                    lineaOriginal,
                    itemReq.getCantidadADevolver(),
                    itemReq.isReponeStock()
            );
        }
    }

    // =====================================================
    // 5) REPOSICIÓN REAL DE STOCK
    // =====================================================

    /**
     * Recorre las líneas solicitadas en la devolución y repone stock
     * únicamente en aquellas que:
     * - tengan reponeStock = true
     * - correspondan a una línea válida de venta
     *
     * IMPORTANTE:
     * - usa la MISMA conexión transaccional
     * - si falla aquí, se hace rollback de toda la devolución
     */
    private void reponerStockSiProcede(
            Connection con,
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) throws SQLException {

        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            if (!itemReq.isReponeStock()) {
                continue;
            }

            VentaItemParaDevolucionDTO linea = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            stockProductoDao.sumarStockEnTransaccion(
                    con,
                    request.getIdSucursalActual(),
                    linea.getIdProducto(),
                    BigDecimal.valueOf(itemReq.getCantidadADevolver())
            );
        }
    }

    // =====================================================
    // 6) JSON DEL TICKET DE DEVOLUCIÓN
    // =====================================================

    private String buildDevolucionTicketJson(
            int idDevolucion,
            RegistrarDevolucionRequest request,
            VentaParaDevolucionDTO ventaOriginal,
            List<VentaItemParaDevolucionDTO> itemsVenta,
            BigDecimal totalDevuelto
    ) throws JsonProcessingException {

        Map<String, Object> root = new LinkedHashMap<>();

        // -------------------------------------------------
        // Cabecera general
        // -------------------------------------------------
        root.put("idDevolucion", idDevolucion);
        root.put("idVentaOriginal", request.getIdVentaOriginal());

        root.put(
                "fechaVentaOriginal",
                ventaOriginal.getFechaVenta() != null ? ventaOriginal.getFechaVenta().toString() : null
        );

        root.put("nombrePedido", ventaOriginal.getNombrePedido());
        root.put("tipoServicio", ventaOriginal.getTipoServicio());
        root.put("metodoPagoOriginal", ventaOriginal.getMetodoPagoOriginal());

        // -------------------------------------------------
        // Datos de la devolución actual
        // -------------------------------------------------
        root.put("idSesionCajaActual", request.getIdSesionCajaActual());
        root.put("idUsuarioAdmin", request.getIdUsuarioAdmin());
        root.put("metodoReembolso", request.getMetodoReembolso());
        root.put("motivo", request.getMotivo());
        root.put("observaciones", request.getObservaciones());
        root.put("totalDevuelto", totalDevuelto);

        // -------------------------------------------------
        // Líneas devueltas
        // -------------------------------------------------
        root.put("items", buildDevolucionItemsJson(request, itemsVenta));

        return objectMapper.writeValueAsString(root);
    }

    private List<Map<String, Object>> buildDevolucionItemsJson(
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            VentaItemParaDevolucionDTO linea = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            BigDecimal brutoUnitario = calcularUnitario(linea.getSubtotalBruto(), linea.getCantidadVendida());
            BigDecimal descuentoUnitario = calcularUnitario(linea.getImporteDescuentoLinea(), linea.getCantidadVendida());
            BigDecimal finalUnitario = calcularUnitario(linea.getSubtotalFinal(), linea.getCantidadVendida());

            int cantidadDevuelta = itemReq.getCantidadADevolver();

            BigDecimal subtotalBrutoDevuelto = brutoUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));
            BigDecimal importeDescuentoDevuelto = descuentoUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));
            BigDecimal subtotalFinalDevuelto = finalUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));

            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("idVentaItem", linea.getIdVentaItem());
            itemMap.put("idProducto", linea.getIdProducto());
            itemMap.put("nombreProducto", linea.getNombreProducto());
            itemMap.put("tamano", linea.getTamano());
            itemMap.put("cantidadVendida", linea.getCantidadVendida());
            itemMap.put("cantidadDevuelta", cantidadDevuelta);
            itemMap.put("precioUnitario", linea.getPrecioUnitario());
            itemMap.put("subtotalBrutoDevuelto", subtotalBrutoDevuelto);
            itemMap.put("importeDescuentoDevuelto", importeDescuentoDevuelto);
            itemMap.put("subtotalFinalDevuelto", subtotalFinalDevuelto);
            itemMap.put("reponeStock", itemReq.isReponeStock());
            itemMap.put("descripcionResumen", buildDescripcionResumen(linea));

            result.add(itemMap);
        }

        return result;
    }

    // =====================================================
    // 7) HELPERS INTERNOS
    // =====================================================

    private VentaItemParaDevolucionDTO buscarLineaOrThrow(
            int idVentaItem,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) {
        if (itemsVenta == null || itemsVenta.isEmpty()) {
            throw new IllegalStateException("No hay líneas de venta cargadas.");
        }

        for (VentaItemParaDevolucionDTO item : itemsVenta) {
            if (item != null && item.getIdVentaItem() == idVentaItem) {
                return item;
            }
        }

        throw new IllegalStateException("No existe la línea de venta id=" + idVentaItem);
    }

    private BigDecimal calcularUnitario(BigDecimal totalLinea, int cantidadVendida) {
        if (totalLinea == null) {
            return BigDecimal.ZERO;
        }
        if (cantidadVendida <= 0) {
            throw new IllegalArgumentException("cantidadVendida debe ser > 0.");
        }

        return totalLinea.divide(
                BigDecimal.valueOf(cantidadVendida),
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    private String buildDescripcionResumen(VentaItemParaDevolucionDTO linea) {
        String producto = linea.getNombreProducto() != null ? linea.getNombreProducto().trim() : "";
        String tamano = linea.getTamano() != null ? linea.getTamano().trim() : "";

        if (!tamano.isBlank()) {
            return producto + " (" + tamano + ")";
        }
        return producto;
    }
}

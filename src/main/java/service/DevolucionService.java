package service;

import dao.DevolucionRegistroDao;
import dao.VentaDao;
import dao.VentaItemDao;
import dtoS.RegistrarDevolucionItemRequest;
import dtoS.RegistrarDevolucionRequest;
import dtoS.RegistrarDevolucionResultDTO;
import dtoS.VentaItemParaDevolucionDTO;
import dtoS.VentaParaDevolucionDTO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DevolucionService {

    private final VentaDao ventaDao;
    private final VentaItemDao ventaItemDao;
    private final DevolucionRegistroDao devolucionRegistroDao;
    private final AuditoriaService auditoriaService;

    public DevolucionService(
            VentaDao ventaDao,
            VentaItemDao ventaItemDao,
            DevolucionRegistroDao devolucionRegistroDao,
            AuditoriaService auditoriaService
    ) {
        if (ventaDao == null) {
            throw new IllegalArgumentException("ventaDao no puede ser null");
        }
        if (ventaItemDao == null) {
            throw new IllegalArgumentException("ventaItemDao no puede ser null");
        }
        if (devolucionRegistroDao == null) {
            throw new IllegalArgumentException("devolucionRegistroDao no puede ser null");
        }
        if (auditoriaService == null) {
            throw new IllegalArgumentException("auditoriaService no puede ser null");
        }

        this.ventaDao = ventaDao;
        this.ventaItemDao = ventaItemDao;
        this.devolucionRegistroDao = devolucionRegistroDao;
        this.auditoriaService = auditoriaService;
    }

    public VentaParaDevolucionDTO getVentaParaDevolucion(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta debe ser > 0.");
        }

        VentaParaDevolucionDTO venta = ventaDao.findVentaParaDevolucionById(idVenta)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe ninguna venta con id=" + idVenta
                ));

        if (venta.isAnulada()) {
            throw new IllegalStateException("La venta indicada está anulada y no puede devolverse.");
        }

        return venta;
    }

    public List<VentaItemParaDevolucionDTO> getItemsParaDevolucion(int idVenta, int idSucursal) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta debe ser > 0.");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0.");
        }

        getVentaParaDevolucion(idVenta);

        List<VentaItemParaDevolucionDTO> items = ventaItemDao.findItemsParaDevolucion(idVenta, idSucursal);

        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("La venta no tiene líneas disponibles para devolución.");
        }

        return items;
    }

    public RegistrarDevolucionResultDTO registrarDevolucion(RegistrarDevolucionRequest request) {
        validarRequest(request);

        VentaParaDevolucionDTO ventaOriginal = getVentaParaDevolucion(request.getIdVentaOriginal());
        List<VentaItemParaDevolucionDTO> itemsVenta =
                getItemsParaDevolucion(request.getIdVentaOriginal(), request.getIdSucursalActual());

        validarItemsSolicitados(request, itemsVenta);
        normalizarReponeStockSegunPermisos(request, itemsVenta);

        BigDecimal totalDevuelto = calcularTotalDevolucion(request, itemsVenta);

        RegistrarDevolucionResultDTO result = devolucionRegistroDao.registrarDevolucionCompleta(
                request,
                ventaOriginal,
                itemsVenta,
                totalDevuelto
        );

        auditarSeguro(
                request.getIdUsuarioAdmin(),
                request.getIdSucursalActual(),
                "DEVOLUCION_REGISTRADA_OK",
                detallesDevolucion(request, result, ventaOriginal)
        );

        return result;
    }

    private void validarRequest(RegistrarDevolucionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request de devolución no puede ser null.");
        }

        if (request.getIdVentaOriginal() <= 0) {
            throw new IllegalArgumentException("La devolución requiere una venta original válida.");
        }

        if (request.getIdSesionCajaActual() <= 0) {
            throw new IllegalArgumentException("La devolución requiere una sesión de caja válida.");
        }

        if (request.getIdUsuarioAdmin() <= 0) {
            throw new IllegalArgumentException("La devolución requiere un usuario administrador válido.");
        }

        if (request.getIdSucursalActual() <= 0) {
            throw new IllegalArgumentException("La devolución requiere una sucursal válida.");
        }

        if (request.getMetodoReembolso() == null || request.getMetodoReembolso().isBlank()) {
            throw new IllegalArgumentException("Debe indicarse un método de reembolso.");
        }

        String metodo = request.getMetodoReembolso().trim().toUpperCase();
        if (!"EFECTIVO".equals(metodo) && !"TARJETA".equals(metodo)) {
            throw new IllegalArgumentException("El método de reembolso debe ser EFECTIVO o TARJETA.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La devolución debe tener al menos una línea.");
        }
    }

    private void validarItemsSolicitados(
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) {
        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            if (itemReq == null) {
                throw new IllegalArgumentException("La devolución contiene un item null.");
            }

            if (itemReq.getIdVentaItem() <= 0) {
                throw new IllegalArgumentException("Todo item de devolución debe tener idVentaItem válido.");
            }

            if (itemReq.getCantidadADevolver() <= 0) {
                throw new IllegalArgumentException("La cantidad a devolver debe ser mayor que 0.");
            }

            VentaItemParaDevolucionDTO linea = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            if (linea.getCantidadDisponible() <= 0) {
                throw new IllegalStateException(
                        "La línea " + linea.getNombreProducto()
                                + " ya no tiene cantidad disponible para devolución."
                );
            }

            if (itemReq.getCantidadADevolver() > linea.getCantidadDisponible()) {
                throw new IllegalStateException(
                        "No se puede devolver más cantidad de la disponible en la línea "
                                + linea.getNombreProducto()
                                + ". Disponible: " + linea.getCantidadDisponible()
                                + ", solicitada: " + itemReq.getCantidadADevolver()
                );
            }
        }
    }

    /**
     * Seguridad defensiva de backend:
     * si la UI manda reponeStock=true pero la línea no lo permite,
     * lo forzamos a false.
     */
    private void normalizarReponeStockSegunPermisos(
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) {
        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            VentaItemParaDevolucionDTO linea = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            if (!linea.isPermiteReponerStock()) {
                itemReq.setReponeStock(false);
            }

            if (itemReq.getCantidadADevolver() <= 0) {
                itemReq.setReponeStock(false);
            }
        }
    }

    private BigDecimal calcularTotalDevolucion(
            RegistrarDevolucionRequest request,
            List<VentaItemParaDevolucionDTO> itemsVenta
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (RegistrarDevolucionItemRequest itemReq : request.getItems()) {
            VentaItemParaDevolucionDTO linea = buscarLineaOrThrow(itemReq.getIdVentaItem(), itemsVenta);

            BigDecimal importeUnitarioFinal = calcularImporteUnitarioFinal(linea);
            BigDecimal importeLineaDevuelta = importeUnitarioFinal.multiply(
                    BigDecimal.valueOf(itemReq.getCantidadADevolver())
            );

            total = total.add(importeLineaDevuelta);
        }

        return total;
    }

    private BigDecimal calcularImporteUnitarioFinal(VentaItemParaDevolucionDTO linea) {
        if (linea == null) {
            throw new IllegalArgumentException("linea no puede ser null");
        }

        if (linea.getCantidadVendida() <= 0) {
            throw new IllegalStateException("La línea tiene cantidadVendida inválida.");
        }

        if (linea.getSubtotalFinal() == null || linea.getSubtotalFinal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("La línea tiene subtotalFinal inválido.");
        }

        return linea.getSubtotalFinal().divide(
                BigDecimal.valueOf(linea.getCantidadVendida()),
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

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

    private Map<String, Object> detallesDevolucion(RegistrarDevolucionRequest request,
                                                   RegistrarDevolucionResultDTO result,
                                                   VentaParaDevolucionDTO ventaOriginal) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idDevolucion", result.getIdDevolucion());
        data.put("idVentaOriginal", result.getIdVentaOriginal());
        data.put("idSesionCajaActual", request.getIdSesionCajaActual());
        data.put("idUsuarioAdmin", request.getIdUsuarioAdmin());
        data.put("idSucursalActual", request.getIdSucursalActual());
        data.put("metodoReembolso", result.getMetodoReembolso());
        data.put("importeTotalDevuelto", result.getImporteTotalDevuelto());
        data.put("ticketGenerado", result.isTicketGenerado());

        data.put("motivo", request.getMotivo());
        data.put("observaciones", request.getObservaciones());
        data.put("numLineas", request.getItems() != null ? request.getItems().size() : 0);
        data.put("lineasConReponeStock", contarLineasConReponeStock(request));

        if (ventaOriginal != null) {
            data.put("nombrePedido", ventaOriginal.getNombrePedido());
            data.put("tipoServicio", ventaOriginal.getTipoServicio());
            data.put("metodoPagoOriginal", ventaOriginal.getMetodoPagoOriginal());
            data.put("totalVentaOriginal", ventaOriginal.getTotalVenta());
        }

        return data;
    }

    private int contarLineasConReponeStock(RegistrarDevolucionRequest request) {
        int total = 0;
        if (request.getItems() == null) {
            return total;
        }

        for (RegistrarDevolucionItemRequest item : request.getItems()) {
            if (item != null && item.isReponeStock()) {
                total++;
            }
        }
        return total;
    }

    private void auditarSeguro(int idUsuario,
                               int idSucursal,
                               String accion,
                               Map<String, Object> detalles) {
        try {
            auditoriaService.registrarEvento(idUsuario, idSucursal, accion, detalles);
        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo registrar evento " + accion + ": " + ex.getMessage());
        }
    }
}
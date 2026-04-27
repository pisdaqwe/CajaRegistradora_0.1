package service;

import app.AppContext;
import dao.StockProductoDao;
import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisponibilidadProductoService {

    private final StockProductoDao stockProductoDao;
    private final AuditoriaService auditoriaService;

    public DisponibilidadProductoService(StockProductoDao stockProductoDao,
                                         AuditoriaService auditoriaService) {
        if (stockProductoDao == null) {
            throw new IllegalArgumentException("stockProductoDao no puede ser null");
        }
        if (auditoriaService == null) {
            throw new IllegalArgumentException("auditoriaService no puede ser null");
        }
        this.stockProductoDao = stockProductoDao;
        this.auditoriaService = auditoriaService;
    }

    // =====================================================
    // CONSULTAS
    // =====================================================

    public List<StockProductoDisponibilidadDTO> getDisponibilidadProductosSucursalActual() {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        return stockProductoDao.findDisponibilidadBySucursal(idSucursal);
    }

    public StockProductoDisponibilidadDTO getDisponibilidadProductoSucursalActual(int idProducto) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();

        return stockProductoDao.findByProductoYSucursal(idProducto, idSucursal)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe configuración de disponibilidad para el producto " + idProducto
                                + " en la sucursal actual."
                ));
    }

    // =====================================================
    // GUARDADO
    // =====================================================

    public void guardarDisponibilidadProductoSucursalActual(
            int idProducto,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stock
    ) {
        int idSucursal = AppContext.getSesionCajaActual().getIdSucursal();
        int idUsuario = AppContext.getUsuarioId();

        StockProductoDisponibilidadDTO actual =
                stockProductoDao.findByProductoYSucursal(idProducto, idSucursal)
                        .orElseThrow(() -> new IllegalStateException(
                                "No existe configuración de disponibilidad para el producto " + idProducto
                                        + " en la sucursal actual."
                        ));

        validarCambio(actual, modoDisponibilidad, stock);

        BigDecimal stockFinal = normalizarStockParaGuardar(modoDisponibilidad, stock);

        stockProductoDao.updateDisponibilidadProducto(
                idSucursal,
                idProducto,
                modoDisponibilidad,
                stockFinal
        );

        auditarSeguro(
                idUsuario,
                idSucursal,
                "DISPONIBILIDAD_PRODUCTO_ACTUALIZADA",
                detallesCambio(actual, modoDisponibilidad, stockFinal)
        );
    }

    // =====================================================
    // VALIDACIONES
    // =====================================================

    private void validarCambio(
            StockProductoDisponibilidadDTO actual,
            ModoDisponibilidadProducto nuevoModo,
            BigDecimal stock
    ) {
        if (actual == null) {
            throw new IllegalArgumentException("El producto actual no puede ser null.");
        }

        if (nuevoModo == null) {
            throw new IllegalArgumentException("Debes indicar un modo de disponibilidad.");
        }

        BigDecimal stockSeguro = stock != null ? stock : BigDecimal.ZERO;

        if (stockSeguro.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        if (!actual.isPermiteStockCantidad()
                && nuevoModo == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD) {
            throw new IllegalArgumentException(
                    "Este producto no permite control de stock por cantidad."
            );
        }
    }

    private BigDecimal normalizarStockParaGuardar(
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stock
    ) {
        BigDecimal stockSeguro = stock != null ? stock : BigDecimal.ZERO;

        return switch (modoDisponibilidad) {
            case NO_DISPONIBLE -> stockSeguro;
            case DISPONIBLE_SIN_CONTROL -> stockSeguro;
            case DISPONIBLE_CON_CANTIDAD -> stockSeguro;
        };
    }

    // =====================================================
    // DETALLES AUDITORÍA
    // =====================================================

    private Map<String, Object> detallesCambio(StockProductoDisponibilidadDTO actual,
                                               ModoDisponibilidadProducto nuevoModo,
                                               BigDecimal stockNuevo) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idProducto", actual.getIdProducto());
        data.put("nombreProducto", actual.getNombreProducto());
        data.put("idSubcategoria", actual.getIdSubcategoria());
        data.put("nombreSubcategoria", actual.getNombreSubcategoria());
        data.put("permiteStockCantidad", actual.isPermiteStockCantidad());

        data.put("modoAnterior", actual.getModoDisponibilidad() != null ? actual.getModoDisponibilidad().name() : null);
        data.put("modoNuevo", nuevoModo != null ? nuevoModo.name() : null);

        data.put("stockAnterior", actual.getStockActual());
        data.put("stockNuevo", stockNuevo);

        return data;
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
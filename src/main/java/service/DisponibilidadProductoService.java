package service;

import app.AppContext;
import dao.StockProductoDao;
import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;
import java.util.List;

public class DisponibilidadProductoService {

    private final StockProductoDao stockProductoDao;

    public DisponibilidadProductoService(StockProductoDao stockProductoDao) {
        this.stockProductoDao = stockProductoDao;
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

        // Si el producto NO permite control por cantidad,
        // no se le puede poner DISPONIBLE_CON_CANTIDAD.
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
}

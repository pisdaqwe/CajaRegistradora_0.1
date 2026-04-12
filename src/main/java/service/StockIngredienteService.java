package service;

import dao.StockIngredienteDao;
import dtoS.IngredienteConsumidoDTO;
import dtoS.RecetaFinalItemDTO;
import dtoS.StockIngredienteDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Service encargado de validar y descontar stock de ingredientes.
 *
 * Este service trabaja sobre la receta final ya resuelta del item.
 *
 * IMPORTANTE:
 * - se ejecuta dentro de una transacción externa
 * - usa FOR UPDATE para bloquear stock_ingrediente
 * - de momento asume que la unidad del ingrediente consumido
 *   coincide con la unidad base del ingrediente en stock
 */
public class StockIngredienteService {

    private final StockIngredienteDao stockIngredienteDao;

    public StockIngredienteService(StockIngredienteDao stockIngredienteDao) {
        if (stockIngredienteDao == null) {
            throw new IllegalArgumentException("stockIngredienteDao no puede ser null");
        }
        this.stockIngredienteDao = stockIngredienteDao;
    }

    /**
     * Valida que haya stock suficiente para todos los ingredientes
     * de la receta final del item.
     *
     * NO descuenta todavía.
     */
    public void validarStockSuficiente(Connection con,
                                       int idSucursal,
                                       RecetaFinalItemDTO recetaFinalItem) {
        validateCommonInputs(con, idSucursal, recetaFinalItem);

        List<IngredienteConsumidoDTO> ingredientes = recetaFinalItem.getIngredientesConsumidos();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return;
        }

        for (IngredienteConsumidoDTO ingredienteConsumido : ingredientes) {
            validateIngredienteConsumido(ingredienteConsumido);

            Optional<StockIngredienteDTO> stockOpt =
                    stockIngredienteDao.findBySucursalAndIngredienteForUpdate(
                            con,
                            idSucursal,
                            ingredienteConsumido.getIdIngrediente()
                    );

            if (stockOpt.isEmpty()) {
                throw new IllegalStateException(
                        "No existe stock configurado para el ingrediente '"
                                + safeNombre(ingredienteConsumido.getNombreIngrediente())
                                + "' en la sucursal " + idSucursal
                );
            }

            StockIngredienteDTO stockActual = stockOpt.get();

            validateUnidadCompatible(stockActual, ingredienteConsumido);

            if (stockActual.getStock() == null) {
                throw new IllegalStateException(
                        "El stock del ingrediente '"
                                + safeNombre(stockActual.getNombreIngrediente())
                                + "' es null."
                );
            }

            if (stockActual.getStock().compareTo(ingredienteConsumido.getCantidad()) < 0) {
                throw new IllegalStateException(
                        "Stock insuficiente para el ingrediente '"
                                + safeNombre(stockActual.getNombreIngrediente())
                                + "'. Disponible: "
                                + stockActual.getStock().stripTrailingZeros().toPlainString()
                                + " "
                                + safeNombre(stockActual.getNombreUnidadBase())
                                + ", requerido: "
                                + ingredienteConsumido.getCantidad().stripTrailingZeros().toPlainString()
                                + " "
                                + safeNombre(ingredienteConsumido.getNombreUnidad())
                );
            }
        }
    }

    /**
     * Descuenta stock de todos los ingredientes del item.
     *
     * OJO:
     * este método asume que ya has validado antes el stock,
     * o bien que estás dispuesto a que falle a mitad si hay un problema.
     */
    public void descontarIngredientes(Connection con,
                                      int idSucursal,
                                      RecetaFinalItemDTO recetaFinalItem) {
        validateCommonInputs(con, idSucursal, recetaFinalItem);

        List<IngredienteConsumidoDTO> ingredientes = recetaFinalItem.getIngredientesConsumidos();
        if (ingredientes == null || ingredientes.isEmpty()) {
            return;
        }

        for (IngredienteConsumidoDTO ingredienteConsumido : ingredientes) {
            validateIngredienteConsumido(ingredienteConsumido);

            stockIngredienteDao.descontarStock(
                    con,
                    idSucursal,
                    ingredienteConsumido.getIdIngrediente(),
                    ingredienteConsumido.getCantidad()
            );
        }
    }

    /**
     * Método cómodo para hacer ambas cosas seguidas.
     *
     * Primero valida, luego descuenta.
     */
    public void validarYDescontar(Connection con,
                                  int idSucursal,
                                  RecetaFinalItemDTO recetaFinalItem) {
        validarStockSuficiente(con, idSucursal, recetaFinalItem);
        descontarIngredientes(con, idSucursal, recetaFinalItem);
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void validateCommonInputs(Connection con,
                                      int idSucursal,
                                      RecetaFinalItemDTO recetaFinalItem) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (recetaFinalItem == null) {
            throw new IllegalArgumentException("recetaFinalItem no puede ser null");
        }
    }

    private void validateIngredienteConsumido(IngredienteConsumidoDTO ingredienteConsumido) {
        if (ingredienteConsumido == null) {
            throw new IllegalArgumentException("Hay un ingrediente consumido null en la receta final.");
        }
        if (ingredienteConsumido.getIdIngrediente() <= 0) {
            throw new IllegalArgumentException("idIngrediente inválido en ingrediente consumido.");
        }
        if (ingredienteConsumido.getCantidad() == null) {
            throw new IllegalArgumentException(
                    "La cantidad es null para el ingrediente '"
                            + safeNombre(ingredienteConsumido.getNombreIngrediente()) + "'."
            );
        }
        if (ingredienteConsumido.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser > 0 para el ingrediente '"
                            + safeNombre(ingredienteConsumido.getNombreIngrediente()) + "'."
            );
        }
    }

    /**
     * Primera versión simple:
     * exige que la unidad del ingrediente consumido coincida
     * con la unidad base del stock.
     *
     * Más adelante, si metes conversiones, aquí se amplía.
     */
    private void validateUnidadCompatible(StockIngredienteDTO stockActual,
                                          IngredienteConsumidoDTO ingredienteConsumido) {
        Integer unidadStock = stockActual.getIdUnidadBase();
        int unidadConsumida = ingredienteConsumido.getIdUnidad();

        if (unidadStock == null) {
            throw new IllegalStateException(
                    "El ingrediente '"
                            + safeNombre(stockActual.getNombreIngrediente())
                            + "' no tiene unidad base configurada."
            );
        }

        if (unidadStock != unidadConsumida) {
            throw new IllegalStateException(
                    "Unidad incompatible para el ingrediente '"
                            + safeNombre(stockActual.getNombreIngrediente())
                            + "'. Unidad stock: "
                            + safeNombre(stockActual.getNombreUnidadBase())
                            + ", unidad consumida: "
                            + safeNombre(ingredienteConsumido.getNombreUnidad())
            );
        }
    }

    private String safeNombre(String value) {
        return value != null ? value : "(sin nombre)";
    }
}
package service;

import dtoS.IngredienteConsumidoDTO;
import dtoS.RecetaFinalItemDTO;
import dtoS.RegistrarVentaItemRequest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de caso de uso que conecta:
 * - resolución de receta
 * - validación/descuento de stock
 * - registro de movimientos
 *
 * Esta versión trabaja ya con RegistrarVentaItemRequest,
 * que es lo que realmente tienes dentro del flujo de venta.
 */
public class VentaStockIngredienteService {

    private final RecipeResolverService recipeResolverService;
    private final StockIngredienteService stockIngredienteService;
    private final MovimientoStockService movimientoStockService;

    public VentaStockIngredienteService(RecipeResolverService recipeResolverService,
                                        StockIngredienteService stockIngredienteService,
                                        MovimientoStockService movimientoStockService) {
        if (recipeResolverService == null) {
            throw new IllegalArgumentException("recipeResolverService no puede ser null");
        }
        if (stockIngredienteService == null) {
            throw new IllegalArgumentException("stockIngredienteService no puede ser null");
        }
        if (movimientoStockService == null) {
            throw new IllegalArgumentException("movimientoStockService no puede ser null");
        }

        this.recipeResolverService = recipeResolverService;
        this.stockIngredienteService = stockIngredienteService;
        this.movimientoStockService = movimientoStockService;
    }

    /**
     * Procesa un item real de venta:
     * 1. resuelve la receta final
     * 2. valida stock
     * 3. descuenta stock
     * 4. registra movimientos
     *
     * Devuelve la receta final usada.
     */
    public RecetaFinalItemDTO procesarItemVenta(Connection con,
                                                int idSucursal,
                                                RegistrarVentaItemRequest item,
                                                String referenciaMovimiento,
                                                String motivoMovimiento) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (item == null) {
            throw new IllegalArgumentException("item no puede ser null");
        }

        // 1) Resolver receta final del item
        RecetaFinalItemDTO recetaFinal = recipeResolverService.resolve(item);

        // 2) Validar stock suficiente
        stockIngredienteService.validarStockSuficiente(con, idSucursal, recetaFinal);

        // 3) Descontar stock
        stockIngredienteService.descontarIngredientes(con, idSucursal, recetaFinal);

        // 4) Registrar movimientos
        registrarMovimientosIngredientes(
                con,
                idSucursal,
                recetaFinal,
                referenciaMovimiento,
                motivoMovimiento
        );

        return recetaFinal;
    }

    /**
     * Variante para una lista de items de venta.
     *
     * Muy útil cuando ya tienes request.getItems().
     */
    public List<RecetaFinalItemDTO> procesarItemsVenta(Connection con,
                                                       int idSucursal,
                                                       List<RegistrarVentaItemRequest> items,
                                                       String referenciaMovimiento,
                                                       String motivoMovimiento) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        List<RecetaFinalItemDTO> recetasFinales = new ArrayList<>();

        if (items == null || items.isEmpty()) {
            return recetasFinales;
        }

        for (RegistrarVentaItemRequest item : items) {
            if (item == null) {
                continue;
            }

            RecetaFinalItemDTO recetaFinal = procesarItemVenta(
                    con,
                    idSucursal,
                    item,
                    referenciaMovimiento,
                    motivoMovimiento
            );

            recetasFinales.add(recetaFinal);
        }

        return recetasFinales;
    }

    private void registrarMovimientosIngredientes(Connection con,
                                                  int idSucursal,
                                                  RecetaFinalItemDTO recetaFinal,
                                                  String referenciaMovimiento,
                                                  String motivoMovimiento) {
        if (recetaFinal == null || recetaFinal.getIngredientesConsumidos() == null) {
            return;
        }

        for (IngredienteConsumidoDTO ingrediente : recetaFinal.getIngredientesConsumidos()) {
            movimientoStockService.registrarSalidaIngrediente(
                    con,
                    idSucursal,
                    ingrediente,
                    referenciaMovimiento,
                    motivoMovimiento
            );
        }
    }
}
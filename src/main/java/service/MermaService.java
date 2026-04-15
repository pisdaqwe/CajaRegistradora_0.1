package service;

import config.DbPool;
import dao.MermaDao;
import dao.MermaItemDao;
import dtoS.IngredienteConsumidoDTO;
import dtoS.MermaItemRequest;
import dtoS.MermaItemResultDTO;
import dtoS.MermaRequest;
import dtoS.MermaResultDTO;
import dtoS.RecetaFinalItemDTO;
import model.TicketItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service principal del caso de uso "registrar merma".
 *
 * RESPONSABILIDADES:
 * - validar request
 * - abrir/cerrar transacción
 * - insertar cabecera y líneas de merma
 * - resolver receta final cuando usarReceta = true
 * - descontar stock de ingredientes
 * - registrar movimientos de stock enlazados con merma y merma_item
 *
 * IMPORTANTE:
 * - NO registra ventas
 * - NO registra pagos
 * - NO genera ticket comercial
 */
public class MermaService {

    private final MermaDao mermaDao;
    private final MermaItemDao mermaItemDao;
    private final RecipeResolverService recipeResolverService;
    private final StockIngredienteService stockIngredienteService;
    private final MovimientoStockService movimientoStockService;

    public MermaService(MermaDao mermaDao,
                        MermaItemDao mermaItemDao,
                        RecipeResolverService recipeResolverService,
                        StockIngredienteService stockIngredienteService,
                        MovimientoStockService movimientoStockService) {

        if (mermaDao == null) {
            throw new IllegalArgumentException("mermaDao no puede ser null");
        }
        if (mermaItemDao == null) {
            throw new IllegalArgumentException("mermaItemDao no puede ser null");
        }
        if (recipeResolverService == null) {
            throw new IllegalArgumentException("recipeResolverService no puede ser null");
        }
        if (stockIngredienteService == null) {
            throw new IllegalArgumentException("stockIngredienteService no puede ser null");
        }
        if (movimientoStockService == null) {
            throw new IllegalArgumentException("movimientoStockService no puede ser null");
        }

        this.mermaDao = mermaDao;
        this.mermaItemDao = mermaItemDao;
        this.recipeResolverService = recipeResolverService;
        this.stockIngredienteService = stockIngredienteService;
        this.movimientoStockService = movimientoStockService;
    }

    /**
     * Registra una merma completa.
     */
    public MermaResultDTO registrarMerma(MermaRequest request) {
        validarRequest(request);

        try (Connection con = DbPool.getConnection()) {
            con.setAutoCommit(false);

            try {
                int idMerma = mermaDao.insert(con, request);

                List<MermaItemResultDTO> itemsPersistidos = new ArrayList<>();

                for (MermaItemRequest item : request.getItems()) {
                    int idMermaItem = mermaItemDao.insert(con, idMerma, item);

                    if (item.isUsarReceta()) {
                        procesarItemConReceta(con, request, idMerma, idMermaItem, item);
                    } else {
                        procesarItemSinReceta(con, request, idMerma, idMermaItem, item);
                    }

                    MermaItemResultDTO itemResult = new MermaItemResultDTO();
                    itemResult.setIdMermaItem(idMermaItem);
                    itemResult.setIdProducto(item.getIdProducto());
                    itemsPersistidos.add(itemResult);
                }

                con.commit();

                MermaResultDTO result = new MermaResultDTO();
                result.setIdMerma(idMerma);
                result.setItemsPersistidos(itemsPersistidos);
                return result;

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Error registrando la merma completa.", e);
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir la conexión para registrar la merma.", e);
        }
    }

    /**
     * Procesa una línea de merma que usa receta:
     * - convierte a TicketItem temporal
     * - resuelve receta final
     * - valida stock
     * - descuenta ingredientes
     * - registra movimientos de stock enlazados a merma
     */
    private void procesarItemConReceta(Connection con,
                                       MermaRequest request,
                                       int idMerma,
                                       int idMermaItem,
                                       MermaItemRequest item) {

        TicketItem ticketItem = MermaTicketMapper.toTicketItem(item);
        RecetaFinalItemDTO recetaFinal = recipeResolverService.resolve(ticketItem);

        stockIngredienteService.validarStockSuficiente(con, request.getIdSucursal(), recetaFinal);
        stockIngredienteService.descontarIngredientes(con, request.getIdSucursal(), recetaFinal);

        registrarMovimientosIngredientesMerma(
                con,
                request,
                idMerma,
                idMermaItem,
                item,
                recetaFinal
        );
    }

    /**
     * Procesa una línea de merma que NO usa receta.
     *
     * Este bloque queda preparado para futuras mermas
     * de producto retail / empaquetado / producto simple.
     *
     * Aquí más adelante podrás:
     * - validar stock_producto
     * - descontar stock_producto
     * - registrar movimiento_stock con idProducto
     */
    private void procesarItemSinReceta(Connection con,
                                       MermaRequest request,
                                       int idMerma,
                                       int idMermaItem,
                                       MermaItemRequest item) {
        // FUTURO:
        // 1) validar stock_producto si aplica
        // 2) descontar stock_producto
        // 3) registrar salida producto enlazada a merma

        // Ejemplo futuro:
        // movimientoStockService.registrarSalidaProductoMerma(
        //         con,
        //         request.getIdSucursal(),
        //         item.getIdProducto(),
        //         item.getCantidad(),
        //         idMerma,
        //         idMermaItem,
        //         buildReferenciaMerma(idMerma, item),
        //         buildMotivoMovimiento(request)
        // );
    }

    private void registrarMovimientosIngredientesMerma(Connection con,
                                                       MermaRequest request,
                                                       int idMerma,
                                                       int idMermaItem,
                                                       MermaItemRequest item,
                                                       RecetaFinalItemDTO recetaFinal) {

        if (recetaFinal == null || recetaFinal.getIngredientesConsumidos() == null) {
            return;
        }

        for (IngredienteConsumidoDTO ingrediente : recetaFinal.getIngredientesConsumidos()) {
            movimientoStockService.registrarSalidaIngredienteMerma(
                    con,
                    request.getIdSucursal(),
                    ingrediente,
                    idMerma,
                    idMermaItem,
                    buildReferenciaMerma(idMerma, item),
                    buildMotivoMovimiento(request)
            );
        }
    }

    private String buildReferenciaMerma(int idMerma, MermaItemRequest item) {
        String nombre = item.getNombreProductoSnapshot() != null
                ? item.getNombreProductoSnapshot().trim()
                : "SIN_PRODUCTO";

        String tamano = item.getNombreTamanoSnapshot() != null
                ? " " + item.getNombreTamanoSnapshot().trim()
                : "";

        return "MERMA:" + idMerma + " - " + nombre + tamano;
    }

    private String buildMotivoMovimiento(MermaRequest request) {
        if (request.getMotivo() == null || request.getMotivo().isBlank()) {
            return "MERMA";
        }
        return "MERMA - " + request.getMotivo().trim();
    }

    private void validarRequest(MermaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("MermaRequest no puede ser null.");
        }

        if (request.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("La merma requiere una sucursal válida.");
        }

        if (request.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("La merma requiere un usuario válido.");
        }

        if (request.getTipoMerma() == null || request.getTipoMerma().isBlank()) {
            throw new IllegalArgumentException("tipoMerma no puede estar vacío.");
        }

        if (request.getOrigen() == null || request.getOrigen().isBlank()) {
            throw new IllegalArgumentException("origen no puede estar vacío.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La merma debe tener al menos un item.");
        }

        for (MermaItemRequest item : request.getItems()) {
            validarItem(item);
        }
    }

    private void validarItem(MermaItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException("La merma contiene un item null.");
        }

        if (item.getIdProducto() <= 0) {
            throw new IllegalArgumentException("Todo item de merma debe tener idProducto válido.");
        }

        if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad del item de merma debe ser > 0.");
        }

        if (item.isUsarReceta() && item.getIdTamano() == null) {
            throw new IllegalArgumentException(
                    "Si usarReceta=true, el item de merma debe tener idTamano."
            );
        }
    }
}
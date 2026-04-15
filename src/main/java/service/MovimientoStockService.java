package service;

import dao.MovimientoStockDao;
import dtoS.IngredienteConsumidoDTO;
import dtoS.RegistrarMovimientoStockRequest;

import java.math.BigDecimal;
import java.sql.Connection;

/**
 * Service para registrar movimientos de stock.
 *
 * RESPONSABILIDADES:
 * - validar request
 * - normalizar datos mínimos
 * - delegar en MovimientoStockDao
 *
 * IMPORTANTE:
 * - aquí sí validamos el XOR producto/ingrediente
 * - el DAO solo inserta
 */
public class MovimientoStockService {

    private final MovimientoStockDao movimientoStockDao;

    public MovimientoStockService(MovimientoStockDao movimientoStockDao) {
        if (movimientoStockDao == null) {
            throw new IllegalArgumentException("movimientoStockDao no puede ser null");
        }
        this.movimientoStockDao = movimientoStockDao;
    }

    /**
     * Registro genérico de movimiento usando conexión externa.
     */
    public void registrar(Connection con, RegistrarMovimientoStockRequest request) {
        validarRequest(request);
        movimientoStockDao.insert(con, request);
    }

    /**
     * Registro genérico sin conexión externa.
     */
    public void registrar(RegistrarMovimientoStockRequest request) {
        validarRequest(request);
        movimientoStockDao.insert(request);
    }

    /**
     * Registra una salida de ingrediente normal.
     *
     * Uso típico:
     * - venta
     * - consumo operativo
     */
    public void registrarSalidaIngrediente(Connection con,
                                           int idSucursal,
                                           IngredienteConsumidoDTO ingrediente,
                                           String referencia,
                                           String motivo) {
        if (ingrediente == null) {
            throw new IllegalArgumentException("ingrediente no puede ser null");
        }

        RegistrarMovimientoStockRequest request = new RegistrarMovimientoStockRequest();
        request.setIdSucursal(idSucursal);
        request.setIdIngrediente(ingrediente.getIdIngrediente());
        request.setCantidad(ingrediente.getCantidad());
        request.setIdUnidad(ingrediente.getIdUnidad());
        request.setTipo("SALIDA");
        request.setReferencia(referencia);
        request.setMotivo(motivo);

        registrar(con, request);
    }

    /**
     * Registra una salida de ingrediente enlazada a una merma concreta.
     *
     * Uso típico:
     * - MermaService cuando usarReceta = true
     */
    public void registrarSalidaIngredienteMerma(Connection con,
                                                int idSucursal,
                                                IngredienteConsumidoDTO ingrediente,
                                                int idMerma,
                                                int idMermaItem,
                                                String referencia,
                                                String motivo) {
        if (ingrediente == null) {
            throw new IllegalArgumentException("ingrediente no puede ser null");
        }
        if (idMerma <= 0) {
            throw new IllegalArgumentException("idMerma debe ser > 0");
        }
        if (idMermaItem <= 0) {
            throw new IllegalArgumentException("idMermaItem debe ser > 0");
        }

        RegistrarMovimientoStockRequest request = new RegistrarMovimientoStockRequest();
        request.setIdSucursal(idSucursal);
        request.setIdIngrediente(ingrediente.getIdIngrediente());
        request.setCantidad(ingrediente.getCantidad());
        request.setIdUnidad(ingrediente.getIdUnidad());
        request.setTipo("SALIDA");
        request.setReferencia(referencia);
        request.setMotivo(motivo);
        request.setIdMerma(idMerma);
        request.setIdMermaItem(idMermaItem);

        registrar(con, request);
    }

    /**
     * Registra una salida de producto enlazada a una merma concreta.
     *
     * Uso típico:
     * - merma de producto retail / empaquetado
     * - merma sin receta
     */
    public void registrarSalidaProductoMerma(Connection con,
                                             int idSucursal,
                                             int idProducto,
                                             BigDecimal cantidad,
                                             int idMerma,
                                             int idMermaItem,
                                             String referencia,
                                             String motivo) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser > 0");
        }
        if (idMerma <= 0) {
            throw new IllegalArgumentException("idMerma debe ser > 0");
        }
        if (idMermaItem <= 0) {
            throw new IllegalArgumentException("idMermaItem debe ser > 0");
        }

        RegistrarMovimientoStockRequest request = new RegistrarMovimientoStockRequest();
        request.setIdSucursal(idSucursal);
        request.setIdProducto(idProducto);
        request.setCantidad(cantidad);
        request.setIdUnidad(null); // stock_producto no usa unidad
        request.setTipo("SALIDA");
        request.setReferencia(referencia);
        request.setMotivo(motivo);
        request.setIdMerma(idMerma);
        request.setIdMermaItem(idMermaItem);

        registrar(con, request);
    }

    private void validarRequest(RegistrarMovimientoStockRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request no puede ser null");
        }

        if (request.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        boolean tieneProducto = request.getIdProducto() != null;
        boolean tieneIngrediente = request.getIdIngrediente() != null;

        // XOR real: uno sí, el otro no
        if (tieneProducto == tieneIngrediente) {
            throw new IllegalArgumentException(
                    "El movimiento debe tener idProducto o idIngrediente, pero no ambos."
            );
        }

        if (request.getCantidad() == null || request.getCantidad().signum() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser > 0");
        }

        if (request.getTipo() == null || request.getTipo().isBlank()) {
            throw new IllegalArgumentException("tipo no puede ser null o vacío");
        }

        String tipo = request.getTipo().trim().toUpperCase();
        if (!tipo.equals("ENTRADA") && !tipo.equals("SALIDA") && !tipo.equals("AJUSTE")) {
            throw new IllegalArgumentException("tipo inválido: " + request.getTipo());
        }

        request.setTipo(tipo);
    }
}
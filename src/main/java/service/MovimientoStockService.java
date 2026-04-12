package service;

import dao.MovimientoStockDao;
import dtoS.IngredienteConsumidoDTO;
import dtoS.RegistrarMovimientoStockRequest;

import java.sql.Connection;

/**
 * Service para registrar movimientos de stock.
 *
 * Reglas:
 * - debe venir idProducto XOR idIngrediente
 * - cantidad > 0
 * - idSucursal > 0
 *
 * En esta fase lo usaremos sobre todo para SALIDA de ingredientes
 * al registrar una venta.
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
     * Registra un movimiento genérico ya montado.
     */
    public void registrar(Connection con, RegistrarMovimientoStockRequest request) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (request == null) {
            throw new IllegalArgumentException("request no puede ser null");
        }

        validarRequest(request);

        movimientoStockDao.insert(con, request);
    }

    /**
     * Método cómodo para registrar la salida de un ingrediente
     * consumido en una venta.
     *
     * Ejemplo de referencia:
     * - VENTA:53 ITEM:137
     */
    public void registrarSalidaIngrediente(Connection con,
                                           int idSucursal,
                                           IngredienteConsumidoDTO ingrediente,
                                           String referencia,
                                           String motivo) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (ingrediente == null) {
            throw new IllegalArgumentException("ingrediente no puede ser null");
        }
        if (ingrediente.getIdIngrediente() <= 0) {
            throw new IllegalArgumentException("idIngrediente inválido");
        }
        if (ingrediente.getCantidad() == null || ingrediente.getCantidad().signum() <= 0) {
            throw new IllegalArgumentException("cantidad inválida");
        }

        RegistrarMovimientoStockRequest request = new RegistrarMovimientoStockRequest();
        request.setIdSucursal(idSucursal);
        request.setIdProducto(null);
        request.setIdIngrediente(ingrediente.getIdIngrediente());
        request.setCantidad(ingrediente.getCantidad());
        request.setIdUnidad(ingrediente.getIdUnidad());
        request.setTipo("SALIDA");
        request.setReferencia(referencia);
        request.setMotivo(motivo);

        registrar(con, request);
    }

    /**
     * Método cómodo para registrar una entrada manual de ingrediente.
     * Lo dejamos ya preparado para futuro.
     */
    public void registrarEntradaIngrediente(Connection con,
                                            int idSucursal,
                                            IngredienteConsumidoDTO ingrediente,
                                            String referencia,
                                            String motivo) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (ingrediente == null) {
            throw new IllegalArgumentException("ingrediente no puede ser null");
        }
        if (ingrediente.getIdIngrediente() <= 0) {
            throw new IllegalArgumentException("idIngrediente inválido");
        }
        if (ingrediente.getCantidad() == null || ingrediente.getCantidad().signum() <= 0) {
            throw new IllegalArgumentException("cantidad inválida");
        }

        RegistrarMovimientoStockRequest request = new RegistrarMovimientoStockRequest();
        request.setIdSucursal(idSucursal);
        request.setIdProducto(null);
        request.setIdIngrediente(ingrediente.getIdIngrediente());
        request.setCantidad(ingrediente.getCantidad());
        request.setIdUnidad(ingrediente.getIdUnidad());
        request.setTipo("ENTRADA");
        request.setReferencia(referencia);
        request.setMotivo(motivo);

        registrar(con, request);
    }

    private void validarRequest(RegistrarMovimientoStockRequest request) {
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
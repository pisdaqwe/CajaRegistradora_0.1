package dao;

import config.DbPool;
import dtoS.RegistrarMovimientoStockRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * DAO para registrar movimientos de stock.
 *
 * IMPORTANTE:
 * - este DAO NO valida el XOR producto/ingrediente
 * - esa validación pertenece al Service
 *
 * AJUSTADO A TU TABLA REAL movimiento_stock:
 * - la columna de fecha se llama "fecha"
 * - id_merma e id_merma_item son opcionales y aquí no se usan
 */
public class MovimientoStockDao {

    private static final String SQL_INSERT = """
        INSERT INTO movimiento_stock (
            id_sucursal,
            id_producto,
            id_ingrediente,
            tipo,
            cantidad,
            id_unidad,
            fecha,
            referencia,
            motivo
        ) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)
        """;

    public void insert(Connection con, RegistrarMovimientoStockRequest request) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (request == null) {
            throw new IllegalArgumentException("request no puede ser null");
        }

        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, request.getIdSucursal());

            if (request.getIdProducto() != null) {
                ps.setInt(2, request.getIdProducto());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            if (request.getIdIngrediente() != null) {
                ps.setInt(3, request.getIdIngrediente());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, request.getTipo());
            ps.setBigDecimal(5, request.getCantidad());

            if (request.getIdUnidad() != null) {
                ps.setInt(6, request.getIdUnidad());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, request.getReferencia());
            ps.setString(8, request.getMotivo());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error insertando movimiento_stock.", e);
        }
    }

    /**
     * Variante cómoda por si en otro punto quieres insertar
     * fuera de una transacción externa.
     */
    public void insert(RegistrarMovimientoStockRequest request) {
        try (Connection cn = DbPool.getConnection()) {
            insert(cn, request);
        } catch (Exception e) {
            throw new RuntimeException("Error insertando movimiento_stock.", e);
        }
    }
}
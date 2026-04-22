
package dao;

import config.DbPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla producto_estacion.
 *
 * Fuente oficial para resolver a qué estación o estaciones
 * pertenece un producto.
 */
public class ProductoEstacionDao {

    private static final String SQL_FIND_IDS_ESTACION_BY_PRODUCTO = """
            SELECT id_estacion
            FROM producto_estacion
            WHERE id_producto = ?
            ORDER BY id_estacion
            """;

    public ProductoEstacionDao() {
    }

    /**
     * Devuelve la lista de estaciones asociadas a un producto.
     *
     * Aunque actualmente cada producto tenga una sola estación,
     * el diseño de BD soporta varias, por eso devolvemos lista.
     */
    public List<Integer> findIdsEstacionByProducto(int idProducto) {
        List<Integer> ids = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_IDS_ESTACION_BY_PRODUCTO)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_estacion"));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al buscar estaciones del producto id=" + idProducto, e
            );
        }

        return ids;
    }
    public List<Integer> findIdsEstacionByProductoYSucursal(int idProducto, int idSucursal) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El id del producto debe ser mayor que 0.");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de sucursal debe ser mayor que 0.");
        }

        String sql = """
                SELECT pe.id_estacion
                FROM producto_estacion pe
                JOIN estacion e ON e.id_estacion = pe.id_estacion
                WHERE pe.id_producto = ?
                  AND e.id_sucursal = ?
                ORDER BY pe.id_estacion
                """;

        List<Integer> ids = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ps.setInt(2, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_estacion"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error obteniendo estaciones del producto " + idProducto
                            + " para la sucursal " + idSucursal, e
            );
        }

        return ids;
    }
}
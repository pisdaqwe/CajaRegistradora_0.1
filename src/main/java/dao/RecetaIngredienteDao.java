package dao;

import config.DbPool;
import dtoS.RecetaIngredienteDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que carga la receta base de un producto+tamaño.
 */
public class RecetaIngredienteDao {

    private static final String SQL_FIND_BY_PRODUCTO_Y_TAMANO = """
        SELECT
            ri.id_producto,
            ri.id_tamano,
            ri.id_ingrediente,
            i.nombre AS nombre_ingrediente,
            ri.cantidad,
            ri.id_unidad,
            u.nombre AS nombre_unidad
        FROM receta_ingrediente ri
        INNER JOIN ingrediente i
            ON i.id_ingrediente = ri.id_ingrediente
        INNER JOIN unidad_ingrediente u
            ON u.id_unidad = ri.id_unidad
        WHERE ri.id_producto = ?
          AND ri.id_tamano = ?
        ORDER BY ri.id_ingrediente
        """;

    public List<RecetaIngredienteDTO> findByProductoYTamano(int idProducto, int idTamano) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (idTamano <= 0) {
            throw new IllegalArgumentException("idTamano debe ser > 0");
        }

        List<RecetaIngredienteDTO> lista = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_FIND_BY_PRODUCTO_Y_TAMANO)) {

            ps.setInt(1, idProducto);
            ps.setInt(2, idTamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecetaIngredienteDTO dto = new RecetaIngredienteDTO();
                    dto.setIdProducto(rs.getInt("id_producto"));
                    dto.setIdTamano(rs.getInt("id_tamano"));
                    dto.setIdIngrediente(rs.getInt("id_ingrediente"));
                    dto.setNombreIngrediente(rs.getString("nombre_ingrediente"));
                    dto.setCantidad(rs.getBigDecimal("cantidad"));
                    dto.setIdUnidad(rs.getInt("id_unidad"));
                    dto.setNombreUnidad(rs.getString("nombre_unidad"));
                    lista.add(dto);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando receta base de producto=" + idProducto + " tamaño=" + idTamano,
                    e
            );
        }

        return lista;
    }
}
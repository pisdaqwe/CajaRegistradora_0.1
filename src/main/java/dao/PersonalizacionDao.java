package dao;

import config.DbPool;
import dtoS.PersonalizacionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonalizacionDao {

    public List<PersonalizacionDTO> findActivasByProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }

        String sql = """
            SELECT
                p.id_personalizacion,
                p.nombre,
                p.tipo,
                p.precio
            FROM producto_personalizacion pp
            JOIN personalizacion p ON p.id_personalizacion = pp.id_personalizacion
            WHERE pp.id_producto = ?
              AND p.activo = 1
            ORDER BY p.orden, p.nombre
            """;

        List<PersonalizacionDTO> personalizaciones = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    personalizaciones.add(mapPersonalizacion(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando personalizaciones del producto " + idProducto, e);
        }

        return personalizaciones;
    }

    private PersonalizacionDTO mapPersonalizacion(ResultSet rs) throws SQLException {
        return new PersonalizacionDTO(
                rs.getInt("id_personalizacion"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getBigDecimal("precio")
        );
    }
}
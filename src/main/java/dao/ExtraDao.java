package dao;

import config.DbPool;
import dtoS.ExtraDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExtraDao {

    public List<ExtraDTO> findActivosByProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }

        String sql = """
            SELECT
                e.id_extra,
                e.nombre,
                e.tipo,
                e.precio
            FROM producto_extra pe
            JOIN extra e ON e.id_extra = pe.id_extra
            WHERE pe.id_producto = ?
              AND e.activo = 1
            ORDER BY e.orden, e.nombre
            """;

        List<ExtraDTO> extras = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    extras.add(mapExtra(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando extras del producto " + idProducto, e);
        }

        return extras;
    }

    private ExtraDTO mapExtra(ResultSet rs) throws SQLException {
        return new ExtraDTO(
                rs.getInt("id_extra"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getBigDecimal("precio")
        );
    }
}
package dao;

import config.DbPool;
import dtoS.CategoriaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    public List<CategoriaDTO> findActivasOrdenadas() {
        String sql = """
            SELECT id_categoria, nombre, color_hex, orden
            FROM categoria
            WHERE activa = 1
            ORDER BY orden ASC, id_categoria ASC
        """;

        List<CategoriaDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new CategoriaDTO(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        rs.getString("color_hex"),
                        rs.getInt("orden")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando categorias activas", e);
        }

        return out;
    }
}

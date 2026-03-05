package dao;

import config.DbPool;
import dtoS.SubCategoriaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SubcategoriaDao {

    public List<SubCategoriaDTO> findActivasByCategoriaOrdenadas(int idCategoria) {
        String sql = """
            SELECT id_subcategoria, id_categoria, nombre, orden
            FROM subcategoria
            WHERE activa = 1 AND id_categoria = ?
            ORDER BY orden ASC, id_subcategoria ASC
        """;

        List<SubCategoriaDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new SubCategoriaDTO(
                            rs.getInt("id_subcategoria"),
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getInt("orden")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando subcategorias activas por categoria " + idCategoria, e);
        }

        return out;
    }
}

package dao;

import config.DbPool;
import dtoS.ProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    public List<ProductoDTO> findBySubcategoriaOrdenados(int idSubcategoria) {
        String sql = """
            SELECT id_producto, id_subcategoria, nombre, orden, permite_extras, permite_personalizacion
            FROM producto
            WHERE activo = 1
              AND visible_tpv = 1
              AND id_subcategoria = ?
            ORDER BY orden ASC, id_producto ASC
        """;

        List<ProductoDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapProductoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando productos por subcategoria " + idSubcategoria, e);
        }

        return out;
    }

    public List<ProductoDTO> findTopBySubcategoriaOrdenados(int idSubcategoria, int limit) {
        String sql = """
            SELECT id_producto, id_subcategoria, nombre, orden, permite_extras, permite_personalizacion
            FROM producto
            WHERE activo = 1
              AND visible_tpv = 1
              AND id_subcategoria = ?
            ORDER BY orden ASC, id_producto ASC
            LIMIT ?
        """;

        List<ProductoDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapProductoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando TOP productos por subcategoria " + idSubcategoria, e);
        }

        return out;
    }

    private ProductoDTO mapProductoDTO(ResultSet rs) throws Exception {
        return new ProductoDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre"),
                rs.getInt("orden"),
                rs.getBoolean("permite_extras"),
                rs.getBoolean("permite_personalizacion")
        );
    }
}

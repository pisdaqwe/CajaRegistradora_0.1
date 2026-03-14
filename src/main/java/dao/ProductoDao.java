package dao;

import config.DbPool;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<ProductoDTO> findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }

        String sql = """
            SELECT id_producto, id_subcategoria, nombre, orden, permite_extras, permite_personalizacion
            FROM producto
            WHERE activo = 1
              AND visible_tpv = 1
              AND sku = ?
            LIMIT 1
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sku.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProductoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando producto por SKU: " + sku, e);
        }

        return Optional.empty();
    }
    public List<ProductoBusquedaRowDTO> findFilasBusquedaProducto() {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre AS nombre_producto,
                p.permite_extras,
                p.permite_personalizacion,
                t.id_tamano,
                t.nombre AS nombre_tamano,
                pt.precio
            FROM producto p
            JOIN producto_tamano pt ON pt.id_producto = p.id_producto
            JOIN tamano t ON t.id_tamano = pt.id_tamano
            WHERE p.activo = 1
              AND p.visible_tpv = 1
            ORDER BY p.nombre ASC, t.orden ASC, t.id_tamano ASC
        """;

        List<ProductoBusquedaRowDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(mapProductoBusquedaRowDTO(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error cargando filas de búsqueda de productos", e);
        }

        return out;
    }
    private ProductoBusquedaRowDTO mapProductoBusquedaRowDTO(ResultSet rs) throws Exception {
        return new ProductoBusquedaRowDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre_producto"),
                rs.getBoolean("permite_extras"),
                rs.getBoolean("permite_personalizacion"),
                rs.getInt("id_tamano"),
                rs.getString("nombre_tamano"),
                rs.getBigDecimal("precio")
        );
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

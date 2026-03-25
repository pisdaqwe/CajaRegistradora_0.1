package dao;

import config.DbPool;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoCatalogoDTO;
import dtoS.ProductoDTO;
import enums.ModoDisponibilidadProducto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de acceso a productos para el TPV.
 *
 * Responsabilidades:
 * - cargar productos por subcategoría
 * - cargar top productos por subcategoría
 * - buscar producto por SKU
 * - cargar filas para el buscador de productos
 * - cargar catálogo operativo por sucursal
 *
 * IMPORTANTE:
 * Esta versión ya trae también:
 * - iva_porcentaje
 * - permite_stock_cantidad
 * - lectura de stock_producto para catálogo operativo
 */
public class ProductoDao {

    // =====================================================
    // CONSULTAS DE PRODUCTOS POR SUBCATEGORÍA (BASE)
    // =====================================================

    public List<ProductoDTO> findBySubcategoriaOrdenados(int idSubcategoria) {
        String sql = """
            SELECT
                id_producto,
                id_subcategoria,
                nombre,
                orden,
                permite_extras,
                permite_personalizacion,
                iva_porcentaje,
                permite_stock_cantidad
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
            throw new RuntimeException(
                    "Error cargando productos por subcategoría " + idSubcategoria,
                    e
            );
        }

        return out;
    }

    public List<ProductoDTO> findTopBySubcategoriaOrdenados(int idSubcategoria, int limit) {
        String sql = """
            SELECT
                id_producto,
                id_subcategoria,
                nombre,
                orden,
                permite_extras,
                permite_personalizacion,
                iva_porcentaje,
                permite_stock_cantidad
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
            throw new RuntimeException(
                    "Error cargando TOP productos por subcategoría " + idSubcategoria,
                    e
            );
        }

        return out;
    }

    // =====================================================
    // CATÁLOGO OPERATIVO POR SUCURSAL
    // =====================================================

    public List<ProductoCatalogoDTO> findCatalogoBySubcategoriaYSucursal(int idSubcategoria, int idSucursal) {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre,
                p.orden,
                p.permite_extras,
                p.permite_personalizacion,
                p.iva_porcentaje,
                p.permite_stock_cantidad,
                sp.modo_disponibilidad,
                sp.stock
            FROM producto p
            JOIN stock_producto sp
                ON sp.id_producto = p.id_producto
            WHERE p.activo = 1
              AND p.visible_tpv = 1
              AND p.id_subcategoria = ?
              AND sp.id_sucursal = ?
            ORDER BY p.orden ASC, p.id_producto ASC
        """;

        List<ProductoCatalogoDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);
            ps.setInt(2, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapProductoCatalogoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando catálogo operativo para subcategoría " + idSubcategoria
                            + " y sucursal " + idSucursal,
                    e
            );
        }

        return out;
    }

    public List<ProductoCatalogoDTO> findTopCatalogoBySubcategoriaYSucursal(int idSubcategoria, int idSucursal, int limit) {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre,
                p.orden,
                p.permite_extras,
                p.permite_personalizacion,
                p.iva_porcentaje,
                p.permite_stock_cantidad,
                sp.modo_disponibilidad,
                sp.stock
            FROM producto p
            JOIN stock_producto sp
                ON sp.id_producto = p.id_producto
            WHERE p.activo = 1
              AND p.visible_tpv = 1
              AND p.id_subcategoria = ?
              AND sp.id_sucursal = ?
            ORDER BY p.orden ASC, p.id_producto ASC
            LIMIT ?
        """;

        List<ProductoCatalogoDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);
            ps.setInt(2, idSucursal);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapProductoCatalogoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando TOP catálogo operativo para subcategoría " + idSubcategoria
                            + " y sucursal " + idSucursal,
                    e
            );
        }

        return out;
    }

    public Optional<ProductoCatalogoDTO> findCatalogoBySkuYSucursal(String sku, int idSucursal) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }

        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre,
                p.orden,
                p.permite_extras,
                p.permite_personalizacion,
                p.iva_porcentaje,
                p.permite_stock_cantidad,
                sp.modo_disponibilidad,
                sp.stock
            FROM producto p
            JOIN stock_producto sp
                ON sp.id_producto = p.id_producto
            WHERE p.activo = 1
              AND p.visible_tpv = 1
              AND p.sku = ?
              AND sp.id_sucursal = ?
            LIMIT 1
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sku.trim());
            ps.setInt(2, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapProductoCatalogoDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error buscando producto operativo por SKU: " + sku + " en sucursal " + idSucursal,
                    e
            );
        }

        return Optional.empty();
    }

    public List<ProductoBusquedaRowDTO> findFilasBusquedaProductoBySucursal(int idSucursal) {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre AS nombre_producto,
                p.permite_extras,
                p.permite_personalizacion,
                p.permite_stock_cantidad,
                p.iva_porcentaje,
                t.id_tamano,
                t.nombre AS nombre_tamano,
                pt.precio,
                sp.modo_disponibilidad,
                sp.stock
            FROM producto p
            JOIN stock_producto sp
                ON sp.id_producto = p.id_producto
            JOIN producto_tamano pt
                ON pt.id_producto = p.id_producto
            JOIN tamano t
                ON t.id_tamano = pt.id_tamano
            WHERE p.activo = 1
              AND p.visible_tpv = 1
              AND sp.id_sucursal = ?
            ORDER BY p.nombre ASC, t.orden ASC, t.id_tamano ASC
        """;

        List<ProductoBusquedaRowDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapProductoBusquedaRowDTOOperativo(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando filas operativas de búsqueda para sucursal " + idSucursal,
                    e
            );
        }

        return out;
    }

    // =====================================================
    // MAPEOS
    // =====================================================

    private ProductoBusquedaRowDTO mapProductoBusquedaRowDTOOperativo(ResultSet rs) throws Exception {
        return new ProductoBusquedaRowDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre_producto"),
                rs.getBoolean("permite_extras"),
                rs.getBoolean("permite_personalizacion"),
                rs.getBoolean("permite_stock_cantidad"),
                rs.getInt("id_tamano"),
                rs.getString("nombre_tamano"),
                rs.getBigDecimal("precio"),
                rs.getBigDecimal("iva_porcentaje"),
                ModoDisponibilidadProducto.valueOf(rs.getString("modo_disponibilidad")),
                rs.getBigDecimal("stock")
        );
    }

    private ProductoDTO mapProductoDTO(ResultSet rs) throws Exception {
        return new ProductoDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre"),
                rs.getInt("orden"),
                rs.getBoolean("permite_extras"),
                rs.getBoolean("permite_personalizacion"),
                rs.getBigDecimal("iva_porcentaje"),
                rs.getBoolean("permite_stock_cantidad")
        );
    }

    private ProductoCatalogoDTO mapProductoCatalogoDTO(ResultSet rs) throws Exception {
        return new ProductoCatalogoDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre"),
                rs.getInt("orden"),
                rs.getBigDecimal("iva_porcentaje"),
                rs.getBoolean("permite_extras"),
                rs.getBoolean("permite_personalizacion"),
                rs.getBoolean("permite_stock_cantidad"),
                ModoDisponibilidadProducto.valueOf(rs.getString("modo_disponibilidad")),
                rs.getBigDecimal("stock")
        );
    }
}
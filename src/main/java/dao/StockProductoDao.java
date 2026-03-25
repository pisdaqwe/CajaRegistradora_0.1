package dao;

import config.DbPool;
import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockProductoDao {

    // =====================================================
    // CONSULTA GENERAL DE DISPONIBILIDAD POR SUCURSAL
    // =====================================================

    public List<StockProductoDisponibilidadDTO> findDisponibilidadBySucursal(int idSucursal) {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre AS nombre_producto,
                s.nombre AS nombre_subcategoria,
                p.permite_stock_cantidad,
                sp.modo_disponibilidad,
                sp.stock
            FROM stock_producto sp
            JOIN producto p
                ON p.id_producto = sp.id_producto
            JOIN subcategoria s
                ON s.id_subcategoria = p.id_subcategoria
            WHERE sp.id_sucursal = ?
              AND p.activo = 1
              AND p.visible_tpv = 1
            ORDER BY s.orden ASC, p.orden ASC, p.id_producto ASC
        """;

        List<StockProductoDisponibilidadDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapDisponibilidadDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando disponibilidad de productos para sucursal " + idSucursal,
                    e
            );
        }

        return out;
    }

    // =====================================================
    // CONSULTA DE UN PRODUCTO CONCRETO POR SUCURSAL
    // =====================================================

    public Optional<StockProductoDisponibilidadDTO> findByProductoYSucursal(int idProducto, int idSucursal) {
        String sql = """
            SELECT
                p.id_producto,
                p.id_subcategoria,
                p.nombre AS nombre_producto,
                s.nombre AS nombre_subcategoria,
                p.permite_stock_cantidad,
                sp.modo_disponibilidad,
                sp.stock
            FROM stock_producto sp
            JOIN producto p
                ON p.id_producto = sp.id_producto
            JOIN subcategoria s
                ON s.id_subcategoria = p.id_subcategoria
            WHERE sp.id_sucursal = ?
              AND sp.id_producto = ?
              AND p.activo = 1
              AND p.visible_tpv = 1
            LIMIT 1
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDisponibilidadDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando disponibilidad del producto " + idProducto + " para sucursal " + idSucursal,
                    e
            );
        }

        return Optional.empty();
    }

    // =====================================================
    // UPDATE DE DISPONIBILIDAD
    // =====================================================

    public void updateDisponibilidadProducto(
            int idSucursal,
            int idProducto,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stock
    ) {
        String sql = """
            UPDATE stock_producto
               SET modo_disponibilidad = ?,
                   stock = ?
             WHERE id_sucursal = ?
               AND id_producto = ?
        """;

        BigDecimal stockSeguro = stock != null ? stock : BigDecimal.ZERO;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, modoDisponibilidad.name());
            ps.setBigDecimal(2, stockSeguro);
            ps.setInt(3, idSucursal);
            ps.setInt(4, idProducto);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException(
                        "No se encontró fila en stock_producto para sucursal=" + idSucursal
                                + " producto=" + idProducto
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error actualizando disponibilidad del producto " + idProducto
                            + " en sucursal " + idSucursal,
                    e
            );
        }
    }

    // =====================================================
    // MAPEO
    // =====================================================

    private StockProductoDisponibilidadDTO mapDisponibilidadDTO(ResultSet rs) throws Exception {
        return new StockProductoDisponibilidadDTO(
                rs.getInt("id_producto"),
                rs.getInt("id_subcategoria"),
                rs.getString("nombre_producto"),
                rs.getString("nombre_subcategoria"),
                rs.getBoolean("permite_stock_cantidad"),
                ModoDisponibilidadProducto.valueOf(rs.getString("modo_disponibilidad")),
                rs.getBigDecimal("stock")
        );
    }
}

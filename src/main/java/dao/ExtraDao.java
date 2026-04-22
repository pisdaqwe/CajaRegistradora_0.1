package dao;

import config.DbPool;
import dtoS.ExtraDTO;
import dtoS.StockExtraDisponibilidadDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtraDao {

    /**
     * Método legacy/base.
     *
     * No aplica disponibilidad por sucursal.
     * Marca todos los extras como disponibles = true.
     *
     * Lo dejamos temporalmente para no romper el código viejo.
     */
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
                    extras.add(mapExtraLegacy(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando extras del producto " + idProducto, e);
        }

        return extras;
    }

    /**
     * Método nuevo.
     *
     * Aplica disponibilidad real por sucursal a través de disponibilidad_extra.
     */
    public List<ExtraDTO> findActivosByProductoYSucursal(int idProducto, int idSucursal) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        String sql = """
            SELECT
                e.id_extra,
                e.nombre,
                e.tipo,
                e.precio,
                COALESCE(de.disponible, 1) AS disponible
            FROM producto_extra pe
            JOIN extra e
                ON e.id_extra = pe.id_extra
            LEFT JOIN disponibilidad_extra de
                ON de.id_extra = e.id_extra
               AND de.id_sucursal = ?
            WHERE pe.id_producto = ?
              AND e.activo = 1
            ORDER BY e.orden, e.nombre
            """;

        List<ExtraDTO> extras = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    extras.add(mapExtraOperativo(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error cargando extras del producto " + idProducto + " para sucursal " + idSucursal,
                    e
            );
        }

        return extras;
    }
    public List<StockExtraDisponibilidadDTO> findDisponibilidadBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        String sql = """
            SELECT
                e.id_extra,
                e.nombre,
                e.tipo,
                COALESCE(de.disponible, 1) AS disponible
            FROM extra e
            LEFT JOIN disponibilidad_extra de
                ON de.id_extra = e.id_extra
               AND de.id_sucursal = ?
            WHERE e.activo = 1
            ORDER BY e.tipo, e.orden, e.nombre
            """;

        List<StockExtraDisponibilidadDTO> extras = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    extras.add(mapExtraDisponibilidad(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error cargando disponibilidad de extras para sucursal " + idSucursal,
                    e
            );
        }

        return extras;
    }

    public Optional<StockExtraDisponibilidadDTO> findDisponibilidadByExtraYSucursal(int idExtra, int idSucursal) {
        if (idExtra <= 0) {
            throw new IllegalArgumentException("idExtra debe ser > 0");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        String sql = """
            SELECT
                e.id_extra,
                e.nombre,
                e.tipo,
                COALESCE(de.disponible, 1) AS disponible
            FROM extra e
            LEFT JOIN disponibilidad_extra de
                ON de.id_extra = e.id_extra
               AND de.id_sucursal = ?
            WHERE e.id_extra = ?
              AND e.activo = 1
            LIMIT 1
            """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);
            ps.setInt(2, idExtra);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapExtraDisponibilidad(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error cargando disponibilidad del extra " + idExtra + " para sucursal " + idSucursal,
                    e
            );
        }

        return Optional.empty();
    }

    public void updateDisponibilidadExtra(int idSucursal, int idExtra, boolean disponible) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (idExtra <= 0) {
            throw new IllegalArgumentException("idExtra debe ser > 0");
        }

        String sql = """
            UPDATE disponibilidad_extra
               SET disponible = ?
             WHERE id_sucursal = ?
               AND id_extra = ?
            """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, disponible);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idExtra);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException(
                        "No existe fila en disponibilidad_extra para sucursal=" + idSucursal
                                + " extra=" + idExtra
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error actualizando disponibilidad del extra " + idExtra + " en sucursal " + idSucursal,
                    e
            );
        }
    }
    public List<ExtraDTO> findTodosActivosOrdenados() {
        String sql = """
            SELECT
                id_extra,
                nombre,
                precio,
                tipo
            FROM extra
            WHERE activo = 1
            ORDER BY orden, nombre
        """;

        List<ExtraDTO> extras = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                extras.add(mapExtraLegacy(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando todos los extras activos para informes", e);
        }

        return extras;
    }
    private StockExtraDisponibilidadDTO mapExtraDisponibilidad(ResultSet rs) throws SQLException {
        return new StockExtraDisponibilidadDTO(
                rs.getInt("id_extra"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getBoolean("disponible")
        );
    }
    private ExtraDTO mapExtraLegacy(ResultSet rs) throws SQLException {
        return new ExtraDTO(
                rs.getInt("id_extra"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getBigDecimal("precio"),
                true
        );
    }

    private ExtraDTO mapExtraOperativo(ResultSet rs) throws SQLException {
        return new ExtraDTO(
                rs.getInt("id_extra"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getBigDecimal("precio"),
                rs.getBoolean("disponible")
        );
    }
}
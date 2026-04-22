package dao;

import config.DbPool;
import model.Estacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstacionDao {

    public Optional<Estacion> findById(int idEstacion) {
        if (idEstacion <= 0) {
            throw new IllegalArgumentException("El id de estación debe ser mayor que 0.");
        }

        String sql = """
                SELECT id_estacion, id_sucursal, nombre, codigo, descripcion
                FROM estacion
                WHERE id_estacion = ?
                """;

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstacion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando estación por id: " + idEstacion, e);
        }
    }

    public List<Estacion> findBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de sucursal debe ser mayor que 0.");
        }

        String sql = """
                SELECT id_estacion, id_sucursal, nombre, codigo, descripcion
                FROM estacion
                WHERE id_sucursal = ?
                ORDER BY nombre
                """;

        List<Estacion> estaciones = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estaciones.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error buscando estaciones de la sucursal " + idSucursal, e
            );
        }

        return estaciones;
    }

    public Optional<Estacion> findByCodigoAndSucursal(String codigo, int idSucursal) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de estación no puede estar vacío.");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("El id de sucursal debe ser mayor que 0.");
        }

        String sql = """
                SELECT id_estacion, id_sucursal, nombre, codigo, descripcion
                FROM estacion
                WHERE codigo = ?
                  AND id_sucursal = ?
                """;

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo.trim());
            ps.setInt(2, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error buscando estación código " + codigo + " en sucursal " + idSucursal, e
            );
        }
    }

    private Estacion map(ResultSet rs) throws SQLException {
        Estacion e = new Estacion();
        e.setIdEstacion(rs.getInt("id_estacion"));
        e.setIdSucursal(rs.getInt("id_sucursal"));
        e.setNombre(rs.getString("nombre"));
        e.setCodigo(rs.getString("codigo"));
        e.setDescripcion(rs.getString("descripcion"));
        return e;
    }
}
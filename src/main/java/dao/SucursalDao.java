package dao;

import config.DbPool;
import model.Sucursal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de acceso a datos para la entidad Sucursal.
 */
public class SucursalDao {

    /**
     * Busca una sucursal por su ID.
     */
    public Optional<Sucursal> findById(int idSucursal) {

        String sql = """
            SELECT id_sucursal, nombre, direccion, telefono
            FROM sucursal
            WHERE id_sucursal = ?
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSucursal(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sucursal por id", e);
        }

        return Optional.empty();
    }

    /**
     * Devuelve todas las sucursales del sistema.
     */
    public List<Sucursal> findAll() {

        String sql = """
            SELECT id_sucursal, nombre, direccion, telefono
            FROM sucursal
            ORDER BY nombre
        """;

        List<Sucursal> sucursales = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sucursales.add(mapSucursal(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error listando sucursales", e);
        }

        return sucursales;
    }

    // =========================
    // Mapper
    // =========================

    private Sucursal mapSucursal(ResultSet rs) throws SQLException {

        Sucursal s = new Sucursal();
        s.setIdSucursal(rs.getInt("id_sucursal"));
        s.setNombre(rs.getString("nombre"));
        s.setDireccion(rs.getString("direccion"));
        s.setTelefono(rs.getString("telefono"));

        return s;
    }
}


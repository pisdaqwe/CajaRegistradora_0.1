package dao;

import config.DbPool;
import model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de roles.
 *
 * Responsabilidades:
 * - Leer roles desde la base de datos.
 *
 * Suposiciones:
 * - Existe una tabla 'rol' con columnas:
 *   id_rol, nombre, descripcion
 * - Existe un model.Rol con setters/getters estándar.
 */
public class RolDao {

    private static final String SQL_FIND_ALL =
            "SELECT id_rol, nombre, descripcion " +
            "FROM rol " +
            "ORDER BY id_rol";

    private static final String SQL_FIND_BY_ID =
            "SELECT id_rol, nombre, descripcion " +
            "FROM rol " +
            "WHERE id_rol = ?";

    public List<Rol> findAll() {
        List<Rol> roles = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener roles", e);
        }

        return roles;
    }

    public Optional<Rol> findById(int idRol) {
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idRol);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar rol por id: " + idRol, e);
        }

        return Optional.empty();
    }

    private Rol mapRow(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombre(rs.getString("nombre"));
        rol.setDescripcion(rs.getString("descripcion"));
        return rol;
    }
}
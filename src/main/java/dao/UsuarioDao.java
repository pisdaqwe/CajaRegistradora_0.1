package dao;

import config.DbPool;
import dtoS.FichajeActivoDTO;
import model.Rol;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    // =========================
    // BUSCAR USUARIO POR ID
    // =========================
    public Optional<Usuario> findById(int idUsuario) {

        String sql = """
            SELECT u.id_usuario,
                   u.nombre,
                   u.usuario,
                   u.pin_hash,
                   u.activo,
                   r.id_rol,
                   r.nombre AS rol_nombre
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            WHERE u.id_usuario = ?
        """;

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuario(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario por id", e);
        }

        return Optional.empty();
    }

    // =========================
    // BUSCAR USUARIO POR USERNAME
    // =========================
    public Optional<Usuario> findByUsuario(String usuario) {

        String sql = """
            SELECT u.id_usuario,
                   u.nombre,
                   u.usuario,
                   u.pin_hash,
                   u.activo,
                   r.id_rol,
                   r.nombre AS rol_nombre
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            WHERE u.usuario = ?
        """;

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUsuario(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario por username", e);
        }

        return Optional.empty();
    }
    

    // =========================
    // MAPEO RESULTSET → USUARIO
    // =========================
    private Usuario mapUsuario(ResultSet rs) throws SQLException {

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setUsuario(rs.getString("usuario"));
        u.setPinHash(rs.getString("pin_hash"));
        u.setActivo(rs.getBoolean("activo"));

        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombre(rs.getString("rol_nombre"));
        u.setRol(rol);

        return u;
    }
}


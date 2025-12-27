package dao;





import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import config.DbPool;
import model.Rol;
import model.Usuario;

public class UsuarioDao {

    private static final String SQL_FIND_BY_USUARIO =
        "SELECT u.id_usuario, u.nombre, u.usuario, u.pin_hash, u.activo, " +
        "r.id_rol, r.nombre AS rol_nombre " +
        "FROM usuario u " +
        "JOIN rol r ON u.id_rol = r.id_rol " +
        "WHERE u.usuario = ?";

    public Optional<Usuario> findByUsuario(String usuario) {

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USUARIO)) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    return Optional.of(u);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario por código", e);
        }

        return Optional.empty();
    }
}

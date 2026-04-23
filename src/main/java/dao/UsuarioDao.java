package dao;

import config.DbPool;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import model.Rol;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao {

    public Optional<Usuario> findById(int idUsuario) {
        String sql = """
            SELECT u.id_usuario,
                   u.nombre,
                   u.usuario,
                   u.pin_hash,
                   u.activo,
                   u.id_sucursal,
                   u.fecha_creacion,
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

    public Optional<Usuario> findByUsuario(String usuario) {
        String sql = """
            SELECT u.id_usuario,
                   u.nombre,
                   u.usuario,
                   u.pin_hash,
                   u.activo,
                   u.id_sucursal,
                   u.fecha_creacion,
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

    public List<Usuario> findActivosBySucursal(int idSucursal) {
        String sql = """
            SELECT u.id_usuario,
                   u.nombre,
                   u.usuario,
                   u.pin_hash,
                   u.activo,
                   u.id_sucursal,
                   u.fecha_creacion,
                   r.id_rol,
                   r.nombre AS rol_nombre
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            WHERE u.activo = 1
              AND u.id_sucursal = ?
            ORDER BY u.nombre
        """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapUsuario(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuarios activos por sucursal", e);
        }

        return usuarios;
    }
    public List<EmpleadoRowDTO> findRowsByFiltro(EmpleadoFiltroDTO filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("u.id_usuario, u.nombre, u.usuario, ")
           .append("r.nombre AS nombre_rol, ")
           .append("u.id_sucursal, s.nombre AS nombre_sucursal, ")
           .append("u.activo, ")
           .append("EXISTS (")
           .append("   SELECT 1 FROM fichaje f ")
           .append("   WHERE f.id_usuario = u.id_usuario ")
           .append("     AND f.estado = 'ABIERTO'")
           .append(") AS fichado, ")
           .append("EXISTS (")
           .append("   SELECT 1 ")
           .append("   FROM sesion_caja sc ")
           .append("   WHERE sc.id_usuario_apertura = u.id_usuario ")
           .append("     AND sc.estado = 'ABIERTA'")
           .append(") AS caja_abierta, ")
           .append("(")
           .append("   SELECT c.id_caja ")
           .append("   FROM sesion_caja sc ")
           .append("   JOIN caja c ON c.id_caja = sc.id_caja ")
           .append("   WHERE sc.id_usuario_apertura = u.id_usuario ")
           .append("     AND sc.estado = 'ABIERTA' ")
           .append("   ORDER BY sc.id_sesion DESC ")
           .append("   LIMIT 1")
           .append(") AS id_caja_actual, ")
           .append("(")
           .append("   SELECT c.nombre ")
           .append("   FROM sesion_caja sc ")
           .append("   JOIN caja c ON c.id_caja = sc.id_caja ")
           .append("   WHERE sc.id_usuario_apertura = u.id_usuario ")
           .append("     AND sc.estado = 'ABIERTA' ")
           .append("   ORDER BY sc.id_sesion DESC ")
           .append("   LIMIT 1")
           .append(") AS nombre_caja_actual ")
           .append("FROM usuario u ")
           .append("JOIN rol r ON r.id_rol = u.id_rol ")
           .append("JOIN sucursal s ON s.id_sucursal = u.id_sucursal ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filtro != null) {
            if (filtro.hasTextoBusqueda()) {
                sql.append("AND (u.nombre LIKE ? OR u.usuario LIKE ?) ");
                String like = "%" + filtro.getTextoBusqueda().trim() + "%";
                params.add(like);
                params.add(like);
            }

            if (filtro.filtraPorRol()) {
                sql.append("AND u.id_rol = ? ");
                params.add(filtro.getIdRol());
            }

            if (filtro.getSoloActivos() != null) {
                sql.append("AND u.activo = ? ");
                params.add(filtro.getSoloActivos());
            }

            if (filtro.filtraPorSucursal()) {
                sql.append("AND u.id_sucursal = ? ");
                params.add(filtro.getIdSucursal());
            }

            if (filtro.filtraPorFichajeAbierto()) {
                sql.append("AND EXISTS (")
                   .append("   SELECT 1 FROM fichaje f ")
                   .append("   WHERE f.id_usuario = u.id_usuario ")
                   .append("     AND f.estado = 'ABIERTO'")
                   .append(") ");
            }

            if (filtro.filtraPorCajaAbierta()) {
                sql.append("AND EXISTS (")
                   .append("   SELECT 1 FROM sesion_caja sc ")
                   .append("   WHERE sc.id_usuario_apertura = u.id_usuario ")
                   .append("     AND sc.estado = 'ABIERTA'")
                   .append(") ");
            }
        }

        sql.append("ORDER BY u.activo DESC, u.nombre ASC");

        List<EmpleadoRowDTO> rows = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmpleadoRowDTO dto = new EmpleadoRowDTO();
                    dto.setIdUsuario(rs.getInt("id_usuario"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setUsuario(rs.getString("usuario"));
                    dto.setNombreRol(rs.getString("nombre_rol"));
                    dto.setIdSucursal(rs.getInt("id_sucursal"));
                    dto.setNombreSucursal(rs.getString("nombre_sucursal"));
                    dto.setActivo(rs.getBoolean("activo"));
                    dto.setFichado(rs.getBoolean("fichado"));
                    dto.setCajaAbierta(rs.getBoolean("caja_abierta"));

                    int idCaja = rs.getInt("id_caja_actual");
                    dto.setIdCajaActual(rs.wasNull() ? null : idCaja);
                    dto.setNombreCajaActual(rs.getString("nombre_caja_actual"));

                    rows.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar empleados filtrados.", e);
        }

        return rows;
    }
    public Optional<EmpleadoDetalleDTO> findDetalleById(int idUsuario) {
        String sql =
                "SELECT " +
                "u.id_usuario, u.nombre, u.usuario, u.id_rol, r.nombre AS nombre_rol, " +
                "u.id_sucursal, s.nombre AS nombre_sucursal, u.activo, u.fecha_creacion, " +
                "(" +
                "   SELECT f.id_fichaje " +
                "   FROM fichaje f " +
                "   WHERE f.id_usuario = u.id_usuario AND f.estado = 'ABIERTO' " +
                "   ORDER BY f.id_fichaje DESC LIMIT 1" +
                ") AS id_fichaje_actual, " +
                "(" +
                "   SELECT f.fecha_entrada " +
                "   FROM fichaje f " +
                "   WHERE f.id_usuario = u.id_usuario AND f.estado = 'ABIERTO' " +
                "   ORDER BY f.id_fichaje DESC LIMIT 1" +
                ") AS fecha_entrada_actual, " +
                "(" +
                "   SELECT sc.id_sesion " +
                "   FROM sesion_caja sc " +
                "   WHERE sc.id_usuario_apertura = u.id_usuario AND sc.estado = 'ABIERTA' " +
                "   ORDER BY sc.id_sesion DESC LIMIT 1" +
                ") AS id_sesion_actual, " +
                "(" +
                "   SELECT c.id_caja " +
                "   FROM sesion_caja sc " +
                "   JOIN caja c ON c.id_caja = sc.id_caja " +
                "   WHERE sc.id_usuario_apertura = u.id_usuario AND sc.estado = 'ABIERTA' " +
                "   ORDER BY sc.id_sesion DESC LIMIT 1" +
                ") AS id_caja_actual, " +
                "(" +
                "   SELECT c.nombre " +
                "   FROM sesion_caja sc " +
                "   JOIN caja c ON c.id_caja = sc.id_caja " +
                "   WHERE sc.id_usuario_apertura = u.id_usuario AND sc.estado = 'ABIERTA' " +
                "   ORDER BY sc.id_sesion DESC LIMIT 1" +
                ") AS nombre_caja_actual " +
                "FROM usuario u " +
                "JOIN rol r ON r.id_rol = u.id_rol " +
                "JOIN sucursal s ON s.id_sucursal = u.id_sucursal " +
                "WHERE u.id_usuario = ?";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                EmpleadoDetalleDTO dto = new EmpleadoDetalleDTO();
                dto.setIdUsuario(rs.getInt("id_usuario"));
                dto.setNombre(rs.getString("nombre"));
                dto.setUsuario(rs.getString("usuario"));
                dto.setIdRol(rs.getInt("id_rol"));
                dto.setNombreRol(rs.getString("nombre_rol"));
                dto.setIdSucursal(rs.getInt("id_sucursal"));
                dto.setNombreSucursal(rs.getString("nombre_sucursal"));
                dto.setActivo(rs.getBoolean("activo"));

                Timestamp tsFechaCreacion = rs.getTimestamp("fecha_creacion");
                dto.setFechaCreacion(tsFechaCreacion != null ? tsFechaCreacion.toLocalDateTime() : null);

                int idFichajeActual = rs.getInt("id_fichaje_actual");
                dto.setIdFichajeActual(rs.wasNull() ? null : idFichajeActual);
                dto.setFichajeAbierto(dto.getIdFichajeActual() != null);

                Timestamp tsEntradaActual = rs.getTimestamp("fecha_entrada_actual");
                dto.setFechaEntradaActual(tsEntradaActual != null ? tsEntradaActual.toLocalDateTime() : null);

                int idSesionActual = rs.getInt("id_sesion_actual");
                dto.setIdSesionCajaActual(rs.wasNull() ? null : idSesionActual);
                dto.setSesionCajaAbierta(dto.getIdSesionCajaActual() != null);

                int idCajaActual = rs.getInt("id_caja_actual");
                dto.setIdCajaActual(rs.wasNull() ? null : idCajaActual);
                dto.setNombreCajaActual(rs.getString("nombre_caja_actual"));

                dto.setUltimaActividad(dto.getFechaEntradaActual());
                dto.setObservacionesOperativas(dto.isSesionCajaAbierta()
                        ? "Empleado con sesión de caja abierta."
                        : "Sin sesión de caja abierta.");

                return Optional.of(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el detalle del empleado.", e);
        }
    }
    public boolean existsByUsuario(String usuario) {
        String sql = "SELECT 1 FROM usuario WHERE usuario = ? LIMIT 1";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al comprobar usuario repetido.", e);
        }
    }

    public boolean existsByUsuarioExcludingId(String usuario, int idUsuarioExcluir) {
        String sql = "SELECT 1 FROM usuario WHERE usuario = ? AND id_usuario <> ? LIMIT 1";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setInt(2, idUsuarioExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al comprobar usuario repetido excluyendo id.", e);
        }
    }
    public int insert(Usuario usuario) {
        String sql =
                "INSERT INTO usuario (nombre, usuario, pin_hash, id_rol, id_sucursal, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getPinHash());
            ps.setInt(4, usuario.getRol().getIdRol());
            ps.setInt(5, usuario.getIdSucursal());
            ps.setBoolean(6, usuario.isActivo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new RuntimeException("No se pudo obtener el id del empleado insertado.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar empleado.", e);
        }
    }
    public void update(Usuario usuario) {
        String sql =
                "UPDATE usuario " +
                "SET nombre = ?, usuario = ?, id_rol = ?, id_sucursal = ?, activo = ? " +
                "WHERE id_usuario = ?";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setInt(3, usuario.getRol().getIdRol());
            ps.setInt(4, usuario.getIdSucursal());
            ps.setBoolean(5, usuario.isActivo());
            ps.setInt(6, usuario.getIdUsuario());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar empleado.", e);
        }
    }

    public void updateActivo(int idUsuario, boolean activo) {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado activo del empleado.", e);
        }
    }

    public void updatePinHash(int idUsuario, String nuevoPinHash) {
        String sql = "UPDATE usuario SET pin_hash = ? WHERE id_usuario = ?";

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoPinHash);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar PIN del empleado.", e);
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setUsuario(rs.getString("usuario"));
        u.setPinHash(rs.getString("pin_hash"));
        u.setActivo(rs.getBoolean("activo"));
        u.setIdSucursal(rs.getInt("id_sucursal"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            u.setFechaCreacion(ts.toLocalDateTime());
        }

        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombre(rs.getString("rol_nombre"));
        u.setRol(rol);

        return u;
    }
}
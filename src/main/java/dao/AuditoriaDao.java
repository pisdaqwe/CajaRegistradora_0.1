package dao;

import config.DbPool;
import dtoS.AuditoriaFiltroDTO;
import dtoS.AuditoriaRowDTO;
import model.Auditoria;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDao {

    public int insert(Auditoria auditoria) {
        String sql = """
                INSERT INTO auditoria (
                    id_usuario,
                    id_sucursal,
                    accion,
                    detalles
                ) VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, auditoria.getIdUsuario());
            ps.setInt(2, auditoria.getIdSucursal());
            ps.setString(3, auditoria.getAccion());
            ps.setString(4, auditoria.getDetalles());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new RuntimeException("No se pudo obtener el id de auditoría generado.");

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando auditoría.", e);
        }
    }

    public List<AuditoriaRowDTO> findRowsByFiltro(AuditoriaFiltroDTO filtro) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    a.id_auditoria,
                    a.id_usuario,
                    u.nombre AS nombre_usuario,
                    a.id_sucursal,
                    s.nombre AS nombre_sucursal,
                    a.accion,
                    a.fecha,
                    a.detalles
                FROM auditoria a
                JOIN usuario u ON u.id_usuario = a.id_usuario
                JOIN sucursal s ON s.id_sucursal = a.id_sucursal
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (filtro != null) {
            if (filtro.getIdSucursal() != null && filtro.getIdSucursal() > 0) {
                sql.append(" AND a.id_sucursal = ? ");
                params.add(filtro.getIdSucursal());
            }

            if (filtro.getAccion() != null && !filtro.getAccion().isBlank()
                    && !"TODAS".equalsIgnoreCase(filtro.getAccion())) {
                sql.append(" AND a.accion = ? ");
                params.add(filtro.getAccion().trim());
            }

            if (filtro.getTextoBusqueda() != null && !filtro.getTextoBusqueda().isBlank()) {
                sql.append(" AND (u.nombre LIKE ? OR a.accion LIKE ? OR a.detalles LIKE ?) ");
                String like = "%" + filtro.getTextoBusqueda().trim() + "%";
                params.add(like);
                params.add(like);
                params.add(like);
            }

            if (filtro.getFechaDesde() != null) {
                sql.append(" AND DATE(a.fecha) >= ? ");
                params.add(Date.valueOf(filtro.getFechaDesde()));
            }

            if (filtro.getFechaHasta() != null) {
                sql.append(" AND DATE(a.fecha) <= ? ");
                params.add(Date.valueOf(filtro.getFechaHasta()));
            }
        }

        sql.append(" ORDER BY a.fecha DESC, a.id_auditoria DESC ");

        List<AuditoriaRowDTO> rows = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditoriaRowDTO dto = new AuditoriaRowDTO();
                    dto.setIdAuditoria(rs.getInt("id_auditoria"));
                    dto.setIdUsuario(rs.getInt("id_usuario"));
                    dto.setNombreUsuario(rs.getString("nombre_usuario"));
                    dto.setIdSucursal(rs.getInt("id_sucursal"));
                    dto.setNombreSucursal(rs.getString("nombre_sucursal"));
                    dto.setAccion(rs.getString("accion"));

                    Timestamp ts = rs.getTimestamp("fecha");
                    dto.setFecha(ts != null ? ts.toLocalDateTime() : null);

                    String detalles = rs.getString("detalles");
                    dto.setDetalles(detalles);
                    dto.setResumenDetalles(resumir(detalles));

                    rows.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando auditoría.", e);
        }

        return rows;
    }

    public List<String> findAccionesDistinct() {
        String sql = """
                SELECT DISTINCT accion
                FROM auditoria
                ORDER BY accion ASC
                """;

        List<String> acciones = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                acciones.add(rs.getString("accion"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando acciones de auditoría.", e);
        }

        return acciones;
    }

    private String resumir(String detalles) {
        if (detalles == null || detalles.isBlank()) {
            return "";
        }

        String limpio = detalles.replace("\n", " ").replace("\r", " ").trim();
        return limpio.length() > 120 ? limpio.substring(0, 120) + "..." : limpio;
    }
}
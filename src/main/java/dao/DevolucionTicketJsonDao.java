package dao;

import config.DbPool;
import dtoS.TicketHoyRowDTO;
import model.DevolucionTicketJson;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de persistencia y lectura del JSON de devolución.
 *
 * Responsabilidades:
 * - insertar el documento JSON de una devolución
 * - leer el ticket JSON por idDevolucion
 * - listar devoluciones del día
 * - buscar devoluciones para el diálogo de tickets
 *
 * IMPORTANTE:
 * - esta clase NO parsea el json_data
 * - solo lee/escribe BD
 * - el parseo real se hace en DevolucionTicketService
 */
public class DevolucionTicketJsonDao {

    // =====================================================
    // 1) INSERTAR TICKET JSON DE DEVOLUCIÓN
    // =====================================================

    /**
     * Inserta el documento JSON de la devolución.
     *
     * Devuelve el id_ticket_devolucion generado.
     */
    public int insert(Connection con, int idDevolucion, String jsonData, String rutaPdf) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null.");
        }
        if (idDevolucion <= 0) {
            throw new IllegalArgumentException("idDevolucion debe ser > 0.");
        }
        if (jsonData == null || jsonData.isBlank()) {
            throw new IllegalArgumentException("jsonData no puede ser null ni vacío.");
        }

        String sql = """
                INSERT INTO devolucion_ticket_json (
                    id_devolucion,
                    json_data,
                    ruta_pdf,
                    fecha_generacion
                ) VALUES (?, ?, ?, NOW())
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idDevolucion);
            ps.setString(2, jsonData);
            ps.setString(3, rutaPdf);

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id del ticket de devolución insertado.");
        }
    }

    // =====================================================
    // 2) LEER TICKET JSON POR ID DE DEVOLUCIÓN
    // =====================================================

    /**
     * Busca el ticket JSON asociado a una devolución concreta.
     *
     * Uso previsto:
     * - abrir vista previa del ticket de devolución
     * - construir el DTO visual desde Service
     */
    public Optional<DevolucionTicketJson> findByIdDevolucion(int idDevolucion) {
        if (idDevolucion <= 0) {
            throw new IllegalArgumentException("idDevolucion debe ser > 0.");
        }

        String sql = """
                SELECT
                    dtj.id_ticket_devolucion,
                    dtj.id_devolucion,
                    dtj.json_data,
                    dtj.ruta_pdf,
                    dtj.fecha_generacion
                FROM devolucion_ticket_json dtj
                WHERE dtj.id_devolucion = ?
                LIMIT 1
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDevolucion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapDevolucionTicketJson(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error leyendo devolucion_ticket_json para idDevolucion=" + idDevolucion,
                    e
            );
        }
    }

    // =====================================================
    // 3) LISTAR DEVOLUCIONES DE HOY
    // =====================================================

    public List<TicketHoyRowDTO> findDevolucionesHoy() {
        return findDevolucionesByFecha(LocalDate.now());
    }

    public List<TicketHoyRowDTO> findDevolucionesByFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("fecha no puede ser null");
        }

        String sql = """
                SELECT
                    d.id_devolucion,
                    d.id_venta_original,
                    dtj.fecha_generacion,
                    JSON_UNQUOTE(JSON_EXTRACT(dtj.json_data, '$.nombrePedido')) AS nombre_pedido,
                    d.metodo_reembolso,
                    d.total_reembolsado,
                    u.nombre AS nombre_empleado
                FROM devolucion_ticket_json dtj
                INNER JOIN devolucion d
                    ON d.id_devolucion = dtj.id_devolucion
                INNER JOIN usuario u
                    ON u.id_usuario = d.id_usuario_admin
                WHERE dtj.fecha_generacion >= ?
                  AND dtj.fecha_generacion < ?
                ORDER BY dtj.fecha_generacion DESC, dtj.id_ticket_devolucion DESC
                """;

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        List<TicketHoyRowDTO> rows = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapDevolucionRow(rs));
                }
            }

            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo devoluciones de la fecha " + fecha, e);
        }
    }

    // =====================================================
    // 4) BUSCAR DEVOLUCIONES
    // =====================================================

    /**
     * Busca devoluciones por:
     * - id_devolucion
     * - id_venta_original
     * - nombrePedido guardado en json_data
     * - método de reembolso
     * - nombre del empleado/admin
     */
    public List<TicketHoyRowDTO> searchDevoluciones(String query, int limit) {
        if (query == null || query.isBlank()) {
            return findDevolucionesHoy();
        }

        if (limit <= 0) {
            limit = 100;
        }

        String sql = """
                SELECT
                    d.id_devolucion,
                    d.id_venta_original,
                    dtj.fecha_generacion,
                    JSON_UNQUOTE(JSON_EXTRACT(dtj.json_data, '$.nombrePedido')) AS nombre_pedido,
                    d.metodo_reembolso,
                    d.total_reembolsado,
                    u.nombre AS nombre_empleado
                FROM devolucion_ticket_json dtj
                INNER JOIN devolucion d
                    ON d.id_devolucion = dtj.id_devolucion
                INNER JOIN usuario u
                    ON u.id_usuario = d.id_usuario_admin
                WHERE
                    CAST(d.id_devolucion AS CHAR) LIKE ?
                    OR CAST(d.id_venta_original AS CHAR) LIKE ?
                    OR JSON_UNQUOTE(JSON_EXTRACT(dtj.json_data, '$.nombrePedido')) LIKE ?
                    OR d.metodo_reembolso LIKE ?
                    OR u.nombre LIKE ?
                ORDER BY dtj.fecha_generacion DESC, dtj.id_ticket_devolucion DESC
                LIMIT ?
                """;

        String like = "%" + query.trim() + "%";
        List<TicketHoyRowDTO> rows = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
            ps.setInt(6, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapDevolucionRow(rs));
                }
            }

            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando devoluciones con query=" + query, e);
        }
    }

    // =====================================================
    // 5) HELPERS INTERNOS
    // =====================================================

    private int getGeneratedId(PreparedStatement ps, String errorMessage) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException(errorMessage);
    }

    private DevolucionTicketJson mapDevolucionTicketJson(ResultSet rs) throws SQLException {
        DevolucionTicketJson ticket = new DevolucionTicketJson();

        ticket.setIdTicketDevolucion(rs.getInt("id_ticket_devolucion"));
        ticket.setIdDevolucion(rs.getInt("id_devolucion"));
        ticket.setJsonData(rs.getString("json_data"));
        ticket.setRutaPdf(rs.getString("ruta_pdf"));

        Timestamp ts = rs.getTimestamp("fecha_generacion");
        ticket.setFechaGeneracion(ts != null ? ts.toLocalDateTime() : null);

        return ticket;
    }

    /**
     * Construye una fila resumen reutilizando TicketHoyRowDTO,
     * pero marcando claramente que el registro es una DEVOLUCIÓN.
     */
    private TicketHoyRowDTO mapDevolucionRow(ResultSet rs) throws SQLException {
        TicketHoyRowDTO dto = new TicketHoyRowDTO();

        // ---------------------------------------------
        // Identificadores
        // ---------------------------------------------
        dto.setIdVenta(0); // en devoluciones no usamos este campo
        dto.setIdDevolucion(rs.getInt("id_devolucion"));
        dto.setIdVentaOriginal(rs.getInt("id_venta_original"));
        dto.setTipoRegistro("DEVOLUCION");

        // ---------------------------------------------
        // Datos visibles
        // ---------------------------------------------
        Timestamp ts = rs.getTimestamp("fecha_generacion");
        dto.setFechaGeneracion(ts != null ? ts.toLocalDateTime() : null);

        dto.setNombrePedido(rs.getString("nombre_pedido"));
        dto.setMetodoPago(rs.getString("metodo_reembolso"));

        BigDecimal total = rs.getBigDecimal("total_reembolsado");
        dto.setTotal(total != null ? total : BigDecimal.ZERO);

        dto.setNombreEmpleado(rs.getString("nombre_empleado"));

        return dto;
    }
}
package dao;

import config.DbPool;
import dtoS.TicketHoyRowDTO;
import model.TicketJson;

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
 * DAO de lectura de tickets persistidos en ticket_json.
 *
 * Responsabilidades:
 * - leer un ticket concreto por idVenta
 * - leer el último ticket de una sesión de caja
 * - listar tickets de un día
 * - buscar tickets por texto
 *
 * IMPORTANTE:
 * - este DAO NO parsea el json_data
 * - solo lee BD y devuelve:
 *   - TicketJson para el ticket completo persistido
 *   - TicketHoyRowDTO para la lista resumen
 */
public class TicketJsonDao {

    // =====================================================
    // 1) BUSCAR TICKET POR ID DE VENTA
    // =====================================================

    public Optional<TicketJson> findByVenta(int idVenta) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta debe ser > 0");
        }

        final String sql = """
                SELECT
                    tj.id_ticket_json,
                    tj.id_venta,
                    tj.json_data,
                    tj.ruta_pdf,
                    tj.fecha_generacion
                FROM ticket_json tj
                WHERE tj.id_venta = ?
                LIMIT 1
                """;

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTicketJson(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo ticket_json por idVenta=" + idVenta, e);
        }
    }

    // =====================================================
    // 2) BUSCAR ÚLTIMO TICKET DE UNA SESIÓN DE CAJA
    // =====================================================

    public Optional<TicketJson> findUltimoTicketDeSesion(int idSesion) {
        if (idSesion <= 0) {
            throw new IllegalArgumentException("idSesion debe ser > 0");
        }

        final String sql = """
                SELECT
                    tj.id_ticket_json,
                    tj.id_venta,
                    tj.json_data,
                    tj.ruta_pdf,
                    tj.fecha_generacion
                FROM ticket_json tj
                INNER JOIN venta v
                    ON v.id_venta = tj.id_venta
                WHERE v.id_sesion = ?
                ORDER BY tj.fecha_generacion DESC, tj.id_ticket_json DESC
                LIMIT 1
                """;

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idSesion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTicketJson(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo último ticket de la sesión idSesion=" + idSesion, e);
        }
    }

    // =====================================================
    // 3) LISTAR TICKETS DE HOY
    // =====================================================

    public List<TicketHoyRowDTO> findTicketsHoy() {
        return findTicketsByFecha(LocalDate.now());
    }

    // =====================================================
    // 4) LISTAR TICKETS POR FECHA
    // =====================================================

    public List<TicketHoyRowDTO> findTicketsByFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("fecha no puede ser null");
        }

        final String sql = """
                SELECT
                    tj.id_venta,
                    tj.fecha_generacion,
                    JSON_UNQUOTE(JSON_EXTRACT(tj.json_data, '$.nombrePedido')) AS nombre_pedido,
                    p.metodo AS metodo_pago,
                    v.total,
                    u.nombre AS nombre_empleado
                FROM ticket_json tj
                INNER JOIN venta v
                    ON v.id_venta = tj.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                LEFT JOIN pago p
                    ON p.id_venta = v.id_venta
                WHERE tj.fecha_generacion >= ?
                  AND tj.fecha_generacion < ?
                ORDER BY tj.fecha_generacion DESC, tj.id_ticket_json DESC
                """;

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        List<TicketHoyRowDTO> rows = new ArrayList<>();

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapTicketHoyRow(rs));
                }
            }

            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo tickets de la fecha " + fecha, e);
        }
    }

    // =====================================================
    // 5) BUSCAR TICKETS POR TEXTO
    // =====================================================

    public List<TicketHoyRowDTO> searchTickets(String query, int limit) {
        if (query == null || query.isBlank()) {
            return findTicketsHoy();
        }

        if (limit <= 0) {
            limit = 100;
        }

        final String sql = """
                SELECT
                    tj.id_venta,
                    tj.fecha_generacion,
                    JSON_UNQUOTE(JSON_EXTRACT(tj.json_data, '$.nombrePedido')) AS nombre_pedido,
                    p.metodo AS metodo_pago,
                    v.total,
                    u.nombre AS nombre_empleado
                FROM ticket_json tj
                INNER JOIN venta v
                    ON v.id_venta = tj.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                LEFT JOIN pago p
                    ON p.id_venta = v.id_venta
                WHERE
                    CAST(tj.id_venta AS CHAR) LIKE ?
                    OR JSON_UNQUOTE(JSON_EXTRACT(tj.json_data, '$.nombrePedido')) LIKE ?
                    OR u.nombre LIKE ?
                    OR p.metodo LIKE ?
                ORDER BY tj.fecha_generacion DESC, tj.id_ticket_json DESC
                LIMIT ?
                """;

        String like = "%" + query.trim() + "%";
        List<TicketHoyRowDTO> rows = new ArrayList<>();

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setInt(5, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapTicketHoyRow(rs));
                }
            }

            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando tickets con query=" + query, e);
        }
    }

    // =====================================================
    // 6) MAPPERS
    // =====================================================

    private TicketJson mapTicketJson(ResultSet rs) throws SQLException {
        TicketJson ticket = new TicketJson();

        ticket.setIdTicketJson(rs.getInt("id_ticket_json"));
        ticket.setIdVenta(rs.getInt("id_venta"));
        ticket.setJsonData(rs.getString("json_data"));
        ticket.setRutaPdf(rs.getString("ruta_pdf"));

        Timestamp ts = rs.getTimestamp("fecha_generacion");
        ticket.setFechaGeneracion(ts != null ? ts.toLocalDateTime() : null);

        return ticket;
    }

    /**
     * Construye una fila resumen para el listado de tickets.
     *
     * Ajuste nuevo:
     * - deja marcado que esta fila es una VENTA normal
     * - prepara el DTO para convivir con devoluciones en el listado mixto
     */
    private TicketHoyRowDTO mapTicketHoyRow(ResultSet rs) throws SQLException {
        TicketHoyRowDTO dto = new TicketHoyRowDTO();

        // ---------------------------------------------
        // Identificadores
        // ---------------------------------------------
        dto.setIdVenta(rs.getInt("id_venta"));
        dto.setIdDevolucion(null);
        dto.setIdVentaOriginal(null);
        dto.setTipoRegistro("VENTA");

        // ---------------------------------------------
        // Datos visibles
        // ---------------------------------------------
        Timestamp ts = rs.getTimestamp("fecha_generacion");
        dto.setFechaGeneracion(ts != null ? ts.toLocalDateTime() : null);

        dto.setNombrePedido(rs.getString("nombre_pedido"));
        dto.setMetodoPago(rs.getString("metodo_pago"));

        BigDecimal total = rs.getBigDecimal("total");
        dto.setTotal(total != null ? total : BigDecimal.ZERO);

        dto.setNombreEmpleado(rs.getString("nombre_empleado"));

        return dto;
    }
}
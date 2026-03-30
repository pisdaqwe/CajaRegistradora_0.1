package dao;

import config.DbPool;
import dtoS.DescuentoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de lectura para la tabla descuento.
 *
 * Responsabilidades:
 * - buscar un descuento por código
 * - obtener el descuento activo de empleado
 * - listar descuentos activos si más adelante hace falta
 *
 * IMPORTANTE:
 * Este DAO NO registra ventas.
 * Solo consulta configuración de descuentos.
 */
public class DescuentoDao {

    /**
     * Busca un descuento por código.
     *
     * La búsqueda es case-insensitive.
     * Devuelve Optional.empty() si no existe.
     */
    public Optional<DescuentoDTO> findByCodigo(String codigo) {
        String normalized = normalize(codigo);
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        String sql = """
            SELECT
                id_descuento,
                nombre,
                descripcion,
                tipo,
                valor,
                codigo,
                origen,
                requiere_codigo,
                requiere_empleado,
                acumulable,
                aplica_a_combos,
                activo,
                fecha_inicio,
                fecha_fin,
                uso_maximo,
                uso_actual,
                fecha_creacion
            FROM descuento
            WHERE UPPER(codigo) = UPPER(?)
            LIMIT 1
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, normalized);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando descuento por código.", e);
        }
    }

    /**
     * Busca el descuento activo configurado para empleados.
     *
     * Regla asumida:
     * - origen = EMPLEADO
     * - activo = 1
     *
     * Si en el futuro tienes más de uno, aquí podrás afinar por fechas,
     * prioridad o lo que decidas.
     */
    public Optional<DescuentoDTO> findDescuentoEmpleadoActivo() {
        String sql = """
            SELECT
                id_descuento,
                nombre,
                descripcion,
                tipo,
                valor,
                codigo,
                origen,
                requiere_codigo,
                requiere_empleado,
                acumulable,
                aplica_a_combos,
                activo,
                fecha_inicio,
                fecha_fin,
                uso_maximo,
                uso_actual,
                fecha_creacion
            FROM descuento
            WHERE origen = 'EMPLEADO'
              AND activo = 1
            ORDER BY id_descuento ASC
            LIMIT 1
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando descuento de empleado activo.", e);
        }
    }

    /**
     * Devuelve todos los descuentos activos.
     *
     * No es imprescindible para el MVP, pero puede venir bien
     * para administración, depuración o pruebas.
     */
    public List<DescuentoDTO> findActivos() {
        String sql = """
            SELECT
                id_descuento,
                nombre,
                descripcion,
                tipo,
                valor,
                codigo,
                origen,
                requiere_codigo,
                requiere_empleado,
                acumulable,
                aplica_a_combos,
                activo,
                fecha_inicio,
                fecha_fin,
                uso_maximo,
                uso_actual,
                fecha_creacion
            FROM descuento
            WHERE activo = 1
            ORDER BY nombre ASC
        """;

        List<DescuentoDTO> result = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Error listando descuentos activos.", e);
        }
    }

    // =====================================================
    // MAPEADO
    // =====================================================

    private DescuentoDTO mapRow(ResultSet rs) throws SQLException {
        DescuentoDTO dto = new DescuentoDTO();

        dto.setIdDescuento(rs.getInt("id_descuento"));
        dto.setNombre(rs.getString("nombre"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setTipo(rs.getString("tipo"));
        dto.setValor(rs.getBigDecimal("valor"));
        dto.setCodigo(rs.getString("codigo"));
        dto.setOrigen(rs.getString("origen"));
        dto.setRequiereCodigo(rs.getBoolean("requiere_codigo"));
        dto.setRequiereEmpleado(rs.getBoolean("requiere_empleado"));
        dto.setAcumulable(rs.getBoolean("acumulable"));
        dto.setAplicaACombos(rs.getBoolean("aplica_a_combos"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setFechaInicio(toLocalDateTime(rs.getTimestamp("fecha_inicio")));
        dto.setFechaFin(toLocalDateTime(rs.getTimestamp("fecha_fin")));

        int usoMaximo = rs.getInt("uso_maximo");
        dto.setUsoMaximo(rs.wasNull() ? null : usoMaximo);

        dto.setUsoActual(rs.getInt("uso_actual"));
        dto.setFechaCreacion(toLocalDateTime(rs.getTimestamp("fecha_creacion")));

        return dto;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
package dao;

import config.DbPool;
import dtoS.CajaEstadoDTO;
import dtoS.LoginRapidoButtonDTO;
import dtoS.SesionCajaRefDTO;
import enums.EstadoSesionCaja;
import model.SesionCaja;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SesionCajaDao {

    private static final List<String> METODOS_PAGO_EFECTIVO = List.of("EFECTIVO", "EFECTIVO_EXACTO");
    private static final List<String> METODOS_PAGO_TARJETA = List.of("TARJETA");

    private static final List<String> METODOS_REEMBOLSO_EFECTIVO = List.of("EFECTIVO");
    private static final List<String> METODOS_REEMBOLSO_TARJETA = List.of("TARJETA");

    // =====================================================
    // SESIONES ABIERTAS
    // =====================================================

    public boolean existeSesionAbiertaPorUsuario(int idUsuario) {
        String sql = """
                SELECT 1
                FROM sesion_caja
                WHERE id_usuario_apertura = ?
                  AND estado = 'ABIERTA'
                LIMIT 1
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error comprobando sesión abierta por usuario", e);
        }
    }
    
    public Optional<SesionCaja> findSesionAbiertaByCaja(int idCaja) {
        String sql = """
                SELECT id_sesion,
                       id_caja,
                       id_usuario_apertura,
                       id_usuario_cierre,
                       fecha_apertura,
                       fecha_cierre,
                       importe_inicial,
                       importe_final,
                       total_ventas,
                       estado,
                       observaciones
                FROM sesion_caja
                WHERE id_caja = ?
                  AND estado = 'ABIERTA'
                ORDER BY fecha_apertura DESC
                LIMIT 1
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCaja);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSesionCaja(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sesión abierta por caja", e);
        }

        return Optional.empty();
    }

    public Optional<SesionCaja> findSesionCajaByIdUsuario(int idUsuario) {
        String sql = """
                SELECT id_sesion,
                       id_caja,
                       id_usuario_apertura,
                       id_usuario_cierre,
                       fecha_apertura,
                       fecha_cierre,
                       importe_inicial,
                       importe_final,
                       total_ventas,
                       estado,
                       observaciones
                FROM sesion_caja
                WHERE id_usuario_apertura = ?
                  AND estado = 'ABIERTA'
                ORDER BY fecha_apertura DESC
                LIMIT 1
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSesionCaja(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sesión abierta por usuario", e);
        }

        return Optional.empty();
    }

    // =====================================================
    // INSERT / UPDATE
    // =====================================================

    public void insert(SesionCaja sesion) {
        String sql = """
                INSERT INTO sesion_caja (
                    id_caja,
                    id_usuario_apertura,
                    fecha_apertura,
                    importe_inicial,
                    total_ventas,
                    estado,
                    observaciones
                ) VALUES (?, ?, NOW(), ?, ?, ?, ?)
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sesion.getIdCaja());
            ps.setInt(2, sesion.getIdUsuarioApertura());
            ps.setBigDecimal(3, safe(sesion.getImporteInicial()));
            ps.setBigDecimal(4, safe(sesion.getTotalVentas()));
            ps.setString(5, sesion.getEstado().name());
            ps.setString(6, sesion.getObservaciones());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    sesion.setIdSesion(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando sesión de caja", e);
        }
    }

    public void cerrarSesion(SesionCaja sesion) {
        String sql = """
                UPDATE sesion_caja
                SET id_usuario_cierre = ?,
                    fecha_cierre = NOW(),
                    importe_final = ?,
                    estado = ?,
                    observaciones = ?
                WHERE id_sesion = ?
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sesion.getIdUsuarioCierre());
            ps.setBigDecimal(2, safe(sesion.getImporteFinal()));
            ps.setString(3, sesion.getEstado().name());
            ps.setString(4, sesion.getObservaciones());
            ps.setInt(5, sesion.getIdSesion());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando sesión de caja", e);
        }
    }

    // =====================================================
    // RESUMEN CIERRE / TOTALES
    // =====================================================

    public BigDecimal getImporteInicial(int idSesion) {
        String sql = """
                SELECT importe_inicial
                FROM sesion_caja
                WHERE id_sesion = ?
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSesion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal("importe_inicial");
                    return value != null ? value : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo importe inicial de la sesión", e);
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPagosEfectivo(int idSesion) {
        return getTotalPagosByMetodos(idSesion, METODOS_PAGO_EFECTIVO);
    }

    public BigDecimal getTotalPagosTarjeta(int idSesion) {
        return getTotalPagosByMetodos(idSesion, METODOS_PAGO_TARJETA);
    }

    public BigDecimal getTotalDevolucionesEfectivo(int idSesion) {
        return getTotalDevolucionesByMetodos(idSesion, METODOS_REEMBOLSO_EFECTIVO);
    }

    public BigDecimal getTotalDevolucionesTarjeta(int idSesion) {
        return getTotalDevolucionesByMetodos(idSesion, METODOS_REEMBOLSO_TARJETA);
    }

    private BigDecimal getTotalPagosByMetodos(int idSesion, List<String> metodos) {
        if (metodos == null || metodos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String placeholders = metodos.stream()
                .map(m -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT COALESCE(SUM(p.monto), 0) AS total
                FROM pago p
                JOIN venta v ON v.id_venta = p.id_venta
                WHERE v.id_sesion = ?
                  AND v.anulada = 0
                  AND p.metodo IN (%s)
                """.formatted(placeholders);

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            ps.setInt(idx++, idSesion);

            for (String metodo : metodos) {
                ps.setString(idx++, metodo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error calculando total de pagos por método", e);
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getTotalDevolucionesByMetodos(int idSesion, List<String> metodos) {
        if (metodos == null || metodos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String placeholders = metodos.stream()
                .map(m -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT COALESCE(SUM(d.total_reembolsado), 0) AS total
                FROM devolucion d
                WHERE d.id_sesion = ?
                  AND d.anulada = 0
                  AND d.metodo_reembolso IN (%s)
                """.formatted(placeholders);

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            ps.setInt(idx++, idSesion);

            for (String metodo : metodos) {
                ps.setString(idx++, metodo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error calculando total de devoluciones por método", e);
        }

        return BigDecimal.ZERO;
    }

    // =====================================================
    // LOGIN RÁPIDO
    // =====================================================

    public List<LoginRapidoButtonDTO> selectBotonesLoginRapidoByCaja(int idCaja) {
        String sql = """
                SELECT DISTINCT
                       u.id_usuario,
                       u.usuario,
                       u.nombre
                FROM sesion_caja sc
                JOIN caja c
                  ON c.id_caja = sc.id_caja
                JOIN usuario u
                  ON u.id_usuario = sc.id_usuario_apertura
                JOIN fichaje f
                  ON f.id_usuario = u.id_usuario
                WHERE sc.id_caja = ?
                  AND sc.estado = 'ABIERTA'
                  AND c.activa = 1
                  AND c.estado = 'OPERATIVA'
                  AND u.activo = 1
                  AND f.estado = 'ABIERTO'
                  AND f.fecha_salida IS NULL
                ORDER BY u.nombre ASC
                """;

        List<LoginRapidoButtonDTO> lista = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCaja);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new LoginRapidoButtonDTO(
                            rs.getInt("id_usuario"),
                            buildNombreVisible(
                                    rs.getString("nombre"),
                                    rs.getString("usuario")
                            )
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando botones de login rápido", e);
        }

        return lista;
    }

    // =====================================================
    // ESTADO CAJAS
    // =====================================================

    public List<CajaEstadoDTO> findEstadoCajas() {
        String sql = """
                SELECT
                    c.id_caja,
                    c.nombre AS nombre_caja,
                    CASE WHEN sc.id_sesion IS NOT NULL THEN 1 ELSE 0 END AS ocupada,
                    u.nombre AS empleado_asignado,
                    CASE
                        WHEN c.activa = 1 AND c.estado = 'OPERATIVA' THEN 1
                        ELSE 0
                    END AS operativa
                FROM caja c
                LEFT JOIN sesion_caja sc
                       ON sc.id_caja = c.id_caja
                      AND sc.estado = 'ABIERTA'
                LEFT JOIN usuario u
                       ON u.id_usuario = sc.id_usuario_apertura
                WHERE c.activa = 1
                ORDER BY c.nombre ASC
                """;

        List<CajaEstadoDTO> lista = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new CajaEstadoDTO(
                        rs.getInt("id_caja"),
                        rs.getString("nombre_caja"),
                        rs.getBoolean("ocupada"),
                        rs.getString("empleado_asignado"),
                        rs.getBoolean("operativa")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cargando estado de cajas", e);
        }

        return lista;
    }

    // =====================================================
    // REF SESIÓN ABIERTA
    // =====================================================

    public Optional<SesionCajaRefDTO> selectRefAbiertaByUsuario(int idUsuario) {
        String sql = """
                SELECT
                    sc.id_sesion,
                    sc.id_caja,
                    c.id_sucursal,
                    c.nombre AS nombre_caja
                FROM sesion_caja sc
                JOIN caja c
                  ON c.id_caja = sc.id_caja
                WHERE sc.id_usuario_apertura = ?
                  AND sc.estado = 'ABIERTA'
                ORDER BY sc.fecha_apertura DESC
                LIMIT 1
                """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SesionCajaRefDTO(
                            rs.getInt("id_sesion"),
                            rs.getInt("id_caja"),
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre_caja")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo referencia de sesión abierta por usuario", e);
        }

        return Optional.empty();
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private SesionCaja mapSesionCaja(ResultSet rs) throws SQLException {
        SesionCaja sesion = new SesionCaja();

        sesion.setIdSesion(rs.getInt("id_sesion"));
        sesion.setIdCaja(rs.getInt("id_caja"));
        sesion.setIdUsuarioApertura(rs.getInt("id_usuario_apertura"));

        int idUsuarioCierre = rs.getInt("id_usuario_cierre");
        if (!rs.wasNull()) {
            sesion.setIdUsuarioCierre(idUsuarioCierre);
        }

        Timestamp fa = rs.getTimestamp("fecha_apertura");
        if (fa != null) {
            sesion.setFechaApertura(fa.toLocalDateTime());
        }

        Timestamp fc = rs.getTimestamp("fecha_cierre");
        if (fc != null) {
            sesion.setFechaCierre(fc.toLocalDateTime());
        }

        sesion.setImporteInicial(safe(rs.getBigDecimal("importe_inicial")));
        sesion.setImporteFinal(safe(rs.getBigDecimal("importe_final")));
        sesion.setTotalVentas(safe(rs.getBigDecimal("total_ventas")));
        sesion.setEstado(EstadoSesionCaja.valueOf(rs.getString("estado")));
        sesion.setObservaciones(rs.getString("observaciones"));

        return sesion;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String buildNombreVisible(String nombreCompleto, String codigoUsuario) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return codigoUsuario != null ? codigoUsuario : "";
        }

        String[] partes = nombreCompleto.trim().split("\\s+");
        String base = partes.length > 0 ? partes[0] : nombreCompleto.trim();

        if (base.length() > 14) {
            return base.substring(0, 14);
        }

        return base;
    }
}
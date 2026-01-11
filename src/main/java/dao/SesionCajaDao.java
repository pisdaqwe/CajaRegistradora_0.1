package dao;

import config.DbPool;
import model.SesionCaja;
import enums.EstadoSesionCaja;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DAO de acceso a datos para SESION_CAJA.
 */
public class SesionCajaDao {
	
	public Optional<SesionCaja>findSesionCajaByIdUsuario(int idUsuario){
		 String sql = """
		            SELECT *
		            FROM sesion_caja
		            WHERE id_usuario_apertura = ?
		              AND estado = 'ABIERTA'
		        """;
		 try (Connection con = DbPool.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
			 ps.setInt(1, idUsuario);
			 try (ResultSet rs = ps.executeQuery()){
				 if(rs.next()) {
					 return Optional.of(mapSesionCaja(rs));
					 
				 }
				 
				
			} 
		 }catch (SQLException e) {
			 throw new RuntimeException("Erro buscando sesion abierta "+ e);
			 
			
		}
		 return Optional.empty();
		
		
	}

    /**
     * Busca la sesión ABIERTA de una caja.
     */
    public Optional<SesionCaja> findSesionAbiertaByCaja(int idCaja) {

        String sql = """
            SELECT *
            FROM sesion_caja
            WHERE id_caja = ?
              AND estado = 'ABIERTA'
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
            throw new RuntimeException("Error buscando sesión abierta", e);
        }

        return Optional.empty();
    }

    /**
     * Inserta una nueva sesión de caja.
     */
    public void insert(SesionCaja sesion) {

        String sql = """
            INSERT INTO sesion_caja
            (id_caja, id_usuario_apertura, fecha_apertura,
             importe_inicial, total_ventas, estado)
            VALUES (?, ?, NOW(), ?, ?, 'ABIERTA')
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sesion.getIdCaja());
            ps.setInt(2, sesion.getIdUsuarioApertura());
            ps.setBigDecimal(3, sesion.getImporteInicial());
            ps.setBigDecimal(4, sesion.getTotalVentas());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    sesion.setIdSesion(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando sesión de caja", e);
        }
    }

    /**
     * Cierra una sesión de caja.
     */
    public void cerrarSesion(SesionCaja sesion) {

        String sql = """
            UPDATE sesion_caja
            SET id_usuario_cierre = ?,
                fecha_cierre = NOW(),
                importe_final = ?,
                observaciones = ?,
                estado = 'CERRADA'
            WHERE id_sesion = ?
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sesion.getIdUsuarioCierre());
            ps.setBigDecimal(2, sesion.getImporteFinal());
            ps.setString(3, sesion.getObservaciones());
            ps.setInt(4, sesion.getIdSesion());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando sesión de caja", e);
        }
    }

    /**
     * Obtiene el importe inicial de una sesión.
     */
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
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo importe inicial", e);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Obtiene el total de pagos en efectivo asociados a una sesión.
     */
    public BigDecimal getTotalPagosEfectivo(int idSesion) {

        String sql = """
            SELECT COALESCE(SUM(p.monto), 0)
            FROM pago p
            JOIN venta v ON p.id_venta = v.id_venta
            WHERE v.id_sesion = ?
              AND p.metodo = 'EFECTIVO'
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSesion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error calculando pagos en efectivo", e);
        }

        return BigDecimal.ZERO;
    }

    // =====================================================
    // MAPPER
    // =====================================================

    private SesionCaja mapSesionCaja(ResultSet rs) throws SQLException {

        SesionCaja s = new SesionCaja();

        s.setIdSesion(rs.getInt("id_sesion"));
        s.setIdCaja(rs.getInt("id_caja"));
        s.setIdUsuarioApertura(rs.getInt("id_usuario_apertura"));

        int cierre = rs.getInt("id_usuario_cierre");
        if (!rs.wasNull()) {
            s.setIdUsuarioCierre(cierre);
        }

        Timestamp fa = rs.getTimestamp("fecha_apertura");
        if (fa != null) {
            s.setFechaApertura(fa.toLocalDateTime());
        }

        Timestamp fc = rs.getTimestamp("fecha_cierre");
        if (fc != null) {
            s.setFechaCierre(fc.toLocalDateTime());
        }

        s.setImporteInicial(rs.getBigDecimal("importe_inicial"));
        s.setImporteFinal(rs.getBigDecimal("importe_final"));
        s.setTotalVentas(rs.getBigDecimal("total_ventas"));
        s.setEstado(EstadoSesionCaja.valueOf(rs.getString("estado")));
        s.setObservaciones(rs.getString("observaciones"));

        return s;
    }
}

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.PseudoColumnUsage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import config.DbPool;
import enums.EstadoCaja;
import model.Caja;

/**
 * Busca una caja por ID.
 */
public class CajaDao {
	public Optional<Caja>findById(int idCaja){
		String sql = """
				SELECT id_caja,nombre,ubicacion,estado,activa,
				id_sucursal,fecha_creacion,ultima_apertura
				FROM caja
				WHERE id_caja=?
				""";
		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)
			){
			ps.setInt(1, idCaja);
			try (ResultSet resultSet = ps.executeQuery()){
				if(resultSet.next()) {
					return Optional.of(mapCaja(resultSet));
				}
				
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error buscando la caja por id");
		}
		return Optional.empty();
		
	}
	/**
     * Devuelve todas las cajas operativas y activas.
     */
	public List<Caja> findCajasOperativas(){
		String sql ="""
				SELECT id_caja,nombre,ubucacion,estado,activa,
						id_sucursal,fecha_creacion,ultima_apertura
				FROM caja
				WHERE activa = 1 AND estadi = 'OPERATIVA'
				
				""";
		List<Caja> cajas = new ArrayList<>();
		try(Connection conn = DbPool.getConnection();
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				ResultSet rs = preparedStatement.executeQuery()) {
			
			while (rs.next()) {
				cajas.add(mapCaja(rs));
				
				
			}
			
		} catch (SQLException  e) {
			throw new RuntimeException("Error listando caja operativas ",e);
		}
		return cajas;
	}
	/**
     * Actualiza la fecha de última apertura de la caja.
     */
    public void updateUltimaApertura(int idCaja) {

        String sql = """
            UPDATE caja
            SET ultima_apertura = NOW()
            WHERE id_caja = ?
        """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCaja);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando ultima_apertura", e);
        }
    }
    public List<Caja> findActivasBySucursal(int idSucursal) {
        String sql = """
            SELECT id_caja,
                   nombre,
                   ubicacion,
                   estado,
                   activa,
                   id_sucursal,
                   fecha_creacion,
                   ultima_apertura
            FROM caja
            WHERE activa = 1
              AND id_sucursal = ?
            ORDER BY nombre
        """;

        List<Caja> cajas = new ArrayList<>();

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Caja caja = new Caja();
                    caja.setIdCaja(rs.getInt("id_caja"));
                    caja.setNombre(rs.getString("nombre"));
                    caja.setUbicacion(rs.getString("ubicacion"));
                    caja.setEstado(EstadoCaja.valueOf(rs.getString("estado")));
                    caja.setActiva(rs.getBoolean("activa"));
                    caja.setIdSucursal(rs.getInt("id_sucursal"));

                    Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
                    if (tsCreacion != null) {
                        caja.setFechaCreacion(tsCreacion.toLocalDateTime());
                    }

                    Timestamp tsUltimaApertura = rs.getTimestamp("ultima_apertura");
                    if (tsUltimaApertura != null) {
                        caja.setUltimaApertura(tsUltimaApertura.toLocalDateTime());
                    }

                    cajas.add(caja);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando cajas activas por sucursal", e);
        }

        return cajas;
    }
    // =====================================================
    // MAPPER
    // =====================================================

	private Caja mapCaja(ResultSet rs )throws SQLException{
		Caja caja = new Caja();
		caja.setIdCaja(rs.getInt("id_caja"));
        caja.setNombre(rs.getString("nombre"));
        caja.setUbicacion(rs.getString("ubicacion"));
        caja.setEstado(EstadoCaja.valueOf(rs.getString("estado")));
        caja.setActiva(rs.getBoolean("activa"));
        caja.setIdSucursal(rs.getInt("id_sucursal"));
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if(fc!=null) {
        	caja.setFechaCreacion(fc.toLocalDateTime());
        	
        }
        Timestamp ua = rs.getTimestamp("ultima_apertura");
        if(ua!=null) {
        	caja.setUltimaApertura(null);
        	
        }
        return caja;
        
	}

}

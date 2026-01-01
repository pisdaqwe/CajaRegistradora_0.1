package dao;

import java.sql.Timestamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import config.DbPool;
import model.UsuarioRecordado;

public class UsuarioRecordadoDao {
	
	
	// =========================
    // BUSCAR BOTÓN POR TERMINAL Y USUARIO
    // =========================
	public Optional< UsuarioRecordado>findByTerminalAndUsuario(int idTerminal,int idUsuario){
		
		String sql ="""
				SELECT id_boton,id_usuario,id_terminal,nombre_boton,
					   posicion,ultimo_acceso
				FROM tpv_botones_inicio
				WHERE id_terminal = ?
				AND id_usuario = ?
				""";
		
		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				){
			
			ps.setInt(1, idTerminal);
			ps.setInt(2, idUsuario);
			
			
			
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					return Optional.of(map(rs));
				}
				
			}
			
		} catch (SQLException  e) {
			throw new RuntimeException("Error obteniendo usuarios recordados",e);
		}
			return Optional.empty();
	}
	// =========================
    // BUSCAR BOTONES POR TERMINAL
    // =========================
	public List<UsuarioRecordado> findButtonsByTerminal(int idTerminal) {
		
		List<UsuarioRecordado>botonesRecordados = new ArrayList<>();
		String sql ="""
				SELECT id_boton,id_usuario,id_terminal,nombre_boton,
				posicion,ultimo_acceso
				FROM tpv_botones_inicio
				WHERE id_terminal = ?
				ORDER BY posicion
				""";
		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)
				
				){
			ps.setInt(1, idTerminal);
			try(ResultSet rs = ps.executeQuery()){
				while (rs.next()) {
					botonesRecordados.add(map(rs));
					
				}
				
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("Error obteniendo usuarios recordados",e);
		}
		return botonesRecordados;
		
	}
	
	
	 // =========================
    // BUSCAR PRIMERA POSICIÓN LIBRE (1–6)
    // =========================
	
	public int findFirstFreePosition(int idTerminal) {
		String sql = """
				SELECT posicion
				FROM tpv_botones_inicio
				WHERE id_terminal = ?
				ORDER BY posicion
				""";
		boolean[] ocupadas = new boolean[6];
		
		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, idTerminal);
			
			try (ResultSet rs  = ps.executeQuery()){
				while (rs.next()) {
					int pos = rs.getInt("posicion");
					if(pos >=1 && pos <= 6) {
						ocupadas[pos-1]=true;
					}
				}
				
			} 
			
		} catch (SQLException e) {
			throw new RuntimeException("Error buscando posicion libre",e);
			
		}
		for (int i = 0;i<ocupadas.length;i++) {
			if(!ocupadas[i]) {
				return i+1;
				
			}
			
		}
		throw new IllegalStateException("No hay posiciones libres en este terminal");
		
	}
	
	private UsuarioRecordado map (ResultSet rs )throws SQLException {
		UsuarioRecordado u = new UsuarioRecordado();
		
		u.setIdBoton(rs.getInt("id_boton"));
		u.setIdTerminal(rs.getInt("id_terminal"));
		u.setIdUsuario(rs.getInt("id_usuario"));
		u.setNombreBoton(rs.getString("nombre_boton"));
		u.setPosicion(rs.getInt("posicion"));
		java.sql.Timestamp timestamp = rs.getTimestamp("ultimo_acceso");
		if (timestamp!=null) {
			u.setUltimoAcceso(timestamp.toLocalDateTime());
			
		}
		
		return u;
	
	}


	// =========================
	// ACTUALIZAR ÚLTIMO ACCESO
	// =========================
	public void updateUltimoAcceso(int idBoton) {

	    String sql = """
	        UPDATE tpv_botones_inicio
	        SET ultimo_acceso = NOW()
	        WHERE id_boton = ?
	    """;

	    try (Connection conn = DbPool.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idBoton);
	        ps.executeUpdate();

	    } catch (SQLException e) {
	        throw new RuntimeException("Error actualizando último acceso", e);
	    }
	}
	// =========================
	// ELIMINAR BOTÓN POR ID
	// =========================
	public void deleteById(int idBoton) {

	    String sql = """
	        DELETE FROM tpv_botones_inicio
	        WHERE id_boton = ?
	    """;

	    try (Connection conn = DbPool.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idBoton);
	        ps.executeUpdate();

	    } catch (SQLException e) {
	        throw new RuntimeException("Error eliminando usuario recordado", e);
	    }
	}
	// =========================
	// LIMPIAR BOTONES POR INACTIVIDAD
	// =========================
	public int deleteByInactividad(int idTerminal, int dias) {

	    String sql = """
	        DELETE FROM tpv_botones_inicio
	        WHERE id_terminal = ?
	          AND ultimo_acceso < NOW() - INTERVAL ? DAY
	    """;

	    try (Connection conn = DbPool.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, idTerminal);
	        ps.setInt(2, dias);

	        return ps.executeUpdate();

	    } catch (SQLException e) {
	        throw new RuntimeException("Error limpiando usuarios recordados por inactividad", e);
	    }
	}


	public void insert(UsuarioRecordado nuevo) {
		
		String sql = """
				INSERT INTO tpv_botones_inicio
				(id_usuario,id_terminal,nombre_boton,posicion,ultimo_acceso)
				VALUES (?,?,?,?,NOW())
				""";
		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
			ps.setInt(1, nuevo.getIdUsuario());
			ps.setInt(2, nuevo.getIdTerminal());
			ps.setString(3, nuevo.getNombreBoton());
			ps.setInt(4, nuevo.getPosicion());
			
			ps.executeUpdate();
			
			try(ResultSet rs = ps.executeQuery() ){
				if(rs.next()) {
					nuevo.setIdBoton(rs.getInt("id_boton"));
					
				}
				
			}
		} catch (SQLException e) {
	        throw new RuntimeException("Error insertando usuario recordado", e);
		}
		
	}
}

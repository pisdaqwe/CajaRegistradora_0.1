package dao;

import config.DbPool;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductoTamanoDao {
	public Optional<TamanoPrecioDTO> selectTamanoDefaultYPrecio(int idProducto) {

		Optional<TamanoPrecioDTO> clasico = selectByNombreTamano(idProducto, "Clasico");
		if (clasico.isPresent()) {
			return clasico;

		}
		return selectPrimeroPorOrden(idProducto);
	}

	private Optional<TamanoPrecioDTO> selectPrimeroPorOrden(int idProducto) {
		String sql = """
				SELECT t.id_taano, t.nombre,t.abreviatura, t.orden, pt.precio
				FROM producto_tamano pt
				JOIN tamano t ON t.id_tmano = pt.id_tamano
				WHERE pt.id_producto = ? 
				ORDER BY t.orden ASC,t.id_tamano ASC
				LIMIT 1
				""";

		try(Connection con = DbPool.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, idProducto);
			try (ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					return Optional.of(mapTamanoPrecio(rs));
					
				}
				
			} 
			
			
		} catch (Exception e) {
			
			throw new RuntimeException("Error buscando el primer tammaño para producto "+idProducto,e);
		}
		return Optional.empty();
	}

	private Optional<TamanoPrecioDTO> selectByNombreTamano(int idProducto, String nombreTamano) {
		String sql = """
				SELECT t.id_taano, t.nombre,t.abreviatura, t.orden, pt.precio
				FROM producto_tamano pt
				JOIN tamano t ON t.id_tmano = pt.id_tamano
				WHERE pt.id_producto = ? AND t.nombre = ?
				LIMIT 1
				""";
		try (Connection con = DbPool.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idProducto);
			ps.setString(2, nombreTamano);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapTamanoPrecio(rs));
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Error buscando tamaño" + nombreTamano,e);

		}
		return Optional.empty();
	}

	private TamanoPrecioDTO mapTamanoPrecio(ResultSet rs) throws Exception {
		TamanoDTO tam = new TamanoDTO(rs.getInt("id_tamano"), rs.getString("nombre"), rs.getString("abreviatura"));
		// si tu TamanoDTO también tiene 'orden', lo añadimos cuando quieras
		return new TamanoPrecioDTO(tam, rs.getBigDecimal("precio"));
	}

}

package dao;

import config.DbPool;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoTamanoDao {
	public Optional<TamanoPrecioDTO> selectTamanoDefaultYPrecio(int idProducto) {

		Optional<TamanoPrecioDTO> clasico = selectByNombreTamano(idProducto, "Clásico");
		if (clasico.isPresent()) {
			return clasico;

		}
		return selectPrimeroPorOrden(idProducto);
	}

	private Optional<TamanoPrecioDTO> selectPrimeroPorOrden(int idProducto) {
		String sql = """
				SELECT t.id_tamano, t.nombre,t.abreviatura, t.orden, pt.precio
				FROM producto_tamano pt
				JOIN tamano t ON t.id_tamano = pt.id_tamano
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
				SELECT t.id_tamano, t.nombre,t.abreviatura, t.orden, pt.precio
				FROM producto_tamano pt
				JOIN tamano t ON t.id_tamano = pt.id_tamano
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
	public List<TamanoDTO> findTamanosByProducto(int idProducto) {
	    if (idProducto <= 0) {
	        throw new IllegalArgumentException("idProducto debe ser > 0");
	    }

	    String sql = """
	            SELECT
	                t.id_tamano,
	                t.nombre,
	                t.abreviatura
	            FROM producto_tamano pt
	            JOIN tamano t ON t.id_tamano = pt.id_tamano
	            WHERE pt.id_producto = ?
	            ORDER BY t.orden ASC, t.id_tamano ASC
	            """;

	    List<TamanoDTO> tamanos = new ArrayList<>();

	    try (Connection con = DbPool.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, idProducto);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                tamanos.add(new TamanoDTO(
	                        rs.getInt("id_tamano"),
	                        rs.getString("nombre"),
	                        rs.getString("abreviatura")
	                ));
	            }
	        }

	    } catch (SQLException e) {
	        throw new RuntimeException("Error cargando tamaños del producto " + idProducto, e);
	    }

	    return tamanos;
	}
	public Optional<TamanoPrecioDTO> findByProductoYTamano(int idProducto, int idTamano) {
	    if (idProducto <= 0) {
	        throw new IllegalArgumentException("idProducto debe ser > 0");
	    }
	    if (idTamano <= 0) {
	        throw new IllegalArgumentException("idTamano debe ser > 0");
	    }

	    String sql = """
	            SELECT
	                t.id_tamano,
	                t.nombre,
	                t.abreviatura,
	                pt.precio
	            FROM producto_tamano pt
	            JOIN tamano t ON t.id_tamano = pt.id_tamano
	            WHERE pt.id_producto = ?
	              AND pt.id_tamano = ?
	            LIMIT 1
	            """;

	    try (Connection con = DbPool.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, idProducto);
	        ps.setInt(2, idTamano);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return Optional.of(mapTamanoPrecio(rs));
	            }
	        } catch (Exception e) {
				
	        	 throw new RuntimeException(
	 	                "Error mapenado  tamaño " + idTamano + " para producto " + idProducto, e
	 	        );
			}

	    } catch (SQLException e) {
	        throw new RuntimeException(
	                "Error cargando tamaño " + idTamano + " para producto " + idProducto, e
	        );
	    }

	    return Optional.empty();
	}

	private TamanoPrecioDTO mapTamanoPrecio(ResultSet rs) throws Exception {
		TamanoDTO tam = new TamanoDTO(rs.getInt("id_tamano"), rs.getString("nombre"), rs.getString("abreviatura"));
		// si tu TamanoDTO también tiene 'orden', lo añadimos cuando quieras
		return new TamanoPrecioDTO(tam, rs.getBigDecimal("precio"));
	}
	

}

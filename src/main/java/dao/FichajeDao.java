package dao;

import config.DbPool;
import dtoS.FichajeActivoDTO;
import enums.EstadoFichaje;
import model.Fichaje;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FichajeDao {

	// =========================================
	// INSERTAR NUEVO FICHAJE (ENTRADA)
	// =========================================
	public void insert(Fichaje fichaje) {

		String sql = """
				    INSERT INTO fichaje (id_usuario, fecha_entrada, estado)
				    VALUES (?, NOW(), ?)
				""";

		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, fichaje.getIdUsuario());
			ps.setString(2, fichaje.getEstado().name());

			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					fichaje.setIdFichaje(keys.getInt(1));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error insertando fichaje", e);
		}
	}

	// =========================================
	// CERRAR FICHAJE (SALIDA)
	// =========================================
	public void cerrarFichaje(int idFichaje) {

		String sql = """
				    UPDATE fichaje
				    SET fecha_salida = NOW(),
				        duracion = TIMESTAMPDIFF(MINUTE, fecha_entrada, NOW()),
				        estado = 'CERRADO'
				    WHERE id_fichaje = ?
				      AND estado = 'ABIERTO'
				""";

		try (Connection conn = DbPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idFichaje);
			ps.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException("Error cerrando fichaje", e);
		}
	}

	// =========================================
	// BUSCAR FICHAJE ABIERTO POR USUARIO
	// =========================================
	public Optional<Fichaje> findFichajeAbiertoByUsuario(int idUsuario) {

		String sql = """
				    SELECT *
				    FROM fichaje
				    WHERE id_usuario = ?
				      AND estado = 'ABIERTO'
				    LIMIT 1
				""";

		try (Connection conn = DbPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idUsuario);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(map(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error buscando fichaje abierto", e);
		}

		return Optional.empty();
	}

	// =========================================
	// BUSCAR FICHAJE POR ID
	// =========================================
	public Optional<Fichaje> findById(int idFichaje) {

		String sql = """
				    SELECT *
				    FROM fichaje
				    WHERE id_fichaje = ?
				""";

		try (Connection conn = DbPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idFichaje);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(map(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error buscando fichaje por id", e);
		}

		return Optional.empty();
	}

	// =========================================
	// HISTÓRICO DE FICHAJES POR USUARIO
	// =========================================
	public List<Fichaje> findByUsuario(int idUsuario) {

		List<Fichaje> fichajes = new ArrayList<>();

		String sql = """
				    SELECT *
				    FROM fichaje
				    WHERE id_usuario = ?
				    ORDER BY fecha_entrada DESC
				""";

		try (Connection conn = DbPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idUsuario);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					fichajes.add(map(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error obteniendo fichajes del usuario", e);
		}

		return fichajes;
	}

	// =========================================
	// MAPEO RESULTSET → ENTIDAD
	// =========================================
	private Fichaje map(ResultSet rs) throws SQLException {

		Fichaje f = new Fichaje();

		f.setIdFichaje(rs.getInt("id_fichaje"));
		f.setIdUsuario(rs.getInt("id_usuario"));
		f.setFechaEntrada(rs.getTimestamp("fecha_entrada").toLocalDateTime());

		Timestamp salida = rs.getTimestamp("fecha_salida");
		if (salida != null) {
			f.setFechaSalida(salida.toLocalDateTime());
		}

		f.setDuracion(rs.getObject("duracion", Integer.class));
		f.setEstado(EstadoFichaje.valueOf(rs.getString("estado")));

		return f;
	}

	

	public List<FichajeActivoDTO> findFichajesActivosConUsuario() {

		String sql = """
				    SELECT u.nombre AS nombre_empleado,
				           f.fecha_entrada,
				           f.estado
				    FROM fichaje f
				    JOIN usuario u ON f.id_usuario = u.id_usuario
				    WHERE f.estado = 'ABIERTO'
				    ORDER BY f.fecha_entrada
				""";

		List<FichajeActivoDTO> resultado = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				resultado.add(new FichajeActivoDTO(rs.getString("nombre_empleado"),
						rs.getTimestamp("fecha_entrada").toLocalDateTime().format(formatter), rs.getString("estado")));
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error obteniendo fichajes activos con usuario", e);
		}

		return resultado;
	}
}

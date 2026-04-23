package dao;

import config.DbPool;
import dtoS.FichajeActivoDTO;
import dtoS.FichajeEmpleadoRowDTO;
import enums.EstadoFichaje;
import model.Fichaje;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FichajeDao {
	public List<FichajeEmpleadoRowDTO> findRowsByFiltro(Integer idSucursal, Integer idUsuario, String textoBusqueda,
			String estado, LocalDate fechaDesde, LocalDate fechaHasta) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append("f.id_fichaje, f.id_usuario, u.nombre AS nombre_empleado, ")
				.append("f.id_sucursal, s.nombre AS nombre_sucursal, ")
				.append("f.fecha_entrada, f.fecha_salida, f.duracion, f.estado, f.observaciones ")
				.append("FROM fichaje f ").
				append("JOIN usuario u ON u.id_usuario = f.id_usuario ")
				.append("JOIN sucursal s ON s.id_sucursal = f.id_sucursal ")
				.append("WHERE 1=1 ");

		List<Object> params = new ArrayList<>();

		if (idSucursal != null) {
			sql.append("AND f.id_sucursal = ? ");
			params.add(idSucursal);
		}

		if (idUsuario != null) {
			sql.append("AND f.id_usuario = ? ");
			params.add(idUsuario);
		}

		if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
			sql.append("AND (u.nombre LIKE ? OR u.usuario LIKE ?) ");
			String like = "%" + textoBusqueda.trim() + "%";
			params.add(like);
			params.add(like);
		}

		if (estado != null && !estado.trim().isEmpty() && !"TODOS".equalsIgnoreCase(estado)) {
			sql.append("AND f.estado = ? ");
			params.add(estado.trim());
		}

		if (fechaDesde != null) {
			sql.append("AND DATE(f.fecha_entrada) >= ? ");
			params.add(Date.valueOf(fechaDesde));
		}

		if (fechaHasta != null) {
			sql.append("AND DATE(f.fecha_entrada) <= ? ");
			params.add(Date.valueOf(fechaHasta));
		}

		sql.append("ORDER BY f.fecha_entrada DESC, f.id_fichaje DESC");

		List<FichajeEmpleadoRowDTO> rows = new ArrayList<>();

		try (Connection conn = DbPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					FichajeEmpleadoRowDTO dto = new FichajeEmpleadoRowDTO();
					dto.setIdFichaje(rs.getInt("id_fichaje"));
					dto.setIdUsuario(rs.getInt("id_usuario"));
					dto.setNombreEmpleado(rs.getString("nombre_empleado"));
					dto.setIdSucursal(rs.getInt("id_sucursal"));
					dto.setNombreSucursal(rs.getString("nombre_sucursal"));

					Timestamp tsEntrada = rs.getTimestamp("fecha_entrada");
					dto.setFechaEntrada(tsEntrada != null ? tsEntrada.toLocalDateTime() : null);

					Timestamp tsSalida = rs.getTimestamp("fecha_salida");
					dto.setFechaSalida(tsSalida != null ? tsSalida.toLocalDateTime() : null);

					int duracion = rs.getInt("duracion");
					dto.setDuracionMinutos(rs.wasNull() ? null : duracion);

					dto.setEstado(rs.getString("estado"));
					dto.setObservaciones(rs.getString("observaciones"));

					rows.add(dto);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error al buscar fichajes filtrados.", e);
		}

		return rows;
	}

	// =========================================
	// INSERTAR NUEVO FICHAJE (ENTRADA)
	// =========================================
	public void insert(Fichaje fichaje) {
		String sql = """
				INSERT INTO fichaje (id_usuario, id_sucursal, fecha_entrada, estado)
				VALUES (?, ?, NOW(), ?)
				""";

		try (Connection conn = DbPool.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, fichaje.getIdUsuario());
			ps.setInt(2, fichaje.getIdSucursal());
			ps.setString(3, fichaje.getEstado().name());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					fichaje.setIdFichaje(rs.getInt(1));
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
		f.setIdSucursal(rs.getInt("id_sucursal"));

		return f;
	}

	public List<FichajeActivoDTO> findFichajesActivosConUsuario() {

		String sql = """
				    SELECT f.id_usuario,
				           u.nombre AS nombre_empleado,
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
				resultado.add(new FichajeActivoDTO(rs.getInt("id_usuario"), rs.getString("nombre_empleado"),
						rs.getTimestamp("fecha_entrada").toLocalDateTime().format(formatter), rs.getString("estado")));
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error obteniendo fichajes activos con usuario", e);
		}

		return resultado;
	}
}

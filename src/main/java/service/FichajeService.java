package service;

import dao.FichajeDao;
import dao.SesionCajaDao;
import dtoS.FichajeActivoDTO;
import dtoS.FichajeEmpleadoRowDTO;
import enums.EstadoFichaje;
import model.Fichaje;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FichajeService {

	private final FichajeDao fichajeDao;
	private final SesionCajaDao sesionCajaDao;
	private final AuditoriaService auditoriaService;

	public FichajeService(FichajeDao fichajeDao, SesionCajaDao sesionCajaDao, AuditoriaService auditoriaService) {
		if (fichajeDao == null || sesionCajaDao == null || auditoriaService == null) {
			throw new IllegalArgumentException("Dependencias no pueden ser null");
		}
		this.fichajeDao = fichajeDao;
		this.sesionCajaDao = sesionCajaDao;
		this.auditoriaService = auditoriaService;
	}

	// =========================
	// FICHAR ENTRADA
	// =========================
	public Fichaje ficharEntrada(int idUsuario, int idSucursal) {
		validarIdUsuario(idUsuario);
		validarIdSucursal(idSucursal);

		fichajeDao.findFichajeAbiertoByUsuario(idUsuario).ifPresent(f -> {
			throw new IllegalStateException("Usuario ya tiene un fichaje abierto");
		});

		Fichaje nuevo = new Fichaje();
		nuevo.setIdUsuario(idUsuario);
		nuevo.setIdSucursal(idSucursal);
		nuevo.setEstado(EstadoFichaje.ABIERTO);
		nuevo.setObservaciones(null);

		fichajeDao.insert(nuevo);

		Fichaje fichajeCreado = fichajeDao.findFichajeAbiertoByUsuario(idUsuario)
				.orElseThrow(() -> new IllegalStateException("Error al crear el fichaje"));

		auditarSeguro(idUsuario, idSucursal, "FICHAJE_ENTRADA_OK", detallesEntrada(fichajeCreado));

		return fichajeCreado;
	}

	// =========================
	// FICHAR SALIDA
	// =========================
	public Fichaje ficharSalida(int idUsuario) {
		validarIdUsuario(idUsuario);

		Fichaje abierto = fichajeDao.findFichajeAbiertoByUsuario(idUsuario)
				.orElseThrow(() -> new IllegalStateException("Usuario no tiene fichaje abierto"));

		if (sesionCajaDao.existeSesionAbiertaPorUsuario(idUsuario)) {
			auditarSeguro(idUsuario, abierto.getIdSucursal(), "FICHAJE_SALIDA_BLOQUEADA_CAJA_ABIERTA",
					detallesSalidaBloqueada(abierto));

			throw new IllegalStateException(
					"No puedes fichar salida mientras tengas una sesión de caja abierta. Cierra primero la caja asignada.");
		}

		fichajeDao.cerrarFichaje(abierto.getIdFichaje());

		Fichaje fichajeCerrado = fichajeDao.findById(abierto.getIdFichaje())
				.orElseThrow(() -> new IllegalStateException("Error al cerrar el fichaje"));

		auditarSeguro(idUsuario, fichajeCerrado.getIdSucursal(), "FICHAJE_SALIDA_OK", detallesSalida(fichajeCerrado));

		return fichajeCerrado;
	}

	public List<FichajeEmpleadoRowDTO> buscarFichajesAdministracion(Integer idSucursal, Integer idUsuario,
			String textoBusqueda, String estado, LocalDate fechaDesde, LocalDate fechaHasta) {
		return fichajeDao.findRowsByFiltro(idSucursal, idUsuario, textoBusqueda, estado, fechaDesde, fechaHasta);
	}

	public List<FichajeActivoDTO> findFichajesActivos() {
		return fichajeDao.findFichajesActivosConUsuario();
	}

	// =========================
	// VALIDACIONES
	// =========================
	private void validarIdUsuario(int idUsuario) {
		if (idUsuario <= 0) {
			throw new IllegalArgumentException("Id de usuario no válido");
		}
	}

	private void validarIdSucursal(int idSucursal) {
		if (idSucursal <= 0) {
			throw new IllegalArgumentException("Id de sucursal no válido");
		}
	}

	// =========================
	// DETALLES AUDITORÍA
	// =========================
	private Map<String, Object> detallesEntrada(Fichaje fichaje) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("idFichaje", fichaje.getIdFichaje());
		data.put("idUsuario", fichaje.getIdUsuario());
		data.put("idSucursal", fichaje.getIdSucursal());
		data.put("fechaEntrada", fichaje.getFechaEntrada() != null ? fichaje.getFechaEntrada().toString() : null);
		data.put("estado", fichaje.getEstado() != null ? fichaje.getEstado().name() : null);
		return data;
	}

	private Map<String, Object> detallesSalida(Fichaje fichaje) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("idFichaje", fichaje.getIdFichaje());
		data.put("idUsuario", fichaje.getIdUsuario());
		data.put("idSucursal", fichaje.getIdSucursal());
		data.put("fechaEntrada", fichaje.getFechaEntrada() != null ? fichaje.getFechaEntrada().toString() : null);
		data.put("fechaSalida", fichaje.getFechaSalida() != null ? fichaje.getFechaSalida().toString() : null);
		data.put("duracionMinutos", fichaje.getDuracion());
		data.put("estado", fichaje.getEstado() != null ? fichaje.getEstado().name() : null);
		return data;
	}

	private Map<String, Object> detallesSalidaBloqueada(Fichaje fichajeAbierto) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("idFichaje", fichajeAbierto.getIdFichaje());
		data.put("idUsuario", fichajeAbierto.getIdUsuario());
		data.put("idSucursal", fichajeAbierto.getIdSucursal());
		data.put("fechaEntrada",
				fichajeAbierto.getFechaEntrada() != null ? fichajeAbierto.getFechaEntrada().toString() : null);
		data.put("estado", fichajeAbierto.getEstado() != null ? fichajeAbierto.getEstado().name() : null);
		data.put("motivoBloqueo", "SESION_CAJA_ABIERTA");
		return data;
	}

	// =========================
	// AUDITORÍA SEGURA
	// =========================
	private void auditarSeguro(int idUsuario, int idSucursal, String accion, Map<String, Object> detalles) {
		try {
			auditoriaService.registrarEvento(idUsuario, idSucursal, accion, detalles);
		} catch (Exception ex) {
			System.err.println("[AUDITORIA] No se pudo registrar evento " + accion + ": " + ex.getMessage());
		}
	}
}
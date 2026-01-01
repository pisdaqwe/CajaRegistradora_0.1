package service;

import java.util.List;
import java.util.Optional;

import dao.UsuarioRecordadoDao;
import model.Usuario;
import model.UsuarioRecordado;

public class UsuarioRecordadoService {
	private final  UsuarioRecordadoDao uRecordadoDao;
	
	public UsuarioRecordadoService (UsuarioRecordadoDao usuarioRecordadoDao){
		if(usuarioRecordadoDao == null) {
			
			throw new IllegalArgumentException("UsuarioRecordadoDao no puede ser Null");
		}
	
		this.uRecordadoDao = usuarioRecordadoDao;
	}
	
	// =========================
    // OBTENER BOTONES DEL TERMINAL
    // =========================
	public List<UsuarioRecordado> getUsuariosRecordadosDelTerminal(int idTerminal) {
        return uRecordadoDao.findButtonsByTerminal(idTerminal);
    }
	
	// =========================
    // REGISTRAR ACCESO EXITOSO
    // =========================
	public void registrarAccesoExitoso(Usuario usuario,int idTerminal) {
		if(usuario == null) {
			throw new IllegalArgumentException("El usuario no puede ser null");
		}
		int idUsuario = usuario.getIdUsuario();
		
		Optional<UsuarioRecordado> existente =
				uRecordadoDao.findByTerminalAndUsuario(idTerminal, idUsuario);
		
		if(existente.isPresent()) {
			// Ya tenía botón → solo actualizar último acceso
			uRecordadoDao.updateUltimoAcceso(existente.get().getIdBoton());
			return;
		}
		
		// No tenía botón → crear uno nuevo
        crearNuevoUsuarioRecordado(usuario, idTerminal);
	}

	private void crearNuevoUsuarioRecordado(Usuario usuario, int idTerminal) {
		int posicionLibre = uRecordadoDao.findFirstFreePosition(idTerminal);
		String nombreBoton = generarNombreBoton(usuario, posicionLibre);
		UsuarioRecordado nuevo = new UsuarioRecordado();
		nuevo.setIdUsuario(usuario.getIdUsuario());
		nuevo.setIdTerminal(idTerminal);
		nuevo.setNombreBoton(nombreBoton);
		nuevo.setPosicion(posicionLibre);
		
		uRecordadoDao.insert(nuevo);
		
		
		
		
	}
	// =========================
	// ELIMINAR BOTÓN MANUALMENTE
	// =========================
	public void eliminarUsuarioRecordado(int idBoton) {

	    if (idBoton <= 0) {
	        throw new IllegalArgumentException("Id de botón no válido");
	    }

	    uRecordadoDao.deleteById(idBoton);
	}

	// =========================
	// LIMPIAR BOTONES POR INACTIVIDAD
	// =========================
	public int limpiarUsuariosInactivos(int idTerminal, int dias) {

	    if (idTerminal <= 0) {
	        throw new IllegalArgumentException("Terminal no válido");
	    }

	    if (dias <= 0) {
	        throw new IllegalArgumentException("Días no válidos");
	    }

	    return uRecordadoDao.deleteByInactividad(idTerminal, dias);
	}

	private String generarNombreBoton(Usuario usuario,int posicion) {
		String nombre = usuario.getNombre();
		if(nombre== null||nombre.isBlank()) {
			return "USUARIO "+posicion;
			
		}
		String[]partes = nombre.trim().split("\\s+");
		if(partes.length==1) {
			return partes[0].toUpperCase();
		}
		String inicialApellido = partes[partes.length-1].substring(0,1).toUpperCase();
		return (partes[0]+" "+inicialApellido);
	}
	
}

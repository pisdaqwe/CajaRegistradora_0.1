package service;

import dao.FichajeDao;
import dao.RolDao;
import dao.SesionCajaDao;
import dao.UsuarioDao;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import dtoS.EmpleadoSaveRequest;
import dtoS.ResetPinEmpleadoRequest;
import model.Rol;
import model.Usuario;
import util.HashUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestión de usuarios / empleados.
 *
 * Responsabilidades:
 * - búsqueda y filtrado de empleados
 * - detalle ampliado
 * - alta y edición
 * - activación / desactivación
 * - reset de PIN
 * - auditoría de acciones administrativas
 */
public class UsuarioService {

    private final UsuarioDao usuarioDao;
    private final RolDao rolDao;
    private final FichajeDao fichajeDao;
    private final SesionCajaDao sesionCajaDao;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioDao usuarioDao,
                          RolDao rolDao,
                          FichajeDao fichajeDao,
                          SesionCajaDao sesionCajaDao,
                          AuditoriaService auditoriaService) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.fichajeDao = fichajeDao;
        this.sesionCajaDao = sesionCajaDao;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Método reutilizable en login y otros flujos.
     */
    public Optional<Usuario> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioDao.findByUsuario(codigo.trim());
    }

    public List<Usuario> findActivosBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("La sucursal no es válida.");
        }
        return usuarioDao.findActivosBySucursal(idSucursal);
    }

    public List<EmpleadoRowDTO> buscarEmpleados(EmpleadoFiltroDTO filtro) {
        if (filtro == null) {
            filtro = new EmpleadoFiltroDTO();
        }
        return usuarioDao.findRowsByFiltro(filtro);
    }

    public Optional<EmpleadoDetalleDTO> getDetalleEmpleado(int idUsuario) {
        if (idUsuario <= 0) {
            return Optional.empty();
        }
        return usuarioDao.findDetalleById(idUsuario);
    }

    public List<Rol> getRoles() {
        return rolDao.findAll();
    }

    public int crearEmpleado(EmpleadoSaveRequest request) {
        validarRequestAlta(request);

        Rol rol = rolDao.findById(request.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol seleccionado."));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre().trim());
        usuario.setUsuario(request.getUsuario().trim());
        usuario.setPinHash(hashPin(request.getPinPlano().trim()));
        usuario.setRol(rol);
        usuario.setIdSucursal(request.getIdSucursal());
        usuario.setActivo(request.isActivo());

        int idUsuarioCreado = usuarioDao.insert(usuario);

        auditarSeguro(
                request.getIdUsuarioAdmin(),
                request.getIdSucursalAdmin(),
                "USUARIO_ALTA_OK",
                detallesAlta(idUsuarioCreado, request, rol)
        );

        return idUsuarioCreado;
    }

    public void actualizarEmpleado(EmpleadoSaveRequest request) {
        validarRequestEdicion(request);

        Usuario actual = usuarioDao.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado a editar."));

        Rol rolNuevo = rolDao.findById(request.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol seleccionado."));

        Rol rolAnterior = actual.getRol();
        String nombreAnterior = actual.getNombre();
        String usuarioAnterior = actual.getUsuario();
        Integer idSucursalAnterior = actual.getIdSucursal();
        boolean activoAnterior = actual.isActivo();

        actual.setNombre(request.getNombre().trim());
        actual.setUsuario(request.getUsuario().trim());
        actual.setRol(rolNuevo);
        actual.setIdSucursal(request.getIdSucursal());
        actual.setActivo(request.isActivo());

        usuarioDao.update(actual);

        auditarSeguro(
                request.getIdUsuarioAdmin(),
                request.getIdSucursalAdmin(),
                "USUARIO_EDICION_OK",
                detallesEdicion(actual, nombreAnterior, usuarioAnterior, idSucursalAnterior, activoAnterior, rolAnterior, rolNuevo)
        );
    }

    public void cambiarEstadoActivo(int idUsuario, boolean nuevoActivo, int idUsuarioAdmin, int idSucursalAdmin) {
        Usuario usuario = usuarioDao.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado."));

        if (!nuevoActivo) {
            boolean tieneCajaAbierta = sesionCajaDao.existsSesionAbiertaByUsuario(idUsuario);
            if (tieneCajaAbierta) {
                throw new IllegalStateException("No se puede desactivar un empleado con sesión de caja abierta.");
            }

            boolean tieneFichajeAbierto = fichajeDao.findFichajeAbiertoByUsuario(idUsuario).isPresent();
            if (tieneFichajeAbierto) {
                throw new IllegalStateException("No se puede desactivar un empleado con fichaje abierto.");
            }
        }

        boolean estadoAnterior = usuario.isActivo();
        usuarioDao.updateActivo(usuario.getIdUsuario(), nuevoActivo);

        auditarSeguro(
                idUsuarioAdmin,
                idSucursalAdmin,
                nuevoActivo ? "USUARIO_ACTIVADO_OK" : "USUARIO_DESACTIVADO_OK",
                detallesCambioEstado(usuario, estadoAnterior, nuevoActivo)
        );
    }

    /**
     * Variante legacy si en alguna parte ya la usabas.
     * Internamente audita con el propio usuario objetivo si no hay admin explícito.
     */
    public void cambiarEstadoActivo(int idUsuario, boolean nuevoActivo) {
        Usuario usuario = usuarioDao.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado."));

        cambiarEstadoActivo(idUsuario, nuevoActivo, usuario.getIdUsuario(), usuario.getIdSucursal() != null ? usuario.getIdSucursal() : 0);
    }

    public void resetPin(ResetPinEmpleadoRequest request) {
        validarResetPin(request);

        Usuario usuario = usuarioDao.findById(request.getIdUsuarioObjetivo())
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado."));

        String nuevoHash = hashPin(request.getNuevoPin().trim());
        usuarioDao.updatePinHash(usuario.getIdUsuario(), nuevoHash);

        auditarSeguro(
                request.getIdUsuarioAdmin(),
                request.getIdSucursalAdmin(),
                "USUARIO_RESET_PIN_OK",
                detallesResetPin(usuario)
        );
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarRequestAlta(EmpleadoSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de alta no puede ser nula.");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (request.getUsuario() == null || request.getUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario/código es obligatorio.");
        }

        if (usuarioDao.existsByUsuario(request.getUsuario().trim())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese usuario/código.");
        }

        if (request.getPinPlano() == null || request.getPinPlano().trim().isEmpty()) {
            throw new IllegalArgumentException("El PIN es obligatorio.");
        }

        if (request.getConfirmarPin() == null || !request.getPinPlano().equals(request.getConfirmarPin())) {
            throw new IllegalArgumentException("La confirmación del PIN no coincide.");
        }

        if (request.getIdRol() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar un rol válido.");
        }

        if (request.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar una sucursal válida.");
        }
    }

    private void validarRequestEdicion(EmpleadoSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de edición no puede ser nula.");
        }

        if (request.getIdUsuario() == null || request.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El id del empleado es obligatorio para editar.");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (request.getUsuario() == null || request.getUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario/código es obligatorio.");
        }

        if (usuarioDao.existsByUsuarioExcludingId(request.getUsuario().trim(), request.getIdUsuario())) {
            throw new IllegalArgumentException("Ya existe otro empleado con ese usuario/código.");
        }

        if (request.getIdRol() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar un rol válido.");
        }

        if (request.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar una sucursal válida.");
        }
    }

    private void validarResetPin(ResetPinEmpleadoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        if (request.getIdUsuarioObjetivo() <= 0) {
            throw new IllegalArgumentException("Empleado objetivo no válido.");
        }

        if (request.getNuevoPin() == null || request.getNuevoPin().trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo PIN es obligatorio.");
        }

        if (request.getConfirmarPin() == null || !request.getNuevoPin().equals(request.getConfirmarPin())) {
            throw new IllegalArgumentException("La confirmación del PIN no coincide.");
        }
    }

    // =========================================================
    // HASH PIN
    // =========================================================

    private String hashPin(String pinPlano) {
        return HashUtil.sha256(pinPlano);
    }

    // =========================================================
    // DETALLES AUDITORÍA
    // =========================================================

    private Map<String, Object> detallesAlta(int idUsuarioCreado, EmpleadoSaveRequest request, Rol rol) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idUsuarioObjetivo", idUsuarioCreado);
        data.put("nombre", request.getNombre());
        data.put("usuario", request.getUsuario());
        data.put("idRol", rol.getIdRol());
        data.put("rol", rol.getNombre());
        data.put("idSucursalObjetivo", request.getIdSucursal());
        data.put("activo", request.isActivo());
        return data;
    }

    private Map<String, Object> detallesEdicion(Usuario actual,
                                                String nombreAnterior,
                                                String usuarioAnterior,
                                                Integer idSucursalAnterior,
                                                boolean activoAnterior,
                                                Rol rolAnterior,
                                                Rol rolNuevo) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idUsuarioObjetivo", actual.getIdUsuario());

        data.put("nombreAnterior", nombreAnterior);
        data.put("nombreNuevo", actual.getNombre());

        data.put("usuarioAnterior", usuarioAnterior);
        data.put("usuarioNuevo", actual.getUsuario());

        data.put("idSucursalAnterior", idSucursalAnterior);
        data.put("idSucursalNueva", actual.getIdSucursal());

        data.put("activoAnterior", activoAnterior);
        data.put("activoNuevo", actual.isActivo());

        data.put("idRolAnterior", rolAnterior != null ? rolAnterior.getIdRol() : null);
        data.put("rolAnterior", rolAnterior != null ? rolAnterior.getNombre() : null);
        data.put("idRolNuevo", rolNuevo != null ? rolNuevo.getIdRol() : null);
        data.put("rolNuevo", rolNuevo != null ? rolNuevo.getNombre() : null);

        return data;
    }

    private Map<String, Object> detallesCambioEstado(Usuario usuario, boolean estadoAnterior, boolean estadoNuevo) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idUsuarioObjetivo", usuario.getIdUsuario());
        data.put("nombre", usuario.getNombre());
        data.put("usuario", usuario.getUsuario());
        data.put("activoAnterior", estadoAnterior);
        data.put("activoNuevo", estadoNuevo);
        data.put("idSucursalObjetivo", usuario.getIdSucursal());
        return data;
    }

    private Map<String, Object> detallesResetPin(Usuario usuario) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("idUsuarioObjetivo", usuario.getIdUsuario());
        data.put("nombre", usuario.getNombre());
        data.put("usuario", usuario.getUsuario());
        data.put("idSucursalObjetivo", usuario.getIdSucursal());
        return data;
    }

    // =========================================================
    // AUDITORÍA SEGURA
    // =========================================================

    private void auditarSeguro(int idUsuario,
                               int idSucursal,
                               String accion,
                               Map<String, Object> detalles) {
        if (idUsuario <= 0 || idSucursal <= 0) {
            return;
        }

        try {
            auditoriaService.registrarEvento(idUsuario, idSucursal, accion, detalles);
        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo registrar evento " + accion + ": " + ex.getMessage());
        }
    }
}
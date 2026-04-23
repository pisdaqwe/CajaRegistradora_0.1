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

import java.util.List;
import java.util.Optional;

/**
 * Service de gestión de usuarios / empleados.
 *
 * Responsabilidades:
 * - Búsqueda y filtrado de empleados.
 * - Obtención de detalle ampliado.
 * - Alta y edición de empleados.
 * - Activación / desactivación.
 * - Reset de PIN.
 *
 * NO debe:
 * - Contener lógica de UI.
 * - Construir SQL.
 */
public class UsuarioService {

    private final UsuarioDao usuarioDao;
    private final RolDao rolDao;
    private final FichajeDao fichajeDao;
    private final SesionCajaDao sesionCajaDao;

    public UsuarioService(UsuarioDao usuarioDao,
                          RolDao rolDao,
                          FichajeDao fichajeDao,
                          SesionCajaDao sesionCajaDao) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.fichajeDao = fichajeDao;
        this.sesionCajaDao = sesionCajaDao;
    }

    /**
     * Método legado / reutilizable si ya lo usabas en login o en otras zonas.
     */
    public Optional<Usuario> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioDao.findByUsuario(codigo.trim());
    }

    /**
     * Método legado / reutilizable.
     */
    public List<Usuario> findActivosBySucursal(int idSucursal) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("La sucursal no es válida.");
        }
        return usuarioDao.findActivosBySucursal(idSucursal);
    }

    /**
     * Listado filtrado para la tabla principal de gestión.
     */
    public List<EmpleadoRowDTO> buscarEmpleados(EmpleadoFiltroDTO filtro) {
        if (filtro == null) {
            filtro = new EmpleadoFiltroDTO();
        }
        return usuarioDao.findRowsByFiltro(filtro);
    }

    /**
     * Detalle ampliado del empleado seleccionado.
     */
    public Optional<EmpleadoDetalleDTO> getDetalleEmpleado(int idUsuario) {
        if (idUsuario <= 0) {
            return Optional.empty();
        }
        return usuarioDao.findDetalleById(idUsuario);
    }

    /**
     * Roles disponibles para formularios.
     */
    public List<Rol> getRoles() {
        return rolDao.findAll();
    }

    /**
     * Alta de empleado.
     */
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

        return usuarioDao.insert(usuario);
    }

    /**
     * Edición de empleado.
     */
    public void actualizarEmpleado(EmpleadoSaveRequest request) {
        validarRequestEdicion(request);

        Usuario actual = usuarioDao.findById(request.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado a editar."));

        Rol rol = rolDao.findById(request.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol seleccionado."));

        actual.setNombre(request.getNombre().trim());
        actual.setUsuario(request.getUsuario().trim());
        actual.setRol(rol);
        actual.setIdSucursal(request.getIdSucursal());
        actual.setActivo(request.isActivo());

        usuarioDao.update(actual);
    }

    /**
     * Activa o desactiva un empleado.
     *
     * Regla actual:
     * - No se puede desactivar si tiene sesión de caja abierta.
     * - No se puede desactivar si tiene fichaje abierto.
     */
    public void cambiarEstadoActivo(int idUsuario, boolean nuevoActivo) {
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

        usuarioDao.updateActivo(usuario.getIdUsuario(), nuevoActivo);
    }

    /**
     * Resetea el PIN de un empleado.
     */
    public void resetPin(ResetPinEmpleadoRequest request) {
        validarResetPin(request);

        Usuario usuario = usuarioDao.findById(request.getIdUsuarioObjetivo())
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado."));

        String nuevoHash = hashPin(request.getNuevoPin().trim());
        usuarioDao.updatePinHash(usuario.getIdUsuario(), nuevoHash);
    }

    // =========================================================
    // VALIDACIONES PRIVADAS
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

    /**
     * Sustituye este método por la utilidad real de hash que ya uses en tu proyecto.
     *
     * Ejemplos posibles:
     * - return HashUtil.sha256(pinPlano);
     * - return PinHasher.hash(pinPlano);
     * - return PasswordUtils.hash(pinPlano);
     */
    private String hashPin(String pinPlano) {
        return HashUtil.sha256(pinPlano);
    }
}
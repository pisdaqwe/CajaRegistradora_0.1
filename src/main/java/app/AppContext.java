package app;

import dtoS.SesionCajaRefDTO;
import model.Usuario;

public final class AppContext {

    private static Usuario usuarioActual;
    private static SesionCajaRefDTO sesionCajaActual;

    // =========================================
    // CONTEXTO FIJO DEL TERMINAL
    // =========================================
    private static Integer idCajaTerminal;
    private static Integer idSucursalTerminal;
    private static String nombreCajaTerminal;

    private AppContext() {
        // Evita instanciación
    }

    // =========================================
    // TERMINAL
    // =========================================
    public static void initTerminal(int idCaja, int idSucursal, String nombreCaja) {
        if (idCaja <= 0) {
            throw new IllegalArgumentException("idCajaTerminal debe ser > 0");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursalTerminal debe ser > 0");
        }
        if (nombreCaja == null || nombreCaja.isBlank()) {
            throw new IllegalArgumentException("nombreCajaTerminal no puede estar vacío");
        }

        idCajaTerminal = idCaja;
        idSucursalTerminal = idSucursal;
        nombreCajaTerminal = nombreCaja.trim();
    }

    public static boolean hasTerminalContext() {
        return idCajaTerminal != null
                && idSucursalTerminal != null
                && nombreCajaTerminal != null
                && !nombreCajaTerminal.isBlank();
    }

    public static int getIdCajaTerminal() {
        if (idCajaTerminal == null) {
            throw new IllegalStateException("No hay contexto de terminal cargado");
        }
        return idCajaTerminal;
    }

    public static int getIdSucursal() {
        if (idSucursalTerminal == null) {
            throw new IllegalStateException("No hay sucursal de terminal cargada");
        }
        return idSucursalTerminal;
    }

    public static String getNombreCajaTerminal() {
        if (nombreCajaTerminal == null || nombreCajaTerminal.isBlank()) {
            throw new IllegalStateException("No hay nombre de caja terminal cargado");
        }
        return nombreCajaTerminal;
    }

    // =========================================
    // USUARIO (solo tras login correcto)
    // =========================================
    public static void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }
        if (usuarioActual != null) {
            throw new IllegalStateException(
                    "Ya existe una sesión activa. Debe cerrarse antes."
            );
        }
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        if (usuarioActual == null) {
            throw new IllegalStateException(
                    "No hay sesión activa. Acceso no permitido."
            );
        }
        return usuarioActual;
    }

    public static int getUsuarioId() {
        return getUsuario().getIdUsuario();
    }

    public static boolean isAuthenticated() {
        return usuarioActual != null;
    }

    // =========================================
    // SESIÓN DE CAJA (asignada por admin)
    // =========================================
    public static void setSesionCajaActual(SesionCajaRefDTO ref) {
        if (ref == null) {
            throw new IllegalArgumentException("SesionCajaRefDTO no puede ser null");
        }
        if (sesionCajaActual != null && ref.getIdSesion() != sesionCajaActual.getIdSesion()) {
            throw new IllegalStateException(
                    "Ya existe una sesión de caja en AppContext. Debe limpiarse antes."
            );
        }
        sesionCajaActual = ref;
    }

    public static SesionCajaRefDTO getSesionCajaActual() {
        if (sesionCajaActual == null) {
            throw new IllegalStateException(
                    "No hay sesión de caja asignada. No se puede entrar a Ventas."
            );
        }
        return sesionCajaActual;
    }

    public static boolean hasSesionCajaActual() {
        return sesionCajaActual != null;
    }

    public static void clearSesionCajaActual() {
        sesionCajaActual = null;
    }

    // =========================================
    // LOGOUT
    // =========================================
    public static void clear() {
        usuarioActual = null;
        sesionCajaActual = null;
    }
}
package app;

import dtoS.SesionCajaRefDTO;
import model.Usuario;

public final class AppContext {

    private static Usuario usuarioActual;
    private static SesionCajaRefDTO sesionCajaActual;

    private AppContext() {
        // Evita instanciación
    }

    // =========================
    // USUARIO (solo tras login correcto)
    // =========================
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

    // =========================
    // SESIÓN DE CAJA (asignada por admin)
    // =========================
    public static void setSesionCajaActual(SesionCajaRefDTO ref) {
        if (ref == null) {
            throw new IllegalArgumentException("SesionCajaRefDTO no puede ser null");
        }
        if (sesionCajaActual != null) {
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

    // =========================
    // LOGOUT
    // =========================
    public static void clear() {
        usuarioActual = null;
        sesionCajaActual = null;
    }
}
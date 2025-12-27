package app;

import model.Usuario;

public final class AppContext {

    private static Usuario usuarioActual;

    private AppContext() {
        // Evita instanciación
    }

    // =========================
    // SET (solo tras login correcto)
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

    // =========================
    // GET (con guard)
    // =========================
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
    // LOGOUT
    // =========================
    public static void clear() {
        usuarioActual = null;
    }
}

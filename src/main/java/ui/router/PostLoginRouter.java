package ui.router;

import app.AppContext;
import enums.PostLoginDestination;
import model.Rol;
import model.Usuario;

public class PostLoginRouter {

    public PostLoginDestination route() {
        Usuario usuario = AppContext.getUsuario();

        if (usuario == null) {
            throw new IllegalStateException("No hay usuario autentificado en AppContext");
        }

        Rol rol = usuario.getRol();

        if (rol == null || rol.getNombre() == null) {
            throw new IllegalStateException(
                "Usuario sin rol válido: " + usuario.getIdUsuario()
            );
        }

        String nombreRol = rol.getNombre().toUpperCase();

        switch (nombreRol) {
            case "ADMIN":
            case "ENCARGADO":
                return PostLoginDestination.ADMIN_PANEL;

            case "TECNICO":
                return PostLoginDestination.TECHNIC_PANEL;

            case "CAJERO":
                if (!AppContext.hasSesionCajaActual()) {
                    throw new IllegalStateException(
                        "El usuario cajero no tiene una sesión de caja activa en AppContext."
                    );
                }
                return PostLoginDestination.OPERATIVE_HOME;

            default:
                throw new IllegalStateException(
                    "Rol no soportado en PostLogin: " + rol.getNombre()
                );
        }
    }
}

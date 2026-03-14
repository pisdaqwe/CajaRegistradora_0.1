package ui.common;

import app.AppContext;

import javax.swing.*;
import java.awt.*;

/**
 * Frame base para TODAS las pantallas del TPV.
 * - Añade barra superior con reloj (siempre visible)
 * - Incluye guard de sesión
 * - Maneja logout de forma centralizada
 */
public abstract class BaseTpvFrame extends JFrame {

    private final Runnable onLogoutNavigate;
    private final TpvTopBar topBar;
    protected final JPanel main = new JPanel(new BorderLayout());

    protected BaseTpvFrame(String screenTitle, Runnable onLogoutNavigate) {
        super(screenTitle);
        this.onLogoutNavigate = onLogoutNavigate;

        // Ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Contenedor principal
        setContentPane(main);

        // Barra superior siempre visible
        topBar = new TpvTopBar(screenTitle);
        main.add(topBar, BorderLayout.NORTH);
    }

    /**
     * Guard: si no hay sesión, no debe mostrarse esta pantalla.
     * Llama a logoutNavigate para volver al login.
     */
    protected final void requireAuthenticatedOrExit() {
        if (!AppContext.isAuthenticated()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay sesión activa. Volviendo al login.",
                    "Sesión requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            safeDispose();
            if (onLogoutNavigate != null) onLogoutNavigate.run();
        }
    }

    /**
     * Logout estándar para todas las pantallas:
     * - limpia AppContext
     * - cierra frame
     * - navega a login mediante callback
     */
    protected final void doLogout() {
        AppContext.clear();
        safeDispose();
        
        if (onLogoutNavigate != null) onLogoutNavigate.run();
    }

    /** Actualizar cabecera (por si cambias usuario/rol). */
    protected final void refreshHeader() {
        topBar.refreshUser();
    }

    /** Cierre seguro: parar reloj antes de destruir. */
    protected final void safeDispose() {
        topBar.stopClock();
        dispose();
    }
}

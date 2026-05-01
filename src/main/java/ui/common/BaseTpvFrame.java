package ui.common;

import app.AppContext;
import service.AppServices;
import ui.dialog.MonitorPreparacionDialog;
import util.I18n;

import javax.swing.*;
import java.awt.*;

/**
 * Frame base para TODAS las pantallas del TPV.
 * - Añade barra superior con reloj (siempre visible)
 * - Incluye guard de sesión
 * - Maneja logout de forma centralizada
 * - Añade acceso global al monitor de preparación
 */
public abstract class BaseTpvFrame extends JFrame {

    protected final Runnable onLogoutNavigate;
    protected final AppServices appServices;

    private final TpvTopBar topBar;
    private MonitorPreparacionDialog prepDialog;

    protected final JPanel main = new JPanel(new BorderLayout());

    protected BaseTpvFrame(String screenTitle, Runnable onLogoutNavigate, AppServices appServices) {
        super(screenTitle);
        this.onLogoutNavigate = onLogoutNavigate;
        this.appServices = appServices;

        // ----------------------------
        // VENTANA
        // ----------------------------
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ----------------------------
        // CONTENEDOR PRINCIPAL
        // ----------------------------
        setContentPane(main);

        // ----------------------------
        // BARRA SUPERIOR SIEMPRE VISIBLE
        // ----------------------------
        topBar = new TpvTopBar(screenTitle, this::openPreparationMonitor);
        main.add(topBar, BorderLayout.NORTH);
    }

    /**
     * Guard: si no hay sesión, no debe mostrarse esta pantalla.
     * Llama a logoutNavigate para volver al login.
     */
    protected final void requireAuthenticatedOrExit() {
        if (!AppContext.isAuthenticated()) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("base.sessionRequired.title"),
                    I18n.t("base.sessionRequired.message")
            );
            safeDispose();
            if (onLogoutNavigate != null) {
                onLogoutNavigate.run();
            }
        }
    }

    /**
     * Logout estándar para todas las pantallas:
     * - limpia AppContext
     * - cierra frame
     * - navega a login mediante callback
     *
     * Decisión de cierre del proyecto: NO se bloquea aquí aunque exista sesión de caja abierta.
     */
    protected final void doLogout() {
        AppContext.clear();
        safeDispose();

        if (onLogoutNavigate != null) {
            onLogoutNavigate.run();
        }
    }

    /**
     * Actualizar cabecera (por si cambias usuario/rol/caja).
     */
    protected final void refreshHeader() {
        topBar.refreshUser();
    }

    /**
     * Apertura global del monitor de preparación.
     * - No comprueba sesión
     * - No comprueba rol
     * - No bloquea la pantalla actual
     */
    protected void openPreparationMonitor() {
        if (prepDialog == null || !prepDialog.isDisplayable()) {
            prepDialog = buildPreparationDialog();
        }

        prepDialog.setLocationRelativeTo(this);
        prepDialog.setVisible(true);
        prepDialog.toFront();
        prepDialog.requestFocus();
    }

    /**
     * Construye el diálogo del monitor.
     */
    protected MonitorPreparacionDialog buildPreparationDialog() {
        return new MonitorPreparacionDialog(this, appServices);
    }

    /**
     * Cierre seguro: parar reloj antes de destruir.
     */
    protected final void safeDispose() {
        topBar.stopClock();

        if (prepDialog != null && prepDialog.isDisplayable()) {
            prepDialog.dispose();
        }

        dispose();
    }
}

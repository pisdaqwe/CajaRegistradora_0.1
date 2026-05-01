package ui.common;

import app.AppContext;
import model.Usuario;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Barra superior TPV: título + usuario/rol/caja + reloj + acceso rápido al monitor de preparación.
 * Se reutiliza en TODAS las pantallas para que el reloj sea siempre visible.
 */
public class TpvTopBar extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter CLOCK_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblUser = new JLabel();
    private final JLabel lblRole = new JLabel();
    private final JLabel lblCashBox = new JLabel();
    private final JLabel lblClock = new JLabel();
    private final JButton btnPrep = new JButton();

    private final Timer clockTimer;

    /**
     * Constructor de compatibilidad.
     * Si lo llamas así, NO se pinta botón de preparación.
     */
    public TpvTopBar(String screenTitle) {
        this(screenTitle, null);
    }

    /**
     * Constructor recomendado.
     *
     * @param screenTitle   título de la pantalla
     * @param onPrepClicked callback para abrir el monitor de preparación.
     *                      Si es null, no se muestra el botón.
     */
    public TpvTopBar(String screenTitle, Runnable onPrepClicked) {

        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        setBackground(new Color(15, 60, 45));

        // ----------------------------
        // TÍTULO (IZQUIERDA)
        // ----------------------------
        lblTitle.setText(screenTitle != null ? screenTitle : "");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setIcon(TpvIconFactory.cashRegister(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);

        // ----------------------------
        // USUARIO / ROL / CAJA / RELOJ
        // ----------------------------
        configureInfoLabel(lblUser, TpvIconFactory.user(15, InformeUiTheme.ACCENT_GOLD));
        configureInfoLabel(lblRole, TpvIconFactory.shield(15, InformeUiTheme.ACCENT_GOLD));
        configureInfoLabel(lblCashBox, TpvIconFactory.cashRegister(15, InformeUiTheme.ACCENT_GOLD));
        configureInfoLabel(lblClock, TpvIconFactory.clock(15, InformeUiTheme.ACCENT_GOLD));
        lblClock.setFont(new Font("Consolas", Font.BOLD, 13));

        JPanel sessionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        sessionPanel.setOpaque(false);
        sessionPanel.add(lblCashBox);
        sessionPanel.add(lblUser);
        sessionPanel.add(lblRole);

        JPanel rightInfo = new JPanel(new GridLayout(2, 1, 0, 3));
        rightInfo.setOpaque(false);
        rightInfo.add(sessionPanel);

        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        clockPanel.setOpaque(false);
        clockPanel.add(lblClock);
        rightInfo.add(clockPanel);

        // ----------------------------
        // BOTÓN PREP
        // ----------------------------
        configurePrepButton(onPrepClicked);

        JPanel right = new JPanel(new BorderLayout(12, 0));
        right.setOpaque(false);
        right.add(rightInfo, BorderLayout.CENTER);

        if (onPrepClicked != null) {
            right.add(btnPrep, BorderLayout.EAST);
        }

        add(lblTitle, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        // ----------------------------
        // RELOJ
        // ----------------------------
        clockTimer = new Timer(1000, e -> refreshClock());
        clockTimer.setInitialDelay(0);
        clockTimer.start();

        refreshUser();
        refreshClock();
    }

    private void configureInfoLabel(JLabel label, Icon icon) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setIcon(icon);
        label.setIconTextGap(6);
    }

    /**
     * Configura el botón del monitor de preparación.
     */
    private void configurePrepButton(Runnable onPrepClicked) {
        btnPrep.setText(I18n.t("topbar.preparation"));
        btnPrep.setToolTipText(I18n.t("topbar.preparation.tooltip"));
        btnPrep.setIcon(TpvIconFactory.product(17, Color.BLACK));
        btnPrep.setIconTextGap(7);
        btnPrep.setFocusable(false);
        btnPrep.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPrep.setMargin(new Insets(7, 12, 7, 12));
        btnPrep.setBackground(InformeUiTheme.ACCENT_GOLD);
        btnPrep.setForeground(Color.BLACK);
        btnPrep.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (onPrepClicked != null) {
            btnPrep.addActionListener(e -> onPrepClicked.run());
        }
    }

    /**
     * Actualiza el texto del usuario/rol/caja leyendo AppContext.
     * Si no hay sesión, muestra "Sin sesión".
     */
    public final void refreshUser() {
        if (!AppContext.isAuthenticated()) {
            lblUser.setText(I18n.t("topbar.noSession"));
            lblRole.setText(I18n.t("topbar.noRole"));
            lblCashBox.setText(resolveCashBoxText());
            return;
        }

        Usuario u = AppContext.getUsuario();
        String userName = u.getNombre() != null && !u.getNombre().isBlank()
                ? u.getNombre().trim()
                : "-";

        String rol = (u.getRol() != null && u.getRol().getNombre() != null && !u.getRol().getNombre().isBlank())
                ? u.getRol().getNombre().trim()
                : I18n.t("topbar.noRole");

        lblUser.setText(I18n.t("topbar.user") + ": " + userName);
        lblRole.setText(I18n.t("topbar.role") + ": " + rol);
        lblCashBox.setText(resolveCashBoxText());
    }

    private String resolveCashBoxText() {
        String cashBoxName = null;

        try {
            if (AppContext.hasSesionCajaActual()) {
                cashBoxName = AppContext.getSesionCajaActual().getNombreCaja();
            }
        } catch (Exception ignored) {
        }

        if (cashBoxName == null || cashBoxName.isBlank()) {
            try {
                if (AppContext.hasTerminalContext()) {
                    cashBoxName = AppContext.getNombreCajaTerminal();
                }
            } catch (Exception ignored) {
            }
        }

        if (cashBoxName == null || cashBoxName.isBlank()) {
            cashBoxName = I18n.t("topbar.noCashBox");
        }

        return I18n.t("topbar.cashBox") + ": " + cashBoxName.trim();
    }

    /**
     * Actualiza el reloj.
     */
    private void refreshClock() {
        lblClock.setText(LocalDateTime.now().format(CLOCK_FMT));
    }

    /**
     * Parar timer si se destruye el frame.
     */
    public void stopClock() {
        clockTimer.stop();
    }
}

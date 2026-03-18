package ui.common;

import app.AppContext;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Barra superior TPV: título + usuario/rol + reloj + acceso rápido al monitor de preparación.
 * Se reutiliza en TODAS las pantallas para que el reloj sea siempre visible.
 */
public class TpvTopBar extends JPanel {

    private static final DateTimeFormatter CLOCK_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblUser = new JLabel();
    private final JLabel lblClock = new JLabel();
    private final JButton btnPrep = new JButton("Prep");

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

        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(10, 12, 10, 12));
        setBackground(new Color(15, 60, 45)); // verde oscuro

        // ----------------------------
        // TÍTULO (IZQUIERDA)
        // ----------------------------
        lblTitle.setText(screenTitle);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        // ----------------------------
        // USUARIO / ROL
        // ----------------------------
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));

        // ----------------------------
        // RELOJ
        // ----------------------------
        lblClock.setForeground(Color.WHITE);
        lblClock.setFont(new Font("Consolas", Font.BOLD, 14));

        // ----------------------------
        // PANEL DE INFO DERECHA
        // ----------------------------
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(lblUser);
        infoPanel.add(lblClock);

        // ----------------------------
        // BOTÓN PREP
        // ----------------------------
        configurePrepButton(onPrepClicked);

        // Panel derecho principal
        JPanel right = new JPanel(new BorderLayout(10, 0));
        right.setOpaque(false);
        right.add(infoPanel, BorderLayout.CENTER);

        // Solo se añade si hay callback
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

    /**
     * Configura el botón del monitor de preparación.
     */
    private void configurePrepButton(Runnable onPrepClicked) {
        btnPrep.setFocusable(false);
        btnPrep.setFont(new Font("Arial", Font.BOLD, 12));
        btnPrep.setMargin(new Insets(6, 12, 6, 12));

        // Colores discretos pero visibles
        btnPrep.setBackground(new Color(212, 175, 55));
        btnPrep.setForeground(Color.BLACK);

        if (onPrepClicked != null) {
            btnPrep.addActionListener(e -> onPrepClicked.run());
        }
    }

    /**
     * Actualiza el texto del usuario/rol leyendo AppContext.
     * Si no hay sesión, muestra "Sin sesión".
     */
    public final void refreshUser() {
        if (!AppContext.isAuthenticated()) {
            lblUser.setText("Sin sesión");
            return;
        }

        Usuario u = AppContext.getUsuario();
        String rol = (u.getRol() != null && u.getRol().getNombre() != null)
                ? u.getRol().getNombre()
                : "SIN_ROL";

        lblUser.setText("Usuario: " + u.getNombre() + "  |  Rol: " + rol);
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

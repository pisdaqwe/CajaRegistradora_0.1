package ui.common;

import app.AppContext;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Barra superior TPV: título + usuario/rol + reloj.
 * Se reutiliza en TODAS las pantallas para que el reloj sea siempre visible.
 */
public class TpvTopBar extends JPanel {

    private static final DateTimeFormatter CLOCK_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JLabel lblTitle = new JLabel();
    private final JLabel lblUser = new JLabel();
    private final JLabel lblClock = new JLabel();

    private final Timer clockTimer;

    public TpvTopBar(String screenTitle) {

        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(10, 12, 10, 12));
        setBackground(new Color(15, 60, 45)); // verde oscuro tipo Micros

        // Título (izquierda)
        lblTitle.setText(screenTitle);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        // Usuario/Rol (centro-derecha)
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));

        // Reloj (derecha)
        lblClock.setForeground(Color.WHITE);
        lblClock.setFont(new Font("Consolas", Font.BOLD, 14));

        // Panel derecha: usuario + reloj
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 2));
        right.setOpaque(false);
        right.add(lblUser);
        right.add(lblClock);

        add(lblTitle, BorderLayout.WEST);
        add(right, BorderLayout.EAST);

        // Iniciar reloj
        clockTimer = new Timer(1000, e -> refreshClock());
        clockTimer.setInitialDelay(0);
        clockTimer.start();

        refreshUser();
        refreshClock();
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

    /** Actualiza el reloj. */
    private void refreshClock() {
        lblClock.setText(LocalDateTime.now().format(CLOCK_FMT));
    }

    /** Parar timer si se destruye el frame (buena práctica). */
    public void stopClock() {
        clockTimer.stop();
    }
}

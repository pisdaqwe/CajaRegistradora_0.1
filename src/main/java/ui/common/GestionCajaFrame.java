package ui.common;

import ui.common.BaseTpvFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Frame de Gestión de Caja (MVP).
 *
 * - No contiene lógica de negocio
 * - Solo UI y estructura
 * - Punto central para abrir / cerrar sesiones de caja
 */
public class GestionCajaFrame extends BaseTpvFrame {

    private final Runnable onBack;

    public GestionCajaFrame(Runnable onLogoutNavigate, Runnable onBack) {
        super("Gestión de Caja", onLogoutNavigate);
        this.onBack = onBack;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(18, 18, 18));

        // =========================
        // TÍTULO
        // =========================
        JLabel title = new JLabel("GESTIÓN DE CAJA");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        root.add(title, BorderLayout.NORTH);

        // =========================
        // PANEL CENTRAL
        // =========================
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setOpaque(false);

        center.add(buildLeftPanel());
        center.add(buildRightPanel());

        root.add(center, BorderLayout.CENTER);

        // =========================
        // FOOTER
        // =========================
        JButton btnVolver = createSecondaryButton("Volver");
        btnVolver.addActionListener(e -> {
            dispose();
            onBack.run();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.add(btnVolver);

        root.add(footer, BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    // =====================================================
    // PANEL IZQUIERDO (ACCIONES)
    // =====================================================

    private JPanel buildLeftPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Estado actual"
        ));
        panel.setOpaque(false);

        JLabel estado = new JLabel("No hay ninguna sesión de caja abierta");
        estado.setForeground(Color.LIGHT_GRAY);
        estado.setBorder(new EmptyBorder(8, 8, 8, 8));

        JButton btnAbrir = createPrimaryButton("Asignar / Abrir sesión de caja");
        JButton btnCerrar = createDangerButton("Cerrar sesión");
        btnCerrar.setEnabled(false); // MVP: desactivado

        panel.add(estado);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnAbrir);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnCerrar);

        return panel;
    }

    // =====================================================
    // PANEL DERECHO (INFO / VISUAL)
    // =====================================================

    private JPanel buildRightPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JPanel fichados = createPlaceholderPanel("Empleados fichados");
        JPanel sesiones = createPlaceholderPanel("Sesiones de caja");

        panel.add(fichados, BorderLayout.CENTER);
        panel.add(sesiones, BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================
    // COMPONENTES AUXILIARES
    // =====================================================

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                title
        ));
        p.setPreferredSize(new Dimension(200, 180));
        p.setOpaque(false);

        JLabel label = new JLabel("(contenido pendiente)");
        label.setForeground(Color.GRAY);
        p.add(label);

        return p;
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new Color(40, 130, 100));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new Color(160, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}

package ui.common;

import ui.common.BaseTpvFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.AppServices;

import java.awt.*;

/**
 * Placeholder: menú Gestión de Empleados (ADMIN).
 * - Reloj + usuario/rol arriba (BaseTpvFrame)
 * - Botones grandes (sin BD todavía)
 * - Volver al Dashboard
 * - Logout correcto
 */
public class EmpleadoMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    public EmpleadoMenuFrame(Runnable onLogoutNavigate, Runnable onBack,AppServices services) {
        super("Gestión de Empleados", onLogoutNavigate,services);
        this.onBack = onBack;
        this.services =  services;
        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(20, 20, 20));

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        JButton btnListado = createBigButton("Listado de Empleados");
        JButton btnAlta = createBigButton("Alta / Edición");
        JButton btnResetPin = createBigButton("Reset PIN");
        JButton btnFichajes = createBigButton("Fichajes");

        btnListado.addActionListener(e -> placeholder("Listado de Empleados"));
        btnAlta.addActionListener(e -> placeholder("Alta / Edición"));
        btnResetPin.addActionListener(e -> placeholder("Reset PIN"));
        btnFichajes.addActionListener(e -> placeholder("Fichajes"));

        grid.add(btnListado);
        grid.add(btnAlta);
        grid.add(btnResetPin);
        grid.add(btnFichajes);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);

        JButton btnVolver = createSecondaryButton("Volver");
        JButton btnLogout = createDangerButton("Cerrar sesión");

        btnVolver.addActionListener(e -> volver());
        btnLogout.addActionListener(e -> doLogout());

        bottom.add(btnVolver);
        bottom.add(btnLogout);

        root.add(grid, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private void placeholder(String nombre) {
        JOptionPane.showMessageDialog(this, nombre + " (pendiente)");
    }

    private void volver() {
        safeDispose();
        if (onBack != null) onBack.run();
    }

    // ===== Estilo botones (mismo look) =====

    private JButton createBigButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.setBackground(new Color(30, 120, 90));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(70, 70, 70));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return b;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBackground(new Color(170, 50, 50));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return b;
    }
}

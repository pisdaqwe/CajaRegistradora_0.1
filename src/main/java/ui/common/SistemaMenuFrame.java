package ui.common;

import ui.common.BaseTpvFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.AppServices;

import java.awt.*;

/**
 * Placeholder: menú Sistema/Herramientas (ADMIN/TECNICO).
 * - Reloj + usuario/rol arriba (BaseTpvFrame)
 * - Botones grandes (sin BD todavía)
 * - Volver al Dashboard
 * - Logout correcto
 */
public class SistemaMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices appServices;
    public SistemaMenuFrame(Runnable onLogoutNavigate, Runnable onBack,AppServices services) {
        super("Sistema / Herramientas", onLogoutNavigate,services);
        this.onBack = onBack;
        this.appServices = services;
        	
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

        JButton btnConfig = createBigButton("Configuración");
        JButton btnAuditoria = createBigButton("Auditoría");
        JButton btnEstado = createBigButton("Estado del Sistema");
        JButton btnImpresion = createBigButton("Cola de Impresión");

        btnConfig.addActionListener(e -> placeholder("Configuración"));
        btnAuditoria.addActionListener(e -> placeholder("Auditoría"));
        btnEstado.addActionListener(e -> placeholder("Estado del Sistema"));
        btnImpresion.addActionListener(e -> placeholder("Cola de Impresión"));

        grid.add(btnConfig);
        grid.add(btnAuditoria);
        grid.add(btnEstado);
        grid.add(btnImpresion);

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

    // ===== Estilo botones =====

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

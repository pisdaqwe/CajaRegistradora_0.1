package ui.common;

import ui.common.BaseTpvFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.AppServices;

import java.awt.*;

public class AuditoriaMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    public AuditoriaMenuFrame(Runnable onLogoutNavigate, Runnable onBack,AppServices services) {
        super("Auditoría", onLogoutNavigate,services);
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

        JButton btnAcciones = createBigButton("Últimas Acciones");
        JButton btnFiltrar = createBigButton("Filtrar por Usuario/Fecha");
        JButton btnExportar = createBigButton("Exportar Informe");
        JButton btnEventos = createBigButton("Eventos Sensibles");

        btnAcciones.addActionListener(e -> placeholder("Últimas Acciones"));
        btnFiltrar.addActionListener(e -> placeholder("Filtrar por Usuario/Fecha"));
        btnExportar.addActionListener(e -> placeholder("Exportar Informe"));
        btnEventos.addActionListener(e -> placeholder("Eventos Sensibles"));

        grid.add(btnAcciones);
        grid.add(btnFiltrar);
        grid.add(btnExportar);
        grid.add(btnEventos);

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

    // estilos
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


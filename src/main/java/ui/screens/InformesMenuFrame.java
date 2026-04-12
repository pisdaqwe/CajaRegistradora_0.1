package ui.screens;

import ui.common.BaseTpvFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import service.AppServices;

import java.awt.*;
import enums.TipoInforme;
import ui.screens.InformesFrame;

/**
 * Placeholder de menú de Informes.
 * - Reloj + usuario/rol arriba (BaseTpvFrame)
 * - Botones grandes (sin BD todavía)
 * - Botón Volver
 */
public class InformesMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final Runnable onLogoutNavigate;
    private final AppServices appServices;

    public InformesMenuFrame(Runnable onLogoutNavigate, Runnable onBack,AppServices services) {
        super("Informes", onLogoutNavigate,services);
        this.onBack = onBack;
        this.appServices = services;
        this.onLogoutNavigate = onLogoutNavigate;
        

        // Guard: si no hay sesión, sale
        requireAuthenticatedOrExit();

        buildUI();
        refreshHeader();
    }

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(20, 20, 20));

        // Grid de botones
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        JButton btnInformeCaja = createBigButton("Informe Caja");
        JButton btnInformeVentas = createBigButton("Informe Ventas");
        JButton btnPagos = createBigButton("Pagos");
        JButton btnResumenArticulos = createBigButton("Resumen Artículos");

        btnInformeCaja.addActionListener(e -> abrirExplorador(TipoInforme.INFORME_CAJA));
        btnInformeVentas.addActionListener(e -> abrirExplorador(TipoInforme.VENTAS_POR_DIA));
        btnPagos.addActionListener(e -> abrirExplorador(TipoInforme.PAGOS_POR_METODO));
        btnResumenArticulos.addActionListener(e -> abrirExplorador(TipoInforme.PRODUCTOS_MAS_VENDIDOS));

        grid.add(btnInformeCaja);
        grid.add(btnInformeVentas);
        grid.add(btnPagos);
        grid.add(btnResumenArticulos);

        // Barra inferior: volver + logout (opcional)
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
        // Cerrar este frame y volver al dashboard (sin limpiar sesión)
        safeDispose();
        if (onBack != null) onBack.run();
    }

    // ==========================
    // Helpers de estilo botones
    // ==========================

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
    
    private void abrirExplorador(TipoInforme tipoInforme) {
        this.setVisible(false);

        InformesFrame frame = new InformesFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                appServices,
                tipoInforme
        );
        frame.setVisible(true);
    }
}


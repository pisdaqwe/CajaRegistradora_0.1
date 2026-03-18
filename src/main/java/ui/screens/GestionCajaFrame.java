package ui.screens;

import ui.common.BaseTpvFrame;
import ui.dialog.AbrirSesionCajaDialog;
import ui.dialog.CerrarSesionCajaDialog;
import ui.table.EmpleadosFichadosTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import service.AppServices;
import dtoS.CajaEstadoDTO;

import java.awt.*;

public class GestionCajaFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onBack;
    private final AppServices services;

    // Empleados fichados (informativo)
    private JTable tablaEmpleados;
    private EmpleadosFichadosTableModel empleadosTableModel;

    // Cajas disponibles (informativo)
    private JPanel panelCajas;

    public GestionCajaFrame(
            Runnable onLogoutNavigate,
            Runnable onBack,
            AppServices services
    ) {
        super("Gestión de Caja", onLogoutNavigate,services);
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
        refreshEmpleadosFichados();
        refreshCajasDisponibles();
    }

    // =====================================================
    // UI GENERAL
    // =====================================================

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(18, 18, 18));

        JLabel title = new JLabel("GESTIÓN DE CAJA");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setOpaque(false);

        center.add(buildLeftPanel());
        center.add(buildRightPanel());

        root.add(center, BorderLayout.CENTER);

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

        JLabel estado = new JLabel("Pantalla informativa");
        estado.setForeground(Color.LIGHT_GRAY);
        estado.setBorder(new EmptyBorder(8, 8, 8, 8));

        JButton btnAbrir = createPrimaryButton("Asignar / Abrir sesión de caja");
        btnAbrir.addActionListener(e->abrirDialogoAbrirCaja());
        JButton btnCerrar = createDangerButton("Cerrar sesión");
        btnCerrar.setEnabled(true); 
        btnCerrar.addActionListener(e->abrirDialogoCerrarCaja());

        panel.add(estado);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnAbrir);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnCerrar);

        return panel;
    }

    // =====================================================
    // PANEL DERECHO (INFO)
    // =====================================================

    private JPanel buildRightPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(buildEmpleadosFichadosPanel());
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildEstadoCajaPanel());

        return panel;
    }

    // =====================================================
    // EMPLEADOS FICHADOS (INFORMATIVO)
    // =====================================================

    private JPanel buildEmpleadosFichadosPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Empleados fichados"
        ));
        panel.setOpaque(false);

        empleadosTableModel = new EmpleadosFichadosTableModel();

        tablaEmpleados = new JTable(empleadosTableModel);
        tablaEmpleados.setFillsViewportHeight(true);
        tablaEmpleados.setRowHeight(24);
        tablaEmpleados.setEnabled(false);
        tablaEmpleados.setBackground(new Color(30, 30, 30));
        tablaEmpleados.setForeground(Color.WHITE);
        tablaEmpleados.setGridColor(Color.DARK_GRAY);

        JTableHeader header = tablaEmpleados.getTableHeader();
        header.setBackground(new Color(50, 50, 50));
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tablaEmpleados);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scroll, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(400, 220));

        return panel;
    }

    private void refreshEmpleadosFichados() {
        try {
            empleadosTableModel.setDatos(
                    services.fichajeService.findFichajesActivos()
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error cargando empleados fichados",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // CAJAS DISPONIBLES (INFORMATIVO)
    // =====================================================

    private JPanel buildEstadoCajaPanel() {

        panelCajas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCajas.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Cajas disponibles"
        ));
        panelCajas.setOpaque(false);
        panelCajas.setPreferredSize(new Dimension(400, 160));

        return panelCajas;
    }

    private void refreshCajasDisponibles() {

        panelCajas.removeAll();

        try {
            for (CajaEstadoDTO caja : services.sesionCajaService.getEstadoCajas()) {
                panelCajas.add(crearTarjetaCaja(caja));
            }
        } catch (Exception ex) {
            JLabel error = new JLabel("Error cargando estado de cajas");
            error.setForeground(Color.RED);
            panelCajas.add(error);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JPanel crearTarjetaCaja(CajaEstadoDTO caja) {

        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setPreferredSize(new Dimension(180, 100));

        Color bg = caja.isOcupada()
                ? new Color(120, 40, 40)
                : new Color(40, 120, 40);

        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel lblNombre = new JLabel(caja.getNombreCaja(), SwingConstants.CENTER);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblEstado = new JLabel(
                caja.isOcupada() ? "Ocupada" : "Libre",
                SwingConstants.CENTER
        );
        lblEstado.setForeground(Color.WHITE);

        card.add(lblNombre, BorderLayout.NORTH);
        card.add(lblEstado, BorderLayout.CENTER);

        if (caja.isOcupada() && caja.getEmpleadoAsignado() != null) {
            JLabel lblEmp = new JLabel(
                    caja.getEmpleadoAsignado(),
                    SwingConstants.CENTER
            );
            lblEmp.setForeground(Color.LIGHT_GRAY);
            card.add(lblEmp, BorderLayout.SOUTH);
        }

        return card;
    }

    // =====================================================
    // BOTONES
    // =====================================================

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
    // =====================================================
    // ABRIR DIALOGO DE APERTURA DE CAJA
    // =====================================================
    private void abrirDialogoAbrirCaja() {

        AbrirSesionCajaDialog dialog =
                new AbrirSesionCajaDialog(this, services);

        dialog.setVisible(true);

        // 👇 cuando se cierra el diálogo, refrescamos TODO
        refreshEmpleadosFichados();
        refreshCajasDisponibles();
    }
    private void abrirDialogoCerrarCaja() {
    	CerrarSesionCajaDialog dialog = new CerrarSesionCajaDialog(this, services);
    	dialog.setVisible(true);
    	refreshCajasDisponibles();
    	refreshEmpleadosFichados();
    	
    }
    
}


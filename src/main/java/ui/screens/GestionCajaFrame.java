package ui.screens;

import dtoS.CajaEstadoDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.InformeUiTheme;
import ui.common.TpvDialogUtils;
import ui.dialog.AbrirSesionCajaDialog;
import ui.dialog.CerrarSesionCajaDialog;
import ui.table.EmpleadosFichadosTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GestionCajaFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onBack;
    private final AppServices services;
    private final Runnable refreshNuevoPedidoVisibility;

    private JTable tablaEmpleados;
    private EmpleadosFichadosTableModel empleadosTableModel;

    private JPanel panelCajas;

    public GestionCajaFrame(
            Runnable onLogoutNavigate,
            Runnable onBack,
            AppServices services,
            Runnable refreshNuevoPedidoVisibility
    ) {
        super("Gestión de Caja", onLogoutNavigate, services);
        this.onBack = onBack;
        this.services = services;
        this.refreshNuevoPedidoVisibility = refreshNuevoPedidoVisibility;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
        refreshEmpleadosFichados();
        refreshCajasDisponibles();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Gestión de Caja");
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel("Asignación, apertura y cierre de sesiones de caja");
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(lblTitulo);
        textPanel.add(lblSubtitulo);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private JComponent buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setOpaque(false);

        center.add(buildLeftPanel());
        center.add(buildRightPanel());

        return center;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(10, 10));

        JLabel lblSeccion = InformeUiTheme.createSectionTitle("Operativa de caja");
        panel.add(lblSeccion, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JTextArea txtInfo = new JTextArea(
                "Desde aquí puedes asignar una caja a un empleado fichado, " +
                "abrir una nueva sesión o cerrar una sesión abierta con validación y conteo."
        );
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setOpaque(false);
        txtInfo.setForeground(InformeUiTheme.TEXT_SECONDARY);
        txtInfo.setFont(InformeUiTheme.FONT_BODY);
        txtInfo.setBorder(null);

        JButton btnAbrir = new JButton("Asignar / Abrir sesión de caja");
        InformeUiTheme.stylePrimaryButton(btnAbrir);
        btnAbrir.addActionListener(e -> abrirDialogoAbrirCaja());

        JButton btnCerrar = new JButton("Cerrar sesión");
        InformeUiTheme.styleDangerButton(btnCerrar);
        btnCerrar.addActionListener(e -> abrirDialogoCerrarCaja());

        content.add(txtInfo);
        content.add(Box.createVerticalStrut(24));
        content.add(btnAbrir);
        content.add(Box.createVerticalStrut(10));
        content.add(btnCerrar);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildEmpleadosFichadosPanel());
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildEstadoCajaPanel());

        return panel;
    }

    private JPanel buildEmpleadosFichadosPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(400, 240));

        JLabel title = InformeUiTheme.createSectionTitle("Empleados fichados");
        panel.add(title, BorderLayout.NORTH);

        empleadosTableModel = new EmpleadosFichadosTableModel();

        tablaEmpleados = new JTable(empleadosTableModel);
        tablaEmpleados.setEnabled(false);
        InformeUiTheme.styleTable(tablaEmpleados);

        JScrollPane scroll = new JScrollPane(tablaEmpleados);
        InformeUiTheme.styleScrollPane(scroll);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void refreshEmpleadosFichados() {
        try {
            empleadosTableModel.setDatos(services.fichajeService.findFichajesActivos());
        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    "Error cargando empleados fichados",
                    ex.getMessage()
            );
        }
    }

    private JPanel buildEstadoCajaPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        wrapper.setPreferredSize(new Dimension(400, 220));

        JLabel title = InformeUiTheme.createSectionTitle("Estado de cajas");
        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        panelCajas.setOpaque(false);

        wrapper.add(panelCajas, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshCajasDisponibles() {
        panelCajas.removeAll();

        try {
            for (CajaEstadoDTO caja : services.sesionCajaService.getEstadoCajas()) {
                panelCajas.add(crearTarjetaCaja(caja));
            }
        } catch (Exception ex) {
            JLabel error = new JLabel("Error cargando estado de cajas");
            error.setForeground(InformeUiTheme.DANGER);
            panelCajas.add(error);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JPanel crearTarjetaCaja(CajaEstadoDTO caja) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setPreferredSize(new Dimension(180, 100));
        card.setBorder(InformeUiTheme.createInnerCardBorder());

        Color bg;
        String estado;
        String detalle = "";

        if (!caja.isOperativa()) {
            bg = new Color(70, 70, 70);
            estado = "Fuera de servicio";
        } else if (caja.isOcupada()) {
            bg = new Color(120, 45, 45);
            estado = "Ocupada";
            detalle = caja.getEmpleadoAsignado() != null ? caja.getEmpleadoAsignado() : "";
        } else {
            bg = InformeUiTheme.STARBUCKS_GREEN_SOFT;
            estado = "Disponible";
        }

        card.setBackground(bg);

        JLabel lblNombre = new JLabel(caja.getNombreCaja(), SwingConstants.CENTER);
        lblNombre.setFont(InformeUiTheme.FONT_LABEL);
        lblNombre.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblEstado = new JLabel(estado, SwingConstants.CENTER);
        lblEstado.setFont(InformeUiTheme.FONT_BODY);
        lblEstado.setForeground(InformeUiTheme.TEXT_PRIMARY);

        card.add(lblNombre, BorderLayout.NORTH);
        card.add(lblEstado, BorderLayout.CENTER);

        if (!detalle.isBlank()) {
            JLabel lblDetalle = new JLabel(detalle, SwingConstants.CENTER);
            lblDetalle.setFont(InformeUiTheme.FONT_SUBTITLE);
            lblDetalle.setForeground(InformeUiTheme.TEXT_SECONDARY);
            card.add(lblDetalle, BorderLayout.SOUTH);
        }

        return card;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);

        JButton btnVolver = new JButton("Volver");
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.addActionListener(e -> volver());

        JButton btnLogout = new JButton("Cerrar sesión");
        InformeUiTheme.styleDangerButton(btnLogout);
        btnLogout.addActionListener(e -> doLogout());

        bottom.add(btnVolver);
        bottom.add(btnLogout);
        return bottom;
    }

    private void abrirDialogoAbrirCaja() {
        AbrirSesionCajaDialog dialog = new AbrirSesionCajaDialog(this, services);
        dialog.setVisible(true);

        refreshEmpleadosFichados();
        refreshCajasDisponibles();
    }

    private void abrirDialogoCerrarCaja() {
        CerrarSesionCajaDialog dialog = new CerrarSesionCajaDialog(this, services);
        dialog.setVisible(true);

        refreshEmpleadosFichados();
        refreshCajasDisponibles();
    }

    private void volver() {
        safeDispose();
        if (refreshNuevoPedidoVisibility != null) {
            refreshNuevoPedidoVisibility.run();
        }
        if (onBack != null) {
            onBack.run();
        }
    }
}
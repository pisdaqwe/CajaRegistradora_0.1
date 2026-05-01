package ui.screens;

import dtoS.CajaEstadoDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.InformeUiTheme;
import ui.common.TpvDialogUtils;
import ui.dialog.AbrirSesionCajaDialog;
import ui.dialog.CerrarSesionCajaDialog;
import ui.table.EmpleadosFichadosTableModel;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class GestionCajaFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private static final Color OK_GREEN = new Color(46, 125, 50);
    private static final Color OCCUPIED_RED = new Color(120, 45, 45);
    private static final Color OUT_OF_SERVICE_GRAY = new Color(70, 70, 70);

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
        super(I18n.t("cashManagement.title"), onLogoutNavigate, services);

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
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(22, 24, 24, 24));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(18, 0));

        JLabel icon = new JLabel(TpvIconFactory.cashRegister(46, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(I18n.t("cashManagement.header.title"));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(I18n.t("cashManagement.header.subtitle"));
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        textPanel.add(lblTitulo);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(lblSubtitulo);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildCenterPanel() {
        JPanel center = transparentPanel(new GridLayout(1, 2, 18, 0));

        center.add(buildLeftPanel());
        center.add(buildRightPanel());

        return center;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(12, 14));

        JLabel lblSeccion = InformeUiTheme.createSectionTitle(I18n.t("cashManagement.operations.title"));
        lblSeccion.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        lblSeccion.setIconTextGap(8);

        panel.add(lblSeccion, BorderLayout.NORTH);

        JPanel content = transparentPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JTextArea txtInfo = new JTextArea(I18n.t("cashManagement.operations.description"));
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setOpaque(false);
        txtInfo.setForeground(InformeUiTheme.TEXT_SECONDARY);
        txtInfo.setFont(InformeUiTheme.FONT_BODY);
        txtInfo.setBorder(null);
        txtInfo.setFocusable(false);
        txtInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionCard = InformeUiTheme.createCardPanel(new GridLayout(2, 1, 0, 12));
        actionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JButton btnAbrir = new JButton(I18n.t("cashManagement.openSession"));
        InformeUiTheme.stylePrimaryButton(btnAbrir);
        btnAbrir.setFont(new Font("SansSerif", Font.BOLD, 17));
        btnAbrir.setIcon(TpvIconFactory.check(20, Color.WHITE));
        btnAbrir.setIconTextGap(10);
        btnAbrir.addActionListener(e -> abrirDialogoAbrirCaja());

        JButton btnCerrar = new JButton(I18n.t("cashManagement.closeSession"));
        InformeUiTheme.styleDangerButton(btnCerrar);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 17));
        btnCerrar.setIcon(TpvIconFactory.cancel(20, Color.WHITE));
        btnCerrar.setIconTextGap(10);
        btnCerrar.addActionListener(e -> abrirDialogoCerrarCaja());

        actionCard.add(btnAbrir);
        actionCard.add(btnCerrar);

        JPanel hintCard = new JPanel(new BorderLayout(10, 0));
        hintCard.setBackground(InformeUiTheme.CARD_BG_2);
        hintCard.setBorder(InformeUiTheme.createInnerCardBorder());
        hintCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel hintIcon = new JLabel(TpvIconFactory.info(24, InformeUiTheme.ACCENT_GOLD));
        JLabel hintText = new JLabel(
                "<html><div style='width:360px;'>"
                        + I18n.t("cashManagement.operations.hint")
                        + "</div></html>"
        );
        hintText.setFont(InformeUiTheme.FONT_BODY);
        hintText.setForeground(InformeUiTheme.TEXT_SECONDARY);

        hintCard.add(hintIcon, BorderLayout.WEST);
        hintCard.add(hintText, BorderLayout.CENTER);

        content.add(txtInfo);
        content.add(Box.createVerticalStrut(22));
        content.add(actionCard);
        content.add(Box.createVerticalStrut(18));
        content.add(hintCard);
        content.add(Box.createVerticalGlue());

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = transparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel empleadosPanel = buildEmpleadosFichadosPanel();
        JPanel cajasPanel = buildEstadoCajaPanel();

        empleadosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cajasPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(empleadosPanel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(cajasPanel);

        return panel;
    }

    private JPanel buildEmpleadosFichadosPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(400, 250));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashManagement.clockedEmployees.title"));
        title.setIcon(TpvIconFactory.users(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        empleadosTableModel = new EmpleadosFichadosTableModel();

        tablaEmpleados = new JTable(empleadosTableModel);
        tablaEmpleados.setEnabled(false);
        InformeUiTheme.styleTable(tablaEmpleados);

        configurarTablaEmpleados();

        JScrollPane scroll = new JScrollPane(tablaEmpleados);
        InformeUiTheme.styleScrollPane(scroll);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void configurarTablaEmpleados() {
        if (tablaEmpleados.getColumnModel().getColumnCount() < 4) {
            return;
        }

        tablaEmpleados.getColumnModel().getColumn(0).setHeaderValue(I18n.t("cashManagement.clockedEmployees.table.id"));
        tablaEmpleados.getColumnModel().getColumn(1).setHeaderValue(I18n.t("cashManagement.clockedEmployees.table.employee"));
        tablaEmpleados.getColumnModel().getColumn(2).setHeaderValue(I18n.t("cashManagement.clockedEmployees.table.entryTime"));
        tablaEmpleados.getColumnModel().getColumn(3).setHeaderValue(I18n.t("cashManagement.clockedEmployees.table.status"));

        tablaEmpleados.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaEmpleados.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaEmpleados.getColumnModel().getColumn(2).setPreferredWidth(130);
        tablaEmpleados.getColumnModel().getColumn(3).setPreferredWidth(100);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tablaEmpleados.getColumnModel().getColumn(0).setCellRenderer(center);

        tablaEmpleados.getTableHeader().repaint();
    }

    private void refreshEmpleadosFichados() {
        try {
            empleadosTableModel.setDatos(services.fichajeService.findFichajesActivos());
            configurarTablaEmpleados();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashManagement.clockedEmployees.loadErrorTitle"),
                    ex.getMessage()
            );
        }
    }

    private JPanel buildEstadoCajaPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));
        wrapper.setPreferredSize(new Dimension(400, 300));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashManagement.cashBoxes.title"));
        title.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelCajas.setOpaque(false);

        JScrollPane scroll = new JScrollPane(panelCajas);
        InformeUiTheme.styleScrollPane(scroll);

        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setBlockIncrement(120);

        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private void refreshCajasDisponibles() {
        panelCajas.removeAll();

        try {
            for (CajaEstadoDTO caja : services.sesionCajaService.getEstadoCajas()) {
                panelCajas.add(crearTarjetaCaja(caja));
            }

        } catch (Exception ex) {
            JLabel error = new JLabel(I18n.t("cashManagement.cashBoxes.loadError"));
            error.setForeground(InformeUiTheme.DANGER);
            error.setIcon(TpvIconFactory.warning(18, InformeUiTheme.DANGER));
            error.setIconTextGap(8);
            panelCajas.add(error);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JPanel crearTarjetaCaja(CajaEstadoDTO caja) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setPreferredSize(new Dimension(190, 112));
        card.setBorder(InformeUiTheme.createInnerCardBorder());

        CajaVisualState state = resolveCajaState(caja);

        card.setBackground(state.background);

        JLabel lblNombre = new JLabel(caja.getNombreCaja(), SwingConstants.CENTER);
        lblNombre.setFont(InformeUiTheme.FONT_LABEL);
        lblNombre.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblNombre.setIcon(TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_PRIMARY));
        lblNombre.setIconTextGap(6);

        JLabel lblEstado = new JLabel(state.label, SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEstado.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblEstado.setIcon(state.icon);
        lblEstado.setIconTextGap(6);

        card.add(lblNombre, BorderLayout.NORTH);
        card.add(lblEstado, BorderLayout.CENTER);

        if (state.detail != null && !state.detail.isBlank()) {
            JLabel lblDetalle = new JLabel(state.detail, SwingConstants.CENTER);
            lblDetalle.setFont(InformeUiTheme.FONT_SUBTITLE);
            lblDetalle.setForeground(InformeUiTheme.TEXT_SECONDARY);
            lblDetalle.setIcon(TpvIconFactory.user(14, InformeUiTheme.TEXT_SECONDARY));
            lblDetalle.setIconTextGap(5);
            card.add(lblDetalle, BorderLayout.SOUTH);
        }

        return card;
    }

    private CajaVisualState resolveCajaState(CajaEstadoDTO caja) {
        if (!caja.isOperativa()) {
            return new CajaVisualState(
                    OUT_OF_SERVICE_GRAY,
                    I18n.t("cashManagement.cashBoxes.status.outOfService"),
                    "",
                    TpvIconFactory.cancel(16, Color.WHITE)
            );
        }

        if (caja.isOcupada()) {
            return new CajaVisualState(
                    OCCUPIED_RED,
                    I18n.t("cashManagement.cashBoxes.status.occupied"),
                    caja.getEmpleadoAsignado() != null ? caja.getEmpleadoAsignado() : "",
                    TpvIconFactory.warning(16, Color.WHITE)
            );
        }

        return new CajaVisualState(
                InformeUiTheme.STARBUCKS_GREEN_SOFT,
                I18n.t("cashManagement.cashBoxes.status.available"),
                "",
                TpvIconFactory.check(16, Color.WHITE)
        );
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnRefrescar = new JButton(I18n.t("common.refresh"));
        InformeUiTheme.styleSecondaryButton(btnRefrescar);
        btnRefrescar.setIcon(TpvIconFactory.refresh(18, InformeUiTheme.TEXT_PRIMARY));
        btnRefrescar.setIconTextGap(8);
        btnRefrescar.addActionListener(e -> refrescarPantalla());

        JButton btnVolver = new JButton(I18n.t("common.back"));
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnVolver.setIconTextGap(8);
        btnVolver.addActionListener(e -> volver());

        JButton btnLogout = new JButton(I18n.t("common.logout"));
        InformeUiTheme.styleDangerButton(btnLogout);
        btnLogout.setIcon(TpvIconFactory.logout(18, Color.WHITE));
        btnLogout.setIconTextGap(8);
        btnLogout.addActionListener(e -> doLogout());

        bottom.add(btnRefrescar);
        bottom.add(btnVolver);
        bottom.add(btnLogout);

        return bottom;
    }

    private void abrirDialogoAbrirCaja() {
        AbrirSesionCajaDialog dialog = new AbrirSesionCajaDialog(this, services);
        dialog.setVisible(true);

        refrescarPantalla();
    }

    private void abrirDialogoCerrarCaja() {
        CerrarSesionCajaDialog dialog = new CerrarSesionCajaDialog(this, services);
        dialog.setVisible(true);

        refrescarPantalla();
    }

    private void refrescarPantalla() {
        refreshEmpleadosFichados();
        refreshCajasDisponibles();

        if (refreshNuevoPedidoVisibility != null) {
            refreshNuevoPedidoVisibility.run();
        }
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

    private JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private JPanel transparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static final class CajaVisualState {

        private final Color background;
        private final String label;
        private final String detail;
        private final Icon icon;

        private CajaVisualState(Color background, String label, String detail, Icon icon) {
            this.background = background;
            this.label = label;
            this.detail = detail;
            this.icon = icon;
        }
    }
}
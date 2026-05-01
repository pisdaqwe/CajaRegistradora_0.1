package ui.screens;

import app.AppContext;
import dtoS.SesionCajaRefDTO;
import model.Usuario;
import service.AppServices;
import ui.common.AuditoriaMenuFrame;
import ui.common.BaseTpvFrame;
import ui.screens.ConfiguracionMenuFrame;
import ui.common.EmpleadoMenuFrame;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dashboard principal de administración / encargado / técnico.
 *
 * Responsabilidades:
 * - navegación principal
 * - acceso a módulos administrativos
 * - acceso a ventas si el usuario tiene sesión de caja abierta
 */
public class AdminDashboardFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onLogoutNavigate;
    private final AppServices services;

    private JButton btnNuevoPedido;

    public AdminDashboardFrame(Runnable onLogoutNavigate, AppServices services) {
        super(I18n.t("admin.title"), onLogoutNavigate, services);

        this.onLogoutNavigate = onLogoutNavigate;
        this.services = services;

        requireAuthenticatedOrExit();

        buildUI();
        refreshHeader();
        refreshNuevoPedidoVisibility();
    }

    // =====================================================
    // UI
    // =====================================================

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(22, 24, 24, 24));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildGridPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel header = InformeUiTheme.createCardPanel(new BorderLayout(16, 4));

        JPanel textPanel = InformeUiTheme.createTransparentPanel(new GridLayout(2, 1, 0, 4));

        JLabel lblTitle = new JLabel(I18n.t("admin.dashboard.title"));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("admin.dashboard.subtitle"));
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        JLabel lblIcon = new JLabel(TpvIconFactory.settings(42, InformeUiTheme.ACCENT_GOLD));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(lblIcon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildGridPanel() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 22, 22));
        grid.setOpaque(false);

        JButton btnInformes = createBigButton(
                I18n.t("admin.module.reports"),
                TpvIconFactory.report(34, Color.WHITE)
        );

        JButton btnEmpleados = createBigButton(
                I18n.t("admin.module.employees"),
                TpvIconFactory.user(34, Color.WHITE)
        );

        JButton btnConfig = createBigButton(
                I18n.t("admin.module.settings"),
                TpvIconFactory.settings(34, Color.WHITE)
        );

        JButton btnAuditoria = createBigButton(
                I18n.t("admin.module.audit"),
                TpvIconFactory.audit(34, Color.WHITE)
        );

        JButton btnHerramientas = createBigButton(
                I18n.t("admin.module.tools"),
                TpvIconFactory.database(34, Color.WHITE)
        );

        JButton btnGestionCaja = createBigButton(
                I18n.t("admin.module.cash"),
                TpvIconFactory.cashRegister(34, Color.WHITE)
        );

        btnInformes.addActionListener(e -> onInformes());
        btnEmpleados.addActionListener(e -> onEmpleados());
        btnConfig.addActionListener(e -> onConfig());
        btnAuditoria.addActionListener(e -> onAuditoria());
        btnHerramientas.addActionListener(e -> onHerramientas());
        btnGestionCaja.addActionListener(e -> onGestionCaja());

        aplicarPermisos(btnEmpleados, btnConfig);

        grid.add(btnInformes);
        grid.add(btnEmpleados);
        grid.add(btnConfig);
        grid.add(btnAuditoria);
        grid.add(btnHerramientas);
        grid.add(btnGestionCaja);

        return grid;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(14, 14));
        bottom.setOpaque(false);

        btnNuevoPedido = createPrimaryButton(
                I18n.t("admin.newOrder"),
                TpvIconFactory.cashRegister(22, Color.WHITE)
        );

        JButton btnLogout = createDangerButton(
                I18n.t("common.logout"),
                TpvIconFactory.logout(22, Color.WHITE)
        );

        btnNuevoPedido.addActionListener(e -> onNuevoPedido());
        btnLogout.addActionListener(e -> doLogout());

        bottom.add(btnNuevoPedido, BorderLayout.CENTER);
        bottom.add(btnLogout, BorderLayout.EAST);

        return bottom;
    }

    // =====================================================
    // LÓGICA
    // =====================================================

    private void refreshNuevoPedidoVisibility() {
        boolean tieneCajaAsignada = services.sesionCajaService
                .findSesionAbiertaByUsuarioActual()
                .isPresent();

        btnNuevoPedido.setVisible(tieneCajaAsignada);
    }

    private void aplicarPermisos(JButton btnEmpleados, JButton btnConfig) {
        Usuario usuario = AppContext.getUsuario();

        String rol = usuario.getRol() != null && usuario.getRol().getNombre() != null
                ? usuario.getRol().getNombre().toUpperCase()
                : "";

        if ("TECNICO".equals(rol)) {
            btnEmpleados.setEnabled(false);
            btnConfig.setEnabled(false);

            btnEmpleados.setToolTipText(I18n.t("admin.permission.technicalDisabled"));
            btnConfig.setToolTipText(I18n.t("admin.permission.technicalDisabled"));
        }
    }

    // =====================================================
    // ACCIONES
    // =====================================================

    private void onNuevoPedido() {
        try {
            int idUsuario = AppContext.getUsuarioId();

            SesionCajaRefDTO ref = services.sesionCajaService
                    .requireSesionAbiertaPorUsuario(idUsuario);

            AppContext.setSesionCajaActual(ref);

            VentasFrame frame = new VentasFrame(
                    onLogoutNavigate,
                    () -> this.setVisible(true),
                    services
            );

            frame.setVisible(true);
            this.setVisible(false);

        } catch (Exception ex) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("admin.openSales.warningTitle"),
                    ex.getMessage()
            );
        }

        refreshNuevoPedidoVisibility();
    }

    private void onGestionCaja() {
        GestionCajaFrame frame = new GestionCajaFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services,
                () -> refreshNuevoPedidoVisibility()
        );

        frame.setVisible(true);
        this.setVisible(false);
    }

    private void onInformes() {
        this.setVisible(false);

        InformesMenuFrame frame = new InformesMenuFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
    }

    private void onEmpleados() {
        EmpleadoMenuFrame frame = new EmpleadoMenuFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
        this.setVisible(false);
    }

    private void onConfig() {
        this.setVisible(false);

        ConfiguracionMenuFrame frame = new ConfiguracionMenuFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
    }

    private void onAuditoria() {
        this.setVisible(false);

        AuditoriaMenuFrame frame = new AuditoriaMenuFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
    }

    private void onHerramientas() {
        SistemaTecnicoFrame frame = new SistemaTecnicoFrame(
                services,
                () -> {
                    AdminDashboardFrame dashboard = new AdminDashboardFrame(onLogoutNavigate, services);
                    dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    dashboard.setVisible(true);
                }
        );

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        safeDispose();
    }

    // =====================================================
    // BOTONES
    // =====================================================

    private JButton createBigButton(String text, Icon icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 19));
        button.setFocusPainted(false);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(24, 18, 24, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setIcon(icon);
        button.setIconTextGap(14);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        return button;
    }

    private JButton createPrimaryButton(String text, Icon icon) {
        JButton button = new JButton(text);
        InformeUiTheme.stylePrimaryButton(button);

        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(100, 70));
        button.setIcon(icon);
        button.setIconTextGap(10);

        return button;
    }

    private JButton createDangerButton(String text, Icon icon) {
        JButton button = new JButton(text);
        InformeUiTheme.styleDangerButton(button);

        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(230, 70));
        button.setIcon(icon);
        button.setIconTextGap(10);

        return button;
    }
}
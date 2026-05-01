package ui.common;

import app.AppContext;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import service.AppServices;
import ui.dialog.EmpleadoFormDialog;
import ui.dialog.ResetPinEmpleadoDialog;
import ui.gestionempleado.FichajesEmpleadosFrame;
import ui.gestionempleado.GestionEmpleadosFrame;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class EmpleadoMenuFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable onLogoutNavigate;
    private final Runnable onBack;
    private final AppServices services;

    public EmpleadoMenuFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super(I18n.t("employees.menu.title"), onLogoutNavigate, services);

        this.onLogoutNavigate = onLogoutNavigate;
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(22, 22));
        root.setBorder(new EmptyBorder(24, 28, 28, 28));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCards(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = InformeUiTheme.createCardPanel(new BorderLayout(18, 0));

        JLabel icon = new JLabel(TpvIconFactory.users(46, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(I18n.t("employees.menu.header"));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(buildSubtitulo());
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        textPanel.add(lblTitulo);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(lblSubtitulo);

        header.add(icon, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildCards() {
        JPanel wrapper = transparentPanel(new GridBagLayout());

        JPanel grid = transparentPanel(new GridLayout(2, 2, 24, 24));
        grid.setPreferredSize(new Dimension(980, 520));

        JButton btnListado = createBigButton(
                I18n.t("employees.menu.management.title"),
                I18n.t("employees.menu.management.description"),
                TpvIconFactory.idCard(46, InformeUiTheme.ACCENT_GOLD)
        );

        JButton btnAlta = createBigButton(
                I18n.t("employees.menu.create.title"),
                I18n.t("employees.menu.create.description"),
                TpvIconFactory.user(46, InformeUiTheme.ACCENT_GOLD)
        );

        JButton btnReset = createBigButton(
                I18n.t("employees.menu.resetPin.title"),
                I18n.t("employees.menu.resetPin.description"),
                TpvIconFactory.key(46, InformeUiTheme.ACCENT_GOLD)
        );

        JButton btnFichajes = createBigButton(
                I18n.t("employees.menu.clockRecords.title"),
                I18n.t("employees.menu.clockRecords.description"),
                TpvIconFactory.clock(46, InformeUiTheme.ACCENT_GOLD)
        );

        btnListado.addActionListener(e -> openGestionEmpleados());
        btnAlta.addActionListener(e -> openAltaEmpleado());
        btnReset.addActionListener(e -> openResetPin());
        btnFichajes.addActionListener(e -> openFichajes());

        grid.add(btnListado);
        grid.add(btnAlta);
        grid.add(btnReset);
        grid.add(btnFichajes);

        wrapper.add(grid);
        return wrapper;
    }

    private JPanel buildBottom() {
        JPanel bottom = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

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

        bottom.add(btnVolver);
        bottom.add(btnLogout);

        return bottom;
    }

    private String buildSubtitulo() {
        if (AppContext.hasTerminalContext()) {
            return I18n.t("employees.menu.branchSubtitle", AppContext.getIdSucursal());
        }

        return I18n.t("employees.menu.noBranchSubtitle");
    }

    private JButton createBigButton(String titulo, String descripcion, Icon icon) {
        String html = "<html>"
                + "<div style='text-align:center; width:330px;'>"
                + "<div style='font-size:22px; font-weight:bold; color:#F5F2EB; margin-bottom:10px;'>"
                + titulo
                + "</div>"
                + "<div style='font-size:13px; color:#BDC8C2;'>"
                + descripcion
                + "</div>"
                + "</div>"
                + "</html>";

        JButton button = new JButton(html);
        button.setFont(InformeUiTheme.FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setForeground(InformeUiTheme.TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(26, 24, 26, 24)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setIcon(icon);
        button.setIconTextGap(18);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        return button;
    }

    private void openGestionEmpleados() {
        GestionEmpleadosFrame frame = new GestionEmpleadosFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
        this.setVisible(false);
    }

    private void openAltaEmpleado() {
        EmpleadoFormDialog dialog = new EmpleadoFormDialog(this, services, null);

        if (dialog.showDialog()) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("employees.created.title"),
                    I18n.t("employees.created.message")
            );
        }
    }

    private void openResetPin() {
        EmpleadoRowDTO empleado = seleccionarEmpleadoParaAccion();

        if (empleado == null) {
            return;
        }

        ResetPinEmpleadoDialog dialog = new ResetPinEmpleadoDialog(
                this,
                services,
                empleado.getIdUsuario()
        );

        if (dialog.showDialog()) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("employees.pinUpdated.title"),
                    I18n.t("employees.pinUpdated.message")
            );
        }
    }

    private void openFichajes() {
        FichajesEmpleadosFrame frame = new FichajesEmpleadosFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
        this.setVisible(false);
    }

    private EmpleadoRowDTO seleccionarEmpleadoParaAccion() {
        EmpleadoFiltroDTO filtro = new EmpleadoFiltroDTO();

        if (AppContext.hasTerminalContext()) {
            filtro.setIdSucursal(AppContext.getIdSucursal());
        }

        List<EmpleadoRowDTO> empleados = services.usuarioService.buscarEmpleados(filtro);

        if (empleados.isEmpty()) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("employees.noEmployees.title"),
                    I18n.t("employees.noEmployees.message")
            );
            return null;
        }

        return TpvDialogUtils.showSelection(
                this,
                I18n.t("employees.select.title"),
                I18n.t("employees.select.message"),
                empleados
        );
    }

    private void volver() {
        safeDispose();

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
}
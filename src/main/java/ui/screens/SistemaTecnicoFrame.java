package ui.screens;

import app.AppContext;
import dtoS.CajaTerminalOptionDTO;
import dtoS.SistemaTecnicoInfoDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SistemaTecnicoFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private static final Color OK_GREEN = new Color(46, 125, 50);

    private final AppServices services;
    private final Runnable onBack;

    private JComboBox<CajaTerminalOptionDTO> comboCajas;

    private JLabel lblCajaActual;
    private JLabel lblCajaSeleccionada;
    private JLabel lblSucursalSeleccionada;
    private JLabel lblEstadoSeleccionado;
    private JLabel lblUbicacionSeleccionada;

    private JLabel lblDbEstado;
    private JLabel lblDbUrl;
    private JLabel lblDbUsuario;

    private JLabel lblApp;
    private JLabel lblVersion;
    private JLabel lblJava;
    private JLabel lblSistema;
    private JLabel lblUsuarioSistema;
    private JLabel lblLogs;
    private JLabel lblTickets;
    private JLabel lblReports;

    public SistemaTecnicoFrame(AppServices services, Runnable onBack) {
        super(I18n.t("technical.title"), onBack, services);

        this.services = Objects.requireNonNull(services, "services no puede ser null");
        this.onBack = onBack;

        requireAuthenticatedOrExit();
        buildUI();
        cargarDatos();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = transparentPanel(new BorderLayout(16, 0));

        JLabel icon = new JLabel(TpvIconFactory.settings(42, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel texts = transparentPanel(new GridLayout(2, 1, 0, 4));

        JLabel title = new JLabel(I18n.t("technical.title"));
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("technical.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        texts.add(title);
        texts.add(subtitle);

        panel.add(icon, BorderLayout.WEST);
        panel.add(texts, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildContent() {
        JPanel content = transparentPanel(new GridLayout(1, 2, 18, 0));
        content.add(buildTerminalCard());
        content.add(buildInfoCard());
        return content;
    }

    private JPanel buildTerminalCard() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("technical.terminalConfig"));
        title.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        card.add(title, BorderLayout.NORTH);

        JPanel body = transparentPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        lblCajaActual = createInfoBox(I18n.t("technical.currentCashBox"), "-");
        addFull(body, lblCajaActual, gbc, 0);

        JLabel selectorLabel = InformeUiTheme.createFieldLabel(I18n.t("technical.selectCashBox"));
        selectorLabel.setIcon(TpvIconFactory.cashRegister(18, InformeUiTheme.TEXT_SECONDARY));
        selectorLabel.setIconTextGap(8);
        addFull(body, selectorLabel, gbc, 1);

        comboCajas = new JComboBox<>();
        InformeUiTheme.styleCombo(comboCajas);
        comboCajas.addActionListener(e -> actualizarDetalleCajaSeleccionada());
        addFull(body, comboCajas, gbc, 2);

        JPanel detalle = buildDetalleCajaPanel();
        addFull(body, detalle, gbc, 3);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildDetalleCajaPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(4, 1, 0, 10));

        lblCajaSeleccionada = createDataLabel(
                I18n.t("technical.cashBox"),
                "-",
                TpvIconFactory.cashRegister(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblSucursalSeleccionada = createDataLabel(
                I18n.t("technical.branch"),
                "-",
                TpvIconFactory.branch(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblEstadoSeleccionado = createDataLabel(
                I18n.t("technical.status"),
                "-",
                TpvIconFactory.warning(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblUbicacionSeleccionada = createDataLabel(
                I18n.t("technical.location"),
                "-",
                TpvIconFactory.location(18, InformeUiTheme.ACCENT_GOLD)
        );

        panel.add(lblCajaSeleccionada);
        panel.add(lblSucursalSeleccionada);
        panel.add(lblEstadoSeleccionado);
        panel.add(lblUbicacionSeleccionada);

        return panel;
    }

    private JPanel buildInfoCard() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("technical.infoTitle"));
        title.setIcon(TpvIconFactory.info(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        card.add(title, BorderLayout.NORTH);

        JPanel body = transparentPanel(new GridLayout(3, 1, 0, 14));

        body.add(buildAppInfoPanel());
        body.add(buildDbInfoPanel());
        body.add(buildPathsInfoPanel());

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAppInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(5, 1, 0, 8));

        lblApp = createDataLabel(
                I18n.t("technical.app"),
                "-",
                TpvIconFactory.settings(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblVersion = createDataLabel(
                I18n.t("technical.version"),
                "-",
                TpvIconFactory.info(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblJava = createDataLabel(
                I18n.t("technical.java"),
                "-",
                TpvIconFactory.settings(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblSistema = createDataLabel(
                I18n.t("technical.os"),
                "-",
                TpvIconFactory.database(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblUsuarioSistema = createDataLabel(
                I18n.t("technical.systemUser"),
                "-",
                TpvIconFactory.user(18, InformeUiTheme.ACCENT_GOLD)
        );

        panel.add(lblApp);
        panel.add(lblVersion);
        panel.add(lblJava);
        panel.add(lblSistema);
        panel.add(lblUsuarioSistema);

        return panel;
    }

    private JPanel buildDbInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(3, 1, 0, 8));

        lblDbEstado = createDataLabel(
                I18n.t("technical.dbConnection"),
                "-",
                TpvIconFactory.warning(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblDbUrl = createDataLabel(
                I18n.t("technical.url"),
                "-",
                TpvIconFactory.database(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblDbUsuario = createDataLabel(
                I18n.t("technical.dbUser"),
                "-",
                TpvIconFactory.user(18, InformeUiTheme.ACCENT_GOLD)
        );

        panel.add(lblDbEstado);
        panel.add(lblDbUrl);
        panel.add(lblDbUsuario);

        return panel;
    }

    private JPanel buildPathsInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(3, 1, 0, 8));

        lblLogs = createDataLabel(
                I18n.t("technical.logs"),
                "-",
                TpvIconFactory.folder(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblTickets = createDataLabel(
                I18n.t("technical.tickets"),
                "-",
                TpvIconFactory.report(18, InformeUiTheme.ACCENT_GOLD)
        );

        lblReports = createDataLabel(
                I18n.t("technical.reports"),
                "-",
                TpvIconFactory.folder(18, InformeUiTheme.ACCENT_GOLD)
        );

        panel.add(lblLogs);
        panel.add(lblTickets);
        panel.add(lblReports);

        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnProbarBd = new JButton(I18n.t("technical.testConnection"));
        InformeUiTheme.styleSecondaryButton(btnProbarBd);
        btnProbarBd.setIcon(TpvIconFactory.database(18, InformeUiTheme.TEXT_PRIMARY));
        btnProbarBd.setIconTextGap(8);
        btnProbarBd.addActionListener(e -> onProbarConexionBD());

        JButton btnGuardar = new JButton(I18n.t("technical.saveTerminalCaja"));
        InformeUiTheme.stylePrimaryButton(btnGuardar);
        btnGuardar.setIcon(TpvIconFactory.save(18, Color.WHITE));
        btnGuardar.setIconTextGap(8);
        btnGuardar.addActionListener(e -> onGuardarCajaTerminal());

        JButton btnVolver = new JButton(I18n.t("common.back"));
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnVolver.setIconTextGap(8);
        btnVolver.addActionListener(e -> volver());

        footer.add(btnProbarBd);
        footer.add(btnGuardar);
        footer.add(btnVolver);

        return footer;
    }

    private JLabel createInfoBox(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b><br>" + nullSafe(value) + "</html>");
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        label.setOpaque(true);
        label.setBackground(InformeUiTheme.CARD_BG_2);
        label.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        label.setIconTextGap(10);
        return label;
    }

    private JLabel createDataLabel(String title, String value, Icon icon) {
        JLabel label = new JLabel(formatData(title, value));
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);

        if (icon != null) {
            label.setIcon(icon);
            label.setIconTextGap(8);
        }

        return label;
    }

    private JLabel createDataLabel(String title, String value) {
        return createDataLabel(title, value, null);
    }

    private String formatData(String title, String value) {
        return "<html><b>" + title + ":</b> " + nullSafe(value) + "</html>";
    }

    private void setData(JLabel label, String title, String value) {
        label.setText(formatData(title, value));
    }

    private void setData(JLabel label, String title, String value, Icon icon) {
        label.setText(formatData(title, value));
        label.setIcon(icon);
        label.setIconTextGap(8);
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        return gbc;
    }

    private void addFull(JPanel panel, Component component, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        panel.add(component, gbc);
    }

    private void cargarDatos() {
        cargarCajas();
        cargarInfoTecnica();
    }

    private void cargarCajas() {
        try {
            int idCajaActual = services.sistemaTecnicoService.getIdCajaTerminalActual();

            lblCajaActual.setText(
                    "<html><b>"
                            + I18n.t("technical.currentCashBoxConfigured")
                            + "</b><br>"
                            + I18n.t("technical.cashBoxId", idCajaActual)
                            + "</html>"
            );

            lblCajaActual.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
            lblCajaActual.setIconTextGap(10);

            comboCajas.removeAllItems();

            List<CajaTerminalOptionDTO> cajas = services.sistemaTecnicoService.getCajasConfigurables();

            CajaTerminalOptionDTO seleccionada = null;

            for (CajaTerminalOptionDTO caja : cajas) {
                comboCajas.addItem(caja);

                if (caja.isSeleccionadaActual()) {
                    seleccionada = caja;
                }
            }

            if (seleccionada != null) {
                comboCajas.setSelectedItem(seleccionada);
            }

            actualizarDetalleCajaSeleccionada();

        } catch (Exception e) {
            mostrarError(I18n.t("technical.loadCashBoxesError"), e);
        }
    }

    private void cargarInfoTecnica() {
        try {
            SistemaTecnicoInfoDTO info = services.sistemaTecnicoService.getInfoTecnica();

            setData(
                    lblApp,
                    I18n.t("technical.app"),
                    info.getAppName(),
                    TpvIconFactory.settings(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblVersion,
                    I18n.t("technical.version"),
                    info.getAppVersion(),
                    TpvIconFactory.info(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblJava,
                    I18n.t("technical.java"),
                    info.getJavaVersion(),
                    TpvIconFactory.settings(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblSistema,
                    I18n.t("technical.os"),
                    info.getSistemaOperativo(),
                    TpvIconFactory.database(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblUsuarioSistema,
                    I18n.t("technical.systemUser"),
                    info.getUsuarioSistema(),
                    TpvIconFactory.user(18, InformeUiTheme.ACCENT_GOLD)
            );

            setEstadoConexionBd(info.isConexionBdOk());

            setData(
                    lblDbUrl,
                    I18n.t("technical.url"),
                    info.getDbUrl(),
                    TpvIconFactory.database(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblDbUsuario,
                    I18n.t("technical.dbUser"),
                    info.getDbUser(),
                    TpvIconFactory.user(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblLogs,
                    I18n.t("technical.logs"),
                    info.getLogsPath(),
                    TpvIconFactory.folder(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblTickets,
                    I18n.t("technical.tickets"),
                    info.getTicketsPath(),
                    TpvIconFactory.report(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblReports,
                    I18n.t("technical.reports"),
                    info.getReportsPath(),
                    TpvIconFactory.folder(18, InformeUiTheme.ACCENT_GOLD)
            );

        } catch (Exception e) {
            mostrarError(I18n.t("technical.loadInfoError"), e);
        }
    }

    private void setEstadoConexionBd(boolean ok) {
        if (ok) {
            lblDbEstado.setForeground(OK_GREEN);
            setData(
                    lblDbEstado,
                    I18n.t("technical.dbConnection"),
                    I18n.t("technical.dbOk"),
                    TpvIconFactory.check(18, OK_GREEN)
            );
            return;
        }

        lblDbEstado.setForeground(InformeUiTheme.DANGER);
        setData(
                lblDbEstado,
                I18n.t("technical.dbConnection"),
                I18n.t("technical.dbError"),
                TpvIconFactory.cancel(18, InformeUiTheme.DANGER)
        );
    }

    private void actualizarDetalleCajaSeleccionada() {
        CajaTerminalOptionDTO caja = (CajaTerminalOptionDTO) comboCajas.getSelectedItem();

        if (caja == null) {
            setData(
                    lblCajaSeleccionada,
                    I18n.t("technical.cashBox"),
                    "-",
                    TpvIconFactory.cashRegister(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblSucursalSeleccionada,
                    I18n.t("technical.branch"),
                    "-",
                    TpvIconFactory.branch(18, InformeUiTheme.ACCENT_GOLD)
            );

            lblEstadoSeleccionado.setForeground(InformeUiTheme.TEXT_PRIMARY);
            setData(
                    lblEstadoSeleccionado,
                    I18n.t("technical.status"),
                    "-",
                    TpvIconFactory.warning(18, InformeUiTheme.ACCENT_GOLD)
            );

            setData(
                    lblUbicacionSeleccionada,
                    I18n.t("technical.location"),
                    "-",
                    TpvIconFactory.location(18, InformeUiTheme.ACCENT_GOLD)
            );

            return;
        }

        setData(
                lblCajaSeleccionada,
                I18n.t("technical.cashBox"),
                caja.getNombreCaja() + " (ID " + caja.getIdCaja() + ")",
                TpvIconFactory.cashRegister(18, InformeUiTheme.ACCENT_GOLD)
        );

        setData(
                lblSucursalSeleccionada,
                I18n.t("technical.branch"),
                caja.getNombreSucursal(),
                TpvIconFactory.branch(18, InformeUiTheme.ACCENT_GOLD)
        );

        setEstadoCaja(caja.getEstado());

        setData(
                lblUbicacionSeleccionada,
                I18n.t("technical.location"),
                caja.getUbicacion(),
                TpvIconFactory.location(18, InformeUiTheme.ACCENT_GOLD)
        );
    }

    private void setEstadoCaja(String estado) {
        String estadoTexto = estado == null || estado.isBlank() ? "-" : estado;

        if ("OPERATIVA".equalsIgnoreCase(estadoTexto)) {
            lblEstadoSeleccionado.setForeground(OK_GREEN);
            setData(
                    lblEstadoSeleccionado,
                    I18n.t("technical.status"),
                    estadoTexto,
                    TpvIconFactory.check(18, OK_GREEN)
            );
            return;
        }

        if ("BLOQUEADA".equalsIgnoreCase(estadoTexto)
                || "INACTIVA".equalsIgnoreCase(estadoTexto)
                || "NO_OPERATIVA".equalsIgnoreCase(estadoTexto)
                || "FUERA_SERVICIO".equalsIgnoreCase(estadoTexto)) {

            lblEstadoSeleccionado.setForeground(InformeUiTheme.DANGER);
            setData(
                    lblEstadoSeleccionado,
                    I18n.t("technical.status"),
                    estadoTexto,
                    TpvIconFactory.cancel(18, InformeUiTheme.DANGER)
            );
            return;
        }

        lblEstadoSeleccionado.setForeground(InformeUiTheme.TEXT_PRIMARY);
        setData(
                lblEstadoSeleccionado,
                I18n.t("technical.status"),
                estadoTexto,
                TpvIconFactory.warning(18, InformeUiTheme.ACCENT_GOLD)
        );
    }

    private void onGuardarCajaTerminal() {
        CajaTerminalOptionDTO caja = (CajaTerminalOptionDTO) comboCajas.getSelectedItem();

        if (caja == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("common.warning"),
                    I18n.t("technical.selectValidCashBox")
            );
            return;
        }

        boolean confirm = TpvDialogUtils.confirm(
                this,
                I18n.t("technical.confirmChangeTitle"),
                I18n.t(
                        "technical.confirmChangeMessage",
                        caja.getNombreCaja(),
                        caja.getNombreSucursal()
                )
        );

        if (!confirm) {
            return;
        }

        try {
            int idCajaAnterior = services.sistemaTecnicoService.getIdCajaTerminalActual();

            services.sistemaTecnicoService.cambiarCajaTerminal(caja.getIdCaja());

            auditarCambioCajaTerminal(idCajaAnterior, caja);

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("technical.savedTitle"),
                    I18n.t("technical.terminalCajaChanged")
            );

            cargarDatos();

        } catch (Exception e) {
            mostrarError(I18n.t("technical.saveCashBoxError"), e);
        }
    }

    private void auditarCambioCajaTerminal(int idCajaAnterior, CajaTerminalOptionDTO cajaNueva) {
        if (cajaNueva == null || services.auditoriaService == null) {
            return;
        }

        if (idCajaAnterior == cajaNueva.getIdCaja()) {
            return;
        }

        try {
            Map<String, Object> detalles = new LinkedHashMap<>();

            detalles.put("idCajaAnterior", idCajaAnterior);
            detalles.put("idCajaNueva", cajaNueva.getIdCaja());
            detalles.put("nombreCajaNueva", cajaNueva.getNombreCaja());
            detalles.put("idSucursalNueva", cajaNueva.getIdSucursal());
            detalles.put("nombreSucursalNueva", cajaNueva.getNombreSucursal());
            detalles.put("estadoCajaNueva", cajaNueva.getEstado());
            detalles.put("ubicacionCajaNueva", cajaNueva.getUbicacion());

            services.auditoriaService.registrarEvento(
                    AppContext.getUsuarioId(),
                    AppContext.getIdSucursal(),
                    "TERMINAL_CAJA_CAMBIADA",
                    detalles
            );

        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo auditar cambio de caja terminal: " + ex.getMessage());
        }
    }

    private void onProbarConexionBD() {
        boolean ok = services.sistemaTecnicoService.probarConexionBD();

        if (ok) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("technical.dbTestTitle"),
                    I18n.t("technical.dbTestOk")
            );
        } else {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("technical.dbTestTitle"),
                    I18n.t("technical.dbTestError")
            );
        }

        cargarInfoTecnica();
    }

    private void volver() {
        safeDispose();

        if (onBack != null) {
            onBack.run();
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        e.printStackTrace();

        TpvDialogUtils.showError(
                this,
                I18n.t("common.error"),
                mensaje + "\n\n" + I18n.t("common.detail") + ": " + e.getMessage()
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "-" : value;
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
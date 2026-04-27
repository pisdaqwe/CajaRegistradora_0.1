package ui.screens;

import dtoS.CajaTerminalOptionDTO;
import dtoS.SistemaTecnicoInfoDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class SistemaTecnicoFrame extends BaseTpvFrame {

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
        super("Herramientas de técnico", null, services);
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
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout());

        JPanel texts = InformeUiTheme.createTransparentPanel(new GridLayout(2, 1, 0, 4));

        JLabel title = new JLabel("Herramientas de técnico");
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Configura este equipo como una caja concreta y revisa el estado técnico de la aplicación.");
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        texts.add(title);
        texts.add(subtitle);

        panel.add(texts, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildContent() {
        JPanel content = InformeUiTheme.createTransparentPanel(new GridLayout(1, 2, 18, 0));
        content.add(buildTerminalCard());
        content.add(buildInfoCard());
        return content;
    }

    private JPanel buildTerminalCard() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        JLabel title = InformeUiTheme.createSectionTitle("Configuración de terminal");
        card.add(title, BorderLayout.NORTH);

        JPanel body = InformeUiTheme.createTransparentPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        lblCajaActual = createInfoBox("Caja actual", "-");

        addFull(body, lblCajaActual, gbc, 0);

        JLabel selectorLabel = InformeUiTheme.createFieldLabel("Seleccionar caja para este equipo");
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

        lblCajaSeleccionada = createDataLabel("Caja", "-");
        lblSucursalSeleccionada = createDataLabel("Sucursal", "-");
        lblEstadoSeleccionado = createDataLabel("Estado", "-");
        lblUbicacionSeleccionada = createDataLabel("Ubicación", "-");

        panel.add(lblCajaSeleccionada);
        panel.add(lblSucursalSeleccionada);
        panel.add(lblEstadoSeleccionado);
        panel.add(lblUbicacionSeleccionada);

        return panel;
    }

    private JPanel buildInfoCard() {
        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 18));

        JLabel title = InformeUiTheme.createSectionTitle("Información técnica");
        card.add(title, BorderLayout.NORTH);

        JPanel body = InformeUiTheme.createTransparentPanel(new GridLayout(3, 1, 0, 14));

        body.add(buildAppInfoPanel());
        body.add(buildDbInfoPanel());
        body.add(buildPathsInfoPanel());

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAppInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(5, 1, 0, 8));

        lblApp = createDataLabel("Aplicación", "-");
        lblVersion = createDataLabel("Versión", "-");
        lblJava = createDataLabel("Java", "-");
        lblSistema = createDataLabel("Sistema operativo", "-");
        lblUsuarioSistema = createDataLabel("Usuario sistema", "-");

        panel.add(lblApp);
        panel.add(lblVersion);
        panel.add(lblJava);
        panel.add(lblSistema);
        panel.add(lblUsuarioSistema);

        return panel;
    }

    private JPanel buildDbInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(3, 1, 0, 8));

        lblDbEstado = createDataLabel("Conexión BD", "-");
        lblDbUrl = createDataLabel("URL", "-");
        lblDbUsuario = createDataLabel("Usuario BD", "-");

        panel.add(lblDbEstado);
        panel.add(lblDbUrl);
        panel.add(lblDbUsuario);

        return panel;
    }

    private JPanel buildPathsInfoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new GridLayout(3, 1, 0, 8));

        lblLogs = createDataLabel("Logs", "-");
        lblTickets = createDataLabel("Tickets", "-");
        lblReports = createDataLabel("Reports", "-");

        panel.add(lblLogs);
        panel.add(lblTickets);
        panel.add(lblReports);

        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnProbarBd = new JButton("Probar conexión BD");
        InformeUiTheme.styleSecondaryButton(btnProbarBd);
        btnProbarBd.addActionListener(e -> onProbarConexionBD());

        JButton btnGuardar = new JButton("Guardar caja terminal");
        InformeUiTheme.stylePrimaryButton(btnGuardar);
        btnGuardar.addActionListener(e -> onGuardarCajaTerminal());

        JButton btnVolver = new JButton("Volver");
        InformeUiTheme.styleSecondaryButton(btnVolver);
        btnVolver.addActionListener(e -> volver());

        footer.add(btnProbarBd);
        footer.add(btnGuardar);
        footer.add(btnVolver);

        return footer;
    }

    private JLabel createInfoBox(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b><br>" + value + "</html>");
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        label.setOpaque(true);
        label.setBackground(InformeUiTheme.CARD_BG_2);
        return label;
    }

    private JLabel createDataLabel(String title, String value) {
        JLabel label = new JLabel(formatData(title, value));
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return label;
    }

    private String formatData(String title, String value) {
        return "<html><b>" + title + ":</b> " + nullSafe(value) + "</html>";
    }

    private void setData(JLabel label, String title, String value) {
        label.setText(formatData(title, value));
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
                    "<html><b>Caja actual configurada</b><br>ID caja: " + idCajaActual + "</html>"
            );

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
            mostrarError("No se pudieron cargar las cajas configurables.", e);
        }
    }

    private void cargarInfoTecnica() {
        try {
            SistemaTecnicoInfoDTO info = services.sistemaTecnicoService.getInfoTecnica();

            setData(lblApp, "Aplicación", info.getAppName());
            setData(lblVersion, "Versión", info.getAppVersion());
            setData(lblJava, "Java", info.getJavaVersion());
            setData(lblSistema, "Sistema operativo", info.getSistemaOperativo());
            setData(lblUsuarioSistema, "Usuario sistema", info.getUsuarioSistema());

            setData(lblDbEstado, "Conexión BD", info.isConexionBdOk() ? "OK" : "ERROR");
            setData(lblDbUrl, "URL", info.getDbUrl());
            setData(lblDbUsuario, "Usuario BD", info.getDbUser());

            setData(lblLogs, "Logs", info.getLogsPath());
            setData(lblTickets, "Tickets", info.getTicketsPath());
            setData(lblReports, "Reports", info.getReportsPath());

        } catch (Exception e) {
            mostrarError("No se pudo cargar la información técnica.", e);
        }
    }

    private void actualizarDetalleCajaSeleccionada() {
        CajaTerminalOptionDTO caja = (CajaTerminalOptionDTO) comboCajas.getSelectedItem();

        if (caja == null) {
            setData(lblCajaSeleccionada, "Caja", "-");
            setData(lblSucursalSeleccionada, "Sucursal", "-");
            setData(lblEstadoSeleccionado, "Estado", "-");
            setData(lblUbicacionSeleccionada, "Ubicación", "-");
            return;
        }

        setData(lblCajaSeleccionada, "Caja", caja.getNombreCaja() + " (ID " + caja.getIdCaja() + ")");
        setData(lblSucursalSeleccionada, "Sucursal", caja.getNombreSucursal());
        setData(lblEstadoSeleccionado, "Estado", caja.getEstado());
        setData(lblUbicacionSeleccionada, "Ubicación", caja.getUbicacion());
    }

    private void onGuardarCajaTerminal() {
        CajaTerminalOptionDTO caja = (CajaTerminalOptionDTO) comboCajas.getSelectedItem();

        if (caja == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una caja válida.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Vas a configurar este equipo como:\n\n"
                        + caja.getNombreCaja()
                        + "\nSucursal: " + caja.getNombreSucursal()
                        + "\n\nEl cambio se aplicará al reiniciar la aplicación.\n\n¿Continuar?",
                "Confirmar cambio de terminal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            services.sistemaTecnicoService.cambiarCajaTerminal(caja.getIdCaja());

            JOptionPane.showMessageDialog(
                    this,
                    "Caja terminal actualizada correctamente.\nReinicia la aplicación para aplicar el cambio.",
                    "Configuración guardada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarDatos();

        } catch (Exception e) {
            mostrarError("No se pudo guardar la caja terminal.", e);
        }
    }

    private void onProbarConexionBD() {
        boolean ok = services.sistemaTecnicoService.probarConexionBD();

        JOptionPane.showMessageDialog(
                this,
                ok ? "Conexión a base de datos correcta." : "No se pudo conectar con la base de datos.",
                "Prueba de conexión",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );

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

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n\nDetalle: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
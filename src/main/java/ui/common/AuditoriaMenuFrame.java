package ui.common;

import app.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dtoS.AuditoriaFiltroDTO;
import dtoS.AuditoriaRowDTO;
import service.AppServices;
import ui.table.AuditoriaTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AuditoriaMenuFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    private JTextField txtBuscar;
    private JComboBox<String> cmbAccion;
    private JSpinner spFechaDesde;
    private JSpinner spFechaHasta;
    private JTable tblAuditoria;
    private AuditoriaTableModel tableModel;

    private JLabel lblIdValor;
    private JLabel lblFechaValor;
    private JLabel lblAccionValor;
    private JLabel lblUsuarioValor;
    private JLabel lblSucursalValor;
    private JTextArea txtDetalles;

    private AuditoriaRowDTO auditoriaSeleccionada;

    public AuditoriaMenuFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super("Auditoría", onLogoutNavigate, services);
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        cargarAcciones();
        cargarAuditoria();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Auditoría del sistema");
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(
                AppContext.getIdSucursal() > 0
                        ? "Sucursal actual: " + AppContext.getIdSucursal()
                        : "Consulta de eventos de negocio"
        );
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        titlePanel.add(lblTitulo);
        titlePanel.add(lblSubtitulo);

        JPanel filtros = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBuscar = new JTextField(22);
        InformeUiTheme.styleTextField(txtBuscar);

        cmbAccion = new JComboBox<>();
        InformeUiTheme.styleCombo(cmbAccion);

        spFechaDesde = new JSpinner(new SpinnerDateModel());
        spFechaHasta = new JSpinner(new SpinnerDateModel());
        InformeUiTheme.styleSpinner(spFechaDesde);
        InformeUiTheme.styleSpinner(spFechaHasta);

        spFechaDesde.setEditor(new JSpinner.DateEditor(spFechaDesde, "dd/MM/yyyy"));
        spFechaHasta.setEditor(new JSpinner.DateEditor(spFechaHasta, "dd/MM/yyyy"));

        JButton btnRefrescar = new JButton("Refrescar");
        InformeUiTheme.stylePrimaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> cargarAuditoria());

        int x = 0;

        gbc.gridx = x++;
        gbc.gridy = 0;
        filtros.add(InformeUiTheme.createFieldLabel("Buscar:"), gbc);

        gbc.gridx = x++;
        gbc.weightx = 1.0;

        JPanel buscarWrapper = new JPanel(new BorderLayout(6, 0));
        buscarWrapper.setOpaque(false);

        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTecladoBuscar = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setToolTipText("Abrir teclado táctil");

        btnTecladoBuscar.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        "Teclado - Buscar auditoría",
                        60
                )
        );

        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        filtros.add(buscarWrapper, gbc);

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(InformeUiTheme.createFieldLabel("Acción:"), gbc);

        gbc.gridx = x++;
        filtros.add(cmbAccion, gbc);

        gbc.gridx = x++;
        filtros.add(InformeUiTheme.createFieldLabel("Desde:"), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaDesde, gbc);

        gbc.gridx = x++;
        filtros.add(InformeUiTheme.createFieldLabel("Hasta:"), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaHasta, gbc);

        gbc.gridx = x;
        filtros.add(btnRefrescar, gbc);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(filtros, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildCenterPanel() {
        tableModel = new AuditoriaTableModel();
        tblAuditoria = new JTable(tableModel);
        InformeUiTheme.styleTable(tblAuditoria);
        tblAuditoria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblAuditoria.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                onAuditoriaSeleccionada();
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblAuditoria);
        InformeUiTheme.styleScrollPane(tableScroll);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(tableScroll, BorderLayout.CENTER);

        JPanel right = buildDetallePanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.72);
        split.setDividerLocation(920);
        split.setBorder(null);

        return split;
    }

    private JPanel buildDetallePanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Detalle del evento");
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblIdValor = createValueLabel();
        lblFechaValor = createValueLabel();
        lblAccionValor = createValueLabel();
        lblUsuarioValor = createValueLabel();
        lblSucursalValor = createValueLabel();

        txtDetalles = new JTextArea(16, 24);
        txtDetalles.setEditable(false);
        txtDetalles.setLineWrap(true);
        txtDetalles.setWrapStyleWord(true);
        txtDetalles.setBackground(InformeUiTheme.CARD_BG_2);
        txtDetalles.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtDetalles.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtDetalles.setBorder(InformeUiTheme.createInnerCardBorder());
        txtDetalles.setFont(new Font("Monospaced", Font.PLAIN, 13));

        int y = 0;
        addDetailRow(form, gbc, y++, "ID:", lblIdValor);
        addDetailRow(form, gbc, y++, "Fecha:", lblFechaValor);
        addDetailRow(form, gbc, y++, "Acción:", lblAccionValor);
        addDetailRow(form, gbc, y++, "Usuario:", lblUsuarioValor);
        addDetailRow(form, gbc, y++, "Sucursal:", lblSucursalValor);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(InformeUiTheme.createFieldLabel("Detalles:"), gbc);

        gbc.gridy = y + 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        InformeUiTheme.styleScrollPane(scrollDetalles);
        form.add(scrollDetalles, gbc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
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

    private void cargarAcciones() {
        cmbAccion.removeAllItems();
        cmbAccion.addItem("TODAS");

        List<String> acciones = services.auditoriaService.getAccionesDisponibles();
        for (String accion : acciones) {
            cmbAccion.addItem(accion);
        }
    }

    private void cargarAuditoria() {
        AuditoriaFiltroDTO filtro = new AuditoriaFiltroDTO();
        filtro.setIdSucursal(AppContext.getIdSucursal() > 0 ? AppContext.getIdSucursal() : null);
        filtro.setTextoBusqueda(txtBuscar.getText());
        filtro.setAccion((String) cmbAccion.getSelectedItem());
        filtro.setFechaDesde(getSpinnerDate(spFechaDesde));
        filtro.setFechaHasta(getSpinnerDate(spFechaHasta));

        List<AuditoriaRowDTO> rows = services.auditoriaService.buscarAuditoria(filtro);
        tableModel.setDatos(rows);

        if (rows.isEmpty()) {
            auditoriaSeleccionada = null;
            limpiarDetalle();
        }
    }

    private void onAuditoriaSeleccionada() {
        int viewRow = tblAuditoria.getSelectedRow();
        if (viewRow < 0) {
            auditoriaSeleccionada = null;
            limpiarDetalle();
            return;
        }

        int modelRow = tblAuditoria.convertRowIndexToModel(viewRow);
        auditoriaSeleccionada = tableModel.getRow(modelRow);

        lblIdValor.setText(String.valueOf(auditoriaSeleccionada.getIdAuditoria()));
        lblFechaValor.setText(auditoriaSeleccionada.getFecha() != null ? auditoriaSeleccionada.getFecha().toString() : "—");
        lblAccionValor.setText(safe(auditoriaSeleccionada.getAccion()));
        lblUsuarioValor.setText(safe(auditoriaSeleccionada.getNombreUsuario()) + " (ID " + auditoriaSeleccionada.getIdUsuario() + ")");
        lblSucursalValor.setText(safe(auditoriaSeleccionada.getNombreSucursal()) + " (ID " + auditoriaSeleccionada.getIdSucursal() + ")");
        txtDetalles.setText(prettyJson(auditoriaSeleccionada.getDetalles()));
        txtDetalles.setCaretPosition(0);
    }

    private String prettyJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Object tree = mapper.readValue(raw, Object.class);

            String pretty = mapper
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(tree);

            return pretty
                    .replace("\\r\\n", "\n")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t");

        } catch (Exception e) {
            return raw
                    .replace("\\r\\n", "\n")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t");
        }
    }

    private void limpiarDetalle() {
        lblIdValor.setText("—");
        lblFechaValor.setText("—");
        lblAccionValor.setText("—");
        lblUsuarioValor.setText("—");
        lblSucursalValor.setText("—");
        txtDetalles.setText("");
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("—");
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(InformeUiTheme.createFieldLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(valueLabel, gbc);
    }

    private LocalDate getSpinnerDate(JSpinner spinner) {
        Object value = spinner.getValue();
        if (!(value instanceof Date date)) {
            return null;
        }

        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void volver() {
        safeDispose();
        if (onBack != null) {
            onBack.run();
        }
    }
}
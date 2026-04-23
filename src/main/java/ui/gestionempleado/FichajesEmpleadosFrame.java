package ui.gestionempleado;

import app.AppContext;
import dtoS.FichajeEmpleadoRowDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.table.FichajesEmpleadoTableModel;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class FichajesEmpleadosFrame extends BaseTpvFrame {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Runnable onBack;
    private final AppServices services;

    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JSpinner spFechaDesde;
    private JSpinner spFechaHasta;

    private JTable tblFichajes;
    private FichajesEmpleadoTableModel tableModel;

    private JLabel lblEmpleadoValor;
    private JLabel lblSucursalValor;
    private JLabel lblEntradaValor;
    private JLabel lblSalidaValor;
    private JLabel lblDuracionValor;
    private JLabel lblEstadoValor;
    private JTextArea txtObservaciones;

    private FichajeEmpleadoRowDTO fichajeSeleccionado;

    public FichajesEmpleadosFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super("Fichajes de Empleados", onLogoutNavigate, services);
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        cargarFichajes();
        refreshHeader();
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
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Fichajes de Empleados");
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(buildSubtitulo());
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        titlePanel.add(lblTitulo);
        titlePanel.add(lblSubtitulo);

        JPanel filtros = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBuscar = new JTextField(20);
        InformeUiTheme.styleTextField(txtBuscar);

        cmbEstado = new JComboBox<>(new String[]{"TODOS", "ABIERTO", "CERRADO", "ANULADO"});
        InformeUiTheme.styleCombo(cmbEstado);

        spFechaDesde = new JSpinner(new SpinnerDateModel());
        spFechaHasta = new JSpinner(new SpinnerDateModel());
        InformeUiTheme.styleSpinner(spFechaDesde);
        InformeUiTheme.styleSpinner(spFechaHasta);

        JSpinner.DateEditor editorDesde = new JSpinner.DateEditor(spFechaDesde, "dd/MM/yyyy");
        JSpinner.DateEditor editorHasta = new JSpinner.DateEditor(spFechaHasta, "dd/MM/yyyy");
        spFechaDesde.setEditor(editorDesde);
        spFechaHasta.setEditor(editorHasta);

        JButton btnRefrescar = new JButton("Refrescar");
        InformeUiTheme.stylePrimaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> refrescarTodo());

        int x = 0;

        gbc.gridx = x++;
        gbc.gridy = 0;
        filtros.add(InformeUiTheme.createFieldLabel("Buscar:"), gbc);

        gbc.gridx = x++;
        gbc.weightx = 1.0;
        filtros.add(txtBuscar, gbc);

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(InformeUiTheme.createFieldLabel("Estado:"), gbc);

        gbc.gridx = x++;
        filtros.add(cmbEstado, gbc);

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

    private JSplitPane buildCenterPanel() {
        tableModel = new FichajesEmpleadoTableModel();
        tblFichajes = new JTable(tableModel);
        InformeUiTheme.styleTable(tblFichajes);
        tblFichajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblFichajes.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                onFichajeSeleccionado();
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblFichajes);
        InformeUiTheme.styleScrollPane(tableScroll);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(tableScroll, BorderLayout.CENTER);

        JPanel right = buildDetailPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.72);
        split.setDividerLocation(900);
        split.setBorder(null);

        return split;
    }

    private JPanel buildDetailPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Detalle del fichaje");
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblEmpleadoValor = createValueLabel();
        lblSucursalValor = createValueLabel();
        lblEntradaValor = createValueLabel();
        lblSalidaValor = createValueLabel();
        lblDuracionValor = createValueLabel();
        lblEstadoValor = createValueLabel();

        txtObservaciones = new JTextArea(6, 20);
        txtObservaciones.setEditable(false);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(InformeUiTheme.CARD_BG_2);
        txtObservaciones.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtObservaciones.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtObservaciones.setBorder(InformeUiTheme.createInnerCardBorder());
        txtObservaciones.setFont(InformeUiTheme.FONT_BODY);

        int y = 0;
        addDetailRow(form, gbc, y++, "Empleado:", lblEmpleadoValor);
        addDetailRow(form, gbc, y++, "Sucursal:", lblSucursalValor);
        addDetailRow(form, gbc, y++, "Entrada:", lblEntradaValor);
        addDetailRow(form, gbc, y++, "Salida:", lblSalidaValor);
        addDetailRow(form, gbc, y++, "Duración:", lblDuracionValor);
        addDetailRow(form, gbc, y++, "Estado:", lblEstadoValor);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(InformeUiTheme.createFieldLabel("Observaciones:"), gbc);

        gbc.gridy = y + 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        InformeUiTheme.styleScrollPane(scrollObs);
        form.add(scrollObs, gbc);

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

    private String buildSubtitulo() {
        if (AppContext.hasTerminalContext()) {
            return "Sucursal actual: " + AppContext.getIdSucursal();
        }
        return "Sin contexto de sucursal";
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("-");
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

    private void cargarFichajes() {
        Integer idSucursal = null;
        if (AppContext.hasTerminalContext()) {
            idSucursal = AppContext.getIdSucursal();
        }

        String textoBusqueda = txtBuscar.getText();
        String estado = (String) cmbEstado.getSelectedItem();
        LocalDate fechaDesde = getSpinnerDate(spFechaDesde);
        LocalDate fechaHasta = getSpinnerDate(spFechaHasta);

        List<FichajeEmpleadoRowDTO> rows = services.fichajeService.buscarFichajesAdministracion(
                idSucursal,
                null,
                textoBusqueda,
                estado,
                fechaDesde,
                fechaHasta
        );

        tableModel.setRows(rows);

        if (rows.isEmpty()) {
            fichajeSeleccionado = null;
            limpiarDetalle();
        }
    }

    private void onFichajeSeleccionado() {
        int viewRow = tblFichajes.getSelectedRow();
        if (viewRow < 0) {
            fichajeSeleccionado = null;
            limpiarDetalle();
            return;
        }

        int modelRow = tblFichajes.convertRowIndexToModel(viewRow);
        fichajeSeleccionado = tableModel.getRow(modelRow);

        if (fichajeSeleccionado == null) {
            limpiarDetalle();
            return;
        }

        lblEmpleadoValor.setText(safe(fichajeSeleccionado.getNombreEmpleado()));
        lblSucursalValor.setText(safe(fichajeSeleccionado.getNombreSucursal()));
        lblEntradaValor.setText(formatDateTime(fichajeSeleccionado.getFechaEntrada()));
        lblSalidaValor.setText(formatDateTime(fichajeSeleccionado.getFechaSalida()));
        lblDuracionValor.setText(fichajeSeleccionado.getDuracionTexto());
        lblEstadoValor.setText(safe(fichajeSeleccionado.getEstado()));
        txtObservaciones.setText(safe(fichajeSeleccionado.getObservaciones()));
    }

    private void refrescarTodo() {
        cargarFichajes();
    }

    private void limpiarDetalle() {
        lblEmpleadoValor.setText("-");
        lblSucursalValor.setText("-");
        lblEntradaValor.setText("-");
        lblSalidaValor.setText("-");
        lblDuracionValor.setText("-");
        lblEstadoValor.setText("-");
        txtObservaciones.setText("");
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

    private String formatDateTime(java.time.LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private void volver() {
        safeDispose();
        if (onBack != null) {
            onBack.run();
        }
    }
}
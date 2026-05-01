package ui.gestionempleado;

import app.AppContext;
import dtoS.FichajeEmpleadoRowDTO;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.TecladoVirtualDialog;
import ui.table.FichajesEmpleadoTableModel;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class FichajesEmpleadosFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String ESTADO_TODOS = "TODOS";
    private static final String ESTADO_ABIERTO = "ABIERTO";
    private static final String ESTADO_CERRADO = "CERRADO";
    private static final String ESTADO_ANULADO = "ANULADO";

    private static final Color OK_GREEN = new Color(46, 125, 50);

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
        super(I18n.t("employees.clockFrame.title"), onLogoutNavigate, services);

        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        cargarFichajes();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel wrapper = transparentPanel(new BorderLayout(0, 12));

        JPanel titlePanel = InformeUiTheme.createCardPanel(new BorderLayout(16, 0));

        JLabel icon = new JLabel(TpvIconFactory.clock(42, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(I18n.t("employees.clockFrame.header"));
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(buildSubtitulo());
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        textPanel.add(lblTitulo);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblSubtitulo);

        titlePanel.add(icon, BorderLayout.WEST);
        titlePanel.add(textPanel, BorderLayout.CENTER);

        JPanel filtros = buildFiltersPanel();

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(filtros, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel buildFiltersPanel() {
        JPanel filtros = InformeUiTheme.createCardPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 7, 6, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        JLabel lblFiltros = InformeUiTheme.createSectionTitle(I18n.t("employees.clockFrame.filters"));
        lblFiltros.setIcon(TpvIconFactory.filter(18, InformeUiTheme.ACCENT_GOLD));
        lblFiltros.setIconTextGap(8);

        txtBuscar = new JTextField(20);
        InformeUiTheme.styleTextField(txtBuscar);

        cmbEstado = new JComboBox<>(new String[]{
                ESTADO_TODOS,
                ESTADO_ABIERTO,
                ESTADO_CERRADO,
                ESTADO_ANULADO
        });
        InformeUiTheme.styleCombo(cmbEstado);
        configurarRendererEstadoCombo();

        spFechaDesde = new JSpinner(new SpinnerDateModel());
        spFechaHasta = new JSpinner(new SpinnerDateModel());

        InformeUiTheme.styleSpinner(spFechaDesde);
        InformeUiTheme.styleSpinner(spFechaHasta);

        spFechaDesde.setEditor(new JSpinner.DateEditor(spFechaDesde, "dd/MM/yyyy"));
        spFechaHasta.setEditor(new JSpinner.DateEditor(spFechaHasta, "dd/MM/yyyy"));

        JButton btnRefrescar = new JButton(I18n.t("common.refresh"));
        InformeUiTheme.stylePrimaryButton(btnRefrescar);
        btnRefrescar.setIcon(TpvIconFactory.refresh(18, Color.WHITE));
        btnRefrescar.setIconTextGap(8);
        btnRefrescar.addActionListener(e -> refrescarTodo());

        JButton btnTecladoBuscar = new JButton();
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setIcon(TpvIconFactory.search(18, InformeUiTheme.TEXT_PRIMARY));
        btnTecladoBuscar.setToolTipText(I18n.t("employees.clockFrame.searchKeyboardTooltip"));
        btnTecladoBuscar.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        I18n.t("employees.clockFrame.searchKeyboardTitle"),
                        40
                )
        );

        JPanel buscarWrapper = transparentPanel(new BorderLayout(6, 0));
        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);
        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        int x = 0;

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(lblFiltros, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.clockFrame.search"),
                TpvIconFactory.search(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        gbc.weightx = 1.0;
        filtros.add(buscarWrapper, gbc);

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.clockFrame.status"),
                TpvIconFactory.history(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(cmbEstado, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.clockFrame.from"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaDesde, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.clockFrame.to"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaHasta, gbc);

        gbc.gridx = x;
        filtros.add(btnRefrescar, gbc);

        return filtros;
    }

    private JSplitPane buildCenterPanel() {
        tableModel = new FichajesEmpleadoTableModel();

        tblFichajes = new JTable(tableModel);
        InformeUiTheme.styleTable(tblFichajes);
        tblFichajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFichajes.setAutoCreateRowSorter(true);

        configurarColumnasTabla();
        configurarRendererTabla();

        tblFichajes.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                onFichajeSeleccionado();
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblFichajes);
        InformeUiTheme.styleScrollPane(tableScroll);

        JPanel left = InformeUiTheme.createCardPanel(new BorderLayout(8, 10));

        JLabel tableTitle = InformeUiTheme.createSectionTitle(I18n.t("employees.clockFrame.table.title"));
        tableTitle.setIcon(TpvIconFactory.table(20, InformeUiTheme.ACCENT_GOLD));
        tableTitle.setIconTextGap(8);

        left.add(tableTitle, BorderLayout.NORTH);
        left.add(tableScroll, BorderLayout.CENTER);

        JPanel right = buildDetailPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.72);
        split.setDividerLocation(900);
        split.setBorder(null);
        split.setOpaque(false);

        return split;
    }

    private void configurarColumnasTabla() {
        if (tblFichajes.getColumnModel().getColumnCount() < 7) {
            return;
        }

        tblFichajes.getColumnModel().getColumn(0).setHeaderValue(I18n.t("employees.clockFrame.table.id"));
        tblFichajes.getColumnModel().getColumn(1).setHeaderValue(I18n.t("employees.clockFrame.table.employee"));
        tblFichajes.getColumnModel().getColumn(2).setHeaderValue(I18n.t("employees.clockFrame.table.branch"));
        tblFichajes.getColumnModel().getColumn(3).setHeaderValue(I18n.t("employees.clockFrame.table.entry"));
        tblFichajes.getColumnModel().getColumn(4).setHeaderValue(I18n.t("employees.clockFrame.table.exit"));
        tblFichajes.getColumnModel().getColumn(5).setHeaderValue(I18n.t("employees.clockFrame.table.duration"));
        tblFichajes.getColumnModel().getColumn(6).setHeaderValue(I18n.t("employees.clockFrame.table.status"));

        tblFichajes.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblFichajes.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblFichajes.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblFichajes.getColumnModel().getColumn(3).setPreferredWidth(145);
        tblFichajes.getColumnModel().getColumn(4).setPreferredWidth(145);
        tblFichajes.getColumnModel().getColumn(5).setPreferredWidth(95);
        tblFichajes.getColumnModel().getColumn(6).setPreferredWidth(110);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblFichajes.getColumnModel().getColumn(0).setCellRenderer(center);

        tblFichajes.getTableHeader().repaint();
    }

    private void configurarRendererTabla() {
        if (tblFichajes.getColumnModel().getColumnCount() < 7) {
            return;
        }

        DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                String estado = value == null ? "" : value.toString();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setText(traducirEstado(estado));

                Color color = colorEstado(estado, isSelected, table);
                label.setForeground(color);
                label.setIcon(iconEstado(estado, 14, color));
                label.setIconTextGap(6);

                return label;
            }
        };

        tblFichajes.getColumnModel().getColumn(6).setCellRenderer(estadoRenderer);
    }

    private JPanel buildDetailPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 10));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("employees.clockFrame.detail.title"));
        title.setIcon(TpvIconFactory.eye(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        JPanel form = transparentPanel(new GridBagLayout());

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

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.employee"),
                TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY),
                lblEmpleadoValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.branch"),
                TpvIconFactory.branch(16, InformeUiTheme.TEXT_SECONDARY),
                lblSucursalValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.entry"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY),
                lblEntradaValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.exit"),
                TpvIconFactory.logout(16, InformeUiTheme.TEXT_SECONDARY),
                lblSalidaValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.duration"),
                TpvIconFactory.clock(16, InformeUiTheme.TEXT_SECONDARY),
                lblDuracionValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("employees.clockFrame.detail.status"),
                TpvIconFactory.history(16, InformeUiTheme.TEXT_SECONDARY),
                lblEstadoValor
        );

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;

        JLabel obsLabel = createFieldLabelWithIcon(
                I18n.t("employees.clockFrame.detail.notes"),
                TpvIconFactory.info(16, InformeUiTheme.TEXT_SECONDARY)
        );

        form.add(obsLabel, gbc);

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

    private void configurarRendererEstadoCombo() {
        cmbEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                String estado = value == null ? ESTADO_TODOS : value.toString();
                Color color = isSelected ? Color.WHITE : colorEstado(estado, false, null);

                label.setText(traducirEstadoFiltro(estado));
                label.setIcon(iconEstadoFiltro(estado, 16, color));
                label.setIconTextGap(8);

                return label;
            }
        });
    }

    private String buildSubtitulo() {
        if (AppContext.hasTerminalContext()) {
            return I18n.t("employees.menu.branchSubtitle", AppContext.getIdSucursal());
        }

        return I18n.t("employees.menu.noBranchSubtitle");
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private JLabel createFieldLabelWithIcon(String text, Icon icon) {
        JLabel lbl = InformeUiTheme.createFieldLabel(text);

        if (icon != null) {
            lbl.setIcon(icon);
            lbl.setIconTextGap(7);
        }

        return lbl;
    }

    private void addDetailRow(JPanel panel,
                              GridBagConstraints gbc,
                              int row,
                              String label,
                              Icon icon,
                              JLabel valueLabel) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(createFieldLabelWithIcon(label, icon), gbc);

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
        configurarColumnasTabla();
        configurarRendererTabla();

        if (rows.isEmpty()) {
            fichajeSeleccionado = null;
            limpiarDetalle();
        } else if (tblFichajes.getRowCount() > 0) {
            tblFichajes.setRowSelectionInterval(0, 0);
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

        setEstadoDetalle(fichajeSeleccionado.getEstado());

        txtObservaciones.setText(safe(fichajeSeleccionado.getObservaciones()));
        txtObservaciones.setCaretPosition(0);
    }

    private void setEstadoDetalle(String estado) {
        String estadoTraducido = traducirEstado(estado);
        Color color = colorEstado(estado, false, null);

        lblEstadoValor.setText(estadoTraducido);
        lblEstadoValor.setForeground(color);
        lblEstadoValor.setIcon(iconEstado(estado, 15, color));
        lblEstadoValor.setIconTextGap(6);
    }

    private Icon iconEstado(String estado, int size, Color color) {
        if (ESTADO_ABIERTO.equalsIgnoreCase(estado)) {
            return TpvIconFactory.clock(size, color);
        }

        if (ESTADO_CERRADO.equalsIgnoreCase(estado)) {
            return TpvIconFactory.check(size, color);
        }

        if (ESTADO_ANULADO.equalsIgnoreCase(estado)) {
            return TpvIconFactory.cancel(size, color);
        }

        return TpvIconFactory.warning(size, color);
    }

    private Icon iconEstadoFiltro(String estado, int size, Color color) {
        if (ESTADO_TODOS.equalsIgnoreCase(estado)) {
            return TpvIconFactory.filter(size, color);
        }

        return iconEstado(estado, size, color);
    }

    private Color colorEstado(String estado, boolean isSelected, JTable table) {
        if (isSelected && table != null) {
            return table.getSelectionForeground();
        }

        if (ESTADO_ABIERTO.equalsIgnoreCase(estado)) {
            return InformeUiTheme.ACCENT_GOLD;
        }

        if (ESTADO_CERRADO.equalsIgnoreCase(estado)) {
            return OK_GREEN;
        }

        if (ESTADO_ANULADO.equalsIgnoreCase(estado)) {
            return InformeUiTheme.DANGER;
        }

        return InformeUiTheme.TEXT_PRIMARY;
    }

    private String traducirEstadoFiltro(String estado) {
        if (ESTADO_TODOS.equalsIgnoreCase(estado)) {
            return I18n.t("employees.clockFrame.status.all");
        }

        return traducirEstado(estado);
    }

    private String traducirEstado(String estado) {
        if (ESTADO_ABIERTO.equalsIgnoreCase(estado)) {
            return I18n.t("employees.clockFrame.status.open");
        }

        if (ESTADO_CERRADO.equalsIgnoreCase(estado)) {
            return I18n.t("employees.clockFrame.status.closed");
        }

        if (ESTADO_ANULADO.equalsIgnoreCase(estado)) {
            return I18n.t("employees.clockFrame.status.cancelled");
        }

        return safe(estado);
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
        lblEstadoValor.setIcon(null);
        lblEstadoValor.setForeground(InformeUiTheme.TEXT_PRIMARY);
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
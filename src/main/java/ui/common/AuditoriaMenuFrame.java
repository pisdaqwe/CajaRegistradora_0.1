package ui.common;

import app.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dtoS.AuditoriaFiltroDTO;
import dtoS.AuditoriaRowDTO;
import service.AppServices;
import ui.table.AuditoriaTableModel;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AuditoriaMenuFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private static final String ALL_ACTION_VALUE = "TODAS";

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
        super(I18n.t("audit.title"), onLogoutNavigate, services);

        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        cargarAcciones();
        cargarAuditoria();
        refreshHeader();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);
        root.add(buildCenterPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPanel() {
        JPanel wrapper = transparentPanel(new BorderLayout(0, 12));

        JPanel titlePanel = InformeUiTheme.createCardPanel(new BorderLayout(16, 0));

        JLabel icon = new JLabel(TpvIconFactory.shield(42, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(I18n.t("audit.header.title"));
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(getSubtitleText());
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

    private String getSubtitleText() {
        if (AppContext.getIdSucursal() > 0) {
            return I18n.t("audit.header.branch", AppContext.getIdSucursal());
        }

        return I18n.t("audit.header.subtitle");
    }

    private JPanel buildFiltersPanel() {
        JPanel filtros = InformeUiTheme.createCardPanel(new GridBagLayout());

        JLabel lblFiltro = InformeUiTheme.createSectionTitle(I18n.t("audit.filters.title"));
        lblFiltro.setIcon(TpvIconFactory.filter(18, InformeUiTheme.ACCENT_GOLD));
        lblFiltro.setIconTextGap(8);

        txtBuscar = new JTextField(22);
        InformeUiTheme.styleTextField(txtBuscar);

        cmbAccion = new JComboBox<>();
        InformeUiTheme.styleCombo(cmbAccion);
        configurarRendererAcciones();

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
        btnRefrescar.addActionListener(e -> cargarAuditoria());

        JButton btnTecladoBuscar = new JButton();
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setIcon(TpvIconFactory.search(18, InformeUiTheme.TEXT_PRIMARY));
        btnTecladoBuscar.setToolTipText(I18n.t("audit.search.keyboardTooltip"));
        btnTecladoBuscar.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        I18n.t("audit.search.keyboardTitle"),
                        60
                )
        );

        JPanel buscarWrapper = transparentPanel(new BorderLayout(6, 0));
        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);
        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 7, 6, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        int x = 0;

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(lblFiltro, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("audit.filters.search"),
                TpvIconFactory.search(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        gbc.weightx = 1.0;
        filtros.add(buscarWrapper, gbc);

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("audit.filters.action"),
                TpvIconFactory.history(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(cmbAccion, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("audit.filters.from"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaDesde, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("audit.filters.to"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(spFechaHasta, gbc);

        gbc.gridx = x;
        filtros.add(btnRefrescar, gbc);

        return filtros;
    }

    private JComponent buildCenterPanel() {
        tableModel = new AuditoriaTableModel();

        tblAuditoria = new JTable(tableModel);
        InformeUiTheme.styleTable(tblAuditoria);
        tblAuditoria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAuditoria.setAutoCreateRowSorter(true);

        configurarColumnasTabla();

        tblAuditoria.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                onAuditoriaSeleccionada();
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblAuditoria);
        InformeUiTheme.styleScrollPane(tableScroll);

        JPanel left = InformeUiTheme.createCardPanel(new BorderLayout(0, 10));

        JLabel tableTitle = InformeUiTheme.createSectionTitle(I18n.t("audit.table.title"));
        tableTitle.setIcon(TpvIconFactory.table(20, InformeUiTheme.ACCENT_GOLD));
        tableTitle.setIconTextGap(8);

        left.add(tableTitle, BorderLayout.NORTH);
        left.add(tableScroll, BorderLayout.CENTER);

        JPanel right = buildDetallePanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.72);
        split.setDividerLocation(920);
        split.setBorder(null);
        split.setOpaque(false);

        return split;
    }

    private void configurarColumnasTabla() {
        if (tblAuditoria.getColumnModel().getColumnCount() < 6) {
            return;
        }

        tblAuditoria.getColumnModel().getColumn(0).setHeaderValue(I18n.t("audit.table.id"));
        tblAuditoria.getColumnModel().getColumn(1).setHeaderValue(I18n.t("audit.table.date"));
        tblAuditoria.getColumnModel().getColumn(2).setHeaderValue(I18n.t("audit.table.action"));
        tblAuditoria.getColumnModel().getColumn(3).setHeaderValue(I18n.t("audit.table.user"));
        tblAuditoria.getColumnModel().getColumn(4).setHeaderValue(I18n.t("audit.table.branch"));
        tblAuditoria.getColumnModel().getColumn(5).setHeaderValue(I18n.t("audit.table.summary"));

        tblAuditoria.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblAuditoria.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblAuditoria.getColumnModel().getColumn(2).setPreferredWidth(210);
        tblAuditoria.getColumnModel().getColumn(3).setPreferredWidth(170);
        tblAuditoria.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblAuditoria.getColumnModel().getColumn(5).setPreferredWidth(360);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblAuditoria.getColumnModel().getColumn(0).setCellRenderer(center);

        tblAuditoria.getTableHeader().repaint();
    }

    private JPanel buildDetallePanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 10));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("audit.detail.title"));
        title.setIcon(TpvIconFactory.eye(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        JPanel form = transparentPanel(new GridBagLayout());

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

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("audit.detail.id"),
                TpvIconFactory.info(16, InformeUiTheme.TEXT_SECONDARY),
                lblIdValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("audit.detail.date"),
                TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY),
                lblFechaValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("audit.detail.action"),
                TpvIconFactory.history(16, InformeUiTheme.TEXT_SECONDARY),
                lblAccionValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("audit.detail.user"),
                TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY),
                lblUsuarioValor
        );

        addDetailRow(
                form,
                gbc,
                y++,
                I18n.t("audit.detail.branch"),
                TpvIconFactory.branch(16, InformeUiTheme.TEXT_SECONDARY),
                lblSucursalValor
        );

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;

        JLabel detallesLabel = createFieldLabelWithIcon(
                I18n.t("audit.detail.details"),
                TpvIconFactory.table(16, InformeUiTheme.TEXT_SECONDARY)
        );

        form.add(detallesLabel, gbc);

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

    private void configurarRendererAcciones() {
        cmbAccion.setRenderer(new DefaultListCellRenderer() {
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

                if (ALL_ACTION_VALUE.equals(value)) {
                    label.setText(I18n.t("audit.filters.allActions"));
                }

                label.setIcon(TpvIconFactory.history(16,
                        isSelected ? Color.WHITE : InformeUiTheme.TEXT_SECONDARY));
                label.setIconTextGap(8);

                return label;
            }
        });
    }

    private void cargarAcciones() {
        cmbAccion.removeAllItems();
        cmbAccion.addItem(ALL_ACTION_VALUE);

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

        configurarColumnasTabla();

        if (rows.isEmpty()) {
            auditoriaSeleccionada = null;
            limpiarDetalle();
        } else if (tblAuditoria.getRowCount() > 0) {
            tblAuditoria.setRowSelectionInterval(0, 0);
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

        panel.add(createFieldLabelWithIcon(label, icon), gbc);

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
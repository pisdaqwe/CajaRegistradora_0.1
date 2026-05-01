package ui.gestionempleado;

import app.AppContext;
import dtoS.EmpleadoDetalleDTO;
import dtoS.EmpleadoFiltroDTO;
import dtoS.EmpleadoRowDTO;
import model.Rol;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.dialog.EmpleadoFormDialog;
import ui.dialog.ResetPinEmpleadoDialog;
import ui.table.EmpleadosTableModel;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GestionEmpleadosFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String STATUS_ALL = "ALL";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

    private static final Color OK_GREEN = new Color(46, 125, 50);

    private final Runnable onBack;
    private final AppServices services;

    private JTextField txtBuscar;
    private JComboBox<Object> cmbRol;
    private JComboBox<String> cmbEstado;
    private JCheckBox chkSoloFichados;
    private JCheckBox chkSoloCajaAbierta;

    private JTable tblEmpleados;
    private EmpleadosTableModel tableModel;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnActivarDesactivar;
    private JButton btnResetPin;
    private JButton btnVerFichajes;

    private JLabel lblNombreValor;
    private JLabel lblUsuarioValor;
    private JLabel lblRolValor;
    private JLabel lblSucursalValor;
    private JLabel lblActivoValor;
    private JLabel lblFechaCreacionValor;
    private JLabel lblFichajeActualValor;
    private JLabel lblSesionCajaValor;
    private JLabel lblCajaActualValor;
    private JLabel lblUltimaActividadValor;
    private JTextArea txtObservaciones;

    private EmpleadoRowDTO empleadoSeleccionado;

    public GestionEmpleadosFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super(I18n.t("employees.management.title"), onLogoutNavigate, services);

        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();
        buildUI();
        cargarCombos();
        cargarTabla();
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

        JLabel icon = new JLabel(TpvIconFactory.idCard(42, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(I18n.t("employees.management.header"));
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

        JLabel lblFiltros = InformeUiTheme.createSectionTitle(I18n.t("employees.management.filters"));
        lblFiltros.setIcon(TpvIconFactory.filter(18, InformeUiTheme.ACCENT_GOLD));
        lblFiltros.setIconTextGap(8);

        txtBuscar = new JTextField(22);
        InformeUiTheme.styleTextField(txtBuscar);

        cmbRol = new JComboBox<>();
        InformeUiTheme.styleCombo(cmbRol);

        cmbEstado = new JComboBox<>(new String[]{
                STATUS_ALL,
                STATUS_ACTIVE,
                STATUS_INACTIVE
        });
        InformeUiTheme.styleCombo(cmbEstado);
        configurarRendererEstado();

        chkSoloFichados = new JCheckBox(I18n.t("employees.management.onlyClocked"));
        InformeUiTheme.styleCheckBox(chkSoloFichados);
        chkSoloFichados.setIcon(TpvIconFactory.clock(16, InformeUiTheme.TEXT_SECONDARY));

        chkSoloCajaAbierta = new JCheckBox(I18n.t("employees.management.onlyCashOpen"));
        InformeUiTheme.styleCheckBox(chkSoloCajaAbierta);
        chkSoloCajaAbierta.setIcon(TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY));

        JButton btnRefrescar = new JButton(I18n.t("common.refresh"));
        InformeUiTheme.styleSecondaryButton(btnRefrescar);
        btnRefrescar.setIcon(TpvIconFactory.refresh(18, InformeUiTheme.TEXT_PRIMARY));
        btnRefrescar.setIconTextGap(8);

        btnNuevo = new JButton(I18n.t("employees.management.newEmployee"));
        InformeUiTheme.stylePrimaryButton(btnNuevo);
        btnNuevo.setIcon(TpvIconFactory.user(18, Color.WHITE));
        btnNuevo.setIconTextGap(8);

        JPanel buscarWrapper = transparentPanel(new BorderLayout(6, 0));
        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTecladoBuscar = new JButton();
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setIcon(TpvIconFactory.search(18, InformeUiTheme.TEXT_PRIMARY));
        btnTecladoBuscar.setToolTipText(I18n.t("employees.management.searchKeyboardTooltip"));
        btnTecladoBuscar.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        I18n.t("employees.management.searchKeyboardTitle"),
                        40
                )
        );

        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        int x = 0;

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(lblFiltros, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.management.search"),
                TpvIconFactory.search(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        gbc.weightx = 1.0;
        filtros.add(buscarWrapper, gbc);

        gbc.gridx = x++;
        gbc.weightx = 0;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.management.role"),
                TpvIconFactory.shield(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(cmbRol, gbc);

        gbc.gridx = x++;
        filtros.add(createFieldLabelWithIcon(
                I18n.t("employees.management.status"),
                TpvIconFactory.check(16, InformeUiTheme.TEXT_SECONDARY)
        ), gbc);

        gbc.gridx = x++;
        filtros.add(cmbEstado, gbc);

        gbc.gridx = x++;
        filtros.add(chkSoloFichados, gbc);

        gbc.gridx = x++;
        filtros.add(chkSoloCajaAbierta, gbc);

        gbc.gridx = x++;
        filtros.add(btnRefrescar, gbc);

        gbc.gridx = x;
        filtros.add(btnNuevo, gbc);

        btnRefrescar.addActionListener(e -> refrescarTodo());
        btnNuevo.addActionListener(e -> onNuevoEmpleado());

        return filtros;
    }

    private JSplitPane buildCenterPanel() {
        tableModel = new EmpleadosTableModel();

        tblEmpleados = new JTable(tableModel);
        InformeUiTheme.styleTable(tblEmpleados);
        tblEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEmpleados.setAutoCreateRowSorter(true);

        configurarColumnasTabla();
        configurarRendererTabla();

        tblEmpleados.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                onEmpleadoSeleccionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tblEmpleados);
        InformeUiTheme.styleScrollPane(scroll);

        JPanel left = InformeUiTheme.createCardPanel(new BorderLayout(8, 10));

        JLabel tableTitle = InformeUiTheme.createSectionTitle(I18n.t("employees.management.table.title"));
        tableTitle.setIcon(TpvIconFactory.table(20, InformeUiTheme.ACCENT_GOLD));
        tableTitle.setIconTextGap(8);

        left.add(tableTitle, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);
        left.add(buildActionsPanel(), BorderLayout.SOUTH);

        JPanel right = buildDetailPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.70);
        split.setDividerLocation(930);
        split.setBorder(null);
        split.setOpaque(false);

        return split;
    }

    private void configurarColumnasTabla() {
        if (tblEmpleados.getColumnModel().getColumnCount() < 8) {
            return;
        }

        tblEmpleados.getColumnModel().getColumn(0).setHeaderValue(I18n.t("employees.table.id"));
        tblEmpleados.getColumnModel().getColumn(1).setHeaderValue(I18n.t("employees.table.name"));
        tblEmpleados.getColumnModel().getColumn(2).setHeaderValue(I18n.t("employees.table.user"));
        tblEmpleados.getColumnModel().getColumn(3).setHeaderValue(I18n.t("employees.table.role"));
        tblEmpleados.getColumnModel().getColumn(4).setHeaderValue(I18n.t("employees.table.branch"));
        tblEmpleados.getColumnModel().getColumn(5).setHeaderValue(I18n.t("employees.table.active"));
        tblEmpleados.getColumnModel().getColumn(6).setHeaderValue(I18n.t("employees.table.clocked"));
        tblEmpleados.getColumnModel().getColumn(7).setHeaderValue(I18n.t("employees.table.cashOpen"));

        tblEmpleados.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblEmpleados.getColumnModel().getColumn(1).setPreferredWidth(190);
        tblEmpleados.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblEmpleados.getColumnModel().getColumn(3).setPreferredWidth(125);
        tblEmpleados.getColumnModel().getColumn(4).setPreferredWidth(145);
        tblEmpleados.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblEmpleados.getColumnModel().getColumn(6).setPreferredWidth(85);
        tblEmpleados.getColumnModel().getColumn(7).setPreferredWidth(105);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblEmpleados.getColumnModel().getColumn(0).setCellRenderer(center);

        tblEmpleados.getTableHeader().repaint();
    }

    private void configurarRendererTabla() {
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
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

                label.setHorizontalAlignment(SwingConstants.CENTER);

                String text = value == null ? "" : value.toString();

                if ("Sí".equalsIgnoreCase(text) || "Si".equalsIgnoreCase(text) || "Activo".equalsIgnoreCase(text)) {
                    label.setText(I18n.t("common.yes"));
                    label.setForeground(isSelected ? table.getSelectionForeground() : OK_GREEN);
                    label.setIcon(TpvIconFactory.check(14, isSelected ? table.getSelectionForeground() : OK_GREEN));
                    label.setIconTextGap(5);

                } else if ("No".equalsIgnoreCase(text) || "Inactivo".equalsIgnoreCase(text)) {
                    label.setText(I18n.t("common.no"));
                    label.setForeground(isSelected ? table.getSelectionForeground() : InformeUiTheme.DANGER);
                    label.setIcon(TpvIconFactory.cancel(14, isSelected ? table.getSelectionForeground() : InformeUiTheme.DANGER));
                    label.setIconTextGap(5);

                } else {
                    label.setIcon(null);
                    label.setForeground(isSelected ? table.getSelectionForeground() : InformeUiTheme.TEXT_PRIMARY);
                }

                return label;
            }
        };

        if (tblEmpleados.getColumnModel().getColumnCount() >= 8) {
            tblEmpleados.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);
            tblEmpleados.getColumnModel().getColumn(6).setCellRenderer(statusRenderer);
            tblEmpleados.getColumnModel().getColumn(7).setCellRenderer(statusRenderer);
        }
    }

    private JPanel buildActionsPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        btnEditar = new JButton(I18n.t("common.edit"));
        btnActivarDesactivar = new JButton(I18n.t("employees.management.toggleActive"));
        btnResetPin = new JButton(I18n.t("employees.menu.resetPin.title"));
        btnVerFichajes = new JButton(I18n.t("employees.management.viewClockRecords"));

        InformeUiTheme.styleSecondaryButton(btnEditar);
        InformeUiTheme.styleSecondaryButton(btnActivarDesactivar);
        InformeUiTheme.styleSecondaryButton(btnResetPin);
        InformeUiTheme.styleSecondaryButton(btnVerFichajes);

        btnEditar.setIcon(TpvIconFactory.idCard(18, InformeUiTheme.TEXT_PRIMARY));
        btnEditar.setIconTextGap(8);

        btnActivarDesactivar.setIcon(TpvIconFactory.check(18, InformeUiTheme.TEXT_PRIMARY));
        btnActivarDesactivar.setIconTextGap(8);

        btnResetPin.setIcon(TpvIconFactory.key(18, InformeUiTheme.TEXT_PRIMARY));
        btnResetPin.setIconTextGap(8);

        btnVerFichajes.setIcon(TpvIconFactory.clock(18, InformeUiTheme.TEXT_PRIMARY));
        btnVerFichajes.setIconTextGap(8);

        btnEditar.addActionListener(e -> onEditarEmpleado());
        btnActivarDesactivar.addActionListener(e -> onActivarDesactivarEmpleado());
        btnResetPin.addActionListener(e -> onResetPinEmpleado());
        btnVerFichajes.addActionListener(e -> onVerFichajes());

        panel.add(btnEditar);
        panel.add(btnActivarDesactivar);
        panel.add(btnResetPin);
        panel.add(btnVerFichajes);

        return panel;
    }

    private JPanel buildDetailPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 10));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("employees.management.detail.title"));
        title.setIcon(TpvIconFactory.eye(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        JPanel form = transparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblNombreValor = createValueLabel();
        lblUsuarioValor = createValueLabel();
        lblRolValor = createValueLabel();
        lblSucursalValor = createValueLabel();
        lblActivoValor = createValueLabel();
        lblFechaCreacionValor = createValueLabel();
        lblFichajeActualValor = createValueLabel();
        lblSesionCajaValor = createValueLabel();
        lblCajaActualValor = createValueLabel();
        lblUltimaActividadValor = createValueLabel();

        txtObservaciones = new JTextArea(5, 20);
        txtObservaciones.setEditable(false);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(InformeUiTheme.CARD_BG_2);
        txtObservaciones.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtObservaciones.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtObservaciones.setBorder(InformeUiTheme.createInnerCardBorder());
        txtObservaciones.setFont(InformeUiTheme.FONT_BODY);

        int y = 0;

        addDetailRow(form, gbc, y++, I18n.t("employee.form.name"), TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY), lblNombreValor);
        addDetailRow(form, gbc, y++, I18n.t("employee.form.username"), TpvIconFactory.idCard(16, InformeUiTheme.TEXT_SECONDARY), lblUsuarioValor);
        addDetailRow(form, gbc, y++, I18n.t("employee.form.role"), TpvIconFactory.shield(16, InformeUiTheme.TEXT_SECONDARY), lblRolValor);
        addDetailRow(form, gbc, y++, I18n.t("employee.form.branch"), TpvIconFactory.branch(16, InformeUiTheme.TEXT_SECONDARY), lblSucursalValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.active"), TpvIconFactory.check(16, InformeUiTheme.TEXT_SECONDARY), lblActivoValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.createdAt"), TpvIconFactory.calendar(16, InformeUiTheme.TEXT_SECONDARY), lblFechaCreacionValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.currentClock"), TpvIconFactory.clock(16, InformeUiTheme.TEXT_SECONDARY), lblFichajeActualValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.cashSession"), TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY), lblSesionCajaValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.currentCashBox"), TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY), lblCajaActualValor);
        addDetailRow(form, gbc, y++, I18n.t("employees.management.detail.lastActivity"), TpvIconFactory.history(16, InformeUiTheme.TEXT_SECONDARY), lblUltimaActividadValor);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;

        JLabel obsLabel = createFieldLabelWithIcon(
                I18n.t("employees.management.detail.notes"),
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

    private void configurarRendererEstado() {
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

                String key = value == null ? STATUS_ALL : value.toString();

                switch (key) {
                    case STATUS_ACTIVE:
                        label.setText(I18n.t("employees.status.active"));
                        label.setIcon(TpvIconFactory.check(16, isSelected ? Color.WHITE : OK_GREEN));
                        break;

                    case STATUS_INACTIVE:
                        label.setText(I18n.t("employees.status.inactive"));
                        label.setIcon(TpvIconFactory.cancel(16, isSelected ? Color.WHITE : InformeUiTheme.DANGER));
                        break;

                    case STATUS_ALL:
                    default:
                        label.setText(I18n.t("employees.status.all"));
                        label.setIcon(TpvIconFactory.filter(16, isSelected ? Color.WHITE : InformeUiTheme.TEXT_SECONDARY));
                        break;
                }

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

    private void cargarCombos() {
        cmbRol.removeAllItems();
        cmbRol.addItem(I18n.t("employees.role.all"));

        List<Rol> roles = services.usuarioService.getRoles();
        for (Rol rol : roles) {
            cmbRol.addItem(rol);
        }
    }

    private void cargarTabla() {
        EmpleadoFiltroDTO filtro = leerFiltroActual();
        List<EmpleadoRowDTO> rows = services.usuarioService.buscarEmpleados(filtro);
        tableModel.setRows(rows);

        configurarColumnasTabla();
        configurarRendererTabla();

        if (rows.isEmpty()) {
            empleadoSeleccionado = null;
            limpiarDetalle();
        } else if (tblEmpleados.getRowCount() > 0) {
            tblEmpleados.setRowSelectionInterval(0, 0);
        }
    }

    private EmpleadoFiltroDTO leerFiltroActual() {
        EmpleadoFiltroDTO filtro = new EmpleadoFiltroDTO();
        filtro.setTextoBusqueda(txtBuscar.getText());

        Object rolSeleccionado = cmbRol.getSelectedItem();
        if (rolSeleccionado instanceof Rol rol) {
            filtro.setIdRol(rol.getIdRol());
        }

        String estado = (String) cmbEstado.getSelectedItem();

        if (STATUS_ACTIVE.equalsIgnoreCase(estado)) {
            filtro.setSoloActivos(Boolean.TRUE);

        } else if (STATUS_INACTIVE.equalsIgnoreCase(estado)) {
            filtro.setSoloActivos(Boolean.FALSE);

        } else {
            filtro.setSoloActivos(null);
        }

        if (AppContext.hasTerminalContext()) {
            filtro.setIdSucursal(AppContext.getIdSucursal());
        }

        filtro.setSoloConFichajeAbierto(chkSoloFichados.isSelected());
        filtro.setSoloConCajaAbierta(chkSoloCajaAbierta.isSelected());

        return filtro;
    }

    private void onEmpleadoSeleccionado() {
        int viewRow = tblEmpleados.getSelectedRow();

        if (viewRow < 0) {
            empleadoSeleccionado = null;
            limpiarDetalle();
            return;
        }

        int modelRow = tblEmpleados.convertRowIndexToModel(viewRow);
        empleadoSeleccionado = tableModel.getRow(modelRow);

        if (empleadoSeleccionado != null) {
            cargarDetalleEmpleado(empleadoSeleccionado.getIdUsuario());
        }
    }

    private void cargarDetalleEmpleado(int idUsuario) {
        Optional<EmpleadoDetalleDTO> opt = services.usuarioService.getDetalleEmpleado(idUsuario);

        if (opt.isEmpty()) {
            limpiarDetalle();
            return;
        }

        EmpleadoDetalleDTO d = opt.get();

        lblNombreValor.setText(safe(d.getNombre()));
        lblUsuarioValor.setText(safe(d.getUsuario()));
        lblRolValor.setText(safe(d.getNombreRol()));
        lblSucursalValor.setText(safe(d.getNombreSucursal()));
        setEstadoActivoDetalle(d.isActivo());
        setFichajeDetalle(d.isFichajeAbierto());
        setSesionCajaDetalle(d.isSesionCajaAbierta());

        lblFechaCreacionValor.setText(formatDateTime(d.getFechaCreacion()));
        lblCajaActualValor.setText(safe(d.getNombreCajaActualTexto()));
        lblUltimaActividadValor.setText(formatDateTime(d.getUltimaActividad()));
        txtObservaciones.setText(safe(d.getObservacionesOperativas()));
    }

    private void setEstadoActivoDetalle(boolean activo) {
        if (activo) {
            lblActivoValor.setText(I18n.t("employees.status.active"));
            lblActivoValor.setForeground(OK_GREEN);
            lblActivoValor.setIcon(TpvIconFactory.check(15, OK_GREEN));
        } else {
            lblActivoValor.setText(I18n.t("employees.status.inactive"));
            lblActivoValor.setForeground(InformeUiTheme.DANGER);
            lblActivoValor.setIcon(TpvIconFactory.cancel(15, InformeUiTheme.DANGER));
        }
        lblActivoValor.setIconTextGap(6);
    }

    private void setFichajeDetalle(boolean fichado) {
        if (fichado) {
            lblFichajeActualValor.setText(I18n.t("employees.clock.open"));
            lblFichajeActualValor.setForeground(OK_GREEN);
            lblFichajeActualValor.setIcon(TpvIconFactory.check(15, OK_GREEN));
        } else {
            lblFichajeActualValor.setText(I18n.t("employees.clock.closed"));
            lblFichajeActualValor.setForeground(InformeUiTheme.TEXT_PRIMARY);
            lblFichajeActualValor.setIcon(TpvIconFactory.clock(15, InformeUiTheme.TEXT_SECONDARY));
        }
        lblFichajeActualValor.setIconTextGap(6);
    }

    private void setSesionCajaDetalle(boolean abierta) {
        if (abierta) {
            lblSesionCajaValor.setText(I18n.t("employees.cashSession.open"));
            lblSesionCajaValor.setForeground(OK_GREEN);
            lblSesionCajaValor.setIcon(TpvIconFactory.check(15, OK_GREEN));
        } else {
            lblSesionCajaValor.setText(I18n.t("employees.cashSession.closed"));
            lblSesionCajaValor.setForeground(InformeUiTheme.TEXT_PRIMARY);
            lblSesionCajaValor.setIcon(TpvIconFactory.cashRegister(15, InformeUiTheme.TEXT_SECONDARY));
        }
        lblSesionCajaValor.setIconTextGap(6);
    }

    private void onNuevoEmpleado() {
        EmpleadoFormDialog dialog = new EmpleadoFormDialog(this, services, null);

        if (dialog.showDialog()) {
            refrescarTodo();
        }
    }

    private void onEditarEmpleado() {
        if (!checkEmpleadoSeleccionado()) {
            return;
        }

        EmpleadoFormDialog dialog = new EmpleadoFormDialog(
                this,
                services,
                empleadoSeleccionado.getIdUsuario()
        );

        if (dialog.showDialog()) {
            refrescarTodo();
        }
    }

    private void onActivarDesactivarEmpleado() {
        if (!checkEmpleadoSeleccionado()) {
            return;
        }

        boolean nuevoEstado = !empleadoSeleccionado.isActivo();

        String accion = nuevoEstado
                ? I18n.t("employees.action.activate")
                : I18n.t("employees.action.deactivate");

        boolean confirm = TpvDialogUtils.confirm(
                this,
                I18n.t("employees.management.confirmStatusTitle"),
                I18n.t("employees.management.confirmStatusMessage", accion)
        );

        if (!confirm) {
            return;
        }

        try {
            services.usuarioService.cambiarEstadoActivo(
                    empleadoSeleccionado.getIdUsuario(),
                    nuevoEstado,
                    AppContext.getUsuarioId(),
                    AppContext.getIdSucursal()
            );

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("employees.management.statusUpdatedTitle"),
                    I18n.t("employees.management.statusUpdatedMessage")
            );

            refrescarTodo();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("employees.management.statusUpdateErrorTitle"),
                    ex.getMessage()
            );
        }
    }

    private void onResetPinEmpleado() {
        if (!checkEmpleadoSeleccionado()) {
            return;
        }

        ResetPinEmpleadoDialog dialog = new ResetPinEmpleadoDialog(
                this,
                services,
                empleadoSeleccionado.getIdUsuario()
        );

        if (dialog.showDialog()) {
            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("employees.pinUpdated.title"),
                    I18n.t("employees.pinUpdated.message")
            );
        }
    }

    private void onVerFichajes() {
        FichajesEmpleadosFrame frame = new FichajesEmpleadosFrame(
                onLogoutNavigate,
                () -> this.setVisible(true),
                services
        );

        frame.setVisible(true);
        this.setVisible(false);
    }

    private void refrescarTodo() {
        cargarTabla();

        if (empleadoSeleccionado != null) {
            cargarDetalleEmpleado(empleadoSeleccionado.getIdUsuario());
        }
    }

    private void limpiarDetalle() {
        lblNombreValor.setText("-");
        lblUsuarioValor.setText("-");
        lblRolValor.setText("-");
        lblSucursalValor.setText("-");
        lblActivoValor.setText("-");
        lblActivoValor.setIcon(null);
        lblFechaCreacionValor.setText("-");
        lblFichajeActualValor.setText("-");
        lblFichajeActualValor.setIcon(null);
        lblSesionCajaValor.setText("-");
        lblSesionCajaValor.setIcon(null);
        lblCajaActualValor.setText("-");
        lblUltimaActividadValor.setText("-");
        txtObservaciones.setText("");
    }

    private boolean checkEmpleadoSeleccionado() {
        if (empleadoSeleccionado == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("employees.management.noSelectionTitle"),
                    I18n.t("employees.management.noSelectionMessage")
            );
            return false;
        }

        return true;
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
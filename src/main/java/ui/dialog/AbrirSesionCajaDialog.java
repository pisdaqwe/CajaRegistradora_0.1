package ui.dialog;

import dtoS.CajaEstadoDTO;
import dtoS.FichajeActivoDTO;
import service.AppServices;
import ui.common.TpvDialogUtils;
import ui.table.EmpleadosFichadosTableModel;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class AbrirSesionCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    private static final Color OK_GREEN = new Color(46, 125, 50);
    private static final Color SELECTED_ORANGE = new Color(189, 110, 65);
    private static final Color OCCUPIED_RED = new Color(134, 58, 58);
    private static final Color DISABLED_GRAY = new Color(85, 85, 85);
    private static final Color DISABLED_TEXT = new Color(170, 170, 170);

    private final AppServices services;

    private JTable tablaEmpleados;
    private EmpleadosFichadosTableModel tableModel;
    private TableRowSorter<EmpleadosFichadosTableModel> sorter;
    private JLabel lblTotalEmpleados;

    private JPanel panelCajas;
    private ButtonGroup grupoCajas;

    private ButtonGroup grupoImportes;
    private JTextField txtImporteCustom;

    private JLabel lblEmpleadoSeleccionado;
    private JLabel lblCajaSeleccionada;
    private JLabel lblImporteSeleccionado;
    private JLabel lblEstadoResumen;
    private JLabel lblEstadoFooter;

    private JButton btnConfirmar;

    private FichajeActivoDTO empleadoSeleccionado;
    private CajaEstadoDTO cajaSeleccionada;
    private BigDecimal importeSeleccionado;

    public AbrirSesionCajaDialog(Window owner, AppServices services) {
        super(owner, I18n.t("cashOpen.title"), ModalityType.APPLICATION_MODAL);
        this.services = services;

        buildUI();
        cargarEmpleadosFichados();
        cargarCajas();
        actualizarResumenYEstado();

        setSize(1180, 850);
        setMinimumSize(new Dimension(1050, 720));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel wrapper = transparentPanel(new BorderLayout(0, 10));

        JPanel textPanel = transparentPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(TpvIconFactory.cashRegister(40, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel texts = transparentPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("cashOpen.header.title"));
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("cashOpen.header.subtitle"));
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.ACCENT_GOLD);

        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(subtitle);

        textPanel.add(icon, BorderLayout.WEST);
        textPanel.add(texts, BorderLayout.CENTER);

        JPanel pasos = InformeUiTheme.createCardPanel(new GridLayout(1, 4, 10, 0));
        pasos.add(createPasoLabel(I18n.t("cashOpen.step.employee"), TpvIconFactory.user(16, InformeUiTheme.ACCENT_GOLD)));
        pasos.add(createPasoLabel(I18n.t("cashOpen.step.cashBox"), TpvIconFactory.cashRegister(16, InformeUiTheme.ACCENT_GOLD)));
        pasos.add(createPasoLabel(I18n.t("cashOpen.step.amount"), TpvIconFactory.cashRegister(16, InformeUiTheme.ACCENT_GOLD)));
        pasos.add(createPasoLabel(I18n.t("cashOpen.step.confirm"), TpvIconFactory.check(16, InformeUiTheme.ACCENT_GOLD)));

        wrapper.add(textPanel, BorderLayout.NORTH);
        wrapper.add(pasos, BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel createPasoLabel(String text, Icon icon) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(InformeUiTheme.FONT_LABEL);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lbl.setIcon(icon);
        lbl.setIconTextGap(6);
        return lbl;
    }

    private JComponent buildCenter() {
        JPanel left = buildEmpleadoPanel();
        JPanel right = buildRightPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.52);
        split.setDividerLocation(560);
        split.setBorder(null);
        split.setOpaque(false);

        return split;
    }

    private JPanel buildEmpleadoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(10, 10));

        JPanel top = transparentPanel(new BorderLayout(14, 0));

        JPanel titlePanel = transparentPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashOpen.clockedEmployees.title"));
        title.setIcon(TpvIconFactory.users(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        lblTotalEmpleados = new JLabel(I18n.t("cashOpen.clockedEmployees.count", 0));
        lblTotalEmpleados.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblTotalEmpleados.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblTotalEmpleados);

        JTextField txtBuscar = new JTextField(18);
        InformeUiTheme.styleTextField(txtBuscar);
        txtBuscar.setPreferredSize(new Dimension(230, 38));

        JLabel lblBuscar = InformeUiTheme.createFieldLabel(I18n.t("common.search"));
        lblBuscar.setIcon(TpvIconFactory.search(16, InformeUiTheme.TEXT_SECONDARY));
        lblBuscar.setIconTextGap(6);

        JPanel searchPanel = transparentPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
        searchPanel.add(lblBuscar, BorderLayout.WEST);
        searchPanel.add(txtBuscar, BorderLayout.CENTER);

        top.add(titlePanel, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        tableModel = new EmpleadosFichadosTableModel();
        tablaEmpleados = new JTable(tableModel);
        InformeUiTheme.styleTable(tablaEmpleados);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.setRowHeight(34);

        configurarTablaEmpleados();

        sorter = new TableRowSorter<>(tableModel);
        tablaEmpleados.setRowSorter(sorter);

        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int filaVista = tablaEmpleados.getSelectedRow();
            if (filaVista >= 0) {
                int filaModelo = tablaEmpleados.convertRowIndexToModel(filaVista);
                empleadoSeleccionado = tableModel.getEmpleadoAt(filaModelo);
            } else {
                empleadoSeleccionado = null;
            }

            actualizarResumenYEstado();
        });

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }

            private void filtrar() {
                String texto = txtBuscar.getText().trim().toLowerCase();

                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                    actualizarTotalVisible();
                    return;
                }

                sorter.setRowFilter(new RowFilter<EmpleadosFichadosTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends EmpleadosFichadosTableModel, ? extends Integer> entry) {
                        String nombre = entry.getStringValue(1);
                        return nombre != null && nombre.toLowerCase().contains(texto);
                    }
                });

                actualizarTotalVisible();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEmpleados);
        InformeUiTheme.styleScrollPane(scroll);
        scroll.setPreferredSize(new Dimension(520, 430));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void configurarTablaEmpleados() {
        if (tablaEmpleados.getColumnModel().getColumnCount() < 4) {
            return;
        }

        tablaEmpleados.getColumnModel().getColumn(0).setHeaderValue(I18n.t("cashOpen.clockedEmployees.table.id"));
        tablaEmpleados.getColumnModel().getColumn(1).setHeaderValue(I18n.t("cashOpen.clockedEmployees.table.employee"));
        tablaEmpleados.getColumnModel().getColumn(2).setHeaderValue(I18n.t("cashOpen.clockedEmployees.table.entry"));
        tablaEmpleados.getColumnModel().getColumn(3).setHeaderValue(I18n.t("cashOpen.clockedEmployees.table.status"));

        tablaEmpleados.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaEmpleados.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaEmpleados.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaEmpleados.getColumnModel().getColumn(3).setPreferredWidth(110);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tablaEmpleados.getColumnModel().getColumn(0).setCellRenderer(center);

        tablaEmpleados.getTableHeader().repaint();
    }

    private JPanel buildRightPanel() {
        JPanel panel = transparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.weighty = 0.22;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(buildResumenPanel(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.48;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(buildCajasPanel(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.30;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(buildImportePanel(), gbc);

        return panel;
    }

    private JPanel buildResumenPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashOpen.summary.title"));
        title.setIcon(TpvIconFactory.info(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        JPanel content = transparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblEmpleadoSeleccionado = createResumenValueLabel();
        lblCajaSeleccionada = createResumenValueLabel();
        lblImporteSeleccionado = createResumenValueLabel();
        lblEstadoResumen = createResumenValueLabel();

        int y = 0;
        addResumenRow(content, gbc, y++, I18n.t("cashOpen.summary.employee"), TpvIconFactory.user(16, InformeUiTheme.TEXT_SECONDARY), lblEmpleadoSeleccionado);
        addResumenRow(content, gbc, y++, I18n.t("cashOpen.summary.cashBox"), TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY), lblCajaSeleccionada);
        addResumenRow(content, gbc, y++, I18n.t("cashOpen.summary.amount"), TpvIconFactory.cashRegister(16, InformeUiTheme.TEXT_SECONDARY), lblImporteSeleccionado);
        addResumenRow(content, gbc, y, I18n.t("cashOpen.summary.status"), TpvIconFactory.warning(16, InformeUiTheme.TEXT_SECONDARY), lblEstadoResumen);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createResumenValueLabel() {
        JLabel lbl = new JLabel("—");
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private void addResumenRow(JPanel panel, GridBagConstraints gbc, int row, String label, Icon icon, JLabel value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel lbl = InformeUiTheme.createFieldLabel(label);
        lbl.setIcon(icon);
        lbl.setIconTextGap(6);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(value, gbc);
    }

   
    private JPanel buildCajasPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashOpen.cashBoxes.title"));
        title.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel();
        panelCajas.setOpaque(false);
        panelCajas.setBorder(new EmptyBorder(4, 4, 4, 4));

        wrapper.add(panelCajas, BorderLayout.CENTER);

        return wrapper;
    }

    private void cargarCajas() {
        panelCajas.removeAll();
        grupoCajas = new ButtonGroup();

        List<CajaEstadoDTO> cajas = services.sesionCajaService.getEstadoCajas();

        int total = cajas.size();
        int columnas = calcularColumnasCajas(total);
        int filas = (int) Math.ceil(total / (double) columnas);

        panelCajas.setLayout(new GridLayout(filas, columnas, 10, 10));

        for (CajaEstadoDTO caja : cajas) {
            JToggleButton btn = crearBotonCaja(caja, columnas);
            grupoCajas.add(btn);
            panelCajas.add(btn);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }
    private int calcularColumnasCajas(int totalCajas) {
        if (totalCajas <= 1) {
            return 1;
        }

        if (totalCajas == 2) {
            return 2;
        }

        if (totalCajas == 3) {
            return 3;
        }

        return 2;
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja, int columnas) {
        JToggleButton btn = new JToggleButton();
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, columnas >= 3 ? 10 : 9));
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(InformeUiTheme.createInnerCardBorder());
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setIconTextGap(4);

        if (columnas >= 3) {
            btn.setPreferredSize(new Dimension(150, 70));
            btn.setMinimumSize(new Dimension(140, 68));
        } else {
            btn.setPreferredSize(new Dimension(230, 76));
            btn.setMinimumSize(new Dimension(200, 72));
        }

        String nombreCaja = safe(caja.getNombreCaja());
        String empleado = safe(caja.getEmpleadoAsignado());

        if (!caja.isOperativa()) {
            btn.setText(
                    "<html><center><b>" + nombreCaja + "</b><br>"
                            + I18n.t("cashOpen.cashBoxes.status.outOfService")
                            + "</center></html>"
            );
            btn.setBackground(DISABLED_GRAY);
            btn.setIcon(TpvIconFactory.cancel(16, Color.WHITE));
            btn.setEnabled(false);

        } else if (caja.isOcupada()) {
            btn.setText(
                    "<html><center><b>" + nombreCaja + "</b><br>"
                            + I18n.t("cashOpen.cashBoxes.status.occupied")
                            + (empleado.isBlank()
                                    ? ""
                                    : "<br><span style='font-size:8px;'>(" + empleado + ")</span>")
                            + "</center></html>"
            );
            btn.setBackground(OCCUPIED_RED);
            btn.setIcon(TpvIconFactory.warning(16, Color.WHITE));
            btn.setEnabled(false);

        } else {
            btn.setText(
                    "<html><center><b>" + nombreCaja + "</b><br>"
                            + I18n.t("cashOpen.cashBoxes.status.available")
                            + "</center></html>"
            );
            btn.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            btn.setIcon(TpvIconFactory.check(16, Color.WHITE));
            btn.setEnabled(true);

            btn.addActionListener(e -> {
                cajaSeleccionada = caja;
                remarcarCajaSeleccionada(btn);
                actualizarResumenYEstado();
            });
        }

        btn.setToolTipText(nombreCaja);

        return btn;
    }

    private void remarcarCajaSeleccionada(JToggleButton botonSeleccionado) {
        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn && btn.isEnabled()) {
                btn.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            }
        }

        botonSeleccionado.setBackground(SELECTED_ORANGE);
    }

    private JPanel buildImportePanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle(I18n.t("cashOpen.amount.title"));
        title.setIcon(TpvIconFactory.cashRegister(20, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);

        panel.add(title, BorderLayout.NORTH);

        JPanel content = transparentPanel(new BorderLayout(0, 12));

        grupoImportes = new ButtonGroup();

        JPanel gridImportes = new JPanel(new GridLayout(2, 2, 10, 10));
        gridImportes.setOpaque(false);

        gridImportes.add(createImporteButton(new BigDecimal("100")));
        gridImportes.add(createImporteButton(new BigDecimal("200")));
        gridImportes.add(createImporteButton(new BigDecimal("250")));
        gridImportes.add(createImporteButton(new BigDecimal("300")));

        JPanel custom = transparentPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JLabel lblCustom = InformeUiTheme.createFieldLabel(I18n.t("cashOpen.amount.manual"));
        lblCustom.setIcon(TpvIconFactory.key(16, InformeUiTheme.TEXT_SECONDARY));
        lblCustom.setIconTextGap(6);

        txtImporteCustom = new JTextField(10);
        InformeUiTheme.styleTextField(txtImporteCustom);
        txtImporteCustom.setPreferredSize(new Dimension(130, 38));

        txtImporteCustom.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onImporteCustomChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onImporteCustomChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onImporteCustomChanged();
            }
        });

        custom.add(lblCustom);
        custom.add(txtImporteCustom);

        content.add(gridImportes, BorderLayout.CENTER);
        content.add(custom, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JToggleButton createImporteButton(BigDecimal importe) {
        JToggleButton btn = new JToggleButton(MONEY.format(importe) + " €");
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(InformeUiTheme.CARD_BG_2);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setBorder(InformeUiTheme.createInnerCardBorder());
        btn.setIcon(TpvIconFactory.cashRegister(15, InformeUiTheme.ACCENT_GOLD));
        btn.setIconTextGap(8);
        btn.setPreferredSize(new Dimension(100, 48));

        btn.addActionListener(e -> {
            txtImporteCustom.setText("");
            importeSeleccionado = importe;
            actualizarResumenYEstado();
        });

        grupoImportes.add(btn);
        return btn;
    }

    private void onImporteCustomChanged() {
        String texto = txtImporteCustom.getText().trim().replace(",", ".");

        if (texto.isEmpty()) {
            importeSeleccionado = null;
            grupoImportes.clearSelection();
            actualizarResumenYEstado();
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(texto);

            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }

            importeSeleccionado = valor;
            grupoImportes.clearSelection();

        } catch (NumberFormatException ex) {
            importeSeleccionado = null;
        }

        actualizarResumenYEstado();
    }

    private JComponent buildFooter() {
        JPanel footer = transparentPanel(new BorderLayout());

        JPanel left = transparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        lblEstadoFooter = new JLabel(I18n.t("cashOpen.status.selectEmployee"));
        lblEstadoFooter.setFont(InformeUiTheme.FONT_BODY);
        lblEstadoFooter.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblEstadoFooter.setIcon(TpvIconFactory.info(18, InformeUiTheme.TEXT_SECONDARY));
        lblEstadoFooter.setIconTextGap(8);
        left.add(lblEstadoFooter);

        JPanel right = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setPreferredSize(new Dimension(150, 48));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton(I18n.t("cashOpen.confirmButton"));
        InformeUiTheme.stylePrimaryButton(btnConfirmar);
        btnConfirmar.setPreferredSize(new Dimension(230, 48));
        btnConfirmar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnConfirmar.setIconTextGap(8);
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarAperturaCaja());

        right.add(btnCancelar);
        right.add(btnConfirmar);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);

        return footer;
    }

    private void cargarEmpleadosFichados() {
        List<FichajeActivoDTO> datos = services.fichajeService.findFichajesActivos();
        tableModel.setDatos(datos);
        configurarTablaEmpleados();
        actualizarTotalVisible();
    }

    private void actualizarTotalVisible() {
        int total = tablaEmpleados.getRowCount();

        lblTotalEmpleados.setText(
                total == 1
                        ? I18n.t("cashOpen.clockedEmployees.countOne")
                        : I18n.t("cashOpen.clockedEmployees.count", total)
        );
    }

    private void actualizarResumenYEstado() {
        lblEmpleadoSeleccionado.setText(
                empleadoSeleccionado != null
                        ? empleadoSeleccionado.getNombreEmpleado() + " (" + empleadoSeleccionado.getIdUsuario() + ")"
                        : "—"
        );

        lblCajaSeleccionada.setText(
                cajaSeleccionada != null
                        ? cajaSeleccionada.getNombreCaja()
                        : "—"
        );

        lblImporteSeleccionado.setText(
                importeSeleccionado != null
                        ? MONEY.format(importeSeleccionado) + " €"
                        : "—"
        );

        String estado;
        Icon estadoIcon;
        Color estadoColor;

        if (empleadoSeleccionado == null) {
            estado = I18n.t("cashOpen.status.selectEmployee");
            estadoIcon = TpvIconFactory.info(16, InformeUiTheme.TEXT_SECONDARY);
            estadoColor = InformeUiTheme.TEXT_SECONDARY;

        } else if (cajaSeleccionada == null) {
            estado = I18n.t("cashOpen.status.selectCashBox");
            estadoIcon = TpvIconFactory.warning(16, InformeUiTheme.ACCENT_GOLD);
            estadoColor = InformeUiTheme.TEXT_SECONDARY;

        } else if (importeSeleccionado == null) {
            estado = I18n.t("cashOpen.status.selectAmount");
            estadoIcon = TpvIconFactory.warning(16, InformeUiTheme.ACCENT_GOLD);
            estadoColor = InformeUiTheme.TEXT_SECONDARY;

        } else {
            estado = I18n.t("cashOpen.status.ready");
            estadoIcon = TpvIconFactory.check(16, OK_GREEN);
            estadoColor = OK_GREEN;
        }

        lblEstadoResumen.setText(estado);
        lblEstadoResumen.setIcon(estadoIcon);
        lblEstadoResumen.setIconTextGap(6);
        lblEstadoResumen.setForeground(estadoColor);

        lblEstadoFooter.setText(estado);
        lblEstadoFooter.setIcon(estadoIcon);
        lblEstadoFooter.setIconTextGap(8);
        lblEstadoFooter.setForeground(estadoColor);

        boolean ok = empleadoSeleccionado != null
                && cajaSeleccionada != null
                && importeSeleccionado != null
                && importeSeleccionado.compareTo(BigDecimal.ZERO) >= 0;

        btnConfirmar.setEnabled(ok);

        if (ok) {
            btnConfirmar.setForeground(Color.WHITE);
            btnConfirmar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        } else {
            btnConfirmar.setForeground(DISABLED_TEXT);
            btnConfirmar.setIcon(TpvIconFactory.check(18, DISABLED_TEXT));
        }
    }

    private void confirmarAperturaCaja() {
        if (empleadoSeleccionado == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashOpen.title"),
                    I18n.t("cashOpen.validation.employee")
            );
            return;
        }

        if (cajaSeleccionada == null) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashOpen.title"),
                    I18n.t("cashOpen.validation.cashBox")
            );
            return;
        }

        if (importeSeleccionado == null || importeSeleccionado.compareTo(BigDecimal.ZERO) < 0) {
            TpvDialogUtils.showWarning(
                    this,
                    I18n.t("cashOpen.title"),
                    I18n.t("cashOpen.validation.amount")
            );
            return;
        }

        try {
            services.cajaFacade.abrirSesionCaja(
                    empleadoSeleccionado,
                    cajaSeleccionada,
                    importeSeleccionado
            );

            TpvDialogUtils.showInfo(
                    this,
                    I18n.t("cashOpen.success.title"),
                    I18n.t("cashOpen.success.message")
            );

            dispose();

        } catch (Exception ex) {
            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashOpen.error.title"),
                    ex.getMessage()
            );
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
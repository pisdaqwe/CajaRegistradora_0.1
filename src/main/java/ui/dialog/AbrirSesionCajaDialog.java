package ui.dialog;

import dtoS.CajaEstadoDTO;
import dtoS.FichajeActivoDTO;
import service.AppServices;
import ui.common.InformeUiTheme;
import ui.table.EmpleadosFichadosTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class AbrirSesionCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

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
        super(owner, "Abrir sesión de caja", ModalityType.APPLICATION_MODAL);
        this.services = services;

        buildUI();
        cargarEmpleadosFichados();
        cargarCajas();
        actualizarResumenYEstado();

        setSize(1120, 960);
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
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Abrir sesión de caja");
        title.setFont(InformeUiTheme.FONT_TITLE);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Selecciona empleado, caja e importe inicial para abrir la sesión");
        subtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        subtitle.setForeground(InformeUiTheme.ACCENT_GOLD);

        textPanel.add(title);
        textPanel.add(subtitle);

        JPanel pasos = InformeUiTheme.createCardPanel(new GridLayout(1, 4, 10, 0));
        pasos.add(createPasoLabel("1. Empleado"));
        pasos.add(createPasoLabel("2. Caja"));
        pasos.add(createPasoLabel("3. Importe"));
        pasos.add(createPasoLabel("4. Confirmar"));

        wrapper.add(textPanel, BorderLayout.NORTH);
        wrapper.add(pasos, BorderLayout.CENTER);

        return wrapper;
    }

    private JLabel createPasoLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(InformeUiTheme.FONT_LABEL);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private JComponent buildCenter() {
        JPanel left = buildEmpleadoPanel();
        JPanel right = buildRightPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.56);
        split.setDividerLocation(590);
        split.setBorder(null);

        return split;
    }

    private JPanel buildEmpleadoPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        JPanel leftTop = new JPanel(new GridLayout(2, 1, 0, 2));
        leftTop.setOpaque(false);

        JLabel title = InformeUiTheme.createSectionTitle("Empleados fichados");
        lblTotalEmpleados = new JLabel("0 empleados");
        lblTotalEmpleados.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblTotalEmpleados.setForeground(InformeUiTheme.TEXT_SECONDARY);

        leftTop.add(title);
        leftTop.add(lblTotalEmpleados);

        JTextField txtBuscar = new JTextField(20);
        InformeUiTheme.styleTextField(txtBuscar);

        top.add(leftTop, BorderLayout.WEST);
        top.add(txtBuscar, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        tableModel = new EmpleadosFichadosTableModel();
        tablaEmpleados = new JTable(tableModel);
        InformeUiTheme.styleTable(tablaEmpleados);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.setRowHeight(34);

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
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildResumenPanel());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildCajasPanel());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildImportePanel());

        return panel;
    }

    private JPanel buildResumenPanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Resumen de apertura");
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblEmpleadoSeleccionado = createResumenValueLabel();
        lblCajaSeleccionada = createResumenValueLabel();
        lblImporteSeleccionado = createResumenValueLabel();
        lblEstadoResumen = createResumenValueLabel();

        int y = 0;
        addResumenRow(content, gbc, y++, "Empleado:", lblEmpleadoSeleccionado);
        addResumenRow(content, gbc, y++, "Caja:", lblCajaSeleccionada);
        addResumenRow(content, gbc, y++, "Importe inicial:", lblImporteSeleccionado);
        addResumenRow(content, gbc, y, "Estado:", lblEstadoResumen);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createResumenValueLabel() {
        JLabel lbl = new JLabel("—");
        lbl.setFont(InformeUiTheme.FONT_BODY);
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return lbl;
    }

    private void addResumenRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(InformeUiTheme.createFieldLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(value, gbc);
    }

    private JPanel buildCajasPanel() {
        JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Cajas disponibles");
        wrapper.add(title, BorderLayout.NORTH);

        panelCajas = new JPanel(new GridLayout(0, 2, 10, 10));
        panelCajas.setOpaque(false);

        wrapper.add(panelCajas, BorderLayout.CENTER);
        return wrapper;
    }

    private void cargarCajas() {
        panelCajas.removeAll();
        grupoCajas = new ButtonGroup();
        cajaSeleccionada = null;

        List<CajaEstadoDTO> cajas = services.sesionCajaService.getEstadoCajas();

        for (CajaEstadoDTO caja : cajas) {
            JToggleButton btn = crearBotonCaja(caja);
            grupoCajas.add(btn);
            panelCajas.add(btn);
        }

        panelCajas.revalidate();
        panelCajas.repaint();
    }

    private JToggleButton crearBotonCaja(CajaEstadoDTO caja) {
        JToggleButton btn = new JToggleButton();
        btn.setFocusPainted(false);
        btn.setFont(InformeUiTheme.FONT_BODY);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 84));
        btn.setBorder(InformeUiTheme.createInnerCardBorder());

        String texto = "<html><center><b>" + safe(caja.getNombreCaja()) + "</b><br/>";

        if (!caja.isOperativa()) {
            texto += "Fuera de servicio</center></html>";
            btn.setBackground(new Color(85, 85, 85));
            btn.setEnabled(false);
        } else if (caja.isOcupada()) {
            texto += "Ocupada<br/>(" + safe(caja.getEmpleadoAsignado()) + ")</center></html>";
            btn.setBackground(new Color(134, 58, 58));
            btn.setEnabled(false);
        } else {
            texto += "Disponible</center></html>";
            btn.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            btn.setEnabled(true);

            btn.addActionListener(e -> {
                cajaSeleccionada = caja;
                remarcarCajaSeleccionada(btn);
                actualizarResumenYEstado();
            });
        }

        btn.setText(texto);
        return btn;
    }

    private void remarcarCajaSeleccionada(JToggleButton botonSeleccionado) {
        for (Component c : panelCajas.getComponents()) {
            if (c instanceof JToggleButton btn && btn.isEnabled()) {
                btn.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            }
        }
        botonSeleccionado.setBackground(new Color(189, 110, 65));
    }

    private JPanel buildImportePanel() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel title = InformeUiTheme.createSectionTitle("Importe inicial");
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        grupoImportes = new ButtonGroup();

        JPanel gridImportes = new JPanel(new GridLayout(2, 2, 10, 10));
        gridImportes.setOpaque(false);

        gridImportes.add(createImporteButton(new BigDecimal("100")));
        gridImportes.add(createImporteButton(new BigDecimal("200")));
        gridImportes.add(createImporteButton(new BigDecimal("250")));
        gridImportes.add(createImporteButton(new BigDecimal("300")));

        JPanel custom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        custom.setOpaque(false);

        JLabel lblCustom = InformeUiTheme.createFieldLabel("Importe manual:");
        txtImporteCustom = new JTextField(10);
        InformeUiTheme.styleTextField(txtImporteCustom);

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

        content.add(gridImportes);
        content.add(Box.createVerticalStrut(12));
        content.add(custom);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JToggleButton createImporteButton(BigDecimal importe) {
        JToggleButton btn = new JToggleButton(MONEY.format(importe) + " €");
        btn.setFocusPainted(false);
        btn.setFont(InformeUiTheme.FONT_BUTTON);
        btn.setBackground(InformeUiTheme.CARD_BG_2);
        btn.setForeground(InformeUiTheme.TEXT_PRIMARY);
        btn.setBorder(InformeUiTheme.createInnerCardBorder());

        btn.addActionListener(e -> {
            importeSeleccionado = importe;
            txtImporteCustom.setText("");
            actualizarResumenYEstado();
        });

        grupoImportes.add(btn);
        return btn;
    }

    private void onImporteCustomChanged() {
        String texto = txtImporteCustom.getText().trim();

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
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        lblEstadoFooter = new JLabel("Selecciona un empleado");
        lblEstadoFooter.setFont(InformeUiTheme.FONT_BODY);
        lblEstadoFooter.setForeground(InformeUiTheme.TEXT_SECONDARY);
        left.add(lblEstadoFooter);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        right.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar = new JButton("Abrir sesión de caja");
        InformeUiTheme.stylePrimaryButton(btnConfirmar);
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
        actualizarTotalVisible();
    }

    private void actualizarTotalVisible() {
        int total = tablaEmpleados.getRowCount();
        lblTotalEmpleados.setText(total + (total == 1 ? " empleado visible" : " empleados visibles"));
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
        if (empleadoSeleccionado == null) {
            estado = "Selecciona un empleado";
        } else if (cajaSeleccionada == null) {
            estado = "Selecciona una caja disponible";
        } else if (importeSeleccionado == null) {
            estado = "Indica un importe inicial";
        } else {
            estado = "Todo listo para abrir la sesión";
        }
        
        if (empleadoSeleccionado == null || cajaSeleccionada == null || importeSeleccionado == null) {
            lblEstadoResumen.setForeground(InformeUiTheme.TEXT_SECONDARY);
            lblEstadoFooter.setForeground(InformeUiTheme.TEXT_SECONDARY);
        } else {
            lblEstadoResumen.setForeground(new Color(105, 197, 125));
            lblEstadoFooter.setForeground(new Color(105, 197, 125));
        }
        
        lblEstadoResumen.setText(estado);
        lblEstadoFooter.setText(estado);

        boolean ok = empleadoSeleccionado != null
                && cajaSeleccionada != null
                && importeSeleccionado != null
                && importeSeleccionado.compareTo(BigDecimal.ZERO) >= 0;

        btnConfirmar.setEnabled(ok);
    }

    private void confirmarAperturaCaja() {
        if (empleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar un empleado fichado.",
                    "Abrir sesión",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cajaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar una caja disponible.",
                    "Abrir sesión",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (importeSeleccionado == null || importeSeleccionado.compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar un importe inicial válido.",
                    "Abrir sesión",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            services.cajaFacade.abrirSesionCaja(
                    empleadoSeleccionado,
                    cajaSeleccionada,
                    importeSeleccionado
            );

            JOptionPane.showMessageDialog(this,
                    "Sesión de caja abierta correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error al abrir la sesión de caja",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
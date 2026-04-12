package ui.dialog;

import app.AppContext;
import dtoS.RegistrarDevolucionItemRequest;
import dtoS.RegistrarDevolucionRequest;
import dtoS.RegistrarDevolucionResultDTO;
import dtoS.TicketDevolucionDTO;
import dtoS.TicketHoyRowDTO;
import dtoS.VentaItemParaDevolucionDTO;
import dtoS.VentaParaDevolucionDTO;
import service.AppServices;
import ui.table.DevolucionRowVM;
import ui.table.DevolucionTableModel;
import ui.table.VentasHoyDevolucionTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Diálogo principal de devoluciones.
 *
 * Ajustes realizados:
 * - tamaño responsive según pantalla
 * - split proporcional
 * - panel táctil rediseñado para que no se deforme
 * - combo de método reembolso con tamaño controlado
 * - mejor distribución de total / motivo / observaciones
 */
public class DevolucionesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =====================================================
    // 1) PALETA / ESTILO
    // =====================================================

    private static final Color BG_MAIN = new Color(14, 48, 35);
    private static final Color BG_PANEL = new Color(20, 67, 47);
    private static final Color BG_BUTTON = new Color(0, 92, 62);
    private static final Color BG_BUTTON_ALT = new Color(28, 84, 62);
    private static final Color BG_BUTTON_DANGER = new Color(120, 46, 46);
    private static final Color BG_INPUT = new Color(245, 245, 240);
    private static final Color TEXT_MAIN = new Color(245, 245, 240);
    private static final Color TEXT_SOFT = new Color(212, 223, 216);
    private static final Color BORDER = new Color(95, 145, 118);
    private static final Color HIGHLIGHT = new Color(165, 204, 183);

    // =====================================================
    // 2) DEPENDENCIAS
    // =====================================================

    private final AppServices services;

    // =====================================================
    // 3) COMPONENTES IZQUIERDA (VENTAS)
    // =====================================================

    private final JTextField txtBuscar = new JTextField();
    private final JButton btnBuscar = new JButton("BUSCAR");
    private final JButton btnResetHoy = new JButton("HOY");

    private final VentasHoyDevolucionTableModel ventasModel = new VentasHoyDevolucionTableModel();
    private final JTable tableVentas = new JTable(ventasModel);

    // =====================================================
    // 4) COMPONENTES DERECHA (CABECERA)
    // =====================================================

    private final JLabel lblVenta = new JLabel("-");
    private final JLabel lblFecha = new JLabel("-");
    private final JLabel lblPedido = new JLabel("-");
    private final JLabel lblServicio = new JLabel("-");
    private final JLabel lblPagoOriginal = new JLabel("-");
    private final JLabel lblTotalVenta = new JLabel("-");

    // =====================================================
    // 5) COMPONENTES DERECHA (LÍNEAS)
    // =====================================================

    private final DevolucionTableModel devolucionModel = new DevolucionTableModel();
    private final JTable tableItems = new JTable(devolucionModel);

    // =====================================================
    // 6) PANEL TÁCTIL DE LA LÍNEA SELECCIONADA
    // =====================================================

    private final JLabel lblLineaProducto = new JLabel("-");
    private final JLabel lblLineaTamano = new JLabel("-");
    private final JLabel lblLineaDisponible = new JLabel("-");
    private final JLabel lblLineaFinal = new JLabel("-");
    private final JButton btnMenos = new JButton("−");
    private final JLabel lblCantidadGrande = new JLabel("0", SwingConstants.CENTER);
    private final JButton btnMas = new JButton("+");
    private final JButton btnDevolverTodo = new JButton("DEVOLVER TODO");
    private final JToggleButton toggleReponeStock = new JToggleButton("REPONE STOCK: NO");

    // =====================================================
    // 7) DATOS DE LA DEVOLUCIÓN
    // =====================================================

    private final JComboBox<String> cmbMetodoReembolso = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA"});
    private final JTextField txtMotivo = new JTextField();
    private final JTextArea txtObservaciones = new JTextArea(3, 20);
    private final JLabel lblTotalDevolucion = new JLabel("0,00 €");

    private final JButton btnConfirmar = new JButton("CONFIRMAR DEVOLUCIÓN");
    private final JButton btnCerrar = new JButton("CERRAR");

    // =====================================================
    // 8) ESTADO
    // =====================================================

    private VentaParaDevolucionDTO ventaActual;

    // =====================================================
    // 9) CONSTRUCTOR
    // =====================================================

    public DevolucionesDialog(Window owner, AppServices services) {
        super(owner, "Devoluciones", ModalityType.APPLICATION_MODAL);

        this.services = services;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMainSplit(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        setContentPane(root);

        configureVentasTable();
        configureItemsTable();
        styleComponents();
        wireActions();

        loadVentasHoy();
        clearVentaSeleccionada();

        configureResponsiveSize(owner);
    }

    public void showDialog() {
        setVisible(true);
    }

    // =====================================================
    // 10) AJUSTE RESPONSIVE
    // =====================================================

    private void configureResponsiveSize(Window owner) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int screenW = screen.width;
        int screenH = screen.height;

        int targetW = (int) (screenW * 0.90);
        int targetH = (int) (screenH * 0.88);

        int minW = 1220;
        int minH = 760;

        int finalW = Math.max(minW, Math.min(targetW, screenW - 40));
        int finalH = Math.max(minH, Math.min(targetH, screenH - 80));

        setMinimumSize(new Dimension(minW, minH));
        setPreferredSize(new Dimension(finalW, finalH));
        setSize(finalW, finalH);
        setLocationRelativeTo(owner);
    }

    // =====================================================
    // 11) CONSTRUCCIÓN UI
    // =====================================================

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("DEVOLUCIONES");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel("Ventas de hoy a la izquierda · Ajuste táctil de líneas a la derecha");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SOFT);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(subtitle);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    private JComponent buildMainSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setResizeWeight(0.32);
        split.setBorder(BorderFactory.createEmptyBorder());

        SwingUtilities.invokeLater(() -> {
            int totalWidth = split.getWidth();
            if (totalWidth > 0) {
                split.setDividerLocation((int) (totalWidth * 0.32));
            }
        });

        return split;
    }

    private JComponent buildLeftPanel() {
        JPanel panel = createCardPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("VENTAS");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setOpaque(false);
        txtBuscar.setPreferredSize(new Dimension(220, 42));
        searchPanel.add(txtBuscar, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(btnBuscar);
        buttons.add(btnResetHoy);
        searchPanel.add(buttons, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(tableVentas);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);
        panel.add(buildResumenVenta(), BorderLayout.NORTH);
        panel.add(buildCenterRight(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildResumenVenta() {
        JPanel panel = createCardPanel(new GridLayout(2, 3, 12, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        panel.add(buildInfoBox("Venta", lblVenta));
        panel.add(buildInfoBox("Fecha", lblFecha));
        panel.add(buildInfoBox("Pedido", lblPedido));
        panel.add(buildInfoBox("Servicio", lblServicio));
        panel.add(buildInfoBox("Pago original", lblPagoOriginal));
        panel.add(buildInfoBox("Total venta", lblTotalVenta));

        return panel;
    }

    private JComponent buildCenterRight() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JComponent itemsPanel = buildItemsPanel();
        JComponent touchPanel = buildTouchSelectorPanel();
        touchPanel.setPreferredSize(new Dimension(0, 320));

        panel.add(itemsPanel, BorderLayout.CENTER);
        panel.add(touchPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildItemsPanel() {
        JPanel panel = createCardPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("LÍNEAS DEL PEDIDO");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        JScrollPane scroll = new JScrollPane(tableItems);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Panel rediseñado para evitar:
     * - combo gigantesco
     * - cajas deformadas
     * - columnas superpuestas
     */
    private JComponent buildTouchSelectorPanel() {
        JPanel panel = createCardPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("AJUSTE TÁCTIL DE LA LÍNEA SELECCIONADA");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);

        // -------------------------------------------------
        // 1) RESUMEN DE LA LÍNEA
        // -------------------------------------------------
        JPanel info = new JPanel(new GridLayout(1, 4, 12, 8));
        info.setOpaque(false);
        info.add(buildInfoBox("Producto", lblLineaProducto));
        info.add(buildInfoBox("Tamaño", lblLineaTamano));
        info.add(buildInfoBox("Disponible", lblLineaDisponible));
        info.add(buildInfoBox("Final línea", lblLineaFinal));

        // -------------------------------------------------
        // 2) CONTROLES DE CANTIDAD
        // -------------------------------------------------
        styleTouchButton(btnMenos);
        styleTouchButton(btnMas);
        styleWideTouchButton(btnDevolverTodo);

        lblCantidadGrande.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblCantidadGrande.setOpaque(true);
        lblCantidadGrande.setBackground(BG_INPUT);
        lblCantidadGrande.setForeground(new Color(14, 48, 35));
        lblCantidadGrande.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 18, 12, 18)
        ));
        lblCantidadGrande.setPreferredSize(new Dimension(110, 64));

        styleToggle(toggleReponeStock);

        JPanel quantityControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quantityControls.setOpaque(false);
        quantityControls.add(btnMenos);
        quantityControls.add(lblCantidadGrande);
        quantityControls.add(btnMas);
        quantityControls.add(btnDevolverTodo);

        JPanel quantityPanel = new JPanel(new BorderLayout(12, 0));
        quantityPanel.setOpaque(false);
        quantityPanel.add(quantityControls, BorderLayout.WEST);
        quantityPanel.add(toggleReponeStock, BorderLayout.EAST);

        // -------------------------------------------------
        // 3) FORMULARIO INFERIOR BIEN REPARTIDO
        // -------------------------------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Método reembolso (arriba izquierda)
        cmbMetodoReembolso.setPreferredSize(new Dimension(220, 42));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        formPanel.add(buildLabeledField("Método reembolso", cmbMetodoReembolso), gbc);

        // Total devolución (arriba derecha)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        formPanel.add(buildInfoBox("Total devolución", lblTotalDevolucion), gbc);

        // Motivo (abajo izquierda)
        txtMotivo.setPreferredSize(new Dimension(0, 42));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.55;
        formPanel.add(buildLabeledField("Motivo", txtMotivo), gbc);

        // Observaciones (abajo derecha)
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane obsScroll = new JScrollPane(txtObservaciones);
        obsScroll.setPreferredSize(new Dimension(0, 80));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.45;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(buildLabeledField("Observaciones", obsScroll), gbc);

        // -------------------------------------------------
        // 4) CONTENIDO GENERAL
        // -------------------------------------------------
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(info);
        content.add(Box.createVerticalStrut(12));
        content.add(quantityPanel);
        content.add(Box.createVerticalStrut(12));
        content.add(formPanel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void styleWideTouchButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(BG_BUTTON_ALT);
        button.setForeground(TEXT_MAIN);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(210, 64));
        button.setMinimumSize(new Dimension(210, 64));
        button.setMaximumSize(new Dimension(210, 64));
        button.setMargin(new Insets(0, 12, 0, 12));
        button.setText("DEVOLVER TODO");
    }

    private JComponent buildBottomBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.add(btnCerrar);
        panel.add(btnConfirmar);
        return panel;
    }

    private JPanel buildInfoBox(String title, JLabel valueLabel) {
        JPanel box = new JPanel(new BorderLayout(4, 4));
        box.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_SOFT);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        valueLabel.setForeground(TEXT_MAIN);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(valueLabel, BorderLayout.CENTER);
        return box;
    }

    private JPanel buildLabeledField(String title, JComponent comp) {
        JPanel box = new JPanel(new BorderLayout(4, 4));
        box.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_SOFT);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(comp, BorderLayout.CENTER);
        return box;
    }

    private JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(BG_PANEL);
        return panel;
    }

    // =====================================================
    // 12) CONFIGURACIÓN DE TABLAS
    // =====================================================

    private void configureVentasTable() {
        tableVentas.setRowHeight(34);
        tableVentas.setFillsViewportHeight(true);
        tableVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableVentas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tableVentas.getTableHeader().setReorderingAllowed(false);

        TableRowSorter<VentasHoyDevolucionTableModel> sorter = new TableRowSorter<>(ventasModel);
        tableVentas.setRowSorter(sorter);

        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof BigDecimal bd) {
                    setText(formatMoney(bd));
                } else {
                    super.setValue(value);
                }
            }
        };
        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");

            @Override
            protected void setValue(Object value) {
                if (value instanceof LocalDateTime ldt) {
                    setText(ldt.format(fmt));
                } else {
                    super.setValue(value);
                }
            }
        };

        tableVentas.getColumnModel().getColumn(VentasHoyDevolucionTableModel.COL_TOTAL).setCellRenderer(moneyRenderer);
        tableVentas.getColumnModel().getColumn(VentasHoyDevolucionTableModel.COL_FECHA).setCellRenderer(dateRenderer);
    }

    private void configureItemsTable() {
        tableItems.setRowHeight(34);
        tableItems.setFillsViewportHeight(true);
        tableItems.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tableItems.getTableHeader().setReorderingAllowed(false);
        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<DevolucionTableModel> sorter = new TableRowSorter<>(devolucionModel);
        tableItems.setRowSorter(sorter);

        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof BigDecimal bd) {
                    setText(formatMoney(bd));
                } else {
                    super.setValue(value);
                }
            }
        };
        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_VENDIDA).setCellRenderer(centerRenderer);
        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_YA_DEVUELTA).setCellRenderer(centerRenderer);
        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_DISPONIBLE).setCellRenderer(centerRenderer);
        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_A_DEVOLVER).setCellRenderer(centerRenderer);

        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_BRUTO).setCellRenderer(moneyRenderer);
        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_DESCUENTO).setCellRenderer(moneyRenderer);
        tableItems.getColumnModel().getColumn(DevolucionTableModel.COL_FINAL).setCellRenderer(moneyRenderer);
    }

    // =====================================================
    // 13) ESTILO
    // =====================================================

    private void styleComponents() {
        styleTextField(txtBuscar);
        styleTextField(txtMotivo);
        styleCombo(cmbMetodoReembolso);
        styleTextArea(txtObservaciones);

        stylePrimaryButton(btnBuscar);
        styleSecondaryButton(btnResetHoy);
        styleSecondaryButton(btnCerrar);
        styleDangerButton(btnConfirmar);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(BG_INPUT);
        field.setForeground(BG_MAIN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleTextArea(JTextArea area) {
        area.setBackground(BG_INPUT);
        area.setForeground(BG_MAIN);
        area.setFont(new Font("SansSerif", Font.PLAIN, 15));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG_INPUT);
        combo.setForeground(BG_MAIN);
        combo.setFont(new Font("SansSerif", Font.BOLD, 15));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(BG_BUTTON);
        button.setForeground(TEXT_MAIN);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(120, 42));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(BG_BUTTON_ALT);
        button.setForeground(TEXT_MAIN);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(120, 42));
    }

    private void styleDangerButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(BG_BUTTON_DANGER);
        button.setForeground(TEXT_MAIN);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(280, 52));
    }

    private void styleTouchButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(BG_BUTTON);
        button.setForeground(TEXT_MAIN);
        button.setFont(new Font("SansSerif", Font.BOLD, 26));
        button.setPreferredSize(new Dimension(78, 64));
    }

    private void styleToggle(JToggleButton toggle) {
        toggle.setFocusPainted(false);
        toggle.setBackground(BG_BUTTON_ALT);
        toggle.setForeground(TEXT_MAIN);
        toggle.setFont(new Font("SansSerif", Font.BOLD, 16));
        toggle.setPreferredSize(new Dimension(240, 64));
        toggle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HIGHLIGHT, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    // =====================================================
    // 14) WIRING DE ACCIONES
    // =====================================================

    private void wireActions() {
        btnBuscar.addActionListener(e -> onBuscarVentas());
        btnResetHoy.addActionListener(e -> onResetHoy());
        btnCerrar.addActionListener(e -> dispose());
        btnConfirmar.addActionListener(e -> onConfirmar());

        btnMas.addActionListener(e -> incrementarCantidadSeleccionada());
        btnMenos.addActionListener(e -> decrementarCantidadSeleccionada());
        btnDevolverTodo.addActionListener(e -> devolverTodoSeleccionado());

        toggleReponeStock.addActionListener(e -> toggleReponeStockSeleccionado());

        tableVentas.getSelectionModel().addListSelectionListener(this::onVentaSeleccionada);
        tableItems.getSelectionModel().addListSelectionListener(this::onLineaSeleccionada);
    }

    // =====================================================
    // 15) CARGA DE VENTAS / BÚSQUEDA
    // =====================================================

    private void loadVentasHoy() {
        List<TicketHoyRowDTO> ventas = services.ticketClienteService.getTicketsHoy();
        ventasModel.setRows(ventas);

        if (ventasModel.getRowCount() > 0) {
            tableVentas.setRowSelectionInterval(0, 0);
        } else {
            clearVentaSeleccionada();
        }
    }

    private void onBuscarVentas() {
        try {
            String query = txtBuscar.getText();
            List<TicketHoyRowDTO> ventas = services.ticketClienteService.searchTickets(query);
            ventasModel.setRows(ventas);

            if (ventasModel.getRowCount() > 0) {
                tableVentas.setRowSelectionInterval(0, 0);
            } else {
                clearVentaSeleccionada();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error buscando ventas", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onResetHoy() {
        txtBuscar.setText("");
        loadVentasHoy();
    }

    // =====================================================
    // 16) SELECCIÓN DE VENTA / CARGA DE LÍNEAS
    // =====================================================

    private void onVentaSeleccionada(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int viewRow = tableVentas.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = tableVentas.convertRowIndexToModel(viewRow);
        TicketHoyRowDTO ventaRow = ventasModel.getRowAtModel(modelRow);

        try {
            loadVentaSeleccionada(ventaRow.getIdVenta());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error cargando venta", JOptionPane.ERROR_MESSAGE);
            clearVentaSeleccionada();
        }
    }

    private void loadVentaSeleccionada(int idVenta) {
        ventaActual = services.devolucionFacade.getVentaParaDevolucion(idVenta);

        List<VentaItemParaDevolucionDTO> items = services.devolucionFacade.getItemsParaDevolucion(
                idVenta,
                AppContext.getIdSucursal()
        );

        lblVenta.setText(String.valueOf(ventaActual.getIdVenta()));
        lblFecha.setText(ventaActual.getFechaVenta() != null ? ventaActual.getFechaVenta().toString() : "-");
        lblPedido.setText(safeText(ventaActual.getNombrePedido()));
        lblServicio.setText(safeText(ventaActual.getTipoServicio()));
        lblPagoOriginal.setText(safeText(ventaActual.getMetodoPagoOriginal()));
        lblTotalVenta.setText(formatMoney(ventaActual.getTotalVenta()));

        devolucionModel.setRows(mapRows(items));
        recalculateTotalPreview();

        if (devolucionModel.size() > 0) {
            tableItems.setRowSelectionInterval(0, 0);
        } else {
            refreshLineaSeleccionadaUI();
        }
    }

    // =====================================================
    // 17) SELECCIÓN DE LÍNEA / AJUSTE TÁCTIL
    // =====================================================

    private void onLineaSeleccionada(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        refreshLineaSeleccionadaUI();
    }

    private void incrementarCantidadSeleccionada() {
        DevolucionRowVM row = getSelectedRowVM();
        if (row == null) {
            return;
        }
        if (row.getCantidadADevolver() < row.getCantidadDisponible()) {
            row.setCantidadADevolver(row.getCantidadADevolver() + 1);
            notifySelectedRowChanged();
        }
    }

    private void decrementarCantidadSeleccionada() {
        DevolucionRowVM row = getSelectedRowVM();
        if (row == null) {
            return;
        }
        if (row.getCantidadADevolver() > 0) {
            row.setCantidadADevolver(row.getCantidadADevolver() - 1);
            if (row.getCantidadADevolver() == 0) {
                row.setReponeStock(false);
            }
            notifySelectedRowChanged();
        }
    }

    private void devolverTodoSeleccionado() {
        DevolucionRowVM row = getSelectedRowVM();
        if (row == null) {
            return;
        }
        row.setCantidadADevolver(row.getCantidadDisponible());
        notifySelectedRowChanged();
    }

    private void toggleReponeStockSeleccionado() {
        DevolucionRowVM row = getSelectedRowVM();
        if (row == null) {
            toggleReponeStock.setSelected(false);
            return;
        }

        if (!row.isPermiteReponerStock() || row.getCantidadADevolver() <= 0) {
            row.setReponeStock(false);
            notifySelectedRowChanged();
            return;
        }

        row.setReponeStock(toggleReponeStock.isSelected());
        notifySelectedRowChanged();
    }

    private void notifySelectedRowChanged() {
        int viewRow = tableItems.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = tableItems.convertRowIndexToModel(viewRow);
        devolucionModel.updateRow(modelRow);
        recalculateTotalPreview();
        refreshLineaSeleccionadaUI();
    }

    private void refreshLineaSeleccionadaUI() {
        DevolucionRowVM row = getSelectedRowVM();

        if (row == null) {
            lblLineaProducto.setText("-");
            lblLineaTamano.setText("-");
            lblLineaDisponible.setText("-");
            lblLineaFinal.setText("-");
            lblCantidadGrande.setText("0");
            toggleReponeStock.setSelected(false);
            toggleReponeStock.setEnabled(false);
            toggleReponeStock.setText("REPONE STOCK: NO");
            btnMas.setEnabled(false);
            btnMenos.setEnabled(false);
            btnDevolverTodo.setEnabled(false);
            return;
        }

        lblLineaProducto.setText(safeText(row.getNombreProducto()));
        lblLineaTamano.setText(safeText(row.getTamano()));
        lblLineaDisponible.setText(String.valueOf(row.getCantidadDisponible()));
        lblLineaFinal.setText(formatMoney(row.getSubtotalFinal()));
        lblCantidadGrande.setText(String.valueOf(row.getCantidadADevolver()));

        boolean enabledStock = row.isPermiteReponerStock() && row.getCantidadADevolver() > 0;
        toggleReponeStock.setEnabled(enabledStock);
        toggleReponeStock.setSelected(enabledStock && row.isReponeStock());
        toggleReponeStock.setText(toggleReponeStock.isSelected() ? "REPONE STOCK: SÍ" : "REPONE STOCK: NO");

        btnMas.setEnabled(row.getCantidadDisponible() > 0);
        btnMenos.setEnabled(row.getCantidadADevolver() > 0);
        btnDevolverTodo.setEnabled(row.getCantidadDisponible() > 0);
    }

    // =====================================================
    // 18) CONFIRMAR DEVOLUCIÓN
    // =====================================================

    private void onConfirmar() {
        try {
            RegistrarDevolucionRequest request = buildRequest();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Confirmar devolución por " + formatMoney(calcularTotalRequest()) + "?",
                    "Confirmar devolución",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            RegistrarDevolucionResultDTO result = services.devolucionFacade.registrarDevolucion(request);

            TicketDevolucionDTO ticket = null;
            try {
                ticket = services.devolucionTicketService.getTicketByDevolucion(result.getIdDevolucion());
            } catch (Exception ignored) {
            }

            if (ticket != null) {
                TicketDevolucionDialog dialog = new TicketDevolucionDialog(this, ticket);
                dialog.showDialog();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Devolución registrada correctamente.\n\n"
                                + "ID devolución: " + result.getIdDevolucion() + "\n"
                                + "Total devuelto: " + formatMoney(result.getImporteTotalDevuelto()),
                        "Devolución registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            if (ventaActual != null) {
                loadVentaSeleccionada(ventaActual.getIdVenta());
            }

            loadVentasHoy();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error registrando devolución",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // 19) CONSTRUCCIÓN DEL REQUEST
    // =====================================================

    private RegistrarDevolucionRequest buildRequest() {
        if (ventaActual == null) {
            throw new IllegalStateException("No hay una venta seleccionada.");
        }

        List<DevolucionRowVM> selectedRows = devolucionModel.getSelectedForReturn();
        if (selectedRows.isEmpty()) {
            throw new IllegalStateException("Debes indicar una cantidad en alguna línea.");
        }

        RegistrarDevolucionRequest request = new RegistrarDevolucionRequest();
        request.setIdVentaOriginal(ventaActual.getIdVenta());
        request.setIdSesionCajaActual(AppContext.getSesionCajaActual().getIdSesion());
        request.setIdUsuarioAdmin(AppContext.getUsuarioId());
        request.setIdSucursalActual(AppContext.getIdSucursal());
        request.setMetodoReembolso(String.valueOf(cmbMetodoReembolso.getSelectedItem()));
        request.setMotivo(normalize(txtMotivo.getText()));
        request.setObservaciones(normalize(txtObservaciones.getText()));

        List<RegistrarDevolucionItemRequest> items = new ArrayList<>();
        for (DevolucionRowVM row : selectedRows) {
            RegistrarDevolucionItemRequest item = new RegistrarDevolucionItemRequest();
            item.setIdVentaItem(row.getIdVentaItem());
            item.setCantidadADevolver(row.getCantidadADevolver());
            item.setReponeStock(row.isReponeStock());
            items.add(item);
        }

        request.setItems(items);
        return request;
    }

    // =====================================================
    // 20) MAPEOS UI
    // =====================================================

    private List<DevolucionRowVM> mapRows(List<VentaItemParaDevolucionDTO> items) {
        List<DevolucionRowVM> rows = new ArrayList<>();

        if (items == null) {
            return rows;
        }

        for (VentaItemParaDevolucionDTO dto : items) {
            DevolucionRowVM row = new DevolucionRowVM();
            row.setIdVentaItem(dto.getIdVentaItem());
            row.setIdProducto(dto.getIdProducto());
            row.setNombreProducto(dto.getNombreProducto());
            row.setTamano(dto.getTamano());
            row.setDescripcionResumen(dto.getDescripcionResumen());
            row.setCantidadVendida(dto.getCantidadVendida());
            row.setCantidadYaDevuelta(dto.getCantidadYaDevuelta());
            row.setCantidadDisponible(dto.getCantidadDisponible());
            row.setPrecioUnitario(safe(dto.getPrecioUnitario()));
            row.setSubtotalBruto(safe(dto.getSubtotalBruto()));
            row.setImporteDescuentoLinea(safe(dto.getImporteDescuentoLinea()));
            row.setSubtotalFinal(safe(dto.getSubtotalFinal()));
            row.setCantidadADevolver(0);
            row.setReponeStock(false);
            row.setPermiteReponerStock(dto.isPermiteReponerStock());
            rows.add(row);
        }

        return rows;
    }

    // =====================================================
    // 21) RESET / TOTALES / HELPERS
    // =====================================================

    private void clearVentaSeleccionada() {
        ventaActual = null;
        lblVenta.setText("-");
        lblFecha.setText("-");
        lblPedido.setText("-");
        lblServicio.setText("-");
        lblPagoOriginal.setText("-");
        lblTotalVenta.setText("-");
        lblTotalDevolucion.setText("0,00 €");
        txtMotivo.setText("");
        txtObservaciones.setText("");
        cmbMetodoReembolso.setSelectedItem("EFECTIVO");
        devolucionModel.clear();
        refreshLineaSeleccionadaUI();
    }

    private void recalculateTotalPreview() {
        lblTotalDevolucion.setText(formatMoney(calcularTotalRequest()));
    }

    private BigDecimal calcularTotalRequest() {
        BigDecimal total = BigDecimal.ZERO;

        for (DevolucionRowVM row : devolucionModel.getSelectedForReturn()) {
            BigDecimal unitarioFinal = BigDecimal.ZERO;

            if (row.getCantidadVendida() > 0) {
                unitarioFinal = safe(row.getSubtotalFinal()).divide(
                        BigDecimal.valueOf(row.getCantidadVendida()),
                        2,
                        java.math.RoundingMode.HALF_UP
                );
            }

            total = total.add(unitarioFinal.multiply(BigDecimal.valueOf(row.getCantidadADevolver())));
        }

        return total;
    }

    private DevolucionRowVM getSelectedRowVM() {
        int viewRow = tableItems.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = tableItems.convertRowIndexToModel(viewRow);
        return devolucionModel.getRowAt(modelRow);
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String safeText(String text) {
        return text == null || text.isBlank() ? "-" : text.trim();
    }

    private String normalize(String text) {
        return text == null ? null : text.trim();
    }

    private String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount != null ? amount : BigDecimal.ZERO) + " €";
    }
}
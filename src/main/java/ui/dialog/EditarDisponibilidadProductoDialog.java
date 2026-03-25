package ui.dialog;

import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class EditarDisponibilidadProductoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // COLORES
    // =========================================================
    private static final Color BG_MAIN = new Color(14, 48, 35);
    private static final Color BG_PANEL = new Color(20, 67, 47);
    private static final Color BG_HEADER = new Color(0, 92, 62);
    private static final Color BORDER = new Color(95, 145, 118);
    private static final Color TEXT_MAIN = new Color(245, 245, 240);
    private static final Color TEXT_SOFT = new Color(212, 223, 216);
    private static final Color TEXT_DARK = new Color(30, 40, 35);

    private static final int WIDTH = 720;
    private static final int HEIGHT = 620;

    // =========================================================
    // ESTADO
    // =========================================================
    private final StockProductoDisponibilidadDTO producto;
    private EditarDisponibilidadProductoDialogResult result =
            EditarDisponibilidadProductoDialogResult.cancelled();

    // =========================================================
    // UI
    // =========================================================
    private JLabel lblNombreProducto;
    private JLabel lblSubcategoria;
    private JLabel lblPermiteCantidad;

    private JComboBox<ModoDisponibilidadProducto> cmbModo;
    private JTextField txtStock;

    public EditarDisponibilidadProductoDialog(JFrame owner, StockProductoDisponibilidadDTO producto) {
        super(owner, "Editar disponibilidad", true);
        this.producto = producto;

        initDialog();
        initComponents();
        buildLayout();
        loadInitialData();
        bindEvents();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        lblNombreProducto = createValueLabel();
        lblSubcategoria = createValueLabel();
        lblPermiteCantidad = createValueLabel();

        cmbModo = new JComboBox<>(buildModoModel());
        cmbModo.setFont(new Font("SansSerif", Font.BOLD, 18));
        cmbModo.setBackground(Color.WHITE);
        cmbModo.setForeground(TEXT_DARK);

        txtStock = new JTextField();
        txtStock.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtStock.setHorizontalAlignment(SwingConstants.CENTER);
        txtStock.setBackground(Color.WHITE);
        txtStock.setForeground(TEXT_DARK);
        txtStock.setCaretColor(TEXT_DARK);
        txtStock.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("EDITAR DISPONIBILIDAD");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Configura el estado operativo del producto en esta sucursal");
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SOFT);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblSubtitle);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel("Producto:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(lblNombreProducto, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel("Subcategoría:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(lblSubcategoria, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel("Permite cantidad:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(lblPermiteCantidad, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel("Modo:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(cmbModo, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel("Stock actual:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(txtStock, gbc);

        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        JLabel help = new JLabel(
                "<html><b>Reglas:</b><br>" +
                        "- No disponible: el producto no se vende.<br>" +
                        "- Disponible sin control: se vende sin contar unidades.<br>" +
                        "- Disponible con cantidad: usa el stock exacto.</html>"
        );
        help.setForeground(TEXT_SOFT);
        help.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(help, gbc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBottomBar() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);

        JButton btnCancelar = createActionButton("CANCELAR");
        JButton btnGuardar = createActionButton("GUARDAR");

        btnCancelar.addActionListener(e -> cancel());
        btnGuardar.addActionListener(e -> accept());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

        return panel;
    }

    private void loadInitialData() {
        lblNombreProducto.setText(producto.getNombreProducto());
        lblSubcategoria.setText(producto.getNombreSubcategoria());
        lblPermiteCantidad.setText(producto.isPermiteStockCantidad() ? "Sí" : "No");

        cmbModo.setSelectedItem(producto.getModoDisponibilidad());
        txtStock.setText(formatStock(producto.getStockActual()));

        updateStockFieldState();
    }

    private void bindEvents() {
        cmbModo.addActionListener(e -> updateStockFieldState());
    }

    private DefaultComboBoxModel<ModoDisponibilidadProducto> buildModoModel() {
        DefaultComboBoxModel<ModoDisponibilidadProducto> model = new DefaultComboBoxModel<>();

        model.addElement(ModoDisponibilidadProducto.NO_DISPONIBLE);
        model.addElement(ModoDisponibilidadProducto.DISPONIBLE_SIN_CONTROL);

        if (producto.isPermiteStockCantidad()
                || producto.getModoDisponibilidad() == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD) {
            model.addElement(ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD);
        }

        return model;
    }

    private void updateStockFieldState() {
        ModoDisponibilidadProducto modo =
                (ModoDisponibilidadProducto) cmbModo.getSelectedItem();

        boolean enableStock = modo == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD;
        txtStock.setEnabled(enableStock);

        if (!enableStock) {
            txtStock.setBackground(new Color(235, 235, 235));
        } else {
            txtStock.setBackground(Color.WHITE);
        }
    }

    private void accept() {
        ModoDisponibilidadProducto modo =
                (ModoDisponibilidadProducto) cmbModo.getSelectedItem();

        if (modo == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un modo de disponibilidad.",
                    "Disponibilidad",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        BigDecimal stock = parseStock();

        if (modo == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD) {
            if (stock == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Introduce un stock válido.",
                        "Disponibilidad",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (stock.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "El stock no puede ser negativo.",
                        "Disponibilidad",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } else {
            // Si no controla cantidad, conservamos el valor escrito
            // o usamos el actual si el campo está vacío.
            if (stock == null) {
                stock = producto.getStockActual();
            }
        }

        result = new EditarDisponibilidadProductoDialogResult(true, modo, stock);
        dispose();
    }

    private BigDecimal parseStock() {
        String text = txtStock.getText();
        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            String normalized = text.trim().replace(",", ".");
            return new BigDecimal(normalized);
        } catch (Exception e) {
            return null;
        }
    }

    private void cancel() {
        result = EditarDisponibilidadProductoDialogResult.cancelled();
        dispose();
    }

    public EditarDisponibilidadProductoDialogResult showDialog() {
        setVisible(true);
        return result;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT_MAIN);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        label.setForeground(TEXT_SOFT);
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_HEADER);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private String formatStock(BigDecimal stock) {
        if (stock == null) {
            return "0";
        }
        return stock.stripTrailingZeros().toPlainString();
    }
}
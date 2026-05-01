package ui.dialog;

import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class EditarDisponibilidadProductoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 720;
    private static final int HEIGHT = 620;

    private final StockProductoDisponibilidadDTO producto;
    private EditarDisponibilidadProductoDialogResult result = EditarDisponibilidadProductoDialogResult.cancelled();

    private JLabel lblNombreProducto;
    private JLabel lblSubcategoria;
    private JLabel lblPermiteCantidad;

    private JComboBox<ModoDisponibilidadProducto> cmbModo;
    private JTextField txtStock;

    public EditarDisponibilidadProductoDialog(JFrame owner, StockProductoDisponibilidadDTO producto) {
        super(owner, I18n.t("availability.productEdit.title"), true);
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
        setMinimumSize(new Dimension(640, 520));
        setResizable(true);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        lblNombreProducto = createValueLabel();
        lblSubcategoria = createValueLabel();
        lblPermiteCantidad = createValueLabel();

        cmbModo = new JComboBox<>(buildModoModel());
        cmbModo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ModoDisponibilidadProducto modo) {
                    setText(formatModo(modo));
                }
                return this;
            }
        });
        InformeUiTheme.styleCombo(cmbModo);
        cmbModo.setFont(InformeUiTheme.FONT_BODY.deriveFont(Font.BOLD, 16f));

        txtStock = new JTextField();
        txtStock.setHorizontalAlignment(SwingConstants.CENTER);
        InformeUiTheme.styleTextField(txtStock);
        txtStock.setFont(InformeUiTheme.FONT_BODY.deriveFont(Font.BOLD, 22f));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(I18n.t("availability.productEdit.header"), SwingConstants.CENTER);
        lblTitle.setIcon(TpvIconFactory.product(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION.deriveFont(22f));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("availability.productEdit.subtitle"), SwingConstants.CENTER);
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSubtitle, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout());

        JPanel form = InformeUiTheme.createTransparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(form, gbc, row++, I18n.t("availability.productEdit.product") + ":", lblNombreProducto);
        addFormRow(form, gbc, row++, I18n.t("availability.productEdit.subcategory") + ":", lblSubcategoria);
        addFormRow(form, gbc, row++, I18n.t("availability.productEdit.allowsQty") + ":", lblPermiteCantidad);
        addFormRow(form, gbc, row++, I18n.t("availability.productEdit.mode") + ":", cmbModo);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(createLabel(I18n.t("availability.productEdit.currentStock") + ":"), gbc);

        JPanel stockWrapper = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 0));
        stockWrapper.add(txtStock, BorderLayout.CENTER);

        JButton btnTecladoStock = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTecladoStock);
        btnTecladoStock.setToolTipText(I18n.t("keyboard.open"));
        btnTecladoStock.setPreferredSize(new Dimension(52, 48));
        btnTecladoStock.addActionListener(e -> TecladoVirtualDialog.showNumerico(
                this,
                txtStock,
                I18n.t("availability.productEdit.keyboardStock"),
                8
        ));
        stockWrapper.add(btnTecladoStock, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(stockWrapper, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        JLabel help = new JLabel(I18n.t("availability.productEdit.rulesHtml"));
        help.setForeground(InformeUiTheme.TEXT_SECONDARY);
        help.setFont(InformeUiTheme.FONT_BODY);
        form.add(help, gbc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        form.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(value, gbc);
    }

    private JComponent buildBottomBar() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new GridLayout(1, 2, 12, 12));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCancelar);

        JButton btnGuardar = new JButton(I18n.t("common.save"));
        btnGuardar.setIcon(TpvIconFactory.save(18, Color.WHITE));
        btnGuardar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnGuardar);

        btnCancelar.addActionListener(e -> cancel());
        btnGuardar.addActionListener(e -> accept());

        panel.add(btnCancelar);
        panel.add(btnGuardar);
        return panel;
    }

    private void loadInitialData() {
        lblNombreProducto.setText(producto.getNombreProducto());
        lblSubcategoria.setText(producto.getNombreSubcategoria());
        lblPermiteCantidad.setText(producto.isPermiteStockCantidad() ? I18n.t("common.yes") : I18n.t("common.no"));

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
        ModoDisponibilidadProducto modo = (ModoDisponibilidadProducto) cmbModo.getSelectedItem();
        boolean enableStock = modo == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD;
        txtStock.setEnabled(enableStock);
        txtStock.setBackground(enableStock ? InformeUiTheme.CARD_BG_2 : new Color(54, 62, 59));
    }

    private void accept() {
        ModoDisponibilidadProducto modo = (ModoDisponibilidadProducto) cmbModo.getSelectedItem();

        if (modo == null) {
            TpvDialogUtils.showWarning(this, I18n.t("availability.title"), I18n.t("availability.productEdit.validation.mode"));
            return;
        }

        BigDecimal stock = parseStock();

        if (modo == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD) {
            if (stock == null) {
                TpvDialogUtils.showWarning(this, I18n.t("availability.title"), I18n.t("availability.productEdit.validation.stock"));
                return;
            }

            if (stock.compareTo(BigDecimal.ZERO) < 0) {
                TpvDialogUtils.showWarning(this, I18n.t("availability.title"), I18n.t("availability.productEdit.validation.stockNegative"));
                return;
            }
        } else if (stock == null) {
            stock = producto.getStockActual();
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
            return new BigDecimal(text.trim().replace(",", "."));
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
        JLabel label = InformeUiTheme.createFieldLabel(text);
        label.setFont(InformeUiTheme.FONT_LABEL.deriveFont(15f));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(InformeUiTheme.FONT_BODY);
        label.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return label;
    }

    private String formatModo(ModoDisponibilidadProducto modo) {
        if (modo == null) {
            return "";
        }
        return switch (modo) {
            case NO_DISPONIBLE -> I18n.t("availability.mode.notAvailable");
            case DISPONIBLE_SIN_CONTROL -> I18n.t("availability.mode.availableNoControl");
            case DISPONIBLE_CON_CANTIDAD -> I18n.t("availability.mode.availableWithQty");
        };
    }

    private String formatStock(BigDecimal stock) {
        if (stock == null) {
            return "0";
        }
        return stock.stripTrailingZeros().toPlainString();
    }
}

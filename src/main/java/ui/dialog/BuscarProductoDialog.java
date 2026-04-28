package ui.dialog;

import dtoS.ProductoBusquedaRowDTO;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BuscarProductoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color BG_MAIN = InformeUiTheme.APP_BG;
    private static final Color BG_PANEL = InformeUiTheme.CARD_BG;
    private static final Color BG_TABLE = new Color(244, 248, 245);
    private static final Color BG_HEADER = InformeUiTheme.STARBUCKS_GREEN;
    private static final Color BG_SELECTION = new Color(159, 196, 173);
    private static final Color BORDER = InformeUiTheme.BORDER;
    private static final Color TEXT_MAIN = InformeUiTheme.TEXT_PRIMARY;
    private static final Color TEXT_DARK = new Color(30, 40, 35);
    private static final Color TEXT_SOFT = InformeUiTheme.TEXT_SECONDARY;
    private static final Color ROW_DISABLED_BG = new Color(228, 228, 228);
    private static final Color ROW_DISABLED_FG = new Color(130, 130, 130);

    private static final int WIDTH = 980;
    private static final int HEIGHT = 680;

    private final List<ProductoBusquedaRowDTO> rows;

    private JTextField txtBuscar;
    private JTable table;
    private ProductoBusquedaTableModel tableModel;
    private TableRowSorter<ProductoBusquedaTableModel> sorter;

    private ProductoBusquedaRowDTO result;

    public BuscarProductoDialog(JFrame owner, List<ProductoBusquedaRowDTO> rows) {
        super(owner, "Buscar producto", true);
        this.rows = rows != null ? rows : new ArrayList<>();

        initDialog();
        initComponents();
        buildLayout();
        bindEvents();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int targetW = (int) (screen.width * 0.78);
        int targetH = (int) (screen.height * 0.78);

        int finalW = Math.max(820, Math.min(WIDTH, targetW));
        int finalH = Math.max(560, Math.min(HEIGHT, targetH));

        setMinimumSize(new Dimension(780, 540));
        setSize(finalW, finalH);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("SansSerif", Font.BOLD, 18));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(TEXT_DARK);
        txtBuscar.setCaretColor(TEXT_DARK);

        tableModel = new ProductoBusquedaTableModel(this.rows);

        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                int modelRow = convertRowIndexToModel(row);
                ProductoBusquedaRowDTO dto = tableModel.getRow(modelRow);

                if (isRowSelected(row)) {
                    c.setBackground(BG_SELECTION);
                    c.setForeground(TEXT_DARK);
                    return c;
                }

                if (!dto.isBotonHabilitado()) {
                    c.setBackground(ROW_DISABLED_BG);
                    c.setForeground(ROW_DISABLED_FG);
                } else {
                    c.setBackground(BG_TABLE);
                    c.setForeground(TEXT_DARK);
                }

                return c;
            }
        };

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(BG_TABLE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(new Color(210, 220, 214));
        table.setSelectionBackground(BG_SELECTION);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(BG_HEADER);
        header.setForeground(TEXT_MAIN);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        configureColumnRenderers();
        configureColumnWidths();
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BG_MAIN);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("BUSCAR PRODUCTO");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Busca por nombre y añade solo productos vendibles");
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_SOFT);

        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(lblSubtitle);

        JPanel searchWrap = new JPanel(new BorderLayout(8, 8));
        searchWrap.setBackground(BG_PANEL);
        searchWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblBuscar.setForeground(TEXT_MAIN);

        JPanel inputWrap = new JPanel(new BorderLayout(6, 0));
        inputWrap.setOpaque(false);
        inputWrap.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTeclado = createSmallActionButton("⌨");
        btnTeclado.setToolTipText("Abrir teclado táctil");
        btnTeclado.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        "Teclado - Buscar producto",
                        60
                )
        );

        inputWrap.add(btnTeclado, BorderLayout.EAST);

        searchWrap.add(lblBuscar, BorderLayout.WEST);
        searchWrap.add(inputWrap, BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(searchWrap, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_TABLE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBottomBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel lblHint = new JLabel("Doble clic sobre un producto para añadirlo.");
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHint.setForeground(TEXT_SOFT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton btnCancelar = createSecondaryActionButton("CANCELAR");
        JButton btnAceptar = createPrimaryActionButton("AÑADIR");

        btnCancelar.addActionListener(e -> cancel());
        btnAceptar.addActionListener(e -> acceptSelected());

        buttons.add(btnCancelar);
        buttons.add(btnAceptar);

        panel.add(lblHint, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
    }

    private JButton createPrimaryActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_HEADER);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 42));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(9, 12, 9, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSecondaryActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(TEXT_MAIN);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 42));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(9, 12, 9, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSmallActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(TEXT_MAIN);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(48, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void bindEvents() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltro();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltro();
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    acceptSelected();
                }
            }
        });

        SwingUtilities.invokeLater(() -> txtBuscar.requestFocusInWindow());
    }

    private void aplicarFiltro() {
        String texto = txtBuscar.getText();

        if (texto == null || texto.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter(
                "(?i)" + Pattern.quote(texto.trim()),
                0
        ));
    }

    private void configureColumnRenderers() {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            private final DecimalFormat df = new DecimalFormat("#,##0.00 €");

            @Override
            protected void setValue(Object value) {
                if (value instanceof BigDecimal bd) {
                    setText(df.format(bd));
                } else {
                    setText("");
                }
            }
        };
        priceRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(left);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(priceRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
    }

    private void configureColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(420);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
    }

    private void acceptSelected() {
        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            TpvDialogUtils.showWarning(
                    this,
                    "Búsqueda de productos",
                    "Selecciona un producto de la tabla."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        ProductoBusquedaRowDTO selected = tableModel.getRow(modelRow);

        if (!selected.isBotonHabilitado()) {
            TpvDialogUtils.showWarning(
                    this,
                    "Producto no disponible",
                    "Ese producto no se puede vender ahora.\n\nEstado: " + selected.getTextoEstado()
            );
            return;
        }

        result = selected;
        dispose();
    }

    private void cancel() {
        result = null;
        dispose();
    }

    public ProductoBusquedaRowDTO showDialog() {
        setVisible(true);
        return result;
    }

    private static final class ProductoBusquedaTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private final String[] columns = {"Producto", "Tamaño", "Precio", "Estado"};
        private final List<ProductoBusquedaRowDTO> rows;

        private ProductoBusquedaTableModel(List<ProductoBusquedaRowDTO> rows) {
            this.rows = rows;
        }

        public ProductoBusquedaRowDTO getRow(int rowIndex) {
            return rows.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ProductoBusquedaRowDTO row = rows.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> row.getNombreProducto();
                case 1 -> row.getNombreTamano();
                case 2 -> row.getPrecio();
                case 3 -> row.getTextoEstado();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 2 -> BigDecimal.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
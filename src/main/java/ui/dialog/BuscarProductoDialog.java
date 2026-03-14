package ui.dialog;

import dtoS.ProductoBusquedaRowDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BuscarProductoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // COLORES TEMÁTICA CAFETERÍA / VERDE TIPO STARBUCKS
    // =========================================================
    private static final Color BG_MAIN = new Color(14, 48, 35);
    private static final Color BG_PANEL = new Color(20, 67, 47);
    private static final Color BG_TABLE = new Color(244, 248, 245);
    private static final Color BG_HEADER = new Color(0, 92, 62);
    private static final Color BG_SELECTION = new Color(159, 196, 173);
    private static final Color BORDER = new Color(95, 145, 118);
    private static final Color TEXT_MAIN = new Color(245, 245, 240);
    private static final Color TEXT_DARK = new Color(30, 40, 35);
    private static final Color TEXT_SOFT = new Color(212, 223, 216);

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
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(TEXT_DARK);
        txtBuscar.setCaretColor(TEXT_DARK);

        tableModel = new ProductoBusquedaTableModel(this.rows);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(BG_TABLE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(new Color(210, 220, 214));
        table.setSelectionBackground(BG_SELECTION);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBackground(BG_HEADER);
        header.setForeground(TEXT_MAIN);
        header.setReorderingAllowed(false);

        configureColumnRenderers();
        configureColumnWidths();
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("BUSCAR PRODUCTO");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Busca por nombre y añade directamente producto + tamaño + precio");
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SOFT);

        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(lblSubtitle);

        JPanel searchWrap = new JPanel(new BorderLayout(8, 8));
        searchWrap.setBackground(BG_PANEL);
        searchWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBuscar.setForeground(TEXT_MAIN);

        searchWrap.add(lblBuscar, BorderLayout.WEST);
        searchWrap.add(txtBuscar, BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(searchWrap, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_TABLE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBottomBar() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);

        JButton btnCancelar = createActionButton("CANCELAR");
        JButton btnAceptar = createActionButton("AÑADIR");

        btnCancelar.addActionListener(e -> cancel());
        btnAceptar.addActionListener(e -> acceptSelected());

        panel.add(btnCancelar);
        panel.add(btnAceptar);

        return panel;
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
                0 // columna Producto
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

        table.getColumnModel().getColumn(0).setCellRenderer(left);       // Producto
        table.getColumnModel().getColumn(1).setCellRenderer(center);     // Tamaño
        table.getColumnModel().getColumn(2).setCellRenderer(priceRenderer); // Precio
    }

    private void configureColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(500);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
    }

    private void acceptSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un producto de la tabla.",
                    "Búsqueda de productos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        result = tableModel.getRow(modelRow);
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

    // =========================================================
    // TABLE MODEL
    // =========================================================
    private static final class ProductoBusquedaTableModel extends AbstractTableModel {

        private final String[] columns = {"Producto", "Tamaño", "Precio"};
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

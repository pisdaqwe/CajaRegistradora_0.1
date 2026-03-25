package ui.dialog;

import dtoS.StockProductoDisponibilidadDTO;
import enums.ModoDisponibilidadProducto;
import service.AppServices;

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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DisponibilidadProductoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // COLORES
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
    private static final Color ROW_DISABLED_BG = new Color(228, 228, 228);
    private static final Color ROW_DISABLED_FG = new Color(130, 130, 130);

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 720;

    // =========================================================
    // DEPENDENCIAS
    // =========================================================
    private final AppServices services;

    // =========================================================
    // UI
    // =========================================================
    private JTextField txtBuscar;
    private JTable table;
    private DisponibilidadProductoTableModel tableModel;
    private TableRowSorter<DisponibilidadProductoTableModel> sorter;

    public DisponibilidadProductoDialog(JFrame owner, AppServices services) {
        super(owner, "Disponibilidad de productos", true);
        this.services = services;

        initDialog();
        initComponents();
        buildLayout();
        bindEvents();
        reloadData();
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

        tableModel = new DisponibilidadProductoTableModel();
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                int modelRow = convertRowIndexToModel(row);
                StockProductoDisponibilidadDTO dto = tableModel.getRow(modelRow);

                if (isRowSelected(row)) {
                    c.setBackground(BG_SELECTION);
                    c.setForeground(TEXT_DARK);
                    return c;
                }

                if (!dto.isDisponible() || dto.isAgotado()) {
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

        JLabel lblTitle = new JLabel("DISPONIBILIDAD DE PRODUCTOS");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Gestiona disponibilidad y cantidad por producto en la sucursal actual");
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

        JButton btnCerrar = createActionButton("CERRAR");
        JButton btnEditar = createActionButton("EDITAR");

        btnCerrar.addActionListener(e -> dispose());
        btnEditar.addActionListener(e -> editSelected());

        panel.add(btnCerrar);
        panel.add(btnEditar);

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
                    editSelected();
                }
            }
        });

        SwingUtilities.invokeLater(() -> txtBuscar.requestFocusInWindow());
    }

    private void reloadData() {
        List<StockProductoDisponibilidadDTO> rows =
                services.disponibilidadProductoService.getDisponibilidadProductosSucursalActual();
        tableModel.setRows(rows);
    }

    private void aplicarFiltro() {
        String texto = txtBuscar.getText();
        if (texto == null || texto.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter(
                "(?i)" + Pattern.quote(texto.trim()),
                0, 1
        ));
    }

    private void configureColumnRenderers() {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setCellRenderer(left);   // Producto
        table.getColumnModel().getColumn(1).setCellRenderer(left);   // Subcategoría
        table.getColumnModel().getColumn(2).setCellRenderer(center); // Modo
        table.getColumnModel().getColumn(3).setCellRenderer(right);  // Stock
        table.getColumnModel().getColumn(4).setCellRenderer(center); // Estado
    }

    private void configureColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(320);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
    }

    private void editSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un producto de la tabla.",
                    "Disponibilidad",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        StockProductoDisponibilidadDTO selected = tableModel.getRow(modelRow);

        EditarDisponibilidadProductoDialog dialog =
                new EditarDisponibilidadProductoDialog((JFrame) getOwner(), selected);

        EditarDisponibilidadProductoDialogResult result = dialog.showDialog();
        if (!result.isConfirmed()) {
            return;
        }

        try {
            services.disponibilidadProductoService.guardarDisponibilidadProductoSucursalActual(
                    selected.getIdProducto(),
                    result.getModoDisponibilidad(),
                    result.getStock()
            );

            reloadData();
            reselectProduct(selected.getIdProducto());

            JOptionPane.showMessageDialog(
                    this,
                    "Disponibilidad actualizada correctamente.",
                    "Disponibilidad",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar la disponibilidad.\n\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void reselectProduct(int idProducto) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getRow(i).getIdProducto() == idProducto) {
                int viewIndex = table.convertRowIndexToView(i);
                if (viewIndex >= 0) {
                    table.setRowSelectionInterval(viewIndex, viewIndex);
                    table.scrollRectToVisible(table.getCellRect(viewIndex, 0, true));
                }
                return;
            }
        }
    }

    public void showDialog() {
        setVisible(true);
    }

    private static final class DisponibilidadProductoTableModel extends AbstractTableModel {

        private final String[] columns = {
                "Producto",
                "Subcategoría",
                "Modo",
                "Stock",
                "Estado"
        };

        private List<StockProductoDisponibilidadDTO> rows = new ArrayList<>();

        public void setRows(List<StockProductoDisponibilidadDTO> rows) {
            this.rows = rows != null ? rows : new ArrayList<>();
            fireTableDataChanged();
        }

        public StockProductoDisponibilidadDTO getRow(int rowIndex) {
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
            StockProductoDisponibilidadDTO row = rows.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> row.getNombreProducto();
                case 1 -> row.getNombreSubcategoria();
                case 2 -> formatModo(row.getModoDisponibilidad());
                case 3 -> formatStock(row.getStockActual());
                case 4 -> row.getTextoEstado();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        private String formatModo(ModoDisponibilidadProducto modo) {
            return switch (modo) {
                case NO_DISPONIBLE -> "No disponible";
                case DISPONIBLE_SIN_CONTROL -> "Disponible sin control";
                case DISPONIBLE_CON_CANTIDAD -> "Disponible con cantidad";
            };
        }

        private String formatStock(BigDecimal stock) {
            if (stock == null) {
                return "0";
            }
            return stock.stripTrailingZeros().toPlainString();
        }
    }
}
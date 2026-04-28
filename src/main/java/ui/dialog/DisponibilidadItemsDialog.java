package ui.dialog;

import dtoS.DisponibilidadItemRowDTO;
import dtoS.StockExtraDisponibilidadDTO;
import dtoS.StockProductoDisponibilidadDTO;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class DisponibilidadItemsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

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

    private static final int WIDTH = 1180;
    private static final int HEIGHT = 760;

    private final AppServices services;

    private JTextField txtBuscar;
    private JTable table;
    private DisponibilidadItemsTableModel tableModel;
    private TableRowSorter<DisponibilidadItemsTableModel> sorter;

    public DisponibilidadItemsDialog(JFrame owner, AppServices services) {
        super(owner, "Disponibilidad catálogo", true);
        this.services = services;

        initDialog();
        initComponents();
        buildLayout();
        bindEvents();
        reloadData();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int targetW = (int) (screen.width * 0.88);
        int targetH = (int) (screen.height * 0.84);

        int finalW = Math.max(1050, Math.min(WIDTH, targetW));
        int finalH = Math.max(650, Math.min(HEIGHT, targetH));

        setMinimumSize(new Dimension(980, 620));
        setSize(finalW, finalH);
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

        tableModel = new DisponibilidadItemsTableModel();

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                int modelRow = convertRowIndexToModel(row);
                DisponibilidadItemRowDTO dto = tableModel.getRow(modelRow);

                if (isRowSelected(row)) {
                    c.setBackground(BG_SELECTION);
                    c.setForeground(TEXT_DARK);
                    return c;
                }

                boolean disabled = !dto.isDisponible() || dto.isAgotado();
                if (disabled) {
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

        JLabel lblTitle = new JLabel("DISPONIBILIDAD DEL CATÁLOGO");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel("Gestiona productos y extras de la sucursal actual");
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(TEXT_SOFT);

        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(3));
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
                        "Teclado - Buscar disponibilidad",
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

    private JButton createSmallActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_HEADER);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(50, 42));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
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
        List<DisponibilidadItemRowDTO> rows = new ArrayList<>();

        List<StockProductoDisponibilidadDTO> productos =
                services.disponibilidadProductoService.getDisponibilidadProductosSucursalActual();

        for (StockProductoDisponibilidadDTO p : productos) {
            rows.add(DisponibilidadItemRowDTO.producto(
                    p.getIdProducto(),
                    p.getNombreProducto(),
                    p.getNombreSubcategoria(),
                    p.isPermiteStockCantidad(),
                    p.getModoDisponibilidad(),
                    p.getStockActual()
            ));
        }

        List<StockExtraDisponibilidadDTO> extras =
                services.disponibilidadExtraService.getDisponibilidadExtrasSucursalActual();

        for (StockExtraDisponibilidadDTO e : extras) {
            rows.add(DisponibilidadItemRowDTO.extra(
                    e.getIdExtra(),
                    e.getNombreExtra(),
                    e.getTipoExtra(),
                    e.isDisponible()
            ));
        }

        rows.sort(Comparator
                .comparing(DisponibilidadItemRowDTO::getTipoItem)
                .thenComparing(DisponibilidadItemRowDTO::getGrupo)
                .thenComparing(DisponibilidadItemRowDTO::getNombre));

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
                1, 2
        ));
    }

    private void configureColumnRenderers() {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center); // Tipo
        table.getColumnModel().getColumn(1).setCellRenderer(left);   // Nombre
        table.getColumnModel().getColumn(2).setCellRenderer(left);   // Grupo
        table.getColumnModel().getColumn(3).setCellRenderer(center); // Modo
        table.getColumnModel().getColumn(4).setCellRenderer(center); // Stock
        table.getColumnModel().getColumn(5).setCellRenderer(center); // Estado
    }

    private void configureColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(240);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
    }

    private void editSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            TpvDialogUtils.showWarning(
                    this,
                    "Disponibilidad",
                    "Selecciona un elemento de la tabla."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        DisponibilidadItemRowDTO selected = tableModel.getRow(modelRow);

        try {
            if (selected.isProducto()) {
                editarProducto(selected);
            } else if (selected.isExtra()) {
                editarExtra(selected);
            }

            reloadData();
            reselectItem(selected.getTipoItem(), selected.getIdItem());

        } catch (Exception e) {
            TpvDialogUtils.showError(
                    this,
                    "Error",
                    "No se pudo guardar el cambio.\n\n" + e.getMessage()
            );
        }
    }

    private void editarProducto(DisponibilidadItemRowDTO selected) {
        StockProductoDisponibilidadDTO dto =
                services.disponibilidadProductoService.getDisponibilidadProductoSucursalActual(selected.getIdItem());

        EditarDisponibilidadProductoDialog dialog =
                new EditarDisponibilidadProductoDialog((JFrame) getOwner(), dto);

        EditarDisponibilidadProductoDialogResult result = dialog.showDialog();
        if (!result.isConfirmed()) {
            return;
        }

        services.disponibilidadProductoService.guardarDisponibilidadProductoSucursalActual(
                dto.getIdProducto(),
                result.getModoDisponibilidad(),
                result.getStock()
        );
    }

    private void editarExtra(DisponibilidadItemRowDTO selected) {
        StockExtraDisponibilidadDTO dto =
                services.disponibilidadExtraService.getDisponibilidadExtraSucursalActual(selected.getIdItem());

        EditarDisponibilidadExtraDialog dialog =
                new EditarDisponibilidadExtraDialog((JFrame) getOwner(), dto);

        EditarDisponibilidadExtraDialogResult result = dialog.showDialog();
        if (!result.isConfirmed()) {
            return;
        }

        services.disponibilidadExtraService.guardarDisponibilidadExtraSucursalActual(
                dto.getIdExtra(),
                result.isDisponible()
        );
    }

    private void reselectItem(String tipoItem, int idItem) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            DisponibilidadItemRowDTO row = tableModel.getRow(i);
            if (row.getTipoItem().equals(tipoItem) && row.getIdItem() == idItem) {
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

    private static final class DisponibilidadItemsTableModel extends AbstractTableModel {

        private final String[] columns = {
                "Tipo",
                "Nombre",
                "Grupo",
                "Modo",
                "Stock",
                "Estado"
        };

        private List<DisponibilidadItemRowDTO> rows = new ArrayList<>();

        public void setRows(List<DisponibilidadItemRowDTO> rows) {
            this.rows = rows != null ? rows : new ArrayList<>();
            fireTableDataChanged();
        }

        public DisponibilidadItemRowDTO getRow(int rowIndex) {
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
            DisponibilidadItemRowDTO row = rows.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> row.getTipoItem();
                case 1 -> row.getNombre();
                case 2 -> row.getGrupo();
                case 3 -> row.getTextoModo();
                case 4 -> row.getTextoStock();
                case 5 -> row.getTextoEstado();
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
    }
}
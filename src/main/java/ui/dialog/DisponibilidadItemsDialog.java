package ui.dialog;

import dtoS.DisponibilidadItemRowDTO;
import dtoS.StockExtraDisponibilidadDTO;
import dtoS.StockProductoDisponibilidadDTO;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class DisponibilidadItemsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 1180;
    private static final int HEIGHT = 760;

    private final AppServices services;

    private JTextField txtBuscar;
    private JTable table;
    private DisponibilidadItemsTableModel tableModel;
    private TableRowSorter<DisponibilidadItemsTableModel> sorter;

    public DisponibilidadItemsDialog(JFrame owner, AppServices services) {
        super(owner, I18n.t("availability.catalog.title"), true);
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
        txtBuscar.setFont(InformeUiTheme.FONT_BODY.deriveFont(Font.BOLD, 18f));
        InformeUiTheme.styleTextField(txtBuscar);

        tableModel = new DisponibilidadItemsTableModel();

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                int modelRow = convertRowIndexToModel(row);
                DisponibilidadItemRowDTO dto = tableModel.getRow(modelRow);

                if (isRowSelected(row)) {
                    c.setBackground(InformeUiTheme.STARBUCKS_GREEN);
                    c.setForeground(Color.WHITE);
                    return c;
                }

                boolean disabled = !dto.isDisponible() || dto.isAgotado();
                if (disabled) {
                    c.setBackground(new Color(54, 62, 59));
                    c.setForeground(new Color(160, 170, 164));
                } else {
                    c.setBackground(InformeUiTheme.PANEL_BG);
                    c.setForeground(InformeUiTheme.TEXT_PRIMARY);
                }

                return c;
            }
        };

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        InformeUiTheme.styleTable(table);

        configureColumnRenderers();
        configureColumnWidths();
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
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(10, 10));

        JPanel titleBox = InformeUiTheme.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(I18n.t("availability.catalog.header"));
        lblTitle.setIcon(TpvIconFactory.product(24, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(10);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(InformeUiTheme.FONT_TITLE.deriveFont(24f));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("availability.catalog.subtitle"));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(lblSubtitle);

        JPanel searchWrap = InformeUiTheme.createCardPanel(new BorderLayout(8, 8));

        JLabel lblBuscar = InformeUiTheme.createFieldLabel(I18n.t("common.search") + ":");
        lblBuscar.setIcon(TpvIconFactory.search(18, InformeUiTheme.ACCENT_GOLD));
        lblBuscar.setIconTextGap(8);

        JPanel inputWrap = InformeUiTheme.createTransparentPanel(new BorderLayout(6, 0));
        inputWrap.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTeclado = createSmallActionButton("⌨");
        btnTeclado.setToolTipText(I18n.t("keyboard.open"));
        btnTeclado.addActionListener(e -> TecladoVirtualDialog.showAlfanumerico(
                this,
                txtBuscar,
                I18n.t("availability.catalog.keyboardTitle"),
                60
        ));

        inputWrap.add(btnTeclado, BorderLayout.EAST);

        searchWrap.add(lblBuscar, BorderLayout.WEST);
        searchWrap.add(inputWrap, BorderLayout.CENTER);

        panel.add(titleBox, BorderLayout.NORTH);
        panel.add(searchWrap, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createSmallActionButton(String text) {
        JButton button = new JButton(text);
        InformeUiTheme.styleSecondaryButton(button);
        button.setPreferredSize(new Dimension(54, 42));
        return button;
    }

    private JComponent buildCenter() {
        JPanel panel = InformeUiTheme.createCardPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        InformeUiTheme.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBottomBar() {
        JPanel panel = InformeUiTheme.createTransparentPanel(new BorderLayout(12, 0));

        JLabel hint = new JLabel(I18n.t("availability.catalog.hint"));
        hint.setFont(InformeUiTheme.FONT_SUBTITLE);
        hint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel actions = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnCerrar = new JButton(I18n.t("common.close"));
        btnCerrar.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnCerrar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCerrar);

        JButton btnEditar = new JButton(I18n.t("common.edit"));
        btnEditar.setIcon(TpvIconFactory.settings(18, Color.WHITE));
        btnEditar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnEditar);

        btnCerrar.addActionListener(e -> dispose());
        btnEditar.addActionListener(e -> editSelected());

        actions.add(btnCerrar);
        actions.add(btnEditar);

        panel.add(hint, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private void bindEvents() {
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
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

        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto.trim()), 1, 2));
    }

    private void configureColumnRenderers() {
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(left);
        table.getColumnModel().getColumn(2).setCellRenderer(left);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
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
            TpvDialogUtils.showWarning(this, I18n.t("availability.title"), I18n.t("availability.validation.selectItem"));
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
            TpvDialogUtils.showError(this, I18n.t("common.error"), I18n.t("availability.error.save", e.getMessage()));
        }
    }

    private void editarProducto(DisponibilidadItemRowDTO selected) {
        StockProductoDisponibilidadDTO dto =
                services.disponibilidadProductoService.getDisponibilidadProductoSucursalActual(selected.getIdItem());

        EditarDisponibilidadProductoDialog dialog = new EditarDisponibilidadProductoDialog((JFrame) getOwner(), dto);
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

        EditarDisponibilidadExtraDialog dialog = new EditarDisponibilidadExtraDialog((JFrame) getOwner(), dto);
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
                I18n.t("availability.table.type"),
                I18n.t("availability.table.name"),
                I18n.t("availability.table.group"),
                I18n.t("availability.table.mode"),
                I18n.t("availability.table.stock"),
                I18n.t("availability.table.status")
        };

        private List<DisponibilidadItemRowDTO> rows = new ArrayList<>();

        public void setRows(List<DisponibilidadItemRowDTO> rows) {
            this.rows = rows != null ? rows : new ArrayList<>();
            fireTableDataChanged();
        }

        public DisponibilidadItemRowDTO getRow(int rowIndex) {
            return rows.get(rowIndex);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DisponibilidadItemRowDTO row = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> translateTipo(row.getTipoItem());
                case 1 -> row.getNombre();
                case 2 -> row.getGrupo();
                case 3 -> row.getTextoModo();
                case 4 -> row.getTextoStock();
                case 5 -> row.getTextoEstado();
                default -> "";
            };
        }

        @Override public Class<?> getColumnClass(int columnIndex) { return String.class; }
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }

        private String translateTipo(String tipo) {
            if (tipo == null) {
                return "";
            }
            return switch (tipo.trim().toUpperCase()) {
                case "PRODUCTO" -> I18n.t("availability.type.product");
                case "EXTRA" -> I18n.t("availability.type.extra");
                default -> tipo;
            };
        }
    }
}

package ui.dialog;

import dtoS.TicketClienteDTO;
import dtoS.TicketDevolucionDTO;
import dtoS.TicketHoyRowDTO;
import service.AppServices;
import ui.common.TecladoVirtualDialog;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
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
 * Diálogo unificado de tickets del día.
 *
 * Responsabilidades:
 * - listar ventas, devoluciones o ambos
 * - filtrar por tipo de registro
 * - buscar dentro del filtro actual
 * - abrir el diálogo correcto según el tipo:
 *   - TicketClienteDialog
 *   - TicketDevolucionDialog
 */
public class TicketsHoyDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =====================================================
    // 1) DEPENDENCIAS
    // =====================================================

    private final AppServices services;

    // =====================================================
    // 2) COMPONENTES UI
    // =====================================================

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<String> cmbFiltroTipo;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnRecargar;
    private JButton btnAbrir;
    private JButton btnCerrar;

    // =====================================================
    // 3) ESTADO
    // =====================================================

    private List<TicketHoyRowDTO> rows = new ArrayList<>();

    // =====================================================
    // 4) CONSTRUCTOR
    // =====================================================

    public TicketsHoyDialog(Window owner, AppServices services) {
        super(owner, "Tickets del día", ModalityType.APPLICATION_MODAL);

        if (services == null) {
            throw new IllegalArgumentException("AppServices no puede ser null");
        }

        this.services = services;

        buildUi();
        loadRowsByCurrentFilter();

        setMinimumSize(new Dimension(1120, 580));
        setPreferredSize(new Dimension(1180, 650));
        pack();
        setLocationRelativeTo(owner);
    }

    public void showDialog() {
        setVisible(true);
    }

    // =====================================================
    // 5) UI
    // =====================================================

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        // -------------------------------------------------
        // NORTH: título + filtros
        // -------------------------------------------------
        JPanel top = new JPanel(new BorderLayout(10, 12));
        top.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout(0, 4));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("TICKETS DEL DÍA");
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Consulta ventas y devoluciones registradas hoy");
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblSubtitle, BorderLayout.CENTER);

        top.add(titlePanel, BorderLayout.NORTH);

        JPanel filtersCard = InformeUiTheme.createCardPanel(new BorderLayout());
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);

        JLabel lblMostrar = InformeUiTheme.createFieldLabel("Mostrar:");

        cmbFiltroTipo = new JComboBox<>(new String[]{"AMBOS", "VENTAS", "DEVOLUCIONES"});
        InformeUiTheme.styleCombo(cmbFiltroTipo);
        cmbFiltroTipo.setPreferredSize(new Dimension(170, 38));

        JLabel lblBuscar = InformeUiTheme.createFieldLabel("Buscar:");

        txtBuscar = new JTextField(22);
        InformeUiTheme.styleTextField(txtBuscar);
        txtBuscar.setPreferredSize(new Dimension(260, 38));

        JPanel buscarWrapper = new JPanel(new BorderLayout(6, 0));
        buscarWrapper.setOpaque(false);
        buscarWrapper.add(txtBuscar, BorderLayout.CENTER);

        JButton btnTecladoBuscar = new JButton("⌨");
        InformeUiTheme.styleSecondaryButton(btnTecladoBuscar);
        btnTecladoBuscar.setToolTipText("Abrir teclado táctil");
        btnTecladoBuscar.setPreferredSize(new Dimension(54, 38));
        btnTecladoBuscar.addActionListener(e ->
                TecladoVirtualDialog.showAlfanumerico(
                        this,
                        txtBuscar,
                        "Teclado - Buscar ticket",
                        60
                )
        );

        buscarWrapper.add(btnTecladoBuscar, BorderLayout.EAST);

        btnBuscar = new JButton("BUSCAR");
        InformeUiTheme.stylePrimaryButton(btnBuscar);

        btnRecargar = new JButton("RECARGAR");
        InformeUiTheme.styleSecondaryButton(btnRecargar);

        cmbFiltroTipo.addActionListener(e -> loadRowsByCurrentFilter());

        btnBuscar.addActionListener(e -> searchRowsByCurrentFilter());

        btnRecargar.addActionListener(e -> {
            txtBuscar.setText("");
            loadRowsByCurrentFilter();
        });

        txtBuscar.addActionListener(e -> searchRowsByCurrentFilter());

        filters.add(lblMostrar);
        filters.add(cmbFiltroTipo);
        filters.add(Box.createHorizontalStrut(12));
        filters.add(lblBuscar);
        filters.add(buscarWrapper);
        filters.add(btnBuscar);
        filters.add(btnRecargar);

        filtersCard.add(filters, BorderLayout.CENTER);
        top.add(filtersCard, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        // -------------------------------------------------
        // CENTER: tabla
        // -------------------------------------------------
        tableModel = new DefaultTableModel(
                new Object[]{"Tipo", "ID", "Referencia", "Fecha", "Pedido", "Pago/Reembolso", "Total", "Empleado"},
                0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        InformeUiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    abrirRegistroSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        InformeUiTheme.styleScrollPane(scroll);

        root.add(scroll, BorderLayout.CENTER);

        // -------------------------------------------------
        // SOUTH: acciones
        // -------------------------------------------------
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel lblHint = new JLabel("Doble clic sobre una fila para abrir el ticket.");
        lblHint.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        btnAbrir = new JButton("ABRIR");
        InformeUiTheme.stylePrimaryButton(btnAbrir);

        btnCerrar = new JButton("CERRAR");
        InformeUiTheme.styleSecondaryButton(btnCerrar);

        btnAbrir.addActionListener(e -> abrirRegistroSeleccionado());
        btnCerrar.addActionListener(e -> dispose());

        actions.add(btnAbrir);
        actions.add(btnCerrar);

        bottom.add(lblHint, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =====================================================
    // 6) CARGA DE DATOS SEGÚN FILTRO
    // =====================================================

    private void loadRowsByCurrentFilter() {
        try {
            String filtro = getFiltroActual();
            List<TicketHoyRowDTO> data = new ArrayList<>();

            if ("AMBOS".equals(filtro) || "VENTAS".equals(filtro)) {
                data.addAll(services.ticketClienteService.getTicketsHoy());
            }

            if ("AMBOS".equals(filtro) || "DEVOLUCIONES".equals(filtro)) {
                data.addAll(services.devolucionTicketService.getDevolucionesHoy());
            }

            sortRowsByFechaDesc(data);
            rows = data;
            reloadTable();

        } catch (Exception ex) {
            ex.printStackTrace();

            TpvDialogUtils.showError(
                    this,
                    "Error al cargar tickets",
                    "No se pudieron cargar los tickets del día.\n\n" + ex.getMessage()
            );
        }
    }

    private void searchRowsByCurrentFilter() {
        try {
            String filtro = getFiltroActual();
            String query = txtBuscar.getText();

            List<TicketHoyRowDTO> data = new ArrayList<>();

            if ("AMBOS".equals(filtro) || "VENTAS".equals(filtro)) {
                data.addAll(services.ticketClienteService.searchTickets(query));
            }

            if ("AMBOS".equals(filtro) || "DEVOLUCIONES".equals(filtro)) {
                data.addAll(services.devolucionTicketService.searchDevoluciones(query));
            }

            sortRowsByFechaDesc(data);
            rows = data;
            reloadTable();

        } catch (Exception ex) {
            ex.printStackTrace();

            TpvDialogUtils.showError(
                    this,
                    "Error al buscar tickets",
                    "No se pudo realizar la búsqueda.\n\n" + ex.getMessage()
            );
        }
    }

    private String getFiltroActual() {
        Object selected = cmbFiltroTipo.getSelectedItem();
        return selected != null ? selected.toString() : "AMBOS";
    }

    private void sortRowsByFechaDesc(List<TicketHoyRowDTO> data) {
        data.sort((a, b) -> {
            LocalDateTime fa = a != null ? a.getFechaGeneracion() : null;
            LocalDateTime fb = b != null ? b.getFechaGeneracion() : null;

            if (fa == null && fb == null) return 0;
            if (fa == null) return 1;
            if (fb == null) return -1;

            return fb.compareTo(fa);
        });
    }

    private void reloadTable() {
        tableModel.setRowCount(0);

        for (TicketHoyRowDTO row : rows) {
            tableModel.addRow(new Object[]{
                    formatTipo(row),
                    formatIdPrincipal(row),
                    formatReferencia(row),
                    formatFecha(row),
                    safe(row.getNombrePedido()),
                    formatMetodoPago(row.getMetodoPago()),
                    formatMoney(row.getTotal()) + " €",
                    safe(row.getNombreEmpleado())
            });
        }

        if (!rows.isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    // =====================================================
    // 7) ABRIR REGISTRO SELECCIONADO
    // =====================================================

    private void abrirRegistroSeleccionado() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow < 0) {
            TpvDialogUtils.showWarning(
                    this,
                    "Abrir ticket",
                    "Selecciona un registro de la tabla."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        if (modelRow < 0 || modelRow >= rows.size()) {
            return;
        }

        TicketHoyRowDTO row = rows.get(modelRow);

        try {
            if (row.isDevolucion()) {
                if (!row.hasIdDevolucion()) {
                    throw new IllegalStateException("La fila de devolución no tiene idDevolucion válido.");
                }

                TicketDevolucionDTO ticket = services.devolucionTicketService
                        .getTicketByDevolucion(row.getIdDevolucion());

                TicketDevolucionDialog dialog = new TicketDevolucionDialog(this, ticket);
                dialog.showDialog();
                return;
            }

            TicketClienteDTO ticket = services.ticketClienteService.getTicketByVenta(row.getIdVenta());
            TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
            dialog.showDialog();

        } catch (Exception e) {
            e.printStackTrace();

            TpvDialogUtils.showError(
                    this,
                    "Error",
                    "No se pudo abrir el registro.\n\n" + e.getMessage()
            );
        }
    }

    // =====================================================
    // 8) HELPERS DE FORMATO
    // =====================================================

    private String formatTipo(TicketHoyRowDTO row) {
        if (row == null) {
            return "";
        }

        if (row.isDevolucion()) {
            return "DEVOLUCIÓN";
        }

        return "VENTA";
    }

    private String formatIdPrincipal(TicketHoyRowDTO row) {
        if (row == null) {
            return "";
        }

        if (row.isDevolucion() && row.hasIdDevolucion()) {
            return String.valueOf(row.getIdDevolucion());
        }

        return String.valueOf(row.getIdVenta());
    }

    private String formatReferencia(TicketHoyRowDTO row) {
        if (row == null) {
            return "";
        }

        if (row.isDevolucion() && row.hasIdVentaOriginal()) {
            return "Venta #" + row.getIdVentaOriginal();
        }

        return "-";
    }

    private String formatFecha(TicketHoyRowDTO row) {
        if (row == null || row.getFechaGeneracion() == null) {
            return "";
        }

        return row.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String formatMetodoPago(String metodo) {
        if (metodo == null) {
            return "";
        }

        return switch (metodo.trim().toUpperCase()) {
            case "EFECTIVO" -> "Efectivo";
            case "TARJETA" -> "Tarjeta";
            case "VALE" -> "Vale";
            case "MIXTO" -> "Mixto";
            default -> metodo;
        };
    }

    private String formatMoney(BigDecimal amount) {
        BigDecimal safe = amount != null ? amount : BigDecimal.ZERO;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(safe);
    }

    private String safe(String txt) {
        return txt == null ? "" : txt;
    }
}
package ui.dialog;

import dtoS.TicketClienteDTO;
import dtoS.TicketDevolucionDTO;
import dtoS.TicketHoyRowDTO;
import service.AppServices;

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
import java.util.Comparator;
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
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(30, 30, 30));

        // -------------------------------------------------
        // NORTH: título + filtros
        // -------------------------------------------------
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        JLabel lblTitle = new JLabel("TICKETS DEL DÍA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(new Color(245, 245, 245));
        top.add(lblTitle, BorderLayout.NORTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);

        cmbFiltroTipo = new JComboBox<>(new String[]{"AMBOS", "VENTAS", "DEVOLUCIONES"});
        txtBuscar = new JTextField(22);
        btnBuscar = new JButton("BUSCAR");
        btnRecargar = new JButton("RECARGAR");

        cmbFiltroTipo.addActionListener(e -> loadRowsByCurrentFilter());
        btnBuscar.addActionListener(e -> searchRowsByCurrentFilter());
        btnRecargar.addActionListener(e -> {
            txtBuscar.setText("");
            loadRowsByCurrentFilter();
        });

        filters.add(new JLabel("Mostrar:"));
        filters.add(cmbFiltroTipo);
        filters.add(Box.createHorizontalStrut(12));
        filters.add(new JLabel("Buscar:"));
        filters.add(txtBuscar);
        filters.add(btnBuscar);
        filters.add(btnRecargar);

        top.add(filters, BorderLayout.CENTER);

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
        table.setRowHeight(28);
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
        root.add(scroll, BorderLayout.CENTER);

        // -------------------------------------------------
        // SOUTH: acciones
        // -------------------------------------------------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        btnAbrir = new JButton("ABRIR");
        btnCerrar = new JButton("CERRAR");

        btnAbrir.addActionListener(e -> abrirRegistroSeleccionado());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnAbrir);
        bottom.add(btnCerrar);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =====================================================
    // 6) CARGA DE DATOS SEGÚN FILTRO
    // =====================================================

    private void loadRowsByCurrentFilter() {
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
    }

    private void searchRowsByCurrentFilter() {
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
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un registro de la tabla.",
                    "Abrir ticket",
                    JOptionPane.WARNING_MESSAGE
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

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir el registro.\n\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
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
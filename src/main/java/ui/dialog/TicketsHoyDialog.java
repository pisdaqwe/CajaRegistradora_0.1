package ui.dialog;

import dtoS.TicketClienteDTO;
import dtoS.TicketHoyRowDTO;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketsHoyDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final AppServices services;

    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnAbrir;
    private JButton btnRecargar;
    private JButton btnCerrar;

    private List<TicketHoyRowDTO> rows = new ArrayList<>();

    public TicketsHoyDialog(Window owner, AppServices services) {
        super(owner, "Tickets del día", ModalityType.APPLICATION_MODAL);

        if (services == null) {
            throw new IllegalArgumentException("AppServices no puede ser null");
        }

        this.services = services;

        buildUi();
        loadTicketsHoy();

        setMinimumSize(new Dimension(920, 520));
        setPreferredSize(new Dimension(980, 580));
        pack();
        setLocationRelativeTo(owner);
    }

    public void showDialog() {
        setVisible(true);
    }

    // =====================================================
    // 1. UI
    // =====================================================

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(30, 30, 30));

        JLabel lblTitle = new JLabel("TICKETS DEL DÍA");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(new Color(245, 245, 245));
        root.add(lblTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID Venta", "Fecha", "Pedido", "Pago", "Total", "Empleado"},
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
                    abrirTicketSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        btnAbrir = new JButton("ABRIR TICKET");
        btnRecargar = new JButton("RECARGAR");
        btnCerrar = new JButton("CERRAR");

        btnAbrir.addActionListener(e -> abrirTicketSeleccionado());
        btnRecargar.addActionListener(e -> loadTicketsHoy());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnAbrir);
        bottom.add(btnRecargar);
        bottom.add(btnCerrar);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =====================================================
    // 2. CARGA DE DATOS
    // =====================================================

    private void loadTicketsHoy() {
        rows = services.ticketClienteService.getTicketsHoy();
        reloadTable();
    }

    private void reloadTable() {
        tableModel.setRowCount(0);

        for (TicketHoyRowDTO row : rows) {
            tableModel.addRow(new Object[]{
                    row.getIdVenta(),
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
    // 3. ACCIONES
    // =====================================================

    private void abrirTicketSeleccionado() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un ticket de la tabla.",
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
            TicketClienteDTO ticket = services.ticketClienteService.getTicketByVenta(row.getIdVenta());
            TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
            dialog.showDialog();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir el ticket.\n\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // 4. HELPERS DE FORMATO
    // =====================================================

    private String formatFecha(TicketHoyRowDTO row) {
        if (row == null || row.getFechaGeneracion() == null) {
            return "";
        }
        return row.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String formatMetodoPago(String metodo) {
        if (metodo == null) return "";
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

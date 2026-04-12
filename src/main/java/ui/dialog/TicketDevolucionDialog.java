package ui.dialog;

import dtoS.TicketDevolucionDTO;
import dtoS.TicketDevolucionItemDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Diálogo de vista previa del ticket de devolución.
 *
 * Responsabilidades:
 * - mostrar en pantalla el ticket de devolución ya parseado
 * - reutilizar la misma idea visual del ticket cliente
 * - dejar preparada la acción futura de reimpresión real
 *
 * IMPORTANTE:
 * - NO parsea JSON
 * - recibe un TicketDevolucionDTO ya listo
 */
public class TicketDevolucionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // =====================================================
    // 1) ESTADO
    // =====================================================

    private final TicketDevolucionDTO ticket;

    // =====================================================
    // 2) COMPONENTES UI
    // =====================================================

    private JTextArea txtTicket;
    private JButton btnCerrar;
    private JButton btnReimprimir;

    // =====================================================
    // 3) CONSTRUCTOR
    // =====================================================

    public TicketDevolucionDialog(Window owner, TicketDevolucionDTO ticket) {
        super(owner, "Ticket devolución", ModalityType.APPLICATION_MODAL);

        if (ticket == null) {
            throw new IllegalArgumentException("TicketDevolucionDTO no puede ser null");
        }

        this.ticket = ticket;

        buildUi();
        loadData();

        setMinimumSize(new Dimension(460, 680));
        setPreferredSize(new Dimension(520, 760));
        pack();
        setLocationRelativeTo(owner);
    }

    public void showDialog() {
        setVisible(true);
    }

    // =====================================================
    // 4) UI
    // =====================================================

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(30, 30, 30));

        JLabel lblTitle = new JLabel("VISTA PREVIA DEL TICKET DE DEVOLUCIÓN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(new Color(245, 245, 245));
        root.add(lblTitle, BorderLayout.NORTH);

        txtTicket = new JTextArea();
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        txtTicket.setLineWrap(false);
        txtTicket.setWrapStyleWord(false);
        txtTicket.setBackground(new Color(250, 248, 240));
        txtTicket.setForeground(Color.BLACK);
        txtTicket.setMargin(new Insets(14, 14, 14, 14));

        JScrollPane scroll = new JScrollPane(txtTicket);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120)));
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        btnReimprimir = new JButton("REIMPRIMIR");
        btnCerrar = new JButton("CERRAR");

        btnReimprimir.addActionListener(e -> onReimprimir());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnReimprimir);
        bottom.add(btnCerrar);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =====================================================
    // 5) CARGA DE DATOS
    // =====================================================

    private void loadData() {
        txtTicket.setText(buildTicketText());
        txtTicket.setCaretPosition(0);
    }

    // =====================================================
    // 6) ACCIONES
    // =====================================================

    private void onReimprimir() {
        JOptionPane.showMessageDialog(
                this,
                "Reimpresión lógica preparada.\n\nDe momento esta pantalla muestra la vista previa del ticket de devolución.",
                "Reimprimir devolución",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // 7) RENDER DEL TICKET
    // =====================================================

    private String buildTicketText() {
        StringBuilder sb = new StringBuilder();

        appendCentered(sb, "TPV CAFETERÍA");
        appendCentered(sb, "TICKET DEVOLUCIÓN");
        sb.append(line()).append("\n");

        // -------------------------------------------------
        // Cabecera temporal
        // -------------------------------------------------
        if (ticket.getFechaGeneracion() != null) {
            sb.append("Fecha devolución: ")
                    .append(ticket.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("\n");
        }

        if (ticket.getFechaVentaOriginal() != null && !ticket.getFechaVentaOriginal().isBlank()) {
            sb.append("Fecha venta orig.: ").append(ticket.getFechaVentaOriginal()).append("\n");
        }

        sb.append("Devolución: ").append(ticket.getIdDevolucion()).append("\n");
        sb.append("Venta original: ").append(ticket.getIdVentaOriginal()).append("\n");
        sb.append("Pedido: ").append(safe(ticket.getNombrePedido(), "Cliente")).append("\n");
        sb.append("Servicio: ").append(formatTipoServicio(ticket.getTipoServicio())).append("\n");
        sb.append("Pago original: ").append(formatMetodoPago(ticket.getMetodoPagoOriginal())).append("\n");
        sb.append("Reembolso: ").append(formatMetodoPago(ticket.getMetodoReembolso())).append("\n");

        if (ticket.hasMotivo()) {
            sb.append("Motivo: ").append(ticket.getMotivo().trim()).append("\n");
        }

        if (ticket.hasObservaciones()) {
            sb.append("Observ.: ").append(ticket.getObservaciones().trim()).append("\n");
        }

        sb.append(line()).append("\n");

        // -------------------------------------------------
        // Líneas devueltas
        // -------------------------------------------------
        List<TicketDevolucionItemDTO> items = ticket.getItems();
        if (items != null) {
            for (TicketDevolucionItemDTO item : items) {
                appendItem(sb, item);
            }
        }

        sb.append(line()).append("\n");

        // -------------------------------------------------
        // Total final devuelto
        // -------------------------------------------------
        sb.append(padRight("TOTAL DEVUELTO", 24))
                .append(padLeft(formatMoney(ticket.getTotalDevuelto()) + " €", 14))
                .append("\n");

        sb.append(line()).append("\n");
        appendCentered(sb, "Operación de devolución registrada");

        return sb.toString();
    }

    private void appendItem(StringBuilder sb, TicketDevolucionItemDTO item) {
        String nombre = safe(item.getNombreProducto(), "PRODUCTO");
        String tamano = safe(item.getTamano(), "");
        String nombreLinea = tamano.isBlank() ? nombre : nombre + " (" + tamano + ")";

        sb.append(nombreLinea).append("\n");

        sb.append("  ")
                .append(padRight("x" + item.getCantidadDevuelta(), 6))
                .append(padLeft(formatMoney(item.getSubtotalFinalDevuelto()) + " €", 30))
                .append("\n");

        BigDecimal descuento = safe(item.getImporteDescuentoDevuelto());
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("    Descuento devuelto: -")
                    .append(formatMoney(descuento))
                    .append(" €\n");
        }

        if (item.isReponeStock()) {
            sb.append("    Repone stock: SÍ\n");
        } else {
            sb.append("    Repone stock: NO\n");
        }

        if (item.hasDescripcionResumen()) {
            sb.append("    ").append(item.getDescripcionResumen().trim()).append("\n");
        }

        sb.append("\n");
    }

    // =====================================================
    // 8) HELPERS DE FORMATO
    // =====================================================

    private String line() {
        return "----------------------------------------";
    }

    private void appendCentered(StringBuilder sb, String text) {
        if (text == null) {
            text = "";
        }

        int width = 40;
        if (text.length() >= width) {
            sb.append(text).append("\n");
            return;
        }

        int left = (width - text.length()) / 2;
        sb.append(" ".repeat(Math.max(0, left))).append(text).append("\n");
    }

    private String padRight(String txt, int width) {
        String value = txt == null ? "" : txt;
        if (value.length() >= width) {
            return value;
        }
        return value + " ".repeat(width - value.length());
    }

    private String padLeft(String txt, int width) {
        String value = txt == null ? "" : txt;
        if (value.length() >= width) {
            return value;
        }
        return " ".repeat(width - value.length()) + value;
    }

    private String safe(String text, String fallback) {
        return (text == null || text.isBlank()) ? fallback : text.trim();
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private String formatTipoServicio(String tipo) {
        if (tipo == null) {
            return "";
        }

        return switch (tipo.trim().toUpperCase()) {
            case "PARA_LLEVAR" -> "Para llevar";
            case "PARA_TOMAR" -> "Para tomar";
            default -> tipo;
        };
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
}

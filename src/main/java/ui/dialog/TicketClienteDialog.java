package ui.dialog;

import dtoS.TicketClienteComboDTO;
import dtoS.TicketClienteDTO;
import dtoS.TicketClienteItemDTO;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;

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
 * Vista previa lógica del ticket cliente.
 *
 * AJUSTE ACTUAL:
 * - ahora también muestra el café seleccionado de cada bebida.
 */
public class TicketClienteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TicketClienteDTO ticket;

    private JTextArea txtTicket;
    private JButton btnCerrar;
    private JButton btnReimprimir;

    public TicketClienteDialog(Window owner, TicketClienteDTO ticket) {
        super(owner, "Ticket cliente", ModalityType.APPLICATION_MODAL);

        if (ticket == null) {
            throw new IllegalArgumentException("TicketClienteDTO no puede ser null");
        }

        this.ticket = ticket;

        buildUi();
        loadData();

        setMinimumSize(new Dimension(460, 650));
        setPreferredSize(new Dimension(500, 720));
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
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("VISTA PREVIA DEL TICKET", SwingConstants.CENTER);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Ticket de cliente generado desde la venta", SwingConstants.CENTER);
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSubtitle, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);

        txtTicket = new JTextArea();
        txtTicket.setEditable(false);
        txtTicket.setFont(InformeUiTheme.FONT_TICKET);
        txtTicket.setLineWrap(false);
        txtTicket.setWrapStyleWord(false);
        txtTicket.setMargin(new Insets(14, 14, 14, 14));

        InformeUiTheme.styleTextArea(txtTicket);

        JScrollPane scroll = new JScrollPane(txtTicket);
        InformeUiTheme.styleScrollPane(scroll);

        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        btnReimprimir = new JButton("REIMPRIMIR");
        InformeUiTheme.stylePrimaryButton(btnReimprimir);

        btnCerrar = new JButton("CERRAR");
        InformeUiTheme.styleSecondaryButton(btnCerrar);

        btnReimprimir.addActionListener(e -> onReimprimir());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnReimprimir);
        bottom.add(btnCerrar);

        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // =====================================================
    // 2. CARGA DE DATOS
    // =====================================================

    private void loadData() {
        txtTicket.setText(buildTicketText());
        txtTicket.setCaretPosition(0);
    }

    // =====================================================
    // 3. ACCIONES
    // =====================================================

    private void onReimprimir() {
        TpvDialogUtils.showInfo(
                this,
                "Reimprimir ticket",
                "Reimpresión lógica preparada.\n\nDe momento esta pantalla muestra la vista previa del ticket."
        );
    }

    // =====================================================
    // 4. RENDER DEL TICKET
    // =====================================================

    private String buildTicketText() {
        StringBuilder sb = new StringBuilder();

        appendCentered(sb, "TPV CAFETERÍA");
        appendCentered(sb, "TICKET CLIENTE");
        sb.append(line()).append("\n");

        if (ticket.getFechaGeneracion() != null) {
            sb.append("Fecha: ")
                    .append(ticket.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("\n");
        }

        sb.append("Venta: ").append(ticket.getIdVenta()).append("\n");
        sb.append("Pedido: ").append(safe(ticket.getNombrePedido(), "Cliente")).append("\n");
        sb.append("Servicio: ").append(formatTipoServicio(ticket.getTipoServicio())).append("\n");
        sb.append("Pago: ").append(formatMetodoPago(ticket.getMetodoPago())).append("\n");

        sb.append(line()).append("\n");

        List<TicketClienteItemDTO> items = ticket.getItems();
        if (items != null) {
            for (TicketClienteItemDTO item : items) {
                appendItem(sb, item);
            }
        }

        // =====================================================
        // COMBOS
        // =====================================================
        if (ticket.hasCombos()) {
            sb.append(line()).append("\n");

            for (TicketClienteComboDTO combo : ticket.getCombos()) {
                appendCombo(sb, combo);
            }
        }

        sb.append(line()).append("\n");

        // =====================================================
        // AHORRO POR COMBOS
        // =====================================================
        if (ticket.hasCombos()) {
            sb.append(padRight("AHORRO COMBOS", 24))
                    .append(padLeft("-" + formatMoney(ticket.getTotalAhorroCombos()) + " €", 14))
                    .append("\n");
        }

        // =====================================================
        // DESCUENTO APLICADO
        // =====================================================
        if (ticket.hasDescuento()) {
            appendDescuento(sb);
        }

        sb.append(padRight("TOTAL", 24))
                .append(padLeft(formatMoney(ticket.getTotal()) + " €", 14))
                .append("\n");

        sb.append(padRight("ENTREGADO", 24))
                .append(padLeft(formatMoney(ticket.getMontoPagado()) + " €", 14))
                .append("\n");

        sb.append(padRight("CAMBIO", 24))
                .append(padLeft(formatMoney(ticket.getCambio()) + " €", 14))
                .append("\n");

        sb.append(line()).append("\n");
        appendCentered(sb, "Gracias por su visita");

        return sb.toString();
    }

    /**
     * Render de una línea del ticket.
     *
     * AJUSTE ACTUAL:
     * - ahora mete el café debajo del nombre si existe.
     */
    private void appendItem(StringBuilder sb, TicketClienteItemDTO item) {
        String nombre = safe(item.getNombreProducto(), "PRODUCTO");
        String tamano = safe(item.getTamano(), "");
        String nombreLinea = tamano.isBlank() ? nombre : nombre + " (" + tamano + ")";

        sb.append(nombreLinea).append("\n");

        sb.append("  ")
                .append(padRight("x" + item.getCantidad(), 6))
                .append(padLeft(formatMoney(item.getSubtotal()) + " €", 30))
                .append("\n");

        // =====================================================
        // NUEVO BLOQUE: café seleccionado
        // =====================================================
        if (item.hasTipoCafe()) {
            sb.append("    Café: ").append(item.getTipoCafe().trim()).append("\n");
        }

        if (item.getExtras() != null) {
            for (String extra : item.getExtras()) {
                if (extra != null && !extra.isBlank()) {
                    sb.append("    + ").append(extra.trim()).append("\n");
                }
            }
        }

        if (item.getPersonalizaciones() != null) {
            for (String p : item.getPersonalizaciones()) {
                if (p != null && !p.isBlank()) {
                    sb.append("    * ").append(p.trim()).append("\n");
                }
            }
        }

        if (item.getAskMe() != null) {
            for (String ask : item.getAskMe()) {
                if (ask != null && !ask.isBlank()) {
                    sb.append("    Ask Me: ").append(ask.trim()).append("\n");
                }
            }
        }

        sb.append("\n");
    }

    private void appendDescuento(StringBuilder sb) {
        String nombre = safe(ticket.getNombreDescuento(), "Descuento");
        String codigo = safe(ticket.getCodigoDescuento(), "");
        String origen = safe(ticket.getOrigenDescuento(), "");

        sb.append(nombre).append("\n");

        // Solo mostrar código si es una promo/cupón real
        if (!codigo.isBlank() && "PROMOCIONAL".equalsIgnoreCase(origen)) {
            sb.append("  Código: ").append(codigo).append("\n");
        }

        sb.append(padRight("AHORRO DESCUENTO", 24))
                .append(padLeft("-" + formatMoney(ticket.getImporteDescuento()) + " €", 14))
                .append("\n");
    }

    private void appendCombo(StringBuilder sb, TicketClienteComboDTO combo) {
        String nombre = safe(combo.getNombreCombo(), "COMBO");

        sb.append("COMBO ").append(nombre.toUpperCase()).append("\n");

        sb.append("  ")
                .append(padRight("", 6))
                .append(padLeft(formatMoney(combo.getPrecioFinal()) + " €", 30))
                .append("\n");

        if (combo.getAhorroTotal() != null && combo.getAhorroTotal().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("    Ahorro: -").append(formatMoney(combo.getAhorroTotal())).append(" €\n");
        }

        sb.append("\n");
    }

    // =====================================================
    // 5. HELPERS DE FORMATO
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

    private String formatTipoServicio(String tipo) {
        if (tipo == null) return "";
        return switch (tipo.trim().toUpperCase()) {
            case "PARA_LLEVAR" -> "Para llevar";
            case "PARA_TOMAR" -> "Para tomar";
            default -> tipo;
        };
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
}
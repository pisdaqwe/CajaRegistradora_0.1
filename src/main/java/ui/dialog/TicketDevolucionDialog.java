package ui.dialog;

import dtoS.TicketDevolucionDTO;
import dtoS.TicketDevolucionItemDTO;
import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketDevolucionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TicketDevolucionDTO ticket;

    private JTextArea txtTicket;
    private JButton btnCerrar;
    private JButton btnReimprimir;

    public TicketDevolucionDialog(Window owner, TicketDevolucionDTO ticket) {
        super(owner, I18n.t("ticketRefund.title"), ModalityType.APPLICATION_MODAL);

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

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel header = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(I18n.t("ticketRefund.header"), SwingConstants.CENTER);
        lblTitle.setIcon(TpvIconFactory.history(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("ticketRefund.subtitle"), SwingConstants.CENTER);
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

        JPanel bottom = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        btnReimprimir = new JButton(I18n.t("ticketRefund.reprint"));
        btnReimprimir.setIcon(TpvIconFactory.report(18, Color.WHITE));
        btnReimprimir.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnReimprimir);

        btnCerrar = new JButton(I18n.t("common.close"));
        btnCerrar.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnCerrar.setIconTextGap(8);
        InformeUiTheme.styleSecondaryButton(btnCerrar);

        btnReimprimir.addActionListener(e -> onReimprimir());
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnReimprimir);
        bottom.add(btnCerrar);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadData() {
        txtTicket.setText(buildTicketText());
        txtTicket.setCaretPosition(0);
    }

    private void onReimprimir() {
        TpvDialogUtils.showInfo(
                this,
                I18n.t("ticketRefund.reprintTitle"),
                I18n.t("ticketRefund.reprintPrepared")
        );
    }

    private String buildTicketText() {
        StringBuilder sb = new StringBuilder();

        appendCentered(sb, I18n.t("receipt.businessName"));
        appendCentered(sb, I18n.t("receipt.refundTicket"));
        sb.append(line()).append("\n");

        if (ticket.getFechaGeneracion() != null) {
            sb.append(I18n.t("receipt.refundDate")).append(": ")
                    .append(ticket.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("\n");
        }

        if (ticket.getFechaVentaOriginal() != null && !ticket.getFechaVentaOriginal().isBlank()) {
            sb.append(I18n.t("receipt.originalSaleDate")).append(": ").append(ticket.getFechaVentaOriginal()).append("\n");
        }

        sb.append(I18n.t("receipt.refund")).append(": ").append(ticket.getIdDevolucion()).append("\n");
        sb.append(I18n.t("receipt.originalSale")).append(": ").append(ticket.getIdVentaOriginal()).append("\n");
        sb.append(I18n.t("receipt.order")).append(": ").append(safe(ticket.getNombrePedido(), I18n.t("receipt.customer"))).append("\n");
        sb.append(I18n.t("receipt.service")).append(": ").append(formatTipoServicio(ticket.getTipoServicio())).append("\n");
        sb.append(I18n.t("receipt.originalPayment")).append(": ").append(formatMetodoPago(ticket.getMetodoPagoOriginal())).append("\n");
        sb.append(I18n.t("receipt.refundMethod")).append(": ").append(formatMetodoPago(ticket.getMetodoReembolso())).append("\n");

        if (ticket.hasMotivo()) {
            sb.append(I18n.t("receipt.reason")).append(": ").append(ticket.getMotivo().trim()).append("\n");
        }

        if (ticket.hasObservaciones()) {
            sb.append(I18n.t("receipt.notesShort")).append(": ").append(ticket.getObservaciones().trim()).append("\n");
        }

        sb.append(line()).append("\n");

        List<TicketDevolucionItemDTO> items = ticket.getItems();
        if (items != null) {
            for (TicketDevolucionItemDTO item : items) {
                appendItem(sb, item);
            }
        }

        sb.append(line()).append("\n");

        sb.append(padRight(I18n.t("receipt.totalRefunded"), 24))
                .append(padLeft(formatMoney(ticket.getTotalDevuelto()) + " €", 14))
                .append("\n");

        sb.append(line()).append("\n");
        appendCentered(sb, I18n.t("receipt.refundRegistered"));

        return sb.toString();
    }

    private void appendItem(StringBuilder sb, TicketDevolucionItemDTO item) {
        String nombre = safe(item.getNombreProducto(), I18n.t("receipt.product"));
        String tamano = safe(item.getTamano(), "");
        String nombreLinea = tamano.isBlank() ? nombre : nombre + " (" + tamano + ")";

        sb.append(nombreLinea).append("\n");

        sb.append("  ")
                .append(padRight("x" + item.getCantidadDevuelta(), 6))
                .append(padLeft(formatMoney(item.getSubtotalFinalDevuelto()) + " €", 30))
                .append("\n");

        BigDecimal descuento = safe(item.getImporteDescuentoDevuelto());
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("    ").append(I18n.t("receipt.discountRefunded")).append(": -")
                    .append(formatMoney(descuento)).append(" €\n");
        }

        sb.append("    ").append(I18n.t("receipt.restock")).append(": ")
                .append(item.isReponeStock() ? I18n.t("common.yes") : I18n.t("common.no"))
                .append("\n");

        if (item.hasDescripcionResumen()) {
            String descripcion = item.getDescripcionResumen().trim();
            if (!descripcion.equalsIgnoreCase(nombreLinea.trim())) {
                sb.append("    ").append(descripcion).append("\n");
            }
        }

        sb.append("\n");
    }

    private String line() { return "----------------------------------------"; }

    private void appendCentered(StringBuilder sb, String text) {
        if (text == null) text = "";
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
        return value.length() >= width ? value : value + " ".repeat(width - value.length());
    }

    private String padLeft(String txt, int width) {
        String value = txt == null ? "" : txt;
        return value.length() >= width ? value : " ".repeat(width - value.length()) + value;
    }

    private String safe(String text, String fallback) {
        return (text == null || text.isBlank()) ? fallback : text.trim();
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private String formatTipoServicio(String tipo) {
        if (tipo == null) return "";
        return switch (tipo.trim().toUpperCase()) {
            case "PARA_LLEVAR" -> I18n.t("receipt.takeAway");
            case "PARA_TOMAR" -> I18n.t("receipt.eatIn");
            default -> tipo;
        };
    }

    private String formatMetodoPago(String metodo) {
        if (metodo == null) return "";
        return switch (metodo.trim().toUpperCase()) {
            case "EFECTIVO" -> I18n.t("payment.cash");
            case "TARJETA" -> I18n.t("payment.card");
            case "VALE" -> I18n.t("payment.voucher");
            case "MIXTO" -> I18n.t("payment.mixed");
            default -> metodo;
        };
    }

    private String formatMoney(BigDecimal amount) {
        BigDecimal safe = amount != null ? amount : BigDecimal.ZERO;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(I18n.getCurrentLocale());
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(safe);
    }
}

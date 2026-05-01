package ui.dialog;

import dtoS.TicketClienteComboDTO;
import dtoS.TicketClienteDTO;
import dtoS.TicketClienteItemDTO;
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

public class TicketClienteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final TicketClienteDTO ticket;

    private JTextArea txtTicket;
    private JButton btnCerrar;
    private JButton btnReimprimir;

    public TicketClienteDialog(Window owner, TicketClienteDTO ticket) {
        super(owner, I18n.t("ticketClient.title"), ModalityType.APPLICATION_MODAL);

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

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(InformeUiTheme.APP_BG);

        JPanel header = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 4));

        JLabel lblTitle = new JLabel(I18n.t("ticketClient.header"), SwingConstants.CENTER);
        lblTitle.setIcon(TpvIconFactory.report(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(I18n.t("ticketClient.subtitle"), SwingConstants.CENTER);
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

        btnReimprimir = new JButton(I18n.t("ticketClient.reprint"));
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
                I18n.t("ticketClient.reprintTitle"),
                I18n.t("ticketClient.reprintPrepared")
        );
    }

    private String buildTicketText() {
        StringBuilder sb = new StringBuilder();

        appendCentered(sb, I18n.t("receipt.businessName"));
        appendCentered(sb, I18n.t("receipt.clientTicket"));
        sb.append(line()).append("\n");

        if (ticket.getFechaGeneracion() != null) {
            sb.append(I18n.t("receipt.date")).append(": ")
                    .append(ticket.getFechaGeneracion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .append("\n");
        }

        sb.append(I18n.t("receipt.sale")).append(": ").append(ticket.getIdVenta()).append("\n");
        sb.append(I18n.t("receipt.order")).append(": ").append(safe(ticket.getNombrePedido(), I18n.t("receipt.customer"))).append("\n");
        sb.append(I18n.t("receipt.service")).append(": ").append(formatTipoServicio(ticket.getTipoServicio())).append("\n");
        sb.append(I18n.t("receipt.payment")).append(": ").append(formatMetodoPago(ticket.getMetodoPago())).append("\n");

        sb.append(line()).append("\n");

        List<TicketClienteItemDTO> items = ticket.getItems();
        if (items != null) {
            for (TicketClienteItemDTO item : items) {
                appendItem(sb, item);
            }
        }

        if (ticket.hasCombos()) {
            sb.append(line()).append("\n");
            for (TicketClienteComboDTO combo : ticket.getCombos()) {
                appendCombo(sb, combo);
            }
        }

        sb.append(line()).append("\n");

        if (ticket.hasCombos()) {
            sb.append(padRight(I18n.t("receipt.comboSavings"), 24))
                    .append(padLeft("-" + formatMoney(ticket.getTotalAhorroCombos()) + " €", 14))
                    .append("\n");
        }

        if (ticket.hasDescuento()) {
            appendDescuento(sb);
        }

        sb.append(padRight(I18n.t("receipt.total"), 24))
                .append(padLeft(formatMoney(ticket.getTotal()) + " €", 14))
                .append("\n");

        sb.append(padRight(I18n.t("receipt.paid"), 24))
                .append(padLeft(formatMoney(ticket.getMontoPagado()) + " €", 14))
                .append("\n");

        sb.append(padRight(I18n.t("receipt.change"), 24))
                .append(padLeft(formatMoney(ticket.getCambio()) + " €", 14))
                .append("\n");

        sb.append(line()).append("\n");
        appendCentered(sb, I18n.t("receipt.thanks"));

        return sb.toString();
    }

    private void appendItem(StringBuilder sb, TicketClienteItemDTO item) {
        String nombre = safe(item.getNombreProducto(), I18n.t("receipt.product"));
        String tamano = safe(item.getTamano(), "");
        String nombreLinea = tamano.isBlank() ? nombre : nombre + " (" + tamano + ")";

        sb.append(nombreLinea).append("\n");

        sb.append("  ")
                .append(padRight("x" + item.getCantidad(), 6))
                .append(padLeft(formatMoney(item.getSubtotal()) + " €", 30))
                .append("\n");

        if (item.hasTipoCafe()) {
            sb.append("    ").append(I18n.t("receipt.coffee")).append(": ").append(item.getTipoCafe().trim()).append("\n");
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
        String nombre = safe(ticket.getNombreDescuento(), I18n.t("receipt.discount"));
        String codigo = safe(ticket.getCodigoDescuento(), "");
        String origen = safe(ticket.getOrigenDescuento(), "");

        sb.append(nombre).append("\n");

        if (!codigo.isBlank() && "PROMOCIONAL".equalsIgnoreCase(origen)) {
            sb.append("  ").append(I18n.t("receipt.code")).append(": ").append(codigo).append("\n");
        }

        sb.append(padRight(I18n.t("receipt.discountSavings"), 24))
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
            sb.append("    ").append(I18n.t("receipt.saving")).append(": -")
                    .append(formatMoney(combo.getAhorroTotal())).append(" €\n");
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

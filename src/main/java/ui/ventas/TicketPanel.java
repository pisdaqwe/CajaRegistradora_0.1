package ui.ventas;

import model.TicketRow;
import model.TicketSession;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class TicketPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final TicketSession ticketSession;
    private final Runnable onSelectionChanged;
    private boolean syncingSelection = false;

    private final DefaultListModel<TicketRow> model = new DefaultListModel<>();
    private final JList<TicketRow> list = new JList<>(model);

    private final JLabel lblSubtotalValue = new JLabel("0,00€", SwingConstants.RIGHT);
    private final JLabel lblAhorroCombosValue = new JLabel("0,00€", SwingConstants.RIGHT);
    private final JLabel lblDescuentoNombreValue = new JLabel("-", SwingConstants.RIGHT);
    private final JLabel lblAhorroDescuentoValue = new JLabel("0,00€", SwingConstants.RIGHT);
    private final JLabel lblTotalValue = new JLabel("0,00€", SwingConstants.RIGHT);

    public TicketPanel(TicketSession ticketSession, Runnable onSelectionChanged) {
        this.ticketSession = ticketSession;
        this.onSelectionChanged = onSelectionChanged;

        setLayout(new BorderLayout(8, 8));
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel(I18n.t("sales.ticket.title"));
        title.setIcon(TpvIconFactory.report(18, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(8);
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);
        return title;
    }

    private JComponent buildList() {
        list.setCellRenderer(new TicketRowRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(InformeUiTheme.PANEL_BG);
        list.setForeground(InformeUiTheme.TEXT_PRIMARY);
        list.setSelectionBackground(InformeUiTheme.STARBUCKS_GREEN);
        list.setSelectionForeground(Color.WHITE);

        list.setFixedCellHeight(58);
        list.setBorder(BorderFactory.createLineBorder(InformeUiTheme.BORDER));

        list.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            if (syncingSelection) return;

            int idx = list.getSelectedIndex();
            ticketSession.setSelectedFlatIndex(idx);

            if (onSelectionChanged != null) {
                onSelectionChanged.run();
            }
        });

        JScrollPane sp = new JScrollPane(list);
        InformeUiTheme.styleScrollPane(sp);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JComponent buildSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(InformeUiTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addSummaryRow(panel, gbc, I18n.t("sales.ticket.subtotal"), lblSubtotalValue, new Color(220, 220, 220), false);
        addSummaryRow(panel, gbc, I18n.t("sales.ticket.comboSavings"), lblAhorroCombosValue, new Color(120, 220, 140), false);
        addSummaryRow(panel, gbc, I18n.t("sales.ticket.discount"), lblDescuentoNombreValue, new Color(170, 200, 255), false);
        addSummaryRow(panel, gbc, I18n.t("sales.ticket.discountSavings"), lblAhorroDescuentoValue, new Color(120, 220, 140), false);
        addSummaryRow(panel, gbc, I18n.t("sales.ticket.total"), lblTotalValue, InformeUiTheme.ACCENT_GOLD, true);

        styleSummaryValueLabel(lblSubtotalValue, new Color(220, 220, 220), false);
        styleSummaryValueLabel(lblAhorroCombosValue, new Color(120, 220, 140), false);
        styleSummaryValueLabel(lblDescuentoNombreValue, new Color(170, 200, 255), false);
        styleSummaryValueLabel(lblAhorroDescuentoValue, new Color(120, 220, 140), false);
        styleSummaryValueLabel(lblTotalValue, InformeUiTheme.ACCENT_GOLD, true);

        return panel;
    }

    private void addSummaryRow(
            JPanel panel,
            GridBagConstraints gbc,
            String labelText,
            JLabel valueLabel,
            Color labelColor,
            boolean total
    ) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(labelColor);
        lbl.setFont(new Font("Monospaced", total ? Font.BOLD : Font.PLAIN, total ? 16 : 14));

        gbc.gridx = 0;
        gbc.weightx = 0.45;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        panel.add(valueLabel, gbc);

        gbc.gridy++;
    }

    private void styleSummaryValueLabel(JLabel label, Color color, boolean total) {
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(new Font("Monospaced", total ? Font.BOLD : Font.PLAIN, total ? 16 : 14));
    }

    private void refreshSummary() {
        BigDecimal subtotal = safe(ticketSession.getTotalSinDescuento());
        BigDecimal ahorroCombos = safe(ticketSession.getAhorroTotalCombos());
        BigDecimal ahorroDescuento = safe(ticketSession.getAhorroTotalDescuento());
        BigDecimal total = safe(ticketSession.getTotal());

        String nombreDescuento = ticketSession.getNombreDescuentoAplicado();
        if (nombreDescuento == null || nombreDescuento.isBlank()) {
            nombreDescuento = "-";
        }

        lblSubtotalValue.setText(formatMoney(subtotal) + "€");
        lblAhorroCombosValue.setText(ahorroCombos.compareTo(BigDecimal.ZERO) > 0
                ? "-" + formatMoney(ahorroCombos) + "€"
                : "0,00€");
        lblDescuentoNombreValue.setText(nombreDescuento);
        lblAhorroDescuentoValue.setText(ahorroDescuento.compareTo(BigDecimal.ZERO) > 0
                ? "-" + formatMoney(ahorroDescuento) + "€"
                : "0,00€");
        lblTotalValue.setText(formatMoney(total) + "€");
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount != null ? amount : BigDecimal.ZERO);
    }

    public void refreshFromTicket() {
        syncingSelection = true;
        try {
            model.clear();

            List<TicketRow> rows = ticketSession.buildRows();
            for (TicketRow r : rows) {
                model.addElement(r);
            }

            int idx = ticketSession.getSelectedFlatIndex();
            if (idx >= 0 && idx < model.size()) {
                list.setSelectedIndex(idx);
                list.ensureIndexIsVisible(idx);
            } else {
                list.clearSelection();
            }

            refreshSummary();

        } finally {
            syncingSelection = false;
        }
    }

    public void syncSelectionFromSession() {
        syncingSelection = true;
        try {
            int idx = ticketSession.getSelectedFlatIndex();
            if (idx >= 0 && idx < model.size()) {
                list.setSelectedIndex(idx);
                list.ensureIndexIsVisible(idx);
            } else {
                list.clearSelection();
            }
        } finally {
            syncingSelection = false;
        }
    }

    public JList<TicketRow> getList() {
        return list;
    }
}

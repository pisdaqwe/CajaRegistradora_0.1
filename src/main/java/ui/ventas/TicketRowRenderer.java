package ui.ventas;

import model.TicketRow;
import enums.TicketRowType;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TicketRowRenderer extends JPanel implements ListCellRenderer<TicketRow> {

    private static final long serialVersionUID = 1L;

    private final JLabel lblLeft = new JLabel();
    private final JLabel lblRight = new JLabel();

    public TicketRowRenderer() {
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.PLAIN, 15f));
        lblRight.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 15f));
        lblRight.setHorizontalAlignment(SwingConstants.RIGHT);

        lblLeft.setVerticalAlignment(SwingConstants.TOP);

        add(lblLeft, BorderLayout.CENTER);
        add(lblRight, BorderLayout.EAST);

        setOpaque(true);
        lblLeft.setOpaque(false);
        lblRight.setOpaque(false);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends TicketRow> list,
            TicketRow value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        if (value == null) {
            lblLeft.setText("");
            lblRight.setText("");
            return this;
        }

        // =====================================================
        // 1) TEXTO IZQUIERDO + SANGRÍA
        // =====================================================
        String prefix = "";

        if (value.getType() == TicketRowType.EXTRA
                || value.getType() == TicketRowType.PERSONALIZACION
                || value.getType() == TicketRowType.ASK_ME) {
            prefix = "   ";
        }

        if (value.getType() == TicketRowType.AHORRO
                || value.getType() == TicketRowType.AHORRO_DESCUENTO) {
            prefix = "   ";
        }

        String label = value.getLabel();
        boolean isHtml = label != null && label.trim().startsWith("<html>");

        if (isHtml) {
            lblLeft.setText(label);
        } else {
            lblLeft.setText(prefix + label);
        }

        // =====================================================
        // 2) FUENTES SEGÚN TIPO DE FILA
        // =====================================================
        switch (value.getType()) {
            case ITEM -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.PLAIN, 15f));
            case EXTRA, PERSONALIZACION -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.PLAIN, 15f));
            case ASK_ME -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.PLAIN, 13f));
            case COMBO -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 15f));
            case AHORRO -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.ITALIC, 14f));
            case DESCUENTO -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 15f));
            case AHORRO_DESCUENTO -> lblLeft.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.ITALIC, 14f));
        }

        // =====================================================
        // 3) IMPORTE DERECHO
        // =====================================================
        BigDecimal amount = value.getAmount();
        lblRight.setText(amount == null ? "" : formatMoney(amount) + "€");

        if (value.getType() == TicketRowType.COMBO
                || value.getType() == TicketRowType.DESCUENTO) {
            lblRight.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 15f));
        } else if (value.getType() == TicketRowType.AHORRO
                || value.getType() == TicketRowType.AHORRO_DESCUENTO) {
            lblRight.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 14f));
        } else {
            lblRight.setFont(InformeUiTheme.FONT_TICKET.deriveFont(Font.BOLD, 15f));
        }

        // =====================================================
        // 4) COLORES
        // =====================================================
        if (isSelected) {
            setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
            lblLeft.setForeground(InformeUiTheme.TEXT_PRIMARY);
            lblRight.setForeground(InformeUiTheme.ACCENT_GOLD);
        } else {
            setBackground(InformeUiTheme.PANEL_BG);

            switch (value.getType()) {
                case ASK_ME -> {
                    lblLeft.setForeground(new Color(180, 220, 255));
                    lblRight.setForeground(new Color(180, 220, 255));
                }

                case COMBO -> {
                    lblLeft.setForeground(InformeUiTheme.ACCENT_GOLD);
                    lblRight.setForeground(InformeUiTheme.ACCENT_GOLD);
                }

                case AHORRO, AHORRO_DESCUENTO -> {
                    lblLeft.setForeground(new Color(120, 220, 140));
                    lblRight.setForeground(new Color(120, 220, 140));
                }

                case DESCUENTO -> {
                    lblLeft.setForeground(new Color(170, 200, 255));
                    lblRight.setForeground(new Color(170, 200, 255));
                }

                default -> {
                    lblLeft.setForeground(InformeUiTheme.TEXT_PRIMARY);
                    lblRight.setForeground(InformeUiTheme.TEXT_SECONDARY);
                }
            }
        }

        return this;
    }

    private String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount != null ? amount : BigDecimal.ZERO);
    }
}

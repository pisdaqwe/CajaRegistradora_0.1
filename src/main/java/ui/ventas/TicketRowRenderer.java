package ui.ventas;

import model.TicketRow;
import enums.TicketRowType;

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

        lblLeft.setFont(new Font("Monospaced", Font.PLAIN, 16));
        lblRight.setFont(new Font("Monospaced", Font.BOLD, 16));
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
            case ITEM -> lblLeft.setFont(new Font("Monospaced", Font.PLAIN, 16));
            case EXTRA, PERSONALIZACION -> lblLeft.setFont(new Font("Monospaced", Font.PLAIN, 16));
            case ASK_ME -> lblLeft.setFont(new Font("Monospaced", Font.PLAIN, 13));
            case COMBO -> lblLeft.setFont(new Font("Monospaced", Font.BOLD, 16));
            case AHORRO -> lblLeft.setFont(new Font("Monospaced", Font.ITALIC, 14));
            case DESCUENTO -> lblLeft.setFont(new Font("Monospaced", Font.BOLD, 16));
            case AHORRO_DESCUENTO -> lblLeft.setFont(new Font("Monospaced", Font.ITALIC, 14));
        }

        // =====================================================
        // 3) IMPORTE DERECHO
        // =====================================================
        BigDecimal amount = value.getAmount();
        lblRight.setText(amount == null ? "" : formatMoney(amount) + "€");

        if (value.getType() == TicketRowType.COMBO
                || value.getType() == TicketRowType.DESCUENTO) {
            lblRight.setFont(new Font("Monospaced", Font.BOLD, 16));
        } else if (value.getType() == TicketRowType.AHORRO
                || value.getType() == TicketRowType.AHORRO_DESCUENTO) {
            lblRight.setFont(new Font("Monospaced", Font.BOLD, 14));
        } else {
            lblRight.setFont(new Font("Monospaced", Font.BOLD, 16));
        }

        // =====================================================
        // 4) COLORES
        // =====================================================
        if (isSelected) {
            setBackground(new Color(60, 60, 60));
            lblLeft.setForeground(Color.WHITE);
            lblRight.setForeground(new Color(255, 210, 0));
        } else {
            setBackground(new Color(30, 30, 30));

            switch (value.getType()) {
                case ASK_ME -> {
                    lblLeft.setForeground(new Color(180, 220, 255));
                    lblRight.setForeground(new Color(180, 220, 255));
                }

                case COMBO -> {
                    lblLeft.setForeground(new Color(255, 215, 120));
                    lblRight.setForeground(new Color(255, 215, 120));
                }

                case AHORRO -> {
                    lblLeft.setForeground(new Color(120, 220, 140));
                    lblRight.setForeground(new Color(120, 220, 140));
                }

                case DESCUENTO -> {
                    lblLeft.setForeground(new Color(170, 200, 255));
                    lblRight.setForeground(new Color(170, 200, 255));
                }

                case AHORRO_DESCUENTO -> {
                    lblLeft.setForeground(new Color(120, 220, 140));
                    lblRight.setForeground(new Color(120, 220, 140));
                }

                default -> {
                    lblLeft.setForeground(new Color(230, 230, 230));
                    lblRight.setForeground(new Color(200, 200, 200));
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

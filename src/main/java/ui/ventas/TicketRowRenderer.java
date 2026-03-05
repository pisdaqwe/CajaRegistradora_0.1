package ui.ventas;



import model.TicketRow;
import enums.TicketRowType;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

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

        // Indent según tipo
        String prefix = "";
        if (value.getType() == TicketRowType.EXTRA || value.getType() == TicketRowType.PERSONALIZACION) {
            prefix = "   "; // sangría
        }

        lblLeft.setText(prefix + value.getLabel());

        BigDecimal amount = value.getAmount();
        lblRight.setText(amount == null ? "" : amount.toString() + "€");

        // Colores TPV
        if (isSelected) {
            setBackground(new Color(60, 60, 60));
            lblLeft.setForeground(Color.WHITE);
            lblRight.setForeground(new Color(255, 210, 0)); // amarillo
        } else {
            setBackground(new Color(30, 30, 30));
            lblLeft.setForeground(new Color(230, 230, 230));
            lblRight.setForeground(new Color(200, 200, 200));
        }

        return this;
    }
}

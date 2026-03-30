package ui.ventas;

import model.DescuentoAplicado;
import model.TicketSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DescuentoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public interface DescuentoActionListener {
        void onAplicarCodigoPromocionalClicked();
        void onAplicarDescuentoEmpleadoClicked();
        void onQuitarDescuentoClicked();
        void onVolverClicked();
    }

    private final TicketSession ticketSession;

    private DescuentoActionListener actionListener;

    private final JButton btnCodigoPromo = new JButton("Código promocional");
    private final JButton btnDescuentoEmpleado = new JButton("Descuento empleado");
    private final JButton btnQuitarDescuento = new JButton("Quitar descuento");
    private final JButton btnVolver = new JButton("Volver");

    private final JLabel lblEstado = new JLabel("Sin descuento aplicado");
    private final JLabel lblNombre = new JLabel("-");
    private final JLabel lblCodigo = new JLabel("-");
    private final JLabel lblOrigen = new JLabel("-");
    private final JLabel lblAhorro = new JLabel("0,00€");
    private final JLabel lblTotalFinal = new JLabel("0,00€");

    public DescuentoPanel(TicketSession ticketSession) {
        this.ticketSession = ticketSession;

        setLayout(new BorderLayout(16, 16));
        setOpaque(true);
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        wireActions();
        refresh();
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("DESCUENTOS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Aplicar promociones o descuentos de empleado");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 180, 180));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(subtitle);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 16));
        panel.setOpaque(false);

        panel.add(buildActionsCard());
        panel.add(buildResumenCard());

        return panel;
    }

    private JComponent buildActionsCard() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel("Acciones");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        stylePrimaryButton(btnCodigoPromo);
        stylePrimaryButton(btnDescuentoEmpleado);
        styleDangerButton(btnQuitarDescuento);
        styleSecondaryButton(btnVolver);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnCodigoPromo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnDescuentoEmpleado);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnQuitarDescuento);
        panel.add(Box.createVerticalGlue());
        panel.add(btnVolver);

        return panel;
    }

    private JComponent buildResumenCard() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel("Resumen descuento");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEstado.setForeground(new Color(170, 200, 255));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(14));
        panel.add(lblEstado);
        panel.add(Box.createVerticalStrut(18));

        panel.add(buildInfoRow("Nombre", lblNombre));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow("Código", lblCodigo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow("Origen", lblOrigen));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow("Ahorro", lblAhorro));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow("Total final", lblTotalFinal));

        return panel;
    }

    private JComponent buildInfoRow(String leftText, JLabel rightLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 8));
        row.setOpaque(false);

        JLabel left = new JLabel(leftText);
        left.setForeground(new Color(210, 210, 210));
        left.setFont(new Font("SansSerif", Font.PLAIN, 14));

        rightLabel.setForeground(Color.WHITE);
        rightLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(left, BorderLayout.WEST);
        row.add(rightLabel, BorderLayout.EAST);

        return row;
    }

    private JComponent buildBottom() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(32, 32, 32));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(46, 125, 50));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 70, 70));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private void styleDangerButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(160, 40, 40));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }

    private void wireActions() {
        btnCodigoPromo.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onAplicarCodigoPromocionalClicked();
            }
        });

        btnDescuentoEmpleado.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onAplicarDescuentoEmpleadoClicked();
            }
        });

        btnQuitarDescuento.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onQuitarDescuentoClicked();
            }
        });

        btnVolver.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onVolverClicked();
            }
        });
    }

    public void setActionListener(DescuentoActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void refresh() {
        DescuentoAplicado descuento = ticketSession.getDescuentoAplicado();

        if (descuento == null) {
            lblEstado.setText("Sin descuento aplicado");
            lblNombre.setText("-");
            lblCodigo.setText("-");
            lblOrigen.setText("-");
            lblAhorro.setText("0,00€");
            lblTotalFinal.setText(formatMoney(ticketSession.getTotal()) + "€");
            btnQuitarDescuento.setEnabled(false);
            return;
        }

        lblEstado.setText("Descuento aplicado correctamente");
        lblNombre.setText(safeText(descuento.getNombre()));
        lblCodigo.setText(safeText(descuento.getCodigoIntroducido()));
        lblOrigen.setText(safeText(descuento.getOrigen()));
        lblAhorro.setText(formatMoney(safe(descuento.getImporteDescuento())) + "€");
        lblTotalFinal.setText(formatMoney(safe(descuento.getTotalFinal())) + "€");
        btnQuitarDescuento.setEnabled(true);
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value.trim();
    }

    private String formatMoney(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount != null ? amount : BigDecimal.ZERO);
    }
}
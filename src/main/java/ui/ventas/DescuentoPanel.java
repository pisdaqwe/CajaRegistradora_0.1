package ui.ventas;

import model.DescuentoAplicado;
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

    private final JButton btnCodigoPromo = new JButton(I18n.t("sales.discounts.promoCode"));
    private final JButton btnDescuentoEmpleado = new JButton(I18n.t("sales.discounts.employeeDiscount"));
    private final JButton btnQuitarDescuento = new JButton(I18n.t("sales.discounts.removeDiscount"));
    private final JButton btnVolver = new JButton(I18n.t("common.back"));

    private final JLabel lblEstado = new JLabel(I18n.t("sales.discounts.noDiscount"));
    private final JLabel lblNombre = new JLabel("-");
    private final JLabel lblCodigo = new JLabel("-");
    private final JLabel lblOrigen = new JLabel("-");
    private final JLabel lblAhorro = new JLabel("0,00€");
    private final JLabel lblTotalFinal = new JLabel("0,00€");

    public DescuentoPanel(TicketSession ticketSession) {
        this.ticketSession = ticketSession;

        setLayout(new BorderLayout(16, 16));
        setOpaque(true);
        setBackground(InformeUiTheme.APP_BG);
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

        JLabel title = new JLabel(I18n.t("sales.discounts.title"));
        title.setIcon(TpvIconFactory.product(24, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(10);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(I18n.t("sales.discounts.subtitle"));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

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

        JLabel lbl = new JLabel(I18n.t("sales.discounts.actions"));
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);

        btnCodigoPromo.setIcon(TpvIconFactory.product(18, Color.WHITE));
        btnDescuentoEmpleado.setIcon(TpvIconFactory.idCard(18, Color.WHITE));
        btnQuitarDescuento.setIcon(TpvIconFactory.cancel(18, Color.WHITE));
        btnVolver.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));

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

        JLabel lbl = new JLabel(I18n.t("sales.discounts.summary"));
        lbl.setIcon(TpvIconFactory.info(18, InformeUiTheme.ACCENT_GOLD));
        lbl.setIconTextGap(8);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);

        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEstado.setForeground(new Color(170, 200, 255));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(14));
        panel.add(lblEstado);
        panel.add(Box.createVerticalStrut(18));

        panel.add(buildInfoRow(I18n.t("sales.discounts.name"), lblNombre));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow(I18n.t("sales.discounts.code"), lblCodigo));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow(I18n.t("sales.discounts.origin"), lblOrigen));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow(I18n.t("sales.discounts.savings"), lblAhorro));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildInfoRow(I18n.t("sales.discounts.finalTotal"), lblTotalFinal));

        return panel;
    }

    private JComponent buildInfoRow(String leftText, JLabel rightLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 8));
        row.setOpaque(false);

        JLabel left = new JLabel(leftText);
        left.setForeground(InformeUiTheme.TEXT_SECONDARY);
        left.setFont(new Font("SansSerif", Font.PLAIN, 14));

        rightLabel.setForeground(InformeUiTheme.TEXT_PRIMARY);
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
        panel.setBackground(InformeUiTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setIconTextGap(8);
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setForeground(InformeUiTheme.TEXT_PRIMARY);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setIconTextGap(8);
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(InformeUiTheme.DANGER);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setIconTextGap(8);
        button.setPreferredSize(new Dimension(0, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            lblEstado.setText(I18n.t("sales.discounts.noDiscount"));
            lblNombre.setText("-");
            lblCodigo.setText("-");
            lblOrigen.setText("-");
            lblAhorro.setText("0,00€");
            lblTotalFinal.setText(formatMoney(ticketSession.getTotal()) + "€");
            btnQuitarDescuento.setEnabled(false);
            return;
        }

        lblEstado.setText(I18n.t("sales.discounts.applied"));
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

package ui.ventas;

import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel placeholder/centro de pago.
 *
 * Puede mantenerse como panel informativo si el flujo real usa PagoPanel.
 */
public class PagoCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public PagoCenterPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildCenterInfo(), BorderLayout.CENTER);
        add(buildMetodosPagoBar(), BorderLayout.SOUTH);
    }

    private JPanel buildCenterInfo() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(24, 28, 24, 28)
        ));

        JLabel title = new JLabel(I18n.t("sales.payment.title"), SwingConstants.CENTER);
        title.setIcon(TpvIconFactory.creditCard(24, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(10);
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        title.setForeground(InformeUiTheme.TEXT_PRIMARY);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));

        JLabel sub = new JLabel(I18n.t("sales.payment.subtitle"), SwingConstants.CENTER);
        sub.setForeground(InformeUiTheme.TEXT_SECONDARY);
        sub.setFont(InformeUiTheme.FONT_SUBTITLE);

        card.add(title, BorderLayout.NORTH);
        card.add(sub, BorderLayout.CENTER);

        wrapper.add(card);

        return wrapper;
    }

    private JPanel buildMetodosPagoBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 12));
        bar.setOpaque(false);

        bar.add(createTpvButton(I18n.t("sales.payment.cash"), false, TpvIconFactory.cashRegister(18, InformeUiTheme.TEXT_PRIMARY)));
        bar.add(createTpvButton(I18n.t("sales.payment.card"), true, TpvIconFactory.creditCard(18, new Color(25, 25, 25))));
        bar.add(createTpvButton(I18n.t("sales.payment.exact"), false, TpvIconFactory.check(18, InformeUiTheme.TEXT_PRIMARY)));

        return bar;
    }

    private JButton createTpvButton(String text, boolean cardPayment, Icon icon) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setIconTextGap(8);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 17));
        button.setForeground(cardPayment ? new Color(25, 25, 25) : InformeUiTheme.TEXT_PRIMARY);
        button.setBackground(cardPayment ? InformeUiTheme.ACCENT_GOLD : InformeUiTheme.STARBUCKS_GREEN);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}

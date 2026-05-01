package ui.ventas;

import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OpcionesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color BG_MAIN = InformeUiTheme.APP_BG;
    private static final Color BG_SECTION = InformeUiTheme.CARD_BG;
    private static final Color BG_BUTTON = InformeUiTheme.STARBUCKS_GREEN_SOFT;
    private static final Color BG_BUTTON_ALT = InformeUiTheme.CARD_BG_2;
    private static final Color BG_BUTTON_ADMIN = new Color(57, 102, 74);
    private static final Color TEXT_MAIN = InformeUiTheme.TEXT_PRIMARY;
    private static final Color TEXT_SOFT = InformeUiTheme.TEXT_SECONDARY;
    private static final Color SEPARATOR = InformeUiTheme.BORDER;
    private static final Color BORDER = InformeUiTheme.BORDER;

    public interface OpcionesActionListener {
        void onDuplicarClicked();
        void onReimprimirClicked();
        void onSkuClicked();
        void onBuscarProductoClicked();
        void onDisponibilidadClicked();
        void onDescuentosClicked();
        void onUltimosTicketsClicked();
        void onDevolucionesClicked();
        void onMermaClicked();
        void onNuevoPedidoClicked();
        void onCerrarSesionClicked();
        void onVolverAdminClicked();
        void onVolverClicked();
    }

    private OpcionesActionListener actionListener;

    private JPanel adminSection;

    private JButton btnDuplicar;
    private JButton btnReimprimir;
    private JButton btnSku;
    private JButton btnBuscarProducto;
    private JButton btnDisponibilidad;
    private JButton btnDescuentos;
    private JButton btnUltimosTickets;
    private JButton btnVolver;
    private JButton btnDevoluciones;
    private JButton btnNuevoPedido;
    private JButton btnCerrarSesion;
    private JButton btnVolverAdmin;
    private JButton btnMerma;

    public OpcionesPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_MAIN);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildContent(), BorderLayout.CENTER);
        setAdminMode(true);
    }

    public void setActionListener(OpcionesActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setAdminMode(boolean adminMode) {
        if (adminSection != null) {
            adminSection.setVisible(adminMode);
        }
        revalidate();
        repaint();
    }

    private JComponent buildContent() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        root.add(buildHeader());
        root.add(Box.createVerticalStrut(14));

        root.add(createSection(
                I18n.t("sales.options.section.ticket"),
                I18n.t("sales.options.section.ticket.subtitle"),
                createButtonGrid(
                        btnDuplicar = createPrimaryButton(I18n.t("sales.options.duplicate"), TpvIconFactory.product(20, TEXT_MAIN), this::fireDuplicarClicked),
                        btnReimprimir = createPrimaryButton(I18n.t("sales.options.reprint"), TpvIconFactory.report(20, TEXT_MAIN), this::fireReimprimirClicked)
                )
        ));

        root.add(Box.createVerticalStrut(12));

        root.add(createSection(
                I18n.t("sales.options.section.search"),
                I18n.t("sales.options.section.search.subtitle"),
                createButtonGrid(
                        btnSku = createPrimaryButton(I18n.t("sales.options.sku"), TpvIconFactory.key(20, TEXT_MAIN), this::fireSkuClicked),
                        btnBuscarProducto = createPrimaryButton(I18n.t("sales.options.searchProduct"), TpvIconFactory.search(20, TEXT_MAIN), this::fireBuscarProductoClicked)
                )
        ));

        root.add(Box.createVerticalStrut(12));

        root.add(createSection(
                I18n.t("sales.options.section.query"),
                I18n.t("sales.options.section.query.subtitle"),
                createButtonGrid(
                        btnDisponibilidad = createPrimaryButton(I18n.t("sales.options.availability"), TpvIconFactory.eye(20, TEXT_MAIN), this::fireDisponibilidadClicked)
                )
        ));

        root.add(Box.createVerticalStrut(12));

        root.add(createSection(
                I18n.t("sales.options.section.operations"),
                I18n.t("sales.options.section.operations.subtitle"),
                createButtonGrid(
                        btnDescuentos = createPrimaryButton(I18n.t("sales.options.discounts"), TpvIconFactory.product(20, TEXT_MAIN), this::fireDescuentosClicked),
                        btnUltimosTickets = createPrimaryButton(I18n.t("sales.options.lastTickets"), TpvIconFactory.history(20, TEXT_MAIN), this::fireUltimosTicketsClicked)
                )
        ));

        root.add(Box.createVerticalStrut(12));

        root.add(createSection(
                I18n.t("sales.options.section.flow"),
                I18n.t("sales.options.section.flow.subtitle"),
                createButtonGrid(
                        btnNuevoPedido = createPrimaryButton(I18n.t("sales.options.newOrder"), TpvIconFactory.cashRegister(20, TEXT_MAIN), this::fireNuevoPedidoClicked),
                        btnCerrarSesion = createPrimaryButton(I18n.t("sales.options.logout"), TpvIconFactory.logout(20, TEXT_MAIN), this::fireCerrarSesionClicked)
                )
        ));

        root.add(Box.createVerticalStrut(12));

        adminSection = createSection(
                I18n.t("sales.options.section.admin"),
                I18n.t("sales.options.section.admin.subtitle"),
                createButtonGrid(
                        btnDevoluciones = createAdminButton(I18n.t("sales.options.returns"), TpvIconFactory.back(20, TEXT_MAIN), this::fireDevolucionesClicked),
                        btnMerma = createAdminButton(I18n.t("sales.options.waste"), TpvIconFactory.warning(20, TEXT_MAIN), this::fireMermaClicked),
                        btnVolverAdmin = createAdminButton(I18n.t("sales.options.backAdmin"), TpvIconFactory.shield(20, TEXT_MAIN), this::fireVolverAdminClicked)
                )
        );
        root.add(adminSection);

        root.add(Box.createVerticalStrut(18));
        root.add(createBottomBar());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(I18n.t("sales.options.title"));
        title.setIcon(TpvIconFactory.settings(28, InformeUiTheme.ACCENT_GOLD));
        title.setIconTextGap(10);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_MAIN);

        JLabel subtitle = new JLabel(I18n.t("sales.options.subtitle"));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SOFT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createSeparator());

        return panel;
    }

    private JPanel createSection(String title, String subtitle, JComponent content) {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(true);
        section.setBackground(BG_SECTION);
        section.setBorder(createSectionBorder());

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_MAIN);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitle.setForeground(TEXT_SOFT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSubtitle);
        header.add(Box.createVerticalStrut(8));
        header.add(createSeparator());

        section.add(header, BorderLayout.NORTH);
        section.add(content, BorderLayout.CENTER);

        return section;
    }

    private Border createSectionBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        );
    }

    private JComponent createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(SEPARATOR);
        separator.setBackground(SEPARATOR);
        return separator;
    }

    private JPanel createButtonGrid(JButton... buttons) {
        int columnas = buttons.length == 1 ? 1 : 2;

        JPanel panel = new JPanel(new GridLayout(0, columnas, 12, 12));
        panel.setOpaque(false);

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }

    private JComponent createBottomBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        btnVolver = new JButton(I18n.t("common.back"));
        btnVolver.setIcon(TpvIconFactory.back(20, TEXT_MAIN));
        btnVolver.setIconTextGap(8);
        btnVolver.addActionListener(e -> fireVolverClicked());
        styleBottomButton(btnVolver);

        panel.add(btnVolver, BorderLayout.CENTER);
        return panel;
    }

    private JButton createPrimaryButton(String text, Icon icon, Runnable action) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setIconTextGap(8);
        button.addActionListener(e -> action.run());
        stylePrimaryButton(button);
        return button;
    }

    private JButton createAdminButton(String text, Icon icon, Runnable action) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setIconTextGap(8);
        button.addActionListener(e -> action.run());
        styleAdminButton(button);
        return button;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_BUTTON);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(123, 170, 145), 1, true),
                new EmptyBorder(18, 12, 18, 12)
        ));
        button.setPreferredSize(new Dimension(220, 82));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleAdminButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_BUTTON_ADMIN);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(146, 183, 161), 1, true),
                new EmptyBorder(18, 12, 18, 12)
        ));
        button.setPreferredSize(new Dimension(220, 82));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleBottomButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(TEXT_MAIN);
        button.setBackground(BG_BUTTON_ALT);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 162, 140), 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        button.setPreferredSize(new Dimension(200, 54));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void fireDuplicarClicked() {
        if (actionListener != null) {
            actionListener.onDuplicarClicked();
        }
    }

    private void fireSkuClicked() {
        if (actionListener != null) {
            actionListener.onSkuClicked();
        }
    }

    private void fireBuscarProductoClicked() {
        if (actionListener != null) {
            actionListener.onBuscarProductoClicked();
        }
    }

    private void fireDisponibilidadClicked() {
        if (actionListener != null) {
            actionListener.onDisponibilidadClicked();
        }
    }

    private void fireDescuentosClicked() {
        if (actionListener != null) {
            actionListener.onDescuentosClicked();
        }
    }

    private void fireReimprimirClicked() {
        if (actionListener != null) {
            actionListener.onReimprimirClicked();
        }
    }

    private void fireUltimosTicketsClicked() {
        if (actionListener != null) {
            actionListener.onUltimosTicketsClicked();
        }
    }

    private void fireDevolucionesClicked() {
        if (actionListener != null) {
            actionListener.onDevolucionesClicked();
        }
    }

    private void fireMermaClicked() {
        if (actionListener != null) {
            actionListener.onMermaClicked();
        }
    }

    private void fireVolverAdminClicked() {
        if (actionListener != null) {
            actionListener.onVolverAdminClicked();
        }
    }

    private void fireVolverClicked() {
        if (actionListener != null) {
            actionListener.onVolverClicked();
        }
    }

    private void fireNuevoPedidoClicked() {
        if (actionListener != null) {
            actionListener.onNuevoPedidoClicked();
        }
    }

    private void fireCerrarSesionClicked() {
        if (actionListener != null) {
            actionListener.onCerrarSesionClicked();
        }
    }
}

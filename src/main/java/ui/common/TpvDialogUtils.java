package ui.common;

import ui.theme.InformeUiTheme;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class TpvDialogUtils {

    private TpvDialogUtils() {
    }

    public static void showInfo(Component parent, String title, String message) {
        showMessage(parent, title, message, "ℹ");
    }

    public static void showWarning(Component parent, String title, String message) {
        showMessage(parent, title, message, "⚠");
    }

    public static void showError(Component parent, String title, String message) {
        showMessage(parent, title, message, "✕");
    }

    public static boolean confirm(Component parent, String title, String message) {
        return showConfirm(parent, title, message);
    }

    private static void showMessage(Component parent, String title, String message, String iconText) {
    	Window owner = resolveOwner(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 30));
        icon.setForeground(InformeUiTheme.ACCENT_GOLD);
        icon.setPreferredSize(new Dimension(48, 48));

        JPanel textPanel = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblMessage = new JLabel(toHtml(message));
        lblMessage.setFont(InformeUiTheme.FONT_BODY);
        lblMessage.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblMessage, BorderLayout.CENTER);

        card.add(icon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        JButton btnOk = new JButton(I18n.t("common.accept"));
        InformeUiTheme.stylePrimaryButton(btnOk);
        btnOk.addActionListener(e -> dialog.dispose());

        JPanel buttons = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnOk);

        root.add(card, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(430, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(btnOk);
        dialog.setVisible(true);
    }

    public static <T> T showSelection(
            Component parent,
            String title,
            String message,
            java.util.List<T> options
    ) {
        if (options == null || options.isEmpty()) {
            return null;
        }

        final Object[] selected = {null};

        Window owner = resolveOwner(parent);

        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 14));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblMessage = new JLabel(toHtml(message));
        lblMessage.setFont(InformeUiTheme.FONT_BODY);
        lblMessage.setForeground(InformeUiTheme.TEXT_SECONDARY);

        DefaultListModel<T> model = new DefaultListModel<>();
        for (T option : options) {
            model.addElement(option);
        }

        JList<T> list = new JList<>(model);
        InformeUiTheme.styleList(list);
        list.setVisibleRowCount(Math.min(8, options.size()));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(list);
        InformeUiTheme.styleScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(420, 260));

        JPanel header = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 8));
        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblMessage, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        JButton btnCancel = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancel);

        JButton btnAccept = new JButton(I18n.t("common.accept"));
        InformeUiTheme.stylePrimaryButton(btnAccept);

        btnCancel.addActionListener(e -> {
            selected[0] = null;
            dialog.dispose();
        });

        btnAccept.addActionListener(e -> {
            selected[0] = list.getSelectedValue();
            dialog.dispose();
        });

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && list.getSelectedValue() != null) {
                    selected[0] = list.getSelectedValue();
                    dialog.dispose();
                }
            }
        });

        JPanel buttons = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnCancel);
        buttons.add(btnAccept);

        root.add(card, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(500, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(btnAccept);
        dialog.setVisible(true);

        @SuppressWarnings("unchecked")
        T result = (T) selected[0];

        return result;
    }
    private static boolean showConfirm(Component parent, String title, String message) {
        final boolean[] accepted = {false};

        Window owner = resolveOwner(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(14, 0));

        JLabel icon = new JLabel("?", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 32));
        icon.setForeground(InformeUiTheme.ACCENT_GOLD);
        icon.setPreferredSize(new Dimension(48, 48));

        JPanel textPanel = InformeUiTheme.createTransparentPanel(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblMessage = new JLabel(toHtml(message));
        lblMessage.setFont(InformeUiTheme.FONT_BODY);
        lblMessage.setForeground(InformeUiTheme.TEXT_SECONDARY);

        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblMessage, BorderLayout.CENTER);

        card.add(icon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        JButton btnCancel = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancel);

        JButton btnAccept = new JButton(I18n.t("common.accept"));
        InformeUiTheme.stylePrimaryButton(btnAccept);

        btnCancel.addActionListener(e -> {
            accepted[0] = false;
            dialog.dispose();
        });

        btnAccept.addActionListener(e -> {
            accepted[0] = true;
            dialog.dispose();
        });

        JPanel buttons = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(btnCancel);
        buttons.add(btnAccept);

        root.add(card, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(460, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.getRootPane().setDefaultButton(btnAccept);
        dialog.setVisible(true);

        return accepted[0];
    }

    private static String toHtml(String text) {
        if (text == null || text.isBlank()) {
            return "<html><body style='width:330px;'>" + I18n.t("dialog.noDetails") + "</body></html>";
        }

        String safe = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        return "<html><body style='width:330px;'>" + safe + "</body></html>";
    }
    private static Window resolveOwner(Component parent) {
        if (parent == null) {
            return null;
        }

        if (parent instanceof Window window) {
            return window;
        }

        return SwingUtilities.getWindowAncestor(parent);
    }
}
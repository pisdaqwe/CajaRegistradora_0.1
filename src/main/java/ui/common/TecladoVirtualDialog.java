package ui.common;

import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class TecladoVirtualDialog {

    private TecladoVirtualDialog() {
    }

    public static void showAlfanumerico(Component parent, JTextField targetField, String titulo, int maxLength) {
        if (targetField == null || !targetField.isEnabled() || !targetField.isEditable()) {
            return;
        }

        Window owner = resolveOwner(parent);
        JDialog dialog = new JDialog(owner, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setIcon(TpvIconFactory.key(20, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblHint = new JLabel(I18n.t("keyboard.alphaHint"));
        lblHint.setFont(InformeUiTheme.FONT_BODY);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel header = InformeUiTheme.createTransparentPanel(new GridLayout(2, 1, 0, 4));
        header.add(lblTitle);
        header.add(lblHint);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 12));
        card.add(new TecladoAlfaNumericoPanel(targetField, maxLength), BorderLayout.CENTER);

        JButton btnCerrar = new JButton(I18n.t("keyboard.accept"));
        btnCerrar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnCerrar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnCerrar);
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel footer = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(btnCerrar);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(760, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        targetField.requestFocusInWindow();
    }

    public static void showNumerico(Component parent, JTextField targetField, String titulo, int maxLength) {
        if (targetField == null || !targetField.isEnabled() || !targetField.isEditable()) {
            return;
        }

        Window owner = resolveOwner(parent);
        JDialog dialog = new JDialog(owner, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setIcon(TpvIconFactory.key(20, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);
        lblTitle.setFont(InformeUiTheme.FONT_SECTION);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblHint = new JLabel(I18n.t("keyboard.numericHint"));
        lblHint.setFont(InformeUiTheme.FONT_BODY);
        lblHint.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel header = InformeUiTheme.createTransparentPanel(new GridLayout(2, 1, 0, 4));
        header.add(lblTitle);
        header.add(lblHint);

        JPanel keypad = new JPanel(new GridLayout(4, 3, 10, 10));
        keypad.setOpaque(false);

        for (int i = 1; i <= 9; i++) {
            final String value = String.valueOf(i);
            keypad.add(createNumberButton(value, () -> append(targetField, value, maxLength)));
        }

        JButton btnBorrar = new JButton("←");
        InformeUiTheme.styleKeypadSpecialButton(btnBorrar, false);
        btnBorrar.setToolTipText(I18n.t("keyboard.delete"));
        btnBorrar.addActionListener(e -> deleteLast(targetField));

        JButton btn0 = new JButton("0");
        InformeUiTheme.styleKeypadButton(btn0);
        btn0.addActionListener(e -> append(targetField, "0", maxLength));

        JButton btnLimpiar = new JButton("C");
        InformeUiTheme.styleKeypadSpecialButton(btnLimpiar, true);
        btnLimpiar.setToolTipText(I18n.t("keyboard.clear.tooltip"));
        btnLimpiar.addActionListener(e -> targetField.setText(""));

        keypad.add(btnBorrar);
        keypad.add(btn0);
        keypad.add(btnLimpiar);

        JButton btnCerrar = new JButton(I18n.t("keyboard.accept"));
        btnCerrar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnCerrar.setIconTextGap(8);
        InformeUiTheme.stylePrimaryButton(btnCerrar);
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout());
        card.add(keypad, BorderLayout.CENTER);

        JPanel footer = InformeUiTheme.createTransparentPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(btnCerrar);

        root.add(header, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 520));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        targetField.requestFocusInWindow();
    }

    private static JButton createNumberButton(String text, Runnable action) {
        JButton button = new JButton(text);
        InformeUiTheme.styleKeypadButton(button);
        button.addActionListener(e -> action.run());
        return button;
    }

    private static void append(JTextField field, String value, int maxLength) {
        String current = field.getText() == null ? "" : field.getText();
        if (current.length() >= maxLength) {
            return;
        }
        field.setText(current + value);
    }

    private static void deleteLast(JTextField field) {
        String current = field.getText();
        if (current == null || current.isEmpty()) {
            return;
        }
        field.setText(current.substring(0, current.length() - 1));
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

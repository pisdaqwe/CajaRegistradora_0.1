package ui.common;

import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

import javax.swing.*;
import java.awt.*;

/**
 * Teclado virtual alfanumérico reutilizable para dialogs del TPV.
 *
 * Características:
 * - Letras A-Z
 * - Números 0-9
 * - Botón borrar
 * - Botón limpiar
 * - Botón guion
 * - Cambio entre mayúsculas y minúsculas
 *
 * Uso:
 * - se le pasa un JTextField destino
 * - el teclado escribe directamente sobre ese campo
 */
public class TecladoAlfaNumericoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField targetField;
    private final int maxLength;

    /**
     * true = MAYÚSCULAS
     * false = minúsculas
     */
    private boolean uppercaseMode = true;

    /**
     * Referencias a botones de letras para poder cambiar su texto
     * al alternar entre mayúsculas/minúsculas.
     */
    private final java.util.List<JButton> letterButtons = new java.util.ArrayList<>();

    private JButton btnShift;

    public TecladoAlfaNumericoPanel(JTextField targetField, int maxLength) {
        this.targetField = targetField;
        this.maxLength = maxLength;

        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        add(buildKeysPanel(), BorderLayout.CENTER);
        add(buildActionsPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildKeysPanel() {
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.add(buildNumberRow("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"));
        container.add(Box.createVerticalStrut(8));
        container.add(buildLetterRow("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"));
        container.add(Box.createVerticalStrut(8));
        container.add(buildLetterRow("A", "S", "D", "F", "G", "H", "J", "K", "L"));
        container.add(Box.createVerticalStrut(8));
        container.add(buildLastLetterRow());

        return container;
    }

    private JPanel buildNumberRow(String... keys) {
        JPanel row = new JPanel(new GridLayout(1, keys.length, 8, 8));
        row.setOpaque(false);

        for (String key : keys) {
            row.add(createKeyButton(key, () -> appendText(key)));
        }

        return row;
    }

    private JPanel buildLetterRow(String... letters) {
        JPanel row = new JPanel(new GridLayout(1, letters.length, 8, 8));
        row.setOpaque(false);

        for (String letter : letters) {
            row.add(createLetterButton(letter));
        }

        return row;
    }

    private JPanel buildLastLetterRow() {
        JPanel row = new JPanel(new GridLayout(1, 9, 8, 8));
        row.setOpaque(false);

        btnShift = createActionButton(
                I18n.t("keyboard.uppercase.short"),
                TpvIconFactory.key(16, Color.WHITE),
                I18n.t("keyboard.uppercase"),
                this::toggleCaseMode
        );
        row.add(btnShift);

        row.add(createLetterButton("Z"));
        row.add(createLetterButton("X"));
        row.add(createLetterButton("C"));
        row.add(createLetterButton("V"));
        row.add(createLetterButton("B"));
        row.add(createLetterButton("N"));
        row.add(createLetterButton("M"));

        row.add(createActionButton("-", null, "-", () -> appendText("-")));

        return row;
    }

    private JComponent buildActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 8));
        panel.setOpaque(false);

        panel.add(createActionButton(
                I18n.t("keyboard.delete"),
                TpvIconFactory.back(16, Color.WHITE),
                I18n.t("keyboard.delete.tooltip"),
                this::deleteLastChar
        ));
        panel.add(createActionButton(
                I18n.t("keyboard.space"),
                null,
                I18n.t("keyboard.space"),
                () -> appendText(" ")
        ));
        panel.add(createActionButton(
                I18n.t("keyboard.clear"),
                TpvIconFactory.cancel(16, Color.WHITE),
                I18n.t("keyboard.clear.tooltip"),
                this::clearText
        ));

        return panel;
    }

    private JButton createLetterButton(String baseLetterUpper) {
        JButton button = new JButton(baseLetterUpper);
        styleKeyButton(button);

        button.putClientProperty("baseLetterUpper", baseLetterUpper);
        letterButtons.add(button);

        button.addActionListener(e -> appendCurrentLetter(button));
        return button;
    }

    private void appendCurrentLetter(JButton button) {
        String baseLetterUpper = (String) button.getClientProperty("baseLetterUpper");
        if (baseLetterUpper == null || baseLetterUpper.isBlank()) {
            return;
        }

        String textToAppend = uppercaseMode
                ? baseLetterUpper.toUpperCase()
                : baseLetterUpper.toLowerCase();

        appendText(textToAppend);
    }

    private JButton createKeyButton(String text, Runnable action) {
        JButton button = new JButton(text);
        styleKeyButton(button);
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton createActionButton(String text, Icon icon, String tooltip, Runnable action) {
        JButton button = new JButton(text);
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(7);
        }
        if (tooltip != null && !tooltip.isBlank()) {
            button.setToolTipText(tooltip);
        }
        styleActionButton(button);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void appendText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String current = targetField.getText() != null ? targetField.getText() : "";
        if (current.length() >= maxLength) {
            return;
        }

        String next = current + text;

        if (next.length() > maxLength) {
            next = next.substring(0, maxLength);
        }

        targetField.setText(next);
    }

    private void deleteLastChar() {
        String current = targetField.getText();
        if (current == null || current.isEmpty()) {
            return;
        }

        targetField.setText(current.substring(0, current.length() - 1));
    }

    private void clearText() {
        targetField.setText("");
    }

    private void toggleCaseMode() {
        uppercaseMode = !uppercaseMode;
        refreshLetterButtons();
        refreshShiftButton();
    }

    private void refreshLetterButtons() {
        for (JButton button : letterButtons) {
            String baseLetterUpper = (String) button.getClientProperty("baseLetterUpper");
            if (baseLetterUpper == null) {
                continue;
            }

            button.setText(uppercaseMode
                    ? baseLetterUpper.toUpperCase()
                    : baseLetterUpper.toLowerCase());
        }
    }

    private void refreshShiftButton() {
        if (btnShift == null) {
            return;
        }

        btnShift.setText(uppercaseMode
                ? I18n.t("keyboard.uppercase.short")
                : I18n.t("keyboard.lowercase.short"));
        btnShift.setToolTipText(uppercaseMode
                ? I18n.t("keyboard.uppercase")
                : I18n.t("keyboard.lowercase"));
    }

    private void styleKeyButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(60, 55));
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleActionButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(110, 55));
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(InformeUiTheme.ACCENT_GOLD, 1, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}

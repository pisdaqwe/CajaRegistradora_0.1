package ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class InformeUiTheme {

    private InformeUiTheme() {
    }

    public static final Color APP_BG = new Color(10, 24, 20);
    public static final Color CARD_BG = new Color(24, 49, 41);
    public static final Color CARD_BG_2 = new Color(30, 57, 48);
    public static final Color PANEL_BG = new Color(18, 37, 31);

    public static final Color STARBUCKS_GREEN = new Color(0, 112, 74);
    public static final Color STARBUCKS_GREEN_SOFT = new Color(30, 80, 63);
    public static final Color ACCENT_GOLD = new Color(201, 169, 110);

    public static final Color TEXT_PRIMARY = new Color(245, 242, 235);
    public static final Color TEXT_SECONDARY = new Color(189, 203, 196);
    public static final Color BORDER = new Color(48, 82, 70);
    public static final Color DANGER = new Color(150, 52, 52);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 17);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_KPI_TITLE = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_KPI_VALUE = new Font("SansSerif", Font.BOLD, 24);

    public static final Font FONT_LOGIN_DISPLAY = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_KEYPAD = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FONT_KEYPAD_SPECIAL = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_LOGIN_CLOCK = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_LOGIN_CLOCK_HOUR = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_TICKET = new Font("Monospaced", Font.PLAIN, 13);

    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD_BG);
        p.setBorder(createCardBorder());
        return p;
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }

    public static Border createInnerCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        );
    }

    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }
    

    public static JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    public static void stylePrimaryButton(JButton b) {
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBackground(STARBUCKS_GREEN);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton b) {
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBackground(STARBUCKS_GREEN_SOFT);
        b.setForeground(TEXT_PRIMARY);
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton b) {
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(12, 18, 12, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(CARD_BG_2);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(CARD_BG_2);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void styleCheckBox(JCheckBox checkBox) {
        checkBox.setOpaque(false);
        checkBox.setForeground(TEXT_PRIMARY);
        checkBox.setFont(FONT_BODY);
    }

    public static void styleList(JList<?> list) {
        list.setBackground(PANEL_BG);
        list.setForeground(TEXT_PRIMARY);
        list.setSelectionBackground(STARBUCKS_GREEN);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(FONT_BODY);
        list.setFixedCellHeight(28);
        list.setBorder(new EmptyBorder(4, 4, 4, 4));
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_BODY);
        spinner.setBorder(BorderFactory.createLineBorder(BORDER));
        spinner.setBackground(CARD_BG_2);

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setFont(FONT_BODY);
            defaultEditor.getTextField().setBackground(CARD_BG_2);
            defaultEditor.getTextField().setForeground(TEXT_PRIMARY);
            defaultEditor.getTextField().setCaretColor(TEXT_PRIMARY);
            defaultEditor.getTextField().setBorder(new EmptyBorder(6, 8, 6, 8));
        }
    }

    public static void styleTable(JTable table) {
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setRowHeight(34);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(STARBUCKS_GREEN);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(STARBUCKS_GREEN_SOFT);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(FONT_LABEL);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
    }

    public static JPanel createTransparentPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    public static void styleDisplayField(JTextField field) {
        field.setFont(FONT_LOGIN_DISPLAY);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
        field.setBackground(CARD_BG_2);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setFont(FONT_BODY);
        field.setBackground(CARD_BG_2);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_TICKET);
        area.setBackground(new Color(248, 243, 232));
        area.setForeground(new Color(35, 35, 35));
        area.setCaretColor(new Color(35, 35, 35));
        area.setBorder(new CompoundBorder(
                new LineBorder(new Color(215, 205, 185), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void styleKeypadButton(JButton button) {
        button.setFont(FONT_KEYPAD);
        button.setFocusPainted(false);
        button.setBackground(CARD_BG_2);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(new EmptyBorder(18, 12, 18, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleKeypadSpecialButton(JButton button, boolean danger) {
        button.setFont(FONT_KEYPAD_SPECIAL);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(danger ? DANGER : STARBUCKS_GREEN_SOFT);
        button.setBorder(new EmptyBorder(18, 12, 18, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleQuickLoginButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(STARBUCKS_GREEN_SOFT);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 14, 16, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
    }
}

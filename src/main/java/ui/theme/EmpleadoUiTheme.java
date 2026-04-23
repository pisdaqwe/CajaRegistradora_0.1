package ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class EmpleadoUiTheme {

    private EmpleadoUiTheme() {
    }

    public static final Color BG_APP = new Color(18, 18, 18);
    public static final Color BG_PANEL = new Color(28, 28, 28);
    public static final Color BG_CARD = new Color(34, 34, 34);
    public static final Color BG_INPUT = new Color(44, 44, 44);

    public static final Color GREEN_DARK = new Color(17, 76, 63);
    public static final Color GREEN_MAIN = new Color(0, 117, 74);
    public static final Color GREEN_LIGHT = new Color(27, 158, 119);

    public static final Color TEXT_MAIN = new Color(245, 245, 245);
    public static final Color TEXT_SOFT = new Color(185, 185, 185);
    public static final Color BORDER = new Color(65, 65, 65);

    public static final Color DANGER = new Color(170, 60, 60);
    public static final Color WARNING = new Color(170, 130, 50);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);

    public static JPanel createRootPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        return panel;
    }

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PANEL);
        panel.setBorder(createTitledBorder(title));
        return panel;
    }

    public static Border createTitledBorder(String title) {
        Border outer = BorderFactory.createLineBorder(BORDER, 1, true);
        Border inner = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        return BorderFactory.createTitledBorder(
                new CompoundBorder(outer, inner),
                title,
                0,
                0,
                FONT_SUBTITLE,
                TEXT_MAIN
        );
    }

    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    public static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_NORMAL);
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    public static JLabel valueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(FONT_NORMAL);
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    public static JTextField textField(int columns) {
        JTextField field = new JTextField(columns);
        styleTextComponent(field);
        return field;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleTextComponent(field);
        return field;
    }

    public static JTextArea textArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setBackground(BG_INPUT);
        area.setForeground(TEXT_MAIN);
        area.setCaretColor(TEXT_MAIN);
        area.setFont(FONT_NORMAL);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static <T> JComboBox<T> comboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_MAIN);
        combo.setFont(FONT_NORMAL);
        combo.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        return combo;
    }

    public static JCheckBox checkBox(String text) {
        JCheckBox check = new JCheckBox(text);
        check.setOpaque(false);
        check.setForeground(TEXT_MAIN);
        check.setFont(FONT_NORMAL);
        return check;
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        styleButton(btn, GREEN_MAIN, TEXT_MAIN);
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        styleButton(btn, GREEN_DARK, TEXT_MAIN);
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        styleButton(btn, DANGER, TEXT_MAIN);
        return btn;
    }

    public static void styleButton(AbstractButton btn, Color bg, Color fg) {
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER);
        table.setRowHeight(30);
        table.setFont(FONT_NORMAL);
        table.setSelectionBackground(GREEN_DARK);
        table.setSelectionForeground(TEXT_MAIN);
        table.getTableHeader().setBackground(GREEN_MAIN);
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setFont(FONT_BUTTON);
        table.setFillsViewportHeight(true);
    }

    public static JScrollPane scrollPane(Component comp) {
        JScrollPane scroll = new JScrollPane(comp);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        return scroll;
    }

    private static void styleTextComponent(JComponent field) {
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_MAIN);
        field.setFont(FONT_NORMAL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        field.setOpaque(true);
    }
}
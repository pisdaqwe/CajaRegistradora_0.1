package ui.dialog;

import ui.common.TpvDialogUtils;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class SkuDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_WIDTH = 820;
    private static final int DEFAULT_HEIGHT = 620;
    private static final int BUTTON_GAP = 6;
    private static final int MAX_CHARS = 30;

    private JTextField txtSku;
    private JLabel lblCounter;

    private String result;

    public SkuDialog(JFrame owner) {
        super(owner, "Buscar por SKU", true);

        initDialog();
        initComponents();
        buildLayout();
        configurarAtajos();
        updateCounter();

        SwingUtilities.invokeLater(() -> txtSku.requestFocusInWindow());
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int targetW = (int) (screen.width * 0.62);
        int targetH = (int) (screen.height * 0.72);

        int finalW = Math.max(720, Math.min(DEFAULT_WIDTH, targetW));
        int finalH = Math.max(540, Math.min(DEFAULT_HEIGHT, targetH));

        setMinimumSize(new Dimension(700, 520));
        setSize(finalW, finalH);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        txtSku = new JTextField();
        txtSku.setFont(new Font("Monospaced", Font.BOLD, 26));
        txtSku.setHorizontalAlignment(JTextField.CENTER);
        txtSku.setEditable(true);
        txtSku.setFocusable(true);
        txtSku.setBackground(InformeUiTheme.CARD_BG_2);
        txtSku.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtSku.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        txtSku.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        ((AbstractDocument) txtSku.getDocument()).setDocumentFilter(new SkuDocumentFilter());

        txtSku.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCounter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCounter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCounter();
            }
        });

        lblCounter = new JLabel("0 / " + MAX_CHARS, SwingConstants.RIGHT);
        lblCounter.setFont(InformeUiTheme.FONT_LABEL);
        lblCounter.setForeground(InformeUiTheme.TEXT_SECONDARY);
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(14, 16, 14, 16));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildKeyboardPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 2, 4, 2));

        JLabel lblTitle = new JLabel("INTRODUCE SKU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Búsqueda rápida de producto por código", SwingConstants.CENTER);
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(lblTitle);
        titles.add(lblSubtitle);

        panel.add(titles, BorderLayout.CENTER);
        panel.add(lblCounter, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel card = InformeUiTheme.createCardPanel(new BorderLayout(0, 8));
        card.setBorder(InformeUiTheme.createCardBorder());

        JLabel lblCampo = InformeUiTheme.createFieldLabel("SKU / Código de producto");
        lblCampo.setHorizontalAlignment(SwingConstants.CENTER);

        txtSku.setPreferredSize(new Dimension(0, 76));

        card.add(lblCampo, BorderLayout.NORTH);
        card.add(txtSku, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildKeyboardPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, BUTTON_GAP));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        panel.add(buildRow(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}));
        panel.add(buildRow(new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"}));
        panel.add(buildRow(new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L", "-"}));
        panel.add(buildRow(new String[]{"Z", "X", "C", "V", "B", "N", "M"}));
        panel.add(buildEditActionsRow());
        panel.add(buildConfirmRow());

        return panel;
    }

    private JPanel buildRow(String[] keys) {
        JPanel row = new JPanel(new GridLayout(1, keys.length, BUTTON_GAP, BUTTON_GAP));
        row.setOpaque(false);

        for (String key : keys) {
            row.add(createCharButton(key));
        }

        return row;
    }

    private JPanel buildEditActionsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, BUTTON_GAP, BUTTON_GAP));
        row.setOpaque(false);

        JButton btnBackspace = createActionButton("BORRAR");
        btnBackspace.addActionListener(e -> backspace());

        JButton btnClear = createDangerActionButton("LIMPIAR");
        btnClear.addActionListener(e -> clearAll());

        JButton btnCancelar = createSecondaryActionButton("CANCELAR");
        btnCancelar.addActionListener(e -> cancel());

        row.add(btnBackspace);
        row.add(btnClear);
        row.add(btnCancelar);

        return row;
    }

    private JPanel buildConfirmRow() {
        JPanel row = new JPanel(new GridLayout(1, 1, BUTTON_GAP, BUTTON_GAP));
        row.setOpaque(false);

        JButton btnAceptar = createPrimaryActionButton("ACEPTAR");
        btnAceptar.addActionListener(e -> accept());

        row.add(btnAceptar);

        return row;
    }

    private JButton createCharButton(String value) {
        JButton button = new JButton(value);
        styleKeyboardButton(button);
        button.addActionListener(e -> appendText(value));
        return button;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        styleKeyboardButton(button);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        return button;
    }

    private JButton createPrimaryActionButton(String text) {
        JButton button = new JButton(text);
        styleKeyboardButton(button);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN);
        return button;
    }

    private JButton createSecondaryActionButton(String text) {
        JButton button = new JButton(text);
        styleKeyboardButton(button);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(InformeUiTheme.STARBUCKS_GREEN_SOFT);
        return button;
    }

    private JButton createDangerActionButton(String text) {
        JButton button = new JButton(text);
        styleKeyboardButton(button);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(InformeUiTheme.DANGER);
        return button;
    }

    private void styleKeyboardButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(InformeUiTheme.TEXT_PRIMARY);
        button.setBackground(InformeUiTheme.CARD_BG_2);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(58, 42));
    }

    private void configurarAtajos() {
        JRootPane rootPane = getRootPane();

        rootPane.registerKeyboardAction(
                e -> accept(),
                KeyStroke.getKeyStroke("ENTER"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        rootPane.registerKeyboardAction(
                e -> cancel(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void appendText(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String current = txtSku.getText() != null ? txtSku.getText() : "";

        if (current.length() >= MAX_CHARS) {
            return;
        }

        int available = MAX_CHARS - current.length();
        String textToAdd = value.length() > available ? value.substring(0, available) : value;

        txtSku.setText(current + textToAdd.toUpperCase());
        txtSku.requestFocusInWindow();
        updateCounter();
    }

    private void backspace() {
        String current = txtSku.getText();

        if (current == null || current.isEmpty()) {
            return;
        }

        txtSku.setText(current.substring(0, current.length() - 1));
        txtSku.requestFocusInWindow();
        updateCounter();
    }

    private void clearAll() {
        txtSku.setText("");
        txtSku.requestFocusInWindow();
        updateCounter();
    }

    private void updateCounter() {
        if (lblCounter == null || txtSku == null) {
            return;
        }

        lblCounter.setText(txtSku.getText().length() + " / " + MAX_CHARS);
    }

    private void accept() {
        String sku = txtSku.getText() != null ? txtSku.getText().trim() : "";

        if (sku.isEmpty()) {
            TpvDialogUtils.showWarning(
                    this,
                    "SKU",
                    "Introduce un SKU antes de aceptar."
            );
            txtSku.requestFocusInWindow();
            return;
        }

        result = sku;
        dispose();
    }

    private void cancel() {
        result = null;
        dispose();
    }

    public String showDialog() {
        setVisible(true);
        return result;
    }

    private static final class SkuDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            if (text == null) {
                return;
            }

            Document doc = fb.getDocument();
            String current = doc.getText(0, doc.getLength());

            String before = current.substring(0, offset);
            String after = current.substring(offset + length);

            String cleaned = clean(text);
            String candidate = before + cleaned + after;

            if (candidate.length() > MAX_CHARS) {
                int available = MAX_CHARS - before.length() - after.length();

                if (available <= 0) {
                    return;
                }

                cleaned = cleaned.substring(0, Math.min(cleaned.length(), available));
            }

            fb.replace(offset, length, cleaned, attrs);
        }

        private String clean(String text) {
            StringBuilder sb = new StringBuilder();

            for (char c : text.toUpperCase().toCharArray()) {
                if (Character.isLetterOrDigit(c) || c == '-') {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }
}
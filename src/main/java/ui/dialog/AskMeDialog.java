package ui.dialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class AskMeDialog extends JDialog {

    // =========================================================
    // CONSTANTES
    // =========================================================
    private static final int DEFAULT_WIDTH = 820;
    private static final int DEFAULT_HEIGHT = 650;
    private static final int BUTTON_GAP = 6;

    // =========================================================
    // ESTADO
    // =========================================================
    private final int maxChars;
    private boolean uppercase = true;
    private AskMeDialogResult result = AskMeDialogResult.cancelled();

    // =========================================================
    // COMPONENTES UI
    // =========================================================
    private JLabel lblTitle;
    private JLabel lblProductName;
    private JLabel lblCounter;
    private JTextArea txtAskMe;
    private JButton btnCaseToggle;

    // =========================================================
    // APOYO PARA TECLADO
    // =========================================================
    private final List<JButton> letterButtons = new ArrayList<>();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public AskMeDialog(Frame owner, String productName, int maxChars) {
        super(owner, "ASK ME", true);
        this.maxChars = maxChars;

        initDialog();
        initComponents(productName);
        buildLayout();

        updateCounter();
        updateKeyboardCase();
    }

    // =========================================================
    // INICIALIZACIÓN GENERAL DEL DIÁLOGO
    // =========================================================
    private void initDialog() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    // =========================================================
    // CREACIÓN DE COMPONENTES
    // =========================================================
    private void initComponents(String productName) {
        lblTitle = new JLabel("ASK ME", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));

        String safeProductName = (productName == null || productName.isBlank())
                ? "Producto"
                : productName;

        lblProductName = new JLabel(safeProductName, SwingConstants.CENTER);
        lblProductName.setFont(new Font("SansSerif", Font.PLAIN, 16));

        lblCounter = new JLabel("0 / " + maxChars, SwingConstants.RIGHT);
        lblCounter.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtAskMe = new JTextArea(4, 20);
        txtAskMe.setLineWrap(true);
        txtAskMe.setWrapStyleWord(true);
        txtAskMe.setFont(new Font("SansSerif", Font.PLAIN, 20));

        // Importante: solo se escribe con el teclado virtual
        txtAskMe.setEditable(false);
        txtAskMe.setFocusable(false);

        txtAskMe.setBackground(Color.WHITE);
        txtAskMe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
    }

    // =========================================================
    // LAYOUT PRINCIPAL
    // =========================================================
    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildTextPanel(), BorderLayout.CENTER);
        add(buildKeyboardPanel(), BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel titlesPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlesPanel.add(lblTitle);
        titlesPanel.add(lblProductName);

        panel.add(titlesPanel, BorderLayout.CENTER);
        panel.add(lblCounter, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================
    // PANEL CENTRAL DEL TEXTO
    // =========================================================
    private JPanel buildTextPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(txtAskMe);
        scrollPane.setPreferredSize(new Dimension(760, 140));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // PANEL DEL TECLADO
    // =========================================================
    private JPanel buildKeyboardPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, BUTTON_GAP));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(buildLettersRow(new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"}));
        panel.add(buildLettersRow(new String[]{"a", "s", "d", "f", "g", "h", "j", "k", "l", "ñ"}));
        panel.add(buildLettersRow(new String[]{"z", "x", "c", "v", "b", "n", "m"}));
        panel.add(buildNumbersRow());
        panel.add(buildEditActionsRow());
        panel.add(buildConfirmRow());

        return panel;
    }

    private JPanel buildLettersRow(String[] letters) {
        JPanel row = new JPanel(new GridLayout(1, letters.length, BUTTON_GAP, BUTTON_GAP));

        for (String letter : letters) {
            row.add(createLetterButton(letter));
        }

        return row;
    }

    private JPanel buildNumbersRow() {
        JPanel row = new JPanel(new GridLayout(1, 10, BUTTON_GAP, BUTTON_GAP));

        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        for (String number : numbers) {
            row.add(createCharButton(number));
        }

        return row;
    }

    private JPanel buildEditActionsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, BUTTON_GAP, BUTTON_GAP));

        btnCaseToggle = createActionButton("MAYÚS");
        btnCaseToggle.addActionListener(e -> toggleUppercase());

        JButton btnSpace = createActionButton("ESPACIO");
        btnSpace.addActionListener(e -> appendText(" "));

        JButton btnBackspace = createActionButton("BORRAR");
        btnBackspace.addActionListener(e -> backspace());

        JButton btnClear = createActionButton("LIMPIAR");
        btnClear.addActionListener(e -> clearText());

        row.add(btnCaseToggle);
        row.add(btnSpace);
        row.add(btnBackspace);
        row.add(btnClear);

        return row;
    }

    private JPanel buildConfirmRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, BUTTON_GAP, BUTTON_GAP));

        JButton btnCancel = createActionButton("CANCELAR");
        btnCancel.addActionListener(e -> cancel());

        JButton btnConfirm = createActionButton("ACEPTAR");
        btnConfirm.addActionListener(e -> confirm());

        row.add(btnCancel);
        row.add(btnConfirm);

        return row;
    }

    // =========================================================
    // CREACIÓN DE BOTONES
    // =========================================================
    private JButton createLetterButton(String lowercaseValue) {
        JButton button = new JButton(resolveLetterDisplay(lowercaseValue));
        styleKeyboardButton(button);

        button.putClientProperty("lowercaseValue", lowercaseValue.toLowerCase());
        button.addActionListener(e -> {
            String lower = (String) button.getClientProperty("lowercaseValue");
            appendText(resolveLetterDisplay(lower));
        });

        letterButtons.add(button);
        return button;
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
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        return button;
    }

    private void styleKeyboardButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(60, 48));
    }

    // =========================================================
    // LÓGICA DE TEXTO
    // =========================================================
    private void appendText(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String current = txtAskMe.getText();
        if (current.length() >= maxChars) {
            return;
        }

        int available = maxChars - current.length();
        String textToAdd = value.length() > available
                ? value.substring(0, available)
                : value;

        txtAskMe.setText(current + textToAdd);
        updateCounter();
    }

    private void backspace() {
        String current = txtAskMe.getText();
        if (current.isEmpty()) {
            return;
        }

        txtAskMe.setText(current.substring(0, current.length() - 1));
        updateCounter();
    }

    private void clearText() {
        txtAskMe.setText("");
        updateCounter();
    }

    private void toggleUppercase() {
        uppercase = !uppercase;
        updateKeyboardCase();
    }

    private void updateCounter() {
        int length = txtAskMe.getText().length();
        lblCounter.setText(length + " / " + maxChars);
    }

    // =========================================================
    // LÓGICA VISUAL DEL TECLADO
    // =========================================================
    private void updateKeyboardCase() {
        for (JButton button : letterButtons) {
            String lower = (String) button.getClientProperty("lowercaseValue");
            button.setText(resolveLetterDisplay(lower));
        }

        if (btnCaseToggle != null) {
            btnCaseToggle.setText(uppercase ? "MAYÚS" : "minús");
        }
    }

    private String resolveLetterDisplay(String lowercaseValue) {
        if (lowercaseValue == null) {
            return "";
        }

        return uppercase
                ? lowercaseValue.toUpperCase()
                : lowercaseValue.toLowerCase();
    }

    // =========================================================
    // ACEPTAR / CANCELAR
    // =========================================================
    private void confirm() {
        String text = txtAskMe.getText() != null
                ? txtAskMe.getText().trim()
                : "";

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introduce una nota antes de confirmar.",
                    "ASK ME",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        result = AskMeDialogResult.confirmed(text);
        dispose();
    }

    private void cancel() {
        result = AskMeDialogResult.cancelled();
        dispose();
    }

    // =========================================================
    // API PÚBLICA
    // =========================================================
    public AskMeDialogResult showDialog() {
        setVisible(true);
        return result;
    }

}

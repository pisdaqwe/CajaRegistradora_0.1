package ui.dialog;

import javax.swing.*;
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
        updateCounter();
    }

    private void initDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        txtSku = new JTextField();
        txtSku.setFont(new Font("Monospaced", Font.BOLD, 26));
        txtSku.setHorizontalAlignment(JTextField.CENTER);
        txtSku.setEditable(false);
        txtSku.setFocusable(false);

        lblCounter = new JLabel("0 / " + MAX_CHARS, SwingConstants.RIGHT);
        lblCounter.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildKeyboardPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JLabel lblTitle = new JLabel("INTRODUCE SKU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel lblSubtitle = new JLabel("Búsqueda rápida de producto por código", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.add(lblTitle);
        titles.add(lblSubtitle);

        panel.add(titles, BorderLayout.CENTER);
        panel.add(lblCounter, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());

        txtSku.setPreferredSize(new Dimension(760, 90));
        txtSku.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        panel.add(txtSku, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildKeyboardPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, BUTTON_GAP));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(buildRow(new String[]{"1","2","3","4","5","6","7","8","9","0"}));
        panel.add(buildRow(new String[]{"Q","W","E","R","T","Y","U","I","O","P"}));
        panel.add(buildRow(new String[]{"A","S","D","F","G","H","J","K","L","-"}));
        panel.add(buildRow(new String[]{"Z","X","C","V","B","N","M"}));
        panel.add(buildEditActionsRow());
        panel.add(buildConfirmRow());

        return panel;
    }

    private JPanel buildRow(String[] keys) {
        JPanel row = new JPanel(new GridLayout(1, keys.length, BUTTON_GAP, BUTTON_GAP));
        for (String key : keys) {
            row.add(createCharButton(key));
        }
        return row;
    }

    private JPanel buildEditActionsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, BUTTON_GAP, BUTTON_GAP));

        JButton btnBackspace = createActionButton("BORRAR");
        btnBackspace.addActionListener(e -> backspace());

        JButton btnClear = createActionButton("LIMPIAR");
        btnClear.addActionListener(e -> clearAll());

        JButton btnCancelar = createActionButton("CANCELAR");
        btnCancelar.addActionListener(e -> cancel());

        row.add(btnBackspace);
        row.add(btnClear);
        row.add(btnCancelar);

        return row;
    }

    private JPanel buildConfirmRow() {
        JPanel row = new JPanel(new GridLayout(1, 1, BUTTON_GAP, BUTTON_GAP));

        JButton btnAceptar = createActionButton("ACEPTAR");
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
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        return button;
    }

    private void styleKeyboardButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(60, 48));
    }

    private void appendText(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String current = txtSku.getText();
        if (current.length() >= MAX_CHARS) {
            return;
        }

        int available = MAX_CHARS - current.length();
        String textToAdd = value.length() > available ? value.substring(0, available) : value;

        txtSku.setText(current + textToAdd);
        updateCounter();
    }

    private void backspace() {
        String current = txtSku.getText();
        if (current.isEmpty()) {
            return;
        }

        txtSku.setText(current.substring(0, current.length() - 1));
        updateCounter();
    }

    private void clearAll() {
        txtSku.setText("");
        updateCounter();
    }

    private void updateCounter() {
        lblCounter.setText(txtSku.getText().length() + " / " + MAX_CHARS);
    }

    private void accept() {
        String sku = txtSku.getText() != null ? txtSku.getText().trim() : "";

        if (sku.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introduce un SKU antes de aceptar.",
                    "SKU",
                    JOptionPane.WARNING_MESSAGE
            );
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
}
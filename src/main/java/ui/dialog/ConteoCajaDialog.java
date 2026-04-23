package ui.dialog;

import ui.common.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;

public class ConteoCajaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtCantidadContada;
    private BigDecimal importeSeleccionado;

    public ConteoCajaDialog(Window owner) {
        super(owner, "Conteo de caja", ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 620);
        setResizable(false);
        setLocationRelativeTo(owner);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        root.setBackground(InformeUiTheme.APP_BG);
        setContentPane(root);

        JLabel lblTitulo = new JLabel("CONTEO DE CAJA", SwingConstants.CENTER);
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);
        root.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setOpaque(false);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        txtCantidadContada = new JTextField();
        txtCantidadContada.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER),
                "Efectivo contado (€)"
        ));
        txtCantidadContada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        txtCantidadContada.setFont(new Font("Monospaced", Font.BOLD, 28));
        txtCantidadContada.setHorizontalAlignment(JTextField.RIGHT);
        txtCantidadContada.setBackground(InformeUiTheme.CARD_BG_2);
        txtCantidadContada.setForeground(InformeUiTheme.TEXT_PRIMARY);
        txtCantidadContada.setCaretColor(InformeUiTheme.TEXT_PRIMARY);

        ((AbstractDocument) txtCantidadContada.getDocument()).setDocumentFilter(new MoneyDocumentFilter());

        panelCentral.add(txtCantidadContada);
        panelCentral.add(Box.createVerticalStrut(20));

        JPanel keypad = new JPanel(new GridLayout(4, 3, 8, 8));
        keypad.setOpaque(false);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "←"};
        for (String key : keys) {
            keypad.add(createKeypadButton(key));
        }

        panelCentral.add(keypad);
        root.add(panelCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancelar = new JButton("Cancelar");
        InformeUiTheme.styleSecondaryButton(btnCancelar);

        JButton btnAceptar = new JButton("Aceptar");
        InformeUiTheme.stylePrimaryButton(btnAceptar);

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> procesarAceptar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        root.add(panelBotones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnAceptar);
        SwingUtilities.invokeLater(() -> txtCantidadContada.requestFocusInWindow());
    }

    private JButton createKeypadButton(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Monospaced", Font.BOLD, 24));
        b.setFocusable(false);
        InformeUiTheme.styleSecondaryButton(b);

        b.addActionListener(e -> {
            if ("←".equals(label)) {
                int len = txtCantidadContada.getText().length();
                if (len > 0) {
                    try {
                        txtCantidadContada.getDocument().remove(len - 1, 1);
                    } catch (Exception ignored) {
                    }
                }
                return;
            }

            if (".".equals(label) && txtCantidadContada.getText().isEmpty()) {
                txtCantidadContada.setText("0.");
                return;
            }

            txtCantidadContada.replaceSelection(label);
        });

        return b;
    }

    private void procesarAceptar() {
        String texto = txtCantidadContada.getText().trim();

        if (texto.isEmpty() || ".".equals(texto)) {
            JOptionPane.showMessageDialog(this,
                    "Debe introducir un importe válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(texto);

            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }

            importeSeleccionado = valor;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Importe no válido (máx. 2 decimales)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public BigDecimal showDialog() {
        setVisible(true);
        return importeSeleccionado;
    }

    private static class MoneyDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (isValid(fb.getDocument().getText(0, fb.getDocument().getLength()), string, offset)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (isValidAfterReplace(fb.getDocument().getText(0, fb.getDocument().getLength()), offset, length, text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String currentText, String insert, int offset) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.insert(offset, insert);
            return sb.toString().matches("\\d*(\\.\\d{0,2})?");
        }

        private boolean isValidAfterReplace(String currentText, int offset, int length, String replacement) {
            StringBuilder sb = new StringBuilder(currentText);
            sb.replace(offset, offset + length, replacement == null ? "" : replacement);
            return sb.toString().matches("\\d*(\\.\\d{0,2})?");
        }
    }
}
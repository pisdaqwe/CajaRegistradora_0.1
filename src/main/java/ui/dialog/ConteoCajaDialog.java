package ui.dialog;

import ui.common.InformeUiTheme;
import ui.common.TpvDialogUtils;
import ui.theme.TpvIconFactory;
import util.I18n;

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
        super(owner, I18n.t("cashCount.title"), ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(430, 650);
        setResizable(false);
        setLocationRelativeTo(owner);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        root.setBackground(InformeUiTheme.APP_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> txtCantidadContada.requestFocusInWindow());
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel icon = new JLabel(TpvIconFactory.cashRegister(34, InformeUiTheme.ACCENT_GOLD));
        icon.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 4));
        texts.setOpaque(false);

        JLabel lblTitulo = new JLabel(I18n.t("cashCount.header.title"));
        lblTitulo.setFont(InformeUiTheme.FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(I18n.t("cashCount.header.subtitle"));
        lblSubtitulo.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.ACCENT_GOLD);

        texts.add(lblTitulo);
        texts.add(lblSubtitulo);

        header.add(icon, BorderLayout.WEST);
        header.add(texts, BorderLayout.CENTER);

        return header;
    }

    private JComponent buildCenter() {
        JPanel panelCentral = new JPanel();
        panelCentral.setOpaque(false);
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        txtCantidadContada = new JTextField();
        txtCantidadContada.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(InformeUiTheme.BORDER),
                I18n.t("cashCount.amountLabel")
        ));
        txtCantidadContada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        txtCantidadContada.setFont(new Font("Monospaced", Font.BOLD, 30));
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

        return panelCentral;
    }

    private JComponent buildFooter() {
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancelar = new JButton(I18n.t("common.cancel"));
        InformeUiTheme.styleSecondaryButton(btnCancelar);
        btnCancelar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
        btnCancelar.setIconTextGap(8);

        JButton btnAceptar = new JButton(I18n.t("common.accept"));
        InformeUiTheme.stylePrimaryButton(btnAceptar);
        btnAceptar.setIcon(TpvIconFactory.check(18, Color.WHITE));
        btnAceptar.setIconTextGap(8);

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> procesarAceptar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        getRootPane().setDefaultButton(btnAceptar);

        return panelBotones;
    }

    private JButton createKeypadButton(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Monospaced", Font.BOLD, 24));
        b.setFocusable(false);
        InformeUiTheme.styleSecondaryButton(b);

        if ("←".equals(label)) {
            b.setIcon(TpvIconFactory.back(16, InformeUiTheme.TEXT_PRIMARY));
            b.setIconTextGap(6);
        }

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
            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashCount.invalidTitle"),
                    I18n.t("cashCount.invalidRequired")
            );
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
            TpvDialogUtils.showError(
                    this,
                    I18n.t("cashCount.invalidTitle"),
                    I18n.t("cashCount.invalidFormat")
            );
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
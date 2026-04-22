package ui.dialog;

import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PinDialog extends JDialog {

    public enum PinDialogMode {
        LOGIN_COMPLETO,
        LOGIN_RAPIDO
    }

    private enum CampoActivo {
        USUARIO,
        PIN
    }

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_KEYPAD = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_KEYPAD_SPECIAL = new Font("SansSerif", Font.BOLD, 16);

    private final PinDialogMode mode;
    private PinDialogResult result;

    private JPasswordField txtPin;
    private JTextField txtUsuario;
    private JButton btnAceptar;
    private JButton btnCancelar;

    private CampoActivo campoActivo;

    public PinDialog(JFrame parent, PinDialogMode mode, String nombreVisible) {
        super(parent, true);
        this.mode = mode;

        setTitle(mode == PinDialogMode.LOGIN_COMPLETO ? "Login Manual" : "Partner Sign-In");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 760);
        setResizable(false);
        setLocationRelativeTo(parent);

        buildUI(nombreVisible);
    }

    private void buildUI(String nombreVisible) {
        campoActivo = (mode == PinDialogMode.LOGIN_RAPIDO)
                ? CampoActivo.PIN
                : CampoActivo.USUARIO;

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(root);

        JPanel card = new JPanel(new BorderLayout(0, 18));
        card.setBackground(InformeUiTheme.CARD_BG);
        card.setBorder(InformeUiTheme.createCardBorder());

        card.add(buildHeaderPanel(nombreVisible), BorderLayout.NORTH);
        card.add(buildCenterPanel(nombreVisible), BorderLayout.CENTER);
        card.add(buildActionsPanel(), BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnAceptar);

        SwingUtilities.invokeLater(() -> {
            if (mode == PinDialogMode.LOGIN_COMPLETO && txtUsuario != null) {
                txtUsuario.requestFocusInWindow();
                campoActivo = CampoActivo.USUARIO;
            } else {
                txtPin.requestFocusInWindow();
                campoActivo = CampoActivo.PIN;
            }
        });
    }

    private JPanel buildHeaderPanel(String nombreVisible) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(
                mode == PinDialogMode.LOGIN_COMPLETO ? "LOGIN MANUAL" : "PARTNER SIGN-IN",
                SwingConstants.CENTER
        );
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(
                mode == PinDialogMode.LOGIN_COMPLETO
                        ? "Introduce usuario y PIN para acceder"
                        : "Introduce el PIN para continuar",
                SwingConstants.CENTER
        );
        lblSubtitulo.setFont(FONT_SUBTITLE);
        lblSubtitulo.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitulo);
        header.add(Box.createVerticalStrut(8));
        header.add(lblSubtitulo);

        if (mode == PinDialogMode.LOGIN_RAPIDO) {
            header.add(Box.createVerticalStrut(16));
            header.add(buildQuickUserBadge(nombreVisible));
        }

        return header;
    }

    private JComponent buildQuickUserBadge(String nombreVisible) {
        JPanel badge = new JPanel(new BorderLayout());
        badge.setOpaque(true);
        badge.setBackground(InformeUiTheme.PANEL_BG);
        badge.setBorder(new CompoundBorder(
                new LineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        badge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel("Usuario: " + (nombreVisible != null ? nombreVisible : "Desconocido"));
        lbl.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));

        badge.add(lbl, BorderLayout.CENTER);
        return badge;
    }

    private JPanel buildCenterPanel(String nombreVisible) {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        if (mode == PinDialogMode.LOGIN_COMPLETO) {
            center.add(createFieldLabel("Usuario"));
            center.add(Box.createVerticalStrut(6));

            txtUsuario = new JTextField();
            styleDisplayTextField(txtUsuario);
            txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            bindCampoActivo(txtUsuario, CampoActivo.USUARIO);
            center.add(txtUsuario);

            center.add(Box.createVerticalStrut(14));
        }

        center.add(createFieldLabel("PIN"));
        center.add(Box.createVerticalStrut(6));

        txtPin = new JPasswordField();
        stylePasswordField(txtPin);
        txtPin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        bindCampoActivo(txtPin, CampoActivo.PIN);
        center.add(txtPin);

        center.add(Box.createVerticalStrut(18));
        center.add(buildKeypadPanel());

        return center;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void bindCampoActivo(JTextField field, CampoActivo campo) {
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                campoActivo = campo;
            }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campoActivo = campo;
            }
        });
    }

    private JPanel buildKeypadPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(0, 330));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        JPanel keypad = new JPanel(new GridLayout(4, 3, 10, 10));
        keypad.setOpaque(false);
        keypad.setBorder(new EmptyBorder(4, 0, 0, 0));
        keypad.setPreferredSize(new Dimension(0, 310));

        for (int i = 1; i <= 9; i++) {
            keypad.add(createKeypadBtn(String.valueOf(i), false, false));
        }

        keypad.add(createKeypadBtn("C", true, true));
        keypad.add(createKeypadBtn("0", false, false));
        keypad.add(createKeypadBtn("←", true, false));

        wrapper.add(keypad, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildActionsPanel() {
        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        btnAceptar = new JButton("Aceptar");

        InformeUiTheme.styleSecondaryButton(btnCancelar);
        InformeUiTheme.stylePrimaryButton(btnAceptar);

        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnAceptar.setFont(new Font("SansSerif", Font.BOLD, 16));

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> procesarAceptar());

        actions.add(btnCancelar);
        actions.add(btnAceptar);
        return actions;
    }

    private JButton createKeypadBtn(String label, boolean special, boolean danger) {
        JButton b = new JButton(label);
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (special) {
            b.setFont(FONT_KEYPAD_SPECIAL);
            b.setForeground(Color.WHITE);
            b.setBackground(danger ? InformeUiTheme.DANGER : InformeUiTheme.STARBUCKS_GREEN_SOFT);
            b.setBorder(new EmptyBorder(12, 10, 12, 10));
        } else {
            b.setFont(FONT_KEYPAD);
            b.setForeground(InformeUiTheme.TEXT_PRIMARY);
            b.setBackground(InformeUiTheme.CARD_BG_2);
            b.setBorder(new CompoundBorder(
                    new LineBorder(InformeUiTheme.BORDER, 1, true),
                    new EmptyBorder(12, 10, 12, 10)
            ));
        }

        b.addActionListener(e -> {
            JTextField tf = getCampoDestino();

            if ("C".equals(label)) {
                tf.setText("");
                tf.requestFocusInWindow();
                return;
            }

            if ("←".equals(label)) {
                int start = tf.getSelectionStart();
                int end = tf.getSelectionEnd();

                if (start != end) {
                    tf.replaceSelection("");
                } else if (start > 0) {
                    try {
                        tf.getDocument().remove(start - 1, 1);
                    } catch (Exception ignored) {
                    }
                }

                tf.requestFocusInWindow();
                return;
            }

            tf.replaceSelection(label);
            tf.requestFocusInWindow();
        });

        return b;
    }

    private JTextField getCampoDestino() {
        if (mode == PinDialogMode.LOGIN_RAPIDO) {
            return txtPin;
        }

        return campoActivo == CampoActivo.PIN ? txtPin : txtUsuario;
    }

    private void styleDisplayTextField(JTextField field) {
        field.setFont(FONT_DISPLAY);
        field.setBackground(InformeUiTheme.CARD_BG_2);
        field.setForeground(InformeUiTheme.TEXT_PRIMARY);
        field.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        field.setBorder(createInputBorder());
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(FONT_DISPLAY);
        field.setBackground(InformeUiTheme.CARD_BG_2);
        field.setForeground(InformeUiTheme.TEXT_PRIMARY);
        field.setCaretColor(InformeUiTheme.TEXT_PRIMARY);
        field.setBorder(createInputBorder());
    }

    private Border createInputBorder() {
        return new CompoundBorder(
                new LineBorder(InformeUiTheme.BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        );
    }

    private void procesarAceptar() {
        String usuario = (mode == PinDialogMode.LOGIN_COMPLETO)
                ? txtUsuario.getText().trim()
                : null;

        String pin = new String(txtPin.getPassword()).trim();

        if (mode == PinDialogMode.LOGIN_COMPLETO && usuario.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese usuario",
                    "Dato obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            txtUsuario.requestFocusInWindow();
            campoActivo = CampoActivo.USUARIO;
            return;
        }

        if (pin.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese PIN",
                    "Dato obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            txtPin.requestFocusInWindow();
            campoActivo = CampoActivo.PIN;
            return;
        }

        this.result = new PinDialogResult(usuario, pin);
        dispose();
    }

    public PinDialogResult showDialog() {
        setVisible(true);
        return result;
    }
}
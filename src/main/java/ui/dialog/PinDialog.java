package ui.dialog;

import javax.swing.*;
import java.awt.*;

public class PinDialog extends JDialog {

    public enum PinDialogMode {
        LOGIN_COMPLETO,
        LOGIN_RAPIDO
    }

    private final PinDialogMode mode;
    private PinDialogResult result;

    // Componentes UI
    private JPasswordField txtPin; // Declarado correctamente como JPasswordField
    private JTextField txtUsuario;
    private JButton btnAceptar;
    private JButton btnCancelar;

    public PinDialog(JFrame parent, PinDialogMode mode, String nombreVisible) {
        super(parent, true);
        this.mode = mode;

        setTitle(mode == PinDialogMode.LOGIN_COMPLETO ? "Login Manual" : "Partner Sign-In");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(380, 600);
        setResizable(false); // Fijo para evitar descuadres en TPV
        setLocationRelativeTo(parent);

        buildUI(nombreVisible);
    }

    private void buildUI(String nombreVisible) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        // --- CABECERA ---
        JLabel lblTitulo = new JLabel(
                mode == PinDialogMode.LOGIN_COMPLETO ? "LOGIN MANUAL" : "PARTNER SIGN-IN",
                SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        root.add(lblTitulo, BorderLayout.NORTH);

        // --- CUERPO (CAMPOS Y TECLADO) ---
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        // Campo Usuario
        if (mode == PinDialogMode.LOGIN_COMPLETO) {
            txtUsuario = new JTextField();
            txtUsuario.setBorder(BorderFactory.createTitledBorder("Usuario"));
            txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            txtUsuario.setFont(new Font("Monospaced", Font.PLAIN, 18));
            panelCentral.add(txtUsuario);
            panelCentral.add(Box.createVerticalStrut(10));
        } else {
            // Etiqueta informativa en modo rápido
            JLabel lblUser = new JLabel("Usuario: " + (nombreVisible != null ? nombreVisible : "Desconocido"));
            lblUser.setFont(new Font("Monospaced", Font.BOLD, 16));
            lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelCentral.add(lblUser);
            panelCentral.add(Box.createVerticalStrut(15));
        }

        // Campo PIN
        txtPin = new JPasswordField();
        txtPin.setBorder(BorderFactory.createTitledBorder("PIN"));
        txtPin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtPin.setFont(new Font("Monospaced", Font.PLAIN, 18));
        panelCentral.add(txtPin);
        panelCentral.add(Box.createVerticalStrut(20));

        // Teclado Numérico
        JPanel keypad = new JPanel(new GridLayout(4, 3, 10, 10));
        keypad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        for (int i = 1; i <= 9; i++) keypad.add(createKeypadBtn(String.valueOf(i), false));
        keypad.add(createKeypadBtn("C", true)); // Clear
        keypad.add(createKeypadBtn("0", false));
        keypad.add(createKeypadBtn("←", true)); // Backspace

        panelCentral.add(keypad);
        root.add(panelCentral, BorderLayout.CENTER);

        // --- PIE (BOTONES ACCIÓN) ---
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnCancelar = new JButton("Cancelar");
        btnAceptar = new JButton("Aceptar");
        
        btnCancelar.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnAceptar.setFont(new Font("Monospaced", Font.BOLD, 16));

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> procesarAceptar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);

        root.add(panelBotones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnAceptar);
        
        SwingUtilities.invokeLater(() -> {
            txtPin.requestFocusInWindow();
            txtPin.selectAll();
        });
    }

    private JButton createKeypadBtn(String label, boolean isSpecial) {
        JButton b = new JButton(label);
        b.setFont(new Font("Monospaced", Font.BOLD, isSpecial ? 18 : 22));
        b.setFocusable(false); // Clave para que el foco siga en el JTextField
        
        b.addActionListener(e -> {
            Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (!(owner instanceof JTextField)) return;
            
            JTextField tf = (JTextField) owner;
            if (label.equals("C")) {
                tf.setText("");
            } else if (label.equals("←")) {
                int start = tf.getSelectionStart();
                if (tf.getSelectionStart() != tf.getSelectionEnd()) {
                    tf.replaceSelection("");
                } else if (start > 0) {
                    try { tf.getDocument().remove(start - 1, 1); } catch (Exception ignored) {}
                }
            } else {
                tf.replaceSelection(label);
            }
        });
        return b;
    }

    private void procesarAceptar() {
        String usuario = (mode == PinDialogMode.LOGIN_COMPLETO) ? txtUsuario.getText().trim() : null;
        String pin = new String(txtPin.getPassword()).trim();

        if (mode == PinDialogMode.LOGIN_COMPLETO && usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario", "Error", JOptionPane.WARNING_MESSAGE);
            txtUsuario.requestFocus();
            return;
        }
        if (pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese PIN", "Error", JOptionPane.WARNING_MESSAGE);
            txtPin.requestFocus();
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

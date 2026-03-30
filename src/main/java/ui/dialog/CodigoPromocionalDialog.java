package ui.dialog;



import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ui.common.TecladoAlfaNumericoPanel;

import java.awt.*;

public class CodigoPromocionalDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private CodigoPromocionalDialogResult result =
            new CodigoPromocionalDialogResult(false, null);

    private final JTextField txtCodigo = new JTextField();

    public CodigoPromocionalDialog(Window owner) {
        super(owner, "Código promocional", ModalityType.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Introduce el código promocional");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        txtCodigo.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtCodigo.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigo.setPreferredSize(new Dimension(420, 50));
        txtCodigo.setEditable(false);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(txtCodigo, BorderLayout.CENTER);

        TecladoAlfaNumericoPanel teclado = new  TecladoAlfaNumericoPanel (txtCodigo, 20);

        JButton btnAceptar = new JButton("APLICAR");
        JButton btnCancelar = new JButton("CANCELAR");

        btnAceptar.addActionListener(e -> onAceptar());
        btnCancelar.addActionListener(e -> onCancelar());

        styleActionButton(btnAceptar, new Color(0, 92, 62));
        styleActionButton(btnCancelar, new Color(90, 90, 90));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnCancelar);
        actions.add(btnAceptar);

        root.add(top, BorderLayout.NORTH);
        root.add(teclado, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void onAceptar() {
        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        result = new CodigoPromocionalDialogResult(true, codigo);
        dispose();
    }

    private void onCancelar() {
        result = new CodigoPromocionalDialogResult(false, null);
        dispose();
    }

    public CodigoPromocionalDialogResult showDialog() {
        txtCodigo.setText("");
        result = new CodigoPromocionalDialogResult(false, null);
        setVisible(true);
        return result;
    }

    private void styleActionButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
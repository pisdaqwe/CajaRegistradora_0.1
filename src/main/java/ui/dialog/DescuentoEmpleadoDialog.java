package ui.dialog;



import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ui.common.TecladoAlfaNumericoPanel;

import java.awt.*;

public class DescuentoEmpleadoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private DescuentoEmpleadoDialogResult result =
            new DescuentoEmpleadoDialogResult(false, null);

    private final JTextField txtCodigoEmpleado = new JTextField();

    public DescuentoEmpleadoDialog(Window owner) {
        super(owner, "Descuento de empleado", ModalityType.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Introduce el código del empleado");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        txtCodigoEmpleado.setFont(new Font("SansSerif", Font.BOLD, 22));
        txtCodigoEmpleado.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigoEmpleado.setPreferredSize(new Dimension(420, 50));
        txtCodigoEmpleado.setEditable(false);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(txtCodigoEmpleado, BorderLayout.CENTER);

        TecladoAlfaNumericoPanel teclado = new TecladoAlfaNumericoPanel(txtCodigoEmpleado, 20);

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
        String codigo = txtCodigoEmpleado.getText() != null ? txtCodigoEmpleado.getText().trim() : "";
        result = new DescuentoEmpleadoDialogResult(true, codigo);
        dispose();
    }

    private void onCancelar() {
        result = new DescuentoEmpleadoDialogResult(false, null);
        dispose();
    }

    public DescuentoEmpleadoDialogResult showDialog() {
        txtCodigoEmpleado.setText("");
        result = new DescuentoEmpleadoDialogResult(false, null);
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
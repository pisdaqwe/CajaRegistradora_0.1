package ui.dialog;

import ui.dialog.MermaDialogResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MermaDialog extends JDialog {

    private static final Color BG = new Color(236, 233, 226);
    private static final Color PANEL = new Color(250, 248, 243);
    private static final Color STARBUCKS = new Color(0, 92, 61);
    private static final Color STARBUCKS_DARK = new Color(0, 70, 45);
    private static final Color TEXT = new Color(45, 45, 45);
    private static final Color BORDER = new Color(210, 210, 210);

    private JTextField txtMotivo;
    private JTextArea txtObservaciones;

    private MermaDialogResult result = MermaDialogResult.cancelled();

    public MermaDialog(Window owner) {
        super(owner, "Registrar merma", ModalityType.APPLICATION_MODAL);

        buildUI();

        setMinimumSize(new Dimension(760, 560));
        setSize(820, 620);
        setLocationRelativeTo(owner);
    }

    public MermaDialogResult showDialog() {
        setVisible(true);
        return result;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JComponent buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(STARBUCKS);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("REGISTRAR MERMA");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Selecciona los productos desde el ticket y registra todos de una vez");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblSub.setForeground(new Color(225, 225, 225));

        left.add(lblTitulo);
        left.add(Box.createVerticalStrut(6));
        left.add(lblSub);

        JLabel lblIcon = new JLabel("⚠");
        lblIcon.setFont(new Font("SansSerif", Font.BOLD, 48));
        lblIcon.setForeground(new Color(255, 215, 90));

        panel.add(left, BorderLayout.WEST);
        panel.add(lblIcon, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(18, 0, 18, 0));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(28, 28, 28, 28)
        ));

        JLabel lblInfo = new JLabel(
                "<html><b>¿Qué se registrará?</b><br>" +
                "Se registrarán todos los items actualmente seleccionados en el modo merma del ticket.</html>"
        );
        lblInfo.setOpaque(true);
        lblInfo.setBackground(new Color(244, 248, 255));
        lblInfo.setBorder(new EmptyBorder(16, 16, 16, 16));
        lblInfo.setForeground(TEXT);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblInfo);
        card.add(Box.createVerticalStrut(26));

        JLabel lblMotivo = new JLabel("Motivo de la merma *");
        lblMotivo.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblMotivo.setForeground(TEXT);
        lblMotivo.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtMotivo = new JTextField();
        txtMotivo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtMotivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtMotivo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        card.add(lblMotivo);
        card.add(Box.createVerticalStrut(10));
        card.add(txtMotivo);
        card.add(Box.createVerticalStrut(24));

        JLabel lblObs = new JLabel("Observaciones");
        lblObs.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblObs.setForeground(TEXT);
        lblObs.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtObservaciones = new JTextArea();
        txtObservaciones.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(Color.WHITE);
        txtObservaciones.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(txtObservaciones);
        scroll.setPreferredSize(new Dimension(400, 180));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHint = new JLabel(
                "Ejemplo: Producto caducado, bebida preparada incorrectamente, caída al suelo..."
        );
        lblHint.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblHint.setForeground(new Color(110, 110, 110));
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblObs);
        card.add(Box.createVerticalStrut(10));
        card.add(scroll);
        card.add(Box.createVerticalStrut(10));
        card.add(lblHint);

        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JComponent buildBottom() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblFooter = new JLabel("La merma descontará stock y quedará registrada en auditoría.");
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblFooter.setForeground(new Color(100, 100, 100));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);

        JButton btnCancelar = createSecondaryButton("Cancelar");
        btnCancelar.addActionListener(e -> {
            result = MermaDialogResult.cancelled();
            dispose();
        });

        JButton btnAceptar = createPrimaryButton("Registrar merma");
        btnAceptar.addActionListener(e -> onAceptar());

        buttons.add(btnCancelar);
        buttons.add(btnAceptar);

        panel.add(lblFooter, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(STARBUCKS);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setPreferredSize(new Dimension(190, 48));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(STARBUCKS_DARK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(STARBUCKS);
            }
        });

        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(TEXT);
        btn.setBackground(new Color(235, 235, 235));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 190, 190), 1, true),
                new EmptyBorder(12, 22, 12, 22)
        ));
        btn.setPreferredSize(new Dimension(150, 48));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(235, 235, 235));
            }
        });

        return btn;
    }

    private void onAceptar() {
        String motivo = txtMotivo.getText() != null
                ? txtMotivo.getText().trim()
                : "";

        String observaciones = txtObservaciones.getText() != null
                ? txtObservaciones.getText().trim()
                : "";

        if (motivo.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes indicar un motivo para registrar la merma.",
                    "Motivo obligatorio",
                    JOptionPane.WARNING_MESSAGE
            );
            txtMotivo.requestFocus();
            return;
        }

        result = MermaDialogResult.confirmed(motivo, observaciones);
        dispose();
    }
}
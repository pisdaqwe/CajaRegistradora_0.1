package ui.ventas;

import model.TicketSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class BottomBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final TicketSession ticketSession;

    private final Runnable onCobrar;
    private final Runnable onCancelar;
    private final Runnable onOpciones;
    private final Runnable onDescuentos;
    private final Runnable onEliminar;

    private final JLabel lblTotal = new JLabel("TOTAL: 0.00€", SwingConstants.LEFT);
    private final JButton btnCobrar = new JButton("COBRAR");
    private final JButton btnCancelar = new JButton("CANCELAR");
    private final JButton btnOpciones = new JButton("OPCIONES");
    private final JButton btnDescuentos = new JButton("DESCUENTOS");
    private final JButton btnEliminar = new JButton("ELIMINAR");

    public BottomBarPanel(
            TicketSession ticketSession,
            Runnable onCobrar,
            Runnable onCancelar,
            Runnable onOpciones,
            Runnable onDescuentos,
            Runnable onEliminar
    ) {
        this.ticketSession = ticketSession;
        this.onCobrar = onCobrar;
        this.onCancelar = onCancelar;
        this.onOpciones = onOpciones;
        this.onDescuentos = onDescuentos;
        this.onEliminar = onEliminar;
        
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblTotal.setForeground(new Color(255, 210, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(lblTotal);

        JPanel right = new JPanel(new GridLayout(1, 4, 10, 10));
        right.setOpaque(false);

        styleBtn(btnDescuentos);
        styleBtn(btnOpciones);
        styleBtn(btnCancelar);
        stylePrimaryBtn(btnCobrar);
        
        btnEliminar.setBackground(new Color(255,0,0));

        btnCobrar.addActionListener(e -> { if (onCobrar != null) onCobrar.run(); });
        btnCancelar.addActionListener(e -> { if (onCancelar != null) onCancelar.run(); });
        btnOpciones.addActionListener(e -> { if (onOpciones != null) onOpciones.run(); });
        btnDescuentos.addActionListener(e -> { if (onDescuentos != null) onDescuentos.run(); });
        btnEliminar.addActionListener(e->{if(onEliminar!=null) onEliminar.run();});

        right.add(btnDescuentos);
        right.add(btnOpciones);
        right.add(btnCancelar);
        right.add(btnCobrar);
        right.add(btnEliminar);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        BigDecimal total = ticketSession.getTotal();
        lblTotal.setText("TOTAL: " + total.toPlainString() + "€");
        btnCobrar.setEnabled(!ticketSession.isEmpty());
        btnCancelar.setEnabled(!ticketSession.isEmpty());
    }

    private void styleBtn(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    private void stylePrimaryBtn(JButton b) {
        styleBtn(b);
        b.setFont(new Font("Monospaced", Font.BOLD, 18));
    }
}
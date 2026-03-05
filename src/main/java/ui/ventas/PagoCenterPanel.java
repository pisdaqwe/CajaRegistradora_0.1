package ui.ventas;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PagoCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public PagoCenterPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(20, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildCenterInfo(), BorderLayout.CENTER);
        add(buildMetodosPagoBar(), BorderLayout.SOUTH);
    }

    private JPanel buildCenterInfo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel title = new JLabel("PAGO (placeholder)", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Monospaced", Font.BOLD, 28));

        JLabel sub = new JLabel("Aquí irá el flujo de cobro (Nombre / Método / Confirmación)", SwingConstants.CENTER);
        sub.setForeground(new Color(200, 200, 200));
        sub.setFont(new Font("Monospaced", Font.PLAIN, 16));

        p.add(title, BorderLayout.CENTER);
        p.add(sub, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildMetodosPagoBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 12));
        bar.setOpaque(false);

        bar.add(createTpvButton("EFECTIVO"));
        bar.add(createTpvButton("TARJETA"));
        bar.add(createTpvButton("EXACTO"));

        return bar;
    }

    private JButton createTpvButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 18));
        b.setBackground(new Color(255, 210, 0)); // amarillo TPV
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        return b;
    }
}

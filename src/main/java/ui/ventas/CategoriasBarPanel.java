package ui.ventas;

import dtoS.CategoriaDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CategoriasBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CategoriasBarPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
    }

    public void setCategorias(List<CategoriaDTO> categorias, Consumer<CategoriaDTO> onClick) {
        removeAll();

        for (CategoriaDTO c : categorias) {
            JButton b = createTpvButton(c.getNombre());
            b.addActionListener(e -> {
                if (onClick != null) onClick.accept(c);
            });
            add(b);
        }

        revalidate();
        repaint();
    }

    private JButton createTpvButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        return b;
    }
}

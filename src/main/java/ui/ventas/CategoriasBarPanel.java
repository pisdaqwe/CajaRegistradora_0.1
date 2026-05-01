package ui.ventas;

import dtoS.CategoriaDTO;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CategoriasBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public CategoriasBarPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 14));
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    public void setCategorias(List<CategoriaDTO> categorias, Consumer<CategoriaDTO> onClick) {
        removeAll();

        if (categorias != null) {
            for (CategoriaDTO c : categorias) {
                JButton b = createTpvButton(c.getNombre());
                b.addActionListener(e -> {
                    if (onClick != null) {
                        onClick.accept(c);
                    }
                });
                add(b);
            }
        }

        revalidate();
        repaint();
    }

    private JButton createTpvButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 15f));
        b.setBackground(InformeUiTheme.ACCENT_GOLD);
        b.setForeground(new Color(28, 28, 22));
        b.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setIcon(TpvIconFactory.folder(18, new Color(28, 28, 22)));
        b.setIconTextGap(8);
        return b;
    }
}

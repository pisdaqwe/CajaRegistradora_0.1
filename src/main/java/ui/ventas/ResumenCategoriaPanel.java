package ui.ventas;

import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ResumenCategoriaPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppServices services;

    private final JPanel content = new JPanel();
    private final JScrollPane scroll;
    Consumer<SubCategoriaDTO>onHeaderClick;

    public ResumenCategoriaPanel(AppServices services,Consumer<SubCategoriaDTO> onHeaderClick) {
        this.services = services;
        this.onHeaderClick = onHeaderClick;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(20, 20, 20));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(20, 20, 20));
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);
    }

    public void loadCategoria(int idCategoria) {
        content.removeAll();

        List<SubCategoriaDTO> subcats = services.catalogoService.getSubcategoriasByCategoria(idCategoria);

        for (SubCategoriaDTO sub : subcats) {
            content.add(buildSubcategoriaSection(sub));
            content.add(Box.createVerticalStrut(14));
        }

        revalidate();
        repaint();
        scroll.getVerticalScrollBar().setValue(0);
    }

    private JComponent buildSubcategoriaSection(SubCategoriaDTO sub) {

        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setOpaque(false);

        JButton header = createHeaderButton(sub.getNombre());
        header.addActionListener(e -> {
            if (onHeaderClick != null) {
                onHeaderClick.accept(sub);
            }
        });

        section.add(header, BorderLayout.NORTH);

        List<ProductoDTO> top = services.catalogoService.getTopProductosBySubcategoria(sub.getIdSubcategoria(), 6);

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);

        for (ProductoDTO p : top) {
            JButton b = createProductoButton(p.getNombre());
            // AÚN NO AÑADIMOS AL TICKET
            b.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Producto pulsado (aún no añade al ticket): " + p.getNombre()));
            grid.add(b);
        }

        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JButton createHeaderButton(String text) {
        JButton b = new JButton(text + "  ▶");
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Monospaced", Font.BOLD, 18));
        b.setBackground(new Color(45, 45, 45));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return b;
    }

    private JButton createProductoButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return b;
    }
}

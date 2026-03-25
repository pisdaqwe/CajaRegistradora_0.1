package ui.ventas;

import dtoS.ProductoCatalogoDTO;
import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

public class ResumenCategoriaPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppServices services;

    private final JPanel content = new JPanel();
    private final JScrollPane scroll;
    private final Consumer<SubCategoriaDTO> onHeaderClick;
    private final Consumer<ProductoDTO> onProductoClicked;

    public ResumenCategoriaPanel(
            AppServices services,
            Consumer<SubCategoriaDTO> onHeaderClick,
            Consumer<ProductoDTO> onProductoClicked
    ) {
        this.services = services;
        this.onHeaderClick = onHeaderClick;
        this.onProductoClicked = onProductoClicked;

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

        List<ProductoCatalogoDTO> top =
                services.catalogoService.getTopProductosCatalogoBySubcategoria(sub.getIdSubcategoria(), 6);

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);

        for (ProductoCatalogoDTO p : top) {
            JButton b = createProductoButton(p);
            b.addActionListener(e -> {
                if (!p.isBotonHabilitado()) {
                    return;
                }

                if (onProductoClicked != null) {
                    onProductoClicked.accept(toProductoDTO(p));
                }
            });
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

    private JButton createProductoButton(ProductoCatalogoDTO producto) {
        JButton b = new JButton(buildButtonText(producto));
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 15));
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        if (!producto.isBotonHabilitado()) {
            b.setEnabled(false);
            b.setBackground(new Color(120, 120, 120));
            b.setForeground(Color.WHITE);
            return b;
        }

        if (producto.muestraContador()) {
            b.setBackground(new Color(255, 230, 120));
            b.setForeground(Color.BLACK);
        } else {
            b.setBackground(new Color(255, 210, 0));
            b.setForeground(Color.BLACK);
        }

        return b;
    }

    private String buildButtonText(ProductoCatalogoDTO producto) {
        if (!producto.isDisponible()) {
            return "<html><center>" + producto.getNombre() + "<br><b>NO DISP.</b></center></html>";
        }

        if (producto.isAgotado()) {
            return "<html><center>" + producto.getNombre() + "<br><b>AGOTADO</b></center></html>";
        }

        if (producto.muestraContador()) {
            return "<html><center>" + producto.getNombre() + "<br><b>("
                    + formatStock(producto.getStockActual()) + ")</b></center></html>";
        }

        return producto.getNombre();
    }

    private String formatStock(BigDecimal stock) {
        if (stock == null) {
            return "0";
        }
        return stock.stripTrailingZeros().toPlainString();
    }

    private ProductoDTO toProductoDTO(ProductoCatalogoDTO p) {
        return new ProductoDTO(
                p.getIdProducto(),
                p.getIdSubcategoria(),
                p.getNombre(),
                p.getOrden(),
                p.isPermiteExtras(),
                p.isPermitePersonalizacion(),
                p.getIvaPorcentaje(),
                p.isPermiteStockCantidad()
        );
    }
}

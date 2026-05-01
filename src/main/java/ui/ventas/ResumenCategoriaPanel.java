package ui.ventas;

import dtoS.ProductoCatalogoDTO;
import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import service.AppServices;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

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
        setBackground(InformeUiTheme.APP_BG);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(InformeUiTheme.APP_BG);
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        scroll = new JScrollPane(content);
        InformeUiTheme.styleScrollPane(scroll);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);
    }

    public void loadCategoria(int idCategoria) {
        content.removeAll();

        List<SubCategoriaDTO> subcats = services.catalogoService.getSubcategoriasByCategoria(idCategoria);

        if (subcats != null) {
            for (SubCategoriaDTO sub : subcats) {
                content.add(buildSubcategoriaSection(sub));
                content.add(Box.createVerticalStrut(14));
            }
        }

        revalidate();
        repaint();
        scroll.getVerticalScrollBar().setValue(0);
    }

    private JComponent buildSubcategoriaSection(SubCategoriaDTO sub) {
        JPanel section = InformeUiTheme.createTransparentPanel(new BorderLayout(10, 10));

        JButton header = createHeaderButton(sub.getNombre());
        header.addActionListener(e -> {
            if (onHeaderClick != null) {
                onHeaderClick.accept(sub);
            }
        });

        section.add(header, BorderLayout.NORTH);

        List<ProductoCatalogoDTO> top =
                services.catalogoService.getTopProductosCatalogoBySubcategoria(sub.getIdSubcategoria(), 6);

        JPanel grid = InformeUiTheme.createTransparentPanel(new GridLayout(2, 3, 12, 12));

        if (top != null) {
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
        }

        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JButton createHeaderButton(String text) {
        JButton b = new JButton(text + "  ▶");
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(InformeUiTheme.FONT_SECTION);
        b.setBackground(InformeUiTheme.CARD_BG_2);
        b.setForeground(InformeUiTheme.TEXT_PRIMARY);
        b.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setIcon(TpvIconFactory.folder(18, InformeUiTheme.ACCENT_GOLD));
        b.setIconTextGap(8);
        return b;
    }

    private JButton createProductoButton(ProductoCatalogoDTO producto) {
        JButton b = new JButton(buildButtonText(producto));
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 15f));
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setIcon(TpvIconFactory.product(18, new Color(28, 28, 22)));
        b.setIconTextGap(8);

        if (!producto.isBotonHabilitado()) {
            b.setEnabled(false);
            b.setBackground(new Color(96, 106, 100));
            b.setForeground(Color.WHITE);
            b.setIcon(TpvIconFactory.warning(18, Color.WHITE));
            return b;
        }

        if (producto.muestraContador()) {
            b.setBackground(new Color(229, 202, 126));
            b.setForeground(new Color(28, 28, 22));
        } else {
            b.setBackground(InformeUiTheme.ACCENT_GOLD);
            b.setForeground(new Color(28, 28, 22));
        }

        return b;
    }

    private String buildButtonText(ProductoCatalogoDTO producto) {
        if (!producto.isDisponible()) {
            return "<html><center>" + producto.getNombre() + "<br><b>"
                    + I18n.t("sales.catalog.unavailable") + "</b></center></html>";
        }

        if (producto.isAgotado()) {
            return "<html><center>" + producto.getNombre() + "<br><b>"
                    + I18n.t("sales.catalog.outOfStock") + "</b></center></html>";
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

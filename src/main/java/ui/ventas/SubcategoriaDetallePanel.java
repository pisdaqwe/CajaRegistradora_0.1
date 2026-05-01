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

public class SubcategoriaDetallePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppServices services;
    private final Runnable onBack;
    private final Consumer<ProductoDTO> onProductoClicked;

    private final JLabel lblTitle = new JLabel("", SwingConstants.LEFT);
    private final JPanel grid = new JPanel();

    public SubcategoriaDetallePanel(
            AppServices services,
            Runnable onBack,
            Consumer<ProductoDTO> onProductoClicked
    ) {
        this.services = services;
        this.onBack = onBack;
        this.onProductoClicked = onProductoClicked;

        setLayout(new BorderLayout(12, 12));
        setBackground(InformeUiTheme.APP_BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildGridScroll(), BorderLayout.CENTER);
    }

    private JComponent buildTopBar() {
        JPanel top = InformeUiTheme.createTransparentPanel(new BorderLayout(12, 12));

        JButton btnBack = createDarkButton(I18n.t("common.back"));
        btnBack.setIcon(TpvIconFactory.back(18, InformeUiTheme.TEXT_PRIMARY));
        btnBack.setIconTextGap(8);
        btnBack.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        lblTitle.setFont(InformeUiTheme.FONT_TITLE.deriveFont(Font.BOLD, 22f));
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);
        lblTitle.setIcon(TpvIconFactory.folder(22, InformeUiTheme.ACCENT_GOLD));
        lblTitle.setIconTextGap(8);

        top.add(btnBack, BorderLayout.WEST);
        top.add(lblTitle, BorderLayout.CENTER);

        return top;
    }

    private JComponent buildGridScroll() {
        grid.setLayout(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);

        JScrollPane sp = new JScrollPane(grid);
        InformeUiTheme.styleScrollPane(sp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(18);

        return sp;
    }

    public void showSubcategoria(SubCategoriaDTO sub) {
        lblTitle.setText(sub.getNombre());

        grid.removeAll();

        List<ProductoCatalogoDTO> productos =
                services.catalogoService.getProductosCatalogoBySubcategoria(sub.getIdSubcategoria());

        if (productos != null) {
            for (ProductoCatalogoDTO p : productos) {
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

        revalidate();
        repaint();
    }

    private JButton createProductoButton(ProductoCatalogoDTO producto) {
        JButton b = new JButton(buildButtonText(producto));
        b.setFocusPainted(false);
        b.setFont(InformeUiTheme.FONT_BUTTON.deriveFont(Font.BOLD, 16f));
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

    private JButton createDarkButton(String text) {
        JButton b = new JButton(text);
        InformeUiTheme.styleSecondaryButton(b);
        return b;
    }
}

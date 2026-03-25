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
        setBackground(new Color(20, 20, 20));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildGridScroll(), BorderLayout.CENTER);
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JButton btnBack = createDarkButton("← VOLVER");
        btnBack.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        top.add(btnBack, BorderLayout.WEST);
        top.add(lblTitle, BorderLayout.CENTER);

        return top;
    }

    private JComponent buildGridScroll() {
        grid.setLayout(new GridLayout(0, 3, 12, 12));
        grid.setOpaque(false);

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(20, 20, 20));
        sp.getVerticalScrollBar().setUnitIncrement(18);

        return sp;
    }

    public void showSubcategoria(SubCategoriaDTO sub) {
        lblTitle.setText(sub.getNombre());

        grid.removeAll();

        List<ProductoCatalogoDTO> productos =
                services.catalogoService.getProductosCatalogoBySubcategoria(sub.getIdSubcategoria());

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

        revalidate();
        repaint();
    }

    private JButton createProductoButton(ProductoCatalogoDTO producto) {
        JButton b = new JButton(buildButtonText(producto));
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
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

    private JButton createDarkButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(new Color(45, 45, 45));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return b;
    }
}

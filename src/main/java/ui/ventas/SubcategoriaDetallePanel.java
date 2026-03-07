package ui.ventas;

import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import service.AppServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SubcategoriaDetallePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppServices services;
    private final Runnable onBack;
    private final Consumer<ProductoDTO> onProductoClicked;

    private final JLabel lblTitle = new JLabel("", SwingConstants.LEFT);
    private final JPanel grid = new JPanel();

    public SubcategoriaDetallePanel(AppServices services, Runnable onBack,Consumer<ProductoDTO> onProductoClicked) {
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
            if (onBack != null) onBack.run();
        });

        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        top.add(btnBack, BorderLayout.WEST);
        top.add(lblTitle, BorderLayout.CENTER);

        return top;
    }

    private JComponent buildGridScroll() {
        grid.setLayout(new GridLayout(0, 3, 12, 12)); // 3 columnas, filas dinámicas
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

        List<ProductoDTO> productos = services.catalogoService.getProductosBySubcategoria(sub.getIdSubcategoria());

        for (ProductoDTO p : productos) {
            JButton b = createYellowButton(p.getNombre());
           
            b.addActionListener(e -> {
                if (onProductoClicked != null) onProductoClicked.accept(p);
            });
            grid.add(b);
        }

        revalidate();
        repaint();
    }

    private JButton createYellowButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 16));
        b.setBackground(new Color(255, 210, 0));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return b;
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

package ui.ventas;

import dtoS.ProductoDTO;
import dtoS.SubCategoriaDTO;
import service.AppServices;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CatalogoCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String CARD_RESUMEN = "RESUMEN";
    private static final String CARD_DETALLE = "DETALLE";

    private final CardLayout layout = new CardLayout();

    private final ResumenCategoriaPanel resumenPanel;
    private final SubcategoriaDetallePanel detallePanel;

    // Para volver al resumen de la categoría actual
    private int currentCategoriaId = -1;

    public CatalogoCenterPanel(AppServices services,Consumer<ProductoDTO> onProductoClicked) {
        setLayout(layout);
        setBackground(new Color(20, 20, 20));

        resumenPanel = new ResumenCategoriaPanel(
                services,
                this::showSubcategoriaDetalle,
                onProductoClicked
        );

        detallePanel = new SubcategoriaDetallePanel(
                services,
                this::showResumenActual,
                onProductoClicked
        );

        add(resumenPanel, CARD_RESUMEN);
        add(detallePanel, CARD_DETALLE);

        layout.show(this, CARD_RESUMEN);
    }

    public void showCategoria(int idCategoria) {
        this.currentCategoriaId = idCategoria;

        resumenPanel.loadCategoria(idCategoria);
        layout.show(this, CARD_RESUMEN);
    }

    private void showSubcategoriaDetalle(SubCategoriaDTO sub) {
        detallePanel.showSubcategoria(sub);
        layout.show(this, CARD_DETALLE);
    }

    private void showResumenActual() {
        if (currentCategoriaId > 0) {
            resumenPanel.loadCategoria(currentCategoriaId);
        }
        layout.show(this, CARD_RESUMEN);
    }
}
package ui.ventas;

import service.AppServices;

import java.awt.CardLayout;
import javax.swing.JPanel;

public class VentasCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final String CARD_CATALOGO = "CATALOGO";
    public static final String CARD_PAGO = "PAGO";

    private final CardLayout cardLayout;

    private final CatalogoCenterPanel catalogoPanel;
    private final PagoCenterPanel pagoPanel;

    public VentasCenterPanel(AppServices services) {
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);

        this.catalogoPanel = new CatalogoCenterPanel(services);
        this.pagoPanel = new PagoCenterPanel(); // placeholder

        add(catalogoPanel, CARD_CATALOGO);
        add(pagoPanel, CARD_PAGO);

        showCatalogo();
    }

    public CatalogoCenterPanel getCatalogoPanel() {
        return catalogoPanel;
    }

    public void showCatalogo() {
        showCard(CARD_CATALOGO);
    }

    public void showPago() {
        showCard(CARD_PAGO);
    }

    public void showCard(String name) {
        cardLayout.show(this, name);
    }
    
}
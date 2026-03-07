package ui.ventas;

import service.AppServices;

import java.awt.CardLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

import dtoS.ProductoDTO;
import enums.CustomizationCard;

public class VentasCenterPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final String CARD_CATALOGO = "CATALOGO";
	public static final String CARD_PAGO = "PAGO";
	public static final String CARD_CUSTOM = "CUSTOM";

	private final CardLayout cardLayout;

	private final CatalogoCenterPanel catalogoPanel;
	private final PagoCenterPanel pagoPanel;
	private final CustomizationCenterPanel customizationCenterPanel;

	public VentasCenterPanel(AppServices services, Consumer<ProductoDTO> onProductoCicked) {
		this.cardLayout = new CardLayout();
		setLayout(cardLayout);

		this.catalogoPanel = new CatalogoCenterPanel(services, onProductoCicked);
		this.pagoPanel = new PagoCenterPanel(); // placeholder
		this.customizationCenterPanel = new CustomizationCenterPanel();

		add(catalogoPanel, CARD_CATALOGO);
		add(pagoPanel, CARD_PAGO);
		add(customizationCenterPanel, CARD_CUSTOM);

		showCatalogo();
	}

	public CatalogoCenterPanel getCatalogoPanel() {
		return catalogoPanel;
	}

	public CustomizationCenterPanel getCustomizationCenterPanel() {
		return customizationCenterPanel;
	}

	public void showCatalogo() {
		showCard(CARD_CATALOGO);
	}

	public void showPago() {
		showCard(CARD_PAGO);
	}

	public void showCustom() {
		showCard(CARD_CUSTOM);
	}

	public void showCard(String name) {
		cardLayout.show(this, name);
	}
	
	 public void showCustomCard(CustomizationCard card) {
	        showCustom();
	        customizationCenterPanel.showCard(card);
	    }

}
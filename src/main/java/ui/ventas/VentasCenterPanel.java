package ui.ventas;

import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import enums.CustomizationCard;
import model.TicketItem;
import service.AppServices;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class VentasCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String CARD_CATALOGO = "CATALOGO";
    private static final String CARD_CUSTOM   = "CUSTOM";
    private static final String CARD_PAGO     = "PAGO";

    private final CardLayout cardLayout = new CardLayout();

    private final AppServices services;

    private final CatalogoCenterPanel catalogoPanel;
    private final CustomizationCenterPanel customizationCenterPanel;
    private final JPanel pagoPanel;

    public VentasCenterPanel(AppServices services, Consumer<ProductoDTO> onProductoClicked) {
        this.services = services;

        setLayout(cardLayout);
        setOpaque(false);

        // Card catálogo
        this.catalogoPanel = new CatalogoCenterPanel(services, onProductoClicked);

        // Card customización
        this.customizationCenterPanel = new CustomizationCenterPanel();

        // Card pago (placeholder por ahora)
        this.pagoPanel = buildPagoPanel();

        add(catalogoPanel, CARD_CATALOGO);
        add(customizationCenterPanel, CARD_CUSTOM);
        add(pagoPanel, CARD_PAGO);

        showCatalogo();
    }

    private JPanel buildPagoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 20, 20));

        JLabel lbl = new JLabel("PAGO", SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 28));
        lbl.setForeground(Color.WHITE);

        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // NAVEGACIÓN GENERAL
    // =========================================================

    public void showCatalogo() {
        cardLayout.show(this, CARD_CATALOGO);
    }

    public void showCustom() {
        cardLayout.show(this, CARD_CUSTOM);
    }

    public void showPago() {
        cardLayout.show(this, CARD_PAGO);
    }

    public void showCustomCard(CustomizationCard card) {
        showCustom();
        customizationCenterPanel.showCard(card);
    }

    // =========================================================
    // CUSTOMIZATION DATA
    // =========================================================

    public void loadCustomizationData(ProductoCustomizationDTO dto) {
        customizationCenterPanel.loadCustomizationData(dto);
    }

    public void clearCustomizationData() {
        customizationCenterPanel.clearCustomizationData();
    }

    public void setCustomizationActionListener(
            CustomizationCenterPanel.CustomizationActionListener listener
    ) {
        customizationCenterPanel.setActionListener(listener);
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public CatalogoCenterPanel getCatalogoPanel() {
        return catalogoPanel;
    }

    public CustomizationCenterPanel getCustomizationCenterPanel() {
        return customizationCenterPanel;
    }

    public AppServices getServices() {
        return services;
    }
    public void ensureValidCustomCardForMode(enums.CustomizationMode mode) {
        customizationCenterPanel.ensureValidCurrentCardForMode(mode);
    }
  
}
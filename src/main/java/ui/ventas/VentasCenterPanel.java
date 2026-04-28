package ui.ventas;

import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import enums.CustomizationCard;
import enums.CustomizationMode;
import enums.TipoServicio;
import model.TicketSession;
import service.AppServices;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class VentasCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // CARDS PRINCIPALES
    // =========================================================
    private static final String CARD_CATALOGO   = "CATALOGO";
    private static final String CARD_CUSTOM     = "CUSTOM";
    private static final String CARD_PAGO       = "PAGO";
    private static final String CARD_OPCIONES   = "OPCIONES";
    private static final String CARD_DESCUENTOS = "DESCUENTOS";

    // =========================================================
    // SUBCARDS INTERNAS DEL FLUJO DE PAGO
    // =========================================================
    private static final String SUBCARD_NOMBRE_PEDIDO = "NOMBRE_PEDIDO";
    private static final String SUBCARD_PAGO_IMPORTE  = "PAGO_IMPORTE";

    // =========================================================
    // LAYOUTS
    // =========================================================
    private final CardLayout cardLayout = new CardLayout();
    private final CardLayout pagoCardLayout = new CardLayout();

    // =========================================================
    // DEPENDENCIAS
    // =========================================================
    private final AppServices services;

    // =========================================================
    // PANELS PRINCIPALES
    // =========================================================
    private final CatalogoCenterPanel catalogoPanel;
    private final CustomizationCenterPanel customizationCenterPanel;
    private final OpcionesPanel opcionesPanel;
    private final DescuentoPanel descuentoPanel;

    /**
     * Contenedor principal de la card PAGO.
     */
    private final JPanel pagoFlowPanel;

    // =========================================================
    // PANELS DEL FLUJO DE PAGO
    // =========================================================
    private final NombrePedidoPanel nombrePedidoPanel;
    private final PagoPanel pagoPanel;

    public VentasCenterPanel(
            AppServices services,
            TicketSession ticketSession,
            Consumer<ProductoDTO> onProductoClicked,
            NombrePedidoPanel.NombrePedidoListener nombrePedidoListener,
            PagoPanel.PagoPanelListener pagoPanelListener
    ) {
        this.services = services;

        setLayout(cardLayout);
        setOpaque(false);

        this.catalogoPanel = new CatalogoCenterPanel(services, onProductoClicked);
        this.customizationCenterPanel = new CustomizationCenterPanel();
        this.opcionesPanel = new OpcionesPanel();
        this.descuentoPanel = new DescuentoPanel(ticketSession);

        this.nombrePedidoPanel = new NombrePedidoPanel(nombrePedidoListener);
        this.pagoPanel = new PagoPanel(pagoPanelListener);

        this.pagoFlowPanel = buildPagoFlowPanel();

        add(catalogoPanel, CARD_CATALOGO);
        add(customizationCenterPanel, CARD_CUSTOM);
        add(pagoFlowPanel, CARD_PAGO);
        add(opcionesPanel, CARD_OPCIONES);
        add(descuentoPanel, CARD_DESCUENTOS);

        showCatalogo();
    }

    public VentasCenterPanel(
            AppServices services,
            TicketSession ticketSession,
            Consumer<ProductoDTO> onProductoClicked
    ) {
        this(
                services,
                ticketSession,
                onProductoClicked,
                new NombrePedidoPanel.NombrePedidoListener() {
                    @Override
                    public void onContinuar(String nombrePedido, TipoServicio tipoServicio) {
                        // no-op
                    }

                    @Override
                    public void onVolver() {
                        // no-op
                    }
                },
                new PagoPanel.PagoPanelListener() {
                    @Override
                    public void onVolver() {
                        // no-op
                    }

                    @Override
                    public void onCobroEfectivo(java.math.BigDecimal importeRecibido) {
                        // no-op
                    }

                    @Override
                    public void onCobroEfectivoExacto() {
                        // no-op
                    }

					@Override
					public void onCobroTarjeta() {
						// TODO Auto-generated method stub
						
					}
                }
        );
    }

    private JPanel buildPagoFlowPanel() {
        JPanel panel = new JPanel(pagoCardLayout);
        panel.setOpaque(false);

        panel.add(nombrePedidoPanel, SUBCARD_NOMBRE_PEDIDO);
        panel.add(pagoPanel, SUBCARD_PAGO_IMPORTE);

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
        showPagoNombre();
    }

    public void showOpciones() {
        cardLayout.show(this, CARD_OPCIONES);
    }

    public void showDescuentos() {
        descuentoPanel.refresh();
        cardLayout.show(this, CARD_DESCUENTOS);
    }

    public void showCustomCard(CustomizationCard card) {
        showCustom();
        customizationCenterPanel.showCard(card);
    }

    // =========================================================
    // NAVEGACIÓN DEL FLUJO DE PAGO
    // =========================================================

    public void showPagoNombre() {
        cardLayout.show(this, CARD_PAGO);
        pagoCardLayout.show(pagoFlowPanel, SUBCARD_NOMBRE_PEDIDO);
    }

    public void showPagoImporte() {
        cardLayout.show(this, CARD_PAGO);
        pagoCardLayout.show(pagoFlowPanel, SUBCARD_PAGO_IMPORTE);
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

    public void ensureValidCustomCardForMode(CustomizationMode mode) {
        customizationCenterPanel.ensureValidCurrentCardForMode(mode);
    }

    // =========================================================
    // OPCIONES / DESCUENTOS DATA
    // =========================================================

    public void setOpcionesActionListener(OpcionesPanel.OpcionesActionListener listener) {
        opcionesPanel.setActionListener(listener);
    }

    public void setOpcionesAdminMode(boolean adminMode) {
        opcionesPanel.setAdminMode(adminMode);
    }

    public void setDescuentoActionListener(DescuentoPanel.DescuentoActionListener listener) {
        descuentoPanel.setActionListener(listener);
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

    public OpcionesPanel getOpcionesPanel() {
        return opcionesPanel;
    }

    public DescuentoPanel getDescuentoPanel() {
        return descuentoPanel;
    }

    public NombrePedidoPanel getNombrePedidoPanel() {
        return nombrePedidoPanel;
    }

    public PagoPanel getPagoPanel() {
        return pagoPanel;
    }

    public AppServices getServices() {
        return services;
    }
}
package ui.ventas;

import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import enums.CustomizationCard;
import enums.CustomizationMode;
import enums.TipoServicio;
import service.AppServices;

import javax.swing.*;

import java.awt.*;
import java.util.function.Consumer;

public class VentasCenterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // CARDS PRINCIPALES
    // =========================================================
    private static final String CARD_CATALOGO = "CATALOGO";
    private static final String CARD_CUSTOM   = "CUSTOM";
    private static final String CARD_PAGO     = "PAGO";
    private static final String CARD_OPCIONES = "OPCIONES";

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

    /**
     * Contenedor principal de la card PAGO.
     * Dentro de este panel hay un CardLayout secundario:
     * - SUBCARD_NOMBRE_PEDIDO
     * - SUBCARD_PAGO_IMPORTE
     */
    private final JPanel pagoFlowPanel;

    // =========================================================
    // PANELS DEL FLUJO DE PAGO
    // =========================================================
    private final NombrePedidoPanel nombrePedidoPanel;
    private final PagoPanel pagoPanel;

    /**
     * Constructor NUEVO recomendado.
     *
     * Este es el que debería usar VentasFrame a partir de ahora,
     * porque permite conectar correctamente el flujo:
     * Cobrar -> Nombre pedido -> Pago
     */
    public VentasCenterPanel(
            AppServices services,
            Consumer<ProductoDTO> onProductoClicked,
            NombrePedidoPanel.NombrePedidoListener nombrePedidoListener,
            PagoPanel.PagoPanelListener pagoPanelListener
    ) {
        this.services = services;

        setLayout(cardLayout);
        setOpaque(false);

        // -----------------------------------------------------
        // Card catálogo
        // -----------------------------------------------------
        this.catalogoPanel = new CatalogoCenterPanel(services, onProductoClicked);

        // -----------------------------------------------------
        // Card customización
        // -----------------------------------------------------
        this.customizationCenterPanel = new CustomizationCenterPanel();

        // -----------------------------------------------------
        // Card opciones
        // -----------------------------------------------------
        this.opcionesPanel = new OpcionesPanel();

        // -----------------------------------------------------
        // Panels reales del flujo de pago
        // -----------------------------------------------------
        this.nombrePedidoPanel = new NombrePedidoPanel(nombrePedidoListener);
        this.pagoPanel = new PagoPanel(pagoPanelListener);

        // -----------------------------------------------------
        // Contenedor de subcards del flujo de pago
        // -----------------------------------------------------
        this.pagoFlowPanel = buildPagoFlowPanel();

        // -----------------------------------------------------
        // Registro de cards principales
        // -----------------------------------------------------
        add(catalogoPanel, CARD_CATALOGO);
        add(customizationCenterPanel, CARD_CUSTOM);
        add(pagoFlowPanel, CARD_PAGO);
        add(opcionesPanel, CARD_OPCIONES);

        showCatalogo();
    }

    /**
     * Constructor compatible con el código anterior.
     *
     * Lo dejo para no romper de golpe todas las llamadas existentes,
     * pero lo ideal es migrar cuanto antes al constructor completo.
     */
    public VentasCenterPanel(AppServices services, Consumer<ProductoDTO> onProductoClicked) {
        this(
                services,
                onProductoClicked,
                new NombrePedidoPanel.NombrePedidoListener() {
                    @Override
                    public void onContinuar(String nombrePedido,TipoServicio tipoServicio) {
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
                }
        );
    }

    /**
     * Construye la card principal de pago con subnavegación interna.
     */
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

    /**
     * Mantengo este método por compatibilidad.
     * Por defecto, entrar en pago abrirá la subcard de nombre del pedido.
     */
    public void showPago() {
        showPagoNombre();
    }

    public void showOpciones() {
        cardLayout.show(this, CARD_OPCIONES);
    }

    public void showCustomCard(CustomizationCard card) {
        showCustom();
        customizationCenterPanel.showCard(card);
    }

    // =========================================================
    // NAVEGACIÓN DEL FLUJO DE PAGO
    // =========================================================

    /**
     * Muestra la card principal de pago y dentro de ella
     * la subpantalla de nombre del pedido.
     */
    public void showPagoNombre() {
        cardLayout.show(this, CARD_PAGO);
        pagoCardLayout.show(pagoFlowPanel, SUBCARD_NOMBRE_PEDIDO);
    }

    /**
     * Muestra la card principal de pago y dentro de ella
     * la subpantalla de introducción de importe / métodos de pago.
     */
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
    // OPCIONES DATA
    // =========================================================

    public void setOpcionesActionListener(OpcionesPanel.OpcionesActionListener listener) {
        opcionesPanel.setActionListener(listener);
    }

    public void setOpcionesAdminMode(boolean adminMode) {
        opcionesPanel.setAdminMode(adminMode);
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
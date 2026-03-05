package ui.screens;



import app.AppContext;
import dtoS.CategoriaDTO;
import model.TicketSession;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.ventas.BottomBarPanel;
import ui.ventas.CategoriasBarPanel;
import ui.ventas.CustomizationPanel;
import ui.ventas.TicketPanel;
import ui.ventas.VentasCenterPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class VentasFrame extends BaseTpvFrame {

    private static final long serialVersionUID = 1L;

    private final AppServices services;

    // Estado venta en memoria (Fase 1)
    private final TicketSession ticketSession = new TicketSession();

    // UI
    private VentasCenterPanel centerPanel;
    private TicketPanel ticketPanel;
    private CustomizationPanel customizationPanel;
    private BottomBarPanel bottomBarPanel;
    private CategoriasBarPanel categoriasBarPanel;

    public VentasFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
        super(buildTitleWithCaja(), onLogoutNavigate); // mantiene reloj + header
        this.services = services;

        requireAuthenticatedOrExit();

        // Guard: debe existir sesión de caja en AppContext (router la setea antes de abrir)
        AppContext.getSesionCajaActual();

        buildUI(onBack);
        loadCategorias();
        refreshAll();
    }

    private static String buildTitleWithCaja() {
        String caja = "(sin caja)";
        try {
            caja = AppContext.getSesionCajaActual().getNombreCaja();
        } catch (Exception ignored) { }
        return "Ventas - Caja: " + caja;
    }

    private void buildUI(Runnable onBack) {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        // -------------------------
        // NORTH: Categorías (placeholder)
        // -------------------------
        categoriasBarPanel = new CategoriasBarPanel();
        root.add(categoriasBarPanel, BorderLayout.NORTH);

        // -------------------------
        // CENTER: CardLayout (CATALOGO / PAGO)
        // -------------------------
        centerPanel = new VentasCenterPanel(services);
        root.add(centerPanel, BorderLayout.CENTER);

        // -------------------------
        // EAST: Ticket + Customization (vertical)
        // -------------------------
        JPanel east = new JPanel(new BorderLayout(12, 12));
        east.setOpaque(false);
        east.setPreferredSize(new Dimension(420, 0)); // columna derecha tipo TPV

        ticketPanel = new TicketPanel(ticketSession, this::onTicketSelectionChanged);
        customizationPanel = new CustomizationPanel(ticketSession, services);

        east.add(ticketPanel, BorderLayout.CENTER);
        east.add(customizationPanel, BorderLayout.SOUTH);

        root.add(east, BorderLayout.EAST);

        // -------------------------
        // SOUTH: Bottom bar
        // -------------------------
        bottomBarPanel = new BottomBarPanel(
                ticketSession,
                this::onCobrar,
                this::onCancelar,
                this::onOpciones,
                this::onDescuentos
        );
        root.add(bottomBarPanel, BorderLayout.SOUTH);

        // Insertar en el contenedor principal de BaseTpvFrame
        main.add(root, BorderLayout.CENTER);
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    private void onTicketSelectionChanged() {
        customizationPanel.refresh();
    }

    private void onCobrar() {
        // En este paso solo cambia a la card PAGO (placeholder)
        if (!ticketSession.isEmpty()) {
            centerPanel.showPago();
        }
    }

    private void onCancelar() {
        int res = JOptionPane.showConfirmDialog(
                this,
                "¿Cancelar el pedido actual?",
                "Cancelar venta",
                JOptionPane.YES_NO_OPTION
        );
        if (res == JOptionPane.YES_OPTION) {
            ticketSession.clear();
            centerPanel.showCatalogo();
            refreshAll();
        }
    }

    private void onOpciones() {
        JOptionPane.showMessageDialog(this, "Opciones (pendiente)");
    }

    private void onDescuentos() {
        JOptionPane.showMessageDialog(this, "Descuentos (pendiente)");
    }
  
    private void loadCategorias() {
        List<CategoriaDTO> categorias = services.catalogoService.getCategoriasTpv();
        categoriasBarPanel.setCategorias(categorias, this::onCategoriaClicked);

        if (!categorias.isEmpty()) {
            onCategoriaClicked(categorias.get(0));
        }
    }

    private void onCategoriaClicked(CategoriaDTO categoria) {
    	centerPanel.showCatalogo();
    	centerPanel.getCatalogoPanel().showCategoria(categoria.getIdCategoria());
        
    }

    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshAll() {
        ticketPanel.refreshFromTicket();
        customizationPanel.refresh();
        bottomBarPanel.refresh();
    }

}
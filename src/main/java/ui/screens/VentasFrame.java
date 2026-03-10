package ui.screens;



import app.AppContext;
import dtoS.CategoriaDTO;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;
import model.TicketItem;
import model.TicketRow;
import model.TicketSession;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.ventas.BottomBarPanel;
import ui.ventas.CategoriasBarPanel;
import ui.ventas.CustomizationCenterPanel;
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
        // CENTER: CardLayout (CATALOGO / PAGO/ CUSTOMIZACION)
        // -------------------------
        centerPanel = new VentasCenterPanel(services, this::onProductoClicked);
        root.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setCustomizationActionListener(new CustomizationCenterPanel.CustomizationActionListener() {
            @Override
            public void onExtraClicked(dtoS.ExtraDTO extra) {
                onCenterExtraClicked(extra);
            }

            @Override
            public void onPersonalizacionClicked(dtoS.PersonalizacionDTO personalizacion) {
                onCenterPersonalizacionClicked(personalizacion);
            }
        });
        
     // EAST: Customization (izquierda) + Ticket (derecha)
        JPanel east = new JPanel(new BorderLayout(12, 12));
        east.setOpaque(false);
        east.setPreferredSize(new Dimension(520, 0)); // columna derecha total

        ticketPanel = new TicketPanel(ticketSession, this::onTicketSelectionChanged);
        customizationPanel = new CustomizationPanel(
                ticketSession,
                services,
                card -> centerPanel.showCustomCard(card),
                this::onTamanoSelected
                
        );

        // Panel izquierdo (customización) fijo
        JPanel customWrap = new JPanel(new BorderLayout());
        customWrap.setOpaque(false);
        customWrap.setPreferredSize(new Dimension(170, 0)); // ancho del panel de customización
        customWrap.add(customizationPanel, BorderLayout.CENTER);

        // Ticket ocupa el resto
        east.add(customWrap, BorderLayout.WEST);
        east.add(ticketPanel, BorderLayout.CENTER);

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
   
    private void onProductoClicked(ProductoDTO producto) {

        // 1) Resolver tamaño default + precio (regla B)
    	dtoS.TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());
       

        // 2) Añadir al ticket
        ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

        // 3) Seleccionar el item recién añadido (fila ITEM de ese item)
        int newItemIndex = ticketSession.getItems().size() - 1;
        ticketSession.setSelectedFlatIndex(findFlatIndexForItem(newItemIndex));

        // 4) Refrescar UI
        refreshAll();
        loadCustomizationForSelectedItem();
    }
   
    private int findFlatIndexForItem(int itemIndex) {
        List<model.TicketRow> rows = ticketSession.buildRows();
        for (int i = 0; i < rows.size(); i++) {
            model.TicketRow r = rows.get(i);
            if (r.getType() == enums.TicketRowType.ITEM && r.getItemIndex() == itemIndex) {
                return i;
            }
        }
        return -1;
    }
    private void loadCustomizationForSelectedItem() {
        model.TicketItem item = ticketSession.getSelectedItemOrNull();

        if (item == null) {
            centerPanel.clearCustomizationData();
            return;
        }

        int idProducto = item.getProducto().getIdProducto();

        ProductoCustomizationDTO dto =
                services.productoPersonalizacionService.getCustomizationByProducto(idProducto);

        centerPanel.loadCustomizationData(dto);
    }
    
    private void onTicketSelectionChanged() {
        customizationPanel.refresh();
        loadCustomizationForSelectedItem();
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
            centerPanel.clearCustomizationData();
            refreshAll();
        }
    }
    private void onCenterExtraClicked(ExtraDTO extra) {
        if (extra == null) return;

        TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
        if (selectedRow == null) return;

        int itemIndex = selectedRow.getItemIndex();
        if (itemIndex < 0) return;

        String tipo = extra.getTipo() == null ? "" : extra.getTipo().trim().toUpperCase();

        switch (tipo) {
            case "MILK" -> ticketSession.replaceExtraByTipo(itemIndex, extra);

            case "SHOT",
                 "SYRUP",
                 "TOPPING",
                 "FOOD_EXTRA" -> ticketSession.addExtra(itemIndex, extra);

            default -> ticketSession.addExtra(itemIndex, extra);
        }

        refreshAll();
    }

    private void onCenterPersonalizacionClicked(PersonalizacionDTO personalizacion) {
        if (personalizacion == null) return;

        TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
        if (selectedRow == null) return;

        int itemIndex = selectedRow.getItemIndex();
        if (itemIndex < 0) return;

        ticketSession.togglePersonalizacion(itemIndex, personalizacion);

        refreshAll();
    }
    
    private void onTamanoSelected(TamanoDTO tamanoSeleccionado) {
        if (tamanoSeleccionado == null) return;

        TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
        if (selectedRow == null) return;

        int itemIndex = selectedRow.getItemIndex();
        if (itemIndex < 0) return;

        TicketItem item = ticketSession.getItems().get(itemIndex);
        int idProducto = item.getProducto().getIdProducto();
        int idTamano = tamanoSeleccionado.getIdTamano();

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
        TamanoPrecioDTO tamanoPrecio =
                services.productoPersonalizacionService.getPrecioByProductoYTamano(idProducto, idTamano);

        ticketSession.changeSize(itemIndex, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());

        // Mantener la selección en la fila ITEM del item padre
        ticketSession.setSelectedFlatIndex(findFlatIndexForItem(itemIndex));

        refreshAll();
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
package ui.screens;

import app.AppContext;
import dtoS.CategoriaDTO;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;
import model.TicketItem;
import model.TicketRow;
import model.TicketSession;
import model.Usuario;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.dialog.AskMeDialog;
import ui.dialog.AskMeDialogResult;
import ui.dialog.BuscarProductoDialog;
import ui.ventas.BottomBarPanel;
import ui.ventas.CategoriasBarPanel;
import ui.ventas.CustomizationCenterPanel;
import ui.ventas.CustomizationPanel;
import ui.ventas.OpcionesPanel.OpcionesActionListener;
import ui.dialog.SkuDialog;
import ui.ventas.TicketPanel;
import ui.ventas.VentasCenterPanel;
import ui.ventas.OpcionesPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Optional;

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

		// Guard: debe existir sesión de caja en AppContext (router la setea antes de
		// abrir)
		AppContext.getSesionCajaActual();

		buildUI(onBack);
		loadCategorias();
		configureOpcionesByRole();
		refreshAll();
	}

	private static String buildTitleWithCaja() {
		String caja = "(sin caja)";
		try {
			caja = AppContext.getSesionCajaActual().getNombreCaja();
		} catch (Exception ignored) {
		}
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

			@Override
			public void onAskMeClicked() {
				VentasFrame.this.onAskMeClicked();
			}

		});
		
		centerPanel.setOpcionesActionListener(new OpcionesPanel.OpcionesActionListener() {
		    @Override
		    public void onDuplicarClicked() {
		        onDuplicar();
		    }

		    @Override
		    public void onSkuClicked() {
		        onSku();
		    }

		    @Override
		    public void onBuscarProductoClicked() {
		        onBuscarProducto();
		    }

		    @Override
		    public void onDisponibilidadClicked() {
		        onDisponibilidad();
		    }

		    @Override
		    public void onStockClicked() {
		        onStock();
		    }

		    @Override
		    public void onDescuentosClicked() {
		        onDescuentos();
		    }

		    @Override
		    public void onReimprimirClicked() {
		        onReimprimir();
		    }

		    @Override
		    public void onUltimosTicketsClicked() {
		        onUltimosTickets();
		    }

		    @Override
		    public void onDevolucionesClicked() {
		        onDevoluciones();
		    }

		    @Override
		    public void onVolverAdminClicked() {
		        onVolverAdmin();
		    }

		    @Override
		    public void onVolverClicked() {
		        onVolverDesdeOpciones();
		    }

		});

		// EAST: Customization (izquierda) + Ticket (derecha)
		JPanel east = new JPanel(new BorderLayout(12, 12));
		east.setOpaque(false);
		east.setPreferredSize(new Dimension(520, 0)); // columna derecha total

		ticketPanel = new TicketPanel(ticketSession, this::onTicketSelectionChanged);
		customizationPanel = new CustomizationPanel(ticketSession, services, card -> centerPanel.showCustomCard(card),
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
				this::onDescuentos,
				this::onEliminar);
		root.add(bottomBarPanel, BorderLayout.SOUTH);

		// Insertar en el contenedor principal de BaseTpvFrame
		main.add(root, BorderLayout.CENTER);
	}

	// =====================================================
	// EVENTOS
	// =====================================================
	private void onDuplicar() {
	    TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
	    if (selectedRow == null) {
	        return;
	    }

	    int itemIndex = selectedRow.getItemIndex();
	    if (itemIndex < 0) {
	        return;
	    }

	    int duplicatedItemIndex = ticketSession.duplicateItem(itemIndex);

	    ticketSession.selectItemRow(duplicatedItemIndex);

	    refreshAll();
	    loadCustomizationForSelectedItem();
	    centerPanel.showCatalogo();
	}

	private void onSku() {
	    SkuDialog dialog = new SkuDialog(this);
	    String sku = dialog.showDialog();

	    if (sku == null || sku.isBlank()) {
	        return;
	    }

	    Optional<ProductoDTO> productoOpt = services.catalogoService.buscarProductoPorSku(sku);
	    if (productoOpt.isEmpty()) {
	        JOptionPane.showMessageDialog(
	                this,
	                "No existe ningún producto con ese SKU.",
	                "SKU no encontrado",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }

	    ProductoDTO producto = productoOpt.get();

	    TamanoPrecioDTO def =
	            services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

	    ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

	    int newItemIndex = ticketSession.getItems().size() - 1;
	    ticketSession.selectItemRow(newItemIndex);

	    refreshAll();
	    loadCustomizationForSelectedItem();
	    centerPanel.showCatalogo();
	}

	private void onDisponibilidad() {
	    // TODO implementar consulta de disponibilidad / stock
	}

	private void onReimprimir() {
	    // TODO implementar reimpresión del último ticket
	}

	private void onVolverDesdeOpciones() {
	    // TODO decidir a qué card volver
	    centerPanel.showCatalogo();
	}

	private void onProductoClicked(ProductoDTO producto) {

		TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

		ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;
		ticketSession.selectItemRow(newItemIndex);

		refreshAll();
		loadCustomizationForSelectedItem();
	}

	private void loadCustomizationForSelectedItem() {
		model.TicketItem item = ticketSession.getSelectedItemOrNull();

		if (item == null) {
			centerPanel.clearCustomizationData();
			return;
		}

		int idProducto = item.getProducto().getIdProducto();

		ProductoCustomizationDTO dto = services.productoPersonalizacionService.getCustomizationByProducto(idProducto);

		centerPanel.loadCustomizationData(dto);

		enums.CustomizationMode mode = resolveMode(item);
		centerPanel.ensureValidCustomCardForMode(mode);
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
	private void onEliminar() {
	    TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
	    if (selectedRow == null) {
	        return;
	    }

	    int flatIndex = ticketSession.getSelectedFlatIndex();
	    int itemIndex = selectedRow.getItemIndex();
	    enums.TicketRowType rowType = selectedRow.getType();

	    // 1) Borrar la fila seleccionada
	    ticketSession.removeByFlatIndex(flatIndex);

	    // 2) Restaurar una selección coherente
	    if (!ticketSession.isEmpty()) {
	        switch (rowType) {
	            case EXTRA, PERSONALIZACION, ASK_ME -> {
	                // El item padre sigue existiendo: volvemos a seleccionar su fila ITEM
	                if (itemIndex >= 0 && itemIndex < ticketSession.getItems().size()) {
	                    ticketSession.selectItemRow(itemIndex);
	                }
	            }

	            case ITEM -> {
	                // Si has borrado un item completo, intenta seleccionar:
	                // - el que ha quedado en esa misma posición
	                // - o el anterior si has borrado el último
	                int targetItemIndex = Math.min(itemIndex, ticketSession.getItems().size() - 1);
	                if (targetItemIndex >= 0) {
	                    ticketSession.selectItemRow(targetItemIndex);
	                }
	            }
	        }
	    }

	    // 3) Refrescar UI
	    refreshAll();
	    loadCustomizationForSelectedItem();
	}

	private void onCancelar() {
		int res = JOptionPane.showConfirmDialog(this, "¿Cancelar el pedido actual?", "Cancelar venta",
				JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION) {
			ticketSession.clear();
			centerPanel.showCatalogo();
			centerPanel.clearCustomizationData();
			refreshAll();
		}
	}

	private void onCenterExtraClicked(ExtraDTO extra) {
		if (extra == null)
			return;

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null)
			return;

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0)
			return;

		String tipo = extra.getTipo() == null ? "" : extra.getTipo().trim().toUpperCase();

		switch (tipo) {
		case "MILK" -> ticketSession.replaceExtraByTipo(itemIndex, extra);

		case "SHOT", "SYRUP", "TOPPING", "FOOD_EXTRA" -> ticketSession.addExtra(itemIndex, extra);

		default -> ticketSession.addExtra(itemIndex, extra);
		}

		ticketSession.selectItemRow(itemIndex);

		refreshAll();
	}

	private void onCenterPersonalizacionClicked(PersonalizacionDTO personalizacion) {
		if (personalizacion == null)
			return;

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null)
			return;

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0)
			return;

		ticketSession.togglePersonalizacion(itemIndex, personalizacion);

		ticketSession.selectItemRow(itemIndex);

		refreshAll();
	}

	private void onTamanoSelected(TamanoDTO tamanoSeleccionado) {
		if (tamanoSeleccionado == null)
			return;

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null)
			return;

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0)
			return;

		TicketItem item = ticketSession.getItems().get(itemIndex);
		int idProducto = item.getProducto().getIdProducto();
		int idTamano = tamanoSeleccionado.getIdTamano();

		TamanoPrecioDTO tamanoPrecio = services.productoPersonalizacionService.getPrecioByProductoYTamano(idProducto,
				idTamano);

		ticketSession.changeSize(itemIndex, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());

		ticketSession.selectItemRow(itemIndex);

		refreshAll();
	}

	private void onAskMeClicked() {
	    TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
	    TicketItem item = ticketSession.getSelectedItemOrNull();

	    if (selectedRow == null || item == null) {
	        return;
	    }

	    int itemIndex = selectedRow.getItemIndex();

	    AskMeDialog dialog = new AskMeDialog(this, item.getProducto().getNombre(), 25);
	    AskMeDialogResult result = dialog.showDialog();

	    if (result.isConfirmed()) {
	        ticketSession.addAskMe(itemIndex, result.getText());
	        ticketSession.selectItemRow(itemIndex);
	        refreshAll();
	    }
	}

	private void onOpciones() {
		centerPanel.showOpciones();
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
	private void onDevoluciones() {
	    JOptionPane.showMessageDialog(this, "Devoluciones (pendiente)");
	}

	private void onVolverAdmin() {
	    JOptionPane.showMessageDialog(this, "Volver a AdminDashboard (pendiente)");
	}
	

	private void onBuscarProducto() {
	    java.util.List<ProductoBusquedaRowDTO> rows = services.catalogoService.getFilasBusquedaProducto();

	    if (rows == null || rows.isEmpty()) {
	        JOptionPane.showMessageDialog(
	                this,
	                "No hay productos disponibles para mostrar.",
	                "Búsqueda de productos",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }

	    BuscarProductoDialog dialog = new BuscarProductoDialog(this, rows);
	    ProductoBusquedaRowDTO selectedRow = dialog.showDialog();

	    if (selectedRow == null) {
	        return;
	    }

	    ProductoDTO producto = new ProductoDTO(
	            selectedRow.getIdProducto(),
	            selectedRow.getIdSubcategoria(),
	            selectedRow.getNombreProducto(),
	            0,
	            selectedRow.isPermiteExtras(),
	            selectedRow.isPermitePersonalizacion()
	    );

	    TamanoPrecioDTO tamanoPrecio =
	            services.productoPersonalizacionService.getPrecioByProductoYTamano(
	                    selectedRow.getIdProducto(),
	                    selectedRow.getIdTamano()
	            );

	    ticketSession.addItem(producto, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());

	    int newItemIndex = ticketSession.getItems().size() - 1;
	    ticketSession.selectItemRow(newItemIndex);

	    refreshAll();
	    loadCustomizationForSelectedItem();
	    centerPanel.showCatalogo();
	}

	

	private void onStock() {
	    JOptionPane.showMessageDialog(this, "Stock (pendiente)");
	}

	


	private void onUltimosTickets() {
	    JOptionPane.showMessageDialog(this, "Últimos tickets (pendiente)");
	}

	

	

	
	// =====================================================
	// REFRESH
	// =====================================================

	private void refreshAll() {
		ticketPanel.refreshFromTicket();
		customizationPanel.refresh();
		bottomBarPanel.refresh();
	}
	private boolean isAdminActual() {
	    Usuario usuario = AppContext.getUsuario();
	    if (usuario == null || usuario.getRol() == null || usuario.getRol().getNombre() == null) {
	        return false;
	    }

	    String nombreRol = usuario.getRol().getNombre().trim().toUpperCase();
	    return "ADMIN".equals(nombreRol) || "ENCARGADO".equals(nombreRol);
	}
	
	private void configureOpcionesByRole() {
	    centerPanel.setOpcionesAdminMode(isAdminActual());
	}
	
	private enums.CustomizationMode resolveMode(TicketItem item) {
		if (item == null || item.getProducto() == null) {
			return enums.CustomizationMode.VACIO;
		}

		int idSubcategoria = item.getProducto().getIdSubcategoria();

		if (idSubcategoria == 1 || idSubcategoria == 2 || idSubcategoria == 3 || idSubcategoria == 4) {
			return enums.CustomizationMode.BEBIDA;
		}

		if (idSubcategoria == 5 || idSubcategoria == 6 || idSubcategoria == 7) {
			return enums.CustomizationMode.COMIDA;
		}

		return enums.CustomizationMode.VACIO;
	}

}
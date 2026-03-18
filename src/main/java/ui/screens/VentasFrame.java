package ui.screens;

import app.AppContext;
import dtoS.CategoriaDTO;
import dtoS.ColaItemDescripcionDTO;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import dtoS.RegistrarVentaExtraRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaItemResultDTO;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;
import dtoS.TicketClienteDTO;
import enums.MetodoPago;
import enums.TipoServicio;
import model.CobroSession;
import model.TicketExtra;
import model.TicketItem;
import model.TicketPersonalizacion;
import model.TicketRow;
import model.TicketSession;
import model.Usuario;
import service.AppServices;
import service.ColaImpresionService.ColaRegistroItemCommand;
import ui.common.BaseTpvFrame;
import ui.dialog.AskMeDialog;
import ui.dialog.AskMeDialogResult;
import ui.dialog.BuscarProductoDialog;
import ui.dialog.SkuDialog;
import ui.dialog.TicketClienteDialog;
import ui.dialog.TicketsHoyDialog;
import ui.ventas.BottomBarPanel;
import ui.ventas.CategoriasBarPanel;
import ui.ventas.CustomizationCenterPanel;
import ui.ventas.CustomizationPanel;
import ui.ventas.NombrePedidoPanel;
import ui.ventas.OpcionesPanel;
import ui.ventas.OpcionesPanel.OpcionesActionListener;
import ui.ventas.PagoPanel;
import ui.ventas.TicketPanel;
import ui.ventas.VentasCenterPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class VentasFrame extends BaseTpvFrame {

	private static final long serialVersionUID = 1L;

	// =====================================================
	// DEPENDENCIAS
	// =====================================================

	private final AppServices services;

	// =====================================================
	// ESTADO EN MEMORIA
	// =====================================================

	/**
	 * Ticket actual en memoria.
	 */
	private final TicketSession ticketSession = new TicketSession();

	/**
	 * Estado temporal del cobro.
	 */
	private final CobroSession cobroSession = new CobroSession();
	private final ObjectMapper objectMapper = new ObjectMapper();

	// =====================================================
	// COMPONENTES UI
	// =====================================================

	private VentasCenterPanel centerPanel;
	private TicketPanel ticketPanel;
	private CustomizationPanel customizationPanel;
	private BottomBarPanel bottomBarPanel;
	private CategoriasBarPanel categoriasBarPanel;

	// =====================================================
	// RUNNABLES
	// =====================================================
	private final Runnable onLogoutNavigateAction;
	private final Runnable onBackToAdminAction;

	// =====================================================
	// CONSTRUCTOR
	// =====================================================

	public VentasFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
		super(buildTitleWithCaja(), onLogoutNavigate, services);
		this.services = services;
		this.onLogoutNavigateAction = onLogoutNavigate;
		this.onBackToAdminAction = onBack;
		requireAuthenticatedOrExit();

		// Guard: debe existir sesión de caja en AppContext
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

	// =====================================================
	// CONSTRUCCIÓN DE UI
	// =====================================================

	private void buildUI(Runnable onBack) {

		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBackground(new Color(20, 20, 20));
		root.setBorder(new EmptyBorder(12, 12, 12, 12));

		// -------------------------------------------------
		// NORTH: categorías
		// -------------------------------------------------
		categoriasBarPanel = new CategoriasBarPanel();
		root.add(categoriasBarPanel, BorderLayout.NORTH);

		// -------------------------------------------------
		// CENTER: catálogo / custom / pago / opciones
		// -------------------------------------------------
		centerPanel = new VentasCenterPanel(services, this::onProductoClicked,
				new NombrePedidoPanel.NombrePedidoListener() {
					@Override
					public void onContinuar(String nombrePedido, TipoServicio servicio) {
						onNombrePedidoContinuar(nombrePedido, servicio);
					}

					@Override
					public void onVolver() {
						onVolverDesdeNombrePedido();
					}
				}, new PagoPanel.PagoPanelListener() {
					@Override
					public void onVolver() {
						onVolverDesdePago();
					}

					@Override
					public void onCobroEfectivo(BigDecimal importeRecibido) {
						onPagoEfectivo(importeRecibido);
					}

					@Override
					public void onCobroEfectivoExacto() {
						onPagoEfectivoExacto();
					}
				});
		root.add(centerPanel, BorderLayout.CENTER);

		centerPanel.setCustomizationActionListener(new CustomizationCenterPanel.CustomizationActionListener() {
			@Override
			public void onExtraClicked(ExtraDTO extra) {
				onCenterExtraClicked(extra);
			}

			@Override
			public void onPersonalizacionClicked(PersonalizacionDTO personalizacion) {
				onCenterPersonalizacionClicked(personalizacion);
			}

			@Override
			public void onAskMeClicked() {
				VentasFrame.this.onAskMeClicked();
			}
		});

		centerPanel.setOpcionesActionListener(new OpcionesActionListener() {
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

			@Override
			public void onNuevoPedidoClicked() {
				onNuevoPedido();

			}

			@Override
			public void onCerrarSesionClicked() {
				onCerrarSesion();

			}
		});

		// -------------------------------------------------
		// EAST: customización + ticket
		// -------------------------------------------------
		JPanel east = new JPanel(new BorderLayout(12, 12));
		east.setOpaque(false);
		east.setPreferredSize(new Dimension(520, 0));

		ticketPanel = new TicketPanel(ticketSession, this::onTicketSelectionChanged);

		customizationPanel = new CustomizationPanel(ticketSession, services, card -> centerPanel.showCustomCard(card),
				this::onTamanoSelected);

		JPanel customWrap = new JPanel(new BorderLayout());
		customWrap.setOpaque(false);
		customWrap.setPreferredSize(new Dimension(170, 0));
		customWrap.add(customizationPanel, BorderLayout.CENTER);

		east.add(customWrap, BorderLayout.WEST);
		east.add(ticketPanel, BorderLayout.CENTER);

		root.add(east, BorderLayout.EAST);

		// -------------------------------------------------
		// SOUTH: bottom bar
		// -------------------------------------------------
		bottomBarPanel = new BottomBarPanel(ticketSession, this::onCobrar, this::onCancelar, this::onOpciones,
				this::onDescuentos, this::onEliminar);
		root.add(bottomBarPanel, BorderLayout.SOUTH);

		main.add(root, BorderLayout.CENTER);
	}

	// =====================================================
	// EVENTOS DE CATÁLOGO / PRODUCTOS
	// =====================================================

	private void onProductoClicked(ProductoDTO producto) {
		TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

		ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;
		ticketSession.selectItemRow(newItemIndex);

		refreshAll();
		loadCustomizationForSelectedItem();
	}

	private void onCategoriaClicked(CategoriaDTO categoria) {
		centerPanel.showCatalogo();
		centerPanel.getCatalogoPanel().showCategoria(categoria.getIdCategoria());
	}

	private void loadCategorias() {
		List<CategoriaDTO> categorias = services.catalogoService.getCategoriasTpv();
		categoriasBarPanel.setCategorias(categorias, this::onCategoriaClicked);

		if (!categorias.isEmpty()) {
			onCategoriaClicked(categorias.get(0));
		}
	}

	// =====================================================
	// EVENTOS DE TICKET
	// =====================================================

	private void onTicketSelectionChanged() {
		customizationPanel.refresh();
		loadCustomizationForSelectedItem();
	}

	private void onEliminar() {
		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null) {
			return;
		}

		int flatIndex = ticketSession.getSelectedFlatIndex();
		int itemIndex = selectedRow.getItemIndex();
		enums.TicketRowType rowType = selectedRow.getType();

		ticketSession.removeByFlatIndex(flatIndex);

		if (!ticketSession.isEmpty()) {
			switch (rowType) {
			case EXTRA, PERSONALIZACION, ASK_ME -> {
				if (itemIndex >= 0 && itemIndex < ticketSession.getItems().size()) {
					ticketSession.selectItemRow(itemIndex);
				}
			}

			case ITEM -> {
				int targetItemIndex = Math.min(itemIndex, ticketSession.getItems().size() - 1);
				if (targetItemIndex >= 0) {
					ticketSession.selectItemRow(targetItemIndex);
				}
			}
			}
		}

		refreshAll();
		loadCustomizationForSelectedItem();
	}

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

	private void onCancelar() {
		int res = JOptionPane.showConfirmDialog(this, "¿Cancelar el pedido actual?", "Cancelar venta",
				JOptionPane.YES_NO_OPTION);

		if (res == JOptionPane.YES_OPTION) {
			ticketSession.clear();
			cobroSession.clear();

			centerPanel.getNombrePedidoPanel().clear();
			centerPanel.getPagoPanel().clear();
			centerPanel.showCatalogo();
			centerPanel.clearCustomizationData();

			refreshAll();
		}
	}

	// =====================================================
	// EVENTOS DE CUSTOMIZACIÓN
	// =====================================================

	private void loadCustomizationForSelectedItem() {
		TicketItem item = ticketSession.getSelectedItemOrNull();

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

	// =====================================================
	// FLUJO DE COBRO
	// =====================================================

	private void onCobrar() {
		if (ticketSession.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos en el ticket.", "Cobrar",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		prepararCobroSession();

		centerPanel.getNombrePedidoPanel().setNombrePedido(cobroSession.getNombrePedido());
		centerPanel.getNombrePedidoPanel().setTipoServicio(cobroSession.getTipoServicio());
		centerPanel.showPagoNombre();
		centerPanel.getNombrePedidoPanel().requestFocusInField();
	}

	private void onNombrePedidoContinuar(String nombrePedido, TipoServicio tipoServicio) {
		if (ticketSession.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos en el ticket.", "Cobrar",
					JOptionPane.WARNING_MESSAGE);
			centerPanel.showCatalogo();
			return;
		}

		String nombreNormalizado = normalizarNombrePedido(nombrePedido);

		cobroSession.setNombrePedido(nombreNormalizado);
		cobroSession.setTipoServicio(tipoServicio);
		cobroSession.setTotal(ticketSession.getTotal());

		centerPanel.getPagoPanel().setData(cobroSession.getNombrePedido(), cobroSession.getTipoServicio(),
				cobroSession.getTotal());

		centerPanel.showPagoImporte();
		centerPanel.getPagoPanel().requestFocusDefault();
	}

	private void onVolverDesdeNombrePedido() {
		centerPanel.showCatalogo();
	}

	private void onVolverDesdePago() {
		centerPanel.showPagoNombre();
		centerPanel.getNombrePedidoPanel().setNombrePedido(cobroSession.getNombrePedido());
		centerPanel.getNombrePedidoPanel().setTipoServicio(cobroSession.getTipoServicio());
		centerPanel.getNombrePedidoPanel().requestFocusInField();
	}

	private void onPagoEfectivo(BigDecimal importeRecibido) {
		BigDecimal total = ticketSession.getTotal();

		if (!validarImporteRecibido(importeRecibido, total)) {
			return;
		}

		cobroSession.setTotal(total);
		cobroSession.setMetodoPago(MetodoPago.EFECTIVO);
		cobroSession.setPagoExacto(false);
		cobroSession.setImporteRecibido(importeRecibido);

		BigDecimal cambio = cobroSession.calcularCambio();

		if (confirmarCobro(total, importeRecibido, cambio, "EFECTIVO")) {
			registrarCobroReal();
		}
	}

	private void onPagoEfectivoExacto() {
		BigDecimal total = ticketSession.getTotal();

		cobroSession.setTotal(total);
		cobroSession.setMetodoPago(MetodoPago.EFECTIVO);
		cobroSession.setPagoExacto(true);
		cobroSession.setImporteRecibido(total);

		if (confirmarCobro(total, total, BigDecimal.ZERO, "EFECTIVO EXACTO")) {
			registrarCobroReal();
		}
	}

	// =====================================================
	// BUILD REQUEST REGISTRAR VENTA
	// =====================================================

	/**
	 * Construye el request completo de la venta a partir del estado actual.
	 */
	private RegistrarVentaRequest buildRegistrarVentaRequest() {
		RegistrarVentaRequest request = new RegistrarVentaRequest();

		int idSesion = AppContext.getSesionCajaActual().getIdSesion();
		int idUsuario = AppContext.getUsuario().getIdUsuario();

		request.setIdSesion(idSesion);
		request.setIdUsuario(idUsuario);
		request.setTotal(ticketSession.getTotal());
		request.setNombrePedido(cobroSession.getNombrePedido());
		request.setTipoServicio(cobroSession.getTipoServicio());
		request.setMetodoPago(cobroSession.getMetodoPago());
		request.setMontoPagado(cobroSession.getImporteRecibido());

		List<RegistrarVentaItemRequest> items = new ArrayList<>();
		for (TicketItem item : ticketSession.getItems()) {
			items.add(buildItemRequest(item));
		}

		request.setItems(items);
		return request;
	}

	/**
	 * Convierte un TicketItem en un item listo para persistir.
	 */
	private RegistrarVentaItemRequest buildItemRequest(TicketItem item) {
		RegistrarVentaItemRequest dto = new RegistrarVentaItemRequest();

		dto.setIdProducto(item.getProducto().getIdProducto());
		dto.setNombreProducto(item.getProducto().getNombre());
		dto.setCantidad(1);
		dto.setPrecioUnitario(safe(item.getPrecioBase()));
		dto.setSubtotal(safe(item.getSubtotal()));

		BigDecimal subtotal = safe(item.getSubtotal());
		BigDecimal ivaPorcentaje = safe(item.getProducto().getIvaPorcentaje());

		dto.setIva(calcularIvaIncluido(subtotal, ivaPorcentaje));
		dto.setDescripcionPersonalizacion(buildDescripcionPersonalizacion(item));

		List<RegistrarVentaExtraRequest> extras = new ArrayList<>();
		for (TicketExtra extra : item.getExtras()) {
			extras.add(buildExtraRequest(extra));
		}
		dto.setExtras(extras);

		return dto;
	}

	/**
	 * Convierte un extra del ticket en DTO de persistencia.
	 */
	private RegistrarVentaExtraRequest buildExtraRequest(TicketExtra extra) {
		RegistrarVentaExtraRequest dto = new RegistrarVentaExtraRequest();
		dto.setIdExtra(extra.getIdExtra());
		dto.setNombreExtra(extra.getNombre());
		dto.setPrecioExtra(safe(extra.getPrecio()));
		return dto;
	}

	/**
	 * Genera una descripción simple de tamaño, personalizaciones y Ask Me. De
	 * momento la guardamos como texto.
	 */
	private String buildDescripcionPersonalizacion(TicketItem item) {
		try {
			Map<String, Object> root = new LinkedHashMap<>();

			// Tamaño
			root.put("tamano", item.getTamano() != null ? item.getTamano().getNombre() : null);

			// Personalizaciones
			List<String> personalizaciones = new ArrayList<>();
			if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
				for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
					if (p != null && p.getNombre() != null && !p.getNombre().isBlank()) {
						personalizaciones.add(p.getNombre().trim());
					}
				}
			}
			root.put("personalizaciones", personalizaciones);

			// Ask Me
			List<String> askMes = new ArrayList<>();
			if (item.getAskMes() != null && !item.getAskMes().isEmpty()) {
				for (String ask : item.getAskMes()) {
					if (ask != null && !ask.isBlank()) {
						askMes.add(ask.trim());
					}
				}
			}
			root.put("askMes", askMes);

			return objectMapper.writeValueAsString(root);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error construyendo JSON de descripcion_personalizacion.", e);
		}
	}

	/**
	 * Ejecuta el registro real de la venta.
	 *
	 * Si todo sale bien, limpia la UI. Si falla, muestra error y conserva el
	 * ticket.
	 */
	private void registrarCobroReal() {
		try {
			RegistrarVentaRequest request = buildRegistrarVentaRequest();

			RegistrarVentaResultDTO result = services.ventaFacade.registrarVenta(request);

			registrarColaImpresion(result);

			finalizarCobroTrasPersistencia(result);

		} catch (Exception e) {
			mostrarErrorRegistroVenta(e);
		}
	}

	private void registrarColaImpresion(RegistrarVentaResultDTO result) {
		if (result == null) {
			return;
		}

		List<RegistrarVentaItemResultDTO> itemsPersistidos = result.getItemsPersistidos();

		if (itemsPersistidos == null || itemsPersistidos.isEmpty()) {
			return;
		}

		if (itemsPersistidos.size() != ticketSession.getItems().size()) {
			throw new IllegalStateException("No coincide el número de items persistidos con los items del ticket.");
		}

		List<ColaRegistroItemCommand> commands = new ArrayList<>();

		for (int i = 0; i < ticketSession.getItems().size(); i++) {
			TicketItem ticketItem = ticketSession.getItems().get(i);
			RegistrarVentaItemResultDTO persisted = itemsPersistidos.get(i);

			ColaItemDescripcionDTO descripcion = buildColaItemDescripcion(ticketItem);

			commands.add(new ColaRegistroItemCommand(result.getIdVenta(), persisted.getIdItem(),
					persisted.getIdProducto(), descripcion));
		}

		services.colaImpresionService.registrarItemsEnCola(commands);
	}

	private ColaItemDescripcionDTO buildColaItemDescripcion(TicketItem item) {
		ColaItemDescripcionDTO dto = new ColaItemDescripcionDTO();

		dto.setNombrePedido(normalizarNombrePedido(cobroSession.getNombrePedido()));

		dto.setTipoServicio(cobroSession.getTipoServicio() != null ? cobroSession.getTipoServicio().name()
				: TipoServicio.PARA_TOMAR.name());

		dto.setProducto(item.getProducto() != null ? item.getProducto().getNombre() : "SIN_PRODUCTO");

		dto.setTamano(item.getTamano() != null ? item.getTamano().getNombre() : "");

		dto.setCantidad(1);

		if (item.getExtras() != null && !item.getExtras().isEmpty()) {
			for (TicketExtra extra : item.getExtras()) {
				if (extra != null && extra.getNombre() != null && !extra.getNombre().isBlank()) {
					dto.addExtra(extra.getNombre().trim());
				}
			}
		}

		if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
			for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
				if (p != null && p.getNombre() != null && !p.getNombre().isBlank()) {
					dto.addPersonalizacion(p.getNombre().trim());
				}
			}
		}

		if (item.getAskMes() != null && !item.getAskMes().isEmpty()) {
			for (String ask : item.getAskMes()) {
				if (ask != null && !ask.isBlank()) {
					dto.addAskMe(ask.trim());
				}
			}
		}

		return dto;
	}

	/**
	 * Limpia estado y UI solo cuando la persistencia fue correcta.
	 */
	private void finalizarCobroTrasPersistencia(RegistrarVentaResultDTO result) {
	    int idVenta = result.getIdVenta();

	    // 1) Intentamos leer el ticket recién guardado desde BD
	    TicketClienteDTO ticket = null;
	    try {
	        ticket = services.ticketClienteService.getTicketByVenta(idVenta);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 2) Limpiamos estado y UI de la venta actual
	    ticketSession.clear();
	    cobroSession.clear();

	    centerPanel.getNombrePedidoPanel().clear();
	    centerPanel.getPagoPanel().clear();
	    centerPanel.clearCustomizationData();
	    centerPanel.showCatalogo();

	    refreshAll();

	    // 3) Si pudimos leer el ticket, lo mostramos
	    if (ticket != null) {
	        TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
	        dialog.showDialog();
	        return;
	    }

	    // 4) Fallback si no se pudo construir/leer el ticket
	    JOptionPane.showMessageDialog(
	            this,
	            "Venta registrada correctamente.\n\nID venta: " + idVenta,
	            "Pago completado",
	            JOptionPane.INFORMATION_MESSAGE
	    );
	}
	/**
	 * Muestra error de persistencia sin perder el ticket.
	 */
	private void mostrarErrorRegistroVenta(Exception e) {
		e.printStackTrace();

		String mensaje = "No se pudo registrar la venta.";

		if (e.getMessage() != null) {
			mensaje += "\n\n" + e.getMessage();
		}

		Throwable cause = e.getCause();
		if (cause != null && cause.getMessage() != null) {
			mensaje += "\n\nCausa: " + cause.getMessage();
		}

		JOptionPane.showMessageDialog(this, mensaje, "Error al registrar venta", JOptionPane.ERROR_MESSAGE);
	}

	// =====================================================
	// COBRO - HELPERS
	// =====================================================
	private BigDecimal calcularIvaIncluido(BigDecimal totalConIva, BigDecimal ivaPorcentaje) {
		BigDecimal total = safe(totalConIva);
		BigDecimal porcentaje = safe(ivaPorcentaje);

		if (total.compareTo(BigDecimal.ZERO) <= 0 || porcentaje.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		return total.multiply(porcentaje).divide(BigDecimal.valueOf(100).add(porcentaje), 2,
				java.math.RoundingMode.HALF_UP);
	}

	private void prepararCobroSession() {
		cobroSession.clear();
		cobroSession.setTotal(ticketSession.getTotal());
		cobroSession.setNombrePedido("");
		cobroSession.setTipoServicio(TipoServicio.PARA_TOMAR);
	}

	private String normalizarNombrePedido(String nombrePedido) {
		String nombre = (nombrePedido != null) ? nombrePedido.trim() : "";
		return nombre.isBlank() ? "Cliente" : nombre;
	}

	private boolean validarImporteRecibido(BigDecimal importeRecibido, BigDecimal total) {
		if (importeRecibido == null || importeRecibido.compareTo(BigDecimal.ZERO) <= 0) {
			JOptionPane.showMessageDialog(this, "Introduce un importe válido.", "Pago", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (total == null) {
			total = BigDecimal.ZERO;
		}

		if (importeRecibido.compareTo(total) < 0) {
			JOptionPane.showMessageDialog(this, "El importe recibido no puede ser menor que el total.", "Pago",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	private boolean confirmarCobro(BigDecimal total, BigDecimal entregado, BigDecimal cambio, String metodo) {

		String mensaje = "Pedido: " + cobroSession.getNombrePedido() + "\n" + "Servicio: "
				+ formatTipoServicio(cobroSession.getTipoServicio()) + "\n" + "Método: " + metodo + "\n" + "Total: "
				+ formatMoney(total) + " €\n" + "Entregado: " + formatMoney(entregado) + " €\n" + "Cambio: "
				+ formatMoney(cambio) + " €\n\n" + "¿Confirmar cobro?";

		int result = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar cobro", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Método antiguo del flujo en memoria. Lo puedes conservar temporalmente o
	 * borrar más adelante.
	 */
	private void finalizarCobroEnMemoria() {
		ticketSession.clear();
		cobroSession.clear();

		centerPanel.getNombrePedidoPanel().clear();
		centerPanel.getPagoPanel().clear();
		centerPanel.clearCustomizationData();
		centerPanel.showCatalogo();

		refreshAll();

		JOptionPane.showMessageDialog(this,
				"Cobro registrado correctamente.\n\nDe momento este paso aún está solo en memoria.", "Pago completado",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private BigDecimal safe(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	private String formatMoney(BigDecimal amount) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
		return df.format(safe);
	}

	private String formatTipoServicio(TipoServicio tipoServicio) {
		if (tipoServicio == TipoServicio.PARA_LLEVAR) {
			return "Para llevar";
		}
		return "Para tomar";
	}

	// =====================================================
	// OPCIONES / ACCIONES SECUNDARIAS
	// =====================================================

	private void onOpciones() {
		centerPanel.showOpciones();
	}

	private void onVolverDesdeOpciones() {
		centerPanel.showCatalogo();
	}

	private void onDescuentos() {
		JOptionPane.showMessageDialog(this, "Descuentos (pendiente)");
	}

	private void onDisponibilidad() {
		// TODO implementar consulta de disponibilidad / stock
	}

	private void onStock() {
		JOptionPane.showMessageDialog(this, "Stock (pendiente)");
	}

	private void onReimprimir() {
	    try {
	        int idSesion = AppContext.getSesionCajaActual().getIdSesion();

	        TicketClienteDTO ticket = services.ticketClienteService.getUltimoTicketDeSesion(idSesion);

	        TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
	        dialog.showDialog();

	    } catch (Exception e) {
	        e.printStackTrace();

	        JOptionPane.showMessageDialog(
	                this,
	                "No se pudo abrir el último ticket.\n\n" + e.getMessage(),
	                "Reimprimir ticket",
	                JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

	private void onUltimosTickets() {
		TicketsHoyDialog dialog = new TicketsHoyDialog(this, services);
		dialog.showDialog();
	}

	private void onDevoluciones() {
		JOptionPane.showMessageDialog(this, "Devoluciones (pendiente)");
	}

	private void onVolverAdmin() {
		boolean confirmado = confirmarCancelacionSiHayTicket(
				"Hay un pedido en curso.\n\n¿Deseas cancelarlo antes de volver al panel de administración?");

		if (!confirmado) {
			return;
		}

		resetVentaActual();
		dispose();

		if (onBackToAdminAction != null) {
			onBackToAdminAction.run();
		}
	}

	private void onCerrarSesion() {
		boolean confirmado = confirmarCancelacionSiHayTicket(
				"Hay un pedido en curso.\n\n¿Deseas cancelarlo antes de cerrar sesión?");

		if (!confirmado) {
			return;
		}

		resetVentaActual();
		dispose();

		if (onLogoutNavigateAction != null) {
			onLogoutNavigateAction.run();
		}
	}

	private void onNuevoPedido() {
		boolean confirmado = confirmarCancelacionSiHayTicket(
				"Hay un pedido en curso.\n\n¿Deseas cancelarlo y crear un nuevo pedido?");

		if (!confirmado) {
			return;
		}

		resetVentaActual();
	}

	private void resetVentaActual() {
		ticketSession.clear();
		cobroSession.clear();

		centerPanel.getNombrePedidoPanel().clear();
		centerPanel.getPagoPanel().clear();
		centerPanel.clearCustomizationData();
		centerPanel.showCatalogo();

		refreshAll();
	}

	private boolean confirmarCancelacionSiHayTicket(String mensaje) {
		if (ticketSession.isEmpty()) {
			return true;
		}

		int res = JOptionPane.showConfirmDialog(this, mensaje, "Pedido en curso", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		return res == JOptionPane.YES_OPTION;
	}
	// =====================================================
	// SKU / BÚSQUEDA
	// =====================================================

	private void onSku() {
		SkuDialog dialog = new SkuDialog(this);
		String sku = dialog.showDialog();

		if (sku == null || sku.isBlank()) {
			return;
		}

		Optional<ProductoDTO> productoOpt = services.catalogoService.buscarProductoPorSku(sku);
		if (productoOpt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No existe ningún producto con ese SKU.", "SKU no encontrado",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductoDTO producto = productoOpt.get();
		TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

		ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;
		ticketSession.selectItemRow(newItemIndex);

		refreshAll();
		loadCustomizationForSelectedItem();
		centerPanel.showCatalogo();
	}

	private void onBuscarProducto() {
		List<ProductoBusquedaRowDTO> rows = services.catalogoService.getFilasBusquedaProducto();

		if (rows == null || rows.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos disponibles para mostrar.", "Búsqueda de productos",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		BuscarProductoDialog dialog = new BuscarProductoDialog(this, rows);
		ProductoBusquedaRowDTO selectedRow = dialog.showDialog();

		if (selectedRow == null) {
			return;
		}

		ProductoDTO producto = new ProductoDTO(selectedRow.getIdProducto(), selectedRow.getIdSubcategoria(),
				selectedRow.getNombreProducto(), 0, selectedRow.isPermiteExtras(),
				selectedRow.isPermitePersonalizacion(), selectedRow.getIva_porcentaje()

		);

		TamanoPrecioDTO tamanoPrecio = services.productoPersonalizacionService
				.getPrecioByProductoYTamano(selectedRow.getIdProducto(), selectedRow.getIdTamano());

		ticketSession.addItem(producto, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;
		ticketSession.selectItemRow(newItemIndex);

		refreshAll();
		loadCustomizationForSelectedItem();
		centerPanel.showCatalogo();
	}

	// =====================================================
	// REFRESH / ROLE
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
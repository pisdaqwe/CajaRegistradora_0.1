package ui.screens;

import app.AppContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtoS.AplicarDescuentoRequest;
import dtoS.CategoriaDTO;
import dtoS.ColaItemDescripcionDTO;
import dtoS.DescuentoAplicadoDTO;
import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoBusquedaRowDTO;
import dtoS.ProductoCatalogoDTO;
import dtoS.ProductoCustomizationDTO;
import dtoS.ProductoDTO;
import dtoS.RegistrarVentaComboItemRequest;
import dtoS.RegistrarVentaComboRequest;
import dtoS.RegistrarVentaDescuentoRequest;
import dtoS.RegistrarVentaExtraRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaItemResultDTO;
import dtoS.RegistrarVentaPersonalizacionRequest;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;
import dtoS.TamanoDTO;
import dtoS.TamanoPrecioDTO;
import dtoS.TicketClienteDTO;
import dtoS.MermaRequest;
import dtoS.MermaItemRequest;
import dtoS.MermaExtraRequest;
import dtoS.MermaPersonalizacionRequest;
import dtoS.MermaResultDTO;
import enums.MetodoPago;
import enums.TipoServicio;
import enums.ModoOperacion;
import model.CobroSession;
import model.DescuentoAplicado;
import model.TicketCombo;
import model.TicketExtra;
import model.TicketItem;
import model.TicketPersonalizacion;
import model.TicketRow;
import model.TicketSession;
import model.Usuario;
import service.AppServices;
import service.ColaImpresionService.ColaRegistroItemCommand;
import ui.common.BaseTpvFrame;
import ui.common.InformeUiTheme;
import ui.dialog.AskMeDialog;
import ui.dialog.AskMeDialogResult;
import ui.dialog.BuscarProductoDialog;
import ui.dialog.CodigoPromocionalDialog;
import ui.dialog.CodigoPromocionalDialogResult;
import ui.dialog.DisponibilidadItemsDialog;
import ui.dialog.MermaDialog;
import ui.dialog.MermaDialogResult;
import ui.dialog.DescuentoEmpleadoDialog;
import ui.dialog.DescuentoEmpleadoDialogResult;
import ui.dialog.DevolucionesDialog;
import ui.dialog.SkuDialog;
import ui.dialog.TicketClienteDialog;
import ui.dialog.TicketsHoyDialog;
import ui.ventas.BottomBarPanel;
import ui.ventas.CategoriasBarPanel;
import ui.ventas.CustomizationCenterPanel;
import ui.ventas.CustomizationPanel;
import ui.ventas.DescuentoPanel;
import ui.ventas.NombrePedidoPanel;
import ui.ventas.OpcionesPanel;
import ui.ventas.OpcionesPanel.OpcionesActionListener;
import ui.ventas.PagoPanel;
import ui.ventas.TicketPanel;
import ui.ventas.VentasCenterPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import dtoS.TipoCafeDTO;

/**
 * Frame principal del módulo de ventas.
 *
 * Responsabilidades principales: - coordinar catálogo, ticket, customización,
 * pago y opciones - mantener el estado en memoria de la venta actual -
 * construir el RegistrarVentaRequest final para persistencia - recalcular
 * combos y descuentos cuando cambia el ticket - delegar toda la lógica de
 * negocio real a Services / Facades
 *
 * IMPORTANTE: - la UI no accede directamente a DAOs - la UI no persiste por sí
 * sola - la UI solo coordina el flujo y refresca el estado visual
 */
public class VentasFrame extends BaseTpvFrame {

	private static final long serialVersionUID = 1L;

	// =====================================================
	// 1) DEPENDENCIAS
	// =====================================================

	private final AppServices services;
	private final ModoOperacion modoOperacion;
	private final MermaDialogResult mermaDialogResult;

	// =====================================================
	// 2) ESTADO EN MEMORIA DE LA VENTA ACTUAL
	// =====================================================

	/**
	 * Ticket actual en memoria.
	 */
	private final TicketSession ticketSession = new TicketSession();

	/**
	 * Estado temporal del cobro.
	 */
	private final CobroSession cobroSession = new CobroSession();

	/**
	 * Mapper auxiliar para construir JSON internos como descripcionPersonalizacion.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();

	// =====================================================
	// 3) COMPONENTES UI PRINCIPALES
	// =====================================================

	private VentasCenterPanel centerPanel;
	private TicketPanel ticketPanel;
	private CustomizationPanel customizationPanel;
	private BottomBarPanel bottomBarPanel;
	private CategoriasBarPanel categoriasBarPanel;

	// =====================================================
	// 4) CALLBACKS DE NAVEGACIÓN EXTERNA
	// =====================================================

	private final Runnable onLogoutNavigateAction;
	private final Runnable onBackToAdminAction;

	// =====================================================
	// 5) CONSTRUCTOR
	// =====================================================

	// =====================================================
	// CONSTRUCTORES NECESARIOS DE VentasFrame
	// =====================================================

	public VentasFrame(Runnable onLogoutNavigate, Runnable onBack, AppServices services) {
	    this(onLogoutNavigate, onBack, services, ModoOperacion.VENTA, null);
	}

	public VentasFrame(Runnable onLogoutNavigate,
	                   Runnable onBack,
	                   AppServices services,
	                   ModoOperacion modoOperacion) {
	    this(onLogoutNavigate, onBack, services, modoOperacion, null);
	}

	public VentasFrame(Runnable onLogoutNavigate,
	                   Runnable onBack,
	                   AppServices services,
	                   ModoOperacion modoOperacion,
	                   MermaDialogResult mermaDialogResult) {
	    super(buildTitleWithCaja(modoOperacion), onLogoutNavigate, services);

	    this.services = services;
	    this.onLogoutNavigateAction = onLogoutNavigate;
	    this.onBackToAdminAction = onBack;
	    this.modoOperacion = modoOperacion != null ? modoOperacion : ModoOperacion.VENTA;
	    this.mermaDialogResult = mermaDialogResult;

	    requireAuthenticatedOrExit();
	    AppContext.getSesionCajaActual();

	    buildUI(onBack);
	    loadCategorias();
	    configureOpcionesByRole();
	    refreshAll();
	}

	private static String buildTitleWithCaja(ModoOperacion modoOperacion) {
		String caja = "(sin caja)";
		try {
			caja = AppContext.getSesionCajaActual().getNombreCaja();
		} catch (Exception ignored) {
		}
		String prefijo = modoOperacion == ModoOperacion.MERMA ? "Merma" : "Ventas";
		return prefijo + " - Caja: " + caja;
	}

	// =====================================================
	// 6) CONSTRUCCIÓN DE LA UI
	// =====================================================

	private void buildUI(Runnable onBack) {

	    JPanel root = new JPanel(new BorderLayout(12, 12));
	    root.setOpaque(true);
	    root.setBackground(InformeUiTheme.APP_BG);
	    root.setBorder(new EmptyBorder(16, 16, 16, 16));

	    // -------------------------------------------------
	    // NORTH: barra de categorías
	    // -------------------------------------------------
	    categoriasBarPanel = new CategoriasBarPanel();
	    root.add(wrapInCard(categoriasBarPanel, new Dimension(0, 112)), BorderLayout.NORTH);
	    // -------------------------------------------------
	    // CENTER: catálogo / custom / pago / opciones / descuentos
	    // -------------------------------------------------
	    centerPanel = new VentasCenterPanel(
	            services,
	            ticketSession,
	            this::onProductoClicked,
	            new NombrePedidoPanel.NombrePedidoListener() {
	                @Override
	                public void onContinuar(String nombrePedido, TipoServicio servicio) {
	                    onNombrePedidoContinuar(nombrePedido, servicio);
	                }

	                @Override
	                public void onVolver() {
	                    onVolverDesdeNombrePedido();
	                }
	            },
	            new PagoPanel.PagoPanelListener() {
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
	            }
	    );

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
	        public void onTipoCafeClicked(TipoCafeDTO tipoCafe) {
	            onCenterTipoCafeClicked(tipoCafe);
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

	        @Override
	        public void onMermaClicked() {
	            onMerma();
	        }
	    });

	    centerPanel.setDescuentoActionListener(new DescuentoPanel.DescuentoActionListener() {
	        @Override
	        public void onAplicarCodigoPromocionalClicked() {
	            onAplicarCodigoPromocional();
	        }

	        @Override
	        public void onAplicarDescuentoEmpleadoClicked() {
	            onAplicarDescuentoEmpleado();
	        }

	        @Override
	        public void onQuitarDescuentoClicked() {
	            onQuitarDescuentoActual();
	        }

	        @Override
	        public void onVolverClicked() {
	            onVolverDesdeDescuentos();
	        }
	    });

	    root.add(wrapInCard(centerPanel, null), BorderLayout.CENTER);

	    // -------------------------------------------------
	    // EAST: customización lateral + ticket
	    // -------------------------------------------------
	    ticketPanel = new TicketPanel(ticketSession, this::onTicketSelectionChanged);

	    customizationPanel = new CustomizationPanel(
	            ticketSession,
	            services,
	            card -> centerPanel.showCustomCard(card),
	            this::onTamanoSelected
	    );

	    root.add(buildEastPanel(), BorderLayout.EAST);

	    // -------------------------------------------------
	    // SOUTH: barra inferior de acciones
	    // -------------------------------------------------
	    bottomBarPanel = new BottomBarPanel(
	            ticketSession,
	            modoOperacion,
	            this::onCobrar,
	            this::onCancelar,
	            this::onOpciones,
	            this::onDescuentos,
	            this::onEliminar
	    );

	    root.add(wrapInCard(bottomBarPanel, null), BorderLayout.SOUTH);

	    main.add(root, BorderLayout.CENTER);
	}
	private JPanel wrapInCard(JComponent content, Dimension preferredSize) {
	    JPanel wrapper = InformeUiTheme.createCardPanel(new BorderLayout());
	    wrapper.add(content, BorderLayout.CENTER);

	    if (preferredSize != null) {
	        wrapper.setPreferredSize(preferredSize);
	    }

	    return wrapper;
	}
	private JPanel buildEastPanel() {
	    JPanel east = new JPanel(new BorderLayout(12, 12));
	    east.setOpaque(false);
	    east.setPreferredSize(new Dimension(540, 0));

	    JPanel customWrap = wrapInCard(customizationPanel, null);
	    customWrap.setPreferredSize(new Dimension(180, 0));

	    JPanel ticketWrap = wrapInCard(ticketPanel, null);

	    east.add(customWrap, BorderLayout.WEST);
	    east.add(ticketWrap, BorderLayout.CENTER);

	    return east;
	}

	// =====================================================
	// 7) CATÁLOGO / CATEGORÍAS / PRODUCTOS
	// =====================================================

	private void onProductoClicked(ProductoDTO producto) {
		TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

		ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;

		// =================================================
		// NUEVO BLOQUE AÑADIDO:
		// aplicar café por defecto si el producto lo tiene
		// configurado en producto_tipo_cafe.
		// =================================================
		applyDefaultTipoCafeIfExists(newItemIndex, producto.getIdProducto());

		ticketSession.selectItemRow(newItemIndex);

		recalcularPromocionesSegunPrioridad();

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
	// 8) SELECCIÓN Y EDICIÓN DEL TICKET
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
			default -> {
			}
			}
		}

		recalcularPromocionesSegunPrioridad();

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

		recalcularPromocionesSegunPrioridad();

		refreshAll();
		loadCustomizationForSelectedItem();
		centerPanel.showCatalogo();
	}

	private void onCancelar() {

	    if (modoOperacion == ModoOperacion.MERMA) {

	        int opcion = JOptionPane.showConfirmDialog(
	                this,
	                "¿Salir del modo merma?",
	                "Merma",
	                JOptionPane.YES_NO_OPTION,
	                JOptionPane.WARNING_MESSAGE
	        );

	        if (opcion == JOptionPane.YES_OPTION) {
	            dispose();

	            if (onBackToAdminAction != null) {
	                onBackToAdminAction.run();
	            }
	        }

	        return;
	    }

	    int res = JOptionPane.showConfirmDialog(
	            this,
	            "¿Cancelar el pedido actual?",
	            "Cancelar venta",
	            JOptionPane.YES_NO_OPTION
	    );

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
	// 9) CUSTOMIZACIÓN DEL ITEM SELECCIONADO
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
		if (extra == null) {
			return;
		}

		if (!extra.isDisponible()) {
			JOptionPane.showMessageDialog(this, "Ese extra no está disponible en esta sucursal.", "Extra no disponible",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null) {
			return;
		}

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0) {
			return;
		}

		String tipo = extra.getTipo() == null ? "" : extra.getTipo().trim().toUpperCase();

		switch (tipo) {
		case "MILK" -> ticketSession.replaceExtraByTipo(itemIndex, extra);
		case "SHOT", "SYRUP", "TOPPING", "FOOD_EXTRA" -> ticketSession.addExtra(itemIndex, extra);
		default -> ticketSession.addExtra(itemIndex, extra);
		}

		ticketSession.selectItemRow(itemIndex);

		recalcularDescuentoAplicadoSiExiste();

		refreshAll();
	}

	private void onCenterPersonalizacionClicked(PersonalizacionDTO personalizacion) {
		if (personalizacion == null) {
			return;
		}

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null) {
			return;
		}

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0) {
			return;
		}

		ticketSession.togglePersonalizacion(itemIndex, personalizacion);
		ticketSession.selectItemRow(itemIndex);

		recalcularDescuentoAplicadoSiExiste();

		refreshAll();
	}

	private void onTamanoSelected(TamanoDTO tamanoSeleccionado) {
		if (tamanoSeleccionado == null) {
			return;
		}

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null) {
			return;
		}

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0) {
			return;
		}

		TicketItem item = ticketSession.getItems().get(itemIndex);
		int idProducto = item.getProducto().getIdProducto();
		int idTamano = tamanoSeleccionado.getIdTamano();

		TamanoPrecioDTO tamanoPrecio = services.productoPersonalizacionService.getPrecioByProductoYTamano(idProducto,
				idTamano);

		ticketSession.changeSize(itemIndex, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());
		ticketSession.selectItemRow(itemIndex);

		recalcularPromocionesSegunPrioridad();

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
	// 10) DESCUENTOS
	// =====================================================

	private void onDescuentos() {
		if (isModoMerma()) {
			JOptionPane.showMessageDialog(this, "En modo merma no están disponibles descuentos ni promociones.",
					"Modo merma", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (ticketSession.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos en el ticket.", "Descuentos",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		centerPanel.getDescuentoPanel().refresh();
		centerPanel.showDescuentos();
	}

	private void onVolverDesdeDescuentos() {
		centerPanel.showCatalogo();
	}

	private void onAplicarCodigoPromocional() {
		CodigoPromocionalDialog dialog = new CodigoPromocionalDialog(this);
		CodigoPromocionalDialogResult dialogResult = dialog.showDialog();

		if (!dialogResult.isConfirmed()) {
			return;
		}

		AplicarDescuentoRequest request = buildAplicarDescuentoRequestBase();
		request.setCodigoPromocional(dialogResult.getCodigo());

		DescuentoAplicadoDTO result = services.descuentoService.aplicarCodigoPromocional(request);

		if (!result.isValido()) {
			JOptionPane.showMessageDialog(this, result.getMensaje(), "Descuento", JOptionPane.WARNING_MESSAGE);
			centerPanel.getDescuentoPanel().refresh();
			return;
		}

		ticketSession.applyDiscount(result.getDescuentoAplicado());
		refreshAll();
		centerPanel.getDescuentoPanel().refresh();

		JOptionPane.showMessageDialog(this, result.getMensaje(), "Descuento aplicado", JOptionPane.INFORMATION_MESSAGE);
	}

	private void onAplicarDescuentoEmpleado() {
		if (ticketSession.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos en el ticket.", "Descuento empleado",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		DescuentoEmpleadoDialog dialog = new DescuentoEmpleadoDialog(this);
		DescuentoEmpleadoDialogResult dialogResult = dialog.showDialog();

		if (!dialogResult.isConfirmed()) {
			return;
		}

		String codigoEmpleado = dialogResult.getCodigoEmpleado();
		if (codigoEmpleado == null || codigoEmpleado.isBlank()) {
			JOptionPane.showMessageDialog(this, "Debes introducir un código de empleado.", "Descuento empleado",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		Optional<Usuario> empleadoOpt = services.usuarioService.findByCodigo(codigoEmpleado.trim());

		if (empleadoOpt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No existe ningún empleado con ese código.", "Descuento empleado",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		Usuario empleado = empleadoOpt.get();

		if (!empleado.isActivo()) {
			JOptionPane.showMessageDialog(this, "El empleado indicado está inactivo.", "Descuento empleado",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		AplicarDescuentoRequest request = buildAplicarDescuentoRequestBase();
		request.setCodigoEmpleado(codigoEmpleado.trim());

		DescuentoAplicadoDTO result = services.descuentoService.aplicarDescuentoEmpleado(request,
				empleado.getIdUsuario(), empleado.getNombre());

		if (!result.isValido()) {
			JOptionPane.showMessageDialog(this, result.getMensaje(), "Descuento empleado", JOptionPane.WARNING_MESSAGE);
			centerPanel.getDescuentoPanel().refresh();
			return;
		}

		ticketSession.applyDiscount(result.getDescuentoAplicado());
		refreshAll();
		centerPanel.getDescuentoPanel().refresh();

		JOptionPane.showMessageDialog(this, result.getMensaje(), "Descuento empleado aplicado",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void onQuitarDescuentoActual() {
		if (!ticketSession.hasDiscount()) {
			JOptionPane.showMessageDialog(this, "No hay ningún descuento aplicado.", "Descuentos",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int res = JOptionPane.showConfirmDialog(this, "¿Deseas quitar el descuento actual del ticket?",
				"Quitar descuento", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (res != JOptionPane.YES_OPTION) {
			return;
		}

		ticketSession.clearDiscount();
		refreshAll();
		centerPanel.getDescuentoPanel().refresh();

		JOptionPane.showMessageDialog(this, "Descuento eliminado correctamente.", "Descuentos",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private AplicarDescuentoRequest buildAplicarDescuentoRequestBase() {
		AplicarDescuentoRequest request = new AplicarDescuentoRequest();

		request.setIdUsuarioActual(AppContext.getUsuario().getIdUsuario());
		request.setSubtotalTicket(ticketSession.getTotalSinDescuento());
		request.setTicketVacio(ticketSession.isEmpty());
		request.setYaTieneDescuento(ticketSession.hasDiscount());
		request.setTieneComboAplicado(ticketSession.hasAppliedCombos());

		return request;
	}

	private AplicarDescuentoRequest buildAplicarDescuentoRequestParaRecalculo() {
		AplicarDescuentoRequest request = new AplicarDescuentoRequest();

		request.setIdUsuarioActual(AppContext.getUsuario().getIdUsuario());
		request.setSubtotalTicket(ticketSession.getTotalSinDescuento());
		request.setTicketVacio(ticketSession.isEmpty());
		request.setYaTieneDescuento(false);
		request.setTieneComboAplicado(ticketSession.hasAppliedCombos());

		return request;
	}

	private void recalcularDescuentoAplicadoSiExiste() {
		if (!ticketSession.hasDiscount()) {
			return;
		}

		DescuentoAplicado actual = ticketSession.getDescuentoAplicado();
		if (actual == null) {
			return;
		}

		AplicarDescuentoRequest request = buildAplicarDescuentoRequestParaRecalculo();

		String origen = actual.getOrigen() != null ? actual.getOrigen().trim().toUpperCase() : "";

		DescuentoAplicadoDTO result;

		switch (origen) {
		case "PROMOCIONAL" -> {
			request.setCodigoPromocional(actual.getCodigoIntroducido());
			result = services.descuentoService.aplicarCodigoPromocional(request);
		}
		case "EMPLEADO" -> {
			request.setCodigoEmpleado(actual.getCodigoIntroducido());

			result = services.descuentoService.aplicarDescuentoEmpleado(request, actual.getIdEmpleadoBeneficiario(),
					actual.getNombreEmpleadoBeneficiario());
		}
		default -> {
			ticketSession.clearDiscount();
			return;
		}
		}

		if (result.isValido()) {
			ticketSession.applyDiscount(result.getDescuentoAplicado());
		} else {
			ticketSession.clearDiscount();
		}
	}

	private RegistrarVentaDescuentoRequest buildDescuentoRequest() {
		if (!ticketSession.hasDiscount()) {
			return null;
		}

		return services.descuentoService.toRegistrarVentaDescuentoRequest(ticketSession.getDescuentoAplicado(),
				AppContext.getUsuario().getIdUsuario());
	}

	// =====================================================
	// 11) FLUJO DE COBRO
	// =====================================================

	private void onCobrar() {
		if (modoOperacion == ModoOperacion.MERMA) {
			onRegistrarMerma();
			return;
		}

		if (ticketSession.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos en el ticket.", "Cobrar",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		recalcularPromocionesSegunPrioridad();
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
	// 12) CONSTRUCCIÓN DEL REQUEST DE REGISTRO DE VENTA
	// =====================================================

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
		request.setIdSucursal(AppContext.getIdSucursal());

		List<RegistrarVentaItemRequest> items = new ArrayList<>();
		for (int i = 0; i < ticketSession.getItems().size(); i++) {
			TicketItem item = ticketSession.getItems().get(i);
			items.add(buildItemRequest(item, i));
		}
		request.setItems(items);

		request.setCombos(buildComboRequests());
		request.setDescuento(buildDescuentoRequest());

		return request;
	}

	private List<RegistrarVentaComboRequest> buildComboRequests() {
		List<RegistrarVentaComboRequest> result = new ArrayList<>();

		List<TicketCombo> appliedCombos = ticketSession.getAppliedCombos();
		if (appliedCombos == null || appliedCombos.isEmpty()) {
			return result;
		}

		for (TicketCombo combo : appliedCombos) {
			if (combo == null || combo.isEmpty()) {
				continue;
			}

			RegistrarVentaComboRequest dto = new RegistrarVentaComboRequest();
			dto.setIdCombo(combo.getIdCombo());
			dto.setNombreCombo(combo.getNombreCombo());
			dto.setTipoCombo(combo.getComboDefinition().getCombo().getTipo());
			dto.setValorCombo(combo.getComboDefinition().getCombo().getValor());
			dto.setPrecioOriginal(safe(combo.getPrecioOriginal()));
			dto.setPrecioFinal(safe(combo.getPrecioFinal()));
			dto.setAhorroTotal(safe(combo.getAhorroTotal()));
			dto.setItems(buildComboItemRequests(combo));

			result.add(dto);
		}

		return result;
	}

	private List<RegistrarVentaComboItemRequest> buildComboItemRequests(TicketCombo combo) {
		List<RegistrarVentaComboItemRequest> result = new ArrayList<>();

		List<Integer> indexes = combo.getTicketItemIndexes();
		if (indexes == null || indexes.isEmpty()) {
			return result;
		}

		BigDecimal ahorroTotal = safe(combo.getAhorroTotal());
		BigDecimal sumaBases = BigDecimal.ZERO;

		List<BigDecimal> bases = new ArrayList<>();
		for (Integer itemIndex : indexes) {
			TicketItem item = ticketSession.getItems().get(itemIndex);
			BigDecimal base = safe(item.getPrecioBase());
			bases.add(base);
			sumaBases = sumaBases.add(base);
		}

		BigDecimal descuentoAcumulado = BigDecimal.ZERO;

		for (int i = 0; i < indexes.size(); i++) {
			int itemIndex = indexes.get(i);
			BigDecimal subtotalOriginal = bases.get(i);

			BigDecimal descuentoAsignado;
			if (i == indexes.size() - 1) {
				descuentoAsignado = ahorroTotal.subtract(descuentoAcumulado);
			} else if (sumaBases.compareTo(BigDecimal.ZERO) == 0) {
				descuentoAsignado = BigDecimal.ZERO;
			} else {
				descuentoAsignado = ahorroTotal.multiply(subtotalOriginal).divide(sumaBases, 2,
						java.math.RoundingMode.HALF_UP);

				descuentoAcumulado = descuentoAcumulado.add(descuentoAsignado);
			}

			BigDecimal subtotalFinal = subtotalOriginal.subtract(descuentoAsignado);

			RegistrarVentaComboItemRequest dto = new RegistrarVentaComboItemRequest();
			dto.setTicketItemIndex(itemIndex);
			dto.setSubtotalOriginalItem(subtotalOriginal);
			dto.setDescuentoAsignado(descuentoAsignado);
			dto.setSubtotalFinalItem(subtotalFinal);

			result.add(dto);
		}

		return result;
	}

	private RegistrarVentaItemRequest buildItemRequest(TicketItem item, int ticketItemIndex) {
		RegistrarVentaItemRequest dto = new RegistrarVentaItemRequest();

		BigDecimal precioUnitario = safe(item.getPrecioBase());
		BigDecimal subtotalBruto = safe(item.getSubtotal());
		BigDecimal importeDescuentoLinea = calcularImporteDescuentoLinea(ticketItemIndex);
		BigDecimal subtotalFinal = subtotalBruto.subtract(importeDescuentoLinea);

		if (subtotalFinal.compareTo(BigDecimal.ZERO) < 0) {
			subtotalFinal = BigDecimal.ZERO;
		}

		dto.setIdProducto(item.getProducto().getIdProducto());
		dto.setNombreProducto(item.getProducto().getNombre());
		dto.setCantidad(1);

		dto.setPrecioUnitario(precioUnitario);
		dto.setSubtotalBruto(subtotalBruto);
		dto.setImporteDescuentoLinea(importeDescuentoLinea);
		dto.setSubtotalFinal(subtotalFinal);

		BigDecimal ivaPorcentaje = safe(item.getProducto().getIvaPorcentaje());
		dto.setIva(calcularIvaIncluido(subtotalFinal, ivaPorcentaje));

		dto.setDescripcionPersonalizacion(buildDescripcionPersonalizacion(item));

		// =====================================================
		// NUEVO BLOQUE: SNAPSHOT DE CAFÉ
		// =====================================================
		dto.setIdTipoCafeSeleccionado(item.getIdTipoCafeSeleccionado());
		dto.setNombreTipoCafeSnapshot(item.getNombreTipoCafeSeleccionado());
		dto.setSuplementoTipoCafe(safe(item.getSuplementoTipoCafe()));

		List<RegistrarVentaExtraRequest> extras = new ArrayList<>();
		for (TicketExtra extra : item.getExtras()) {
			extras.add(buildExtraRequest(extra));
		}
		dto.setExtras(extras);

		dto.setIdTamano(item.getTamano().getIdTamano());
		dto.setNombreTamano(item.getTamano().getNombre());

		dto.setIdIngredienteTipoCafeSeleccionado(item.getIdIngredienteTipoCafeSeleccionado());

		List<RegistrarVentaPersonalizacionRequest> pers = new ArrayList<>();
		if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
			for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
				if (p == null) {
					continue;
				}

				RegistrarVentaPersonalizacionRequest rp = new RegistrarVentaPersonalizacionRequest();
				rp.setIdPersonalizacion(p.getIdPersonalizacion());
				rp.setNombrePersonalizacion(p.getNombre());
				rp.setPrecioPersonalizacion(p.getPrecio());
				pers.add(rp);
			}
		}
		dto.setPersonalizaciones(pers);

		return dto;
	}

	/**
	 * Decide qué descuento corresponde a una línea concreta.
	 *
	 * Reglas: - si hay combo aplicado, se usa el descuento imputado por combo - si
	 * hay descuento global y NO hay combos, se reparte proporcionalmente - si no
	 * hay nada aplicado, devuelve 0
	 *
	 * VentaService ya valida que no se registren combos y descuento a la vez.
	 */
	private BigDecimal calcularImporteDescuentoLinea(int ticketItemIndex) {
		if (ticketSession.hasAppliedCombos()) {
			return calcularDescuentoComboParaItem(ticketItemIndex);
		}

		if (ticketSession.hasDiscount()) {
			return calcularDescuentoGlobalParaItem(ticketItemIndex);
		}

		return BigDecimal.ZERO;
	}

	/**
	 * Calcula el descuento de combo imputado a un item concreto.
	 *
	 * IMPORTANTE: - replica la misma lógica de reparto que
	 * buildComboItemRequests(...) - el descuento del combo se reparte sobre el
	 * precio base del item - los extras/personalizaciones siguen dentro del
	 * subtotalBruto de la línea
	 */
	private BigDecimal calcularDescuentoComboParaItem(int ticketItemIndex) {
		List<TicketCombo> appliedCombos = ticketSession.getAppliedCombos();
		if (appliedCombos == null || appliedCombos.isEmpty()) {
			return BigDecimal.ZERO;
		}

		for (TicketCombo combo : appliedCombos) {
			if (combo == null || !combo.containsItemIndex(ticketItemIndex)) {
				continue;
			}

			List<Integer> indexes = combo.getTicketItemIndexes();
			if (indexes == null || indexes.isEmpty()) {
				return BigDecimal.ZERO;
			}

			BigDecimal ahorroTotal = safe(combo.getAhorroTotal());
			BigDecimal sumaBases = BigDecimal.ZERO;
			List<BigDecimal> bases = new ArrayList<>();

			for (Integer idx : indexes) {
				TicketItem comboItem = ticketSession.getItems().get(idx);
				BigDecimal base = safe(comboItem.getPrecioBase());
				bases.add(base);
				sumaBases = sumaBases.add(base);
			}

			BigDecimal descuentoAcumulado = BigDecimal.ZERO;

			for (int i = 0; i < indexes.size(); i++) {
				int currentIndex = indexes.get(i);
				BigDecimal subtotalOriginal = bases.get(i);

				BigDecimal descuentoAsignado;
				if (i == indexes.size() - 1) {
					descuentoAsignado = ahorroTotal.subtract(descuentoAcumulado);
				} else if (sumaBases.compareTo(BigDecimal.ZERO) == 0) {
					descuentoAsignado = BigDecimal.ZERO;
				} else {
					descuentoAsignado = ahorroTotal.multiply(subtotalOriginal).divide(sumaBases, 2,
							java.math.RoundingMode.HALF_UP);

					descuentoAcumulado = descuentoAcumulado.add(descuentoAsignado);
				}

				if (currentIndex == ticketItemIndex) {
					return descuentoAsignado.max(BigDecimal.ZERO);
				}
			}

			return BigDecimal.ZERO;
		}

		return BigDecimal.ZERO;
	}

	/**
	 * Calcula el descuento global imputado a una línea concreta.
	 *
	 * Solo se usa cuando: - hay descuento aplicado - no hay combos aplicados
	 *
	 * Se reparte proporcionalmente por subtotal bruto de línea.
	 */
	private BigDecimal calcularDescuentoGlobalParaItem(int ticketItemIndex) {
		DescuentoAplicado descuento = ticketSession.getDescuentoAplicado();
		if (descuento == null) {
			return BigDecimal.ZERO;
		}

		BigDecimal descuentoTotal = safe(descuento.getImporteDescuento());
		if (descuentoTotal.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		List<TicketItem> items = ticketSession.getItems();
		if (items == null || items.isEmpty()) {
			return BigDecimal.ZERO;
		}

		BigDecimal sumaBrutos = BigDecimal.ZERO;
		List<BigDecimal> subtotalesBrutos = new ArrayList<>();

		for (TicketItem current : items) {
			BigDecimal bruto = safe(current.getSubtotal());
			subtotalesBrutos.add(bruto);
			sumaBrutos = sumaBrutos.add(bruto);
		}

		if (sumaBrutos.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal descuentoAcumulado = BigDecimal.ZERO;

		for (int i = 0; i < subtotalesBrutos.size(); i++) {
			BigDecimal brutoLinea = subtotalesBrutos.get(i);

			BigDecimal descuentoLinea;
			if (i == subtotalesBrutos.size() - 1) {
				descuentoLinea = descuentoTotal.subtract(descuentoAcumulado);
			} else {
				descuentoLinea = descuentoTotal.multiply(brutoLinea).divide(sumaBrutos, 2,
						java.math.RoundingMode.HALF_UP);

				descuentoAcumulado = descuentoAcumulado.add(descuentoLinea);
			}

			if (i == ticketItemIndex) {
				return descuentoLinea.max(BigDecimal.ZERO);
			}
		}

		return BigDecimal.ZERO;
	}

	private RegistrarVentaExtraRequest buildExtraRequest(TicketExtra extra) {
		RegistrarVentaExtraRequest dto = new RegistrarVentaExtraRequest();
		dto.setIdExtra(extra.getIdExtra());
		dto.setNombreExtra(extra.getNombre());
		dto.setPrecioExtra(safe(extra.getPrecio()));
		return dto;
	}

	private String buildDescripcionPersonalizacion(TicketItem item) {
		try {
			Map<String, Object> root = new LinkedHashMap<>();

			root.put("tamano", item.getTamano() != null ? item.getTamano().getNombre() : null);

			// =====================================================
			// NUEVO BLOQUE: meter también el café en el JSON interno
			// =====================================================
			root.put("tipoCafe", item.getNombreTipoCafeSeleccionado());
			root.put("suplementoTipoCafe", safe(item.getSuplementoTipoCafe()));

			List<String> personalizaciones = new ArrayList<>();
			if (item.getPersonalizaciones() != null && !item.getPersonalizaciones().isEmpty()) {
				for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
					if (p != null && p.getNombre() != null && !p.getNombre().isBlank()) {
						personalizaciones.add(p.getNombre().trim());
					}
				}
			}
			root.put("personalizaciones", personalizaciones);

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

	// =====================================================
	// 13) PERSISTENCIA FINAL DEL COBRO
	// =====================================================

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

	    int idSucursal = AppContext.getIdSucursal();

	    List<ColaRegistroItemCommand> commands = new ArrayList<>();

	    for (int i = 0; i < ticketSession.getItems().size(); i++) {
	        TicketItem ticketItem = ticketSession.getItems().get(i);
	        RegistrarVentaItemResultDTO persisted = itemsPersistidos.get(i);

	        ColaItemDescripcionDTO descripcion = buildColaItemDescripcion(ticketItem);

	        commands.add(new ColaRegistroItemCommand(
	                result.getIdVenta(),
	                persisted.getIdItem(),
	                persisted.getIdProducto(),
	                idSucursal,
	                descripcion
	        ));
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

		// =====================================================
		// NUEVO BLOQUE: snapshot de café para cola/monitor
		// =====================================================
		dto.setTipoCafe(item.getNombreTipoCafeSeleccionado());

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

	private void finalizarCobroTrasPersistencia(RegistrarVentaResultDTO result) {
		int idVenta = result.getIdVenta();

		TicketClienteDTO ticket = null;
		try {
			ticket = services.ticketClienteService.getTicketByVenta(idVenta);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ticketSession.clear();
		cobroSession.clear();

		centerPanel.getNombrePedidoPanel().clear();
		centerPanel.getPagoPanel().clear();
		centerPanel.clearCustomizationData();
		centerPanel.showCatalogo();

		refreshAll();

		if (ticket != null) {
			TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
			dialog.showDialog();
			return;
		}

		JOptionPane.showMessageDialog(this, "Venta registrada correctamente.\n\nID venta: " + idVenta,
				"Pago completado", JOptionPane.INFORMATION_MESSAGE);
	}

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
	// 14) HELPERS DE COBRO Y CÁLCULO
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
	// 15) OPCIONES SECUNDARIAS DEL TPV
	// =====================================================

	private void onOpciones() {
		if (isModoMerma()) {
			JOptionPane.showMessageDialog(this, "En modo merma no están disponibles las opciones comerciales.",
					"Modo merma", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		centerPanel.showOpciones();
	}

	private void onVolverDesdeOpciones() {
		centerPanel.showCatalogo();
	}

	private void onDisponibilidad() {
		DisponibilidadItemsDialog dialog = new DisponibilidadItemsDialog(this, services);
		dialog.showDialog();
		refreshAll();
		loadCustomizationForSelectedItem();
	}

	private void onStock() {
		JOptionPane.showMessageDialog(this, "Stock (pendiente)");
	}

	private void onReimprimir() {
		if (isModoMerma()) {
			JOptionPane.showMessageDialog(this, "En modo merma no está disponible la reimpresión de tickets.",
					"Modo merma", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			int idSesion = AppContext.getSesionCajaActual().getIdSesion();

			TicketClienteDTO ticket = services.ticketClienteService.getUltimoTicketDeSesion(idSesion);

			TicketClienteDialog dialog = new TicketClienteDialog(this, ticket);
			dialog.showDialog();

		} catch (Exception e) {
			e.printStackTrace();

			JOptionPane.showMessageDialog(this, "No se pudo abrir el último ticket.\n\n" + e.getMessage(),
					"Reimprimir ticket", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onUltimosTickets() {
		if (isModoMerma()) {
			JOptionPane.showMessageDialog(this, "En modo merma no está disponible la consulta de tickets.",
					"Modo merma", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		TicketsHoyDialog dialog = new TicketsHoyDialog(this, services);
		dialog.showDialog();
	}

	private void onDevoluciones() {
		if (isModoMerma()) {
			JOptionPane.showMessageDialog(this, "En modo merma no está disponible la devoluciones.", "Modo merma",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		try {
			DevolucionesDialog dialog = new DevolucionesDialog(this, services);
			dialog.showDialog();

			refreshAll();
			loadCustomizationForSelectedItem();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "No se pudo abrir devoluciones.\n\n" + e.getMessage(), "Devoluciones",
					JOptionPane.ERROR_MESSAGE);
		}
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
	// 16) BÚSQUEDA / SKU / AÑADIDO DE PRODUCTOS
	// =====================================================

	// =====================================================
	// 4) REEMPLAZAR onSku(...)
//     Igual que en catálogo: al añadir item por SKU,
//     aplicar café por defecto si existe.
	// =====================================================

	private void onSku() {
		SkuDialog dialog = new SkuDialog(this);
		String sku = dialog.showDialog();

		if (sku == null || sku.isBlank()) {
			return;
		}

		Optional<ProductoCatalogoDTO> productoOpt = services.catalogoService.buscarProductoCatalogoPorSku(sku);
		if (productoOpt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No existe ningún producto con ese SKU en esta sucursal.",
					"SKU no encontrado", JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductoCatalogoDTO productoCatalogo = productoOpt.get();

		if (!productoCatalogo.isBotonHabilitado()) {
			JOptionPane.showMessageDialog(this,
					"Ese producto no se puede vender ahora.\n\nEstado: " + buildEstadoProducto(productoCatalogo),
					"Producto no disponible", JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductoDTO producto = toProductoDTO(productoCatalogo);
		TamanoPrecioDTO def = services.catalogoService.getTamanoDefaultYPrecio(producto.getIdProducto());

		ticketSession.addItem(producto, def.getTamanoDTO(), def.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;

		// NUEVO
		applyDefaultTipoCafeIfExists(newItemIndex, producto.getIdProducto());

		ticketSession.selectItemRow(newItemIndex);

		recalcularPromocionesSegunPrioridad();

		refreshAll();
		loadCustomizationForSelectedItem();
		centerPanel.showCatalogo();
	}

//=====================================================
//5) REEMPLAZAR onBuscarProducto(...)
//  Igual que en los otros dos flujos de alta de item.
//=====================================================

	private void onBuscarProducto() {
		List<ProductoBusquedaRowDTO> rows = services.catalogoService.getFilasBusquedaProductoOperativa();

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

		if (!selectedRow.isBotonHabilitado()) {
			JOptionPane.showMessageDialog(this,
					"Ese producto no se puede vender ahora.\n\nEstado: " + selectedRow.getTextoEstado(),
					"Producto no disponible", JOptionPane.WARNING_MESSAGE);
			return;
		}

		ProductoDTO producto = new ProductoDTO(selectedRow.getIdProducto(), selectedRow.getIdSubcategoria(),
				selectedRow.getNombreProducto(), 0, selectedRow.isPermiteExtras(),
				selectedRow.isPermitePersonalizacion(), selectedRow.getIvaPorcentaje(),
				selectedRow.isPermiteStockCantidad());

		TamanoPrecioDTO tamanoPrecio = services.productoPersonalizacionService
				.getPrecioByProductoYTamano(selectedRow.getIdProducto(), selectedRow.getIdTamano());

		ticketSession.addItem(producto, tamanoPrecio.getTamanoDTO(), tamanoPrecio.getPrecio());

		int newItemIndex = ticketSession.getItems().size() - 1;

		// NUEVO
		applyDefaultTipoCafeIfExists(newItemIndex, selectedRow.getIdProducto());

		ticketSession.selectItemRow(newItemIndex);

		recalcularPromocionesSegunPrioridad();

		refreshAll();
		loadCustomizationForSelectedItem();
		centerPanel.showCatalogo();
	}

	// =====================================================
	// 17) REFRESH / ROL / MODO VISUAL
	// =====================================================

	private void refreshAll() {
	    ticketPanel.refreshFromTicket();
	    customizationPanel.refresh();
	    bottomBarPanel.refresh();

	    if (centerPanel != null && centerPanel.getDescuentoPanel() != null) {
	        centerPanel.getDescuentoPanel().refresh();
	    }

	    if (centerPanel != null) {
	        centerPanel.revalidate();
	        centerPanel.repaint();
	    }

	    if (ticketPanel != null) {
	        ticketPanel.revalidate();
	        ticketPanel.repaint();
	    }

	    if (customizationPanel != null) {
	        customizationPanel.revalidate();
	        customizationPanel.repaint();
	    }

	    if (bottomBarPanel != null) {
	        bottomBarPanel.revalidate();
	        bottomBarPanel.repaint();
	    }

	    main.revalidate();
	    main.repaint();
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
		centerPanel.setOpcionesAdminMode(!isModoMerma() && isAdminActual());
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

	private ProductoDTO toProductoDTO(ProductoCatalogoDTO p) {
		return new ProductoDTO(p.getIdProducto(), p.getIdSubcategoria(), p.getNombre(), p.getOrden(),
				p.isPermiteExtras(), p.isPermitePersonalizacion(), p.getIvaPorcentaje(), p.isPermiteStockCantidad());
	}

	private String buildEstadoProducto(ProductoCatalogoDTO producto) {
		if (!producto.isDisponible()) {
			return "No disponible";
		}
		if (producto.isAgotado()) {
			return "Agotado";
		}
		if (producto.muestraContador()) {
			return "Stock: " + producto.getStockActual().stripTrailingZeros().toPlainString();
		}
		return "Disponible";
	}

	/**
	 * Recalcula automáticamente los combos del ticket actual.
	 */
	private void recalcularCombosAutomaticos() {
		try {
			List<TicketCombo> combos = services.comboMatcherService.detectAppliedCombos(
					services.getCombosActivosCache(), ticketSession, java.time.LocalDateTime.now());

			ticketSession.replaceAppliedCombos(combos);

		} catch (Exception e) {
			e.printStackTrace();
			ticketSession.clearAppliedCombos();
		}
	}

	private void recalcularPromocionesSegunPrioridad() {
		if (isModoMerma()) {
			ticketSession.clearAppliedCombos();
			ticketSession.clearDiscount();
			return;
		}

		if (ticketSession.hasDiscount()) {
			recalcularDescuentoAplicadoSiExiste();

			// Si tras recalcular se perdió el descuento,
			// entonces ahora sí dejamos entrar combos.
			if (!ticketSession.hasDiscount()) {
				recalcularCombosAutomaticos();
			}

		} else {
			recalcularCombosAutomaticos();
		}
	}

	// =====================================================
	// 6) MÉTODO NUEVO
//	    Gestión del click en la card CAFE.
	// =====================================================

	private void onCenterTipoCafeClicked(TipoCafeDTO tipoCafe) {
		if (tipoCafe == null) {
			return;
		}

		TicketRow selectedRow = ticketSession.getSelectedRowOrNull();
		if (selectedRow == null) {
			return;
		}

		int itemIndex = selectedRow.getItemIndex();
		if (itemIndex < 0) {
			return;
		}

		// =================================================
		// NUEVO:
		// guardar el café seleccionado en el item actual.
		// =================================================
		ticketSession.changeTipoCafe(itemIndex, tipoCafe);
		ticketSession.selectItemRow(itemIndex);

		// =================================================
		// IMPORTANTE:
		// si hay descuento aplicado, hay que recalcularlo
		// porque el subtotal del item puede cambiar por
		// el suplemento del café.
		// =================================================
		recalcularDescuentoAplicadoSiExiste();

		refreshAll();
	}
	// =====================================================
	// 7) MÉTODO NUEVO
//	    Aplica automáticamente el café por defecto del producto
//	    cuando se crea el item en el ticket.
	// =====================================================

	private boolean isModoMerma() {
		return modoOperacion == ModoOperacion.MERMA;
	}

	private void onRegistrarMerma() {
		try {
			MermaRequest request = buildMermaRequestFromDialog();
			MermaResultDTO result = services.mermaFacade.registrarMerma(request);

			JOptionPane.showMessageDialog(this, "Merma registrada correctamente.\nID merma: " + result.getIdMerma(),
					"Merma registrada", JOptionPane.INFORMATION_MESSAGE);

			clearAfterMerma();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error registrando la merma:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private MermaRequest buildMermaRequestFromDialog() {
		System.out.print(AppContext.getIdSucursal());
		if (mermaDialogResult == null || !mermaDialogResult.isConfirmed()) {
			throw new IllegalStateException("No hay contexto válido de merma.");
		}

		if (ticketSession == null || ticketSession.getItems() == null || ticketSession.getItems().isEmpty()) {
			throw new IllegalStateException("No hay items en el ticket para registrar como merma.");
		}

		MermaRequest request = new MermaRequest();
		request.setIdSucursal(AppContext.getIdSucursal());
		request.setIdUsuario(AppContext.getUsuarioId());
		request.setTipoMerma("PRODUCTO_TERMINADO");
		request.setOrigen("VENTAS");
		request.setMotivo(mermaDialogResult.getMotivo());
		request.setObservaciones(mermaDialogResult.getObservaciones());

		List<MermaItemRequest> items = new ArrayList<>();

		for (TicketItem item : ticketSession.getItems()) {
			if (item == null || item.getProducto() == null) {
				continue;
			}

			MermaItemRequest mi = new MermaItemRequest();
			mi.setIdProducto(item.getProducto().getIdProducto());
			mi.setNombreProductoSnapshot(item.getProducto().getNombre());

			if (item.getTamano() != null) {
				mi.setIdTamano(item.getTamano().getIdTamano());
				mi.setNombreTamanoSnapshot(item.getTamano().getNombre());
			}

			mi.setIdTipoCafeSeleccionado(item.getIdTipoCafeSeleccionado());
			mi.setNombreTipoCafeSnapshot(item.getNombreTipoCafeSeleccionado());
			mi.setIdIngredienteTipoCafeSeleccionado(item.getIdIngredienteTipoCafeSeleccionado());
			mi.setSuplementoTipoCafe(safe(item.getSuplementoTipoCafe()));

			mi.setCantidad(BigDecimal.ONE);
			mi.setUsarReceta(item.getTamano() != null);

			mi.setExtras(buildMermaExtras(item));
			mi.setPersonalizaciones(buildMermaPersonalizaciones(item));
			mi.setAskMes(item.getAskMes() != null ? new ArrayList<>(item.getAskMes()) : new ArrayList<>());

			mi.setConfiguracionJson(buildMermaConfiguracionJson(item));
			mi.setDescripcionSnapshot(buildMermaDescripcionSnapshot(item));

			items.add(mi);
		}

		request.setItems(items);
		return request;
	}

	private MermaItemRequest buildMermaItemRequest(TicketItem item) {
		MermaItemRequest dto = new MermaItemRequest();
		dto.setIdProducto(item.getProducto().getIdProducto());
		dto.setNombreProductoSnapshot(item.getProducto().getNombre());

		if (item.getTamano() != null) {
			dto.setIdTamano(item.getTamano().getIdTamano());
			dto.setNombreTamanoSnapshot(item.getTamano().getNombre());
		}

		dto.setIdTipoCafeSeleccionado(item.getIdTipoCafeSeleccionado());
		dto.setNombreTipoCafeSnapshot(item.getNombreTipoCafeSeleccionado());
		dto.setIdIngredienteTipoCafeSeleccionado(item.getIdIngredienteTipoCafeSeleccionado());
		dto.setSuplementoTipoCafe(safe(item.getSuplementoTipoCafe()));
		dto.setCantidad(BigDecimal.ONE);
		dto.setUsarReceta(debeUsarReceta(item));

		List<MermaExtraRequest> extras = new ArrayList<>();
		for (TicketExtra extra : item.getExtras()) {
			MermaExtraRequest e = new MermaExtraRequest();
			e.setIdExtra(extra.getIdExtra());
			e.setNombreExtra(extra.getNombre());
			e.setTipoExtra(extra.getTipo());
			e.setPrecioExtra(safe(extra.getPrecio()));
			e.setCantidad(BigDecimal.ONE);
			extras.add(e);
		}
		dto.setExtras(extras);

		List<MermaPersonalizacionRequest> personalizaciones = new ArrayList<>();
		for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
			MermaPersonalizacionRequest mp = new MermaPersonalizacionRequest();
			mp.setIdPersonalizacion(p.getIdPersonalizacion());
			mp.setNombrePersonalizacion(p.getNombre());
			mp.setTipoPersonalizacion(p.getTipo());
			mp.setPrecioPersonalizacion(safe(p.getPrecio()));
			personalizaciones.add(mp);
		}
		dto.setPersonalizaciones(personalizaciones);

		dto.setAskMes(new ArrayList<>(item.getAskMes()));
		dto.setConfiguracionJson(buildMermaConfiguracionJson(item));
		dto.setDescripcionSnapshot(buildMermaDescripcionSnapshot(item));

		return dto;
	}

	private boolean debeUsarReceta(TicketItem item) {
		if (item == null || item.getProducto() == null) {
			return false;
		}

		int idSubcategoria = item.getProducto().getIdSubcategoria();
		return idSubcategoria >= 1 && idSubcategoria <= 7;
	}

	private String buildMermaConfiguracionJson(TicketItem item) {
		try {
			Map<String, Object> root = new LinkedHashMap<>();
			root.put("producto", item.getProducto() != null ? item.getProducto().getNombre() : null);
			root.put("tamano", item.getTamano() != null ? item.getTamano().getNombre() : null);
			root.put("idTipoCafe", item.getIdTipoCafeSeleccionado());
			root.put("tipoCafe", item.getNombreTipoCafeSeleccionado());
			root.put("suplementoTipoCafe", safe(item.getSuplementoTipoCafe()));

			List<Map<String, Object>> extras = new ArrayList<>();
			for (TicketExtra extra : item.getExtras()) {
				Map<String, Object> e = new LinkedHashMap<>();
				e.put("idExtra", extra.getIdExtra());
				e.put("nombre", extra.getNombre());
				e.put("tipo", extra.getTipo());
				e.put("precio", safe(extra.getPrecio()));
				extras.add(e);
			}
			root.put("extras", extras);

			List<Map<String, Object>> personalizaciones = new ArrayList<>();
			for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
				Map<String, Object> mp = new LinkedHashMap<>();
				mp.put("idPersonalizacion", p.getIdPersonalizacion());
				mp.put("nombre", p.getNombre());
				mp.put("tipo", p.getTipo());
				mp.put("precio", safe(p.getPrecio()));
				personalizaciones.add(mp);
			}
			root.put("personalizaciones", personalizaciones);
			root.put("askMes", new ArrayList<>(item.getAskMes()));

			return objectMapper.writeValueAsString(root);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error construyendo configuracion_json de merma.", e);
		}
	}

	private String buildMermaDescripcionSnapshot(TicketItem item) {
		StringBuilder sb = new StringBuilder();

		if (item.getProducto() != null) {
			sb.append(item.getProducto().getNombre());
		}

		if (item.getTamano() != null && item.getTamano().getNombre() != null) {
			sb.append(" ").append(item.getTamano().getNombre());
		}

		if (item.hasTipoCafeSeleccionado() && item.getNombreTipoCafeSeleccionado() != null
				&& !item.getNombreTipoCafeSeleccionado().isBlank()) {
			sb.append(" | Café: ").append(item.getNombreTipoCafeSeleccionado().trim());
		}

		for (TicketExtra extra : item.getExtras()) {
			sb.append(" + ").append(extra.getNombre());
		}

		for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
			sb.append(" - ").append(p.getNombre());
		}

		for (String ask : item.getAskMes()) {
			if (ask != null && !ask.isBlank()) {
				sb.append(" | Ask Me: ").append(ask.trim());
			}
		}

		return sb.toString().trim();
	}

	private void registrarMermaReal(MermaRequest request) {
		try {
			MermaResultDTO result = services.mermaFacade.registrarMerma(request);

			ticketSession.clear();
			cobroSession.clear();
			centerPanel.getNombrePedidoPanel().clear();
			centerPanel.getPagoPanel().clear();
			centerPanel.clearCustomizationData();
			centerPanel.showCatalogo();
			refreshAll();

			JOptionPane.showMessageDialog(this, "Merma registrada correctamente.\n\nID merma: " + result.getIdMerma(),
					"Merma registrada", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();

			String mensaje = "No se pudo registrar la merma.";
			if (e.getMessage() != null) {
				mensaje += "\n\n" + e.getMessage();
			}
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				mensaje += "\n\nCausa: " + e.getCause().getMessage();
			}

			JOptionPane.showMessageDialog(this, mensaje, "Error al registrar merma", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void onMerma() {
	    if (!isAdminActual()) {
	        JOptionPane.showMessageDialog(
	                this,
	                "Solo administradores o encargados pueden registrar mermas.",
	                "Acceso denegado",
	                JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    }

	    MermaDialog dialog = new MermaDialog(this);
	    MermaDialogResult result = dialog.showDialog();

	    if (result == null || !result.isConfirmed()) {
	        return;
	    }

	    // ocultamos la pantalla actual de ventas
	    this.setVisible(false);

	    VentasFrame mermaFrame = new VentasFrame(
	            onLogoutNavigateAction,
	            () -> {
	                // al salir del modo merma, reabrimos esta misma pantalla
	                this.setVisible(true);
	                this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	                this.toFront();
	            },
	            services,
	            ModoOperacion.MERMA,
	            result
	    );

	    mermaFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    mermaFrame.setVisible(true);
	}

	private List<MermaExtraRequest> buildMermaExtras(TicketItem item) {
		List<MermaExtraRequest> out = new ArrayList<>();

		if (item.getExtras() == null || item.getExtras().isEmpty()) {
			return out;
		}

		for (TicketExtra extra : item.getExtras()) {
			if (extra == null)
				continue;

			MermaExtraRequest dto = new MermaExtraRequest();
			dto.setIdExtra(extra.getIdExtra());
			dto.setNombreExtra(extra.getNombre());
			dto.setTipoExtra(extra.getTipo());
			dto.setPrecioExtra(safe(extra.getPrecio()));
			dto.setCantidad(BigDecimal.ONE);
			out.add(dto);
		}

		return out;
	}

	private List<MermaPersonalizacionRequest> buildMermaPersonalizaciones(TicketItem item) {
		List<MermaPersonalizacionRequest> out = new ArrayList<>();

		if (item.getPersonalizaciones() == null || item.getPersonalizaciones().isEmpty()) {
			return out;
		}

		for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
			if (p == null)
				continue;

			MermaPersonalizacionRequest dto = new MermaPersonalizacionRequest();
			dto.setIdPersonalizacion(p.getIdPersonalizacion());
			dto.setNombrePersonalizacion(p.getNombre());
			dto.setTipoPersonalizacion(p.getTipo());
			dto.setPrecioPersonalizacion(safe(p.getPrecio()));
			out.add(dto);
		}

		return out;
	}

	private void clearAfterMerma() {
		if (ticketSession != null) {
			ticketSession.clear();
		}

		if (ticketPanel != null) {
			ticketPanel.refreshFromTicket();
		}

		if (customizationPanel != null) {
			customizationPanel.refresh();
		}

		if (bottomBarPanel != null) {
			bottomBarPanel.refresh();
		}

		if (centerPanel != null) {
			centerPanel.showCatalogo();
		}
	}

	private String askRequiredValue(String title, String message, String suggestedValue) {
		while (true) {
			Object value = JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE, null, null,
					suggestedValue);

			if (value == null) {
				return null;
			}

			String normalized = value.toString().trim();
			if (!normalized.isEmpty()) {
				return normalized;
			}

			JOptionPane.showMessageDialog(this, "Este campo es obligatorio.", title, JOptionPane.WARNING_MESSAGE);
		}
	}

	private void applyDefaultTipoCafeIfExists(int itemIndex, int idProducto) {
		List<TipoCafeDTO> tiposCafe = services.productoPersonalizacionService.getTiposCafeByProducto(idProducto);

		if (tiposCafe == null || tiposCafe.isEmpty()) {
			return;
		}

		for (TipoCafeDTO tipoCafe : tiposCafe) {
			if (tipoCafe != null && tipoCafe.isPorDefecto()) {
				ticketSession.changeTipoCafe(itemIndex, tipoCafe);
				return;
			}
		}
	}
}

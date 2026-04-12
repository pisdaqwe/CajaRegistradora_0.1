package service;

import config.DbPool;
import dao.VentaRegistroDao;
import dao.VentaRegistroDao.VentaItemsPersistidosResult;
import dtoS.RegistrarVentaComboItemRequest;
import dtoS.RegistrarVentaComboRequest;
import dtoS.RegistrarVentaDescuentoRequest;
import dtoS.RegistrarVentaItemRequest;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio de negocio para registrar una venta.
 *
 * DISEÑO CORRECTO:
 * - el Service valida reglas de negocio
 * - el Service abre/cierra la transacción
 * - el Service orquesta otros services
 * - el DAO solo inserta/lee/escribe en BD
 *
 * Esta versión ya integra:
 * - persistencia de venta
 * - stock de ingredientes
 * - movimientos de stock
 */
public class VentaService {

    private final VentaRegistroDao ventaRegistroDao;
    private final VentaStockIngredienteService ventaStockIngredienteService;

    public VentaService(VentaRegistroDao ventaRegistroDao,
                        VentaStockIngredienteService ventaStockIngredienteService) {
        if (ventaRegistroDao == null) {
            throw new IllegalArgumentException("ventaRegistroDao no puede ser null");
        }
        if (ventaStockIngredienteService == null) {
            throw new IllegalArgumentException("ventaStockIngredienteService no puede ser null");
        }

        this.ventaRegistroDao = ventaRegistroDao;
        this.ventaStockIngredienteService = ventaStockIngredienteService;
    }

    /**
     * Registra una venta completa.
     *
     * Flujo:
     * 1. validar request
     * 2. abrir conexión y transacción
     * 3. insertar venta
     * 4. insertar items y extras
     * 5. resolver receta final / validar stock ingredientes / descontar / movimientos
     * 6. insertar combos
     * 7. insertar descuento
     * 8. descontar stock_producto
     * 9. insertar pago
     * 10. insertar ticket_json
     * 11. commit
     */
    public RegistrarVentaResultDTO registrarVenta(RegistrarVentaRequest request) {
        validarRequest(request);

        try (Connection con = DbPool.getConnection()) {
            con.setAutoCommit(false);

            try {
                int idVenta = ventaRegistroDao.insertVenta(con, request);

                VentaItemsPersistidosResult itemsResult =
                        ventaRegistroDao.insertVentaItemsYExtras(con, idVenta, request.getItems());

                // =====================================================
                // NUEVO BLOQUE: stock de ingredientes
                // =====================================================
                procesarStockIngredientesVenta(
                        con,
                        idVenta,
                        request,
                        itemsResult.ticketIndexToVentaItemId()
                );

                ventaRegistroDao.insertVentaCombos(
                        con,
                        idVenta,
                        request.getCombos(),
                        itemsResult.ticketIndexToVentaItemId()
                );

                ventaRegistroDao.insertVentaDescuento(
                        con,
                        idVenta,
                        request.getDescuento()
                );

                ventaRegistroDao.descontarStockProductos(con, request);

                int idPago = ventaRegistroDao.insertPago(con, idVenta, request);

                int idTicketJson = ventaRegistroDao.insertTicketJson(
                        con,
                        idVenta,
                        request,
                        itemsResult.ticketIndexToVentaItemId()
                );

                con.commit();

                RegistrarVentaResultDTO result = new RegistrarVentaResultDTO();
                result.setIdVenta(idVenta);
                result.setIdPago(idPago);
                result.setIdTicketJson(idTicketJson);
                result.setItemsPersistidos(itemsResult.itemsPersistidos());

                return result;

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("Error registrando la venta completa.", e);
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir la conexión para registrar la venta.", e);
        }
    }

    /**
     * Procesa el bloque de stock de ingredientes por cada item persistido.
     *
     * IMPORTANTE:
     * - usa el mismo índice del ticket/request
     * - toma el id_item real ya insertado
     * - genera referencias de movimiento por item
     */
    private void procesarStockIngredientesVenta(Connection con,
                                                int idVenta,
                                                RegistrarVentaRequest request,
                                                Map<Integer, Integer> ticketIndexToVentaItemId) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return;
        }

        for (int i = 0; i < request.getItems().size(); i++) {
            RegistrarVentaItemRequest item = request.getItems().get(i);
            if (item == null) {
                continue;
            }

            Integer idItemPersistido = ticketIndexToVentaItemId.get(i);

            String referenciaMovimiento = "VENTA:" + idVenta
                    + " ITEM:" + (idItemPersistido != null ? idItemPersistido : "SIN_ID");

            String motivoMovimiento = "CONSUMO_VENTA";

            ventaStockIngredienteService.procesarItemVenta(
                    con,
                    request.getIdSucursal(),
                    item,
                    referenciaMovimiento,
                    motivoMovimiento
            );
        }
    }

    // =====================================================
    // VALIDACIÓN
    // =====================================================

    private void validarRequest(RegistrarVentaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request de venta no puede ser null.");
        }

        if (request.getIdSesion() <= 0) {
            throw new IllegalArgumentException("La venta requiere una sesión de caja válida.");
        }

        if (request.getIdSucursal() <= 0) {
            throw new IllegalArgumentException("La venta requiere una sucursal válida.");
        }

        if (request.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("La venta requiere un usuario válido.");
        }

        if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total de la venta debe ser mayor que 0.");
        }

        if (request.getMetodoPago() == null) {
            throw new IllegalArgumentException("Debe indicarse un método de pago.");
        }

        if (request.getMontoPagado() == null || request.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor que 0.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un item.");
        }

        if (request.getMontoPagado().compareTo(request.getTotal()) < 0) {
            throw new IllegalArgumentException("El monto pagado no puede ser menor que el total.");
        }

        validarItems(request);
        validarCombos(request);
        validarDescuento(request);
    }

    private void validarItems(RegistrarVentaRequest request) {
        List<RegistrarVentaItemRequest> items = request.getItems();

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un item.");
        }

        BigDecimal sumaSubtotalFinal = BigDecimal.ZERO;

        for (RegistrarVentaItemRequest item : items) {
            if (item == null) {
                throw new IllegalArgumentException("La venta contiene un item null.");
            }

            if (item.getIdProducto() <= 0) {
                throw new IllegalArgumentException("Todo item debe tener un idProducto válido.");
            }

            // NUEVO: ahora el tamaño es obligatorio para resolver receta/stock
            if (item.getIdTamano() <= 0) {
                throw new IllegalArgumentException("Todo item debe tener un idTamano válido.");
            }

            if (item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Todo item debe tener cantidad mayor que 0.");
            }

            if (item.getPrecioUnitario() == null || item.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("precioUnitario inválido en item " + item.getNombreProducto());
            }

            if (item.getSubtotalBruto() == null || item.getSubtotalBruto().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("subtotalBruto inválido en item " + item.getNombreProducto());
            }

            if (item.getImporteDescuentoLinea() == null
                    || item.getImporteDescuentoLinea().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("importeDescuentoLinea inválido en item " + item.getNombreProducto());
            }

            if (item.getSubtotalFinal() == null || item.getSubtotalFinal().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("subtotalFinal inválido en item " + item.getNombreProducto());
            }

            if (item.getImporteDescuentoLinea().compareTo(item.getSubtotalBruto()) > 0) {
                throw new IllegalArgumentException(
                        "El importeDescuentoLinea no puede ser mayor que subtotalBruto en item "
                                + item.getNombreProducto()
                );
            }

            BigDecimal subtotalFinalEsperado = item.getSubtotalBruto()
                    .subtract(item.getImporteDescuentoLinea());

            if (subtotalFinalEsperado.compareTo(item.getSubtotalFinal()) != 0) {
                throw new IllegalArgumentException(
                        "subtotalFinal no cuadra con subtotalBruto - importeDescuentoLinea en item "
                                + item.getNombreProducto()
                );
            }

            if (item.getIva() == null || item.getIva().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("IVA inválido en item " + item.getNombreProducto());
            }

            sumaSubtotalFinal = sumaSubtotalFinal.add(item.getSubtotalFinal());
        }

        if (sumaSubtotalFinal.compareTo(request.getTotal()) != 0) {
            throw new IllegalArgumentException(
                    "La suma de subtotalFinal de los items no coincide con el total de la venta. "
                            + "Esperado: " + request.getTotal()
                            + ", calculado: " + sumaSubtotalFinal
            );
        }
    }

    private void validarCombos(RegistrarVentaRequest request) {
        List<RegistrarVentaComboRequest> combos = request.getCombos();
        if (combos == null || combos.isEmpty()) {
            return;
        }

        int totalItems = request.getItems().size();
        Set<Integer> ticketIndexesUsadosEnCombos = new HashSet<>();

        for (RegistrarVentaComboRequest combo : combos) {
            if (combo == null) {
                throw new IllegalArgumentException("La lista de combos contiene un elemento null.");
            }

            if (combo.getIdCombo() <= 0) {
                throw new IllegalArgumentException("Todo combo aplicado debe tener un idCombo válido.");
            }

            if (combo.getNombreCombo() == null || combo.getNombreCombo().isBlank()) {
                throw new IllegalArgumentException("Todo combo aplicado debe tener nombre.");
            }

            if (combo.getTipoCombo() == null) {
                throw new IllegalArgumentException("Todo combo aplicado debe tener tipo.");
            }

            if (combo.getPrecioOriginal() == null || combo.getPrecioOriginal().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("precioOriginal del combo inválido.");
            }

            if (combo.getPrecioFinal() == null || combo.getPrecioFinal().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("precioFinal del combo inválido.");
            }

            if (combo.getAhorroTotal() == null || combo.getAhorroTotal().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("ahorroTotal del combo inválido.");
            }

            if (combo.getPrecioOriginal().compareTo(combo.getPrecioFinal()) < 0) {
                throw new IllegalArgumentException(
                        "El precio original del combo no puede ser menor que el precio final."
                );
            }

            BigDecimal ahorroEsperado = combo.getPrecioOriginal().subtract(combo.getPrecioFinal());
            if (ahorroEsperado.compareTo(combo.getAhorroTotal()) != 0) {
                throw new IllegalArgumentException(
                        "El ahorroTotal del combo no cuadra con precioOriginal - precioFinal. Combo: "
                                + combo.getNombreCombo()
                );
            }

            List<RegistrarVentaComboItemRequest> comboItems = combo.getItems();
            if (comboItems == null || comboItems.isEmpty()) {
                throw new IllegalArgumentException(
                        "Todo combo aplicado debe tener al menos un item asociado."
                );
            }

            BigDecimal sumaOriginal = BigDecimal.ZERO;
            BigDecimal sumaDescuento = BigDecimal.ZERO;
            BigDecimal sumaFinal = BigDecimal.ZERO;

            Set<Integer> indexesDentroDelMismoCombo = new HashSet<>();

            for (RegistrarVentaComboItemRequest comboItem : comboItems) {
                if (comboItem == null) {
                    throw new IllegalArgumentException("Un combo contiene un item null.");
                }

                int ticketItemIndex = comboItem.getTicketItemIndex();
                if (ticketItemIndex < 0 || ticketItemIndex >= totalItems) {
                    throw new IllegalArgumentException(
                            "ticketItemIndex fuera de rango en combo: " + ticketItemIndex
                    );
                }

                if (!indexesDentroDelMismoCombo.add(ticketItemIndex)) {
                    throw new IllegalArgumentException(
                            "Un combo no puede repetir el mismo ticketItemIndex: " + ticketItemIndex
                    );
                }

                if (!ticketIndexesUsadosEnCombos.add(ticketItemIndex)) {
                    throw new IllegalArgumentException(
                            "Un item del ticket no puede pertenecer a más de un combo. ticketItemIndex="
                                    + ticketItemIndex
                    );
                }

                if (comboItem.getSubtotalOriginalItem() == null
                        || comboItem.getSubtotalOriginalItem().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("subtotalOriginalItem inválido en combo.");
                }

                if (comboItem.getDescuentoAsignado() == null
                        || comboItem.getDescuentoAsignado().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("descuentoAsignado inválido en combo.");
                }

                if (comboItem.getSubtotalFinalItem() == null
                        || comboItem.getSubtotalFinalItem().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("subtotalFinalItem inválido en combo.");
                }

                BigDecimal finalEsperado = comboItem.getSubtotalOriginalItem()
                        .subtract(comboItem.getDescuentoAsignado());

                if (finalEsperado.compareTo(comboItem.getSubtotalFinalItem()) != 0) {
                    throw new IllegalArgumentException(
                            "subtotalFinalItem no cuadra con subtotalOriginalItem - descuentoAsignado."
                    );
                }

                sumaOriginal = sumaOriginal.add(comboItem.getSubtotalOriginalItem());
                sumaDescuento = sumaDescuento.add(comboItem.getDescuentoAsignado());
                sumaFinal = sumaFinal.add(comboItem.getSubtotalFinalItem());
            }

            if (sumaOriginal.compareTo(combo.getPrecioOriginal()) != 0) {
                throw new IllegalArgumentException(
                        "La suma de subtotalOriginalItem no coincide con precioOriginal del combo: "
                                + combo.getNombreCombo()
                );
            }

            if (sumaDescuento.compareTo(combo.getAhorroTotal()) != 0) {
                throw new IllegalArgumentException(
                        "La suma de descuentoAsignado no coincide con ahorroTotal del combo: "
                                + combo.getNombreCombo()
                );
            }

            if (sumaFinal.compareTo(combo.getPrecioFinal()) != 0) {
                throw new IllegalArgumentException(
                        "La suma de subtotalFinalItem no coincide con precioFinal del combo: "
                                + combo.getNombreCombo()
                );
            }
        }
    }

    private void validarDescuento(RegistrarVentaRequest request) {
        RegistrarVentaDescuentoRequest descuento = request.getDescuento();
        if (descuento == null) {
            return;
        }

        if (descuento.getIdDescuento() <= 0) {
            throw new IllegalArgumentException("El descuento aplicado debe tener idDescuento válido.");
        }

        if (descuento.getIdUsuarioAplica() <= 0) {
            throw new IllegalArgumentException("El descuento aplicado debe tener idUsuarioAplica válido.");
        }

        if (descuento.getTipoDescuentoAplicado() == null || descuento.getTipoDescuentoAplicado().isBlank()) {
            throw new IllegalArgumentException("El descuento aplicado debe tener tipo.");
        }

        if (descuento.getValorDescuentoAplicado() == null
                || descuento.getValorDescuentoAplicado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El descuento aplicado debe tener valor válido.");
        }

        if (descuento.getImporteBase() == null || descuento.getImporteBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El descuento aplicado debe tener importeBase válido.");
        }

        if (descuento.getImporteDescuento() == null
                || descuento.getImporteDescuento().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento aplicado debe tener importeDescuento válido.");
        }

        if (descuento.getImporteDescuento().compareTo(descuento.getImporteBase()) > 0) {
            throw new IllegalArgumentException("El importeDescuento no puede ser mayor que el importeBase.");
        }

        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            throw new IllegalArgumentException("No se permite registrar una venta con combos y descuento a la vez.");
        }

        BigDecimal totalEsperado = descuento.getImporteBase().subtract(descuento.getImporteDescuento());
        if (totalEsperado.compareTo(request.getTotal()) != 0) {
            throw new IllegalArgumentException(
                    "El total de la venta no cuadra con el descuento aplicado. Esperado: "
                            + totalEsperado + ", recibido: " + request.getTotal()
            );
        }
    }
}
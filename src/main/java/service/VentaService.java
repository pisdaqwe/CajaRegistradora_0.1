package service;

import dao.VentaRegistroDao;
import dtoS.RegistrarVentaComboItemRequest;
import dtoS.RegistrarVentaComboRequest;
import dtoS.RegistrarVentaDescuentoRequest;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de negocio para registrar una venta.
 *
 * En esta arquitectura:
 * - el Service valida reglas de negocio
 * - el DAO se encarga de abrir la conexión y ejecutar toda la transacción completa
 */
public class VentaService {

    private final VentaRegistroDao ventaRegistroDao;

    public VentaService(VentaRegistroDao ventaRegistroDao) {
        this.ventaRegistroDao = ventaRegistroDao;
    }

    /**
     * Registra una venta completa.
     */
    public RegistrarVentaResultDTO registrarVenta(RegistrarVentaRequest request) {
        validarRequest(request);
        return ventaRegistroDao.registrarVentaCompleta(request);
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

        validarCombos(request);
        validarDescuento(request);
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

                BigDecimal finalEsperado = comboItem.getSubtotalOriginalItem().subtract(comboItem.getDescuentoAsignado());
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
package dtoS;

import java.math.BigDecimal;

/**
 * DTO que representa un item del ticket que participa en un combo.
 *
 * IMPORTANTE:
 * - ticketItemIndex es la posición del item en TicketSession al momento del cobro
 * - más adelante, en VentaRegistroDao, se traducirá a id_item real de venta_item
 */
public class RegistrarVentaComboItemRequest {

    /**
     * Índice del item dentro de TicketSession / lista de items del request.
     */
    private int ticketItemIndex;

    /**
     * Subtotal original del item antes de aplicar el combo.
     * Aquí guardaremos la base del item que entra en el combo.
     */
    private BigDecimal subtotalOriginalItem = BigDecimal.ZERO;

    /**
     * Descuento económico asignado a este item dentro del combo.
     */
    private BigDecimal descuentoAsignado = BigDecimal.ZERO;

    /**
     * Subtotal final del item dentro del combo tras aplicar su parte del descuento.
     */
    private BigDecimal subtotalFinalItem = BigDecimal.ZERO;

    public int getTicketItemIndex() {
        return ticketItemIndex;
    }

    public void setTicketItemIndex(int ticketItemIndex) {
        this.ticketItemIndex = ticketItemIndex;
    }

    public BigDecimal getSubtotalOriginalItem() {
        return subtotalOriginalItem;
    }

    public void setSubtotalOriginalItem(BigDecimal subtotalOriginalItem) {
        this.subtotalOriginalItem = subtotalOriginalItem != null ? subtotalOriginalItem : BigDecimal.ZERO;
    }

    public BigDecimal getDescuentoAsignado() {
        return descuentoAsignado;
    }

    public void setDescuentoAsignado(BigDecimal descuentoAsignado) {
        this.descuentoAsignado = descuentoAsignado != null ? descuentoAsignado : BigDecimal.ZERO;
    }

    public BigDecimal getSubtotalFinalItem() {
        return subtotalFinalItem;
    }

    public void setSubtotalFinalItem(BigDecimal subtotalFinalItem) {
        this.subtotalFinalItem = subtotalFinalItem != null ? subtotalFinalItem : BigDecimal.ZERO;
    }
}
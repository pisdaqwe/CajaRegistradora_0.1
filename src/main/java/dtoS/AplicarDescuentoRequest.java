package dtoS;

import java.math.BigDecimal;

/**
 * DTO que encapsula los datos necesarios para solicitar
 * la aplicación de un descuento al ticket actual.
 *
 * Esta clase se usa como entrada del DescuentoService.
 *
 * La idea es que la UI NO pase objetos Swing ni dependa
 * directamente de la lógica de negocio.
 */
public class AplicarDescuentoRequest {

    /**
     * ID del usuario actual que está operando la caja.
     */
    private Integer idUsuarioActual;

    /**
     * Código promocional introducido por el cajero.
     *
     * Se usa en el flujo de descuentos promocionales.
     * Puede ser null o vacío si no aplica.
     */
    private String codigoPromocional;

    /**
     * Código o identificador del empleado beneficiario.
     *
     * Normalmente aquí irá el número de empleado / usuario
     * usado para validar descuentos de empleado.
     */
    private String codigoEmpleado;

    /**
     * Subtotal actual del ticket antes de aplicar descuento.
     */
    private BigDecimal subtotalTicket;

    /**
     * Indica si el ticket está vacío.
     */
    private boolean ticketVacio;

    /**
     * Indica si el ticket ya tiene un descuento aplicado.
     */
    private boolean yaTieneDescuento;

    /**
     * Indica si el ticket tiene algún combo aplicado.
     */
    private boolean tieneComboAplicado;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public Integer getIdUsuarioActual() {
        return idUsuarioActual;
    }

    public void setIdUsuarioActual(Integer idUsuarioActual) {
        this.idUsuarioActual = idUsuarioActual;
    }

    public String getCodigoPromocional() {
        return codigoPromocional;
    }

    public void setCodigoPromocional(String codigoPromocional) {
        this.codigoPromocional = codigoPromocional;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public BigDecimal getSubtotalTicket() {
        return subtotalTicket;
    }

    public void setSubtotalTicket(BigDecimal subtotalTicket) {
        this.subtotalTicket = subtotalTicket;
    }

    public boolean isTicketVacio() {
        return ticketVacio;
    }

    public void setTicketVacio(boolean ticketVacio) {
        this.ticketVacio = ticketVacio;
    }

    public boolean isYaTieneDescuento() {
        return yaTieneDescuento;
    }

    public void setYaTieneDescuento(boolean yaTieneDescuento) {
        this.yaTieneDescuento = yaTieneDescuento;
    }

    public boolean isTieneComboAplicado() {
        return tieneComboAplicado;
    }

    public void setTieneComboAplicado(boolean tieneComboAplicado) {
        this.tieneComboAplicado = tieneComboAplicado;
    }
}

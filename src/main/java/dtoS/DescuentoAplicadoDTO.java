package dtoS;

import model.DescuentoAplicado;

/**
 * DTO de respuesta del DescuentoService.
 *
 * Indica si la operación fue válida o no,
 * devuelve un mensaje legible para la UI
 * y, si todo fue correcto, devuelve el descuento
 * ya calculado y listo para aplicar en TicketSession.
 */
public class DescuentoAplicadoDTO {

    /**
     * Indica si el descuento pudo aplicarse correctamente.
     */
    private boolean valido;

    /**
     * Mensaje legible para mostrar en UI.
     *
     * Ejemplos:
     * - "Descuento aplicado correctamente."
     * - "El código promocional no existe."
     * - "No se puede aplicar descuento a un ticket con combo."
     */
    private String mensaje;

    /**
     * Objeto de dominio ya calculado y preparado
     * para guardarse en TicketSession.
     *
     * Será null si la validación falla.
     */
    private DescuentoAplicado descuentoAplicado;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public DescuentoAplicado getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(DescuentoAplicado descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
}
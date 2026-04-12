package dtoS;

import java.math.BigDecimal;

/**
 * DTO que representa una personalización aplicada
 * a un item de venta.
 *
 * Lo necesitamos para que el flujo de venta conserve
 * la información suficiente para resolver la receta final.
 */
public class RegistrarVentaPersonalizacionRequest {

    /**
     * ID real de la personalización.
     */
    private int idPersonalizacion;

    /**
     * Nombre visible.
     */
    private String nombrePersonalizacion;

    /**
     * Precio aplicado si lo tuviera.
     */
    private BigDecimal precioPersonalizacion;

    public int getIdPersonalizacion() {
        return idPersonalizacion;
    }

    public void setIdPersonalizacion(int idPersonalizacion) {
        this.idPersonalizacion = idPersonalizacion;
    }

    public String getNombrePersonalizacion() {
        return nombrePersonalizacion;
    }

    public void setNombrePersonalizacion(String nombrePersonalizacion) {
        this.nombrePersonalizacion = nombrePersonalizacion;
    }

    public BigDecimal getPrecioPersonalizacion() {
        return precioPersonalizacion;
    }

    public void setPrecioPersonalizacion(BigDecimal precioPersonalizacion) {
        this.precioPersonalizacion = precioPersonalizacion;
    }
}

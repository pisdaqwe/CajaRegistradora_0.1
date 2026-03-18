package dtoS;

import java.math.BigDecimal;

/**
 * DTO que representa un extra asociado a un item de venta.
 *
 * Ejemplo:
 * - Extra shot
 * - Sirope vainilla
 * - Topping caramelo
 *
 * Esta clase NO es una entidad de BD.
 * Solo se usa para transportar datos desde la UI
 * hacia la capa Service al registrar una venta.
 */
public class RegistrarVentaExtraRequest {

    /**
     * ID real del extra en base de datos.
     */
    private int idExtra;

    /**
     * Nombre visible del extra.
     */
    private String nombreExtra;

    /**
     * Precio aplicado a este extra.
     */
    private BigDecimal precioExtra;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public int getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(int idExtra) {
        this.idExtra = idExtra;
    }

    public String getNombreExtra() {
        return nombreExtra;
    }

    public void setNombreExtra(String nombreExtra) {
        this.nombreExtra = nombreExtra;
    }

    public BigDecimal getPrecioExtra() {
        return precioExtra;
    }

    public void setPrecioExtra(BigDecimal precioExtra) {
        this.precioExtra = precioExtra;
    }
}
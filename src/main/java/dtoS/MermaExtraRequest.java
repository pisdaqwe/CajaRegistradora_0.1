package dtoS;

import java.math.BigDecimal;

/**
 * Representa un extra asociado a una línea de merma.
 *
 * Sirve para:
 * - guardar snapshot de lo que llevaba el producto mermado
 * - persistirlo si más adelante decides usar tabla merma_item_extra
 * - reconstruir configuracion_json y descripcion_snapshot
 *
 * IMPORTANTE:
 * - no implica por sí solo descuento de stock
 * - el efecto real sobre receta lo resolverá RecipeResolverService
 *   usando el idExtra y las reglas configuradas
 */
public class MermaExtraRequest {

    /**
     * ID real del extra.
     */
    private int idExtra;

    /**
     * Nombre visible del extra.
     */
    private String nombreExtra;

    /**
     * Tipo del extra.
     *
     * Ejemplos:
     * - MILK
     * - SHOT
     * - SYRUP
     * - TOPPING
     * - FOOD_EXTRA
     */
    private String tipoExtra;

    /**
     * Precio snapshot del extra.
     *
     * En merma no se cobra, pero puede ser útil
     * para auditoría o trazabilidad futura.
     */
    private BigDecimal precioExtra = BigDecimal.ZERO;

    /**
     * Cantidad lógica del extra.
     *
     * Normalmente será 1.
     * Se deja preparada por si más adelante
     * quieres soportar repetición explícita.
     */
    private BigDecimal cantidad = BigDecimal.ONE;

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

    public String getTipoExtra() {
        return tipoExtra;
    }

    public void setTipoExtra(String tipoExtra) {
        this.tipoExtra = tipoExtra;
    }

    public BigDecimal getPrecioExtra() {
        return precioExtra != null ? precioExtra : BigDecimal.ZERO;
    }

    public void setPrecioExtra(BigDecimal precioExtra) {
        this.precioExtra = precioExtra != null ? precioExtra : BigDecimal.ZERO;
    }

    public BigDecimal getCantidad() {
        return cantidad != null ? cantidad : BigDecimal.ONE;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad != null ? cantidad : BigDecimal.ONE;
    }
}
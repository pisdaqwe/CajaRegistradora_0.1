package dtoS;

import java.math.BigDecimal;

/**
 * Representa una personalización aplicada a una línea de merma.
 *
 * Ejemplos:
 * - Sin espuma
 * - Extra caliente
 * - Cortado por la mitad
 * - Bien tostado
 *
 * Sirve para:
 * - guardar snapshot de cómo estaba configurado el producto mermado
 * - reconstruir configuracion_json y descripcion_snapshot
 * - permitir que RecipeResolverService aplique reglas de receta
 *   cuando la personalización tenga impacto real en stock
 *
 * IMPORTANTE:
 * - no todas las personalizaciones afectan al stock
 * - el efecto real se decide por las reglas de
 *   personalizacion_receta_regla
 */
public class MermaPersonalizacionRequest {

    /**
     * ID real de la personalización.
     */
    private int idPersonalizacion;

    /**
     * Nombre visible de la personalización.
     */
    private String nombrePersonalizacion;

    /**
     * Tipo de personalización.
     *
     * Ejemplos:
     * - PREP
     * - FOOD_PREP
     * - otros tipos futuros
     */
    private String tipoPersonalizacion;

    /**
     * Precio snapshot de la personalización.
     *
     * En merma no se cobra, pero puede servir
     * para trazabilidad y auditoría.
     */
    private BigDecimal precioPersonalizacion = BigDecimal.ZERO;

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

    public String getTipoPersonalizacion() {
        return tipoPersonalizacion;
    }

    public void setTipoPersonalizacion(String tipoPersonalizacion) {
        this.tipoPersonalizacion = tipoPersonalizacion;
    }

    public BigDecimal getPrecioPersonalizacion() {
        return precioPersonalizacion != null ? precioPersonalizacion : BigDecimal.ZERO;
    }

    public void setPrecioPersonalizacion(BigDecimal precioPersonalizacion) {
        this.precioPersonalizacion = precioPersonalizacion != null
                ? precioPersonalizacion
                : BigDecimal.ZERO;
    }
}

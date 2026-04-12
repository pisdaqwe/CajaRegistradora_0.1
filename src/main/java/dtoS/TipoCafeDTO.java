package dtoS;

import java.math.BigDecimal;

/**
 * DTO que representa un tipo de café seleccionable
 * dentro de la customización de un producto.
 *
 * OBJETIVO:
 * - transportar desde BD a la UI la información del café
 * - permitir pintar el bloque "CAFÉ" en customización
 * - indicar si el café lleva suplemento
 * - indicar si es el café por defecto del producto
 *
 * IMPORTANTE:
 * - esto NO es un extra
 * - esto NO es una personalización PREP
 * - esto representa una elección base del item
 *
 * CAMPOS NUEVOS IMPORTANTES:
 * - suplementoPrecio:
 *   importe extra que se sumará al item si se elige este café
 *
 * - porDefecto:
 *   indica si este tipo de café es el default para ese producto
 *
 * - idIngrediente:
 *   deja preparado el modelo para la futura fase de recetas/stock,
 *   donde habrá que resolver qué ingrediente real se descuenta
 */
public class TipoCafeDTO {

    /**
     * ID del tipo de café en la tabla tipo_cafe.
     */
    private int idTipoCafe;

    /**
     * Nombre visible del café.
     *
     * Ejemplos:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     */
    private String nombre;

    /**
     * ID del ingrediente real asociado a este café.
     *
     * Ejemplos:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     *
     * Se usará más adelante en el motor de recetas/stock.
     */
    private int idIngrediente;

    /**
     * Familia del café.
     *
     * Ejemplos:
     * - ESPRESSO
     * - COLD_BREW
     *
     * Lo dejamos como String porque así ya encaja
     * con lo que hoy tienes en BD.
     */
    private String familiaCafe;

    /**
     * Indica si este tipo de café es descafeinado.
     */
    private boolean esDescafeinado;

    /**
     * Suplemento económico que añade este café
     * al subtotal del item cuando se selecciona.
     *
     * Ejemplos:
     * - 0.00 para Espresso normal
     * - 0.30 para Colombia Campaña
     * - 0.50 para Etiopía Campaña
     */
    private BigDecimal suplementoPrecio;

    /**
     * Indica si este café es el café por defecto
     * para el producto concreto que se está customizando.
     *
     * OJO:
     * este valor viene realmente de producto_tipo_cafe,
     * no solo de tipo_cafe.
     */
    private boolean porDefecto;

    /**
     * Indica si está activo y se puede ofrecer en UI.
     */
    private boolean activo;

    /**
     * Orden visual para mostrarlo en la UI.
     */
    private int orden;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public int getIdTipoCafe() {
        return idTipoCafe;
    }

    public void setIdTipoCafe(int idTipoCafe) {
        this.idTipoCafe = idTipoCafe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getFamiliaCafe() {
        return familiaCafe;
    }

    public void setFamiliaCafe(String familiaCafe) {
        this.familiaCafe = familiaCafe;
    }

    public boolean isEsDescafeinado() {
        return esDescafeinado;
    }

    public void setEsDescafeinado(boolean esDescafeinado) {
        this.esDescafeinado = esDescafeinado;
    }

    public BigDecimal getSuplementoPrecio() {
        return suplementoPrecio;
    }

    public void setSuplementoPrecio(BigDecimal suplementoPrecio) {
        this.suplementoPrecio = suplementoPrecio;
    }

    public boolean isPorDefecto() {
        return porDefecto;
    }

    public void setPorDefecto(boolean porDefecto) {
        this.porDefecto = porDefecto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}

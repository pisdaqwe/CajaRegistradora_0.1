
package enums;

/**
 * Cards disponibles dentro del panel central de customización.
 *
 * USO:
 * - el panel lateral decide a qué card navegar
 * - VentasCenterPanel / CustomizationCenterPanel muestran la card activa
 *
 * AÑADIDO AHORA:
 * - CAFE
 *
 * OBJETIVO DEL CAMBIO:
 * - tener una card propia para seleccionar el tipo de café
 * - no mezclar el café con SHOTS, MILK o PREP
 */
public enum CustomizationCard {

    /**
     * NUEVO:
     * Card específica para el selector de tipo de café.
     *
     * Ejemplos de opciones:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     * - Espresso Etiopía Campaña
     */
    CAFE,

    /**
     * Shots extra del producto.
     */
    SHOTS,

    /**
     * Siropes.
     */
    SYRUPS,

    /**
     * Toppings.
     */
    TOPPINGS,

    /**
     * Tipo de leche.
     */
    MILK,

    /**
     * Preparaciones de bebida.
     */
    PREP,

    /**
     * Preparaciones de comida.
     */
    PREP_FOOD,

    /**
     * Extras de comida.
     */
    OPCIONES_FOOD
}
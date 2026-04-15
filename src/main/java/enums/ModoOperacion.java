package enums;

/**
 * Define el modo operativo del frame principal reutilizado
 * para el flujo comercial normal y para el flujo de merma.
 *
 * IDEA:
 * - VENTA: comportamiento TPV normal
 * - MERMA: comportamiento de desperdicio / pérdida,
 *          sin cobro ni pago
 *
 * Este enum permitirá que VentasFrame cambie:
 * - botones visibles
 * - textos
 * - flujo final
 * - validaciones
 */
public enum ModoOperacion {

    /**
     * Modo normal del TPV.
     *
     * Permite:
     * - cobrar
     * - descuentos
     * - combos
     * - pago
     * - ticket cliente
     * - persistencia de venta
     */
    VENTA,

    /**
     * Modo de registro de merma.
     *
     * Permite:
     * - construir producto como en ventas
     * - customizar
     * - usar ticket lateral
     * - registrar pérdida
     *
     * No permite:
     * - cobrar
     * - descuentos
     * - combos
     * - pago
     * - ticket cliente
     * - persistencia en venta/pago
     */
    MERMA
}
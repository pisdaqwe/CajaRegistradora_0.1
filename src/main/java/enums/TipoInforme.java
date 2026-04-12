package enums;

/**
 * Tipos de informe soportados por el explorador de informes.
 *
 * En esta primera fase todavía no ejecutan SQL real.
 * Se usan para:
 * - pintar la UI
 * - cambiar el gráfico demo
 * - cambiar la tabla demo
 * - preparar la futura conexión con InformesService
 */
public enum TipoInforme {

    RESUMEN_EJECUTIVO("Resumen ejecutivo"),
    INFORME_CAJA("Informe de caja"),
    VENTAS_POR_DIA("Ventas por día"),
    TICKET_MEDIO_POR_DIA("Ticket medio por día"),
    PAGOS_POR_METODO("Pagos por método"),
    PRODUCTOS_MAS_VENDIDOS("Productos más vendidos"),
    COMBOS_VENDIDOS("Combos vendidos"),
    DESCUENTOS("Descuentos"),
    DEVOLUCIONES("Devoluciones");

    private final String displayName;

    TipoInforme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

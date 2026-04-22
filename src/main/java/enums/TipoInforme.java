package enums;

public enum TipoInforme {

    RESUMEN_EJECUTIVO("Resumen ejecutivo", "Visión general del negocio", FamiliaInforme.VENTAS_TIEMPO),
    VENTAS_POR_DIA("Ventas por día", "Evolución diaria de ventas", FamiliaInforme.VENTAS_TIEMPO),
    VENTAS_POR_FRANJA_HORARIA("Ventas por franja horaria", "Concentración de ventas por hora", FamiliaInforme.VENTAS_TIEMPO),
    TICKET_MEDIO_POR_DIA("Ticket medio por día", "Valor medio por ticket y día", FamiliaInforme.VENTAS_TIEMPO),
    PAGOS_POR_METODO("Pagos por método", "Distribución de métodos de pago", FamiliaInforme.VENTAS_TIEMPO),
    VENTAS_NETAS_VS_DEVOLUCIONES("Ventas netas vs devoluciones", "Comparativa entre ventas y devoluciones", FamiliaInforme.VENTAS_TIEMPO),

    PRODUCTOS_MAS_VENDIDOS("Productos más vendidos", "Ranking de productos", FamiliaInforme.COMERCIAL),
    EXTRAS_MAS_VENDIDOS("Extras más vendidos", "Upselling y complementos", FamiliaInforme.COMERCIAL),
    COMBOS_VENDIDOS("Combos vendidos", "Impacto de combos y promociones", FamiliaInforme.COMERCIAL),
    DESCUENTOS_APLICADOS("Descuentos aplicados", "Control de descuentos", FamiliaInforme.COMERCIAL),
    DEVOLUCIONES_POR_PRODUCTO("Devoluciones por producto", "Productos con más devoluciones", FamiliaInforme.COMERCIAL),

    RANKING_EMPLEADOS_POR_VENTAS("Ranking empleados por ventas", "Rendimiento comercial del equipo", FamiliaInforme.EQUIPO),
    RANKING_EMPLEADOS_POR_EXTRAS("Ranking empleados por extras", "Capacidad de upselling por empleado", FamiliaInforme.EQUIPO),
    PRODUCTOS_VENDIDOS_POR_EMPLEADO("Productos vendidos por empleado", "Detalle comercial por empleado", FamiliaInforme.EQUIPO),

    VENTAS_POR_CAJA("Ventas por caja", "Rendimiento por caja", FamiliaInforme.OPERATIVA),
    VENTAS_POR_SESION_CAJA("Ventas por sesión de caja", "Resumen por sesión operativa", FamiliaInforme.OPERATIVA),
    TIEMPOS_POR_ESTACION("Tiempos por estación", "Carga y eficiencia operativa", FamiliaInforme.OPERATIVA),
    MERMA_POR_PERIODO("Merma por período", "Control de pérdidas", FamiliaInforme.OPERATIVA),
    MOVIMIENTOS_STOCK_AJUSTES("Movimientos de stock / ajustes", "Auditoría de inventario", FamiliaInforme.OPERATIVA),
    VENTAS_PRODUCTO_POR_EMPLEADO(
            "Ventas producto por empleado",
            "Muestra qué empleados han vendido determinados productos, con unidades e importe generado.",
            FamiliaInforme.EQUIPO
    ),
    RANKING_EMPLEADOS_POR_PRODUCTO(
            "Ranking empleados por producto",
            "Ordena a los empleados según las ventas realizadas de los productos seleccionados.",
            FamiliaInforme.EQUIPO
    ),
    VENTAS_EXTRA_POR_EMPLEADO(
            "Ventas extra por empleado",
            "Muestra qué empleados han vendido determinados extras, con frecuencia e importe generado.",
            FamiliaInforme.EQUIPO
    ),
    RANKING_EMPLEADOS_POR_EXTRA(
            "Ranking empleados por extra",
            "Ordena a los empleados según las ventas realizadas de los extras seleccionados.",
            FamiliaInforme.EQUIPO
    );
   
	private final String displayName;
    private final String shortDescription;
    private final FamiliaInforme familia;

    TipoInforme(String displayName, String shortDescription, FamiliaInforme familia) {
        this.displayName = displayName;
        this.shortDescription = shortDescription;
        this.familia = familia;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public FamiliaInforme getFamilia() {
        return familia;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
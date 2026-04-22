package util;

import dtoS.InformePdfExportRequest;
import dtoS.InformePdfKpiItem;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformeProductosVendidosResultDTO;
import dtoS.InformeExtrasVendidosResultDTO;
import dtoS.InformeCombosVendidosResultDTO;
import dtoS.InformeDescuentosAplicadosResultDTO;
import dtoS.InformeDevolucionesProductoResultDTO;
import dtoS.InformeRankingEmpleadosVentasResultDTO;
import dtoS.InformeRankingEmpleadosExtrasResultDTO;
import dtoS.InformeProductosPorEmpleadoResultDTO;
import dtoS.InformeVentasCajaResultDTO;
import dtoS.InformeVentasSesionCajaResultDTO;
import dtoS.InformeTiemposEstacionResultDTO;
import dtoS.InformeMermaPeriodoResultDTO;
import dtoS.InformeMovimientoStockResultDTO;
import dtoS.InformeVentasProductoEmpleadoResultDTO;
import dtoS.InformeRankingEmpleadosProductoResultDTO;
import dtoS.InformeVentasExtraEmpleadoResultDTO;
import dtoS.InformeRankingEmpleadosExtraResultDTO;
import enums.TipoInforme;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder responsable de transformar el resultado real del informe
 * en una lista de KPIs lista para exportar a PDF.
 *
 * Responsabilidades:
 * - Leer el InformePdfExportRequest.
 * - Detectar el tipo de informe.
 * - Convertir el DTO real en una lista de KPI simples:
 *      label + value
 *
 * IMPORTANTE:
 * - Esta clase NO pinta PDF.
 * - Esta clase NO consulta BD.
 * - Esta clase NO depende de Swing.
 *
 * Primera versión:
 * - Soporta solo TipoInforme.VENTAS_POR_DIA
 */
public class InformePdfKpiBuilder {

    /**
     * Punto de entrada principal.
     *
     * @param request request completo de exportación PDF
     * @return lista de KPIs ya formateados como texto
     */
    public List<InformePdfKpiItem> build(InformePdfExportRequest request) {
        Objects.requireNonNull(request, "request no puede ser null");
        request.validate();

        TipoInforme tipoInforme = request.getTipoInforme();

        return switch (tipoInforme) {
        case RESUMEN_EJECUTIVO -> buildResumenEjecutivoKpis(request);
        case VENTAS_POR_DIA -> buildVentasPorDiaKpis(request);
        case VENTAS_POR_FRANJA_HORARIA -> buildVentasPorFranjaKpis(request);
        case TICKET_MEDIO_POR_DIA -> buildTicketMedioPorDiaKpis(request);
        case PAGOS_POR_METODO -> buildPagosPorMetodoKpis(request);
        case VENTAS_NETAS_VS_DEVOLUCIONES -> buildVentasNetasVsDevolucionesKpis(request);
        case PRODUCTOS_MAS_VENDIDOS -> buildProductosMasVendidosKpis(request);
        case EXTRAS_MAS_VENDIDOS -> buildExtrasMasVendidosKpis(request);
        case COMBOS_VENDIDOS -> buildCombosVendidosKpis(request);
        case DESCUENTOS_APLICADOS -> buildDescuentosAplicadosKpis(request);
        case DEVOLUCIONES_POR_PRODUCTO -> buildDevolucionesProductoKpis(request);
        
        case RANKING_EMPLEADOS_POR_VENTAS -> buildRankingEmpleadosVentasKpis(request);
        case RANKING_EMPLEADOS_POR_EXTRAS -> buildRankingEmpleadosExtrasKpis(request);
        case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> buildProductosPorEmpleadoKpis(request);

        case VENTAS_POR_CAJA -> buildVentasPorCajaKpis(request);
        case VENTAS_POR_SESION_CAJA -> buildVentasPorSesionKpis(request);
        case TIEMPOS_POR_ESTACION -> buildTiemposEstacionKpis(request);
        case MERMA_POR_PERIODO -> buildMermaPeriodoKpis(request);
        case MOVIMIENTOS_STOCK_AJUSTES -> buildMovimientosStockKpis(request);

        case VENTAS_PRODUCTO_POR_EMPLEADO -> buildVentasProductoEmpleadoKpis(request);
        case RANKING_EMPLEADOS_POR_PRODUCTO -> buildRankingEmpleadosProductoKpis(request);
        case VENTAS_EXTRA_POR_EMPLEADO -> buildVentasExtraEmpleadoKpis(request);
        case RANKING_EMPLEADOS_POR_EXTRA -> buildRankingEmpleadosExtraKpis(request);
        
            default -> throw new UnsupportedOperationException(
                    "Todavía no se han implementado los KPIs PDF para el informe: " + tipoInforme
            );
        };
    }

    /**
     * Construye los KPIs del informe VENTAS_POR_DIA.
     *
     * KPIs elegidos para la primera versión:
     * - Total ventas
     * - Total devoluciones
     * - Total neto
     * - Total tickets
     * - Ticket medio global
     * - Mejor día
     */
    private List<InformePdfKpiItem> buildVentasPorDiaKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasPorDiaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasPorDiaResultDTO para VENTAS_POR_DIA"
            );
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();

        kpis.add(new InformePdfKpiItem(
                "Total ventas",
                InformePdfFormatUtils.formatMoney(result.getTotalVentas())
        ));

        kpis.add(new InformePdfKpiItem(
                "Total devoluciones",
                InformePdfFormatUtils.formatMoney(result.getTotalDevoluciones())
        ));

        kpis.add(new InformePdfKpiItem(
                "Total neto",
                InformePdfFormatUtils.formatMoney(result.getTotalNeto())
        ));

        kpis.add(new InformePdfKpiItem(
                "Total tickets",
                InformePdfFormatUtils.formatText(result.getTotalTickets())
        ));

        kpis.add(new InformePdfKpiItem(
                "Ticket medio global",
                InformePdfFormatUtils.formatMoney(result.getTicketMedioGlobal())
        ));

        String mejorDiaTexto = buildMejorDiaTexto(result);
        kpis.add(new InformePdfKpiItem("Mejor día", mejorDiaTexto));

        return kpis;
    }
    private List<InformePdfKpiItem> buildResumenEjecutivoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeResumenEjecutivoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeResumenEjecutivoResultDTO para RESUMEN_EJECUTIVO"
            );
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();

        kpis.add(new InformePdfKpiItem(
                "Ventas brutas",
                InformePdfFormatUtils.formatMoney(result.getVentasBrutas())
        ));

        kpis.add(new InformePdfKpiItem(
                "Devoluciones",
                InformePdfFormatUtils.formatMoney(result.getDevoluciones())
        ));

        kpis.add(new InformePdfKpiItem(
                "Neto",
                InformePdfFormatUtils.formatMoney(result.getNeto())
        ));

        kpis.add(new InformePdfKpiItem(
                "Ticket medio",
                InformePdfFormatUtils.formatMoney(result.getTicketMedio())
        ));

        kpis.add(new InformePdfKpiItem(
                "Combos vendidos",
                InformePdfFormatUtils.formatText(result.getTotalCombos())
        ));

        kpis.add(new InformePdfKpiItem(
                "Ahorro total",
                InformePdfFormatUtils.formatMoney(result.getAhorroTotal())
        ));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildVentasPorFranjaKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasFranjaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasFranjaResultDTO para VENTAS_POR_FRANJA_HORARIA"
            );
        }

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalDevoluciones = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;
        int totalTickets = 0;

        String franjaPico = "Sin datos";
        BigDecimal maxVentas = BigDecimal.ZERO;

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));
                totalTickets += safe(row.getNumeroTickets());

                if (safe(row.getTotalVentas()).compareTo(maxVentas) > 0) {
                    maxVentas = safe(row.getTotalVentas());
                    franjaPico = InformePdfFormatUtils.formatText(row.getFranja());
                }
            }
        }

        BigDecimal ticketMedioGlobal = divide(totalVentas, totalTickets);

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Total ventas", InformePdfFormatUtils.formatMoney(totalVentas)));
        kpis.add(new InformePdfKpiItem("Total devoluciones", InformePdfFormatUtils.formatMoney(totalDevoluciones)));
        kpis.add(new InformePdfKpiItem("Total neto", InformePdfFormatUtils.formatMoney(totalNeto)));
        kpis.add(new InformePdfKpiItem("Total tickets", InformePdfFormatUtils.formatText(totalTickets)));
        kpis.add(new InformePdfKpiItem("Ticket medio global", InformePdfFormatUtils.formatMoney(ticketMedioGlobal)));
        kpis.add(new InformePdfKpiItem("Franja pico", franjaPico + " · " + InformePdfFormatUtils.formatMoney(maxVentas)));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildTicketMedioPorDiaKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTicketMedioDiaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeTicketMedioDiaResultDTO para TICKET_MEDIO_POR_DIA"
            );
        }

        int totalTickets = 0;
        BigDecimal totalVentas = BigDecimal.ZERO;

        BigDecimal mejorTicketMedio = BigDecimal.ZERO;
        String mejorDiaTicketMedio = "Sin datos";

        BigDecimal mejorVentas = BigDecimal.ZERO;
        String mejorDiaVentas = "Sin datos";

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                totalTickets += safe(row.getNumeroTickets());
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));

                if (safe(row.getTicketMedio()).compareTo(mejorTicketMedio) > 0) {
                    mejorTicketMedio = safe(row.getTicketMedio());
                    mejorDiaTicketMedio = InformePdfFormatUtils.formatDate(row.getFecha());
                }

                if (safe(row.getTotalVentas()).compareTo(mejorVentas) > 0) {
                    mejorVentas = safe(row.getTotalVentas());
                    mejorDiaVentas = InformePdfFormatUtils.formatDate(row.getFecha());
                }
            }
        }

        BigDecimal ticketMedioGlobal = divide(totalVentas, totalTickets);

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Total tickets", InformePdfFormatUtils.formatText(totalTickets)));
        kpis.add(new InformePdfKpiItem("Total ventas", InformePdfFormatUtils.formatMoney(totalVentas)));
        kpis.add(new InformePdfKpiItem("Ticket medio global", InformePdfFormatUtils.formatMoney(ticketMedioGlobal)));
        kpis.add(new InformePdfKpiItem("Mejor día por ticket medio",
                mejorDiaTicketMedio + " · " + InformePdfFormatUtils.formatMoney(mejorTicketMedio)));
        kpis.add(new InformePdfKpiItem("Mejor día por ventas",
                mejorDiaVentas + " · " + InformePdfFormatUtils.formatMoney(mejorVentas)));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildPagosPorMetodoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformePagosMetodoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformePagosMetodoResultDTO para PAGOS_POR_METODO"
            );
        }

        int totalOperaciones = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;

        String metodoPrincipal = "Sin datos";
        BigDecimal importeMetodoPrincipal = BigDecimal.ZERO;
        int numeroMetodos = 0;

        if (result.getRows() != null) {
            numeroMetodos = result.getRows().size();

            for (var row : result.getRows()) {
                totalOperaciones += safe(row.getNumeroOperaciones());
                importeTotal = importeTotal.add(safe(row.getImporteTotal()));

                if (safe(row.getImporteTotal()).compareTo(importeMetodoPrincipal) > 0) {
                    importeMetodoPrincipal = safe(row.getImporteTotal());
                    metodoPrincipal = InformePdfFormatUtils.formatText(row.getMetodoPago());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Operaciones totales", InformePdfFormatUtils.formatText(totalOperaciones)));
        kpis.add(new InformePdfKpiItem("Método principal",
                metodoPrincipal + " · " + InformePdfFormatUtils.formatMoney(importeMetodoPrincipal)));
        kpis.add(new InformePdfKpiItem("Métodos usados", InformePdfFormatUtils.formatText(numeroMetodos)));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildVentasNetasVsDevolucionesKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeNetoVsDevolucionesResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeNetoVsDevolucionesResultDTO para VENTAS_NETAS_VS_DEVOLUCIONES"
            );
        }

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalDevoluciones = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;

        BigDecimal mayorDevolucion = BigDecimal.ZERO;
        String diaMayorDevolucion = "Sin datos";

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));

                if (safe(row.getTotalDevoluciones()).compareTo(mayorDevolucion) > 0) {
                    mayorDevolucion = safe(row.getTotalDevoluciones());
                    diaMayorDevolucion = InformePdfFormatUtils.formatDate(row.getFecha());
                }
            }
        }

        BigDecimal ratioGlobal = BigDecimal.ZERO;
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            ratioGlobal = totalDevoluciones
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalVentas, 2, RoundingMode.HALF_UP);
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Total ventas", InformePdfFormatUtils.formatMoney(totalVentas)));
        kpis.add(new InformePdfKpiItem("Total devoluciones", InformePdfFormatUtils.formatMoney(totalDevoluciones)));
        kpis.add(new InformePdfKpiItem("Total neto", InformePdfFormatUtils.formatMoney(totalNeto)));
        kpis.add(new InformePdfKpiItem("Ratio devolución global", InformePdfFormatUtils.formatPercent(ratioGlobal)));
        kpis.add(new InformePdfKpiItem("Día con mayor devolución",
                diaMayorDevolucion + " · " + InformePdfFormatUtils.formatMoney(mayorDevolucion)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildProductosMasVendidosKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeProductosVendidosResultDTO para PRODUCTOS_MAS_VENDIDOS"
            );
        }

        int unidadesTotales = 0;
        BigDecimal brutoTotal = BigDecimal.ZERO;
        BigDecimal devolucionesTotal = BigDecimal.ZERO;
        BigDecimal netoTotal = BigDecimal.ZERO;

        String productoTop = "Sin datos";
        int unidadesTop = 0;

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                unidadesTotales += safe(row.getUnidadesVendidas());
                brutoTotal = brutoTotal.add(safe(row.getImporteBruto()));
                devolucionesTotal = devolucionesTotal.add(safe(row.getImporteDevoluciones()));
                netoTotal = netoTotal.add(safe(row.getImporteNeto()));

                if (safe(row.getUnidadesVendidas()) > unidadesTop) {
                    unidadesTop = safe(row.getUnidadesVendidas());
                    productoTop = InformePdfFormatUtils.formatText(row.getNombreProducto());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Unidades totales", InformePdfFormatUtils.formatText(unidadesTotales)));
        kpis.add(new InformePdfKpiItem("Bruto total", InformePdfFormatUtils.formatMoney(brutoTotal)));
        kpis.add(new InformePdfKpiItem("Devoluciones totales", InformePdfFormatUtils.formatMoney(devolucionesTotal)));
        kpis.add(new InformePdfKpiItem("Neto total", InformePdfFormatUtils.formatMoney(netoTotal)));
        kpis.add(new InformePdfKpiItem("Producto top", productoTop + " · " + unidadesTop + " uds"));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildExtrasMasVendidosKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeExtrasVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeExtrasVendidosResultDTO para EXTRAS_MAS_VENDIDOS"
            );
        }

        int vecesTotales = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        String extraTop = "Sin datos";
        int vecesTop = 0;
        java.util.Set<String> grupos = new java.util.HashSet<>();

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                vecesTotales += safe(row.getVecesVendido());
                importeTotal = importeTotal.add(safe(row.getImporteGenerado()));

                String grupo = InformePdfFormatUtils.formatText(row.getGrupoPrincipal());
                if (!grupo.isBlank()) {
                    grupos.add(grupo);
                }

                if (safe(row.getVecesVendido()) > vecesTop) {
                    vecesTop = safe(row.getVecesVendido());
                    extraTop = InformePdfFormatUtils.formatText(row.getNombreExtra());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Veces vendidas", InformePdfFormatUtils.formatText(vecesTotales)));
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Extra top", extraTop + " · " + vecesTop + " veces"));
        kpis.add(new InformePdfKpiItem("Grupos principales", InformePdfFormatUtils.formatText(grupos.size())));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildCombosVendidosKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeCombosVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeCombosVendidosResultDTO para COMBOS_VENDIDOS"
            );
        }

        int totalCombos = 0;
        BigDecimal precioOriginalTotal = BigDecimal.ZERO;
        BigDecimal precioFinalTotal = BigDecimal.ZERO;
        BigDecimal ahorroTotal = BigDecimal.ZERO;

        String comboTop = "Sin datos";
        int vecesTop = 0;

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                totalCombos += safe(row.getVecesVendido());
                precioOriginalTotal = precioOriginalTotal.add(safe(row.getPrecioOriginalTotal()));
                precioFinalTotal = precioFinalTotal.add(safe(row.getPrecioFinalTotal()));
                ahorroTotal = ahorroTotal.add(safe(row.getAhorroTotal()));

                if (safe(row.getVecesVendido()) > vecesTop) {
                    vecesTop = safe(row.getVecesVendido());
                    comboTop = InformePdfFormatUtils.formatText(row.getNombreCombo());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Combos vendidos", InformePdfFormatUtils.formatText(totalCombos)));
        kpis.add(new InformePdfKpiItem("Precio original total", InformePdfFormatUtils.formatMoney(precioOriginalTotal)));
        kpis.add(new InformePdfKpiItem("Precio final total", InformePdfFormatUtils.formatMoney(precioFinalTotal)));
        kpis.add(new InformePdfKpiItem("Ahorro total", InformePdfFormatUtils.formatMoney(ahorroTotal)));
        kpis.add(new InformePdfKpiItem("Combo top", comboTop + " · " + vecesTop + " veces"));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildDescuentosAplicadosKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDescuentosAplicadosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeDescuentosAplicadosResultDTO para DESCUENTOS_APLICADOS"
            );
        }

        int usosTotales = 0;
        BigDecimal baseTotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;

        String descuentoTop = "Sin datos";
        BigDecimal importeTop = BigDecimal.ZERO;
        java.util.Set<String> tipos = new java.util.HashSet<>();

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                usosTotales += safe(row.getNumeroUsos());
                baseTotal = baseTotal.add(safe(row.getImporteBase()));
                descuentoTotal = descuentoTotal.add(safe(row.getImporteDescuento()));

                String tipo = InformePdfFormatUtils.formatText(row.getTipoBeneficio());
                if (!tipo.isBlank()) {
                    tipos.add(tipo);
                }

                if (safe(row.getImporteDescuento()).compareTo(importeTop) > 0) {
                    importeTop = safe(row.getImporteDescuento());
                    descuentoTop = InformePdfFormatUtils.formatText(row.getNombreDescuento());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Usos totales", InformePdfFormatUtils.formatText(usosTotales)));
        kpis.add(new InformePdfKpiItem("Base total", InformePdfFormatUtils.formatMoney(baseTotal)));
        kpis.add(new InformePdfKpiItem("Descuento total", InformePdfFormatUtils.formatMoney(descuentoTotal)));
        kpis.add(new InformePdfKpiItem("Descuento top",
                descuentoTop + " · " + InformePdfFormatUtils.formatMoney(importeTop)));
        kpis.add(new InformePdfKpiItem("Tipos de beneficio", InformePdfFormatUtils.formatText(tipos.size())));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildDevolucionesProductoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDevolucionesProductoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeDevolucionesProductoResultDTO para DEVOLUCIONES_POR_PRODUCTO"
            );
        }

        int cantidadDevueltaTotal = 0;
        BigDecimal reembolsoTotal = BigDecimal.ZERO;
        int numeroDevolucionesTotal = 0;

        String productoMasDevuelto = "Sin datos";
        int cantidadTop = 0;
        int productosConReposicion = 0;

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                cantidadDevueltaTotal += safe(row.getCantidadDevuelta());
                reembolsoTotal = reembolsoTotal.add(safe(row.getImporteReembolsado()));
                numeroDevolucionesTotal += safe(row.getNumeroDevoluciones());

                if (row.isReponeStock()) {
                    productosConReposicion++;
                }

                if (safe(row.getCantidadDevuelta()) > cantidadTop) {
                    cantidadTop = safe(row.getCantidadDevuelta());
                    productoMasDevuelto = InformePdfFormatUtils.formatText(row.getNombreProducto());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Cantidad devuelta", InformePdfFormatUtils.formatText(cantidadDevueltaTotal)));
        kpis.add(new InformePdfKpiItem("Reembolso total", InformePdfFormatUtils.formatMoney(reembolsoTotal)));
        kpis.add(new InformePdfKpiItem("Nº devoluciones", InformePdfFormatUtils.formatText(numeroDevolucionesTotal)));
        kpis.add(new InformePdfKpiItem("Producto más devuelto",
                productoMasDevuelto + " · " + cantidadTop + " uds"));
        kpis.add(new InformePdfKpiItem("Productos con reposición",
                InformePdfFormatUtils.formatText(productosConReposicion)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildRankingEmpleadosExtrasKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtrasResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosExtrasResultDTO para RANKING_EMPLEADOS_POR_EXTRAS"
            );
        }

        int extrasTotales = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        String empleadoTop = "Sin datos";
        int extrasTop = 0;
        int empleados = 0;

        if (result.getRows() != null) {
            empleados = result.getRows().size();

            for (dtoS.InformeRankingEmpleadosExtrasRowDTO row : result.getRows()) {
                extrasTotales += safe(row.getTotalExtrasVendidos());
                importeTotal = importeTotal.add(safe(row.getImporteExtras()));

                if (safe(row.getTotalExtrasVendidos()) > extrasTop) {
                    extrasTop = safe(row.getTotalExtrasVendidos());
                    empleadoTop = InformePdfFormatUtils.formatText(row.getNombreEmpleado());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Extras vendidos", InformePdfFormatUtils.formatText(extrasTotales)));
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Empleado top", empleadoTop + " · " + extrasTop + " extras"));
        kpis.add(new InformePdfKpiItem("Nº empleados", InformePdfFormatUtils.formatText(empleados)));

        return kpis;
    }
   
    private List<InformePdfKpiItem> buildProductosPorEmpleadoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosPorEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeProductosPorEmpleadoResultDTO para PRODUCTOS_VENDIDOS_POR_EMPLEADO"
            );
        }

        int unidadesTotales = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        Set<String> empleados = new HashSet<>();
        Set<String> productos = new HashSet<>();

        if (result.getRows() != null) {
            for (dtoS.InformeProductosPorEmpleadoRowDTO row : result.getRows()) {
                unidadesTotales += safe(row.getUnidadesVendidas());
                importeTotal = importeTotal.add(safe(row.getImporteTotal()));
                empleados.add(InformePdfFormatUtils.formatText(row.getNombreEmpleado()));
                productos.add(InformePdfFormatUtils.formatText(row.getNombreProducto()));
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Unidades totales", InformePdfFormatUtils.formatText(unidadesTotales)));
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Empleados distintos", InformePdfFormatUtils.formatText(empleados.size())));
        kpis.add(new InformePdfKpiItem("Productos distintos", InformePdfFormatUtils.formatText(productos.size())));

        return kpis;
    }
    private List<InformePdfKpiItem> buildVentasPorCajaKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasCajaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasCajaResultDTO para VENTAS_POR_CAJA"
            );
        }

        BigDecimal ventasTotales = BigDecimal.ZERO;
        BigDecimal devolucionesTotales = BigDecimal.ZERO;
        BigDecimal netoTotal = BigDecimal.ZERO;
        int ticketsTotales = 0;

        String cajaTop = "Sin datos";
        BigDecimal netoTop = BigDecimal.ZERO;

        if (result.getRows() != null) {
            for (dtoS.InformeVentasCajaRowDTO row : result.getRows()) {
                ventasTotales = ventasTotales.add(safe(row.getTotalVentas()));
                devolucionesTotales = devolucionesTotales.add(safe(row.getTotalDevoluciones()));
                netoTotal = netoTotal.add(safe(row.getTotalNeto()));
                ticketsTotales += safe(row.getNumeroTickets());

                if (safe(row.getTotalNeto()).compareTo(netoTop) > 0) {
                    netoTop = safe(row.getTotalNeto());
                    cajaTop = InformePdfFormatUtils.formatText(row.getNombreCaja());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Ventas totales", InformePdfFormatUtils.formatMoney(ventasTotales)));
        kpis.add(new InformePdfKpiItem("Devoluciones totales", InformePdfFormatUtils.formatMoney(devolucionesTotales)));
        kpis.add(new InformePdfKpiItem("Neto total", InformePdfFormatUtils.formatMoney(netoTotal)));
        kpis.add(new InformePdfKpiItem("Tickets totales", InformePdfFormatUtils.formatText(ticketsTotales)));
        kpis.add(new InformePdfKpiItem("Caja top", cajaTop + " · " + InformePdfFormatUtils.formatMoney(netoTop)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildVentasPorSesionKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasSesionCajaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasSesionCajaResultDTO para VENTAS_POR_SESION_CAJA"
            );
        }

        int sesiones = 0;
        BigDecimal ventasTotales = BigDecimal.ZERO;
        BigDecimal devolucionesTotales = BigDecimal.ZERO;
        BigDecimal netoTotal = BigDecimal.ZERO;

        String sesionTop = "Sin datos";
        BigDecimal netoTop = BigDecimal.ZERO;

        if (result.getRows() != null) {
            sesiones = result.getRows().size();

            for (dtoS.InformeVentasSesionCajaRowDTO row : result.getRows()) {
                ventasTotales = ventasTotales.add(safe(row.getTotalVentas()));
                devolucionesTotales = devolucionesTotales.add(safe(row.getTotalDevoluciones()));
                netoTotal = netoTotal.add(safe(row.getTotalNeto()));

                if (safe(row.getTotalNeto()).compareTo(netoTop) > 0) {
                    netoTop = safe(row.getTotalNeto());
                    sesionTop = "#" + InformePdfFormatUtils.formatText(row.getIdSesion());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Nº sesiones", InformePdfFormatUtils.formatText(sesiones)));
        kpis.add(new InformePdfKpiItem("Ventas totales", InformePdfFormatUtils.formatMoney(ventasTotales)));
        kpis.add(new InformePdfKpiItem("Devoluciones totales", InformePdfFormatUtils.formatMoney(devolucionesTotales)));
        kpis.add(new InformePdfKpiItem("Neto total", InformePdfFormatUtils.formatMoney(netoTotal)));
        kpis.add(new InformePdfKpiItem("Sesión top", sesionTop + " · " + InformePdfFormatUtils.formatMoney(netoTop)));

        return kpis;
    }
    
    private List<InformePdfKpiItem> buildTiemposEstacionKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTiemposEstacionResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeTiemposEstacionResultDTO para TIEMPOS_POR_ESTACION"
            );
        }

        int itemsTotales = 0;
        BigDecimal sumaTiempoPonderado = BigDecimal.ZERO;
        BigDecimal tiempoMaximo = BigDecimal.ZERO;
        String estacionMasLenta = "Sin datos";
        BigDecimal peorTiempoMedio = BigDecimal.ZERO;

        if (result.getRows() != null) {
            for (dtoS.InformeTiemposEstacionRowDTO row : result.getRows()) {
                int items = safe(row.getItemsProcesados());
                itemsTotales += items;

                sumaTiempoPonderado = sumaTiempoPonderado.add(
                        safe(row.getTiempoMedioSegundos()).multiply(BigDecimal.valueOf(items))
                );

                if (safe(row.getTiempoMaximoSegundos()).compareTo(tiempoMaximo) > 0) {
                    tiempoMaximo = safe(row.getTiempoMaximoSegundos());
                }

                if (safe(row.getTiempoMedioSegundos()).compareTo(peorTiempoMedio) > 0) {
                    peorTiempoMedio = safe(row.getTiempoMedioSegundos());
                    estacionMasLenta = InformePdfFormatUtils.formatText(row.getNombreEstacion());
                }
            }
        }

        BigDecimal tiempoMedioPonderado = divide(sumaTiempoPonderado, itemsTotales);

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Items procesados", InformePdfFormatUtils.formatText(itemsTotales)));
        kpis.add(new InformePdfKpiItem("Tiempo medio ponderado", InformePdfFormatUtils.formatText(tiempoMedioPonderado)));
        kpis.add(new InformePdfKpiItem("Tiempo máximo absoluto", InformePdfFormatUtils.formatText(tiempoMaximo)));
        kpis.add(new InformePdfKpiItem("Estación más lenta",
                estacionMasLenta + " · " + InformePdfFormatUtils.formatText(peorTiempoMedio)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildMermaPeriodoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMermaPeriodoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeMermaPeriodoResultDTO para MERMA_POR_PERIODO"
            );
        }

        BigDecimal cantidadTotal = BigDecimal.ZERO;
        int registros = 0;
        LocalDate fechaMasReciente = null;
        String tipoMasFrecuente = "Sin datos";
        java.util.Map<String, Integer> tiposCount = new java.util.HashMap<>();

        if (result.getRows() != null) {
            registros = result.getRows().size();

            for (dtoS.InformeMermaPeriodoRowDTO row : result.getRows()) {
                cantidadTotal = cantidadTotal.add(safe(row.getCantidad()));

                if (row.getFecha() != null && (fechaMasReciente == null || row.getFecha().isAfter(fechaMasReciente))) {
                    fechaMasReciente = row.getFecha();
                }

                String tipo = InformePdfFormatUtils.formatText(row.getTipoMerma());
                tiposCount.put(tipo, tiposCount.getOrDefault(tipo, 0) + 1);
            }
        }

        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : tiposCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                tipoMasFrecuente = entry.getKey();
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Cantidad total", InformePdfFormatUtils.formatText(cantidadTotal)));
        kpis.add(new InformePdfKpiItem("Nº registros", InformePdfFormatUtils.formatText(registros)));
        kpis.add(new InformePdfKpiItem("Fecha más reciente", InformePdfFormatUtils.formatDate(fechaMasReciente)));
        kpis.add(new InformePdfKpiItem("Tipo más frecuente", tipoMasFrecuente));

        return kpis;
    }
    private List<InformePdfKpiItem> buildMovimientosStockKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMovimientoStockResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeMovimientoStockResultDTO para MOVIMIENTOS_STOCK_AJUSTES"
            );
        }

        int movimientos = 0;
        BigDecimal cantidadTotal = BigDecimal.ZERO;
        LocalDateTime ultimoMovimiento = null;
        String tipoMasFrecuente = "Sin datos";
        java.util.Map<String, Integer> tiposCount = new java.util.HashMap<>();

        if (result.getRows() != null) {
            movimientos = result.getRows().size();

            for (dtoS.InformeMovimientoStockRowDTO row : result.getRows()) {
                cantidadTotal = cantidadTotal.add(safe(row.getCantidad()));

                if (row.getFecha() != null && (ultimoMovimiento == null || row.getFecha().isAfter(ultimoMovimiento))) {
                    ultimoMovimiento = row.getFecha();
                }

                String tipo = InformePdfFormatUtils.formatText(row.getTipoMovimiento());
                tiposCount.put(tipo, tiposCount.getOrDefault(tipo, 0) + 1);
            }
        }

        int maxCount = 0;
        for (java.util.Map.Entry<String, Integer> entry : tiposCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                tipoMasFrecuente = entry.getKey();
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Nº movimientos", InformePdfFormatUtils.formatText(movimientos)));
        kpis.add(new InformePdfKpiItem("Cantidad total movida", InformePdfFormatUtils.formatText(cantidadTotal)));
        kpis.add(new InformePdfKpiItem("Último movimiento", InformePdfFormatUtils.formatDateTime(ultimoMovimiento)));
        kpis.add(new InformePdfKpiItem("Tipo más frecuente", tipoMasFrecuente));

        return kpis;
    }
    private List<InformePdfKpiItem> buildVentasProductoEmpleadoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasProductoEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasProductoEmpleadoResultDTO para VENTAS_PRODUCTO_POR_EMPLEADO"
            );
        }

        int unidadesTotales = 0;
        BigDecimal brutoTotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal netoTotal = BigDecimal.ZERO;

        String topPar = "Sin datos";
        BigDecimal netoTop = BigDecimal.ZERO;

        if (result.getRows() != null) {
            for (dtoS.InformeVentasProductoEmpleadoRowDTO row : result.getRows()) {
                unidadesTotales += safe(row.getUnidadesVendidas());
                brutoTotal = brutoTotal.add(safe(row.getImporteBruto()));
                descuentoTotal = descuentoTotal.add(safe(row.getImporteDescuento()));
                netoTotal = netoTotal.add(safe(row.getImporteNeto()));

                if (safe(row.getImporteNeto()).compareTo(netoTop) > 0) {
                    netoTop = safe(row.getImporteNeto());
                    topPar = InformePdfFormatUtils.formatText(row.getNombreEmpleado())
                            + " / "
                            + InformePdfFormatUtils.formatText(row.getNombreProducto());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Unidades totales", InformePdfFormatUtils.formatText(unidadesTotales)));
        kpis.add(new InformePdfKpiItem("Bruto total", InformePdfFormatUtils.formatMoney(brutoTotal)));
        kpis.add(new InformePdfKpiItem("Descuento total", InformePdfFormatUtils.formatMoney(descuentoTotal)));
        kpis.add(new InformePdfKpiItem("Neto total", InformePdfFormatUtils.formatMoney(netoTotal)));
        kpis.add(new InformePdfKpiItem("Par top", topPar + " · " + InformePdfFormatUtils.formatMoney(netoTop)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildRankingEmpleadosProductoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosProductoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosProductoResultDTO para RANKING_EMPLEADOS_POR_PRODUCTO"
            );
        }

        int unidadesTotales = 0;
        BigDecimal netoTotal = BigDecimal.ZERO;
        String topRanking = "Sin datos";
        int filas = 0;

        if (result.getRows() != null) {
            filas = result.getRows().size();

            for (dtoS.InformeRankingEmpleadosProductoRowDTO row : result.getRows()) {
                unidadesTotales += safe(row.getUnidadesVendidas());
                netoTotal = netoTotal.add(safe(row.getImporteNeto()));

                if (safe(row.getPosicion()) == 1) {
                    topRanking = InformePdfFormatUtils.formatText(row.getNombreEmpleado())
                            + " / "
                            + InformePdfFormatUtils.formatText(row.getNombreProducto());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Unidades totales", InformePdfFormatUtils.formatText(unidadesTotales)));
        kpis.add(new InformePdfKpiItem("Neto total", InformePdfFormatUtils.formatMoney(netoTotal)));
        kpis.add(new InformePdfKpiItem("Top ranking", topRanking));
        kpis.add(new InformePdfKpiItem("Filas ranking", InformePdfFormatUtils.formatText(filas)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildVentasExtraEmpleadoKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasExtraEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasExtraEmpleadoResultDTO para VENTAS_EXTRA_POR_EMPLEADO"
            );
        }

        int vecesTotales = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        String topPar = "Sin datos";
        BigDecimal importeTop = BigDecimal.ZERO;
        Set<String> extras = new HashSet<>();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasExtraEmpleadoRowDTO row : result.getRows()) {
                vecesTotales += safe(row.getVecesVendido());
                importeTotal = importeTotal.add(safe(row.getImporteGenerado()));
                extras.add(InformePdfFormatUtils.formatText(row.getNombreExtra()));

                if (safe(row.getImporteGenerado()).compareTo(importeTop) > 0) {
                    importeTop = safe(row.getImporteGenerado());
                    topPar = InformePdfFormatUtils.formatText(row.getNombreEmpleado())
                            + " / "
                            + InformePdfFormatUtils.formatText(row.getNombreExtra());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Veces totales", InformePdfFormatUtils.formatText(vecesTotales)));
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Par top", topPar + " · " + InformePdfFormatUtils.formatMoney(importeTop)));
        kpis.add(new InformePdfKpiItem("Extras distintos", InformePdfFormatUtils.formatText(extras.size())));

        return kpis;
    }
    private List<InformePdfKpiItem> buildRankingEmpleadosExtraKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtraResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosExtraResultDTO para RANKING_EMPLEADOS_POR_EXTRA"
            );
        }

        int vecesTotales = 0;
        BigDecimal importeTotal = BigDecimal.ZERO;
        String topRanking = "Sin datos";
        int filas = 0;

        if (result.getRows() != null) {
            filas = result.getRows().size();

            for (dtoS.InformeRankingEmpleadosExtraRowDTO row : result.getRows()) {
                vecesTotales += safe(row.getVecesVendido());
                importeTotal = importeTotal.add(safe(row.getImporteGenerado()));

                if (safe(row.getPosicion()) == 1) {
                    topRanking = InformePdfFormatUtils.formatText(row.getNombreEmpleado())
                            + " / "
                            + InformePdfFormatUtils.formatText(row.getNombreExtra());
                }
            }
        }

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Veces totales", InformePdfFormatUtils.formatText(vecesTotales)));
        kpis.add(new InformePdfKpiItem("Importe total", InformePdfFormatUtils.formatMoney(importeTotal)));
        kpis.add(new InformePdfKpiItem("Top ranking", topRanking));
        kpis.add(new InformePdfKpiItem("Filas ranking", InformePdfFormatUtils.formatText(filas)));

        return kpis;
    }
    private List<InformePdfKpiItem> buildRankingEmpleadosVentasKpis(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosVentasResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosVentasResultDTO para RANKING_EMPLEADOS_POR_VENTAS"
            );
        }

        BigDecimal ventasTotales = BigDecimal.ZERO;
        int ticketsTotales = 0;
        String empleadoTop = "Sin datos";
        BigDecimal ventasTop = BigDecimal.ZERO;
        int empleados = 0;

        if (result.getRows() != null) {
            empleados = result.getRows().size();

            for (dtoS.InformeRankingEmpleadosVentasRowDTO row : result.getRows()) {
                ventasTotales = ventasTotales.add(safe(row.getTotalVentas()));
                ticketsTotales += safe(row.getNumeroTickets());

                if (safe(row.getTotalVentas()).compareTo(ventasTop) > 0) {
                    ventasTop = safe(row.getTotalVentas());
                    empleadoTop = InformePdfFormatUtils.formatText(row.getNombreEmpleado());
                }
            }
        }

        BigDecimal ticketMedioGlobal = divide(ventasTotales, ticketsTotales);

        List<InformePdfKpiItem> kpis = new ArrayList<>();
        kpis.add(new InformePdfKpiItem("Ventas totales", InformePdfFormatUtils.formatMoney(ventasTotales)));
        kpis.add(new InformePdfKpiItem("Tickets totales", InformePdfFormatUtils.formatText(ticketsTotales)));
        kpis.add(new InformePdfKpiItem("Ticket medio global", InformePdfFormatUtils.formatMoney(ticketMedioGlobal)));
        kpis.add(new InformePdfKpiItem("Empleado top", empleadoTop + " · " + InformePdfFormatUtils.formatMoney(ventasTop)));
        kpis.add(new InformePdfKpiItem("Nº empleados", InformePdfFormatUtils.formatText(empleados)));

        return kpis;
    }

    /**
     * Construye el texto del KPI "Mejor día".
     *
     * Ejemplo:
     * - 05/04/2026 · 1.230,40 €
     *
     * Si faltan datos, devuelve un texto seguro.
     */
    private String buildMejorDiaTexto(InformeVentasPorDiaResultDTO result) {
        boolean tieneFecha = result.getFechaMejorDia() != null;
        boolean tieneImporte = result.getImporteMejorDia() != null;

        if (!tieneFecha && !tieneImporte) {
            return "Sin datos";
        }

        String fecha = InformePdfFormatUtils.formatDate(result.getFechaMejorDia());
        String importe = InformePdfFormatUtils.formatMoney(result.getImporteMejorDia());

        if (tieneFecha && tieneImporte) {
            return fecha + " · " + importe;
        }

        if (tieneFecha) {
            return fecha;
        }

        return importe;
    }
    
    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private int safe(Integer value) {
        return value != null ? value : 0;
    }

    private BigDecimal divide(BigDecimal total, int divisor) {
        if (divisor <= 0) {
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
    }
}
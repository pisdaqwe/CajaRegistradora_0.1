package util;

import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformePdfExportRequest;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Builder responsable de generar una imagen de gráfico
 * lista para insertarse en el PDF.
 *
 * Responsabilidades:
 * - leer el request PDF
 * - detectar el tipo de informe
 * - construir un JFreeChart simple
 * - devolverlo renderizado como BufferedImage
 *
 * IMPORTANTE:
 * - esta clase NO usa Swing
 * - esta clase NO abre diálogos
 * - esta clase NO consulta la BD
 *
 * Primera versión:
 * - soporta solo informes del bloque 1 con sentido visual
 */
public class InformePdfChartBuilder {

    private static final int CHART_WIDTH = 900;
    private static final int CHART_HEIGHT = 420;

    public BufferedImage buildChartImage(InformePdfExportRequest request) {
        Objects.requireNonNull(request, "request no puede ser null");
        request.validate();

        TipoInforme tipoInforme = request.getTipoInforme();

        JFreeChart chart = switch (tipoInforme) {
        case VENTAS_POR_DIA -> buildVentasPorDiaChart(request);
        case VENTAS_POR_FRANJA_HORARIA -> buildVentasPorFranjaChart(request);
        case TICKET_MEDIO_POR_DIA -> buildTicketMedioPorDiaChart(request);
        case PAGOS_POR_METODO -> buildPagosPorMetodoChart(request);
        case VENTAS_NETAS_VS_DEVOLUCIONES -> buildVentasNetasVsDevolucionesChart(request);

        case RESUMEN_EJECUTIVO -> buildResumenEjecutivoChart(request);

        case PRODUCTOS_MAS_VENDIDOS -> buildProductosMasVendidosChart(request);
        case EXTRAS_MAS_VENDIDOS -> buildExtrasMasVendidosChart(request);
        case COMBOS_VENDIDOS -> buildCombosVendidosChart(request);
        case DESCUENTOS_APLICADOS -> buildDescuentosAplicadosChart(request);
        case DEVOLUCIONES_POR_PRODUCTO -> buildDevolucionesProductoChart(request);

        case RANKING_EMPLEADOS_POR_VENTAS -> buildRankingEmpleadosVentasChart(request);
        case RANKING_EMPLEADOS_POR_EXTRAS -> buildRankingEmpleadosExtrasChart(request);
        case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> buildProductosPorEmpleadoChart(request);

        case VENTAS_POR_CAJA -> buildVentasPorCajaChart(request);
        case VENTAS_POR_SESION_CAJA -> buildVentasPorSesionCajaChart(request);
        case TIEMPOS_POR_ESTACION -> buildTiemposPorEstacionChart(request);
        case MERMA_POR_PERIODO -> buildMermaPorPeriodoChart(request);
        case MOVIMIENTOS_STOCK_AJUSTES -> buildMovimientosStockChart(request);

        case VENTAS_PRODUCTO_POR_EMPLEADO -> buildVentasProductoEmpleadoChart(request);
        case RANKING_EMPLEADOS_POR_PRODUCTO -> buildRankingEmpleadosProductoChart(request);
        case VENTAS_EXTRA_POR_EMPLEADO -> buildVentasExtraEmpleadoChart(request);
        case RANKING_EMPLEADOS_POR_EXTRA -> buildRankingEmpleadosExtraChart(request);
    };

        if (chart == null) {
            return null;
        }

        return chart.createBufferedImage(CHART_WIDTH, CHART_HEIGHT);
    }

    private JFreeChart buildVentasPorDiaChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasPorDiaResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasPorDiaRowDTO row : result.getRows()) {
                String categoria = InformePdfFormatUtils.formatDate(row.getFecha());
                dataset.addValue(safe(row.getTotalVentas()), "Ventas", categoria);
                dataset.addValue(safe(row.getTotalNeto()), "Neto", categoria);
            }
        }

        return ChartFactory.createLineChart(
                "Ventas por día",
                "Fecha",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }

    private JFreeChart buildVentasPorFranjaChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasFranjaResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasFranjaRowDTO row : result.getRows()) {
                String categoria = row.getFranja() != null ? row.getFranja() : "";
                dataset.addValue(safe(row.getTotalVentas()), "Ventas", categoria);
                dataset.addValue(safe(row.getTotalNeto()), "Neto", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ventas por franja horaria",
                "Franja",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }

    private JFreeChart buildTicketMedioPorDiaChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTicketMedioDiaResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeTicketMedioDiaRowDTO row : result.getRows()) {
                String categoria = InformePdfFormatUtils.formatDate(row.getFecha());
                dataset.addValue(safe(row.getTicketMedio()), "Ticket medio", categoria);
            }
        }

        return ChartFactory.createLineChart(
                "Ticket medio por día",
                "Fecha",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }

    private JFreeChart buildPagosPorMetodoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformePagosMetodoResultDTO result)) {
            return null;
        }

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        if (result.getRows() != null) {
            for (dtoS.InformePagosMetodoRowDTO row : result.getRows()) {
                dataset.setValue(
                        row.getMetodoPago() != null ? row.getMetodoPago() : "N/D",
                        safe(row.getImporteTotal())
                );
            }
        }

        return ChartFactory.createPieChart(
                "Pagos por método",
                dataset,
                true,
                false,
                false
        );
    }

    private JFreeChart buildVentasNetasVsDevolucionesChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeNetoVsDevolucionesResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeNetoVsDevolucionesRowDTO row : result.getRows()) {
                String categoria = InformePdfFormatUtils.formatDate(row.getFecha());
                dataset.addValue(safe(row.getTotalVentas()), "Ventas", categoria);
                dataset.addValue(safe(row.getTotalDevoluciones()), "Devoluciones", categoria);
                dataset.addValue(safe(row.getTotalNeto()), "Neto", categoria);
            }
        }

        return ChartFactory.createLineChart(
                "Ventas netas vs devoluciones",
                "Fecha",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildResumenEjecutivoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeResumenEjecutivoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(safe(result.getVentasBrutas()), "Resumen", "Ventas");
        dataset.addValue(safe(result.getDevoluciones()), "Resumen", "Devoluciones");
        dataset.addValue(safe(result.getNeto()), "Resumen", "Neto");
        dataset.addValue(safe(result.getAhorroTotal()), "Resumen", "Ahorro combos");

        return ChartFactory.createBarChart(
                "Resumen ejecutivo",
                "Indicador",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
    }
    private JFreeChart buildProductosMasVendidosChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosVendidosResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeProductosVendidosRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getUnidadesVendidas()), "Unidades", safeText(row.getNombreProducto()));
            }
        }

        return ChartFactory.createBarChart(
                "Productos más vendidos",
                "Producto",
                "Unidades",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildExtrasMasVendidosChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeExtrasVendidosResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeExtrasVendidosRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getVecesVendido()), "Veces", safeText(row.getNombreExtra()));
            }
        }

        return ChartFactory.createBarChart(
                "Extras más vendidos",
                "Extra",
                "Veces",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildCombosVendidosChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeCombosVendidosResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeCombosVendidosRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getVecesVendido()), "Veces", safeText(row.getNombreCombo()));
                dataset.addValue(safe(row.getAhorroTotal()), "Ahorro", safeText(row.getNombreCombo()));
            }
        }

        return ChartFactory.createBarChart(
                "Combos vendidos",
                "Combo",
                "Valor",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildDescuentosAplicadosChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDescuentosAplicadosResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeDescuentosAplicadosRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getImporteDescuento()), "Importe descuento", safeText(row.getNombreDescuento()));
            }
        }

        return ChartFactory.createBarChart(
                "Descuentos aplicados",
                "Descuento",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildDevolucionesProductoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDevolucionesProductoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeDevolucionesProductoRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getCantidadDevuelta()), "Cantidad devuelta", safeText(row.getNombreProducto()));
            }
        }

        return ChartFactory.createBarChart(
                "Devoluciones por producto",
                "Producto",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildRankingEmpleadosVentasChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosVentasResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosVentasRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getTotalVentas()), "Ventas", safeText(row.getNombreEmpleado()));
            }
        }

        return ChartFactory.createBarChart(
                "Ranking empleados por ventas",
                "Empleado",
                "Ventas",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildRankingEmpleadosExtrasChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtrasResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosExtrasRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getTotalExtrasVendidos()), "Extras vendidos", safeText(row.getNombreEmpleado()));
            }
        }

        return ChartFactory.createBarChart(
                "Ranking empleados por extras",
                "Empleado",
                "Extras",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildProductosPorEmpleadoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosPorEmpleadoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeProductosPorEmpleadoRowDTO row : result.getRows()) {
                String categoria = safeText(row.getNombreEmpleado()) + " / " + safeText(row.getNombreProducto());
                dataset.addValue(safe(row.getUnidadesVendidas()), "Unidades", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Productos vendidos por empleado",
                "Empleado / Producto",
                "Unidades",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildVentasPorCajaChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasCajaResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasCajaRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getTotalVentas()), "Ventas", safeText(row.getNombreCaja()));
                dataset.addValue(safe(row.getTotalNeto()), "Neto", safeText(row.getNombreCaja()));
            }
        }

        return ChartFactory.createBarChart(
                "Ventas por caja",
                "Caja",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildVentasPorSesionCajaChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasSesionCajaResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasSesionCajaRowDTO row : result.getRows()) {
                String categoria = "#" + safeText(row.getIdSesion());
                dataset.addValue(safe(row.getTotalNeto()), "Neto", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ventas por sesión de caja",
                "Sesión",
                "Neto",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildTiemposPorEstacionChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTiemposEstacionResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeTiemposEstacionRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getTiempoMedioSegundos()), "Tiempo medio", safeText(row.getNombreEstacion()));
                dataset.addValue(safe(row.getTiempoMaximoSegundos()), "Tiempo máximo", safeText(row.getNombreEstacion()));
            }
        }

        return ChartFactory.createBarChart(
                "Tiempos por estación",
                "Estación",
                "Segundos",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildMermaPorPeriodoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMermaPeriodoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeMermaPeriodoRowDTO row : result.getRows()) {
                String categoria = InformePdfFormatUtils.formatDate(row.getFecha());
                dataset.addValue(safe(row.getCantidad()), "Cantidad", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Merma por período",
                "Fecha",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildMovimientosStockChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMovimientoStockResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeMovimientoStockRowDTO row : result.getRows()) {
                dataset.addValue(safe(row.getCantidad()), "Cantidad", safeText(row.getTipoMovimiento()));
            }
        }

        return ChartFactory.createBarChart(
                "Movimientos de stock / ajustes",
                "Tipo movimiento",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildVentasProductoEmpleadoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasProductoEmpleadoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasProductoEmpleadoRowDTO row : result.getRows()) {
                String categoria = safeText(row.getNombreEmpleado()) + " / " + safeText(row.getNombreProducto());
                dataset.addValue(safe(row.getImporteNeto()), "Neto", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ventas producto por empleado",
                "Empleado / Producto",
                "Neto",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildRankingEmpleadosProductoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosProductoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosProductoRowDTO row : result.getRows()) {
                String categoria = safeText(row.getNombreEmpleado()) + " / " + safeText(row.getNombreProducto());
                dataset.addValue(safe(row.getUnidadesVendidas()), "Unidades", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ranking empleados por producto",
                "Empleado / Producto",
                "Unidades",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildVentasExtraEmpleadoChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasExtraEmpleadoResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeVentasExtraEmpleadoRowDTO row : result.getRows()) {
                String categoria = safeText(row.getNombreEmpleado()) + " / " + safeText(row.getNombreExtra());
                dataset.addValue(safe(row.getImporteGenerado()), "Importe", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ventas extra por empleado",
                "Empleado / Extra",
                "Importe",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private JFreeChart buildRankingEmpleadosExtraChart(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtraResultDTO result)) {
            return null;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosExtraRowDTO row : result.getRows()) {
                String categoria = safeText(row.getNombreEmpleado()) + " / " + safeText(row.getNombreExtra());
                dataset.addValue(safe(row.getVecesVendido()), "Veces", categoria);
            }
        }

        return ChartFactory.createBarChart(
                "Ranking empleados por extra",
                "Empleado / Extra",
                "Veces",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
    }
    private String safeText(Object value) {
        return value != null ? String.valueOf(value) : "";
    }
    
    private int safe(Integer value) {
        return value != null ? value : 0;
    }

    private java.math.BigDecimal safe(java.math.BigDecimal value) {
        return value != null ? value : java.math.BigDecimal.ZERO;
    }
}

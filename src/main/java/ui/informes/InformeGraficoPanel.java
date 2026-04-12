package ui.informes;

import enums.TipoInforme;
import ui.common.InformeUiTheme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de visualización gráfica.
 *
 * Primera fase:
 * - genera gráficos demo distintos según el tipo de informe
 * - sirve para validar que JFreeChart ya está integrado correctamente
 */
public class InformeGraficoPanel extends JPanel {

    private ChartPanel chartPanel;

    public InformeGraficoPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        JLabel title = InformeUiTheme.createSectionTitle("Visualización");
        add(title, BorderLayout.NORTH);

        cargarDemo(TipoInforme.RESUMEN_EJECUTIVO);

        setPreferredSize(new Dimension(460, 600));
    }

    public void cargarDemo(TipoInforme tipo) {
        JFreeChart chart;

        switch (tipo) {
            case INFORME_CAJA -> chart = createBarChartCaja();
            case VENTAS_POR_DIA -> chart = createLineChartVentasPorDia();
            case TICKET_MEDIO_POR_DIA -> chart = createLineChartTicketMedio();
            case PAGOS_POR_METODO -> chart = createPieChartPagos();
            case PRODUCTOS_MAS_VENDIDOS -> chart = createBarChartProductos();
            case COMBOS_VENDIDOS -> chart = createBarChartCombos();
            case DESCUENTOS -> chart = createBarChartDescuentos();
            case DEVOLUCIONES -> chart = createBarChartDevoluciones();
            case RESUMEN_EJECUTIVO -> chart = createBarChartResumen();
            default -> chart = createBarChartResumen();
        }

        replaceChart(chart);
    }

    private void replaceChart(JFreeChart chart) {
        if (chartPanel != null) {
            remove(chartPanel);
        }

        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(false);

        add(chartPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JFreeChart createBarChartResumen() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(6214.10, "Importe", "Ventas");
        dataset.addValue(145.20, "Importe", "Devoluciones");
        dataset.addValue(6068.90, "Importe", "Neto");
        dataset.addValue(415.85, "Importe", "Ahorro");

        JFreeChart chart = ChartFactory.createBarChart(
                "Resumen ejecutivo",
                "",
                "€",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private JFreeChart createBarChartCaja() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(250.00, "Caja 1", "Apertura");
        dataset.addValue(2136.40, "Caja 1", "Ventas");
        dataset.addValue(52.34, "Caja 1", "Devoluciones");
        dataset.addValue(2084.06, "Caja 1", "Neto");

        JFreeChart chart = ChartFactory.createBarChart(
                "Informe de caja",
                "",
                "€",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private JFreeChart createLineChartVentasPorDia() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(770.40, "Neto", "01/04");
        dataset.addValue(883.20, "Neto", "02/04");
        dataset.addValue(827.90, "Neto", "03/04");
        dataset.addValue(981.60, "Neto", "04/04");
        dataset.addValue(1092.40, "Neto", "05/04");

        JFreeChart chart = ChartFactory.createLineChart(
                "Ventas netas por día",
                "",
                "€",
                dataset
        );

        styleLineChart(chart);
        return chart;
    }

    private JFreeChart createLineChartTicketMedio() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(9.19, "Ticket medio", "01/04");
        dataset.addValue(9.43, "Ticket medio", "02/04");
        dataset.addValue(9.19, "Ticket medio", "03/04");
        dataset.addValue(9.38, "Ticket medio", "04/04");
        dataset.addValue(9.36, "Ticket medio", "05/04");

        JFreeChart chart = ChartFactory.createLineChart(
                "Ticket medio por día",
                "",
                "€",
                dataset
        );

        styleLineChart(chart);
        return chart;
    }

    private JFreeChart createPieChartPagos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Tarjeta", 58.0);
        dataset.setValue("Efectivo", 39.4);
        dataset.setValue("Vale", 2.6);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución por método de pago",
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(InformeUiTheme.CARD_BG);
        chart.getTitle().setPaint(InformeUiTheme.TEXT_PRIMARY);
        chart.getTitle().setFont(InformeUiTheme.FONT_SECTION);

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setBackgroundPaint(InformeUiTheme.CARD_BG);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(InformeUiTheme.CARD_BG_2);
        plot.setLabelOutlinePaint(InformeUiTheme.BORDER);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(InformeUiTheme.TEXT_PRIMARY);
        plot.setShadowPaint(null);
        plot.setSectionPaint("Tarjeta", InformeUiTheme.STARBUCKS_GREEN);
        plot.setSectionPaint("Efectivo", InformeUiTheme.ACCENT_GOLD);
        plot.setSectionPaint("Vale", InformeUiTheme.WARNING);

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(InformeUiTheme.CARD_BG);
            chart.getLegend().setItemPaint(InformeUiTheme.TEXT_PRIMARY);
        }

        return chart;
    }

    private JFreeChart createBarChartProductos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(148, "Unidades", "Latte");
        dataset.addValue(123, "Unidades", "Cappuccino");
        dataset.addValue(112, "Unidades", "Croissant");
        dataset.addValue(87, "Unidades", "Matcha");

        JFreeChart chart = ChartFactory.createBarChart(
                "Productos más vendidos",
                "",
                "Unidades",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private JFreeChart createBarChartCombos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(22, "Veces", "Desayuno");
        dataset.addValue(15, "Veces", "Merienda");
        dataset.addValue(11, "Veces", "Frío+Snack");

        JFreeChart chart = ChartFactory.createBarChart(
                "Combos vendidos",
                "",
                "Veces",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private JFreeChart createBarChartDescuentos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(126.00, "Descuento", "Empleado");
        dataset.addValue(61.50, "Descuento", "Promo QR");
        dataset.addValue(26.25, "Descuento", "Cupón");

        JFreeChart chart = ChartFactory.createBarChart(
                "Impacto económico de descuentos",
                "",
                "€",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private JFreeChart createBarChartDevoluciones() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(14.95, "Reembolso", "Termo");
        dataset.addValue(9.95, "Reembolso", "Taza");
        dataset.addValue(2.30, "Reembolso", "Aquarius");

        JFreeChart chart = ChartFactory.createBarChart(
                "Devoluciones del período",
                "",
                "€",
                dataset
        );

        styleBarChart(chart);
        return chart;
    }

    private void styleBarChart(JFreeChart chart) {
        chart.setBackgroundPaint(InformeUiTheme.CARD_BG);
        chart.getTitle().setPaint(InformeUiTheme.TEXT_PRIMARY);
        chart.getTitle().setFont(InformeUiTheme.FONT_SECTION);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(InformeUiTheme.PANEL_BG);
        plot.setRangeGridlinePaint(InformeUiTheme.BORDER);
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(InformeUiTheme.TEXT_SECONDARY);
        domainAxis.setLabelPaint(InformeUiTheme.TEXT_SECONDARY);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(InformeUiTheme.TEXT_SECONDARY);
        rangeAxis.setLabelPaint(InformeUiTheme.TEXT_SECONDARY);

        if (plot.getRenderer() instanceof BarRenderer renderer) {
            renderer.setSeriesPaint(0, InformeUiTheme.STARBUCKS_GREEN);
            renderer.setMaximumBarWidth(0.15);
        }
    }

    private void styleLineChart(JFreeChart chart) {
        chart.setBackgroundPaint(InformeUiTheme.CARD_BG);
        chart.getTitle().setPaint(InformeUiTheme.TEXT_PRIMARY);
        chart.getTitle().setFont(InformeUiTheme.FONT_SECTION);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(InformeUiTheme.PANEL_BG);
        plot.setRangeGridlinePaint(InformeUiTheme.BORDER);
        plot.setOutlineVisible(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(InformeUiTheme.TEXT_SECONDARY);
        domainAxis.setLabelPaint(InformeUiTheme.TEXT_SECONDARY);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(InformeUiTheme.TEXT_SECONDARY);
        rangeAxis.setLabelPaint(InformeUiTheme.TEXT_SECONDARY);

        if (plot.getRenderer() instanceof LineAndShapeRenderer renderer) {
            renderer.setSeriesPaint(0, InformeUiTheme.ACCENT_GOLD);
            renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        }
    }
}
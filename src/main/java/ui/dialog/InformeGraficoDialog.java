package ui.dialog;

import enums.ModoVistaInforme;
import enums.TipoInforme;
import ui.theme.InformeUiTheme;

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
import java.util.ArrayList;
import java.util.List;

public class InformeGraficoDialog extends JDialog {

    private final TipoInforme tipoInforme;
    private final String filterSummary;

    private final JComboBox<ModoVistaInforme> cmbModoVista;
    private final JComboBox<String> cmbTipoGrafico;
    private final JPanel chartContainer;

    private ChartPanel chartPanel;

    public InformeGraficoDialog(Window owner,
                                TipoInforme tipoInforme,
                                ModoVistaInforme modoVistaInicial,
                                String filterSummary) {
        super(owner, "Visualización de gráfico", ModalityType.APPLICATION_MODAL);
        this.tipoInforme = tipoInforme;
        this.filterSummary = filterSummary;

        getContentPane().setBackground(InformeUiTheme.APP_BG);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(InformeUiTheme.APP_BG);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel top = InformeUiTheme.createCardPanel(new BorderLayout(16, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(tipoInforme.getDisplayName());
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSummary = new JLabel(filterSummary != null ? filterSummary : "");
        lblSummary.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSummary.setForeground(InformeUiTheme.TEXT_SECONDARY);

        left.add(lblTitle);
        left.add(Box.createVerticalStrut(4));
        left.add(lblSummary);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        cmbModoVista = new JComboBox<>(ModoVistaInforme.values());
        cmbModoVista.setSelectedItem(modoVistaInicial != null ? modoVistaInicial : ModoVistaInforme.AGREGADA);
        InformeUiTheme.styleCombo(cmbModoVista);

        cmbTipoGrafico = new JComboBox<>(buildGraphOptions());
        InformeUiTheme.styleCombo(cmbTipoGrafico);

        if (!supportsComparativeMode(tipoInforme)) {
            cmbModoVista.setSelectedItem(ModoVistaInforme.AGREGADA);
            cmbModoVista.setEnabled(false);
        }

        cmbModoVista.addActionListener(e -> refreshChart());
        cmbTipoGrafico.addActionListener(e -> refreshChart());

        right.add(cmbModoVista);
        right.add(cmbTipoGrafico);

        top.add(left, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        chartContainer = InformeUiTheme.createCardPanel(new BorderLayout());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton btnCerrar = new JButton("Cerrar");
        InformeUiTheme.styleDangerButton(btnCerrar);
        btnCerrar.addActionListener(e -> dispose());

        bottom.add(btnCerrar);

        root.add(top, BorderLayout.NORTH);
        root.add(chartContainer, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);

        refreshChart();

        setSize(1100, 700);
        setLocationRelativeTo(owner);
    }

    private String[] buildGraphOptions() {
        List<String> options = new ArrayList<>();
        options.add("Líneas");
        options.add("Barras");

        if (supportsPieChart(tipoInforme)) {
            options.add("Circular");
        }

        return options.toArray(new String[0]);
    }

    private boolean supportsPieChart(TipoInforme tipoInforme) {
        return switch (tipoInforme) {
            case PAGOS_POR_METODO,
                 RESUMEN_EJECUTIVO,
                 DESCUENTOS_APLICADOS,
                 COMBOS_VENDIDOS,
                 EXTRAS_MAS_VENDIDOS -> true;
            default -> false;
        };
    }

    private boolean supportsComparativeMode(TipoInforme tipoInforme) {
        return switch (tipoInforme) {
            case VENTAS_POR_DIA,
                 TICKET_MEDIO_POR_DIA,
                 VENTAS_POR_FRANJA_HORARIA -> true;
            default -> false;
        };
    }

    private void refreshChart() {
        String selectedGraph = (String) cmbTipoGrafico.getSelectedItem();
        ModoVistaInforme modoVista = (ModoVistaInforme) cmbModoVista.getSelectedItem();

        JFreeChart chart;

        if ("Circular".equalsIgnoreCase(selectedGraph) && supportsPieChart(tipoInforme)) {
            chart = buildPieChartByTipo();
        } else if ("Barras".equalsIgnoreCase(selectedGraph)) {
            chart = buildBarChartByTipo(modoVista);
        } else {
            chart = buildLineChartByTipo(modoVista);
        }

        if (chartPanel != null) {
            chartContainer.remove(chartPanel);
        }

        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(false);

        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private JFreeChart buildBarChartByTipo(ModoVistaInforme modoVista) {
        return switch (tipoInforme) {
            case RESUMEN_EJECUTIVO -> createBarChartResumen();
            case VENTAS_POR_DIA -> createBarChartVentasPorDia(modoVista);
            case VENTAS_POR_FRANJA_HORARIA -> createBarChartVentasPorFranja(modoVista);
            case TICKET_MEDIO_POR_DIA -> createBarChartTicketMedio(modoVista);
            case PAGOS_POR_METODO -> createBarChartPagos();
            case VENTAS_NETAS_VS_DEVOLUCIONES -> createBarChartNetasVsDevoluciones();
            case PRODUCTOS_MAS_VENDIDOS -> createBarChartProductos();
            case EXTRAS_MAS_VENDIDOS -> createBarChartExtras();
            case COMBOS_VENDIDOS -> createBarChartCombos();
            case DESCUENTOS_APLICADOS -> createBarChartDescuentos();
            case DEVOLUCIONES_POR_PRODUCTO -> createBarChartDevolucionesProducto();
            case RANKING_EMPLEADOS_POR_VENTAS -> createBarChartRankingVentas();
            case RANKING_EMPLEADOS_POR_EXTRAS -> createBarChartRankingExtras();
            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> createBarChartProductosPorEmpleado();
            case VENTAS_POR_CAJA -> createBarChartVentasPorCaja();
            case VENTAS_POR_SESION_CAJA -> createBarChartVentasPorSesion();
            case TIEMPOS_POR_ESTACION -> createBarChartTiemposEstacion();
            case MERMA_POR_PERIODO -> createBarChartMerma();
            case MOVIMIENTOS_STOCK_AJUSTES -> createBarChartMovimientosStock();
        };
    }

    private JFreeChart buildLineChartByTipo(ModoVistaInforme modoVista) {
        return switch (tipoInforme) {
            case RESUMEN_EJECUTIVO -> createLineChartResumen();
            case VENTAS_POR_DIA -> createLineChartVentasPorDia(modoVista);
            case VENTAS_POR_FRANJA_HORARIA -> createLineChartVentasPorFranja(modoVista);
            case TICKET_MEDIO_POR_DIA -> createLineChartTicketMedio(modoVista);
            case PAGOS_POR_METODO -> createLineChartPagos();
            case VENTAS_NETAS_VS_DEVOLUCIONES -> createLineChartNetasVsDevoluciones();
            case PRODUCTOS_MAS_VENDIDOS -> createLineChartProductos();
            case EXTRAS_MAS_VENDIDOS -> createLineChartExtras();
            case COMBOS_VENDIDOS -> createLineChartCombos();
            case DESCUENTOS_APLICADOS -> createLineChartDescuentos();
            case DEVOLUCIONES_POR_PRODUCTO -> createLineChartDevolucionesProducto();
            case RANKING_EMPLEADOS_POR_VENTAS -> createLineChartRankingVentas();
            case RANKING_EMPLEADOS_POR_EXTRAS -> createLineChartRankingExtras();
            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> createLineChartProductosPorEmpleado();
            case VENTAS_POR_CAJA -> createLineChartVentasPorCaja();
            case VENTAS_POR_SESION_CAJA -> createLineChartVentasPorSesion();
            case TIEMPOS_POR_ESTACION -> createLineChartTiemposEstacion();
            case MERMA_POR_PERIODO -> createLineChartMerma();
            case MOVIMIENTOS_STOCK_AJUSTES -> createLineChartMovimientosStock();
        };
    }

    private JFreeChart buildPieChartByTipo() {
        return switch (tipoInforme) {
            case RESUMEN_EJECUTIVO -> createPieChartResumen();
            case PAGOS_POR_METODO -> createPieChartPagos();
            case COMBOS_VENDIDOS -> createPieChartCombos();
            case DESCUENTOS_APLICADOS -> createPieChartDescuentos();
            case EXTRAS_MAS_VENDIDOS -> createPieChartExtras();
            default -> createPieChartResumen();
        };
    }

    // =====================================================
    // BARRAS
    // =====================================================

    private JFreeChart createBarChartResumen() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(6214.10, "Importe", "Ventas");
        dataset.addValue(145.20, "Importe", "Devoluciones");
        dataset.addValue(6068.90, "Importe", "Neto");
        dataset.addValue(415.85, "Importe", "Ahorro");
        return buildBarChart("Resumen ejecutivo", "€", dataset);
    }

    private JFreeChart createBarChartVentasPorDia(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(320.4, "Ana", "01/04");
            dataset.addValue(280.2, "Luis", "01/04");
            dataset.addValue(190.0, "Marta", "01/04");
            dataset.addValue(340.8, "Ana", "02/04");
            dataset.addValue(295.3, "Luis", "02/04");
            dataset.addValue(215.4, "Marta", "02/04");
        } else {
            dataset.addValue(770.4, "Neto", "01/04");
            dataset.addValue(883.2, "Neto", "02/04");
            dataset.addValue(827.9, "Neto", "03/04");
            dataset.addValue(981.6, "Neto", "04/04");
            dataset.addValue(1092.4, "Neto", "05/04");
        }

        return buildBarChart("Ventas por día", "€", dataset);
    }

    private JFreeChart createBarChartVentasPorFranja(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(180, "Ana", "08-10h");
            dataset.addValue(140, "Luis", "08-10h");
            dataset.addValue(210, "Ana", "10-12h");
            dataset.addValue(160, "Luis", "10-12h");
            dataset.addValue(120, "Ana", "12-14h");
            dataset.addValue(90, "Luis", "12-14h");
        } else {
            dataset.addValue(420, "Ventas", "08-10h");
            dataset.addValue(610, "Ventas", "10-12h");
            dataset.addValue(540, "Ventas", "12-14h");
            dataset.addValue(330, "Ventas", "14-16h");
        }

        return buildBarChart("Ventas por franja horaria", "€", dataset);
    }

    private JFreeChart createBarChartTicketMedio(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(10.2, "Ana", "01/04");
            dataset.addValue(9.1, "Luis", "01/04");
            dataset.addValue(8.8, "Marta", "01/04");
            dataset.addValue(10.8, "Ana", "02/04");
            dataset.addValue(9.4, "Luis", "02/04");
            dataset.addValue(9.0, "Marta", "02/04");
        } else {
            dataset.addValue(9.19, "Ticket medio", "01/04");
            dataset.addValue(9.43, "Ticket medio", "02/04");
            dataset.addValue(9.19, "Ticket medio", "03/04");
            dataset.addValue(9.38, "Ticket medio", "04/04");
            dataset.addValue(9.36, "Ticket medio", "05/04");
        }

        return buildBarChart("Ticket medio por día", "€", dataset);
    }

    private JFreeChart createBarChartPagos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2848.2, "Importe", "Tarjeta");
        dataset.addValue(1936.4, "Importe", "Efectivo");
        dataset.addValue(127.6, "Importe", "Vale");
        return buildBarChart("Pagos por método", "€", dataset);
    }

    private JFreeChart createBarChartNetasVsDevoluciones() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(905.2, "Ventas", "02/04");
        dataset.addValue(22.0, "Devoluciones", "02/04");
        dataset.addValue(883.2, "Neto", "02/04");
        dataset.addValue(1012.6, "Ventas", "04/04");
        dataset.addValue(31.0, "Devoluciones", "04/04");
        dataset.addValue(981.6, "Neto", "04/04");
        return buildBarChart("Ventas netas vs devoluciones", "€", dataset);
    }

    private JFreeChart createBarChartProductos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(148, "Unidades", "Latte");
        dataset.addValue(123, "Unidades", "Cappuccino");
        dataset.addValue(112, "Unidades", "Croissant");
        dataset.addValue(87, "Unidades", "Matcha");
        return buildBarChart("Productos más vendidos", "Unidades", dataset);
    }

    private JFreeChart createBarChartExtras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(96, "Ventas", "Shot extra");
        dataset.addValue(81, "Ventas", "Sirope vainilla");
        dataset.addValue(62, "Ventas", "Leche avena");
        dataset.addValue(41, "Ventas", "Topping caramelo");
        return buildBarChart("Extras más vendidos", "Veces", dataset);
    }

    private JFreeChart createBarChartCombos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(22, "Veces", "Desayuno");
        dataset.addValue(15, "Veces", "Merienda");
        dataset.addValue(11, "Veces", "Frío+Snack");
        return buildBarChart("Combos vendidos", "Veces", dataset);
    }

    private JFreeChart createBarChartDescuentos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(126.00, "Descuento", "Empleado");
        dataset.addValue(61.50, "Descuento", "Promo QR");
        dataset.addValue(26.25, "Descuento", "Cupón");
        return buildBarChart("Descuentos aplicados", "€", dataset);
    }

    private JFreeChart createBarChartDevolucionesProducto() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(14.95, "Reembolso", "Termo");
        dataset.addValue(9.95, "Reembolso", "Taza");
        dataset.addValue(2.30, "Reembolso", "Aquarius");
        return buildBarChart("Devoluciones por producto", "€", dataset);
    }

    private JFreeChart createBarChartRankingVentas() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1840.50, "Ventas", "Ana");
        dataset.addValue(1620.30, "Ventas", "Luis");
        dataset.addValue(1488.90, "Ventas", "Marta");
        return buildBarChart("Ranking empleados por ventas", "€", dataset);
    }

    private JFreeChart createBarChartRankingExtras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(82, "Extras", "Ana");
        dataset.addValue(69, "Extras", "Luis");
        dataset.addValue(58, "Extras", "Marta");
        return buildBarChart("Ranking empleados por extras", "Veces", dataset);
    }

    private JFreeChart createBarChartProductosPorEmpleado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(118, "Ana", "Latte");
        dataset.addValue(102, "Ana", "Croissant");
        dataset.addValue(95, "Luis", "Cappuccino");
        dataset.addValue(74, "Marta", "Matcha");
        return buildBarChart("Productos vendidos por empleado", "Unidades", dataset);
    }

    private JFreeChart createBarChartVentasPorCaja() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2136.40, "Ventas", "Caja 1");
        dataset.addValue(1845.20, "Ventas", "Caja 2");
        return buildBarChart("Ventas por caja", "€", dataset);
    }

    private JFreeChart createBarChartVentasPorSesion() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(980.40, "Ventas", "Sesión 101");
        dataset.addValue(1120.20, "Ventas", "Sesión 102");
        dataset.addValue(875.90, "Ventas", "Sesión 103");
        return buildBarChart("Ventas por sesión de caja", "€", dataset);
    }

    private JFreeChart createBarChartTiemposEstacion() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(145, "Segundos", "Calientes");
        dataset.addValue(122, "Segundos", "Frías");
        dataset.addValue(210, "Segundos", "Comida");
        return buildBarChart("Tiempos por estación", "Segundos", dataset);
    }

    private JFreeChart createBarChartMerma() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(45.0, "Merma", "01/04");
        dataset.addValue(62.5, "Merma", "02/04");
        dataset.addValue(38.0, "Merma", "03/04");
        return buildBarChart("Merma por período", "Unidades / €", dataset);
    }

    private JFreeChart createBarChartMovimientosStock() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(18, "Cantidad", "Entrada");
        dataset.addValue(36, "Cantidad", "Salida");
        dataset.addValue(7, "Cantidad", "Ajuste");
        dataset.addValue(5, "Cantidad", "Merma");
        return buildBarChart("Movimientos de stock / ajustes", "Movimientos", dataset);
    }

    // =====================================================
    // LÍNEAS
    // =====================================================

    private JFreeChart createLineChartResumen() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(5200, "Ventas", "Semana 1");
        dataset.addValue(6100, "Ventas", "Semana 2");
        dataset.addValue(5800, "Ventas", "Semana 3");
        dataset.addValue(6400, "Ventas", "Semana 4");
        return buildLineChart("Resumen ejecutivo", "€", dataset);
    }

    private JFreeChart createLineChartVentasPorDia(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(320.4, "Ana", "01/04");
            dataset.addValue(280.2, "Luis", "01/04");
            dataset.addValue(190.0, "Marta", "01/04");
            dataset.addValue(340.8, "Ana", "02/04");
            dataset.addValue(295.3, "Luis", "02/04");
            dataset.addValue(215.4, "Marta", "02/04");
        } else {
            dataset.addValue(770.4, "Neto", "01/04");
            dataset.addValue(883.2, "Neto", "02/04");
            dataset.addValue(827.9, "Neto", "03/04");
            dataset.addValue(981.6, "Neto", "04/04");
            dataset.addValue(1092.4, "Neto", "05/04");
        }

        return buildLineChart("Ventas por día", "€", dataset);
    }

    private JFreeChart createLineChartVentasPorFranja(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(180, "Ana", "08-10h");
            dataset.addValue(140, "Luis", "08-10h");
            dataset.addValue(210, "Ana", "10-12h");
            dataset.addValue(160, "Luis", "10-12h");
            dataset.addValue(120, "Ana", "12-14h");
            dataset.addValue(90, "Luis", "12-14h");
        } else {
            dataset.addValue(420, "Ventas", "08-10h");
            dataset.addValue(610, "Ventas", "10-12h");
            dataset.addValue(540, "Ventas", "12-14h");
            dataset.addValue(330, "Ventas", "14-16h");
        }

        return buildLineChart("Ventas por franja horaria", "€", dataset);
    }

    private JFreeChart createLineChartTicketMedio(ModoVistaInforme modoVista) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            dataset.addValue(10.2, "Ana", "01/04");
            dataset.addValue(9.1, "Luis", "01/04");
            dataset.addValue(8.8, "Marta", "01/04");
            dataset.addValue(10.8, "Ana", "02/04");
            dataset.addValue(9.4, "Luis", "02/04");
            dataset.addValue(9.0, "Marta", "02/04");
        } else {
            dataset.addValue(9.19, "Ticket medio", "01/04");
            dataset.addValue(9.43, "Ticket medio", "02/04");
            dataset.addValue(9.19, "Ticket medio", "03/04");
            dataset.addValue(9.38, "Ticket medio", "04/04");
            dataset.addValue(9.36, "Ticket medio", "05/04");
        }

        return buildLineChart("Ticket medio por día", "€", dataset);
    }

    private JFreeChart createLineChartPagos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(620, "Tarjeta", "01/04");
        dataset.addValue(580, "Tarjeta", "02/04");
        dataset.addValue(390, "Efectivo", "01/04");
        dataset.addValue(410, "Efectivo", "02/04");
        return buildLineChart("Pagos por método", "€", dataset);
    }

    private JFreeChart createLineChartNetasVsDevoluciones() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(770.4, "Neto", "01/04");
        dataset.addValue(12.0, "Devoluciones", "01/04");
        dataset.addValue(883.2, "Neto", "02/04");
        dataset.addValue(22.0, "Devoluciones", "02/04");
        return buildLineChart("Ventas netas vs devoluciones", "€", dataset);
    }

    private JFreeChart createLineChartProductos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(120, "Latte", "Semana 1");
        dataset.addValue(132, "Latte", "Semana 2");
        dataset.addValue(98, "Cappuccino", "Semana 1");
        dataset.addValue(110, "Cappuccino", "Semana 2");
        return buildLineChart("Productos más vendidos", "Unidades", dataset);
    }

    private JFreeChart createLineChartExtras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(20, "Shot extra", "Semana 1");
        dataset.addValue(26, "Shot extra", "Semana 2");
        dataset.addValue(18, "Sirope vainilla", "Semana 1");
        dataset.addValue(22, "Sirope vainilla", "Semana 2");
        return buildLineChart("Extras más vendidos", "Veces", dataset);
    }

    private JFreeChart createLineChartCombos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(6, "Desayuno", "Lunes");
        dataset.addValue(8, "Desayuno", "Martes");
        dataset.addValue(4, "Merienda", "Lunes");
        dataset.addValue(6, "Merienda", "Martes");
        return buildLineChart("Combos vendidos", "Veces", dataset);
    }

    private JFreeChart createLineChartDescuentos() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(32, "Usos", "Empleado");
        dataset.addValue(18, "Usos", "Promo QR");
        dataset.addValue(9, "Usos", "Cupón");
        return buildLineChart("Descuentos aplicados", "Usos", dataset);
    }

    private JFreeChart createLineChartDevolucionesProducto() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2, "Devoluciones", "Termo");
        dataset.addValue(1, "Devoluciones", "Taza");
        dataset.addValue(1, "Devoluciones", "Aquarius");
        return buildLineChart("Devoluciones por producto", "Veces", dataset);
    }

    private JFreeChart createLineChartRankingVentas() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1800, "Ventas", "Ana");
        dataset.addValue(1620, "Ventas", "Luis");
        dataset.addValue(1490, "Ventas", "Marta");
        return buildLineChart("Ranking empleados por ventas", "€", dataset);
    }

    private JFreeChart createLineChartRankingExtras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(82, "Extras", "Ana");
        dataset.addValue(69, "Extras", "Luis");
        dataset.addValue(58, "Extras", "Marta");
        return buildLineChart("Ranking empleados por extras", "Veces", dataset);
    }

    private JFreeChart createLineChartProductosPorEmpleado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(118, "Ana", "Latte");
        dataset.addValue(95, "Luis", "Cappuccino");
        dataset.addValue(74, "Marta", "Matcha");
        return buildLineChart("Productos vendidos por empleado", "Unidades", dataset);
    }

    private JFreeChart createLineChartVentasPorCaja() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(520, "Caja 1", "Lunes");
        dataset.addValue(610, "Caja 1", "Martes");
        dataset.addValue(450, "Caja 2", "Lunes");
        dataset.addValue(570, "Caja 2", "Martes");
        return buildLineChart("Ventas por caja", "€", dataset);
    }

    private JFreeChart createLineChartVentasPorSesion() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(980, "Ventas", "Sesión 101");
        dataset.addValue(1120, "Ventas", "Sesión 102");
        dataset.addValue(875, "Ventas", "Sesión 103");
        return buildLineChart("Ventas por sesión de caja", "€", dataset);
    }

    private JFreeChart createLineChartTiemposEstacion() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(145, "Calientes", "09h");
        dataset.addValue(122, "Frías", "09h");
        dataset.addValue(210, "Comida", "09h");
        dataset.addValue(158, "Calientes", "11h");
        dataset.addValue(130, "Frías", "11h");
        dataset.addValue(240, "Comida", "11h");
        return buildLineChart("Tiempos por estación", "Segundos", dataset);
    }

    private JFreeChart createLineChartMerma() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(45.0, "Merma", "01/04");
        dataset.addValue(62.5, "Merma", "02/04");
        dataset.addValue(38.0, "Merma", "03/04");
        return buildLineChart("Merma por período", "Unidades / €", dataset);
    }

    private JFreeChart createLineChartMovimientosStock() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(8, "Entradas", "01/04");
        dataset.addValue(14, "Salidas", "01/04");
        dataset.addValue(3, "Ajustes", "01/04");
        dataset.addValue(10, "Entradas", "02/04");
        dataset.addValue(16, "Salidas", "02/04");
        dataset.addValue(4, "Ajustes", "02/04");
        return buildLineChart("Movimientos de stock / ajustes", "Movimientos", dataset);
    }

    // =====================================================
    // CIRCULARES
    // =====================================================

    private JFreeChart createPieChartResumen() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Ventas netas", 6068.90);
        dataset.setValue("Devoluciones", 145.20);
        dataset.setValue("Ahorro", 415.85);
        return buildPieChart("Resumen ejecutivo", dataset);
    }

    private JFreeChart createPieChartPagos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Tarjeta", 58.0);
        dataset.setValue("Efectivo", 39.4);
        dataset.setValue("Vale", 2.6);
        return buildPieChart("Pagos por método", dataset);
    }

    private JFreeChart createPieChartCombos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Desayuno", 22);
        dataset.setValue("Merienda", 15);
        dataset.setValue("Frío+Snack", 11);
        return buildPieChart("Combos vendidos", dataset);
    }

    private JFreeChart createPieChartDescuentos() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Empleado", 126.0);
        dataset.setValue("Promo QR", 61.5);
        dataset.setValue("Cupón", 26.25);
        return buildPieChart("Descuentos aplicados", dataset);
    }

    private JFreeChart createPieChartExtras() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Shot extra", 96);
        dataset.setValue("Sirope vainilla", 81);
        dataset.setValue("Leche avena", 62);
        dataset.setValue("Topping caramelo", 41);
        return buildPieChart("Extras más vendidos", dataset);
    }

    // =====================================================
    // HELPERS DE CONSTRUCCIÓN
    // =====================================================

    private JFreeChart buildBarChart(String title, String rangeLabel, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(title, "", rangeLabel, dataset);
        styleBarChart(chart);
        return chart;
    }

    private JFreeChart buildLineChart(String title, String rangeLabel, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(title, "", rangeLabel, dataset);
        styleLineChart(chart);
        return chart;
    }

    private JFreeChart buildPieChart(String title, DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);

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

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(InformeUiTheme.CARD_BG);
            chart.getLegend().setItemPaint(InformeUiTheme.TEXT_PRIMARY);
        }

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

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, InformeUiTheme.STARBUCKS_GREEN);
        renderer.setSeriesPaint(1, InformeUiTheme.ACCENT_GOLD);
        renderer.setSeriesPaint(2, Color.WHITE);
        renderer.setMaximumBarWidth(0.15);
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

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, InformeUiTheme.ACCENT_GOLD);
        renderer.setSeriesPaint(1, InformeUiTheme.STARBUCKS_GREEN);
        renderer.setSeriesPaint(2, Color.WHITE);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesStroke(1, new BasicStroke(2.2f));
        renderer.setSeriesStroke(2, new BasicStroke(2.2f));
    }
}
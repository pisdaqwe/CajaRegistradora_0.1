package ui.dialog;

import enums.ModoVistaInforme;
import enums.TipoInforme;
import ui.theme.InformeUiTheme;
import ui.theme.TpvIconFactory;
import util.I18n;

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

import dtoS.InformeCombosVendidosResultDTO;
import dtoS.InformeCombosVendidosRowDTO;
import dtoS.InformeDescuentosAplicadosResultDTO;
import dtoS.InformeDescuentosAplicadosRowDTO;
import dtoS.InformeDevolucionesProductoResultDTO;
import dtoS.InformeDevolucionesProductoRowDTO;
import dtoS.InformeExtrasVendidosResultDTO;
import dtoS.InformeExtrasVendidosRowDTO;
import dtoS.InformeMermaPeriodoResultDTO;
import dtoS.InformeMermaPeriodoRowDTO;
import dtoS.InformeMovimientoStockResultDTO;
import dtoS.InformeMovimientoStockRowDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformeNetoVsDevolucionesRowDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformePagosMetodoRowDTO;
import dtoS.InformeProductosPorEmpleadoResultDTO;
import dtoS.InformeProductosPorEmpleadoRowDTO;
import dtoS.InformeProductosVendidosResultDTO;
import dtoS.InformeProductosVendidosRowDTO;
import dtoS.InformeRankingEmpleadosExtraResultDTO;
import dtoS.InformeRankingEmpleadosExtraRowDTO;
import dtoS.InformeRankingEmpleadosExtrasResultDTO;
import dtoS.InformeRankingEmpleadosExtrasRowDTO;
import dtoS.InformeRankingEmpleadosProductoResultDTO;
import dtoS.InformeRankingEmpleadosProductoRowDTO;
import dtoS.InformeRankingEmpleadosVentasResultDTO;
import dtoS.InformeRankingEmpleadosVentasRowDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformeTicketMedioDiaRowDTO;
import dtoS.InformeTiemposEstacionResultDTO;
import dtoS.InformeTiemposEstacionRowDTO;
import dtoS.InformeVentasCajaResultDTO;
import dtoS.InformeVentasCajaRowDTO;
import dtoS.InformeVentasExtraEmpleadoResultDTO;
import dtoS.InformeVentasExtraEmpleadoRowDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeVentasFranjaRowDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeVentasPorDiaRowDTO;
import dtoS.InformeVentasProductoEmpleadoResultDTO;
import dtoS.InformeVentasProductoEmpleadoRowDTO;
import dtoS.InformeVentasSesionCajaResultDTO;
import dtoS.InformeVentasSesionCajaRowDTO;

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

	private final InformeResumenEjecutivoResultDTO resumenEjecutivoResult;
	private final InformeVentasPorDiaResultDTO ventasPorDiaResult;
	private final InformeVentasFranjaResultDTO ventasFranjaResult;
	private final InformeTicketMedioDiaResultDTO ticketMedioDiaResult;
	private final InformePagosMetodoResultDTO pagosMetodoResult;
	private final InformeNetoVsDevolucionesResultDTO netoVsDevolucionesResult;

	private final InformeProductosVendidosResultDTO productosVendidosResult;
	private final InformeExtrasVendidosResultDTO extrasVendidosResult;
	private final InformeCombosVendidosResultDTO combosVendidosResult;
	private final InformeDescuentosAplicadosResultDTO descuentosAplicadosResult;
	private final InformeDevolucionesProductoResultDTO devolucionesProductoResult;

	private final InformeRankingEmpleadosVentasResultDTO rankingEmpleadosVentasResult;
	private final InformeRankingEmpleadosExtrasResultDTO rankingEmpleadosExtrasResult;
	private final InformeProductosPorEmpleadoResultDTO productosPorEmpleadoResult;

	private final InformeVentasCajaResultDTO ventasCajaResult;
	private final InformeVentasSesionCajaResultDTO ventasSesionCajaResult;
	private final InformeTiemposEstacionResultDTO tiemposEstacionResult;
	private final InformeMermaPeriodoResultDTO mermaPeriodoResult;
	private final InformeMovimientoStockResultDTO movimientosStockResult;
	private final InformeVentasProductoEmpleadoResultDTO ventasProductoEmpleadoResult;
	private final InformeRankingEmpleadosProductoResultDTO rankingEmpleadosProductoResult;
	private final InformeVentasExtraEmpleadoResultDTO ventasExtraEmpleadoResult;
	private final InformeRankingEmpleadosExtraResultDTO rankingEmpleadosExtraResult;
	private ChartPanel chartPanel;

	public InformeGraficoDialog(Window owner, TipoInforme tipoInforme, ModoVistaInforme modoVistaInicial,
			String filterSummary, InformeResumenEjecutivoResultDTO resumenEjecutivoResult,
			InformeVentasPorDiaResultDTO ventasPorDiaResult, InformeVentasFranjaResultDTO ventasFranjaResult,
			InformeTicketMedioDiaResultDTO ticketMedioDiaResult, InformePagosMetodoResultDTO pagosMetodoResult,
			InformeNetoVsDevolucionesResultDTO netoVsDevolucionesResult,
			InformeProductosVendidosResultDTO productosVendidosResult,
			InformeExtrasVendidosResultDTO extrasVendidosResult, InformeCombosVendidosResultDTO combosVendidosResult,
			InformeDescuentosAplicadosResultDTO descuentosAplicadosResult,
			InformeDevolucionesProductoResultDTO devolucionesProductoResult,
			InformeRankingEmpleadosVentasResultDTO rankingEmpleadosVentasResult,
			InformeRankingEmpleadosExtrasResultDTO rankingEmpleadosExtrasResult,
			InformeProductosPorEmpleadoResultDTO productosPorEmpleadoResult,
			InformeVentasCajaResultDTO ventasCajaResult, InformeVentasSesionCajaResultDTO ventasSesionCajaResult,
			InformeTiemposEstacionResultDTO tiemposEstacionResult, InformeMermaPeriodoResultDTO mermaPeriodoResult,
			InformeMovimientoStockResultDTO movimientosStockResult,
			InformeVentasProductoEmpleadoResultDTO ventasProductoEmpleadoResult,
			InformeRankingEmpleadosProductoResultDTO rankingEmpleadosProductoResult,
			InformeVentasExtraEmpleadoResultDTO ventasExtraEmpleadoResult,
			InformeRankingEmpleadosExtraResultDTO rankingEmpleadosExtraResult) {

		super(owner, I18n.t("reportGraph.title"), ModalityType.APPLICATION_MODAL);
		this.tipoInforme = tipoInforme;
		this.filterSummary = filterSummary;

		this.resumenEjecutivoResult = resumenEjecutivoResult;
		this.ventasPorDiaResult = ventasPorDiaResult;
		this.ventasFranjaResult = ventasFranjaResult;
		this.ticketMedioDiaResult = ticketMedioDiaResult;
		this.pagosMetodoResult = pagosMetodoResult;
		this.netoVsDevolucionesResult = netoVsDevolucionesResult;

		this.productosVendidosResult = productosVendidosResult;
		this.extrasVendidosResult = extrasVendidosResult;
		this.combosVendidosResult = combosVendidosResult;
		this.descuentosAplicadosResult = descuentosAplicadosResult;
		this.devolucionesProductoResult = devolucionesProductoResult;

		this.rankingEmpleadosVentasResult = rankingEmpleadosVentasResult;
		this.rankingEmpleadosExtrasResult = rankingEmpleadosExtrasResult;
		this.productosPorEmpleadoResult = productosPorEmpleadoResult;

		this.ventasCajaResult = ventasCajaResult;
		this.ventasSesionCajaResult = ventasSesionCajaResult;
		this.tiemposEstacionResult = tiemposEstacionResult;
		this.mermaPeriodoResult = mermaPeriodoResult;
		this.movimientosStockResult = movimientosStockResult;

		this.ventasProductoEmpleadoResult = ventasProductoEmpleadoResult;
		this.rankingEmpleadosProductoResult = rankingEmpleadosProductoResult;
		this.ventasExtraEmpleadoResult = ventasExtraEmpleadoResult;
		this.rankingEmpleadosExtraResult = rankingEmpleadosExtraResult;

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

		refreshGraphOptions();

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

		JButton btnCerrar = new JButton(I18n.t("common.close"));
		InformeUiTheme.styleDangerButton(btnCerrar);
		btnCerrar.setIcon(TpvIconFactory.cancel(18, InformeUiTheme.TEXT_PRIMARY));
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

	private String graphLine() {
		return I18n.t("reportGraph.type.line");
	}

	private String graphBar() {
		return I18n.t("reportGraph.type.bar");
	}

	private String graphPie() {
		return I18n.t("reportGraph.type.pie");
	}

	private String[] buildGraphOptions() {
		List<String> options = new ArrayList<>();
		options.add(graphLine());
		options.add(graphBar());

		if (supportsPieChart(tipoInforme)) {
			options.add(graphPie());
		}

		return options.toArray(new String[0]);
	}

	private boolean supportsPieChart(TipoInforme tipoInforme) {
		return tipoInforme == TipoInforme.RESUMEN_EJECUTIVO || tipoInforme == TipoInforme.PAGOS_POR_METODO
				|| tipoInforme == TipoInforme.EXTRAS_MAS_VENDIDOS || tipoInforme == TipoInforme.COMBOS_VENDIDOS
				|| tipoInforme == TipoInforme.DESCUENTOS_APLICADOS;
	}

	private boolean supportsComparativeMode(TipoInforme tipoInforme) {
		return switch (tipoInforme) {
		case VENTAS_POR_DIA, TICKET_MEDIO_POR_DIA, VENTAS_POR_FRANJA_HORARIA -> true;
		default -> false;
		};
	}

	private void refreshGraphOptions() {
		cmbTipoGrafico.removeAllItems();

		if (tipoInforme == TipoInforme.RESUMEN_EJECUTIVO) {
			cmbTipoGrafico.addItem(graphBar());
			cmbTipoGrafico.addItem(graphPie());
			cmbTipoGrafico.setSelectedItem(graphBar());
			return;
		}

		if (tipoInforme == TipoInforme.VENTAS_POR_DIA || tipoInforme == TipoInforme.VENTAS_POR_FRANJA_HORARIA
				|| tipoInforme == TipoInforme.TICKET_MEDIO_POR_DIA
				|| tipoInforme == TipoInforme.VENTAS_NETAS_VS_DEVOLUCIONES || tipoInforme == TipoInforme.VENTAS_POR_CAJA
				|| tipoInforme == TipoInforme.TIEMPOS_POR_ESTACION || tipoInforme == TipoInforme.MERMA_POR_PERIODO
				|| tipoInforme == TipoInforme.MOVIMIENTOS_STOCK_AJUSTES) {
			cmbTipoGrafico.addItem(graphLine());
			cmbTipoGrafico.addItem(graphBar());
			cmbTipoGrafico.setSelectedItem(graphLine());
			return;
		}

		if (tipoInforme == TipoInforme.PAGOS_POR_METODO || tipoInforme == TipoInforme.EXTRAS_MAS_VENDIDOS
				|| tipoInforme == TipoInforme.COMBOS_VENDIDOS || tipoInforme == TipoInforme.DESCUENTOS_APLICADOS) {
			cmbTipoGrafico.addItem(graphBar());
			cmbTipoGrafico.addItem(graphPie());
			cmbTipoGrafico.setSelectedItem(graphBar());
			return;
		}

		if (tipoInforme == TipoInforme.PRODUCTOS_MAS_VENDIDOS || tipoInforme == TipoInforme.DEVOLUCIONES_POR_PRODUCTO
				|| tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS
				|| tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS
				|| tipoInforme == TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO
				|| tipoInforme == TipoInforme.VENTAS_POR_SESION_CAJA
				|| tipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO
				|| tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO
				|| tipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO
				|| tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA) {
			cmbTipoGrafico.addItem(graphBar());
			cmbTipoGrafico.setSelectedItem(graphBar());
			return;
		}

		cmbTipoGrafico.addItem(graphBar());
		cmbTipoGrafico.setSelectedItem(graphBar());
	}


	private void refreshChart() {
	    String selectedGraph = (String) cmbTipoGrafico.getSelectedItem();
	    ModoVistaInforme modoVista = (ModoVistaInforme) cmbModoVista.getSelectedItem();

	    JFreeChart chart;

	    if (tipoInforme == TipoInforme.RESUMEN_EJECUTIVO && resumenEjecutivoResult != null) {

	        chart = graphPie().equalsIgnoreCase(selectedGraph)
	                ? createRealPieChartResumenEjecutivo()
	                : createRealBarChartResumenEjecutivo();

	    } else if (tipoInforme == TipoInforme.VENTAS_POR_DIA && ventasPorDiaResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartVentasPorDia(modoVista)
	                : createRealLineChartVentasPorDia(modoVista);

	    } else if (tipoInforme == TipoInforme.VENTAS_POR_FRANJA_HORARIA && ventasFranjaResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartVentasPorFranja(modoVista)
	                : createRealLineChartVentasPorFranja(modoVista);

	    } else if (tipoInforme == TipoInforme.TICKET_MEDIO_POR_DIA && ticketMedioDiaResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartTicketMedioPorDia(modoVista)
	                : createRealLineChartTicketMedioPorDia(modoVista);

	    } else if (tipoInforme == TipoInforme.PAGOS_POR_METODO && pagosMetodoResult != null) {

	        chart = graphPie().equalsIgnoreCase(selectedGraph)
	                ? createRealPieChartPagosPorMetodo()
	                : createRealBarChartPagosPorMetodo();

	    } else if (tipoInforme == TipoInforme.VENTAS_NETAS_VS_DEVOLUCIONES && netoVsDevolucionesResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartVentasNetasVsDevoluciones()
	                : createRealLineChartVentasNetasVsDevoluciones();

	    } else if (tipoInforme == TipoInforme.PRODUCTOS_MAS_VENDIDOS && productosVendidosResult != null) {

	        chart = createRealBarChartProductosMasVendidos();

	    } else if (tipoInforme == TipoInforme.EXTRAS_MAS_VENDIDOS && extrasVendidosResult != null) {

	        chart = graphPie().equalsIgnoreCase(selectedGraph)
	                ? createRealPieChartExtrasMasVendidos()
	                : createRealBarChartExtrasMasVendidos();

	    } else if (tipoInforme == TipoInforme.COMBOS_VENDIDOS && combosVendidosResult != null) {

	        chart = graphPie().equalsIgnoreCase(selectedGraph)
	                ? createRealPieChartCombosVendidos()
	                : createRealBarChartCombosVendidos();

	    } else if (tipoInforme == TipoInforme.DESCUENTOS_APLICADOS && descuentosAplicadosResult != null) {

	        chart = graphPie().equalsIgnoreCase(selectedGraph)
	                ? createRealPieChartDescuentosAplicados()
	                : createRealBarChartDescuentosAplicados();

	    } else if (tipoInforme == TipoInforme.DEVOLUCIONES_POR_PRODUCTO && devolucionesProductoResult != null) {

	        chart = createRealBarChartDevolucionesPorProducto();

	    } else if (tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS && rankingEmpleadosVentasResult != null) {

	        chart = createRealBarChartRankingEmpleadosPorVentas();

	    } else if (tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS && rankingEmpleadosExtrasResult != null) {

	        chart = createRealBarChartRankingEmpleadosPorExtras();

	    } else if (tipoInforme == TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO && productosPorEmpleadoResult != null) {

	        chart = createRealBarChartProductosVendidosPorEmpleado(modoVista);

	    } else if (tipoInforme == TipoInforme.VENTAS_POR_CAJA && ventasCajaResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartVentasPorCaja()
	                : createRealLineChartVentasPorCaja();

	    } else if (tipoInforme == TipoInforme.VENTAS_POR_SESION_CAJA && ventasSesionCajaResult != null) {

	        chart = createRealBarChartVentasPorSesionCaja();

	    } else if (tipoInforme == TipoInforme.TIEMPOS_POR_ESTACION && tiemposEstacionResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartTiemposPorEstacion()
	                : createRealLineChartTiemposPorEstacion();

	    } else if (tipoInforme == TipoInforme.MERMA_POR_PERIODO && mermaPeriodoResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartMermaPorPeriodo()
	                : createRealLineChartMermaPorPeriodo();

	    } else if (tipoInforme == TipoInforme.MOVIMIENTOS_STOCK_AJUSTES && movimientosStockResult != null) {

	        chart = graphBar().equalsIgnoreCase(selectedGraph)
	                ? createRealBarChartMovimientosStockAjustes()
	                : createRealLineChartMovimientosStockAjustes();

	    } else if (tipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO && ventasProductoEmpleadoResult != null) {

	        chart = createRealBarChartVentasProductoPorEmpleado(modoVista);

	    } else if (tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO && rankingEmpleadosProductoResult != null) {

	        chart = createRealBarChartRankingEmpleadosPorProducto();

	    } else if (tipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO && ventasExtraEmpleadoResult != null) {

	        chart = createRealBarChartVentasExtraPorEmpleado(modoVista);

	    } else if (tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA && rankingEmpleadosExtraResult != null) {

	        chart = createRealBarChartRankingEmpleadosPorExtra();

	    } else {
	        throw new IllegalStateException(I18n.t("reportGraph.error.notImplemented", tipoInforme));
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

	private JFreeChart createRealBarChartVentasPorDia(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeVentasPorDiaRowDTO row : ventasPorDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeVentasPorDiaRowDTO row : ventasPorDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO,
						"Ventas", categoria);
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
						categoria);
			}
		}

		return buildBarChart("Ventas por día", "€", dataset);
	}

	private JFreeChart createRealLineChartVentasPorDia(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeVentasPorDiaRowDTO row : ventasPorDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeVentasPorDiaRowDTO row : ventasPorDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO,
						"Ventas", categoria);
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
						categoria);
			}
		}

		return buildLineChart("Ventas por día", "€", dataset);
	}

	private JFreeChart createRealBarChartPagosPorMetodo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformePagosMetodoRowDTO row : pagosMetodoResult.getRows()) {
			String categoria = row.getMetodoPago() != null ? row.getMetodoPago() : "Método";

			dataset.addValue(row.getImporteTotal() != null ? row.getImporteTotal() : java.math.BigDecimal.ZERO,
					"Importe", categoria);

			dataset.addValue(row.getNumeroOperaciones() != null ? row.getNumeroOperaciones() : 0, "Operaciones",
					categoria);
		}

		return buildBarChart("Pagos por método", "Valor", dataset);
	}

	private JFreeChart createRealPieChartPagosPorMetodo() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		for (InformePagosMetodoRowDTO row : pagosMetodoResult.getRows()) {
			String metodo = row.getMetodoPago() != null ? row.getMetodoPago() : "Método";
			dataset.setValue(metodo, row.getImporteTotal() != null ? row.getImporteTotal() : java.math.BigDecimal.ZERO);
		}

		return buildPieChart("Pagos por método", dataset);
	}

	private JFreeChart createRealBarChartResumenEjecutivo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(resumenEjecutivoResult.getVentasBrutas() != null ? resumenEjecutivoResult.getVentasBrutas()
				: java.math.BigDecimal.ZERO, "Importe", "Ventas brutas");
		dataset.addValue(resumenEjecutivoResult.getDevoluciones() != null ? resumenEjecutivoResult.getDevoluciones()
				: java.math.BigDecimal.ZERO, "Importe", "Devoluciones");
		dataset.addValue(
				resumenEjecutivoResult.getNeto() != null ? resumenEjecutivoResult.getNeto() : java.math.BigDecimal.ZERO,
				"Importe", "Neto");
		dataset.addValue(resumenEjecutivoResult.getAhorroTotal() != null ? resumenEjecutivoResult.getAhorroTotal()
				: java.math.BigDecimal.ZERO, "Importe", "Ahorro combos");

		return buildBarChart("Resumen ejecutivo", "€", dataset);
	}

	private JFreeChart createRealPieChartResumenEjecutivo() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		dataset.setValue("Ventas brutas",
				resumenEjecutivoResult.getVentasBrutas() != null ? resumenEjecutivoResult.getVentasBrutas()
						: java.math.BigDecimal.ZERO);
		dataset.setValue("Devoluciones",
				resumenEjecutivoResult.getDevoluciones() != null ? resumenEjecutivoResult.getDevoluciones()
						: java.math.BigDecimal.ZERO);
		dataset.setValue("Ahorro combos",
				resumenEjecutivoResult.getAhorroTotal() != null ? resumenEjecutivoResult.getAhorroTotal()
						: java.math.BigDecimal.ZERO);

		return buildPieChart("Resumen ejecutivo", dataset);
	}

	private JFreeChart createRealBarChartVentasPorFranja(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeVentasFranjaRowDTO row : ventasFranjaResult.getRows()) {
				String categoria = row.getFranja() != null ? row.getFranja() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeVentasFranjaRowDTO row : ventasFranjaResult.getRows()) {
				String categoria = row.getFranja() != null ? row.getFranja() : "";

				dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO,
						"Ventas", categoria);
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
						categoria);
			}
		}

		return buildBarChart("Ventas por franja horaria", "€", dataset);
	}

	private JFreeChart createRealLineChartVentasPorFranja(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeVentasFranjaRowDTO row : ventasFranjaResult.getRows()) {
				String categoria = row.getFranja() != null ? row.getFranja() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeVentasFranjaRowDTO row : ventasFranjaResult.getRows()) {
				String categoria = row.getFranja() != null ? row.getFranja() : "";

				dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO,
						"Ventas", categoria);
				dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
						categoria);
			}
		}

		return buildLineChart("Ventas por franja horaria", "€", dataset);
	}

	private JFreeChart createRealBarChartTicketMedioPorDia(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeTicketMedioDiaRowDTO row : ticketMedioDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

				dataset.addValue(row.getTicketMedio() != null ? row.getTicketMedio() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeTicketMedioDiaRowDTO row : ticketMedioDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";

				dataset.addValue(row.getTicketMedio() != null ? row.getTicketMedio() : java.math.BigDecimal.ZERO,
						"Ticket medio", categoria);
			}
		}

		return buildBarChart("Ticket medio por día", "€", dataset);
	}

	private JFreeChart createRealLineChartTicketMedioPorDia(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (modoVista == ModoVistaInforme.COMPARATIVA) {
			for (InformeTicketMedioDiaRowDTO row : ticketMedioDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";
				String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

				dataset.addValue(row.getTicketMedio() != null ? row.getTicketMedio() : java.math.BigDecimal.ZERO, serie,
						categoria);
			}
		} else {
			for (InformeTicketMedioDiaRowDTO row : ticketMedioDiaResult.getRows()) {
				String categoria = row.getFecha() != null ? row.getFecha().toString() : "";

				dataset.addValue(row.getTicketMedio() != null ? row.getTicketMedio() : java.math.BigDecimal.ZERO,
						"Ticket medio", categoria);
			}
		}

		return buildLineChart("Ticket medio por día", "€", dataset);
	}

	private JFreeChart createRealBarChartVentasNetasVsDevoluciones() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeNetoVsDevolucionesRowDTO row : netoVsDevolucionesResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toString() : "";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);
			dataset.addValue(
					row.getTotalDevoluciones() != null ? row.getTotalDevoluciones() : java.math.BigDecimal.ZERO,
					"Devoluciones", categoria);
			dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildBarChart("Ventas netas vs devoluciones", "€", dataset);
	}

	private JFreeChart createRealLineChartVentasNetasVsDevoluciones() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeNetoVsDevolucionesRowDTO row : netoVsDevolucionesResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toString() : "";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);
			dataset.addValue(
					row.getTotalDevoluciones() != null ? row.getTotalDevoluciones() : java.math.BigDecimal.ZERO,
					"Devoluciones", categoria);
			dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildLineChart("Ventas netas vs devoluciones", "€", dataset);
	}

	private JFreeChart createRealBarChartProductosMasVendidos() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeProductosVendidosRowDTO row : productosVendidosResult.getRows()) {
			String categoria = row.getNombreProducto() != null ? row.getNombreProducto() : "Producto";

			dataset.addValue(row.getUnidadesVendidas() != null ? row.getUnidadesVendidas() : 0, "Unidades", categoria);

			dataset.addValue(row.getImporteNeto() != null ? row.getImporteNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildBarChart("Productos más vendidos", "Valor", dataset);
	}

	private JFreeChart createRealBarChartExtrasMasVendidos() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeExtrasVendidosRowDTO row : extrasVendidosResult.getRows()) {
			String categoria = row.getNombreExtra() != null ? row.getNombreExtra() : "Extra";

			dataset.addValue(row.getVecesVendido() != null ? row.getVecesVendido() : 0, "Veces", categoria);

			dataset.addValue(row.getImporteGenerado() != null ? row.getImporteGenerado() : java.math.BigDecimal.ZERO,
					"Importe", categoria);
		}

		return buildBarChart("Extras más vendidos", "Valor", dataset);
	}

	private JFreeChart createRealPieChartExtrasMasVendidos() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		for (InformeExtrasVendidosRowDTO row : extrasVendidosResult.getRows()) {
			String categoria = row.getNombreExtra() != null ? row.getNombreExtra() : "Extra";

			dataset.setValue(categoria,
					row.getImporteGenerado() != null ? row.getImporteGenerado() : java.math.BigDecimal.ZERO);
		}

		return buildPieChart("Extras más vendidos", dataset);
	}

	private JFreeChart createRealBarChartCombosVendidos() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeCombosVendidosRowDTO row : combosVendidosResult.getRows()) {
			String categoria = row.getNombreCombo() != null ? row.getNombreCombo() : "Combo";

			dataset.addValue(row.getVecesVendido() != null ? row.getVecesVendido() : 0, "Veces", categoria);

			dataset.addValue(row.getAhorroTotal() != null ? row.getAhorroTotal() : java.math.BigDecimal.ZERO, "Ahorro",
					categoria);
		}

		return buildBarChart("Combos vendidos", "Valor", dataset);
	}

	private JFreeChart createRealPieChartCombosVendidos() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		for (InformeCombosVendidosRowDTO row : combosVendidosResult.getRows()) {
			String categoria = row.getNombreCombo() != null ? row.getNombreCombo() : "Combo";

			dataset.setValue(categoria, row.getVecesVendido() != null ? row.getVecesVendido() : 0);
		}

		return buildPieChart("Combos vendidos", dataset);
	}

	private JFreeChart createRealBarChartDescuentosAplicados() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeDescuentosAplicadosRowDTO row : descuentosAplicadosResult.getRows()) {
			String categoria = row.getNombreDescuento() != null ? row.getNombreDescuento() : "Descuento";

			dataset.addValue(row.getNumeroUsos() != null ? row.getNumeroUsos() : 0, "Usos", categoria);

			dataset.addValue(row.getImporteDescuento() != null ? row.getImporteDescuento() : java.math.BigDecimal.ZERO,
					"Importe descuento", categoria);
		}

		return buildBarChart("Descuentos aplicados", "Valor", dataset);
	}

	private JFreeChart createRealPieChartDescuentosAplicados() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

		for (InformeDescuentosAplicadosRowDTO row : descuentosAplicadosResult.getRows()) {
			String categoria = row.getNombreDescuento() != null ? row.getNombreDescuento() : "Descuento";

			dataset.setValue(categoria, row.getNumeroUsos() != null ? row.getNumeroUsos() : 0);
		}

		return buildPieChart("Descuentos aplicados", dataset);
	}

	private JFreeChart createRealBarChartDevolucionesPorProducto() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeDevolucionesProductoRowDTO row : devolucionesProductoResult.getRows()) {
			String categoria = row.getNombreProducto() != null ? row.getNombreProducto() : "Producto";

			dataset.addValue(row.getCantidadDevuelta() != null ? row.getCantidadDevuelta() : 0, "Cantidad devuelta",
					categoria);

			dataset.addValue(
					row.getImporteReembolsado() != null ? row.getImporteReembolsado() : java.math.BigDecimal.ZERO,
					"Reembolso", categoria);
		}

		return buildBarChart("Devoluciones por producto", "Valor", dataset);
	}

	private JFreeChart createRealBarChartRankingEmpleadosPorVentas() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeRankingEmpleadosVentasRowDTO row : rankingEmpleadosVentasResult.getRows()) {
			String categoria = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);

			dataset.addValue(row.getNumeroTickets() != null ? row.getNumeroTickets() : 0, "Tickets", categoria);
		}

		return buildBarChart("Ranking empleados por ventas", "Valor", dataset);
	}

	private JFreeChart createRealBarChartRankingEmpleadosPorExtras() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeRankingEmpleadosExtrasRowDTO row : rankingEmpleadosExtrasResult.getRows()) {
			String categoria = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getTotalExtrasVendidos() != null ? row.getTotalExtrasVendidos() : 0, "Extras",
					categoria);

			dataset.addValue(row.getImporteExtras() != null ? row.getImporteExtras() : java.math.BigDecimal.ZERO,
					"Importe", categoria);
		}

		return buildBarChart("Ranking empleados por extras", "Valor", dataset);
	}

	private JFreeChart createRealBarChartProductosVendidosPorEmpleado(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeProductosPorEmpleadoRowDTO row : productosPorEmpleadoResult.getRows()) {
			String categoria = row.getNombreProducto() != null ? row.getNombreProducto() : "Producto";
			String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getUnidadesVendidas() != null ? row.getUnidadesVendidas() : 0, serie, categoria);
		}

		return buildBarChart("Productos vendidos por empleado", "Unidades", dataset);
	}

	private JFreeChart createRealBarChartVentasPorCaja() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeVentasCajaRowDTO row : ventasCajaResult.getRows()) {
			String categoria = row.getNombreCaja() != null ? row.getNombreCaja() : "Caja";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);

			dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildBarChart("Ventas por caja", "€", dataset);
	}

	private JFreeChart createRealLineChartVentasPorCaja() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeVentasCajaRowDTO row : ventasCajaResult.getRows()) {
			String categoria = row.getNombreCaja() != null ? row.getNombreCaja() : "Caja";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);

			dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildLineChart("Ventas por caja", "€", dataset);
	}

	private JFreeChart createRealBarChartVentasPorSesionCaja() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeVentasSesionCajaRowDTO row : ventasSesionCajaResult.getRows()) {
			String categoria = row.getIdSesion() != null ? "#" + row.getIdSesion() : "Sesión";

			dataset.addValue(row.getTotalVentas() != null ? row.getTotalVentas() : java.math.BigDecimal.ZERO, "Ventas",
					categoria);

			dataset.addValue(row.getTotalNeto() != null ? row.getTotalNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildBarChart("Ventas por sesión de caja", "€", dataset);
	}

	private JFreeChart createRealBarChartTiemposPorEstacion() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeTiemposEstacionRowDTO row : tiemposEstacionResult.getRows()) {
			String categoria = row.getNombreEstacion() != null ? row.getNombreEstacion() : "Estación";

			dataset.addValue(
					row.getTiempoMedioSegundos() != null ? row.getTiempoMedioSegundos() : java.math.BigDecimal.ZERO,
					"Tiempo medio", categoria);

			dataset.addValue(
					row.getTiempoMaximoSegundos() != null ? row.getTiempoMaximoSegundos() : java.math.BigDecimal.ZERO,
					"Tiempo máximo", categoria);
		}

		return buildBarChart("Tiempos por estación", "Segundos", dataset);
	}

	private JFreeChart createRealLineChartTiemposPorEstacion() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeTiemposEstacionRowDTO row : tiemposEstacionResult.getRows()) {
			String categoria = row.getNombreEstacion() != null ? row.getNombreEstacion() : "Estación";

			dataset.addValue(
					row.getTiempoMedioSegundos() != null ? row.getTiempoMedioSegundos() : java.math.BigDecimal.ZERO,
					"Tiempo medio", categoria);

			dataset.addValue(
					row.getTiempoMaximoSegundos() != null ? row.getTiempoMaximoSegundos() : java.math.BigDecimal.ZERO,
					"Tiempo máximo", categoria);
		}

		return buildLineChart("Tiempos por estación", "Segundos", dataset);
	}

	private JFreeChart createRealBarChartMermaPorPeriodo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeMermaPeriodoRowDTO row : mermaPeriodoResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toString() : "Fecha";

			dataset.addValue(row.getCantidad() != null ? row.getCantidad() : java.math.BigDecimal.ZERO, "Cantidad",
					categoria);
		}

		return buildBarChart("Merma por período", "Cantidad", dataset);
	}

	private JFreeChart createRealLineChartMermaPorPeriodo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeMermaPeriodoRowDTO row : mermaPeriodoResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toString() : "Fecha";

			dataset.addValue(row.getCantidad() != null ? row.getCantidad() : java.math.BigDecimal.ZERO, "Cantidad",
					categoria);
		}

		return buildLineChart("Merma por período", "Cantidad", dataset);
	}

	private JFreeChart createRealBarChartMovimientosStockAjustes() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeMovimientoStockRowDTO row : movimientosStockResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toLocalDate().toString() : "Fecha";
			String serie = row.getTipoMovimiento() != null ? row.getTipoMovimiento() : "Movimiento";

			dataset.addValue(row.getCantidad() != null ? row.getCantidad() : java.math.BigDecimal.ZERO, serie,
					categoria);
		}

		return buildBarChart("Movimientos de stock / ajustes", "Cantidad", dataset);
	}

	private JFreeChart createRealLineChartMovimientosStockAjustes() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeMovimientoStockRowDTO row : movimientosStockResult.getRows()) {
			String categoria = row.getFecha() != null ? row.getFecha().toLocalDate().toString() : "Fecha";
			String serie = row.getTipoMovimiento() != null ? row.getTipoMovimiento() : "Movimiento";

			dataset.addValue(row.getCantidad() != null ? row.getCantidad() : java.math.BigDecimal.ZERO, serie,
					categoria);
		}

		return buildLineChart("Movimientos de stock / ajustes", "Cantidad", dataset);
	}

	private JFreeChart createRealBarChartVentasProductoPorEmpleado(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeVentasProductoEmpleadoRowDTO row : ventasProductoEmpleadoResult.getRows()) {
			String categoria = row.getNombreProducto() != null ? row.getNombreProducto() : "Producto";
			String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getUnidadesVendidas() != null ? row.getUnidadesVendidas() : 0, serie, categoria);
		}

		return buildBarChart("Ventas producto por empleado", "Unidades", dataset);
	}

	private JFreeChart createRealBarChartRankingEmpleadosPorProducto() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeRankingEmpleadosProductoRowDTO row : rankingEmpleadosProductoResult.getRows()) {
			String categoria = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getUnidadesVendidas() != null ? row.getUnidadesVendidas() : 0, "Unidades", categoria);

			dataset.addValue(row.getImporteNeto() != null ? row.getImporteNeto() : java.math.BigDecimal.ZERO, "Neto",
					categoria);
		}

		return buildBarChart("Ranking empleados por producto", "Valor", dataset);
	}

	private JFreeChart createRealBarChartVentasExtraPorEmpleado(ModoVistaInforme modoVista) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeVentasExtraEmpleadoRowDTO row : ventasExtraEmpleadoResult.getRows()) {
			String categoria = row.getNombreExtra() != null ? row.getNombreExtra() : "Extra";
			String serie = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getVecesVendido() != null ? row.getVecesVendido() : 0, serie, categoria);
		}

		return buildBarChart("Ventas extra por empleado", "Veces", dataset);
	}

	private JFreeChart createRealBarChartRankingEmpleadosPorExtra() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (InformeRankingEmpleadosExtraRowDTO row : rankingEmpleadosExtraResult.getRows()) {
			String categoria = row.getNombreEmpleado() != null ? row.getNombreEmpleado() : "Empleado";

			dataset.addValue(row.getVecesVendido() != null ? row.getVecesVendido() : 0, "Veces", categoria);

			dataset.addValue(row.getImporteGenerado() != null ? row.getImporteGenerado() : java.math.BigDecimal.ZERO,
					"Importe", categoria);
		}

		return buildBarChart("Ranking empleados por extra", "Valor", dataset);
	}

	private JFreeChart createPlaceholderChart(String title) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(0, "Pendiente", "Sin datos");
		return buildBarChart(title, "", dataset);
	}
}

package ui.screens;

import dtoS.InformeFiltroDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformePdfExportRequest;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.TpvDialogUtils;
import ui.dialog.InformeGraficoDialog;
import ui.informes.InformeFiltrosPanel;
import ui.informes.InformeKpiPanel;
import ui.informes.InformeTablaPanel;
import ui.informes.InformeToolbarPanel;
import ui.theme.InformeUiTheme;
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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.AppContext;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InformesFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    private InformeToolbarPanel toolbarPanel;
    private InformeFiltrosPanel filtrosPanel;
    private InformeKpiPanel kpiPanel;
    private InformeTablaPanel tablaPanel;

    private TipoInforme currentTipoInforme;
    private boolean generated;
    private InformeResumenEjecutivoResultDTO currentResumenEjecutivoResult;
    private InformeVentasPorDiaResultDTO currentVentasPorDiaResult;
    private InformeVentasFranjaResultDTO currentVentasFranjaResult;
    private InformeTicketMedioDiaResultDTO currentTicketMedioDiaResult;
    private InformePagosMetodoResultDTO currentPagosMetodoResult;
    private InformeNetoVsDevolucionesResultDTO currentNetoVsDevolucionesResult;
    private InformeProductosVendidosResultDTO currentProductosVendidosResult;
    private InformeExtrasVendidosResultDTO currentExtrasVendidosResult;
    private InformeCombosVendidosResultDTO currentCombosVendidosResult;
    private InformeDescuentosAplicadosResultDTO currentDescuentosAplicadosResult;
    private InformeDevolucionesProductoResultDTO currentDevolucionesProductoResult;
    private InformeRankingEmpleadosVentasResultDTO currentRankingEmpleadosVentasResult;
    private InformeRankingEmpleadosExtrasResultDTO currentRankingEmpleadosExtrasResult;
    private InformeProductosPorEmpleadoResultDTO currentProductosPorEmpleadoResult;

    private InformeVentasCajaResultDTO currentVentasCajaResult;
    private InformeVentasSesionCajaResultDTO currentVentasSesionCajaResult;
    private InformeTiemposEstacionResultDTO currentTiemposEstacionResult;
    private InformeMermaPeriodoResultDTO currentMermaPeriodoResult;
    private InformeMovimientoStockResultDTO currentMovimientosStockResult;
    
    private InformeVentasProductoEmpleadoResultDTO currentVentasProductoEmpleadoResult;
    private InformeRankingEmpleadosProductoResultDTO currentRankingEmpleadosProductoResult;
    private InformeVentasExtraEmpleadoResultDTO currentVentasExtraEmpleadoResult;
    private InformeRankingEmpleadosExtraResultDTO currentRankingEmpleadosExtraResult;

    private ModoVistaInforme currentGeneratedModoVista;
    
    
    public InformesFrame(Runnable onLogoutNavigate,
                         Runnable onBack,
                         AppServices services,
                         TipoInforme tipoInicial) {
        super("Centro de Informes", onLogoutNavigate, services);
        this.onBack = onBack;
        this.services = services;

        requireAuthenticatedOrExit();

        buildUI();
        refreshHeader();

        currentTipoInforme = tipoInicial != null ? tipoInicial : TipoInforme.RESUMEN_EJECUTIVO;
        toolbarPanel.setSelectedTipoInforme(currentTipoInforme);
        applyTipoInforme(currentTipoInforme);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(InformeUiTheme.APP_BG);

        toolbarPanel = new InformeToolbarPanel();
        filtrosPanel = new InformeFiltrosPanel(services);
        kpiPanel = new InformeKpiPanel();
        tablaPanel = new InformeTablaPanel();

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(kpiPanel, BorderLayout.NORTH);
        center.add(tablaPanel, BorderLayout.CENTER);

        root.add(toolbarPanel, BorderLayout.NORTH);
        root.add(filtrosPanel, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);

        main.add(root, BorderLayout.CENTER);

        wireEvents();
    }

    private void wireEvents() {
        toolbarPanel.onTipoInformeChanged(e -> {
            TipoInforme selected = toolbarPanel.getSelectedTipoInforme();
            applyTipoInforme(selected);
        });

        toolbarPanel.onGenerar(e -> generateMock());
        toolbarPanel.onLimpiar(e -> resetCurrentVisualState());
        toolbarPanel.onVerGrafico(e -> openGraphDialog());

        toolbarPanel.onExportarPdf(e -> exportarPdfActual());
        toolbarPanel.onVolver(e -> {
            safeDispose();
            if (onBack != null) {
                onBack.run();
            }
        });
    }

    private void applyTipoInforme(TipoInforme tipoInforme) {
        currentTipoInforme = tipoInforme;

        clearAllCurrentResults();

        toolbarPanel.updateSubtitle(tipoInforme);
        filtrosPanel.setTipoInforme(tipoInforme);

        generated = false;
        toolbarPanel.setGraficoEnabled(false);

        kpiPanel.showPlaceholderempty(tipoInforme);
        tablaPanel.showEmpty("Configura los filtros y pulsa Generar para visualizar este informe.");
    }
    private void resetCurrentVisualState() {
        filtrosPanel.resetCurrent();
        clearAllCurrentResults();
        generated = false;
        toolbarPanel.setGraficoEnabled(false);
        kpiPanel.showPlaceholderempty(currentTipoInforme);
        tablaPanel.showEmpty("Estado reiniciado. Pulsa Generar para reconstruir la vista.");
    }
    private void generateMock() {
        // 1) Construimos el DTO universal de filtros desde la UI
        InformeFiltroDTO filtroDTO = filtrosPanel.buildFiltroDTO();
        generated = true;

        try {
            // =====================================================
            // RESUMEN EJECUTIVO
            // =====================================================
            if (currentTipoInforme == TipoInforme.RESUMEN_EJECUTIVO) {
                InformeResumenEjecutivoResultDTO result = services.informesService.getResumenEjecutivo(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentResumenEjecutivoResult = result;

                kpiPanel.cargarResumenEjecutivo(result);
                tablaPanel.cargarResumenEjecutivo(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS POR DÍA
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_POR_DIA) {
                InformeVentasPorDiaResultDTO result = services.informesService.getVentasPorDia(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasPorDiaResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarVentasPorDia(result);
                tablaPanel.cargarVentasPorDia(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS POR FRANJA HORARIA
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_POR_FRANJA_HORARIA) {
                InformeVentasFranjaResultDTO result = services.informesService.getVentasPorFranjaHoraria(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasFranjaResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarVentasPorFranjaHoraria(result);
                tablaPanel.cargarVentasPorFranjaHoraria(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // TICKET MEDIO POR DÍA
            // =====================================================
            if (currentTipoInforme == TipoInforme.TICKET_MEDIO_POR_DIA) {
                InformeTicketMedioDiaResultDTO result = services.informesService.getTicketMedioPorDia(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentTicketMedioDiaResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarTicketMedioPorDia(result);
                tablaPanel.cargarTicketMedioPorDia(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // PAGOS POR MÉTODO
            // =====================================================
            if (currentTipoInforme == TipoInforme.PAGOS_POR_METODO) {
                InformePagosMetodoResultDTO result = services.informesService.getPagosPorMetodo(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentPagosMetodoResult = result;

                kpiPanel.cargarPagosPorMetodo(result);
                tablaPanel.cargarPagosPorMetodo(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS NETAS VS DEVOLUCIONES
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_NETAS_VS_DEVOLUCIONES) {
                InformeNetoVsDevolucionesResultDTO result = services.informesService.getVentasNetasVsDevoluciones(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentNetoVsDevolucionesResult = result;

                kpiPanel.cargarVentasNetasVsDevoluciones(result);
                tablaPanel.cargarVentasNetasVsDevoluciones(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }
            // =====================================================
            // PRODUCTOS MÁS VENDIDOS
            // =====================================================
            if (currentTipoInforme == TipoInforme.PRODUCTOS_MAS_VENDIDOS) {
                InformeProductosVendidosResultDTO result = services.informesService.getProductosMasVendidos(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentProductosVendidosResult = result;

                kpiPanel.cargarProductosMasVendidos(result);
                tablaPanel.cargarProductosMasVendidos(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // EXTRAS MÁS VENDIDOS
            // =====================================================
            if (currentTipoInforme == TipoInforme.EXTRAS_MAS_VENDIDOS) {
                InformeExtrasVendidosResultDTO result = services.informesService.getExtrasMasVendidos(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentExtrasVendidosResult = result;

                kpiPanel.cargarExtrasMasVendidos(result);
                tablaPanel.cargarExtrasMasVendidos(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // COMBOS VENDIDOS
            // =====================================================
            if (currentTipoInforme == TipoInforme.COMBOS_VENDIDOS) {
                InformeCombosVendidosResultDTO result = services.informesService.getCombosVendidos(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentCombosVendidosResult = result;

                kpiPanel.cargarCombosVendidos(result);
                tablaPanel.cargarCombosVendidos(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // DESCUENTOS APLICADOS
            // =====================================================
            if (currentTipoInforme == TipoInforme.DESCUENTOS_APLICADOS) {
                InformeDescuentosAplicadosResultDTO result = services.informesService.getDescuentosAplicados(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentDescuentosAplicadosResult = result;

                kpiPanel.cargarDescuentosAplicados(result);
                tablaPanel.cargarDescuentosAplicados(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // DEVOLUCIONES POR PRODUCTO
            // =====================================================
            if (currentTipoInforme == TipoInforme.DEVOLUCIONES_POR_PRODUCTO) {
                InformeDevolucionesProductoResultDTO result = services.informesService.getDevolucionesPorProducto(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentDevolucionesProductoResult = result;

                kpiPanel.cargarDevolucionesPorProducto(result);
                tablaPanel.cargarDevolucionesPorProducto(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }
            // =====================================================
            // RANKING EMPLEADOS POR VENTAS
            // =====================================================
            if (currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS) {
                InformeRankingEmpleadosVentasResultDTO result = services.informesService.getRankingEmpleadosPorVentas(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentRankingEmpleadosVentasResult = result;

                kpiPanel.cargarRankingEmpleadosPorVentas(result);
                tablaPanel.cargarRankingEmpleadosPorVentas(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // RANKING EMPLEADOS POR EXTRAS
            // =====================================================
            if (currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS) {
                InformeRankingEmpleadosExtrasResultDTO result = services.informesService.getRankingEmpleadosPorExtras(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentRankingEmpleadosExtrasResult = result;

                kpiPanel.cargarRankingEmpleadosPorExtras(result);
                tablaPanel.cargarRankingEmpleadosPorExtras(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // PRODUCTOS VENDIDOS POR EMPLEADO
            // =====================================================
            if (currentTipoInforme == TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO) {
                InformeProductosPorEmpleadoResultDTO result = services.informesService.getProductosVendidosPorEmpleado(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentProductosPorEmpleadoResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarProductosVendidosPorEmpleado(result);
                tablaPanel.cargarProductosVendidosPorEmpleado(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS POR CAJA
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_POR_CAJA) {
                InformeVentasCajaResultDTO result = services.informesService.getVentasPorCaja(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasCajaResult = result;

                kpiPanel.cargarVentasPorCaja(result);
                tablaPanel.cargarVentasPorCaja(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS POR SESIÓN DE CAJA
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_POR_SESION_CAJA) {
                InformeVentasSesionCajaResultDTO result = services.informesService.getVentasPorSesionCaja(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasSesionCajaResult = result;

                kpiPanel.cargarVentasPorSesionCaja(result);
                tablaPanel.cargarVentasPorSesionCaja(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // TIEMPOS POR ESTACIÓN
            // =====================================================
            if (currentTipoInforme == TipoInforme.TIEMPOS_POR_ESTACION) {
                InformeTiemposEstacionResultDTO result = services.informesService.getTiemposPorEstacion(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentTiemposEstacionResult = result;

                kpiPanel.cargarTiemposPorEstacion(result);
                tablaPanel.cargarTiemposPorEstacion(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // MERMA POR PERÍODO
            // =====================================================
            if (currentTipoInforme == TipoInforme.MERMA_POR_PERIODO) {
                InformeMermaPeriodoResultDTO result = services.informesService.getMermaPorPeriodo(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentMermaPeriodoResult = result;

                kpiPanel.cargarMermaPorPeriodo(result);
                tablaPanel.cargarMermaPorPeriodo(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // MOVIMIENTOS DE STOCK / AJUSTES
            // =====================================================
            if (currentTipoInforme == TipoInforme.MOVIMIENTOS_STOCK_AJUSTES) {
                InformeMovimientoStockResultDTO result = services.informesService.getMovimientosStockAjustes(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentMovimientosStockResult = result;

                kpiPanel.cargarMovimientosStockAjustes(result);
                tablaPanel.cargarMovimientosStockAjustes(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }
            // =====================================================
            // VENTAS PRODUCTO POR EMPLEADO
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO) {
                InformeVentasProductoEmpleadoResultDTO result = services.informesService.getVentasProductoPorEmpleado(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasProductoEmpleadoResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarVentasProductoPorEmpleado(result);
                tablaPanel.cargarVentasProductoPorEmpleado(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // RANKING EMPLEADOS POR PRODUCTO
            // =====================================================
            if (currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO) {
                InformeRankingEmpleadosProductoResultDTO result = services.informesService.getRankingEmpleadosPorProducto(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentRankingEmpleadosProductoResult = result;

                kpiPanel.cargarRankingEmpleadosPorProducto(result);
                tablaPanel.cargarRankingEmpleadosPorProducto(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // VENTAS EXTRA POR EMPLEADO
            // =====================================================
            if (currentTipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO) {
                InformeVentasExtraEmpleadoResultDTO result = services.informesService.getVentasExtraPorEmpleado(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentVentasExtraEmpleadoResult = result;
                currentGeneratedModoVista = filtroDTO.getModoVista();

                kpiPanel.cargarVentasExtraPorEmpleado(result);
                tablaPanel.cargarVentasExtraPorEmpleado(result, currentGeneratedModoVista);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }

            // =====================================================
            // RANKING EMPLEADOS POR EXTRA
            // =====================================================
            if (currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA) {
                InformeRankingEmpleadosExtraResultDTO result = services.informesService.getRankingEmpleadosPorExtra(filtroDTO);

                if (result.getRows() == null || result.getRows().isEmpty()) {
                    clearAllCurrentResults();
                    generated = false;
                    toolbarPanel.setGraficoEnabled(false);
                    kpiPanel.showPlaceholderempty(currentTipoInforme);
                    tablaPanel.showEmpty("No hay resultados para los filtros seleccionados.");
                    return;
                }

                clearAllCurrentResults();
                currentRankingEmpleadosExtraResult = result;

                kpiPanel.cargarRankingEmpleadosPorExtra(result);
                tablaPanel.cargarRankingEmpleadosPorExtra(result);
                toolbarPanel.setGraficoEnabled(true);
                return;
            }
            

            // =====================================================
            // RESTO DE INFORMES - placeholder
            // =====================================================
            clearAllCurrentResults();
            kpiPanel.showPlaceholderempty(currentTipoInforme);
            tablaPanel.showPlaceholder(currentTipoInforme, filtroDTO.getModoVista());
            toolbarPanel.setGraficoEnabled(true);

        } catch (Exception e) {
            clearAllCurrentResults();
            generated = false;
            toolbarPanel.setGraficoEnabled(false);

            TpvDialogUtils.showError(
                    this,
                    "Error",
                    "Error al generar el informe:\n" + e.getMessage()
            );
            e.printStackTrace();
        }
    }
    private void clearAllCurrentResults() {
        // Bloque 1
        currentResumenEjecutivoResult = null;
        currentVentasPorDiaResult = null;
        currentVentasFranjaResult = null;
        currentTicketMedioDiaResult = null;
        currentPagosMetodoResult = null;
        currentNetoVsDevolucionesResult = null;

        // Bloque 2
        currentProductosVendidosResult = null;
        currentExtrasVendidosResult = null;
        currentCombosVendidosResult = null;
        currentDescuentosAplicadosResult = null;
        currentDevolucionesProductoResult = null;

        // Bloque 3
        currentRankingEmpleadosVentasResult = null;
        currentRankingEmpleadosExtrasResult = null;
        currentProductosPorEmpleadoResult = null;

        // Bloque 4
        currentVentasCajaResult = null;
        currentVentasSesionCajaResult = null;
        currentTiemposEstacionResult = null;
        currentMermaPeriodoResult = null;
        currentMovimientosStockResult = null;

        // Bloque 5
        currentVentasProductoEmpleadoResult = null;
        currentRankingEmpleadosProductoResult = null;
        currentVentasExtraEmpleadoResult = null;
        currentRankingEmpleadosExtraResult = null;

        currentGeneratedModoVista = null;
    }
    
    private void openGraphDialog() {
        if (!generated) {
        	TpvDialogUtils.showWarning(
        	        this,
        	        "Gráfico no disponible",
        	        "Primero genera el informe para poder visualizar el gráfico."
        	);
            return;
        }

        InformeGraficoDialog dialog = new InformeGraficoDialog(
                this,
                currentTipoInforme,
                currentGeneratedModoVista != null ? currentGeneratedModoVista : filtrosPanel.getCurrentModoVista(),
                filtrosPanel.getCurrentFilterSummary(),

                // Bloque 1
                currentResumenEjecutivoResult,
                currentVentasPorDiaResult,
                currentVentasFranjaResult,
                currentTicketMedioDiaResult,
                currentPagosMetodoResult,
                currentNetoVsDevolucionesResult,

                // Bloque 2
                currentProductosVendidosResult,
                currentExtrasVendidosResult,
                currentCombosVendidosResult,
                currentDescuentosAplicadosResult,
                currentDevolucionesProductoResult,

                // Bloque 3
                currentRankingEmpleadosVentasResult,
                currentRankingEmpleadosExtrasResult,
                currentProductosPorEmpleadoResult,

                // Bloque 4
                currentVentasCajaResult,
                currentVentasSesionCajaResult,
                currentTiemposEstacionResult,
                currentMermaPeriodoResult,
                currentMovimientosStockResult,

                // Bloque 5
                currentVentasProductoEmpleadoResult,
                currentRankingEmpleadosProductoResult,
                currentVentasExtraEmpleadoResult,
                currentRankingEmpleadosExtraResult
        );

        dialog.setVisible(true);
    }
    
    private void exportarPdfActual() {
        if (!generated) {
        	TpvDialogUtils.showWarning(
        	        this,
        	        "PDF no disponible",
        	        "Primero genera el informe antes de exportarlo a PDF."
        	);
            return;
        }

        Object currentResult = resolveCurrentResultOrNull();
        if (currentResult == null) {
        	TpvDialogUtils.showWarning(
        	        this,
        	        "PDF no disponible",
        	        "No hay datos del informe actual para exportar."
        	);
            return;
        }

        File selectedFile = choosePdfDestination();
        if (selectedFile == null) {
            return;
        }

        try {
            String usuarioGenerador = "Usuario";
            if (AppContext.isAuthenticated()
                    && AppContext.getUsuario() != null
                    && AppContext.getUsuario().getNombre() != null) {
                usuarioGenerador = AppContext.getUsuario().getNombre();
            }

            InformeFiltroDTO filtroDTO = filtrosPanel.buildFiltroDTO();

            InformePdfExportRequest request = new InformePdfExportRequest();
            request.setTipoInforme(currentTipoInforme);
            request.setModoVista(
                    currentGeneratedModoVista != null
                            ? currentGeneratedModoVista
                            : filtrosPanel.getCurrentModoVista()
            );
            request.setResumenFiltros(filtrosPanel.getCurrentFilterSummary());
            request.setFiltroDTO(filtroDTO);
            request.setUsuarioGenerador(usuarioGenerador);
            request.setFechaGeneracion(LocalDateTime.now());
            request.setResult(currentResult);

            File pdfGenerado = services.informePdfService.exportarInforme(request, selectedFile);

            TpvDialogUtils.showInfo(
                    this,
                    "Exportación completada",
                    "PDF generado correctamente:\n" + pdfGenerado.getAbsolutePath()
            );

        } catch (Exception ex) {
        	TpvDialogUtils.showError(
        	        this,
        	        "Error",
        	        "Error al exportar el PDF:\n" + ex.getMessage()
        	);
            ex.printStackTrace();
        }
    }

    private File choosePdfDestination() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar informe en PDF");
        chooser.setSelectedFile(new File(buildDefaultPdfFileName()));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File selected = chooser.getSelectedFile();
        if (selected == null) {
            return null;
        }

        // Confirmación de sobrescritura si el archivo ya existe
        File normalized = selected.getName().toLowerCase().endsWith(".pdf")
                ? selected
                : new File(selected.getParentFile(), selected.getName() + ".pdf");

        if (normalized.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe.\n¿Quieres sobrescribirlo?",
                    "Confirmar sobrescritura",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (overwrite != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return normalized;
    }

    private String buildDefaultPdfFileName() {
        String nombreBase = currentTipoInforme != null
                ? currentTipoInforme.name().toLowerCase()
                : "informe";

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));

        return "informe_" + nombreBase + "_" + timestamp + ".pdf";
    }

    private Object resolveCurrentResultOrNull() {
        return switch (currentTipoInforme) {
            case VENTAS_POR_DIA -> currentVentasPorDiaResult;
            case RESUMEN_EJECUTIVO -> currentResumenEjecutivoResult;
            case VENTAS_POR_FRANJA_HORARIA -> currentVentasFranjaResult;
            case TICKET_MEDIO_POR_DIA -> currentTicketMedioDiaResult;
            case PAGOS_POR_METODO -> currentPagosMetodoResult;
            case VENTAS_NETAS_VS_DEVOLUCIONES -> currentNetoVsDevolucionesResult;
            case PRODUCTOS_MAS_VENDIDOS -> currentProductosVendidosResult;
            case EXTRAS_MAS_VENDIDOS -> currentExtrasVendidosResult;
            case COMBOS_VENDIDOS -> currentCombosVendidosResult;
            case DESCUENTOS_APLICADOS -> currentDescuentosAplicadosResult;
            case DEVOLUCIONES_POR_PRODUCTO -> currentDevolucionesProductoResult;
            case RANKING_EMPLEADOS_POR_VENTAS -> currentRankingEmpleadosVentasResult;
            case RANKING_EMPLEADOS_POR_EXTRAS -> currentRankingEmpleadosExtrasResult;
            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> currentProductosPorEmpleadoResult;
            case VENTAS_POR_CAJA -> currentVentasCajaResult;
            case VENTAS_POR_SESION_CAJA -> currentVentasSesionCajaResult;
            case TIEMPOS_POR_ESTACION -> currentTiemposEstacionResult;
            case MERMA_POR_PERIODO -> currentMermaPeriodoResult;
            case MOVIMIENTOS_STOCK_AJUSTES -> currentMovimientosStockResult;
            case VENTAS_PRODUCTO_POR_EMPLEADO -> currentVentasProductoEmpleadoResult;
            case RANKING_EMPLEADOS_POR_PRODUCTO -> currentRankingEmpleadosProductoResult;
            case VENTAS_EXTRA_POR_EMPLEADO -> currentVentasExtraEmpleadoResult;
            case RANKING_EMPLEADOS_POR_EXTRA -> currentRankingEmpleadosExtraResult;
        };
    }
}
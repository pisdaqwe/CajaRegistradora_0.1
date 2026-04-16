package ui.screens;

import dtoS.InformeFiltroDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.dialog.InformeGraficoDialog;
import ui.informes.InformeFiltrosPanel;
import ui.informes.InformeKpiPanel;
import ui.informes.InformeTablaPanel;
import ui.informes.InformeToolbarPanel;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InformesFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices services;

    private InformeToolbarPanel toolbarPanel;
    private InformeFiltrosPanel filtrosPanel;
    private InformeKpiPanel kpiPanel;
    private InformeTablaPanel tablaPanel;

    private TipoInforme currentTipoInforme;
    private boolean generated;

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

        toolbarPanel.onExportarPdf(e -> JOptionPane.showMessageDialog(
                this,
                "PDF pendiente.\nPrimero cerramos la UI y luego metemos la lógica real.",
                "Pendiente",
                JOptionPane.INFORMATION_MESSAGE
        ));

        toolbarPanel.onVolver(e -> {
            safeDispose();
            if (onBack != null) {
                onBack.run();
            }
        });
    }

    private void applyTipoInforme(TipoInforme tipoInforme) {
        currentTipoInforme = tipoInforme;

        toolbarPanel.updateSubtitle(tipoInforme);
        filtrosPanel.setTipoInforme(tipoInforme);

        generated = false;
        toolbarPanel.setGraficoEnabled(false);

        kpiPanel.showPlaceholder(tipoInforme);
        tablaPanel.showEmpty("Configura los filtros y pulsa Generar para visualizar este informe.");
    }

    private void resetCurrentVisualState() {
        filtrosPanel.resetCurrent();
        generated = false;
        toolbarPanel.setGraficoEnabled(false);
        kpiPanel.showPlaceholder(currentTipoInforme);
        tablaPanel.showEmpty("Estado reiniciado. Pulsa Generar para reconstruir la vista.");
    }

    private void generateMock() {
        InformeFiltroDTO filtroDTO = filtrosPanel.buildFiltroDTO();
        generated = true;

        try {
            if (currentTipoInforme == TipoInforme.VENTAS_POR_DIA) {
                InformeVentasPorDiaResultDTO result = services.informesService.getVentasPorDia(filtroDTO);

                kpiPanel.cargarVentasPorDia(result);
                tablaPanel.cargarVentasPorDia(result, filtroDTO.getModoVista());
                toolbarPanel.setGraficoEnabled(true);

                System.out.println("Informe real generado: " + filtroDTO.getTipoInforme());
                System.out.println("Modo vista: " + filtroDTO.getModoVista());
                System.out.println("Fecha desde: " + filtroDTO.getFechaDesde());
                System.out.println("Fecha hasta: " + filtroDTO.getFechaHasta());
                System.out.println("Id sucursal: " + filtroDTO.getIdSucursal());
                System.out.println("Id caja: " + filtroDTO.getIdCaja());
                System.out.println("Todos empleados: " + filtroDTO.isTodosLosEmpleados());
                System.out.println("Ids empleados: " + filtroDTO.getIdsEmpleados());
                System.out.println("Método pago: " + filtroDTO.getMetodoPago());
                System.out.println("Incluir devoluciones: " + filtroDTO.isIncluirDevoluciones());
                return;
            }

            kpiPanel.showPlaceholder(currentTipoInforme);
            tablaPanel.showPlaceholder(currentTipoInforme, filtroDTO.getModoVista());
            toolbarPanel.setGraficoEnabled(true);

        } catch (Exception e) {
            generated = false;
            toolbarPanel.setGraficoEnabled(false);

            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar el informe:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    private void openGraphDialog() {
        if (!generated) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero genera el informe para poder visualizar el gráfico.",
                    "Gráfico no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        InformeGraficoDialog dialog = new InformeGraficoDialog(
                this,
                currentTipoInforme,
                filtrosPanel.getCurrentModoVista(),
                filtrosPanel.getCurrentFilterSummary()
        );
        dialog.setVisible(true);
    }
}
package ui.screens;



import dtoS.InformeFiltroDTO;
import enums.TipoInforme;
import service.AppServices;
import ui.common.BaseTpvFrame;
import ui.common.InformeUiTheme;
import ui.informes.InformeFiltrosPanel;
import ui.informes.InformeGraficoPanel;
import ui.informes.InformeKpiPanel;
import ui.informes.InformeTablaPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pantalla principal del módulo de informes.
 *
 * Esta es la primera versión visual profesional:
 * - filtros a la izquierda
 * - KPIs arriba en el centro
 * - tabla abajo en el centro
 * - gráfico a la derecha
 *
 * De momento:
 * - no hay SQL real
 * - no hay PDF real
 * - sí hay datos demo para validar diseño y flujo
 */
public class InformesFrame extends BaseTpvFrame {

    private final Runnable onBack;
    private final AppServices appServices;

    private InformeFiltrosPanel filtrosPanel;
    private InformeKpiPanel kpiPanel;
    private InformeTablaPanel tablaPanel;
    private InformeGraficoPanel graficoPanel;

    public InformesFrame(Runnable onLogoutNavigate,
                         Runnable onBack,
                         AppServices services,
                         TipoInforme tipoInicial) {
        super("Centro de Informes", onLogoutNavigate, services);
        this.onBack = onBack;
        this.appServices = services;

        requireAuthenticatedOrExit();

        buildUI();
        refreshHeader();

        if (tipoInicial != null) {
            filtrosPanel.setTipoInforme(tipoInicial);
        }

        generarVistaDemo();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(InformeUiTheme.APP_BG);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);

        main.add(root, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel topBar = InformeUiTheme.createCardPanel(new BorderLayout(16, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Explorador de Informes");
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Panel analítico visual · KPIs · tabla · gráfico · exportación futura");
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSubtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnGenerar = new JButton("Generar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnPdf = new JButton("Exportar PDF");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnVolver = new JButton("Volver");

        InformeUiTheme.stylePrimaryButton(btnGenerar);
        InformeUiTheme.styleSecondaryButton(btnLimpiar);
        InformeUiTheme.styleSecondaryButton(btnPdf);
        InformeUiTheme.styleSecondaryButton(btnImprimir);
        InformeUiTheme.styleDangerButton(btnVolver);

        btnGenerar.addActionListener(e -> generarVistaDemo());
        btnLimpiar.addActionListener(e -> {
            filtrosPanel.reset();
            generarVistaDemo();
        });

        btnPdf.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Exportación PDF pendiente.\nPrimero vamos a cerrar la UI y luego conectamos PDFBox.",
                        "PDF pendiente",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        btnImprimir.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Impresión pendiente.\nLa conectaremos después de generar el PDF.",
                        "Impresión pendiente",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        btnVolver.addActionListener(e -> volver());

        actions.add(btnGenerar);
        actions.add(btnLimpiar);
        actions.add(btnPdf);
        actions.add(btnImprimir);
        actions.add(btnVolver);

        topBar.add(titlePanel, BorderLayout.CENTER);
        topBar.add(actions, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);

        filtrosPanel = new InformeFiltrosPanel();
        kpiPanel = new InformeKpiPanel();
        tablaPanel = new InformeTablaPanel();
        graficoPanel = new InformeGraficoPanel();

        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);
        centerPanel.add(kpiPanel, BorderLayout.NORTH);
        centerPanel.add(tablaPanel, BorderLayout.CENTER);

        body.add(filtrosPanel, BorderLayout.WEST);
        body.add(centerPanel, BorderLayout.CENTER);
        body.add(graficoPanel, BorderLayout.EAST);

        return body;
    }

    private void generarVistaDemo() {
        InformeFiltroDTO filtros = filtrosPanel.leerFiltros();
        TipoInforme tipo = filtros.getTipoInforme();

        kpiPanel.cargarDemo(tipo);
        tablaPanel.cargarDemo(tipo);
        graficoPanel.cargarDemo(tipo);
    }

    private void volver() {
        safeDispose();
        if (onBack != null) {
            onBack.run();
        }
    }
}
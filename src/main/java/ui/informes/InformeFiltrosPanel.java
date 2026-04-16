package ui.informes;

import dtoS.InformeFiltroDTO;
import enums.FamiliaInforme;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import service.AppServices;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class InformeFiltrosPanel extends JPanel {

    private final AppServices services;

    private final JLabel lblModuleTitle;
    private final JLabel lblModuleSubtitle;

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final Map<FamiliaInforme, InformeFilterModule> modules;

    private FamiliaInforme currentFamilia;
    private TipoInforme currentTipoInforme;

    public InformeFiltrosPanel(AppServices services) {
        this.services = services;

        setLayout(new BorderLayout(0, 12));
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = InformeUiTheme.createSectionTitle("Filtros del informe");

        lblModuleTitle = new JLabel("Configuración");
        lblModuleTitle.setFont(InformeUiTheme.FONT_LABEL);
        lblModuleTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        lblModuleSubtitle = new JLabel("La configuración cambia según el tipo de informe");
        lblModuleSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblModuleSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(lblModuleTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblModuleSubtitle);

        modules = new EnumMap<>(FamiliaInforme.class);
        modules.put(FamiliaInforme.VENTAS_TIEMPO, new VentasTiempoFilterPanel(services));
        modules.put(FamiliaInforme.COMERCIAL, new ComercialFilterPanel());
        modules.put(FamiliaInforme.EQUIPO, new EquipoFilterPanel());
        modules.put(FamiliaInforme.OPERATIVA, new OperativaFilterPanel());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        for (Map.Entry<FamiliaInforme, InformeFilterModule> entry : modules.entrySet()) {
            cardPanel.add((Component) entry.getValue(), entry.getKey().name());
        }

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setPreferredSize(new Dimension(340, 650));
    }

    public void setTipoInforme(TipoInforme tipoInforme) {
        if (tipoInforme == null) {
            return;
        }

        currentTipoInforme = tipoInforme;
        currentFamilia = tipoInforme.getFamilia();

        lblModuleTitle.setText(tipoInforme.getDisplayName());
        lblModuleSubtitle.setText(tipoInforme.getShortDescription());

        cardLayout.show(cardPanel, currentFamilia.name());

        InformeFilterModule module = modules.get(currentFamilia);
        if (module != null) {
            module.setTipoInforme(tipoInforme);
        }

        revalidate();
        repaint();
    }

    public void resetCurrent() {
        InformeFilterModule module = getCurrentModule();
        if (module != null) {
            module.reset();
        }
    }

    public ModoVistaInforme getCurrentModoVista() {
        InformeFilterModule module = getCurrentModule();
        return module != null ? module.getModoVista() : ModoVistaInforme.AGREGADA;
    }

    public String getCurrentFilterSummary() {
        InformeFilterModule module = getCurrentModule();
        if (module == null) {
            return currentTipoInforme != null ? currentTipoInforme.getDisplayName() : "Sin configuración";
        }
        return module.buildSummary();
    }

    public InformeFiltroDTO buildFiltroDTO() {
        InformeFilterModule module = getCurrentModule();

        if (module == null) {
            InformeFiltroDTO dto = new InformeFiltroDTO();
            dto.setTipoInforme(currentTipoInforme);
            dto.setModoVista(ModoVistaInforme.AGREGADA);
            return dto;
        }

        InformeFiltroDTO dto = module.buildFiltroDTO();

        if (dto.getTipoInforme() == null) {
            dto.setTipoInforme(currentTipoInforme);
        }

        return dto;
    }

    public TipoInforme getCurrentTipoInforme() {
        return currentTipoInforme;
    }

    public FamiliaInforme getCurrentFamilia() {
        return currentFamilia;
    }

    private InformeFilterModule getCurrentModule() {
        if (currentFamilia == null) {
            return null;
        }
        return modules.get(currentFamilia);
    }
}
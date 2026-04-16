package ui.informes;

import dtoS.InformeFiltroDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class BaseInformeFilterPanel extends JPanel implements InformeFilterModule {

    protected TipoInforme currentTipoInforme;

    protected BaseInformeFilterPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(4, 0, 0, 0));
    }

    protected JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    protected JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        block.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel label = InformeUiTheme.createFieldLabel(labelText);
        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);

        return block;
    }

    @Override
    public void setTipoInforme(TipoInforme tipoInforme) {
        this.currentTipoInforme = tipoInforme;
        onTipoInformeChanged(tipoInforme);
    }

    protected abstract void onTipoInformeChanged(TipoInforme tipoInforme);

    @Override
    public ModoVistaInforme getModoVista() {
        return ModoVistaInforme.AGREGADA;
    }

    @Override
    public String buildSummary() {
        return currentTipoInforme != null ? currentTipoInforme.getDisplayName() : "Informe";
    }

    @Override
    public InformeFiltroDTO buildFiltroDTO() {
        InformeFiltroDTO dto = new InformeFiltroDTO();
        dto.setTipoInforme(currentTipoInforme);
        dto.setModoVista(getModoVista());
        return dto;
    }
}
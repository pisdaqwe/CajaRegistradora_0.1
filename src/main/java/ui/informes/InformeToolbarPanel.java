package ui.informes;

import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InformeToolbarPanel extends JPanel {

    private final JComboBox<TipoInforme> cmbTipoInforme;
    private final JButton btnGenerar;
    private final JButton btnLimpiar;
    private final JButton btnVerGrafico;
    private final JButton btnExportarPdf;
    private final JButton btnVolver;
    private final JLabel lblSubtitle;

    private boolean syncingTipoInforme = false;

    public InformeToolbarPanel() {
        setLayout(new BorderLayout(16, 0));
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Centro de Informes");
        lblTitle.setFont(InformeUiTheme.FONT_TITLE);
        lblTitle.setForeground(InformeUiTheme.TEXT_PRIMARY);

        lblSubtitle = new JLabel("Selecciona un informe para configurar la vista");
        lblSubtitle.setFont(InformeUiTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSubtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        cmbTipoInforme = new JComboBox<>(TipoInforme.values());
        cmbTipoInforme.setPreferredSize(new Dimension(260, 40));
        InformeUiTheme.styleCombo(cmbTipoInforme);

        btnGenerar = new JButton("Generar");
        btnLimpiar = new JButton("Limpiar");
        btnVerGrafico = new JButton("Ver gráfico");
        btnExportarPdf = new JButton("Exportar PDF");
        btnVolver = new JButton("Volver");

        InformeUiTheme.stylePrimaryButton(btnGenerar);
        InformeUiTheme.styleSecondaryButton(btnLimpiar);
        InformeUiTheme.styleSecondaryButton(btnVerGrafico);
        InformeUiTheme.styleSecondaryButton(btnExportarPdf);
        InformeUiTheme.styleDangerButton(btnVolver);

        btnVerGrafico.setEnabled(false);

        right.add(cmbTipoInforme);
        right.add(btnGenerar);
        right.add(btnLimpiar);
        right.add(btnVerGrafico);
        right.add(btnExportarPdf);
        right.add(btnVolver);

        add(titlePanel, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    public TipoInforme getSelectedTipoInforme() {
        return (TipoInforme) cmbTipoInforme.getSelectedItem();
    }

    public void setSelectedTipoInforme(TipoInforme tipoInforme) {
        syncingTipoInforme = true;
        try {
            cmbTipoInforme.setSelectedItem(tipoInforme);
            updateSubtitle(tipoInforme);
        } finally {
            syncingTipoInforme = false;
        }
    }

    public void updateSubtitle(TipoInforme tipoInforme) {
        lblSubtitle.setText(tipoInforme.getShortDescription());
    }

    public void setGraficoEnabled(boolean enabled) {
        btnVerGrafico.setEnabled(enabled);
    }

    public void onTipoInformeChanged(ActionListener listener) {
        cmbTipoInforme.addActionListener(e -> {
            if (!syncingTipoInforme) {
                listener.actionPerformed(e);
            }
        });
    }

    public void onGenerar(ActionListener listener) {
        btnGenerar.addActionListener(listener);
    }

    public void onLimpiar(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }

    public void onVerGrafico(ActionListener listener) {
        btnVerGrafico.addActionListener(listener);
    }

    public void onExportarPdf(ActionListener listener) {
        btnExportarPdf.addActionListener(listener);
    }

    public void onVolver(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }
}
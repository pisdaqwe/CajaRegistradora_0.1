package ui.informes;



import enums.TipoInforme;
import ui.common.InformeUiTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Panel superior de KPIs.
 *
 * Primera fase:
 * - valores demo según el tipo de informe
 * - tarjetas visuales grandes y profesionales
 */
public class InformeKpiPanel extends JPanel {

    private final KpiCard cardVentas;
    private final KpiCard cardDevoluciones;
    private final KpiCard cardNeto;
    private final KpiCard cardTicketMedio;
    private final KpiCard cardCombos;
    private final KpiCard cardAhorro;

    public InformeKpiPanel() {
        setLayout(new GridLayout(2, 3, 14, 14));
        setOpaque(false);

        cardVentas = new KpiCard("Ventas");
        cardDevoluciones = new KpiCard("Devoluciones");
        cardNeto = new KpiCard("Neto");
        cardTicketMedio = new KpiCard("Ticket medio");
        cardCombos = new KpiCard("Combos vendidos");
        cardAhorro = new KpiCard("Ahorro / impacto");

        add(cardVentas);
        add(cardDevoluciones);
        add(cardNeto);
        add(cardTicketMedio);
        add(cardCombos);
        add(cardAhorro);

        cargarDemo(TipoInforme.RESUMEN_EJECUTIVO);
    }

    public void cargarDemo(TipoInforme tipo) {
        switch (tipo) {
            case INFORME_CAJA -> {
                cardVentas.setValue("2.136,40 €");
                cardDevoluciones.setValue("-52,34 €");
                cardNeto.setValue("2.084,06 €");
                cardTicketMedio.setValue("8,42 €");
                cardCombos.setValue("17");
                cardAhorro.setValue("148,90 €");
            }
            case VENTAS_POR_DIA -> {
                cardVentas.setValue("5.482,70 €");
                cardDevoluciones.setValue("-114,20 €");
                cardNeto.setValue("5.368,50 €");
                cardTicketMedio.setValue("9,14 €");
                cardCombos.setValue("31");
                cardAhorro.setValue("282,10 €");
            }
            case TICKET_MEDIO_POR_DIA -> {
                cardVentas.setValue("5.482,70 €");
                cardDevoluciones.setValue("-114,20 €");
                cardNeto.setValue("5.368,50 €");
                cardTicketMedio.setValue("9,14 €");
                cardCombos.setValue("31");
                cardAhorro.setValue("Tendencia +4,8%");
            }
            case PAGOS_POR_METODO -> {
                cardVentas.setValue("4.990,20 €");
                cardDevoluciones.setValue("-78,00 €");
                cardNeto.setValue("4.912,20 €");
                cardTicketMedio.setValue("8,76 €");
                cardCombos.setValue("24");
                cardAhorro.setValue("Tarjeta 58%");
            }
            case PRODUCTOS_MAS_VENDIDOS -> {
                cardVentas.setValue("3.908,40 €");
                cardDevoluciones.setValue("-35,20 €");
                cardNeto.setValue("3.873,20 €");
                cardTicketMedio.setValue("8,12 €");
                cardCombos.setValue("12");
                cardAhorro.setValue("Top 10");
            }
            case COMBOS_VENDIDOS -> {
                cardVentas.setValue("1.986,30 €");
                cardDevoluciones.setValue("-21,00 €");
                cardNeto.setValue("1.965,30 €");
                cardTicketMedio.setValue("11,32 €");
                cardCombos.setValue("48");
                cardAhorro.setValue("304,45 €");
            }
            case DESCUENTOS -> {
                cardVentas.setValue("4.320,00 €");
                cardDevoluciones.setValue("-65,00 €");
                cardNeto.setValue("4.255,00 €");
                cardTicketMedio.setValue("8,60 €");
                cardCombos.setValue("19");
                cardAhorro.setValue("213,75 €");
            }
            case DEVOLUCIONES -> {
                cardVentas.setValue("4.120,00 €");
                cardDevoluciones.setValue("-264,60 €");
                cardNeto.setValue("3.855,40 €");
                cardTicketMedio.setValue("8,10 €");
                cardCombos.setValue("9");
                cardAhorro.setValue("Ratio 6,4%");
            }
            case RESUMEN_EJECUTIVO -> {
                cardVentas.setValue("6.214,10 €");
                cardDevoluciones.setValue("-145,20 €");
                cardNeto.setValue("6.068,90 €");
                cardTicketMedio.setValue("9,42 €");
                cardCombos.setValue("36");
                cardAhorro.setValue("415,85 €");
            }
        }
    }

    /**
     * Tarjeta visual individual de KPI.
     */
    private static class KpiCard extends JPanel {

        private final JLabel lblValue;

        private KpiCard(String title) {
            setLayout(new BorderLayout(0, 8));
            setBackground(InformeUiTheme.CARD_BG);
            setBorder(InformeUiTheme.createInnerCardBorder());

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(InformeUiTheme.FONT_KPI_TITLE);
            lblTitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

            lblValue = new JLabel("--");
            lblValue.setFont(InformeUiTheme.FONT_KPI_VALUE);
            lblValue.setForeground(InformeUiTheme.TEXT_PRIMARY);

            JPanel accent = new JPanel();
            accent.setPreferredSize(new Dimension(10, 5));
            accent.setBackground(InformeUiTheme.ACCENT_GOLD);

            add(accent, BorderLayout.NORTH);
            add(lblTitle, BorderLayout.CENTER);
            add(lblValue, BorderLayout.SOUTH);
        }

        private void setValue(String value) {
            lblValue.setText(value);
        }
    }
}

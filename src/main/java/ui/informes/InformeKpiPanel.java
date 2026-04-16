package ui.informes;

import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;

import dtoS.InformeVentasPorDiaResultDTO;

import java.awt.*;

public class InformeKpiPanel extends JPanel {

    private final KpiCard card1;
    private final KpiCard card2;
    private final KpiCard card3;
    private final KpiCard card4;

    public InformeKpiPanel() {
        setLayout(new GridLayout(1, 4, 14, 14));
        setOpaque(false);

        card1 = new KpiCard("KPI 1");
        card2 = new KpiCard("KPI 2");
        card3 = new KpiCard("KPI 3");
        card4 = new KpiCard("KPI 4");

        add(card1);
        add(card2);
        add(card3);
        add(card4);
    }

    public void showPlaceholder(TipoInforme tipoInforme) {
        switch (tipoInforme) {
            case RESUMEN_EJECUTIVO -> {
                card1.setData("Ventas", "6.214,10 €");
                card2.setData("Devoluciones", "-145,20 €");
                card3.setData("Neto", "6.068,90 €");
                card4.setData("Ticket medio", "9,42 €");
            }

            case VENTAS_POR_DIA -> {
                card1.setData("Ventas", "5.482,70 €");
                card2.setData("Devoluciones", "-114,20 €");
                card3.setData("Neto", "5.368,50 €");
                card4.setData("Ticket medio", "9,14 €");
            }

            case VENTAS_POR_FRANJA_HORARIA -> {
                card1.setData("Franja pico", "10-12h");
                card2.setData("Ventas pico", "610,00 €");
                card3.setData("Franja baja", "14-16h");
                card4.setData("Ventas bajas", "330,00 €");
            }

            case TICKET_MEDIO_POR_DIA -> {
                card1.setData("Ticket medio", "9,14 €");
                card2.setData("Mejor día", "02/04");
                card3.setData("Valor máximo", "9,43 €");
                card4.setData("Tendencia", "+4,8%");
            }

            case PAGOS_POR_METODO -> {
                card1.setData("Tarjeta", "58,0%");
                card2.setData("Efectivo", "39,4%");
                card3.setData("Vale", "2,6%");
                card4.setData("Total", "4.912,20 €");
            }

            case VENTAS_NETAS_VS_DEVOLUCIONES -> {
                card1.setData("Ventas", "5.482,70 €");
                card2.setData("Devoluciones", "114,20 €");
                card3.setData("Neto", "5.368,50 €");
                card4.setData("Ratio devolución", "2,08%");
            }

            case PRODUCTOS_MAS_VENDIDOS -> {
                card1.setData("Top producto", "Latte");
                card2.setData("Unidades", "148");
                card3.setData("Top 3", "383 uds");
                card4.setData("Neto", "2.277,90 €");
            }

            case EXTRAS_MAS_VENDIDOS -> {
                card1.setData("Top extra", "Shot extra");
                card2.setData("Veces", "96");
                card3.setData("Upselling", "280,30 €");
                card4.setData("Top 3", "239 ventas");
            }

            case COMBOS_VENDIDOS -> {
                card1.setData("Combos", "48");
                card2.setData("Ahorro", "304,45 €");
                card3.setData("Top combo", "Desayuno");
                card4.setData("Ticket medio", "11,32 €");
            }

            case DESCUENTOS_APLICADOS -> {
                card1.setData("Descuento total", "213,75 €");
                card2.setData("Más usado", "Empleado");
                card3.setData("Usos", "52");
                card4.setData("Impacto", "4,9%");
            }

            case DEVOLUCIONES_POR_PRODUCTO -> {
                card1.setData("Producto top", "Termo");
                card2.setData("Reembolsado", "14,95 €");
                card3.setData("Items devueltos", "3");
                card4.setData("Ratio", "0,7%");
            }

            case RANKING_EMPLEADOS_POR_VENTAS -> {
                card1.setData("Nº1", "Ana");
                card2.setData("Ventas", "1.840,50 €");
                card3.setData("Nº2", "Luis");
                card4.setData("Ventas", "1.620,30 €");
            }

            case RANKING_EMPLEADOS_POR_EXTRAS -> {
                card1.setData("Nº1", "Ana");
                card2.setData("Extras", "82");
                card3.setData("Nº2", "Luis");
                card4.setData("Extras", "69");
            }

            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> {
                card1.setData("Empleado", "Ana");
                card2.setData("Top producto", "Latte");
                card3.setData("Unidades", "118");
                card4.setData("Variedad", "12 productos");
            }

            case VENTAS_POR_CAJA -> {
                card1.setData("Caja 1", "2.136,40 €");
                card2.setData("Caja 2", "1.845,20 €");
                card3.setData("Mejor caja", "Caja 1");
                card4.setData("Diferencia", "291,20 €");
            }

            case VENTAS_POR_SESION_CAJA -> {
                card1.setData("Mejor sesión", "#102");
                card2.setData("Ventas", "1.120,20 €");
                card3.setData("Sesiones", "3");
                card4.setData("Media", "992,17 €");
            }

            case TIEMPOS_POR_ESTACION -> {
                card1.setData("Más lenta", "Comida");
                card2.setData("Tiempo", "210 s");
                card3.setData("Más rápida", "Frías");
                card4.setData("Tiempo", "122 s");
            }

            case MERMA_POR_PERIODO -> {
                card1.setData("Merma total", "145,50");
                card2.setData("Peor día", "02/04");
                card3.setData("Valor pico", "62,50");
                card4.setData("Tendencia", "Estable");
            }

            case MOVIMIENTOS_STOCK_AJUSTES -> {
                card1.setData("Entradas", "18");
                card2.setData("Salidas", "36");
                card3.setData("Ajustes", "7");
                card4.setData("Mermas", "5");
            }
        }
    }

    private static class KpiCard extends JPanel {

        private final JLabel lblTitle;
        private final JLabel lblValue;

        private KpiCard(String title) {
            setLayout(new BorderLayout(0, 8));
            setBackground(InformeUiTheme.CARD_BG);
            setBorder(InformeUiTheme.createInnerCardBorder());

            JPanel accent = new JPanel();
            accent.setPreferredSize(new Dimension(10, 5));
            accent.setBackground(InformeUiTheme.ACCENT_GOLD);

            lblTitle = new JLabel(title);
            lblTitle.setFont(InformeUiTheme.FONT_KPI_TITLE);
            lblTitle.setForeground(InformeUiTheme.TEXT_SECONDARY);

            lblValue = new JLabel("--");
            lblValue.setFont(InformeUiTheme.FONT_KPI_VALUE);
            lblValue.setForeground(InformeUiTheme.TEXT_PRIMARY);

            add(accent, BorderLayout.NORTH);
            add(lblTitle, BorderLayout.CENTER);
            add(lblValue, BorderLayout.SOUTH);
        }

        private void setData(String title, String value) {
            lblTitle.setText(title);
            lblValue.setText(value);
        }
    }
    public void cargarVentasPorDia(InformeVentasPorDiaResultDTO result) {
        card1.setData("Ventas", formatMoney(result.getTotalVentas()));
        card2.setData("Devoluciones", formatMoney(result.getTotalDevoluciones()));
        card3.setData("Neto", formatMoney(result.getTotalNeto()));
        card4.setData("Ticket medio", formatMoney(result.getTicketMedioGlobal()));
    }

    private String formatMoney(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f €", safe);
    }
}
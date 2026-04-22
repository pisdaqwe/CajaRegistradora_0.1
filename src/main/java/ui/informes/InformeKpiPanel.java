package ui.informes;

import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;

import dtoS.InformeCombosVendidosResultDTO;
import dtoS.InformeDescuentosAplicadosResultDTO;
import dtoS.InformeDevolucionesProductoResultDTO;
import dtoS.InformeExtrasVendidosResultDTO;
import dtoS.InformeMermaPeriodoResultDTO;
import dtoS.InformeMovimientoStockResultDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformeProductosPorEmpleadoResultDTO;
import dtoS.InformeProductosVendidosResultDTO;
import dtoS.InformeRankingEmpleadosExtraResultDTO;
import dtoS.InformeRankingEmpleadosExtrasResultDTO;
import dtoS.InformeRankingEmpleadosProductoResultDTO;
import dtoS.InformeRankingEmpleadosVentasResultDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformeTiemposEstacionResultDTO;
import dtoS.InformeVentasCajaResultDTO;
import dtoS.InformeVentasExtraEmpleadoResultDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeVentasProductoEmpleadoResultDTO;
import dtoS.InformeVentasSesionCajaResultDTO;

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

    public void showPlaceholderempty(TipoInforme tipoInforme) {
        card1.setData("-", "-");
        card2.setData("-", "-");
        card3.setData("-", "-");
        card4.setData("-", "-");
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


   
    public void cargarResumenEjecutivo(InformeResumenEjecutivoResultDTO result) {
        card1.setData("Ventas brutas", formatMoney(result.getVentasBrutas()));
        card2.setData("Devoluciones", formatMoney(result.getDevoluciones()));
        card3.setData("Neto", formatMoney(result.getNeto()));
        card4.setData("Ticket medio", formatMoney(result.getTicketMedio()));
    }

    public void cargarVentasPorFranjaHoraria(InformeVentasFranjaResultDTO result) {
        card1.setData("Ventas", formatMoney(result.getTotalVentas()));
        card2.setData("Devoluciones", formatMoney(result.getTotalDevoluciones()));
        card3.setData("Neto", formatMoney(result.getTotalNeto()));
        card4.setData("Mejor franja", safeText(result.getMejorFranja()));
    }

    public void cargarTicketMedioPorDia(InformeTicketMedioDiaResultDTO result) {
        card1.setData("Ticket medio", formatMoney(result.getTicketMedioGlobal()));
        card2.setData("Tickets", String.valueOf(result.getTotalTickets()));
        card3.setData("Ventas", formatMoney(result.getTotalVentas()));
        card4.setData("Mejor día", result.getMejorDia() != null ? result.getMejorDia().toString() : "-");
    }

    public void cargarPagosPorMetodo(InformePagosMetodoResultDTO result) {
        card1.setData("Importe total", formatMoney(result.getTotalImporte()));
        card2.setData("Operaciones", String.valueOf(result.getTotalOperaciones()));
        card3.setData("Método top", safeText(result.getMetodoPrincipal()));
        card4.setData("Importe top", formatMoney(result.getImporteMetodoPrincipal()));
    }

    public void cargarVentasNetasVsDevoluciones(InformeNetoVsDevolucionesResultDTO result) {
        card1.setData("Ventas", formatMoney(result.getTotalVentas()));
        card2.setData("Devoluciones", formatMoney(result.getTotalDevoluciones()));
        card3.setData("Neto", formatMoney(result.getTotalNeto()));
        card4.setData("Ratio dev.", formatPercent(result.getRatioGlobalDevolucion()));
    }

    public void cargarProductosMasVendidos(InformeProductosVendidosResultDTO result) {
        card1.setData("Unidades", String.valueOf(result.getTotalUnidades()));
        card2.setData("Neto", formatMoney(result.getTotalNeto()));
        card3.setData("Producto top", safeText(result.getProductoTop()));
        card4.setData("Unidades top", String.valueOf(result.getUnidadesProductoTop()));
    }

    public void cargarExtrasMasVendidos(InformeExtrasVendidosResultDTO result) {
        card1.setData("Veces", String.valueOf(result.getTotalVeces()));
        card2.setData("Importe", formatMoney(result.getTotalImporte()));
        card3.setData("Extra top", safeText(result.getExtraTop()));
        card4.setData("Veces top", String.valueOf(result.getVecesExtraTop()));
    }

    public void cargarCombosVendidos(InformeCombosVendidosResultDTO result) {
        card1.setData("Combos", String.valueOf(result.getTotalCombos()));
        card2.setData("Ahorro", formatMoney(result.getTotalAhorro()));
        card3.setData("Combo top", safeText(result.getComboTop()));
        card4.setData("Veces top", String.valueOf(result.getVecesComboTop()));
    }

    public void cargarDescuentosAplicados(InformeDescuentosAplicadosResultDTO result) {
        card1.setData("Usos", String.valueOf(result.getTotalUsos()));
        card2.setData("Base", formatMoney(result.getTotalBase()));
        card3.setData("Descuento", formatMoney(result.getTotalImporteDescuento()));
        card4.setData("Más usado", safeText(result.getDescuentoMasUsado()));
    }

    public void cargarDevolucionesPorProducto(InformeDevolucionesProductoResultDTO result) {
        card1.setData("Cantidad", String.valueOf(result.getTotalCantidadDevuelta()));
        card2.setData("Devoluciones", String.valueOf(result.getTotalDevoluciones()));
        card3.setData("Reembolso", formatMoney(result.getTotalReembolsado()));
        card4.setData("Producto top", safeText(result.getProductoMasDevuelto()));
    }
    public void cargarRankingEmpleadosPorVentas(InformeRankingEmpleadosVentasResultDTO result) {
        card1.setData("Ventas", formatMoney(result.getTotalVentas()));
        card2.setData("Tickets", String.valueOf(result.getTotalTickets()));
        card3.setData("Mejor empleado", safeText(result.getMejorEmpleado()));
        card4.setData("Ventas top", formatMoney(result.getVentasMejorEmpleado()));
    }

    public void cargarRankingEmpleadosPorExtras(InformeRankingEmpleadosExtrasResultDTO result) {
        card1.setData("Extras", String.valueOf(result.getTotalExtrasVendidos()));
        card2.setData("Importe", formatMoney(result.getTotalImporteExtras()));
        card3.setData("Mejor empleado", safeText(result.getMejorEmpleado()));
        card4.setData("Extras top", String.valueOf(result.getExtrasMejorEmpleado()));
    }

    public void cargarProductosVendidosPorEmpleado(InformeProductosPorEmpleadoResultDTO result) {
        card1.setData("Unidades", String.valueOf(result.getTotalUnidades()));
        card2.setData("Importe", formatMoney(result.getTotalImporte()));
        card3.setData("Empleado top", safeText(result.getEmpleadoTop()));
        card4.setData("Producto top", safeText(result.getProductoTop()));
    }

    public void cargarVentasPorCaja(InformeVentasCajaResultDTO result) {
        card1.setData("Ventas", formatMoney(result.getTotalVentas()));
        card2.setData("Devoluciones", formatMoney(result.getTotalDevoluciones()));
        card3.setData("Neto", formatMoney(result.getTotalNeto()));
        card4.setData("Mejor caja", safeText(result.getMejorCaja()));
    }

    public void cargarVentasPorSesionCaja(InformeVentasSesionCajaResultDTO result) {
        card1.setData("Sesiones", String.valueOf(result.getTotalSesiones()));
        card2.setData("Ventas", formatMoney(result.getTotalVentas()));
        card3.setData("Neto", formatMoney(result.getTotalNeto()));
        card4.setData("Mejor sesión", result.getIdMejorSesion() != null ? "#" + result.getIdMejorSesion() : "-");
    }

    public void cargarTiemposPorEstacion(InformeTiemposEstacionResultDTO result) {
        card1.setData("Tiempo medio", formatNumber(result.getTiempoMedioGlobalSegundos()) + " s");
        card2.setData("Items", String.valueOf(result.getTotalItemsProcesados()));
        card3.setData("Más lenta", safeText(result.getEstacionMasLenta()));
        card4.setData("Tiempo", formatNumber(result.getTiempoEstacionMasLenta()) + " s");
    }

    public void cargarMermaPorPeriodo(InformeMermaPeriodoResultDTO result) {
        card1.setData("Cantidad", formatNumber(result.getTotalCantidad()));
        card2.setData("Registros", String.valueOf(result.getTotalRegistros()));
        card3.setData("Peor día", result.getPeorDia() != null ? result.getPeorDia().toString() : "-");
        card4.setData("Cantidad pico", formatNumber(result.getCantidadPeorDia()));
    }

    public void cargarMovimientosStockAjustes(InformeMovimientoStockResultDTO result) {
        card1.setData("Movimientos", String.valueOf(result.getTotalMovimientos()));
        card2.setData("Cantidad", formatNumber(result.getTotalCantidad()));
        card3.setData("Entradas", String.valueOf(result.getTotalEntradas()));
        card4.setData("Salidas", String.valueOf(result.getTotalSalidas()));
    }
    
    public void cargarVentasProductoPorEmpleado(InformeVentasProductoEmpleadoResultDTO result) {
        card1.setData("Unidades", String.valueOf(result.getTotalUnidades()));
        card2.setData("Neto", formatMoney(result.getTotalNeto()));
        card3.setData("Empleado top", safeText(result.getEmpleadoTop()));
        card4.setData("Producto top", safeText(result.getProductoTop()));
    }

    public void cargarRankingEmpleadosPorProducto(InformeRankingEmpleadosProductoResultDTO result) {
        card1.setData("Unidades", String.valueOf(result.getTotalUnidades()));
        card2.setData("Neto", formatMoney(result.getTotalImporteNeto()));
        card3.setData("Empleado top", safeText(result.getEmpleadoTop()));
        card4.setData("Producto top", safeText(result.getProductoTop()));
    }

    public void cargarVentasExtraPorEmpleado(InformeVentasExtraEmpleadoResultDTO result) {
        card1.setData("Veces", String.valueOf(result.getTotalVeces()));
        card2.setData("Importe", formatMoney(result.getTotalImporte()));
        card3.setData("Empleado top", safeText(result.getEmpleadoTop()));
        card4.setData("Extra top", safeText(result.getExtraTop()));
    }

    public void cargarRankingEmpleadosPorExtra(InformeRankingEmpleadosExtraResultDTO result) {
        card1.setData("Veces", String.valueOf(result.getTotalVeces()));
        card2.setData("Importe", formatMoney(result.getTotalImporte()));
        card3.setData("Empleado top", safeText(result.getEmpleadoTop()));
        card4.setData("Extra top", safeText(result.getExtraTop()));
    }
    private String formatMoney(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f €", safe);
    }
    private String safeText(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }

    private String formatPercent(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f %%", safe);
    }

    private String formatNumber(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f", safe);
    }
    
}
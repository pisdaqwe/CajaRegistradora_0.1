package ui.informes;

import enums.ModoVistaInforme;
import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

public class InformeTablaPanel extends JPanel {

    private final JLabel lblTitle;
    private final JTable table;
    private final DefaultTableModel model;
    private final JLabel lblEmpty;

    private final CardLayout centerLayout;
    private final JPanel centerPanel;

    public InformeTablaPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        lblTitle = InformeUiTheme.createSectionTitle("Resultado tabular");

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        InformeUiTheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        InformeUiTheme.styleScrollPane(scrollPane);

        lblEmpty = new JLabel("Selecciona un informe y pulsa Generar.", SwingConstants.CENTER);
        lblEmpty.setForeground(InformeUiTheme.TEXT_SECONDARY);
        lblEmpty.setFont(InformeUiTheme.FONT_BODY);

        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setOpaque(false);
        centerPanel.add(lblEmpty, "EMPTY");
        centerPanel.add(scrollPane, "TABLE");

        add(lblTitle, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        centerLayout.show(centerPanel, "EMPTY");
    }

    public void showPlaceholder(TipoInforme tipoInforme, ModoVistaInforme modoVista) {
        String[] cols= null;
        Object[][] rows= null;

        lblTitle.setText("Resultado · " + tipoInforme.getDisplayName());

        switch (tipoInforme) {
            case RESUMEN_EJECUTIVO -> {
                cols = new String[]{"Indicador", "Valor"};
                rows = new Object[][]{
                        {"Ventas brutas", "6.214,10 €"},
                        {"Devoluciones", "-145,20 €"},
                        {"Neto", "6.068,90 €"},
                        {"Ticket medio", "9,42 €"},
                        {"Combos vendidos", "36"},
                        {"Ahorro total", "415,85 €"}
                };
            }

            case VENTAS_POR_DIA -> {
                if (modoVista == ModoVistaInforme.COMPARATIVA) {
                    cols = new String[]{"Fecha", "Empleado", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
                    rows = new Object[][]{
                            {"01/04/2026", "Ana", "320,40 €", "-10,00 €", "310,40 €", 31, "10,33 €"},
                            {"01/04/2026", "Luis", "280,20 €", "0,00 €", "280,20 €", 29, "9,66 €"},
                            {"01/04/2026", "Marta", "190,00 €", "-2,00 €", "188,00 €", 22, "8,54 €"},
                            {"02/04/2026", "Ana", "340,80 €", "-8,00 €", "332,80 €", 33, "10,33 €"},
                            {"02/04/2026", "Luis", "295,30 €", "-4,00 €", "291,30 €", 30, "9,84 €"},
                            {"02/04/2026", "Marta", "215,40 €", "-1,00 €", "214,40 €", 24, "8,98 €"}
                    };
                } else {
                    cols = new String[]{"Fecha", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
                    rows = new Object[][]{
                            {"01/04/2026", "782,40 €", "-12,00 €", "770,40 €", 84, "9,19 €"},
                            {"02/04/2026", "905,20 €", "-22,00 €", "883,20 €", 96, "9,43 €"},
                            {"03/04/2026", "846,10 €", "-18,20 €", "827,90 €", 92, "9,19 €"},
                            {"04/04/2026", "1.012,60 €", "-31,00 €", "981,60 €", 108, "9,38 €"},
                            {"05/04/2026", "1.123,40 €", "-31,00 €", "1.092,40 €", 120, "9,36 €"}
                    };
                }
            }

            case VENTAS_POR_FRANJA_HORARIA -> {
                if (modoVista == ModoVistaInforme.COMPARATIVA) {
                    cols = new String[]{"Franja", "Empleado", "Ventas", "Tickets", "Ticket medio"};
                    rows = new Object[][]{
                            {"08-10h", "Ana", "180,00 €", 17, "10,58 €"},
                            {"08-10h", "Luis", "140,00 €", 16, "8,75 €"},
                            {"10-12h", "Ana", "210,00 €", 20, "10,50 €"},
                            {"10-12h", "Luis", "160,00 €", 18, "8,89 €"},
                            {"12-14h", "Ana", "120,00 €", 12, "10,00 €"},
                            {"12-14h", "Luis", "90,00 €", 10, "9,00 €"}
                    };
                } else {
                    cols = new String[]{"Franja", "Ventas", "Tickets", "Ticket medio"};
                    rows = new Object[][]{
                            {"08-10h", "420,00 €", 44, "9,54 €"},
                            {"10-12h", "610,00 €", 64, "9,53 €"},
                            {"12-14h", "540,00 €", 58, "9,31 €"},
                            {"14-16h", "330,00 €", 37, "8,92 €"}
                    };
                }
            }

            case TICKET_MEDIO_POR_DIA -> {
                if (modoVista == ModoVistaInforme.COMPARATIVA) {
                    cols = new String[]{"Fecha", "Empleado", "Tickets", "Ventas", "Ticket medio"};
                    rows = new Object[][]{
                            {"01/04/2026", "Ana", 31, "320,40 €", "10,33 €"},
                            {"01/04/2026", "Luis", 29, "280,20 €", "9,66 €"},
                            {"01/04/2026", "Marta", 22, "190,00 €", "8,54 €"},
                            {"02/04/2026", "Ana", 33, "340,80 €", "10,33 €"},
                            {"02/04/2026", "Luis", 30, "295,30 €", "9,84 €"},
                            {"02/04/2026", "Marta", 24, "215,40 €", "8,98 €"}
                    };
                } else {
                    cols = new String[]{"Fecha", "Tickets", "Ventas", "Ticket medio"};
                    rows = new Object[][]{
                            {"01/04/2026", 84, "782,40 €", "9,19 €"},
                            {"02/04/2026", 96, "905,20 €", "9,43 €"},
                            {"03/04/2026", 92, "846,10 €", "9,19 €"},
                            {"04/04/2026", 108, "1.012,60 €", "9,38 €"},
                            {"05/04/2026", 120, "1.123,40 €", "9,36 €"}
                    };
                }
            }

            case PAGOS_POR_METODO -> {
                cols = new String[]{"Método", "Operaciones", "Importe", "% total"};
                rows = new Object[][]{
                        {"Tarjeta", 326, "2.848,20 €", "58,0%"},
                        {"Efectivo", 214, "1.936,40 €", "39,4%"},
                        {"Vale", 14, "127,60 €", "2,6%"}
                };
            }

            case VENTAS_NETAS_VS_DEVOLUCIONES -> {
                cols = new String[]{"Fecha", "Ventas", "Devoluciones", "Neto", "Ratio devolución"};
                rows = new Object[][]{
                        {"01/04/2026", "782,40 €", "12,00 €", "770,40 €", "1,53%"},
                        {"02/04/2026", "905,20 €", "22,00 €", "883,20 €", "2,43%"},
                        {"03/04/2026", "846,10 €", "18,20 €", "827,90 €", "2,15%"},
                        {"04/04/2026", "1.012,60 €", "31,00 €", "981,60 €", "3,06%"}
                };
            }

            case PRODUCTOS_MAS_VENDIDOS -> {
                cols = new String[]{"Producto", "Unidades", "Bruto", "Devoluciones", "Neto"};
                rows = new Object[][]{
                        {"Latte Clásico", 148, "740,00 €", "-8,00 €", "732,00 €"},
                        {"Cappuccino Grande", 123, "676,50 €", "-6,50 €", "670,00 €"},
                        {"Croissant Mantequilla", 112, "392,00 €", "-12,00 €", "380,00 €"},
                        {"Matcha Latte", 87, "495,90 €", "-0,00 €", "495,90 €"}
                };
            }

            case EXTRAS_MAS_VENDIDOS -> {
                cols = new String[]{"Extra", "Veces vendido", "Importe generado", "Categoría principal"};
                rows = new Object[][]{
                        {"Shot extra", 96, "144,00 €", "Café"},
                        {"Sirope vainilla", 81, "121,50 €", "Café"},
                        {"Leche avena", 62, "93,00 €", "Bebidas"},
                        {"Topping caramelo", 41, "61,50 €", "Frappé"}
                };
            }

            case COMBOS_VENDIDOS -> {
                cols = new String[]{"Combo", "Veces vendido", "Precio original", "Precio final", "Ahorro"};
                rows = new Object[][]{
                        {"Desayuno Classic", 22, "198,00 €", "171,60 €", "26,40 €"},
                        {"Merienda Duo", 15, "165,00 €", "141,75 €", "23,25 €"},
                        {"Frío + Snack", 11, "121,00 €", "101,20 €", "19,80 €"}
                };
            }

            case DESCUENTOS_APLICADOS -> {
                cols = new String[]{"Descuento", "Usos", "Base", "Importe descuento", "Beneficio"};
                rows = new Object[][]{
                        {"Empleado 30%", 18, "420,00 €", "126,00 €", "Interno"},
                        {"Promo QR 10%", 25, "615,00 €", "61,50 €", "Cliente"},
                        {"Cupón lanzamiento", 9, "180,00 €", "26,25 €", "Cliente"}
                };
            }

            case DEVOLUCIONES_POR_PRODUCTO -> {
                cols = new String[]{"Producto", "Cantidad devuelta", "Reembolso", "Repone stock"};
                rows = new Object[][]{
                        {"Termo Acero", 1, "14,95 €", "Sí"},
                        {"Taza Térmica Pequeña", 1, "9,95 €", "Sí"},
                        {"Aquarius Limón 50cl", 1, "2,30 €", "Sí"}
                };
            }

            case RANKING_EMPLEADOS_POR_VENTAS -> {
                cols = new String[]{"Posición", "Empleado", "Ventas", "Tickets", "Ticket medio"};
                rows = new Object[][]{
                        {1, "Ana", "1.840,50 €", 183, "10,06 €"},
                        {2, "Luis", "1.620,30 €", 172, "9,42 €"},
                        {3, "Marta", "1.488,90 €", 169, "8,81 €"}
                };
            }

            case RANKING_EMPLEADOS_POR_EXTRAS -> {
                cols = new String[]{"Posición", "Empleado", "Extras vendidos", "Importe extras"};
                rows = new Object[][]{
                        {1, "Ana", 82, "123,00 €"},
                        {2, "Luis", 69, "103,50 €"},
                        {3, "Marta", 58, "87,00 €"}
                };
            }

            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> {
                cols = new String[]{"Empleado", "Producto", "Unidades", "Importe"};
                rows = new Object[][]{
                        {"Ana", "Latte", 118, "590,00 €"},
                        {"Ana", "Croissant", 102, "357,00 €"},
                        {"Luis", "Cappuccino", 95, "522,50 €"},
                        {"Marta", "Matcha", 74, "421,80 €"}
                };
            }

            case VENTAS_POR_CAJA -> {
                cols = new String[]{"Caja", "Ventas", "Devoluciones", "Neto", "Tickets"};
                rows = new Object[][]{
                        {"Caja 1", "2.136,40 €", "-52,34 €", "2.084,06 €", 214},
                        {"Caja 2", "1.845,20 €", "-18,00 €", "1.827,20 €", 196}
                };
            }

            case VENTAS_POR_SESION_CAJA -> {
                cols = new String[]{"Sesión", "Caja", "Empleado apertura", "Ventas", "Neto"};
                rows = new Object[][]{
                        {"#101", "Caja 1", "Ana", "980,40 €", "960,40 €"},
                        {"#102", "Caja 1", "Luis", "1.120,20 €", "1.110,20 €"},
                        {"#103", "Caja 2", "Marta", "875,90 €", "856,90 €"}
                };
            }

            case TIEMPOS_POR_ESTACION -> {
                cols = new String[]{"Estación", "Tiempo medio", "Items procesados", "Pico máximo"};
                rows = new Object[][]{
                        {"Bebidas calientes", "145 s", 128, "240 s"},
                        {"Bebidas frías", "122 s", 91, "198 s"},
                        {"Comida", "210 s", 74, "320 s"}
                };
            }

            case MERMA_POR_PERIODO -> {
                cols = new String[]{"Fecha", "Tipo", "Cantidad", "Observación"};
                rows = new Object[][]{
                        {"01/04/2026", "Producto", "45,00", "Mermas varias"},
                        {"02/04/2026", "Ingrediente", "62,50", "Ajuste por caducidad"},
                        {"03/04/2026", "Producto", "38,00", "Rotura / devolución interna"}
                };
            }

            case MOVIMIENTOS_STOCK_AJUSTES -> {
                cols = new String[]{"Fecha", "Movimiento", "Objeto", "Cantidad", "Motivo"};
                rows = new Object[][]{
                        {"01/04/2026", "ENTRADA", "Producto", "18", "Reposición"},
                        {"01/04/2026", "SALIDA", "Ingrediente", "36", "Consumo operativo"},
                        {"02/04/2026", "AJUSTE", "Ingrediente", "7", "Corrección"},
                        {"02/04/2026", "MERMA", "Producto", "5", "Pérdida"}
                };
            }
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void showEmpty(String text) {
        lblEmpty.setText(text);
        centerLayout.show(centerPanel, "EMPTY");
    }
    
    public void cargarVentasPorDia(InformeVentasPorDiaResultDTO result, ModoVistaInforme modoVista) {
        lblTitle.setText("Resultado · Ventas por día");

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            String[] cols = {"Fecha", "Empleado", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][7];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
                rows[i][1] = row.getNombreEmpleado();
                rows[i][2] = formatMoney(row.getTotalVentas());
                rows[i][3] = formatMoney(row.getTotalDevoluciones());
                rows[i][4] = formatMoney(row.getTotalNeto());
                rows[i][5] = row.getNumeroTickets();
                rows[i][6] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        } else {
            String[] cols = {"Fecha", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][6];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
                rows[i][1] = formatMoney(row.getTotalVentas());
                rows[i][2] = formatMoney(row.getTotalDevoluciones());
                rows[i][3] = formatMoney(row.getTotalNeto());
                rows[i][4] = row.getNumeroTickets();
                rows[i][5] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        }

        centerLayout.show(centerPanel, "TABLE");
    }
   
    public void cargarResumenEjecutivo(InformeResumenEjecutivoResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Resumen ejecutivo.");
            return;
        }

        lblTitle.setText("Resultado · Resumen ejecutivo");

        String[] cols = {"Indicador", "Valor", "Descripción"};
        Object[][] rows = new Object[result.getRows().size()][3];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getIndicador();
            rows[i][1] = formatMoney(row.getValor());
            rows[i][2] = row.getDescripcion();
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarVentasPorFranjaHoraria(InformeVentasFranjaResultDTO result, ModoVistaInforme modoVista) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas por franja horaria.");
            return;
        }

        lblTitle.setText("Resultado · Ventas por franja horaria");

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            String[] cols = {"Franja", "Empleado", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][7];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFranja();
                rows[i][1] = row.getNombreEmpleado();
                rows[i][2] = formatMoney(row.getTotalVentas());
                rows[i][3] = formatMoney(row.getTotalDevoluciones());
                rows[i][4] = formatMoney(row.getTotalNeto());
                rows[i][5] = row.getNumeroTickets();
                rows[i][6] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        } else {
            String[] cols = {"Franja", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][6];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFranja();
                rows[i][1] = formatMoney(row.getTotalVentas());
                rows[i][2] = formatMoney(row.getTotalDevoluciones());
                rows[i][3] = formatMoney(row.getTotalNeto());
                rows[i][4] = row.getNumeroTickets();
                rows[i][5] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        }

        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarTicketMedioPorDia(InformeTicketMedioDiaResultDTO result, ModoVistaInforme modoVista) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ticket medio por día.");
            return;
        }

        lblTitle.setText("Resultado · Ticket medio por día");

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            String[] cols = {"Fecha", "Empleado", "Tickets", "Ventas", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][5];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
                rows[i][1] = row.getNombreEmpleado();
                rows[i][2] = row.getNumeroTickets();
                rows[i][3] = formatMoney(row.getTotalVentas());
                rows[i][4] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        } else {
            String[] cols = {"Fecha", "Tickets", "Ventas", "Ticket medio"};
            Object[][] rows = new Object[result.getRows().size()][4];

            for (int i = 0; i < result.getRows().size(); i++) {
                var row = result.getRows().get(i);
                rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
                rows[i][1] = row.getNumeroTickets();
                rows[i][2] = formatMoney(row.getTotalVentas());
                rows[i][3] = formatMoney(row.getTicketMedio());
            }

            model.setDataVector(rows, cols);
        }

        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarPagosPorMetodo(InformePagosMetodoResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Pagos por método.");
            return;
        }

        lblTitle.setText("Resultado · Pagos por método");

        String[] cols = {"Método", "Operaciones", "Importe", "% total"};
        Object[][] rows = new Object[result.getRows().size()][4];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getMetodoPago();
            rows[i][1] = row.getNumeroOperaciones();
            rows[i][2] = formatMoney(row.getImporteTotal());
            rows[i][3] = formatPercent(row.getPorcentajeSobreTotal());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarVentasNetasVsDevoluciones(InformeNetoVsDevolucionesResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas netas vs devoluciones.");
            return;
        }

        lblTitle.setText("Resultado · Ventas netas vs devoluciones");

        String[] cols = {"Fecha", "Ventas", "Devoluciones", "Neto", "Ratio devolución"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
            rows[i][1] = formatMoney(row.getTotalVentas());
            rows[i][2] = formatMoney(row.getTotalDevoluciones());
            rows[i][3] = formatMoney(row.getTotalNeto());
            rows[i][4] = formatPercent(row.getRatioDevolucion());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }
    
    public void cargarProductosMasVendidos(InformeProductosVendidosResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Productos más vendidos.");
            return;
        }

        lblTitle.setText("Resultado · Productos más vendidos");

        String[] cols = {"Producto", "Unidades", "Bruto", "Devoluciones", "Neto"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreProducto();
            rows[i][1] = row.getUnidadesVendidas();
            rows[i][2] = formatMoney(row.getImporteBruto());
            rows[i][3] = formatMoney(row.getImporteDevoluciones());
            rows[i][4] = formatMoney(row.getImporteNeto());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarExtrasMasVendidos(InformeExtrasVendidosResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Extras más vendidos.");
            return;
        }

        lblTitle.setText("Resultado · Extras más vendidos");

        String[] cols = {"Extra", "Grupo", "Veces", "Importe"};
        Object[][] rows = new Object[result.getRows().size()][4];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreExtra();
            rows[i][1] = row.getGrupoPrincipal();
            rows[i][2] = row.getVecesVendido();
            rows[i][3] = formatMoney(row.getImporteGenerado());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarCombosVendidos(InformeCombosVendidosResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Combos vendidos.");
            return;
        }

        lblTitle.setText("Resultado · Combos vendidos");

        String[] cols = {"Combo", "Veces", "Precio original", "Precio final", "Ahorro"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreCombo();
            rows[i][1] = row.getVecesVendido();
            rows[i][2] = formatMoney(row.getPrecioOriginalTotal());
            rows[i][3] = formatMoney(row.getPrecioFinalTotal());
            rows[i][4] = formatMoney(row.getAhorroTotal());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarDescuentosAplicados(InformeDescuentosAplicadosResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Descuentos aplicados.");
            return;
        }

        lblTitle.setText("Resultado · Descuentos aplicados");

        String[] cols = {"Descuento", "Tipo", "Usos", "Base", "Importe descuento"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreDescuento();
            rows[i][1] = row.getTipoBeneficio();
            rows[i][2] = row.getNumeroUsos();
            rows[i][3] = formatMoney(row.getImporteBase());
            rows[i][4] = formatMoney(row.getImporteDescuento());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarDevolucionesPorProducto(InformeDevolucionesProductoResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Devoluciones por producto.");
            return;
        }

        lblTitle.setText("Resultado · Devoluciones por producto");

        String[] cols = {"Producto", "Cantidad devuelta", "Reembolso", "Nº devoluciones", "Repone stock"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreProducto();
            rows[i][1] = row.getCantidadDevuelta();
            rows[i][2] = formatMoney(row.getImporteReembolsado());
            rows[i][3] = row.getNumeroDevoluciones();
            rows[i][4] = row.isReponeStock() ? "Sí" : "No";
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }
    
    public void cargarRankingEmpleadosPorVentas(InformeRankingEmpleadosVentasResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ranking empleados por ventas.");
            return;
        }

        lblTitle.setText("Resultado · Ranking empleados por ventas");

        String[] cols = {"Posición", "Empleado", "Ventas", "Tickets", "Ticket medio"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getPosicion();
            rows[i][1] = row.getNombreEmpleado();
            rows[i][2] = formatMoney(row.getTotalVentas());
            rows[i][3] = row.getNumeroTickets();
            rows[i][4] = formatMoney(row.getTicketMedio());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarRankingEmpleadosPorExtras(InformeRankingEmpleadosExtrasResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ranking empleados por extras.");
            return;
        }

        lblTitle.setText("Resultado · Ranking empleados por extras");

        String[] cols = {"Posición", "Empleado", "Extras vendidos", "Importe extras"};
        Object[][] rows = new Object[result.getRows().size()][4];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getPosicion();
            rows[i][1] = row.getNombreEmpleado();
            rows[i][2] = row.getTotalExtrasVendidos();
            rows[i][3] = formatMoney(row.getImporteExtras());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarProductosVendidosPorEmpleado(InformeProductosPorEmpleadoResultDTO result, ModoVistaInforme modoVista) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Productos vendidos por empleado.");
            return;
        }

        lblTitle.setText("Resultado · Productos vendidos por empleado");

        String[] cols = {"Empleado", "Producto", "Unidades", "Importe"};
        Object[][] rows = new Object[result.getRows().size()][4];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreEmpleado();
            rows[i][1] = row.getNombreProducto();
            rows[i][2] = row.getUnidadesVendidas();
            rows[i][3] = formatMoney(row.getImporteTotal());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarVentasPorCaja(InformeVentasCajaResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas por caja.");
            return;
        }

        lblTitle.setText("Resultado · Ventas por caja");

        String[] cols = {"Caja", "Ventas", "Devoluciones", "Neto", "Tickets"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreCaja();
            rows[i][1] = formatMoney(row.getTotalVentas());
            rows[i][2] = formatMoney(row.getTotalDevoluciones());
            rows[i][3] = formatMoney(row.getTotalNeto());
            rows[i][4] = row.getNumeroTickets();
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarVentasPorSesionCaja(InformeVentasSesionCajaResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas por sesión de caja.");
            return;
        }

        lblTitle.setText("Resultado · Ventas por sesión de caja");

        String[] cols = {"Sesión", "Caja", "Empleado apertura", "Apertura", "Cierre", "Ventas", "Devoluciones", "Neto"};
        Object[][] rows = new Object[result.getRows().size()][8];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getIdSesion();
            rows[i][1] = row.getNombreCaja();
            rows[i][2] = row.getNombreEmpleadoApertura();
            rows[i][3] = row.getFechaApertura() != null ? row.getFechaApertura().toString() : "";
            rows[i][4] = row.getFechaCierre() != null ? row.getFechaCierre().toString() : "";
            rows[i][5] = formatMoney(row.getTotalVentas());
            rows[i][6] = formatMoney(row.getTotalDevoluciones());
            rows[i][7] = formatMoney(row.getTotalNeto());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarTiemposPorEstacion(InformeTiemposEstacionResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Tiempos por estación.");
            return;
        }

        lblTitle.setText("Resultado · Tiempos por estación");

        String[] cols = {"Estación", "Tiempo medio (s)", "Tiempo máximo (s)", "Items procesados"};
        Object[][] rows = new Object[result.getRows().size()][4];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreEstacion();
            rows[i][1] = row.getTiempoMedioSegundos();
            rows[i][2] = row.getTiempoMaximoSegundos();
            rows[i][3] = row.getItemsProcesados();
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarMermaPorPeriodo(InformeMermaPeriodoResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Merma por período.");
            return;
        }

        lblTitle.setText("Resultado · Merma por período");

        String[] cols = {"Fecha", "Tipo", "Origen", "Motivo", "Cantidad", "Observaciones"};
        Object[][] rows = new Object[result.getRows().size()][6];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
            rows[i][1] = row.getTipoMerma();
            rows[i][2] = row.getOrigen();
            rows[i][3] = row.getMotivo();
            rows[i][4] = row.getCantidad();
            rows[i][5] = row.getObservaciones();
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarMovimientosStockAjustes(InformeMovimientoStockResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Movimientos de stock / ajustes.");
            return;
        }

        lblTitle.setText("Resultado · Movimientos de stock / ajustes");

        String[] cols = {"Fecha", "Tipo movimiento", "Tipo objeto", "Objeto", "Cantidad", "Motivo", "Referencia"};
        Object[][] rows = new Object[result.getRows().size()][7];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getFecha() != null ? row.getFecha().toString() : "";
            rows[i][1] = row.getTipoMovimiento();
            rows[i][2] = row.getTipoObjeto();
            rows[i][3] = row.getNombreObjeto();
            rows[i][4] = row.getCantidad();
            rows[i][5] = row.getMotivo();
            rows[i][6] = row.getReferencia();
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }
  
    public void cargarVentasProductoPorEmpleado(InformeVentasProductoEmpleadoResultDTO result, ModoVistaInforme modoVista) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas producto por empleado.");
            return;
        }

        lblTitle.setText("Resultado · Ventas producto por empleado");

        String[] cols = {"Empleado", "Producto", "Unidades", "Bruto", "Descuento", "Neto"};
        Object[][] rows = new Object[result.getRows().size()][6];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreEmpleado();
            rows[i][1] = row.getNombreProducto();
            rows[i][2] = row.getUnidadesVendidas();
            rows[i][3] = formatMoney(row.getImporteBruto());
            rows[i][4] = formatMoney(row.getImporteDescuento());
            rows[i][5] = formatMoney(row.getImporteNeto());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarRankingEmpleadosPorProducto(InformeRankingEmpleadosProductoResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ranking empleados por producto.");
            return;
        }

        lblTitle.setText("Resultado · Ranking empleados por producto");

        String[] cols = {"Posición", "Empleado", "Producto", "Unidades", "Neto"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getPosicion();
            rows[i][1] = row.getNombreEmpleado();
            rows[i][2] = row.getNombreProducto();
            rows[i][3] = row.getUnidadesVendidas();
            rows[i][4] = formatMoney(row.getImporteNeto());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarVentasExtraPorEmpleado(InformeVentasExtraEmpleadoResultDTO result, ModoVistaInforme modoVista) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ventas extra por empleado.");
            return;
        }

        lblTitle.setText("Resultado · Ventas extra por empleado");

        String[] cols = {"Empleado", "Extra", "Tipo", "Veces", "Importe"};
        Object[][] rows = new Object[result.getRows().size()][5];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getNombreEmpleado();
            rows[i][1] = row.getNombreExtra();
            rows[i][2] = row.getTipoExtra();
            rows[i][3] = row.getVecesVendido();
            rows[i][4] = formatMoney(row.getImporteGenerado());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }

    public void cargarRankingEmpleadosPorExtra(InformeRankingEmpleadosExtraResultDTO result) {
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            showEmpty("No hay datos para Ranking empleados por extra.");
            return;
        }

        lblTitle.setText("Resultado · Ranking empleados por extra");

        String[] cols = {"Posición", "Empleado", "Extra", "Tipo", "Veces", "Importe"};
        Object[][] rows = new Object[result.getRows().size()][6];

        for (int i = 0; i < result.getRows().size(); i++) {
            var row = result.getRows().get(i);
            rows[i][0] = row.getPosicion();
            rows[i][1] = row.getNombreEmpleado();
            rows[i][2] = row.getNombreExtra();
            rows[i][3] = row.getTipoExtra();
            rows[i][4] = row.getVecesVendido();
            rows[i][5] = formatMoney(row.getImporteGenerado());
        }

        model.setDataVector(rows, cols);
        centerLayout.show(centerPanel, "TABLE");
    }
    

    private String formatMoney(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f €", safe);
    }
    private String formatPercent(java.math.BigDecimal value) {
        java.math.BigDecimal safe = value != null ? value : java.math.BigDecimal.ZERO;
        return String.format(java.util.Locale.forLanguageTag("es-ES"), "%,.2f %%", safe);
    }
    
}
package ui.informes;

import enums.TipoInforme;
import ui.common.InformeUiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel central de tabla de resultados.
 *
 * Primera fase:
 * - muestra datos demo
 * - sirve para validar diseño, tamaños y estructura visual
 */
public class InformeTablaPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;

    public InformeTablaPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        JLabel title = InformeUiTheme.createSectionTitle("Resultado tabular");
        add(title, BorderLayout.NORTH);

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

        add(scrollPane, BorderLayout.CENTER);

        cargarDemo(TipoInforme.RESUMEN_EJECUTIVO);
    }

    public void cargarDemo(TipoInforme tipo) {
        switch (tipo) {
            case INFORME_CAJA -> setData(
                    new String[]{"Caja", "Apertura", "Ventas", "Devoluciones", "Neto", "Estado"},
                    new Object[][]{
                            {"Caja 1", "250,00 €", "2.136,40 €", "-52,34 €", "2.084,06 €", "Cerrada"},
                            {"Caja 2", "300,00 €", "1.845,20 €", "-18,00 €", "1.827,20 €", "Abierta"}
                    }
            );
            case VENTAS_POR_DIA -> setData(
                    new String[]{"Fecha", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio"},
                    new Object[][]{
                            {"01/04/2026", "782,40 €", "-12,00 €", "770,40 €", "84", "9,19 €"},
                            {"02/04/2026", "905,20 €", "-22,00 €", "883,20 €", "96", "9,43 €"},
                            {"03/04/2026", "846,10 €", "-18,20 €", "827,90 €", "92", "9,19 €"},
                            {"04/04/2026", "1.012,60 €", "-31,00 €", "981,60 €", "108", "9,38 €"},
                            {"05/04/2026", "1.123,40 €", "-31,00 €", "1.092,40 €", "120", "9,36 €"}
                    }
            );
            case TICKET_MEDIO_POR_DIA -> setData(
                    new String[]{"Fecha", "Tickets", "Ventas", "Ticket medio", "Combo medio"},
                    new Object[][]{
                            {"01/04/2026", "84", "782,40 €", "9,19 €", "11,80 €"},
                            {"02/04/2026", "96", "905,20 €", "9,43 €", "12,10 €"},
                            {"03/04/2026", "92", "846,10 €", "9,19 €", "11,75 €"},
                            {"04/04/2026", "108", "1.012,60 €", "9,38 €", "12,42 €"},
                            {"05/04/2026", "120", "1.123,40 €", "9,36 €", "12,58 €"}
                    }
            );
            case PAGOS_POR_METODO -> setData(
                    new String[]{"Método", "Operaciones", "Importe", "% total"},
                    new Object[][]{
                            {"Tarjeta", "326", "2.848,20 €", "58,0%"},
                            {"Efectivo", "214", "1.936,40 €", "39,4%"},
                            {"Vale", "14", "127,60 €", "2,6%"}
                    }
            );
            case PRODUCTOS_MAS_VENDIDOS -> setData(
                    new String[]{"Producto", "Unidades", "Bruto", "Devoluciones", "Neto"},
                    new Object[][]{
                            {"Latte Clásico", "148", "740,00 €", "-8,00 €", "732,00 €"},
                            {"Cappuccino Grande", "123", "676,50 €", "-6,50 €", "670,00 €"},
                            {"Croissant Mantequilla", "112", "392,00 €", "-12,00 €", "380,00 €"},
                            {"Matcha Latte", "87", "495,90 €", "-0,00 €", "495,90 €"}
                    }
            );
            case COMBOS_VENDIDOS -> setData(
                    new String[]{"Combo", "Veces vendido", "Precio original", "Precio final", "Ahorro"},
                    new Object[][]{
                            {"Desayuno Classic", "22", "198,00 €", "171,60 €", "26,40 €"},
                            {"Merienda Duo", "15", "165,00 €", "141,75 €", "23,25 €"},
                            {"Frío + Snack", "11", "121,00 €", "101,20 €", "19,80 €"}
                    }
            );
            case DESCUENTOS -> setData(
                    new String[]{"Descuento", "Usos", "Base", "Importe descuento", "Beneficio"},
                    new Object[][]{
                            {"Empleado 30%", "18", "420,00 €", "126,00 €", "Interno"},
                            {"Promo QR 10%", "25", "615,00 €", "61,50 €", "Cliente"},
                            {"Cupón lanzamiento", "9", "180,00 €", "26,25 €", "Cliente"}
                    }
            );
            case DEVOLUCIONES -> setData(
                    new String[]{"Fecha", "Producto", "Cantidad", "Reembolso", "Repone stock", "Admin"},
                    new Object[][]{
                            {"05/04/2026", "Termo Acero", "1", "14,95 €", "Sí", "Administrador"},
                            {"05/04/2026", "Taza Térmica Pequeña", "1", "9,95 €", "Sí", "Administrador"},
                            {"05/04/2026", "Aquarius Limón 50cl", "1", "2,30 €", "Sí", "Administrador"}
                    }
            );
            case RESUMEN_EJECUTIVO -> setData(
                    new String[]{"Indicador", "Valor"},
                    new Object[][]{
                            {"Ventas brutas", "6.214,10 €"},
                            {"Devoluciones", "-145,20 €"},
                            {"Neto", "6.068,90 €"},
                            {"Ticket medio", "9,42 €"},
                            {"Combos vendidos", "36"},
                            {"Ahorro total", "415,85 €"}
                    }
            );
        }
    }

    private void setData(String[] columns, Object[][] rows) {
        model.setDataVector(rows, columns);
    }
}

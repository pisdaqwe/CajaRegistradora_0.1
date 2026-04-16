package ui.informes;

import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class ComercialFilterPanel extends BaseInformeFilterPanel {

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;
    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;
    private final JComboBox<String> cmbCategoria;
    private final JComboBox<String> cmbSubcategoria;
    private final JComboBox<String> cmbProducto;
    private final JComboBox<String> cmbObjetoComercial;
    private final JSpinner spTopN;

    public ComercialFilterPanel() {
        JPanel content = createContentPanel();

        spFechaDesde = createDateSpinner(firstDayOfCurrentMonth());
        spFechaHasta = createDateSpinner(new Date());

        cmbSucursal = new JComboBox<>(new String[]{"Todas las sucursales", "Tienda principal"});
        cmbCaja = new JComboBox<>(new String[]{"Todas las cajas", "Caja 1", "Caja 2"});
        cmbCategoria = new JComboBox<>(new String[]{"Todas las categorías", "Bebidas", "Comida", "Merch"});
        cmbSubcategoria = new JComboBox<>(new String[]{"Todas las subcategorías", "Espresso & Café", "Tés & Matcha", "Fríos / Iced"});
        cmbProducto = new JComboBox<>(new String[]{"Todos los productos", "Latte", "Cappuccino", "Croissant"});
        cmbObjetoComercial = new JComboBox<>(new String[]{"Todos", "Combo Desayuno", "Descuento Empleado", "Shot Espresso", "Sirope Vainilla"});
        spTopN = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));

        InformeUiTheme.styleCombo(cmbSucursal);
        InformeUiTheme.styleCombo(cmbCaja);
        InformeUiTheme.styleCombo(cmbCategoria);
        InformeUiTheme.styleCombo(cmbSubcategoria);
        InformeUiTheme.styleCombo(cmbProducto);
        InformeUiTheme.styleCombo(cmbObjetoComercial);
        InformeUiTheme.styleSpinner(spTopN);

        content.add(createFieldBlock("Fecha desde", spFechaDesde));
        content.add(createFieldBlock("Fecha hasta", spFechaHasta));
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));
        content.add(createFieldBlock("Categoría", cmbCategoria));
        content.add(createFieldBlock("Subcategoría", cmbSubcategoria));
        content.add(createFieldBlock("Producto", cmbProducto));
        content.add(createFieldBlock("Objeto comercial", cmbObjetoComercial));
        content.add(createFieldBlock("Top N", spTopN));

        add(content, BorderLayout.NORTH);
    }

    @Override
    protected void onTipoInformeChanged(TipoInforme tipoInforme) {
        switch (tipoInforme) {
            case PRODUCTOS_MAS_VENDIDOS -> cmbObjetoComercial.setEnabled(false);
            case EXTRAS_MAS_VENDIDOS, COMBOS_VENDIDOS, DESCUENTOS_APLICADOS -> cmbObjetoComercial.setEnabled(true);
            case DEVOLUCIONES_POR_PRODUCTO -> cmbObjetoComercial.setEnabled(false);
            default -> cmbObjetoComercial.setEnabled(true);
        }
    }

    @Override
    public void reset() {
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbSucursal.setSelectedIndex(0);
        cmbCaja.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        cmbSubcategoria.setSelectedIndex(0);
        cmbProducto.setSelectedIndex(0);
        cmbObjetoComercial.setSelectedIndex(0);
        spTopN.setValue(10);
    }

    private JSpinner createDateSpinner(Date date) {
        JSpinner spinner = new JSpinner(new SpinnerDateModel(date, null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        InformeUiTheme.styleSpinner(spinner);
        return spinner;
    }

    private Date firstDayOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
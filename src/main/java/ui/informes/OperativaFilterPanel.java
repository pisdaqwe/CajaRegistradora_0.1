package ui.informes;

import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class OperativaFilterPanel extends BaseInformeFilterPanel {

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;
    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;
    private final JComboBox<String> cmbSesion;
    private final JComboBox<String> cmbEstacion;
    private final JComboBox<String> cmbTipoMovimiento;
    private final JComboBox<String> cmbObjeto;

    public OperativaFilterPanel() {
        JPanel content = createContentPanel();

        spFechaDesde = createDateSpinner(firstDayOfCurrentMonth());
        spFechaHasta = createDateSpinner(new Date());

        cmbSucursal = new JComboBox<>(new String[]{"Todas las sucursales", "Tienda principal"});
        cmbCaja = new JComboBox<>(new String[]{"Todas las cajas", "Caja 1", "Caja 2"});
        cmbSesion = new JComboBox<>(new String[]{"Todas las sesiones", "Sesión #101", "Sesión #102"});
        cmbEstacion = new JComboBox<>(new String[]{"Todas las estaciones", "Bebidas calientes", "Bebidas frías", "Comida"});
        cmbTipoMovimiento = new JComboBox<>(new String[]{"Todos los movimientos", "ENTRADA", "SALIDA", "AJUSTE", "MERMA"});
        cmbObjeto = new JComboBox<>(new String[]{"Todos", "Producto", "Ingrediente", "Extra"});

        InformeUiTheme.styleCombo(cmbSucursal);
        InformeUiTheme.styleCombo(cmbCaja);
        InformeUiTheme.styleCombo(cmbSesion);
        InformeUiTheme.styleCombo(cmbEstacion);
        InformeUiTheme.styleCombo(cmbTipoMovimiento);
        InformeUiTheme.styleCombo(cmbObjeto);

        content.add(createFieldBlock("Fecha desde", spFechaDesde));
        content.add(createFieldBlock("Fecha hasta", spFechaHasta));
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));
        content.add(createFieldBlock("Sesión", cmbSesion));
        content.add(createFieldBlock("Estación", cmbEstacion));
        content.add(createFieldBlock("Tipo de movimiento", cmbTipoMovimiento));
        content.add(createFieldBlock("Objeto", cmbObjeto));

        add(content, BorderLayout.NORTH);
    }

    @Override
    protected void onTipoInformeChanged(TipoInforme tipoInforme) {
        cmbSesion.setEnabled(tipoInforme == TipoInforme.VENTAS_POR_SESION_CAJA);
        cmbEstacion.setEnabled(tipoInforme == TipoInforme.TIEMPOS_POR_ESTACION);
        cmbTipoMovimiento.setEnabled(tipoInforme == TipoInforme.MOVIMIENTOS_STOCK_AJUSTES
                || tipoInforme == TipoInforme.MERMA_POR_PERIODO);
    }

    @Override
    public void reset() {
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbSucursal.setSelectedIndex(0);
        cmbCaja.setSelectedIndex(0);
        cmbSesion.setSelectedIndex(0);
        cmbEstacion.setSelectedIndex(0);
        cmbTipoMovimiento.setSelectedIndex(0);
        cmbObjeto.setSelectedIndex(0);
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

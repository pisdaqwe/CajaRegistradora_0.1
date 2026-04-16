package ui.informes;

import enums.ModoVistaInforme;
import enums.TipoInforme;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class EquipoFilterPanel extends BaseInformeFilterPanel {

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;

    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;

    private final JCheckBox chkTodos;
    private final DefaultListModel<String> empleadosModel;
    private final JList<String> lstEmpleados;

    private final JCheckBox chkCompararHorasFichaje;
    private final JSpinner spTopN;

    public EquipoFilterPanel() {
        JPanel content = createContentPanel();

        spFechaDesde = createDateSpinner(firstDayOfCurrentMonth());
        spFechaHasta = createDateSpinner(new Date());

        cmbSucursal = new JComboBox<>(new String[]{
                "Todas las sucursales",
                "Tienda principal"
        });
        InformeUiTheme.styleCombo(cmbSucursal);

        cmbCaja = new JComboBox<>(new String[]{
                "Todas las cajas",
                "Caja 1",
                "Caja 2"
        });
        InformeUiTheme.styleCombo(cmbCaja);

        chkTodos = new JCheckBox("Todos los empleados");
        chkTodos.setSelected(true);
        InformeUiTheme.styleCheckBox(chkTodos);

        empleadosModel = new DefaultListModel<>();
        empleadosModel.addElement("Administrador Temporal");
        empleadosModel.addElement("Bogdan");
        empleadosModel.addElement("Ana");
        empleadosModel.addElement("Luis");
        empleadosModel.addElement("Marta");

        lstEmpleados = new JList<>(empleadosModel);
        lstEmpleados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstEmpleados.setVisibleRowCount(5);
        InformeUiTheme.styleList(lstEmpleados);
        lstEmpleados.setEnabled(false);

        chkCompararHorasFichaje = new JCheckBox("Comparar con horas de fichaje");
        InformeUiTheme.styleCheckBox(chkCompararHorasFichaje);

        spTopN = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        InformeUiTheme.styleSpinner(spTopN);

        chkTodos.addActionListener(e -> {
            boolean todos = chkTodos.isSelected();
            lstEmpleados.setEnabled(!todos);
            if (todos) {
                lstEmpleados.clearSelection();
            }
        });

        content.add(createFieldBlock("Fecha desde", spFechaDesde));
        content.add(createFieldBlock("Fecha hasta", spFechaHasta));
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));

        JPanel empleadosBlock = new JPanel(new BorderLayout(0, 8));
        empleadosBlock.setOpaque(false);
        empleadosBlock.add(InformeUiTheme.createFieldLabel("Empleados"), BorderLayout.NORTH);

        JPanel empleadosTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        empleadosTop.setOpaque(false);
        empleadosTop.add(chkTodos);

        JScrollPane scrollEmpleados = new JScrollPane(lstEmpleados);
        scrollEmpleados.setPreferredSize(new Dimension(250, 120));
        InformeUiTheme.styleScrollPane(scrollEmpleados);

        JPanel empleadosCenter = new JPanel(new BorderLayout(0, 8));
        empleadosCenter.setOpaque(false);
        empleadosCenter.add(empleadosTop, BorderLayout.NORTH);
        empleadosCenter.add(scrollEmpleados, BorderLayout.CENTER);

        empleadosBlock.add(empleadosCenter, BorderLayout.CENTER);
        content.add(empleadosBlock);

        content.add(createFieldBlock("Top N", spTopN));
        content.add(createFieldBlock("Opciones", wrapCheckBox(chkCompararHorasFichaje)));

        add(content, BorderLayout.NORTH);
    }

    @Override
    protected void onTipoInformeChanged(TipoInforme tipoInforme) {
        boolean usaTopN = tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS
                || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS;

        spTopN.setEnabled(usaTopN);
        chkCompararHorasFichaje.setEnabled(tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS);

        if (!usaTopN) {
            spTopN.setValue(10);
        }

        if (tipoInforme != TipoInforme.RANKING_EMPLEADOS_POR_VENTAS) {
            chkCompararHorasFichaje.setSelected(false);
        }
    }

    @Override
    public void reset() {
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbSucursal.setSelectedIndex(0);
        cmbCaja.setSelectedIndex(0);
        chkTodos.setSelected(true);
        lstEmpleados.clearSelection();
        lstEmpleados.setEnabled(false);
        chkCompararHorasFichaje.setSelected(false);
        spTopN.setValue(10);
    }

    @Override
    public ModoVistaInforme getModoVista() {
        return ModoVistaInforme.AGREGADA;
    }

    @Override
    public String buildSummary() {
        String empleados;
        if (chkTodos.isSelected()) {
            empleados = "Todos los empleados";
        } else if (lstEmpleados.getSelectedValuesList().isEmpty()) {
            empleados = "Sin empleados seleccionados";
        } else {
            empleados = String.join(", ", lstEmpleados.getSelectedValuesList());
        }

        return String.format(
                "%s · %s",
                currentTipoInforme != null ? currentTipoInforme.getDisplayName() : "Informe de equipo",
                empleados
        );
    }

    private JPanel wrapCheckBox(JCheckBox checkBox) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.add(checkBox);
        return panel;
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
package ui.informes;

import dtoS.InformeFiltroDTO;
import enums.TipoInforme;
import ui.common.InformeUiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Panel lateral de filtros del módulo de informes.
 *
 * Primera fase:
 * - filtros visuales
 * - lectura de estado
 * - todavía sin carga real desde BD
 */
public class InformeFiltrosPanel extends JPanel {

    private final JComboBox<TipoInforme> cmbTipoInforme;

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;

    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;
    private final JComboBox<String> cmbEmpleado;
    private final JComboBox<String> cmbMetodoPago;

    private final JSpinner spTopN;

    private final JCheckBox chkIncluirDevoluciones;

    public InformeFiltrosPanel() {
        setLayout(new BorderLayout());
        setBackground(InformeUiTheme.CARD_BG);
        setBorder(InformeUiTheme.createCardBorder());

        JLabel title = InformeUiTheme.createSectionTitle("Filtros del informe");
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        cmbTipoInforme = new JComboBox<>(TipoInforme.values());
        InformeUiTheme.styleCombo(cmbTipoInforme);

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

        cmbEmpleado = new JComboBox<>(new String[]{
                "Todos los empleados",
                "Administrador Temporal",
                "Cajero Demo"
        });
        InformeUiTheme.styleCombo(cmbEmpleado);

        cmbMetodoPago = new JComboBox<>(new String[]{
                "Todos los métodos",
                "Efectivo",
                "Tarjeta",
                "Vale"
        });
        InformeUiTheme.styleCombo(cmbMetodoPago);

        spTopN = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        InformeUiTheme.styleSpinner(spTopN);

        chkIncluirDevoluciones = new JCheckBox("Incluir devoluciones");
        chkIncluirDevoluciones.setSelected(true);
        InformeUiTheme.styleCheckBox(chkIncluirDevoluciones);

        content.add(createFieldBlock("Tipo de informe", cmbTipoInforme));
        content.add(createFieldBlock("Fecha desde", spFechaDesde));
        content.add(createFieldBlock("Fecha hasta", spFechaHasta));
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));
        content.add(createFieldBlock("Empleado", cmbEmpleado));
        content.add(createFieldBlock("Método de pago", cmbMetodoPago));
        content.add(createFieldBlock("Top N", spTopN));

        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setOpaque(false);
        checkPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        checkPanel.add(chkIncluirDevoluciones);
        content.add(checkPanel);

        content.add(Box.createVerticalGlue());

        add(content, BorderLayout.CENTER);

        setPreferredSize(new Dimension(320, 600));
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        block.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel label = InformeUiTheme.createFieldLabel(labelText);

        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
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

    public InformeFiltroDTO leerFiltros() {
        InformeFiltroDTO dto = new InformeFiltroDTO();

        dto.setTipoInforme((TipoInforme) cmbTipoInforme.getSelectedItem());
        dto.setFechaDesde(toLocalDate((Date) spFechaDesde.getValue()));
        dto.setFechaHasta(toLocalDate((Date) spFechaHasta.getValue()));
        dto.setIncluirDevoluciones(chkIncluirDevoluciones.isSelected());
        dto.setTopN((Integer) spTopN.getValue());

        String metodo = (String) cmbMetodoPago.getSelectedItem();
        if (metodo != null && !metodo.equalsIgnoreCase("Todos los métodos")) {
            dto.setMetodoPago(metodo);
        }

        // De momento los IDs se quedan null
        dto.setIdSucursal(null);
        dto.setIdCaja(null);
        dto.setIdEmpleado(null);
        dto.setIdCategoria(null);
        dto.setIdProducto(null);

        return dto;
    }

    public TipoInforme getTipoInformeSeleccionado() {
        return (TipoInforme) cmbTipoInforme.getSelectedItem();
    }

    public void setTipoInforme(TipoInforme tipoInforme) {
        cmbTipoInforme.setSelectedItem(tipoInforme);
    }

    public void reset() {
        cmbTipoInforme.setSelectedItem(TipoInforme.RESUMEN_EJECUTIVO);
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbSucursal.setSelectedIndex(0);
        cmbCaja.setSelectedIndex(0);
        cmbEmpleado.setSelectedIndex(0);
        cmbMetodoPago.setSelectedIndex(0);
        spTopN.setValue(10);
        chkIncluirDevoluciones.setSelected(true);
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
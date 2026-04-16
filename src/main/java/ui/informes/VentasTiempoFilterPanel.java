package ui.informes;

import app.AppContext;
import dtoS.InformeFiltroDTO;
import enums.AgrupacionTemporal;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import model.Caja;
import model.Sucursal;
import model.Usuario;
import service.AppServices;
import ui.theme.InformeUiTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VentasTiempoFilterPanel extends BaseInformeFilterPanel {

    private final AppServices services;

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;
    private final JComboBox<AgrupacionTemporal> cmbAgrupacion;
    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;
    private final JComboBox<String> cmbMetodoPago;

    private final JCheckBox chkTodosEmpleados;
    private final DefaultListModel<String> empleadosModel;
    private final JList<String> lstEmpleados;

    private final JCheckBox chkIncluirDevoluciones;

    private final JRadioButton rbAgregada;
    private final JRadioButton rbComparativa;

    private final List<Caja> cajasCargadas = new ArrayList<>();
    private final List<Usuario> usuariosCargados = new ArrayList<>();

    public VentasTiempoFilterPanel(AppServices services) {
        this.services = services;

        JPanel content = createContentPanel();

        spFechaDesde = createDateSpinner(firstDayOfCurrentMonth());
        spFechaHasta = createDateSpinner(new Date());

        cmbAgrupacion = new JComboBox<>(AgrupacionTemporal.values());
        InformeUiTheme.styleCombo(cmbAgrupacion);

        cmbSucursal = new JComboBox<>();
        InformeUiTheme.styleCombo(cmbSucursal);
        cmbSucursal.setEnabled(false);

        cmbCaja = new JComboBox<>();
        InformeUiTheme.styleCombo(cmbCaja);

        cmbMetodoPago = new JComboBox<>(new String[]{
                "Todos los métodos",
                "Efectivo",
                "Tarjeta",
                "Vale"
        });
        InformeUiTheme.styleCombo(cmbMetodoPago);

        chkTodosEmpleados = new JCheckBox("Todos los empleados");
        chkTodosEmpleados.setSelected(true);
        InformeUiTheme.styleCheckBox(chkTodosEmpleados);

        empleadosModel = new DefaultListModel<>();
        lstEmpleados = new JList<>(empleadosModel);
        lstEmpleados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        InformeUiTheme.styleList(lstEmpleados);
        lstEmpleados.setEnabled(false);

        chkIncluirDevoluciones = new JCheckBox("Incluir devoluciones");
        chkIncluirDevoluciones.setSelected(true);
        InformeUiTheme.styleCheckBox(chkIncluirDevoluciones);

        rbAgregada = new JRadioButton(ModoVistaInforme.AGREGADA.getLabel());
        rbComparativa = new JRadioButton(ModoVistaInforme.COMPARATIVA.getLabel());

        rbAgregada.setOpaque(false);
        rbComparativa.setOpaque(false);
        rbAgregada.setForeground(InformeUiTheme.TEXT_PRIMARY);
        rbComparativa.setForeground(InformeUiTheme.TEXT_PRIMARY);
        rbAgregada.setSelected(true);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAgregada);
        bg.add(rbComparativa);

        chkTodosEmpleados.addActionListener(e -> {
            boolean todos = chkTodosEmpleados.isSelected();
            lstEmpleados.setEnabled(!todos);
            if (todos) {
                lstEmpleados.clearSelection();
            }
        });

        content.add(createFieldBlock("Fecha desde", spFechaDesde));
        content.add(createFieldBlock("Fecha hasta", spFechaHasta));
        content.add(createFieldBlock("Agrupación temporal", cmbAgrupacion));
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));
        content.add(createFieldBlock("Método de pago", cmbMetodoPago));

        JPanel empleadosBlock = new JPanel(new BorderLayout(0, 8));
        empleadosBlock.setOpaque(false);
        empleadosBlock.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        empleadosBlock.add(InformeUiTheme.createFieldLabel("Empleados"), BorderLayout.NORTH);

        JPanel empleadosTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        empleadosTop.setOpaque(false);
        empleadosTop.add(chkTodosEmpleados);

        JScrollPane spEmpleados = new JScrollPane(lstEmpleados);
        spEmpleados.setPreferredSize(new Dimension(250, 120));
        InformeUiTheme.styleScrollPane(spEmpleados);

        JPanel empleadosContainer = new JPanel(new BorderLayout(0, 8));
        empleadosContainer.setOpaque(false);
        empleadosContainer.add(empleadosTop, BorderLayout.NORTH);
        empleadosContainer.add(spEmpleados, BorderLayout.CENTER);

        empleadosBlock.add(empleadosContainer, BorderLayout.CENTER);
        content.add(empleadosBlock);

        JPanel modoBlock = new JPanel(new GridLayout(2, 1, 0, 6));
        modoBlock.setOpaque(false);
        modoBlock.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        modoBlock.add(rbAgregada);
        modoBlock.add(rbComparativa);

        content.add(createFieldBlock("Modo de visualización", modoBlock));
        content.add(createFieldBlock("Opciones", wrapCheckBox(chkIncluirDevoluciones)));

        add(content, BorderLayout.NORTH);

        cargarSucursalActual();
        cargarCajasPorSucursalActual();
        cargarUsuariosPorSucursalActual();
    }

    @Override
    protected void onTipoInformeChanged(TipoInforme tipoInforme) {
        boolean ventasPorDia = tipoInforme == TipoInforme.VENTAS_POR_DIA;
        boolean ticketMedio = tipoInforme == TipoInforme.TICKET_MEDIO_POR_DIA;
        boolean franja = tipoInforme == TipoInforme.VENTAS_POR_FRANJA_HORARIA;
        boolean pagos = tipoInforme == TipoInforme.PAGOS_POR_METODO;

        cmbAgrupacion.setEnabled(!ticketMedio && !pagos);

        if (franja) {
            cmbAgrupacion.setSelectedItem(AgrupacionTemporal.HORA);
            cmbAgrupacion.setEnabled(false);
        } else if (ventasPorDia || ticketMedio) {
            cmbAgrupacion.setSelectedItem(AgrupacionTemporal.DIA);
        }

        rbComparativa.setEnabled(ventasPorDia || ticketMedio || franja);
        if (!(ventasPorDia || ticketMedio || franja)) {
            rbAgregada.setSelected(true);
        }
    }

    @Override
    public void reset() {
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbAgrupacion.setSelectedItem(AgrupacionTemporal.DIA);
        cmbCaja.setSelectedIndex(0);
        cmbMetodoPago.setSelectedIndex(0);
        chkTodosEmpleados.setSelected(true);
        lstEmpleados.clearSelection();
        lstEmpleados.setEnabled(false);
        chkIncluirDevoluciones.setSelected(true);
        rbAgregada.setSelected(true);

        cargarSucursalActual();
        cargarCajasPorSucursalActual();
        cargarUsuariosPorSucursalActual();
    }

    @Override
    public ModoVistaInforme getModoVista() {
        return rbComparativa.isSelected() ? ModoVistaInforme.COMPARATIVA : ModoVistaInforme.AGREGADA;
    }

    @Override
    public String buildSummary() {
        String empleados;
        if (chkTodosEmpleados.isSelected()) {
            empleados = "Todos los empleados";
        } else if (lstEmpleados.getSelectedValuesList().isEmpty()) {
            empleados = "Sin empleados seleccionados";
        } else {
            empleados = String.join(", ", lstEmpleados.getSelectedValuesList());
        }

        return String.format(
                "%s · %s · %s",
                currentTipoInforme != null ? currentTipoInforme.getDisplayName() : "Informe",
                empleados,
                getModoVista().getLabel()
        );
    }

    @Override
    public InformeFiltroDTO buildFiltroDTO() {
        InformeFiltroDTO dto = new InformeFiltroDTO();

        dto.setTipoInforme(currentTipoInforme);
        dto.setModoVista(getModoVista());
        dto.setAgrupacionTemporal((AgrupacionTemporal) cmbAgrupacion.getSelectedItem());

        dto.setFechaDesde(toLocalDate((Date) spFechaDesde.getValue()));
        dto.setFechaHasta(toLocalDate((Date) spFechaHasta.getValue()));

        dto.setIdSucursal(AppContext.getIdSucursal());
        dto.setIdCaja(getSelectedCajaId());

        dto.setTodosLosEmpleados(chkTodosEmpleados.isSelected());
        dto.setIdsEmpleados(buildSelectedEmpleadoIds());

        String metodo = (String) cmbMetodoPago.getSelectedItem();
        if (metodo != null && !metodo.equalsIgnoreCase("Todos los métodos")) {
            dto.setMetodoPago(metodo);
        }

        dto.setIncluirDevoluciones(chkIncluirDevoluciones.isSelected());

        return dto;
    }

    private void cargarSucursalActual() {
        int idSucursal = AppContext.getIdSucursal();
        if (idSucursal <= 0) {
            throw new IllegalStateException("AppContext no tiene una sucursal actual válida");
        }

        Sucursal sucursal = services.sucursalService.findByIdOrThrow(idSucursal);

        cmbSucursal.removeAllItems();
        cmbSucursal.addItem(sucursal.getNombre());
        cmbSucursal.setSelectedIndex(0);
        cmbSucursal.setEnabled(false);
    }

    private void cargarCajasPorSucursalActual() {
        int idSucursal = AppContext.getIdSucursal();

        cajasCargadas.clear();
        cmbCaja.removeAllItems();
        cmbCaja.addItem("Todas las cajas");

        List<Caja> cajas = services.sesionCajaService.findActivasBySucursal(idSucursal);
        cajasCargadas.addAll(cajas);

        for (Caja caja : cajas) {
            cmbCaja.addItem(caja.getNombre());
        }

        cmbCaja.setSelectedIndex(0);
    }

    private void cargarUsuariosPorSucursalActual() {
        int idSucursal = AppContext.getIdSucursal();

        usuariosCargados.clear();
        empleadosModel.clear();

        List<Usuario> usuarios = services.usuarioService.findActivosBySucursal(idSucursal);
        usuariosCargados.addAll(usuarios);

        for (Usuario usuario : usuarios) {
            String label = usuario.getNombre() + " (" + usuario.getUsuario() + ")";
            empleadosModel.addElement(label);
        }

        chkTodosEmpleados.setSelected(true);
        lstEmpleados.clearSelection();
        lstEmpleados.setEnabled(false);
    }

    private Integer getSelectedCajaId() {
        int index = cmbCaja.getSelectedIndex();

        if (index <= 0) {
            return null;
        }

        int cajaIndex = index - 1;
        if (cajaIndex >= 0 && cajaIndex < cajasCargadas.size()) {
            return cajasCargadas.get(cajaIndex).getIdCaja();
        }

        return null;
    }

    private List<Integer> buildSelectedEmpleadoIds() {
        List<Integer> ids = new ArrayList<>();

        if (chkTodosEmpleados.isSelected()) {
            return ids;
        }

        for (int selectedIndex : lstEmpleados.getSelectedIndices()) {
            if (selectedIndex >= 0 && selectedIndex < usuariosCargados.size()) {
                ids.add(usuariosCargados.get(selectedIndex).getIdUsuario());
            }
        }

        return ids;
    }

    private JPanel wrapCheckBox(JCheckBox checkBox) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(checkBox);
        return p;
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

    private LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
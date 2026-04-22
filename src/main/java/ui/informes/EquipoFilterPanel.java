package ui.informes;

import app.AppContext;
import dtoS.ExtraDTO;
import dtoS.InformeFiltroDTO;
import dtoS.ProductoDTO;
import enums.AgrupacionTemporal;
import enums.ModoVistaInforme;
import enums.TipoInforme;
import model.Caja;
import model.Extra;
import model.Producto;
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

public class EquipoFilterPanel extends BaseInformeFilterPanel {

    private final AppServices services;

    private final JSpinner spFechaDesde;
    private final JSpinner spFechaHasta;
    private final JComboBox<String> cmbSucursal;
    private final JComboBox<String> cmbCaja;
    private final JComboBox<String> cmbMetodoPago;

    private final JCheckBox chkTodosEmpleados;
    private final DefaultListModel<String> empleadosModel;
    private final JList<String> lstEmpleados;

    private final JSpinner spTopN;

    private final JRadioButton rbAgregada;
    private final JRadioButton rbComparativa;

    private final List<Caja> cajasCargadas = new ArrayList<>();
    private final List<Usuario> usuariosCargados = new ArrayList<>();
    
    private final JCheckBox chkTodosProductos;
    private final DefaultListModel<String> productosModel;
    private final JList<String> lstProductos;

    private final JCheckBox chkTodosExtras;
    private final DefaultListModel<String> extrasModel;
    private final JList<String> lstExtras;

    private final List<ProductoDTO> productosCargados = new ArrayList<>();
    private final List<ExtraDTO> extrasCargados = new ArrayList<>();

    public EquipoFilterPanel(AppServices services) {
        this.services = services;

        JPanel content = createContentPanel();

        spFechaDesde = createDateSpinner(firstDayOfCurrentMonth());
        spFechaHasta = createDateSpinner(new Date());

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

        spTopN = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        InformeUiTheme.styleSpinner(spTopN);

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
        content.add(createFieldBlock("Sucursal", cmbSucursal));
        content.add(createFieldBlock("Caja", cmbCaja));
        content.add(createFieldBlock("Método de pago", cmbMetodoPago));
        content.add(createFieldBlock("Top N", spTopN));

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

        add(content, BorderLayout.NORTH);
        chkTodosProductos = new JCheckBox("Todos los productos");
        chkTodosProductos.setSelected(true);
        InformeUiTheme.styleCheckBox(chkTodosProductos);

        productosModel = new DefaultListModel<>();
        lstProductos = new JList<>(productosModel);
        lstProductos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        InformeUiTheme.styleList(lstProductos);
        lstProductos.setEnabled(false);

        chkTodosExtras = new JCheckBox("Todos los extras");
        chkTodosExtras.setSelected(true);
        InformeUiTheme.styleCheckBox(chkTodosExtras);

        extrasModel = new DefaultListModel<>();
        lstExtras = new JList<>(extrasModel);
        lstExtras.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        InformeUiTheme.styleList(lstExtras);
        lstExtras.setEnabled(false);

        chkTodosProductos.addActionListener(e -> {
            boolean todos = chkTodosProductos.isSelected();
            lstProductos.setEnabled(!todos);
            if (todos) {
                lstProductos.clearSelection();
            }
        });

        chkTodosExtras.addActionListener(e -> {
            boolean todos = chkTodosExtras.isSelected();
            lstExtras.setEnabled(!todos);
            if (todos) {
                lstExtras.clearSelection();
            }
        });
        JPanel productosBlock = new JPanel(new BorderLayout(0, 8));
        productosBlock.setOpaque(false);
        productosBlock.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        productosBlock.add(InformeUiTheme.createFieldLabel("Productos"), BorderLayout.NORTH);

        JPanel productosTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        productosTop.setOpaque(false);
        productosTop.add(chkTodosProductos);

        JScrollPane spProductos = new JScrollPane(lstProductos);
        spProductos.setPreferredSize(new Dimension(250, 120));
        InformeUiTheme.styleScrollPane(spProductos);

        JPanel productosContainer = new JPanel(new BorderLayout(0, 8));
        productosContainer.setOpaque(false);
        productosContainer.add(productosTop, BorderLayout.NORTH);
        productosContainer.add(spProductos, BorderLayout.CENTER);

        productosBlock.add(productosContainer, BorderLayout.CENTER);
        content.add(productosBlock);

        JPanel extrasBlock = new JPanel(new BorderLayout(0, 8));
        extrasBlock.setOpaque(false);
        extrasBlock.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        extrasBlock.add(InformeUiTheme.createFieldLabel("Extras"), BorderLayout.NORTH);

        JPanel extrasTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        extrasTop.setOpaque(false);
        extrasTop.add(chkTodosExtras);

        JScrollPane spExtras = new JScrollPane(lstExtras);
        spExtras.setPreferredSize(new Dimension(250, 120));
        InformeUiTheme.styleScrollPane(spExtras);

        JPanel extrasContainer = new JPanel(new BorderLayout(0, 8));
        extrasContainer.setOpaque(false);
        extrasContainer.add(extrasTop, BorderLayout.NORTH);
        extrasContainer.add(spExtras, BorderLayout.CENTER);

        extrasBlock.add(extrasContainer, BorderLayout.CENTER);
        content.add(extrasBlock);

        cargarSucursalActual();
        cargarCajasPorSucursalActual();
        cargarUsuariosPorSucursalActual();
        cargarExtrasActivos();
        cargarProductosActivos();
    }

    @Override
    
    protected void onTipoInformeChanged(TipoInforme tipoInforme) {
        boolean ranking =
                tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS
                        || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS
                        || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO
                        || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA;

        boolean productosPorEmpleado =
                tipoInforme == TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO
                        || tipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO
                        || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO;

        boolean extrasPorEmpleado =
                tipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO
                        || tipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA;

        spTopN.setEnabled(ranking);
        if (!ranking) {
            spTopN.setValue(10);
        }

        rbComparativa.setEnabled(
                tipoInforme == TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO
                        || tipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO
                        || tipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO
        );

        if (!rbComparativa.isEnabled()) {
            rbAgregada.setSelected(true);
        }

        chkTodosProductos.setEnabled(productosPorEmpleado);
        if (!productosPorEmpleado) {
            chkTodosProductos.setSelected(true);
            lstProductos.clearSelection();
            lstProductos.setEnabled(false);
        } else {
            lstProductos.setEnabled(!chkTodosProductos.isSelected());
        }

        chkTodosExtras.setEnabled(extrasPorEmpleado);
        if (!extrasPorEmpleado) {
            chkTodosExtras.setSelected(true);
            lstExtras.clearSelection();
            lstExtras.setEnabled(false);
        } else {
            lstExtras.setEnabled(!chkTodosExtras.isSelected());
        }
    }

    @Override
    public void reset() {
        spFechaDesde.setValue(firstDayOfCurrentMonth());
        spFechaHasta.setValue(new Date());
        cmbCaja.setSelectedIndex(0);
        cmbMetodoPago.setSelectedIndex(0);
        chkTodosEmpleados.setSelected(true);
        lstEmpleados.clearSelection();
        lstEmpleados.setEnabled(false);
        spTopN.setValue(10);
        rbAgregada.setSelected(true);
        chkTodosProductos.setSelected(true);
        lstProductos.clearSelection();
        lstProductos.setEnabled(false);

        chkTodosExtras.setSelected(true);
        lstExtras.clearSelection();
        lstExtras.setEnabled(false);

        cargarSucursalActual();
        cargarCajasPorSucursalActual();
        cargarUsuariosPorSucursalActual();
        cargarProductosActivos();
        cargarExtrasActivos();
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
        dto.setAgrupacionTemporal(AgrupacionTemporal.DIA);

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

        if (currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_VENTAS
                || currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS) {
            dto.setTopN((Integer) spTopN.getValue());
        }
        boolean usaProductos =
                currentTipoInforme == TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO
                        || currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO;

        dto.setTodosLosProductos(!usaProductos || chkTodosProductos.isSelected());
        dto.setIdsProductos(usaProductos ? buildSelectedProductoIds() : new ArrayList<>());

        boolean usaExtras =
                currentTipoInforme == TipoInforme.VENTAS_EXTRA_POR_EMPLEADO
                        || currentTipoInforme == TipoInforme.RANKING_EMPLEADOS_POR_EXTRA;

        dto.setTodosLosExtras(!usaExtras || chkTodosExtras.isSelected());
        dto.setIdsExtras(usaExtras ? buildSelectedExtraIds() : new ArrayList<>());

        return dto;
    }
    
    private void cargarProductosActivos() {
        productosCargados.clear();
        productosModel.clear();

        List<ProductoDTO> productos = services.catalogoService.getProductosActivosVisiblesParaInformes();
        productosCargados.addAll(productos);

        for (ProductoDTO producto : productos) {
            productosModel.addElement(producto.getNombre());
        }

        chkTodosProductos.setSelected(true);
        lstProductos.clearSelection();
        lstProductos.setEnabled(false);
    }

    private void cargarExtrasActivos() {
        extrasCargados.clear();
        extrasModel.clear();

        List<ExtraDTO> extras = services.productoPersonalizacionService.getTodosLosExtrasActivosParaInformes();
        extrasCargados.addAll(extras);

        for (ExtraDTO extra : extras) {
            String tipo = extra.getTipo() != null ? " [" + extra.getTipo() + "]" : "";
            extrasModel.addElement(extra.getNombre() + tipo);
        }

        chkTodosExtras.setSelected(true);
        lstExtras.clearSelection();
        lstExtras.setEnabled(false);
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
    
    private List<Integer> buildSelectedProductoIds() {
        List<Integer> ids = new ArrayList<>();

        if (chkTodosProductos.isSelected()) {
            return ids;
        }

        for (int selectedIndex : lstProductos.getSelectedIndices()) {
            if (selectedIndex >= 0 && selectedIndex < productosCargados.size()) {
                ids.add(productosCargados.get(selectedIndex).getIdProducto());
            }
        }

        return ids;
    }

    private List<Integer> buildSelectedExtraIds() {
        List<Integer> ids = new ArrayList<>();

        if (chkTodosExtras.isSelected()) {
            return ids;
        }

        for (int selectedIndex : lstExtras.getSelectedIndices()) {
            if (selectedIndex >= 0 && selectedIndex < extrasCargados.size()) {
                ids.add(extrasCargados.get(selectedIndex).getIdExtra());
            }
        }

        return ids;
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
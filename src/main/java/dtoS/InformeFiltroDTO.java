package dtoS;

import enums.AgrupacionTemporal;
import enums.ModoVistaInforme;
import enums.TipoInforme;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InformeFiltroDTO {

    // =========================================
    // IDENTIDAD DEL INFORME
    // =========================================
    private TipoInforme tipoInforme;
    private ModoVistaInforme modoVista = ModoVistaInforme.AGREGADA;
    private AgrupacionTemporal agrupacionTemporal = AgrupacionTemporal.DIA;

    // =========================================
    // RANGO TEMPORAL
    // =========================================
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // =========================================
    // ÁMBITO ORGANIZATIVO
    // =========================================
    private Integer idSucursal;
    private Integer idCaja;
    private Integer idSesionCaja;

    // =========================================
    // EMPLEADOS
    // =========================================
    private boolean todosLosEmpleados = true;
    private List<Integer> idsEmpleados = new ArrayList<>();

    // =========================================
    // FILTROS COMERCIALES
    // =========================================
    private String metodoPago;
    private Integer idCategoria;
    private Integer idSubcategoria;
    private Integer idProducto;
    private Integer idExtra;
    private Integer idCombo;
    
    // =========================================
    // FLAGS / OPCIONES
    // =========================================
    private boolean incluirDevoluciones = true;
    private boolean incluirAnuladas = false;
    private Integer topN;
    
    private boolean todosLosProductos = true;
    private List<Integer> idsProductos = new ArrayList<>();

    private boolean todosLosExtras = true;
    private List<Integer> idsExtras = new ArrayList<>();
    
	private Integer idDescuento;

    // =========================================
    // OPERATIVA / STOCK
    // =========================================
    private Integer idEstacion;
    private String tipoMovimientoStock;

    public boolean isTodosLosProductos() {
		return todosLosProductos;
	}

	public void setTodosLosProductos(boolean todosLosProductos) {
		this.todosLosProductos = todosLosProductos;
	}

	public List<Integer> getIdsProductos() {
		return idsProductos;
	}

	public void setIdsProductos(List<Integer> idsProductos) {
		this.idsProductos = idsProductos;
	}

	public boolean isTodosLosExtras() {
		return todosLosExtras;
	}

	public void setTodosLosExtras(boolean todosLosExtras) {
		this.todosLosExtras = todosLosExtras;
	}

	public List<Integer> getIdsExtras() {
		return idsExtras;
	}

	public void setIdsExtras(List<Integer> idsExtras) {
		this.idsExtras = idsExtras;
	}

    public TipoInforme getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(TipoInforme tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public ModoVistaInforme getModoVista() {
        return modoVista;
    }

    public void setModoVista(ModoVistaInforme modoVista) {
        this.modoVista = modoVista;
    }

    public AgrupacionTemporal getAgrupacionTemporal() {
        return agrupacionTemporal;
    }

    public void setAgrupacionTemporal(AgrupacionTemporal agrupacionTemporal) {
        this.agrupacionTemporal = agrupacionTemporal;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public Integer getIdSesionCaja() {
        return idSesionCaja;
    }

    public void setIdSesionCaja(Integer idSesionCaja) {
        this.idSesionCaja = idSesionCaja;
    }

    public boolean isTodosLosEmpleados() {
        return todosLosEmpleados;
    }

    public void setTodosLosEmpleados(boolean todosLosEmpleados) {
        this.todosLosEmpleados = todosLosEmpleados;
    }

    public List<Integer> getIdsEmpleados() {
        return idsEmpleados;
    }

    public void setIdsEmpleados(List<Integer> idsEmpleados) {
        this.idsEmpleados = idsEmpleados;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Integer idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(Integer idExtra) {
        this.idExtra = idExtra;
    }

    public Integer getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Integer idCombo) {
        this.idCombo = idCombo;
    }

    public Integer getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Integer idDescuento) {
        this.idDescuento = idDescuento;
    }

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getTipoMovimientoStock() {
        return tipoMovimientoStock;
    }

    public void setTipoMovimientoStock(String tipoMovimientoStock) {
        this.tipoMovimientoStock = tipoMovimientoStock;
    }

    public boolean isIncluirDevoluciones() {
        return incluirDevoluciones;
    }

    public void setIncluirDevoluciones(boolean incluirDevoluciones) {
        this.incluirDevoluciones = incluirDevoluciones;
    }

    public boolean isIncluirAnuladas() {
        return incluirAnuladas;
    }

    public void setIncluirAnuladas(boolean incluirAnuladas) {
        this.incluirAnuladas = incluirAnuladas;
    }

    public Integer getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }
}
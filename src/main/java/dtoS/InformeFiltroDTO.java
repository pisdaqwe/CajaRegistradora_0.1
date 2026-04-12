package dtoS;

import enums.TipoInforme;

import java.time.LocalDate;

/**
 * DTO que representa los filtros seleccionados en la pantalla de informes.
 *
 * En esta primera fase se usa para transportar el estado de la UI
 * aunque todavía no estemos lanzando consultas SQL reales.
 */
public class InformeFiltroDTO {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    private Integer idSucursal;
    private Integer idCaja;
    private Integer idEmpleado;
    private Integer idCategoria;
    private Integer idProducto;

    private String metodoPago;

    private boolean incluirDevoluciones;
    private Integer topN;

    private TipoInforme tipoInforme;

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

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean isIncluirDevoluciones() {
        return incluirDevoluciones;
    }

    public void setIncluirDevoluciones(boolean incluirDevoluciones) {
        this.incluirDevoluciones = incluirDevoluciones;
    }

    public Integer getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }

    public TipoInforme getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(TipoInforme tipoInforme) {
        this.tipoInforme = tipoInforme;
    }
}
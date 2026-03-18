package model;

import java.time.LocalDateTime;

/**
 * Modelo persistente de la tabla cola_impresion.
 *
 * Representa una fila real de BD.
 */
public class ColaImpresion {

    private Integer idCola;
    private Integer idVenta;
    private Integer idItem;
    private Integer idEstacion;

    /**
     * Aquí guardaremos el snapshot del item.
     * Recomendación: JSON serializado como String.
     */
    private String descripcion;

    private Boolean impreso;
    private Boolean preparado;
    private Boolean cancelado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaImpresion;
    private LocalDateTime fechaPreparado;

    public ColaImpresion() {
    }

    public ColaImpresion(Integer idCola,
                         Integer idVenta,
                         Integer idItem,
                         Integer idEstacion,
                         String descripcion,
                         Boolean impreso,
                         Boolean preparado,
                         Boolean cancelado,
                         LocalDateTime fechaCreacion,
                         LocalDateTime fechaImpresion,
                         LocalDateTime fechaPreparado) {
        this.idCola = idCola;
        this.idVenta = idVenta;
        this.idItem = idItem;
        this.idEstacion = idEstacion;
        this.descripcion = descripcion;
        this.impreso = impreso;
        this.preparado = preparado;
        this.cancelado = cancelado;
        this.fechaCreacion = fechaCreacion;
        this.fechaImpresion = fechaImpresion;
        this.fechaPreparado = fechaPreparado;
    }

    public Integer getIdCola() {
        return idCola;
    }

    public void setIdCola(Integer idCola) {
        this.idCola = idCola;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getImpreso() {
        return impreso;
    }

    public void setImpreso(Boolean impreso) {
        this.impreso = impreso;
    }

    public Boolean getPreparado() {
        return preparado;
    }

    public void setPreparado(Boolean preparado) {
        this.preparado = preparado;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaImpresion() {
        return fechaImpresion;
    }

    public void setFechaImpresion(LocalDateTime fechaImpresion) {
        this.fechaImpresion = fechaImpresion;
    }

    public LocalDateTime getFechaPreparado() {
        return fechaPreparado;
    }

    public void setFechaPreparado(LocalDateTime fechaPreparado) {
        this.fechaPreparado = fechaPreparado;
    }

    /**
     * Helpers útiles de negocio.
     */
    public boolean isImpreso() {
        return Boolean.TRUE.equals(impreso);
    }

    public boolean isPreparado() {
        return Boolean.TRUE.equals(preparado);
    }

    public boolean isCancelado() {
        return Boolean.TRUE.equals(cancelado);
    }

    public boolean isPendiente() {
        return !isImpreso() && !isPreparado() && !isCancelado();
    }

    public boolean isEnPreparacion() {
        return isImpreso() && !isPreparado() && !isCancelado();
    }

    @Override
    public String toString() {
        return "ColaImpresion{" +
                "idCola=" + idCola +
                ", idVenta=" + idVenta +
                ", idItem=" + idItem +
                ", idEstacion=" + idEstacion +
                ", impreso=" + impreso +
                ", preparado=" + preparado +
                ", cancelado=" + cancelado +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaImpresion=" + fechaImpresion +
                ", fechaPreparado=" + fechaPreparado +
                '}';
    }
}
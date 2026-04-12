package dtoS;

import java.math.BigDecimal;

/**
 * Regla que indica cómo un extra modifica la receta.
 *
 * tipoRegla:
 * - ADD
 * - REPLACE
 * - REMOVE
 * - NO_STOCK_EFFECT
 *
 * fuenteIngrediente:
 * - FIJO
 * - CAFE_SELECCIONADO
 * - CAFE_BASE
 * - CAFE_SELECCIONADO_O_BASE
 */
public class ExtraRecetaReglaDTO {

    private int idRegla;
    private int idExtra;

    private Integer idProducto;
    private Integer idTamano;

    private String tipoRegla;
    private String fuenteIngrediente;

    private Integer idIngredienteOrigen;
    private String nombreIngredienteOrigen;

    private Integer idIngredienteDestino;
    private String nombreIngredienteDestino;

    private BigDecimal cantidad;

    private Integer idUnidad;
    private String nombreUnidad;

    private boolean heredaCantidadOrigen;
    private boolean activo;
    private int orden;
    private String observaciones;

    public int getIdRegla() {
        return idRegla;
    }

    public void setIdRegla(int idRegla) {
        this.idRegla = idRegla;
    }

    public int getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(int idExtra) {
        this.idExtra = idExtra;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdTamano() {
        return idTamano;
    }

    public void setIdTamano(Integer idTamano) {
        this.idTamano = idTamano;
    }

    public String getTipoRegla() {
        return tipoRegla;
    }

    public void setTipoRegla(String tipoRegla) {
        this.tipoRegla = tipoRegla;
    }

    public String getFuenteIngrediente() {
        return fuenteIngrediente;
    }

    public void setFuenteIngrediente(String fuenteIngrediente) {
        this.fuenteIngrediente = fuenteIngrediente;
    }

    public Integer getIdIngredienteOrigen() {
        return idIngredienteOrigen;
    }

    public void setIdIngredienteOrigen(Integer idIngredienteOrigen) {
        this.idIngredienteOrigen = idIngredienteOrigen;
    }

    public String getNombreIngredienteOrigen() {
        return nombreIngredienteOrigen;
    }

    public void setNombreIngredienteOrigen(String nombreIngredienteOrigen) {
        this.nombreIngredienteOrigen = nombreIngredienteOrigen;
    }

    public Integer getIdIngredienteDestino() {
        return idIngredienteDestino;
    }

    public void setIdIngredienteDestino(Integer idIngredienteDestino) {
        this.idIngredienteDestino = idIngredienteDestino;
    }

    public String getNombreIngredienteDestino() {
        return nombreIngredienteDestino;
    }

    public void setNombreIngredienteDestino(String nombreIngredienteDestino) {
        this.nombreIngredienteDestino = nombreIngredienteDestino;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Integer idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getNombreUnidad() {
        return nombreUnidad;
    }

    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }

    public boolean isHeredaCantidadOrigen() {
        return heredaCantidadOrigen;
    }

    public void setHeredaCantidadOrigen(boolean heredaCantidadOrigen) {
        this.heredaCantidadOrigen = heredaCantidadOrigen;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
package dtoS;

import java.math.BigDecimal;

/**
 * Request para registrar un movimiento de stock.
 *
 * Puede representar movimientos de:
 * - producto
 * - ingrediente
 *
 * Y además puede enlazarse opcionalmente con una merma:
 * - idMerma
 * - idMermaItem
 */
public class RegistrarMovimientoStockRequest {

    private int idSucursal;

    private Integer idProducto;
    private Integer idIngrediente;

    private BigDecimal cantidad;
    private Integer idUnidad;

    /**
     * Tipos válidos esperados:
     * - ENTRADA
     * - SALIDA
     * - AJUSTE
     */
    private String tipo;

    private String referencia;
    private String motivo;

    /**
     * NUEVO:
     * id de la merma origen, si aplica.
     */
    private Integer idMerma;

    /**
     * NUEVO:
     * id de la línea de merma origen, si aplica.
     */
    private Integer idMermaItem;

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getIdMerma() {
        return idMerma;
    }

    public void setIdMerma(Integer idMerma) {
        this.idMerma = idMerma;
    }

    public Integer getIdMermaItem() {
        return idMermaItem;
    }

    public void setIdMermaItem(Integer idMermaItem) {
        this.idMermaItem = idMermaItem;
    }
}
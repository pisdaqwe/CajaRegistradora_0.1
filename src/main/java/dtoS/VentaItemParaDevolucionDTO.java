package dtoS;

import java.math.BigDecimal;

/**
 * DTO de una línea de venta preparada para devolución.
 */
public class VentaItemParaDevolucionDTO {

    private int idVentaItem;
    private int idVenta;
    private int idProducto;

    private String nombreProducto;
    private String tamano;
    private String descripcionResumen;

    private int cantidadVendida;
    private int cantidadYaDevuelta;
    private int cantidadDisponible;

    private BigDecimal precioUnitario;
    private BigDecimal subtotalBruto;
    private BigDecimal importeDescuentoLinea;
    private BigDecimal subtotalFinal;
    private BigDecimal iva;

    /**
     * True solo si el producto permite reposición real de stock
     * en la sucursal actual.
     */
    private boolean permiteReponerStock;

    public int getIdVentaItem() {
        return idVentaItem;
    }

    public void setIdVentaItem(int idVentaItem) {
        this.idVentaItem = idVentaItem;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getDescripcionResumen() {
        return descripcionResumen;
    }

    public void setDescripcionResumen(String descripcionResumen) {
        this.descripcionResumen = descripcionResumen;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public int getCantidadYaDevuelta() {
        return cantidadYaDevuelta;
    }

    public void setCantidadYaDevuelta(int cantidadYaDevuelta) {
        this.cantidadYaDevuelta = cantidadYaDevuelta;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotalBruto() {
        return subtotalBruto;
    }

    public void setSubtotalBruto(BigDecimal subtotalBruto) {
        this.subtotalBruto = subtotalBruto;
    }

    public BigDecimal getImporteDescuentoLinea() {
        return importeDescuentoLinea;
    }

    public void setImporteDescuentoLinea(BigDecimal importeDescuentoLinea) {
        this.importeDescuentoLinea = importeDescuentoLinea;
    }

    public BigDecimal getSubtotalFinal() {
        return subtotalFinal;
    }

    public void setSubtotalFinal(BigDecimal subtotalFinal) {
        this.subtotalFinal = subtotalFinal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public boolean isPermiteReponerStock() {
        return permiteReponerStock;
    }

    public void setPermiteReponerStock(boolean permiteReponerStock) {
        this.permiteReponerStock = permiteReponerStock;
    }
}

package dtoS;

import java.math.BigDecimal;

public class InformeDevolucionesProductoRowDTO {

    private Integer idProducto;
    private String nombreProducto;

    private Integer cantidadDevuelta = 0;
    private BigDecimal importeReembolsado = BigDecimal.ZERO;
    private Integer numeroDevoluciones = 0;
    private boolean reponeStock;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(Integer cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public BigDecimal getImporteReembolsado() {
        return importeReembolsado;
    }

    public void setImporteReembolsado(BigDecimal importeReembolsado) {
        this.importeReembolsado = importeReembolsado;
    }

    public Integer getNumeroDevoluciones() {
        return numeroDevoluciones;
    }

    public void setNumeroDevoluciones(Integer numeroDevoluciones) {
        this.numeroDevoluciones = numeroDevoluciones;
    }

    public boolean isReponeStock() {
        return reponeStock;
    }

    public void setReponeStock(boolean reponeStock) {
        this.reponeStock = reponeStock;
    }
}
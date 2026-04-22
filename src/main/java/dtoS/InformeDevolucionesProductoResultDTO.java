package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeDevolucionesProductoResultDTO {

    private List<InformeDevolucionesProductoRowDTO> rows = new ArrayList<>();

    private Integer totalCantidadDevuelta = 0;
    private Integer totalDevoluciones = 0;
    private BigDecimal totalReembolsado = BigDecimal.ZERO;

    private String productoMasDevuelto;
    private Integer cantidadProductoMasDevuelto = 0;

    public List<InformeDevolucionesProductoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeDevolucionesProductoRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalCantidadDevuelta() {
        return totalCantidadDevuelta;
    }

    public void setTotalCantidadDevuelta(Integer totalCantidadDevuelta) {
        this.totalCantidadDevuelta = totalCantidadDevuelta;
    }

    public Integer getTotalDevoluciones() {
        return totalDevoluciones;
    }

    public void setTotalDevoluciones(Integer totalDevoluciones) {
        this.totalDevoluciones = totalDevoluciones;
    }

    public BigDecimal getTotalReembolsado() {
        return totalReembolsado;
    }

    public void setTotalReembolsado(BigDecimal totalReembolsado) {
        this.totalReembolsado = totalReembolsado;
    }

    public String getProductoMasDevuelto() {
        return productoMasDevuelto;
    }

    public void setProductoMasDevuelto(String productoMasDevuelto) {
        this.productoMasDevuelto = productoMasDevuelto;
    }

    public Integer getCantidadProductoMasDevuelto() {
        return cantidadProductoMasDevuelto;
    }

    public void setCantidadProductoMasDevuelto(Integer cantidadProductoMasDevuelto) {
        this.cantidadProductoMasDevuelto = cantidadProductoMasDevuelto;
    }
}
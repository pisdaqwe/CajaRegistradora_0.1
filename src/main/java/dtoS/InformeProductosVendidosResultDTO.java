package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeProductosVendidosResultDTO {

    private List<InformeProductosVendidosRowDTO> rows = new ArrayList<>();

    private Integer totalUnidades = 0;
    private BigDecimal totalBruto = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;

    private String productoTop;
    private Integer unidadesProductoTop = 0;

    public List<InformeProductosVendidosRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeProductosVendidosRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Integer totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getTotalDevoluciones() {
        return totalDevoluciones;
    }

    public void setTotalDevoluciones(BigDecimal totalDevoluciones) {
        this.totalDevoluciones = totalDevoluciones;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public String getProductoTop() {
        return productoTop;
    }

    public void setProductoTop(String productoTop) {
        this.productoTop = productoTop;
    }

    public Integer getUnidadesProductoTop() {
        return unidadesProductoTop;
    }

    public void setUnidadesProductoTop(Integer unidadesProductoTop) {
        this.unidadesProductoTop = unidadesProductoTop;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasProductoEmpleadoResultDTO {

    private List<InformeVentasProductoEmpleadoRowDTO> rows = new ArrayList<>();

    private Integer totalUnidades;
    private BigDecimal totalBruto;
    private BigDecimal totalDescuento;
    private BigDecimal totalNeto;

    private String empleadoTop;
    private String productoTop;
    private Integer unidadesTop;

    public List<InformeVentasProductoEmpleadoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasProductoEmpleadoRowDTO> rows) {
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

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public String getEmpleadoTop() {
        return empleadoTop;
    }

    public void setEmpleadoTop(String empleadoTop) {
        this.empleadoTop = empleadoTop;
    }

    public String getProductoTop() {
        return productoTop;
    }

    public void setProductoTop(String productoTop) {
        this.productoTop = productoTop;
    }

    public Integer getUnidadesTop() {
        return unidadesTop;
    }

    public void setUnidadesTop(Integer unidadesTop) {
        this.unidadesTop = unidadesTop;
    }
}
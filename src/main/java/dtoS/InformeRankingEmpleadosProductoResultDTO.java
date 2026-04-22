package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeRankingEmpleadosProductoResultDTO {

    private List<InformeRankingEmpleadosProductoRowDTO> rows = new ArrayList<>();

    private Integer totalUnidades;
    private BigDecimal totalImporteNeto;

    private String empleadoTop;
    private String productoTop;
    private Integer unidadesTop;

    public List<InformeRankingEmpleadosProductoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeRankingEmpleadosProductoRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Integer totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public BigDecimal getTotalImporteNeto() {
        return totalImporteNeto;
    }

    public void setTotalImporteNeto(BigDecimal totalImporteNeto) {
        this.totalImporteNeto = totalImporteNeto;
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
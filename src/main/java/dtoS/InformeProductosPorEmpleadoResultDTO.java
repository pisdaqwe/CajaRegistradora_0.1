package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeProductosPorEmpleadoResultDTO {

    private List<InformeProductosPorEmpleadoRowDTO> rows = new ArrayList<>();

    private Integer totalUnidades = 0;
    private BigDecimal totalImporte = BigDecimal.ZERO;

    private String empleadoTop;
    private String productoTop;
    private Integer unidadesTop = 0;

    public List<InformeProductosPorEmpleadoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeProductosPorEmpleadoRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalUnidades() {
        return totalUnidades;
    }

    public void setTotalUnidades(Integer totalUnidades) {
        this.totalUnidades = totalUnidades;
    }

    public BigDecimal getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(BigDecimal totalImporte) {
        this.totalImporte = totalImporte;
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
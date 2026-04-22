package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasExtraEmpleadoResultDTO {

    private List<InformeVentasExtraEmpleadoRowDTO> rows = new ArrayList<>();

    private Integer totalVeces;
    private BigDecimal totalImporte;

    private String empleadoTop;
    private String extraTop;
    private Integer vecesTop;

    public List<InformeVentasExtraEmpleadoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasExtraEmpleadoRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalVeces() {
        return totalVeces;
    }

    public void setTotalVeces(Integer totalVeces) {
        this.totalVeces = totalVeces;
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

    public String getExtraTop() {
        return extraTop;
    }

    public void setExtraTop(String extraTop) {
        this.extraTop = extraTop;
    }

    public Integer getVecesTop() {
        return vecesTop;
    }

    public void setVecesTop(Integer vecesTop) {
        this.vecesTop = vecesTop;
    }
}
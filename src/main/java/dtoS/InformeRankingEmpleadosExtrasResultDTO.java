package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeRankingEmpleadosExtrasResultDTO {

    private List<InformeRankingEmpleadosExtrasRowDTO> rows = new ArrayList<>();

    private Integer totalExtrasVendidos = 0;
    private BigDecimal totalImporteExtras = BigDecimal.ZERO;

    private String mejorEmpleado;
    private Integer extrasMejorEmpleado = 0;

    public List<InformeRankingEmpleadosExtrasRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeRankingEmpleadosExtrasRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalExtrasVendidos() {
        return totalExtrasVendidos;
    }

    public void setTotalExtrasVendidos(Integer totalExtrasVendidos) {
        this.totalExtrasVendidos = totalExtrasVendidos;
    }

    public BigDecimal getTotalImporteExtras() {
        return totalImporteExtras;
    }

    public void setTotalImporteExtras(BigDecimal totalImporteExtras) {
        this.totalImporteExtras = totalImporteExtras;
    }

    public String getMejorEmpleado() {
        return mejorEmpleado;
    }

    public void setMejorEmpleado(String mejorEmpleado) {
        this.mejorEmpleado = mejorEmpleado;
    }

    public Integer getExtrasMejorEmpleado() {
        return extrasMejorEmpleado;
    }

    public void setExtrasMejorEmpleado(Integer extrasMejorEmpleado) {
        this.extrasMejorEmpleado = extrasMejorEmpleado;
    }
}
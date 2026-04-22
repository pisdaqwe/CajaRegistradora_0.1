package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeCombosVendidosResultDTO {

    private List<InformeCombosVendidosRowDTO> rows = new ArrayList<>();

    private Integer totalCombos = 0;
    private BigDecimal totalPrecioOriginal = BigDecimal.ZERO;
    private BigDecimal totalPrecioFinal = BigDecimal.ZERO;
    private BigDecimal totalAhorro = BigDecimal.ZERO;

    private String comboTop;
    private Integer vecesComboTop = 0;

    public List<InformeCombosVendidosRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeCombosVendidosRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalCombos() {
        return totalCombos;
    }

    public void setTotalCombos(Integer totalCombos) {
        this.totalCombos = totalCombos;
    }

    public BigDecimal getTotalPrecioOriginal() {
        return totalPrecioOriginal;
    }

    public void setTotalPrecioOriginal(BigDecimal totalPrecioOriginal) {
        this.totalPrecioOriginal = totalPrecioOriginal;
    }

    public BigDecimal getTotalPrecioFinal() {
        return totalPrecioFinal;
    }

    public void setTotalPrecioFinal(BigDecimal totalPrecioFinal) {
        this.totalPrecioFinal = totalPrecioFinal;
    }

    public BigDecimal getTotalAhorro() {
        return totalAhorro;
    }

    public void setTotalAhorro(BigDecimal totalAhorro) {
        this.totalAhorro = totalAhorro;
    }

    public String getComboTop() {
        return comboTop;
    }

    public void setComboTop(String comboTop) {
        this.comboTop = comboTop;
    }

    public Integer getVecesComboTop() {
        return vecesComboTop;
    }

    public void setVecesComboTop(Integer vecesComboTop) {
        this.vecesComboTop = vecesComboTop;
    }
}
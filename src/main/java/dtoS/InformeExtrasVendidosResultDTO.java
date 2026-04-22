package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeExtrasVendidosResultDTO {

    private List<InformeExtrasVendidosRowDTO> rows = new ArrayList<>();

    private Integer totalVeces = 0;
    private BigDecimal totalImporte = BigDecimal.ZERO;

    private String extraTop;
    private Integer vecesExtraTop = 0;

    public List<InformeExtrasVendidosRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeExtrasVendidosRowDTO> rows) {
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

    public String getExtraTop() {
        return extraTop;
    }

    public void setExtraTop(String extraTop) {
        this.extraTop = extraTop;
    }

    public Integer getVecesExtraTop() {
        return vecesExtraTop;
    }

    public void setVecesExtraTop(Integer vecesExtraTop) {
        this.vecesExtraTop = vecesExtraTop;
    }
}

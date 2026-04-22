package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformePagosMetodoResultDTO {

    private List<InformePagosMetodoRowDTO> rows = new ArrayList<>();

    private BigDecimal totalImporte = BigDecimal.ZERO;
    private Integer totalOperaciones = 0;

    private String metodoPrincipal;
    private BigDecimal importeMetodoPrincipal = BigDecimal.ZERO;

    public List<InformePagosMetodoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformePagosMetodoRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(BigDecimal totalImporte) {
        this.totalImporte = totalImporte;
    }

    public Integer getTotalOperaciones() {
        return totalOperaciones;
    }

    public void setTotalOperaciones(Integer totalOperaciones) {
        this.totalOperaciones = totalOperaciones;
    }

    public String getMetodoPrincipal() {
        return metodoPrincipal;
    }

    public void setMetodoPrincipal(String metodoPrincipal) {
        this.metodoPrincipal = metodoPrincipal;
    }

    public BigDecimal getImporteMetodoPrincipal() {
        return importeMetodoPrincipal;
    }

    public void setImporteMetodoPrincipal(BigDecimal importeMetodoPrincipal) {
        this.importeMetodoPrincipal = importeMetodoPrincipal;
    }
}
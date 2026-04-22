package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeMovimientoStockResultDTO {

    private List<InformeMovimientoStockRowDTO> rows = new ArrayList<>();

    private Integer totalMovimientos = 0;
    private BigDecimal totalCantidad = BigDecimal.ZERO;

    private Integer totalEntradas = 0;
    private Integer totalSalidas = 0;
    private Integer totalAjustes = 0;
    private Integer totalMermas = 0;

    public List<InformeMovimientoStockRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeMovimientoStockRowDTO> rows) {
        this.rows = rows;
    }

    public Integer getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(Integer totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

    public BigDecimal getTotalCantidad() {
        return totalCantidad;
    }

    public void setTotalCantidad(BigDecimal totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    public Integer getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(Integer totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public Integer getTotalSalidas() {
        return totalSalidas;
    }

    public void setTotalSalidas(Integer totalSalidas) {
        this.totalSalidas = totalSalidas;
    }

    public Integer getTotalAjustes() {
        return totalAjustes;
    }

    public void setTotalAjustes(Integer totalAjustes) {
        this.totalAjustes = totalAjustes;
    }

    public Integer getTotalMermas() {
        return totalMermas;
    }

    public void setTotalMermas(Integer totalMermas) {
        this.totalMermas = totalMermas;
    }
}
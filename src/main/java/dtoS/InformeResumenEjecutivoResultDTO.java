package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeResumenEjecutivoResultDTO {

    private List<InformeResumenEjecutivoRowDTO> rows = new ArrayList<>();

    private BigDecimal ventasBrutas = BigDecimal.ZERO;
    private BigDecimal devoluciones = BigDecimal.ZERO;
    private BigDecimal neto = BigDecimal.ZERO;
    private BigDecimal ticketMedio = BigDecimal.ZERO;
    private Integer totalCombos = 0;
    private BigDecimal ahorroTotal = BigDecimal.ZERO;

    public List<InformeResumenEjecutivoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeResumenEjecutivoRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getVentasBrutas() {
        return ventasBrutas;
    }

    public void setVentasBrutas(BigDecimal ventasBrutas) {
        this.ventasBrutas = ventasBrutas;
    }

    public BigDecimal getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(BigDecimal devoluciones) {
        this.devoluciones = devoluciones;
    }

    public BigDecimal getNeto() {
        return neto;
    }

    public void setNeto(BigDecimal neto) {
        this.neto = neto;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }

    public Integer getTotalCombos() {
        return totalCombos;
    }

    public void setTotalCombos(Integer totalCombos) {
        this.totalCombos = totalCombos;
    }

    public BigDecimal getAhorroTotal() {
        return ahorroTotal;
    }

    public void setAhorroTotal(BigDecimal ahorroTotal) {
        this.ahorroTotal = ahorroTotal;
    }
}
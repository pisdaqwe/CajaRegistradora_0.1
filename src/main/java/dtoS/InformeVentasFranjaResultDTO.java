package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasFranjaResultDTO {

    private List<InformeVentasFranjaRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;

    private Integer totalTickets = 0;
    private BigDecimal ticketMedioGlobal = BigDecimal.ZERO;

    private String mejorFranja;
    private BigDecimal importeMejorFranja = BigDecimal.ZERO;

    public List<InformeVentasFranjaRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasFranjaRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalDevoluciones() {
        return totalDevoluciones;
    }

    public void setTotalDevoluciones(BigDecimal totalDevoluciones) {
        this.totalDevoluciones = totalDevoluciones;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getTicketMedioGlobal() {
        return ticketMedioGlobal;
    }

    public void setTicketMedioGlobal(BigDecimal ticketMedioGlobal) {
        this.ticketMedioGlobal = ticketMedioGlobal;
    }

    public String getMejorFranja() {
        return mejorFranja;
    }

    public void setMejorFranja(String mejorFranja) {
        this.mejorFranja = mejorFranja;
    }

    public BigDecimal getImporteMejorFranja() {
        return importeMejorFranja;
    }

    public void setImporteMejorFranja(BigDecimal importeMejorFranja) {
        this.importeMejorFranja = importeMejorFranja;
    }
}
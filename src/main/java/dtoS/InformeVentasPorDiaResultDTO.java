package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasPorDiaResultDTO {

    private List<InformeVentasPorDiaRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;

    private Integer totalTickets = 0;
    private BigDecimal ticketMedioGlobal = BigDecimal.ZERO;

    private LocalDate fechaMejorDia;
    private BigDecimal importeMejorDia = BigDecimal.ZERO;

    public List<InformeVentasPorDiaRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasPorDiaRowDTO> rows) {
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

    public LocalDate getFechaMejorDia() {
        return fechaMejorDia;
    }

    public void setFechaMejorDia(LocalDate fechaMejorDia) {
        this.fechaMejorDia = fechaMejorDia;
    }

    public BigDecimal getImporteMejorDia() {
        return importeMejorDia;
    }

    public void setImporteMejorDia(BigDecimal importeMejorDia) {
        this.importeMejorDia = importeMejorDia;
    }
}

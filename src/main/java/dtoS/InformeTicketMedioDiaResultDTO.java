package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InformeTicketMedioDiaResultDTO {

    private List<InformeTicketMedioDiaRowDTO> rows = new ArrayList<>();

    private BigDecimal ticketMedioGlobal = BigDecimal.ZERO;
    private Integer totalTickets = 0;
    private BigDecimal totalVentas = BigDecimal.ZERO;

    private LocalDate mejorDia;
    private BigDecimal mejorTicketMedio = BigDecimal.ZERO;

    public List<InformeTicketMedioDiaRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeTicketMedioDiaRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTicketMedioGlobal() {
        return ticketMedioGlobal;
    }

    public void setTicketMedioGlobal(BigDecimal ticketMedioGlobal) {
        this.ticketMedioGlobal = ticketMedioGlobal;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public LocalDate getMejorDia() {
        return mejorDia;
    }

    public void setMejorDia(LocalDate mejorDia) {
        this.mejorDia = mejorDia;
    }

    public BigDecimal getMejorTicketMedio() {
        return mejorTicketMedio;
    }

    public void setMejorTicketMedio(BigDecimal mejorTicketMedio) {
        this.mejorTicketMedio = mejorTicketMedio;
    }
}
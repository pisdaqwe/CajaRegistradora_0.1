package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeRankingEmpleadosVentasResultDTO {

    private List<InformeRankingEmpleadosVentasRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private Integer totalTickets = 0;
    private BigDecimal ticketMedioGlobal = BigDecimal.ZERO;

    private String mejorEmpleado;
    private BigDecimal ventasMejorEmpleado = BigDecimal.ZERO;

    public List<InformeRankingEmpleadosVentasRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeRankingEmpleadosVentasRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
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

    public String getMejorEmpleado() {
        return mejorEmpleado;
    }

    public void setMejorEmpleado(String mejorEmpleado) {
        this.mejorEmpleado = mejorEmpleado;
    }

    public BigDecimal getVentasMejorEmpleado() {
        return ventasMejorEmpleado;
    }

    public void setVentasMejorEmpleado(BigDecimal ventasMejorEmpleado) {
        this.ventasMejorEmpleado = ventasMejorEmpleado;
    }
}
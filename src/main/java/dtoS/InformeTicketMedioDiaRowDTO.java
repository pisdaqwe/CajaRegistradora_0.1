package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InformeTicketMedioDiaRowDTO {

    private LocalDate fecha;

    private Integer idEmpleado;
    private String nombreEmpleado;

    private Integer numeroTickets = 0;
    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal ticketMedio = BigDecimal.ZERO;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public Integer getNumeroTickets() {
        return numeroTickets;
    }

    public void setNumeroTickets(Integer numeroTickets) {
        this.numeroTickets = numeroTickets;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }
}
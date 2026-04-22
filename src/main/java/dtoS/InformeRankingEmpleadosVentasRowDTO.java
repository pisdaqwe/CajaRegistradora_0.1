package dtoS;

import java.math.BigDecimal;

public class InformeRankingEmpleadosVentasRowDTO {

    private Integer posicion;
    private Integer idEmpleado;
    private String nombreEmpleado;

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private Integer numeroTickets = 0;
    private BigDecimal ticketMedio = BigDecimal.ZERO;

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
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

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Integer getNumeroTickets() {
        return numeroTickets;
    }

    public void setNumeroTickets(Integer numeroTickets) {
        this.numeroTickets = numeroTickets;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public void setTicketMedio(BigDecimal ticketMedio) {
        this.ticketMedio = ticketMedio;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasCajaResultDTO {

    private List<InformeVentasCajaRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;
    private Integer totalTickets = 0;

    private String mejorCaja;
    private BigDecimal netoMejorCaja = BigDecimal.ZERO;

    public List<InformeVentasCajaRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasCajaRowDTO> rows) {
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

    public String getMejorCaja() {
        return mejorCaja;
    }

    public void setMejorCaja(String mejorCaja) {
        this.mejorCaja = mejorCaja;
    }

    public BigDecimal getNetoMejorCaja() {
        return netoMejorCaja;
    }

    public void setNetoMejorCaja(BigDecimal netoMejorCaja) {
        this.netoMejorCaja = netoMejorCaja;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InformeNetoVsDevolucionesRowDTO {

    private LocalDate fecha;
    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;
    private BigDecimal ratioDevolucion = BigDecimal.ZERO;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    public BigDecimal getRatioDevolucion() {
        return ratioDevolucion;
    }

    public void setRatioDevolucion(BigDecimal ratioDevolucion) {
        this.ratioDevolucion = ratioDevolucion;
    }
}
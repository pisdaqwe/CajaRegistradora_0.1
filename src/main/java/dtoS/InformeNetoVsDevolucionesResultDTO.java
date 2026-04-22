package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InformeNetoVsDevolucionesResultDTO {

    private List<InformeNetoVsDevolucionesRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;
    private BigDecimal ratioGlobalDevolucion = BigDecimal.ZERO;

    private LocalDate peorDiaDevoluciones;
    private BigDecimal importePeorDiaDevoluciones = BigDecimal.ZERO;

    public List<InformeNetoVsDevolucionesRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeNetoVsDevolucionesRowDTO> rows) {
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

    public BigDecimal getRatioGlobalDevolucion() {
        return ratioGlobalDevolucion;
    }

    public void setRatioGlobalDevolucion(BigDecimal ratioGlobalDevolucion) {
        this.ratioGlobalDevolucion = ratioGlobalDevolucion;
    }

    public LocalDate getPeorDiaDevoluciones() {
        return peorDiaDevoluciones;
    }

    public void setPeorDiaDevoluciones(LocalDate peorDiaDevoluciones) {
        this.peorDiaDevoluciones = peorDiaDevoluciones;
    }

    public BigDecimal getImportePeorDiaDevoluciones() {
        return importePeorDiaDevoluciones;
    }

    public void setImportePeorDiaDevoluciones(BigDecimal importePeorDiaDevoluciones) {
        this.importePeorDiaDevoluciones = importePeorDiaDevoluciones;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InformeMermaPeriodoResultDTO {

    private List<InformeMermaPeriodoRowDTO> rows = new ArrayList<>();

    private BigDecimal totalCantidad = BigDecimal.ZERO;
    private Integer totalRegistros = 0;

    private LocalDate peorDia;
    private BigDecimal cantidadPeorDia = BigDecimal.ZERO;

    public List<InformeMermaPeriodoRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeMermaPeriodoRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTotalCantidad() {
        return totalCantidad;
    }

    public void setTotalCantidad(BigDecimal totalCantidad) {
        this.totalCantidad = totalCantidad;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public LocalDate getPeorDia() {
        return peorDia;
    }

    public void setPeorDia(LocalDate peorDia) {
        this.peorDia = peorDia;
    }

    public BigDecimal getCantidadPeorDia() {
        return cantidadPeorDia;
    }

    public void setCantidadPeorDia(BigDecimal cantidadPeorDia) {
        this.cantidadPeorDia = cantidadPeorDia;
    }
}
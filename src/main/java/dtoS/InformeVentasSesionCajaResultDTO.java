package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeVentasSesionCajaResultDTO {

    private List<InformeVentasSesionCajaRowDTO> rows = new ArrayList<>();

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;

    private Integer totalSesiones = 0;

    private Integer idMejorSesion;
    private BigDecimal netoMejorSesion = BigDecimal.ZERO;

    public List<InformeVentasSesionCajaRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeVentasSesionCajaRowDTO> rows) {
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

    public Integer getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(Integer totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public Integer getIdMejorSesion() {
        return idMejorSesion;
    }

    public void setIdMejorSesion(Integer idMejorSesion) {
        this.idMejorSesion = idMejorSesion;
    }

    public BigDecimal getNetoMejorSesion() {
        return netoMejorSesion;
    }

    public void setNetoMejorSesion(BigDecimal netoMejorSesion) {
        this.netoMejorSesion = netoMejorSesion;
    }
}
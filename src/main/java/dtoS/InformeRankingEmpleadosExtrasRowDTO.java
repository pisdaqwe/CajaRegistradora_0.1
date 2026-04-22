package dtoS;

import java.math.BigDecimal;

public class InformeRankingEmpleadosExtrasRowDTO {

    private Integer posicion;
    private Integer idEmpleado;
    private String nombreEmpleado;

    private Integer totalExtrasVendidos = 0;
    private BigDecimal importeExtras = BigDecimal.ZERO;

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

    public Integer getTotalExtrasVendidos() {
        return totalExtrasVendidos;
    }

    public void setTotalExtrasVendidos(Integer totalExtrasVendidos) {
        this.totalExtrasVendidos = totalExtrasVendidos;
    }

    public BigDecimal getImporteExtras() {
        return importeExtras;
    }

    public void setImporteExtras(BigDecimal importeExtras) {
        this.importeExtras = importeExtras;
    }
}
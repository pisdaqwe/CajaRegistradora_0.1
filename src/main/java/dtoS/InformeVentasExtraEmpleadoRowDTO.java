package dtoS;

import java.math.BigDecimal;

public class InformeVentasExtraEmpleadoRowDTO {

    private Integer idEmpleado;
    private String nombreEmpleado;

    private Integer idExtra;
    private String nombreExtra;
    private String tipoExtra;

    private Integer vecesVendido;
    private BigDecimal importeGenerado;

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

    public Integer getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(Integer idExtra) {
        this.idExtra = idExtra;
    }

    public String getNombreExtra() {
        return nombreExtra;
    }

    public void setNombreExtra(String nombreExtra) {
        this.nombreExtra = nombreExtra;
    }

    public String getTipoExtra() {
        return tipoExtra;
    }

    public void setTipoExtra(String tipoExtra) {
        this.tipoExtra = tipoExtra;
    }

    public Integer getVecesVendido() {
        return vecesVendido;
    }

    public void setVecesVendido(Integer vecesVendido) {
        this.vecesVendido = vecesVendido;
    }

    public BigDecimal getImporteGenerado() {
        return importeGenerado;
    }

    public void setImporteGenerado(BigDecimal importeGenerado) {
        this.importeGenerado = importeGenerado;
    }
}
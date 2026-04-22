package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InformeVentasSesionCajaRowDTO {

    private Integer idSesion;
    private Integer idCaja;
    private String nombreCaja;

    private Integer idEmpleadoApertura;
    private String nombreEmpleadoApertura;

    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;

    private BigDecimal totalVentas = BigDecimal.ZERO;
    private BigDecimal totalDevoluciones = BigDecimal.ZERO;
    private BigDecimal totalNeto = BigDecimal.ZERO;

    public Integer getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(Integer idSesion) {
        this.idSesion = idSesion;
    }

    public Integer getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public Integer getIdEmpleadoApertura() {
        return idEmpleadoApertura;
    }

    public void setIdEmpleadoApertura(Integer idEmpleadoApertura) {
        this.idEmpleadoApertura = idEmpleadoApertura;
    }

    public String getNombreEmpleadoApertura() {
        return nombreEmpleadoApertura;
    }

    public void setNombreEmpleadoApertura(String nombreEmpleadoApertura) {
        this.nombreEmpleadoApertura = nombreEmpleadoApertura;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
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
}
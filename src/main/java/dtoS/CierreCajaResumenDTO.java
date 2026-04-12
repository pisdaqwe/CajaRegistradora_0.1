package dtoS;

import java.math.BigDecimal;

public class CierreCajaResumenDTO {

    private int idSesion;
    private BigDecimal importeInicial = BigDecimal.ZERO;
    private BigDecimal ventasEfectivo = BigDecimal.ZERO;
    private BigDecimal ventasTarjeta = BigDecimal.ZERO;
    private BigDecimal devolucionesEfectivo = BigDecimal.ZERO;
    private BigDecimal devolucionesTarjeta = BigDecimal.ZERO;

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public BigDecimal getImporteInicial() {
        return safe(importeInicial);
    }

    public void setImporteInicial(BigDecimal importeInicial) {
        this.importeInicial = safe(importeInicial);
    }

    public BigDecimal getVentasEfectivo() {
        return safe(ventasEfectivo);
    }

    public void setVentasEfectivo(BigDecimal ventasEfectivo) {
        this.ventasEfectivo = safe(ventasEfectivo);
    }

    public BigDecimal getVentasTarjeta() {
        return safe(ventasTarjeta);
    }

    public void setVentasTarjeta(BigDecimal ventasTarjeta) {
        this.ventasTarjeta = safe(ventasTarjeta);
    }

    public BigDecimal getDevolucionesEfectivo() {
        return safe(devolucionesEfectivo);
    }

    public void setDevolucionesEfectivo(BigDecimal devolucionesEfectivo) {
        this.devolucionesEfectivo = safe(devolucionesEfectivo);
    }

    public BigDecimal getDevolucionesTarjeta() {
        return safe(devolucionesTarjeta);
    }

    public void setDevolucionesTarjeta(BigDecimal devolucionesTarjeta) {
        this.devolucionesTarjeta = safe(devolucionesTarjeta);
    }

    public BigDecimal getEfectivoEsperado() {
        return getImporteInicial()
                .add(getVentasEfectivo())
                .subtract(getDevolucionesEfectivo());
    }

    public BigDecimal getTarjetaNeta() {
        return getVentasTarjeta().subtract(getDevolucionesTarjeta());
    }

    public BigDecimal getTotalVentas() {
        return getVentasEfectivo().add(getVentasTarjeta());
    }

    public BigDecimal getTotalDevoluciones() {
        return getDevolucionesEfectivo().add(getDevolucionesTarjeta());
    }

    public BigDecimal getTotalNeto() {
        return getTotalVentas().subtract(getTotalDevoluciones());
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

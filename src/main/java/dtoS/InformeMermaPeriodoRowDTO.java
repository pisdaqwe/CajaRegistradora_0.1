package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InformeMermaPeriodoRowDTO {

    private LocalDate fecha;
    private String tipoMerma;
    private String origen;
    private String motivo;

    private BigDecimal cantidad = BigDecimal.ZERO;
    private String observaciones;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoMerma() {
        return tipoMerma;
    }

    public void setTipoMerma(String tipoMerma) {
        this.tipoMerma = tipoMerma;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
package dtoS;

import java.math.BigDecimal;

public class InformeTiemposEstacionRowDTO {

    private Integer idEstacion;
    private String nombreEstacion;

    private BigDecimal tiempoMedioSegundos = BigDecimal.ZERO;
    private BigDecimal tiempoMaximoSegundos = BigDecimal.ZERO;
    private Integer itemsProcesados = 0;

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getNombreEstacion() {
        return nombreEstacion;
    }

    public void setNombreEstacion(String nombreEstacion) {
        this.nombreEstacion = nombreEstacion;
    }

    public BigDecimal getTiempoMedioSegundos() {
        return tiempoMedioSegundos;
    }

    public void setTiempoMedioSegundos(BigDecimal tiempoMedioSegundos) {
        this.tiempoMedioSegundos = tiempoMedioSegundos;
    }

    public BigDecimal getTiempoMaximoSegundos() {
        return tiempoMaximoSegundos;
    }

    public void setTiempoMaximoSegundos(BigDecimal tiempoMaximoSegundos) {
        this.tiempoMaximoSegundos = tiempoMaximoSegundos;
    }

    public Integer getItemsProcesados() {
        return itemsProcesados;
    }

    public void setItemsProcesados(Integer itemsProcesados) {
        this.itemsProcesados = itemsProcesados;
    }
}
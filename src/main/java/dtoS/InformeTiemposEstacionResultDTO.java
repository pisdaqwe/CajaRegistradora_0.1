package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InformeTiemposEstacionResultDTO {

    private List<InformeTiemposEstacionRowDTO> rows = new ArrayList<>();

    private BigDecimal tiempoMedioGlobalSegundos = BigDecimal.ZERO;
    private Integer totalItemsProcesados = 0;

    private String estacionMasLenta;
    private BigDecimal tiempoEstacionMasLenta = BigDecimal.ZERO;

    public List<InformeTiemposEstacionRowDTO> getRows() {
        return rows;
    }

    public void setRows(List<InformeTiemposEstacionRowDTO> rows) {
        this.rows = rows;
    }

    public BigDecimal getTiempoMedioGlobalSegundos() {
        return tiempoMedioGlobalSegundos;
    }

    public void setTiempoMedioGlobalSegundos(BigDecimal tiempoMedioGlobalSegundos) {
        this.tiempoMedioGlobalSegundos = tiempoMedioGlobalSegundos;
    }

    public Integer getTotalItemsProcesados() {
        return totalItemsProcesados;
    }

    public void setTotalItemsProcesados(Integer totalItemsProcesados) {
        this.totalItemsProcesados = totalItemsProcesados;
    }

    public String getEstacionMasLenta() {
        return estacionMasLenta;
    }

    public void setEstacionMasLenta(String estacionMasLenta) {
        this.estacionMasLenta = estacionMasLenta;
    }

    public BigDecimal getTiempoEstacionMasLenta() {
        return tiempoEstacionMasLenta;
    }

    public void setTiempoEstacionMasLenta(BigDecimal tiempoEstacionMasLenta) {
        this.tiempoEstacionMasLenta = tiempoEstacionMasLenta;
    }
}
package dtoS;

import java.math.BigDecimal;

public class InformeResumenEjecutivoRowDTO {

    private String indicador;
    private BigDecimal valor = BigDecimal.ZERO;
    private String descripcion;

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
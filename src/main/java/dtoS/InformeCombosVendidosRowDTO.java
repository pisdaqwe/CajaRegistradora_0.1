package dtoS;

import java.math.BigDecimal;

public class InformeCombosVendidosRowDTO {

    private Integer idCombo;
    private String nombreCombo;

    private Integer vecesVendido = 0;
    private BigDecimal precioOriginalTotal = BigDecimal.ZERO;
    private BigDecimal precioFinalTotal = BigDecimal.ZERO;
    private BigDecimal ahorroTotal = BigDecimal.ZERO;

    public Integer getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Integer idCombo) {
        this.idCombo = idCombo;
    }

    public String getNombreCombo() {
        return nombreCombo;
    }

    public void setNombreCombo(String nombreCombo) {
        this.nombreCombo = nombreCombo;
    }

    public Integer getVecesVendido() {
        return vecesVendido;
    }

    public void setVecesVendido(Integer vecesVendido) {
        this.vecesVendido = vecesVendido;
    }

    public BigDecimal getPrecioOriginalTotal() {
        return precioOriginalTotal;
    }

    public void setPrecioOriginalTotal(BigDecimal precioOriginalTotal) {
        this.precioOriginalTotal = precioOriginalTotal;
    }

    public BigDecimal getPrecioFinalTotal() {
        return precioFinalTotal;
    }

    public void setPrecioFinalTotal(BigDecimal precioFinalTotal) {
        this.precioFinalTotal = precioFinalTotal;
    }

    public BigDecimal getAhorroTotal() {
        return ahorroTotal;
    }

    public void setAhorroTotal(BigDecimal ahorroTotal) {
        this.ahorroTotal = ahorroTotal;
    }
}
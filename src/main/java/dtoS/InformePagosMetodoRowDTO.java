package dtoS;

import java.math.BigDecimal;

public class InformePagosMetodoRowDTO {

    private String metodoPago;
    private Integer numeroOperaciones = 0;
    private BigDecimal importeTotal = BigDecimal.ZERO;
    private BigDecimal porcentajeSobreTotal = BigDecimal.ZERO;

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getNumeroOperaciones() {
        return numeroOperaciones;
    }

    public void setNumeroOperaciones(Integer numeroOperaciones) {
        this.numeroOperaciones = numeroOperaciones;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public BigDecimal getPorcentajeSobreTotal() {
        return porcentajeSobreTotal;
    }

    public void setPorcentajeSobreTotal(BigDecimal porcentajeSobreTotal) {
        this.porcentajeSobreTotal = porcentajeSobreTotal;
    }
}
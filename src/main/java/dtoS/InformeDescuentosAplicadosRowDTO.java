package dtoS;

import java.math.BigDecimal;

public class InformeDescuentosAplicadosRowDTO {

    private Integer idDescuento;
    private String nombreDescuento;

    private Integer numeroUsos = 0;
    private BigDecimal importeBase = BigDecimal.ZERO;
    private BigDecimal importeDescuento = BigDecimal.ZERO;
    private String tipoBeneficio;

    public Integer getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(Integer idDescuento) {
        this.idDescuento = idDescuento;
    }

    public String getNombreDescuento() {
        return nombreDescuento;
    }

    public void setNombreDescuento(String nombreDescuento) {
        this.nombreDescuento = nombreDescuento;
    }

    public Integer getNumeroUsos() {
        return numeroUsos;
    }

    public void setNumeroUsos(Integer numeroUsos) {
        this.numeroUsos = numeroUsos;
    }

    public BigDecimal getImporteBase() {
        return importeBase;
    }

    public void setImporteBase(BigDecimal importeBase) {
        this.importeBase = importeBase;
    }

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public String getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(String tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }
}
package dtoS;

import java.math.BigDecimal;

public class InformeExtrasVendidosRowDTO {

    private Integer idExtra;
    private String nombreExtra;
    private String grupoPrincipal;

    private Integer vecesVendido = 0;
    private BigDecimal importeGenerado = BigDecimal.ZERO;

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

    public String getGrupoPrincipal() {
        return grupoPrincipal;
    }

    public void setGrupoPrincipal(String grupoPrincipal) {
        this.grupoPrincipal = grupoPrincipal;
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
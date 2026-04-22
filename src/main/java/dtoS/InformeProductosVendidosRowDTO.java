package dtoS;

import java.math.BigDecimal;

public class InformeProductosVendidosRowDTO {

    private Integer idProducto;
    private String nombreProducto;

    private Integer unidadesVendidas = 0;
    private BigDecimal importeBruto = BigDecimal.ZERO;
    private BigDecimal importeDevoluciones = BigDecimal.ZERO;
    private BigDecimal importeNeto = BigDecimal.ZERO;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getUnidadesVendidas() {
        return unidadesVendidas;
    }

    public void setUnidadesVendidas(Integer unidadesVendidas) {
        this.unidadesVendidas = unidadesVendidas;
    }

    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public BigDecimal getImporteDevoluciones() {
        return importeDevoluciones;
    }

    public void setImporteDevoluciones(BigDecimal importeDevoluciones) {
        this.importeDevoluciones = importeDevoluciones;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }
}
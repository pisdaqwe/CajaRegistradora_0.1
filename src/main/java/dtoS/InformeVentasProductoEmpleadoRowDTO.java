package dtoS;

import java.math.BigDecimal;

public class InformeVentasProductoEmpleadoRowDTO {

    private Integer idEmpleado;
    private String nombreEmpleado;

    private Integer idProducto;
    private String nombreProducto;

    private Integer unidadesVendidas;
    private BigDecimal importeBruto;
    private BigDecimal importeDescuento;
    private BigDecimal importeNeto;

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

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

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }
}
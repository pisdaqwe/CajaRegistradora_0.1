package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.ComboTipo;



public class VentaComboDTO {

    private int idVentaCombo;
    private int idVenta;
    private int idCombo;
    private String nombreCombo;
    private ComboTipo tipoCombo;
    private BigDecimal valorCombo;
    private BigDecimal precioOriginal;
    private BigDecimal precioFinal;
    private BigDecimal ahorroTotal;
    private LocalDateTime fechaAplicacion;

    public VentaComboDTO() {
        this.valorCombo = BigDecimal.ZERO;
        this.precioOriginal = BigDecimal.ZERO;
        this.precioFinal = BigDecimal.ZERO;
        this.ahorroTotal = BigDecimal.ZERO;
    }

    public int getIdVentaCombo() {
        return idVentaCombo;
    }

    public void setIdVentaCombo(int idVentaCombo) {
        this.idVentaCombo = idVentaCombo;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public String getNombreCombo() {
        return nombreCombo;
    }

    public void setNombreCombo(String nombreCombo) {
        this.nombreCombo = nombreCombo == null ? "" : nombreCombo.trim();
    }

    public ComboTipo getTipoCombo() {
        return tipoCombo;
    }

    public void setTipoCombo(ComboTipo tipoCombo) {
        this.tipoCombo = tipoCombo;
    }

    public BigDecimal getValorCombo() {
        return valorCombo;
    }

    public void setValorCombo(BigDecimal valorCombo) {
        this.valorCombo = valorCombo == null ? BigDecimal.ZERO : valorCombo;
    }

    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(BigDecimal precioOriginal) {
        this.precioOriginal = precioOriginal == null ? BigDecimal.ZERO : precioOriginal;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal == null ? BigDecimal.ZERO : precioFinal;
    }

    public BigDecimal getAhorroTotal() {
        return ahorroTotal;
    }

    public void setAhorroTotal(BigDecimal ahorroTotal) {
        this.ahorroTotal = ahorroTotal == null ? BigDecimal.ZERO : ahorroTotal;
    }

    public LocalDateTime getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDateTime fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    @Override
    public String toString() {
        return "VentaComboDTO{" +
                "idVentaCombo=" + idVentaCombo +
                ", idVenta=" + idVenta +
                ", idCombo=" + idCombo +
                ", nombreCombo='" + nombreCombo + '\'' +
                ", tipoCombo=" + tipoCombo +
                ", valorCombo=" + valorCombo +
                ", precioOriginal=" + precioOriginal +
                ", precioFinal=" + precioFinal +
                ", ahorroTotal=" + ahorroTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VentaComboDTO other)) return false;
        return idVentaCombo == other.idVentaCombo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVentaCombo);
    }
}
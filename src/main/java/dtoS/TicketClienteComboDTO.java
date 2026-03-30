package dtoS;

import java.math.BigDecimal;

/**
 * DTO de un combo ya guardado en ticket_json y listo para mostrar
 * en la vista previa / reimpresión del ticket cliente.
 */
public class TicketClienteComboDTO {

    private int idCombo;
    private String nombreCombo;
    private String tipoCombo;

    private BigDecimal valorCombo = BigDecimal.ZERO;
    private BigDecimal precioOriginal = BigDecimal.ZERO;
    private BigDecimal precioFinal = BigDecimal.ZERO;
    private BigDecimal ahorroTotal = BigDecimal.ZERO;

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
        this.nombreCombo = nombreCombo;
    }

    public String getTipoCombo() {
        return tipoCombo;
    }

    public void setTipoCombo(String tipoCombo) {
        this.tipoCombo = tipoCombo;
    }

    public BigDecimal getValorCombo() {
        return valorCombo;
    }

    public void setValorCombo(BigDecimal valorCombo) {
        this.valorCombo = valorCombo != null ? valorCombo : BigDecimal.ZERO;
    }

    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(BigDecimal precioOriginal) {
        this.precioOriginal = precioOriginal != null ? precioOriginal : BigDecimal.ZERO;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal != null ? precioFinal : BigDecimal.ZERO;
    }

    public BigDecimal getAhorroTotal() {
        return ahorroTotal;
    }

    public void setAhorroTotal(BigDecimal ahorroTotal) {
        this.ahorroTotal = ahorroTotal != null ? ahorroTotal : BigDecimal.ZERO;
    }
}
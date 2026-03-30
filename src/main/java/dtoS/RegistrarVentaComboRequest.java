package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import enums.ComboTipo;

/**
 * DTO que representa un combo aplicado a una venta en el momento del cobro.
 *
 * Este objeto viajará desde la UI hasta la capa DAO para persistirse en:
 * - venta_combo
 * - venta_combo_item
 */
public class RegistrarVentaComboRequest {

    /**
     * ID del combo maestro en la tabla combo.
     */
    private int idCombo;

    /**
     * Nombre visible del combo aplicado.
     */
    private String nombreCombo;

    /**
     * Tipo del combo.
     */
    private ComboTipo tipoCombo;

    /**
     * Valor configurado del combo.
     * Ejemplo:
     * - descuento fijo
     * - porcentaje
     * - precio cerrado
     */
    private BigDecimal valorCombo = BigDecimal.ZERO;

    /**
     * Suma original de bases de los items antes del combo.
     */
    private BigDecimal precioOriginal = BigDecimal.ZERO;

    /**
     * Precio final del combo aplicado.
     */
    private BigDecimal precioFinal = BigDecimal.ZERO;

    /**
     * Ahorro total generado por el combo.
     */
    private BigDecimal ahorroTotal = BigDecimal.ZERO;

    /**
     * Items del ticket que forman este combo.
     */
    private List<RegistrarVentaComboItemRequest> items = new ArrayList<>();

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

    public List<RegistrarVentaComboItemRequest> getItems() {
        return items;
    }

    public void setItems(List<RegistrarVentaComboItemRequest> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
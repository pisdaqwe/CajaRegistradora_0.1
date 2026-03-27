package dtoS;

import java.math.BigDecimal;
import java.util.Objects;

public class VentaComboItemDTO {

    private int idVentaCombo;
    private int idItem;
    private BigDecimal subtotalOriginalItem;
    private BigDecimal descuentoAsignado;
    private BigDecimal subtotalFinalItem;

    public VentaComboItemDTO() {
        this.subtotalOriginalItem = BigDecimal.ZERO;
        this.descuentoAsignado = BigDecimal.ZERO;
        this.subtotalFinalItem = BigDecimal.ZERO;
    }

    public int getIdVentaCombo() {
        return idVentaCombo;
    }

    public void setIdVentaCombo(int idVentaCombo) {
        this.idVentaCombo = idVentaCombo;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public BigDecimal getSubtotalOriginalItem() {
        return subtotalOriginalItem;
    }

    public void setSubtotalOriginalItem(BigDecimal subtotalOriginalItem) {
        this.subtotalOriginalItem = subtotalOriginalItem == null ? BigDecimal.ZERO : subtotalOriginalItem;
    }

    public BigDecimal getDescuentoAsignado() {
        return descuentoAsignado;
    }

    public void setDescuentoAsignado(BigDecimal descuentoAsignado) {
        this.descuentoAsignado = descuentoAsignado == null ? BigDecimal.ZERO : descuentoAsignado;
    }

    public BigDecimal getSubtotalFinalItem() {
        return subtotalFinalItem;
    }

    public void setSubtotalFinalItem(BigDecimal subtotalFinalItem) {
        this.subtotalFinalItem = subtotalFinalItem == null ? BigDecimal.ZERO : subtotalFinalItem;
    }

    @Override
    public String toString() {
        return "VentaComboItemDTO{" +
                "idVentaCombo=" + idVentaCombo +
                ", idItem=" + idItem +
                ", subtotalOriginalItem=" + subtotalOriginalItem +
                ", descuentoAsignado=" + descuentoAsignado +
                ", subtotalFinalItem=" + subtotalFinalItem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VentaComboItemDTO other)) return false;
        return idVentaCombo == other.idVentaCombo
                && idItem == other.idItem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVentaCombo, idItem);
    }
}
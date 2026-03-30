package model;

import java.math.BigDecimal;
import java.util.Objects;

import enums.TicketRowType;

/**
 * Fila plana del ticket para pintar en JList y mapear selección → acción.
 *
 * Tipos posibles:
 *
 * - ITEM:
 *   itemIndex válido, subIndex = -1, idExtra/idPersonalizacion = null
 *
 * - EXTRA:
 *   itemIndex válido, subIndex = index del extra, idExtra != null
 *
 * - PERSONALIZACION:
 *   itemIndex válido, subIndex puede ser -1, idPersonalizacion != null
 *
 * - ASK_ME:
 *   itemIndex válido, subIndex = index del ask me, sin ids extra/personalización
 *
 * - COMBO / AHORRO / DESCUENTO / AHORRO_DESCUENTO:
 *   filas informativas, no asociadas directamente a un item real
 *   => itemIndex = -1, subIndex = -1, ids null
 */
public final class TicketRow {

    private final TicketRowType type;
    private final int itemIndex;
    private final int subIndex;

    private final Integer idExtra;
    private final Integer idPersonalizacion;

    private final String label;
    private final BigDecimal amount;

    public TicketRow(
            TicketRowType type,
            int itemIndex,
            int subIndex,
            Integer idExtra,
            Integer idPersonalizacion,
            String label,
            BigDecimal amount
    ) {
        this.type = Objects.requireNonNull(type, "type no puede ser null");
        this.itemIndex = itemIndex;
        this.subIndex = subIndex;

        this.idExtra = idExtra;
        this.idPersonalizacion = idPersonalizacion;

        this.label = Objects.requireNonNull(label, "label no puede ser null").trim();
        if (this.label.isEmpty()) {
            throw new IllegalArgumentException("label no puede estar vacío");
        }

        this.amount = amount;
    }

    public TicketRowType getType() {
        return type;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public int getSubIndex() {
        return subIndex;
    }

    public Integer getIdExtra() {
        return idExtra;
    }

    public Integer getIdPersonalizacion() {
        return idPersonalizacion;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "TicketRow{type=" + type
                + ", itemIndex=" + itemIndex
                + ", subIndex=" + subIndex
                + ", idExtra=" + idExtra
                + ", idPersonalizacion=" + idPersonalizacion
                + ", label='" + label + '\''
                + ", amount=" + amount + '}';
    }
}
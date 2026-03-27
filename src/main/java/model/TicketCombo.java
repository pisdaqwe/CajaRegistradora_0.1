package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TicketCombo {

    private final UUID comboInstanceId;
    private final ComboDefinition comboDefinition;
    private final List<Integer> ticketItemIndexes;
    private final BigDecimal precioOriginal;
    private final BigDecimal precioFinal;
    private final BigDecimal ahorroTotal;

    public TicketCombo(ComboDefinition comboDefinition,
                       List<Integer> ticketItemIndexes,
                       BigDecimal precioOriginal,
                       BigDecimal precioFinal,
                       BigDecimal ahorroTotal) {

        this.comboInstanceId = UUID.randomUUID();
        this.comboDefinition = Objects.requireNonNull(comboDefinition, "comboDefinition no puede ser null");
        this.ticketItemIndexes = ticketItemIndexes == null ? new ArrayList<>() : new ArrayList<>(ticketItemIndexes);
        this.precioOriginal = precioOriginal == null ? BigDecimal.ZERO : precioOriginal;
        this.precioFinal = precioFinal == null ? BigDecimal.ZERO : precioFinal;
        this.ahorroTotal = ahorroTotal == null ? BigDecimal.ZERO : ahorroTotal;
    }

    public UUID getComboInstanceId() {
        return comboInstanceId;
    }

    public ComboDefinition getComboDefinition() {
        return comboDefinition;
    }

    public List<Integer> getTicketItemIndexes() {
        return Collections.unmodifiableList(ticketItemIndexes);
    }

    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public BigDecimal getAhorroTotal() {
        return ahorroTotal;
    }

    public int getIdCombo() {
        return comboDefinition.getIdCombo();
    }

    public String getNombreCombo() {
        return comboDefinition.getNombreCombo();
    }

    public boolean containsItemIndex(int itemIndex) {
        return ticketItemIndexes.contains(itemIndex);
    }

    public boolean isEmpty() {
        return ticketItemIndexes.isEmpty();
    }

    public int size() {
        return ticketItemIndexes.size();
    }

    @Override
    public String toString() {
        return "TicketCombo{" +
                "comboInstanceId=" + comboInstanceId +
                ", nombreCombo='" + getNombreCombo() + '\'' +
                ", ticketItemIndexes=" + ticketItemIndexes +
                ", precioOriginal=" + precioOriginal +
                ", precioFinal=" + precioFinal +
                ", ahorroTotal=" + ahorroTotal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TicketCombo other)) return false;
        return Objects.equals(comboInstanceId, other.comboInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comboInstanceId);
    }
}

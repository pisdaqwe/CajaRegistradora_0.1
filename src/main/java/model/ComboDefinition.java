package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dtoS.ComboDTO;
import dtoS.ComboItemDTO;

public class ComboDefinition {

    private final ComboDTO combo;
    private final List<ComboItemDTO> items;

    public ComboDefinition(ComboDTO combo, List<ComboItemDTO> items) {
        this.combo = Objects.requireNonNull(combo, "combo no puede ser null");
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    public ComboDTO getCombo() {
        return combo;
    }

    public List<ComboItemDTO> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getIdCombo() {
        return combo.getIdCombo();
    }

    public String getNombreCombo() {
        return combo.getNombre();
    }

    public boolean isActivo() {
        return combo.isActivo();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        return "ComboDefinition{" +
                "combo=" + combo +
                ", items=" + items +
                '}';
    }
}
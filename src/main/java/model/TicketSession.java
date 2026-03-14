package model;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import enums.TicketRowType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Estado completo de la venta en curso.
 * - Mantiene items en memoria
 * - Construye filas planas (TicketRow) para el ticket visual
 * - Permite eliminar por flatIndex (según selección en JList)
 */
public final class TicketSession {

    // =====================================================
    // ESTADO (items + selección)
    // =====================================================

    private final List<TicketItem> items = new ArrayList<>();
    private int selectedFlatIndex = -1;

    // =====================================================
    // GETTERS BÁSICOS / UTILIDADES
    // =====================================================

    public List<TicketItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (TicketItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    // =====================================================
    // SELECCIÓN (guardada en el modelo)
    // =====================================================

    public void setSelectedFlatIndex(int flatIndex) {
        this.selectedFlatIndex = flatIndex;
    }

    public int getSelectedFlatIndex() {
        return selectedFlatIndex;
    }

    public void clearSelection() {
        selectedFlatIndex = -1;
    }

    public TicketRow getSelectedRowOrNull() {
        if (selectedFlatIndex < 0) return null;

        List<TicketRow> rows = buildRows();
        if (selectedFlatIndex >= rows.size()) return null;

        return rows.get(selectedFlatIndex);
    }

    public TicketItem getSelectedItemOrNull() {
        return getItemFromFlatIndexOrNull(selectedFlatIndex);
    }

    // =====================================================
    // CICLO DE VIDA DEL TICKET
    // =====================================================

    public void clear() {
        items.clear();
        clearSelection();
    }

    // =====================================================
    // OPERACIONES PRINCIPALES (TPV)
    // =====================================================

    public void addItem(ProductoDTO producto, TamanoDTO tamanoDefault, BigDecimal precioTamano) {
        Objects.requireNonNull(producto, "producto no puede ser null");
        Objects.requireNonNull(tamanoDefault, "tamanoDefault no puede ser null");
        Objects.requireNonNull(precioTamano, "precioTamano no puede ser null");

        if (precioTamano.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precioTamano no puede ser negativo");
        }

        items.add(new TicketItem(producto, tamanoDefault, precioTamano));
    }

    public void changeSize(int itemIndex, TamanoDTO nuevoTamano, BigDecimal nuevoPrecioTamano) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.setTamano(nuevoTamano, nuevoPrecioTamano);
    }

    public void addExtra(int itemIndex, ExtraDTO extra) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.addExtra(extra);
    }

    /**
     * Reemplaza el extra de un tipo concreto por el nuevo.
     * Caso típico: MILK.
     */
    public void replaceExtraByTipo(int itemIndex, ExtraDTO extra) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.replaceExtraByTipo(extra);
    }

    public void togglePersonalizacion(int itemIndex, PersonalizacionDTO p) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.togglePersonalizacion(p);
    }
    public int duplicateItem(int itemIndex) {
        TicketItem original = getItemOrThrow(itemIndex);
        TicketItem copy = original.duplicate();

        items.add(copy);
        return items.size() - 1;
    }

    // =====================================================
    // ELIMINACIÓN (por selección o por flatIndex)
    // =====================================================

    public void removeSelected() {
        if (selectedFlatIndex < 0) return;
        removeByFlatIndex(selectedFlatIndex);
    }

    public void removeByFlatIndex(int flatIndex) {
        if (flatIndex < 0) return;

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) return;

        TicketRow row = rows.get(flatIndex);

        int itemIndex = row.getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) return;

        TicketItem item = items.get(itemIndex);

        switch (row.getType()) {

        case ITEM -> items.remove(itemIndex);

        case EXTRA -> {
            int extraIndex = row.getSubIndex();
            item.removeExtraByIndex(extraIndex);
        }

        case PERSONALIZACION -> {
            Integer idP = row.getIdPersonalizacion();
            if (idP != null) {
                item.getPersonalizaciones().remove(idP);
            }
        }

        case ASK_ME -> {
            int askMeIndex = row.getSubIndex();
            item.removeAskMeByIndex(askMeIndex);
        }
    }

        clearSelection();
    }
    public void addAskMe(int itemIndex, String text) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.addAskMe(text);
    }

    public void removeAskMe(int itemIndex, int askMeIndex) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.removeAskMeByIndex(askMeIndex);
    }

    // =====================================================
    // MAPEOS flatIndex → itemIndex / item (para panel derecho)
    // =====================================================

    public int getItemIndexFromFlatIndexOrThrow(int flatIndex) {
        if (flatIndex < 0) {
            throw new IllegalArgumentException("flatIndex no puede ser < 0");
        }

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) {
            throw new IndexOutOfBoundsException("flatIndex fuera de rango: " + flatIndex);
        }

        int itemIndex = rows.get(flatIndex).getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new IllegalStateException("itemIndex inválido calculado desde flatIndex: " + itemIndex);
        }

        return itemIndex;
    }

    public TicketItem getItemFromFlatIndexOrNull(int flatIndex) {
        if (flatIndex < 0) return null;

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) return null;

        int itemIndex = rows.get(flatIndex).getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) return null;

        return items.get(itemIndex);
    }
    
    public int findFlatIndexForItem(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return -1;
        }

        List<TicketRow> rows = buildRows();

        for (int i = 0; i < rows.size(); i++) {
            TicketRow row = rows.get(i);

            if (row.getType() == TicketRowType.ITEM && row.getItemIndex() == itemIndex) {
                return i;
            }
        }

        return -1;
    }

    // =====================================================
    // FILAS PLANAS (para UI: JList<TicketRow>)
    // =====================================================

    public List<TicketRow> buildRows() {
        List<TicketRow> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            TicketItem item = items.get(i);

            // ITEM
            String labelItem = item.getProducto().getNombre() + " " + item.getTamano().getNombre();
            rows.add(new TicketRow(
                    TicketRowType.ITEM,
                    i,
                    -1,
                    null,
                    null,
                    labelItem,
                    item.getPrecioBase()
            ));

            // EXTRAS
            for (int e = 0; e < item.getExtras().size(); e++) {
                TicketExtra ex = item.getExtras().get(e);

                rows.add(new TicketRow(
                        TicketRowType.EXTRA,
                        i,
                        e,
                        ex.getIdExtra(),
                        null,
                        "+ " + ex.getNombre(),
                        ex.getPrecio()
                ));
            }

            // PERSONALIZACIONES
            for (TicketPersonalizacion p : item.getPersonalizaciones().values()) {
                rows.add(new TicketRow(
                        TicketRowType.PERSONALIZACION,
                        i,
                        -1,
                        null,
                        p.getIdPersonalizacion(),
                        "- " + p.getNombre(),
                        p.getPrecio().compareTo(BigDecimal.ZERO) == 0 ? null : p.getPrecio()
                ));
            }
            //ASK ME
            for (int a = 0; a < item.getAskMes().size(); a++) {
                String askMe = item.getAskMes().get(a);

                rows.add(new TicketRow(
                        TicketRowType.ASK_ME,
                        i,
                        a,
                        null,
                        null,
                        "Ask Me: " + askMe,
                        null
                ));
            }
            
            
        }

        return rows;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private TicketItem getItemOrThrow(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new IndexOutOfBoundsException("itemIndex fuera de rango: " + itemIndex);
        }
        return items.get(itemIndex);
    }
    public void selectItemRow(int itemIndex) {
        int flatIndex = findFlatIndexForItem(itemIndex);
        setSelectedFlatIndex(flatIndex);
    }
}
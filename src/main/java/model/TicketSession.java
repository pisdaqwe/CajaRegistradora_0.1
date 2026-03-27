package model;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import enums.TicketRowType;
import model.TicketCombo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Estado completo de la venta en curso.
 *
 * Responsabilidades:
 * - Mantener los items actuales del ticket en memoria
 * - Mantener los combos aplicados en memoria
 * - Gestionar la selección actual del ticket
 * - Construir filas planas para pintar el ticket visual (JList<TicketRow>)
 * - Calcular el total final del ticket teniendo en cuenta combos
 */
public final class TicketSession {

    // =====================================================
    // 1) ESTADO INTERNO DEL TICKET
    // =====================================================

    /**
     * Lista principal de items del ticket.
     * Cada TicketItem representa un producto base con su tamaño,
     * extras, personalizaciones y Ask Me.
     */
    private final List<TicketItem> items = new ArrayList<>();

    /**
     * Lista de combos actualmente aplicados al ticket.
     * Se usará para calcular el total final sin cobrar dos veces
     * la base de los productos que ya pertenecen a un combo.
     */
    private final List<TicketCombo> appliedCombos = new ArrayList<>();

    /**
     * Índice de la fila seleccionada en el ticket visual plano.
     * Este índice se refiere a la lista que construye buildRows().
     */
    private int selectedFlatIndex = -1;

    // =====================================================
    // 2) GETTERS BÁSICOS DEL ESTADO
    // =====================================================

    /**
     * Devuelve los items actuales del ticket en modo solo lectura.
     */
    public List<TicketItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Devuelve los combos aplicados en modo solo lectura.
     */
    public List<TicketCombo> getAppliedCombos() {
        return Collections.unmodifiableList(appliedCombos);
    }

    /**
     * Sustituye por completo la lista de combos aplicados.
     * Se utilizará cuando el sistema recalcule combos.
     */
    public void replaceAppliedCombos(List<TicketCombo> combos) {
        appliedCombos.clear();
        if (combos != null) {
            appliedCombos.addAll(combos);
        }
    }

    /**
     * Borra todos los combos aplicados.
     */
    public void clearAppliedCombos() {
        appliedCombos.clear();
    }

    /**
     * Indica si actualmente hay combos aplicados.
     */
    public boolean hasAppliedCombos() {
        return !appliedCombos.isEmpty();
    }

    /**
     * Indica si el ticket no tiene ningún item.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // =====================================================
    // 3) CÁLCULO DEL TOTAL FINAL DEL TICKET
    // =====================================================

    /**
     * Total final del ticket.
     *
     * Regla actual:
     * - Los combos sustituyen al precio base de los productos que incluyen
     * - Los extras y personalizaciones de esos items en combo se cobran aparte
     * - Los items que no pertenecen a combo se cobran completos
     */
    public BigDecimal getTotal() {
        return getTotalCombosAplicados()
                .add(getTotalExtrasDeItemsEnCombo())
                .add(getTotalItemsFueraDeCombo());
    }

    /**
     * Suma el precio final de todos los combos aplicados.
     */
    public BigDecimal getTotalCombosAplicados() {
        BigDecimal total = BigDecimal.ZERO;

        for (TicketCombo combo : appliedCombos) {
            total = total.add(combo.getPrecioFinal());
        }

        return total;
    }

    /**
     * Suma extras y personalizaciones de los items que pertenecen a algún combo.
     * Esto sirve para cobrarlos aparte del precio cerrado del combo.
     */
    public BigDecimal getTotalExtrasDeItemsEnCombo() {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < items.size(); i++) {
            if (isItemInAnyCombo(i)) {
                total = total.add(items.get(i).getTotalExtrasYPersonalizaciones());
            }
        }

        return total;
    }

    /**
     * Suma el subtotal completo de los items que no pertenecen a ningún combo.
     */
    public BigDecimal getTotalItemsFueraDeCombo() {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < items.size(); i++) {
            if (!isItemInAnyCombo(i)) {
                total = total.add(items.get(i).getSubtotal());
            }
        }

        return total;
    }

    // =====================================================
    // 4) GESTIÓN DE SELECCIÓN DEL TICKET VISUAL
    // =====================================================

    /**
     * Guarda qué fila plana del ticket está seleccionada.
     */
    public void setSelectedFlatIndex(int flatIndex) {
        this.selectedFlatIndex = flatIndex;
    }

    /**
     * Devuelve el índice de la fila plana seleccionada.
     */
    public int getSelectedFlatIndex() {
        return selectedFlatIndex;
    }

    /**
     * Limpia la selección actual.
     */
    public void clearSelection() {
        selectedFlatIndex = -1;
    }

    /**
     * Devuelve la fila seleccionada o null si no hay selección válida.
     */
    public TicketRow getSelectedRowOrNull() {
        if (selectedFlatIndex < 0) return null;

        List<TicketRow> rows = buildRows();
        if (selectedFlatIndex >= rows.size()) return null;

        return rows.get(selectedFlatIndex);
    }

    /**
     * Devuelve el TicketItem asociado a la fila seleccionada.
     */
    public TicketItem getSelectedItemOrNull() {
        return getItemFromFlatIndexOrNull(selectedFlatIndex);
    }

    // =====================================================
    // 5) CICLO DE VIDA DEL TICKET
    // =====================================================

    /**
     * Vacía el ticket por completo:
     * - items
     * - combos
     * - selección
     */
    public void clear() {
        items.clear();
        appliedCombos.clear();
        clearSelection();
    }

    // =====================================================
    // 6) OPERACIONES PRINCIPALES SOBRE EL TICKET
    // =====================================================

    /**
     * Añade un nuevo item al ticket.
     *
     * Al añadir un producto nuevo se invalidan los combos actuales,
     * porque el contenido del ticket ha cambiado.
     */
    public void addItem(ProductoDTO producto, TamanoDTO tamanoDefault, BigDecimal precioTamano) {
        Objects.requireNonNull(producto, "producto no puede ser null");
        Objects.requireNonNull(tamanoDefault, "tamanoDefault no puede ser null");
        Objects.requireNonNull(precioTamano, "precioTamano no puede ser null");

        if (precioTamano.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precioTamano no puede ser negativo");
        }

        items.add(new TicketItem(producto, tamanoDefault, precioTamano));
        appliedCombos.clear();
    }

    /**
     * Cambia el tamaño y precio base de un item existente.
     *
     * Al cambiar el tamaño se invalidan los combos actuales,
     * porque cambia la base económica del item.
     */
    public void changeSize(int itemIndex, TamanoDTO nuevoTamano, BigDecimal nuevoPrecioTamano) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.setTamano(nuevoTamano, nuevoPrecioTamano);
        appliedCombos.clear();
    }

    /**
     * Añade un extra a un item.
     *
     * Regla actual:
     * añadir extra NO rompe el combo.
     * Solo modifica el subtotal del item.
     */
    public void addExtra(int itemIndex, ExtraDTO extra) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.addExtra(extra);
    }

    /**
     * Reemplaza el extra de un tipo concreto por otro.
     *
     * Caso típico: cambiar la leche.
     *
     * Regla actual:
     * cambiar leche NO rompe el combo.
     * Solo cambia el subtotal del item.
     */
    public void replaceExtraByTipo(int itemIndex, ExtraDTO extra) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.replaceExtraByTipo(extra);
    }

    /**
     * Activa o desactiva una personalización del item.
     *
     * Por ahora no se invalidan combos aquí.
     */
    public void togglePersonalizacion(int itemIndex, PersonalizacionDTO p) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.togglePersonalizacion(p);
    }

    /**
     * Duplica un item completo del ticket.
     *
     * Al duplicar se invalidan combos, porque cambia la composición del ticket.
     *
     * @return índice del nuevo item duplicado
     */
    public int duplicateItem(int itemIndex) {
        TicketItem original = getItemOrThrow(itemIndex);
        TicketItem copy = original.duplicate();

        items.add(copy);
        appliedCombos.clear();
        return items.size() - 1;
    }

    /**
     * Añade un texto Ask Me a un item.
     * No afecta al precio ni al combo.
     */
    public void addAskMe(int itemIndex, String text) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.addAskMe(text);
    }

    /**
     * Elimina un Ask Me concreto de un item.
     * No afecta al precio ni al combo.
     */
    public void removeAskMe(int itemIndex, int askMeIndex) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.removeAskMeByIndex(askMeIndex);
    }

    // =====================================================
    // 7) ELIMINACIÓN DESDE EL TICKET VISUAL
    // =====================================================

    /**
     * Elimina la fila actualmente seleccionada.
     */
    public void removeSelected() {
        if (selectedFlatIndex < 0) return;
        removeByFlatIndex(selectedFlatIndex);
    }

    /**
     * Elimina una fila del ticket visual plano.
     *
     * Dependiendo del tipo de fila:
     * - ITEM: elimina el item entero
     * - EXTRA: elimina solo ese extra
     * - PERSONALIZACION: elimina esa personalización
     * - ASK_ME: elimina ese texto Ask Me
     *
     * Regla actual:
     * borrar un ITEM sí rompe combos
     * borrar un EXTRA no rompe combos
     */
    public void removeByFlatIndex(int flatIndex) {
        if (flatIndex < 0) return;

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) return;

        TicketRow row = rows.get(flatIndex);

        int itemIndex = row.getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) return;

        TicketItem item = items.get(itemIndex);

        switch (row.getType()) {

            case ITEM -> {
                items.remove(itemIndex);
                appliedCombos.clear();
            }

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

    // =====================================================
    // 8) HELPERS DE MAPEADO ENTRE FILAS PLANAS E ITEMS
    // =====================================================

    /**
     * Convierte un flatIndex visual en itemIndex real.
     * Lanza excepción si el índice no es válido.
     */
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

    /**
     * Devuelve el item asociado a una fila plana o null si no es válido.
     */
    public TicketItem getItemFromFlatIndexOrNull(int flatIndex) {
        if (flatIndex < 0) return null;

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) return null;

        int itemIndex = rows.get(flatIndex).getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) return null;

        return items.get(itemIndex);
    }

    /**
     * Busca el flatIndex de la fila principal ITEM correspondiente
     * a un itemIndex real.
     */
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

    /**
     * Selecciona visualmente la fila ITEM principal de un item concreto.
     */
    public void selectItemRow(int itemIndex) {
        int flatIndex = findFlatIndexForItem(itemIndex);
        setSelectedFlatIndex(flatIndex);
    }

    // =====================================================
    // 9) HELPERS RELACIONADOS CON COMBOS
    // =====================================================

    /**
     * Indica si un item pertenece actualmente a alguno de los combos aplicados.
     */
    public boolean isItemInAnyCombo(int itemIndex) {
        for (TicketCombo combo : appliedCombos) {
            if (combo.containsItemIndex(itemIndex)) {
                return true;
            }
        }
        return false;
    }

    // =====================================================
    // 10) CONSTRUCCIÓN DE FILAS PLANAS PARA LA UI
    // =====================================================

    /**
     * Construye la representación plana del ticket para pintarlo en una JList.
     *
     * Por cada item genera:
     * - una fila ITEM
     * - varias filas EXTRA
     * - varias filas PERSONALIZACION
     * - varias filas ASK_ME
     */
    public List<TicketRow> buildRows() {
        List<TicketRow> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            TicketItem item = items.get(i);

            // -------------------------
            // Fila principal del ITEM
            // -------------------------
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

            // -------------------------
            // Filas de EXTRAS
            // -------------------------
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

            // -------------------------
            // Filas de PERSONALIZACIONES
            // -------------------------
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

            // -------------------------
            // Filas de ASK ME
            // -------------------------
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
    // 11) HELPER PRIVADO INTERNO
    // =====================================================

    /**
     * Devuelve un item por índice o lanza excepción si no existe.
     */
    private TicketItem getItemOrThrow(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new IndexOutOfBoundsException("itemIndex fuera de rango: " + itemIndex);
        }
        return items.get(itemIndex);
    }
}
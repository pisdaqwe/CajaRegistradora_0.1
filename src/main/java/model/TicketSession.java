package model;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import dtoS.TipoCafeDTO;
import enums.TicketRowType;

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
 * - Mantener el descuento aplicado en memoria
 * - Gestionar la selección actual del ticket
 * - Construir filas planas para pintar el ticket visual
 * - Calcular el total final del ticket
 *
 * CAMBIOS AÑADIDOS EN ESTA VERSIÓN:
 * - soporte para cambiar tipo de café en un item
 * - reflejar el café seleccionado en el ticket visual
 * - incluir el suplemento de café en los cálculos del ticket
 */
public final class TicketSession {

    // =====================================================
    // 1) ESTADO INTERNO DEL TICKET
    // =====================================================

    private final List<TicketItem> items = new ArrayList<>();
    private final List<TicketCombo> appliedCombos = new ArrayList<>();

    /**
     * Descuento actualmente aplicado al ticket.
     *
     * Será null cuando no haya descuento.
     */
    private DescuentoAplicado descuentoAplicado;

    /**
     * Índice plano actualmente seleccionado en el ticket visual.
     */
    private int selectedFlatIndex = -1;

    // =====================================================
    // 2) GETTERS BÁSICOS DEL ESTADO
    // =====================================================

    public List<TicketItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<TicketCombo> getAppliedCombos() {
        return Collections.unmodifiableList(appliedCombos);
    }

    public void replaceAppliedCombos(List<TicketCombo> combos) {
        appliedCombos.clear();
        if (combos != null) {
            appliedCombos.addAll(combos);
        }
    }

    public void clearAppliedCombos() {
        appliedCombos.clear();
    }

    public boolean hasAppliedCombos() {
        return !appliedCombos.isEmpty();
    }

    /**
     * Indica si hay descuento aplicado.
     */
    public boolean hasDiscount() {
        return descuentoAplicado != null;
    }

    /**
     * Devuelve el descuento aplicado o null si no existe.
     */
    public DescuentoAplicado getDescuentoAplicado() {
        return descuentoAplicado;
    }

    /**
     * Aplica o sustituye el descuento actual del ticket.
     */
    public void applyDiscount(DescuentoAplicado descuentoAplicado) {
        this.descuentoAplicado = Objects.requireNonNull(descuentoAplicado, "descuentoAplicado no puede ser null");
    }

    /**
     * Elimina el descuento actual del ticket.
     */
    public void clearDiscount() {
        this.descuentoAplicado = null;
    }

    /**
     * Devuelve el importe descontado actual.
     */
    public BigDecimal getImporteDescuento() {
        if (descuentoAplicado == null || descuentoAplicado.getImporteDescuento() == null) {
            return BigDecimal.ZERO;
        }
        return descuentoAplicado.getImporteDescuento();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // =====================================================
    // 3) CÁLCULO DEL TOTAL FINAL DEL TICKET
    // =====================================================

    /**
     * Total base del ticket sin descuento manual/promocional.
     *
     * Aquí se tienen en cuenta:
     * - combos aplicados
     * - extras / personalizaciones de items en combo
     * - suplemento del tipo de café de items en combo
     * - items fuera de combo completos
     */
    public BigDecimal getTotalSinDescuento() {
        return getTotalCombosAplicados()
                .add(getTotalExtrasDeItemsEnCombo())
                .add(getTotalItemsFueraDeCombo());
    }

    /**
     * Total final del ticket.
     *
     * Si hay descuento aplicado, se devuelve el total con descuento.
     * Si no, se devuelve el total base del ticket.
     */
    public BigDecimal getTotal() {
        BigDecimal totalBase = getTotalSinDescuento();

        if (!hasDiscount()) {
            return totalBase;
        }

        BigDecimal importeDescuento = getImporteDescuento();
        BigDecimal totalFinal = totalBase.subtract(importeDescuento);

        if (totalFinal.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return totalFinal;
    }

    public BigDecimal getTotalCombosAplicados() {
        BigDecimal total = BigDecimal.ZERO;

        for (TicketCombo combo : appliedCombos) {
            if (combo != null && combo.getPrecioFinal() != null) {
                total = total.add(combo.getPrecioFinal());
            }
        }

        return total;
    }

    /**
     * Devuelve el total de añadidos de los items que están dentro de combo.
     *
     * CAMBIO IMPORTANTE:
     * - antes solo sumaba extras y personalizaciones
     * - ahora también suma el suplemento del tipo de café
     *
     * Esto evita que el café premium se pierda cuando un item forma parte de un combo.
     */
    public BigDecimal getTotalExtrasDeItemsEnCombo() {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < items.size(); i++) {
            if (isItemInAnyCombo(i)) {
                TicketItem item = items.get(i);

                total = total
                        .add(item.getTotalExtrasYPersonalizaciones())
                        .add(item.getSuplementoTipoCafeSafe());
            }
        }

        return total;
    }

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
        if (selectedFlatIndex < 0) {
            return null;
        }

        List<TicketRow> rows = buildRows();
        if (selectedFlatIndex >= rows.size()) {
            return null;
        }

        return rows.get(selectedFlatIndex);
    }

    public TicketItem getSelectedItemOrNull() {
        return getItemFromFlatIndexOrNull(selectedFlatIndex);
    }

    // =====================================================
    // 5) CICLO DE VIDA DEL TICKET
    // =====================================================

    public void clear() {
        items.clear();
        appliedCombos.clear();
        descuentoAplicado = null;
        clearSelection();
    }

    // =====================================================
    // 6) OPERACIONES PRINCIPALES SOBRE EL TICKET
    // =====================================================

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

    public void changeSize(int itemIndex, TamanoDTO nuevoTamano, BigDecimal nuevoPrecioTamano) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.setTamano(nuevoTamano, nuevoPrecioTamano);
        appliedCombos.clear();
    }

    /**
     * NUEVO:
     * Cambia el tipo de café seleccionado de un item.
     *
     * REGLAS:
     * - el café es una elección única del item
     * - sustituye la selección anterior
     * - no se modela como extra
     * - no rompe la selección actual del ticket
     *
     * NOTA:
     * - no limpiamos combos porque el producto sigue siendo el mismo
     * - el suplemento del café se sumará en el total del ticket
     */
    public void changeTipoCafe(int itemIndex, TipoCafeDTO tipoCafe) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.setTipoCafe(tipoCafe);
    }

    public void addExtra(int itemIndex, ExtraDTO extra) {
        TicketItem item = getItemOrThrow(itemIndex);
        item.addExtra(extra);
    }

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
        appliedCombos.clear();
        return items.size() - 1;
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
    // 7) ELIMINACIÓN DESDE EL TICKET VISUAL
    // =====================================================

    public void removeSelected() {
        if (selectedFlatIndex < 0) {
            return;
        }
        removeByFlatIndex(selectedFlatIndex);
    }

    public void removeByFlatIndex(int flatIndex) {
        if (flatIndex < 0) {
            return;
        }

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) {
            return;
        }

        TicketRow row = rows.get(flatIndex);

        // Si la fila es informativa de combo/ahorro/descuento, no se elimina nada
        if (row.getType() == TicketRowType.COMBO
                || row.getType() == TicketRowType.AHORRO
                || row.getType() == TicketRowType.DESCUENTO
                || row.getType() == TicketRowType.AHORRO_DESCUENTO) {
            clearSelection();
            return;
        }

        int itemIndex = row.getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return;
        }

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

            case COMBO, AHORRO, DESCUENTO, AHORRO_DESCUENTO -> {
                // Nunca debería entrar aquí por el control anterior,
                // pero lo dejamos por seguridad.
            }
        }

        clearSelection();
    }

    // =====================================================
    // 8) HELPERS DE MAPEADO ENTRE FILAS PLANAS E ITEMS
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
        if (flatIndex < 0) {
            return null;
        }

        List<TicketRow> rows = buildRows();
        if (flatIndex >= rows.size()) {
            return null;
        }

        int itemIndex = rows.get(flatIndex).getItemIndex();
        if (itemIndex < 0 || itemIndex >= items.size()) {
            return null;
        }

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

    public void selectItemRow(int itemIndex) {
        int flatIndex = findFlatIndexForItem(itemIndex);
        setSelectedFlatIndex(flatIndex);
    }

    // =====================================================
    // 9) HELPERS RELACIONADOS CON COMBOS
    // =====================================================

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

    public List<TicketRow> buildRows() {
        List<TicketRow> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            TicketItem item = items.get(i);

            // -------------------------
            // Fila principal del ITEM
            // -------------------------
            String labelItem = buildItemLabel(item);

            /**
             * CAMBIO IMPORTANTE:
             * la fila principal ahora refleja:
             * - precio base
             * - suplemento del café seleccionado
             *
             * Los extras y personalizaciones siguen yendo en sus filas propias.
             */
            BigDecimal amountItem = item.getPrecioBase().add(item.getSuplementoTipoCafeSafe());

            rows.add(new TicketRow(
                    TicketRowType.ITEM,
                    i,
                    -1,
                    null,
                    null,
                    labelItem,
                    amountItem
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

        // =====================================================
        // FILAS VISUALES DE COMBOS APLICADOS
        // =====================================================
        for (TicketCombo combo : appliedCombos) {
            rows.add(new TicketRow(
                    TicketRowType.COMBO,
                    -1,
                    -1,
                    null,
                    null,
                    "COMBO " + combo.getNombreCombo().toUpperCase(),
                    combo.getPrecioFinal()
            ));

            rows.add(new TicketRow(
                    TicketRowType.AHORRO,
                    -1,
                    -1,
                    null,
                    null,
                    "Ahorro cliente",
                    combo.getAhorroTotal().negate()
            ));
        }

        // =====================================================
        // FILAS VISUALES DEL DESCUENTO APLICADO
        // =====================================================
        if (descuentoAplicado != null) {
            String nombreDescuento = descuentoAplicado.getNombre() != null
                    ? descuentoAplicado.getNombre().trim()
                    : "DESCUENTO";

            rows.add(new TicketRow(
                    TicketRowType.DESCUENTO,
                    -1,
                    -1,
                    null,
                    null,
                    "DESCUENTO " + nombreDescuento.toUpperCase(),
                    null
            ));

            rows.add(new TicketRow(
                    TicketRowType.AHORRO_DESCUENTO,
                    -1,
                    -1,
                    null,
                    null,
                    "Ahorro cliente",
                    descuentoAplicado.getImporteDescuento() != null
                            ? descuentoAplicado.getImporteDescuento().negate()
                            : BigDecimal.ZERO
            ));
        }

        return rows;
    }

    // =====================================================
    // 11) HELPERS PRIVADOS
    // =====================================================

    private TicketItem getItemOrThrow(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new IndexOutOfBoundsException("itemIndex fuera de rango: " + itemIndex);
        }
        return items.get(itemIndex);
    }

    /**
     * NUEVO:
     * construye el label principal del item para el ticket visual.
     *
     * Si hay café seleccionado, se refleja debajo del nombre del producto
     * usando HTML para que el renderer lo pinte en dos líneas.
     */
    private String buildItemLabel(TicketItem item) {
        String base = item.getProducto().getNombre() + " " + item.getTamano().getNombre();

        if (item.hasTipoCafeSeleccionado()
                && item.getNombreTipoCafeSeleccionado() != null
                && !item.getNombreTipoCafeSeleccionado().isBlank()) {

            return "<html>"
                    + escapeHtml(base)
                    + "<br/>"
                    + "<span style='color:#CFCFCF;'>Café: "
                    + escapeHtml(item.getNombreTipoCafeSeleccionado())
                    + "</span>"
                    + "</html>";
        }

        return base;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public BigDecimal getAhorroTotalCombos() {
        BigDecimal total = BigDecimal.ZERO;

        for (TicketCombo combo : appliedCombos) {
            if (combo != null && combo.getAhorroTotal() != null) {
                total = total.add(combo.getAhorroTotal());
            }
        }

        return total;
    }

    public BigDecimal getAhorroTotalDescuento() {
        if (descuentoAplicado == null || descuentoAplicado.getImporteDescuento() == null) {
            return BigDecimal.ZERO;
        }
        return descuentoAplicado.getImporteDescuento();
    }

    public String getNombreDescuentoAplicado() {
        if (descuentoAplicado == null || descuentoAplicado.getNombre() == null) {
            return "";
        }
        return descuentoAplicado.getNombre();
    }
}
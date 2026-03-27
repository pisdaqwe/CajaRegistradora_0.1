package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;

/**
 * Representa un item real dentro del ticket.
 *
 * Un TicketItem contiene:
 * - producto base
 * - tamaño seleccionado
 * - precio base del tamaño
 * - extras aplicados
 * - personalizaciones activas
 * - notas Ask Me
 *
 * Reglas actuales:
 * - los extras son repetibles salvo que desde fuera se use replace por tipo
 * - las personalizaciones se guardan por id y funcionan con toggle
 * - Ask Me es solo texto informativo y no afecta al precio
 */
public final class TicketItem {

    // =====================================================
    // 1) ESTADO INTERNO DEL ITEM
    // =====================================================

    /**
     * Producto base del item.
     * No cambia durante la vida del TicketItem.
     */
    private final ProductoDTO producto;

    /**
     * Tamaño actual seleccionado para el producto.
     */
    private TamanoDTO tamano;

    /**
     * Precio base actual del item según el tamaño seleccionado.
     * No incluye extras ni personalizaciones.
     */
    private BigDecimal precioBase;

    /**
     * Lista de extras añadidos al item.
     * Puede contener repetidos en tipos como SHOT o SYRUP.
     */
    private final List<TicketExtra> extras = new ArrayList<>();

    /**
     * Personalizaciones activas del item.
     * Se guardan por id para poder hacer toggle fácilmente
     * y mantener orden de inserción.
     */
    private final LinkedHashMap<Integer, TicketPersonalizacion> personalizaciones = new LinkedHashMap<>();

    /**
     * Lista de textos Ask Me asociados al item.
     */
    private final List<String> askMes = new ArrayList<>();

    // =====================================================
    // 2) CONSTRUCTOR
    // =====================================================

    public TicketItem(ProductoDTO producto, TamanoDTO tamano, BigDecimal precioBase) {
        this.producto = Objects.requireNonNull(producto, "producto no puede ser null");
        this.tamano = Objects.requireNonNull(tamano, "tamano no puede ser null");

        this.precioBase = Objects.requireNonNull(precioBase, "precioBase no puede ser null");
        if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precioBase no puede ser negativo");
        }
    }

    // =====================================================
    // 3) GETTERS BÁSICOS
    // =====================================================

    public ProductoDTO getProducto() {
        return producto;
    }

    public TamanoDTO getTamano() {
        return tamano;
    }

    /**
     * Devuelve el precio base del item.
     * No incluye extras ni personalizaciones.
     */
    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public List<TicketExtra> getExtras() {
        return extras;
    }

    public Map<Integer, TicketPersonalizacion> getPersonalizaciones() {
        return personalizaciones;
    }

    public List<String> getAskMes() {
        return askMes;
    }

    // =====================================================
    // 4) DUPLICADO DEL ITEM
    // =====================================================

    /**
     * Crea una copia completa del item actual:
     * - mismo producto
     * - mismo tamaño
     * - mismo precio base
     * - copia de extras
     * - copia de personalizaciones
     * - copia de Ask Me
     */
    public TicketItem duplicate() {
        TicketItem copy = new TicketItem(this.producto, this.tamano, this.precioBase);

        // Copiar extras
        for (TicketExtra extra : this.extras) {
            copy.extras.add(new TicketExtra(
                    extra.getIdExtra(),
                    extra.getNombre(),
                    extra.getTipo(),
                    extra.getPrecio()
            ));
        }

        // Copiar personalizaciones
        for (TicketPersonalizacion p : this.personalizaciones.values()) {
            TicketPersonalizacion copyP = new TicketPersonalizacion(
                    p.getIdPersonalizacion(),
                    p.getNombre(),
                    p.getTipo(),
                    p.getPrecio()
            );
            copy.personalizaciones.put(copyP.getIdPersonalizacion(), copyP);
        }

        // Copiar Ask Me
        copy.askMes.addAll(this.askMes);

        return copy;
    }

    // =====================================================
    // 5) CAMBIOS SOBRE EL PRODUCTO BASE
    // =====================================================

    /**
     * Cambia el tamaño del item y actualiza su nuevo precio base.
     */
    public void setTamano(TamanoDTO nuevoTamano, BigDecimal nuevoPrecioBase) {
        this.tamano = Objects.requireNonNull(nuevoTamano, "nuevoTamano no puede ser null");

        this.precioBase = Objects.requireNonNull(nuevoPrecioBase, "nuevoPrecioBase no puede ser null");
        if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("nuevoPrecioBase no puede ser negativo");
        }
    }

    // =====================================================
    // 6) GESTIÓN DE EXTRAS
    // =====================================================

    /**
     * Añade un extra al item.
     *
     * Para tipos repetibles como SHOT, SYRUP o TOPPING:
     * este método vale directamente.
     *
     * Para tipos exclusivos como MILK:
     * normalmente desde fuera se usará replaceExtraByTipo(...).
     */
    public void addExtra(ExtraDTO extraDto) {
        Objects.requireNonNull(extraDto, "extraDto no puede ser null");

        extras.add(new TicketExtra(
                extraDto.getIdExtra(),
                extraDto.getNombre(),
                extraDto.getTipo(),
                extraDto.getPrecio()
        ));
    }

    /**
     * Elimina un extra por posición dentro de la lista.
     */
    public void removeExtraByIndex(int extraIndex) {
        if (extraIndex < 0 || extraIndex >= extras.size()) {
            throw new IndexOutOfBoundsException("extraIndex fuera de rango: " + extraIndex);
        }
        extras.remove(extraIndex);
    }

    /**
     * Elimina todos los extras de un tipo concreto.
     *
     * Ejemplo típico:
     * quitar la leche actual antes de poner una nueva.
     */
    public void removeExtrasByTipo(String tipo) {
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        extras.removeIf(e -> tipo.equalsIgnoreCase(e.getTipo()));
    }

    /**
     * Reemplaza el extra de un tipo por el nuevo.
     *
     * Ejemplo típico:
     * MILK, donde solo debe existir una leche activa.
     */
    public void replaceExtraByTipo(ExtraDTO extraDto) {
        Objects.requireNonNull(extraDto, "extraDto no puede ser null");
        removeExtrasByTipo(extraDto.getTipo());
        addExtra(extraDto);
    }

    /**
     * Comprueba si el item ya tiene aplicado un extra concreto.
     */
    public boolean hasExtraById(int idExtra) {
        return extras.stream().anyMatch(e -> e.getIdExtra() == idExtra);
    }

    // =====================================================
    // 7) GESTIÓN DE PERSONALIZACIONES
    // =====================================================

    /**
     * Activa o desactiva una personalización.
     *
     * Si ya existe, la elimina.
     * Si no existe, la añade.
     *
     * @return true si se ha activado, false si se ha desactivado
     */
    public boolean togglePersonalizacion(PersonalizacionDTO pDto) {
        Objects.requireNonNull(pDto, "pDto no puede ser null");
        int id = pDto.getIdPersonalizacion();

        if (personalizaciones.containsKey(id)) {
            personalizaciones.remove(id);
            return false;
        }

        personalizaciones.put(
                id,
                new TicketPersonalizacion(
                        pDto.getIdPersonalizacion(),
                        pDto.getNombre(),
                        pDto.getTipo(),
                        pDto.getPrecio()
                )
        );

        return true;
    }

    // =====================================================
    // 8) GESTIÓN DE ASK ME
    // =====================================================

    /**
     * Añade una nota Ask Me si el texto es válido.
     */
    public void addAskMe(String text) {
        if (text == null) {
            return;
        }

        String normalized = text.trim();
        if (normalized.isEmpty()) {
            return;
        }

        askMes.add(normalized);
    }

    /**
     * Elimina una nota Ask Me por índice.
     */
    public void removeAskMeByIndex(int askMeIndex) {
        if (askMeIndex < 0 || askMeIndex >= askMes.size()) {
            throw new IndexOutOfBoundsException("askMeIndex fuera de rango: " + askMeIndex);
        }
        askMes.remove(askMeIndex);
    }

    /**
     * Borra todas las notas Ask Me del item.
     */
    public void clearAskMes() {
        askMes.clear();
    }

    /**
     * Indica si el item tiene notas Ask Me.
     */
    public boolean hasAskMes() {
        return !askMes.isEmpty();
    }

    // =====================================================
    // 9) CÁLCULOS ECONÓMICOS DEL ITEM
    // =====================================================

    /**
     * Devuelve la suma de extras y personalizaciones.
     *
     * Sirve para combos, donde el precio base puede quedar cubierto
     * por el combo pero los añadidos deben cobrarse aparte.
     */
    public BigDecimal getTotalExtrasYPersonalizaciones() {
        BigDecimal total = BigDecimal.ZERO;

        for (TicketExtra e : extras) {
            total = total.add(e.getPrecio());
        }

        for (TicketPersonalizacion p : personalizaciones.values()) {
            total = total.add(p.getPrecio());
        }

        return total;
    }

    /**
     * Devuelve el subtotal completo del item:
     * precio base + extras + personalizaciones.
     */
    public BigDecimal getSubtotal() {
        return precioBase.add(getTotalExtrasYPersonalizaciones());
    }
}
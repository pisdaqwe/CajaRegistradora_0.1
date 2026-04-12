package model;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;
import dtoS.TipoCafeDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representa un item real dentro del ticket.
 *
 * Un TicketItem contiene:
 * - producto base
 * - tamaño seleccionado
 * - precio base del tamaño
 * - tipo de café seleccionado
 * - suplemento del tipo de café
 * - extras aplicados
 * - personalizaciones activas
 * - notas Ask Me
 *
 * REGLAS ACTUALES:
 * - los extras son repetibles salvo que desde fuera se use replace por tipo
 * - las personalizaciones se guardan por id y funcionan con toggle
 * - Ask Me es solo texto informativo y no afecta al precio
 * - el tipo de café es una elección única del item
 * - el suplemento del tipo de café sí afecta al precio
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
     * No incluye extras, personalizaciones ni suplemento de café.
     */
    private BigDecimal precioBase;

    /**
     * NUEVO:
     * ID del tipo de café seleccionado para el item.
     *
     * Puede ser null si el item todavía no tiene café elegido.
     */
    private Integer idTipoCafeSeleccionado;

    /**
     * NUEVO:
     * Nombre visible del tipo de café seleccionado.
     *
     * Ejemplos:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     */
    private String nombreTipoCafeSeleccionado;

    /**
     * NUEVO:
     * ID del ingrediente real asociado al tipo de café.
     *
     * Lo dejamos preparado para la futura fase de recetas/stock.
     */
    private Integer idIngredienteTipoCafeSeleccionado;

    /**
     * NUEVO:
     * Suplemento económico del café seleccionado.
     *
     * Ejemplos:
     * - 0.00 para Espresso
     * - 0.30 para Colombia Campaña
     * - 0.50 para Etiopía Campaña
     */
    private BigDecimal suplementoTipoCafe = BigDecimal.ZERO;

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
     * No incluye extras, personalizaciones ni suplemento de café.
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
    // 4) GETTERS DEL BLOQUE CAFÉ
    // =====================================================

    public Integer getIdTipoCafeSeleccionado() {
        return idTipoCafeSeleccionado;
    }

    public String getNombreTipoCafeSeleccionado() {
        return nombreTipoCafeSeleccionado;
    }

    public Integer getIdIngredienteTipoCafeSeleccionado() {
        return idIngredienteTipoCafeSeleccionado;
    }

    public BigDecimal getSuplementoTipoCafe() {
        return suplementoTipoCafe;
    }

    /**
     * Devuelve el suplemento del tipo de café en modo seguro.
     */
    public BigDecimal getSuplementoTipoCafeSafe() {
        return suplementoTipoCafe != null ? suplementoTipoCafe : BigDecimal.ZERO;
    }

    /**
     * Indica si el item tiene un tipo de café seleccionado.
     */
    public boolean hasTipoCafeSeleccionado() {
        return idTipoCafeSeleccionado != null;
    }

    // =====================================================
    // 5) DUPLICADO DEL ITEM
    // =====================================================

    /**
     * Crea una copia completa del item actual:
     * - mismo producto
     * - mismo tamaño
     * - mismo precio base
     * - mismo tipo de café
     * - copia de extras
     * - copia de personalizaciones
     * - copia de Ask Me
     */
    public TicketItem duplicate() {
        TicketItem copy = new TicketItem(this.producto, this.tamano, this.precioBase);

        // =================================================
        // NUEVO BLOQUE AÑADIDO:
        // copiar también el café seleccionado
        // =================================================
        copy.idTipoCafeSeleccionado = this.idTipoCafeSeleccionado;
        copy.nombreTipoCafeSeleccionado = this.nombreTipoCafeSeleccionado;
        copy.idIngredienteTipoCafeSeleccionado = this.idIngredienteTipoCafeSeleccionado;
        copy.suplementoTipoCafe = this.getSuplementoTipoCafeSafe();

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
            copy.personalizaciones.put(
                    p.getIdPersonalizacion(),
                    new TicketPersonalizacion(
                            p.getIdPersonalizacion(),
                            p.getNombre(),
                            p.getTipo(),
                            p.getPrecio()
                    )
            );
        }

        // Copiar Ask Me
        copy.askMes.addAll(this.askMes);

        return copy;
    }

    // =====================================================
    // 6) CAMBIO DE TAMAÑO
    // =====================================================

    /**
     * Cambia el tamaño y actualiza el precio base del item.
     *
     * IMPORTANTE:
     * - no toca extras
     * - no toca personalizaciones
     * - no toca el tipo de café
     */
    public void setTamano(TamanoDTO nuevoTamano, BigDecimal nuevoPrecioBase) {
        this.tamano = Objects.requireNonNull(nuevoTamano, "nuevoTamano no puede ser null");
        this.precioBase = Objects.requireNonNull(nuevoPrecioBase, "nuevoPrecioBase no puede ser null");

        if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("nuevoPrecioBase no puede ser negativo");
        }
    }

    // =====================================================
    // 7) GESTIÓN DEL BLOQUE CAFÉ
    // =====================================================

    /**
     * NUEVO:
     * Aplica un tipo de café al item.
     *
     * REGLAS:
     * - el café es único por item
     * - sustituye cualquier selección anterior
     * - el suplemento sí afecta al subtotal del item
     */
    public void setTipoCafe(TipoCafeDTO tipoCafe) {
        Objects.requireNonNull(tipoCafe, "tipoCafe no puede ser null");

        if (tipoCafe.getIdTipoCafe() <= 0) {
            throw new IllegalArgumentException("idTipoCafe debe ser > 0");
        }

        this.idTipoCafeSeleccionado = tipoCafe.getIdTipoCafe();
        this.nombreTipoCafeSeleccionado = normalizeText(tipoCafe.getNombre(), "nombreTipoCafe");
        this.idIngredienteTipoCafeSeleccionado = tipoCafe.getIdIngrediente();
        this.suplementoTipoCafe = safe(tipoCafe.getSuplementoPrecio());
    }

    /**
     * NUEVO:
     * Limpia el tipo de café seleccionado del item.
     *
     * Útil si en algún momento quieres volver al estado sin selección.
     */
    public void clearTipoCafe() {
        this.idTipoCafeSeleccionado = null;
        this.nombreTipoCafeSeleccionado = null;
        this.idIngredienteTipoCafeSeleccionado = null;
        this.suplementoTipoCafe = BigDecimal.ZERO;
    }

    // =====================================================
    // 8) GESTIÓN DE EXTRAS
    // =====================================================

    /**
     * Añade un extra al item.
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
     * Elimina todos los extras de un tipo concreto.
     *
     * Útil para reglas como MILK:
     * solo puede haber una leche activa a la vez.
     */
    public void removeExtrasByTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return;
        }

        String tipoNormalizado = tipo.trim().toUpperCase();
        extras.removeIf(e -> tipoNormalizado.equals(e.getTipo().trim().toUpperCase()));
    }

    /**
     * Sustituye el extra existente del mismo tipo por otro nuevo.
     *
     * Ejemplo:
     * - cambiar leche entera por avena
     */
    public void replaceExtraByTipo(ExtraDTO extraDto) {
        Objects.requireNonNull(extraDto, "extraDto no puede ser null");

        removeExtrasByTipo(extraDto.getTipo());
        addExtra(extraDto);
    }

    /**
     * Indica si el item ya tiene un extra por id.
     */
    public boolean hasExtraById(int idExtra) {
        for (TicketExtra extra : extras) {
            if (extra.getIdExtra() == idExtra) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un extra por índice interno.
     */
    public void removeExtraByIndex(int extraIndex) {
        if (extraIndex < 0 || extraIndex >= extras.size()) {
            throw new IndexOutOfBoundsException("extraIndex fuera de rango: " + extraIndex);
        }
        extras.remove(extraIndex);
    }

    // =====================================================
    // 9) GESTIÓN DE PERSONALIZACIONES
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
    // 10) GESTIÓN DE ASK ME
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
    // 11) CÁLCULOS ECONÓMICOS DEL ITEM
    // =====================================================

    /**
     * Devuelve la suma de extras y personalizaciones.
     *
     * Sirve para combos, donde el precio base puede quedar cubierto
     * por el combo pero los añadidos deben cobrarse aparte.
     *
     * IMPORTANTE:
     * - aquí NO se incluye el suplemento del café
     * - el suplemento se suma aparte en getSubtotal()
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
     * NUEVO:
     * Devuelve el subtotal completo del item:
     * precio base + suplemento de café + extras + personalizaciones.
     */
    public BigDecimal getSubtotal() {
        return safe(precioBase)
                .add(getSuplementoTipoCafeSafe())
                .add(getTotalExtrasYPersonalizaciones());
    }

    // =====================================================
    // 12) HELPERS PRIVADOS
    // =====================================================

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizeText(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " no puede ser null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío");
        }
        return normalized;
    }
}
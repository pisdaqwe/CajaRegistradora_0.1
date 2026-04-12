package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO agregado que representa toda la customización
 * disponible para un producto.
 *
 * ANTES:
 * - tamaños
 * - extras
 * - personalizaciones
 *
 * AÑADIDO AHORA:
 * - tipos de café
 *
 * OBJETIVO:
 * - seguir usando un único DTO para que la UI cargue
 *   toda la customización del producto seleccionado
 * - mantener compatibilidad con el constructor anterior
 * - permitir que el panel central pinte la nueva card CAFÉ
 */
public class ProductoCustomizationDTO {

    /**
     * Tamaños disponibles para el producto.
     */
    private List<TamanoDTO> tamanos = new ArrayList<>();

    /**
     * Extras disponibles para el producto.
     */
    private List<ExtraDTO> extras = new ArrayList<>();

    /**
     * Personalizaciones disponibles para el producto.
     */
    private List<PersonalizacionDTO> personalizaciones = new ArrayList<>();

    /**
     * NUEVO:
     * Tipos de café disponibles para el producto.
     *
     * Solo tendrá contenido real en bebidas donde aplique.
     * En productos sin selector de café podrá venir vacío.
     */
    private List<TipoCafeDTO> tiposCafe = new ArrayList<>();

    // =====================================================
    // CONSTRUCTORES
    // =====================================================

    /**
     * Constructor antiguo mantenido por compatibilidad.
     *
     * IMPORTANTE:
     * - no rompe el código ya existente
     * - deja tiposCafe como lista vacía
     */
    public ProductoCustomizationDTO(
            List<TamanoDTO> tamanos,
            List<ExtraDTO> extras,
            List<PersonalizacionDTO> personalizaciones
    ) {
        this.tamanos = tamanos != null ? tamanos : new ArrayList<>();
        this.extras = extras != null ? extras : new ArrayList<>();
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
        this.tiposCafe = new ArrayList<>();
    }

    /**
     * NUEVO constructor completo.
     *
     * Se usará cuando ProductoPersonalizacionService
     * ya cargue también los tipos de café.
     */
    public ProductoCustomizationDTO(
            List<TamanoDTO> tamanos,
            List<ExtraDTO> extras,
            List<PersonalizacionDTO> personalizaciones,
            List<TipoCafeDTO> tiposCafe
    ) {
        this.tamanos = tamanos != null ? tamanos : new ArrayList<>();
        this.extras = extras != null ? extras : new ArrayList<>();
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
        this.tiposCafe = tiposCafe != null ? tiposCafe : new ArrayList<>();
    }

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public List<TamanoDTO> getTamanos() {
        return tamanos;
    }

    public void setTamanos(List<TamanoDTO> tamanos) {
        this.tamanos = tamanos != null ? tamanos : new ArrayList<>();
    }

    public List<ExtraDTO> getExtras() {
        return extras;
    }

    public void setExtras(List<ExtraDTO> extras) {
        this.extras = extras != null ? extras : new ArrayList<>();
    }

    public List<PersonalizacionDTO> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<PersonalizacionDTO> personalizaciones) {
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
    }

    /**
     * NUEVO getter de tipos de café.
     */
    public List<TipoCafeDTO> getTiposCafe() {
        return tiposCafe;
    }

    /**
     * NUEVO setter de tipos de café.
     */
    public void setTiposCafe(List<TipoCafeDTO> tiposCafe) {
        this.tiposCafe = tiposCafe != null ? tiposCafe : new ArrayList<>();
    }
}

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
 * Un item del ticket: producto + tamaño + precio base + extras + personalizaciones.
 * - Extras: repetibles (lista)
 * - Personalizaciones: no repetibles (LinkedHashMap por id para toggle)
 */
public final class TicketItem {

    private final ProductoDTO producto;

    private TamanoDTO tamano;
    private BigDecimal precioBase; // precio del tamaño

    private final List<TicketExtra> extras = new ArrayList<>();
    private final LinkedHashMap<Integer, TicketPersonalizacion> personalizaciones = new LinkedHashMap<>();

    public TicketItem(ProductoDTO producto, TamanoDTO tamano, BigDecimal precioBase) {
        this.producto = Objects.requireNonNull(producto, "producto no puede ser null");
        this.tamano = Objects.requireNonNull(tamano, "tamano no puede ser null");

        this.precioBase = Objects.requireNonNull(precioBase, "precioBase no puede ser null");
        if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precioBase no puede ser negativo");
        }
    }

    // =========================
    // GETTERS
    // =========================

    public ProductoDTO getProducto() {
        return producto;
    }

    public TamanoDTO getTamano() {
        return tamano;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public List<TicketExtra> getExtras() {
        return extras;
    }

    public Map<Integer, TicketPersonalizacion> getPersonalizaciones() {
        return personalizaciones;
    }

    // =========================
    // OPERACIONES
    // =========================

    public void setTamano(TamanoDTO nuevoTamano, BigDecimal nuevoPrecioBase) {
        this.tamano = Objects.requireNonNull(nuevoTamano, "nuevoTamano no puede ser null");

        this.precioBase = Objects.requireNonNull(nuevoPrecioBase, "nuevoPrecioBase no puede ser null");
        if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("nuevoPrecioBase no puede ser negativo");
        }
    }

    public void addExtra(ExtraDTO extraDto) {
        Objects.requireNonNull(extraDto, "extraDto no puede ser null");
        extras.add(new TicketExtra(
                extraDto.getIdExtra(),
                extraDto.getNombre(),
                extraDto.getPrecio()
        ));
    }

    public void removeExtraByIndex(int extraIndex) {
        if (extraIndex < 0 || extraIndex >= extras.size()) {
            throw new IndexOutOfBoundsException("extraIndex fuera de rango: " + extraIndex);
        }
        extras.remove(extraIndex);
    }

    public boolean togglePersonalizacion(PersonalizacionDTO pDto) {
        Objects.requireNonNull(pDto, "pDto no puede ser null");
        int id = pDto.getIdPersonalizacion();

        if (personalizaciones.containsKey(id)) {
            personalizaciones.remove(id);
            return false; // desactivada
        }

        personalizaciones.put(id, new TicketPersonalizacion(
                pDto.getIdPersonalizacion(),
                pDto.getNombre(),
                pDto.getTipo(),
                pDto.getPrecio()
        ));

        return true; // activada
    }

    // =========================
    // TOTALES
    // =========================

    public BigDecimal getSubtotal() {
        BigDecimal total = precioBase;

        for (TicketExtra e : extras) {
            total = total.add(e.getPrecio());
        }

        for (TicketPersonalizacion p : personalizaciones.values()) {
            total = total.add(p.getPrecio());
        }

        return total;
    }
}
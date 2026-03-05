package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Personalización aplicada a un TicketItem.
 * No es repetible: se controla por id mediante LinkedHashMap en TicketItem.
 */
public final class TicketPersonalizacion {

    private final int idPersonalizacion;
    private final String nombre;
    private final String tipo;
    private final BigDecimal precio;

    public TicketPersonalizacion(int idPersonalizacion, String nombre, String tipo, BigDecimal precio) {
        if (idPersonalizacion <= 0) throw new IllegalArgumentException("idPersonalizacion debe ser > 0");
        this.idPersonalizacion = idPersonalizacion;

        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null").trim();
        if (this.nombre.isEmpty()) throw new IllegalArgumentException("nombre no puede estar vacío");

        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser null").trim();
        if (this.tipo.isEmpty()) throw new IllegalArgumentException("tipo no puede estar vacío");

        this.precio = Objects.requireNonNull(precio, "precio no puede ser null");
        if (this.precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precio no puede ser negativo");
        }
    }
  
    public int getIdPersonalizacion() {
        return idPersonalizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "TicketPersonalizacion{idPersonalizacion=" + idPersonalizacion
                + ", nombre='" + nombre + "', tipo='" + tipo + "', precio=" + precio + "}";
    }
}
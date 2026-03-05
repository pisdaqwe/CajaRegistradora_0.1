package model;



import java.math.BigDecimal;
import java.util.Objects;

/**
 * Extra aplicado a un TicketItem.
 * Es repetible (si el negocio lo permite): se añaden varias instancias.
 */
public final class TicketExtra {

    private final int idExtra;
    private final String nombre;
    private final BigDecimal precio;

    public TicketExtra(int idExtra, String nombre, BigDecimal precio) {
        if (idExtra <= 0) throw new IllegalArgumentException("idExtra debe ser > 0");
        this.idExtra = idExtra;

        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null").trim();
        if (this.nombre.isEmpty()) throw new IllegalArgumentException("nombre no puede estar vacío");

        this.precio = Objects.requireNonNull(precio, "precio no puede ser null");
        if (this.precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precio no puede ser negativo");
        }
    }

    public int getIdExtra() {
        return idExtra;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "TicketExtra{idExtra=" + idExtra + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}

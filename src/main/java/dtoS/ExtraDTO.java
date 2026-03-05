package dtoS;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExtraDTO {

    private final int idExtra;
    private final String nombre;
    private final BigDecimal precio;

    public ExtraDTO(int idExtra, String nombre, BigDecimal precio) {
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
        return "ExtraDTO{idExtra=" + idExtra + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}

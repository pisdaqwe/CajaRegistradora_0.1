
package dtoS;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExtraDTO {

    private final int idExtra;
    private final String nombre;
    private final String tipo;
    private final BigDecimal precio;
    private final boolean disponible;

    public ExtraDTO(int idExtra, String nombre, String tipo, BigDecimal precio, boolean disponible) {
        if (idExtra <= 0) {
            throw new IllegalArgumentException("idExtra debe ser > 0");
        }
        this.idExtra = idExtra;

        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null").trim();
        if (this.nombre.isEmpty()) {
            throw new IllegalArgumentException("nombre no puede estar vacío");
        }

        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser null").trim();
        if (this.tipo.isEmpty()) {
            throw new IllegalArgumentException("tipo no puede estar vacío");
        }

        this.precio = Objects.requireNonNull(precio, "precio no puede ser null");
        if (this.precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precio no puede ser negativo");
        }

        this.disponible = disponible;
    }

    public int getIdExtra() {
        return idExtra;
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

    public boolean isDisponible() {
        return disponible;
    }

    @Override
    public String toString() {
        return "ExtraDTO{idExtra=" + idExtra
                + ", nombre='" + nombre + '\''
                + ", tipo='" + tipo + '\''
                + ", precio=" + precio
                + ", disponible=" + disponible
                + "}";
    }
}
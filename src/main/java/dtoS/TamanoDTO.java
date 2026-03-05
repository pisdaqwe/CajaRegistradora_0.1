package dtoS;

import java.util.Objects;

public final class TamanoDTO {

    private final int idTamano;
    private final String nombre;
    private final String abreviatura;

    public TamanoDTO(int idTamano, String nombre, String abreviatura) {
        if (idTamano <= 0) throw new IllegalArgumentException("idTamano debe ser > 0");
        this.idTamano = idTamano;

        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null").trim();
        if (this.nombre.isEmpty()) throw new IllegalArgumentException("nombre no puede estar vacío");

        this.abreviatura = Objects.requireNonNull(abreviatura, "abreviatura no puede ser null").trim();
        if (this.abreviatura.isEmpty()) throw new IllegalArgumentException("abreviatura no puede estar vacía");
    }

    public int getIdTamano() {
        return idTamano;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    @Override
    public String toString() {
        return "TamanoDTO{idTamano=" + idTamano + ", nombre='" + nombre + "', abreviatura='" + abreviatura + "'}";
    }
}
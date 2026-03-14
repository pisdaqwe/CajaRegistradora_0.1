package dtoS;

import java.util.Objects;

public final class LoginRapidoButtonDTO {

    private final int idUsuario;
    private final String nombreBoton;

    public LoginRapidoButtonDTO(int idUsuario, String nombreBoton) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("idUsuario debe ser > 0");
        }

        this.idUsuario = idUsuario;
        this.nombreBoton = Objects.requireNonNull(nombreBoton, "nombreBoton no puede ser null").trim();

        if (this.nombreBoton.isEmpty()) {
            throw new IllegalArgumentException("nombreBoton no puede estar vacío");
        }
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreBoton() {
        return nombreBoton;
    }

    @Override
    public String toString() {
        return "LoginRapidoButtonDTO{idUsuario=" + idUsuario +
                ", nombreBoton='" + nombreBoton + "'}";
    }
}

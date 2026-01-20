package dtoS;

/**
 * DTO para mostrar empleados con fichaje activo en la UI.
 * 
 * NO es una entidad de base de datos.
 * NO contiene lógica de negocio.
 */
public class FichajeActivoDTO {

    private final String nombreEmpleado;
    private final String horaEntrada;
    private final String estado;

    public FichajeActivoDTO(String nombreEmpleado, String horaEntrada, String estado) {
        this.nombreEmpleado = nombreEmpleado;
        this.horaEntrada = horaEntrada;
        this.estado = estado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getEstado() {
        return estado;
    }
}


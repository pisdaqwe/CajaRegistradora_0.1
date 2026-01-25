package dtoS;

/**
 * DTO para mostrar empleados con fichaje activo en la UI.
 * 
 * NO es una entidad de base de datos.
 * NO contiene lógica de negocio.
 */
public class FichajeActivoDTO {
	
	private int idUsuario;
    private final String nombreEmpleado;
    private final String horaEntrada;
    private final String estado;

    public FichajeActivoDTO(int idUsuario, String nombreEmpleado, String horaEntrada, String estado) {
        this.idUsuario = idUsuario;
    	this.nombreEmpleado = nombreEmpleado;
        this.horaEntrada = horaEntrada;
        this.estado = estado;
    }

    public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
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


package dtoS;

public class CajaEstadoDTO {

    private final int idCaja;
    private final String nombreCaja;
    private final boolean ocupada;
    private final String empleadoAsignado;
    private boolean operativa;
    public CajaEstadoDTO(
            int idCaja,
            String nombreCaja,
            boolean ocupada,
            String empleadoAsignado,
            boolean operativa
    ) {
        this.idCaja = idCaja;
        this.nombreCaja = nombreCaja;
        this.ocupada = ocupada;
        this.empleadoAsignado = empleadoAsignado;
        this.operativa = operativa;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public String getEmpleadoAsignado() {
        return empleadoAsignado;
    }

	public boolean isOperativa() {
		return operativa;
	}

	public void setOperativa(boolean operativa) {
		this.operativa = operativa;
	}
    
}

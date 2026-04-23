package dtoS;

/**
 * DTO para el reseteo de PIN de un empleado.
 *
 * Responsabilidades:
 * - Transportar el empleado objetivo y el nuevo PIN.
 */
public class ResetPinEmpleadoRequest {

    private int idUsuarioObjetivo;
    private String nuevoPin;
    private String confirmarPin;

    public ResetPinEmpleadoRequest() {
    }

    public ResetPinEmpleadoRequest(int idUsuarioObjetivo, String nuevoPin, String confirmarPin) {
        this.idUsuarioObjetivo = idUsuarioObjetivo;
        this.nuevoPin = nuevoPin;
        this.confirmarPin = confirmarPin;
    }

    public int getIdUsuarioObjetivo() {
        return idUsuarioObjetivo;
    }

    public void setIdUsuarioObjetivo(int idUsuarioObjetivo) {
        this.idUsuarioObjetivo = idUsuarioObjetivo;
    }

    public String getNuevoPin() {
        return nuevoPin;
    }

    public void setNuevoPin(String nuevoPin) {
        this.nuevoPin = nuevoPin;
    }

    public String getConfirmarPin() {
        return confirmarPin;
    }

    public void setConfirmarPin(String confirmarPin) {
        this.confirmarPin = confirmarPin;
    }

    @Override
    public String toString() {
        return "ResetPinEmpleadoRequest{" +
                "idUsuarioObjetivo=" + idUsuarioObjetivo +
                '}';
    }
}

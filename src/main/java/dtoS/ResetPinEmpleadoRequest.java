package dtoS;

/**
 * DTO para el reseteo de PIN de un empleado.
 *
 * Responsabilidades:
 * - transportar el empleado objetivo
 * - transportar el nuevo PIN
 * - transportar el usuario admin que realiza la acción
 * - transportar la sucursal desde la que se ejecuta
 */
public class ResetPinEmpleadoRequest {

    private int idUsuarioObjetivo;
    private String nuevoPin;
    private String confirmarPin;

    private int idUsuarioAdmin;
    private int idSucursalAdmin;

    public ResetPinEmpleadoRequest() {
    }

    public ResetPinEmpleadoRequest(int idUsuarioObjetivo,
                                   String nuevoPin,
                                   String confirmarPin,
                                   int idUsuarioAdmin,
                                   int idSucursalAdmin) {
        this.idUsuarioObjetivo = idUsuarioObjetivo;
        this.nuevoPin = nuevoPin;
        this.confirmarPin = confirmarPin;
        this.idUsuarioAdmin = idUsuarioAdmin;
        this.idSucursalAdmin = idSucursalAdmin;
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

    public int getIdUsuarioAdmin() {
        return idUsuarioAdmin;
    }

    public void setIdUsuarioAdmin(int idUsuarioAdmin) {
        this.idUsuarioAdmin = idUsuarioAdmin;
    }

    public int getIdSucursalAdmin() {
        return idSucursalAdmin;
    }

    public void setIdSucursalAdmin(int idSucursalAdmin) {
        this.idSucursalAdmin = idSucursalAdmin;
    }

    @Override
    public String toString() {
        return "ResetPinEmpleadoRequest{" +
                "idUsuarioObjetivo=" + idUsuarioObjetivo +
                ", idUsuarioAdmin=" + idUsuarioAdmin +
                ", idSucursalAdmin=" + idSucursalAdmin +
                '}';
    }
}
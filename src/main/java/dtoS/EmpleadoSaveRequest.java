package dtoS;

/**
 * DTO usado para alta y edición de empleados.
 *
 * Responsabilidades:
 * - Recoger los datos del formulario.
 * - Permitir al service distinguir entre ALTA y EDICIÓN.
 */
public class EmpleadoSaveRequest {

    private Integer idUsuario;
    private String nombre;
    private String usuario;
    private String pinPlano;
    private String confirmarPin;
    private int idRol;
    private int idSucursal;
    private boolean activo;

    public EmpleadoSaveRequest() {
    }

    public EmpleadoSaveRequest(Integer idUsuario,
                               String nombre,
                               String usuario,
                               String pinPlano,
                               String confirmarPin,
                               int idRol,
                               int idSucursal,
                               boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.pinPlano = pinPlano;
        this.confirmarPin = confirmarPin;
        this.idRol = idRol;
        this.idSucursal = idSucursal;
        this.activo = activo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPinPlano() {
        return pinPlano;
    }

    public void setPinPlano(String pinPlano) {
        this.pinPlano = pinPlano;
    }

    public String getConfirmarPin() {
        return confirmarPin;
    }

    public void setConfirmarPin(String confirmarPin) {
        this.confirmarPin = confirmarPin;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isAlta() {
        return idUsuario == null || idUsuario <= 0;
    }

    public boolean isEdicion() {
        return !isAlta();
    }

    @Override
    public String toString() {
        return "EmpleadoSaveRequest{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + usuario + '\'' +
                ", idRol=" + idRol +
                ", idSucursal=" + idSucursal +
                ", activo=" + activo +
                '}';
    }
}
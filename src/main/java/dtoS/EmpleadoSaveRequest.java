package dtoS;

/**
 * DTO usado para alta y edición de empleados.
 *
 * Responsabilidades:
 * - recoger los datos del formulario
 * - permitir al service distinguir entre ALTA y EDICIÓN
 * - transportar el admin/encargado que ejecuta la acción
 * - transportar la sucursal desde la que se realiza
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

    private int idUsuarioAdmin;
    private int idSucursalAdmin;

    public EmpleadoSaveRequest() {
    }

    public EmpleadoSaveRequest(Integer idUsuario,
                               String nombre,
                               String usuario,
                               String pinPlano,
                               String confirmarPin,
                               int idRol,
                               int idSucursal,
                               boolean activo,
                               int idUsuarioAdmin,
                               int idSucursalAdmin) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.pinPlano = pinPlano;
        this.confirmarPin = confirmarPin;
        this.idRol = idRol;
        this.idSucursal = idSucursal;
        this.activo = activo;
        this.idUsuarioAdmin = idUsuarioAdmin;
        this.idSucursalAdmin = idSucursalAdmin;
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
                ", idUsuarioAdmin=" + idUsuarioAdmin +
                ", idSucursalAdmin=" + idSucursalAdmin +
                '}';
    }
}
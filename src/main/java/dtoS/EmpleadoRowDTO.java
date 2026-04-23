package dtoS;

/**
 * DTO de una fila del listado principal de empleados.
 *
 * Responsabilidades:
 * - Representar una fila lista para JTable.
 * - Llevar información resumida del estado operativo del empleado.
 */
public class EmpleadoRowDTO {

    private int idUsuario;
    private String nombre;
    private String usuario;
    private String nombreRol;
    private int idSucursal;
    private String nombreSucursal;
    private boolean activo;
    private boolean fichado;
    private boolean cajaAbierta;
    private Integer idCajaActual;
    private String nombreCajaActual;

    public EmpleadoRowDTO() {
    }

    public EmpleadoRowDTO(int idUsuario,
                          String nombre,
                          String usuario,
                          String nombreRol,
                          int idSucursal,
                          String nombreSucursal,
                          boolean activo,
                          boolean fichado,
                          boolean cajaAbierta,
                          Integer idCajaActual,
                          String nombreCajaActual) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.nombreRol = nombreRol;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.activo = activo;
        this.fichado = fichado;
        this.cajaAbierta = cajaAbierta;
        this.idCajaActual = idCajaActual;
        this.nombreCajaActual = nombreCajaActual;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isFichado() {
        return fichado;
    }

    public void setFichado(boolean fichado) {
        this.fichado = fichado;
    }

    public boolean isCajaAbierta() {
        return cajaAbierta;
    }

    public void setCajaAbierta(boolean cajaAbierta) {
        this.cajaAbierta = cajaAbierta;
    }

    public Integer getIdCajaActual() {
        return idCajaActual;
    }

    public void setIdCajaActual(Integer idCajaActual) {
        this.idCajaActual = idCajaActual;
    }

    public String getNombreCajaActual() {
        return nombreCajaActual;
    }

    public void setNombreCajaActual(String nombreCajaActual) {
        this.nombreCajaActual = nombreCajaActual;
    }

    public String getActivoTexto() {
        return activo ? "Sí" : "No";
    }

    public String getFichadoTexto() {
        return fichado ? "Sí" : "No";
    }

    public String getCajaAbiertaTexto() {
        return cajaAbierta ? "Sí" : "No";
    }

    @Override
    public String toString() {
        return nombre + " (" + usuario + ")";
    }
}

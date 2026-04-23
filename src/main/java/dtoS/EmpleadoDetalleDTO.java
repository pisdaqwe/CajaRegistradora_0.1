package dtoS;

import java.time.LocalDateTime;

/**
 * DTO de detalle de empleado para el panel lateral/detalle.
 *
 * Responsabilidades:
 * - Transportar toda la información ampliada del empleado seleccionado.
 * - Incluir estado operativo actual resumido.
 */
public class EmpleadoDetalleDTO {

    private int idUsuario;
    private String nombre;
    private String usuario;
    private int idRol;
    private String nombreRol;
    private int idSucursal;
    private String nombreSucursal;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    private boolean fichajeAbierto;
    private Integer idFichajeActual;
    private LocalDateTime fechaEntradaActual;

    private boolean sesionCajaAbierta;
    private Integer idSesionCajaActual;
    private Integer idCajaActual;
    private String nombreCajaActual;

    private LocalDateTime ultimaActividad;
    private String observacionesOperativas;

    public EmpleadoDetalleDTO() {
    }

    public EmpleadoDetalleDTO(int idUsuario,
                              String nombre,
                              String usuario,
                              int idRol,
                              String nombreRol,
                              int idSucursal,
                              String nombreSucursal,
                              boolean activo,
                              LocalDateTime fechaCreacion,
                              boolean fichajeAbierto,
                              Integer idFichajeActual,
                              LocalDateTime fechaEntradaActual,
                              boolean sesionCajaAbierta,
                              Integer idSesionCajaActual,
                              Integer idCajaActual,
                              String nombreCajaActual,
                              LocalDateTime ultimaActividad,
                              String observacionesOperativas) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fichajeAbierto = fichajeAbierto;
        this.idFichajeActual = idFichajeActual;
        this.fechaEntradaActual = fechaEntradaActual;
        this.sesionCajaAbierta = sesionCajaAbierta;
        this.idSesionCajaActual = idSesionCajaActual;
        this.idCajaActual = idCajaActual;
        this.nombreCajaActual = nombreCajaActual;
        this.ultimaActividad = ultimaActividad;
        this.observacionesOperativas = observacionesOperativas;
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

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isFichajeAbierto() {
        return fichajeAbierto;
    }

    public void setFichajeAbierto(boolean fichajeAbierto) {
        this.fichajeAbierto = fichajeAbierto;
    }

    public Integer getIdFichajeActual() {
        return idFichajeActual;
    }

    public void setIdFichajeActual(Integer idFichajeActual) {
        this.idFichajeActual = idFichajeActual;
    }

    public LocalDateTime getFechaEntradaActual() {
        return fechaEntradaActual;
    }

    public void setFechaEntradaActual(LocalDateTime fechaEntradaActual) {
        this.fechaEntradaActual = fechaEntradaActual;
    }

    public boolean isSesionCajaAbierta() {
        return sesionCajaAbierta;
    }

    public void setSesionCajaAbierta(boolean sesionCajaAbierta) {
        this.sesionCajaAbierta = sesionCajaAbierta;
    }

    public Integer getIdSesionCajaActual() {
        return idSesionCajaActual;
    }

    public void setIdSesionCajaActual(Integer idSesionCajaActual) {
        this.idSesionCajaActual = idSesionCajaActual;
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

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public String getObservacionesOperativas() {
        return observacionesOperativas;
    }

    public void setObservacionesOperativas(String observacionesOperativas) {
        this.observacionesOperativas = observacionesOperativas;
    }

    public String getEstadoActivoTexto() {
        return activo ? "Activo" : "Inactivo";
    }

    public String getFichajeActualTexto() {
        return fichajeAbierto ? "Abierto" : "Cerrado";
    }

    public String getSesionCajaActualTexto() {
        return sesionCajaAbierta ? "Abierta" : "Cerrada";
    }

    public String getNombreCajaActualTexto() {
        return nombreCajaActual != null && !nombreCajaActual.trim().isEmpty()
                ? nombreCajaActual
                : "-";
    }
}
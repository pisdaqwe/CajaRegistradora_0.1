package dtoS;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO de una fila del histórico de fichajes de empleados.
 *
 * Responsabilidades:
 * - Representar una fila lista para mostrarse en JTable.
 * - Transportar datos ya preparados para la capa UI.
 */
public class FichajeEmpleadoRowDTO {

    private int idFichaje;
    private int idUsuario;
    private String nombreEmpleado;
    private int idSucursal;
    private String nombreSucursal;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private Integer duracionMinutos;
    private String estado;
    private String observaciones;

    public FichajeEmpleadoRowDTO() {
    }

    public FichajeEmpleadoRowDTO(int idFichaje,
                                 int idUsuario,
                                 String nombreEmpleado,
                                 int idSucursal,
                                 String nombreSucursal,
                                 LocalDateTime fechaEntrada,
                                 LocalDateTime fechaSalida,
                                 Integer duracionMinutos,
                                 String estado,
                                 String observaciones) {
        this.idFichaje = idFichaje;
        this.idUsuario = idUsuario;
        this.nombreEmpleado = nombreEmpleado;
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getIdFichaje() {
        return idFichaje;
    }

    public void setIdFichaje(int idFichaje) {
        this.idFichaje = idFichaje;
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

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
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

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDuracionTexto() {
        Integer minutos = duracionMinutos;

        if (minutos == null && fechaEntrada != null && fechaSalida != null) {
            minutos = (int) Duration.between(fechaEntrada, fechaSalida).toMinutes();
        }

        if (minutos == null || minutos < 0) {
            return "-";
        }

        int horas = minutos / 60;
        int mins = minutos % 60;

        if (horas > 0) {
            return horas + "h " + mins + "m";
        }

        return mins + " min";
    }

    @Override
    public String toString() {
        return "FichajeEmpleadoRowDTO{" +
                "idFichaje=" + idFichaje +
                ", idUsuario=" + idUsuario +
                ", nombreEmpleado='" + nombreEmpleado + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
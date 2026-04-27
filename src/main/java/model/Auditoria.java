package model;

import java.time.LocalDateTime;

public class Auditoria {

    private int idAuditoria;
    private int idUsuario;
    private int idSucursal;
    private String accion;
    private LocalDateTime fecha;
    private String detalles;

    public Auditoria() {
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Auditoria{" +
                "idAuditoria=" + idAuditoria +
                ", idUsuario=" + idUsuario +
                ", idSucursal=" + idSucursal +
                ", accion='" + accion + '\'' +
                ", fecha=" + fecha +
                ", detalles='" + detalles + '\'' +
                '}';
    }
}
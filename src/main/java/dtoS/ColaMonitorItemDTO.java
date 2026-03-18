package dtoS;

import java.time.LocalDateTime;

/**
 * DTO de salida para la UI del monitor de preparación.
 *
 * No representa la tabla cruda tal cual,
 * sino la información ya preparada para pintar la lista y el detalle.
 */
public class ColaMonitorItemDTO {

    private Integer idCola;
    private Integer idVenta;
    private Integer idItem;
    private Integer idEstacion;

    private String nombreEstacion;
    private String resumenLista;
    private String detalleTexto;

    private LocalDateTime fechaCreacion;

    public ColaMonitorItemDTO() {
    }

    public ColaMonitorItemDTO(Integer idCola,
                              Integer idVenta,
                              Integer idItem,
                              Integer idEstacion,
                              String nombreEstacion,
                              String resumenLista,
                              String detalleTexto,
                              LocalDateTime fechaCreacion) {
        this.idCola = idCola;
        this.idVenta = idVenta;
        this.idItem = idItem;
        this.idEstacion = idEstacion;
        this.nombreEstacion = nombreEstacion;
        this.resumenLista = resumenLista;
        this.detalleTexto = detalleTexto;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdCola() {
        return idCola;
    }

    public void setIdCola(Integer idCola) {
        this.idCola = idCola;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getNombreEstacion() {
        return nombreEstacion;
    }

    public void setNombreEstacion(String nombreEstacion) {
        this.nombreEstacion = nombreEstacion;
    }

    public String getResumenLista() {
        return resumenLista;
    }

    public void setResumenLista(String resumenLista) {
        this.resumenLista = resumenLista;
    }

    public String getDetalleTexto() {
        return detalleTexto;
    }

    public void setDetalleTexto(String detalleTexto) {
        this.detalleTexto = detalleTexto;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return resumenLista != null ? resumenLista : ("Cola #" + idCola);
    }
}

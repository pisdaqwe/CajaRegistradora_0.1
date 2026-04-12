package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO principal del caso de uso "registrar devolución".
 */
public class RegistrarDevolucionRequest {

    private int idVentaOriginal;
    private int idSesionCajaActual;
    private int idUsuarioAdmin;
    private int idSucursalActual;

    /**
     * Valores esperados:
     * - EFECTIVO
     * - TARJETA
     */
    private String metodoReembolso;

    private String motivo;
    private String observaciones;

    private List<RegistrarDevolucionItemRequest> items = new ArrayList<>();

    public int getIdVentaOriginal() {
        return idVentaOriginal;
    }

    public void setIdVentaOriginal(int idVentaOriginal) {
        this.idVentaOriginal = idVentaOriginal;
    }

    public int getIdSesionCajaActual() {
        return idSesionCajaActual;
    }

    public void setIdSesionCajaActual(int idSesionCajaActual) {
        this.idSesionCajaActual = idSesionCajaActual;
    }

    public int getIdUsuarioAdmin() {
        return idUsuarioAdmin;
    }

    public void setIdUsuarioAdmin(int idUsuarioAdmin) {
        this.idUsuarioAdmin = idUsuarioAdmin;
    }

    public int getIdSucursalActual() {
        return idSucursalActual;
    }

    public void setIdSucursalActual(int idSucursalActual) {
        this.idSucursalActual = idSucursalActual;
    }

    public String getMetodoReembolso() {
        return metodoReembolso;
    }

    public void setMetodoReembolso(String metodoReembolso) {
        this.metodoReembolso = metodoReembolso;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<RegistrarDevolucionItemRequest> getItems() {
        return items;
    }

    public void setItems(List<RegistrarDevolucionItemRequest> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
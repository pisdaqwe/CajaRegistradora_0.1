package model;

import java.time.LocalDateTime;

/**
 * Modelo persistido de la tabla ticket_json.
 *
 * Representa el registro guardado en BD:
 * - id_ticket_json
 * - id_venta
 * - json_data
 * - ruta_pdf
 * - fecha_generacion
 *
 * Esta clase NO interpreta el contenido del json_data.
 * Solo representa la fila tal y como está almacenada.
 */
public class TicketJson {

    private int idTicketJson;
    private int idVenta;
    private String jsonData;
    private String rutaPdf;
    private LocalDateTime fechaGeneracion;

    public TicketJson() {
    }

    public TicketJson(int idTicketJson, int idVenta, String jsonData, String rutaPdf, LocalDateTime fechaGeneracion) {
        this.idTicketJson = idTicketJson;
        this.idVenta = idVenta;
        this.jsonData = jsonData;
        this.rutaPdf = rutaPdf;
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getIdTicketJson() {
        return idTicketJson;
    }

    public void setIdTicketJson(int idTicketJson) {
        this.idTicketJson = idTicketJson;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    @Override
    public String toString() {
        return "TicketJson{" +
                "idTicketJson=" + idTicketJson +
                ", idVenta=" + idVenta +
                ", rutaPdf='" + rutaPdf + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                '}';
    }
}

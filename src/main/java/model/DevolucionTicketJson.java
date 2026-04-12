package model;

import java.time.LocalDateTime;

/**
 * Modelo persistido del documento JSON de una devolución.
 *
 * Responsabilidades:
 * - representar una fila real de la tabla devolucion_ticket_json
 * - servir como objeto intermedio entre DAO y Service
 *
 * IMPORTANTE:
 * - esta clase NO parsea el contenido del JSON
 * - solo transporta los datos tal y como vienen de BD
 */
public class DevolucionTicketJson {

    // =====================================================
    // 1) ATRIBUTOS PERSISTIDOS
    // =====================================================

    /**
     * PK de la tabla devolucion_ticket_json.
     */
    private int idTicketDevolucion;

    /**
     * FK a la devolución registrada.
     */
    private int idDevolucion;

    /**
     * JSON completo del ticket de devolución.
     */
    private String jsonData;

    /**
     * Ruta del PDF generado, si existe.
     * En MVP puede venir null.
     */
    private String rutaPdf;

    /**
     * Fecha/hora en la que se generó el ticket JSON.
     */
    private LocalDateTime fechaGeneracion;

    // =====================================================
    // 2) CONSTRUCTOR VACÍO
    // =====================================================

    public DevolucionTicketJson() {
    }

    // =====================================================
    // 3) GETTERS Y SETTERS
    // =====================================================

    public int getIdTicketDevolucion() {
        return idTicketDevolucion;
    }

    public void setIdTicketDevolucion(int idTicketDevolucion) {
        this.idTicketDevolucion = idTicketDevolucion;
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
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

    // =====================================================
    // 4) HELPERS ÚTILES
    // =====================================================

    /**
     * Indica si este ticket tiene una ruta PDF asociada.
     */
    public boolean hasRutaPdf() {
        return rutaPdf != null && !rutaPdf.isBlank();
    }

    /**
     * Indica si el JSON del ticket existe y no está vacío.
     */
    public boolean hasJsonData() {
        return jsonData != null && !jsonData.isBlank();
    }
}
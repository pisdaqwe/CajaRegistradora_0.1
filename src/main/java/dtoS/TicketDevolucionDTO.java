package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo del ticket de devolución.
 *
 * Responsabilidades:
 * - representar la cabecera + líneas ya parseadas del json_data
 * - servir directamente a la UI del ticket de devolución
 *
 * IMPORTANTE:
 * - no contiene lógica de negocio
 * - no accede a BD
 * - se construye desde DevolucionTicketService
 */
public class TicketDevolucionDTO {

    // =====================================================
    // 1) IDENTIFICADORES PRINCIPALES
    // =====================================================

    /**
     * ID de la devolución registrada.
     */
    private int idDevolucion;

    /**
     * ID de la venta original asociada.
     */
    private int idVentaOriginal;

    // =====================================================
    // 2) FECHAS
    // =====================================================

    /**
     * Fecha/hora de generación del ticket de devolución.
     */
    private LocalDateTime fechaGeneracion;

    /**
     * Fecha/hora de la venta original, si se quiere mostrar.
     */
    private String fechaVentaOriginal;

    // =====================================================
    // 3) DATOS GENERALES DEL PEDIDO ORIGINAL
    // =====================================================

    private String nombrePedido;
    private String tipoServicio;
    private String metodoPagoOriginal;

    // =====================================================
    // 4) DATOS DE LA DEVOLUCIÓN
    // =====================================================

    private String metodoReembolso;
    private String motivo;
    private String observaciones;
    private BigDecimal totalDevuelto;

    // =====================================================
    // 5) LÍNEAS DEL TICKET DE DEVOLUCIÓN
    // =====================================================

    private List<TicketDevolucionItemDTO> items = new ArrayList<>();

    // =====================================================
    // 6) CONSTRUCTOR VACÍO
    // =====================================================

    public TicketDevolucionDTO() {
    }

    // =====================================================
    // 7) GETTERS Y SETTERS
    // =====================================================

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdVentaOriginal() {
        return idVentaOriginal;
    }

    public void setIdVentaOriginal(int idVentaOriginal) {
        this.idVentaOriginal = idVentaOriginal;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getFechaVentaOriginal() {
        return fechaVentaOriginal;
    }

    public void setFechaVentaOriginal(String fechaVentaOriginal) {
        this.fechaVentaOriginal = fechaVentaOriginal;
    }

    public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = nombrePedido;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getMetodoPagoOriginal() {
        return metodoPagoOriginal;
    }

    public void setMetodoPagoOriginal(String metodoPagoOriginal) {
        this.metodoPagoOriginal = metodoPagoOriginal;
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

    public BigDecimal getTotalDevuelto() {
        return totalDevuelto;
    }

    public void setTotalDevuelto(BigDecimal totalDevuelto) {
        this.totalDevuelto = totalDevuelto;
    }

    public List<TicketDevolucionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TicketDevolucionItemDTO> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    // =====================================================
    // 8) HELPERS ÚTILES
    // =====================================================

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean hasMotivo() {
        return motivo != null && !motivo.isBlank();
    }

    public boolean hasObservaciones() {
        return observaciones != null && !observaciones.isBlank();
    }
}
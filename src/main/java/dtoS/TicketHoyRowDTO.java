package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de fila resumen para la pantalla/lista de tickets del día.
 *
 * Se usa para pintar JTable o lista con:
 * - tickets de venta
 * - tickets de devolución
 */
public class TicketHoyRowDTO {

    // =====================================================
    // 1) IDENTIFICADORES
    // =====================================================

    private int idVenta;
    private Integer idDevolucion;
    private Integer idVentaOriginal;

    // =====================================================
    // 2) TIPO DE REGISTRO
    // =====================================================

    /**
     * Valores esperados:
     * - VENTA
     * - DEVOLUCION
     */
    private String tipoRegistro;

    // =====================================================
    // 3) DATOS VISIBLES
    // =====================================================

    private LocalDateTime fechaGeneracion;
    private String nombrePedido;
    private String metodoPago;
    private BigDecimal total;
    private String nombreEmpleado;

    // =====================================================
    // 4) CONSTRUCTORES
    // =====================================================

    public TicketHoyRowDTO() {
    }

    public TicketHoyRowDTO(
            int idVenta,
            Integer idDevolucion,
            Integer idVentaOriginal,
            String tipoRegistro,
            LocalDateTime fechaGeneracion,
            String nombrePedido,
            String metodoPago,
            BigDecimal total,
            String nombreEmpleado
    ) {
        this.idVenta = idVenta;
        this.idDevolucion = idDevolucion;
        this.idVentaOriginal = idVentaOriginal;
        this.tipoRegistro = tipoRegistro;
        this.fechaGeneracion = fechaGeneracion;
        this.nombrePedido = nombrePedido;
        this.metodoPago = metodoPago;
        this.total = total;
        this.nombreEmpleado = nombreEmpleado;
    }

    // =====================================================
    // 5) GETTERS Y SETTERS
    // =====================================================

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Integer idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Integer getIdVentaOriginal() {
        return idVentaOriginal;
    }

    public void setIdVentaOriginal(Integer idVentaOriginal) {
        this.idVentaOriginal = idVentaOriginal;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = nombrePedido;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    // =====================================================
    // 6) HELPERS
    // =====================================================

    public boolean isVenta() {
        return "VENTA".equalsIgnoreCase(tipoRegistro);
    }

    public boolean isDevolucion() {
        return "DEVOLUCION".equalsIgnoreCase(tipoRegistro);
    }

    public boolean hasIdDevolucion() {
        return idDevolucion != null && idDevolucion > 0;
    }

    public boolean hasIdVentaOriginal() {
        return idVentaOriginal != null && idVentaOriginal > 0;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de fila resumen para la pantalla/lista de tickets del día.
 *
 * Se usa para pintar JTable o lista con los tickets consultables.
 */
public class TicketHoyRowDTO {

    private int idVenta;
    private LocalDateTime fechaGeneracion;
    private String nombrePedido;
    private String metodoPago;
    private BigDecimal total;
    private String nombreEmpleado;

    public TicketHoyRowDTO() {
    }

    public TicketHoyRowDTO(int idVenta,
                           LocalDateTime fechaGeneracion,
                           String nombrePedido,
                           String metodoPago,
                           BigDecimal total,
                           String nombreEmpleado) {
        this.idVenta = idVenta;
        this.fechaGeneracion = fechaGeneracion;
        this.nombrePedido = nombrePedido;
        this.metodoPago = metodoPago;
        this.total = total;
        this.nombreEmpleado = nombreEmpleado;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
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
}
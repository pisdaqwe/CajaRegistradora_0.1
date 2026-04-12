package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de cabecera de una venta cargada para devolución.
 *
 * Se usa para mostrar en pantalla la venta original
 * antes de seleccionar qué líneas devolver.
 */
public class VentaParaDevolucionDTO {

    private int idVenta;
    private int idSesion;
    private int idUsuario;
    private LocalDateTime fechaVenta;

    private String nombrePedido;
    private String tipoServicio;
    private String metodoPagoOriginal;

    private BigDecimal totalVenta;
    private boolean anulada;

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
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

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }
}
package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import enums.MetodoPago;
import enums.TipoServicio;

/**
 * DTO principal del caso de uso "registrar venta".
 *
 * Este objeto representa la venta completa lista para ser procesada
 * por la capa Service.
 *
 * Contiene:
 * - datos de sesión de caja
 * - usuario que vende
 * - total del cobro
 * - nombre del pedido
 * - tipo de servicio
 * - método de pago
 * - monto pagado
 * - lista de items vendidos
 */
public class RegistrarVentaRequest {

    /**
     * ID de la sesión de caja abierta.
     */
    private int idSesion;

    /**
     * ID del usuario que realiza la venta.
     */
    private int idUsuario;

    /**
     * Total final de la venta.
     */
    private BigDecimal total;

    /**
     * Nombre del pedido o cliente.
     *
     * Ejemplos:
     * - Carlos
     * - Mesa 2
     * - Pedido Glovo
     */
    private String nombrePedido;

    /**
     * Tipo de servicio:
     * - PARA_TOMAR
     * - PARA_LLEVAR
     */
    private TipoServicio tipoServicio;

    /**
     * Método de pago:
     * - EFECTIVO
     * - TARJETA
     */
    private MetodoPago metodoPago;

    /**
     * Importe recibido o pagado.
     *
     * En efectivo puede ser mayor que el total.
     * En tarjeta normalmente coincidirá con el total.
     */
    private BigDecimal montoPagado;

    /**
     * Items que componen la venta.
     */
    private List<RegistrarVentaItemRequest> items = new ArrayList<>();

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = nombrePedido;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public List<RegistrarVentaItemRequest> getItems() {
        return items;
    }

    public void setItems(List<RegistrarVentaItemRequest> items) {
        this.items = items;
    }
}
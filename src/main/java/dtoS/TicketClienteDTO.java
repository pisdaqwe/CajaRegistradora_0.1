package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo del ticket cliente listo para mostrar en UI.
 *
 * Sale de:
 * - ticket_json.json_data
 * - fecha_generacion de ticket_json
 *
 * Este DTO ya viene preparado para que el diálogo solo pinte datos,
 * sin tener que parsear JSON ni consultar BD.
 */
public class TicketClienteDTO {

    private int idVenta;
    private LocalDateTime fechaGeneracion;

    private String nombrePedido;
    private String tipoServicio;
    private String metodoPago;

    private BigDecimal montoPagado;
    private BigDecimal total;
    private BigDecimal cambio;
    private String origenDescuento;

    private List<TicketClienteItemDTO> items = new ArrayList<>();

    /**
     * Combos aplicados guardados en ticket_json.
     */
    private List<TicketClienteComboDTO> combos = new ArrayList<>();

    /**
     * NUEVO: nombre visible del descuento aplicado.
     *
     * Ejemplo:
     * - Promo 10%
     * - Descuento empleado 20%
     */
    private String nombreDescuento;

    /**
     * NUEVO: código introducido al aplicar el descuento.
     *
     * Ejemplo:
     * - PROMO10
     * - EMP20
     */
    private String codigoDescuento;

    /**
     * NUEVO: importe total descontado en euros.
     */
    private BigDecimal importeDescuento;

    public TicketClienteDTO() {
    }
    
    public String getOrigenDescuento() {
        return origenDescuento;
    }

    public void setOrigenDescuento(String origenDescuento) {
        this.origenDescuento = origenDescuento;
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

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public List<TicketClienteItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TicketClienteItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public List<TicketClienteComboDTO> getCombos() {
        return combos;
    }

    public void setCombos(List<TicketClienteComboDTO> combos) {
        this.combos = combos != null ? combos : new ArrayList<>();
    }

    public boolean hasCombos() {
        return combos != null && !combos.isEmpty();
    }

    public BigDecimal getTotalAhorroCombos() {
        BigDecimal totalAhorro = BigDecimal.ZERO;

        if (combos != null) {
            for (TicketClienteComboDTO combo : combos) {
                if (combo != null && combo.getAhorroTotal() != null) {
                    totalAhorro = totalAhorro.add(combo.getAhorroTotal());
                }
            }
        }

        return totalAhorro;
    }

    public String getNombreDescuento() {
        return nombreDescuento;
    }

    public void setNombreDescuento(String nombreDescuento) {
        this.nombreDescuento = nombreDescuento;
    }

    public String getCodigoDescuento() {
        return codigoDescuento;
    }

    public void setCodigoDescuento(String codigoDescuento) {
        this.codigoDescuento = codigoDescuento;
    }

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public boolean hasDescuento() {
        return importeDescuento != null && importeDescuento.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTotalAhorroGlobal() {
        return getTotalAhorroCombos().add(
                importeDescuento != null ? importeDescuento : BigDecimal.ZERO
        );
    }
}

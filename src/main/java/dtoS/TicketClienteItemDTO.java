package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de una línea del ticket cliente.
 *
 * Representa un producto ya listo para mostrar en pantalla,
 * incluyendo:
 * - nombre del producto
 * - tamaño
 * - precio
 * - extras
 * - personalizaciones
 * - ask me
 */
public class TicketClienteItemDTO {

    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private String tamano;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private BigDecimal iva;

    private List<String> extras = new ArrayList<>();
    private List<String> personalizaciones = new ArrayList<>();
    private List<String> askMe = new ArrayList<>();

    public TicketClienteItemDTO() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras != null ? extras : new ArrayList<>();
    }

    public List<String> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<String> personalizaciones) {
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
    }

    public List<String> getAskMe() {
        return askMe;
    }

    public void setAskMe(List<String> askMe) {
        this.askMe = askMe != null ? askMe : new ArrayList<>();
    }

    public boolean hasExtras() {
        return extras != null && !extras.isEmpty();
    }

    public boolean hasPersonalizaciones() {
        return personalizaciones != null && !personalizaciones.isEmpty();
    }

    public boolean hasAskMe() {
        return askMe != null && !askMe.isEmpty();
    }
}

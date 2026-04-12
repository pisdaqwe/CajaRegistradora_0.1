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
 * - café seleccionado
 * - precio base
 * - subtotal bruto
 * - descuento imputado a la línea
 * - subtotal final
 * - subtotal visual
 * - extras
 * - personalizaciones
 * - ask me
 */
public class TicketClienteItemDTO {

    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private String tamano;

    /**
     * NUEVO:
     * nombre del café seleccionado para este item.
     *
     * Ejemplo:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     */
    private String tipoCafe;

    private BigDecimal precioUnitario;

    /**
     * Valor bruto de la línea antes del descuento.
     */
    private BigDecimal subtotalBruto;

    /**
     * Descuento imputado a esta línea.
     */
    private BigDecimal importeDescuentoLinea;

    /**
     * Valor final realmente cobrado por la línea.
     */
    private BigDecimal subtotalFinal;

    /**
     * Campo visual/transicional.
     *
     * Mientras el diálogo siga usando getSubtotal(),
     * aquí meteremos el valor que queremos pintar.
     *
     * En ticket cliente lo normal será mostrar subtotalBruto.
     */
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

    public String getTipoCafe() {
        return tipoCafe;
    }

    public void setTipoCafe(String tipoCafe) {
        this.tipoCafe = tipoCafe;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotalBruto() {
        return subtotalBruto;
    }

    public void setSubtotalBruto(BigDecimal subtotalBruto) {
        this.subtotalBruto = subtotalBruto;
    }

    public BigDecimal getImporteDescuentoLinea() {
        return importeDescuentoLinea;
    }

    public void setImporteDescuentoLinea(BigDecimal importeDescuentoLinea) {
        this.importeDescuentoLinea = importeDescuentoLinea;
    }

    public BigDecimal getSubtotalFinal() {
        return subtotalFinal;
    }

    public void setSubtotalFinal(BigDecimal subtotalFinal) {
        this.subtotalFinal = subtotalFinal;
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

    public boolean hasTipoCafe() {
        return tipoCafe != null && !tipoCafe.isBlank();
    }
}
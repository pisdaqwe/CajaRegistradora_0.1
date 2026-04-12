package dtoS;

import java.math.BigDecimal;

/**
 * DTO de una línea individual del ticket de devolución.
 *
 * Responsabilidades:
 * - transportar una línea ya parseada desde el json_data
 * - servir directamente a la UI del ticket de devolución
 *
 * IMPORTANTE:
 * - no contiene lógica de negocio
 * - no accede a BD
 * - solo representa datos listos para pintar
 */
public class TicketDevolucionItemDTO {

    // =====================================================
    // 1) IDENTIFICADORES
    // =====================================================

    /**
     * ID real de la línea original de venta.
     */
    private int idVentaItem;

    /**
     * ID del producto original.
     */
    private int idProducto;

    // =====================================================
    // 2) DATOS VISIBLES DE LA LÍNEA
    // =====================================================

    /**
     * Nombre del producto.
     */
    private String nombreProducto;

    /**
     * Tamaño visible, si aplica.
     */
    private String tamano;

    /**
     * Texto resumen opcional para UI rápida.
     */
    private String descripcionResumen;

    // =====================================================
    // 3) CANTIDADES
    // =====================================================

    /**
     * Cantidad devuelta en esta operación.
     */
    private int cantidadDevuelta;

    // =====================================================
    // 4) IMPORTES
    // =====================================================

    /**
     * Precio unitario original de la línea.
     */
    private BigDecimal precioUnitario;

    /**
     * Subtotal bruto devuelto.
     */
    private BigDecimal subtotalBrutoDevuelto;

    /**
     * Importe de descuento correspondiente a lo devuelto.
     */
    private BigDecimal importeDescuentoDevuelto;

    /**
     * Subtotal final devuelto.
     */
    private BigDecimal subtotalFinalDevuelto;

    // =====================================================
    // 5) STOCK
    // =====================================================

    /**
     * Indica si esta línea repuso stock al devolverla.
     */
    private boolean reponeStock;

    // =====================================================
    // 6) CONSTRUCTOR VACÍO
    // =====================================================

    public TicketDevolucionItemDTO() {
    }

    // =====================================================
    // 7) GETTERS Y SETTERS
    // =====================================================

    public int getIdVentaItem() {
        return idVentaItem;
    }

    public void setIdVentaItem(int idVentaItem) {
        this.idVentaItem = idVentaItem;
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

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getDescripcionResumen() {
        return descripcionResumen;
    }

    public void setDescripcionResumen(String descripcionResumen) {
        this.descripcionResumen = descripcionResumen;
    }

    public int getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(int cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotalBrutoDevuelto() {
        return subtotalBrutoDevuelto;
    }

    public void setSubtotalBrutoDevuelto(BigDecimal subtotalBrutoDevuelto) {
        this.subtotalBrutoDevuelto = subtotalBrutoDevuelto;
    }

    public BigDecimal getImporteDescuentoDevuelto() {
        return importeDescuentoDevuelto;
    }

    public void setImporteDescuentoDevuelto(BigDecimal importeDescuentoDevuelto) {
        this.importeDescuentoDevuelto = importeDescuentoDevuelto;
    }

    public BigDecimal getSubtotalFinalDevuelto() {
        return subtotalFinalDevuelto;
    }

    public void setSubtotalFinalDevuelto(BigDecimal subtotalFinalDevuelto) {
        this.subtotalFinalDevuelto = subtotalFinalDevuelto;
    }

    public boolean isReponeStock() {
        return reponeStock;
    }

    public void setReponeStock(boolean reponeStock) {
        this.reponeStock = reponeStock;
    }

    // =====================================================
    // 8) HELPERS ÚTILES
    // =====================================================

    public boolean hasTamano() {
        return tamano != null && !tamano.isBlank();
    }

    public boolean hasDescripcionResumen() {
        return descripcionResumen != null && !descripcionResumen.isBlank();
    }
}

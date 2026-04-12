package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa un item individual dentro de una venta.
 *
 * Esta versión transporta:
 * - producto
 * - tamaño
 * - desglose económico
 * - extras
 * - personalizaciones
 * - snapshot del café seleccionado
 *
 * Así luego podremos resolver la receta real
 * incluso cuando ya no tengamos el TicketItem original.
 */
public class RegistrarVentaItemRequest {

    private int idProducto;
    private String nombreProducto;

    /**
     * NUEVO:
     * tamaño real del item.
     */
    private int idTamano;
    private String nombreTamano;

    private int cantidad;

    /**
     * Precio base del producto+tamaño sin descuento de línea.
     */
    private BigDecimal precioUnitario;

    /**
     * Incluye:
     * - precio base
     * - suplemento café
     * - extras
     * - personalizaciones con precio
     */
    private BigDecimal subtotalBruto;

    private BigDecimal importeDescuentoLinea;
    private BigDecimal subtotalFinal;
    private BigDecimal iva;

    /**
     * JSON lógico para ticket/reimpresión.
     */
    private String descripcionPersonalizacion;

    private List<RegistrarVentaExtraRequest> extras = new ArrayList<>();

    /**
     * NUEVO:
     * personalizaciones reales seleccionadas.
     */
    private List<RegistrarVentaPersonalizacionRequest> personalizaciones = new ArrayList<>();

    // =====================================================
    // BLOQUE CAFÉ
    // =====================================================

    private Integer idTipoCafeSeleccionado;
    private String nombreTipoCafeSnapshot;
    private BigDecimal suplementoTipoCafe;

    /**
     * NUEVO:
     * ingrediente real del café seleccionado.
     *
     * Esto es clave para receta/stock.
     */
    private Integer idIngredienteTipoCafeSeleccionado;

    public RegistrarVentaItemRequest() {
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotalBruto = BigDecimal.ZERO;
        this.importeDescuentoLinea = BigDecimal.ZERO;
        this.subtotalFinal = BigDecimal.ZERO;
        this.iva = BigDecimal.ZERO;
        this.suplementoTipoCafe = BigDecimal.ZERO;
        this.extras = new ArrayList<>();
        this.personalizaciones = new ArrayList<>();
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

    public int getIdTamano() {
        return idTamano;
    }

    public void setIdTamano(int idTamano) {
        this.idTamano = idTamano;
    }

    public String getNombreTamano() {
        return nombreTamano;
    }

    public void setNombreTamano(String nombreTamano) {
        this.nombreTamano = nombreTamano;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
    }

    public BigDecimal getSubtotalBruto() {
        return subtotalBruto != null ? subtotalBruto : BigDecimal.ZERO;
    }

    public void setSubtotalBruto(BigDecimal subtotalBruto) {
        this.subtotalBruto = subtotalBruto != null ? subtotalBruto : BigDecimal.ZERO;
    }

    public BigDecimal getImporteDescuentoLinea() {
        return importeDescuentoLinea != null ? importeDescuentoLinea : BigDecimal.ZERO;
    }

    public void setImporteDescuentoLinea(BigDecimal importeDescuentoLinea) {
        this.importeDescuentoLinea = importeDescuentoLinea != null ? importeDescuentoLinea : BigDecimal.ZERO;
    }

    public BigDecimal getSubtotalFinal() {
        return subtotalFinal != null ? subtotalFinal : BigDecimal.ZERO;
    }

    public void setSubtotalFinal(BigDecimal subtotalFinal) {
        this.subtotalFinal = subtotalFinal != null ? subtotalFinal : BigDecimal.ZERO;
    }

    public BigDecimal getIva() {
        return iva != null ? iva : BigDecimal.ZERO;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva != null ? iva : BigDecimal.ZERO;
    }

    public String getDescripcionPersonalizacion() {
        return descripcionPersonalizacion;
    }

    public void setDescripcionPersonalizacion(String descripcionPersonalizacion) {
        this.descripcionPersonalizacion = descripcionPersonalizacion;
    }

    public List<RegistrarVentaExtraRequest> getExtras() {
        return extras;
    }

    public void setExtras(List<RegistrarVentaExtraRequest> extras) {
        this.extras = extras != null ? extras : new ArrayList<>();
    }

    public List<RegistrarVentaPersonalizacionRequest> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<RegistrarVentaPersonalizacionRequest> personalizaciones) {
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
    }

    public Integer getIdTipoCafeSeleccionado() {
        return idTipoCafeSeleccionado;
    }

    public void setIdTipoCafeSeleccionado(Integer idTipoCafeSeleccionado) {
        this.idTipoCafeSeleccionado = idTipoCafeSeleccionado;
    }

    public String getNombreTipoCafeSnapshot() {
        return nombreTipoCafeSnapshot;
    }

    public void setNombreTipoCafeSnapshot(String nombreTipoCafeSnapshot) {
        this.nombreTipoCafeSnapshot = nombreTipoCafeSnapshot;
    }

    public BigDecimal getSuplementoTipoCafe() {
        return suplementoTipoCafe != null ? suplementoTipoCafe : BigDecimal.ZERO;
    }

    public void setSuplementoTipoCafe(BigDecimal suplementoTipoCafe) {
        this.suplementoTipoCafe = suplementoTipoCafe != null ? suplementoTipoCafe : BigDecimal.ZERO;
    }

    public Integer getIdIngredienteTipoCafeSeleccionado() {
        return idIngredienteTipoCafeSeleccionado;
    }

    public void setIdIngredienteTipoCafeSeleccionado(Integer idIngredienteTipoCafeSeleccionado) {
        this.idIngredienteTipoCafeSeleccionado = idIngredienteTipoCafeSeleccionado;
    }
}
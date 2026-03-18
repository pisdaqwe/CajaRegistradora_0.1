package dtoS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa un item individual dentro de una venta.
 *
 * Ejemplos: - Latte grande - Croissant - Cappuccino mediano
 *
 * Este objeto agrupa: - producto vendido - precios - IVA - personalización -
 * extras asociados
 */
public class RegistrarVentaItemRequest {

	/**
	 * ID del producto vendido.
	 */
	private int idProducto;

	private String nombreProducto;

	/**
	 * Cantidad vendida.
	 *
	 * En tu TPV actual normalmente será 1 por item, pero lo dejamos preparado por
	 * si en el futuro quieres agrupar cantidades.
	 */
	private int cantidad;

	/**
	 * Precio unitario base del item.
	 */
	private BigDecimal precioUnitario;

	/**
	 * Subtotal final del item. Puede incluir tamaño, extras, etc.
	 */
	private BigDecimal subtotal;

	/**
	 * IVA calculado para este item.
	 */
	private BigDecimal iva;

	/**
	 * Texto o JSON simplificado con la personalización del item.
	 *
	 * Ejemplo: "Sin espuma, extra caliente" o más adelante un JSON si prefieres
	 * guardar estructura.
	 */
	private String descripcionPersonalizacion;

	/**
	 * Extras asociados a este item.
	 */
	private List<RegistrarVentaExtraRequest> extras = new ArrayList<>();

	// =====================================================
	// GETTERS Y SETTERS
	// =====================================================

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
		this.extras = extras;
	}
}
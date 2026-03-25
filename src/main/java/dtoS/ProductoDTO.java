package dtoS;

import java.math.BigDecimal;

public final class ProductoDTO {

	private final int idProducto;
	private final int idSubcategoria;
	private final String nombre;
	private final int orden;
	private BigDecimal ivaPorcentaje;
	private final boolean permiteExtras;
	private final boolean permitePersonalizacion;
	private final boolean permiteStockCantidad;

	public ProductoDTO(int idProducto,
			int idSubcategoria,
			String nombre,
			int orden,
			boolean permiteExtras,
			boolean permitePersonalizacion,
			BigDecimal ivaPorcentaje,
			boolean permiteStockCantidad) {
		this.idProducto = idProducto;
		this.idSubcategoria = idSubcategoria;
		this.nombre = nombre;
		this.orden = orden;
		this.permiteExtras = permiteExtras;
		this.permitePersonalizacion = permitePersonalizacion;
		this.ivaPorcentaje = ivaPorcentaje;
		this.permiteStockCantidad = permiteStockCantidad;
	}

	public int getIdProducto() {
		return idProducto;
	}

	public int getIdSubcategoria() {
		return idSubcategoria;
	}

	public String getNombre() {
		return nombre;
	}

	public int getOrden() {
		return orden;
	}

	public BigDecimal getIvaPorcentaje() {
		return ivaPorcentaje;
	}

	public void setIvaPorcentaje(BigDecimal ivaPorcentaje) {
		this.ivaPorcentaje = ivaPorcentaje;
	}

	public boolean isPermiteExtras() {
		return permiteExtras;
	}

	public boolean isPermitePersonalizacion() {
		return permitePersonalizacion;
	}

	public boolean isPermiteStockCantidad() {
		return permiteStockCantidad;
	}
}
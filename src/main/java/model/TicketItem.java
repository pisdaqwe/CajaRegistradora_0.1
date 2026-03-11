package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dtoS.ExtraDTO;
import dtoS.PersonalizacionDTO;
import dtoS.ProductoDTO;
import dtoS.TamanoDTO;

/**
 * Un item del ticket: producto + tamaño + precio base + extras +
 * personalizaciones. - Extras: repetibles (lista), salvo cuando la lógica use
 * replace por tipo - Personalizaciones: no repetibles (LinkedHashMap por id
 * para toggle)
 */
public final class TicketItem {

	private final ProductoDTO producto;

	private TamanoDTO tamano;
	private BigDecimal precioBase; // precio del tamaño

	private final List<TicketExtra> extras = new ArrayList<>();
	private final LinkedHashMap<Integer, TicketPersonalizacion> personalizaciones = new LinkedHashMap<>();
	private final List<String> askMes = new ArrayList<>();

	public TicketItem(ProductoDTO producto, TamanoDTO tamano, BigDecimal precioBase) {
		this.producto = Objects.requireNonNull(producto, "producto no puede ser null");
		this.tamano = Objects.requireNonNull(tamano, "tamano no puede ser null");

		this.precioBase = Objects.requireNonNull(precioBase, "precioBase no puede ser null");
		if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("precioBase no puede ser negativo");
		}
	}

	// =========================
	// GETTERS
	// =========================

	public ProductoDTO getProducto() {
		return producto;
	}

	public TamanoDTO getTamano() {
		return tamano;
	}

	public BigDecimal getPrecioBase() {
		return precioBase;
	}

	public List<TicketExtra> getExtras() {
		return extras;
	}

	public Map<Integer, TicketPersonalizacion> getPersonalizaciones() {
		return personalizaciones;
	}

	public List<String> getAskMes() {
		return askMes;
	}

	// =========================
	// OPERACIONES
	// =========================

	public void setTamano(TamanoDTO nuevoTamano, BigDecimal nuevoPrecioBase) {
		this.tamano = Objects.requireNonNull(nuevoTamano, "nuevoTamano no puede ser null");

		this.precioBase = Objects.requireNonNull(nuevoPrecioBase, "nuevoPrecioBase no puede ser null");
		if (this.precioBase.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("nuevoPrecioBase no puede ser negativo");
		}
	}

	/**
	 * Añade un extra al item. Para tipos repetibles (SHOT, SYRUP, TOPPING) esta
	 * operación vale directamente. Para tipos exclusivos como MILK, se usará
	 * replaceExtraByTipo(...) desde fuera.
	 */
	public void addExtra(ExtraDTO extraDto) {
		Objects.requireNonNull(extraDto, "extraDto no puede ser null");

		extras.add(
				new TicketExtra(extraDto.getIdExtra(), extraDto.getNombre(), extraDto.getTipo(), extraDto.getPrecio()));
	}

	public void addAskMe(String text) {
		if (text == null) {
			return;
		}

		String normalized = text.trim();
		if (normalized.isEmpty()) {
			return;
		}

		askMes.add(normalized);
	}

	public void removeAskMeByIndex(int askMeIndex) {
		if (askMeIndex < 0 || askMeIndex >= askMes.size()) {
			throw new IndexOutOfBoundsException("askMeIndex fuera de rango: " + askMeIndex);
		}
		askMes.remove(askMeIndex);
	}

	public void clearAskMes() {
		askMes.clear();
	}

	public boolean hasAskMes() {
		return !askMes.isEmpty();
	}

	public void removeExtraByIndex(int extraIndex) {
		if (extraIndex < 0 || extraIndex >= extras.size()) {
			throw new IndexOutOfBoundsException("extraIndex fuera de rango: " + extraIndex);
		}
		extras.remove(extraIndex);
	}

	/**
	 * Elimina todos los extras de un tipo concreto. Ejemplo típico: borrar la leche
	 * actual antes de poner otra.
	 */
	public void removeExtrasByTipo(String tipo) {
		Objects.requireNonNull(tipo, "tipo no puede ser null");
		extras.removeIf(e -> tipo.equalsIgnoreCase(e.getTipo()));
	}

	/**
	 * Reemplaza el extra de un tipo por el nuevo. Muy útil para MILK: solo debe
	 * haber una leche activa por item.
	 */
	public void replaceExtraByTipo(ExtraDTO extraDto) {
		Objects.requireNonNull(extraDto, "extraDto no puede ser null");
		removeExtrasByTipo(extraDto.getTipo());
		addExtra(extraDto);
	}

	/**
	 * Permite saber si un extra concreto ya está aplicado. Puede servir para evitar
	 * duplicados en algunos casos.
	 */
	public boolean hasExtraById(int idExtra) {
		return extras.stream().anyMatch(e -> e.getIdExtra() == idExtra);
	}

	public boolean togglePersonalizacion(PersonalizacionDTO pDto) {
		Objects.requireNonNull(pDto, "pDto no puede ser null");
		int id = pDto.getIdPersonalizacion();

		if (personalizaciones.containsKey(id)) {
			personalizaciones.remove(id);
			return false; // desactivada
		}

		personalizaciones.put(id, new TicketPersonalizacion(pDto.getIdPersonalizacion(), pDto.getNombre(),
				pDto.getTipo(), pDto.getPrecio()));

		return true; // activada
	}

	// =========================
	// TOTALES
	// =========================

	public BigDecimal getSubtotal() {
		BigDecimal total = precioBase;

		for (TicketExtra e : extras) {
			total = total.add(e.getPrecio());
		}

		for (TicketPersonalizacion p : personalizaciones.values()) {
			total = total.add(p.getPrecio());
		}

		return total;
	}
}
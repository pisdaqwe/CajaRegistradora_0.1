package service;

import dao.VentaRegistroDao;
import dtoS.RegistrarVentaRequest;
import dtoS.RegistrarVentaResultDTO;

import java.math.BigDecimal;

/**
 * Servicio de negocio para registrar una venta.
 *
 * En esta arquitectura: - el Service valida reglas de negocio - el DAO se
 * encarga de abrir la conexión y ejecutar toda la transacción completa
 */
public class VentaService {

	private final VentaRegistroDao ventaRegistroDao;

	public VentaService(VentaRegistroDao ventaRegistroDao) {
		this.ventaRegistroDao = ventaRegistroDao;
	}

	/**
	 * Registra una venta completa.
	 */
	public RegistrarVentaResultDTO registrarVenta(RegistrarVentaRequest request) {
		validarRequest(request);
		return ventaRegistroDao.registrarVentaCompleta(request);
	}

	// =====================================================
	// VALIDACIÓN
	// =====================================================

	private void validarRequest(RegistrarVentaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El request de venta no puede ser null.");
		}

		if (request.getIdSesion() <= 0) {
			throw new IllegalArgumentException("La venta requiere una sesión de caja válida.");
		}

		if (request.getIdUsuario() <= 0) {
			throw new IllegalArgumentException("La venta requiere un usuario válido.");
		}

		if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El total de la venta debe ser mayor que 0.");
		}

		if (request.getMetodoPago() == null) {
			throw new IllegalArgumentException("Debe indicarse un método de pago.");
		}

		if (request.getMontoPagado() == null || request.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El monto pagado debe ser mayor que 0.");
		}

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new IllegalArgumentException("La venta debe tener al menos un item.");
		}
		if (request.getMontoPagado().compareTo(request.getTotal()) < 0) {
			throw new IllegalArgumentException("El monto pagado no puede ser menor que el total.");
		}
	}
}

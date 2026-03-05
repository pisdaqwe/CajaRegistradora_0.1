package dtoS;

import java.util.Objects;

/**
 * Referencia ligera a la sesión de caja activa para el usuario actual.
 * Se guarda en AppContext y se usa en Ventas para:
 * - Saber qué id_sesion usar al registrar una venta
 * - Mostrar el nombre de la caja (FOODTRUCK, BARRA, etc.)
 * - Tener idSucursal para futuras funciones (stock, disponibilidad, etc.)
 */
public final class SesionCajaRefDTO {

    private final int idSesion;
    private final int idCaja;
    private final int idSucursal;
    private final String nombreCaja;

    public SesionCajaRefDTO(int idSesion, int idCaja, int idSucursal, String nombreCaja) {
        if (idSesion <= 0) throw new IllegalArgumentException("idSesion debe ser > 0");
        if (idCaja <= 0) throw new IllegalArgumentException("idCaja debe ser > 0");
        if (idSucursal <= 0) throw new IllegalArgumentException("idSucursal debe ser > 0");

        this.idSesion = idSesion;
        this.idCaja = idCaja;
        this.idSucursal = idSucursal;
        this.nombreCaja = Objects.requireNonNull(nombreCaja, "nombreCaja no puede ser null").trim();
        if (this.nombreCaja.isEmpty()) {
            throw new IllegalArgumentException("nombreCaja no puede estar vacío");
        }
    }

    public int getIdSesion() {
        return idSesion;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    @Override
    public String toString() {
        return "SesionCajaRefDTO{idSesion=" + idSesion
                + ", idCaja=" + idCaja
                + ", idSucursal=" + idSucursal
                + ", nombreCaja='" + nombreCaja + "'}";
    }
}

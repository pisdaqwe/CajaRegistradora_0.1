package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * Request principal del caso de uso "registrar merma".
 *
 * Representa la cabecera completa de una merma y sus líneas.
 *
 * Sirve para:
 * - insertar la cabecera en tabla merma
 * - insertar las líneas en merma_item
 * - decidir qué stock debe descontarse
 * - registrar movimientos de stock asociados
 *
 * IMPORTANTE:
 * - no representa una venta
 * - no hay pago
 * - no hay cobro
 * - no hay ticket comercial
 */
public class MermaRequest {

    /**
     * Sucursal donde se registra la merma.
     */
    private int idSucursal;

    /**
     * Usuario que declara la merma.
     */
    private int idUsuario;

    /**
     * Tipo general de merma.
     *
     * Ejemplos:
     * - PRODUCTO_TERMINADO
     * - PRODUCTO_RETAIL
     * - INGREDIENTE_SUELTO
     */
    private String tipoMerma;

    /**
     * Origen general de la merma.
     *
     * Ejemplos:
     * - VENTAS
     * - COCINA
     * - ALMACEN
     * - CADUCIDAD
     */
    private String origen;

    /**
     * Motivo corto de la merma.
     *
     * Ejemplos:
     * - Error de preparación
     * - Producto roto
     * - Caducado
     */
    private String motivo;

    /**
     * Observaciones libres.
     */
    private String observaciones;

    /**
     * Líneas de merma.
     */
    private List<MermaItemRequest> items = new ArrayList<>();

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoMerma() {
        return tipoMerma;
    }

    public void setTipoMerma(String tipoMerma) {
        this.tipoMerma = tipoMerma;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<MermaItemRequest> getItems() {
        return items;
    }

    public void setItems(List<MermaItemRequest> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
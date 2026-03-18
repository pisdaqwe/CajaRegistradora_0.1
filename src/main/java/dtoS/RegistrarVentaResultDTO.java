package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * RESULTADO FINAL DE LA VENTA.
 *
 * Devuelve:
 * - id de la venta
 * - id del pago
 * - id del ticket_json
 * - lista de items ya guardados en venta_item
 *
 * Con esto luego podremos:
 * - generar cola de impresión
 * - reimprimir
 * - auditar
 * - seguir ampliando lógica
 */
public class RegistrarVentaResultDTO {

    /**
     * ID generado de la venta.
     */
    private int idVenta;

    /**
     * ID generado del pago.
     */
    private int idPago;

    /**
     * ID generado del ticket JSON.
     */
    private int idTicketJson;

    /**
     * Lista de items ya insertados realmente en venta_item.
     */
    private List<RegistrarVentaItemResultDTO> itemsPersistidos = new ArrayList<>();

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdTicketJson() {
        return idTicketJson;
    }

    public void setIdTicketJson(int idTicketJson) {
        this.idTicketJson = idTicketJson;
    }

    public List<RegistrarVentaItemResultDTO> getItemsPersistidos() {
        return itemsPersistidos;
    }

    public void setItemsPersistidos(List<RegistrarVentaItemResultDTO> itemsPersistidos) {
        this.itemsPersistidos = itemsPersistidos != null ? itemsPersistidos : new ArrayList<>();
    }
}
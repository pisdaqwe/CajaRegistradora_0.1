package dtoS;

/**
 * DTO de una línea seleccionada para devolución.
 *
 * La UI solo indica:
 * - qué línea original quiere devolver
 * - cuánta cantidad
 * - si repone stock
 *
 * Los importes reales se recalcularán en Service
 * usando la venta original persistida.
 */
public class RegistrarDevolucionItemRequest {

    private int idVentaItem;
    private int cantidadADevolver;
    private boolean reponeStock;

    public int getIdVentaItem() {
        return idVentaItem;
    }

    public void setIdVentaItem(int idVentaItem) {
        this.idVentaItem = idVentaItem;
    }

    public int getCantidadADevolver() {
        return cantidadADevolver;
    }

    public void setCantidadADevolver(int cantidadADevolver) {
        this.cantidadADevolver = cantidadADevolver;
    }

    public boolean isReponeStock() {
        return reponeStock;
    }

    public void setReponeStock(boolean reponeStock) {
        this.reponeStock = reponeStock;
    }
}
package dtoS;

/**
 * ITEM GUARDADO EN BD.
 *
 * Esta clase representa una línea ya insertada en venta_item.
 *
 * Sirve para devolver al caller:
 * - el id real generado en venta_item
 * - el id del producto de esa línea
 *
 * Esto luego se usará para:
 * - cola de impresión
 * - stock
 * - reimpresión
 */
public class RegistrarVentaItemResultDTO {

    /**
     * ID real generado en la tabla venta_item.
     */
    private int idItem;

    /**
     * ID del producto asociado a esa línea.
     */
    private int idProducto;

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}

package dtoS;

/**
 * Resultado de una línea individual de merma ya persistida.
 *
 * Sirve para devolver a la capa UI qué línea se insertó
 * y con qué identificador quedó guardada.
 *
 * También deja preparado el camino para futuras ampliaciones,
 * por ejemplo:
 * - ids de movimientos generados
 * - resultado por línea
 * - errores parciales si algún día se permitiesen
 */
public class MermaItemResultDTO {

    /**
     * ID real de la línea insertada en merma_item.
     */
    private int idMermaItem;

    /**
     * Producto asociado a la línea insertada.
     */
    private int idProducto;

    public int getIdMermaItem() {
        return idMermaItem;
    }

    public void setIdMermaItem(int idMermaItem) {
        this.idMermaItem = idMermaItem;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
}
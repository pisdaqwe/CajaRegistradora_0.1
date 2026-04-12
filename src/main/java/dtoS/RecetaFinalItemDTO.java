package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la receta final de un item del ticket
 * ya resuelta según:
 * - receta base
 * - café elegido
 * - extras
 * - personalizaciones
 *
 * Esto es lo que luego servirá para descontar stock.
 */
public class RecetaFinalItemDTO {

    /**
     * Producto del item.
     */
    private int idProducto;

    /**
     * Nombre visible del producto.
     */
    private String nombreProducto;

    /**
     * Tamaño del item.
     */
    private int idTamano;

    /**
     * Nombre visible del tamaño.
     */
    private String nombreTamano;

    /**
     * Ingredientes finales reales que se van a consumir.
     */
    private List<IngredienteConsumidoDTO> ingredientesConsumidos;

    public RecetaFinalItemDTO() {
        this.ingredientesConsumidos = new ArrayList<>();
    }

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

    public int getIdTamano() {
        return idTamano;
    }

    public void setIdTamano(int idTamano) {
        this.idTamano = idTamano;
    }

    public String getNombreTamano() {
        return nombreTamano;
    }

    public void setNombreTamano(String nombreTamano) {
        this.nombreTamano = nombreTamano;
    }

    public List<IngredienteConsumidoDTO> getIngredientesConsumidos() {
        return ingredientesConsumidos;
    }

    public void setIngredientesConsumidos(List<IngredienteConsumidoDTO> ingredientesConsumidos) {
        this.ingredientesConsumidos = ingredientesConsumidos != null
                ? ingredientesConsumidos
                : new ArrayList<>();
    }
}

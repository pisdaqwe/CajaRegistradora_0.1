package dtoS;

import java.math.BigDecimal;

/**
 * Una línea de la receta base de un producto+tamaño.
 *
 * Ejemplo:
 * Latte Grande ->
 * - Espresso Base / 2 / SHOT
 * - Leche Entera / 300 / ML
 */
public class RecetaIngredienteDTO {

    /**
     * Producto al que pertenece esta línea de receta.
     */
    private int idProducto;

    /**
     * Tamaño al que pertenece esta línea de receta.
     */
    private int idTamano;

    /**
     * Ingrediente real de la receta.
     */
    private int idIngrediente;

    /**
     * Nombre visible del ingrediente.
     */
    private String nombreIngrediente;

    /**
     * Cantidad usada de ese ingrediente.
     */
    private BigDecimal cantidad;

    /**
     * Unidad de medida de la cantidad.
     */
    private int idUnidad;

    /**
     * Nombre visible de la unidad.
     * Ejemplo: ML, G, SHOT, UD
     */
    private String nombreUnidad;

    public RecetaIngredienteDTO() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdTamano() {
        return idTamano;
    }

    public void setIdTamano(int idTamano) {
        this.idTamano = idTamano;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNombreIngrediente() {
        return nombreIngrediente;
    }

    public void setNombreIngrediente(String nombreIngrediente) {
        this.nombreIngrediente = nombreIngrediente;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getNombreUnidad() {
        return nombreUnidad;
    }

    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }

    @Override
    public String toString() {
        return "RecetaIngredienteDTO{" +
                "idProducto=" + idProducto +
                ", idTamano=" + idTamano +
                ", idIngrediente=" + idIngrediente +
                ", nombreIngrediente='" + nombreIngrediente + '\'' +
                ", cantidad=" + cantidad +
                ", idUnidad=" + idUnidad +
                ", nombreUnidad='" + nombreUnidad + '\'' +
                '}';
    }
}
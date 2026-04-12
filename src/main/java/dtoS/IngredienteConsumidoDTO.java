package dtoS;

import java.math.BigDecimal;

/**
 * Representa un ingrediente final que se va a consumir
 * realmente al preparar un item del ticket.
 *
 * Esta clase ya NO representa la receta base.
 * Representa el resultado final después de aplicar:
 * - café elegido
 * - extras
 * - personalizaciones
 */
public class IngredienteConsumidoDTO {

    /**
     * Ingrediente real que se va a descontar del stock.
     */
    private int idIngrediente;

    /**
     * Nombre visible del ingrediente.
     */
    private String nombreIngrediente;

    /**
     * Cantidad final que se va a consumir.
     */
    private BigDecimal cantidad;

    /**
     * Unidad de esa cantidad.
     */
    private int idUnidad;

    /**
     * Nombre visible de la unidad.
     */
    private String nombreUnidad;

    public IngredienteConsumidoDTO() {
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
        return "IngredienteConsumidoDTO{" +
                "idIngrediente=" + idIngrediente +
                ", nombreIngrediente='" + nombreIngrediente + '\'' +
                ", cantidad=" + cantidad +
                ", idUnidad=" + idUnidad +
                ", nombreUnidad='" + nombreUnidad + '\'' +
                '}';
    }
}

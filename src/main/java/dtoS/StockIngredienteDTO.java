package dtoS;

import java.math.BigDecimal;

/**
 * Stock actual de un ingrediente en una sucursal.
 */
public class StockIngredienteDTO {

    private int idSucursal;
    private int idIngrediente;
    private String nombreIngrediente;

    /**
     * Cantidad actual disponible.
     */
    private BigDecimal stock;

    /**
     * Unidad base del ingrediente.
     */
    private Integer idUnidadBase;
    private String nombreUnidadBase;

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
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

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public Integer getIdUnidadBase() {
        return idUnidadBase;
    }

    public void setIdUnidadBase(Integer idUnidadBase) {
        this.idUnidadBase = idUnidadBase;
    }

    public String getNombreUnidadBase() {
        return nombreUnidadBase;
    }

    public void setNombreUnidadBase(String nombreUnidadBase) {
        this.nombreUnidadBase = nombreUnidadBase;
    }
}

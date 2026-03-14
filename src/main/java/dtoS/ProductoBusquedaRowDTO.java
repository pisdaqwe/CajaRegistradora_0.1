package dtoS;

import java.math.BigDecimal;

public final class ProductoBusquedaRowDTO {

    private final int idProducto;
    private final int idSubcategoria;
    private final String nombreProducto;

    private final boolean permiteExtras;
    private final boolean permitePersonalizacion;

    private final int idTamano;
    private final String nombreTamano;
    private final BigDecimal precio;

    public ProductoBusquedaRowDTO(
            int idProducto,
            int idSubcategoria,
            String nombreProducto,
            boolean permiteExtras,
            boolean permitePersonalizacion,
            int idTamano,
            String nombreTamano,
            BigDecimal precio
    ) {
        this.idProducto = idProducto;
        this.idSubcategoria = idSubcategoria;
        this.nombreProducto = nombreProducto;
        this.permiteExtras = permiteExtras;
        this.permitePersonalizacion = permitePersonalizacion;
        this.idTamano = idTamano;
        this.nombreTamano = nombreTamano;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdSubcategoria() {
        return idSubcategoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public boolean isPermiteExtras() {
        return permiteExtras;
    }

    public boolean isPermitePersonalizacion() {
        return permitePersonalizacion;
    }

    public int getIdTamano() {
        return idTamano;
    }

    public String getNombreTamano() {
        return nombreTamano;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}

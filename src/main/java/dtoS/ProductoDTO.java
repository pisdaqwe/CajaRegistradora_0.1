package dtoS;

public final class ProductoDTO {

    private final int idProducto;
    private final int idSubcategoria;
    private final String nombre;
    private final int orden;

    private final boolean permiteExtras;
    private final boolean permitePersonalizacion;

    public ProductoDTO(int idProducto, int idSubcategoria, String nombre, int orden,
                       boolean permiteExtras, boolean permitePersonalizacion) {
        this.idProducto = idProducto;
        this.idSubcategoria = idSubcategoria;
        this.nombre = nombre;
        this.orden = orden;
        this.permiteExtras = permiteExtras;
        this.permitePersonalizacion = permitePersonalizacion;
    }

    public int getIdProducto() { return idProducto; }
    public int getIdSubcategoria() { return idSubcategoria; }
    public String getNombre() { return nombre; }
    public int getOrden() { return orden; }

    public boolean isPermiteExtras() { return permiteExtras; }
    public boolean isPermitePersonalizacion() { return permitePersonalizacion; }
}
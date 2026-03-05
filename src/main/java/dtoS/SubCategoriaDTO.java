package dtoS;

public final class SubCategoriaDTO {

    private final int idSubcategoria;
    private final int idCategoria;
    private final String nombre;
    private final int orden;

    public SubCategoriaDTO(int idSubcategoria, int idCategoria, String nombre, int orden) {
        this.idSubcategoria = idSubcategoria;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.orden = orden;
    }

    public int getIdSubcategoria() { return idSubcategoria; }
    public int getIdCategoria() { return idCategoria; }
    public String getNombre() { return nombre; }
    public int getOrden() { return orden; }
}
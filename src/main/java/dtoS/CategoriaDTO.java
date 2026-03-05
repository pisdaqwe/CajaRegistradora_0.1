package dtoS;

public final class CategoriaDTO {

    private final int idCategoria;
    private final String nombre;
    private final String colorHex;
    private final int orden;

    public CategoriaDTO(int idCategoria, String nombre, String colorHex, int orden) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.colorHex = colorHex;
        this.orden = orden;
    }

    public int getIdCategoria() { return idCategoria; }
    public String getNombre() { return nombre; }
    public String getColorHex() { return colorHex; }
    public int getOrden() { return orden; }
}
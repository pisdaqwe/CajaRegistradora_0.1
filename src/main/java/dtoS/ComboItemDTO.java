package dtoS;

import java.util.Objects;

public class ComboItemDTO {

    private int idCombo;
    private int idProducto;
    private int cantidad;
    private int orden;

    public ComboItemDTO() {
    }

    public ComboItemDTO(int idCombo, int idProducto, int cantidad, int orden) {
        this.idCombo = idCombo;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.orden = orden;
    }

    public int getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    @Override
    public String toString() {
        return "ComboItemDTO{" +
                "idCombo=" + idCombo +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", orden=" + orden +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComboItemDTO other)) return false;
        return idCombo == other.idCombo
                && idProducto == other.idProducto;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCombo, idProducto);
    }
}

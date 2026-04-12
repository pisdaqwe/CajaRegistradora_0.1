package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa el snapshot lógico del item
 * que se guarda dentro de cola_impresion.descripcion.
 *
 * Recomendación:
 * serializar esta clase a JSON String.
 *
 * AJUSTE ACTUAL:
 * - añade tipoCafe para que el monitor/mini-ticket de estación
 *   pueda mostrar el café seleccionado.
 */
public class ColaItemDescripcionDTO {

    private String nombrePedido;
    private String tipoServicio;

    private String producto;
    private String tamano;
    private Integer cantidad;

    /**
     * NUEVO:
     * nombre visible del café seleccionado.
     *
     * Ejemplo:
     * - Espresso
     * - Espresso Decaf
     * - Espresso Colombia Campaña
     */
    private String tipoCafe;

    private List<String> extras;
    private List<String> personalizaciones;
    private List<String> askMe;

    public ColaItemDescripcionDTO() {
        this.extras = new ArrayList<>();
        this.personalizaciones = new ArrayList<>();
        this.askMe = new ArrayList<>();
    }

    public ColaItemDescripcionDTO(String nombrePedido,
                                  String tipoServicio,
                                  String producto,
                                  String tamano,
                                  Integer cantidad,
                                  String tipoCafe,
                                  List<String> extras,
                                  List<String> personalizaciones,
                                  List<String> askMe) {
        this.nombrePedido = nombrePedido;
        this.tipoServicio = tipoServicio;
        this.producto = producto;
        this.tamano = tamano;
        this.cantidad = cantidad;
        this.tipoCafe = tipoCafe;
        this.extras = extras != null ? extras : new ArrayList<>();
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
        this.askMe = askMe != null ? askMe : new ArrayList<>();
    }

    public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = nombrePedido;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipoCafe() {
        return tipoCafe;
    }

    public void setTipoCafe(String tipoCafe) {
        this.tipoCafe = tipoCafe;
    }

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras != null ? extras : new ArrayList<>();
    }

    public List<String> getPersonalizaciones() {
        return personalizaciones;
    }

    public void setPersonalizaciones(List<String> personalizaciones) {
        this.personalizaciones = personalizaciones != null ? personalizaciones : new ArrayList<>();
    }

    public List<String> getAskMe() {
        return askMe;
    }

    public void setAskMe(List<String> askMe) {
        this.askMe = askMe != null ? askMe : new ArrayList<>();
    }

    public void addExtra(String extra) {
        if (extra != null && !extra.isBlank()) {
            this.extras.add(extra);
        }
    }

    public void addPersonalizacion(String personalizacion) {
        if (personalizacion != null && !personalizacion.isBlank()) {
            this.personalizaciones.add(personalizacion);
        }
    }

    public void addAskMe(String texto) {
        if (texto != null && !texto.isBlank()) {
            this.askMe.add(texto);
        }
    }

    @Override
    public String toString() {
        return "ColaItemDescripcionDTO{" +
                "nombrePedido='" + nombrePedido + '\'' +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", producto='" + producto + '\'' +
                ", tamano='" + tamano + '\'' +
                ", cantidad=" + cantidad +
                ", tipoCafe='" + tipoCafe + '\'' +
                ", extras=" + extras +
                ", personalizaciones=" + personalizaciones +
                ", askMe=" + askMe +
                '}';
    }
}
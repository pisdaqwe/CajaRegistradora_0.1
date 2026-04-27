package dtoS;

public class CajaTerminalOptionDTO {

    private int idCaja;
    private String nombreCaja;
    private String ubicacion;
    private String estado;
    private boolean activa;

    private int idSucursal;
    private String nombreSucursal;

    private boolean seleccionadaActual;

    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public boolean isSeleccionadaActual() {
        return seleccionadaActual;
    }

    public void setSeleccionadaActual(boolean seleccionadaActual) {
        this.seleccionadaActual = seleccionadaActual;
    }

    @Override
    public String toString() {
        return nombreCaja + " · " + nombreSucursal + " · " + estado;
    }
}

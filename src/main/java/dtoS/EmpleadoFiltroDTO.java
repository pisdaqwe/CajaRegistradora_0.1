package dtoS;

/**
 * DTO de filtros para la búsqueda/listado de empleados.
 *
 * Responsabilidades:
 * - Transportar los filtros seleccionados en la UI.
 * - Ofrecer helpers simples para saber si un filtro está activo.
 *
 * NO debe:
 * - Contener lógica de acceso a datos.
 * - Validar reglas de negocio complejas.
 */
public class EmpleadoFiltroDTO {

    private String textoBusqueda;
    private Integer idRol;
    private Boolean soloActivos;
    private Integer idSucursal;
    private Boolean soloConFichajeAbierto;
    private Boolean soloConCajaAbierta;

    public EmpleadoFiltroDTO() {
    }

    public EmpleadoFiltroDTO(String textoBusqueda,
                             Integer idRol,
                             Boolean soloActivos,
                             Integer idSucursal,
                             Boolean soloConFichajeAbierto,
                             Boolean soloConCajaAbierta) {
        this.textoBusqueda = textoBusqueda;
        this.idRol = idRol;
        this.soloActivos = soloActivos;
        this.idSucursal = idSucursal;
        this.soloConFichajeAbierto = soloConFichajeAbierto;
        this.soloConCajaAbierta = soloConCajaAbierta;
    }

    public String getTextoBusqueda() {
        return textoBusqueda;
    }

    public void setTextoBusqueda(String textoBusqueda) {
        this.textoBusqueda = textoBusqueda;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Boolean getSoloActivos() {
        return soloActivos;
    }

    public void setSoloActivos(Boolean soloActivos) {
        this.soloActivos = soloActivos;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Boolean getSoloConFichajeAbierto() {
        return soloConFichajeAbierto;
    }

    public void setSoloConFichajeAbierto(Boolean soloConFichajeAbierto) {
        this.soloConFichajeAbierto = soloConFichajeAbierto;
    }

    public Boolean getSoloConCajaAbierta() {
        return soloConCajaAbierta;
    }

    public void setSoloConCajaAbierta(Boolean soloConCajaAbierta) {
        this.soloConCajaAbierta = soloConCajaAbierta;
    }

    public boolean hasTextoBusqueda() {
        return textoBusqueda != null && !textoBusqueda.trim().isEmpty();
    }

    public boolean filtraPorRol() {
        return idRol != null;
    }

    public boolean filtraPorSucursal() {
        return idSucursal != null;
    }

    public boolean filtraSoloActivos() {
        return Boolean.TRUE.equals(soloActivos);
    }

    public boolean filtraSoloInactivos() {
        return Boolean.FALSE.equals(soloActivos);
    }

    public boolean filtraPorFichajeAbierto() {
        return Boolean.TRUE.equals(soloConFichajeAbierto);
    }

    public boolean filtraPorCajaAbierta() {
        return Boolean.TRUE.equals(soloConCajaAbierta);
    }

    @Override
    public String toString() {
        return "EmpleadoFiltroDTO{" +
                "textoBusqueda='" + textoBusqueda + '\'' +
                ", idRol=" + idRol +
                ", soloActivos=" + soloActivos +
                ", idSucursal=" + idSucursal +
                ", soloConFichajeAbierto=" + soloConFichajeAbierto +
                ", soloConCajaAbierta=" + soloConCajaAbierta +
                '}';
    }
}

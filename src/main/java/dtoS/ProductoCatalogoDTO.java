package dtoS;

import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;

public final class ProductoCatalogoDTO {

    private final int idProducto;
    private final int idSubcategoria;
    private final String nombre;
    private final int orden;

    private final BigDecimal ivaPorcentaje;

    private final boolean permiteExtras;
    private final boolean permitePersonalizacion;
    private final boolean permiteStockCantidad;

    private final ModoDisponibilidadProducto modoDisponibilidad;
    private final BigDecimal stockActual;

    public ProductoCatalogoDTO(
            int idProducto,
            int idSubcategoria,
            String nombre,
            int orden,
            BigDecimal ivaPorcentaje,
            boolean permiteExtras,
            boolean permitePersonalizacion,
            boolean permiteStockCantidad,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stockActual
    ) {
        this.idProducto = idProducto;
        this.idSubcategoria = idSubcategoria;
        this.nombre = nombre;
        this.orden = orden;
        this.ivaPorcentaje = ivaPorcentaje;
        this.permiteExtras = permiteExtras;
        this.permitePersonalizacion = permitePersonalizacion;
        this.permiteStockCantidad = permiteStockCantidad;
        this.modoDisponibilidad = modoDisponibilidad;
        this.stockActual = stockActual != null ? stockActual : BigDecimal.ZERO;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdSubcategoria() {
        return idSubcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public int getOrden() {
        return orden;
    }

    public BigDecimal getIvaPorcentaje() {
        return ivaPorcentaje;
    }

    public boolean isPermiteExtras() {
        return permiteExtras;
    }

    public boolean isPermitePersonalizacion() {
        return permitePersonalizacion;
    }

    public boolean isPermiteStockCantidad() {
        return permiteStockCantidad;
    }

    public ModoDisponibilidadProducto getModoDisponibilidad() {
        return modoDisponibilidad;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public boolean isDisponible() {
        return modoDisponibilidad != ModoDisponibilidadProducto.NO_DISPONIBLE;
    }

    public boolean isConControlCantidad() {
        return modoDisponibilidad == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD;
    }

    public boolean isAgotado() {
        return isConControlCantidad() && stockActual.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean muestraContador() {
        return isConControlCantidad();
    }

    public boolean isBotonHabilitado() {
        if (modoDisponibilidad == ModoDisponibilidadProducto.NO_DISPONIBLE) {
            return false;
        }

        if (modoDisponibilidad == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD) {
            return stockActual.compareTo(BigDecimal.ZERO) > 0;
        }

        return true;
    }
}

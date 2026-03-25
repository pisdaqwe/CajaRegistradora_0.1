package dtoS;

import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;

public final class StockProductoDisponibilidadDTO {

    private final int idProducto;
    private final int idSubcategoria;
    private final String nombreProducto;
    private final String nombreSubcategoria;

    private final boolean permiteStockCantidad;
    private final ModoDisponibilidadProducto modoDisponibilidad;
    private final BigDecimal stockActual;

    public StockProductoDisponibilidadDTO(
            int idProducto,
            int idSubcategoria,
            String nombreProducto,
            String nombreSubcategoria,
            boolean permiteStockCantidad,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stockActual
    ) {
        this.idProducto = idProducto;
        this.idSubcategoria = idSubcategoria;
        this.nombreProducto = nombreProducto;
        this.nombreSubcategoria = nombreSubcategoria;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getNombreSubcategoria() {
        return nombreSubcategoria;
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

    public String getTextoEstado() {
        if (modoDisponibilidad == ModoDisponibilidadProducto.NO_DISPONIBLE) {
            return "No disponible";
        }

        if (isAgotado()) {
            return "Agotado";
        }

        if (isConControlCantidad()) {
            return "Stock: " + formatStock(stockActual);
        }

        return "Disponible";
    }

    private String formatStock(BigDecimal stock) {
        if (stock == null) {
            return "0";
        }
        return stock.stripTrailingZeros().toPlainString();
    }
}
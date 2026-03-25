package dtoS;

import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;

public final class DisponibilidadItemRowDTO {

    private final String tipoItem; // PRODUCTO | EXTRA

    private final int idItem;
    private final String nombre;
    private final String grupo;

    // Solo producto
    private final boolean permiteStockCantidad;
    private final ModoDisponibilidadProducto modoDisponibilidadProducto;
    private final BigDecimal stockActual;

    // Solo extra
    private final Boolean disponibleExtra;

    private DisponibilidadItemRowDTO(
            String tipoItem,
            int idItem,
            String nombre,
            String grupo,
            boolean permiteStockCantidad,
            ModoDisponibilidadProducto modoDisponibilidadProducto,
            BigDecimal stockActual,
            Boolean disponibleExtra
    ) {
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.nombre = nombre;
        this.grupo = grupo;
        this.permiteStockCantidad = permiteStockCantidad;
        this.modoDisponibilidadProducto = modoDisponibilidadProducto;
        this.stockActual = stockActual != null ? stockActual : BigDecimal.ZERO;
        this.disponibleExtra = disponibleExtra;
    }

    public static DisponibilidadItemRowDTO producto(
            int idProducto,
            String nombreProducto,
            String nombreSubcategoria,
            boolean permiteStockCantidad,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stockActual
    ) {
        return new DisponibilidadItemRowDTO(
                "PRODUCTO",
                idProducto,
                nombreProducto,
                nombreSubcategoria,
                permiteStockCantidad,
                modoDisponibilidad,
                stockActual,
                null
        );
    }

    public static DisponibilidadItemRowDTO extra(
            int idExtra,
            String nombreExtra,
            String tipoExtra,
            boolean disponible
    ) {
        return new DisponibilidadItemRowDTO(
                "EXTRA",
                idExtra,
                nombreExtra,
                tipoExtra,
                false,
                null,
                BigDecimal.ZERO,
                disponible
        );
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public int getIdItem() {
        return idItem;
    }

    public String getNombre() {
        return nombre;
    }

    public String getGrupo() {
        return grupo;
    }

    public boolean isPermiteStockCantidad() {
        return permiteStockCantidad;
    }

    public ModoDisponibilidadProducto getModoDisponibilidadProducto() {
        return modoDisponibilidadProducto;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public Boolean getDisponibleExtra() {
        return disponibleExtra;
    }

    public boolean isProducto() {
        return "PRODUCTO".equals(tipoItem);
    }

    public boolean isExtra() {
        return "EXTRA".equals(tipoItem);
    }

    public boolean isDisponible() {
        if (isProducto()) {
            return modoDisponibilidadProducto != ModoDisponibilidadProducto.NO_DISPONIBLE;
        }
        return Boolean.TRUE.equals(disponibleExtra);
    }

    public boolean isConControlCantidad() {
        return isProducto()
                && modoDisponibilidadProducto == ModoDisponibilidadProducto.DISPONIBLE_CON_CANTIDAD;
    }

    public boolean isAgotado() {
        return isConControlCantidad() && stockActual.compareTo(BigDecimal.ZERO) <= 0;
    }

    public String getTextoModo() {
        if (isProducto()) {
            return switch (modoDisponibilidadProducto) {
                case NO_DISPONIBLE -> "No disponible";
                case DISPONIBLE_SIN_CONTROL -> "Disponible sin control";
                case DISPONIBLE_CON_CANTIDAD -> "Disponible con cantidad";
            };
        }
        return Boolean.TRUE.equals(disponibleExtra) ? "Disponible" : "No disponible";
    }

    public String getTextoStock() {
        if (!isProducto()) {
            return "-";
        }

        if (!isConControlCantidad()) {
            return "-";
        }

        return stockActual.stripTrailingZeros().toPlainString();
    }

    public String getTextoEstado() {
        if (isProducto()) {
            if (modoDisponibilidadProducto == ModoDisponibilidadProducto.NO_DISPONIBLE) {
                return "No disponible";
            }
            if (isAgotado()) {
                return "Agotado";
            }
            if (isConControlCantidad()) {
                return "Stock: " + stockActual.stripTrailingZeros().toPlainString();
            }
            return "Disponible";
        }

        return Boolean.TRUE.equals(disponibleExtra) ? "Disponible" : "No disponible";
    }
}

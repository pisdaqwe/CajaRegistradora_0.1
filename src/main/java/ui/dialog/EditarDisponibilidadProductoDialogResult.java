package ui.dialog;

import enums.ModoDisponibilidadProducto;

import java.math.BigDecimal;

public final class EditarDisponibilidadProductoDialogResult {

    private final boolean confirmed;
    private final ModoDisponibilidadProducto modoDisponibilidad;
    private final BigDecimal stock;

    public EditarDisponibilidadProductoDialogResult(
            boolean confirmed,
            ModoDisponibilidadProducto modoDisponibilidad,
            BigDecimal stock
    ) {
        this.confirmed = confirmed;
        this.modoDisponibilidad = modoDisponibilidad;
        this.stock = stock;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ModoDisponibilidadProducto getModoDisponibilidad() {
        return modoDisponibilidad;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public static EditarDisponibilidadProductoDialogResult cancelled() {
        return new EditarDisponibilidadProductoDialogResult(false, null, null);
    }
}
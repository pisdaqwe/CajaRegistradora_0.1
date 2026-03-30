package ui.dialog;

/**
 * Resultado del diálogo de descuento de empleado.
 */
public class DescuentoEmpleadoDialogResult {

    private final boolean confirmed;
    private final String codigoEmpleado;

    public DescuentoEmpleadoDialogResult(boolean confirmed, String codigoEmpleado) {
        this.confirmed = confirmed;
        this.codigoEmpleado = codigoEmpleado;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }
}

package ui.dialog;

/**
 * Resultado del diálogo de código promocional.
 */
public class CodigoPromocionalDialogResult {

    private final boolean confirmed;
    private final String codigo;

    public CodigoPromocionalDialogResult(boolean confirmed, String codigo) {
        this.confirmed = confirmed;
        this.codigo = codigo;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getCodigo() {
        return codigo;
    }
}

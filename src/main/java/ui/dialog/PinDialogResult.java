package ui.dialog;

public class PinDialogResult {

    private final String usuario; // null en login rápido
    private final String pin;

    public PinDialogResult(String usuario, String pin) {
        this.usuario = usuario;
        this.pin = pin;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPin() {
        return pin;
    }
}


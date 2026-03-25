package ui.dialog;

public final class EditarDisponibilidadExtraDialogResult {

    private final boolean confirmed;
    private final boolean disponible;

    public EditarDisponibilidadExtraDialogResult(boolean confirmed, boolean disponible) {
        this.confirmed = confirmed;
        this.disponible = disponible;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public static EditarDisponibilidadExtraDialogResult cancelled() {
        return new EditarDisponibilidadExtraDialogResult(false, false);
    }
}

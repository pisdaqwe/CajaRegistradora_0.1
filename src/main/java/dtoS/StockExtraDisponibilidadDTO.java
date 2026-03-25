package dtoS;

public final class StockExtraDisponibilidadDTO {

    private final int idExtra;
    private final String nombreExtra;
    private final String tipoExtra;
    private final boolean disponible;

    public StockExtraDisponibilidadDTO(
            int idExtra,
            String nombreExtra,
            String tipoExtra,
            boolean disponible
    ) {
        if (idExtra <= 0) {
            throw new IllegalArgumentException("idExtra debe ser > 0");
        }
        if (nombreExtra == null || nombreExtra.isBlank()) {
            throw new IllegalArgumentException("nombreExtra no puede estar vacío");
        }
        if (tipoExtra == null || tipoExtra.isBlank()) {
            throw new IllegalArgumentException("tipoExtra no puede estar vacío");
        }

        this.idExtra = idExtra;
        this.nombreExtra = nombreExtra.trim();
        this.tipoExtra = tipoExtra.trim();
        this.disponible = disponible;
    }

    public int getIdExtra() {
        return idExtra;
    }

    public String getNombreExtra() {
        return nombreExtra;
    }

    public String getTipoExtra() {
        return tipoExtra;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public String getTextoEstado() {
        return disponible ? "Disponible" : "No disponible";
    }
}
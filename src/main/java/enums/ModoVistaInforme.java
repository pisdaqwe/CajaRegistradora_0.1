package enums;

public enum ModoVistaInforme {
    AGREGADA("Vista agregada"),
    COMPARATIVA("Vista comparativa");

    private final String label;

    ModoVistaInforme(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
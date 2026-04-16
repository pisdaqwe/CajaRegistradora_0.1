package enums;

public enum AgrupacionTemporal {
    HORA("Por hora"),
    DIA("Por día"),
    SEMANA("Por semana"),
    MES("Por mes");

    private final String label;

    AgrupacionTemporal(String label) {
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
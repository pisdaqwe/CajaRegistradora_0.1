package dtoS;

/**
 * Representa un KPI listo para pintar en el PDF.
 *
 * Ejemplos:
 * - Total ventas -> 4.250,30 €
 * - Total tickets -> 431
 * - Ticket medio -> 9,86 €
 *
 * Responsabilidades:
 * - Mantener una etiqueta y un valor ya formateado.
 * - Servir como estructura neutra entre Service y Renderer.
 */
public class InformePdfKpiItem {

    private String label;
    private String value;

    public InformePdfKpiItem() {
    }

    public InformePdfKpiItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
package dtoS;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la tabla ya transformada a un formato neutro
 * listo para ser pintado en PDF.
 *
 * Responsabilidades:
 * - Desacoplar el renderer PDF del JTable/Swing.
 * - Guardar columnas y filas ya preparadas en texto.
 *
 * IMPORTANTE:
 * - Aquí no hay lógica de consulta ni de Swing.
 * - El renderer PDF solo debería "dibujar" esto.
 */
public class InformePdfTableData {

    private String tituloTabla;
    private List<String> columnas = new ArrayList<>();
    private List<List<String>> filas = new ArrayList<>();

    public InformePdfTableData() {
    }

    public String getTituloTabla() {
        return tituloTabla;
    }

    public void setTituloTabla(String tituloTabla) {
        this.tituloTabla = tituloTabla;
    }

    public List<String> getColumnas() {
        return columnas;
    }

    public void setColumnas(List<String> columnas) {
        this.columnas = columnas;
    }

    public List<List<String>> getFilas() {
        return filas;
    }

    public void setFilas(List<List<String>> filas) {
        this.filas = filas;
    }

    public void addFila(List<String> fila) {
        this.filas.add(fila);
    }

    public boolean isEmpty() {
        return filas == null || filas.isEmpty();
    }
}

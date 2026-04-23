package ui.table;

import dtoS.FichajeEmpleadoRowDTO;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel del histórico/listado de fichajes de empleados.
 *
 * Responsabilidades:
 * - Adaptar una lista de FichajeEmpleadoRowDTO a JTable.
 */
public class FichajesEmpleadoTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "ID",
            "Empleado",
            "Sucursal",
            "Entrada",
            "Salida",
            "Duración",
            "Estado"
    };

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<FichajeEmpleadoRowDTO> rows = new ArrayList<>();

    public void setRows(List<FichajeEmpleadoRowDTO> nuevosRows) {
        rows.clear();
        if (nuevosRows != null) {
            rows.addAll(nuevosRows);
        }
        fireTableDataChanged();
    }

    public FichajeEmpleadoRowDTO getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return null;
        }
        return rows.get(rowIndex);
    }

    public List<FichajeEmpleadoRowDTO> getRows() {
        return new ArrayList<>(rows);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FichajeEmpleadoRowDTO row = rows.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return row.getIdFichaje();
            case 1:
                return row.getNombreEmpleado();
            case 2:
                return row.getNombreSucursal();
            case 3:
                return formatDateTime(row.getFechaEntrada());
            case 4:
                return formatDateTime(row.getFechaSalida());
            case 5:
                return row.getDuracionTexto();
            case 6:
                return row.getEstado();
            default:
                return "";
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMATTER);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}

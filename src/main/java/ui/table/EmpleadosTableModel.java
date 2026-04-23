package ui.table;

import dtoS.EmpleadoRowDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel del listado principal de empleados.
 *
 * Responsabilidades:
 * - Adaptar una lista de EmpleadoRowDTO a JTable.
 * - Mantener el listado actual en memoria.
 */
public class EmpleadosTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {
            "ID",
            "Nombre",
            "Usuario",
            "Rol",
            "Sucursal",
            "Activo",
            "Fichado",
            "Caja abierta"
    };

    private final List<EmpleadoRowDTO> rows = new ArrayList<>();

    public void setRows(List<EmpleadoRowDTO> nuevosRows) {
        rows.clear();
        if (nuevosRows != null) {
            rows.addAll(nuevosRows);
        }
        fireTableDataChanged();
    }

    public EmpleadoRowDTO getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return null;
        }
        return rows.get(rowIndex);
    }

    public List<EmpleadoRowDTO> getRows() {
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
        EmpleadoRowDTO row = rows.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return row.getIdUsuario();
            case 1:
                return row.getNombre();
            case 2:
                return row.getUsuario();
            case 3:
                return row.getNombreRol();
            case 4:
                return row.getNombreSucursal();
            case 5:
                return row.getActivoTexto();
            case 6:
                return row.getFichadoTexto();
            case 7:
                return row.getCajaAbiertaTexto();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}

package ui.table;

import dtoS.AuditoriaRowDTO;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaTableModel extends AbstractTableModel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String[] columns = {
            "ID", "Fecha", "Acción", "Usuario", "Sucursal", "Resumen"
    };

    private List<AuditoriaRowDTO> datos = new ArrayList<>();

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AuditoriaRowDTO dto = datos.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> dto.getIdAuditoria();
            case 1 -> dto.getFecha() != null ? dto.getFecha().format(FMT) : "";
            case 2 -> dto.getAccion();
            case 3 -> dto.getNombreUsuario();
            case 4 -> dto.getNombreSucursal();
            case 5 -> dto.getResumenDetalles();
            default -> "";
        };
    }

    public void setDatos(List<AuditoriaRowDTO> nuevosDatos) {
        this.datos = nuevosDatos != null ? nuevosDatos : new ArrayList<>();
        fireTableDataChanged();
    }

    public AuditoriaRowDTO getRow(int row) {
        return datos.get(row);
    }
}
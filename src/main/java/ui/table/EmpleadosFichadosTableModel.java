package ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import dtoS.FichajeActivoDTO;

public class EmpleadosFichadosTableModel extends AbstractTableModel {
	private  final String[] columnNames = {
			"Empleado",
			"Hora entrada",
			"Estado"
	};
	private List<FichajeActivoDTO> datos = new ArrayList<>();

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return datos.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		 FichajeActivoDTO dto = datos.get(rowIndex);

	        return switch (columnIndex) {
	            case 0 -> dto.getNombreEmpleado();
	            case 1 -> dto.getHoraEntrada();
	            case 2 -> dto.getEstado();
	            default -> "";
	        };
	}
	  @Override
	    public String getColumnName(int column) {
	        return columnNames[column];
	    }
	 @Override
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
	        return false; // tabla solo lectura
	    }
	 /**
	     * Reemplaza los datos de la tabla y notifica a la vista.
	     */
	    public void setDatos(List<FichajeActivoDTO> nuevosDatos) {
	        this.datos = nuevosDatos != null ? nuevosDatos : new ArrayList<>();
	        fireTableDataChanged();
	    }

}

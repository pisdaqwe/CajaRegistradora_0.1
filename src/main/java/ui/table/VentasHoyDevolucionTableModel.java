package ui.table;

import dtoS.TicketHoyRowDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentasHoyDevolucionTableModel extends AbstractTableModel {

    public static final int COL_VENTA = 0;
    public static final int COL_FECHA = 1;
    public static final int COL_PEDIDO = 2;
    public static final int COL_PAGO = 3;
    public static final int COL_TOTAL = 4;
    public static final int COL_EMPLEADO = 5;

    private final String[] columns = {
            "Venta",
            "Fecha",
            "Pedido",
            "Pago",
            "Total",
            "Empleado"
    };

    private final List<TicketHoyRowDTO> rows = new ArrayList<>();

    @Override
    public int getRowCount() {
        return rows.size();
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
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COL_VENTA -> Integer.class;
            case COL_FECHA -> LocalDateTime.class;
            case COL_TOTAL -> BigDecimal.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TicketHoyRowDTO row = rows.get(rowIndex);

        return switch (columnIndex) {
            case COL_VENTA -> row.getIdVenta();
            case COL_FECHA -> row.getFechaGeneracion();
            case COL_PEDIDO -> row.getNombrePedido();
            case COL_PAGO -> row.getMetodoPago();
            case COL_TOTAL -> row.getTotal();
            case COL_EMPLEADO -> row.getNombreEmpleado();
            default -> null;
        };
    }

    public void setRows(List<TicketHoyRowDTO> nuevasRows) {
        rows.clear();
        if (nuevasRows != null) {
            rows.addAll(nuevasRows);
        }
        fireTableDataChanged();
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
    }

    public TicketHoyRowDTO getRowAtModel(int rowIndex) {
        return rows.get(rowIndex);
    }
}

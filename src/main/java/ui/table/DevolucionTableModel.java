package ui.table;

import util.I18n;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DevolucionTableModel extends AbstractTableModel {

    public static final int COL_PRODUCTO = 0;
    public static final int COL_TAMANO = 1;
    public static final int COL_VENDIDA = 2;
    public static final int COL_YA_DEVUELTA = 3;
    public static final int COL_DISPONIBLE = 4;
    public static final int COL_BRUTO = 5;
    public static final int COL_DESCUENTO = 6;
    public static final int COL_FINAL = 7;
    public static final int COL_A_DEVOLVER = 8;
    public static final int COL_REPONE_STOCK = 9;

    private final List<DevolucionRowVM> rows = new ArrayList<>();

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 10; }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case COL_PRODUCTO -> I18n.t("refund.table.product");
            case COL_TAMANO -> I18n.t("refund.table.size");
            case COL_VENDIDA -> I18n.t("refund.table.sold");
            case COL_YA_DEVUELTA -> I18n.t("refund.table.alreadyRefunded");
            case COL_DISPONIBLE -> I18n.t("refund.table.available");
            case COL_BRUTO -> I18n.t("refund.table.gross");
            case COL_DESCUENTO -> I18n.t("refund.table.discount");
            case COL_FINAL -> I18n.t("refund.table.final");
            case COL_A_DEVOLVER -> I18n.t("refund.table.toRefund");
            case COL_REPONE_STOCK -> I18n.t("refund.table.restock");
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case COL_VENDIDA, COL_YA_DEVUELTA, COL_DISPONIBLE, COL_A_DEVOLVER -> Integer.class;
            case COL_BRUTO, COL_DESCUENTO, COL_FINAL -> BigDecimal.class;
            case COL_REPONE_STOCK -> Boolean.class;
            default -> String.class;
        };
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DevolucionRowVM row = rows.get(rowIndex);
        return switch (columnIndex) {
            case COL_PRODUCTO -> row.getNombreProducto();
            case COL_TAMANO -> row.getTamano();
            case COL_VENDIDA -> row.getCantidadVendida();
            case COL_YA_DEVUELTA -> row.getCantidadYaDevuelta();
            case COL_DISPONIBLE -> row.getCantidadDisponible();
            case COL_BRUTO -> safe(row.getSubtotalBruto());
            case COL_DESCUENTO -> safe(row.getImporteDescuentoLinea());
            case COL_FINAL -> safe(row.getSubtotalFinal());
            case COL_A_DEVOLVER -> row.getCantidadADevolver();
            case COL_REPONE_STOCK -> row.isReponeStock();
            default -> null;
        };
    }

    public void setRows(List<DevolucionRowVM> nuevasRows) {
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

    public int size() { return rows.size(); }
    public DevolucionRowVM getRowAt(int modelRow) { return rows.get(modelRow); }
    public void updateRow(int modelRow) { fireTableRowsUpdated(modelRow, modelRow); }

    public List<DevolucionRowVM> getSelectedForReturn() {
        List<DevolucionRowVM> result = new ArrayList<>();
        for (DevolucionRowVM row : rows) {
            if (row.getCantidadADevolver() > 0) {
                result.add(row);
            }
        }
        return result;
    }

    private BigDecimal safe(BigDecimal value) { return value != null ? value : BigDecimal.ZERO; }
}

package dao;

import dtoS.VentaItemParaDevolucionDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO de detalle de devoluciones.
 *
 * Responsabilidades:
 * - insertar líneas devueltas en devolucion_item
 */
public class DevolucionItemDao {

    /**
     * Inserta una línea devuelta ya calculada.
     */
    public void insertItem(
            Connection con,
            int idDevolucion,
            VentaItemParaDevolucionDTO lineaOriginal,
            int cantidadDevuelta,
            boolean reponeStock
    ) throws SQLException {

        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null.");
        }
        if (idDevolucion <= 0) {
            throw new IllegalArgumentException("idDevolucion debe ser > 0.");
        }
        if (lineaOriginal == null) {
            throw new IllegalArgumentException("lineaOriginal no puede ser null.");
        }
        if (cantidadDevuelta <= 0) {
            throw new IllegalArgumentException("cantidadDevuelta debe ser > 0.");
        }

        BigDecimal brutoUnitario = calcularUnitario(lineaOriginal.getSubtotalBruto(), lineaOriginal.getCantidadVendida());
        BigDecimal descuentoUnitario = calcularUnitario(lineaOriginal.getImporteDescuentoLinea(), lineaOriginal.getCantidadVendida());
        BigDecimal finalUnitario = calcularUnitario(lineaOriginal.getSubtotalFinal(), lineaOriginal.getCantidadVendida());

        BigDecimal subtotalBrutoDevuelto = brutoUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));
        BigDecimal importeDescuentoDevuelto = descuentoUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));
        BigDecimal subtotalFinalDevuelto = finalUnitario.multiply(BigDecimal.valueOf(cantidadDevuelta));

        String descripcionResumen = buildDescripcionResumen(lineaOriginal);

        String sql = """
                INSERT INTO devolucion_item (
                    id_devolucion,
                    id_item,
                    cantidad_devuelta,
                    subtotal_bruto_devuelto,
                    importe_descuento_devuelto,
                    subtotal_final_devuelto,
                    repone_stock,
                    descripcion_resumen
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDevolucion);
            ps.setInt(2, lineaOriginal.getIdVentaItem());
            ps.setInt(3, cantidadDevuelta);
            ps.setBigDecimal(4, subtotalBrutoDevuelto);
            ps.setBigDecimal(5, importeDescuentoDevuelto);
            ps.setBigDecimal(6, subtotalFinalDevuelto);
            ps.setBoolean(7, reponeStock);
            ps.setString(8, descripcionResumen);

            ps.executeUpdate();
        }
    }

    /**
     * Inserta varias líneas de una misma devolución.
     */
    public void insertItems(
            Connection con,
            int idDevolucion,
            List<DevolucionItemInsertCommand> items
    ) throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La devolución debe tener al menos un item.");
        }

        for (DevolucionItemInsertCommand cmd : items) {
            insertItem(con, idDevolucion, cmd.lineaOriginal(), cmd.cantidadDevuelta(), cmd.reponeStock());
        }
    }

    private BigDecimal calcularUnitario(BigDecimal totalLinea, int cantidadVendida) {
        if (totalLinea == null) {
            return BigDecimal.ZERO;
        }
        if (cantidadVendida <= 0) {
            throw new IllegalArgumentException("cantidadVendida debe ser > 0.");
        }

        return totalLinea.divide(
                BigDecimal.valueOf(cantidadVendida),
                2,
                java.math.RoundingMode.HALF_UP
        );
    }

    private String buildDescripcionResumen(VentaItemParaDevolucionDTO lineaOriginal) {
        String producto = lineaOriginal.getNombreProducto() != null ? lineaOriginal.getNombreProducto().trim() : "";
        String tamano = lineaOriginal.getTamano() != null ? lineaOriginal.getTamano().trim() : "";

        if (!tamano.isBlank()) {
            return producto + " (" + tamano + ")";
        }
        return producto;
    }

    /**
     * Comando interno simple para insertar varias líneas.
     */
    public record DevolucionItemInsertCommand(
            VentaItemParaDevolucionDTO lineaOriginal,
            int cantidadDevuelta,
            boolean reponeStock
    ) {
    }
}
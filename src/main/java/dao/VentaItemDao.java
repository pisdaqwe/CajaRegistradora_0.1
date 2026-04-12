package dao;

import config.DbPool;
import dtoS.VentaItemParaDevolucionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de lectura de líneas de venta para devoluciones.
 */
public class VentaItemDao {

    public List<VentaItemParaDevolucionDTO> findItemsParaDevolucion(int idVenta, int idSucursal) {
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta debe ser > 0");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        final String sql = """
                SELECT
                    vi.id_item,
                    vi.id_venta,
                    vi.id_producto,
                    p.nombre AS nombre_producto,
                    JSON_UNQUOTE(JSON_EXTRACT(vi.descripcion_personalizacion, '$.tamano')) AS tamano,
                    vi.cantidad AS cantidad_vendida,
                    COALESCE((
                        SELECT SUM(di.cantidad_devuelta)
                        FROM devolucion_item di
                        INNER JOIN devolucion d
                            ON d.id_devolucion = di.id_devolucion
                        WHERE di.id_item = vi.id_item
                          AND d.anulada = 0
                    ), 0) AS cantidad_ya_devuelta,
                    vi.precio_unitario,
                    vi.subtotal_bruto,
                    vi.importe_descuento_linea,
                    vi.subtotal_final,
                    vi.iva,
                    CASE
                        WHEN sp.modo_disponibilidad = 'DISPONIBLE_CON_CANTIDAD' THEN 1
                        ELSE 0
                    END AS permite_reponer_stock
                FROM venta_item vi
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                LEFT JOIN stock_producto sp
                    ON sp.id_producto = vi.id_producto
                   AND sp.id_sucursal = ?
                WHERE vi.id_venta = ?
                ORDER BY vi.id_item ASC
                """;

        List<VentaItemParaDevolucionDTO> result = new ArrayList<>();

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idSucursal);
            ps.setInt(2, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapVentaItemParaDevolucion(rs));
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo líneas para devolución de idVenta=" + idVenta, e);
        }
    }

    public Optional<VentaItemParaDevolucionDTO> findItemParaDevolucionById(int idVentaItem, int idSucursal) {
        if (idVentaItem <= 0) {
            throw new IllegalArgumentException("idVentaItem debe ser > 0");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }

        final String sql = """
                SELECT
                    vi.id_item,
                    vi.id_venta,
                    vi.id_producto,
                    p.nombre AS nombre_producto,
                    JSON_UNQUOTE(JSON_EXTRACT(vi.descripcion_personalizacion, '$.tamano')) AS tamano,
                    vi.cantidad AS cantidad_vendida,
                    COALESCE((
                        SELECT SUM(di.cantidad_devuelta)
                        FROM devolucion_item di
                        INNER JOIN devolucion d
                            ON d.id_devolucion = di.id_devolucion
                        WHERE di.id_item = vi.id_item
                          AND d.anulada = 0
                    ), 0) AS cantidad_ya_devuelta,
                    vi.precio_unitario,
                    vi.subtotal_bruto,
                    vi.importe_descuento_linea,
                    vi.subtotal_final,
                    vi.iva,
                    CASE
                        WHEN sp.modo_disponibilidad = 'DISPONIBLE_CON_CANTIDAD' THEN 1
                        ELSE 0
                    END AS permite_reponer_stock
                FROM venta_item vi
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                LEFT JOIN stock_producto sp
                    ON sp.id_producto = vi.id_producto
                   AND sp.id_sucursal = ?
                WHERE vi.id_item = ?
                LIMIT 1
                """;

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idSucursal);
            ps.setInt(2, idVentaItem);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapVentaItemParaDevolucion(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo venta_item para devolución idItem=" + idVentaItem, e);
        }
    }

    private VentaItemParaDevolucionDTO mapVentaItemParaDevolucion(ResultSet rs) throws Exception {
        VentaItemParaDevolucionDTO dto = new VentaItemParaDevolucionDTO();

        int cantidadVendida = rs.getInt("cantidad_vendida");
        int cantidadYaDevuelta = rs.getInt("cantidad_ya_devuelta");
        int cantidadDisponible = Math.max(0, cantidadVendida - cantidadYaDevuelta);

        dto.setIdVentaItem(rs.getInt("id_item"));
        dto.setIdVenta(rs.getInt("id_venta"));
        dto.setIdProducto(rs.getInt("id_producto"));
        dto.setNombreProducto(rs.getString("nombre_producto"));
        dto.setTamano(rs.getString("tamano"));
        dto.setDescripcionResumen(null);

        dto.setCantidadVendida(cantidadVendida);
        dto.setCantidadYaDevuelta(cantidadYaDevuelta);
        dto.setCantidadDisponible(cantidadDisponible);

        dto.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        dto.setSubtotalBruto(rs.getBigDecimal("subtotal_bruto"));
        dto.setImporteDescuentoLinea(rs.getBigDecimal("importe_descuento_linea"));
        dto.setSubtotalFinal(rs.getBigDecimal("subtotal_final"));
        dto.setIva(rs.getBigDecimal("iva"));

        dto.setPermiteReponerStock(rs.getBoolean("permite_reponer_stock"));

        return dto;
    }
}

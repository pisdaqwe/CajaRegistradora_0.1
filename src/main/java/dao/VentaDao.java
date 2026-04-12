package dao;

import config.DbPool;
import dtoS.VentaParaDevolucionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * DAO de lectura de cabecera de ventas para devoluciones.
 */
public class VentaDao {

    public Optional<VentaParaDevolucionDTO> findVentaParaDevolucionById(int idVenta) {
    	
    	
        if (idVenta <= 0) {
            throw new IllegalArgumentException("idVenta debe ser > 0");
        }

        final String sql = """
                SELECT
                    v.id_venta,
                    v.id_sesion,
                    v.id_usuario,
                    v.fecha_venta,
                    v.total,
                    v.anulada,
                    JSON_UNQUOTE(JSON_EXTRACT(tj.json_data, '$.nombrePedido')) AS nombre_pedido,
                    JSON_UNQUOTE(JSON_EXTRACT(tj.json_data, '$.tipoServicio')) AS tipo_servicio,
                    p.metodo AS metodo_pago_original
                FROM venta v
                LEFT JOIN ticket_json tj
                    ON tj.id_venta = v.id_venta
                LEFT JOIN pago p
                    ON p.id_venta = v.id_venta
                WHERE v.id_venta = ?
                LIMIT 1
                """;

        try (
                Connection conn = DbPool.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VentaParaDevolucionDTO dto = new VentaParaDevolucionDTO();

                    dto.setIdVenta(rs.getInt("id_venta"));
                    dto.setIdSesion(rs.getInt("id_sesion"));
                    dto.setIdUsuario(rs.getInt("id_usuario"));

                    Timestamp ts = rs.getTimestamp("fecha_venta");
                    dto.setFechaVenta(ts != null ? ts.toLocalDateTime() : null);

                    dto.setNombrePedido(rs.getString("nombre_pedido"));
                    dto.setTipoServicio(rs.getString("tipo_servicio"));
                    dto.setMetodoPagoOriginal(rs.getString("metodo_pago_original"));
                    dto.setTotalVenta(rs.getBigDecimal("total"));
                    dto.setAnulada(rs.getBoolean("anulada"));

                    return Optional.of(dto);
                }

                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo venta para devolución idVenta=" + idVenta, e);
        }
    }
}

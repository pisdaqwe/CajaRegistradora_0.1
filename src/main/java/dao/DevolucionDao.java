package dao;

import config.DbPool;
import dtoS.RegistrarDevolucionRequest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO de cabecera de devoluciones.
 *
 * Responsabilidades:
 * - insertar cabecera devolucion
 * - más adelante: búsqueda por id / por venta original
 */
public class DevolucionDao {

    /**
     * Inserta la cabecera de una devolución dentro de una transacción abierta.
     *
     * Devuelve el id_devolucion generado.
     */
    public int insert(Connection con, RegistrarDevolucionRequest request, BigDecimal totalReembolsado)
            throws SQLException {

        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null.");
        }
        if (request == null) {
            throw new IllegalArgumentException("request no puede ser null.");
        }
        if (totalReembolsado == null || totalReembolsado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("totalReembolsado inválido.");
        }

        String sql = """
                INSERT INTO devolucion (
                    id_venta_original,
                    id_sesion,
                    id_usuario_admin,
                    fecha_devolucion,
                    metodo_reembolso,
                    motivo,
                    observaciones,
                    total_reembolsado,
                    anulada
                ) VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, request.getIdVentaOriginal());
            ps.setInt(2, request.getIdSesionCajaActual());
            ps.setInt(3, request.getIdUsuarioAdmin());
            ps.setString(4, request.getMetodoReembolso());
            ps.setString(5, request.getMotivo());
            ps.setString(6, request.getObservaciones());
            ps.setBigDecimal(7, totalReembolsado);
            ps.setBoolean(8, false);

            ps.executeUpdate();

            return getGeneratedId(ps, "No se pudo obtener el id de la devolución insertada.");
        }
    }

    private int getGeneratedId(PreparedStatement ps, String errorMessage) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException(errorMessage);
    }
}
package dao;

import dtoS.MermaRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO encargado de insertar la cabecera de una merma.
 *
 * RESPONSABILIDAD:
 * - insertar en tabla merma
 * - devolver el id generado
 *
 * IMPORTANTE:
 * - NO abre ni cierra transacciones completas
 * - NO contiene lógica de negocio
 * - trabaja sobre una Connection externa
 */
public class MermaDao {

    private static final String SQL_INSERT = """
        INSERT INTO merma (
            id_sucursal,
            id_usuario,
            tipo_merma,
            origen,
            motivo,
            observaciones
        ) VALUES (?, ?, ?, ?, ?, ?)
        """;

    /**
     * Inserta la cabecera de merma y devuelve el id generado.
     */
    public int insert(Connection con, MermaRequest request) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (request == null) {
            throw new IllegalArgumentException("MermaRequest no puede ser null");
        }

        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, request.getIdSucursal());
            ps.setInt(2, request.getIdUsuario());
            ps.setString(3, request.getTipoMerma());
            ps.setString(4, request.getOrigen());
            ps.setString(5, request.getMotivo());
            ps.setString(6, request.getObservaciones());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener el id generado de merma.");
    }
}
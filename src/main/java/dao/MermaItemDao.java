package dao;

import dtoS.MermaItemRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO encargado de insertar líneas en merma_item.
 *
 * RESPONSABILIDAD:
 * - insertar una línea de merma
 * - devolver el id generado
 *
 * IMPORTANTE:
 * - no abre ni cierra transacciones
 * - no contiene lógica de negocio
 * - trabaja sobre una Connection externa
 */
public class MermaItemDao {

    private static final String SQL_INSERT = """
        INSERT INTO merma_item (
            id_merma,
            id_producto,
            id_tamano,
            id_tipo_cafe_seleccionado,
            cantidad,
            usar_receta,
            configuracion_json,
            descripcion_snapshot
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    /**
     * Inserta una línea de merma y devuelve el id generado.
     */
    public int insert(Connection con, int idMerma, MermaItemRequest item) throws SQLException {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idMerma <= 0) {
            throw new IllegalArgumentException("idMerma debe ser > 0");
        }
        if (item == null) {
            throw new IllegalArgumentException("MermaItemRequest no puede ser null");
        }

        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idMerma);
            ps.setInt(2, item.getIdProducto());

            if (item.getIdTamano() != null) {
                ps.setInt(3, item.getIdTamano());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            if (item.getIdTipoCafeSeleccionado() != null) {
                ps.setInt(4, item.getIdTipoCafeSeleccionado());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setBigDecimal(5, item.getCantidad());
            ps.setBoolean(6, item.isUsarReceta());
            ps.setString(7, item.getConfiguracionJson());
            ps.setString(8, item.getDescripcionSnapshot());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener el id generado de merma_item.");
    }
}
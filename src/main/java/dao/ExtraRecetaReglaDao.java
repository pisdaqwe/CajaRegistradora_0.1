package dao;

import config.DbPool;
import dtoS.ExtraRecetaReglaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para cargar reglas de receta de extras.
 */
public class ExtraRecetaReglaDao {

    private static final String SQL_FIND_BY_PRODUCTO_TAMANO_EXTRA = """
        SELECT
            err.id_regla,
            err.id_extra,
            err.id_producto,
            err.id_tamano,
            err.tipo_regla,
            err.fuente_ingrediente,
            err.id_ingrediente_origen,
            io.nombre AS nombre_ingrediente_origen,
            err.id_ingrediente_destino,
            idest.nombre AS nombre_ingrediente_destino,
            err.cantidad,
            err.id_unidad,
            u.nombre AS nombre_unidad,
            err.hereda_cantidad_origen,
            err.activo,
            err.orden,
            err.observaciones
        FROM extra_receta_regla err
        LEFT JOIN ingrediente io
            ON io.id_ingrediente = err.id_ingrediente_origen
        LEFT JOIN ingrediente idest
            ON idest.id_ingrediente = err.id_ingrediente_destino
        LEFT JOIN unidad_ingrediente u
            ON u.id_unidad = err.id_unidad
        WHERE err.id_extra = ?
          AND (err.id_producto = ? OR err.id_producto IS NULL)
          AND (err.id_tamano = ? OR err.id_tamano IS NULL)
          AND err.activo = 1
        ORDER BY
            CASE WHEN err.id_producto IS NULL THEN 1 ELSE 0 END,
            CASE WHEN err.id_tamano IS NULL THEN 1 ELSE 0 END,
            err.orden ASC,
            err.id_regla ASC
        """;

    public List<ExtraRecetaReglaDTO> findByProductoTamanoYExtra(int idProducto, int idTamano, int idExtra) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (idTamano <= 0) {
            throw new IllegalArgumentException("idTamano debe ser > 0");
        }
        if (idExtra <= 0) {
            throw new IllegalArgumentException("idExtra debe ser > 0");
        }

        List<ExtraRecetaReglaDTO> lista = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_FIND_BY_PRODUCTO_TAMANO_EXTRA)) {

            ps.setInt(1, idExtra);
            ps.setInt(2, idProducto);
            ps.setInt(3, idTamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExtraRecetaReglaDTO dto = new ExtraRecetaReglaDTO();
                    dto.setIdRegla(rs.getInt("id_regla"));
                    dto.setIdExtra(rs.getInt("id_extra"));
                    dto.setIdProducto((Integer) rs.getObject("id_producto"));
                    dto.setIdTamano((Integer) rs.getObject("id_tamano"));
                    dto.setTipoRegla(rs.getString("tipo_regla"));
                    dto.setFuenteIngrediente(rs.getString("fuente_ingrediente"));
                    dto.setIdIngredienteOrigen((Integer) rs.getObject("id_ingrediente_origen"));
                    dto.setNombreIngredienteOrigen(rs.getString("nombre_ingrediente_origen"));
                    dto.setIdIngredienteDestino((Integer) rs.getObject("id_ingrediente_destino"));
                    dto.setNombreIngredienteDestino(rs.getString("nombre_ingrediente_destino"));
                    dto.setCantidad(rs.getBigDecimal("cantidad"));
                    dto.setIdUnidad((Integer) rs.getObject("id_unidad"));
                    dto.setNombreUnidad(rs.getString("nombre_unidad"));
                    dto.setHeredaCantidadOrigen(rs.getBoolean("hereda_cantidad_origen"));
                    dto.setActivo(rs.getBoolean("activo"));
                    dto.setOrden(rs.getInt("orden"));
                    dto.setObservaciones(rs.getString("observaciones"));
                    lista.add(dto);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando reglas de receta del extra. producto=" + idProducto
                            + ", tamano=" + idTamano
                            + ", extra=" + idExtra,
                    e
            );
        }

        return lista;
    }
}

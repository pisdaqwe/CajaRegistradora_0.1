package dao;

import config.DbPool;
import dtoS.PersonalizacionRecetaReglaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para cargar reglas de receta de personalizaciones.
 */
public class PersonalizacionRecetaReglaDao {

    private static final String SQL_FIND_BY_PRODUCTO_TAMANO_PERSONALIZACION = """
        SELECT
            prr.id_regla,
            prr.id_personalizacion,
            prr.id_producto,
            prr.id_tamano,
            prr.tipo_regla,
            prr.fuente_ingrediente,
            prr.id_ingrediente_origen,
            io.nombre AS nombre_ingrediente_origen,
            prr.id_ingrediente_destino,
            idest.nombre AS nombre_ingrediente_destino,
            prr.cantidad,
            prr.id_unidad,
            u.nombre AS nombre_unidad,
            prr.hereda_cantidad_origen,
            prr.activo,
            prr.orden,
            prr.observaciones
        FROM personalizacion_receta_regla prr
        LEFT JOIN ingrediente io
            ON io.id_ingrediente = prr.id_ingrediente_origen
        LEFT JOIN ingrediente idest
            ON idest.id_ingrediente = prr.id_ingrediente_destino
        LEFT JOIN unidad_ingrediente u
            ON u.id_unidad = prr.id_unidad
        WHERE prr.id_personalizacion = ?
          AND (prr.id_producto = ? OR prr.id_producto IS NULL)
          AND (prr.id_tamano = ? OR prr.id_tamano IS NULL)
          AND prr.activo = 1
        ORDER BY
            CASE WHEN prr.id_producto IS NULL THEN 1 ELSE 0 END,
            CASE WHEN prr.id_tamano IS NULL THEN 1 ELSE 0 END,
            prr.orden ASC,
            prr.id_regla ASC
        """;

    public List<PersonalizacionRecetaReglaDTO> findByProductoTamanoYPersonalizacion(
            int idProducto,
            int idTamano,
            int idPersonalizacion
    ) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }
        if (idTamano <= 0) {
            throw new IllegalArgumentException("idTamano debe ser > 0");
        }
        if (idPersonalizacion <= 0) {
            throw new IllegalArgumentException("idPersonalizacion debe ser > 0");
        }

        List<PersonalizacionRecetaReglaDTO> lista = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_FIND_BY_PRODUCTO_TAMANO_PERSONALIZACION)) {

            ps.setInt(1, idPersonalizacion);
            ps.setInt(2, idProducto);
            ps.setInt(3, idTamano);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PersonalizacionRecetaReglaDTO dto = new PersonalizacionRecetaReglaDTO();
                    dto.setIdRegla(rs.getInt("id_regla"));
                    dto.setIdPersonalizacion(rs.getInt("id_personalizacion"));
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
                    "Error cargando reglas de receta de la personalización. producto=" + idProducto
                            + ", tamano=" + idTamano
                            + ", personalizacion=" + idPersonalizacion,
                    e
            );
        }

        return lista;
    }
}

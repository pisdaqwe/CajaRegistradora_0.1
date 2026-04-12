package dao;

import config.DbPool;
import dtoS.TipoCafeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO que carga los tipos de café permitidos para un producto.
 *
 * OBJETIVO:
 * - leer la relación producto_tipo_cafe + tipo_cafe
 * - devolver a la UI solo los cafés válidos para ese producto
 * - incluir si es el café por defecto
 * - incluir el suplemento de precio
 *
 * IMPORTANTE:
 * - este DAO NO decide lógica de negocio
 * - solo devuelve los datos necesarios para customización
 * - el service decidirá cómo usar esta información
 */
public class ProductoTipoCafeDao {

    // =====================================================
    // 1) LISTADO DE TIPOS DE CAFÉ ACTIVOS POR PRODUCTO
    // =====================================================

    /**
     * Devuelve todos los tipos de café activos que puede usar un producto.
     *
     * Orden:
     * - primero el café por defecto
     * - después por orden configurado en tipo_cafe
     * - y como desempate por id
     */
    public List<TipoCafeDTO> findActivosByProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }

        String sql = """
            SELECT
                tc.id_tipo_cafe,
                tc.nombre,
                tc.id_ingrediente,
                tc.familia_cafe,
                tc.es_descafeinado,
                tc.suplemento_precio,
                tc.activo,
                tc.orden,
                ptc.por_defecto
            FROM producto_tipo_cafe ptc
            JOIN tipo_cafe tc
                ON tc.id_tipo_cafe = ptc.id_tipo_cafe
            WHERE ptc.id_producto = ?
              AND ptc.activo = 1
              AND tc.activo = 1
            ORDER BY ptc.por_defecto DESC, tc.orden ASC, tc.id_tipo_cafe ASC
            """;

        List<TipoCafeDTO> out = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapTipoCafeDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando tipos de café para el producto " + idProducto,
                    e
            );
        }

        return out;
    }

    // =====================================================
    // 2) TIPO DE CAFÉ POR DEFECTO DE UN PRODUCTO
    // =====================================================

    /**
     * Devuelve el tipo de café por defecto del producto, si existe.
     *
     * Esto será útil para:
     * - inicializar TicketItem al añadir una bebida
     * - reflejar en UI qué opción debe aparecer seleccionada
     */
    public Optional<TipoCafeDTO> findDefaultByProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto debe ser > 0");
        }

        String sql = """
            SELECT
                tc.id_tipo_cafe,
                tc.nombre,
                tc.id_ingrediente,
                tc.familia_cafe,
                tc.es_descafeinado,
                tc.suplemento_precio,
                tc.activo,
                tc.orden,
                ptc.por_defecto
            FROM producto_tipo_cafe ptc
            JOIN tipo_cafe tc
                ON tc.id_tipo_cafe = ptc.id_tipo_cafe
            WHERE ptc.id_producto = ?
              AND ptc.por_defecto = 1
              AND ptc.activo = 1
              AND tc.activo = 1
            LIMIT 1
            """;

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTipoCafeDTO(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error cargando el tipo de café por defecto del producto " + idProducto,
                    e
            );
        }

        return Optional.empty();
    }

    // =====================================================
    // 3) MAPEO
    // =====================================================

    /**
     * Mapea una fila SQL a TipoCafeDTO.
     *
     * CAMPOS IMPORTANTES:
     * - suplemento_precio:
     *   será el importe extra que se sumará al item
     *
     * - por_defecto:
     *   marca qué café debe aparecer seleccionado inicialmente
     */
    private TipoCafeDTO mapTipoCafeDTO(ResultSet rs) throws Exception {
        TipoCafeDTO dto = new TipoCafeDTO();
        dto.setIdTipoCafe(rs.getInt("id_tipo_cafe"));
        dto.setNombre(rs.getString("nombre"));
        dto.setIdIngrediente(rs.getInt("id_ingrediente"));
        dto.setFamiliaCafe(rs.getString("familia_cafe"));
        dto.setEsDescafeinado(rs.getBoolean("es_descafeinado"));
        dto.setSuplementoPrecio(rs.getBigDecimal("suplemento_precio"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setOrden(rs.getInt("orden"));
        dto.setPorDefecto(rs.getBoolean("por_defecto"));
        return dto;
    }
}
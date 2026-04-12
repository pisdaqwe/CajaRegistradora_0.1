package dao;

import config.DbPool;
import dtoS.StockIngredienteDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * DAO de stock real de ingredientes.
 */
public class StockIngredienteDao {

    private static final String SQL_FIND_BY_SUCURSAL_INGREDIENTE = """
        SELECT
            si.id_sucursal,
            si.id_ingrediente,
            i.nombre AS nombre_ingrediente,
            si.stock,
            i.unidad_base AS id_unidad_base,
            u.nombre AS nombre_unidad_base
        FROM stock_ingrediente si
        INNER JOIN ingrediente i
            ON i.id_ingrediente = si.id_ingrediente
        LEFT JOIN unidad_ingrediente u
            ON u.id_unidad = i.unidad_base
        WHERE si.id_sucursal = ?
          AND si.id_ingrediente = ?
        """;

    private static final String SQL_FIND_BY_SUCURSAL_INGREDIENTE_FOR_UPDATE = """
        SELECT
            si.id_sucursal,
            si.id_ingrediente,
            i.nombre AS nombre_ingrediente,
            si.stock,
            i.unidad_base AS id_unidad_base,
            u.nombre AS nombre_unidad_base
        FROM stock_ingrediente si
        INNER JOIN ingrediente i
            ON i.id_ingrediente = si.id_ingrediente
        LEFT JOIN unidad_ingrediente u
            ON u.id_unidad = i.unidad_base
        WHERE si.id_sucursal = ?
          AND si.id_ingrediente = ?
        FOR UPDATE
        """;

    private static final String SQL_DESCONTAR_STOCK = """
        UPDATE stock_ingrediente
           SET stock = stock - ?
         WHERE id_sucursal = ?
           AND id_ingrediente = ?
        """;

    public Optional<StockIngredienteDTO> findBySucursalAndIngrediente(int idSucursal, int idIngrediente) {
        return findInternal(idSucursal, idIngrediente, false, null);
    }

    /**
     * Igual que el normal, pero bloqueando la fila.
     * Este es el que usarás dentro de la transacción real de venta.
     */
    public Optional<StockIngredienteDTO> findBySucursalAndIngredienteForUpdate(
            Connection con,
            int idSucursal,
            int idIngrediente
    ) {
        return findInternal(idSucursal, idIngrediente, true, con);
    }

    public void descontarStock(Connection con,
                               int idSucursal,
                               int idIngrediente,
                               BigDecimal cantidadADescontar) {
        if (con == null) {
            throw new IllegalArgumentException("Connection no puede ser null");
        }
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (idIngrediente <= 0) {
            throw new IllegalArgumentException("idIngrediente debe ser > 0");
        }
        if (cantidadADescontar == null || cantidadADescontar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("cantidadADescontar debe ser > 0");
        }

        try (PreparedStatement ps = con.prepareStatement(SQL_DESCONTAR_STOCK)) {
            ps.setBigDecimal(1, cantidadADescontar);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idIngrediente);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException(
                        "No se pudo descontar stock del ingrediente " + idIngrediente
                                + " en sucursal " + idSucursal
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error descontando stock. sucursal=" + idSucursal
                            + ", ingrediente=" + idIngrediente
                            + ", cantidad=" + cantidadADescontar,
                    e
            );
        }
    }

    private Optional<StockIngredienteDTO> findInternal(int idSucursal,
                                                       int idIngrediente,
                                                       boolean forUpdate,
                                                       Connection externalConnection) {
        if (idSucursal <= 0) {
            throw new IllegalArgumentException("idSucursal debe ser > 0");
        }
        if (idIngrediente <= 0) {
            throw new IllegalArgumentException("idIngrediente debe ser > 0");
        }

        String sql = forUpdate ? SQL_FIND_BY_SUCURSAL_INGREDIENTE_FOR_UPDATE : SQL_FIND_BY_SUCURSAL_INGREDIENTE;

        try {
            if (externalConnection != null) {
                return executeFind(externalConnection, sql, idSucursal, idIngrediente);
            }

            try (Connection cn = DbPool.getConnection()) {
                return executeFind(cn, sql, idSucursal, idIngrediente);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error consultando stock_ingrediente. sucursal=" + idSucursal
                            + ", ingrediente=" + idIngrediente,
                    e
            );
        }
    }

    private Optional<StockIngredienteDTO> executeFind(Connection cn,
                                                      String sql,
                                                      int idSucursal,
                                                      int idIngrediente) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSucursal);
            ps.setInt(2, idIngrediente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StockIngredienteDTO dto = new StockIngredienteDTO();
                    dto.setIdSucursal(rs.getInt("id_sucursal"));
                    dto.setIdIngrediente(rs.getInt("id_ingrediente"));
                    dto.setNombreIngrediente(rs.getString("nombre_ingrediente"));
                    dto.setStock(rs.getBigDecimal("stock"));
                    dto.setIdUnidadBase((Integer) rs.getObject("id_unidad_base"));
                    dto.setNombreUnidadBase(rs.getString("nombre_unidad_base"));
                    return Optional.of(dto);
                }
                return Optional.empty();
            }
        }
    }
}
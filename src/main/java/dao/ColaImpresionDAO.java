package dao;

import config.DbPool;
import model.ColaImpresion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de la tabla cola_impresion.
 *
 * Trabaja con el modelo persistente ColaImpresion.
 */
public class ColaImpresionDAO {

    private static final String SQL_INSERT = """
            INSERT INTO cola_impresion (
                id_venta,
                id_item,
                id_estacion,
                descripcion,
                impreso,
                preparado,
                cancelado,
                fecha_creacion,
                fecha_impresion,
                fecha_preparado
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_FIND_PENDIENTES_HOY_BY_ESTACION = """
            SELECT
                id_cola,
                id_venta,
                id_item,
                id_estacion,
                descripcion,
                impreso,
                preparado,
                cancelado,
                fecha_creacion,
                fecha_impresion,
                fecha_preparado
            FROM cola_impresion
            WHERE id_estacion = ?
              AND cancelado = 0
              AND preparado = 0
              AND fecha_creacion >= ?
              AND fecha_creacion < ?
            ORDER BY id_cola ASC
            """;

    private static final String SQL_FIND_SIGUIENTE_PENDIENTE_HOY_BY_ESTACION = """
            SELECT
                id_cola,
                id_venta,
                id_item,
                id_estacion,
                descripcion,
                impreso,
                preparado,
                cancelado,
                fecha_creacion,
                fecha_impresion,
                fecha_preparado
            FROM cola_impresion
            WHERE id_estacion = ?
              AND cancelado = 0
              AND preparado = 0
              AND fecha_creacion >= ?
              AND fecha_creacion < ?
            ORDER BY id_cola ASC
            LIMIT 1
            """;

    private static final String SQL_MARK_IMPRESO_Y_PREPARADO = """
            UPDATE cola_impresion
            SET impreso = 1,
                preparado = 1,
                fecha_impresion = ?,
                fecha_preparado = ?
            WHERE id_cola = ?
            """;

    private static final String SQL_MARK_CANCELADO = """
            UPDATE cola_impresion
            SET cancelado = 1
            WHERE id_cola = ?
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT
                id_cola,
                id_venta,
                id_item,
                id_estacion,
                descripcion,
                impreso,
                preparado,
                cancelado,
                fecha_creacion,
                fecha_impresion,
                fecha_preparado
            FROM cola_impresion
            WHERE id_cola = ?
            """;

    public ColaImpresionDAO() {
    }

    /**
     * Inserta una fila de cola usando una conexión externa.
     * Útil si alguna vez quieres encadenarlo dentro de una operación mayor.
     */
    public void insert(Connection con, ColaImpresion cola) {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            fillInsert(ps, cola);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar en cola_impresion", e);
        }
    }

    /**
     * Inserta una fila de cola usando la conexión del pool global.
     */
    public void insert(ColaImpresion cola) {
        try (Connection con = DbPool.getConnection()) {
            insert(con, cola);
        } catch (Exception e) {
            throw new RuntimeException("Error al insertar en cola_impresion", e);
        }
    }

    /**
     * Devuelve todos los pendientes del día para una estación concreta.
     */
    public List<ColaImpresion> findPendientesHoyByEstacion(
            int idEstacion,
            LocalDateTime inicioDia,
            LocalDateTime finDia
    ) {
        List<ColaImpresion> result = new ArrayList<>();

        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_PENDIENTES_HOY_BY_ESTACION)) {

            ps.setInt(1, idEstacion);
            ps.setTimestamp(2, Timestamp.valueOf(inicioDia));
            ps.setTimestamp(3, Timestamp.valueOf(finDia));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al buscar pendientes de hoy para estación id=" + idEstacion, e
            );
        }

        return result;
    }

    /**
     * Devuelve el siguiente item pendiente del día para una estación.
     */
    public Optional<ColaImpresion> findSiguientePendienteHoyByEstacion(
            int idEstacion,
            LocalDateTime inicioDia,
            LocalDateTime finDia
    ) {
        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_SIGUIENTE_PENDIENTE_HOY_BY_ESTACION)) {

            ps.setInt(1, idEstacion);
            ps.setTimestamp(2, Timestamp.valueOf(inicioDia));
            ps.setTimestamp(3, Timestamp.valueOf(finDia));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al buscar siguiente pendiente de hoy para estación id=" + idEstacion, e
            );
        }

        return Optional.empty();
    }

    /**
     * Marca un item como impreso y preparado.
     */
    public void marcarImpresoYPreparado(
            int idCola,
            LocalDateTime fechaImpresion,
            LocalDateTime fechaPreparado
    ) {
        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_MARK_IMPRESO_Y_PREPARADO)) {

            ps.setTimestamp(1, Timestamp.valueOf(fechaImpresion));
            ps.setTimestamp(2, Timestamp.valueOf(fechaPreparado));
            ps.setInt(3, idCola);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al marcar impreso/preparado id_cola=" + idCola, e
            );
        }
    }

    /**
     * Marca un item de cola como cancelado.
     */
    public void marcarCancelado(int idCola) {
        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_MARK_CANCELADO)) {

            ps.setInt(1, idCola);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al marcar cancelado id_cola=" + idCola, e
            );
        }
    }

    /**
     * Busca una fila de cola por id.
     */
    public Optional<ColaImpresion> findById(int idCola) {
        try (Connection con = DbPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, idCola);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al buscar cola_impresion por id_cola=" + idCola, e
            );
        }

        return Optional.empty();
    }

    private void fillInsert(PreparedStatement ps, ColaImpresion cola) throws Exception {
        ps.setInt(1, cola.getIdVenta());
        ps.setInt(2, cola.getIdItem());
        ps.setInt(3, cola.getIdEstacion());
        ps.setString(4, cola.getDescripcion());

        ps.setBoolean(5, Boolean.TRUE.equals(cola.getImpreso()));
        ps.setBoolean(6, Boolean.TRUE.equals(cola.getPreparado()));
        ps.setBoolean(7, Boolean.TRUE.equals(cola.getCancelado()));

        if (cola.getFechaCreacion() != null) {
            ps.setTimestamp(8, Timestamp.valueOf(cola.getFechaCreacion()));
        } else {
            ps.setTimestamp(8, null);
        }

        if (cola.getFechaImpresion() != null) {
            ps.setTimestamp(9, Timestamp.valueOf(cola.getFechaImpresion()));
        } else {
            ps.setTimestamp(9, null);
        }

        if (cola.getFechaPreparado() != null) {
            ps.setTimestamp(10, Timestamp.valueOf(cola.getFechaPreparado()));
        } else {
            ps.setTimestamp(10, null);
        }
    }

    private ColaImpresion mapRow(ResultSet rs) throws Exception {
        ColaImpresion cola = new ColaImpresion();

        cola.setIdCola(rs.getInt("id_cola"));
        cola.setIdVenta(rs.getInt("id_venta"));
        cola.setIdItem(rs.getInt("id_item"));
        cola.setIdEstacion(rs.getInt("id_estacion"));
        cola.setDescripcion(rs.getString("descripcion"));

        cola.setImpreso(rs.getBoolean("impreso"));
        cola.setPreparado(rs.getBoolean("preparado"));
        cola.setCancelado(rs.getBoolean("cancelado"));

        Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
        if (tsCreacion != null) {
            cola.setFechaCreacion(tsCreacion.toLocalDateTime());
        }

        Timestamp tsImpresion = rs.getTimestamp("fecha_impresion");
        if (tsImpresion != null) {
            cola.setFechaImpresion(tsImpresion.toLocalDateTime());
        }

        Timestamp tsPreparado = rs.getTimestamp("fecha_preparado");
        if (tsPreparado != null) {
            cola.setFechaPreparado(tsPreparado.toLocalDateTime());
        }

        return cola;
    }
}

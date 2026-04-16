
package dao;

import config.DbPool;
import dtoS.InformeFiltroDTO;
import dtoS.InformeVentasPorDiaRowDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InformesDao {

    public InformesDao() {
    }

    public List<InformeVentasPorDiaRowDTO> findVentasPorDiaAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(v.fecha_venta) AS fecha,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    COUNT(*) AS numero_tickets,
                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE COALESCE(SUM(v.total), 0) / COUNT(*)
                    END AS ticket_medio
                FROM venta v
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago p
                    ON p.id_venta = v.id_venta
                WHERE 1 = 1
                """);

        if (!filtros.isIncluirAnuladas()) {
            sql.append(" AND v.anulada = 0 ");
        }

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND v.fecha_venta >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND v.fecha_venta < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        if (filtros.getIdSucursal() != null) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(filtros.getIdSucursal());
        }

        if (filtros.getIdCaja() != null) {
            sql.append(" AND sc.id_caja = ? ");
            params.add(filtros.getIdCaja());
        }

        if (!filtros.isTodosLosEmpleados()
                && filtros.getIdsEmpleados() != null
                && !filtros.getIdsEmpleados().isEmpty()) {

            sql.append(" AND v.id_usuario IN (");
            appendPlaceholders(sql, filtros.getIdsEmpleados().size());
            sql.append(") ");

            params.addAll(filtros.getIdsEmpleados());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(p.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY DATE(v.fecha_venta)
                ORDER BY DATE(v.fecha_venta)
                """);

        List<InformeVentasPorDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    public List<InformeVentasPorDiaRowDTO> findDevolucionesPorDiaAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(d.fecha_devolucion) AS fecha,
                    COALESCE(SUM(d.total_reembolsado), 0) AS total_devoluciones
                FROM devolucion d
                INNER JOIN venta v
                    ON v.id_venta = d.id_venta_original
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                WHERE 1 = 1
                """);

        if (!filtros.isIncluirAnuladas()) {
            sql.append(" AND d.anulada = 0 ");
        }

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND d.fecha_devolucion >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND d.fecha_devolucion < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        if (filtros.getIdSucursal() != null) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(filtros.getIdSucursal());
        }

        if (filtros.getIdCaja() != null) {
            sql.append(" AND sc.id_caja = ? ");
            params.add(filtros.getIdCaja());
        }

        if (!filtros.isTodosLosEmpleados()
                && filtros.getIdsEmpleados() != null
                && !filtros.getIdsEmpleados().isEmpty()) {

            sql.append(" AND v.id_usuario IN (");
            appendPlaceholders(sql, filtros.getIdsEmpleados().size());
            sql.append(") ");

            params.addAll(filtros.getIdsEmpleados());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(d.metodo_reembolso) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY DATE(d.fecha_devolucion)
                ORDER BY DATE(d.fecha_devolucion)
                """);

        List<InformeVentasPorDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setTotalDevoluciones(nvl(rs.getBigDecimal("total_devoluciones")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    private void appendPlaceholders(StringBuilder sql, int count) {
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
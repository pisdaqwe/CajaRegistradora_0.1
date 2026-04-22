
package dao;

import config.DbPool;
import dtoS.InformeCombosVendidosRowDTO;
import dtoS.InformeDescuentosAplicadosRowDTO;
import dtoS.InformeDevolucionesProductoRowDTO;
import dtoS.InformeExtrasVendidosRowDTO;
import dtoS.InformeFiltroDTO;
import dtoS.InformeMermaPeriodoRowDTO;
import dtoS.InformeMovimientoStockRowDTO;
import dtoS.InformeNetoVsDevolucionesRowDTO;
import dtoS.InformePagosMetodoRowDTO;
import dtoS.InformeProductosPorEmpleadoRowDTO;
import dtoS.InformeProductosVendidosRowDTO;
import dtoS.InformeRankingEmpleadosExtraRowDTO;
import dtoS.InformeRankingEmpleadosExtrasRowDTO;
import dtoS.InformeRankingEmpleadosProductoRowDTO;
import dtoS.InformeRankingEmpleadosVentasRowDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeResumenEjecutivoRowDTO;
import dtoS.InformeTicketMedioDiaRowDTO;
import dtoS.InformeTiemposEstacionRowDTO;
import dtoS.InformeVentasCajaRowDTO;
import dtoS.InformeVentasExtraEmpleadoRowDTO;
import dtoS.InformeVentasFranjaRowDTO;
import dtoS.InformeVentasPorDiaRowDTO;
import dtoS.InformeVentasProductoEmpleadoRowDTO;
import dtoS.InformeVentasSesionCajaRowDTO;
import enums.TipoInforme;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InformesDao {

    public InformesDao() {
    }

    // =====================================================
    // BLOQUE 1 - RESUMEN EJECUTIVO
    // =====================================================

    // Qué hace:
    // Devuelve la base del resumen ejecutivo reutilizando:
    // - ventas por día agregado
    // - devoluciones por día agregado
    // - combos del resumen
    //
    // Qué calcula aquí:
    // - ventas brutas
    // - devoluciones
    // - neto
    // - ticket medio
    // - total combos
    // - ahorro total
    public InformeResumenEjecutivoResultDTO findResumenEjecutivoBase(InformeFiltroDTO filtros) throws SQLException {
        List<InformeVentasPorDiaRowDTO> ventasRows = findVentasPorDiaAgregado(filtros);

        List<InformeVentasPorDiaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? findDevolucionesPorDiaAgregado(filtros)
                : Collections.emptyList();

        List<InformeCombosVendidosRowDTO> combosRows = findResumenEjecutivoCombos(filtros);

        BigDecimal ventasBrutas = BigDecimal.ZERO;
        BigDecimal devoluciones = BigDecimal.ZERO;
        BigDecimal neto;
        BigDecimal ticketMedio = BigDecimal.ZERO;
        int totalTickets = 0;
        int totalCombos = 0;
        BigDecimal ahorroTotal = BigDecimal.ZERO;

        for (InformeVentasPorDiaRowDTO row : ventasRows) {
            ventasBrutas = ventasBrutas.add(nvl(row.getTotalVentas()));
            totalTickets += row.getNumeroTickets() != null ? row.getNumeroTickets() : 0;
        }

        for (InformeVentasPorDiaRowDTO row : devolucionesRows) {
            devoluciones = devoluciones.add(nvl(row.getTotalDevoluciones()));
        }

        for (InformeCombosVendidosRowDTO row : combosRows) {
            totalCombos += row.getVecesVendido() != null ? row.getVecesVendido() : 0;
            ahorroTotal = ahorroTotal.add(nvl(row.getAhorroTotal()));
        }

        neto = ventasBrutas.subtract(devoluciones);

        if (totalTickets > 0) {
            ticketMedio = ventasBrutas.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP);
        }

        InformeResumenEjecutivoResultDTO result = new InformeResumenEjecutivoResultDTO();
        result.setVentasBrutas(ventasBrutas);
        result.setDevoluciones(devoluciones);
        result.setNeto(neto);
        result.setTicketMedio(ticketMedio);
        result.setTotalCombos(totalCombos);
        result.setAhorroTotal(ahorroTotal);

        List<InformeResumenEjecutivoRowDTO> rows = new ArrayList<>();
        rows.add(buildResumenRow("Ventas brutas", ventasBrutas, "Suma total de ventas registradas"));
        rows.add(buildResumenRow("Devoluciones", devoluciones, "Importe total reembolsado"));
        rows.add(buildResumenRow("Neto", neto, "Ventas menos devoluciones"));
        rows.add(buildResumenRow("Ticket medio", ticketMedio, "Importe medio por venta"));
        rows.add(buildResumenRow("Ahorro combos", ahorroTotal, "Ahorro total generado por combos"));
        result.setRows(rows);

        return result;
    }

    // Qué hace:
    // Devuelve el desglose de pagos para el resumen ejecutivo.
    // Reutiliza el informe de pagos por método.
    public List<InformePagosMetodoRowDTO> findResumenEjecutivoPagos(InformeFiltroDTO filtros) throws SQLException {
        return findPagosPorMetodo(filtros);
    }

    // Qué hace:
    // Devuelve una fila por combo aplicado en ventas.
    // Usa VENTA_COMBO y aplica los mismos filtros generales.
    public List<InformeCombosVendidosRowDTO> findResumenEjecutivoCombos(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    vc.id_combo,
                    vc.nombre_combo,
                    COUNT(*) AS veces_vendido,
                    COALESCE(SUM(vc.precio_original), 0) AS precio_original_total,
                    COALESCE(SUM(vc.precio_final), 0) AS precio_final_total,
                    COALESCE(SUM(vc.ahorro_total), 0) AS ahorro_total
                FROM venta_combo vc
                INNER JOIN venta v
                    ON v.id_venta = vc.id_venta
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
            sql.append(" AND vc.fecha_aplicacion >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND vc.fecha_aplicacion < ? ");
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
                GROUP BY vc.id_combo, vc.nombre_combo
                ORDER BY ahorro_total DESC, veces_vendido DESC
                """);
        
        if (filtros.getTipoInforme() == TipoInforme.COMBOS_VENDIDOS
                && filtros.getTopN() != null
                && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeCombosVendidosRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeCombosVendidosRowDTO row = new InformeCombosVendidosRowDTO();
                    row.setIdCombo(rs.getInt("id_combo"));
                    row.setNombreCombo(rs.getString("nombre_combo"));
                    row.setVecesVendido(rs.getInt("veces_vendido"));
                    row.setPrecioOriginalTotal(nvl(rs.getBigDecimal("precio_original_total")));
                    row.setPrecioFinalTotal(nvl(rs.getBigDecimal("precio_final_total")));
                    row.setAhorroTotal(nvl(rs.getBigDecimal("ahorro_total")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Devuelve una fila por descuento aplicado.
    // Usa VENTA_DESCUENTO y DESCUENTO y aplica los mismos filtros generales.
    public List<InformeDescuentosAplicadosRowDTO> findResumenEjecutivoDescuentos(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    d.id_descuento,
                    d.nombre AS nombre_descuento,
                    d.origen AS tipo_beneficio,
                    COUNT(*) AS numero_usos,
                    COALESCE(SUM(vd.importe_base), 0) AS importe_base,
                    COALESCE(SUM(vd.importe_descuento), 0) AS importe_descuento
                FROM venta_descuento vd
                INNER JOIN descuento d
                    ON d.id_descuento = vd.id_descuento
                INNER JOIN venta v
                    ON v.id_venta = vd.id_venta
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
            sql.append(" AND vd.fecha_aplicacion >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND vd.fecha_aplicacion < ? ");
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
                GROUP BY d.id_descuento, d.nombre, d.origen
                ORDER BY importe_descuento DESC, numero_usos DESC
                """);

        List<InformeDescuentosAplicadosRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeDescuentosAplicadosRowDTO row = new InformeDescuentosAplicadosRowDTO();
                    row.setIdDescuento(rs.getInt("id_descuento"));
                    row.setNombreDescuento(rs.getString("nombre_descuento"));
                    row.setTipoBeneficio(rs.getString("tipo_beneficio"));
                    row.setNumeroUsos(rs.getInt("numero_usos"));
                    row.setImporteBase(nvl(rs.getBigDecimal("importe_base")));
                    row.setImporteDescuento(nvl(rs.getBigDecimal("importe_descuento")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 1 - VENTAS POR FRANJA HORARIA
    // =====================================================

    // Qué hace:
    // Agregado = una fila por franja horaria de 2 horas.
    public List<InformeVentasFranjaRowDTO> findVentasPorFranjaAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    CONCAT(
                        LPAD(FLOOR(HOUR(v.fecha_venta) / 2) * 2, 2, '0'),
                        '-',
                        LPAD(FLOOR(HOUR(v.fecha_venta) / 2) * 2 + 2, 2, '0'),
                        'h'
                    ) AS franja,
                    FLOOR(HOUR(v.fecha_venta) / 2) * 2 AS franja_orden,
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
                GROUP BY franja_orden, franja
                ORDER BY franja_orden
                """);

        List<InformeVentasFranjaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
                    row.setFranja(rs.getString("franja"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Comparativo = una fila por franja horaria + empleado.
    public List<InformeVentasFranjaRowDTO> findVentasPorFranjaComparativo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    CONCAT(
                        LPAD(FLOOR(HOUR(v.fecha_venta) / 2) * 2, 2, '0'),
                        '-',
                        LPAD(FLOOR(HOUR(v.fecha_venta) / 2) * 2 + 2, 2, '0'),
                        'h'
                    ) AS franja,
                    FLOOR(HOUR(v.fecha_venta) / 2) * 2 AS franja_orden,
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    COUNT(*) AS numero_tickets,
                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE COALESCE(SUM(v.total), 0) / COUNT(*)
                    END AS ticket_medio
                FROM venta v
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
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
                GROUP BY franja_orden, franja, v.id_usuario, u.nombre
                ORDER BY franja_orden, u.nombre
                """);
        
        if (filtros.getTipoInforme() == TipoInforme.DESCUENTOS_APLICADOS
                && filtros.getTopN() != null
                && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeVentasFranjaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
                    row.setFranja(rs.getString("franja"));
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Devuelve devoluciones agregadas por franja horaria.
    public List<InformeVentasFranjaRowDTO> findDevolucionesPorFranjaAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    CONCAT(
                        LPAD(FLOOR(HOUR(d.fecha_devolucion) / 2) * 2, 2, '0'),
                        '-',
                        LPAD(FLOOR(HOUR(d.fecha_devolucion) / 2) * 2 + 2, 2, '0'),
                        'h'
                    ) AS franja,
                    FLOOR(HOUR(d.fecha_devolucion) / 2) * 2 AS franja_orden,
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
                GROUP BY franja_orden, franja
                ORDER BY franja_orden
                """);

        List<InformeVentasFranjaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
                    row.setFranja(rs.getString("franja"));
                    row.setTotalDevoluciones(nvl(rs.getBigDecimal("total_devoluciones")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Devuelve devoluciones por franja horaria + empleado original de la venta.
    public List<InformeVentasFranjaRowDTO> findDevolucionesPorFranjaComparativo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    CONCAT(
                        LPAD(FLOOR(HOUR(d.fecha_devolucion) / 2) * 2, 2, '0'),
                        '-',
                        LPAD(FLOOR(HOUR(d.fecha_devolucion) / 2) * 2 + 2, 2, '0'),
                        'h'
                    ) AS franja,
                    FLOOR(HOUR(d.fecha_devolucion) / 2) * 2 AS franja_orden,
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COALESCE(SUM(d.total_reembolsado), 0) AS total_devoluciones
                FROM devolucion d
                INNER JOIN venta v
                    ON v.id_venta = d.id_venta_original
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
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
                GROUP BY franja_orden, franja, v.id_usuario, u.nombre
                ORDER BY franja_orden, u.nombre
                """);

        List<InformeVentasFranjaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
                    row.setFranja(rs.getString("franja"));
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalDevoluciones(nvl(rs.getBigDecimal("total_devoluciones")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 1 - TICKET MEDIO POR DÍA
    // =====================================================

    // Qué hace:
    // Agregado = una fila por día con tickets, ventas y ticket medio.
    public List<InformeTicketMedioDiaRowDTO> findTicketMedioPorDiaAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(v.fecha_venta) AS fecha,
                    COUNT(*) AS numero_tickets,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
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

        List<InformeTicketMedioDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeTicketMedioDiaRowDTO row = new InformeTicketMedioDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Comparativo = una fila por día + empleado.
    public List<InformeTicketMedioDiaRowDTO> findTicketMedioPorDiaComparativo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(v.fecha_venta) AS fecha,
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COUNT(*) AS numero_tickets,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE COALESCE(SUM(v.total), 0) / COUNT(*)
                    END AS ticket_medio
                FROM venta v
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
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
                GROUP BY DATE(v.fecha_venta), v.id_usuario, u.nombre
                ORDER BY DATE(v.fecha_venta), u.nombre
                """);

        List<InformeTicketMedioDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeTicketMedioDiaRowDTO row = new InformeTicketMedioDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 1 - PAGOS POR MÉTODO
    // =====================================================

    // Qué hace:
    // Devuelve una fila por método de pago.
    // El porcentaje no lo calcula aquí: lo calcula el service.
    public List<InformePagosMetodoRowDTO> findPagosPorMetodo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    p.metodo AS metodo_pago,
                    COUNT(*) AS numero_operaciones,
                    COALESCE(SUM(p.monto), 0) AS importe_total
                FROM pago p
                INNER JOIN venta v
                    ON v.id_venta = p.id_venta
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                WHERE 1 = 1
                """);

        if (!filtros.isIncluirAnuladas()) {
            sql.append(" AND v.anulada = 0 ");
        }

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND p.fecha_pago >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND p.fecha_pago < ? ");
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

        sql.append("""
                GROUP BY p.metodo
                ORDER BY importe_total DESC
                """);

        List<InformePagosMetodoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformePagosMetodoRowDTO row = new InformePagosMetodoRowDTO();
                    row.setMetodoPago(rs.getString("metodo_pago"));
                    row.setNumeroOperaciones(rs.getInt("numero_operaciones"));
                    row.setImporteTotal(nvl(rs.getBigDecimal("importe_total")));
                    row.setPorcentajeSobreTotal(BigDecimal.ZERO);
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 1 - VENTAS NETAS VS DEVOLUCIONES
    // =====================================================

    // Qué hace:
    // Devuelve una fila por día con ventas, devoluciones, neto y ratio de devolución.
    // Reutiliza las consultas de ventas por día y devoluciones por día que ya tienes.
    public List<InformeNetoVsDevolucionesRowDTO> findVentasNetasVsDevoluciones(InformeFiltroDTO filtros) throws SQLException {
        List<InformeVentasPorDiaRowDTO> ventasRows = findVentasPorDiaAgregado(filtros);

        List<InformeVentasPorDiaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? findDevolucionesPorDiaAgregado(filtros)
                : Collections.emptyList();

        Map<LocalDate, InformeNetoVsDevolucionesRowDTO> rowsByDate = new TreeMap<>();

        // 1) Cargar ventas del día
        for (InformeVentasPorDiaRowDTO ventaRow : ventasRows) {
            InformeNetoVsDevolucionesRowDTO row = new InformeNetoVsDevolucionesRowDTO();
            row.setFecha(ventaRow.getFecha());
            row.setTotalVentas(nvl(ventaRow.getTotalVentas()));
            row.setTotalDevoluciones(BigDecimal.ZERO);
            row.setTotalNeto(nvl(ventaRow.getTotalVentas()));
            row.setRatioDevolucion(BigDecimal.ZERO);

            rowsByDate.put(row.getFecha(), row);
        }

        // 2) Mezclar devoluciones del día
        for (InformeVentasPorDiaRowDTO devRow : devolucionesRows) {
            LocalDate fecha = devRow.getFecha();
            if (fecha == null) {
                continue;
            }

            InformeNetoVsDevolucionesRowDTO existing = rowsByDate.get(fecha);
            if (existing == null) {
                existing = new InformeNetoVsDevolucionesRowDTO();
                existing.setFecha(fecha);
                existing.setTotalVentas(BigDecimal.ZERO);
                existing.setTotalDevoluciones(BigDecimal.ZERO);
                existing.setTotalNeto(BigDecimal.ZERO);
                existing.setRatioDevolucion(BigDecimal.ZERO);
                rowsByDate.put(fecha, existing);
            }

            existing.setTotalDevoluciones(nvl(devRow.getTotalDevoluciones()));
            existing.setTotalNeto(
                    nvl(existing.getTotalVentas()).subtract(nvl(existing.getTotalDevoluciones()))
            );
        }

        // 3) Calcular ratio de devolución diario
        for (InformeNetoVsDevolucionesRowDTO row : rowsByDate.values()) {
            BigDecimal ventas = nvl(row.getTotalVentas());
            BigDecimal devoluciones = nvl(row.getTotalDevoluciones());

            if (ventas.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = devoluciones
                        .multiply(BigDecimal.valueOf(100))
                        .divide(ventas, 2, RoundingMode.HALF_UP);

                row.setRatioDevolucion(ratio);
            } else {
                row.setRatioDevolucion(BigDecimal.ZERO);
            }
        }

        return new ArrayList<>(rowsByDate.values());
    }

    // =====================================================
    // HELPERS DEL BLOQUE 1
    // =====================================================

    private InformeResumenEjecutivoRowDTO buildResumenRow(String indicador, BigDecimal valor, String descripcion) {
        InformeResumenEjecutivoRowDTO row = new InformeResumenEjecutivoRowDTO();
        row.setIndicador(indicador);
        row.setValor(nvl(valor));
        row.setDescripcion(descripcion);
        return row;
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
    
    public List<InformeVentasPorDiaRowDTO> findVentasPorDiaComparativo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(v.fecha_venta) AS fecha,
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    COUNT(*) AS numero_tickets,
                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE COALESCE(SUM(v.total), 0) / COUNT(*)
                    END AS ticket_medio
                FROM venta v
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
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
                GROUP BY DATE(v.fecha_venta), v.id_usuario, u.nombre
                ORDER BY DATE(v.fecha_venta), u.nombre
                """);

        List<InformeVentasPorDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }
    
    public List<InformeVentasPorDiaRowDTO> findDevolucionesPorDiaComparativo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(d.fecha_devolucion) AS fecha,
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COALESCE(SUM(d.total_reembolsado), 0) AS total_devoluciones
                FROM devolucion d
                INNER JOIN venta v
                    ON v.id_venta = d.id_venta_original
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
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
                GROUP BY DATE(d.fecha_devolucion), v.id_usuario, u.nombre
                ORDER BY DATE(d.fecha_devolucion), u.nombre
                """);

        List<InformeVentasPorDiaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
                    row.setFecha(rs.getDate("fecha").toLocalDate());
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalDevoluciones(nvl(rs.getBigDecimal("total_devoluciones")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }
    
    // =====================================================
    // BLOQUE 2 - PRODUCTOS MÁS VENDIDOS
    // =====================================================

    // Qué hace:
    // Devuelve una fila por producto vendido.
    // Saca unidades vendidas e importe bruto.
    // Las devoluciones se dejan en 0 de momento para no complicar este informe base.
    public List<InformeProductosVendidosRowDTO> findProductosMasVendidos(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    p.id_producto,
                    p.nombre AS nombre_producto,
                    COALESCE(SUM(vi.cantidad), 0) AS unidades_vendidas,
                    COALESCE(SUM(vi.subtotal), 0) AS importe_bruto
                FROM venta_item vi
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY p.id_producto, p.nombre
                ORDER BY unidades_vendidas DESC, importe_bruto DESC
                """);
        
        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeProductosVendidosRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeProductosVendidosRowDTO row = new InformeProductosVendidosRowDTO();
                    row.setIdProducto(rs.getInt("id_producto"));
                    row.setNombreProducto(rs.getString("nombre_producto"));
                    row.setUnidadesVendidas(rs.getInt("unidades_vendidas"));
                    row.setImporteBruto(nvl(rs.getBigDecimal("importe_bruto")));
                    row.setImporteDevoluciones(BigDecimal.ZERO);
                    row.setImporteNeto(nvl(rs.getBigDecimal("importe_bruto")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 2 - EXTRAS MÁS VENDIDOS
    // =====================================================

    // Qué hace:
    // Devuelve una fila por extra vendido.
    // Usa venta_item_extra + extra.
    public List<InformeExtrasVendidosRowDTO> findExtrasMasVendidos(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    e.id_extra,
                    e.nombre AS nombre_extra,
                    e.tipo AS grupo_principal,
                    COUNT(*) AS veces_vendido,
                    COALESCE(SUM(vie.precio_extra), 0) AS importe_generado
                FROM venta_item_extra vie
                INNER JOIN extra e
                    ON e.id_extra = vie.id_extra
                INNER JOIN venta_item vi
                    ON vi.id_item = vie.id_item
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY e.id_extra, e.nombre, e.tipo
                ORDER BY veces_vendido DESC, importe_generado DESC
                """);
        
        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }
        
        List<InformeExtrasVendidosRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeExtrasVendidosRowDTO row = new InformeExtrasVendidosRowDTO();
                    row.setIdExtra(rs.getInt("id_extra"));
                    row.setNombreExtra(rs.getString("nombre_extra"));
                    row.setGrupoPrincipal(rs.getString("grupo_principal"));
                    row.setVecesVendido(rs.getInt("veces_vendido"));
                    row.setImporteGenerado(nvl(rs.getBigDecimal("importe_generado")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 2 - COMBOS VENDIDOS
    // =====================================================

    // Qué hace:
    // Reutiliza la consulta ya hecha en el bloque 1 para resumen ejecutivo.
    public List<InformeCombosVendidosRowDTO> findCombosVendidos(InformeFiltroDTO filtros) throws SQLException {
        return findResumenEjecutivoCombos(filtros);
    }

    // =====================================================
    // BLOQUE 2 - DESCUENTOS APLICADOS
    // =====================================================

    // Qué hace:
    // Reutiliza la consulta ya hecha en el bloque 1 para resumen ejecutivo.
    public List<InformeDescuentosAplicadosRowDTO> findDescuentosAplicados(InformeFiltroDTO filtros) throws SQLException {
        return findResumenEjecutivoDescuentos(filtros);
    }

    // =====================================================
    // BLOQUE 2 - DEVOLUCIONES POR PRODUCTO
    // =====================================================

    // Qué hace:
    // Devuelve una fila por producto devuelto.
    //
    // IMPORTANTE:
    // Este método asume estos nombres:
    // - devolucion_item.id_item_venta
    // - devolucion_item.importe_reembolso
    // - devolucion_item.repone_stock
    //
    // Si en tu BD real esos nombres cambian, solo ajusta este SQL.
    public List<InformeDevolucionesProductoRowDTO> findDevolucionesPorProducto(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    p.id_producto,
                    p.nombre AS nombre_producto,
                    COALESCE(SUM(di.cantidad_devuelta), 0) AS cantidad_devuelta,
                    COALESCE(SUM(di.subtotal_final_devuelto), 0) AS importe_reembolsado,
                    COUNT(DISTINCT d.id_devolucion) AS numero_devoluciones,
                    MAX(CASE WHEN di.repone_stock = 1 THEN 1 ELSE 0 END) AS repone_stock
                FROM devolucion_item di
                INNER JOIN devolucion d
                    ON d.id_devolucion = di.id_devolucion
                INNER JOIN venta_item vi
                    ON vi.id_item = di.id_item
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                INNER JOIN venta v
                    ON v.id_venta = d.id_venta_original
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
                GROUP BY p.id_producto, p.nombre
                ORDER BY cantidad_devuelta DESC, importe_reembolsado DESC
                """);

        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeDevolucionesProductoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeDevolucionesProductoRowDTO row = new InformeDevolucionesProductoRowDTO();
                    row.setIdProducto(rs.getInt("id_producto"));
                    row.setNombreProducto(rs.getString("nombre_producto"));
                    row.setCantidadDevuelta(rs.getInt("cantidad_devuelta"));
                    row.setImporteReembolsado(nvl(rs.getBigDecimal("importe_reembolsado")));
                    row.setNumeroDevoluciones(rs.getInt("numero_devoluciones"));
                    row.setReponeStock(rs.getInt("repone_stock") == 1);
                    rows.add(row);
                }
            }
        }

        return rows;
    }
    // =====================================================
    // BLOQUE 3 - RANKING EMPLEADOS POR VENTAS
    // =====================================================

    // Qué hace:
    // Devuelve una fila por empleado con ventas, tickets y ticket medio.
    public List<InformeRankingEmpleadosVentasRowDTO> findRankingEmpleadosPorVentas(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    COUNT(*) AS numero_tickets,
                    CASE
                        WHEN COUNT(*) = 0 THEN 0
                        ELSE COALESCE(SUM(v.total), 0) / COUNT(*)
                    END AS ticket_medio
                FROM venta v
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre
                ORDER BY total_ventas DESC, numero_tickets DESC
                """);

        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeRankingEmpleadosVentasRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    InformeRankingEmpleadosVentasRowDTO row = new InformeRankingEmpleadosVentasRowDTO();
                    row.setPosicion(posicion++);
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTicketMedio(nvl(rs.getBigDecimal("ticket_medio")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 3 - RANKING EMPLEADOS POR EXTRAS
    // =====================================================

    // Qué hace:
    // Devuelve una fila por empleado con total de extras vendidos e importe generado.
    public List<InformeRankingEmpleadosExtrasRowDTO> findRankingEmpleadosPorExtras(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    COUNT(*) AS total_extras_vendidos,
                    COALESCE(SUM(vie.precio_extra), 0) AS importe_extras
                FROM venta_item_extra vie
                INNER JOIN venta_item vi
                    ON vi.id_item = vie.id_item
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre
                ORDER BY total_extras_vendidos DESC, importe_extras DESC
                """);

        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeRankingEmpleadosExtrasRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    InformeRankingEmpleadosExtrasRowDTO row = new InformeRankingEmpleadosExtrasRowDTO();
                    row.setPosicion(posicion++);
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setTotalExtrasVendidos(rs.getInt("total_extras_vendidos"));
                    row.setImporteExtras(nvl(rs.getBigDecimal("importe_extras")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 3 - PRODUCTOS VENDIDOS POR EMPLEADO
    // =====================================================

    // Qué hace:
    // Agregado = una fila por empleado + producto.
    public List<InformeProductosPorEmpleadoRowDTO> findProductosVendidosPorEmpleadoAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    p.id_producto,
                    p.nombre AS nombre_producto,
                    COALESCE(SUM(vi.cantidad), 0) AS unidades_vendidas,
                    COALESCE(SUM(vi.subtotal), 0) AS importe_total
                FROM venta_item vi
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre, p.id_producto, p.nombre
                ORDER BY u.nombre, unidades_vendidas DESC, importe_total DESC
                """);

        List<InformeProductosPorEmpleadoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeProductosPorEmpleadoRowDTO row = new InformeProductosPorEmpleadoRowDTO();
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setIdProducto(rs.getInt("id_producto"));
                    row.setNombreProducto(rs.getString("nombre_producto"));
                    row.setUnidadesVendidas(rs.getInt("unidades_vendidas"));
                    row.setImporteTotal(nvl(rs.getBigDecimal("importe_total")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // Qué hace:
    // Comparativo = por ahora reutiliza la misma base.
    // Si luego quieres una comparativa visual distinta, el cambio se hará más en el Service/UI.
    public List<InformeProductosPorEmpleadoRowDTO> findProductosVendidosPorEmpleadoComparativo(InformeFiltroDTO filtros) throws SQLException {
        return findProductosVendidosPorEmpleadoAgregado(filtros);
    }

    // =====================================================
    // BLOQUE 4 - VENTAS POR CAJA
    // =====================================================

    // Qué hace:
    // Devuelve una fila por caja con ventas, devoluciones, neto y tickets.
    public List<InformeVentasCajaRowDTO> findVentasPorCaja(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    c.id_caja,
                    c.nombre AS nombre_caja,
                    COALESCE(SUM(v.total), 0) AS total_ventas,
                    COUNT(*) AS numero_tickets
                FROM venta v
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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
            sql.append(" AND c.id_caja = ? ");
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
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY c.id_caja, c.nombre
                ORDER BY total_ventas DESC, numero_tickets DESC
                """);

        List<InformeVentasCajaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasCajaRowDTO row = new InformeVentasCajaRowDTO();
                    row.setIdCaja(rs.getInt("id_caja"));
                    row.setNombreCaja(rs.getString("nombre_caja"));
                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setNumeroTickets(rs.getInt("numero_tickets"));
                    row.setTotalDevoluciones(BigDecimal.ZERO);
                    row.setTotalNeto(nvl(rs.getBigDecimal("total_ventas")));
                    rows.add(row);
                }
            }
        }

        // Mezclar devoluciones por caja
        mergeDevolucionesEnVentasCaja(rows, filtros);

        return rows;
    }

    // =====================================================
    // BLOQUE 4 - VENTAS POR SESIÓN DE CAJA
    // =====================================================

    // Qué hace:
    // Devuelve una fila por sesión de caja.
    public List<InformeVentasSesionCajaRowDTO> findVentasPorSesionCaja(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    sc.id_sesion,
                    c.id_caja,
                    c.nombre AS nombre_caja,
                    sc.id_usuario_apertura AS id_empleado_apertura,
                    u.nombre AS nombre_empleado_apertura,
                    sc.fecha_apertura,
                    sc.fecha_cierre,
                    COALESCE(SUM(v.total), 0) AS total_ventas
                FROM sesion_caja sc
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN usuario u
                    ON u.id_usuario = sc.id_usuario_apertura
                LEFT JOIN venta v
                    ON v.id_sesion = sc.id_sesion
                    AND (v.anulada = 0 OR ? = 1)
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
                WHERE 1 = 1
                """);

        // truco para no duplicar lógica al filtrar anuladas en el LEFT JOIN
        params.add(filtros.isIncluirAnuladas());

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND sc.fecha_apertura >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND sc.fecha_apertura < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        if (filtros.getIdSucursal() != null) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(filtros.getIdSucursal());
        }

        if (filtros.getIdCaja() != null) {
            sql.append(" AND c.id_caja = ? ");
            params.add(filtros.getIdCaja());
        }

        if (!filtros.isTodosLosEmpleados()
                && filtros.getIdsEmpleados() != null
                && !filtros.getIdsEmpleados().isEmpty()) {
            sql.append(" AND sc.id_usuario_apertura IN (");
            appendPlaceholders(sql, filtros.getIdsEmpleados().size());
            sql.append(") ");
            params.addAll(filtros.getIdsEmpleados());
        }

        sql.append("""
                GROUP BY sc.id_sesion, c.id_caja, c.nombre, sc.id_usuario_apertura, u.nombre, sc.fecha_apertura, sc.fecha_cierre
                ORDER BY sc.fecha_apertura DESC
                """);

        List<InformeVentasSesionCajaRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasSesionCajaRowDTO row = new InformeVentasSesionCajaRowDTO();
                    row.setIdSesion(rs.getInt("id_sesion"));
                    row.setIdCaja(rs.getInt("id_caja"));
                    row.setNombreCaja(rs.getString("nombre_caja"));
                    row.setIdEmpleadoApertura(rs.getInt("id_empleado_apertura"));
                    row.setNombreEmpleadoApertura(rs.getString("nombre_empleado_apertura"));

                    Timestamp tsApertura = rs.getTimestamp("fecha_apertura");
                    Timestamp tsCierre = rs.getTimestamp("fecha_cierre");

                    row.setFechaApertura(tsApertura != null ? tsApertura.toLocalDateTime() : null);
                    row.setFechaCierre(tsCierre != null ? tsCierre.toLocalDateTime() : null);

                    row.setTotalVentas(nvl(rs.getBigDecimal("total_ventas")));
                    row.setTotalDevoluciones(BigDecimal.ZERO);
                    row.setTotalNeto(nvl(rs.getBigDecimal("total_ventas")));
                    rows.add(row);
                }
            }
        }

        mergeDevolucionesEnVentasSesion(rows, filtros);

        return rows;
    }

    // =====================================================
    // BLOQUE 4 - TIEMPOS POR ESTACIÓN
    // =====================================================

    // Qué hace:
    // Devuelve una fila por estación con tiempos medios.
    //
    // IMPORTANTE:
    // Este método asume en cola_impresion:
    // - fecha_creacion
    // - fecha_preparacion
    // Si en tu esquema usas otro nombre real, ajusta solo este SQL.
    public List<InformeTiemposEstacionRowDTO> findTiemposPorEstacion(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    e.id_estacion,
                    e.nombre AS nombre_estacion,
                    COALESCE(AVG(TIMESTAMPDIFF(SECOND, ci.fecha_creacion, ci.fecha_preparado)), 0) AS tiempo_medio_segundos,
                    COALESCE(MAX(TIMESTAMPDIFF(SECOND, ci.fecha_creacion, ci.fecha_preparado)), 0) AS tiempo_maximo_segundos,
                    COUNT(*) AS items_procesados
                FROM cola_impresion ci
                INNER JOIN estacion e
                    ON e.id_estacion = ci.id_estacion
                INNER JOIN venta_item vi
                    ON vi.id_item = ci.id_item
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                WHERE ci.fecha_preparado IS NOT NULL
                """);

        if (!filtros.isIncluirAnuladas()) {
            sql.append(" AND v.anulada = 0 ");
        }

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND ci.fecha_creacion >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND ci.fecha_creacion < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        if (filtros.getIdSucursal() != null) {
            sql.append(" AND c.id_sucursal = ? ");
            params.add(filtros.getIdSucursal());
        }

        sql.append("""
                GROUP BY e.id_estacion, e.nombre
                ORDER BY tiempo_medio_segundos DESC
                """);

        List<InformeTiemposEstacionRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeTiemposEstacionRowDTO row = new InformeTiemposEstacionRowDTO();
                    row.setIdEstacion(rs.getInt("id_estacion"));
                    row.setNombreEstacion(rs.getString("nombre_estacion"));
                    row.setTiempoMedioSegundos(nvl(rs.getBigDecimal("tiempo_medio_segundos")));
                    row.setTiempoMaximoSegundos(nvl(rs.getBigDecimal("tiempo_maximo_segundos")));
                    row.setItemsProcesados(rs.getInt("items_procesados"));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 4 - MERMA POR PERÍODO
    // =====================================================

    // Qué hace:
    // Devuelve una fila por registro de merma.
    //
    // IMPORTANTE:
    // Este método asume:
    // - merma.fecha_merma
    // - merma.tipo_merma
    // - merma.origen
    // - merma.motivo
    // - merma.observaciones
    // - merma_item.cantidad
    //
    // Si en tu BD real algún nombre cambia, ajusta este SQL.
    public List<InformeMermaPeriodoRowDTO> findMermaPorPeriodo(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    DATE(m.fecha_merma) AS fecha,
                    m.tipo_merma,
                    m.origen,
                    m.motivo,
                    COALESCE(SUM(mi.cantidad), 0) AS cantidad,
                    m.observaciones
                FROM merma m
                INNER JOIN merma_item mi
                    ON mi.id_merma = m.id_merma
                WHERE 1 = 1
                """);

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND m.fecha_merma >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND m.fecha_merma < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        sql.append("""
                GROUP BY DATE(m.fecha_merma), m.tipo_merma, m.origen, m.motivo, m.observaciones
                ORDER BY fecha DESC
                """);

        List<InformeMermaPeriodoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeMermaPeriodoRowDTO row = new InformeMermaPeriodoRowDTO();
                    Date fecha = rs.getDate("fecha");
                    row.setFecha(fecha != null ? fecha.toLocalDate() : null);
                    row.setTipoMerma(rs.getString("tipo_merma"));
                    row.setOrigen(rs.getString("origen"));
                    row.setMotivo(rs.getString("motivo"));
                    row.setCantidad(nvl(rs.getBigDecimal("cantidad")));
                    row.setObservaciones(rs.getString("observaciones"));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 4 - MOVIMIENTOS DE STOCK / AJUSTES
    // =====================================================

    // Qué hace:
    // Devuelve una fila por movimiento de stock.
    //
    // IMPORTANTE:
    // Este método asume:
    // - movimiento_stock.fecha_movimiento
    // - movimiento_stock.tipo_movimiento
    // - movimiento_stock.tipo_objeto
    // - movimiento_stock.nombre_objeto
    // - movimiento_stock.cantidad
    // - movimiento_stock.motivo
    // - movimiento_stock.referencia
    //
    // Si tu tabla real tiene otros nombres, ajusta este SQL.
    public List<InformeMovimientoStockRowDTO> findMovimientosStockAjustes(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    ms.fecha AS fecha_movimiento,
                    CASE
                        WHEN ms.id_merma IS NOT NULL OR ms.id_merma_item IS NOT NULL THEN 'MERMA'
                        ELSE ms.tipo
                    END AS tipo_movimiento,
                    CASE
                        WHEN ms.id_producto IS NOT NULL THEN 'PRODUCTO'
                        WHEN ms.id_ingrediente IS NOT NULL THEN 'INGREDIENTE'
                        ELSE 'DESCONOCIDO'
                    END AS tipo_objeto,
                    CASE
                        WHEN ms.id_producto IS NOT NULL THEN p.nombre
                        WHEN ms.id_ingrediente IS NOT NULL THEN CONCAT('Ingrediente #', ms.id_ingrediente)
                        ELSE 'Sin objeto'
                    END AS nombre_objeto,
                    ms.cantidad,
                    ms.motivo,
                    ms.referencia
                FROM movimiento_stock ms
                LEFT JOIN producto p
                    ON p.id_producto = ms.id_producto
                WHERE 1 = 1
                """);

        if (filtros.getFechaDesde() != null) {
            sql.append(" AND ms.fecha >= ? ");
            params.add(Timestamp.valueOf(filtros.getFechaDesde().atStartOfDay()));
        }

        if (filtros.getFechaHasta() != null) {
            sql.append(" AND ms.fecha < ? ");
            params.add(Timestamp.valueOf(filtros.getFechaHasta().plusDays(1).atStartOfDay()));
        }

        if (filtros.getIdSucursal() != null) {
            sql.append(" AND ms.id_sucursal = ? ");
            params.add(filtros.getIdSucursal());
        }

        sql.append("""
                ORDER BY ms.fecha DESC
                """);

        List<InformeMovimientoStockRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeMovimientoStockRowDTO row = new InformeMovimientoStockRowDTO();

                    Timestamp ts = rs.getTimestamp("fecha_movimiento");
                    row.setFecha(ts != null ? ts.toLocalDateTime() : null);
                    row.setTipoMovimiento(rs.getString("tipo_movimiento"));
                    row.setTipoObjeto(rs.getString("tipo_objeto"));
                    row.setNombreObjeto(rs.getString("nombre_objeto"));
                    row.setCantidad(nvl(rs.getBigDecimal("cantidad")));
                    row.setMotivo(rs.getString("motivo"));
                    row.setReferencia(rs.getString("referencia"));

                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // HELPERS PRIVADOS BLOQUES 3 Y 4
    // =====================================================

    private void mergeDevolucionesEnVentasCaja(List<InformeVentasCajaRowDTO> rows, InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    c.id_caja,
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
            sql.append(" AND c.id_caja = ? ");
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

        sql.append("""
                GROUP BY c.id_caja
                """);

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCaja = rs.getInt("id_caja");
                    BigDecimal totalDev = nvl(rs.getBigDecimal("total_devoluciones"));

                    for (InformeVentasCajaRowDTO row : rows) {
                        if (row.getIdCaja() != null && row.getIdCaja() == idCaja) {
                            row.setTotalDevoluciones(totalDev);
                            row.setTotalNeto(safe(row.getTotalVentas()).subtract(totalDev));
                            break;
                        }
                    }
                }
            }
        }
    }

    private void mergeDevolucionesEnVentasSesion(List<InformeVentasSesionCajaRowDTO> rows, InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    sc.id_sesion,
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
            sql.append(" AND c.id_caja = ? ");
            params.add(filtros.getIdCaja());
        }

        if (!filtros.isTodosLosEmpleados()
                && filtros.getIdsEmpleados() != null
                && !filtros.getIdsEmpleados().isEmpty()) {
            sql.append(" AND sc.id_usuario_apertura IN (");
            appendPlaceholders(sql, filtros.getIdsEmpleados().size());
            sql.append(") ");
            params.addAll(filtros.getIdsEmpleados());
        }

        sql.append("""
                GROUP BY sc.id_sesion
                """);

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idSesion = rs.getInt("id_sesion");
                    BigDecimal totalDev = nvl(rs.getBigDecimal("total_devoluciones"));

                    for (InformeVentasSesionCajaRowDTO row : rows) {
                        if (row.getIdSesion() != null && row.getIdSesion() == idSesion) {
                            row.setTotalDevoluciones(totalDev);
                            row.setTotalNeto(safe(row.getTotalVentas()).subtract(totalDev));
                            break;
                        }
                    }
                }
            }
        }
    }
    
    // =====================================================
    // BLOQUE 5 - VENTAS PRODUCTO POR EMPLEADO
    // =====================================================

    public List<InformeVentasProductoEmpleadoRowDTO> findVentasProductoPorEmpleadoAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    p.id_producto,
                    p.nombre AS nombre_producto,
                    COALESCE(SUM(vi.cantidad), 0) AS unidades_vendidas,
                    COALESCE(SUM(vi.subtotal_bruto), 0) AS importe_bruto,
                    COALESCE(SUM(vi.importe_descuento_linea), 0) AS importe_descuento,
                    COALESCE(SUM(vi.subtotal_final), 0) AS importe_neto
                FROM venta_item vi
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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

        if (!filtros.isTodosLosProductos()
                && filtros.getIdsProductos() != null
                && !filtros.getIdsProductos().isEmpty()) {
            sql.append(" AND p.id_producto IN (");
            appendPlaceholders(sql, filtros.getIdsProductos().size());
            sql.append(") ");
            params.addAll(filtros.getIdsProductos());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre, p.id_producto, p.nombre
                ORDER BY u.nombre, unidades_vendidas DESC, importe_neto DESC
                """);

        List<InformeVentasProductoEmpleadoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasProductoEmpleadoRowDTO row = new InformeVentasProductoEmpleadoRowDTO();
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setIdProducto(rs.getInt("id_producto"));
                    row.setNombreProducto(rs.getString("nombre_producto"));
                    row.setUnidadesVendidas(rs.getInt("unidades_vendidas"));
                    row.setImporteBruto(nvl(rs.getBigDecimal("importe_bruto")));
                    row.setImporteDescuento(nvl(rs.getBigDecimal("importe_descuento")));
                    row.setImporteNeto(nvl(rs.getBigDecimal("importe_neto")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    public List<InformeVentasProductoEmpleadoRowDTO> findVentasProductoPorEmpleadoComparativo(InformeFiltroDTO filtros) throws SQLException {
        return findVentasProductoPorEmpleadoAgregado(filtros);
    }

    // =====================================================
    // BLOQUE 5 - RANKING EMPLEADOS POR PRODUCTO
    // =====================================================

    public List<InformeRankingEmpleadosProductoRowDTO> findRankingEmpleadosPorProducto(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    p.id_producto,
                    p.nombre AS nombre_producto,
                    COALESCE(SUM(vi.cantidad), 0) AS unidades_vendidas,
                    COALESCE(SUM(vi.subtotal_final), 0) AS importe_neto
                FROM venta_item vi
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN producto p
                    ON p.id_producto = vi.id_producto
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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

        if (!filtros.isTodosLosProductos()
                && filtros.getIdsProductos() != null
                && !filtros.getIdsProductos().isEmpty()) {
            sql.append(" AND p.id_producto IN (");
            appendPlaceholders(sql, filtros.getIdsProductos().size());
            sql.append(") ");
            params.addAll(filtros.getIdsProductos());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre, p.id_producto, p.nombre
                ORDER BY unidades_vendidas DESC, importe_neto DESC
                """);

        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeRankingEmpleadosProductoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    InformeRankingEmpleadosProductoRowDTO row = new InformeRankingEmpleadosProductoRowDTO();
                    row.setPosicion(posicion++);
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setIdProducto(rs.getInt("id_producto"));
                    row.setNombreProducto(rs.getString("nombre_producto"));
                    row.setUnidadesVendidas(rs.getInt("unidades_vendidas"));
                    row.setImporteNeto(nvl(rs.getBigDecimal("importe_neto")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    // =====================================================
    // BLOQUE 5 - VENTAS EXTRA POR EMPLEADO
    // =====================================================

    public List<InformeVentasExtraEmpleadoRowDTO> findVentasExtraPorEmpleadoAgregado(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    e.id_extra,
                    e.nombre AS nombre_extra,
                    e.tipo AS tipo_extra,
                    COUNT(*) AS veces_vendido,
                    COALESCE(SUM(vie.precio_extra), 0) AS importe_generado
                FROM venta_item_extra vie
                INNER JOIN extra e
                    ON e.id_extra = vie.id_extra
                INNER JOIN venta_item vi
                    ON vi.id_item = vie.id_item
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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

        if (!filtros.isTodosLosExtras()
                && filtros.getIdsExtras() != null
                && !filtros.getIdsExtras().isEmpty()) {
            sql.append(" AND e.id_extra IN (");
            appendPlaceholders(sql, filtros.getIdsExtras().size());
            sql.append(") ");
            params.addAll(filtros.getIdsExtras());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre, e.id_extra, e.nombre, e.tipo
                ORDER BY u.nombre, veces_vendido DESC, importe_generado DESC
                """);

        List<InformeVentasExtraEmpleadoRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InformeVentasExtraEmpleadoRowDTO row = new InformeVentasExtraEmpleadoRowDTO();
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setIdExtra(rs.getInt("id_extra"));
                    row.setNombreExtra(rs.getString("nombre_extra"));
                    row.setTipoExtra(rs.getString("tipo_extra"));
                    row.setVecesVendido(rs.getInt("veces_vendido"));
                    row.setImporteGenerado(nvl(rs.getBigDecimal("importe_generado")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    public List<InformeVentasExtraEmpleadoRowDTO> findVentasExtraPorEmpleadoComparativo(InformeFiltroDTO filtros) throws SQLException {
        return findVentasExtraPorEmpleadoAgregado(filtros);
    }

    // =====================================================
    // BLOQUE 5 - RANKING EMPLEADOS POR EXTRA
    // =====================================================

    public List<InformeRankingEmpleadosExtraRowDTO> findRankingEmpleadosPorExtra(InformeFiltroDTO filtros) throws SQLException {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("""
                SELECT
                    v.id_usuario AS id_empleado,
                    u.nombre AS nombre_empleado,
                    e.id_extra,
                    e.nombre AS nombre_extra,
                    e.tipo AS tipo_extra,
                    COUNT(*) AS veces_vendido,
                    COALESCE(SUM(vie.precio_extra), 0) AS importe_generado
                FROM venta_item_extra vie
                INNER JOIN extra e
                    ON e.id_extra = vie.id_extra
                INNER JOIN venta_item vi
                    ON vi.id_item = vie.id_item
                INNER JOIN venta v
                    ON v.id_venta = vi.id_venta
                INNER JOIN usuario u
                    ON u.id_usuario = v.id_usuario
                INNER JOIN sesion_caja sc
                    ON sc.id_sesion = v.id_sesion
                INNER JOIN caja c
                    ON c.id_caja = sc.id_caja
                LEFT JOIN pago pa
                    ON pa.id_venta = v.id_venta
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

        if (!filtros.isTodosLosExtras()
                && filtros.getIdsExtras() != null
                && !filtros.getIdsExtras().isEmpty()) {
            sql.append(" AND e.id_extra IN (");
            appendPlaceholders(sql, filtros.getIdsExtras().size());
            sql.append(") ");
            params.addAll(filtros.getIdsExtras());
        }

        if (filtros.getMetodoPago() != null && !filtros.getMetodoPago().isBlank()) {
            sql.append(" AND UPPER(pa.metodo) = UPPER(?) ");
            params.add(filtros.getMetodoPago());
        }

        sql.append("""
                GROUP BY v.id_usuario, u.nombre, e.id_extra, e.nombre, e.tipo
                ORDER BY veces_vendido DESC, importe_generado DESC
                """);

        if (filtros.getTopN() != null && filtros.getTopN() > 0) {
            sql.append(" LIMIT ? ");
            params.add(filtros.getTopN());
        }

        List<InformeRankingEmpleadosExtraRowDTO> rows = new ArrayList<>();

        try (Connection cn = DbPool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    InformeRankingEmpleadosExtraRowDTO row = new InformeRankingEmpleadosExtraRowDTO();
                    row.setPosicion(posicion++);
                    row.setIdEmpleado(rs.getInt("id_empleado"));
                    row.setNombreEmpleado(rs.getString("nombre_empleado"));
                    row.setIdExtra(rs.getInt("id_extra"));
                    row.setNombreExtra(rs.getString("nombre_extra"));
                    row.setTipoExtra(rs.getString("tipo_extra"));
                    row.setVecesVendido(rs.getInt("veces_vendido"));
                    row.setImporteGenerado(nvl(rs.getBigDecimal("importe_generado")));
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
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
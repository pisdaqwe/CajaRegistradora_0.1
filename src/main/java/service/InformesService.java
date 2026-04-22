package service;

import dao.InformesDao;
import dtoS.InformeCombosVendidosResultDTO;
import dtoS.InformeCombosVendidosRowDTO;
import dtoS.InformeDescuentosAplicadosResultDTO;
import dtoS.InformeDescuentosAplicadosRowDTO;
import dtoS.InformeDevolucionesProductoResultDTO;
import dtoS.InformeDevolucionesProductoRowDTO;
import dtoS.InformeExtrasVendidosResultDTO;
import dtoS.InformeExtrasVendidosRowDTO;
import dtoS.InformeFiltroDTO;
import dtoS.InformeMermaPeriodoResultDTO;
import dtoS.InformeMermaPeriodoRowDTO;
import dtoS.InformeMovimientoStockResultDTO;
import dtoS.InformeMovimientoStockRowDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformeNetoVsDevolucionesRowDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformePagosMetodoRowDTO;
import dtoS.InformeProductosPorEmpleadoResultDTO;
import dtoS.InformeProductosPorEmpleadoRowDTO;
import dtoS.InformeProductosVendidosResultDTO;
import dtoS.InformeProductosVendidosRowDTO;
import dtoS.InformeRankingEmpleadosExtraResultDTO;
import dtoS.InformeRankingEmpleadosExtraRowDTO;
import dtoS.InformeRankingEmpleadosExtrasResultDTO;
import dtoS.InformeRankingEmpleadosExtrasRowDTO;
import dtoS.InformeRankingEmpleadosProductoResultDTO;
import dtoS.InformeRankingEmpleadosProductoRowDTO;
import dtoS.InformeRankingEmpleadosVentasResultDTO;
import dtoS.InformeRankingEmpleadosVentasRowDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformeTicketMedioDiaRowDTO;
import dtoS.InformeTiemposEstacionResultDTO;
import dtoS.InformeTiemposEstacionRowDTO;
import dtoS.InformeVentasCajaResultDTO;
import dtoS.InformeVentasCajaRowDTO;
import dtoS.InformeVentasExtraEmpleadoResultDTO;
import dtoS.InformeVentasExtraEmpleadoRowDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeVentasFranjaRowDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeVentasPorDiaRowDTO;
import dtoS.InformeVentasProductoEmpleadoResultDTO;
import dtoS.InformeVentasProductoEmpleadoRowDTO;
import dtoS.InformeVentasSesionCajaResultDTO;
import dtoS.InformeVentasSesionCajaRowDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class InformesService {

    private final InformesDao informesDao;

    public InformesService(InformesDao informesDao) {
        this.informesDao = Objects.requireNonNull(informesDao, "informesDao no puede ser null");
    }
    public InformeVentasPorDiaResultDTO getVentasPorDia(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_POR_DIA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_POR_DIA");
        }

        try {
            return filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                    ? buildVentasPorDiaComparativo(filtros)
                    : buildVentasPorDiaAgregado(filtros);

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas por día", e);
        }
    }
    
    private InformeVentasPorDiaResultDTO buildVentasPorDiaAgregado(InformeFiltroDTO filtros) throws Exception {
        List<InformeVentasPorDiaRowDTO> ventasRows = informesDao.findVentasPorDiaAgregado(filtros);

        List<InformeVentasPorDiaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? informesDao.findDevolucionesPorDiaAgregado(filtros)
                : Collections.emptyList();

        Map<LocalDate, InformeVentasPorDiaRowDTO> rowsByDate = new TreeMap<>();

        for (InformeVentasPorDiaRowDTO ventaRow : ventasRows) {
            InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
            row.setFecha(ventaRow.getFecha());
            row.setTotalVentas(safe(ventaRow.getTotalVentas()));
            row.setNumeroTickets(safeInt(ventaRow.getNumeroTickets()));
            row.setTicketMedio(safe(ventaRow.getTicketMedio()));
            row.setTotalDevoluciones(BigDecimal.ZERO);
            row.setTotalNeto(safe(ventaRow.getTotalVentas()));
            rowsByDate.put(row.getFecha(), row);
        }

        for (InformeVentasPorDiaRowDTO devRow : devolucionesRows) {
            LocalDate fecha = devRow.getFecha();
            if (fecha == null) {
                continue;
            }

            InformeVentasPorDiaRowDTO existing = rowsByDate.get(fecha);
            if (existing == null) {
                existing = new InformeVentasPorDiaRowDTO();
                existing.setFecha(fecha);
                existing.setTotalVentas(BigDecimal.ZERO);
                existing.setNumeroTickets(0);
                existing.setTicketMedio(BigDecimal.ZERO);
                rowsByDate.put(fecha, existing);
            }

            existing.setTotalDevoluciones(safe(devRow.getTotalDevoluciones()));
            existing.setTotalNeto(
                    safe(existing.getTotalVentas()).subtract(safe(existing.getTotalDevoluciones()))
            );
        }

        for (InformeVentasPorDiaRowDTO row : rowsByDate.values()) {
            if (row.getTotalNeto() == null) {
                row.setTotalNeto(
                        safe(row.getTotalVentas()).subtract(safe(row.getTotalDevoluciones()))
                );
            }
        }

        return buildResult(new ArrayList<>(rowsByDate.values()));
    }

    private InformeVentasPorDiaResultDTO buildVentasPorDiaComparativo(InformeFiltroDTO filtros) throws Exception {
        List<InformeVentasPorDiaRowDTO> ventasRows = informesDao.findVentasPorDiaComparativo(filtros);

        List<InformeVentasPorDiaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? informesDao.findDevolucionesPorDiaComparativo(filtros)
                : Collections.emptyList();

        Map<String, InformeVentasPorDiaRowDTO> rowsByKey = new LinkedHashMap<>();

        for (InformeVentasPorDiaRowDTO ventaRow : ventasRows) {
            InformeVentasPorDiaRowDTO row = new InformeVentasPorDiaRowDTO();
            row.setFecha(ventaRow.getFecha());
            row.setIdEmpleado(ventaRow.getIdEmpleado());
            row.setNombreEmpleado(ventaRow.getNombreEmpleado());
            row.setTotalVentas(safe(ventaRow.getTotalVentas()));
            row.setNumeroTickets(safeInt(ventaRow.getNumeroTickets()));
            row.setTicketMedio(safe(ventaRow.getTicketMedio()));
            row.setTotalDevoluciones(BigDecimal.ZERO);
            row.setTotalNeto(safe(ventaRow.getTotalVentas()));

            rowsByKey.put(buildComparativeKey(row.getFecha(), row.getIdEmpleado()), row);
        }

        for (InformeVentasPorDiaRowDTO devRow : devolucionesRows) {
            String key = buildComparativeKey(devRow.getFecha(), devRow.getIdEmpleado());
            InformeVentasPorDiaRowDTO existing = rowsByKey.get(key);

            if (existing == null) {
                existing = new InformeVentasPorDiaRowDTO();
                existing.setFecha(devRow.getFecha());
                existing.setIdEmpleado(devRow.getIdEmpleado());
                existing.setNombreEmpleado(devRow.getNombreEmpleado());
                existing.setTotalVentas(BigDecimal.ZERO);
                existing.setNumeroTickets(0);
                existing.setTicketMedio(BigDecimal.ZERO);
                rowsByKey.put(key, existing);
            }

            existing.setTotalDevoluciones(safe(devRow.getTotalDevoluciones()));
            existing.setTotalNeto(
                    safe(existing.getTotalVentas()).subtract(safe(existing.getTotalDevoluciones()))
            );
        }

        List<InformeVentasPorDiaRowDTO> orderedRows = new ArrayList<>(rowsByKey.values());
        orderedRows.sort(Comparator
                .comparing(InformeVentasPorDiaRowDTO::getFecha)
                .thenComparing(r -> r.getNombreEmpleado() != null ? r.getNombreEmpleado() : ""));

        return buildResult(orderedRows);
    }

    private InformeVentasPorDiaResultDTO buildResult(List<InformeVentasPorDiaRowDTO> rows) {
        InformeVentasPorDiaResultDTO result = new InformeVentasPorDiaResultDTO();
        result.setRows(rows);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalDevoluciones = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;
        int totalTickets = 0;

        LocalDate fechaMejorDia = null;
        BigDecimal importeMejorDia = null;

        Map<LocalDate, BigDecimal> netoPorDia = new HashMap<>();

        for (InformeVentasPorDiaRowDTO row : rows) {
            totalVentas = totalVentas.add(safe(row.getTotalVentas()));
            totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
            totalNeto = totalNeto.add(safe(row.getTotalNeto()));
            totalTickets += safeInt(row.getNumeroTickets());

            netoPorDia.merge(row.getFecha(), safe(row.getTotalNeto()), BigDecimal::add);
        }

        for (Map.Entry<LocalDate, BigDecimal> entry : netoPorDia.entrySet()) {
            if (importeMejorDia == null || entry.getValue().compareTo(importeMejorDia) > 0) {
                fechaMejorDia = entry.getKey();
                importeMejorDia = entry.getValue();
            }
        }

        result.setTotalVentas(totalVentas);
        result.setTotalDevoluciones(totalDevoluciones);
        result.setTotalNeto(totalNeto);
        result.setTotalTickets(totalTickets);

        if (totalTickets > 0) {
            result.setTicketMedioGlobal(
                    totalVentas.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP)
            );
        } else {
            result.setTicketMedioGlobal(BigDecimal.ZERO);
        }

        result.setFechaMejorDia(fechaMejorDia);
        result.setImporteMejorDia(importeMejorDia != null ? importeMejorDia : BigDecimal.ZERO);

        return result;
    }
    // =====================================================
    // BLOQUE 1 - RESUMEN EJECUTIVO
    // =====================================================

    // Qué hace:
    // Construye el resumen ejecutivo completo a partir del DAO.
    // El DAO ya devuelve la base y aquí solo se consolida el resultado.
    public InformeResumenEjecutivoResultDTO getResumenEjecutivo(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.RESUMEN_EJECUTIVO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.RESUMEN_EJECUTIVO");
        }

        try {
            return informesDao.findResumenEjecutivoBase(filtros);
        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de resumen ejecutivo", e);
        }
    }

    // =====================================================
    // BLOQUE 1 - VENTAS POR FRANJA HORARIA
    // =====================================================

    // Qué hace:
    // Construye el informe por franjas horarias.
    // Si el filtro viene en comparativa, separa por franja + empleado.
    public InformeVentasFranjaResultDTO getVentasPorFranjaHoraria(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_POR_FRANJA_HORARIA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_POR_FRANJA_HORARIA");
        }

        try {
            return filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                    ? buildVentasPorFranjaComparativo(filtros)
                    : buildVentasPorFranjaAgregado(filtros);

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas por franja horaria", e);
        }
    }

    private InformeVentasFranjaResultDTO buildVentasPorFranjaAgregado(InformeFiltroDTO filtros) throws Exception {
        List<InformeVentasFranjaRowDTO> ventasRows = informesDao.findVentasPorFranjaAgregado(filtros);

        List<InformeVentasFranjaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? informesDao.findDevolucionesPorFranjaAgregado(filtros)
                : Collections.emptyList();

        Map<String, InformeVentasFranjaRowDTO> rowsByFranja = new LinkedHashMap<>();

        for (InformeVentasFranjaRowDTO ventaRow : ventasRows) {
            InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
            row.setFranja(ventaRow.getFranja());
            row.setTotalVentas(safe(ventaRow.getTotalVentas()));
            row.setNumeroTickets(safeInt(ventaRow.getNumeroTickets()));
            row.setTicketMedio(safe(ventaRow.getTicketMedio()));
            row.setTotalDevoluciones(BigDecimal.ZERO);
            row.setTotalNeto(safe(ventaRow.getTotalVentas()));

            rowsByFranja.put(row.getFranja(), row);
        }

        for (InformeVentasFranjaRowDTO devRow : devolucionesRows) {
            String franja = devRow.getFranja();
            if (franja == null) {
                continue;
            }

            InformeVentasFranjaRowDTO existing = rowsByFranja.get(franja);
            if (existing == null) {
                existing = new InformeVentasFranjaRowDTO();
                existing.setFranja(franja);
                existing.setTotalVentas(BigDecimal.ZERO);
                existing.setNumeroTickets(0);
                existing.setTicketMedio(BigDecimal.ZERO);
                rowsByFranja.put(franja, existing);
            }

            existing.setTotalDevoluciones(safe(devRow.getTotalDevoluciones()));
            existing.setTotalNeto(
                    safe(existing.getTotalVentas()).subtract(safe(existing.getTotalDevoluciones()))
            );
        }

        return buildVentasFranjaResult(new ArrayList<>(rowsByFranja.values()));
    }

    private InformeVentasFranjaResultDTO buildVentasPorFranjaComparativo(InformeFiltroDTO filtros) throws Exception {
        List<InformeVentasFranjaRowDTO> ventasRows = informesDao.findVentasPorFranjaComparativo(filtros);

        List<InformeVentasFranjaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                ? informesDao.findDevolucionesPorFranjaComparativo(filtros)
                : Collections.emptyList();

        Map<String, InformeVentasFranjaRowDTO> rowsByKey = new LinkedHashMap<>();

        for (InformeVentasFranjaRowDTO ventaRow : ventasRows) {
            InformeVentasFranjaRowDTO row = new InformeVentasFranjaRowDTO();
            row.setFranja(ventaRow.getFranja());
            row.setIdEmpleado(ventaRow.getIdEmpleado());
            row.setNombreEmpleado(ventaRow.getNombreEmpleado());
            row.setTotalVentas(safe(ventaRow.getTotalVentas()));
            row.setNumeroTickets(safeInt(ventaRow.getNumeroTickets()));
            row.setTicketMedio(safe(ventaRow.getTicketMedio()));
            row.setTotalDevoluciones(BigDecimal.ZERO);
            row.setTotalNeto(safe(ventaRow.getTotalVentas()));

            rowsByKey.put(buildFranjaComparativeKey(row.getFranja(), row.getIdEmpleado()), row);
        }

        for (InformeVentasFranjaRowDTO devRow : devolucionesRows) {
            String key = buildFranjaComparativeKey(devRow.getFranja(), devRow.getIdEmpleado());
            InformeVentasFranjaRowDTO existing = rowsByKey.get(key);

            if (existing == null) {
                existing = new InformeVentasFranjaRowDTO();
                existing.setFranja(devRow.getFranja());
                existing.setIdEmpleado(devRow.getIdEmpleado());
                existing.setNombreEmpleado(devRow.getNombreEmpleado());
                existing.setTotalVentas(BigDecimal.ZERO);
                existing.setNumeroTickets(0);
                existing.setTicketMedio(BigDecimal.ZERO);
                rowsByKey.put(key, existing);
            }

            existing.setTotalDevoluciones(safe(devRow.getTotalDevoluciones()));
            existing.setTotalNeto(
                    safe(existing.getTotalVentas()).subtract(safe(existing.getTotalDevoluciones()))
            );
        }

        List<InformeVentasFranjaRowDTO> orderedRows = new ArrayList<>(rowsByKey.values());
        orderedRows.sort(Comparator
                .comparing(InformeVentasFranjaRowDTO::getFranja, Comparator.nullsLast(String::compareTo))
                .thenComparing(r -> r.getNombreEmpleado() != null ? r.getNombreEmpleado() : ""));

        return buildVentasFranjaResult(orderedRows);
    }

    private InformeVentasFranjaResultDTO buildVentasFranjaResult(List<InformeVentasFranjaRowDTO> rows) {
        InformeVentasFranjaResultDTO result = new InformeVentasFranjaResultDTO();
        result.setRows(rows);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalDevoluciones = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;
        int totalTickets = 0;

        String mejorFranja = null;
        BigDecimal importeMejorFranja = null;

        Map<String, BigDecimal> netoPorFranja = new HashMap<>();

        for (InformeVentasFranjaRowDTO row : rows) {
            totalVentas = totalVentas.add(safe(row.getTotalVentas()));
            totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
            totalNeto = totalNeto.add(safe(row.getTotalNeto()));
            totalTickets += safeInt(row.getNumeroTickets());

            if (row.getFranja() != null) {
                netoPorFranja.merge(row.getFranja(), safe(row.getTotalNeto()), BigDecimal::add);
            }
        }

        for (Map.Entry<String, BigDecimal> entry : netoPorFranja.entrySet()) {
            if (importeMejorFranja == null || entry.getValue().compareTo(importeMejorFranja) > 0) {
                mejorFranja = entry.getKey();
                importeMejorFranja = entry.getValue();
            }
        }

        result.setTotalVentas(totalVentas);
        result.setTotalDevoluciones(totalDevoluciones);
        result.setTotalNeto(totalNeto);
        result.setTotalTickets(totalTickets);

        if (totalTickets > 0) {
            result.setTicketMedioGlobal(
                    totalVentas.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP)
            );
        } else {
            result.setTicketMedioGlobal(BigDecimal.ZERO);
        }

        result.setMejorFranja(mejorFranja);
        result.setImporteMejorFranja(importeMejorFranja != null ? importeMejorFranja : BigDecimal.ZERO);

        return result;
    }

    private String buildFranjaComparativeKey(String franja, Integer idEmpleado) {
        return (franja != null ? franja : "SIN_FRANJA") + "#" + (idEmpleado != null ? idEmpleado : 0);
    }

    // =====================================================
    // BLOQUE 1 - TICKET MEDIO POR DÍA
    // =====================================================

    // Qué hace:
    // Construye el informe de ticket medio por día.
    // Puede funcionar en agregada o comparativa.
    public InformeTicketMedioDiaResultDTO getTicketMedioPorDia(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.TICKET_MEDIO_POR_DIA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.TICKET_MEDIO_POR_DIA");
        }

        try {
            List<InformeTicketMedioDiaRowDTO> rows = filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                    ? informesDao.findTicketMedioPorDiaComparativo(filtros)
                    : informesDao.findTicketMedioPorDiaAgregado(filtros);

            InformeTicketMedioDiaResultDTO result = new InformeTicketMedioDiaResultDTO();
            result.setRows(rows);

            BigDecimal totalVentas = BigDecimal.ZERO;
            int totalTickets = 0;

            LocalDate mejorDia = null;
            BigDecimal mejorTicketMedio = null;

            Map<LocalDate, BigDecimal> mejorTicketPorDia = new HashMap<>();

            for (InformeTicketMedioDiaRowDTO row : rows) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalTickets += safeInt(row.getNumeroTickets());

                if (row.getFecha() != null) {
                    BigDecimal actual = mejorTicketPorDia.get(row.getFecha());
                    if (actual == null || safe(row.getTicketMedio()).compareTo(actual) > 0) {
                        mejorTicketPorDia.put(row.getFecha(), safe(row.getTicketMedio()));
                    }
                }
            }

            for (Map.Entry<LocalDate, BigDecimal> entry : mejorTicketPorDia.entrySet()) {
                if (mejorTicketMedio == null || entry.getValue().compareTo(mejorTicketMedio) > 0) {
                    mejorDia = entry.getKey();
                    mejorTicketMedio = entry.getValue();
                }
            }

            result.setTotalVentas(totalVentas);
            result.setTotalTickets(totalTickets);

            if (totalTickets > 0) {
                result.setTicketMedioGlobal(
                        totalVentas.divide(BigDecimal.valueOf(totalTickets), 2, RoundingMode.HALF_UP)
                );
            } else {
                result.setTicketMedioGlobal(BigDecimal.ZERO);
            }

            result.setMejorDia(mejorDia);
            result.setMejorTicketMedio(mejorTicketMedio != null ? mejorTicketMedio : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ticket medio por día", e);
        }
    }

    // =====================================================
    // BLOQUE 1 - PAGOS POR MÉTODO
    // =====================================================

    // Qué hace:
    // Construye el informe de pagos agrupado por método.
    // Calcula total, operaciones, método principal y porcentajes por fila.
    public InformePagosMetodoResultDTO getPagosPorMetodo(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.PAGOS_POR_METODO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.PAGOS_POR_METODO");
        }

        try {
            List<InformePagosMetodoRowDTO> rows = informesDao.findPagosPorMetodo(filtros);

            InformePagosMetodoResultDTO result = new InformePagosMetodoResultDTO();
            result.setRows(rows);

            BigDecimal totalImporte = BigDecimal.ZERO;
            int totalOperaciones = 0;

            String metodoPrincipal = null;
            BigDecimal importeMetodoPrincipal = null;

            for (InformePagosMetodoRowDTO row : rows) {
                totalImporte = totalImporte.add(safe(row.getImporteTotal()));
                totalOperaciones += safeInt(row.getNumeroOperaciones());
            }

            for (InformePagosMetodoRowDTO row : rows) {
                if (totalImporte.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal porcentaje = safe(row.getImporteTotal())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalImporte, 2, RoundingMode.HALF_UP);

                    row.setPorcentajeSobreTotal(porcentaje);
                } else {
                    row.setPorcentajeSobreTotal(BigDecimal.ZERO);
                }

                if (importeMetodoPrincipal == null || safe(row.getImporteTotal()).compareTo(importeMetodoPrincipal) > 0) {
                    metodoPrincipal = row.getMetodoPago();
                    importeMetodoPrincipal = safe(row.getImporteTotal());
                }
            }

            result.setTotalImporte(totalImporte);
            result.setTotalOperaciones(totalOperaciones);
            result.setMetodoPrincipal(metodoPrincipal);
            result.setImporteMetodoPrincipal(importeMetodoPrincipal != null ? importeMetodoPrincipal : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de pagos por método", e);
        }
    }

    // =====================================================
    // BLOQUE 1 - VENTAS NETAS VS DEVOLUCIONES
    // =====================================================

    // Qué hace:
    // Construye el informe diario de ventas, devoluciones, neto y ratio global.
    public InformeNetoVsDevolucionesResultDTO getVentasNetasVsDevoluciones(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_NETAS_VS_DEVOLUCIONES) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_NETAS_VS_DEVOLUCIONES");
        }

        try {
            List<InformeNetoVsDevolucionesRowDTO> rows = informesDao.findVentasNetasVsDevoluciones(filtros);

            InformeNetoVsDevolucionesResultDTO result = new InformeNetoVsDevolucionesResultDTO();
            result.setRows(rows);

            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalDevoluciones = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;

            LocalDate peorDia = null;
            BigDecimal importePeorDia = null;

            for (InformeNetoVsDevolucionesRowDTO row : rows) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));

                if (importePeorDia == null || safe(row.getTotalDevoluciones()).compareTo(importePeorDia) > 0) {
                    peorDia = row.getFecha();
                    importePeorDia = safe(row.getTotalDevoluciones());
                }
            }

            result.setTotalVentas(totalVentas);
            result.setTotalDevoluciones(totalDevoluciones);
            result.setTotalNeto(totalNeto);

            if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratioGlobal = totalDevoluciones
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalVentas, 2, RoundingMode.HALF_UP);

                result.setRatioGlobalDevolucion(ratioGlobal);
            } else {
                result.setRatioGlobalDevolucion(BigDecimal.ZERO);
            }

            result.setPeorDiaDevoluciones(peorDia);
            result.setImportePeorDiaDevoluciones(importePeorDia != null ? importePeorDia : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas netas vs devoluciones", e);
        }
    }
    // =====================================================
    // BLOQUE 2 - PRODUCTOS MÁS VENDIDOS
    // =====================================================

    // Qué hace:
    // Construye el resultado completo del informe de productos más vendidos.
    // Usa filas del DAO y calcula:
    // - total unidades
    // - bruto
    // - devoluciones
    // - neto
    // - producto top
    public InformeProductosVendidosResultDTO getProductosMasVendidos(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.PRODUCTOS_MAS_VENDIDOS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.PRODUCTOS_MAS_VENDIDOS");
        }

        try {
            List<InformeProductosVendidosRowDTO> rows = informesDao.findProductosMasVendidos(filtros);

            InformeProductosVendidosResultDTO result = new InformeProductosVendidosResultDTO();
            result.setRows(rows);

            int totalUnidades = 0;
            BigDecimal totalBruto = BigDecimal.ZERO;
            BigDecimal totalDevoluciones = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;

            String productoTop = null;
            Integer unidadesTop = null;

            for (InformeProductosVendidosRowDTO row : rows) {
                totalUnidades += safeInt(row.getUnidadesVendidas());
                totalBruto = totalBruto.add(safe(row.getImporteBruto()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getImporteDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getImporteNeto()));

                if (unidadesTop == null || safeInt(row.getUnidadesVendidas()) > unidadesTop) {
                    productoTop = row.getNombreProducto();
                    unidadesTop = safeInt(row.getUnidadesVendidas());
                }
            }

            result.setTotalUnidades(totalUnidades);
            result.setTotalBruto(totalBruto);
            result.setTotalDevoluciones(totalDevoluciones);
            result.setTotalNeto(totalNeto);
            result.setProductoTop(productoTop);
            result.setUnidadesProductoTop(unidadesTop != null ? unidadesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de productos más vendidos", e);
        }
    }

    // =====================================================
    // BLOQUE 2 - EXTRAS MÁS VENDIDOS
    // =====================================================

    // Qué hace:
    // Construye el resultado del informe de extras más vendidos.
    // Calcula:
    // - total veces
    // - total importe
    // - extra top
    public InformeExtrasVendidosResultDTO getExtrasMasVendidos(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.EXTRAS_MAS_VENDIDOS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.EXTRAS_MAS_VENDIDOS");
        }

        try {
            List<InformeExtrasVendidosRowDTO> rows = informesDao.findExtrasMasVendidos(filtros);

            InformeExtrasVendidosResultDTO result = new InformeExtrasVendidosResultDTO();
            result.setRows(rows);

            int totalVeces = 0;
            BigDecimal totalImporte = BigDecimal.ZERO;

            String extraTop = null;
            Integer vecesTop = null;

            for (InformeExtrasVendidosRowDTO row : rows) {
                totalVeces += safeInt(row.getVecesVendido());
                totalImporte = totalImporte.add(safe(row.getImporteGenerado()));

                if (vecesTop == null || safeInt(row.getVecesVendido()) > vecesTop) {
                    extraTop = row.getNombreExtra();
                    vecesTop = safeInt(row.getVecesVendido());
                }
            }

            result.setTotalVeces(totalVeces);
            result.setTotalImporte(totalImporte);
            result.setExtraTop(extraTop);
            result.setVecesExtraTop(vecesTop != null ? vecesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de extras más vendidos", e);
        }
    }

    // =====================================================
    // BLOQUE 2 - COMBOS VENDIDOS
    // =====================================================

    // Qué hace:
    // Construye el resultado del informe de combos vendidos.
    // Calcula:
    // - total combos
    // - precio original total
    // - precio final total
    // - ahorro total
    // - combo top
    public InformeCombosVendidosResultDTO getCombosVendidos(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.COMBOS_VENDIDOS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.COMBOS_VENDIDOS");
        }

        try {
            List<InformeCombosVendidosRowDTO> rows = informesDao.findCombosVendidos(filtros);

            InformeCombosVendidosResultDTO result = new InformeCombosVendidosResultDTO();
            result.setRows(rows);

            int totalCombos = 0;
            BigDecimal totalPrecioOriginal = BigDecimal.ZERO;
            BigDecimal totalPrecioFinal = BigDecimal.ZERO;
            BigDecimal totalAhorro = BigDecimal.ZERO;

            String comboTop = null;
            Integer vecesTop = null;

            for (InformeCombosVendidosRowDTO row : rows) {
                totalCombos += safeInt(row.getVecesVendido());
                totalPrecioOriginal = totalPrecioOriginal.add(safe(row.getPrecioOriginalTotal()));
                totalPrecioFinal = totalPrecioFinal.add(safe(row.getPrecioFinalTotal()));
                totalAhorro = totalAhorro.add(safe(row.getAhorroTotal()));

                if (vecesTop == null || safeInt(row.getVecesVendido()) > vecesTop) {
                    comboTop = row.getNombreCombo();
                    vecesTop = safeInt(row.getVecesVendido());
                }
            }

            result.setTotalCombos(totalCombos);
            result.setTotalPrecioOriginal(totalPrecioOriginal);
            result.setTotalPrecioFinal(totalPrecioFinal);
            result.setTotalAhorro(totalAhorro);
            result.setComboTop(comboTop);
            result.setVecesComboTop(vecesTop != null ? vecesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de combos vendidos", e);
        }
    }

    // =====================================================
    // BLOQUE 2 - DESCUENTOS APLICADOS
    // =====================================================

    // Qué hace:
    // Construye el resultado del informe de descuentos aplicados.
    // Calcula:
    // - total usos
    // - base total
    // - descuento total
    // - descuento más usado
    public InformeDescuentosAplicadosResultDTO getDescuentosAplicados(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.DESCUENTOS_APLICADOS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.DESCUENTOS_APLICADOS");
        }

        try {
            List<InformeDescuentosAplicadosRowDTO> rows = informesDao.findDescuentosAplicados(filtros);

            InformeDescuentosAplicadosResultDTO result = new InformeDescuentosAplicadosResultDTO();
            result.setRows(rows);

            int totalUsos = 0;
            BigDecimal totalBase = BigDecimal.ZERO;
            BigDecimal totalImporteDescuento = BigDecimal.ZERO;

            String descuentoMasUsado = null;
            Integer usosTop = null;

            for (InformeDescuentosAplicadosRowDTO row : rows) {
                totalUsos += safeInt(row.getNumeroUsos());
                totalBase = totalBase.add(safe(row.getImporteBase()));
                totalImporteDescuento = totalImporteDescuento.add(safe(row.getImporteDescuento()));

                if (usosTop == null || safeInt(row.getNumeroUsos()) > usosTop) {
                    descuentoMasUsado = row.getNombreDescuento();
                    usosTop = safeInt(row.getNumeroUsos());
                }
            }

            result.setTotalUsos(totalUsos);
            result.setTotalBase(totalBase);
            result.setTotalImporteDescuento(totalImporteDescuento);
            result.setDescuentoMasUsado(descuentoMasUsado);
            result.setUsosDescuentoMasUsado(usosTop != null ? usosTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de descuentos aplicados", e);
        }
    }

    // =====================================================
    // BLOQUE 2 - DEVOLUCIONES POR PRODUCTO
    // =====================================================

    // Qué hace:
    // Construye el resultado del informe de devoluciones por producto.
    // Calcula:
    // - cantidad total devuelta
    // - número total de devoluciones
    // - total reembolsado
    // - producto más devuelto
    public InformeDevolucionesProductoResultDTO getDevolucionesPorProducto(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.DEVOLUCIONES_POR_PRODUCTO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.DEVOLUCIONES_POR_PRODUCTO");
        }

        try {
            List<InformeDevolucionesProductoRowDTO> rows = informesDao.findDevolucionesPorProducto(filtros);

            InformeDevolucionesProductoResultDTO result = new InformeDevolucionesProductoResultDTO();
            result.setRows(rows);

            int totalCantidadDevuelta = 0;
            int totalDevoluciones = 0;
            BigDecimal totalReembolsado = BigDecimal.ZERO;

            String productoMasDevuelto = null;
            Integer cantidadTop = null;

            for (InformeDevolucionesProductoRowDTO row : rows) {
                totalCantidadDevuelta += safeInt(row.getCantidadDevuelta());
                totalDevoluciones += safeInt(row.getNumeroDevoluciones());
                totalReembolsado = totalReembolsado.add(safe(row.getImporteReembolsado()));

                if (cantidadTop == null || safeInt(row.getCantidadDevuelta()) > cantidadTop) {
                    productoMasDevuelto = row.getNombreProducto();
                    cantidadTop = safeInt(row.getCantidadDevuelta());
                }
            }

            result.setTotalCantidadDevuelta(totalCantidadDevuelta);
            result.setTotalDevoluciones(totalDevoluciones);
            result.setTotalReembolsado(totalReembolsado);
            result.setProductoMasDevuelto(productoMasDevuelto);
            result.setCantidadProductoMasDevuelto(cantidadTop != null ? cantidadTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de devoluciones por producto", e);
        }
    }
    // =====================================================
    // BLOQUE 3 - RANKING EMPLEADOS POR VENTAS
    // =====================================================

    public InformeRankingEmpleadosVentasResultDTO getRankingEmpleadosPorVentas(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.RANKING_EMPLEADOS_POR_VENTAS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.RANKING_EMPLEADOS_POR_VENTAS");
        }

        try {
            List<InformeRankingEmpleadosVentasRowDTO> rows = informesDao.findRankingEmpleadosPorVentas(filtros);

            InformeRankingEmpleadosVentasResultDTO result = new InformeRankingEmpleadosVentasResultDTO();
            result.setRows(rows);

            BigDecimal totalVentas = BigDecimal.ZERO;
            int totalTickets = 0;

            String mejorEmpleado = null;
            BigDecimal ventasMejorEmpleado = null;

            for (InformeRankingEmpleadosVentasRowDTO row : rows) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalTickets += safeInt(row.getNumeroTickets());

                if (ventasMejorEmpleado == null || safe(row.getTotalVentas()).compareTo(ventasMejorEmpleado) > 0) {
                    mejorEmpleado = row.getNombreEmpleado();
                    ventasMejorEmpleado = safe(row.getTotalVentas());
                }
            }

            result.setTotalVentas(totalVentas);
            result.setTotalTickets(totalTickets);

            if (totalTickets > 0) {
                result.setTicketMedioGlobal(
                        totalVentas.divide(BigDecimal.valueOf(totalTickets), 2, java.math.RoundingMode.HALF_UP)
                );
            } else {
                result.setTicketMedioGlobal(BigDecimal.ZERO);
            }

            result.setMejorEmpleado(mejorEmpleado);
            result.setVentasMejorEmpleado(ventasMejorEmpleado != null ? ventasMejorEmpleado : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ranking empleados por ventas", e);
        }
    }

    // =====================================================
    // BLOQUE 3 - RANKING EMPLEADOS POR EXTRAS
    // =====================================================

    public InformeRankingEmpleadosExtrasResultDTO getRankingEmpleadosPorExtras(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.RANKING_EMPLEADOS_POR_EXTRAS");
        }

        try {
            List<InformeRankingEmpleadosExtrasRowDTO> rows = informesDao.findRankingEmpleadosPorExtras(filtros);

            InformeRankingEmpleadosExtrasResultDTO result = new InformeRankingEmpleadosExtrasResultDTO();
            result.setRows(rows);

            int totalExtras = 0;
            BigDecimal totalImporte = BigDecimal.ZERO;

            String mejorEmpleado = null;
            Integer extrasTop = null;

            for (InformeRankingEmpleadosExtrasRowDTO row : rows) {
                totalExtras += safeInt(row.getTotalExtrasVendidos());
                totalImporte = totalImporte.add(safe(row.getImporteExtras()));

                if (extrasTop == null || safeInt(row.getTotalExtrasVendidos()) > extrasTop) {
                    mejorEmpleado = row.getNombreEmpleado();
                    extrasTop = safeInt(row.getTotalExtrasVendidos());
                }
            }

            result.setTotalExtrasVendidos(totalExtras);
            result.setTotalImporteExtras(totalImporte);
            result.setMejorEmpleado(mejorEmpleado);
            result.setExtrasMejorEmpleado(extrasTop != null ? extrasTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ranking empleados por extras", e);
        }
    }

    // =====================================================
    // BLOQUE 3 - PRODUCTOS VENDIDOS POR EMPLEADO
    // =====================================================

    public InformeProductosPorEmpleadoResultDTO getProductosVendidosPorEmpleado(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.PRODUCTOS_VENDIDOS_POR_EMPLEADO");
        }

        try {
            List<InformeProductosPorEmpleadoRowDTO> rows =
                    filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                            ? informesDao.findProductosVendidosPorEmpleadoComparativo(filtros)
                            : informesDao.findProductosVendidosPorEmpleadoAgregado(filtros);

            InformeProductosPorEmpleadoResultDTO result = new InformeProductosPorEmpleadoResultDTO();
            result.setRows(rows);

            int totalUnidades = 0;
            BigDecimal totalImporte = BigDecimal.ZERO;

            String empleadoTop = null;
            String productoTop = null;
            Integer unidadesTop = null;

            for (InformeProductosPorEmpleadoRowDTO row : rows) {
                totalUnidades += safeInt(row.getUnidadesVendidas());
                totalImporte = totalImporte.add(safe(row.getImporteTotal()));

                if (unidadesTop == null || safeInt(row.getUnidadesVendidas()) > unidadesTop) {
                    empleadoTop = row.getNombreEmpleado();
                    productoTop = row.getNombreProducto();
                    unidadesTop = safeInt(row.getUnidadesVendidas());
                }
            }

            result.setTotalUnidades(totalUnidades);
            result.setTotalImporte(totalImporte);
            result.setEmpleadoTop(empleadoTop);
            result.setProductoTop(productoTop);
            result.setUnidadesTop(unidadesTop != null ? unidadesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de productos vendidos por empleado", e);
        }
    }

    // =====================================================
    // BLOQUE 4 - VENTAS POR CAJA
    // =====================================================

    public InformeVentasCajaResultDTO getVentasPorCaja(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_POR_CAJA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_POR_CAJA");
        }

        try {
            List<InformeVentasCajaRowDTO> rows = informesDao.findVentasPorCaja(filtros);

            InformeVentasCajaResultDTO result = new InformeVentasCajaResultDTO();
            result.setRows(rows);

            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalDevoluciones = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;
            int totalTickets = 0;

            String mejorCaja = null;
            BigDecimal netoMejorCaja = null;

            for (InformeVentasCajaRowDTO row : rows) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));
                totalTickets += safeInt(row.getNumeroTickets());

                if (netoMejorCaja == null || safe(row.getTotalNeto()).compareTo(netoMejorCaja) > 0) {
                    mejorCaja = row.getNombreCaja();
                    netoMejorCaja = safe(row.getTotalNeto());
                }
            }

            result.setTotalVentas(totalVentas);
            result.setTotalDevoluciones(totalDevoluciones);
            result.setTotalNeto(totalNeto);
            result.setTotalTickets(totalTickets);
            result.setMejorCaja(mejorCaja);
            result.setNetoMejorCaja(netoMejorCaja != null ? netoMejorCaja : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas por caja", e);
        }
    }

    // =====================================================
    // BLOQUE 4 - VENTAS POR SESIÓN DE CAJA
    // =====================================================

    public InformeVentasSesionCajaResultDTO getVentasPorSesionCaja(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_POR_SESION_CAJA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_POR_SESION_CAJA");
        }

        try {
            List<InformeVentasSesionCajaRowDTO> rows = informesDao.findVentasPorSesionCaja(filtros);

            InformeVentasSesionCajaResultDTO result = new InformeVentasSesionCajaResultDTO();
            result.setRows(rows);

            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalDevoluciones = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;
            int totalSesiones = 0;

            Integer idMejorSesion = null;
            BigDecimal netoMejorSesion = null;

            for (InformeVentasSesionCajaRowDTO row : rows) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));
                totalSesiones++;

                if (netoMejorSesion == null || safe(row.getTotalNeto()).compareTo(netoMejorSesion) > 0) {
                    idMejorSesion = row.getIdSesion();
                    netoMejorSesion = safe(row.getTotalNeto());
                }
            }

            result.setTotalVentas(totalVentas);
            result.setTotalDevoluciones(totalDevoluciones);
            result.setTotalNeto(totalNeto);
            result.setTotalSesiones(totalSesiones);
            result.setIdMejorSesion(idMejorSesion);
            result.setNetoMejorSesion(netoMejorSesion != null ? netoMejorSesion : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas por sesión de caja", e);
        }
    }

    // =====================================================
    // BLOQUE 4 - TIEMPOS POR ESTACIÓN
    // =====================================================

    public InformeTiemposEstacionResultDTO getTiemposPorEstacion(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.TIEMPOS_POR_ESTACION) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.TIEMPOS_POR_ESTACION");
        }

        try {
            List<InformeTiemposEstacionRowDTO> rows = informesDao.findTiemposPorEstacion(filtros);

            InformeTiemposEstacionResultDTO result = new InformeTiemposEstacionResultDTO();
            result.setRows(rows);

            BigDecimal sumaTiemposMedios = BigDecimal.ZERO;
            int totalFilas = 0;
            int totalItems = 0;

            String estacionMasLenta = null;
            BigDecimal tiempoMasLento = null;

            for (InformeTiemposEstacionRowDTO row : rows) {
                sumaTiemposMedios = sumaTiemposMedios.add(safe(row.getTiempoMedioSegundos()));
                totalFilas++;
                totalItems += safeInt(row.getItemsProcesados());

                if (tiempoMasLento == null || safe(row.getTiempoMedioSegundos()).compareTo(tiempoMasLento) > 0) {
                    estacionMasLenta = row.getNombreEstacion();
                    tiempoMasLento = safe(row.getTiempoMedioSegundos());
                }
            }

            if (totalFilas > 0) {
                result.setTiempoMedioGlobalSegundos(
                        sumaTiemposMedios.divide(BigDecimal.valueOf(totalFilas), 2, java.math.RoundingMode.HALF_UP)
                );
            } else {
                result.setTiempoMedioGlobalSegundos(BigDecimal.ZERO);
            }

            result.setTotalItemsProcesados(totalItems);
            result.setEstacionMasLenta(estacionMasLenta);
            result.setTiempoEstacionMasLenta(tiempoMasLento != null ? tiempoMasLento : BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de tiempos por estación", e);
        }
    }

    // =====================================================
    // BLOQUE 4 - MERMA POR PERÍODO
    // =====================================================

    public InformeMermaPeriodoResultDTO getMermaPorPeriodo(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.MERMA_POR_PERIODO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.MERMA_POR_PERIODO");
        }

        try {
            List<InformeMermaPeriodoRowDTO> rows = informesDao.findMermaPorPeriodo(filtros);

            InformeMermaPeriodoResultDTO result = new InformeMermaPeriodoResultDTO();
            result.setRows(rows);

            BigDecimal totalCantidad = BigDecimal.ZERO;
            int totalRegistros = 0;

            LocalDate peorDia = null;
            BigDecimal cantidadPeorDia = BigDecimal.ZERO;

            java.util.Map<LocalDate, BigDecimal> acumuladoPorDia = new java.util.HashMap<>();

            for (InformeMermaPeriodoRowDTO row : rows) {
                totalCantidad = totalCantidad.add(safe(row.getCantidad()));
                totalRegistros++;

                if (row.getFecha() != null) {
                    acumuladoPorDia.merge(row.getFecha(), safe(row.getCantidad()), BigDecimal::add);
                }
            }

            for (java.util.Map.Entry<LocalDate, BigDecimal> entry : acumuladoPorDia.entrySet()) {
                if (entry.getValue().compareTo(cantidadPeorDia) > 0) {
                    peorDia = entry.getKey();
                    cantidadPeorDia = entry.getValue();
                }
            }

            result.setTotalCantidad(totalCantidad);
            result.setTotalRegistros(totalRegistros);
            result.setPeorDia(peorDia);
            result.setCantidadPeorDia(cantidadPeorDia);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de merma por período", e);
        }
    }

    // =====================================================
    // BLOQUE 4 - MOVIMIENTOS DE STOCK / AJUSTES
    // =====================================================

    public InformeMovimientoStockResultDTO getMovimientosStockAjustes(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.MOVIMIENTOS_STOCK_AJUSTES) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.MOVIMIENTOS_STOCK_AJUSTES");
        }

        try {
            List<InformeMovimientoStockRowDTO> rows = informesDao.findMovimientosStockAjustes(filtros);

            InformeMovimientoStockResultDTO result = new InformeMovimientoStockResultDTO();
            result.setRows(rows);

            int totalMovimientos = 0;
            BigDecimal totalCantidad = BigDecimal.ZERO;

            int totalEntradas = 0;
            int totalSalidas = 0;
            int totalAjustes = 0;
            int totalMermas = 0;

            for (InformeMovimientoStockRowDTO row : rows) {
                totalMovimientos++;
                totalCantidad = totalCantidad.add(safe(row.getCantidad()));

                String tipo = row.getTipoMovimiento() != null ? row.getTipoMovimiento().trim().toUpperCase() : "";

                switch (tipo) {
                    case "ENTRADA" -> totalEntradas++;
                    case "SALIDA" -> totalSalidas++;
                    case "AJUSTE" -> totalAjustes++;
                    case "MERMA" -> totalMermas++;
                }
            }

            result.setTotalMovimientos(totalMovimientos);
            result.setTotalCantidad(totalCantidad);
            result.setTotalEntradas(totalEntradas);
            result.setTotalSalidas(totalSalidas);
            result.setTotalAjustes(totalAjustes);
            result.setTotalMermas(totalMermas);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de movimientos de stock / ajustes", e);
        }
    }
    
    // =====================================================
    // BLOQUE 5 - VENTAS PRODUCTO POR EMPLEADO
    // =====================================================

    public InformeVentasProductoEmpleadoResultDTO getVentasProductoPorEmpleado(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_PRODUCTO_POR_EMPLEADO");
        }

        try {
            List<InformeVentasProductoEmpleadoRowDTO> rows =
                    filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                            ? informesDao.findVentasProductoPorEmpleadoComparativo(filtros)
                            : informesDao.findVentasProductoPorEmpleadoAgregado(filtros);

            InformeVentasProductoEmpleadoResultDTO result = new InformeVentasProductoEmpleadoResultDTO();
            result.setRows(rows);

            int totalUnidades = 0;
            BigDecimal totalBruto = BigDecimal.ZERO;
            BigDecimal totalDescuento = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;

            String empleadoTop = null;
            String productoTop = null;
            Integer unidadesTop = null;

            for (InformeVentasProductoEmpleadoRowDTO row : rows) {
                totalUnidades += safeInt(row.getUnidadesVendidas());
                totalBruto = totalBruto.add(safe(row.getImporteBruto()));
                totalDescuento = totalDescuento.add(safe(row.getImporteDescuento()));
                totalNeto = totalNeto.add(safe(row.getImporteNeto()));

                if (unidadesTop == null || safeInt(row.getUnidadesVendidas()) > unidadesTop) {
                    empleadoTop = row.getNombreEmpleado();
                    productoTop = row.getNombreProducto();
                    unidadesTop = safeInt(row.getUnidadesVendidas());
                }
            }

            result.setTotalUnidades(totalUnidades);
            result.setTotalBruto(totalBruto);
            result.setTotalDescuento(totalDescuento);
            result.setTotalNeto(totalNeto);
            result.setEmpleadoTop(empleadoTop);
            result.setProductoTop(productoTop);
            result.setUnidadesTop(unidadesTop != null ? unidadesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas producto por empleado", e);
        }
    }

    // =====================================================
    // BLOQUE 5 - RANKING EMPLEADOS POR PRODUCTO
    // =====================================================

    public InformeRankingEmpleadosProductoResultDTO getRankingEmpleadosPorProducto(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.RANKING_EMPLEADOS_POR_PRODUCTO");
        }

        try {
            List<InformeRankingEmpleadosProductoRowDTO> rows = informesDao.findRankingEmpleadosPorProducto(filtros);

            InformeRankingEmpleadosProductoResultDTO result = new InformeRankingEmpleadosProductoResultDTO();
            result.setRows(rows);

            int totalUnidades = 0;
            BigDecimal totalImporteNeto = BigDecimal.ZERO;

            String empleadoTop = null;
            String productoTop = null;
            Integer unidadesTop = null;

            for (InformeRankingEmpleadosProductoRowDTO row : rows) {
                totalUnidades += safeInt(row.getUnidadesVendidas());
                totalImporteNeto = totalImporteNeto.add(safe(row.getImporteNeto()));

                if (unidadesTop == null || safeInt(row.getUnidadesVendidas()) > unidadesTop) {
                    empleadoTop = row.getNombreEmpleado();
                    productoTop = row.getNombreProducto();
                    unidadesTop = safeInt(row.getUnidadesVendidas());
                }
            }

            result.setTotalUnidades(totalUnidades);
            result.setTotalImporteNeto(totalImporteNeto);
            result.setEmpleadoTop(empleadoTop);
            result.setProductoTop(productoTop);
            result.setUnidadesTop(unidadesTop != null ? unidadesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ranking empleados por producto", e);
        }
    }

    // =====================================================
    // BLOQUE 5 - VENTAS EXTRA POR EMPLEADO
    // =====================================================

    public InformeVentasExtraEmpleadoResultDTO getVentasExtraPorEmpleado(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.VENTAS_EXTRA_POR_EMPLEADO) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.VENTAS_EXTRA_POR_EMPLEADO");
        }

        try {
            List<InformeVentasExtraEmpleadoRowDTO> rows =
                    filtros.getModoVista() == ModoVistaInforme.COMPARATIVA
                            ? informesDao.findVentasExtraPorEmpleadoComparativo(filtros)
                            : informesDao.findVentasExtraPorEmpleadoAgregado(filtros);

            InformeVentasExtraEmpleadoResultDTO result = new InformeVentasExtraEmpleadoResultDTO();
            result.setRows(rows);

            int totalVeces = 0;
            BigDecimal totalImporte = BigDecimal.ZERO;

            String empleadoTop = null;
            String extraTop = null;
            Integer vecesTop = null;

            for (InformeVentasExtraEmpleadoRowDTO row : rows) {
                totalVeces += safeInt(row.getVecesVendido());
                totalImporte = totalImporte.add(safe(row.getImporteGenerado()));

                if (vecesTop == null || safeInt(row.getVecesVendido()) > vecesTop) {
                    empleadoTop = row.getNombreEmpleado();
                    extraTop = row.getNombreExtra();
                    vecesTop = safeInt(row.getVecesVendido());
                }
            }

            result.setTotalVeces(totalVeces);
            result.setTotalImporte(totalImporte);
            result.setEmpleadoTop(empleadoTop);
            result.setExtraTop(extraTop);
            result.setVecesTop(vecesTop != null ? vecesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas extra por empleado", e);
        }
    }

    // =====================================================
    // BLOQUE 5 - RANKING EMPLEADOS POR EXTRA
    // =====================================================

    public InformeRankingEmpleadosExtraResultDTO getRankingEmpleadosPorExtra(InformeFiltroDTO filtros) {
        Objects.requireNonNull(filtros, "filtros no puede ser null");

        if (filtros.getTipoInforme() != TipoInforme.RANKING_EMPLEADOS_POR_EXTRA) {
            throw new IllegalArgumentException("Este método solo soporta TipoInforme.RANKING_EMPLEADOS_POR_EXTRA");
        }

        try {
            List<InformeRankingEmpleadosExtraRowDTO> rows = informesDao.findRankingEmpleadosPorExtra(filtros);

            InformeRankingEmpleadosExtraResultDTO result = new InformeRankingEmpleadosExtraResultDTO();
            result.setRows(rows);

            int totalVeces = 0;
            BigDecimal totalImporte = BigDecimal.ZERO;

            String empleadoTop = null;
            String extraTop = null;
            Integer vecesTop = null;

            for (InformeRankingEmpleadosExtraRowDTO row : rows) {
                totalVeces += safeInt(row.getVecesVendido());
                totalImporte = totalImporte.add(safe(row.getImporteGenerado()));

                if (vecesTop == null || safeInt(row.getVecesVendido()) > vecesTop) {
                    empleadoTop = row.getNombreEmpleado();
                    extraTop = row.getNombreExtra();
                    vecesTop = safeInt(row.getVecesVendido());
                }
            }

            result.setTotalVeces(totalVeces);
            result.setTotalImporte(totalImporte);
            result.setEmpleadoTop(empleadoTop);
            result.setExtraTop(extraTop);
            result.setVecesTop(vecesTop != null ? vecesTop : 0);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ranking empleados por extra", e);
        }
    }
  

    private String buildComparativeKey(LocalDate fecha, Integer idEmpleado) {
        return fecha + "#" + (idEmpleado != null ? idEmpleado : 0);
    }
    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
    
}
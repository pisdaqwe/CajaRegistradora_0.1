package service;

import dao.InformesDao;
import dtoS.InformeFiltroDTO;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeVentasPorDiaRowDTO;
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

        if (filtros.getModoVista() == ModoVistaInforme.COMPARATIVA) {
            throw new UnsupportedOperationException("Modo comparativo todavía no implementado");
        }

        try {
            List<InformeVentasPorDiaRowDTO> ventasRows = informesDao.findVentasPorDiaAgregado(filtros);

            List<InformeVentasPorDiaRowDTO> devolucionesRows = filtros.isIncluirDevoluciones()
                    ? informesDao.findDevolucionesPorDiaAgregado(filtros)
                    : Collections.emptyList();

            Map<LocalDate, InformeVentasPorDiaRowDTO> rowsByDate = new TreeMap<>();

            // 1) Cargar ventas
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

            // 2) Mezclar devoluciones
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

            // 3) Asegurar neto si no hubo devoluciones
            for (InformeVentasPorDiaRowDTO row : rowsByDate.values()) {
                if (row.getTotalNeto() == null) {
                    row.setTotalNeto(
                            safe(row.getTotalVentas()).subtract(safe(row.getTotalDevoluciones()))
                    );
                }
            }

            // 4) Construir resultado final
            InformeVentasPorDiaResultDTO result = new InformeVentasPorDiaResultDTO();
            result.setRows(new ArrayList<>(rowsByDate.values()));

            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalDevoluciones = BigDecimal.ZERO;
            BigDecimal totalNeto = BigDecimal.ZERO;
            int totalTickets = 0;

            LocalDate fechaMejorDia = null;
            BigDecimal importeMejorDia = null;

            for (InformeVentasPorDiaRowDTO row : result.getRows()) {
                totalVentas = totalVentas.add(safe(row.getTotalVentas()));
                totalDevoluciones = totalDevoluciones.add(safe(row.getTotalDevoluciones()));
                totalNeto = totalNeto.add(safe(row.getTotalNeto()));
                totalTickets += safeInt(row.getNumeroTickets());

                if (importeMejorDia == null || safe(row.getTotalNeto()).compareTo(importeMejorDia) > 0) {
                    importeMejorDia = safe(row.getTotalNeto());
                    fechaMejorDia = row.getFecha();
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

        } catch (Exception e) {
            throw new RuntimeException("Error generando informe de ventas por día", e);
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
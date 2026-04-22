package util;

import dtoS.InformePdfExportRequest;
import dtoS.InformePdfTableData;
import dtoS.InformeVentasPorDiaResultDTO;
import dtoS.InformeResumenEjecutivoResultDTO;
import dtoS.InformeVentasFranjaResultDTO;
import dtoS.InformeTicketMedioDiaResultDTO;
import dtoS.InformePagosMetodoResultDTO;
import dtoS.InformeNetoVsDevolucionesResultDTO;
import dtoS.InformeProductosVendidosResultDTO;
import dtoS.InformeExtrasVendidosResultDTO;
import dtoS.InformeCombosVendidosResultDTO;
import dtoS.InformeDescuentosAplicadosResultDTO;
import dtoS.InformeDevolucionesProductoResultDTO;
import dtoS.InformeRankingEmpleadosVentasResultDTO;
import dtoS.InformeRankingEmpleadosExtrasResultDTO;
import dtoS.InformeProductosPorEmpleadoResultDTO;
import dtoS.InformeVentasCajaResultDTO;
import dtoS.InformeVentasSesionCajaResultDTO;
import dtoS.InformeTiemposEstacionResultDTO;
import dtoS.InformeMermaPeriodoResultDTO;
import dtoS.InformeMovimientoStockResultDTO;
import dtoS.InformeVentasProductoEmpleadoResultDTO;
import dtoS.InformeRankingEmpleadosProductoResultDTO;
import dtoS.InformeVentasExtraEmpleadoResultDTO;
import dtoS.InformeRankingEmpleadosExtraResultDTO;
import enums.ModoVistaInforme;
import enums.TipoInforme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder responsable de transformar el resultado real de un informe
 * en una estructura tabular neutra lista para exportar a PDF.
 *
 * Responsabilidades:
 * - Leer el InformePdfExportRequest.
 * - Detectar el tipo de informe.
 * - Convertir el DTO real del informe en:
 *      - título de tabla
 *      - columnas
 *      - filas en String
 *
 * IMPORTANTE:
 * - Esta clase NO pinta PDF.
 * - Esta clase NO consulta la BD.
 * - Esta clase NO depende de Swing/JTable.
 *
 * Primera versión:
 * - Soporta solo TipoInforme.VENTAS_POR_DIA
 * - Soporta AGREGADA y COMPARATIVA
 *
 * Más adelante:
 * - añadir casos para el resto de informes
 * - mantener aquí la misma lógica de columnas que en InformeTablaPanel
 */
public class InformePdfTableBuilder {

    /**
     * Punto de entrada principal.
     *
     * @param request request completo de exportación PDF
     * @return tabla neutra lista para que el renderer la dibuje
     */
    public InformePdfTableData build(InformePdfExportRequest request) {
        Objects.requireNonNull(request, "request no puede ser null");
        request.validate();

        TipoInforme tipoInforme = request.getTipoInforme();

        return switch (tipoInforme) {
            case VENTAS_POR_DIA -> buildVentasPorDiaTable(request);
            case RESUMEN_EJECUTIVO -> buildResumenEjecutivoTable(request);
            case VENTAS_POR_FRANJA_HORARIA -> buildVentasPorFranjaTable(request);
            case TICKET_MEDIO_POR_DIA -> buildTicketMedioPorDiaTable(request);
            case PAGOS_POR_METODO -> buildPagosPorMetodoTable(request);
            case VENTAS_NETAS_VS_DEVOLUCIONES -> buildVentasNetasVsDevolucionesTable(request);
            case PRODUCTOS_MAS_VENDIDOS -> buildProductosMasVendidosTable(request);
            case EXTRAS_MAS_VENDIDOS -> buildExtrasMasVendidosTable(request);
            case COMBOS_VENDIDOS -> buildCombosVendidosTable(request);
            case DESCUENTOS_APLICADOS -> buildDescuentosAplicadosTable(request);
            case DEVOLUCIONES_POR_PRODUCTO -> buildDevolucionesPorProductoTable(request);
            case RANKING_EMPLEADOS_POR_VENTAS -> buildRankingEmpleadosVentasTable(request);
            case RANKING_EMPLEADOS_POR_EXTRAS -> buildRankingEmpleadosExtrasTable(request);
            case PRODUCTOS_VENDIDOS_POR_EMPLEADO -> buildProductosPorEmpleadoTable(request);

            case VENTAS_POR_CAJA -> buildVentasPorCajaTable(request);
            case VENTAS_POR_SESION_CAJA -> buildVentasPorSesionCajaTable(request);
            case TIEMPOS_POR_ESTACION -> buildTiemposPorEstacionTable(request);
            case MERMA_POR_PERIODO -> buildMermaPorPeriodoTable(request);
            case MOVIMIENTOS_STOCK_AJUSTES -> buildMovimientosStockTable(request);

            case VENTAS_PRODUCTO_POR_EMPLEADO -> buildVentasProductoEmpleadoTable(request);
            case RANKING_EMPLEADOS_POR_PRODUCTO -> buildRankingEmpleadosProductoTable(request);
            case VENTAS_EXTRA_POR_EMPLEADO -> buildVentasExtraEmpleadoTable(request);
            case RANKING_EMPLEADOS_POR_EXTRA -> buildRankingEmpleadosExtraTable(request);
            
            default -> throw new UnsupportedOperationException(
                    "Todavía no se ha implementado la tabla PDF para el informe: " + tipoInforme
            );
        };
    }

    /**
     * Construye la tabla del informe VENTAS_POR_DIA.
     *
     * Reglas:
     * - Si el modo es COMPARATIVA:
     *      Fecha | Empleado | Ventas | Devoluciones | Neto | Tickets | Ticket medio
     *
     * - Si el modo es AGREGADA:
     *      Fecha | Ventas | Devoluciones | Neto | Tickets | Ticket medio
     *
     * Esta estructura sigue el mismo criterio que InformeTablaPanel.
     */
    private InformePdfTableData buildVentasPorDiaTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasPorDiaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasPorDiaResultDTO para VENTAS_POR_DIA"
            );
        }

        InformePdfTableData tableData = new InformePdfTableData();
        tableData.setTituloTabla("Resultado · Ventas por día");

        ModoVistaInforme modoVista = request.getModoVista();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            tableData.setColumnas(List.of(
                    "Fecha",
                    "Empleado",
                    "Ventas",
                    "Devoluciones",
                    "Neto",
                    "Tickets",
                    "Ticket medio"
            ));

            if (result.getRows() != null) {
                for (var row : result.getRows()) {
                    List<String> fila = new ArrayList<>();
                    fila.add(InformePdfFormatUtils.formatDate(row.getFecha()));
                    fila.add(InformePdfFormatUtils.formatText(row.getNombreEmpleado()));
                    fila.add(InformePdfFormatUtils.formatMoney(row.getTotalVentas()));
                    fila.add(InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()));
                    fila.add(InformePdfFormatUtils.formatMoney(row.getTotalNeto()));
                    fila.add(InformePdfFormatUtils.formatText(row.getNumeroTickets()));
                    fila.add(InformePdfFormatUtils.formatMoney(row.getTicketMedio()));

                    tableData.addFila(fila);
                }
            }

            return tableData;
        }

        // Por defecto tratamos cualquier otro caso como AGREGADA.
        tableData.setColumnas(List.of(
                "Fecha",
                "Ventas",
                "Devoluciones",
                "Neto",
                "Tickets",
                "Ticket medio"
        ));

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                List<String> fila = new ArrayList<>();
                fila.add(InformePdfFormatUtils.formatDate(row.getFecha()));
                fila.add(InformePdfFormatUtils.formatMoney(row.getTotalVentas()));
                fila.add(InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()));
                fila.add(InformePdfFormatUtils.formatMoney(row.getTotalNeto()));
                fila.add(InformePdfFormatUtils.formatText(row.getNumeroTickets()));
                fila.add(InformePdfFormatUtils.formatMoney(row.getTicketMedio()));

                tableData.addFila(fila);
            }
        }

        return tableData;
    }
    private InformePdfTableData buildResumenEjecutivoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeResumenEjecutivoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeResumenEjecutivoResultDTO para RESUMEN_EJECUTIVO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Resumen ejecutivo",
                List.of("Indicador", "Valor", "Descripción")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getIndicador()),
                        InformePdfFormatUtils.formatMoney(row.getValor()),
                        InformePdfFormatUtils.formatText(row.getDescripcion())
                ));
            }
        }

        return table;
    }
    
    private InformePdfTableData buildVentasPorFranjaTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasFranjaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasFranjaResultDTO para VENTAS_POR_FRANJA_HORARIA"
            );
        }

        ModoVistaInforme modoVista = request.getModoVista();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            InformePdfTableData table = newTable(
                    "Resultado · Ventas por franja horaria",
                    List.of("Franja", "Empleado", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio")
            );

            if (result.getRows() != null) {
                for (var row : result.getRows()) {
                    table.addFila(List.of(
                            InformePdfFormatUtils.formatText(row.getFranja()),
                            InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                            InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                            InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()),
                            InformePdfFormatUtils.formatMoney(row.getTotalNeto()),
                            InformePdfFormatUtils.formatText(row.getNumeroTickets()),
                            InformePdfFormatUtils.formatMoney(row.getTicketMedio())
                    ));
                }
            }

            return table;
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas por franja horaria",
                List.of("Franja", "Ventas", "Devoluciones", "Neto", "Tickets", "Ticket medio")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getFranja()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()),
                        InformePdfFormatUtils.formatMoney(row.getTotalNeto()),
                        InformePdfFormatUtils.formatText(row.getNumeroTickets()),
                        InformePdfFormatUtils.formatMoney(row.getTicketMedio())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildTicketMedioPorDiaTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTicketMedioDiaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeTicketMedioDiaResultDTO para TICKET_MEDIO_POR_DIA"
            );
        }

        ModoVistaInforme modoVista = request.getModoVista();

        if (modoVista == ModoVistaInforme.COMPARATIVA) {
            InformePdfTableData table = newTable(
                    "Resultado · Ticket medio por día",
                    List.of("Fecha", "Empleado", "Tickets", "Ventas", "Ticket medio")
            );

            if (result.getRows() != null) {
                for (var row : result.getRows()) {
                    table.addFila(List.of(
                            InformePdfFormatUtils.formatDate(row.getFecha()),
                            InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                            InformePdfFormatUtils.formatText(row.getNumeroTickets()),
                            InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                            InformePdfFormatUtils.formatMoney(row.getTicketMedio())
                    ));
                }
            }

            return table;
        }

        InformePdfTableData table = newTable(
                "Resultado · Ticket medio por día",
                List.of("Fecha", "Tickets", "Ventas", "Ticket medio")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatDate(row.getFecha()),
                        InformePdfFormatUtils.formatText(row.getNumeroTickets()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatMoney(row.getTicketMedio())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildPagosPorMetodoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformePagosMetodoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformePagosMetodoResultDTO para PAGOS_POR_METODO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Pagos por método",
                List.of("Método", "Operaciones", "Importe", "% total")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getMetodoPago()),
                        InformePdfFormatUtils.formatText(row.getNumeroOperaciones()),
                        InformePdfFormatUtils.formatMoney(row.getImporteTotal()),
                        InformePdfFormatUtils.formatPercent(row.getPorcentajeSobreTotal())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildVentasNetasVsDevolucionesTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeNetoVsDevolucionesResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeNetoVsDevolucionesResultDTO para VENTAS_NETAS_VS_DEVOLUCIONES"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas netas vs devoluciones",
                List.of("Fecha", "Ventas", "Devoluciones", "Neto", "Ratio devolución")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatDate(row.getFecha()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()),
                        InformePdfFormatUtils.formatMoney(row.getTotalNeto()),
                        InformePdfFormatUtils.formatPercent(row.getRatioDevolucion())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildProductosMasVendidosTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeProductosVendidosResultDTO para PRODUCTOS_MAS_VENDIDOS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Productos más vendidos",
                List.of("Producto", "Unidades", "Bruto", "Devoluciones", "Neto")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreProducto()),
                        InformePdfFormatUtils.formatText(row.getUnidadesVendidas()),
                        InformePdfFormatUtils.formatMoney(row.getImporteBruto()),
                        InformePdfFormatUtils.formatMoney(row.getImporteDevoluciones()),
                        InformePdfFormatUtils.formatMoney(row.getImporteNeto())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildCombosVendidosTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeCombosVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeCombosVendidosResultDTO para COMBOS_VENDIDOS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Combos vendidos",
                List.of("Combo", "Veces", "Precio original", "Precio final", "Ahorro")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreCombo()),
                        InformePdfFormatUtils.formatText(row.getVecesVendido()),
                        InformePdfFormatUtils.formatMoney(row.getPrecioOriginalTotal()),
                        InformePdfFormatUtils.formatMoney(row.getPrecioFinalTotal()),
                        InformePdfFormatUtils.formatMoney(row.getAhorroTotal())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildDescuentosAplicadosTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDescuentosAplicadosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeDescuentosAplicadosResultDTO para DESCUENTOS_APLICADOS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Descuentos aplicados",
                List.of("Descuento", "Tipo", "Usos", "Base", "Importe descuento")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreDescuento()),
                        InformePdfFormatUtils.formatText(row.getTipoBeneficio()),
                        InformePdfFormatUtils.formatText(row.getNumeroUsos()),
                        InformePdfFormatUtils.formatMoney(row.getImporteBase()),
                        InformePdfFormatUtils.formatMoney(row.getImporteDescuento())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildDevolucionesPorProductoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeDevolucionesProductoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeDevolucionesProductoResultDTO para DEVOLUCIONES_POR_PRODUCTO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Devoluciones por producto",
                List.of("Producto", "Cantidad devuelta", "Reembolso", "Nº devoluciones", "Repone stock")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreProducto()),
                        InformePdfFormatUtils.formatText(row.getCantidadDevuelta()),
                        InformePdfFormatUtils.formatMoney(row.getImporteReembolsado()),
                        InformePdfFormatUtils.formatText(row.getNumeroDevoluciones()),
                        row.isReponeStock() ? "Sí" : "No"
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildExtrasMasVendidosTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeExtrasVendidosResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeExtrasVendidosResultDTO para EXTRAS_MAS_VENDIDOS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Extras más vendidos",
                List.of("Extra", "Grupo", "Veces", "Importe")
        );

        if (result.getRows() != null) {
            for (var row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreExtra()),
                        InformePdfFormatUtils.formatText(row.getGrupoPrincipal()),
                        InformePdfFormatUtils.formatText(row.getVecesVendido()),
                        InformePdfFormatUtils.formatMoney(row.getImporteGenerado())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildRankingEmpleadosVentasTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosVentasResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosVentasResultDTO para RANKING_EMPLEADOS_POR_VENTAS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ranking empleados por ventas",
                List.of("Posición", "Empleado", "Ventas", "Tickets", "Ticket medio")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosVentasRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getPosicion()),
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatText(row.getNumeroTickets()),
                        InformePdfFormatUtils.formatMoney(row.getTicketMedio())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildRankingEmpleadosExtrasTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtrasResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosExtrasResultDTO para RANKING_EMPLEADOS_POR_EXTRAS"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ranking empleados por extras",
                List.of("Posición", "Empleado", "Extras vendidos", "Importe extras")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosExtrasRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getPosicion()),
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getTotalExtrasVendidos()),
                        InformePdfFormatUtils.formatMoney(row.getImporteExtras())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildProductosPorEmpleadoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeProductosPorEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeProductosPorEmpleadoResultDTO para PRODUCTOS_VENDIDOS_POR_EMPLEADO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Productos vendidos por empleado",
                List.of("Empleado", "Producto", "Unidades", "Importe")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeProductosPorEmpleadoRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getNombreProducto()),
                        InformePdfFormatUtils.formatText(row.getUnidadesVendidas()),
                        InformePdfFormatUtils.formatMoney(row.getImporteTotal())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildVentasPorCajaTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasCajaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasCajaResultDTO para VENTAS_POR_CAJA"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas por caja",
                List.of("Caja", "Ventas", "Devoluciones", "Neto", "Tickets")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeVentasCajaRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreCaja()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()),
                        InformePdfFormatUtils.formatMoney(row.getTotalNeto()),
                        InformePdfFormatUtils.formatText(row.getNumeroTickets())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildVentasPorSesionCajaTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasSesionCajaResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasSesionCajaResultDTO para VENTAS_POR_SESION_CAJA"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas por sesión de caja",
                List.of("Sesión", "Caja", "Empleado apertura", "Apertura", "Cierre", "Ventas", "Devoluciones", "Neto")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeVentasSesionCajaRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getIdSesion()),
                        InformePdfFormatUtils.formatText(row.getNombreCaja()),
                        InformePdfFormatUtils.formatText(row.getNombreEmpleadoApertura()),
                        InformePdfFormatUtils.formatDateTime(row.getFechaApertura()),
                        InformePdfFormatUtils.formatDateTime(row.getFechaCierre()),
                        InformePdfFormatUtils.formatMoney(row.getTotalVentas()),
                        InformePdfFormatUtils.formatMoney(row.getTotalDevoluciones()),
                        InformePdfFormatUtils.formatMoney(row.getTotalNeto())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildTiemposPorEstacionTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeTiemposEstacionResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeTiemposEstacionResultDTO para TIEMPOS_POR_ESTACION"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Tiempos por estación",
                List.of("Estación", "Tiempo medio (s)", "Tiempo máximo (s)", "Items procesados")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeTiemposEstacionRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreEstacion()),
                        InformePdfFormatUtils.formatText(row.getTiempoMedioSegundos()),
                        InformePdfFormatUtils.formatText(row.getTiempoMaximoSegundos()),
                        InformePdfFormatUtils.formatText(row.getItemsProcesados())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildMermaPorPeriodoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMermaPeriodoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeMermaPeriodoResultDTO para MERMA_POR_PERIODO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Merma por período",
                List.of("Fecha", "Tipo", "Origen", "Motivo", "Cantidad", "Observaciones")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeMermaPeriodoRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatDate(row.getFecha()),
                        InformePdfFormatUtils.formatText(row.getTipoMerma()),
                        InformePdfFormatUtils.formatText(row.getOrigen()),
                        InformePdfFormatUtils.formatText(row.getMotivo()),
                        InformePdfFormatUtils.formatText(row.getCantidad()),
                        InformePdfFormatUtils.formatText(row.getObservaciones())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildMovimientosStockTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeMovimientoStockResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeMovimientoStockResultDTO para MOVIMIENTOS_STOCK_AJUSTES"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Movimientos de stock / ajustes",
                List.of("Fecha", "Tipo movimiento", "Tipo objeto", "Objeto", "Cantidad", "Motivo", "Referencia")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeMovimientoStockRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatDateTime(row.getFecha()),
                        InformePdfFormatUtils.formatText(row.getTipoMovimiento()),
                        InformePdfFormatUtils.formatText(row.getTipoObjeto()),
                        InformePdfFormatUtils.formatText(row.getNombreObjeto()),
                        InformePdfFormatUtils.formatText(row.getCantidad()),
                        InformePdfFormatUtils.formatText(row.getMotivo()),
                        InformePdfFormatUtils.formatText(row.getReferencia())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildVentasProductoEmpleadoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasProductoEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasProductoEmpleadoResultDTO para VENTAS_PRODUCTO_POR_EMPLEADO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas producto por empleado",
                List.of("Empleado", "Producto", "Unidades", "Bruto", "Descuento", "Neto")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeVentasProductoEmpleadoRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getNombreProducto()),
                        InformePdfFormatUtils.formatText(row.getUnidadesVendidas()),
                        InformePdfFormatUtils.formatMoney(row.getImporteBruto()),
                        InformePdfFormatUtils.formatMoney(row.getImporteDescuento()),
                        InformePdfFormatUtils.formatMoney(row.getImporteNeto())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildRankingEmpleadosProductoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosProductoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosProductoResultDTO para RANKING_EMPLEADOS_POR_PRODUCTO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ranking empleados por producto",
                List.of("Posición", "Empleado", "Producto", "Unidades", "Neto")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosProductoRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getPosicion()),
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getNombreProducto()),
                        InformePdfFormatUtils.formatText(row.getUnidadesVendidas()),
                        InformePdfFormatUtils.formatMoney(row.getImporteNeto())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildVentasExtraEmpleadoTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeVentasExtraEmpleadoResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeVentasExtraEmpleadoResultDTO para VENTAS_EXTRA_POR_EMPLEADO"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ventas extra por empleado",
                List.of("Empleado", "Extra", "Tipo", "Veces", "Importe")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeVentasExtraEmpleadoRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getNombreExtra()),
                        InformePdfFormatUtils.formatText(row.getTipoExtra()),
                        InformePdfFormatUtils.formatText(row.getVecesVendido()),
                        InformePdfFormatUtils.formatMoney(row.getImporteGenerado())
                ));
            }
        }

        return table;
    }
    private InformePdfTableData buildRankingEmpleadosExtraTable(InformePdfExportRequest request) {
        if (!(request.getResult() instanceof InformeRankingEmpleadosExtraResultDTO result)) {
            throw new IllegalArgumentException(
                    "El result del request no es InformeRankingEmpleadosExtraResultDTO para RANKING_EMPLEADOS_POR_EXTRA"
            );
        }

        InformePdfTableData table = newTable(
                "Resultado · Ranking empleados por extra",
                List.of("Posición", "Empleado", "Extra", "Tipo", "Veces", "Importe")
        );

        if (result.getRows() != null) {
            for (dtoS.InformeRankingEmpleadosExtraRowDTO row : result.getRows()) {
                table.addFila(List.of(
                        InformePdfFormatUtils.formatText(row.getPosicion()),
                        InformePdfFormatUtils.formatText(row.getNombreEmpleado()),
                        InformePdfFormatUtils.formatText(row.getNombreExtra()),
                        InformePdfFormatUtils.formatText(row.getTipoExtra()),
                        InformePdfFormatUtils.formatText(row.getVecesVendido()),
                        InformePdfFormatUtils.formatMoney(row.getImporteGenerado())
                ));
            }
        }

        return table;
    }
    
    private InformePdfTableData newTable(String titulo, List<String> columnas) {
        InformePdfTableData table = new InformePdfTableData();
        table.setTituloTabla(titulo);
        table.setColumnas(columnas);
        return table;
    }
}
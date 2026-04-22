package service;

import dtoS.InformeFiltroDTO;
import dtoS.InformePdfDocumentData;
import dtoS.InformePdfExportRequest;
import util.InformePdfChartBuilder;
import util.InformePdfFormatUtils;
import util.InformePdfKpiBuilder;
import util.InformePdfTableBuilder;

/**
 * Builder responsable de construir el objeto final de documento PDF
 * a partir del request de exportación.
 *
 * Responsabilidades:
 * - Crear la estructura final que consumirá el renderer PDF.
 * - Unificar:
 *      - cabecera
 *      - subtítulo
 *      - filtros
 *      - usuario
 *      - fecha de generación
 *      - KPIs
 *      - tabla
 *
 * IMPORTANTE:
 * - Esta clase NO pinta el PDF.
 * - Esta clase NO consulta la BD.
 * - Esta clase NO depende de Swing.
 *
 * Flujo:
 * 1) valida el request
 * 2) construye KPIs con InformePdfKpiBuilder
 * 3) construye tabla con InformePdfTableBuilder
 * 4) rellena el documento final
 */
public class InformePdfDocumentBuilder {

    private final InformePdfTableBuilder tableBuilder;
    private final InformePdfKpiBuilder kpiBuilder;
    private final InformePdfChartBuilder chartBuilder;

    public InformePdfDocumentBuilder() {
        this.tableBuilder = new InformePdfTableBuilder();
        this.kpiBuilder = new InformePdfKpiBuilder();
        this.chartBuilder = new InformePdfChartBuilder();
    }

    public InformePdfDocumentBuilder(InformePdfTableBuilder tableBuilder,
            InformePdfKpiBuilder kpiBuilder,
            InformePdfChartBuilder chartBuilder) {
this.tableBuilder = tableBuilder != null ? tableBuilder : new InformePdfTableBuilder();
this.kpiBuilder = kpiBuilder != null ? kpiBuilder : new InformePdfKpiBuilder();
this.chartBuilder = chartBuilder != null ? chartBuilder : new InformePdfChartBuilder();
}

    /**
     * Construye el documento PDF final listo para ser renderizado.
     *
     * @param request request de exportación
     * @return estructura completa del documento PDF
     */
    public InformePdfDocumentData build(InformePdfExportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request no puede ser null");
        }

        request.validate();

        InformePdfDocumentData documentData = new InformePdfDocumentData();

        // =====================================================
        // CABECERA
        // =====================================================
        documentData.setTitulo(request.getTipoInforme().getDisplayName());
        documentData.setSubtitulo(request.getTipoInforme().getShortDescription());

        // =====================================================
        // BLOQUE CONTEXTUAL
        // =====================================================
        documentData.setResumenFiltros(buildResumenFiltros(request));
        documentData.setUsuarioGenerador(buildUsuarioGenerador(request));
        documentData.setFechaGeneracionTexto(
                InformePdfFormatUtils.formatDateTime(request.getFechaGeneracion())
        );

        // =====================================================
        // KPIs Y TABLA y Grafico 
        // =====================================================
        documentData.setKpis(kpiBuilder.build(request));
        documentData.setTableData(tableBuilder.build(request));
        documentData.setChartImage(chartBuilder.buildChartImage(request));

        return documentData;
    }

    /**
     * Devuelve un resumen de filtros seguro.
     *
     * Si el resumen viene vacío o nulo desde la UI,
     * devolvemos un texto por defecto para que el PDF
     * no quede con un bloque en blanco extraño.
     */
    private String buildResumenFiltros(InformePdfExportRequest request) {
        InformeFiltroDTO filtro = request.getFiltroDTO();

        if (filtro == null) {
            String resumen = request.getResumenFiltros();
            return (resumen == null || resumen.isBlank())
                    ? "Sin resumen de filtros disponible."
                    : resumen.trim();
        }

        String periodo = buildPeriodoTexto(filtro);
        String vista = request.getModoVista() != null ? request.getModoVista().name() : "N/D";
        String empleados = filtro.isTodosLosEmpleados() ? "Todos" : "Selección manual";

        return "Periodo: " + periodo
                + " | Vista: " + vista
                + " | Empleados: " + empleados;
    }
    /**
     * Devuelve el nombre de usuario generador de forma segura.
     *
     * Si no llega el usuario desde la UI, se deja un texto neutro.
     */
    private String buildUsuarioGenerador(InformePdfExportRequest request) {
        String usuario = request.getUsuarioGenerador();

        if (usuario == null || usuario.isBlank()) {
            return "Usuario no disponible";
        }

        return usuario.trim();
    }
    private String buildPeriodoTexto(InformeFiltroDTO filtro) {
        if (filtro.getFechaDesde() == null && filtro.getFechaHasta() == null) {
            return "Sin rango definido";
        }

        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            return InformePdfFormatUtils.formatDate(filtro.getFechaDesde())
                    + " - "
                    + InformePdfFormatUtils.formatDate(filtro.getFechaHasta());
        }

        if (filtro.getFechaDesde() != null) {
            return "Desde " + InformePdfFormatUtils.formatDate(filtro.getFechaDesde());
        }

        return "Hasta " + InformePdfFormatUtils.formatDate(filtro.getFechaHasta());
    }
}

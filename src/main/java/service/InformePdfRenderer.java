package service;

import dtoS.InformePdfDocumentData;
import dtoS.InformePdfKpiItem;
import dtoS.InformePdfTableData;
import util.InformePdfStyles;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Renderer principal del PDF.
 *
 * Responsabilidades:
 * - Crear páginas dentro del documento PDF.
 * - Dibujar:
 *      - cabecera
 *      - subtítulo
 *      - bloque de metadatos/filtros
 *      - KPIs
 *      - tabla
 *      - pie de página
 * - Gestionar saltos de página.
 *
 * IMPORTANTE:
 * - Esta clase NO consulta la BD.
 * - Esta clase NO interpreta DTOs de negocio.
 * - Solo pinta un InformePdfDocumentData ya preparado.
 *
 * Primera versión:
 * - A4 vertical
 * - Estilo limpio y profesional
 * - Columnas con ancho uniforme
 * - Texto de tabla truncado con "..."
 * - Saltos de página automáticos
 */
public class InformePdfRenderer {

    // =====================================================
    // FUENTES BASE PDF
    // =====================================================

    private static final PDFont FONT_REGULAR = PDType1Font.HELVETICA;
    private static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont FONT_ITALIC = PDType1Font.HELVETICA_OBLIQUE;

    // =====================================================
    // ESTADO INTERNO DEL RENDER
    // =====================================================

    private PDDocument document;
    private InformePdfDocumentData data;

    private PDPage currentPage;
    private PDPageContentStream contentStream;

    private float cursorY;
    private int pageNumber;

    /**
     * Punto de entrada principal.
     *
     * @param document documento PDF ya creado
     * @param data     datos listos para renderizar
     */
    public void render(PDDocument document, InformePdfDocumentData data) throws IOException {
        this.document = Objects.requireNonNull(document, "document no puede ser null");
        this.data = Objects.requireNonNull(data, "data no puede ser null");

        try {
            startNewPage();
            drawDocumentHeader();
            drawContextBlock();
            drawKpis();
            drawChart();
            drawTable();
        } finally {
            closeCurrentPage();
        }
    }

    // =====================================================
    // CICLO DE VIDA DE PÁGINAS
    // =====================================================

    /**
     * Abre una nueva página y prepara el content stream.
     *
     * Si ya había una página abierta, primero pinta su pie y la cierra.
     */
    private void startNewPage() throws IOException {
        closeCurrentPage();

        currentPage = new PDPage(InformePdfStyles.PAGE_SIZE);
        document.addPage(currentPage);

        contentStream = new PDPageContentStream(document, currentPage);
        cursorY = InformePdfStyles.getStartY();
        pageNumber++;
    }

    /**
     * Cierra la página actual si existe.
     */
    private void closeCurrentPage() throws IOException {
        if (contentStream != null) {
            drawFooter();
            contentStream.close();
            contentStream = null;
        }
    }

    /**
     * Garantiza que hay espacio vertical suficiente.
     *
     * Si no hay espacio, abre nueva página.
     */
    private void ensureSpace(float requiredHeight) throws IOException {
        if ((cursorY - requiredHeight) < InformePdfStyles.getMinY()) {
            startNewPage();
        }
    }

    // =====================================================
    // BLOQUES PRINCIPALES
    // =====================================================

    /**
     * Dibuja la cabecera principal del documento:
     * - título
     * - subtítulo
     */
    private void drawDocumentHeader() throws IOException {
        float startX = InformePdfStyles.MARGIN_LEFT;
        float contentWidth = InformePdfStyles.getContentWidth();

        // Título principal
        drawText(
                data.getTitulo(),
                startX,
                cursorY,
                FONT_BOLD,
                InformePdfStyles.FONT_SIZE_TITLE,
                InformePdfStyles.COLOR_TEXT_PRIMARY
        );
        cursorY -= 4f;
        cursorY -= InformePdfStyles.SPACE_AFTER_TITLE;

        // Subtítulo con wrapping
        List<String> subtitleLines = wrapText(
                safeText(data.getSubtitulo()),
                FONT_REGULAR,
                InformePdfStyles.FONT_SIZE_SUBTITLE,
                contentWidth
        );

        for (String line : subtitleLines) {
            drawText(
                    line,
                    startX,
                    cursorY,
                    FONT_REGULAR,
                    InformePdfStyles.FONT_SIZE_SUBTITLE,
                    InformePdfStyles.COLOR_TEXT_SECONDARY
            );
            cursorY -= (InformePdfStyles.FONT_SIZE_SUBTITLE + 3f);
        }

        cursorY -= InformePdfStyles.SPACE_AFTER_SUBTITLE;

        drawHorizontalLine(cursorY, 0.8f, InformePdfStyles.COLOR_LINE);
        cursorY -= 12f;
    }

    /**
     * Dibuja bloque de:
     * - usuario generador
     * - fecha de generación
     * - resumen de filtros
     */
    private void drawContextBlock() throws IOException {
        float startX = InformePdfStyles.MARGIN_LEFT;
        float contentWidth = InformePdfStyles.getContentWidth();

        String metaLine = "Generado por: " + safeText(data.getUsuarioGenerador())
                + "    |    Fecha: " + safeText(data.getFechaGeneracionTexto());

        drawText(
                metaLine,
                startX,
                cursorY,
                FONT_REGULAR,
                InformePdfStyles.FONT_SIZE_SMALL,
                InformePdfStyles.COLOR_TEXT_SECONDARY
        );
        cursorY -= InformePdfStyles.SPACE_AFTER_META;

        drawSectionTitle("Filtros aplicados");
        cursorY -= 6f;
        List<String> filterLines = wrapText(
                safeText(data.getResumenFiltros()),
                FONT_REGULAR,
                InformePdfStyles.FONT_SIZE_BODY,
                contentWidth
        );

        for (String line : filterLines) {
            ensureSpace(InformePdfStyles.FONT_SIZE_BODY + 4f);

            drawText(
                    line,
                    startX,
                    cursorY,
                    FONT_REGULAR,
                    InformePdfStyles.FONT_SIZE_BODY,
                    InformePdfStyles.COLOR_TEXT_PRIMARY
            );
            cursorY -= (InformePdfStyles.FONT_SIZE_BODY + 4f);
        }

        cursorY -= InformePdfStyles.SPACE_AFTER_FILTERS;
    }

    /**
     * Dibuja el bloque de KPIs en una rejilla simple.
     *
     * Primera versión:
     * - máximo 3 KPIs por fila
     */
    private void drawKpis() throws IOException {
        List<InformePdfKpiItem> kpis = data.getKpis() != null ? data.getKpis() : Collections.emptyList();

        if (kpis.isEmpty()) {
            return;
        }

        drawSectionTitle("Indicadores clave");

        int columns = Math.min(3, Math.max(1, kpis.size()));
        float contentWidth = InformePdfStyles.getContentWidth();
        float boxGap = InformePdfStyles.KPI_GAP;
        float totalGap = (columns - 1) * boxGap;
        float boxWidth = (contentWidth - totalGap) / columns;

        float startX = InformePdfStyles.MARGIN_LEFT;

        int index = 0;
        while (index < kpis.size()) {
            ensureSpace(InformePdfStyles.KPI_BOX_HEIGHT + 8f);

            for (int col = 0; col < columns && index < kpis.size(); col++, index++) {
                float x = startX + col * (boxWidth + boxGap);
                float yTop = cursorY;

                drawFilledRect(
                        x,
                        yTop - InformePdfStyles.KPI_BOX_HEIGHT,
                        boxWidth,
                        InformePdfStyles.KPI_BOX_HEIGHT,
                        InformePdfStyles.COLOR_KPI_BG
                );
                drawRectBorder(
                        x,
                        yTop - InformePdfStyles.KPI_BOX_HEIGHT,
                        boxWidth,
                        InformePdfStyles.KPI_BOX_HEIGHT,
                        InformePdfStyles.COLOR_LINE,
                        0.6f
                );

                InformePdfKpiItem item = kpis.get(index);

                String label = truncateToFit(
                        safeText(item.getLabel()),
                        FONT_REGULAR,
                        InformePdfStyles.KPI_LABEL_FONT_SIZE,
                        boxWidth - (2 * InformePdfStyles.KPI_INTERNAL_PADDING)
                );

                String value = truncateToFit(
                        safeText(item.getValue()),
                        FONT_BOLD,
                        InformePdfStyles.KPI_VALUE_FONT_SIZE,
                        boxWidth - (2 * InformePdfStyles.KPI_INTERNAL_PADDING)
                );

                drawText(
                        label,
                        x + InformePdfStyles.KPI_INTERNAL_PADDING,
                        yTop - 12f,
                        FONT_REGULAR,
                        InformePdfStyles.KPI_LABEL_FONT_SIZE,
                        InformePdfStyles.COLOR_TEXT_SECONDARY
                );

                drawText(
                        value,
                        x + InformePdfStyles.KPI_INTERNAL_PADDING,
                        yTop - 25f,
                        FONT_BOLD,
                        InformePdfStyles.KPI_VALUE_FONT_SIZE,
                        InformePdfStyles.COLOR_TEXT_PRIMARY
                );
            }

            cursorY -= (InformePdfStyles.KPI_BOX_HEIGHT + 10f);
        }

        cursorY -= InformePdfStyles.SPACE_AFTER_KPIS;
    }

    /**
     * Dibuja la tabla completa con paginación automática.
     */
    private void drawTable() throws IOException {
        InformePdfTableData tableData = data.getTableData();

        if (tableData == null || tableData.getColumnas() == null || tableData.getColumnas().isEmpty()) {
            return;
        }

        drawSectionTitle(safeText(tableData.getTituloTabla()));

        List<String> columns = tableData.getColumnas();
        List<List<String>> rows = tableData.getFilas() != null ? tableData.getFilas() : Collections.emptyList();

        float startX = InformePdfStyles.MARGIN_LEFT;
        float tableWidth = InformePdfStyles.getContentWidth();
        float columnWidth = tableWidth / columns.size();

        // Cabecera inicial
        ensureSpace(InformePdfStyles.TABLE_HEADER_HEIGHT + InformePdfStyles.TABLE_ROW_HEIGHT);
        drawTableHeader(startX, cursorY, columns, columnWidth);
        cursorY -= InformePdfStyles.TABLE_HEADER_HEIGHT;

        for (List<String> row : rows) {
            ensureSpace(InformePdfStyles.TABLE_ROW_HEIGHT);

            // Si ha saltado de página, re-dibujamos cabecera tabla
            if ((cursorY - InformePdfStyles.TABLE_ROW_HEIGHT) < InformePdfStyles.getMinY()) {
                startNewPage();
                drawSectionTitle(safeText(tableData.getTituloTabla()) + " (continuación)");
                drawTableHeader(startX, cursorY, columns, columnWidth);
                cursorY -= InformePdfStyles.TABLE_HEADER_HEIGHT;
            }

            drawTableRow(startX, cursorY, row, columnWidth, columns.size());
            cursorY -= InformePdfStyles.TABLE_ROW_HEIGHT;
        }
    }

    // =====================================================
    // DIBUJO DE TABLA
    // =====================================================

    private void drawTableHeader(float startX,
                                 float yTop,
                                 List<String> columns,
                                 float columnWidth) throws IOException {
        float x = startX;

        for (String column : columns) {
            drawFilledRect(
                    x,
                    yTop - InformePdfStyles.TABLE_HEADER_HEIGHT,
                    columnWidth,
                    InformePdfStyles.TABLE_HEADER_HEIGHT,
                    InformePdfStyles.COLOR_TABLE_HEADER_BG
            );

            drawRectBorder(
                    x,
                    yTop - InformePdfStyles.TABLE_HEADER_HEIGHT,
                    columnWidth,
                    InformePdfStyles.TABLE_HEADER_HEIGHT,
                    InformePdfStyles.COLOR_LINE,
                    0.5f
            );

            String text = truncateToFit(
                    safeText(column),
                    FONT_BOLD,
                    InformePdfStyles.FONT_SIZE_TABLE_HEADER,
                    columnWidth - 2 * InformePdfStyles.TABLE_CELL_PADDING_X
            );

            drawText(
                    text,
                    x + InformePdfStyles.TABLE_CELL_PADDING_X,
                    yTop - 14f,
                    FONT_BOLD,
                    InformePdfStyles.FONT_SIZE_TABLE_HEADER,
                    InformePdfStyles.COLOR_TEXT_PRIMARY
            );

            x += columnWidth;
        }
    }

    private void drawTableRow(float startX,
                              float yTop,
                              List<String> row,
                              float columnWidth,
                              int expectedColumns) throws IOException {
        float x = startX;

        List<String> safeRow = normalizeRow(row, expectedColumns);

        for (String cell : safeRow) {
            drawRectBorder(
                    x,
                    yTop - InformePdfStyles.TABLE_ROW_HEIGHT,
                    columnWidth,
                    InformePdfStyles.TABLE_ROW_HEIGHT,
                    InformePdfStyles.COLOR_LINE,
                    0.4f
            );

            String text = truncateToFit(
                    safeText(cell),
                    FONT_REGULAR,
                    InformePdfStyles.FONT_SIZE_TABLE_BODY,
                    columnWidth - 2 * InformePdfStyles.TABLE_CELL_PADDING_X
            );

            drawText(
                    text,
                    x + InformePdfStyles.TABLE_CELL_PADDING_X,
                    yTop - 13f,
                    FONT_REGULAR,
                    InformePdfStyles.FONT_SIZE_TABLE_BODY,
                    InformePdfStyles.COLOR_TEXT_PRIMARY
            );

            x += columnWidth;
        }
    }
    private void drawChart() throws IOException {
        if (data.getChartImage() == null) {
            return;
        }

        drawSectionTitle("Gráfico");

        float maxWidth = InformePdfStyles.getContentWidth();
        float maxHeight = 220f;

        int imgWidth = data.getChartImage().getWidth();
        int imgHeight = data.getChartImage().getHeight();

        if (imgWidth <= 0 || imgHeight <= 0) {
            return;
        }

        float scale = Math.min(maxWidth / imgWidth, maxHeight / imgHeight);
        float drawWidth = imgWidth * scale;
        float drawHeight = imgHeight * scale;

        ensureSpace(drawHeight + 12f);

        if ((cursorY - drawHeight) < InformePdfStyles.getMinY()) {
            startNewPage();
            drawSectionTitle("Gráfico");
        }

        PDImageXObject pdfImage = LosslessFactory.createFromImage(document, data.getChartImage());

        contentStream.drawImage(
                pdfImage,
                InformePdfStyles.MARGIN_LEFT,
                cursorY - drawHeight,
                drawWidth,
                drawHeight
        );

        cursorY -= (drawHeight + 14f);
    }

    // =====================================================
    // PIE DE PÁGINA
    // =====================================================

    /**
     * Dibuja el pie de la página actual.
     */
    private void drawFooter() throws IOException {
        if (contentStream == null) {
            return;
        }

        float y = InformePdfStyles.FOOTER_Y_OFFSET;
        float startX = InformePdfStyles.MARGIN_LEFT;

        drawHorizontalLine(y + 12f, 0.6f, InformePdfStyles.COLOR_LINE);

        String footerLeft = "Informe exportado";
        String footerRight = "Página " + pageNumber;

        drawText(
                footerLeft,
                startX,
                y,
                FONT_ITALIC,
                InformePdfStyles.FONT_SIZE_FOOTER,
                InformePdfStyles.COLOR_TEXT_SECONDARY
        );

        float rightTextWidth = getTextWidth(
                footerRight,
                FONT_REGULAR,
                InformePdfStyles.FONT_SIZE_FOOTER
        );

        drawText(
                footerRight,
                InformePdfStyles.PAGE_SIZE.getWidth() - InformePdfStyles.MARGIN_RIGHT - rightTextWidth,
                y,
                FONT_REGULAR,
                InformePdfStyles.FONT_SIZE_FOOTER,
                InformePdfStyles.COLOR_TEXT_SECONDARY
        );
    }

    // =====================================================
    // HELPERS DE SECCIONES
    // =====================================================

    private void drawSectionTitle(String title) throws IOException {
        ensureSpace(22f);

        drawText(
                safeText(title),
                InformePdfStyles.MARGIN_LEFT,
                cursorY,
                FONT_BOLD,
                InformePdfStyles.FONT_SIZE_SECTION,
                InformePdfStyles.COLOR_TEXT_PRIMARY
        );

        cursorY -= InformePdfStyles.SPACE_AFTER_SECTION_TITLE;
    }

    // =====================================================
    // HELPERS GRÁFICOS
    // =====================================================

    private void drawText(String text,
                          float x,
                          float y,
                          PDFont font,
                          float fontSize,
                          Color color) throws IOException {
        if (text == null || text.isBlank()) {
            return;
        }

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private void drawHorizontalLine(float y, float lineWidth, Color color) throws IOException {
        contentStream.setStrokingColor(color);
        contentStream.setLineWidth(lineWidth);
        contentStream.moveTo(InformePdfStyles.MARGIN_LEFT, y);
        contentStream.lineTo(
                InformePdfStyles.PAGE_SIZE.getWidth() - InformePdfStyles.MARGIN_RIGHT,
                y
        );
        contentStream.stroke();
    }

    private void drawFilledRect(float x,
                                float y,
                                float width,
                                float height,
                                Color fillColor) throws IOException {
        contentStream.setNonStrokingColor(fillColor);
        contentStream.addRect(x, y, width, height);
        contentStream.fill();
    }

    private void drawRectBorder(float x,
                                float y,
                                float width,
                                float height,
                                Color borderColor,
                                float lineWidth) throws IOException {
        contentStream.setStrokingColor(borderColor);
        contentStream.setLineWidth(lineWidth);
        contentStream.addRect(x, y, width, height);
        contentStream.stroke();
    }

    // =====================================================
    // HELPERS DE TEXTO
    // =====================================================

    /**
     * Divide un texto largo en líneas según ancho máximo.
     */
    private List<String> wrapText(String text,
                                  PDFont font,
                                  float fontSize,
                                  float maxWidth) throws IOException {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<String> lines = new ArrayList<>();
        String[] words = text.trim().split("\\s+");

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.isEmpty()
                    ? word
                    : currentLine + " " + word;

            float candidateWidth = getTextWidth(candidate, font, fontSize);

            if (candidateWidth <= maxWidth) {
                currentLine.setLength(0);
                currentLine.append(candidate);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    /**
     * Trunca un texto para que quepa en una celda o bloque.
     */
    private String truncateToFit(String text,
                                 PDFont font,
                                 float fontSize,
                                 float maxWidth) throws IOException {
        String safe = safeText(text);

        if (safe.isEmpty()) {
            return "";
        }

        if (getTextWidth(safe, font, fontSize) <= maxWidth) {
            return safe;
        }

        String ellipsis = "...";
        float ellipsisWidth = getTextWidth(ellipsis, font, fontSize);

        StringBuilder sb = new StringBuilder();
        for (char c : safe.toCharArray()) {
            String candidate = sb.toString() + c;
            float width = getTextWidth(candidate, font, fontSize);

            if ((width + ellipsisWidth) > maxWidth) {
                break;
            }

            sb.append(c);
        }

        return sb + ellipsis;
    }

    private float getTextWidth(String text, PDFont font, float fontSize) throws IOException {
        if (text == null || text.isEmpty()) {
            return 0f;
        }
        return (font.getStringWidth(text) / 1000f) * fontSize;
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }

    /**
     * Normaliza una fila para que tenga exactamente el número de columnas esperado.
     */
    private List<String> normalizeRow(List<String> row, int expectedColumns) {
        List<String> normalized = new ArrayList<>();

        if (row != null) {
            normalized.addAll(row);
        }

        while (normalized.size() < expectedColumns) {
            normalized.add("");
        }

        if (normalized.size() > expectedColumns) {
            return new ArrayList<>(normalized.subList(0, expectedColumns));
        }

        return normalized;
    }
}
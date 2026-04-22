package util;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.Color;

/**
 * Clase de estilos y constantes visuales para la exportación PDF.
 *
 * Responsabilidades:
 * - Centralizar medidas y estilos del documento.
 * - Evitar números mágicos dentro del renderer.
 * - Servir como punto único de ajuste visual del PDF.
 *
 * IMPORTANTE:
 * - Esta clase NO renderiza nada.
 * - Esta clase NO contiene lógica de negocio.
 * - Esta clase solo define constantes y pequeños helpers geométricos.
 *
 * Primera versión:
 * - Pensada para informes A4 verticales.
 * - Estilo limpio y profesional.
 * - Sin depender del tema Swing.
 */
public final class InformePdfStyles {

    // =====================================================
    // PÁGINA
    // =====================================================

    /**
     * Tamaño de página base del informe.
     * Usamos A4 vertical para una primera versión estándar.
     */
    public static final PDRectangle PAGE_SIZE = PDRectangle.A4;

    // =====================================================
    // MÁRGENES
    // =====================================================

    public static final float MARGIN_TOP = 48f;
    public static final float MARGIN_RIGHT = 42f;
    public static final float MARGIN_BOTTOM = 48f;
    public static final float MARGIN_LEFT = 42f;

    // =====================================================
    // TIPOGRAFÍA - TAMAÑOS
    // =====================================================

    public static final float FONT_SIZE_TITLE = 18f;
    public static final float FONT_SIZE_SUBTITLE = 10f;
    public static final float FONT_SIZE_SECTION = 11f;
    public static final float FONT_SIZE_BODY = 9f;
    public static final float FONT_SIZE_SMALL = 8f;
    public static final float FONT_SIZE_TABLE_HEADER = 8.5f;
    public static final float FONT_SIZE_TABLE_BODY = 8.5f;
    public static final float FONT_SIZE_FOOTER = 8f;

    // =====================================================
    // ESPACIADOS VERTICALES
    // =====================================================

    public static final float SPACE_AFTER_TITLE = 8f;
    public static final float SPACE_AFTER_SUBTITLE = 14f;
    public static final float SPACE_AFTER_META = 14f;
    public static final float SPACE_AFTER_FILTERS = 16f;
    public static final float SPACE_AFTER_KPIS = 18f;
    public static final float SPACE_BEFORE_TABLE = 10f;
    public static final float SPACE_AFTER_SECTION_TITLE = 8f;

    // =====================================================
    // KPIs
    // =====================================================

    public static final float KPI_BOX_HEIGHT = 34f;
    public static final float KPI_GAP = 8f;
    public static final float KPI_LABEL_FONT_SIZE = 8f;
    public static final float KPI_VALUE_FONT_SIZE = 10f;
    public static final float KPI_INTERNAL_PADDING = 6f;

    // =====================================================
    // TABLA
    // =====================================================

    public static final float TABLE_ROW_HEIGHT = 20f;
    public static final float TABLE_HEADER_HEIGHT = 22f;
    public static final float TABLE_CELL_PADDING_X = 4f;
    public static final float TABLE_CELL_PADDING_Y = 5f;
    public static final float TABLE_MIN_BOTTOM_RESERVE = 42f;

    // =====================================================
    // PIE DE PÁGINA
    // =====================================================

    public static final float FOOTER_Y_OFFSET = 24f;

    // =====================================================
    // COLORES
    // =====================================================

    /**
     * Colores neutros para una V1 limpia.
     * Más adelante puedes cambiar a verdes corporativos si quieres.
     */
    public static final Color COLOR_TEXT_PRIMARY = new Color(35, 35, 35);
    public static final Color COLOR_TEXT_SECONDARY = new Color(95, 95, 95);
    public static final Color COLOR_LINE = new Color(210, 210, 210);
    public static final Color COLOR_TABLE_HEADER_BG = new Color(235, 239, 242);
    public static final Color COLOR_KPI_BG = new Color(245, 247, 249);
    public static final Color COLOR_WHITE = Color.WHITE;

    private InformePdfStyles() {
        // Utility class
    }

    // =====================================================
    // HELPERS GEOMÉTRICOS
    // =====================================================

    /**
     * Devuelve el ancho útil de contenido dentro de la página,
     * descontando márgenes laterales.
     */
    public static float getContentWidth() {
        return PAGE_SIZE.getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
    }

    /**
     * Devuelve la coordenada Y inicial útil para empezar a dibujar
     * contenido en una página nueva.
     */
    public static float getStartY() {
        return PAGE_SIZE.getHeight() - MARGIN_TOP;
    }

    /**
     * Devuelve la coordenada Y mínima recomendable antes de considerar
     * un salto de página.
     *
     * Se reserva espacio para el pie de página y evitar que la tabla
     * termine pegada abajo.
     */
    public static float getMinY() {
        return MARGIN_BOTTOM + TABLE_MIN_BOTTOM_RESERVE;
    }

    /**
     * Devuelve el ancho recomendado para cada KPI en una rejilla horizontal.
     *
     * @param kpiCount número de KPIs a mostrar en la fila
     * @return ancho disponible por KPI
     */
    public static float calculateKpiBoxWidth(int kpiCount) {
        if (kpiCount <= 0) {
            return getContentWidth();
        }

        float totalGap = (kpiCount - 1) * KPI_GAP;
        return (getContentWidth() - totalGap) / kpiCount;
    }

    /**
     * Calcula el ancho de columnas repartidas de forma uniforme.
     *
     * Útil para la primera versión del renderer.
     * Más adelante podemos meter anchos específicos por informe.
     *
     * @param columnCount número de columnas
     * @return ancho uniforme por columna
     */
    public static float calculateUniformColumnWidth(int columnCount) {
        if (columnCount <= 0) {
            return getContentWidth();
        }
        return getContentWidth() / columnCount;
    }
}
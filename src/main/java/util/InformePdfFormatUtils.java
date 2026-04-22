package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidades comunes de formateo para exportación PDF.
 *
 * IMPORTANTE:
 * - Centralizar aquí el formato evita duplicarlo
 *   en builders y renderer.
 */
public final class InformePdfFormatUtils {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-ES");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private InformePdfFormatUtils() {
    }

    public static String formatMoney(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return String.format(LOCALE_ES, "%,.2f €", safe);
    }

    public static String formatPercent(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return String.format(LOCALE_ES, "%,.2f %%", safe);
    }

    public static String formatDate(LocalDate value) {
        return value != null ? value.format(DATE_FORMAT) : "";
    }

    public static String formatDateTime(LocalDateTime value) {
        return value != null ? value.format(DATE_TIME_FORMAT) : "";
    }

    public static String formatText(Object value) {
        return value != null ? String.valueOf(value) : "";
    }
}
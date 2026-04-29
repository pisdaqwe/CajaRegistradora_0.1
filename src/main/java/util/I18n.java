package util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utilidad central de internacionalización.
 *
 * Los textos se cargan desde:
 * - src/main/resources/i18n/messages_es.properties
 * - src/main/resources/i18n/messages_en.properties
 *
 * Uso:
 * I18n.t("common.save")
 * I18n.t("dialog.welcome", nombre)
 */
public final class I18n {

    private static final String BASE_NAME = "i18n.messages";
    private static final String DEFAULT_LANGUAGE = "es";

    private static Locale currentLocale = new Locale("es", "ES");
    private static ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, currentLocale);

    private I18n() {
    }

    public static void setLanguage(String languageCode) {
        String normalized = normalizeLanguage(languageCode);

        switch (normalized) {
            case "en":
                currentLocale = Locale.ENGLISH;
                break;

            case "es":
            default:
                currentLocale = new Locale("es", "ES");
                break;
        }

        bundle = ResourceBundle.getBundle(BASE_NAME, currentLocale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getCurrentLanguageCode() {
        return currentLocale.getLanguage();
    }

    public static boolean isSpanish() {
        return "es".equalsIgnoreCase(getCurrentLanguageCode());
    }

    public static boolean isEnglish() {
        return "en".equalsIgnoreCase(getCurrentLanguageCode());
    }

    public static String t(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "";
        }

        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            return "!" + key + "!";
        }
    }

    public static String t(String key, Object... args) {
        String pattern = t(key);
        return MessageFormat.format(pattern, args);
    }

    private static String normalizeLanguage(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return DEFAULT_LANGUAGE;
        }

        String value = languageCode.trim().toLowerCase();

        if (value.startsWith("en")) {
            return "en";
        }

        return "es";
    }
}
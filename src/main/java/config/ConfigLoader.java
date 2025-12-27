package config;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class ConfigLoader {

    private static final String CONFIG_FILE = "config.properties";

    private static Properties properties;
    private static boolean loaded = false;

    // =========================================
    // CONSTRUCTOR PRIVADO
    // =========================================
    private ConfigLoader() {
        // Evita instanciación
    }

    // =========================================
    // CARGA DE CONFIGURACIÓN
    // =========================================
    public static void load() {

        if (loaded) return;

        try (InputStream is = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (is == null) {
                throw new RuntimeException(
                        "No se encontró " + CONFIG_FILE + " en resources"
                );
            }

            properties = new Properties();
            properties.load(is);
            loaded = true;

            System.out.println("[CONFIG] Configuración cargada correctamente.");

        } catch (Exception e) {
            System.err.println("[ERROR] Error cargando configuración");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void checkLoaded() {
        if (!loaded) {
            throw new IllegalStateException(
                    "ConfigLoader no inicializado. Llama a ConfigLoader.load()"
            );
        }
    }

    // =========================================
    // APLICACIÓN
    // =========================================
    public static String getAppName() {
        checkLoaded();
        return properties.getProperty("app.name", "TPV");
    }

    public static String getAppVersion() {
        checkLoaded();
        return properties.getProperty("app.version", "0.0.0");
    }

    public static boolean isDebug() {
        checkLoaded();
        return Boolean.parseBoolean(
                properties.getProperty("app.debug", "false")
        );
    }

    public static String getTimezone() {
        checkLoaded();
        return properties.getProperty("app.timezone", "Europe/Madrid");
    }

    // =========================================
    // LOCALIZACIÓN
    // =========================================
    public static Locale getLocale() {
        checkLoaded();
        String language = properties.getProperty("locale.language", "es");
        String country = properties.getProperty("locale.country", "ES");
        return new Locale(language, country);
    }

    public static String getCurrency() {
        checkLoaded();
        return properties.getProperty("currency", "EUR");
    }

    // =========================================
    // BASE DE DATOS
    // =========================================
    public static String getDbUrl() {
        checkLoaded();
        return properties.getProperty("db.url");
    }

    public static String getDbUser() {
        checkLoaded();
        return properties.getProperty("db.user");
    }

    public static String getDbPassword() {
        checkLoaded();
        return properties.getProperty("db.password");
    }

    public static String getDbDriver() {
        checkLoaded();
        return properties.getProperty(
                "db.driver",
                "org.mariadb.jdbc.Driver"
        );
    }

    // =========================================
    // HIKARICP
    // =========================================
    public static int getDbPoolMaxSize() {
        checkLoaded();
        return Integer.parseInt(
                properties.getProperty("db.pool.maxSize", "10")
        );
    }

    public static int getDbPoolMinIdle() {
        checkLoaded();
        return Integer.parseInt(
                properties.getProperty("db.pool.minIdle", "2")
        );
    }

    public static long getDbConnectionTimeout() {
        checkLoaded();
        return Long.parseLong(
                properties.getProperty("db.pool.connectionTimeout", "30000")
        );
    }

    public static long getDbIdleTimeout() {
        checkLoaded();
        return Long.parseLong(
                properties.getProperty("db.pool.idleTimeout", "600000")
        );
    }

    public static long getDbMaxLifetime() {
        checkLoaded();
        return Long.parseLong(
                properties.getProperty("db.pool.maxLifetime", "1800000")
        );
    }

    // =========================================
    // RUTAS
    // =========================================
    public static String getTicketsPath() {
        checkLoaded();
        return properties.getProperty("path.tickets", "./data/tickets");
    }

    public static String getReportsPath() {
        checkLoaded();
        return properties.getProperty("path.reports", "./data/reports");
    }

    public static String getLogsPath() {
        checkLoaded();
        return properties.getProperty("path.logs", "./logs");
    }

    // =========================================
    // IMPRESIÓN
    // =========================================
    public static boolean isPrinterEnabled() {
        checkLoaded();
        return Boolean.parseBoolean(
                properties.getProperty("printer.enabled", "true")
        );
    }

    public static String getDefaultPrinter() {
        checkLoaded();
        return properties.getProperty("printer.default", "");
    }

    public static boolean isKitchenPrinterEnabled() {
        checkLoaded();
        return Boolean.parseBoolean(
                properties.getProperty("printer.kitchen.enabled", "true")
        );
    }
}


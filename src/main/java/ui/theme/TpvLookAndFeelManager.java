package ui.theme;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Gestor central de Look & Feel del TPV.
 *
 * Valores admitidos:
 * - cafe
 * - flat_light
 * - flat_dark
 * - flat_intellij
 * - flat_darcula
 */
public final class TpvLookAndFeelManager {

    public static final String CAFE = "cafe";
    public static final String FLAT_LIGHT = "flat_light";
    public static final String FLAT_DARK = "flat_dark";
    public static final String FLAT_INTELLIJ = "flat_intellij";
    public static final String FLAT_DARCULA = "flat_darcula";

    private TpvLookAndFeelManager() {
    }

    public static void apply(String lookAndFeel) {
        String laf = normalize(lookAndFeel);

        try {
            configureCommonDefaults();

            switch (laf) {
                case FLAT_DARK:
                    FlatDarkLaf.setup();
                    configureDarkDefaults();
                    break;

                case FLAT_INTELLIJ:
                    FlatIntelliJLaf.setup();
                    configureLightDefaults();
                    break;

                case FLAT_DARCULA:
                    FlatDarculaLaf.setup();
                    configureDarkDefaults();
                    break;

                case FLAT_LIGHT:
                    FlatLightLaf.setup();
                    configureLightDefaults();
                    break;

                case CAFE:
                default:
                    FlatLightLaf.setup();
                    configureCafeDefaults();
                    break;
            }

            System.out.println("[LAF] Look & Feel cargado: " + laf);

        } catch (Exception ex) {
            System.err.println("[LAF] No se pudo aplicar Look & Feel: " + ex.getMessage());

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }

    public static String normalize(String lookAndFeel) {
        if (lookAndFeel == null || lookAndFeel.isBlank()) {
            return CAFE;
        }

        String value = lookAndFeel.trim().toLowerCase();

        if ("flat_light".equals(value) || "flatlight".equals(value) || "light".equals(value)) {
            return FLAT_LIGHT;
        }

        if ("flat_dark".equals(value) || "flatdark".equals(value) || "dark".equals(value)) {
            return FLAT_DARK;
        }

        if ("flat_intellij".equals(value) || "flatintellij".equals(value) || "intellij".equals(value)) {
            return FLAT_INTELLIJ;
        }

        if ("flat_darcula".equals(value) || "flatdarcula".equals(value) || "darcula".equals(value)) {
            return FLAT_DARCULA;
        }

        return CAFE;
    }

    public static String displayName(String lookAndFeel) {
        String laf = normalize(lookAndFeel);

        switch (laf) {
            case FLAT_LIGHT:
                return "FlatLaf claro";
            case FLAT_DARK:
                return "FlatLaf oscuro";
            case FLAT_INTELLIJ:
                return "FlatLaf IntelliJ";
            case FLAT_DARCULA:
                return "FlatLaf Darcula";
            case CAFE:
            default:
                return "Tema cafetería";
        }
    }

    private static void configureCommonDefaults() {
        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 16);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.thumbArc", 12);
        UIManager.put("ScrollBar.width", 12);

        UIManager.put("Button.margin", new Insets(9, 14, 9, 14));
        UIManager.put("TextField.margin", new Insets(8, 10, 8, 10));
        UIManager.put("PasswordField.margin", new Insets(8, 10, 8, 10));
        UIManager.put("ComboBox.padding", new Insets(7, 10, 7, 10));
    }

    private static void configureCafeDefaults() {
        UIManager.put("Button.background", InformeUiTheme.STARBUCKS_GREEN);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.hoverBackground", InformeUiTheme.STARBUCKS_GREEN_SOFT);
        UIManager.put("Button.pressedBackground", InformeUiTheme.STARBUCKS_GREEN);

        UIManager.put("Component.focusColor", InformeUiTheme.ACCENT_GOLD);
        UIManager.put("TextField.selectionBackground", InformeUiTheme.STARBUCKS_GREEN_SOFT);
        UIManager.put("PasswordField.selectionBackground", InformeUiTheme.STARBUCKS_GREEN_SOFT);
    }

    private static void configureLightDefaults() {
        UIManager.put("Component.focusColor", new Color(55, 112, 210));
    }

    private static void configureDarkDefaults() {
        UIManager.put("Component.focusColor", new Color(76, 175, 120));
    }
}
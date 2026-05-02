package util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AppIcon {

	private static final String ICON_PATH = "/logo/app_logo.png";
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 128, 256};

    private static List<Image> cachedImages;

    private AppIcon() {
    }

    public static List<Image> getIconImages() {
        if (cachedImages != null) {
            return cachedImages;
        }

        BufferedImage source = loadSourceImage();

        if (source == null) {
            cachedImages = Collections.emptyList();
            return cachedImages;
        }

        List<Image> images = new ArrayList<>();

        for (int size : ICON_SIZES) {
            images.add(resize(source, size, size));
        }

        cachedImages = Collections.unmodifiableList(images);
        return cachedImages;
    }

    public static Image getIconImage() {
        List<Image> images = getIconImages();

        if (images.isEmpty()) {
            return null;
        }

        return images.get(images.size() - 1);
    }

    public static ImageIcon getSwingIcon(int size) {
        BufferedImage source = loadSourceImage();

        if (source == null) {
            return null;
        }

        return new ImageIcon(resize(source, size, size));
    }

    public static void applyTo(Window window) {
        if (window == null) {
            return;
        }

        List<Image> images = getIconImages();

        if (!images.isEmpty()) {
            window.setIconImages(images);
        }
    }

    public static void applyToTaskbar() {
        try {
            if (!Taskbar.isTaskbarSupported()) {
                return;
            }

            Image image = getIconImage();

            if (image != null) {
                Taskbar.getTaskbar().setIconImage(image);
            }
        } catch (Exception ignored) {
            // No todos los sistemas permiten cambiar el icono de la barra de tareas.
        }
    }

    private static BufferedImage loadSourceImage() {
        try (InputStream in = AppIcon.class.getResourceAsStream(ICON_PATH)) {
            if (in == null) {
                System.err.println("[ICON] No se encontró el icono: " + ICON_PATH);
                return null;
            }

            return ImageIO.read(in);
        } catch (Exception e) {
            System.err.println("[ICON] Error cargando icono de aplicación: " + e.getMessage());
            return null;
        }
    }

    private static BufferedImage resize(BufferedImage source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = resized.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(source, 0, 0, width, height, null);
        } finally {
            g2.dispose();
        }

        return resized;
    }
}
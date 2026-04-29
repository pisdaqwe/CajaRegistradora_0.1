package ui.theme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Factoría de iconos vectoriales simples para Swing.
 *
 * Ventajas:
 * - No requiere PNG ni SVG externos.
 * - No añade dependencias.
 * - Escala bien.
 * - Permite usar colores del tema.
 *
 * Uso:
 * button.setIcon(TpvIconFactory.language(22, InformeUiTheme.PRIMARY));
 */
public final class TpvIconFactory {

    private static final int DEFAULT_SIZE = 22;
    private static final Color DEFAULT_COLOR = new Color(32, 92, 58);

    private TpvIconFactory() {
    }

    public static Icon language(int size, Color color) {
        return icon(IconType.LANGUAGE, size, color);
    }

    public static Icon palette(int size, Color color) {
        return icon(IconType.PALETTE, size, color);
    }

    public static Icon settings(int size, Color color) {
        return icon(IconType.SETTINGS, size, color);
    }

    public static Icon save(int size, Color color) {
        return icon(IconType.SAVE, size, color);
    }

    public static Icon back(int size, Color color) {
        return icon(IconType.BACK, size, color);
    }

    public static Icon check(int size, Color color) {
        return icon(IconType.CHECK, size, color);
    }

    public static Icon cancel(int size, Color color) {
        return icon(IconType.CANCEL, size, color);
    }

    public static Icon warning(int size, Color color) {
        return icon(IconType.WARNING, size, color);
    }

    public static Icon info(int size, Color color) {
        return icon(IconType.INFO, size, color);
    }

    public static Icon database(int size, Color color) {
        return icon(IconType.DATABASE, size, color);
    }

    public static Icon cashRegister(int size, Color color) {
        return icon(IconType.CASH_REGISTER, size, color);
    }

    public static Icon user(int size, Color color) {
        return icon(IconType.USER, size, color);
    }

    public static Icon report(int size, Color color) {
        return icon(IconType.REPORT, size, color);
    }

    public static Icon audit(int size, Color color) {
        return icon(IconType.AUDIT, size, color);
    }

    public static Icon logout(int size, Color color) {
        return icon(IconType.LOGOUT, size, color);
    }

    public static Icon icon(IconType type, int size, Color color) {
        return new VectorIcon(
                type != null ? type : IconType.INFO,
                size > 0 ? size : DEFAULT_SIZE,
                color != null ? color : DEFAULT_COLOR
        );
    }

    public enum IconType {
        LANGUAGE,
        PALETTE,
        SETTINGS,
        SAVE,
        BACK,
        CHECK,
        CANCEL,
        WARNING,
        INFO,
        DATABASE,
        CASH_REGISTER,
        USER,
        REPORT,
        AUDIT,
        LOGOUT
    }

    private static final class VectorIcon implements Icon {

        private final IconType type;
        private final int size;
        private final Color color;

        private VectorIcon(IconType type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();

            try {
                g2.translate(x, y);

                double scale = size / 24.0;
                g2.scale(scale, scale);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                g2.setColor(color);
                g2.setStroke(stroke(1.9f));

                paintByType(g2);

            } finally {
                g2.dispose();
            }
        }

        private void paintByType(Graphics2D g2) {
            switch (type) {
                case LANGUAGE:
                    paintLanguage(g2);
                    break;
                case PALETTE:
                    paintPalette(g2);
                    break;
                case SETTINGS:
                    paintSettings(g2);
                    break;
                case SAVE:
                    paintSave(g2);
                    break;
                case BACK:
                    paintBack(g2);
                    break;
                case CHECK:
                    paintCheck(g2);
                    break;
                case CANCEL:
                    paintCancel(g2);
                    break;
                case WARNING:
                    paintWarning(g2);
                    break;
                case INFO:
                    paintInfo(g2);
                    break;
                case DATABASE:
                    paintDatabase(g2);
                    break;
                case CASH_REGISTER:
                    paintCashRegister(g2);
                    break;
                case USER:
                    paintUser(g2);
                    break;
                case REPORT:
                    paintReport(g2);
                    break;
                case AUDIT:
                    paintAudit(g2);
                    break;
                case LOGOUT:
                    paintLogout(g2);
                    break;
                default:
                    paintInfo(g2);
                    break;
            }
        }

        private void paintLanguage(Graphics2D g2) {
            g2.draw(new Ellipse2D.Double(3.5, 3.5, 17, 17));
            g2.draw(new Arc2D.Double(7, 3.5, 10, 17, 90, 180, Arc2D.OPEN));
            g2.draw(new Arc2D.Double(7, 3.5, 10, 17, -90, 180, Arc2D.OPEN));
            g2.draw(new Line2D.Double(4.5, 9, 19.5, 9));
            g2.draw(new Line2D.Double(4.5, 15, 19.5, 15));
            g2.draw(new Line2D.Double(12, 3.7, 12, 20.3));
        }

        private void paintPalette(Graphics2D g2) {
            Path2D p = new Path2D.Double();
            p.moveTo(12, 3.2);
            p.curveTo(6.8, 3.2, 3.2, 6.8, 3.2, 12);
            p.curveTo(3.2, 17.2, 7.4, 20.8, 12.4, 20.8);
            p.curveTo(14.2, 20.8, 14.7, 19.2, 14.1, 18.1);
            p.curveTo(13.5, 17, 14.2, 15.7, 15.6, 15.7);
            p.lineTo(17.1, 15.7);
            p.curveTo(19.5, 15.7, 20.8, 14, 20.8, 11.7);
            p.curveTo(20.8, 6.8, 17.2, 3.2, 12, 3.2);
            p.closePath();
            g2.draw(p);

            fillCircle(g2, 8, 9, 1.1);
            fillCircle(g2, 11.5, 7, 1.1);
            fillCircle(g2, 15.2, 9, 1.1);
            fillCircle(g2, 8.8, 13.2, 1.1);
        }

        private void paintSettings(Graphics2D g2) {
            g2.draw(new Ellipse2D.Double(8, 8, 8, 8));

            for (int i = 0; i < 8; i++) {
                double angle = Math.toRadians(i * 45);
                double x1 = 12 + Math.cos(angle) * 7;
                double y1 = 12 + Math.sin(angle) * 7;
                double x2 = 12 + Math.cos(angle) * 9.5;
                double y2 = 12 + Math.sin(angle) * 9.5;
                g2.draw(new Line2D.Double(x1, y1, x2, y2));
            }

            g2.draw(new Ellipse2D.Double(10.1, 10.1, 3.8, 3.8));
        }

        private void paintSave(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Double(4, 3.5, 16, 17, 2.2, 2.2));
            g2.draw(new RoundRectangle2D.Double(7, 4.8, 9, 5, 1.2, 1.2));
            g2.draw(new RoundRectangle2D.Double(7, 13, 10, 6.2, 1.2, 1.2));
            g2.draw(new Line2D.Double(15.5, 5.2, 15.5, 9.2));
        }

        private void paintBack(Graphics2D g2) {
            g2.draw(new Line2D.Double(6, 12, 19, 12));
            g2.draw(new Line2D.Double(6, 12, 12, 6));
            g2.draw(new Line2D.Double(6, 12, 12, 18));
        }

        private void paintCheck(Graphics2D g2) {
            Path2D p = new Path2D.Double();
            p.moveTo(5, 12.5);
            p.lineTo(10, 17.2);
            p.lineTo(19.5, 6.8);
            g2.draw(p);
        }

        private void paintCancel(Graphics2D g2) {
            g2.draw(new Line2D.Double(6.5, 6.5, 17.5, 17.5));
            g2.draw(new Line2D.Double(17.5, 6.5, 6.5, 17.5));
        }

        private void paintWarning(Graphics2D g2) {
            Path2D p = new Path2D.Double();
            p.moveTo(12, 3.5);
            p.lineTo(21, 19.5);
            p.lineTo(3, 19.5);
            p.closePath();
            g2.draw(p);

            g2.draw(new Line2D.Double(12, 8.5, 12, 14));
            fillCircle(g2, 12, 17, 1.1);
        }

        private void paintInfo(Graphics2D g2) {
            g2.draw(new Ellipse2D.Double(4, 4, 16, 16));
            fillCircle(g2, 12, 8.2, 1);
            g2.draw(new Line2D.Double(12, 11, 12, 16.8));
        }

        private void paintDatabase(Graphics2D g2) {
            g2.draw(new Ellipse2D.Double(5, 4, 14, 5));
            g2.draw(new Line2D.Double(5, 6.5, 5, 17.5));
            g2.draw(new Line2D.Double(19, 6.5, 19, 17.5));
            g2.draw(new Arc2D.Double(5, 15, 14, 5, 180, 180, Arc2D.OPEN));
            g2.draw(new Arc2D.Double(5, 9.5, 14, 5, 180, 180, Arc2D.OPEN));
            g2.draw(new Arc2D.Double(5, 4, 14, 5, 180, 180, Arc2D.OPEN));
        }

        private void paintCashRegister(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Double(4, 10, 16, 8.5, 1.8, 1.8));
            g2.draw(new RoundRectangle2D.Double(7, 5, 8, 4.5, 1.3, 1.3));
            g2.draw(new Line2D.Double(8, 13.5, 16, 13.5));
            g2.draw(new Line2D.Double(7, 18.5, 17, 18.5));
            fillCircle(g2, 8, 15.8, 0.8);
            fillCircle(g2, 11, 15.8, 0.8);
            fillCircle(g2, 14, 15.8, 0.8);
        }

        private void paintUser(Graphics2D g2) {
            g2.draw(new Ellipse2D.Double(8.2, 4.2, 7.6, 7.6));
            Path2D p = new Path2D.Double();
            p.moveTo(4.8, 20);
            p.curveTo(5.8, 15.5, 8.6, 13.4, 12, 13.4);
            p.curveTo(15.4, 13.4, 18.2, 15.5, 19.2, 20);
            g2.draw(p);
        }

        private void paintReport(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Double(5, 3.5, 14, 17, 1.8, 1.8));
            g2.draw(new Line2D.Double(8, 8, 16, 8));
            g2.draw(new Line2D.Double(8, 11, 16, 11));

            g2.draw(new Line2D.Double(8, 17, 8, 14));
            g2.draw(new Line2D.Double(12, 17, 12, 12.8));
            g2.draw(new Line2D.Double(16, 17, 16, 15));
        }

        private void paintAudit(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Double(6, 4, 10, 15.5, 1.7, 1.7));
            g2.draw(new RoundRectangle2D.Double(8.4, 3, 5.2, 3.2, 1, 1));

            g2.draw(new Line2D.Double(8.5, 9, 13.5, 9));
            g2.draw(new Line2D.Double(8.5, 12, 12.5, 12));

            g2.draw(new Ellipse2D.Double(13.5, 13, 5, 5));
            g2.draw(new Line2D.Double(17.2, 16.8, 20.2, 19.8));
        }

        private void paintLogout(Graphics2D g2) {
            g2.draw(new RoundRectangle2D.Double(4.5, 5, 9, 14, 1.8, 1.8));
            g2.draw(new Line2D.Double(11, 12, 20, 12));
            g2.draw(new Line2D.Double(20, 12, 16.5, 8.5));
            g2.draw(new Line2D.Double(20, 12, 16.5, 15.5));
        }

        private Stroke stroke(float width) {
            return new BasicStroke(
                    width,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            );
        }

        private void fillCircle(Graphics2D g2, double centerX, double centerY, double radius) {
            Shape shape = new Ellipse2D.Double(
                    centerX - radius,
                    centerY - radius,
                    radius * 2,
                    radius * 2
            );
            g2.fill(shape);
        }
    }
}
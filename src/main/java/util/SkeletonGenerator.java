package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkeletonGenerator {

    private static final String BASE_DIR = "src/main/java";

    public static void main(String[] args) throws IOException {
        System.out.println("=== Generador de clases vacías para el TPV ===");

        // 1) MODELOS
        createModelClasses();

        // 2) DAOs
        createDaoClasses();

        // 3) SERVICES
        createServiceClasses();

        // 4) UI SCREENS
        createScreenClasses();

        // 5) UI COMPONENTS
        createComponentClasses();

        System.out.println("=== Fin del generador. Revisa el proyecto y refresca (F5) en Eclipse. ===");
    }

    /* -------------------------------------------------------------------------
     * HELPERS GENERALES
     * ---------------------------------------------------------------------- */

    private static void createClass(String packageName, String className, String body) throws IOException {
        Path dir = Paths.get(BASE_DIR, packageName.replace('.', '/'));
        Files.createDirectories(dir);

        Path file = dir.resolve(className + ".java");
        if (Files.exists(file)) {
            System.out.println("Omitiendo (ya existe): " + file);
            return;
        }

        String content = "package " + packageName + ";\n\n" + body;
        Files.writeString(file, content);
        System.out.println("Creado: " + file);
    }

    /* -------------------------------------------------------------------------
     * 1) MODELOS
     * ---------------------------------------------------------------------- */

    private static void createModelClasses() throws IOException {
        String pkg = "model";
        String[] classes = {
                "Canje",
                "Devolucion",
                "DevolucionItem",
                "Vale",
                "UnidadIngrediente",
                "ConversionIngrediente",
                "PresentacionIngrediente",
                "Recompensa"
        };

        for (String cls : classes) {
            String body =
                    "public class " + cls + " {\n" +
                    "    // TODO: implementar campos y métodos según el esquema de base de datos.\n" +
                    "}\n";
            createClass(pkg, cls, body);
        }
    }

    /* -------------------------------------------------------------------------
     * 2) DAOs
     * ---------------------------------------------------------------------- */

    private static void createDaoClasses() throws IOException {
        String pkg = "dao";
        String[] classes = {
                "CanjeDao",
                "DevolucionDao",
                "DevolucionItemDao",
                "ValeDao",
                "UnidadIngredienteDao",
                "ConversionIngredienteDao",
                "PresentacionIngredienteDao",
                "RecompensaDao"
        };

        for (String cls : classes) {
            String body =
                    "public class " + cls + " {\n" +
                    "    // TODO: implementar operaciones CRUD para esta entidad.\n" +
                    "}\n";
            createClass(pkg, cls, body);
        }
    }

    /* -------------------------------------------------------------------------
     * 3) SERVICES
     * ---------------------------------------------------------------------- */

    private static void createServiceClasses() throws IOException {
        String pkg = "service";
        String[] classes = {
                "ClienteService",
                "IngredienteService",
                "UnidadIngredienteService",
                "ConversionIngredienteService",
                "PresentacionIngredienteService",
                "ComboService",
                "SucursalService",
                "SubcategoriaService",
                "TamanoService",
                "CanjeService",
                "DevolucionService",
                "MovimientoStockService",
                "RecompensaService",
                "ValeService"
        };

        for (String cls : classes) {
            String body =
                    "public class " + cls + " {\n" +
                    "    // TODO: lógica de negocio para " + cls.replace("Service", "") + ".\n" +
                    "}\n";
            createClass(pkg, cls, body);
        }
    }

    /* -------------------------------------------------------------------------
     * 4) UI SCREENS
     * ---------------------------------------------------------------------- */

    private static void createScreenClasses() throws IOException {
        String pkg = "ui.screens";
        String[] classes = {
                "TamanosScreen",
                "ExtrasScreen",
                "PersonalizacionesScreen",
                "EstacionesScreen",
                "RecetasScreen",
                "DevolucionesScreen",
                "CanjesScreen",
                "FidelizacionScreen",
                "MovimientoStockScreen",
                "UnidadesScreen"
        };

        for (String cls : classes) {
            String body =
                    "import javax.swing.JPanel;\n\n" +
                    "public class " + cls + " extends JPanel {\n\n" +
                    "    public " + cls + "() {\n" +
                    "        // TODO: construir la interfaz gráfica de esta pantalla.\n" +
                    "    }\n" +
                    "}\n";
            createClass(pkg, cls, body);
        }
    }

    /* -------------------------------------------------------------------------
     * 5) UI COMPONENTS
     * ---------------------------------------------------------------------- */

    private static void createComponentClasses() throws IOException {
        String pkg = "ui.components";
        String[] classes = {
                "TamanoSelectorPanel",
                "IngredienteSelectorPanel",
                "ComboSelectorPanel",
                "DevolucionItemPanel",
                "ClienteSelectorPanel",
                "UnidadSelectorPanel"
        };

        for (String cls : classes) {
            String body =
                    "import javax.swing.JPanel;\n\n" +
                    "public class " + cls + " extends JPanel {\n\n" +
                    "    public " + cls + "() {\n" +
                    "        // TODO: construir el componente gráfico.\n" +
                    "    }\n" +
                    "}\n";
            createClass(pkg, cls, body);
        }
    }
}

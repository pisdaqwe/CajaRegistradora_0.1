package service;

import dtoS.InformePdfDocumentData;
import dtoS.InformePdfExportRequest;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Servicio principal de exportación PDF.
 *
 * Responsabilidades:
 * - Validar request y destino
 * - Construir el documento lógico
 * - Delegar el render al renderer PDF
 * - Guardar el archivo final en disco
 *
 * IMPORTANTE:
 * - No consulta BD
 * - No depende de Swing
 * - Recibe ya el request preparado desde la UI
 */
public class InformePdfService {

    private final InformePdfDocumentBuilder documentBuilder;
    private final InformePdfRenderer renderer;

    public InformePdfService() {
        this.documentBuilder = new InformePdfDocumentBuilder();
        this.renderer = new InformePdfRenderer();
    }

    public InformePdfService(InformePdfDocumentBuilder documentBuilder,
                             InformePdfRenderer renderer) {
        this.documentBuilder = documentBuilder != null ? documentBuilder : new InformePdfDocumentBuilder();
        this.renderer = renderer != null ? renderer : new InformePdfRenderer();
    }

    /**
     * Exporta un informe a PDF en el archivo indicado.
     *
     * @param request request de exportación
     * @param outputFile archivo destino
     * @return archivo PDF generado
     */
    public File exportarInforme(InformePdfExportRequest request, File outputFile) throws IOException {
        Objects.requireNonNull(request, "request no puede ser null");
        Objects.requireNonNull(outputFile, "outputFile no puede ser null");

        request.validate();

        File normalizedFile = ensurePdfExtension(outputFile);
        ensureParentDirectoryExists(normalizedFile);

        InformePdfDocumentData documentData = documentBuilder.build(request);

        try (PDDocument document = new PDDocument()) {
            renderer.render(document, documentData);
            document.save(normalizedFile);
        }

        return normalizedFile;
    }

    /**
     * Garantiza extensión .pdf
     */
    private File ensurePdfExtension(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            return file;
        }

        return new File(file.getParentFile(), file.getName() + ".pdf");
    }

    /**
     * Crea la carpeta padre si no existe.
     */
    private void ensureParentDirectoryExists(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent == null) {
            return;
        }

        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta destino: " + parent.getAbsolutePath());
        }
    }
}
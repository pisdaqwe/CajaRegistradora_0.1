package service;

import app.AppContext;
import dtoS.InformePdfDocumentData;
import dtoS.InformePdfExportRequest;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Servicio principal de exportación PDF.
 *
 * Auditoría:
 * - registra la exportación correcta del PDF
 */
public class InformePdfService {

    private final InformePdfDocumentBuilder documentBuilder;
    private final InformePdfRenderer renderer;
    private final AuditoriaService auditoriaService;

    public InformePdfService(AuditoriaService auditoriaService) {
        this.documentBuilder = new InformePdfDocumentBuilder();
        this.renderer = new InformePdfRenderer();
        this.auditoriaService = auditoriaService;
    }

    public InformePdfService(InformePdfDocumentBuilder documentBuilder,
                             InformePdfRenderer renderer,
                             AuditoriaService auditoriaService) {
        this.documentBuilder = documentBuilder != null ? documentBuilder : new InformePdfDocumentBuilder();
        this.renderer = renderer != null ? renderer : new InformePdfRenderer();
        this.auditoriaService = auditoriaService;
    }

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

        auditarSeguro(request, normalizedFile);

        return normalizedFile;
    }

    private File ensurePdfExtension(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            return file;
        }

        return new File(file.getParentFile(), file.getName() + ".pdf");
    }

    private void ensureParentDirectoryExists(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent == null) {
            return;
        }

        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta destino: " + parent.getAbsolutePath());
        }
    }

    private void auditarSeguro(InformePdfExportRequest request, File normalizedFile) {
        if (auditoriaService == null) {
            return;
        }

        if (!AppContext.isAuthenticated() || AppContext.getIdSucursal() <= 0) {
            return;
        }

        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("tipoInforme", request.getTipoInforme() != null ? request.getTipoInforme().name() : null);
            data.put("modoVista", request.getModoVista() != null ? request.getModoVista().name() : null);
            data.put("usuarioGenerador", request.getUsuarioGenerador());
            data.put("fechaGeneracion", request.getFechaGeneracion() != null ? request.getFechaGeneracion().toString() : null);
            data.put("resumenFiltros", request.getResumenFiltros());
            data.put("rutaArchivo", normalizedFile.getAbsolutePath());
            data.put("nombreArchivo", normalizedFile.getName());

            auditoriaService.registrarEvento(
                    AppContext.getUsuarioId(),
                    AppContext.getIdSucursal(),
                    "INFORME_EXPORTADO_PDF",
                    data
            );
        } catch (Exception ex) {
            System.err.println("[AUDITORIA] No se pudo registrar evento INFORME_EXPORTADO_PDF: " + ex.getMessage());
        }
    }
}
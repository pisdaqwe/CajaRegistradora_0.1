package dtoS;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InformePdfDocumentData {

    private String titulo;
    private String subtitulo;
    private String resumenFiltros;
    private String usuarioGenerador;
    private String fechaGeneracionTexto;
    private List<InformePdfKpiItem> kpis = new ArrayList<>();
    private InformePdfTableData tableData;

    /**
     * Imagen del gráfico asociada al informe.
     * Puede ser null si ese informe no soporta gráfico
     * o si no se ha generado.
     */
    private BufferedImage chartImage;

    public InformePdfDocumentData() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getResumenFiltros() {
        return resumenFiltros;
    }

    public void setResumenFiltros(String resumenFiltros) {
        this.resumenFiltros = resumenFiltros;
    }

    public String getUsuarioGenerador() {
        return usuarioGenerador;
    }

    public void setUsuarioGenerador(String usuarioGenerador) {
        this.usuarioGenerador = usuarioGenerador;
    }

    public String getFechaGeneracionTexto() {
        return fechaGeneracionTexto;
    }

    public void setFechaGeneracionTexto(String fechaGeneracionTexto) {
        this.fechaGeneracionTexto = fechaGeneracionTexto;
    }

    public List<InformePdfKpiItem> getKpis() {
        return kpis;
    }

    public void setKpis(List<InformePdfKpiItem> kpis) {
        this.kpis = kpis;
    }

    public InformePdfTableData getTableData() {
        return tableData;
    }

    public void setTableData(InformePdfTableData tableData) {
        this.tableData = tableData;
    }

    public BufferedImage getChartImage() {
        return chartImage;
    }

    public void setChartImage(BufferedImage chartImage) {
        this.chartImage = chartImage;
    }
}
package dtoS;

import enums.ModoVistaInforme;
import enums.TipoInforme;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO base para solicitar la exportación de un informe a PDF.
 *
 * Responsabilidades:
 * - Transportar toda la información necesaria desde la UI
 *   hacia la capa de exportación PDF.
 * - Desacoplar InformesFrame del renderer PDF.
 *
 * Qué contiene:
 * - tipo de informe
 * - modo vista usado al generarlo
 * - resumen legible de filtros
 * - usuario que genera el PDF
 * - fecha y hora de generación
 * - resultado real del informe ya calculado
 *
 * IMPORTANTE:
 * - El campo result NO debe salir de la BD otra vez.
 * - Debe contener el DTO ya generado en memoria por InformesFrame.
 *
 * Ejemplos de result:
 * - InformeVentasPorDiaResultDTO
 * - InformeResumenEjecutivoResultDTO
 * - InformePagosMetodoResultDTO
 */
public class InformePdfExportRequest {

	private InformeFiltroDTO filtroDTO;
	private TipoInforme tipoInforme;
    private ModoVistaInforme modoVista;
    private String resumenFiltros;
    private String usuarioGenerador;
    private LocalDateTime fechaGeneracion;
    private Object result;

    public InformePdfExportRequest() {
    }

    public InformePdfExportRequest(TipoInforme tipoInforme,
                                   ModoVistaInforme modoVista,
                                   String resumenFiltros,
                                   String usuarioGenerador,
                                   LocalDateTime fechaGeneracion,
                                   Object result,
                                   InformeFiltroDTO filtroDTO) {
        this.tipoInforme = tipoInforme;
        this.modoVista = modoVista;
        this.resumenFiltros = resumenFiltros;
        this.usuarioGenerador = usuarioGenerador;
        this.fechaGeneracion = fechaGeneracion;
        this.result = result;
        this.filtroDTO = filtroDTO;
    }
    
    public InformeFiltroDTO getFiltroDTO() {
		return filtroDTO;
	}

	public void setFiltroDTO(InformeFiltroDTO filtroDTO) {
		this.filtroDTO = filtroDTO;
	}

    public TipoInforme getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(TipoInforme tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public ModoVistaInforme getModoVista() {
        return modoVista;
    }

    public void setModoVista(ModoVistaInforme modoVista) {
        this.modoVista = modoVista;
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

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Validación mínima defensiva para evitar lanzar exportaciones rotas.
     */
    public void validate() {
        Objects.requireNonNull(tipoInforme, "tipoInforme no puede ser null");
        Objects.requireNonNull(fechaGeneracion, "fechaGeneracion no puede ser null");
        Objects.requireNonNull(result, "result no puede ser null");
    }
}
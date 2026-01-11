package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import enums.EstadoSesionCaja;

public class SesionCaja {
	private int idSesion;
	
	/**
     * Caja en la que se abre la sesión.
     */
	private int idCaja;
	
	/**
     * Usuario que abre la sesión.
     */
	private int idUsuarioApertura;
	
	 /**
     * Usuario que cierra la sesión.
     * Puede ser null mientras esté abierta.
     */
	private Integer idUsuarioCierre;
	private LocalDateTime fechaApertura;
	private LocalDateTime fechaCierre;
	
	private BigDecimal importeInicial;
	private BigDecimal importeFinal;
	
	private BigDecimal totalVentas;
	
	private EstadoSesionCaja estado;
	private String observaciones;
	
	
	public int getIdSesion() {
		return idSesion;
	}
	public void setIdSesion(int idSesion) {
		this.idSesion = idSesion;
	}
	
	
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	
	
	public int getIdUsuarioApertura() {
		return idUsuarioApertura;
	}
	public void setIdUsuarioApertura(int idUsuariosApertura) {
		this.idUsuarioApertura = idUsuariosApertura;
	}
	
	
	public Integer getIdUsuarioCierre() {
		return idUsuarioCierre;
	}
	public void setIdUsuarioCierre(Integer idUsuarioCierre) {
		this.idUsuarioCierre = idUsuarioCierre;
	}
	
	
	public LocalDateTime getFechaApertura() {
		return fechaApertura;
	}
	public void setFechaApertura(LocalDateTime fechaApertura) {
		this.fechaApertura = fechaApertura;
	}
	
	
	public LocalDateTime getFechaCierre() {
		return fechaCierre;
	}
	public void setFechaCierre(LocalDateTime fechaCierre) {
		this.fechaCierre = fechaCierre;
	}
	
	
	public BigDecimal getImporteInicial() {
		return importeInicial;
	}
	public void setImporteInicial(BigDecimal importeInicial) {
		this.importeInicial = importeInicial;
	}
	
	
	public BigDecimal getImporteFinal() {
		return importeFinal;
	}
	public void setImporteFinal(BigDecimal importedFinal) {
		this.importeFinal = importedFinal;
	}
	
	
	public BigDecimal getTotalVentas() {
		return totalVentas;
	}
	public void setTotalVentas(BigDecimal totalVentas) {
		this.totalVentas = totalVentas;
	}
	
	
	public EstadoSesionCaja getEstado() {
		return estado;
	}
	public void setEstado(EstadoSesionCaja estado) {
		this.estado = estado;
	}
	
	
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	
	

}

package model;

import java.time.LocalDateTime;

import enums.EstadoCaja;

public class Caja {
	private int idCaja;
	private String nombre;
	private String ubicacion;
	private EstadoCaja estado;
	private boolean activa;
	private int idSucursal;
	private LocalDateTime fechaCreacion;
	private LocalDateTime ultimaApertura;
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public EstadoCaja getEstado() {
		return estado;
	}
	public void setEstado(EstadoCaja estado) {
		this.estado = estado;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public int getIdSucursal() {
		return idSucursal;
	}
	public void setIdSucursal(int idSucursal) {
		this.idSucursal = idSucursal;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime dechaCreacion) {
		this.fechaCreacion = dechaCreacion;
	}
	public LocalDateTime getUltimaApertura() {
		return ultimaApertura;
	}
	public void setUltimaApertura(LocalDateTime ultimaApertura) {
		this.ultimaApertura = ultimaApertura;
	}
	
	
	
	
	
	
	

}

package model;

import java.time.LocalDateTime;

public class UsuarioRecordado {
	private int idBoton;
	private int idUsuario;
	private int idTerminal;
	private String nombreBoton;
	private int posicion;
	private LocalDateTime ultimoAceso;
	public int getIdBoton() {
		return idBoton;
	}
	public void setIdBoton(int idBoton) {
		this.idBoton = idBoton;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getIdTerminal() {
		return idTerminal;
	}
	public void setIdTerminal(int idTerminal) {
		this.idTerminal = idTerminal;
	}
	public String getNombreBoton() {
		return nombreBoton;
	}
	public void setNombreBoton(String nombreBoton) {
		this.nombreBoton = nombreBoton;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public LocalDateTime getUltimoAceso() {
		return ultimoAceso;
	}
	public void setUltimoAcceso(LocalDateTime ultimoAceso) {
		this.ultimoAceso = ultimoAceso;
	}
	
	

}

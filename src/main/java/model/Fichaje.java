package model;

import java.time.LocalDateTime;

import enums.EstadoFichaje;

public class Fichaje {
	
	
    private int idFichaje;
    private int idUsuario;
    private int idSucursal;
    public int getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(int idSucursal) {
		this.idSucursal = idSucursal;
	}

	private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private Integer duracion; // en minutos (nullable hasta cerrar)
    private EstadoFichaje estado;
    private String Observaciones;

    // Constructor vacío (obligatorio)
    public Fichaje() {
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public int getIdFichaje() {
        return idFichaje;
    }

    public String getObservaciones() {
		return Observaciones;
	}

	public void setObservaciones(String observaciones) {
		Observaciones = observaciones;
	}

	public void setIdFichaje(int idFichaje) {
        this.idFichaje = idFichaje;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public EstadoFichaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoFichaje estado) {
        this.estado = estado;
    }
}
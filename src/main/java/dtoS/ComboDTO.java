package dtoS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Objects;

import enums.ComboTipo;



public class ComboDTO {

    public enum DiaSemanaCombo {
        LUN, MAR, MIE, JUE, VIE, SAB, DOM
    }

    private int idCombo;
    private String nombre;
    private String descripcion;
    private ComboTipo tipo;
    private BigDecimal valor;
    private int prioridad;
    private boolean activo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EnumSet<DiaSemanaCombo> diasSemana;

    public ComboDTO() {
        this.valor = BigDecimal.ZERO;
        this.diasSemana = EnumSet.noneOf(DiaSemanaCombo.class);
    }

    public int getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = safeTrim(nombre);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = safeTrimNullable(descripcion);
    }

    public ComboTipo getTipo() {
        return tipo;
    }

    public void setTipo(ComboTipo tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor == null ? BigDecimal.ZERO : valor;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public EnumSet<DiaSemanaCombo> getDiasSemana() {
        return diasSemana == null
                ? EnumSet.noneOf(DiaSemanaCombo.class)
                : EnumSet.copyOf(diasSemana);
    }

    public void setDiasSemana(EnumSet<DiaSemanaCombo> diasSemana) {
        this.diasSemana = diasSemana == null
                ? EnumSet.noneOf(DiaSemanaCombo.class)
                : EnumSet.copyOf(diasSemana);
    }

    public boolean tieneRestriccionFecha() {
        return fechaInicio != null || fechaFin != null;
    }

    public boolean tieneRestriccionHora() {
        return horaInicio != null || horaFin != null;
    }

    public boolean tieneRestriccionDias() {
        return diasSemana != null && !diasSemana.isEmpty();
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeTrimNullable(String value) {
        return value == null ? null : value.trim();
    }

    @Override
    public String toString() {
        return "ComboDTO{" +
                "idCombo=" + idCombo +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", valor=" + valor +
                ", prioridad=" + prioridad +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ComboDTO other)) return false;
        return idCombo == other.idCombo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCombo);
    }
}
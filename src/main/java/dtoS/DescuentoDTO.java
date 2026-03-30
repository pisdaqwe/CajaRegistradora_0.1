package dtoS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que representa un descuento leído desde base de datos.
 *
 * Esta clase transporta la configuración de un descuento disponible
 * en la tabla descuento.
 *
 * Ejemplos:
 * - PROMO10 -> 10% de descuento
 * - EMPLEADO20 -> 20% de descuento para empleados
 * - VALE2 -> 2 euros de descuento fijo
 *
 * IMPORTANTE:
 * Esta clase NO contiene lógica de negocio.
 * Solo transporta datos entre DAO y Service.
 */
public class DescuentoDTO {

    /**
     * ID real del descuento en base de datos.
     */
    private int idDescuento;

    /**
     * Nombre visible del descuento.
     * Ejemplo: "Promo 10%", "Descuento Empleado 20%".
     */
    private String nombre;

    /**
     * Descripción opcional del descuento.
     */
    private String descripcion;

    /**
     * Tipo de descuento.
     *
     * Valores esperados:
     * - PORCENTAJE
     * - IMPORTE_FIJO
     */
    private String tipo;

    /**
     * Valor bruto del descuento.
     *
     * Ejemplos:
     * - Si tipo = PORCENTAJE y valor = 10.00 -> 10%
     * - Si tipo = IMPORTE_FIJO y valor = 2.00 -> 2€
     */
    private BigDecimal valor;

    /**
     * Código promocional asociado al descuento.
     *
     * Puede ser null en descuentos internos o descuentos de empleado.
     */
    private String codigo;

    /**
     * Origen funcional del descuento.
     *
     * Valores esperados:
     * - PROMOCIONAL
     * - EMPLEADO
     * - MANUAL
     */
    private String origen;

    /**
     * Indica si este descuento exige introducir un código.
     */
    private boolean requiereCodigo;

    /**
     * Indica si este descuento exige validar un empleado beneficiario.
     */
    private boolean requiereEmpleado;

    /**
     * Indica si el descuento puede acumularse con otros.
     *
     * En el MVP normalmente será false.
     */
    private boolean acumulable;

    /**
     * Indica si este descuento puede aplicarse a tickets con combos.
     *
     * En el MVP normalmente será false.
     */
    private boolean aplicaACombos;

    /**
     * Indica si el descuento está activo.
     */
    private boolean activo;

    /**
     * Fecha de inicio de vigencia.
     * Puede ser null si no tiene inicio definido.
     */
    private LocalDateTime fechaInicio;

    /**
     * Fecha de fin de vigencia.
     * Puede ser null si no tiene fin definido.
     */
    private LocalDateTime fechaFin;

    /**
     * Máximo número de usos permitidos.
     * Puede ser null si no hay límite.
     */
    private Integer usoMaximo;

    /**
     * Número de usos actuales registrados.
     */
    private int usoActual;

    /**
     * Fecha de creación del descuento.
     */
    private LocalDateTime fechaCreacion;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public int getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(int idDescuento) {
        this.idDescuento = idDescuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public boolean isRequiereCodigo() {
        return requiereCodigo;
    }

    public void setRequiereCodigo(boolean requiereCodigo) {
        this.requiereCodigo = requiereCodigo;
    }

    public boolean isRequiereEmpleado() {
        return requiereEmpleado;
    }

    public void setRequiereEmpleado(boolean requiereEmpleado) {
        this.requiereEmpleado = requiereEmpleado;
    }

    public boolean isAcumulable() {
        return acumulable;
    }

    public void setAcumulable(boolean acumulable) {
        this.acumulable = acumulable;
    }

    public boolean isAplicaACombos() {
        return aplicaACombos;
    }

    public void setAplicaACombos(boolean aplicaACombos) {
        this.aplicaACombos = aplicaACombos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getUsoMaximo() {
        return usoMaximo;
    }

    public void setUsoMaximo(Integer usoMaximo) {
        this.usoMaximo = usoMaximo;
    }

    public int getUsoActual() {
        return usoActual;
    }

    public void setUsoActual(int usoActual) {
        this.usoActual = usoActual;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

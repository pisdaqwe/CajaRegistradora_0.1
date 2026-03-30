package model;

import java.math.BigDecimal;

/**
 * Modelo en memoria que representa el descuento actualmente
 * aplicado al ticket abierto en la caja.
 *
 * Esta clase vive dentro de TicketSession mientras el pedido
 * todavía no se ha cobrado.
 *
 * IMPORTANTE:
 * - NO es una entidad JPA
 * - NO representa directamente una fila completa de BD
 * - representa el estado calculado del descuento en el ticket actual
 */
public class DescuentoAplicado {

    /**
     * ID del descuento maestro en base de datos.
     */
    private int idDescuento;

    /**
     * Nombre visible del descuento aplicado.
     */
    private String nombre;

    /**
     * Tipo del descuento aplicado.
     *
     * Valores esperados:
     * - PORCENTAJE
     * - IMPORTE_FIJO
     */
    private String tipo;

    /**
     * Valor bruto configurado.
     *
     * Ejemplo:
     * - 10.00 para un 10%
     * - 2.00 para un descuento fijo de 2€
     */
    private BigDecimal valor;

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
     * Código introducido por el usuario al aplicar el descuento.
     */
    private String codigoIntroducido;

    /**
     * ID del empleado beneficiario, si existe.
     */
    private Integer idEmpleadoBeneficiario;

    /**
     * Nombre visible del empleado beneficiario, si aplica.
     */
    private String nombreEmpleadoBeneficiario;

    /**
     * Importe base sobre el que se calculó el descuento.
     */
    private BigDecimal importeBase;

    /**
     * Importe real descontado.
     */
    private BigDecimal importeDescuento;

    /**
     * Total final del ticket una vez aplicado el descuento.
     */
    private BigDecimal totalFinal;

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

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getCodigoIntroducido() {
        return codigoIntroducido;
    }

    public void setCodigoIntroducido(String codigoIntroducido) {
        this.codigoIntroducido = codigoIntroducido;
    }

    public Integer getIdEmpleadoBeneficiario() {
        return idEmpleadoBeneficiario;
    }

    public void setIdEmpleadoBeneficiario(Integer idEmpleadoBeneficiario) {
        this.idEmpleadoBeneficiario = idEmpleadoBeneficiario;
    }

    public String getNombreEmpleadoBeneficiario() {
        return nombreEmpleadoBeneficiario;
    }

    public void setNombreEmpleadoBeneficiario(String nombreEmpleadoBeneficiario) {
        this.nombreEmpleadoBeneficiario = nombreEmpleadoBeneficiario;
    }

    public BigDecimal getImporteBase() {
        return importeBase;
    }

    public void setImporteBase(BigDecimal importeBase) {
        this.importeBase = importeBase;
    }

    public BigDecimal getImporteDescuento() {
        return importeDescuento;
    }

    public void setImporteDescuento(BigDecimal importeDescuento) {
        this.importeDescuento = importeDescuento;
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }
}

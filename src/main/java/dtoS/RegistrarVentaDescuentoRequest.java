package dtoS;

import java.math.BigDecimal;

/**
 * DTO que representa el descuento que debe persistirse
 * al registrar una venta.
 *
 * Este objeto viajará dentro de RegistrarVentaRequest y luego
 * será usado por VentaRegistroDao para insertar en venta_descuento.
 */
public class RegistrarVentaDescuentoRequest {

    /**
     * ID real del descuento maestro en BD.
     */
    private int idDescuento;

    /**
     * ID del usuario que aplica el descuento en caja.
     */
    private int idUsuarioAplica;

    /**
     * ID del empleado beneficiario, si aplica.
     *
     * Será null en descuentos promocionales normales.
     */
    private Integer idEmpleadoBeneficiario;

    /**
     * Código introducido por el usuario al aplicar el descuento.
     *
     * Puede ser null en descuentos manuales.
     */
    private String codigoIntroducido;

    /**
     * Tipo de descuento aplicado en el momento de la venta.
     *
     * Valores esperados:
     * - PORCENTAJE
     * - IMPORTE_FIJO
     */
    private String tipoDescuentoAplicado;

    /**
     * Valor bruto aplicado.
     *
     * Ejemplos:
     * - 10.00 si fue 10%
     * - 2.00 si fueron 2€
     */
    private BigDecimal valorDescuentoAplicado;

    /**
     * Importe base sobre el que se calculó el descuento.
     */
    private BigDecimal importeBase;

    /**
     * Importe real descontado en euros.
     */
    private BigDecimal importeDescuento;
    

    private String nombreDescuento;
    private String origenDescuento;

    /**
     * Observaciones opcionales.
     */
    private String observaciones;

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

    public int getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(int idDescuento) {
        this.idDescuento = idDescuento;
    }

    public int getIdUsuarioAplica() {
        return idUsuarioAplica;
    }

    public void setIdUsuarioAplica(int idUsuarioAplica) {
        this.idUsuarioAplica = idUsuarioAplica;
    }

    public Integer getIdEmpleadoBeneficiario() {
        return idEmpleadoBeneficiario;
    }

    public void setIdEmpleadoBeneficiario(Integer idEmpleadoBeneficiario) {
        this.idEmpleadoBeneficiario = idEmpleadoBeneficiario;
    }

    public String getCodigoIntroducido() {
        return codigoIntroducido;
    }

    public void setCodigoIntroducido(String codigoIntroducido) {
        this.codigoIntroducido = codigoIntroducido;
    }

    public String getTipoDescuentoAplicado() {
        return tipoDescuentoAplicado;
    }

    public void setTipoDescuentoAplicado(String tipoDescuentoAplicado) {
        this.tipoDescuentoAplicado = tipoDescuentoAplicado;
    }

    public BigDecimal getValorDescuentoAplicado() {
        return valorDescuentoAplicado;
    }

    public void setValorDescuentoAplicado(BigDecimal valorDescuentoAplicado) {
        this.valorDescuentoAplicado = valorDescuentoAplicado;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public String getNombreDescuento() {
        return nombreDescuento;
    }

    public void setNombreDescuento(String nombreDescuento) {
        this.nombreDescuento = nombreDescuento;
    }

    public String getOrigenDescuento() {
        return origenDescuento;
    }

    public void setOrigenDescuento(String origenDescuento) {
        this.origenDescuento = origenDescuento;
    }
}
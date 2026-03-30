package service;

import dao.DescuentoDao;
import dtoS.AplicarDescuentoRequest;
import dtoS.DescuentoAplicadoDTO;
import dtoS.DescuentoDTO;
import dtoS.RegistrarVentaDescuentoRequest;
import exceptions.DescuentoValidationException;
import model.DescuentoAplicado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Servicio de negocio del módulo descuentos.
 *
 * Responsabilidades:
 * - validar si un ticket admite descuento
 * - buscar descuentos configurados en BD
 * - validar vigencia, activo y límites de uso
 * - calcular el importe descontado
 * - construir el objeto DescuentoAplicado
 * - convertirlo luego a RegistrarVentaDescuentoRequest
 *
 * IMPORTANTE:
 * Este servicio NO persiste la venta.
 * Solo valida y prepara el descuento.
 */
public class DescuentoService {

    private final DescuentoDao descuentoDao;

    public DescuentoService(DescuentoDao descuentoDao) {
        this.descuentoDao = Objects.requireNonNull(descuentoDao, "descuentoDao no puede ser null");
    }

    /**
     * Flujo de descuento promocional por código.
     */
    public DescuentoAplicadoDTO aplicarCodigoPromocional(AplicarDescuentoRequest request) {
        try {
            validarRequestBase(request);
            validarTicketParaDescuento(request);

            String codigo = normalize(request.getCodigoPromocional());
            if (codigo.isEmpty()) {
                throw new DescuentoValidationException("Debes introducir un código promocional.");
            }

            DescuentoDTO descuento = descuentoDao.findByCodigo(codigo)
                    .orElseThrow(() -> new DescuentoValidationException("El código promocional no existe."));

            validarDescuentoAplicable(descuento, request);

            DescuentoAplicado aplicado = construirDescuentoAplicado(
                    descuento,
                    codigo,
                    null,
                    null,
                    request.getSubtotalTicket()
            );

            return buildOk("Descuento aplicado correctamente.", aplicado);

        } catch (DescuentoValidationException e) {
            return buildError(e.getMessage());
        } catch (Exception e) {
            return buildError("No se pudo aplicar el descuento.");
        }
    }

    /**
     * Flujo de descuento de empleado.
     *
     * NOTA:
     * Aquí NO resuelvo el empleado desde BD porque aún no me has pasado
     * el método exacto de búsqueda en tu UsuarioDao/UsuarioService.
     *
     * Este método asume que el caller YA ha validado y resuelto:
     * - idEmpleadoBeneficiario
     * - nombreEmpleadoBeneficiario
     */
    public DescuentoAplicadoDTO aplicarDescuentoEmpleado(
            AplicarDescuentoRequest request,
            Integer idEmpleadoBeneficiario,
            String nombreEmpleadoBeneficiario
    ) {
        try {
            validarRequestBase(request);
            validarTicketParaDescuento(request);

            String codigoEmpleado = normalize(request.getCodigoEmpleado());
            if (codigoEmpleado.isEmpty()) {
                throw new DescuentoValidationException("Debes introducir el código de empleado.");
            }

            if (idEmpleadoBeneficiario == null || idEmpleadoBeneficiario <= 0) {
                throw new DescuentoValidationException("No se ha podido validar el empleado beneficiario.");
            }

            DescuentoDTO descuento = descuentoDao.findDescuentoEmpleadoActivo()
                    .orElseThrow(() -> new DescuentoValidationException(
                            "No hay ningún descuento de empleado activo configurado."
                    ));

            validarDescuentoAplicable(descuento, request);

            DescuentoAplicado aplicado = construirDescuentoAplicado(
                    descuento,
                    codigoEmpleado,
                    idEmpleadoBeneficiario,
                    nombreEmpleadoBeneficiario,
                    request.getSubtotalTicket()
            );

            return buildOk("Descuento de empleado aplicado correctamente.", aplicado);

        } catch (DescuentoValidationException e) {
            return buildError(e.getMessage());
        } catch (Exception e) {
            return buildError("No se pudo aplicar el descuento de empleado.");
        }
    }

    /**
     * Convierte el descuento aplicado en memoria en DTO persistible
     * para integrarlo luego en RegistrarVentaRequest.
     */
    public RegistrarVentaDescuentoRequest toRegistrarVentaDescuentoRequest(
            DescuentoAplicado descuentoAplicado,
            int idUsuarioAplica
    ) {
        if (descuentoAplicado == null) {
            return null;
        }

        if (idUsuarioAplica <= 0) {
            throw new IllegalArgumentException("idUsuarioAplica inválido.");
        }

        RegistrarVentaDescuentoRequest dto = new RegistrarVentaDescuentoRequest();
        dto.setIdDescuento(descuentoAplicado.getIdDescuento());
        dto.setIdUsuarioAplica(idUsuarioAplica);
        dto.setIdEmpleadoBeneficiario(descuentoAplicado.getIdEmpleadoBeneficiario());
        dto.setCodigoIntroducido(descuentoAplicado.getCodigoIntroducido());
        dto.setTipoDescuentoAplicado(descuentoAplicado.getTipo());
        dto.setValorDescuentoAplicado(safe(descuentoAplicado.getValor()));
        dto.setImporteBase(safe(descuentoAplicado.getImporteBase()));
        dto.setImporteDescuento(safe(descuentoAplicado.getImporteDescuento()));
        dto.setObservaciones(buildObservaciones(descuentoAplicado));
        dto.setNombreDescuento(descuentoAplicado.getNombre());
        dto.setOrigenDescuento(descuentoAplicado.getOrigen());

        return dto;
    }

    // =====================================================
    // VALIDACIONES
    // =====================================================

    private void validarRequestBase(AplicarDescuentoRequest request) {
        if (request == null) {
            throw new DescuentoValidationException("La solicitud de descuento no puede ser null.");
        }

        if (request.getSubtotalTicket() == null || request.getSubtotalTicket().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescuentoValidationException("El ticket no tiene un subtotal válido.");
        }
    }

    private void validarTicketParaDescuento(AplicarDescuentoRequest request) {
        if (request.isTicketVacio()) {
            throw new DescuentoValidationException("El ticket está vacío.");
        }

        if (request.isYaTieneDescuento()) {
            throw new DescuentoValidationException("El ticket ya tiene un descuento aplicado.");
        }
    }

    private void validarDescuentoAplicable(DescuentoDTO descuento, AplicarDescuentoRequest request) {
        if (descuento == null) {
            throw new DescuentoValidationException("El descuento no existe.");
        }

        if (!descuento.isActivo()) {
            throw new DescuentoValidationException("El descuento no está activo.");
        }

        if (!estaVigente(descuento)) {
            throw new DescuentoValidationException("El descuento no está vigente.");
        }

        if (superaLimiteUsos(descuento)) {
            throw new DescuentoValidationException("El descuento ha alcanzado su límite de uso.");
        }

        if (request.isTieneComboAplicado() && !descuento.isAplicaACombos()) {
            throw new DescuentoValidationException(
                    "No se puede aplicar este descuento a un ticket con combos."
            );
        }

        if (descuento.getTipo() == null || descuento.getTipo().isBlank()) {
            throw new DescuentoValidationException("El descuento no tiene tipo configurado.");
        }

        if (descuento.getValor() == null || descuento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DescuentoValidationException("El descuento no tiene un valor válido.");
        }

        if ("PORCENTAJE".equalsIgnoreCase(descuento.getTipo())
                && descuento.getValor().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DescuentoValidationException("El porcentaje de descuento no puede ser mayor que 100.");
        }
    }

    private boolean estaVigente(DescuentoDTO descuento) {
        LocalDateTime now = LocalDateTime.now();

        if (descuento.getFechaInicio() != null && now.isBefore(descuento.getFechaInicio())) {
            return false;
        }

        if (descuento.getFechaFin() != null && now.isAfter(descuento.getFechaFin())) {
            return false;
        }

        return true;
    }

    private boolean superaLimiteUsos(DescuentoDTO descuento) {
        if (descuento.getUsoMaximo() == null) {
            return false;
        }

        return descuento.getUsoActual() >= descuento.getUsoMaximo();
    }

    // =====================================================
    // CÁLCULOS
    // =====================================================

    private DescuentoAplicado construirDescuentoAplicado(
            DescuentoDTO descuento,
            String codigoIntroducido,
            Integer idEmpleadoBeneficiario,
            String nombreEmpleadoBeneficiario,
            BigDecimal subtotalTicket
    ) {
        BigDecimal subtotal = safe(subtotalTicket);
        BigDecimal importeDescuento = calcularImporteDescuento(
                descuento.getTipo(),
                descuento.getValor(),
                subtotal
        );
        BigDecimal totalFinal = subtotal.subtract(importeDescuento);

        DescuentoAplicado aplicado = new DescuentoAplicado();
        aplicado.setIdDescuento(descuento.getIdDescuento());
        aplicado.setNombre(descuento.getNombre());
        aplicado.setTipo(descuento.getTipo());
        aplicado.setValor(safe(descuento.getValor()));
        aplicado.setOrigen(descuento.getOrigen());
        aplicado.setCodigoIntroducido(normalize(codigoIntroducido));
        aplicado.setIdEmpleadoBeneficiario(idEmpleadoBeneficiario);
        aplicado.setNombreEmpleadoBeneficiario(
                nombreEmpleadoBeneficiario != null ? nombreEmpleadoBeneficiario.trim() : null
        );
        aplicado.setImporteBase(subtotal);
        aplicado.setImporteDescuento(importeDescuento);
        aplicado.setTotalFinal(totalFinal.max(BigDecimal.ZERO));

        return aplicado;
    }

    private BigDecimal calcularImporteDescuento(String tipo, BigDecimal valor, BigDecimal subtotal) {
        BigDecimal subtotalSafe = safe(subtotal);
        BigDecimal valorSafe = safe(valor);

        if (subtotalSafe.compareTo(BigDecimal.ZERO) <= 0 || valorSafe.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal descuento;

        if ("PORCENTAJE".equalsIgnoreCase(tipo)) {
            descuento = subtotalSafe
                    .multiply(valorSafe)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("IMPORTE_FIJO".equalsIgnoreCase(tipo)) {
            descuento = valorSafe.setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new DescuentoValidationException("Tipo de descuento no soportado: " + tipo);
        }

        if (descuento.compareTo(subtotalSafe) > 0) {
            return subtotalSafe;
        }

        return descuento;
    }

    // =====================================================
    // HELPERS DTO RESPUESTA
    // =====================================================

    private DescuentoAplicadoDTO buildOk(String mensaje, DescuentoAplicado descuentoAplicado) {
        DescuentoAplicadoDTO dto = new DescuentoAplicadoDTO();
        dto.setValido(true);
        dto.setMensaje(mensaje);
        dto.setDescuentoAplicado(descuentoAplicado);
        return dto;
    }

    private DescuentoAplicadoDTO buildError(String mensaje) {
        DescuentoAplicadoDTO dto = new DescuentoAplicadoDTO();
        dto.setValido(false);
        dto.setMensaje(mensaje);
        dto.setDescuentoAplicado(null);
        return dto;
    }

    private String buildObservaciones(DescuentoAplicado descuentoAplicado) {
        if (descuentoAplicado.getIdEmpleadoBeneficiario() != null) {
            String nombre = descuentoAplicado.getNombreEmpleadoBeneficiario();
            if (nombre != null && !nombre.isBlank()) {
                return "Beneficiario: " + nombre.trim();
            }
            return "Beneficiario ID: " + descuentoAplicado.getIdEmpleadoBeneficiario();
        }
        return null;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
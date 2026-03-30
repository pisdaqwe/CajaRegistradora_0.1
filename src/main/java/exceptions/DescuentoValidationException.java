package exceptions;

/**
 * Excepción de negocio del módulo descuentos.
 *
 * Se usa para cortar validaciones internas y devolver
 * mensajes claros a la UI.
 */
public class DescuentoValidationException extends RuntimeException {

    public DescuentoValidationException(String message) {
        super(message);
    }
}
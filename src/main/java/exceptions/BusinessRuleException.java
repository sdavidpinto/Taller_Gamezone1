package exceptions;

public class BusinessRuleException extends GameZoneException {
/**
 * Se lanza cuando una operación viola una regla de negocio, como stock
 * insuficiente, identificación duplicada o una venta sin productos.
 */
    public BusinessRuleException(String message) {
        /**
     * Crea la excepción con la descripción de la regla violada.
     *
     * @param message descripción de la regla de negocio que no se cumplió
     */
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}
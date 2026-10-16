package exceptions;
/**
 * Se lanza cuando ocurre un error al leer o escribir archivos de datos,
 * como un archivo corrupto, sin permisos o inaccesible. Conserva la causa
 * original para no perder la trazabilidad del error.
 */
public class PersistenceException extends GameZoneException {
    /**
     * Crea la excepción con un mensaje de contexto y la causa original.
     *
     * @param message descripción del error, indicando archivo y operación
     * @param cause excepción original que provocó el error (por ejemplo, una IOException)
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE_ERROR");
        initCause(cause);
    }
}
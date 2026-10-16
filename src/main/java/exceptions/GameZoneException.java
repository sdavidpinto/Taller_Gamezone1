package exceptions;

/**
 * Clase raíz de la jerarquía de excepciones personalizadas de GameZone Unicesar.
 * Es abstracta y no verificada (extiende RuntimeException), por lo que los
 * métodos de servicios y repositorios no necesitan declarar {@code throws}.
 * Cada excepción lleva un código de error que identifica su categoría.
 */
public abstract class GameZoneException extends RuntimeException {

    private final String errorCode;

    /**
     * Crea una excepción con un mensaje y un código de error genérico.
     *
     * @param message descripción del problema
     */
    protected GameZoneException(String message) {
        this(message, "GENERIC_ERROR");
    }

    /**
     * Crea una excepción con un mensaje y un código de error específico.
     *
     * @param message descripción del problema
     * @param errorCode código que identifica la categoría del error
     */
    protected GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Obtiene el código de error asociado a la excepción.
     *
     * @return el código de error
     */
    public String getErrorCode() {
        return errorCode;
    }
}
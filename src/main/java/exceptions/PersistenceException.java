package exceptions;

public class PersistenceException extends GameZoneException {

    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE_ERROR");
        initCause(cause);
    }
}
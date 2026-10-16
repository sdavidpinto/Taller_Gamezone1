package exceptions;

public abstract class GameZoneException extends RuntimeException {

    private final String errorCode;

    protected GameZoneException(String message) {
        this(message, "GENERIC_ERROR");
    }

    protected GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
package exceptions;

public class InvalidDataException extends GameZoneException {

    public InvalidDataException(String fieldName, String problem) {
        super("El campo '" + fieldName + "' " + problem, "INVALID_DATA");
    }
}
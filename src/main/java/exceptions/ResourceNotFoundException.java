package exceptions;

public class ResourceNotFoundException extends GameZoneException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super("El recurso de tipo " + resourceType + " con identificador "
                + identifier + " no fue encontrado", "RESOURCE_NOT_FOUND");
    }
}
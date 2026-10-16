package exceptions;
/**
 * Se lanza cuando se busca un recurso del sistema (producto, cliente,
 * vendedor, venta) por su identificador y no existe.
 */
public class ResourceNotFoundException extends GameZoneException {
 /**
     * Crea la excepción construyendo un mensaje descriptivo en español.
     *
     * @param resourceType tipo de recurso buscado (por ejemplo, "producto")
     * @param identifier identificador que se buscó y no se encontró
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("El recurso de tipo " + resourceType + " con identificador "
                + identifier + " no fue encontrado", "RESOURCE_NOT_FOUND");
    }
}
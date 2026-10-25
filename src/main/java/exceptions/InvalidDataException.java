package exceptions;
/**
 * Se lanza cuando un dato de entrada no cumple los criterios de formato,
 * rango o tipo, por ejemplo un precio negativo o un campo vacío.
 */
public class InvalidDataException extends GameZoneException {
/**
     * Crea la excepción construyendo un mensaje descriptivo en español
     * a partir del campo inválido y del problema encontrado.
     * @param fieldName nombre del campo que no es válido
     * @param problem descripción del problema, escrita como complemento
     * del campo (por ejemplo, "debe ser mayor que cero")
     */
    public InvalidDataException(String fieldName, String problem) {
        super("El campo '" + fieldName + "' " + problem, "INVALID_DATA");
    }
}
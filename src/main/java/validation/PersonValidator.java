package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Person;

import java.util.List;

/**
 * Validador del módulo de personas (clientes y vendedores). Verifica datos
 * obligatorios, formato de email, existencia y unicidad de identificación.
 * Solo depende de las capas model y exceptions.
 */
public class PersonValidator {

    /**
     * Valida los datos comunes a toda persona: nombre, identificación y
     * teléfono obligatorios. Se usa para personas sin email, como los vendedores.
     *
     * @param id número de identificación
     * @param name nombre completo
     * @param phone teléfono de contacto
     * @throws InvalidDataException si algún campo está vacío o es nulo
     */
    public static void validatePersonData(String id, String name, String phone) {
        requireNonBlank(name, "nombre");
        requireNonBlank(id, "identificación");
        requireNonBlank(phone, "teléfono");
    }

    /**
     * Valida los datos de una persona que tiene email, como los clientes:
     * los campos obligatorios y que el email contenga arroba y punto.
     *
     * @param id número de identificación
     * @param name nombre completo
     * @param email correo electrónico
     * @param phone teléfono de contacto
     * @throws InvalidDataException si algún campo está vacío o el email no tiene formato válido
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        validatePersonData(id, name, phone);
        requireNonBlank(email, "email");
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("email", "no tiene un formato válido");
        }
    }

    /**
     * Valida que la persona buscada exista.
     *
     * @param person persona obtenida de la búsqueda (puede ser null)
     * @param personId identificación con la que se buscó, usada en el mensaje
     * @throws ResourceNotFoundException si la persona es null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Valida que no exista otra persona registrada con la misma identificación.
     *
     * @param existing personas ya registradas
     * @param newId identificación que se quiere registrar
     * @throws BusinessRuleException si ya existe una persona con esa identificación
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (person.getIdNumber() != null && person.getIdNumber().equals(newId)) {
                throw new BusinessRuleException("Ya existe una persona registrada con la identificación " + newId);
            }
        }
    }

    /**
     * Verifica que un texto obligatorio no sea nulo ni esté en blanco.
     *
     * @param value texto a verificar
     * @param fieldName nombre del campo, usado en el mensaje de error
     * @throws InvalidDataException si el texto es nulo o está en blanco
     */
    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "es obligatorio");
        }
    }
}
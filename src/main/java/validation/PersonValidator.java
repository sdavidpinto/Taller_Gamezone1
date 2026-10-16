package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Person;

import java.util.List;

public class PersonValidator {

    public static void validatePersonData(String id, String name, String phone) {
        requireNonBlank(name, "nombre");
        requireNonBlank(id, "identificación");
        requireNonBlank(phone, "teléfono");
    }

    public static void validatePersonData(String id, String name, String email, String phone) {
        validatePersonData(id, name, phone);
        requireNonBlank(email, "email");
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("email", "no tiene un formato válido");
        }
    }

    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (person.getIdNumber() != null && person.getIdNumber().equals(newId)) {
                throw new BusinessRuleException("Ya existe una persona registrada con la identificación " + newId);
            }
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "es obligatorio");
        }
    }
}
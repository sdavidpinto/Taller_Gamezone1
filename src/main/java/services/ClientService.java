package services;

import model.Client;
import persistence.ClientRepository;

import java.util.List;

/**
 * Capa de servicios para Client. Recibe la interfaz ClientRepository
 * por inyección de dependencias (constructor), nunca crea su propia
 * implementación concreta.
 */
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Valida que un campo obligatorio no venga nulo ni vacío/en blanco.
     */
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio.");
        }
    }

    public void registerClient(String name, String idNumber, String phone, String email) {
        requireNonBlank(name, "nombre");
        requireNonBlank(idNumber, "identificación");
        requireNonBlank(phone, "teléfono");
        requireNonBlank(email, "email");
        if (clientRepository.findByIdNumber(idNumber) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ID: " + idNumber);
        }
        Client cliente = new Client(name, idNumber, phone, email);
        clientRepository.save(cliente);
    }

    public Client findByIdNumber(String idNumber) {
        return clientRepository.findByIdNumber(idNumber);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public boolean updateClient(String idNumber, String nuevoNombre, String nuevoPhone, String nuevoEmail) {
        Client existente = clientRepository.findByIdNumber(idNumber);
        if (existente == null) {
            return false;
        }
        existente.setName(nuevoNombre);
        existente.setPhone(nuevoPhone);
        existente.setEmail(nuevoEmail);
        return clientRepository.update(existente);
    }

    public boolean deleteClient(String idNumber) {
        return clientRepository.deleteByIdNumber(idNumber);
    }
}
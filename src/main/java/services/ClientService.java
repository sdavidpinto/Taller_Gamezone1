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

    /**
     * Registers a new client after validating that all required fields
     * are present and that no other client is already registered with
     * the same identification number.
     *
     * @param name the full name of the client
     * @param idNumber the identification number of the client
     * @param phone the contact phone number of the client
     * @param email the email address of the client
     * @throws IllegalArgumentException if any field is missing/blank,
     *         or if a client with the same idNumber already exists
     */
    public void registerClient(String name, String idNumber, String phone, String email) {
        requireNonBlank(name, "nombre");
        requireNonBlank(idNumber, "identificación");
        requireNonBlank(phone, "teléfono");
        requireNonBlank(email, "email");
        if (clientRepository.findByIdNumber(idNumber) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ID: " + idNumber);
        }
        Client client = new Client(name, idNumber, phone, email);
        clientRepository.save(client);
    }

    /**
     * Finds a client by their identification number.
     *
     * @param idNumber the identification number to search for
     * @return the client with the given ID, or null if not found
     */
    public Client findByIdNumber(String idNumber) {
        return clientRepository.findByIdNumber(idNumber);
    }

    /**
     * Returns all registered clients.
     *
     * @return a list containing all clients
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Updates the information of an existing client.
     *
     * @param idNumber the identification number of the client to update
     * @param newName the new name of the client
     * @param newPhone the new phone number of the client
     * @param newEmail the new email address of the client
     * @return true if the client was found and updated, false otherwise
     */
    public boolean updateClient(String idNumber, String newName, String newPhone, String newEmail) {
        Client existingClient = clientRepository.findByIdNumber(idNumber);
        if (existingClient == null) {
            return false;
        }
        existingClient.setName(newName);
        existingClient.setPhone(newPhone);
        existingClient.setEmail(newEmail);
        return clientRepository.update(existingClient);
    }

    /**
     * Deletes a client by their identification number.
     *
     * @param idNumber the identification number of the client to delete
     * @return true if the client was found and deleted, false otherwise
     */
    public boolean deleteClient(String idNumber) {
        return clientRepository.deleteByIdNumber(idNumber);
    }
}
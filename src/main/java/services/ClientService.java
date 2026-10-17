package services;

import validation.PersonValidator;

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
     * Registers a new client after validating that all required fields
     * are present and that no other client is already registered with
     * the same identification number.
     *
     * @param name the full name of the client
     * @param idNumber the identification number of the client
     * @param phone the contact phone number of the client
     * @param email the email address of the client
     * @throws exceptions.InvalidDataException if any field is missing/blank or the email is malformed
     * @throws exceptions.BusinessRuleException if a client with the same idNumber already exists
     */
    public void registerClient(String name, String idNumber, String phone, String email) {
        PersonValidator.validatePersonData(idNumber, name, email, phone);
        PersonValidator.validateUniquePerson(clientRepository.findAll(), idNumber);
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
        Client client = clientRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(client, idNumber);
        return client;
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
        PersonValidator.validatePersonExists(existingClient, idNumber);
        PersonValidator.validatePersonData(idNumber, newName, newEmail, newPhone);
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
        Client client = clientRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(client, idNumber);
        return clientRepository.deleteByIdNumber(idNumber);
    }
}
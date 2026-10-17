package services;

import validation.PersonValidator;
import model.Client;
import persistence.ClientRepository;
import java.util.List;

/**
 * Gestiona las operaciones relacionadas con los clientes.
 * Utiliza {@link ClientRepository} mediante inyección de dependencias.
 */
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Crea el servicio de clientes.
     *
     * @param clientRepository repositorio utilizado para gestionar los clientes
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Registra un cliente después de validar sus datos y verificar que su
     * número de identificación no esté registrado.
     *
     * @param name nombre completo del cliente
     * @param idNumber número de identificación del cliente
     * @param phone teléfono de contacto del cliente
     * @param email correo electrónico del cliente
     * @throws exceptions.InvalidDataException si algún dato es inválido
     * @throws exceptions.BusinessRuleException si el cliente ya existe
     */
    public void registerClient(String name, String idNumber, String phone, String email) {
        PersonValidator.validatePersonData(idNumber, name, email, phone);
        PersonValidator.validateUniquePerson(clientRepository.findAll(), idNumber);
        Client client = new Client(name, idNumber, phone, email);
        clientRepository.save(client);
    }

    /**
     * Busca un cliente por su número de identificación.
     *
     * @param idNumber número de identificación que se desea buscar
     * @return cliente encontrado
     * @throws exceptions.ResourceNotFoundException si el cliente no existe
     */
    public Client findByIdNumber(String idNumber) {
        Client client = clientRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(client, idNumber);
        return client;
    }

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return lista de clientes registrados
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param idNumber número de identificación del cliente
     * @param newName nuevo nombre del cliente
     * @param newPhone nuevo teléfono del cliente
     * @param newEmail nuevo correo electrónico del cliente
     * @return {@code true} si el cliente fue actualizado
     * @throws exceptions.ResourceNotFoundException si el cliente no existe
     * @throws exceptions.InvalidDataException si algún dato es inválido
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
     * Elimina un cliente por su número de identificación.
     *
     * @param idNumber número de identificación del cliente
     * @return {@code true} si el cliente fue eliminado
     * @throws exceptions.ResourceNotFoundException si el cliente no existe
     */
    public boolean deleteClient(String idNumber) {
        Client client = clientRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(client, idNumber);
        return clientRepository.deleteByIdNumber(idNumber);
    }
}

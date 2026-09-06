package persistence;

import model.Client;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Client.
 * Los clientes se identifican de forma única por su idNumber.
 */
public interface ClientRepository {

    /**
     * Guarda un nuevo cliente.
     *
     * @param client el cliente a guardar
     */
    void save(Client client);

    /**
     * Busca un cliente por su número de identificación.
     *
     * @param idNumber el número de identificación a buscar
     * @return el cliente con el ID dado, o null si no se encuentra
     */
    Client findByIdNumber(String idNumber);

    /**
     * Retorna todos los clientes registrados.
     *
     * @return una lista con todos los clientes
     */
    List<Client> findAll();

    /**
     * Actualiza la información de un cliente existente.
     *
     * @param client el cliente con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Client client);

    /**
     * Elimina un cliente por su número de identificación.
     *
     * @param idNumber el número de identificación del cliente a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteByIdNumber(String idNumber);
}
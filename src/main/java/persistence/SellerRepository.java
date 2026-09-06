package persistence;

import model.Seller;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Seller.
 * Los vendedores se identifican de forma única por su idNumber.
 */
public interface SellerRepository {

    /**
     * Guarda un nuevo vendedor.
     *
     * @param seller el vendedor a guardar
     */
    void save(Seller seller);

    /**
     * Busca un vendedor por su número de identificación.
     *
     * @param idNumber el número de identificación a buscar
     * @return el vendedor con el ID dado, o null si no se encuentra
     */
    Seller findByIdNumber(String idNumber);

    /**
     * Retorna todos los vendedores registrados.
     *
     * @return una lista con todos los vendedores
     */
    List<Seller> findAll();

    /**
     * Actualiza la información de un vendedor existente.
     *
     * @param seller el vendedor con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Seller seller);

    /**
     * Elimina un vendedor por su número de identificación.
     *
     * @param idNumber el número de identificación del vendedor a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteByIdNumber(String idNumber);
}
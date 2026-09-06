package persistence;

import model.Seller;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Seller.
 * Los vendedores se identifican de forma única por su idNumber.
 */
public interface SellerRepository {

    void save(Seller seller);

    Seller findByIdNumber(String idNumber);

    List<Seller> findAll();

    boolean update(Seller seller);

    boolean deleteByIdNumber(String idNumber);
}
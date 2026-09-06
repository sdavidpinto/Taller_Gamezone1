package persistence;

import model.Sale;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Sale.
 * La capa de servicios depende de esta interfaz, nunca de su implementación
 * concreta, de modo que el origen de los datos (archivo, base de datos, etc.)
 * pueda cambiarse sin afectar el resto de la aplicación.
 */
public interface SaleRepository {

    void save(Sale sale);

    Sale findByCode(String code);

    List<Sale> findAll();

    boolean update(Sale sale);

    boolean deleteByCode(String code);
}

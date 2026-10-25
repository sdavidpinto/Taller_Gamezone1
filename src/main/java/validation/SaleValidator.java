package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Client;
import model.Product;
import model.Sale;
import model.Seller;

import java.util.List;

/**
 * Validador del módulo de ventas. Verifica que una venta tenga cliente,
 * vendedor y productos, y que una venta buscada exista. Solo depende de
 * las capas model y exceptions.
 */
public class SaleValidator {

    /**
     * Valida los datos necesarios para registrar una venta.
     *
     * @param client cliente que realiza la compra
     * @param seller vendedor que atiende la venta
     * @param products productos y accesorios de la venta
     * @throws InvalidDataException si el cliente o el vendedor son nulos
     * @throws BusinessRuleException si la lista de productos es nula o está vacía
     */
    public static void validateSaleData(Client client, Seller seller, List<Product> products) {
        if (client == null) {
            throw new InvalidDataException("cliente", "es obligatorio");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "es obligatorio");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("La venta debe incluir al menos un producto o accesorio");
        }
    }

    /**
     * Valida que la venta buscada exista.
     *
     * @param sale venta obtenida de la búsqueda (puede ser null)
     * @param saleId código con el que se buscó, usado en el mensaje
     * @throws ResourceNotFoundException si la venta es null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}
package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Client;
import model.Product;
import model.Sale;
import model.Seller;

import java.util.List;

public class SaleValidator {

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

    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}
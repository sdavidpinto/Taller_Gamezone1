package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Product;

public class ProductValidator {

    public static void validateProductData(String id, String title, double price, int stock) {
        requireNonBlank(id, "identificador");
        requireNonBlank(title, "título");
        if (price <= 0) {
            throw new InvalidDataException("precio", "debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getAvailableQuantity() < requestedQuantity) {
            throw new BusinessRuleException("Stock insuficiente para '" + product.getTitle()
                    + "': disponible " + product.getAvailableQuantity()
                    + ", solicitado " + requestedQuantity);
        }
    }

    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "no puede estar vacío");
        }
    }
}
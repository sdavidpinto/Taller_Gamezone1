package validation;

import exceptions.BusinessRuleException;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import model.Product;

/**
 * Validador del módulo de productos. Concentra las verificaciones de datos
 * y reglas sobre productos, y lanza las excepciones personalizadas cuando
 * algo no se cumple. Solo depende de las capas model y exceptions.
 */
public class ProductValidator {

    /**
     * Valida los datos básicos de un producto: identificador y título no
     * vacíos, precio mayor que cero y stock no negativo.
     *
     * @param id identificador del producto
     * @param title título del producto
     * @param price precio unitario
     * @param stock cantidad disponible en inventario
     * @throws InvalidDataException si algún dato no cumple los criterios
     */
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

    /**
     * Valida que el producto tenga stock suficiente para la cantidad solicitada.
     *
     * @param product producto que se quiere vender
     * @param requestedQuantity cantidad solicitada
     * @throws BusinessRuleException si el stock disponible es menor que la cantidad solicitada
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getAvailableQuantity() < requestedQuantity) {
            throw new BusinessRuleException("Stock insuficiente para '" + product.getTitle()
                    + "': disponible " + product.getAvailableQuantity()
                    + ", solicitado " + requestedQuantity);
        }
    }

    /**
     * Valida que el producto buscado exista.
     *
     * @param product producto obtenido de la búsqueda (puede ser null)
     * @param productId identificador con el que se buscó, usado en el mensaje
     * @throws ResourceNotFoundException si el producto es null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }

    /**
     * Verifica que un texto obligatorio no sea nulo ni esté en blanco.
     *
     * @param value texto a verificar
     * @param fieldName nombre del campo, usado en el mensaje de error
     * @throws InvalidDataException si el texto es nulo o está en blanco
     */
    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName, "no puede estar vacío");
        }
    }
}
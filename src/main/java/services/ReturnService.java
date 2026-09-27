package services;

import model.Product;
import model.Return;
import model.Sale;
import persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Capa de servicio para Return. Recibe ReturnRepository, SaleService y
 * ProductService mediante inyección por constructor, y contiene la lógica
 * de negocio para registrar devoluciones (validación de plazo, validación
 * de pertenencia, restauración de stock) y generar el reporte de balance
 * mensual.
 */
public class ReturnService {

    private static final int RETURN_WINDOW_DAYS = 30;

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Crea una nueva instancia de ReturnService.
     *
     * @param returnRepository repositorio usado para persistir y consultar devoluciones
     * @param saleService servicio usado para resolver y validar ventas
     * @param productService servicio usado para restaurar el stock de productos
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
    }
    
    /**
 * Registra una nueva devolución luego de validar la existencia de la
 * venta, el plazo de 30 días para devoluciones, y que los productos
 * solicitados realmente pertenezcan a la venta original. Si todo es
 * válido, restaura el stock de los productos devueltos y persiste la
 * nueva devolución.
 *
 * @param saleId el código de la venta original
 * @param productIds los identificadores de los productos a devolver
 * @param reason el motivo de la devolución
 * @return la devolución recién creada
 * @throws IllegalArgumentException si la venta no existe, si el plazo
 *         de 30 días ya expiró, o si algún producto no pertenece a la venta
 */
public Return registerReturn(String saleId, List<String> productIds, String reason) {
    Sale sale = saleService.findByCode(saleId);
    if (sale == null) {
        throw new IllegalArgumentException("No existe una venta con el código: " + saleId);
    }
    if (!sale.canBeReturned()) {
        throw new IllegalArgumentException("El plazo de 30 días para devolver esta venta ya expiró.");
    }

    List<Product> productsToReturn = resolveAndValidateProducts(sale, productIds);

    Return newReturn = new Return(generateId(), LocalDate.now(), sale, productsToReturn, reason);
    newReturn.calculateRefundAmount();

    for (Product product : productsToReturn) {
        productService.restoreStock(product.getIdentifier(), 1);
    }

    List<Return> returns = returnRepository.loadAll();
    returns.add(newReturn);
    returnRepository.saveAll(returns);

    return newReturn;
}

/**
 * Valida que cada identificador de producto solicitado realmente
 * pertenezca a la venta dada, y retorna los objetos Product
 * correspondientes.
 *
 * @param sale la venta original
 * @param productIds los identificadores de los productos solicitados para devolución
 * @return la lista de objetos Product que coinciden con los identificadores solicitados
 * @throws IllegalArgumentException si algún producto solicitado no pertenece a la venta
 */
private List<Product> resolveAndValidateProducts(Sale sale, List<String> productIds) {
    List<Product> result = new ArrayList<>();
    for (String productId : productIds) {
        Product match = null;
        for (Product product : sale.getProducts()) {
            if (product.getIdentifier().equals(productId)) {
                match = product;
                break;
            }
        }
        if (match == null) {
            throw new IllegalArgumentException(
                    "El producto " + productId + " no pertenece a la venta " + sale.getCode());
        }
        result.add(match);
    }
    return result;
}

/**
 * Genera un identificador único para una nueva devolución.
 *
 * @return un identificador generado aleatoriamente
 */
private String generateId() {
    return UUID.randomUUID().toString();
}

/**
 * Retorna todas las devoluciones registradas.
 *
 * @return una lista con todas las devoluciones
 */
public List<Return> viewAllReturns() {
    return returnRepository.loadAll();
}

/**
 * Retorna todas las devoluciones cuya venta original pertenece al
 * cliente indicado.
 *
 * @param customerId el número de identificación del cliente
 * @return una lista con las devoluciones realizadas por ese cliente
 */
public List<Return> viewReturnsByCustomer(String customerId) {
    List<Return> result = new ArrayList<>();
    for (Return r : returnRepository.loadAll()) {
        if (r.getSale().getClient().getIdNumber().equals(customerId)) {
            result.add(r);
        }
    }
    return result;
}

/**
 * Retorna todas las devoluciones asociadas a una venta específica.
 *
 * @param saleId el código de la venta
 * @return una lista con las devoluciones realizadas sobre esa venta
 */
public List<Return> viewReturnsBySale(String saleId) {
    List<Return> result = new ArrayList<>();
    for (Return r : returnRepository.loadAll()) {
        if (r.getSale().getCode().equals(saleId)) {
            result.add(r);
        }
    }
    return result;
}
    
}
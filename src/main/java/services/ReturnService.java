package services;

import model.Product;
import model.Return;
import model.Sale;
import persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Accessory;

/**
 * Capa de servicios para Return. Ahora también recibe AccessoryService
 * por inyección de dependencias (constructor), para que el stock se
 * pueda restaurar correctamente tanto para productos como para
 * accesorios al procesar una devolución.
 */
public class ReturnService {

    private static final int RETURN_WINDOW_DAYS = 30;

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Crea un nuevo ReturnService.
     *
     * @param returnRepository el repositorio de devoluciones
     * @param saleService usado para validar y resolver ventas
     * @param productService usado para resolver y restaurar productos
     * @param accessoryService usado para resolver y restaurar accesorios
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService,
                          ProductService productService, AccessoryService accessoryService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
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

    for (Product item : productsToReturn) {
    restoreStockFor(item);
}

    List<Return> returns = returnRepository.loadAll();
    returns.add(newReturn);
    returnRepository.saveAll(returns);

    return newReturn;
}

    /**
     * Restaura el stock de un ítem devuelto. Si el ítem es un Accessory,
     * delega en AccessoryService; en caso contrario (un Product como
     * VideoGame o Console), delega en ProductService. Esto evita duplicar
     * la lógica de restauración de stock por cada tipo de ítem.
     *
     * @param item el ítem devuelto cuyo stock debe restaurarse
     */
    private void restoreStockFor(Product item) {
        if (item instanceof Accessory accessory) {
            accessoryService.restoreStock(accessory.getIdentifier(), 1);
        } else {
            productService.restoreStock(item.getIdentifier(), 1);
        }
    }
    
    /**
     * Calcula el total de ventas de un mes y año dados, usando el total
     * final de cada venta (que ya refleja descuentos por promociones y
     * costos adicionales por garantías extendidas), no el precio de lista.
     *
     * @param month el mes a evaluar (1-12)
     * @param year el año a evaluar
     * @return la suma de los totales finales de las ventas de ese período
     */
    public double calculateMonthlySales(int month, int year) {
        double salesTotal = 0;
        for (Sale sale : saleService.findAll()) {
            if (sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year) {
                salesTotal += sale.getTotal();
            }
        }
        return salesTotal;
    }
    
    /**
     * Calcula el total reembolsado por devoluciones de un mes y año dados.
     *
     * @param month el mes a evaluar (1-12)
     * @param year el año a evaluar
     * @return la suma de los montos reembolsados en las devoluciones de ese período
     */
    public double calculateMonthlyReturns(int month, int year) {
        double returnsTotal = 0;
        for (Return r : returnRepository.loadAll()) {
            if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
                returnsTotal += r.getRefundAmount();
            }
        }
        return returnsTotal;
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
        if (r.getOriginalSale().getClient().getIdNumber().equals(customerId)) {
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
        if (r.getOriginalSale().getCode().equals(saleId)) {
            result.add(r);
        }
    }
    return result;
}

/**
 * Calcula el balance neto de un mes y año dados: el total de ventas
 * menos el total de devoluciones en ese período.
 *
 * @param month el mes a evaluar (1-12)
 * @param year el año a evaluar
 * @return el balance neto (total de ventas menos total de devoluciones)
 */
public double generateMonthlyBalance(int month, int year) {
    double salesTotal = 0;
    for (Sale sale : saleService.findAll()) {
        if (sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year) {
            salesTotal += sale.getTotal();
        }
    }

    double returnsTotal = 0;
    for (Return r : returnRepository.loadAll()) {
        if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
            returnsTotal += r.getRefundAmount();
        }
    }

    return salesTotal - returnsTotal;
}  
}
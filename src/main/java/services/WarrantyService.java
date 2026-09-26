package services;

import model.BasicWarranty;
import model.ExtendedWarranty;
import model.Product;
import model.Sale;
import model.Warranty;
import persistence.SaleRepository;
import persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Capa de servicios para Warranty. Recibe WarrantyRepository, SaleRepository
 * y ProductService por inyección de dependencias (constructor). Resolver
 * el producto y la venta de una garantía ahora ocurre aquí (al momento de
 * la llamada), en lugar de dentro de WarrantyRepositoryFile, lo que rompe
 * la dependencia circular que existía antes entre SaleService,
 * WarrantyService y WarrantyRepository.
 */
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final SaleRepository saleRepository;
    private final ProductService productService;

    /**
     * Crea un nuevo WarrantyService.
     *
     * @param warrantyRepository el repositorio de garantías
     * @param saleRepository usado para resolver ventas por código
     * @param productService usado para resolver productos por identificador
     */
    public WarrantyService(WarrantyRepository warrantyRepository, SaleRepository saleRepository, ProductService productService) {
        this.warrantyRepository = warrantyRepository;
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Carga todas las garantías, resolviendo el producto y la venta de
     * cada una mediante ProductService y SaleRepository.
     *
     * @return la lista de garantías completamente resueltas
     */
    private List<Warranty> loadAllResolved() {
        return warrantyRepository.loadAll(productService::findByIdentifier, saleRepository::findByCode);
    }

    /**
     * Genera un identificador único para una nueva garantía.
     *
     * @return un identificador generado aleatoriamente
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Agrega una garantía nueva a la lista actual y persiste la lista completa.
     *
     * @param warranty la garantía a agregar
     */
    private void saveNewWarranty(Warranty warranty) {
        List<Warranty> warranties = loadAllResolved();
        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);
    }

    /**
     * Crea y persiste una garantía básica automática para el producto y
     * la venta indicados.
     *
     * @param product el producto cubierto por la garantía
     * @param sale la venta en la que se compró el producto
     * @param startDate la fecha desde la que empieza la cobertura
     * @return la BasicWarranty recién creada
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = new BasicWarranty(generateId(), product, sale, startDate);
        saveNewWarranty(warranty);
        return warranty;
    }

    /**
     * Crea y persiste una garantía extendida para el producto y la venta
     * indicados.
     *
     * @param product el producto cubierto por la garantía
     * @param sale la venta en la que se compró el producto
     * @param startDate la fecha desde la que empieza la cobertura
     * @return la ExtendedWarranty recién creada
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = new ExtendedWarranty(generateId(), product, sale, startDate);
        saveNewWarranty(warranty);
        return warranty;
    }

    /**
     * Busca la garantía asociada a un producto específico dentro de una
     * venta específica.
     *
     * @param productId el identificador del producto
     * @param saleCode el código de la venta
     * @return la garantía encontrada, o null si no existe
     */
    public Warranty findWarrantyByProduct(String productId, String saleCode) {
        for (Warranty warranty : loadAllResolved()) {
            boolean sameProduct = warranty.getProduct().getIdentifier().equals(productId);
            boolean sameSale = warranty.getSale().getCode().equals(saleCode);
            if (sameProduct && sameSale) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Retorna todas las garantías registradas.
     *
     * @return una lista con todas las garantías
     */
    public List<Warranty> listAllWarranties() {
        return loadAllResolved();
    }

    /**
     * Retorna las garantías que están vigentes actualmente.
     *
     * @return una lista con solo las garantías actualmente vigentes
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> activeWarranties = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Warranty warranty : loadAllResolved()) {
            if (warranty.isActive(today)) {
                activeWarranties.add(warranty);
            }
        }
        return activeWarranties;
    }

    /**
     * Retorna las garantías cuya fecha de fin cae dentro de la cantidad
     * de días indicada desde hoy, y que aún no han vencido.
     *
     * @param daysAhead la cantidad de días de anticipación a revisar
     * @return una lista con las garantías que vencen dentro de ese período
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        List<Warranty> expiringSoon = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);
        for (Warranty warranty : loadAllResolved()) {
            LocalDate endDate = warranty.getEndDate();
            boolean notExpiredYet = !endDate.isBefore(today);
            boolean withinRange = !endDate.isAfter(limit);
            if (notExpiredYet && withinRange) {
                expiringSoon.add(warranty);
            }
        }
        return expiringSoon;
    }
}
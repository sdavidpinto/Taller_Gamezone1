
package services;

import java.time.LocalDate;
import model.Client;
import model.Product;
import model.Sale;
import model.Seller;
import model.Accessory;
import model.Console;
import model.Promotion;
import persistence.ClientRepository;
import persistence.ProductRepository;
import persistence.SaleRepository;
import persistence.SellerRepository;
import persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.ExtendedWarranty;

/**
 * Capa de servicios para Sale. Aquí viven las reglas de negocio
 * (validaciones, control de stock, actualización de historial de
 * cliente). SaleService depende de las INTERFACES de los repositorios,
 * no de sus implementaciones concretas, por lo que puede recibir por
 * inyección de dependencias cualquier combinación (archivo, memoria,
 * SQL) sin cambiar una sola línea de esta clase.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final AccessoryRepository accessoryRepository;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;
    
    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository,
                        SellerRepository sellerRepository, ProductRepository productRepository,AccessoryRepository accessoryRepository,PromotionService promotionService,WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.accessoryRepository=accessoryRepository;
        this.promotionService=promotionService;
         this.warrantyService=warrantyService;
    }

    /**
     * Registra una venta nueva a partir de identificadores (no de objetos
     * ya armados). Valida existencia de cliente/vendedor/productos y
     * disponibilidad de stock, y descuenta el stock vendido.
     */
    public Sale registerSale(String code, String clientIdNumber, String sellerIdNumber, List<String> productIdentifiers, List<String> accessoryIdentifiers, List<String> extendedWarrantyProductIds) {
        if ((productIdentifiers == null || productIdentifiers.isEmpty())
                && (accessoryIdentifiers == null || accessoryIdentifiers.isEmpty())) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto o accesorio");
        }
        Client client = clientRepository.findByIdNumber(clientIdNumber);
        if (client == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + clientIdNumber);
        }

        Seller seller = sellerRepository.findByIdNumber(sellerIdNumber);
        if (seller == null) {
            throw new IllegalArgumentException("Vendedor no encontrado: " + sellerIdNumber);
        }

        List<Product> products = new ArrayList<>();
    if (productIdentifiers != null) {
        for (String id : productIdentifiers) {
            Product p = productRepository.findByIdentifier(id);
            if (p == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + id);
            }
            if (p.getAvailableQuantity() <= 0) {
                throw new IllegalStateException("Sin stock disponible: " + p.getTitle());
            }
            products.add(p); 
        }
    }
    if (accessoryIdentifiers != null) {
        for (String id : accessoryIdentifiers) {
            Accessory a = accessoryRepository.findByIdentifier(id);
            if (a == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + id);
            }
            if (a.getAvailableQuantity() <= 0) {
                throw new IllegalStateException("Sin stock disponible: " + a.getTitle());
            }
            products.add(a);
        }
    }

        Sale sale = new Sale(code, new Date(), client, seller, products);
        applyBestPromotion(sale);
        
        for (Product p : products) {
            if (p instanceof Console) {
                warrantyService.assignBasicWarranty(p, sale, LocalDate.now());
            }
        }
        
        
        double extendedWarrantyCost = 0;
        if (extendedWarrantyProductIds != null) {
            for (String id : extendedWarrantyProductIds) {
                Product warrantyProduct = null;
                for (Product p : products) {
                    if (p.getIdentifier().equals(id)) {
                        warrantyProduct = p;
                        break;
                    }
                }
                if (warrantyProduct == null) {
                    throw new IllegalArgumentException("Producto no encontrado para garantia extendida: " + id);
                }
                ExtendedWarranty extendedWarranty = warrantyService.assignExtendedWarranty(warrantyProduct, sale, LocalDate.now());
                extendedWarrantyCost += extendedWarranty.getAdditionalCost();
            }
        }
        
        sale.setTotal(sale.getTotal() + extendedWarrantyCost);
        saleRepository.save(sale);
        client.addSale(sale);
        return sale;
    }

    public Sale findByCode(String code) {
        return saleRepository.findByCode(code);
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    /**
     * Cancela una venta: la elimina del repositorio y devuelve el stock
     * de cada producto vendido.
     */
    public boolean cancelSale(String code) {
        Sale sale = saleRepository.findByCode(code);
        if (sale == null) {
            return false;
        }
        for (Product p : sale.getProducts()) {
        if (p instanceof Accessory) {
            Accessory actualAccessory = accessoryRepository.findByIdentifier(p.getIdentifier());
            if (actualAccessory != null) {
                actualAccessory.setAvailableQuantity(actualAccessory.getAvailableQuantity() + 1);
                accessoryRepository.update(actualAccessory);
            }
        } else {
            Product actual = productRepository.findByIdentifier(p.getIdentifier());
            if (actual != null) {
                actual.setAvailableQuantity(actual.getAvailableQuantity() + 1);
                productRepository.update(actual);
            }
        }
    }

        return saleRepository.deleteByCode(code);
    }
    
    private void applyBestPromotion(Sale sale) {
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion == null) {
            return;
        }
        double discount = bestPromotion.calculateDiscount(sale.getProducts());
        sale.setAppliedPromotionName(bestPromotion.getName());
        sale.setDiscountAmount(discount);
        sale.setTotal(sale.getTotal() - discount);
    }
}

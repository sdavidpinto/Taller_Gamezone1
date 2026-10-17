
package services;

import validation.PersonValidator;
import validation.ProductValidator;
import validation.SaleValidator;
import exceptions.ResourceNotFoundException;

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
    public Sale registerSale(String code, String clientIdNumber, String sellerIdNumber,List<String> productIdentifiers, List<String> accessoryIdentifiers,List<String> extendedWarrantyProductIds) {

                Client client = clientRepository.findByIdNumber(clientIdNumber);
        PersonValidator.validatePersonExists(client, clientIdNumber);
        Seller seller = sellerRepository.findByIdNumber(sellerIdNumber);
        PersonValidator.validatePersonExists(seller, sellerIdNumber);


        List<Product> products = new ArrayList<>();
        if (productIdentifiers != null) {
            for (String id : productIdentifiers) {
                Product p = productRepository.findByIdentifier(id);
                ProductValidator.validateProductExists(p, id);
                ProductValidator.validateStockAvailability(p, 1);
                products.add(p);
            }
        }
        if (accessoryIdentifiers != null) {
            for (String id : accessoryIdentifiers) {
                Accessory a = accessoryRepository.findByIdentifier(id);
                ProductValidator.validateProductExists(a, id);
                ProductValidator.validateStockAvailability(a, 1);
                products.add(a);
            }
        }

        SaleValidator.validateSaleData(client, seller, products);
        Sale sale = new Sale(code, client, seller, products);

        for (Product p : products) {
            if (p instanceof Console) {
                warrantyService.assignBasicWarranty(p, sale, LocalDate.now());
            }
        }

        applyBestPromotion(sale);

        double warrantyCost = 0;
        if (extendedWarrantyProductIds != null) {
            for (String id : extendedWarrantyProductIds) {
                Product warrantyProduct = null;
                for (Product p : products) {
                    if (p.getIdentifier().equals(id)) { warrantyProduct = p; break; }
                }
                if (warrantyProduct == null) {
                    throw new ResourceNotFoundException("producto (garantía extendida)", id);
                }
                ExtendedWarranty extendedWarranty = warrantyService.assignExtendedWarranty(warrantyProduct, sale, LocalDate.now());
                warrantyCost += extendedWarranty.getAdditionalCost();
            }
        }

        sale.setWarrantyCost(warrantyCost);
        sale.setTotal(sale.getTotal() + warrantyCost);

        // Recién aquí, con la venta completa ya construida sin errores, se toca el inventario
        for (Product p : products) {
            p.setAvailableQuantity(p.getAvailableQuantity() - 1);
            if (p instanceof Accessory) {
                accessoryRepository.update((Accessory) p);
            } else {
                productRepository.update(p);
            }
        }

        saleRepository.save(sale);
        client.addSale(sale);
        return sale;
    }

    public Sale findByCode(String code) {
        Sale sale = saleRepository.findByCode(code);
        SaleValidator.validateSaleExists(sale, code);
        return sale;
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
        SaleValidator.validateSaleExists(sale, code);
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

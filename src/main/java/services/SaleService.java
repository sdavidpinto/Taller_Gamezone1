
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

import model.Promotion;
import persistence.ClientRepository;

import persistence.SaleRepository;
import persistence.SellerRepository;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;
    
    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository,SellerRepository sellerRepository, ProductService productService,AccessoryService accessoryService, PromotionService promotionService,WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
        this.productService= productService;
        this.accessoryService=accessoryService;
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


        List<Product> products = resolveItems(productIdentifiers, accessoryIdentifiers);
        

        SaleValidator.validateSaleData(client, seller, products);
        Sale sale = new Sale(code, client, seller, products);

        
        assignBasicWarranties(sale);
        applyBestPromotion(sale);

        
        double warrantyCost = assignExtendedWarranties(sale, extendedWarrantyProductIds);
        sale.setWarrantyCost(warrantyCost);
        sale.setTotal(sale.getTotal() + warrantyCost);

        
        updateInventory(products);
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
            restoreStock(p);
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
    
    private List<Product> resolveItems(List<String> productIdentifiers, List<String> accessoryIdentifiers) {
        List<Product> products = new ArrayList<>();
        Map<String, Integer> requested = new HashMap<>();
        if (productIdentifiers != null) {
            for (String id : productIdentifiers) {
                Product p = productService.findByIdentifier(id);   // lanza ResourceNotFoundException si no existe
                int units = requested.merge("P:" + id, 1, Integer::sum);
                ProductValidator.validateStockAvailability(p, units);
                products.add(p);
            }
        }
        if (accessoryIdentifiers != null) {
            for (String id : accessoryIdentifiers) {
                Accessory a = accessoryService.findByIdentifier(id);
                ProductValidator.validateProductExists(a, id);
                int units = requested.merge("A:" + id, 1, Integer::sum);
                ProductValidator.validateStockAvailability(a, units);
                products.add(a);
            }
        }
        return products;
    }

    private void assignBasicWarranties(Sale sale) {
        for (Product p : sale.getProducts()) {
            if (p.requiresBasicWarranty()) {
                warrantyService.assignBasicWarranty(p, sale, LocalDate.now());
            }
        }
    }

    private double assignExtendedWarranties(Sale sale, List<String> extendedWarrantyProductIds) {
        double warrantyCost = 0;
        if (extendedWarrantyProductIds == null) {
            return warrantyCost;
        }
        for (String id : extendedWarrantyProductIds) {
            Product warrantyProduct = null;
            for (Product p : sale.getProducts()) {
                if (p.getIdentifier().equals(id)) { warrantyProduct = p; break; }
            }
            if (warrantyProduct == null) {
                throw new ResourceNotFoundException("producto (garantía extendida)", id);
            }
            ExtendedWarranty extendedWarranty = warrantyService.assignExtendedWarranty(warrantyProduct, sale, LocalDate.now());
            warrantyCost += extendedWarranty.getAdditionalCost();
        }
        return warrantyCost;
    }

    /**
     * Descuenta del inventario las unidades vendidas. Si un identificador
     * aparece varias veces en la venta, descuenta todas sus unidades de una
     * sola vez (cada copia del producto trae el mismo stock inicial, así que
     * descontar de a una pisaría la escritura anterior).
     */
    private void updateInventory(List<Product> products) {
        Map<String, Integer> units = new LinkedHashMap<>();
        Map<String, Product> firstOf = new HashMap<>();
        for (Product p : products) {
            String key = (p instanceof Accessory ? "A:" : "P:") + p.getIdentifier();
            units.merge(key, 1, Integer::sum);
            firstOf.putIfAbsent(key, p);
        }
        for (Map.Entry<String, Integer> e : units.entrySet()) {
            Product p = firstOf.get(e.getKey());
            int newStock = p.getAvailableQuantity() - e.getValue();
            if (p instanceof Accessory) {
                accessoryService.updateStock(p.getIdentifier(), newStock);
            } else {
                productService.updateStock(p.getIdentifier(), newStock);
            }
        }
    }

    private void restoreStock(Product p) {
        if (p instanceof Accessory) {
            accessoryService.restoreStock(p.getIdentifier(), 1);   // devuelve false si no existe
        } else {
            try {
                productService.restoreStock(p.getIdentifier(), 1);
            } catch (ResourceNotFoundException e) {
                // el producto fue eliminado del catálogo: no hay stock que restaurar
            }
        }
    }
    
}

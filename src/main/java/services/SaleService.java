
package services;

import model.Client;
import model.Product;
import model.Sale;
import model.Seller;
import persistence.ClientRepository;
import persistence.ProductRepository;
import persistence.SaleRepository;
import persistence.SellerRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public SaleService(SaleRepository saleRepository, ClientRepository clientRepository,
                        SellerRepository sellerRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.clientRepository = clientRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
    }

    /**
     * Registra una venta nueva a partir de identificadores (no de objetos
     * ya armados). Valida existencia de cliente/vendedor/productos y
     * disponibilidad de stock, y descuenta el stock vendido.
     */
    public Sale registerSale(String code, String clientIdNumber, String sellerIdNumber, List<String> productIdentifiers) {
        Client client = clientRepository.findByIdNumber(clientIdNumber);
        if (client == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + clientIdNumber);
        }

        Seller seller = sellerRepository.findByIdNumber(sellerIdNumber);
        if (seller == null) {
            throw new IllegalArgumentException("Vendedor no encontrado: " + sellerIdNumber);
        }

        List<Product> productos = new ArrayList<>();
        for (String id : productIdentifiers) {
            Product p = productRepository.findByIdentifier(id);
            if (p == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + id);
            }
            if (p.getAvailableQuantity() <= 0) {
                throw new IllegalStateException("Sin stock disponible: " + p.getTitle());
            }
            productos.add(p);
            p.setAvailableQuantity(p.getAvailableQuantity() - 1);
            productRepository.update(p);
        }

        Sale sale = new Sale(code, new Date(), client, seller, productos);
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
            Product actual = productRepository.findByIdentifier(p.getIdentifier());
            if (actual != null) {
                actual.setAvailableQuantity(actual.getAvailableQuantity() + 1);
                productRepository.update(actual);
            }
        }
        return saleRepository.deleteByCode(code);
    }
}

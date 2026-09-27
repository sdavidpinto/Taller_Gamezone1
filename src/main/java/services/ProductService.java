package services;

import model.Console;
import model.Product;
import model.VideoGame;
import persistence.ProductRepository;

import java.util.List;

/**
 * Capa de servicios para Product. Recibe la interfaz ProductRepository
 * por inyección de dependencias (constructor).
 * Como Product es abstracta, este service expone dos métodos de registro:
 * uno para VideoGame y otro para Console.
 */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void registerVideoGame(String identifier, String title, double price, int stock,
                                    String platform, String genre, String ageRating) {
        if (productRepository.findByIdentifier(identifier) != null) {
            throw new IllegalArgumentException("Ya existe un producto con identifier: " + identifier);
        }
        Product videoGame = new VideoGame(identifier, title, price, stock, platform, genre, ageRating);
        productRepository.save(videoGame);
    }

    public void registerConsole(String identifier, String title, double price, int stock,
                                  String brand, String model, String generation) {
        if (productRepository.findByIdentifier(identifier) != null) {
            throw new IllegalArgumentException("Ya existe un producto con identifier: " + identifier);
        }
        Product console = new Console(identifier, title, price, stock, brand, model, generation);
        productRepository.save(console);
    }

    public Product findByIdentifier(String identifier) {
        return productRepository.findByIdentifier(identifier);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public boolean updateStock(String identifier, int nuevoStock) {
        Product existente = productRepository.findByIdentifier(identifier);
        if (existente == null) {
            return false;
        }
        existente.setAvailableQuantity(nuevoStock);
        return productRepository.update(existente);
    }

    public boolean deleteProduct(String identifier) {
        return productRepository.deleteByIdentifier(identifier);
    }
    
    public boolean restoreStock(String identifier, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a restaurar debe ser mayor a 0.");
        }
        Product existente = productRepository.findByIdentifier(identifier);
        if (existente == null) {
            return false;
        }
        existente.setAvailableQuantity(existente.getAvailableQuantity() + quantity);
        return productRepository.update(existente);
    }
    
}
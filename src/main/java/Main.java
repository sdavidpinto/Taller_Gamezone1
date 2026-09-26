import java.io.File;
import persistence.AccessoryRepository;
import persistence.AccessoryRepositoryFile;
import persistence.ClientRepository;
import persistence.ClientRepositoryFile;
import persistence.ProductRepository;
import persistence.ProductRepositoryFile;
import persistence.SaleRepository;
import persistence.SaleRepositoryFile;
import persistence.SellerRepository;
import persistence.SellerRepositoryFile;
import persistence.PromotionRepository;
import persistence.PromotionRepositoryFile;
import persistence.WarrantyRepository;
import persistence.WarrantyRepositoryFile;
import services.PromotionService;
import services.ClientService;
import services.ProductService;
import services.AccessoryService;
import services.SaleService;
import services.SellerService;
import services.WarrantyService;
import ui.MenuUI;

public class Main {

    private static final String DATA_DIR = System.getProperty("user.dir") + File.separator + "data";

    private static String dataFile(String fileName) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return DATA_DIR + File.separator + fileName;
    }

    public static void main(String[] args) {

        // --- Capa persistence: repositorios en archivo ---
        ClientRepository clientRepository = new ClientRepositoryFile(dataFile("clients.txt"));
        SellerRepository sellerRepository = new SellerRepositoryFile(dataFile("sellers.txt"));
        ProductRepository productRepository = new ProductRepositoryFile(dataFile("products.txt"));
        AccessoryRepository accessoryRepository = new AccessoryRepositoryFile(dataFile("Accesories.txt"), productRepository);
        PromotionRepository promotionRepository = new PromotionRepositoryFile(dataFile("promotions.txt"));
        SaleRepository saleRepository = new SaleRepositoryFile(
                dataFile("sales.txt"),
                clientRepository::findByIdNumber,
                sellerRepository::findByIdNumber
        );
        WarrantyRepository warrantyRepository = new WarrantyRepositoryFile(dataFile("warranties.csv"));

        // --- Capa service: reciben los repositorios por constructor ---
        ClientService clientService = new ClientService(clientRepository);
        SellerService sellerService = new SellerService(sellerRepository);
        ProductService productService = new ProductService(productRepository);
        AccessoryService accesoryService = new AccessoryService(accessoryRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository, saleRepository, productService);
        SaleService saleService = new SaleService(saleRepository, clientRepository, sellerRepository, productRepository, accessoryRepository, promotionService, warrantyService);

        // --- Capa ui: recibe los services por constructor ---
        MenuUI menu = new MenuUI(clientService, sellerService, productService, saleService, accesoryService, promotionService, warrantyService);
        menu.start();
    }
}
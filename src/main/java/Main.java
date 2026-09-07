import java.io.File;
import persistence.ClientRepository;
import persistence.ClientRepositoryFile;
import persistence.ProductRepository;
import persistence.ProductRepositoryFile;
import persistence.SaleRepository;
import persistence.SaleRepositoryFile;
import persistence.SellerRepository;
import persistence.SellerRepositoryFile;
import services.ClientService;
import service.ProductService;
import services.SaleService;
import services.SellerService;
import ui.MenuUI;

/**
 * Punto de entrada del sistema GameZone.
 *
 * Esta clase NO tiene lógica de negocio ni de presentación: su única
 * responsabilidad es la inyección de dependencias por constructor,
 * es decir, construir cada capa e ir "enchufando" la de abajo dentro
 * de la de arriba:
 *
 *   persistence (repositorios)  -->  service (reglas de negocio)  -->  ui (JOptionPane)
 *
 * Cada capa solo conoce interfaces (SaleRepository, ClientRepository, etc.),
 * nunca implementaciones concretas. Por eso, para cambiar de persistencia
 * en archivo a persistencia en base de datos, bastaría con cambiar las
 * líneas de esta clase (ej. SaleRepositoryFile -> SaleRepositorySQL) sin
 * tocar ni una línea de service ni de ui.
 */
public class Main {

    /**
     * Carpeta donde viven todos los archivos .txt de persistencia.
     * Se resuelve contra el directorio de trabajo del proceso (user.dir),
     * que en un proyecto Maven ejecutado normalmente (mvn exec, o el IDE
     * configurado con working directory = raiz del proyecto) es la raiz
     * del proyecto, al mismo nivel que la carpeta src/.
     */
    private static final String DATA_DIR = System.getProperty("user.dir") + File.separator + "data";

    /**
     * Construye la ruta completa de un archivo de datos dentro de DATA_DIR,
     * creando la carpeta si todavia no existe.
     */
    private static String dataFile(String fileName) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return DATA_DIR + File.separator + fileName;
    }

    public static void main(String[] args) {

        // --- Capa persistence: repositorios en archivo ---
        // Las rutas se deciden aqui, en el punto de composicion; los
        // repositorios solo reciben el filePath ya resuelto (inyeccion de
        // configuracion, no solo de dependencias).
        ClientRepository clientRepository = new ClientRepositoryFile(dataFile("clients.txt"));
        SellerRepository sellerRepository = new SellerRepositoryFile(dataFile("sellers.txt"));
        ProductRepository productRepository = new ProductRepositoryFile(dataFile("products.txt"));
        SaleRepository saleRepository = new SaleRepositoryFile(
                dataFile("sales.txt"),
                clientRepository::findByIdNumber,
                sellerRepository::findByIdNumber
        );

        // --- Capa service: reciben los repositorios por constructor ---
        ClientService clientService = new ClientService(clientRepository);
        SellerService sellerService = new SellerService(sellerRepository);
        ProductService productService = new ProductService(productRepository);
        SaleService saleService = new SaleService(saleRepository, clientRepository, sellerRepository, productRepository);

        // --- Capa ui: recibe los services por constructor ---
        MenuUI menu = new MenuUI(clientService, sellerService, productService, saleService);
        menu.start();
    }
}


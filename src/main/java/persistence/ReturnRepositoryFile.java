package persistence;

import model.Product;
import model.Return;
import model.Sale;
import services.ProductService;
import services.SaleService;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ReturnRepository basada en archivos.
 * Almacena y recupera las devoluciones desde un archivo CSV. Como una
 * devolución hace referencia a una Sale y a una lista de Products, esta
 * clase depende de SaleService y ProductService para resolver esas
 * referencias al reconstruir las devoluciones desde el archivo, tal
 * como lo exigen los requisitos del examen.
 */
public class ReturnRepositoryFile implements ReturnRepository {

    private static final String PRODUCT_ID_SEPARATOR = ";";

    private final String filePath;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Crea una nueva instancia de ReturnRepositoryFile.
     *
     * @param filePath la ruta del archivo CSV usado para la persistencia
     * @param saleService usado para resolver las referencias a ventas por código
     * @param productService usado para resolver las referencias a productos por identificador
     */
    public ReturnRepositoryFile(String filePath, SaleService saleService, ProductService productService) {
        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
        createFileIfNotExists();
    }

    /**
     * Garantiza que el archivo de persistencia exista antes de usarlo.
     * Crea la carpeta padre (por ejemplo, "data/") si aún no existe.
     */
    private void createFileIfNotExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    @Override
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return returns;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    Return r = parseLine(line);
                    if (r != null) {
                        returns.add(r);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer las devoluciones desde: " + filePath, e);
        }
        return returns;
    }

    /**
     * Reconstruye un objeto Return a partir de una línea CSV, resolviendo la
     * venta asociada mediante SaleService y cada producto devuelto mediante
     * ProductService. Si la venta o alguno de los productos no puede
     * resolverse, la línea se descarta y se retorna null.
     *
     * @param line la línea CSV a parsear
     * @return la devolución reconstruida, o null si la venta o alguno de los
     *         productos referenciados en la línea no pudo resolverse
     */
    private Return parseLine(String line) {
        String[] parts = line.split(",", -1);
        String id = parts[0];
        LocalDate date = LocalDate.parse(parts[1]);
        String saleCode = parts[2];
        String productIdsField = parts[3];
        String reason = parts[4];

        Sale sale = saleService.findByCode(saleCode);
        if (sale == null) {
            return null;
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIdsField.split(PRODUCT_ID_SEPARATOR)) {
            Product product = productService.findByIdentifier(productId);
            if (product == null) {
                return null;
            }
            products.add(product);
        }

        return new Return(id, date, sale, products, reason);
    }

    @Override
    public void saveAll(List<Return> returns) {
        // TODO: siguiente commit
    }
}
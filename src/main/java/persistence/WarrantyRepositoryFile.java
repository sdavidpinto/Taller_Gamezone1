package persistence;

import model.BasicWarranty;
import model.ExtendedWarranty;
import model.Product;
import model.Sale;
import model.Warranty;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de WarrantyRepository basada en archivos.
 * Guarda y recupera garantías desde un archivo CSV, usando un discriminador
 * de tipo para distinguir entre BasicWarranty y ExtendedWarranty. Como una
 * garantía referencia a un Product y a un Sale, esta clase depende de
 * ProductRepository y SaleRepository para resolver esas referencias al
 * reconstruir garantías desde el archivo.
 */
public class WarrantyRepositoryFile implements WarrantyRepository {

    private final String filePath;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    /**
     * Crea un nuevo WarrantyRepositoryFile.
     *
     * @param filePath la ruta del archivo CSV usado para persistencia
     * @param productRepository usado para resolver referencias a productos por identificador
     * @param saleRepository usado para resolver referencias a ventas por código
     */
    public WarrantyRepositoryFile(String filePath, ProductRepository productRepository, SaleRepository saleRepository) {
        this.filePath = filePath;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Crea la carpeta contenedora (por ejemplo "data/") si aún no existe.
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

    /**
     * Carga todas las garantías desde el archivo CSV.
     * Retorna una lista vacía si el archivo no existe o está vacío.
     *
     * @return la lista de garantías cargadas desde el archivo
     */
    @Override
    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return warranties;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    Warranty warranty = parseLine(line);
                    if (warranty != null) {
                        warranties.add(warranty);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo garantías de: " + filePath, e);
        }
        return warranties;
    }

    /**
     * Reconstruye un objeto Warranty a partir de una línea CSV, usando el
     * discriminador de tipo para determinar la clase concreta, y resolviendo
     * el producto y la venta asociados a través de los repositorios inyectados.
     * Si el producto o la venta referenciados ya no se encuentran, la línea
     * se omite y se retorna null.
     *
     * @param line la línea CSV a interpretar
     * @return la garantía reconstruida, o null si el producto o la venta
     *         referenciados no pudieron resolverse
     * @throws IllegalStateException si el tipo indicado en la línea es desconocido
     */
    private Warranty parseLine(String line) {
        String[] parts = line.split(",");
        String type = parts[0];
        String id = parts[1];
        String productId = parts[2];
        String saleCode = parts[3];
        LocalDate startDate = LocalDate.parse(parts[4]);

        Product product = productRepository.findByIdentifier(productId);
        Sale sale = saleRepository.findByCode(saleCode);
        if (product == null || sale == null) {
            return null;
        }

        if (type.equals("BASIC")) {
            return new BasicWarranty(id, product, sale, startDate);
        } else if (type.equals("EXTENDED")) {
            return new ExtendedWarranty(id, product, sale, startDate);
        }
        throw new IllegalStateException("Tipo de garantía desconocido en el archivo: " + type);
    }  
}
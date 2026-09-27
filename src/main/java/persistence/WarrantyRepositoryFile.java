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
import java.util.function.Function;

/**
 * Implementación de WarrantyRepository basada en archivos.
 * Persiste únicamente los identificadores del producto y la venta
 * relacionados, y ya no depende de ProductRepository ni SaleRepository
 * al momento de construirse. Los objetos Product y Sale reales se
 * resuelven al momento de invocar loadAll(), a través de las funciones
 * de resolución recibidas, lo que rompe la dependencia circular que
 * existía antes (SaleService -> WarrantyService -> WarrantyRepository
 * -> SaleService).
 */
public class WarrantyRepositoryFile implements WarrantyRepository {

    private final String filePath;

    /**
     * Crea un nuevo WarrantyRepositoryFile.
     *
     * @param filePath la ruta del archivo CSV usado para persistencia
     */
    public WarrantyRepositoryFile(String filePath) {
        this.filePath = filePath;
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
     * Guarda la lista completa de garantías en el archivo CSV,
     * sobrescribiendo cualquier contenido anterior.
     *
     * @param warranties la lista de garantías a guardar
     */
    @Override
    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Warranty warranty : warranties) {
                bw.write(toLine(warranty));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error guardando garantías en: " + filePath, e);
        }
    }

    /**
     * Carga todas las garantías desde el archivo CSV, resolviendo el
     * producto y la venta de cada una mediante las funciones indicadas.
     * Retorna una lista vacía si el archivo no existe o está vacío.
     *
     * @param productResolver una función que busca un Product por su identificador
     * @param saleResolver una función que busca un Sale por su código
     * @return la lista de garantías cargadas desde el archivo
     */
    @Override
    public List<Warranty> loadAll(Function<String, Product> productResolver, Function<String, Sale> saleResolver) {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return warranties;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    Warranty warranty = parseLine(line, productResolver, saleResolver);
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
     * Convierte una garantía en una línea CSV, guardando solo los
     * identificadores del producto y la venta asociados.
     *
     * @param warranty la garantía a convertir
     * @return la línea CSV que representa la garantía
     * @throws IllegalArgumentException si el tipo de garantía no es soportado
     */
    private String toLine(Warranty warranty) {
        String type;
        if (warranty instanceof BasicWarranty) {
            type = "BASIC";
        } else if (warranty instanceof ExtendedWarranty) {
            type = "EXTENDED";
        } else {
            throw new IllegalArgumentException("Tipo de garantía no soportado para persistencia: " + warranty.getClass());
        }
        return String.join(",",
                type,
                warranty.getId(),
                warranty.getProduct().getIdentifier(),
                warranty.getSale().getCode(),
                warranty.getStartDate().toString());
    }

    /**
     * Reconstruye un objeto Warranty a partir de una línea CSV, resolviendo
     * el producto y la venta asociados mediante las funciones de resolución
     * indicadas, en lugar de repositorios inyectados. Si alguno no puede
     * resolverse, la línea se omite y se retorna null.
     *
     * @param line la línea CSV a interpretar
     * @param productResolver una función que busca un Product por su identificador
     * @param saleResolver una función que busca un Sale por su código
     * @return la garantía reconstruida, o null si el producto o la venta
     *         referenciados no pudieron resolverse
     * @throws IllegalStateException si el tipo indicado en la línea es desconocido
     */
    private Warranty parseLine(String line, Function<String, Product> productResolver, Function<String, Sale> saleResolver) {
        String[] parts = line.split(",");
        String type = parts[0];
        String id = parts[1];
        String productId = parts[2];
        String saleCode = parts[3];
        LocalDate startDate = LocalDate.parse(parts[4]);

        Product product = productResolver.apply(productId);
        Sale sale = saleResolver.apply(saleCode);
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
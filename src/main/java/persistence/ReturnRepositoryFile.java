package persistence;

import model.Accessory;
import model.Product;
import model.Return;
import model.Sale;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * File-based implementation of ReturnRepository. It stores returns in a CSV file.
 * A return references a sale and a list of items (products or accessories), so
 * the repository receives resolver functions to rebuild those references when
 * loading. It does not depend on the service layer.
 */
public class ReturnRepositoryFile implements ReturnRepository {

    private static final String PRODUCT_ID_SEPARATOR = ";";

    private final String filePath;
    private final Function<String, Sale> saleResolver;
    private final Function<String, Product> productResolver;
    private final Function<String, Accessory> accessoryResolver;

    /**
     * Creates a new ReturnRepositoryFile.
     *
     * @param filePath path of the CSV file used for persistence
     * @param saleResolver finds a sale by its code
     * @param productResolver finds a product by its identifier
     * @param accessoryResolver finds an accessory by its identifier
     */
    public ReturnRepositoryFile(String filePath, Function<String, Sale> saleResolver,
                                Function<String, Product> productResolver,
                                Function<String, Accessory> accessoryResolver) {
        this.filePath = filePath;
        this.saleResolver = saleResolver;
        this.productResolver = productResolver;
        this.accessoryResolver = accessoryResolver;
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
     * Guarda la lista completa de devoluciones en el archivo CSV,
     * sobrescribiendo cualquier contenido anterior.
     *
     * @param returns la lista de devoluciones a guardar
     */
    @Override
    public void saveAll(List<Return> returns) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Return r : returns) {
                bw.write(toLine(r));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error guardando devoluciones en: " + filePath, e);
        }
    }

    /**
     * Carga todas las devoluciones desde el archivo CSV.
     * Retorna una lista vacía si el archivo no existe o está vacío.
     *
     * @return la lista de devoluciones cargadas desde el archivo
     */
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
            throw new RuntimeException("Error leyendo devoluciones de: " + filePath, e);
        }
        return returns;
    }

    /**
     * Convierte una devolución en una línea CSV. Los productos devueltos
     * se guardan como una lista de identificadores separados por punto y
     * coma dentro de un solo campo CSV.
     *
     * @param r la devolución a convertir
     * @return la línea CSV que representa la devolución
     */
    private String toLine(Return r) {
        StringBuilder productIds = new StringBuilder();
        for (Product product : r.getReturnedProducts()) {
            if (productIds.length() > 0) {
                productIds.append(PRODUCT_ID_SEPARATOR);
            }
            productIds.append(product.getIdentifier());
        }
        return String.join(",",
                r.getId(),
                r.getDate().toString(),
                r.getOriginalSale().getCode(),
                productIds.toString(),
                r.getReason(),
                String.valueOf(r.getWarrantyRefundAmount()));
    }

    /**
     * Rebuilds a Return from a CSV line, resolving the sale through the sale
     * resolver and each returned item through resolveItem (products first, then
     * accessories). If the sale or any item cannot be resolved the line is
     * skipped and null is returned.
     *
     * @param line the CSV line to parse
     * @return the rebuilt return, or null if the sale or an item could not be resolved
     */
    private Return parseLine(String line) {
        String[] parts = line.split(",", -1);
        String id = parts[0];
        LocalDate date = LocalDate.parse(parts[1]);
        String saleCode = parts[2];
        String productIdsField = parts[3];
        String reason = parts[4];

        Sale sale = saleResolver.apply(saleCode);
        if (sale == null) {
            return null;
        }

        List<Product> items = new ArrayList<>();
        for (String itemId : productIdsField.split(PRODUCT_ID_SEPARATOR)) {
            Product item = resolveItem(itemId);
            if (item == null) {
                return null;
            }
            items.add(item);
        }

        Return r = new Return(id, date, sale, items, reason);
        String warrantyRefundField = parts.length > 5 ? parts[5] : "0";
        double warrantyRefund = warrantyRefundField.isBlank() ? 0 : Double.parseDouble(warrantyRefundField);
        if (warrantyRefund > 0) {
            r.addWarrantyRefund(warrantyRefund);
        }
        return r;
    }

    /**
     * Resolves an item identifier as a Product or, if not found, as an Accessory.
     *
     * @param itemId the identifier to resolve
     * @return the Product or Accessory found, or null if neither resolver finds it
     */
    private Product resolveItem(String itemId) {
        Product product = productResolver.apply(itemId);
        if (product != null) {
            return product;
        }
        return accessoryResolver.apply(itemId);
    }
}
package persistence;

import model.Product;
import model.Return;
import model.Sale;
import services.AccessoryService;
import services.ProductService;
import services.SaleService;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ReturnRepository basada en archivos.
 * Guarda y recupera devoluciones desde un archivo CSV. Como una devolución
 * referencia una venta y una lista de ítems (productos o accesorios), esta
 * clase depende de SaleService, ProductService y AccessoryService para
 * resolver esas referencias al reconstruir devoluciones desde el archivo.
 */
public class ReturnRepositoryFile implements ReturnRepository {

    private static final String PRODUCT_ID_SEPARATOR = ";";

    private final String filePath;
    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Crea un nuevo ReturnRepositoryFile.
     *
     * @param filePath la ruta del archivo CSV usado para persistencia
     * @param saleService usado para resolver referencias a ventas por código
     * @param productService usado para resolver referencias a productos por identificador
     * @param accessoryService usado para resolver referencias a accesorios por identificador
     */
    public ReturnRepositoryFile(String filePath, SaleService saleService,
                                 ProductService productService, AccessoryService accessoryService) {
        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
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
     * Reconstruye un objeto Return a partir de una línea CSV, resolviendo
     * la venta asociada mediante SaleService y cada ítem devuelto mediante
     * resolveItem (que revisa productos y accesorios). Si la venta o algún
     * ítem no pueden resolverse, la línea se omite y se retorna null.
     *
     * @param line la línea CSV a interpretar
     * @return la devolución reconstruida, o null si la venta o algún ítem
     *         referenciados en la línea no pudieron resolverse
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
        return new Return(id, date, sale, items, reason);
    }

    /**
     * Resuelve un identificador de ítem como Product o como Accessory,
     * revisando primero ProductService y, si no lo encuentra, buscando
     * en AccessoryService.
     *
     * @param itemId el identificador a resolver
     * @return el Product o Accessory encontrado, o null si no existe en ninguno
     */
    private Product resolveItem(String itemId) {
        Product product = productService.findByIdentifier(itemId);
        if (product != null) {
            return product;
        }
        return accessoryService.findByIdentifier(itemId);
    }
}
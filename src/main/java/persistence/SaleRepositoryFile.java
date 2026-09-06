package persistence;

import model.Client;
import model.Product;
import model.Sale;
import model.Seller;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementación de SaleRepository que guarda y lee las ventas en un
 * archivo de texto, usando el formato producido por Sale.Display():
 *
 *  
 * cada venta separada de la siguiente por una línea de guiones.
 *
 * LIMITACIÓN: el formato de texto solo guarda de cada producto el título
 * y el precio (no su identificador real, cantidad, ni si es VideoGame o
 * Console). Por eso este repositorio recibe dos funciones de búsqueda
 * (clientFinder, sellerFinder) para reconstruir el Client y el Seller
 * reales a partir del idNumber leído; y reconstruye cada Product como
 * una versión simplificada (ProductoSimple) que conserva solo título y
 * precio.
 */
public class SaleRepositoryFile implements SaleRepository {

    private static final String SEPARADOR = "--------------------------";
    private static final Pattern PRODUCTO_PATTERN = Pattern.compile("^\\s*-\\s*(.+?)\\s*\\(\\$(.+?)\\)\\s*$");

    private final String filePath;
    private final Function<String, Client> clientFinder; // recibe idNumber
    private final Function<String, Seller> sellerFinder; // recibe idNumber

    /**
     * @param filePath     ruta del archivo de ventas
     * @param clientFinder función que, dado un idNumber de cliente, devuelve el Client real
     * @param sellerFinder función que, dado un idNumber de vendedor, devuelve el Seller real
     */
    public SaleRepositoryFile(String filePath, Function<String, Client> clientFinder, Function<String, Seller> sellerFinder) {
        this.filePath = filePath;
        this.clientFinder = clientFinder;
        this.sellerFinder = sellerFinder;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Si la carpeta contenedora (por ejemplo "data/") no existe todavia, la crea,
     * para que no falle al intentar crear el archivo dentro de ella.
     */
    private void createFileIfNotExists() {
        File archivo = new File(filePath);
        File parent = archivo.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    @Override
    public void save(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        if (findByCode(sale.getCode()) != null) {
            throw new IllegalArgumentException("Ya existe una venta con el código: " + sale.getCode());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(sale.Display());
            bw.newLine();
            bw.write(SEPARADOR);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando en el archivo: " + filePath, e);
        }
    }

    @Override
    public Sale findByCode(String code) {
        for (Sale sale : findAll()) {
            if (sale.getCode() != null && sale.getCode().equals(code)) {
                return sale;
            }
        }
        return null;
    }

    @Override
    public List<Sale> findAll() {
        List<Sale> ventas = new ArrayList<>();
        List<String> bloqueActual = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().startsWith("---")) {
                    if (!bloqueActual.isEmpty()) {
                        ventas.add(parseBlock(bloqueActual));
                        bloqueActual = new ArrayList<>();
                    }
                } else if (!linea.isBlank()) {
                    bloqueActual.add(linea);
                }
            }
            if (!bloqueActual.isEmpty()) {
                ventas.add(parseBlock(bloqueActual));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo: " + filePath, e);
        }

        return ventas;
    }

    /**
     * Edita una venta existente: reescribe todo el archivo reemplazando
     * el bloque cuyo código coincida por el de la venta actualizada.
     */
    @Override
    public boolean update(Sale sale) {
        List<Sale> ventas = findAll();
        boolean encontrada = false;

        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getCode().equals(sale.getCode())) {
                ventas.set(i, sale);
                encontrada = true;
                break;
            }
        }

        if (encontrada) {
            rewriteFile(ventas);
        }
        return encontrada;
    }

    @Override
    public boolean deleteByCode(String code) {
        List<Sale> ventas = findAll();
        boolean eliminada = ventas.removeIf(s -> s.getCode() != null && s.getCode().equals(code));
        if (eliminada) {
            rewriteFile(ventas);
        }
        return eliminada;
    }

    private void rewriteFile(List<Sale> ventas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Sale venta : ventas) {
                bw.write(venta.Display());
                bw.newLine();
                bw.write(SEPARADOR);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reescribiendo el archivo: " + filePath, e);
        }
    }

    // --- Parsing de un bloque de líneas a Sale ---

    private Sale parseBlock(List<String> lineas) {
        String code = null;
        LocalDate date = null;
        String clientId = null;
        String sellerId = null;
        double total = 0;
        List<Product> productos = new ArrayList<>();

        for (String linea : lineas) {
            if (linea.startsWith("code:")) {
                code = linea.substring("code:".length()).trim();
            } else if (linea.startsWith("Sale date:")) {
                date = LocalDate.parse(linea.substring("Sale date:".length()).trim());
            } else if (linea.startsWith("Client:")) {
                clientId = linea.substring("Client:".length()).trim();
            } else if (linea.startsWith("Seller:")) {
                sellerId = linea.substring("Seller:".length()).trim();
            } else if (linea.startsWith("Products:")) {
                // solo marca el inicio de la lista
            } else if (linea.trim().startsWith("Total:")) {
                total = Double.parseDouble(linea.substring(linea.indexOf('$') + 1).trim());
            } else {
                Matcher m = PRODUCTO_PATTERN.matcher(linea);
                if (m.matches()) {
                    String titulo = m.group(1);
                    double precio = Double.parseDouble(m.group(2));
                    productos.add(new SimpleProduct(titulo, precio));
                }
            }
        }

        Client client = clientFinder.apply(clientId);
        Seller seller = sellerFinder.apply(sellerId);
        if (client == null) {
            throw new IllegalStateException("No se encontró un Client con idNumber: " + clientId);
        }
        if (seller == null) {
            throw new IllegalStateException("No se encontró un Seller con idNumber: " + sellerId);
        }

        Sale sale = new Sale(code, java.sql.Date.valueOf(date), client, seller, productos);
        sale.setTotal(total); // se restaura el total tal como estaba guardado en el archivo
        return sale;
    }

    /**
     * Versión simplificada de Product usada solo para reconstruir productos
     * leídos de texto, donde únicamente se conservan título y precio.
     */
  private static class SimpleProduct extends Product {
    SimpleProduct(String title, double price) {
        super(title, title, price, 0);
    }
    @Override
    public String getDescription() {
        return getTitle() + " - $" + getPrice();
    }
 }
    }
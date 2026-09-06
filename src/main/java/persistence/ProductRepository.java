package persistence;

import model.Console;
import model.Product;
import model.VideoGame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ProductRepository que guarda y lee los productos en un
 * archivo de texto, una línea por producto, en formato clave-valor.
 * Como Product es abstracta, cada línea incluye "type" para saber si
 * reconstruir un VideoGame o un Console.
 */
public class ProductRepositoryFile implements ProductRepository {

    private final String filePath;

    public ProductRepositoryFile(String filePath) {
        this.filePath = filePath;
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
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        if (findByIdentifier(product.getIdentifier()) != null) {
            throw new IllegalArgumentException("Ya existe un producto con identifier: " + product.getIdentifier());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(product));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando producto en: " + filePath, e);
        }
    }

    @Override
    public Product findByIdentifier(String identifier) {
        for (Product p : findAll()) {
            if (p.getIdentifier() != null && p.getIdentifier().equals(identifier)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Product> findAll() {
        List<Product> productos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    productos.add(parseLine(linea));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo productos de: " + filePath, e);
        }
        return productos;
    }

    @Override
    public boolean update(Product product) {
        List<Product> productos = findAll();
        boolean encontrado = false;
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getIdentifier().equals(product.getIdentifier())) {
                productos.set(i, product);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            rewriteFile(productos);
        }
        return encontrado;
    }

    @Override
    public boolean deleteByIdentifier(String identifier) {
        List<Product> productos = findAll();
        boolean eliminado = productos.removeIf(p -> p.getIdentifier() != null && p.getIdentifier().equals(identifier));
        if (eliminado) {
            rewriteFile(productos);
        }
        return eliminado;
    }

    private void rewriteFile(List<Product> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Product p : productos) {
                bw.write(toLine(p));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reescribiendo productos en: " + filePath, e);
        }
    }

    private String toLine(Product p) {
        if (p instanceof VideoGame) {
            VideoGame v = (VideoGame) p;
            return "type: VideoGame"
                    + "; identifier: " + v.getIdentifier()
                    + "; title: " + v.getTitle()
                    + "; price: " + v.getPrice()
                    + "; stock: " + v.getAvailableQuantity()
                    + "; platform: " + v.getPlatform()
                    + "; genre: " + v.getGenre()
                    + "; ageRating: " + v.getAgeRating();
        } else if (p instanceof Console) {
            Console c = (Console) p;
            return "type: Console"
                    + "; identifier: " + c.getIdentifier()
                    + "; title: " + c.getTitle()
                    + "; price: " + c.getPrice()
                    + "; stock: " + c.getAvailableQuantity()
                    + "; brand: " + c.getBrand()
                    + "; model: " + c.getModel()
                    + "; generation: " + c.getGeneration();
        }
        throw new IllegalArgumentException("Tipo de producto no soportado para persistencia: " + p.getClass());
    }

    private Product parseLine(String linea) {
        String[] partes = linea.split(";");
        String type = value(partes[0]);
        String identifier = value(partes[1]);
        String title = value(partes[2]);
        double price = Double.parseDouble(value(partes[3]));
        int stock = Integer.parseInt(value(partes[4]));

        if (type.equals("VideoGame")) {
            String platform = value(partes[5]);
            String genre = value(partes[6]);
            String ageRating = value(partes[7]);
            return new VideoGame(identifier, title, price, stock, platform, genre, ageRating);
        } else if (type.equals("Console")) {
            String brand = value(partes[5]);
            String model = value(partes[6]);
            String generation = value(partes[7]);
            return new Console(identifier, title, price, stock, brand, model, generation);
        }
        throw new IllegalStateException("Tipo de producto desconocido en archivo: " + type);
    }

    private String value(String parte) {
        return parte.split(":", 2)[1].trim();
    }
}
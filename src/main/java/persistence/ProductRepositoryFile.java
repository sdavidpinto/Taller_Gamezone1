package persistence;

import exceptions.PersistenceException;
import exceptions.InvalidDataException;
import exceptions.BusinessRuleException;

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

    /**
     * Crea un repositorio de productos respaldado por el archivo indicado.
     *
     * @param filePath ruta del archivo de persistencia
     * @throws PersistenceException si el archivo no puede crearse
     */
    public ProductRepositoryFile(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Si la carpeta contenedora (por ejemplo "data/") no existe todavía, la crea,
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
                throw new PersistenceException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    /**
     * Guarda un producto en el archivo de persistencia.
     *
     * @param product producto que se desea guardar
     * @throws InvalidDataException si el producto es nulo o no es compatible
     * @throws BusinessRuleException si el producto ya existe
     * @throws PersistenceException si ocurre un error de escritura
     */
    @Override
    public void save(Product product) {
        if (product == null) {
            throw new InvalidDataException("producto", "no puede ser nulo.");
        }
        if (findByIdentifier(product.getIdentifier()) != null) {
            throw new BusinessRuleException("Ya existe un producto con identifier: " + product.getIdentifier());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(product));
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenceException("Error guardando producto en: " + filePath, e);
        }
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param identifier identificador único del producto
     * @return producto encontrado o {@code null} si no existe
     * @throws PersistenceException si ocurre un error de lectura
     */
    @Override
    public Product findByIdentifier(String identifier) {
        for (Product p : findAll()) {
            if (p.getIdentifier() != null && p.getIdentifier().equals(identifier)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los productos almacenados.
     *
     * @return lista de productos almacenados
     * @throws PersistenceException si ocurre un error de lectura
     */
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
            throw new PersistenceException("Error leyendo productos de: " + filePath, e);
        }
        return productos;
    }

    /**
     * Actualiza un producto existente.
     *
     * @param product producto con los datos actualizados
     * @return {@code true} si fue actualizado; {@code false} si no existe
     * @throws PersistenceException si ocurre un error de escritura
     */
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

    /**
     * Elimina un producto por su identificador.
     *
     * @param identifier identificador del producto
     * @return {@code true} si fue eliminado; {@code false} si no existe
     * @throws PersistenceException si ocurre un error de escritura
     */
    @Override
    public boolean deleteByIdentifier(String identifier) {
        List<Product> productos = findAll();
        boolean eliminado = productos.removeIf(p -> p.getIdentifier() != null && p.getIdentifier().equals(identifier));
        if (eliminado) {
            rewriteFile(productos);
        }
        return eliminado;
    }

    /**
     * Reescribe el archivo con la lista de productos indicada.
     *
     * @param productos productos que se escribirán
     * @throws PersistenceException si ocurre un error de escritura
     */
    private void rewriteFile(List<Product> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Product p : productos) {
                bw.write(toLine(p));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Error reescribiendo productos en: " + filePath, e);
        }
    }

    /**
     * Convierte un producto al formato de almacenamiento.
     *
     * @param p producto que se desea convertir
     * @return representación del producto en una línea
     * @throws InvalidDataException si el tipo de producto no es compatible
     */
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
        throw new InvalidDataException("tipo", "Tipo de producto no soportado para persistencia: " + p.getClass());
    }

    /**
     * Reconstruye un producto desde una línea del archivo.
     *
     * @param linea línea con los datos del producto
     * @return producto reconstruido
     * @throws PersistenceException si el tipo almacenado es desconocido
     */
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
        throw new PersistenceException("Tipo de producto desconocido en archivo: " + type,
        new IllegalArgumentException("Tipo no soportado: " + type));
    }

    /**
     * Extrae el valor de una sección con formato clave-valor.
     *
     * @param parte sección que contiene la clave y el valor
     * @return valor extraído
     */
    private String value(String parte) {
        return parte.split(":", 2)[1].trim();
    }
}

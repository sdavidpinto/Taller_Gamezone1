package ui;

import model.Console;
import model.Product;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.ProductService;

/**
 * Submenú de productos. Recibe solo los servicios que necesita.
 */
public class ProductMenu extends SubMenu {

    private final ProductService productService;

    public ProductMenu(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void show() {
        String menu = "=== Productos ===\n"
                + "1. Registrar VideoGame\n"
                + "2. Registrar Console\n"
                + "3. Buscar por identifier\n"
                + "4. Listar todos\n"
                + "5. Actualizar stock\n"
                + "6. Eliminar\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Productos", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerVideoGame();
            case 2 -> registerConsole();
            case 3 -> searchProduct();
            case 4 -> listProducts();
            case 5 -> updateStock();
            case 6 -> deleteProduct();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerVideoGame() {
        String title = askRequiredText("Título:");
        if (title == null) return;
        Double price = askDouble("Precio:");
        if (price == null) return;
        Integer stock = askInt("Stock:");
        if (stock == null) return;
        String platform = askRequiredText("Plataforma:");
        if (platform == null) return;
        String genre = askRequiredText("Género:");
        if (genre == null) return;
        String ageRating = askRequiredText("Clasificación de edad:");
        if (ageRating == null) return;

        boolean registrado = false;
        do {
            String identifier = askRequiredText("Identificador:");
            if (identifier == null) return;
            try {
                productService.registerVideoGame(identifier, title, price, stock, platform, genre, ageRating);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro identificador.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "VideoGame registrado con éxito.");
    }

    private void registerConsole() {
        String title = askRequiredText("Título:");
        if (title == null) return;
        Double price = askDouble("Precio:");
        if (price == null) return;
        Integer stock = askInt("Stock:");
        if (stock == null) return;
        String brand = askRequiredText("Marca:");
        if (brand == null) return;
        String model = askRequiredText("Modelo:");
        if (model == null) return;
        String generation = askRequiredText("Generación:");
        if (generation == null) return;

        boolean registrado = false;
        do {
            String identifier = askRequiredText("Identificador:");
            if (identifier == null) return;
            try {
                productService.registerConsole(identifier, title, price, stock, brand, model, generation);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro identificador.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Console registrada con éxito.");
    }

    private void searchProduct() {
        String identifier = JOptionPane.showInputDialog("Identifier del producto a buscar:");
        Product p = productService.findByIdentifier(identifier);
        JOptionPane.showMessageDialog(null, p != null ? p.getDescription() : "No se encontró el producto.");
    }

    private void listProducts() {
        List<Product> productos = productService.findAll();
        JTextArea salida = new JTextArea(20, 60);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("identifier\tdescription\n");
        for (Product p : productos) {
            salida.append(p.getIdentifier() + "\t" + p.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void updateStock() {
        String identifier;
        do {
            identifier = askRequiredText("Identifier del producto:");
            if (identifier == null) return;
            if (productService.findByIdentifier(identifier) == null) {
                JOptionPane.showMessageDialog(null, "No existe un producto con ese identifier.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (productService.findByIdentifier(identifier) == null);

        Integer nuevoStock = askInt("Nuevo stock:");
        if (nuevoStock == null) return;

        productService.updateStock(identifier, nuevoStock);
        JOptionPane.showMessageDialog(null, "Stock actualizado.");
    }

    private void deleteProduct() {
        String identifier = JOptionPane.showInputDialog("Identifier del producto a eliminar:");
        boolean ok = productService.deleteProduct(identifier);
        JOptionPane.showMessageDialog(null, ok ? "Producto eliminado." : "No se encontró el producto.");
    }
}
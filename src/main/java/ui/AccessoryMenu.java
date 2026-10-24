package ui;

import model.Accessory;
import model.Cable;
import model.Console;
import model.Controller;
import model.Memory;
import model.Product;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.ProductService;
import services.AccessoryService;

/**
 * Submenú de accesorios. Recibe solo los servicios que necesita.
 */
public class AccessoryMenu extends SubMenu {

    private final ProductService productService;
    private final AccessoryService accessoryService;

    public AccessoryMenu(ProductService productService, AccessoryService accessoryService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    @Override
    public void show() {
    String menu = "=== Accesorios ===\n"
            + "1. Registrar Control\n"
            + "2. Registrar Cable\n"
            + "3. Registrar Memoria\n"
            + "4. Listar todos\n"
            + "5. Listar por tipo\n"
            + "6. Consultar compatibles con una consola (por marca)\n"
            + "7. Agregar producto compatible a un accesorio\n"
            + "8. Listar accesorios compatibles con un producto (específico)\n"
            + "0. Volver";
    String entrada = JOptionPane.showInputDialog(null, menu, "Accesorios", JOptionPane.PLAIN_MESSAGE);
    if (entrada == null) return;

    switch (parseOption(entrada)) {
        case 1 -> registerController();
        case 2 -> registerCable();
        case 3 -> registerMemory();
        case 4 -> listAllAccessories();
        case 5 -> listAccessoriesByType();
        case 6 -> consultCompatibleAccessories();
        case 7 -> addCompatibleProduct();
        case 8 -> listCompatibleAccessories();
        case 0 -> { /* volver */ }
        default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
    }
}

    private void registerController() {
        String title = askRequiredText("Título:");
        if (title == null) return;
        Double price = askDouble("Precio:");
        if (price == null) return;
        Integer stock = askInt("Stock:");
        if (stock == null) return;
        String brand = askRequiredText("Marca:");
        if (brand == null) return;
        Boolean alambric = askYesNo("¿Es alámbrico?");
        if (alambric == null) return;

        boolean registrado = false;
        do {
            String identifier = askRequiredText("Identificador:");
            if (identifier == null) return;
            try {
                accessoryService.registerAccessory(new Controller(identifier, title, price, stock, brand, alambric));
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro identificador.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Control registrado con éxito.");
    }

    private void registerCable() {
        String title = askRequiredText("Título:");
        if (title == null) return;
        Double price = askDouble("Precio:");
        if (price == null) return;
        Integer stock = askInt("Stock:");
        if (stock == null) return;
        String brand = askRequiredText("Marca:");
        if (brand == null) return;
        String connectionType = askRequiredText("Tipo de conexión:");
        if (connectionType == null) return;
        Integer length = askInt("Longitud (cm):");
        if (length == null) return;

        boolean registrado = false;
        do {
            String identifier = askRequiredText("Identificador:");
            if (identifier == null) return;
            try {
                accessoryService.registerAccessory(new Cable(identifier, title, price, stock, brand, connectionType, length));
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro identificador.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Cable registrado con éxito.");
    }

    private void registerMemory() {
        String title = askRequiredText("Título:");
        if (title == null) return;
        Double price = askDouble("Precio:");
        if (price == null) return;
        Integer stock = askInt("Stock:");
        if (stock == null) return;
        String brand = askRequiredText("Marca:");
        if (brand == null) return;
        String memoryType = askRequiredText("Tipo de memoria:");
        if (memoryType == null) return;
        Integer storage = askInt("Capacidad (GB):");
        if (storage == null) return;

        boolean registrado = false;
        do {
            String identifier = askRequiredText("Identificador:");
            if (identifier == null) return;
            try {
                accessoryService.registerAccessory(new Memory(identifier, title, price, stock, brand, memoryType, storage));
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro identificador.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Memoria registrada con éxito.");
    }

    private void listAllAccessories() {
        List<Accessory> accesorios = accessoryService.findAll();
        JTextArea salida = new JTextArea(20, 60);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("identifier\tdescription\n");
        for (Accessory a : accesorios) {
            salida.append(a.getIdentifier() + "\t" + a.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void listAccessoriesByType() {
        String type = askRequiredText("Tipo (Controller / Cable / Memory):");
        if (type == null) return;

        List<Accessory> accesorios = accessoryService.findByType(type);
        if (accesorios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay accesorios de tipo: " + type);
            return;
        }
        JTextArea salida = new JTextArea(20, 60);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("identifier\tdescription\n");
        for (Accessory a : accesorios) {
            salida.append(a.getIdentifier() + "\t" + a.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void consultCompatibleAccessories() {
        String consoleIdentifier = askRequiredText("Identifier de la consola:");
        if (consoleIdentifier == null) return;

        Product producto = productService.findByIdentifier(consoleIdentifier);
        if (!(producto instanceof Console consola)) {
            JOptionPane.showMessageDialog(null, "No existe una consola con ese identifier.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Accessory> compatibles = accessoryService.findCompatibleWithConsoleBrand(consola.getBrand());
        if (compatibles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay accesorios compatibles con la marca: " + consola.getBrand());
            return;
        }
        JTextArea salida = new JTextArea(20, 60);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("identifier\tdescription\n");
        for (Accessory a : compatibles) {
            salida.append(a.getIdentifier() + "\t" + a.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }
    private void addCompatibleProduct() {
        String accessoryId = askRequiredText("Identifier del accesorio:");
        if (accessoryId == null) return;

        if (accessoryService.findByIdentifier(accessoryId) == null) {
            JOptionPane.showMessageDialog(null, "No existe un accesorio con ese identifier.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productId = askRequiredText("Identifier del producto compatible (ej. una consola):");
        if (productId == null) return;

        Product product = productService.findByIdentifier(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(null, "No existe un producto con ese identifier.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            accessoryService.addCompatibleProduct(accessoryId, product);
            JOptionPane.showMessageDialog(null, "Producto agregado como compatible correctamente.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void listCompatibleAccessories() {
        String productId = askRequiredText("Identifier del producto (ej. una consola):");
        if (productId == null) return;

        if (productService.findByIdentifier(productId) == null) {
            JOptionPane.showMessageDialog(null, "No existe un producto con ese identifier.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Accessory> compatibles = accessoryService.findAccessoriesCompatibleWith(productId);
        if (compatibles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay accesorios compatibles con ese producto.");
            return;
        }
        JTextArea salida = new JTextArea(20, 60);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("identifier\tdescription\n");
        for (Accessory a : compatibles) {
            salida.append(a.getIdentifier() + "\t" + a.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }
}
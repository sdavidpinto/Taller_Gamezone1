package ui;

import model.Client;
import model.Product;
import model.Sale;
import model.Seller;
import services.ClientService;
import service.ProductService;
import services.SaleService;
import services.SellerService;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.Arrays;
import java.util.List;

/**
 * Capa de presentación (UI) con JOptionPane.
 *
 * Esta clase NO conoce los repositorios ni el formato de persistencia:
 * solo recibe los 4 Services ya construidos (inyección de dependencias
 * por constructor) y los llama para cumplir cada opción del menú.
 * Si mañana cambia cómo se guardan los datos (archivo, SQL, memoria),
 * esta clase no se entera ni se modifica.
 */
public class MenuUI {

    private final ClientService clientService;
    private final SellerService sellerService;
    private final ProductService productService;
    private final SaleService saleService;

    public MenuUI(ClientService clientService, SellerService sellerService,
                   ProductService productService, SaleService saleService) {
        this.clientService = clientService;
        this.sellerService = sellerService;
        this.productService = productService;
        this.saleService = saleService;
    }

    /**
     * Inicia el ciclo del menú principal. Se queda corriendo hasta que
     * el usuario elige "Salir".
     */
    public void start() {
        int opcion;
        do {
            String menuPrincipal = "=== GameZone ===\n"
                    + "1. Clientes\n"
                    + "2. Vendedores\n"
                    + "3. Productos\n"
                    + "4. Ventas\n"
                    + "0. Salir";
            String entrada = JOptionPane.showInputDialog(null, menuPrincipal, "Menú principal", JOptionPane.PLAIN_MESSAGE);

            if (entrada == null) {
                break; // el usuario cerró el diálogo
            }

            opcion = parseOption(entrada);

            switch (opcion) {
                case 1 -> clientsMenu();
                case 2 -> sellersMenu();
                case 3 -> productsMenu();
                case 4 -> salesMenu();
                case 0 -> JOptionPane.showMessageDialog(null, "Hasta luego.");
                default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        } while (opcion != 0);
    }

    private int parseOption(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ================= CLIENTES =================

    private void clientsMenu() {
        String menu = "=== Clientes ===\n"
                + "1. Registrar\n"
                + "2. Buscar por ID\n"
                + "3. Listar todos\n"
                + "4. Actualizar\n"
                + "5. Eliminar\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Clientes", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerClient();
            case 2 -> searchClient();
            case 3 -> listClients();
            case 4 -> updateClient();
            case 5 -> deleteClient();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    /**
     * Pide un texto obligatorio repitiendo la pregunta mientras venga vacío
     * o en blanco. Si el usuario cancela el diálogo, se interrumpe todo el
     * registro devolviendo null (se revisa en cada método que llama esto).
     */
    private String askRequiredText(String mensaje) {
        String valor;
        do {
            valor = JOptionPane.showInputDialog(mensaje);
            if (valor == null) {
                return null; // el usuario canceló el diálogo
            }
            if (valor.isBlank()) {
                JOptionPane.showMessageDialog(null, "Este campo no puede quedar vacío.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (valor.isBlank());
        return valor;
    }

    /**
     * Pide un número decimal repitiendo la pregunta mientras el texto
     * ingresado no se pueda convertir a double. Devuelve null si el
     * usuario cancela.
     */
    private Double askDouble(String mensaje) {
        while (true) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                return null;
            }
            try {
                return Double.parseDouble(texto.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa un número válido (ej. 59.99).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Pide un número entero repitiendo la pregunta mientras el texto
     * ingresado no se pueda convertir a int. Devuelve null si el usuario
     * cancela.
     */
    private Integer askInt(String mensaje) {
        while (true) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                return null;
            }
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa un número entero válido.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void registerClient() {
        String name = askRequiredText("Nombre:");
        if (name == null) return;
        String phone = askRequiredText("Teléfono:");
        if (phone == null) return;
        String email = askRequiredText("Email:");
        if (email == null) return;

        // Este do-while es distinto: no valida formato, valida una regla
        // de negocio (ID duplicado) que solo el service conoce. Por eso
        // reintenta atrapando la excepción que lanza clientService.
        boolean registrado = false;
        do {
            String idNumber = askRequiredText("Identificación:");
            if (idNumber == null) return;
            try {
                clientService.registerClient(name, idNumber, phone, email);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otra identificación.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Cliente registrado con éxito.");
    }

    private void searchClient() {
        String idNumber = JOptionPane.showInputDialog("ID del cliente a buscar:");
        Client c = clientService.findByIdNumber(idNumber);
        JOptionPane.showMessageDialog(null, c != null ? c.display() : "No se encontró el cliente.");
    }

    private void listClients() {
        List<Client> clientes = clientService.findAll();
        JTextArea salida = new JTextArea(20, 50);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("idNumber\tname\tphone\temail\n");
        for (Client c : clientes) {
            salida.append(c.getIdNumber() + "\t" + c.getName() + "\t" + c.getPhone() + "\t" + c.getEmail() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void updateClient() {
        String idNumber;
        do {
            idNumber = askRequiredText("ID del cliente a actualizar:");
            if (idNumber == null) return;
            if (clientService.findByIdNumber(idNumber) == null) {
                JOptionPane.showMessageDialog(null, "No existe un cliente con ese ID.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (clientService.findByIdNumber(idNumber) == null);

        String name = askRequiredText("Nuevo nombre:");
        if (name == null) return;
        String phone = askRequiredText("Nuevo teléfono:");
        if (phone == null) return;
        String email = askRequiredText("Nuevo email:");
        if (email == null) return;

        clientService.updateClient(idNumber, name, phone, email);
        JOptionPane.showMessageDialog(null, "Cliente actualizado.");
    }

    private void deleteClient() {
        String idNumber = JOptionPane.showInputDialog("ID del cliente a eliminar:");
        boolean ok = clientService.deleteClient(idNumber);
        JOptionPane.showMessageDialog(null, ok ? "Cliente eliminado." : "No se encontró el cliente.");
    }

    // ================= VENDEDORES =================

    private void sellersMenu() {
        String menu = "=== Vendedores ===\n"
                + "1. Registrar\n"
                + "2. Buscar por ID\n"
                + "3. Listar todos\n"
                + "4. Actualizar\n"
                + "5. Eliminar\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Vendedores", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerSeller();
            case 2 -> searchSeller();
            case 3 -> listSellers();
            case 4 -> updateSeller();
            case 5 -> deleteSeller();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerSeller() {
        String name = askRequiredText("Nombre:");
        if (name == null) return;
        String phone = askRequiredText("Teléfono:");
        if (phone == null) return;
        String employeeCode = askRequiredText("Código de empleado:");
        if (employeeCode == null) return;
        String shift = askRequiredText("Turno:");
        if (shift == null) return;

        boolean registrado = false;
        do {
            String idNumber = askRequiredText("Identificación:");
            if (idNumber == null) return;
            try {
                sellerService.registerSeller(name, idNumber, phone, employeeCode, shift);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otra identificación.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Vendedor registrado con éxito.");
    }

    private void searchSeller() {
        String idNumber = JOptionPane.showInputDialog("ID del vendedor a buscar:");
        Seller s = sellerService.findByIdNumber(idNumber);
        JOptionPane.showMessageDialog(null, s != null ? s.display() : "No se encontró el vendedor.");
    }

    private void listSellers() {
        List<Seller> vendedores = sellerService.findAll();
        JTextArea salida = new JTextArea(20, 50);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("idNumber\tname\temployeeCode\tshift\n");
        for (Seller s : vendedores) {
            salida.append(s.getIdNumber() + "\t" + s.getName() + "\t" + s.getEmployeeCode() + "\t\t" + s.getShift() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void updateSeller() {
        String idNumber;
        do {
            idNumber = askRequiredText("ID del vendedor a actualizar:");
            if (idNumber == null) return;
            if (sellerService.findByIdNumber(idNumber) == null) {
                JOptionPane.showMessageDialog(null, "No existe un vendedor con ese ID.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (sellerService.findByIdNumber(idNumber) == null);

        String name = askRequiredText("Nuevo nombre:");
        if (name == null) return;
        String phone = askRequiredText("Nuevo teléfono:");
        if (phone == null) return;
        String employeeCode = askRequiredText("Nuevo código de empleado:");
        if (employeeCode == null) return;
        String shift = askRequiredText("Nuevo turno:");
        if (shift == null) return;

        sellerService.updateSeller(idNumber, name, phone, employeeCode, shift);
        JOptionPane.showMessageDialog(null, "Vendedor actualizado.");
    }

    private void deleteSeller() {
        String idNumber = JOptionPane.showInputDialog("ID del vendedor a eliminar:");
        boolean ok = sellerService.deleteSeller(idNumber);
        JOptionPane.showMessageDialog(null, ok ? "Vendedor eliminado." : "No se encontró el vendedor.");
    }

    // ================= PRODUCTOS =================

    private void productsMenu() {
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

    // ================= VENTAS =================

    private void salesMenu() {
        String menu = "=== Ventas ===\n"
                + "1. Registrar venta\n"
                + "2. Buscar por código\n"
                + "3. Listar todas\n"
                + "4. Cancelar venta\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Ventas", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerSale();
            case 2 -> searchSale();
            case 3 -> listSales();
            case 4 -> cancelSale();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerSale() {
        String code = askRequiredText("Código de la venta:");
        if (code == null) return;

        // Se valida cliente y vendedor ANTES de llamar a saleService, para
        // poder reintentar solo ese campo en vez de reintentar todo.
        String clientId;
        do {
            clientId = askRequiredText("ID del cliente:");
            if (clientId == null) return;
            if (clientService.findByIdNumber(clientId) == null) {
                JOptionPane.showMessageDialog(null, "No existe un cliente con ese ID.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (clientService.findByIdNumber(clientId) == null);

        String sellerId;
        do {
            sellerId = askRequiredText("ID del vendedor:");
            if (sellerId == null) return;
            if (sellerService.findByIdNumber(sellerId) == null) {
                JOptionPane.showMessageDialog(null, "No existe un vendedor con ese ID.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (sellerService.findByIdNumber(sellerId) == null);

        // El texto de productos se valida con un do-while propio: primero
        // que no venga vacío, y después que el registro con saleService no
        // falle por producto inexistente o sin stock (ahí sí se necesita
        // el service, porque el stock cambia en cada intento).
        boolean registrado = false;
        Sale venta = null;
        do {
            String productosTexto = askRequiredText("Identificadores de productos separados por coma (ej: P001,P002):");
            if (productosTexto == null) return;
            List<String> productIds = Arrays.asList(productosTexto.split("\\s*,\\s*"));
            try {
                venta = saleService.registerSale(code, clientId, sellerId, productIds);
                registrado = true;
            } catch (IllegalArgumentException | IllegalStateException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nRevisa los identificadores de producto e intenta de nuevo.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Venta registrada:\n" + venta.Display());
    }

    private void searchSale() {
        String code = JOptionPane.showInputDialog("Código de la venta a buscar:");
        Sale s = saleService.findByCode(code);
        JOptionPane.showMessageDialog(null, s != null ? s.Display() : "No se encontró la venta.");
    }

    private void listSales() {
        List<Sale> ventas = saleService.findAll();
        JTextArea salida = new JTextArea(20, 70);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("code\tdate\t\tclient\t\tseller\t\ttotal\n");
        for (Sale s : ventas) {
            salida.append(s.getCode() + "\t" + s.getDate() + "\t"
                    + s.getClient().getName() + "\t" + s.getSeller().getName()
                    + "\t$" + s.getTotal() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void cancelSale() {
        String code = JOptionPane.showInputDialog("Código de la venta a cancelar:");
        boolean ok = saleService.cancelSale(code);
        JOptionPane.showMessageDialog(null, ok ? "Venta cancelada, stock devuelto." : "No se encontró la venta.");
    }
}

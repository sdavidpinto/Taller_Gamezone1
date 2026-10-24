package ui;

import model.Sale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.ClientService;
import services.SellerService;
import services.SaleService;

/**
 * Submenú de ventas. Recibe solo los servicios que necesita.
 */
public class SaleMenu extends SubMenu {

    private final ClientService clientService;
    private final SellerService sellerService;
    private final SaleService saleService;

    public SaleMenu(ClientService clientService, SellerService sellerService, SaleService saleService) {
        this.clientService = clientService;
        this.sellerService = sellerService;
        this.saleService = saleService;
    }

    @Override
    public void show() {
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
            
            String accesoriosTexto = JOptionPane.showInputDialog("Identificadores de accesorios separados por coma (opcional, deja vacío si no aplica):");
            if (accesoriosTexto == null) return; // el usuario canceló
            List<String> accessoryIds = accesoriosTexto.isBlank()
                    ? new ArrayList<>()
                    : Arrays.asList(accesoriosTexto.split("\\s*,\\s*"));
            String garantiaExtendidaTexto = JOptionPane.showInputDialog("Identificadores de productos con garantía extendida, separados por coma (opcional, deja vacío si no aplica):");
            if (garantiaExtendidaTexto == null) return; // el usuario canceló
            List<String> extendedWarrantyProductIds = garantiaExtendidaTexto.isBlank()
                    ? new ArrayList<>()
                    : Arrays.asList(garantiaExtendidaTexto.split("\\s*,\\s*"));
            
            try {
                venta = saleService.registerSale(code, clientId, sellerId, productIds,accessoryIds, extendedWarrantyProductIds);
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
        salida.setText("code\tdate\tclient\tseller\ttotal\n");
        for (Sale s : ventas) {
            salida.append(s.getCode()+ "\t" + s.getDate()+ "\t"
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
package ui;

import javax.swing.JOptionPane;
import services.AccessoryService;
import services.ClientService;
import services.ProductService;
import services.PromotionService;
import services.ReturnService;
import services.SaleService;
import services.SellerService;
import services.WarrantyService;

/**
 * Menú principal de la consola. Solo muestra las opciones y delega cada
 * una en su submenú (ClientMenu, SellerMenu, ProductMenu, SaleMenu,
 * AccessoryMenu, PromotionMenu, WarrantyMenu, ReturnMenu). No contiene
 * lógica de ningún módulo.
 *
 * Esta clase NO conoce los repositorios ni el formato de persistencia:
 * solo recibe los Services ya construidos (inyección de dependencias por
 * constructor) y se los reparte a cada submenú.
 */
public class MenuUI {

    private final SubMenu clientMenu;
    private final SubMenu sellerMenu;
    private final SubMenu productMenu;
    private final SubMenu saleMenu;
    private final SubMenu accessoryMenu;
    private final SubMenu promotionMenu;
    private final SubMenu warrantyMenu;
    private final SubMenu returnMenu;

    public MenuUI(ClientService clientService, SellerService sellerService,
                   ProductService productService, SaleService saleService, AccessoryService accessoryService,
                   PromotionService promotionService, WarrantyService warrantyService, ReturnService returnService) {
        this.clientMenu = new ClientMenu(clientService, saleService);
        this.sellerMenu = new SellerMenu(sellerService, saleService);
        this.productMenu = new ProductMenu(productService);
        this.saleMenu = new SaleMenu(clientService, sellerService, saleService);
        this.accessoryMenu = new AccessoryMenu(productService, accessoryService);
        this.promotionMenu = new PromotionMenu(promotionService);
        this.warrantyMenu = new WarrantyMenu(warrantyService);
        this.returnMenu = new ReturnMenu(saleService, returnService);
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
                    + "5. Accesorios\n"
                    + "6. Promociones\n"
                    + "7. Garantías\n"
                    + "8. Devoluciones\n"
                    + "0. Salir";
            String entrada = JOptionPane.showInputDialog(null, menuPrincipal, "Menú principal", JOptionPane.PLAIN_MESSAGE);

            if (entrada == null) {
                break; // el usuario cerró el diálogo
            }

            opcion = parseOption(entrada);

            switch (opcion) {
                case 1 -> clientMenu.show();
                case 2 -> sellerMenu.show();
                case 3 -> productMenu.show();
                case 4 -> saleMenu.show();
                case 5 -> accessoryMenu.show();
                case 6 -> promotionMenu.show();
                case 7 -> warrantyMenu.show();
                case 8 -> returnMenu.show();
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
}
package ui;

import model.Warranty;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.WarrantyService;

/**
 * Submenú de garantías. Recibe solo los servicios que necesita.
 */
public class WarrantyMenu extends SubMenu {

    private final WarrantyService warrantyService;

    public WarrantyMenu(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    @Override
    public void show() {
        String menu = "=== Garantías ===\n"
                + "1. Buscar garantía por producto y venta\n"
                + "2. Listar todas\n"
                + "3. Listar vigentes\n"
                + "4. Listar próximas a vencer\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Garantías", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> searchWarranty();
            case 2 -> listAllWarranties();
            case 3 -> listActiveWarranties();
            case 4 -> listWarrantiesExpiringSoon();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void searchWarranty() {
        String productId = askRequiredText("Identifier del producto:");
        if (productId == null) return;
        String saleCode = askRequiredText("Código de la venta:");
        if (saleCode == null) return;

        Warranty w = warrantyService.findWarrantyByProduct(productId, saleCode);
        JOptionPane.showMessageDialog(null, w != null ? w.generateWarrantyCertificate() : "No se encontró una garantía para ese producto y venta.");
    }

    private void listAllWarranties() {
        displayWarrantyList(warrantyService.listAllWarranties(), "No hay garantías registradas.");
    }

    private void listActiveWarranties() {
        displayWarrantyList(warrantyService.listActiveWarranties(), "No hay garantías vigentes.");
    }

    private void listWarrantiesExpiringSoon() {
        Integer dias = askInt("¿Con cuántos días de anticipación quieres revisar?");
        if (dias == null) return;

        displayWarrantyList(warrantyService.listWarrantiesExpiringSoon(dias), "No hay garantías próximas a vencer en ese período.");
    }

    private void displayWarrantyList(List<Warranty> warranties, String emptyMessage) {
        if (warranties.isEmpty()) {
            JOptionPane.showMessageDialog(null, emptyMessage);
            return;
        }
        JTextArea salida = new JTextArea(20, 70);
        JScrollPane tabla = new JScrollPane(salida);
        for (Warranty w : warranties) {
            salida.append(w.generateWarrantyCertificate() + "\n\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }
}
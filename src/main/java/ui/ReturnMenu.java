package ui;

import model.Return;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.SaleService;
import services.ReturnService;

/**
 * Submenú de devoluciones. Recibe solo los servicios que necesita.
 */
public class ReturnMenu extends SubMenu {

    private final SaleService saleService;
    private final ReturnService returnService;

    public ReturnMenu(SaleService saleService, ReturnService returnService) {
        this.saleService = saleService;
        this.returnService = returnService;
    }

    @Override
    public void show() {
        String menu = "=== Devoluciones ===\n"
                + "1. Registrar devolución\n"
                + "2. Listar todas\n"
                + "3. Buscar por cliente\n"
                + "4. Buscar por venta\n"
                + "5. Balance mensual\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Devoluciones", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerReturn();
            case 2 -> listAllReturns();
            case 3 -> searchReturnsByCustomer();
            case 4 -> searchReturnsBySale();
            case 5 -> monthlyBalance();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerReturn() {
        String saleId;
        do {
            saleId = askRequiredText("Código de la venta original:");
            if (saleId == null) return;
            if (saleService.findByCode(saleId) == null) {
                JOptionPane.showMessageDialog(null, "No existe una venta con ese código.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (saleService.findByCode(saleId) == null);

        String reason = askRequiredText("Motivo de la devolución:");
        if (reason == null) return;

        boolean registrada = false;
        Return devolucion = null;
        do {
            String productosTexto = askRequiredText("Identificadores de productos a devolver, separados por coma (ej: P001,P002):");
            if (productosTexto == null) return;
            List<String> productIds = Arrays.asList(productosTexto.split("\\s*,\\s*"));

            try {
                devolucion = returnService.registerReturn(saleId, productIds, reason);
                registrada = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nRevisa los identificadores e intenta de nuevo.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrada);

        JOptionPane.showMessageDialog(null, "Devolución registrada:\n" + devolucion.generateReturnReceipt());
    }

    private void listAllReturns() {
        displayReturnList(returnService.viewAllReturns(), "No hay devoluciones registradas.");
    }

    private void searchReturnsByCustomer() {
        String customerId = askRequiredText("ID del cliente:");
        if (customerId == null) return;
        displayReturnList(returnService.viewReturnsByCustomer(customerId), "Ese cliente no tiene devoluciones registradas.");
    }

    private void searchReturnsBySale() {
        String saleId = askRequiredText("Código de la venta:");
        if (saleId == null) return;
        displayReturnList(returnService.viewReturnsBySale(saleId), "Esa venta no tiene devoluciones registradas.");
    }

    private void displayReturnList(List<Return> devoluciones, String emptyMessage) {
        if (devoluciones.isEmpty()) {
            JOptionPane.showMessageDialog(null, emptyMessage);
            return;
        }
        JTextArea salida = new JTextArea(20, 70);
        JScrollPane tabla = new JScrollPane(salida);
        for (Return r : devoluciones) {
            salida.append(r.generateReturnReceipt() + "\n\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }
    
    private void monthlyBalance() {
        Integer month = askInt("Mes a consultar (1-12):");
        if (month == null) return;
        Integer year = askInt("Año a consultar (ej. 2026):");
        if (year == null) return;

        double balance = returnService.generateMonthlyBalance(month, year);
        JOptionPane.showMessageDialog(null, "Balance neto de " + month + "/" + year + ": $" + balance);
}
    
}
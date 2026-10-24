package ui;

import model.Sale;
import model.Seller;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.SellerService;
import services.SaleService;

/**
 * Submenú de vendedores. Recibe solo los servicios que necesita.
 */
public class SellerMenu extends SubMenu {

    private final SellerService sellerService;
    private final SaleService saleService;

    public SellerMenu(SellerService sellerService, SaleService saleService) {
        this.sellerService = sellerService;
        this.saleService = saleService;
    }

    @Override
    public void show() {
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

        if (s == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el vendedor.");
            return;
        }

        StringBuilder mensaje = new StringBuilder(s.display());

        List<Sale> ventasDelVendedor = saleService.findAll().stream()
                .filter(venta -> venta.getSeller().getIdNumber().equals(idNumber))
                .toList();

        if (ventasDelVendedor.isEmpty()) {
            mensaje.append("\n\nEste vendedor no tiene ventas registradas.");
        } else {
            mensaje.append("\n\nVentas del vendedor:\n");
            for (Sale venta : ventasDelVendedor) {
                mensaje.append(venta.Display()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null, mensaje.toString());
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
}
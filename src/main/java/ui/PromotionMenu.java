package ui;

import model.Promotion;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.PromotionService;

/**
 * Submenú de promociones. Recibe solo los servicios que necesita.
 */
public class PromotionMenu extends SubMenu {

    private final PromotionService promotionService;

    public PromotionMenu(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Override
    public void show() {
        String menu = "=== Promociones ===\n"
                + "1. Registrar descuento por porcentaje\n"
                + "2. Registrar descuento por categoría\n"
                + "3. Registrar descuento por volumen\n"
                + "4. Listar todas\n"
                + "5. Listar vigentes\n"
                + "6. Buscar por ID\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Promociones", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerPercentageDiscount();
            case 2 -> registerCategoryDiscount();
            case 3 -> registerBulkPurchaseDiscount();
            case 4 -> listAllPromotions();
            case 5 -> listActivePromotions();
            case 6 -> searchPromotion();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerPercentageDiscount() {
        String name = askRequiredText("Nombre de la promoción:");
        if (name == null) return;
        LocalDate startDate = askDate("Fecha de inicio:");
        if (startDate == null) return;
        LocalDate endDate = askDate("Fecha de fin:");
        if (endDate == null) return;
        Double percentage = askDouble("Porcentaje de descuento (ej. 10 para 10%):");
        if (percentage == null) return;

        boolean registrado = false;
        do {
            String id = askRequiredText("ID de la promoción:");
            if (id == null) return;
            try {
                promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro ID.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Promoción registrada con éxito.");
    }

    private void registerCategoryDiscount() {
        String name = askRequiredText("Nombre de la promoción:");
        if (name == null) return;
        LocalDate startDate = askDate("Fecha de inicio:");
        if (startDate == null) return;
        LocalDate endDate = askDate("Fecha de fin:");
        if (endDate == null) return;
        String targetCategory = askRequiredText("Categoría objetivo (VIDEOGAME / CONSOLE / ACCESSORY):");
        if (targetCategory == null) return;
        Double percentage = askDouble("Porcentaje de descuento (ej. 10 para 10%):");
        if (percentage == null) return;

        boolean registrado = false;
        do {
            String id = askRequiredText("ID de la promoción:");
            if (id == null) return;
            try {
                promotionService.registerCategoryDiscount(id, name, startDate, endDate, targetCategory, percentage);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro ID.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Promoción registrada con éxito.");
    }

    private void registerBulkPurchaseDiscount() {
        String name = askRequiredText("Nombre de la promoción:");
        if (name == null) return;
        LocalDate startDate = askDate("Fecha de inicio:");
        if (startDate == null) return;
        LocalDate endDate = askDate("Fecha de fin:");
        if (endDate == null) return;
        Integer minQuantity = askInt("Cantidad mínima de artículos:");
        if (minQuantity == null) return;
        Double percentage = askDouble("Porcentaje de descuento (ej. 10 para 10%):");
        if (percentage == null) return;

        boolean registrado = false;
        do {
            String id = askRequiredText("ID de la promoción:");
            if (id == null) return;
            try {
                promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otro ID.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Promoción registrada con éxito.");
    }

    private void listAllPromotions() {
        List<Promotion> promociones = promotionService.listAllPromotions();
        mostrarListaPromociones(promociones, "No hay promociones registradas.");
    }

    private void listActivePromotions() {
        List<Promotion> promociones = promotionService.listActivePromotions();
        mostrarListaPromociones(promociones, "No hay promociones vigentes.");
    }

    private void mostrarListaPromociones(List<Promotion> promociones, String mensajeVacio) {
        if (promociones.isEmpty()) {
            JOptionPane.showMessageDialog(null, mensajeVacio);
            return;
        }
        JTextArea salida = new JTextArea(20, 70);
        JScrollPane tabla = new JScrollPane(salida);
        for (Promotion p : promociones) {
            salida.append(p.getDescription() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void searchPromotion() {
        String id = JOptionPane.showInputDialog("ID de la promoción a buscar:");
        Promotion p = promotionService.findById(id);
        JOptionPane.showMessageDialog(null, p != null ? p.getDescription() : "No se encontró la promoción.");
    }
}
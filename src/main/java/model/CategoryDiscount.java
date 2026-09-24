package model;

import java.time.LocalDate;
import java.util.List;

public class CategoryDiscount extends Promotion {

    public static final String VIDEOGAME = "VIDEOGAME";
    public static final String CONSOLE = "CONSOLE";

    private static final List<String> VALID_CATEGORIES = List.of(VIDEOGAME, CONSOLE);

    private final String targetCategory;
    private final double percentage;

    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                            String targetCategory, double percentage) {
        super(id, name, startDate, endDate);
        if (!isValidCategory(targetCategory)) {
            throw new IllegalArgumentException(
                    "Categoria no valida: " + targetCategory + ". Permitidas: " + VALID_CATEGORIES);
        }
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
        this.targetCategory = targetCategory.trim().toUpperCase();
        this.percentage = percentage;
    }

    public static boolean isValidCategory(String category) {
        return category != null && VALID_CATEGORIES.contains(category.trim().toUpperCase());
    }

    public String getTargetCategory() { return targetCategory; }

    public double getPercentage() { return percentage; }

    @Override
    public double calculateDiscount(List<Product> products) {
        if (products == null) {
            return 0;
        }
        double eligibleTotal = 0;
        for (Product product : products) {
            if (product != null && belongsToTargetCategory(product)) {
                eligibleTotal += product.getPrice();
            }
        }
        return eligibleTotal * percentage / 100.0;
    }

    private boolean belongsToTargetCategory(Product product) {
        if (targetCategory.equals(VIDEOGAME)) {
            return product instanceof VideoGame;
        }
        if (targetCategory.equals(CONSOLE)) {
            return product instanceof Console;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return String.format("%s [CategoryDiscount] - %.1f%% off %s | Valid: %s to %s",
                getName(), percentage, targetCategory, getStartDate(), getEndDate());
    }
}

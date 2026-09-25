package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Promocion que descuenta un porcentaje pero solo a los productos de una
 * categoria especifica. Los productos de otras categorias no se tocan.
 */
public class CategoryDiscount extends Promotion {

    /** Categoria para videojuegos. */
    public static final String VIDEOGAME = "VIDEOGAME";
    /** Categoria para consolas. */
    public static final String CONSOLE = "CONSOLE";

    private static final List<String> VALID_CATEGORIES = List.of(VIDEOGAME, CONSOLE);

    private final String targetCategory;
    private final double percentage;

    /**
     * Crea el descuento por categoria.
     *
     * @param id id de la promocion
     * @param name nombre de la promocion
     * @param startDate desde que fecha es valida
     * @param endDate hasta que fecha es valida
     * @param targetCategory la categoria a la que aplica ("VIDEOGAME" o "CONSOLE")
     * @param percentage el porcentaje a descontar, de 0 a 100
     * @throws IllegalArgumentException si la categoria no es valida o el
     *         porcentaje esta fuera de rango
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                            String targetCategory, double percentage) {
        super(id, name, startDate, endDate);
        if (!isValidCategory(targetCategory)) {
            throw new IllegalArgumentException(
                    "Categoria no valida: " + targetCategory + ". Permitidas: " + VALID_CATEGORIES);
        }
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 (excluido) y 100.");
        }
        this.targetCategory = targetCategory.trim().toUpperCase();
        this.percentage = percentage;
    }

    /**
     * Revisa si una categoria es una de las permitidas.
     *
     * @param category el nombre de la categoria a revisar
     * @return true si esa categoria se puede usar en esta promocion
     */
    public static boolean isValidCategory(String category) {
        return category != null && VALID_CATEGORIES.contains(category.trim().toUpperCase());
    }

    /** @return la categoria a la que aplica esta promocion */
    public String getTargetCategory() { return targetCategory; }

    /** @return el porcentaje de descuento */
    public double getPercentage() { return percentage; }

    /**
     * Suma el precio solo de los productos que son de la categoria objetivo
     * y le saca el porcentaje.
     *
     * @param products los productos de la venta
     * @return el monto del descuento, o 0 si ningun producto es de esa categoria
     */
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

    /**
     * Revisa si un producto es de la categoria a la que aplica esta promocion.
     *
     * @param product el producto a revisar
     * @return true si el producto pertenece a la categoria objetivo
     */
    private boolean belongsToTargetCategory(Product product) {
        if (targetCategory.equals(VIDEOGAME)) {
            return product instanceof VideoGame;
        }
        if (targetCategory.equals(CONSOLE)) {
            return product instanceof Console;
        }
        return false;
    }

    /**
     * Texto con los datos de esta promocion.
     *
     * @return la descripcion de la promocion
     */
    @Override
    public String getDescription() {
        return String.format("%s [CategoryDiscount] - %.1f%% off %s | Valid: %s to %s",
                getName(), percentage, targetCategory, getStartDate(), getEndDate());
    }
}

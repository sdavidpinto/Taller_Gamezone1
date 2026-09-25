package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Promocion que descuenta un porcentaje cuando el cliente lleva varios
 * productos, siempre que llegue a una cantidad minima de articulos.
 * Cada producto cuenta como una unidad, sin importar el tipo.
 */
public class BulkPurchaseDiscount extends Promotion {

    private final int minQuantity;
    private final double percentage;

    /**
     * Crea el descuento por cantidad.
     *
     * @param id id de la promocion
     * @param name nombre de la promocion
     * @param startDate desde que fecha es valida
     * @param endDate hasta que fecha es valida
     * @param minQuantity minimo de articulos que se deben llevar (al menos 1)
     * @param percentage el porcentaje a descontar, de 0 a 100
     * @throws IllegalArgumentException si la cantidad minima es menor a 1 o
     *         el porcentaje esta fuera de rango
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                int minQuantity, double percentage) {
        super(id, name, startDate, endDate);
        if (minQuantity < 1) {
            throw new IllegalArgumentException("La cantidad minima debe ser al menos 1.");
        }
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 (excluido) y 100.");
        }
        this.minQuantity = minQuantity;
        this.percentage = percentage;
    }

    /** @return la cantidad minima de articulos que se necesita */
    public int getMinQuantity() { return minQuantity; }

    /** @return el porcentaje de descuento */
    public double getPercentage() { return percentage; }

    /**
     * Cuenta cuantos productos hay en la venta. Si llega al minimo, calcula
     * el descuento sobre el total; si no, no hay descuento.
     *
     * @param products los productos de la venta
     * @return el monto del descuento, o 0 si no se junta la cantidad minima
     */
    @Override
    public double calculateDiscount(List<Product> products) {
        if (products == null) {
            return 0;
        }
        int quantity = 0;
        double total = 0;
        for (Product product : products) {
            if (product != null) {
                quantity++;
                total += product.getPrice();
            }
        }
        if (quantity < minQuantity) {
            return 0;
        }
        return total * percentage / 100.0;
    }

    /**
     * Texto con los datos de esta promocion.
     *
     * @return la descripcion de la promocion
     */
    @Override
    public String getDescription() {
        return String.format("%s [BulkPurchaseDiscount] - %.1f%% off when buying %d or more items | Valid: %s to %s",
                getName(), percentage, minQuantity, getStartDate(), getEndDate());
    }
}

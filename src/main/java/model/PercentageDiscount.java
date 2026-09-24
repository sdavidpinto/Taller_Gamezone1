package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Promocion que descuenta un porcentaje sobre el total de la venta.
 * No le importa que tipo de productos sean, descuenta sobre todo.
 */
public class PercentageDiscount extends Promotion {

    private final double percentage;

    /**
     * Crea el descuento por porcentaje.
     *
     * @param id id de la promocion
     * @param name nombre de la promocion
     * @param startDate desde que fecha es valida
     * @param endDate hasta que fecha es valida
     * @param percentage el porcentaje a descontar, va de 0 a 100 (por ejemplo, 10 es 10%)
     * @throws IllegalArgumentException si el porcentaje es 0, negativo o mayor a 100
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                              double percentage) {
        super(id, name, startDate, endDate);
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 (excluido) y 100.");
        }
        this.percentage = percentage;
    }

    /** @return el porcentaje de descuento */
    public double getPercentage() { return percentage; }

    /**
     * Suma el precio de todos los productos y le saca el porcentaje.
     *
     * @param products los productos de la venta
     * @return el monto del descuento, o 0 si la lista viene vacia
     */
    @Override
    public double calculateDiscount(List<Product> products) {
        if (products == null) {
            return 0;
        }
        double total = 0;
        for (Product product : products) {
            if (product != null) {
                total += product.getPrice();
            }
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
        return String.format("%s [PercentageDiscount] - %.1f%% off the whole sale | Valid: %s to %s",
                getName(), percentage, getStartDate(), getEndDate());
    }
}

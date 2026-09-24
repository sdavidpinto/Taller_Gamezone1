package model;

import java.time.LocalDate;
import java.util.List;

public class BulkPurchaseDiscount extends Promotion {

    private final int minQuantity;
    private final double percentage;

    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                int minQuantity, double percentage) {
        super(id, name, startDate, endDate);
        if (minQuantity < 1) {
            throw new IllegalArgumentException("La cantidad minima debe ser al menos 1.");
        }
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
        this.minQuantity = minQuantity;
        this.percentage = percentage;
    }

    public int getMinQuantity() { return minQuantity; }

    public double getPercentage() { return percentage; }

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

    @Override
    public String getDescription() {
        return String.format("%s [BulkPurchaseDiscount] - %.1f%% off when buying %d or more items | Valid: %s to %s",
                getName(), percentage, minQuantity, getStartDate(), getEndDate());
    }
}

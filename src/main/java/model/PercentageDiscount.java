package model;

import java.time.LocalDate;
import java.util.List;

public class PercentageDiscount extends Promotion {

    private final double percentage;

    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                              double percentage) {
        super(id, name, startDate, endDate);
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
        this.percentage = percentage;
    }

    public double getPercentage() { return percentage; }

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

    @Override
    public String getDescription() {
        return String.format("%s [PercentageDiscount] - %.1f%% off the whole sale | Valid: %s to %s",
                getName(), percentage, getStartDate(), getEndDate());
    }
}

package model;

import java.time.LocalDate;
import java.util.List;

public class Return {

    private String id;
    private LocalDate date;
    private final Sale originalSale;
    private final List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Sale getOriginalSale() {
        return originalSale;
    }

    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    public String getReason() {
        return reason;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
    public double calculateRefundAmount() {
        double amount = 0;
        if (returnedProducts != null) {
            for (Product product : returnedProducts) {
                amount += product.getPrice();
            }
        }
        this.refundAmount = amount;
        return amount;
    }
}
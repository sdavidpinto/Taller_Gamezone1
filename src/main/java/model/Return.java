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
        public String generateReturnReceipt() {
        StringBuilder productsStr = new StringBuilder();
        if (returnedProducts != null) {
            for (Product product : returnedProducts) {
                productsStr.append("  - ").append(product.getTitle())
                        .append(" ($").append(product.getPrice()).append(")\n");
            }
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("Devolucion: ").append(id).append("\n")
               .append("Fecha: ").append(date).append("\n")
               .append("Venta original: ").append(originalSale.getCode()).append("\n")
               .append("Productos devueltos:\n").append(productsStr)
               .append("Motivo: ").append(reason).append("\n")
               .append("Monto reembolsado: $").append(refundAmount);

        return receipt.toString();
    }
}
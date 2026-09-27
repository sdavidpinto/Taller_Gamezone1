package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a return of one or more products that belonged to a
 * previously registered sale. A Return keeps an association with the
 * original Sale (it does not own or replace it) and stores only the
 * subset of products that the client is giving back.
 */
public class Return {

    private String id;
    private LocalDate date;
    private final Sale originalSale;
    private final List<Product> returnedProducts;
    private String reason;
    private double refundAmount;
    private double warrantyRefundAmount = 0;

    /**
     * Builds a new Return for a set of products belonging to an original sale.
     *
     * @param id identifier of the return.
     * @param date date on which the return is registered.
     * @param originalSale sale that the returned products belong to.
     * @param returnedProducts products that the client is giving back.
     * @param reason reason given by the client for the return.
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /** @return the identifier of the return. */
    public String getId() {
        return id;
    }

    /** @return the date the return was registered. */
    public LocalDate getDate() {
        return date;
    }

    /** @return the original sale this return references. */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /** @return the products included in this return. */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    /** @return the reason given for the return. */
    public String getReason() {
        return reason;
    }

    /** @return the refunded amount for this return. */
    public double getRefundAmount() {
        return refundAmount;
    }

    /** @return the portion of the refund that comes from canceled warranties. */
    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    /**
     * Calculates the refund amount as the sum of the list price of every
     * returned product, and stores the result in refundAmount.
     *
     * @return the calculated refund amount.
     */
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

    /**
     * Adds the given amount to the refund, corresponding to warranties
     * that were canceled because the covered product was returned.
     * This is invoked from ReturnService after the warranty for a
     * returned console (if any) has been canceled.
     *
     * @param amount the additional refundable amount from canceled warranties
     */
    public void addWarrantyRefund(double amount) {
        this.warrantyRefundAmount = amount;
        this.refundAmount += amount;
    }

    /**
     * Builds a formatted, Spanish-language receipt describing this return.
     *
     * @return a string with the return id, date, reference sale, returned
     *         products with their prices, reason, warranty refund (if any)
     *         and total refunded amount.
     */
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
               .append("Motivo: ").append(reason).append("\n");

        if (warrantyRefundAmount > 0) {
            receipt.append("Reembolso por garantia cancelada: $").append(warrantyRefundAmount).append("\n");
        }

        receipt.append("Monto reembolsado: $").append(refundAmount);

        return receipt.toString();
    }
}
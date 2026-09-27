package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa la devolucion de uno o mas productos que pertenecian a una
 * venta previamente registrada. Un Return mantiene una asociacion con la
 * venta original (no la posee ni la reemplaza) y almacena solo el
 * subconjunto de productos que el cliente esta devolviendo.
 */
public class Return {

    private String id;
    private LocalDate date;
    private final Sale originalSale;
    private final List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Construye una nueva devolucion para un conjunto de productos que
     * pertenecen a una venta original.
     *
     * @param id identificador de la devolucion.
     * @param date fecha en la que se registra la devolucion.
     * @param originalSale venta a la que pertenecen los productos devueltos.
     * @param returnedProducts productos que el cliente esta devolviendo.
     * @param reason motivo dado por el cliente para la devolucion.
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /** @return el identificador de la devolucion. */
    public String getId() {
        return id;
    }

    /** @return la fecha en la que se registro la devolucion. */
    public LocalDate getDate() {
        return date;
    }

    /** @return la venta original a la que hace referencia esta devolucion. */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /** @return los productos incluidos en esta devolucion. */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    /** @return el motivo dado para la devolucion. */
    public String getReason() {
        return reason;
    }

    /** @return el monto reembolsado por esta devolucion. */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calcula el monto reembolsado de forma proporcional al descuento
     * aplicado en la venta original, y almacena el resultado en refundAmount.
     *
     * @return el monto de reembolso calculado.
     */
    public double calculateRefundAmount() {
        double amount = 0;
        if (returnedProducts != null) {
            double discountRatio = resolveDiscountRatio();
            for (Product product : returnedProducts) {
                amount += product.getPrice() * (1 - discountRatio);
            }
        }
        this.refundAmount = amount;
        return amount;
    }

    /**
     * Calcula la proporcion del subtotal de la venta original que fue
     * descontada, para poder reembolsar cada producto devuelto de forma
     * proporcional a lo que el cliente realmente pago por el.
     *
     * @return la proporcion de descuento, o 0 si la venta no tuvo descuento
     *         o no tiene subtotal.
     */
    private double resolveDiscountRatio() {
        double subtotal = originalSale.getTotal() + originalSale.getDiscountAmount() - originalSale.getWarrantyCost();
        if (subtotal <= 0) {
            return 0;
        }
        return originalSale.getDiscountAmount() / subtotal;
    }

    /**
     * Construye un recibo formateado en español que describe esta devolucion,
     * mostrando el precio de lista, el descuento proporcional y el monto
     * reembolsado de cada producto.
     *
     * @return una cadena con el identificador de la devolucion, la fecha, la
     *         venta de referencia, el detalle de los productos devueltos, el
     *         motivo y el monto total reembolsado.
     */
    public String generateReturnReceipt() {
        StringBuilder productsStr = new StringBuilder();
        if (returnedProducts != null) {
            double discountRatio = resolveDiscountRatio();
            for (Product product : returnedProducts) {
                double listPrice = product.getPrice();
                double proportionalDiscount = listPrice * discountRatio;
                double refunded = listPrice - proportionalDiscount;
                productsStr.append("  - ").append(product.getTitle())
                        .append(" | Precio de lista: $").append(listPrice)
                        .append(" | Descuento proporcional: -$").append(proportionalDiscount)
                        .append(" | Reembolsado: $").append(refunded)
                        .append("\n");
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
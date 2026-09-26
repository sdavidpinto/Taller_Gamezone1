
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.temporal.ChronoUnit;

/**
 *   Class Sale: Una clase que relaciona a un cliente con una cantidad de productos a comprar y lo registra como una venta en un momento determinado
 * 
 * Date:fecha en la que se realizo la venta
 * client: Instancia de Client que compro los productos
 * Seller: Instancia de Seller que vendio el producto
 * products: Arraylist que tiene la lista de productos de la venta
 * total: Cantidad de dinero obtenido por la compra
 * 
 */
public class Sale {
    private String code;
    private LocalDate date;
    private final Client client;
    private final Seller seller;
    private List<Product> products=new ArrayList<>();
    double total;
    private String appliedPromotionName;
    private double discountAmount;

    public Sale(String code,Date date, Client client, Seller seller, List<Product> products) {
        this.code=code;
        this.date = LocalDate.now();
        this.client = client;
        this.seller = seller;
        this.products =products;
        this.total = calculateTotal(products);
    }

    
    /**
 * Setters y getters por cada atributo mencionado
 */
    
    
    
    public String getCode() {
        return code;
    }

    public LocalDate getDate() {
        return date;
    }

    public Client getClient() {
        return client;
    }

    public Seller getSeller() {
        return seller;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotal() {
        return total;
    }

    
    
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    /**
 * Metodo calculateTotal para determinar el precio de la venta y enviarlo como parametro de el constructor
 */
    
    public double calculateTotal(List<Product> products){
        double Total=0;
        if (products == null) return 0;
        for (Product product : products) {
            Total=Total+product.getPrice();
        }
        return Total;
    }
    /**
     * Checks whether this sale is still within the 30-calendar-day window
     * in which a return can be registered.
     *
     * @return true if today's date is within 30 days of the sale date.
     */
    public boolean canBeReturned() {
        long daysSinceSale = ChronoUnit.DAYS.between(date, LocalDate.now());
        return daysSinceSale >= 0 && daysSinceSale <= 30;
    }
/**
 * Metodo Display para mostrar la venta cuando se consulte por codigo
 */
public String Display() {
    StringBuilder productosStr = new StringBuilder();
    for (Product product : products) {
        productosStr.append("  - ").append(product.getTitle()).append(" ($").append(product.getPrice()).append(")\n");
    }

    double subtotal = total + discountAmount;

    StringBuilder resultado = new StringBuilder();
    resultado.append("code: ").append(code).append("\n")
             .append("Sale date: ").append(date).append("\n")
             .append("Client: ").append(client.getIdNumber()).append("\n")
             .append("Seller: ").append(seller.getIdNumber()).append("\n")
             .append("Products:\n").append(productosStr)
             .append("Subtotal: $").append(subtotal).append("\n");

    if (appliedPromotionName != null && !appliedPromotionName.isEmpty()) {
        resultado.append("Promotion applied: ").append(appliedPromotionName).append("\n")
                 .append("Discount: -$").append(discountAmount).append("\n");
    }

    resultado.append("Total: $").append(total);

    return resultado.toString();
}

    
    
    
}
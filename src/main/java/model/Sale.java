
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Samue
 */
public class Sale {
    private LocalDate Date;
    private final Client client;
    private final Seller seller;
    private List<Product> products=new ArrayList<>();
    double total;

    public Sale(Date Date, Client client, Seller seller, List<Product> products, double total) {
        this.Date = LocalDate.now();
        this.client = client;
        this.seller = seller;
        this.products =products;
        this.total = total;
    }

    public LocalDate getDate() {
        return Date;
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

    public void setDate(LocalDate Date) {
        this.Date = Date;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    
    
    
    
    
}
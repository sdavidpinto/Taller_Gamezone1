package com.gamezone.model;
import java.util.ArrayList;
import java.util.List;

public class Client extends Person {

    private String email;
    private List<Sale> salesHistory;

    public Client(String name, String idNumber, String phone, String email) {
        super(name, idNumber, phone);
        this.email = email;
        this.salesHistory = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Sale> getSalesHistory() {
        return salesHistory;
    }

    public void addSale(Sale sale) {
        salesHistory.add(sale);
    }
    
    @Override
    public String display() {
    return "Client: " + getName() + " | ID: " + getIdNumber() + " | Phone: " 
    + getPhone() + " | Email: " + email + " | Purchases: " + salesHistory.size();
}
}
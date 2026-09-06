package com.gamezone.model;
import java.util.ArrayList;
import java.util.List;
/**
 * Representa a un cliente que compra productos en la tienda.
 * Un cliente tiene un correo electrónico y mantiene un historial
 * de las ventas en las que ha participado.
 */
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
 /**
 * Construye una representación en texto de la información del cliente,
 * incluyendo nombre, identificación, teléfono, correo y número de compras.
 *
 * @return una cadena de texto formateada describiendo al cliente
 */
    @Override
    public String display() {
    return "Client: " + getName() + " | ID: " + getIdNumber() + " | Phone: " 
    + getPhone() + " | Email: " + email + " | Purchases: " + salesHistory.size();
}
}
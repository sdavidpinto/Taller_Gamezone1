package com.gamezone.model;

public abstract class Product {

    private String identifier;
    private String title;
    private double price;
    private int availableQuantity;

    protected Product(String identifier, String title, double price, int availableQuantity) {
        this.identifier = identifier;
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public String getIdentifier() { return identifier; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getAvailableQuantity() { return availableQuantity; }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    public abstract String getDescription();
}
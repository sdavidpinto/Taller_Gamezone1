package model;

/**
 * Represents a generic product in the GameZone inventory system.
 * This is an abstract base class that provides common attributes and methods 
 * for specific product types.
 */
public abstract class Product {

    private String identifier;
    private String title;
    private double price;
    private int availableQuantity;

    /**
     * Constructs a new Product with the specified details.
     * 
     * @param identifier The unique identifier of the product.
     * @param title The name or title of the product.
     * @param price The price of the product.
     * @param availableQuantity The stock quantity available for the product.
     */
    protected Product(String identifier, String title, double price, int availableQuantity) {
        this.identifier = identifier;
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Gets the unique identifier of the product.
     * 
     * @return The product identifier.
     */
    public String getIdentifier() { return identifier; }

    /**
     * Gets the title or name of the product.
     * 
     * @return The product title.
     */
    public String getTitle() { return title; }

    /**
     * Gets the price of the product.
     * 
     * @return The product price.
     */
    public double getPrice() { return price; }

    /**
     * Gets the available quantity of the product in stock.
     * 
     * @return The available quantity.
     */
    public int getAvailableQuantity() { return availableQuantity; }

    /**
     * Updates the available quantity of the product in stock.
     * 
     * @param availableQuantity The new quantity to set.
     */
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Generates a formatted string containing the product's details.
     * 
     * @return A string representation of the product description.
     */
    public abstract String getDescription();

    /**
     * Prints the product's description directly to the standard output (console).
     */
    public void mostrar() {
        System.out.println(getDescription());
    }
}
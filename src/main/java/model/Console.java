package model;

/**
 * Represents a video game console product in the inventory.
 * Extends the base Product class with console-specific attributes.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Constructs a new Console with the specified details.
     *
     * @param identifier The unique identifier of the console.
     * @param title The name of the console.
     * @param price The price of the console.
     * @param availableQuantity The stock quantity available.
     * @param brand The brand or manufacturer of the console (e.g., Sony, Microsoft).
     * @param model The specific model of the console.
     * @param generation The generation of the console.
     */
    public Console(String identifier, String title, double price, int availableQuantity,
                   String brand, String model, String generation) {
        super(identifier, title, price, availableQuantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Gets the brand of the console.
     * 
     * @return The console brand.
     */
    public String getBrand() { return brand; }

    /**
     * Gets the specific model of the console.
     * 
     * @return The console model.
     */
    public String getModel() { return model; }

    /**
     * Gets the generation of the console.
     * 
     * @return The console generation.
     */
    public String getGeneration() { return generation; }

    /**
     * Generates a formatted string containing the console's specific details.
     * 
     * @return A string representation of the console description.
     */
    @Override
    public String getDescription() {
        return String.format("%s [Console] - Brand: %s | Model: %s | Generation: %s | Price: $%.2f | Stock: %d",
                getTitle(), brand, model, generation, getPrice(), getAvailableQuantity());
    }
}
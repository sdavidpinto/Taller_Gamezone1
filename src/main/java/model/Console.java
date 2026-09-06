package model;

public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    public Console(String identifier, String title, double price, int availableQuantity,
                   String brand, String model, String generation) {
        super(identifier, title, price, availableQuantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getGeneration() { return generation; }

    @Override
    public String getDescription() {
        return String.format("%s [Console] - Brand: %s | Model: %s | Generation: %s | Price: $%.2f | Stock: %d",
                getTitle(), brand, model, generation, getPrice(), getAvailableQuantity());
    }
}
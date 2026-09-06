package model;

/**
 * Representa un producto de tipo consola de videojuegos.
 * Extiende la clase Product para incluir atributos específicos de consolas.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Construye una nueva instancia de Console con los detalles especificados.
     *
     * @param identifier El identificador único de la consola.
     * @param title El nombre de la consola.
     * @param price El precio de la consola.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param brand La marca o fabricante de la consola.
     * @param model El modelo específico de la consola.
     * @param generation La generación a la que pertenece la consola.
     */
    public Console(String identifier, String title, double price, int availableQuantity,
                   String brand, String model, String generation) {
        super(identifier, title, price, availableQuantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Obtiene la marca de la consola.
     * 
     * @return La marca de la consola.
     */
    public String getBrand() { return brand; }

    /**
     * Obtiene el modelo de la consola.
     * 
     * @return El modelo de la consola.
     */
    public String getModel() { return model; }

    /**
     * Obtiene la generación de la consola.
     * 
     * @return La generación de la consola.
     */
    public String getGeneration() { return generation; }

    /**
     * Genera una descripción formateada con los detalles específicos de la consola.
     * 
     * @return Una cadena de texto formateada con los datos de la consola.
     */
    @Override
    public String getDescription() {
        return String.format("%s [Console] - Brand: %s | Model: %s | Generation: %s | Price: $%.2f | Stock: %d",
                getTitle(), brand, model, generation, getPrice(), getAvailableQuantity());
    }
}
package model;

/**
 * Representa un producto genérico en el sistema de inventario de GameZone.
 * Esta clase base abstracta define los atributos y métodos comunes para 
 * todos los tipos de productos del sistema.
 */
public abstract class Product {

    private String identifier;
    private String title;
    private double price;
    private int availableQuantity;

    /**
     * Construye una nueva instancia de Producto con los detalles especificados.
     * 
     * @param identifier El identificador único del producto.
     * @param title El título o nombre del producto.
     * @param price El precio unitario del producto.
     * @param availableQuantity La cantidad disponible en inventario.
     */
    protected Product(String identifier, String title, double price, int availableQuantity) {
        this.identifier = identifier;
        this.title = title;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Obtiene el identificador único del producto.
     * 
     * @return El identificador del producto.
     */
    public String getIdentifier() { return identifier; }

    /**
     * Obtiene el título o nombre del producto.
     * 
     * @return El título del producto.
     */
    public String getTitle() { return title; }

    /**
     * Obtiene el precio del producto.
     * 
     * @return El precio del producto.
     */
    public double getPrice() { return price; }

    /**
     * Obtiene la cantidad disponible en inventario.
     * 
     * @return La cantidad disponible.
     */
    public int getAvailableQuantity() { return availableQuantity; }

    /**
     * Establece la cantidad disponible en inventario.
     * 
     * @param availableQuantity La nueva cantidad a asignar.
     */
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Genera una descripción formateada del producto.
     * 
     * @return Una cadena de texto con los detalles del producto.
     */
    public abstract String getDescription();

    /**
     * Muestra la descripción del producto directamente en la consola.
     */
    public void mostrar() {
        System.out.println(getDescription());
    }
}
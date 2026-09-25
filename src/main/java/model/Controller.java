package model;

import java.util.ArrayList;

/**
 * Representa un Accesorio de tipo Control.
 * Extiende la clase Product para incluir atributos específicos de un Control.
 */
public class Controller extends Accessory {

    private boolean alambric;

    /**
     * Construye una nueva instancia de Control con los detalles especificados.
     *
     * @param identifier El identificador único del videojuego.
     * @param title El Nombre del Control.
     * @param price El precio del Control.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param alambric Si la clase de conexion es alambrica o inalambrica.
     */
    
    public Controller( String identifier, String title, double price, int availableQuantity, String brand, boolean alambric) {
        super(identifier, title, price, availableQuantity, brand);
        this.alambric = alambric;
    }

    @Override
    public String getDescription() {
        return String.format("%s [Controller] | Price: $%.2f | Stock: %s | Brand %s | Alambric: %s",
        getTitle(), getPrice(), getAvailableQuantity(), getBrand(), isAlambric());
    }

    public boolean isAlambric() {
        return alambric;
    }

    public void setAlambric(boolean alambric) {
        this.alambric = alambric;
    }

    @Override
    public void addProduct(Product p) {
        if (compatible == null) {
            compatible = new ArrayList<>();
        }
        if (p != null && !compatible.contains(p)) {
            compatible.add(p);
        }
    }
    
}


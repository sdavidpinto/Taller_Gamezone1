package model;

/**
 * Representa un Accesorio de tipo Control.
 * Extiende la clase Product para incluir atributos específicos de un Control.
 */
public class Controller extends Accessory {

    private boolean ConnectionType;

    /**
     * Construye una nueva instancia de VideoGame con los detalles especificados.
     *
     * @param identifier El identificador único del videojuego.
     * @param title El Nombre del Control.
     * @param price El precio del Control.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param ConnectionType Si la clase de conexion es alambrica o inalambrica.
     */
    
    public Controller( String identifier, String title, double price, int availableQuantity, String brand, boolean ConnectionType) {
        super(identifier, title, price, availableQuantity, brand);
        this.ConnectionType = ConnectionType;
    }

    @Override
    public String getDescription() {
        return String.format("%s [Controller] %s | Price: $%.2f | Stock: %s | Brand %s ! Connectiontype %d",
                getTitle(), getPrice(), getAvailableQuantity(), getBrand(), isConnectionType());
    }

    public boolean isConnectionType() {
        return ConnectionType;
    }

    public void setConnectionType(boolean ConnectionType) {
        this.ConnectionType = ConnectionType;
    }
}
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un Accesorio de tipo Control.
 * Extiende la clase Product para incluir atributos específicos de un Control.
 */
public class Cable extends Accessory {

    private String ConnectionType;
    private int Length;

    /**
     * Construye una nueva instancia de VideoGame con los detalles especificados.
     *
     * @param identifier El identificador único del cable.
     * @param title El Nombre del Cable.
     * @param price El precio del Cable.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param ConnectionType la clase de conexion.
     */
    
    
 
    public Cable(String identifier, String title, double price, int availableQuantity, String brand,String ConnectionType, int Length) {
        super(identifier, title, price, availableQuantity, brand);
        this.ConnectionType = ConnectionType;
        this.Length = Length;
    }

    public Cable( String identifier, String title, double price, int availableQuantity, String brand, List<Product> compatible,String ConnectionType, int Length) {
        super(identifier, title, price, availableQuantity, brand, compatible);
        this.ConnectionType = ConnectionType;
        this.Length = Length;
    }

   
    
    @Override
    public String getDescription() {
        return String.format("%s [Cable] | Price: $%.2f | Stock: %s | Brand %s | ConnectionType %s | Length %d",
                getTitle(), getPrice(), getAvailableQuantity(), getBrand(), getConnectionType(), getLength());
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int Length) {
        this.Length = Length;
    }

    public String getConnectionType() {
        return ConnectionType;
    }

    public void setConnectionType(String ConnectionType) {
        this.ConnectionType = ConnectionType;
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
package model;

/**
 * Representa un Accesorio de tipo Control.
 * Extiende la clase Product para incluir atributos específicos de un Control.
 */
public class Memory extends Accessory {

    private String MemoryType;
    private int Storage;

    /**
     * Construye una nueva instancia de Memoria con los detalles especificados.
     *
     * @param identifier El identificador único del cable.
     * @param title El Nombre de la memoria.
     * @param price El precio de la memoria.
     * @param availableQuantity La cantidad disponible en inventario.
     * @param MemoryType la clase de conexion.
     */
    
    

    public Memory(String identifier, String title, double price, int availableQuantity, String brand, String MemoryType, int Storage) {
        super(identifier, title, price, availableQuantity, brand);
        this.MemoryType = MemoryType;
        this.Storage = Storage;
    }

    
    
    @Override
    public String getDescription() {
        return String.format("%s [Memory] | Price: $%.2f | Stock: %s | Brand %s | MemoryType %s | Storage %d",
        getTitle(), getPrice(), getAvailableQuantity(), getBrand(), getMemoryType(), getStorage());
    }

    public String getMemoryType() {
        return MemoryType;
    }

    public void setMemoryType(String MemoryType) {
        this.MemoryType = MemoryType;
    }

    public int getStorage() {
        return Storage;
    }

    public void setStorage(int Storage) {
        this.Storage = Storage;
    }

   
    
}
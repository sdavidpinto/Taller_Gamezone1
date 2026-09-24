package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un producto genérico en el sistema de inventario de GameZone.
 * Esta clase base abstracta define los atributos y métodos comunes para 
 * todos los tipos de productos del sistema.
 */
public abstract class Accessory extends Product{

    protected String brand;
    protected List<Product> compatible;
    /**
     * Construye una nueva instancia de Producto con los detalles especificados.
     * 
     * @param identifier El identificador único del producto.
     * @param title El título o nombre del producto.
     * @param price El precio unitario del producto.
     * @param availableQuantity La cantidad disponible en inventario.
     */
    public Accessory(String identifier, String title, double price, int availableQuantity,String brand) {
        super(identifier, title, price, availableQuantity);
        this.brand = brand;
        this.compatible=new ArrayList<>();
    }

    public Accessory( String identifier, String title, double price, int availableQuantity,String brand, List<Product> compatible) {
        super(identifier, title, price, availableQuantity);
        this.brand = brand;
        this.compatible = compatible;
    }

    public List<Product> getCompatible() {
        return compatible;
    }

    public void setCompatible(List<Product> compatible) {
        this.compatible = compatible;
    }

    @Override
    public int getAvailableQuantity() {
        return super.getAvailableQuantity(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void setAvailableQuantity(int availableQuantity) {
        super.setAvailableQuantity(availableQuantity); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    
    
    /**
     * Genera agrega los productos compatibles a el accesorio.
     */
    public abstract void addProduct(Product p);
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
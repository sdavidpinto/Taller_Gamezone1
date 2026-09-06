package persistence;

import model.Product;
import java.util.List;

/**
 * Interfaz que define el contrato para el repositorio de productos.
 * Proporciona los métodos básicos para realizar operaciones CRUD 
 * (Crear, Leer, Actualizar, Eliminar) sobre los productos del inventario.
 */
public interface ProductRepository {

    /**
     * Guarda un nuevo producto en el repositorio.
     * 
     * @param product El producto a guardar.
     */
    void save(Product product);

    /**
     * Busca un producto por su identificador único.
     * 
     * @param identifier El identificador del producto a buscar.
     * @return El producto encontrado, o null si no existe.
     */
    Product findByIdentifier(String identifier);

    /**
     * Obtiene una lista con todos los productos almacenados en el repositorio.
     * 
     * @return Una lista de productos.
     */
    List<Product> findAll();

    /**
     * Actualiza la información de un producto existente.
     * 
     * @param product El producto con los datos actualizados.
     * @return true si el producto se actualizó correctamente, false si no se encontró.
     */
    boolean update(Product product);

    /**
     * Elimina un producto del repositorio usando su identificador.
     * 
     * @param identifier El identificador del producto a eliminar.
     * @return true si el producto se eliminó correctamente, false si no se encontró.
     */
    boolean deleteByIdentifier(String identifier);
}
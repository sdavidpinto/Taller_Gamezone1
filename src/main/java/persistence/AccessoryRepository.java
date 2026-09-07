package persistence;

import model.Accessory;
import java.util.List;

/**
 * Interfaz que define el contrato para el repositorio de accesorios.
 * Proporciona los métodos básicos para realizar operaciones CRUD 
 * (Crear, Leer, Actualizar, Eliminar) sobre los productos del inventario.
 */
public interface AccessoryRepository {
    
    /**
     * Guarda un nuevo accesorio en el repositorio.
     * 
     * @param accessory El accesorio a guardar.
     */
    void save(Accessory accessory);

    /**
     * Busca un accesorio por su identificador único.
     * 
     * @param identifier El identificador del accesorio a buscar.
     * @return El accesorio encontrado, o null si no existe.
     */
    Accessory findByIdentifier(String identifier);

    /**
     * Obtiene una lista con todos los accesorios almacenados en el repositorio.
     * 
     * @return Una lista de accesorios.
     */
    List<Accessory> findAll();

    /**
     * Actualiza la información de un accesorio existente.
     * 
     * @param accessory El accesorio con los datos actualizados.
     * @return true si el accesorio se actualizó correctamente, false si no se encontró.
     */
    boolean update(Accessory accessory);

    /**
     * Elimina un accesorio del repositorio usando su identificador.
     * 
     * @param identifier El identificador del accesorio a eliminar.
     * @return true si el accesorio se eliminó correctamente, false si no se encontró.
     */
    boolean deleteByIdentifier(String identifier);
    
}

package services;

import model.Accessory;
import persistence.AccessoryRepository;

import java.util.List;

/**
 * Capa de servicios para Accessory. Recibe la interfaz AccessoryRepository
 * por inyección de dependencias (constructor), nunca crea su propia
 * implementación concreta.
 */
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;

    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Valida que un campo obligatorio no sea nulo ni esté en blanco.
     *
     * @param value el valor a validar
     * @param fieldName el nombre del campo, usado en el mensaje de error
     * @throws IllegalArgumentException si el valor es nulo o está en blanco
     */
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio.");
        }
    }

    /**
     * Registra un nuevo accesorio después de validar que no exista ya
     * y que su cantidad en inventario no sea negativa.
     *
     * @param accessory el accesorio a registrar
     * @throws IllegalArgumentException si el accesorio es nulo, tiene un
     *         identificador inválido, una cantidad negativa, o ya existe
     *         en el repositorio
     */
    public void registerAccessory(Accessory accessory) {
        if (accessory == null) {
            throw new IllegalArgumentException("El accesorio no puede ser nulo.");
        }
        requireNonBlank(accessory.getIdentifier(), "identifier");
        requireNonBlank(accessory.getTitle(), "title");
        if (accessory.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa.");
        }
        if (accessoryRepository.findByIdentifier(accessory.getIdentifier()) != null) {
            throw new IllegalArgumentException("Ya existe un accesorio con identifier: " + accessory.getIdentifier());
        }
        accessoryRepository.save(accessory);
    }

    /**
     * Busca un accesorio por su identificador único.
     *
     * @param identifier el identificador a buscar
     * @return el accesorio con ese identificador, o null si no se encuentra
     */
    public Accessory findByIdentifier(String identifier) {
        return accessoryRepository.findByIdentifier(identifier);
    }

    /**
     * Retorna todos los accesorios registrados.
     *
     * @return una lista con todos los accesorios
     */
    public List<Accessory> findAll() {
        return accessoryRepository.findAll();
    }

    /**
     * Actualiza la cantidad disponible en inventario de un accesorio existente.
     * Esta es la operación que se usa cuando una venta reduce el inventario.
     *
     * @param identifier el identificador del accesorio a actualizar
     * @param newQuantity la nueva cantidad disponible
     * @return true si el accesorio se encontró y actualizó, false en caso contrario
     * @throws IllegalArgumentException si la nueva cantidad es negativa
     */
    public boolean updateStock(String identifier, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa.");
        }
        Accessory existingAccessory = accessoryRepository.findByIdentifier(identifier);
        if (existingAccessory == null) {
            return false;
        }
        existingAccessory.setAvailableQuantity(newQuantity);
        return accessoryRepository.update(existingAccessory);
    }

    /**
     * Verifica si hay suficiente inventario disponible para vender la
     * cantidad solicitada.
     *
     * @param identifier el identificador del accesorio a verificar
     * @param requestedQuantity la cantidad solicitada para una venta
     * @return true si hay suficiente inventario, false en caso contrario
     *         (incluyendo si el accesorio no existe)
     */
    public boolean hasEnoughStock(String identifier, int requestedQuantity) {
        Accessory accessory = accessoryRepository.findByIdentifier(identifier);
        return accessory != null && accessory.getAvailableQuantity() >= requestedQuantity;
    }

    /**
     * Elimina un accesorio por su identificador único.
     *
     * @param identifier el identificador del accesorio a eliminar
     * @return true si el accesorio se encontró y eliminó, false en caso contrario
     */
    public boolean deleteAccessory(String identifier) {
        return accessoryRepository.deleteByIdentifier(identifier);
    }
}
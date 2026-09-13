package services;

import model.Accessory;
import persistence.AccessoryRepository;

import java.util.List;

/**
 * Service layer for Accessory. Receives the AccessoryRepository interface
 * through constructor injection, never creates its own concrete implementation.
 */
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;

    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Validates that a required field is not null or blank.
     *
     * @param value the value to validate
     * @param fieldName the name of the field, used in the error message
     * @throws IllegalArgumentException if the value is null or blank
     */
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The field " + fieldName + " is required.");
        }
    }

    /**
     * Registers a new accessory after validating that it does not already
     * exist and that its stock quantity is not negative.
     *
     * @param accessory the accessory to register
     * @throws IllegalArgumentException if the accessory is null, has an
     *         invalid identifier, a negative stock quantity, or already
     *         exists in the repository
     */
    public void registerAccessory(Accessory accessory) {
        if (accessory == null) {
            throw new IllegalArgumentException("The accessory cannot be null.");
        }
        requireNonBlank(accessory.getIdentifier(), "identifier");
        requireNonBlank(accessory.getTitle(), "title");
        if (accessory.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException("The available quantity cannot be negative.");
        }
        if (accessoryRepository.findByIdentifier(accessory.getIdentifier()) != null) {
            throw new IllegalArgumentException("An accessory already exists with identifier: " + accessory.getIdentifier());
        }
        accessoryRepository.save(accessory);
    }

    /**
     * Finds an accessory by its unique identifier.
     *
     * @param identifier the identifier to search for
     * @return the accessory with the given identifier, or null if not found
     */
    public Accessory findByIdentifier(String identifier) {
        return accessoryRepository.findByIdentifier(identifier);
    }

    /**
     * Returns all registered accessories.
     *
     * @return a list containing all accessories
     */
    public List<Accessory> findAll() {
        return accessoryRepository.findAll();
    }

    /**
     * Updates the available stock quantity of an existing accessory.
     * This is the operation used when a sale reduces inventory.
     *
     * @param identifier the identifier of the accessory to update
     * @param newQuantity the new available quantity
     * @return true if the accessory was found and updated, false otherwise
     * @throws IllegalArgumentException if the new quantity is negative
     */
    public boolean updateStock(String identifier, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("The available quantity cannot be negative.");
        }
        Accessory existingAccessory = accessoryRepository.findByIdentifier(identifier);
        if (existingAccessory == null) {
            return false;
        }
        existingAccessory.setAvailableQuantity(newQuantity);
        return accessoryRepository.update(existingAccessory);
    }

    /**
     * Checks whether there is enough available stock to sell the given quantity.
     *
     * @param identifier the identifier of the accessory to check
     * @param requestedQuantity the quantity requested for a sale
     * @return true if there is enough stock, false otherwise (including if
     *         the accessory does not exist)
     */
    public boolean hasEnoughStock(String identifier, int requestedQuantity) {
        Accessory accessory = accessoryRepository.findByIdentifier(identifier);
        return accessory != null && accessory.getAvailableQuantity() >= requestedQuantity;
    }

    /**
     * Deletes an accessory by its unique identifier.
     *
     * @param identifier the identifier of the accessory to delete
     * @return true if the accessory was found and deleted, false otherwise
     */
    public boolean deleteAccessory(String identifier) {
        return accessoryRepository.deleteByIdentifier(identifier);
    }
}
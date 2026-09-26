package persistence;

import model.Warranty;
import java.util.List;

/**
 * Defines the data access contract for the Warranty entity.
 * Warranties are persisted as a whole collection (save/load all).
 */
public interface WarrantyRepository {

    /**
     * Saves the complete list of warranties, replacing any previously
     * stored data.
     *
     * @param warranties the list of warranties to save
     */
    void saveAll(List<Warranty> warranties);

    /**
     * Loads all warranties from persistent storage.
     * Returns an empty list if the data file does not exist yet.
     *
     * @return the list of warranties loaded from storage
     */
    List<Warranty> loadAll();
}
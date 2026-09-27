package persistence;

import model.Return;
import java.util.List;

/**
 * Defines the data access contract for the Return entity.
 * Returns are persisted as a whole collection (save/load all).
 */
public interface ReturnRepository {

    /**
     * Saves the complete list of returns, replacing any previously
     * stored data.
     *
     * @param returns the list of returns to save
     */
    void saveAll(List<Return> returns);

    /**
     * Loads all returns from persistent storage.
     * Returns an empty list if the data file does not exist yet.
     *
     * @return the list of returns loaded from storage
     */
    List<Return> loadAll();
}
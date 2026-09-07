package persistence;

import model.Memory;
import model.Accessory;
import model.Cable;
import model.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;

public class AccessoryRepositoryFile implements AccessoryRepository{
    
    private final String filePath;

    public ProductRepositoryFile(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Si la carpeta contenedora (por ejemplo "data/") no existe todavía, la crea,
     * para que no falle al intentar crear el archivo dentro de ella.
     */
    private void createFileIfNotExists() {
        File archivo = new File(filePath);
        File parent = archivo.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }
    
    @Override
    public void save(Accessory accessory) {
        if (accessory == null) {
            throw new IllegalArgumentException("El accesorio no puede ser nulo");
        }
        if (findByIdentifier(accessory.getIdentifier()) != null) {
            throw new IllegalArgumentException("Ya existe un accesorio con identifier: " + accessory.getIdentifier());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(accessory));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando accesorio en: " + filePath, e);
        }
    }
    
}

package persistence;

import model.Memory;
import model.Accessory;
import model.Cable;
import model.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de AccessoryRepository basada en archivos.
 * Guarda y recupera accesorios (Cable, Controller, Memory) desde un archivo de texto.
 */
public class AccessoryRepositoryFile implements AccessoryRepository {

    private final String filePath;

    public AccessoryRepositoryFile(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Crea la carpeta contenedora (por ejemplo "data/") si aún no existe.
     */
    private void createFileIfNotExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    /**
     * Guarda un nuevo accesorio en el archivo.
     *
     * @param accessory el accesorio a guardar
     * @throws IllegalArgumentException si el accesorio es nulo o si ya
     *         existe un accesorio con el mismo identificador
     */
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

    /**
     * Busca un accesorio por su identificador único.
     *
     * @param identifier el identificador a buscar
     * @return el accesorio con ese identificador, o null si no se encuentra
     */
    @Override
    public Accessory findByIdentifier(String identifier) {
        for (Accessory a : findAll()) {
            if (a.getIdentifier() != null && a.getIdentifier().equals(identifier)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Retorna todos los accesorios almacenados en el archivo.
     *
     * @return una lista con todos los accesorios
     */
    @Override
    public List<Accessory> findAll() {
        List<Accessory> accessories = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    accessories.add(parseLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo accesorios de: " + filePath, e);
        }
        return accessories;
    }

    /**
     * Actualiza la información de un accesorio existente.
     *
     * @param accessory el accesorio con los datos actualizados
     * @return true si se encontró y actualizó, false en caso contrario
     */
    @Override
    public boolean update(Accessory accessory) {
        List<Accessory> accessories = findAll();
        boolean found = false;
        for (int i = 0; i < accessories.size(); i++) {
            if (accessories.get(i).getIdentifier().equals(accessory.getIdentifier())) {
                accessories.set(i, accessory);
                found = true;
                break;
            }
        }
        if (found) {
            rewriteFile(accessories);
        }
        return found;
    }

    /**
     * Elimina un accesorio por su identificador único.
     *
     * @param identifier el identificador del accesorio a eliminar
     * @return true si se encontró y eliminó, false en caso contrario
     */
    @Override
    public boolean deleteByIdentifier(String identifier) {
        List<Accessory> accessories = findAll();
        boolean deleted = accessories.removeIf(a -> a.getIdentifier() != null && a.getIdentifier().equals(identifier));
        if (deleted) {
            rewriteFile(accessories);
        }
        return deleted;
    }

    /**
     * Reescribe todo el archivo con la lista de accesorios dada.
     * Se usa como estrategia simple para reflejar actualizaciones y eliminaciones.
     *
     * @param accessories la lista completa de accesorios a escribir
     */
    private void rewriteFile(List<Accessory> accessories) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Accessory a : accessories) {
                bw.write(toLine(a));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reescribiendo accesorios en: " + filePath, e);
        }
    }

    /**
     * Convierte un accesorio en una línea de texto para persistirlo,
     * incluyendo una etiqueta de tipo para poder reconstruirlo después.
     *
     * @param a el accesorio a convertir
     * @return la línea de texto correspondiente
     * @throws IllegalArgumentException si el tipo de accesorio no es soportado
     */
    private String toLine(Accessory a) {
        if (a instanceof Cable ca) {
            return "type: Cable"
                    + "; identifier: " + ca.getIdentifier()
                    + "; title: " + ca.getTitle()
                    + "; price: " + ca.getPrice()
                    + "; stock: " + ca.getAvailableQuantity()
                    + "; brand: " + ca.getBrand()
                    + "; connectionType: " + ca.getConnectionType()
                    + "; length: " + ca.getLength();
        } else if (a instanceof Controller co) {
            return "type: Controller"
                    + "; identifier: " + co.getIdentifier()
                    + "; title: " + co.getTitle()
                    + "; price: " + co.getPrice()
                    + "; stock: " + co.getAvailableQuantity()
                    + "; brand: " + co.getBrand()
                    + "; wired: " + co.isAlambric();
        } else if (a instanceof Memory m) {
            return "type: Memory"
                    + "; identifier: " + m.getIdentifier()
                    + "; title: " + m.getTitle()
                    + "; price: " + m.getPrice()
                    + "; stock: " + m.getAvailableQuantity()
                    + "; brand: " + m.getBrand()
                    + "; memoryType: " + m.getMemoryType()
                    + "; storage: " + m.getStorage();
        }
        throw new IllegalArgumentException("Tipo de accesorio no soportado para persistencia: " + a.getClass());
    }

    /**
     * Reconstruye un objeto Accessory a partir de una línea de texto,
     * identificando el tipo concreto mediante la etiqueta "type".
     *
     * @param line la línea de texto a interpretar
     * @return el objeto Accessory reconstruido (Cable, Controller o Memory)
     * @throws IllegalStateException si el tipo indicado en la línea es desconocido
     */
    private Accessory parseLine(String line) {
        String[] parts = line.split(";");
        String type = value(parts[0]);
        String identifier = value(parts[1]);
        String title = value(parts[2]);
        double price = Double.parseDouble(value(parts[3]));
        int stock = Integer.parseInt(value(parts[4]));
        String brand = value(parts[5]);

        if (type.equals("Cable")) {
            String connectionType = value(parts[6]);
            int length = Integer.parseInt(value(parts[7]));
            return new Cable(identifier, title, price, stock, brand, connectionType, length);
        } else if (type.equals("Controller")) {
            boolean wired = Boolean.parseBoolean(value(parts[6]));
            return new Controller(identifier, title, price, stock, brand, wired);
        } else if (type.equals("Memory")) {
            String memoryType = value(parts[6]);
            int storage = Integer.parseInt(value(parts[7]));
            return new Memory(identifier, title, price, stock, brand, memoryType, storage);
        }
        throw new IllegalStateException("Tipo de producto desconocido en el archivo: " + type);
    }

    /**
     * Extrae el valor de un segmento con formato "clave: valor".
     *
     * @param part el segmento de texto a interpretar
     * @return el valor extraído, sin espacios al inicio o al final
     */
    private String value(String part) {
        return part.split(":", 2)[1].trim();
    }
}
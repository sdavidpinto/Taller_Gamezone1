package persistence;

import model.Memory;
import model.Accessory;
import model.Cable;
import model.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of AccessoryRepository.
 * Stores and retrieves accessories (Cable, Controller, Memory) from a text file.
 */
public class AccessoryRepositoryFile implements AccessoryRepository {

    private final String filePath;

    public AccessoryRepositoryFile(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    /**
     * Ensures the persistence file exists before using it.
     * Creates the parent folder (e.g. "data/") if it does not exist yet.
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
                throw new RuntimeException("Could not create file: " + filePath, e);
            }
        }
    }

    @Override
    public void save(Accessory accessory) {
        if (accessory == null) {
            throw new IllegalArgumentException("The accessory cannot be null");
        }
        if (findByIdentifier(accessory.getIdentifier()) != null) {
            throw new IllegalArgumentException("An accessory already exists with identifier: " + accessory.getIdentifier());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(accessory));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error saving accessory to: " + filePath, e);
        }
    }

    @Override
    public Accessory findByIdentifier(String identifier) {
        for (Accessory a : findAll()) {
            if (a.getIdentifier() != null && a.getIdentifier().equals(identifier)) {
                return a;
            }
        }
        return null;
    }

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
            throw new RuntimeException("Error reading accessories from: " + filePath, e);
        }
        return accessories;
    }

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

    @Override
    public boolean deleteByIdentifier(String identifier) {
        List<Accessory> accessories = findAll();
        boolean deleted = accessories.removeIf(a -> a.getIdentifier() != null && a.getIdentifier().equals(identifier));
        if (deleted) {
            rewriteFile(accessories);
        }
        return deleted;
    }

    private void rewriteFile(List<Accessory> accessories) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Accessory a : accessories) {
                bw.write(toLine(a));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error rewriting accessories in: " + filePath, e);
        }
    }

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
        throw new IllegalArgumentException("Unsupported accessory type for persistence: " + a.getClass());
    }

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
        throw new IllegalStateException("Unknown product type in file: " + type);
    }

    private String value(String part) {
        return part.split(":", 2)[1].trim();
    }
}
package persistence;

import model.BulkPurchaseDiscount;
import model.CategoryDiscount;
import model.PercentageDiscount;
import model.Promotion;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de PromotionRepository basada en archivos.
 * Guarda y recupera promociones desde un archivo CSV, usando una columna
 * discriminadora de tipo para distinguir entre los tres tipos concretos
 * al momento de cargar los datos.
 */
public class PromotionRepositoryFile implements PromotionRepository {

    private final String filePath;

    public PromotionRepositoryFile(String filePath) {
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
     * Guarda la lista completa de promociones en el archivo CSV,
     * sobrescribiendo cualquier contenido anterior.
     *
     * @param promotions la lista de promociones a guardar
     */
    @Override
    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Promotion promotion : promotions) {
                bw.write(toLine(promotion));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error guardando promociones en: " + filePath, e);
        }
    }
    
    /**
     * Carga todas las promociones desde el archivo CSV.
     * Retorna una lista vacía si el archivo no existe o está vacío.
     *
     * @return la lista de promociones cargadas desde el archivo
     */
    @Override
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return promotions;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    promotions.add(parseLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo promociones de: " + filePath, e);
        }
        return promotions;
    }
    
    /**
     * Convierte una promoción en una línea CSV, incluyendo un discriminador
     * de tipo como primera columna para poder reconstruirla correctamente
     * al momento de cargar.
     *
     * @param promotion la promoción a convertir
     * @return la línea CSV que representa la promoción
     * @throws IllegalArgumentException si el tipo de promoción no es soportado
     */
    
private String toLine(Promotion promotion) {
        if (promotion instanceof PercentageDiscount p) {
            return String.join(",",
                    "PERCENTAGE",
                    p.getId(),
                    p.getName(),
                    p.getStartDate().toString(),
                    p.getEndDate().toString(),
                    String.valueOf(p.getPercentage()));
        } else if (promotion instanceof CategoryDiscount c) {
            return String.join(",",
                    "CATEGORY",
                    c.getId(),
                    c.getName(),
                    c.getStartDate().toString(),
                    c.getEndDate().toString(),
                    c.getTargetCategory(),
                    String.valueOf(c.getPercentage()));
        } else if (promotion instanceof BulkPurchaseDiscount b) {
            return String.join(",",
                    "BULK",
                    b.getId(),
                    b.getName(),
                    b.getStartDate().toString(),
                    b.getEndDate().toString(),
                    String.valueOf(b.getMinQuantity()),
                    String.valueOf(b.getPercentage()));
        }
        throw new IllegalArgumentException("Tipo de promoción no soportado para persistencia: " + promotion.getClass());
    }

    /**
     * Reconstruye un objeto Promotion a partir de una línea CSV, usando el
     * discriminador de tipo en la primera columna para determinar la
     * clase concreta.
     *
     * @param line la línea CSV a interpretar
     * @return la promoción reconstruida (PercentageDiscount, CategoryDiscount
     *         o BulkPurchaseDiscount)
     * @throws IllegalStateException si el tipo indicado en la línea es desconocido
     */
    private Promotion parseLine(String line) {
        String[] parts = line.split(",");
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        if (type.equals("PERCENTAGE")) {
            double percentage = Double.parseDouble(parts[5]);
            return new PercentageDiscount(id, name, startDate, endDate, percentage);
        } else if (type.equals("CATEGORY")) {
            String targetCategory = parts[5];
            double percentage = Double.parseDouble(parts[6]);
            return new CategoryDiscount(id, name, startDate, endDate, targetCategory, percentage);
        } else if (type.equals("BULK")) {
            int minQuantity = Integer.parseInt(parts[5]);
            double percentage = Double.parseDouble(parts[6]);
            return new BulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
        }
        throw new IllegalStateException("Tipo de promoción desconocido en el archivo: " + type);
    }
}
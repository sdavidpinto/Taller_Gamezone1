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
    
}
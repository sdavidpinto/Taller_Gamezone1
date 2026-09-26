package services;

import model.BulkPurchaseDiscount;
import model.CategoryDiscount;
import model.PercentageDiscount;
import model.Promotion;
import model.Sale;
import persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de servicios para Promotion. Recibe la interfaz PromotionRepository
 * por inyección de dependencias (constructor), y contiene la lógica de
 * negocio para registrar promociones y seleccionar la mejor promoción
 * aplicable a una venta.
 */
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    /**
     * Valida que un campo obligatorio no sea nulo ni esté en blanco.
     */
    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + fieldName + " es obligatorio.");
        }
    }

    /**
     * Verifica que no exista ya una promoción con el mismo id.
     */
    private void requireUniqueId(String id) {
        if (findById(id) != null) {
            throw new IllegalArgumentException("Ya existe una promoción con id: " + id);
        }
    }

    /**
     * Registra una nueva promoción de tipo porcentaje.
     *
     * @param id el identificador único de la promoción
     * @param name el nombre de la promoción
     * @param startDate la fecha desde la que es válida
     * @param endDate la fecha hasta la que es válida
     * @param percentage el porcentaje de descuento (mayor a 0 y hasta 100)
     * @throws IllegalArgumentException si los datos son inválidos o el id ya existe
     */
    public void registerPercentageDiscount(String id, String name, LocalDate startDate,
                                            LocalDate endDate, double percentage) {
        requireNonBlank(id, "id");
        requireNonBlank(name, "name");
        requireUniqueId(id);
        PercentageDiscount promotion = new PercentageDiscount(id, name, startDate, endDate, percentage);
        saveNewPromotion(promotion);
    }

    /**
     * Registra una nueva promoción de tipo categoría.
     *
     * @param id el identificador único de la promoción
     * @param name el nombre de la promoción
     * @param startDate la fecha desde la que es válida
     * @param endDate la fecha hasta la que es válida
     * @param targetCategory la categoría objetivo ("VIDEOGAME" o "CONSOLE")
     * @param percentage el porcentaje de descuento (mayor a 0 y hasta 100)
     * @throws IllegalArgumentException si los datos son inválidos o el id ya existe
     */
    public void registerCategoryDiscount(String id, String name, LocalDate startDate,
                                          LocalDate endDate, String targetCategory, double percentage) {
        requireNonBlank(id, "id");
        requireNonBlank(name, "name");
        requireUniqueId(id);
        if (!CategoryDiscount.isValidCategory(targetCategory)) {
            throw new IllegalArgumentException("Categoria no permitida para promociones: " + targetCategory);
        }
        CategoryDiscount promotion = new CategoryDiscount(id, name, startDate, endDate, targetCategory, percentage);
        saveNewPromotion(promotion);
    }

    /**
     * Registra una nueva promoción de tipo volumen de compra.
     *
     * @param id el identificador único de la promoción
     * @param name el nombre de la promoción
     * @param startDate la fecha desde la que es válida
     * @param endDate la fecha hasta la que es válida
     * @param minQuantity la cantidad mínima de productos requerida
     * @param percentage el porcentaje de descuento (mayor a 0 y hasta 100)
     * @throws IllegalArgumentException si los datos son inválidos o el id ya existe
     */
    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate,
                                              LocalDate endDate, int minQuantity, double percentage) {
        requireNonBlank(id, "id");
        requireNonBlank(name, "name");
        requireUniqueId(id);
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
        saveNewPromotion(promotion);
    }

    /**
     * Agrega una promoción nueva a la lista actual y persiste la lista completa.
     *
     * @param promotion la promoción ya construida y validada a guardar
     */
    private void saveNewPromotion(Promotion promotion) {
        List<Promotion> promotions = promotionRepository.loadAll();
        promotions.add(promotion);
        promotionRepository.saveAll(promotions);
    }

    /**
     * Retorna todas las promociones registradas.
     *
     * @return una lista con todas las promociones
     */
    public List<Promotion> listAllPromotions() {
        return promotionRepository.loadAll();
    }

    /**
     * Retorna las promociones vigentes en la fecha actual.
     *
     * @return una lista con las promociones cuya fecha actual está dentro
     *         de su rango de vigencia
     */
    public List<Promotion> listActivePromotions() {
        List<Promotion> activePromotions = new ArrayList<>();
        for (Promotion promotion : promotionRepository.loadAll()) {
            if (promotion.isActive()) {
                activePromotions.add(promotion);
            }
        }
        return activePromotions;
    }

    /**
     * Busca una promoción por su identificador.
     *
     * @param id el identificador a buscar
     * @return la promoción con ese id, o null si no se encuentra
     */
    public Promotion findById(String id) {
        for (Promotion promotion : promotionRepository.loadAll()) {
            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }
        return null;
    }

    /**
     * Entre las promociones vigentes, encuentra la que otorgaría el mayor
     * descuento monetario a la venta indicada. Las promociones no son
     * acumulables: solo se selecciona una.
     *
     * @param sale la venta sobre la que se evalúan las promociones
     * @return la promoción con el mayor descuento, o null si ninguna
     *         promoción vigente aplica o el descuento máximo es cero
     */
    public Promotion findBestPromotionFor(Sale sale) {
        Promotion bestPromotion = null;
        double bestDiscount = 0;

        for (Promotion promotion : listActivePromotions()) {
            double discount = promotion.calculateDiscount(sale.getProducts());
            if (discount > bestDiscount) {
                bestDiscount = discount;
                bestPromotion = promotion;
            }
        }
        return bestPromotion;
    }
}
package services;

import model.BasicWarranty;
import model.ExtendedWarranty;
import model.Product;
import model.Sale;
import model.Warranty;
import persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Capa de servicios para Warranty. Recibe la interfaz WarrantyRepository
 * por inyección de dependencias (constructor), y contiene la lógica de
 * negocio para asignar garantías y consultar su vigencia.
 */
public class WarrantyService {

    private final WarrantyRepository warrantyRepository;

    public WarrantyService(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }

    /**
     * Genera un identificador único para una nueva garantía.
     *
     * @return un identificador generado aleatoriamente
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Agrega una garantía nueva a la lista actual y persiste la lista completa.
     *
     * @param warranty la garantía a agregar
     */
    private void saveNewWarranty(Warranty warranty) {
        List<Warranty> warranties = warrantyRepository.loadAll();
        warranties.add(warranty);
        warrantyRepository.saveAll(warranties);
    }
    
        /**
     * Crea y persiste una garantía básica automática para el producto y
     * la venta indicados.
     *
     * @param product el producto cubierto por la garantía
     * @param sale la venta en la que se compró el producto
     * @param startDate la fecha desde la que empieza la cobertura
     * @return la BasicWarranty recién creada
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        BasicWarranty warranty = new BasicWarranty(generateId(), product, sale, startDate);
        saveNewWarranty(warranty);
        return warranty;
    }

    /**
     * Crea y persiste una garantía extendida para el producto y la venta
     * indicados.
     *
     * @param product el producto cubierto por la garantía
     * @param sale la venta en la que se compró el producto
     * @param startDate la fecha desde la que empieza la cobertura
     * @return la ExtendedWarranty recién creada
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        ExtendedWarranty warranty = new ExtendedWarranty(generateId(), product, sale, startDate);
        saveNewWarranty(warranty);
        return warranty;
    }
    
}
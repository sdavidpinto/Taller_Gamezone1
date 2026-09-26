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
}
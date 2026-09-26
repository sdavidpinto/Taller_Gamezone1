package model;

import java.time.LocalDate;

/**
 * Garantia basica que se asigna automaticamente, sin costo, cuando se
 * vende una consola. Dura 6 meses desde la fecha de venta.
 */
public class BasicWarranty extends Warranty {

    /**
     * Crea una garantia basica.
     *
     * @param id id de la garantia
     * @param product el producto cubierto
     * @param sale la venta en la que se genero
     * @param startDate desde que fecha empieza a correr
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    /** @return 6, la garantia basica dura 6 meses */
    @Override
    public int getDurationInMonths() { return 6; }

    /** @return "Garantia Basica" */
    @Override
    public String getWarrantyType() { return "Garantia Basica"; }

    /** @return 0.0, la garantia basica no tiene costo adicional */
    @Override
    public double getAdditionalCost() { return 0.0; }
}
package model;

import java.time.LocalDate;

/**
 * Garantia extendida que el cliente compra de forma opcional al momento
 * de la venta. Dura 12 meses y cuesta el 10% del precio del producto
 * cubierto.
 */
public class ExtendedWarranty extends Warranty {

    /** Porcentaje del precio del producto que cuesta la garantia extendida. */
    public static final double COST_PERCENTAGE = 0.10;

    /**
     * Crea una garantia extendida.
     *
     * @param id id de la garantia
     * @param product el producto cubierto
     * @param sale la venta en la que se genero
     * @param startDate desde que fecha empieza a correr
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    /** @return 12, la garantia extendida dura 12 meses */
    @Override
    public int getDurationInMonths() { return 12; }

    /** @return "Garantia Extendida" */
    @Override
    public String getWarrantyType() { return "Garantia Extendida"; }

    /** @return el 10% del precio del producto cubierto */
    @Override
    public double getAdditionalCost() { return getProduct().getPrice() * COST_PERCENTAGE; }
}
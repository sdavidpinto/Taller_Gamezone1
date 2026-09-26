package model;

import java.time.LocalDate;

/**
 * Garantia basica que se asigna automaticamente, sin costo, cuando se
 * vende una consola. Dura un tiempo fijo definido por la tienda.
 */
public class BasicWarranty extends Warranty {

    /** Duracion fija de la garantia basica, en meses. */
    public static final int DEFAULT_DURATION_MONTHS = 12;

    /**
     * Crea una garantia basica con la duracion por defecto de la tienda.
     *
     * @param id id de la garantia
     * @param product el producto cubierto
     * @param sale la venta en la que se genero
     * @param startDate desde que fecha empieza a correr
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate, DEFAULT_DURATION_MONTHS);
    }

    /**
     * Arma el texto del certificado de esta garantia basica.
     *
     * @return el texto del certificado
     */
    @Override
    public String generateWarrantyCertificate() {
        return String.format(
                "Certificado de garantia basica #%s%nProducto: %s%nVenta: %s%nCubre desde %s hasta %s (sin costo)",
                getId(), getProduct().getTitle(), getSale().getCode(), getStartDate(), getEndDate());
    }
}

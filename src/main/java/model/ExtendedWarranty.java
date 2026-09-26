package model;

import java.time.LocalDate;

/**
 * Garantia extendida que el cliente compra de forma opcional al momento
 * de la venta, con una duracion y un costo adicional definidos por el
 * cliente.
 */
public class ExtendedWarranty extends Warranty {

    private final double cost;

    /**
     * Crea una garantia extendida.
     *
     * @param id id de la garantia
     * @param product el producto cubierto
     * @param sale la venta en la que se genero
     * @param startDate desde que fecha empieza a correr
     * @param durationMonths cuantos meses adicionales dura, al menos 1
     * @param cost el costo adicional que paga el cliente, debe ser mayor a 0
     * @throws IllegalArgumentException si el costo no es mayor a 0
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate,
                            int durationMonths, double cost) {
        super(id, product, sale, startDate, durationMonths);
        if (cost <= 0) {
            throw new IllegalArgumentException("El costo de la garantia extendida debe ser mayor a 0.");
        }
        this.cost = cost;
    }

    /** @return el costo adicional de esta garantia extendida */
    public double getCost() { return cost; }

    /**
     * Arma el texto del certificado de esta garantia extendida.
     *
     * @return el texto del certificado
     */
    @Override
    public String generateWarrantyCertificate() {
        return String.format(
                "Certificado de garantia extendida #%s%nProducto: %s%nVenta: %s%nCubre desde %s hasta %s | Costo: $%.2f",
                getId(), getProduct().getTitle(), getSale().getCode(), getStartDate(), getEndDate(), cost);
    }
}

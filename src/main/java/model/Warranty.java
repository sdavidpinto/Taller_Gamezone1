package model;

import java.time.LocalDate;

/**
 * Clase base para todas las garantias de la tienda.
 * Guarda a que producto y a que venta pertenece la garantia, desde cuando
 * empieza a correr y hasta cuando dura. La duracion, el tipo y el costo
 * adicional los define cada subclase (basica o extendida).
 */
public abstract class Warranty {

    private final String id;
    private final Product product;
    private final Sale sale;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Crea una garantia nueva. La fecha de fin se calcula automaticamente
     * llamando a getDurationInMonths(), que cada subclase implementa.
     *
     * @param id un identificador para la garantia, no puede estar vacio
     * @param product el producto cubierto, no puede ser nulo
     * @param sale la venta en la que se genero la garantia, no puede ser nula
     * @param startDate desde que fecha empieza a correr la garantia
     * @throws IllegalArgumentException si algun dato es invalido
     */
    protected Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El campo id de la garantia es obligatorio.");
        }
        if (product == null) {
            throw new IllegalArgumentException("La garantia debe estar asociada a un producto.");
        }
        if (sale == null) {
            throw new IllegalArgumentException("La garantia debe estar asociada a una venta.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("La garantia debe tener una fecha de inicio.");
        }
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /** @return el id de la garantia */
    public String getId() { return id; }

    /** @return el producto cubierto por la garantia */
    public Product getProduct() { return product; }

    /** @return la venta en la que se genero la garantia */
    public Sale getSale() { return sale; }

    /** @return la fecha desde la que empieza a correr la garantia */
    public LocalDate getStartDate() { return startDate; }

    /** @return la fecha en la que vence la garantia */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Cuantos meses dura este tipo de garantia. Cada subclase devuelve un
     * numero fijo (por ejemplo, 6 para basica o 12 para extendida).
     *
     * @return la duracion en meses
     */
    public abstract int getDurationInMonths();

    /**
     * El nombre de este tipo de garantia, para mostrar al usuario.
     *
     * @return el nombre del tipo de garantia
     */
    public abstract String getWarrantyType();

    /**
     * Cuanto le cuesta esta garantia al cliente, ademas del producto.
     *
     * @return el costo adicional (0 si la garantia no tiene costo)
     */
    public abstract double getAdditionalCost();

    /**
     * Dice si la garantia esta vigente en una fecha especifica.
     * El dia de inicio y el dia de fin tambien cuentan como validos.
     *
     * @param date la fecha que se quiere revisar
     * @return true si esa fecha esta dentro del rango de la garantia
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Arma el texto del certificado de garantia, para mostrarle al cliente.
     * Sirve para cualquier tipo de garantia, porque usa getWarrantyType()
     * y getAdditionalCost() para completar los datos que cambian.
     *
     * @return el texto del certificado
     */
    public String generateWarrantyCertificate() {
        return String.format(
                "Certificado de garantia #%s%nTipo: %s%nProducto: %s%nVenta: %s%nVigencia: %s a %s%nCosto adicional: $%.2f",
                id, getWarrantyType(), product.getTitle(), sale.getCode(), startDate, endDate, getAdditionalCost());
    }
}
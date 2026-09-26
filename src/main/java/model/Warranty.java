package model;

import java.time.LocalDate;

/**
 * Clase base para todas las garantias de la tienda.
 * Guarda a que producto y a que venta pertenece la garantia, desde cuando
 * empieza a correr y hasta cuando dura. Cada tipo de garantia (basica o
 * extendida) hereda de esta clase y define su propio certificado.
 */
public abstract class Warranty {

    private final String id;
    private final Product product;
    private final Sale sale;
    private final LocalDate startDate;
    private final int durationMonths;
    private final LocalDate endDate;

    /**
     * Crea una garantia nueva. La fecha de fin se calcula automaticamente
     * sumando la duracion (en meses) a la fecha de inicio.
     *
     * @param id un identificador para la garantia, no puede estar vacio
     * @param product el producto cubierto, no puede ser nulo
     * @param sale la venta en la que se genero la garantia, no puede ser nula
     * @param startDate desde que fecha empieza a correr la garantia
     * @param durationMonths cuantos meses dura la garantia, debe ser al menos 1
     * @throws IllegalArgumentException si algun dato es invalido
     */
    protected Warranty(String id, Product product, Sale sale, LocalDate startDate, int durationMonths) {
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
        if (durationMonths < 1) {
            throw new IllegalArgumentException("La duracion de la garantia debe ser al menos 1 mes.");
        }
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
        this.endDate = startDate.plusMonths(durationMonths);
    }

    /** @return el id de la garantia */
    public String getId() { return id; }

    /** @return el producto cubierto por la garantia */
    public Product getProduct() { return product; }

    /** @return la venta en la que se genero la garantia */
    public Sale getSale() { return sale; }

    /** @return la fecha desde la que empieza a correr la garantia */
    public LocalDate getStartDate() { return startDate; }

    /** @return la duracion de la garantia, en meses */
    public int getDurationMonths() { return durationMonths; }

    /** @return la fecha en la que vence la garantia */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Dice si la garantia sigue vigente hoy.
     *
     * @return true si hoy cae dentro del rango de la garantia
     */
    public boolean isActive() {
        return isActive(LocalDate.now());
    }

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
     *
     * @return el texto del certificado
     */
    public abstract String generateWarrantyCertificate();
}

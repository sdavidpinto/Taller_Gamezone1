package model;

import java.time.LocalDate;

public abstract class Warranty {

    private final String id;
    private final Product product;
    private final Sale sale;
    private final LocalDate startDate;
    private final int durationMonths;
    private final LocalDate endDate;

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

    public String getId() { return id; }

    public Product getProduct() { return product; }

    public Sale getSale() { return sale; }

    public LocalDate getStartDate() { return startDate; }

    public int getDurationMonths() { return durationMonths; }

    public LocalDate getEndDate() { return endDate; }
}

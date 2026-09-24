package model;

import java.time.LocalDate;
import java.util.List;

public abstract class Promotion {

    private final String id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;

    protected Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El campo id de la promocion es obligatorio.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El campo name de la promocion es obligatorio.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("La promocion debe tener fecha de inicio y de fin.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    public abstract double calculateDiscount(List<Product> products);

    public abstract String getDescription();
}

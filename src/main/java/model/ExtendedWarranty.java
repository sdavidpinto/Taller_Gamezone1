package model;

import java.time.LocalDate;

public class ExtendedWarranty extends Warranty {

    private final double cost;

    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate,
                            int durationMonths, double cost) {
        super(id, product, sale, startDate, durationMonths);
        if (cost <= 0) {
            throw new IllegalArgumentException("El costo de la garantia extendida debe ser mayor a 0.");
        }
        this.cost = cost;
    }

    public double getCost() { return cost; }

    @Override
    public String generateWarrantyCertificate() {
        return String.format(
                "Certificado de garantia extendida #%s%nProducto: %s%nVenta: %s%nCubre desde %s hasta %s | Costo: $%.2f",
                getId(), getProduct().getTitle(), getSale().getCode(), getStartDate(), getEndDate(), cost);
    }
}

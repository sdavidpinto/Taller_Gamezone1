package model;

import java.time.LocalDate;

public class BasicWarranty extends Warranty {

    public static final int DEFAULT_DURATION_MONTHS = 12;

    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate, DEFAULT_DURATION_MONTHS);
    }

    @Override
    public String generateWarrantyCertificate() {
        return String.format(
                "Certificado de garantia basica #%s%nProducto: %s%nVenta: %s%nCubre desde %s hasta %s (sin costo)",
                getId(), getProduct().getTitle(), getSale().getCode(), getStartDate(), getEndDate());
    }
}

package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase base para todas las promociones de la tienda.
 * Aqui se guardan los datos que toda promocion necesita: un id, un nombre
 * y las fechas entre las que es valida. Cada tipo de promocion (por
 * porcentaje, por categoria, por cantidad) hereda de esta clase y define
 * su propia forma de calcular el descuento.
 */
public abstract class Promotion {

    private final String id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Crea una promocion nueva.
     *
     * @param id un identificador para la promocion, no puede estar vacio
     * @param name el nombre de la promocion, no puede estar vacio
     * @param startDate desde que fecha es valida (incluida)
     * @param endDate hasta que fecha es valida (incluida)
     * @throws IllegalArgumentException si algun dato es invalido, por ejemplo
     *         si la fecha de fin es antes que la de inicio
     */
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

    /** @return el id de la promocion */
    public String getId() { return id; }

    /** @return el nombre de la promocion */
    public String getName() { return name; }

    /** @return la fecha desde la que es valida */
    public LocalDate getStartDate() { return startDate; }

    /** @return la fecha hasta la que es valida */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Dice si la promocion esta vigente hoy.
     *
     * @return true si hoy cae dentro del rango de fechas
     */
    public boolean isActive() {
        return isActive(LocalDate.now());
    }

    /**
     * Dice si la promocion esta vigente en una fecha especifica.
     * El dia de inicio y el dia de fin tambien cuentan como validos.
     *
     * @param date la fecha que se quiere revisar
     * @return true si esa fecha esta dentro del rango de la promocion
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calcula cuanto dinero se descuenta segun esta promocion.
     * Ojo: este metodo no revisa si la promocion esta vigente,
     * para eso se usa isActive().
     *
     * @param products la lista de productos de la venta
     * @return el monto a descontar (nunca es negativo)
     */
    public abstract double calculateDiscount(List<Product> products);

    /**
     * Arma un texto para mostrar los datos de la promocion.
     *
     * @return el texto con la descripcion
     */
    public abstract String getDescription();
}

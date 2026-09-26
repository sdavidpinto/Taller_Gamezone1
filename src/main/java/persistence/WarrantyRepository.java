package persistence;

import model.Product;
import model.Sale;
import model.Warranty;

import java.util.List;
import java.util.function.Function;

/**
 * Contrato de acceso a datos para la entidad Warranty.
 * loadAll recibe funciones de resolución en lugar de repositorios concretos,
 * para que esta clase pueda construirse sin depender de otros repositorios
 * o servicios, evitando la construcción circular en Main.
 */
public interface WarrantyRepository {

    /**
     * Guarda la lista completa de garantías, reemplazando cualquier
     * dato almacenado previamente.
     *
     * @param warranties la lista de garantías a guardar
     */
    void saveAll(List<Warranty> warranties);

    /**
     * Carga todas las garantías desde el almacenamiento persistente,
     * resolviendo el producto y la venta de cada garantía mediante las
     * funciones de resolución indicadas.
     *
     * @param productResolver una función que busca un Product por su identificador
     * @param saleResolver una función que busca un Sale por su código
     * @return la lista de garantías cargadas desde el almacenamiento
     */
    List<Warranty> loadAll(Function<String, Product> productResolver, Function<String, Sale> saleResolver);
}
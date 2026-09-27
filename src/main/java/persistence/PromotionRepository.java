package persistence;

import model.Promotion;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Promotion.
 * Las promociones se persisten como una colección completa (guardar/cargar
 * todas), ya que el sistema siempre trabaja con la lista completa para
 * determinar cuáles promociones están vigentes actualmente.
 */
public interface PromotionRepository {

    /**
     * Guarda la lista completa de promociones, reemplazando cualquier
     * dato almacenado previamente.
     *
     * @param promotions la lista de promociones a guardar
     */
    void saveAll(List<Promotion> promotions);

    /**
     * Carga todas las promociones desde el almacenamiento persistente.
     * Retorna una lista vacía si el archivo de datos aún no existe.
     *
     * @return la lista de promociones cargadas desde el almacenamiento
     */
    List<Promotion> loadAll();
}
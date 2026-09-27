package services;

import model.Product;
import model.Return;
import model.Sale;
import persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Capa de servicio para Return. Recibe ReturnRepository, SaleService y
 * ProductService mediante inyección por constructor, y contiene la lógica
 * de negocio para registrar devoluciones (validación de plazo, validación
 * de pertenencia, restauración de stock) y generar el reporte de balance
 * mensual.
 */
public class ReturnService {

    private static final int RETURN_WINDOW_DAYS = 30;

    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Crea una nueva instancia de ReturnService.
     *
     * @param returnRepository repositorio usado para persistir y consultar devoluciones
     * @param saleService servicio usado para resolver y validar ventas
     * @param productService servicio usado para restaurar el stock de productos
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
    }
}
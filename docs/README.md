# GameZone Unicesar

Sistema de información para la gestión de una tienda de videojuegos y consolas ubicada en el sector universitario de Valledupar. Permite registrar y consultar productos, accesorios, clientes, vendedores, promociones, garantías, ventas y devoluciones, con persistencia de datos en archivos.

## Equipo

Ver [`docs/Team.md`](./docs/Team.md) para roles y distribución de trabajo.

## Arquitectura

Organizado en cuatro capas:

```
Taller_Gamezone1/
├── pom.xml
├── data/
├── docs/
│   ├── README.md
│   ├── Team.md
│   ├── analysis.md
│   ├── hierarchy-diagram.md
│   ├── class-diagram.md
│   ├── layers-diagram.md
│   └── ai-usage/
│       ├── Leader-ai-log.md
│       ├── developer1-ai-log.md
│       └── developer2-ai-log.md
├── README.md
├── .gitignore
└── src/
    └── main/
        └── java/
            ├── Main.java
            ├── model/
            │   ├── Person.java
            │   ├── Client.java
            │   ├── Seller.java
            │   ├── Product.java
            │   ├── VideoGame.java
            │   ├── Console.java
            │   ├── Accessory.java
            │   ├── Controller.java
            │   ├── Cable.java
            │   ├── Memory.java
            │   ├── Promotion.java
            │   ├── PercentageDiscount.java
            │   ├── CategoryDiscount.java
            │   ├── BulkPurchaseDiscount.java
            │   ├── Warranty.java
            │   ├── BasicWarranty.java
            │   ├── ExtendedWarranty.java
            │   ├── Sale.java
            │   └── Return.java
            ├── persistence/
            │   ├── ClientRepository.java
            │   ├── ClientRepositoryFile.java
            │   ├── ProductRepository.java
            │   ├── ProductRepositoryFile.java
            │   ├── AccessoryRepository.java
            │   ├── AccessoryRepositoryFile.java
            │   ├── PromotionRepository.java
            │   ├── PromotionRepositoryFile.java
            │   ├── WarrantyRepository.java
            │   ├── WarrantyRepositoryFile.java
            │   ├── SaleRepository.java
            │   ├── SaleRepositoryFile.java
            │   ├── SellerRepository.java
            │   ├── SellerRepositoryFile.java
            │   ├── ReturnRepository.java
            │   └── ReturnRepositoryFile.java
            ├── services/
            │   ├── ClientService.java
            │   ├── ProductService.java
            │   ├── AccessoryService.java
            │   ├── PromotionService.java
            │   ├── WarrantyService.java
            │   ├── SaleService.java
            │   ├── SellerService.java
            │   └── ReturnService.java
            └── ui/
                └── MenuUI.java
```

Dependencias: `ui → services → persistence → model`

Diagramas de diseño en [`docs/`](./docs/).

## Requisitos

- JDK [versión]
- Maven [versión]

## Compilación y ejecución

**NetBeans:** abrir el proyecto (File → Open Project) y presionar Run.

## Funcionalidades

- Registrar/listar videojuegos, consolas, accesorios (controles, cables, memorias), clientes y vendedores.
- Registrar promociones (porcentaje, categoría, volumen de compra) y consultar cuáles están vigentes.
- Registrar ventas —incluyendo accesorios y garantía extendida opcional— y consultar historial (general, por cliente, por vendedor).
- Asignar garantía básica automática a consolas vendidas; consultar garantías (todas, vigentes, próximas a vencer).
- Registrar devoluciones dentro del plazo de 30 días, validando que los productos pertenezcan a la venta original y restaurando el stock automáticamente.
- Consultar devoluciones (todas, por cliente o por venta) y generar el balance mensual neto (ventas menos devoluciones).
- Carga y guardado automático de datos en cada ejecución.

## Documentación adicional

- [`docs/analysis.md`](./docs/analysis.md)
- [`docs/hierarchy-diagram.md`](./docs/hierarchy-diagram.md)
- [`docs/class-diagram.md`](./docs/class-diagram.md)
- [`docs/layers-diagram.md`](./docs/layers-diagram.md)
- [`docs/ai-usage/`](./docs/ai-usage/)

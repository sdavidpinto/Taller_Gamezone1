# GameZone Unicesar

Sistema de información para la gestión de una tienda de videojuegos y consolas ubicada en el sector universitario de Valledupar. Permite registrar y consultar productos, accesorios, clientes, vendedores, promociones, garantías, ventas y devoluciones, con persistencia de datos en archivos.

Las cuatro ampliaciones (accesorios, promociones, garantías y devoluciones) operan de forma integrada sobre una misma venta: una venta puede incluir videojuegos, consolas y accesorios; recibe automáticamente la mejor promoción vigente; genera garantías básicas y extendidas; y puede ser objeto de una devolución parcial que se refleja en el inventario, en las garantías y en el balance mensual. Ver [Integración del sistema](#integración-del-sistema) para el detalle de los ajustes que hacen posible esta integración.

## Equipo

Ver [`docs/Team.md`](./Team.md) para roles y distribución de trabajo.

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

Diagramas de diseño en esta misma carpeta [`docs/`](./).

## Requisitos

- JDK [versión]
- Maven [versión]

## Compilación y ejecución

**NetBeans:** abrir el proyecto (File → Open Project) y presionar Run.

## Funcionalidades

- Registrar/listar videojuegos, consolas, accesorios (controles, cables, memorias), clientes y vendedores.
- Registrar promociones (porcentaje, categoría —incluida la categoría de accesorios— y volumen de compra) y consultar cuáles están vigentes.
- Registrar ventas —incluyendo productos y accesorios en una misma venta, con garantía extendida opcional por consola— y consultar historial (general, por cliente, por vendedor). El total de la venta se calcula como *subtotal − descuento de la mejor promoción vigente + costo de las garantías extendidas*, y el recibo desglosa cada uno de esos valores.
- Asignar garantía básica automática a consolas vendidas; consultar garantías (todas, vigentes, próximas a vencer).
- Registrar devoluciones parciales dentro del plazo de 30 días, validando que los productos (o accesorios) pertenezcan a la venta original, restaurando el stock del ítem según su tipo y anulando la garantía si el producto devuelto es una consola con garantía vigente.
- El monto reembolsado de una devolución es proporcional al descuento que tuvo la venta original (no el precio de lista) y, si aplica, incluye el costo de la garantía extendida cancelada.
- Consultar devoluciones (todas, por cliente o por venta) y generar el balance mensual: total de ventas, total de devoluciones y balance neto.
- Carga y guardado automático de datos en cada ejecución.


Ver [`docs/class-diagram.md`](./class-diagram.md) y [`docs/layers-diagram.md`](./layers-diagram.md) para el detalle de clases, métodos y dependencias que introduce cada ajuste.

## Documentación adicional

- [`docs/analysis.md`](./analysis.md)
- [`docs/hierarchy-diagram.md`](./hierarchy-diagram.md)
- [`docs/class-diagram.md`](./class-diagram.md)
- [`docs/layers-diagram.md`](./layers-diagram.md)
- [`docs/ai-usage/`](./ai-usage/)

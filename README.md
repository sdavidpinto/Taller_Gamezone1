# GameZone Unicesar

Sistema de información para la gestión de una tienda de videojuegos y consolas ubicada en el sector universitario de Valledupar. Permite registrar y consultar productos, clientes, vendedores y ventas, con persistencia de datos en archivos.

## Equipo

Ver [`TEAM.md`](./TEAM.md) para roles y distribución de trabajo.

## Arquitectura

Paquete raíz `com.gamezone`, organizado en cuatro capas:

```
Taller_Gamezone1/
├── pom.xml
├── data/
├── docs/                             
│   ├── analysis.md
│   ├── hierarchy-diagram.md
│   ├── class-diagram.md
│   ├── layers-diagram.md
│   └── ai-usage/
│       ├── leader-ai-log.md
│       ├── developer1-ai-log.md
│       └── developer2-ai-log.md
├── README.md                          
├── TEAM.md                            
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
            │   └── Sale.java
            ├── persistence/
            │   ├── ClientRepository.java
            │   ├── ClientRepositoryFile.java
            │   ├── ProductRepository.java
            │   ├── ProductRepositoryFile.java
            │   ├── SaleRepository.java
            │   ├── SaleRepositoryFile.java
            │   ├── SellerRepository.java
            │   └── SellerRepositoryFile.java
            ├── service/
            │   ├── ClientService.java
            │   ├── ProductService.java
            │   ├── SaleService.java
            │   └── SellerService.java
            └── ui/
                └── MenuUI.java
```

Dependencias: `ui → service → persistence → model`

Diagramas de diseño en [`docs/`](./docs/).

## Requisitos

- JDK [versión]
- Maven [versión]

## Compilación y ejecución

**NetBeans:** abrir el proyecto (File → Open Project) y presionar Run.

## Funcionalidades

- Registrar/listar videojuegos, consolas, clientes y vendedores.
- Registrar ventas y consultar historial (general, por cliente, por vendedor).
- Carga y guardado automático de datos en cada ejecución.

## Documentación adicional

- [`docs/analysis.md`](./docs/analysis.md)
- [`docs/hierarchy-diagram.md`](./docs/hierarchy-diagram.md)
- [`docs/class-diagram.md`](./docs/class-diagram.md)
- [`docs/layers-diagram.md`](./docs/layers-diagram.md)
- [`docs/ai-usage/`](./docs/ai-usage/)

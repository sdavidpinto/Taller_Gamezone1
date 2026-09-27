```mermaid
classDiagram
    direction TB

    %% Person Hierarchy
    class Person {
        <<abstract>>
    }
    class Client {
    }
    class Seller {
    }

    Person <|-- Client
    Person <|-- Seller

    %% Product Hierarchy
    class Product {
        <<abstract>>
    }
    class VideoGame {
    }
    class Console {
    }
    class Accessory {
        <<abstract>>
    }
    class Cable {
    }
    class Controller {
    }
    class Memory {
    }

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Cable
    Accessory <|-- Controller
    Accessory <|-- Memory

    %% Promotion Hierarchy
    class Promotion {
        <<abstract>>
    }
    class PercentageDiscount {
    }
    class CategoryDiscount {
    }
    class BulkPurchaseDiscount {
    }

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    %% Warranty Hierarchy
    class Warranty {
        <<abstract>>
    }
    class BasicWarranty {
    }
    class ExtendedWarranty {
    }

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% Nota: Return (model) es una clase nueva de la funcionalidad de
    %% devoluciones, pero no participa de ninguna jerarquía de herencia:
    %% es una clase concreta e independiente que solo se asocia con
    %% Sale y Product (ver class-diagram.md y layers-diagram.md).
    %%
    %% Nota (Requerimiento 5, ajuste A1): CategoryDiscount ahora también
    %% acepta "ACCESSORY" como categoría objetivo, además de "VIDEOGAME" y
    %% "CONSOLE". Esto no crea ninguna clase ni relación de herencia nueva:
    %% Accessory ya es subclase de Product, así que basta con que
    %% CategoryDiscount reconozca las instancias de Accessory (y por lo
    %% tanto también de Cable, Controller y Memory) al calcular el
    %% descuento.
```

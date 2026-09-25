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
```
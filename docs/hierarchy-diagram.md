```mermaid
classDiagram
    direction TB

    %% Person Hierarchy
    class Person {
        <<abstract>>
    }
    class Customer {
    }
    class Seller {
    }

    Person <|-- Customer
    Person <|-- Seller

    %% Product Hierarchy
    class Product {
        <<abstract>>
    }
    class VideoGame {
    }
    class Console {
    }

    Product <|-- VideoGame
    Product <|-- Console
```

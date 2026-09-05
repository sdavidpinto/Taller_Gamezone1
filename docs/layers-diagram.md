classDiagram
    direction TB

    namespace UI_Layer {
        class MainMenu {
        }
    }

    namespace Service_Layer {
        class ProductService {
        }
        class PersonService {
        }
        class SaleService {
        }
    }

    namespace Persistence_Layer {
        class ProductRepository {
        }
        class PersonRepository {
        }
        class SaleRepository {
        }
    }

    namespace Model_Layer {
        class Person {
            <<abstract>>
        }
        class Customer {
        }
        class Seller {
        }
        class Product {
            <<abstract>>
        }
        class VideoGame {
        }
        class Console {
        }
        class Sale {
        }
    }

    %% Dependencies between layers
    MainMenu ..> ProductService : uses
    MainMenu ..> PersonService : uses
    MainMenu ..> SaleService : uses

    SaleService ..> ProductService : uses
    SaleService ..> PersonService : uses
    SaleService ..> SaleRepository : uses
    SaleService ..> ProductRepository : uses

    ProductService ..> ProductRepository : uses
    ProductService ..> Product : uses

    PersonService ..> PersonRepository : uses
    PersonService ..> Person : uses

    SaleRepository ..> Sale : uses
    ProductRepository ..> Product : uses
    PersonRepository ..> Person : uses
```

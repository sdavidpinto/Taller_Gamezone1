```mermaid
classDiagram
    direction TB

    class Main {
        <<Application Entry>>
        + main(String[] args) void
    }

    namespace ui_layer {
        class MenuUI {
            <<UI>>
            + MenuUI(ClientService, SellerService, ProductService, SaleService, AccessoryService, PromotionService, WarrantyService, ReturnService)
            + start() void
            - clientsMenu() void
            - sellersMenu() void
            - productsMenu() void
            - salesMenu() void
            - accessoriesMenu() void
            - promotionsMenu() void
            - warrantiesMenu() void
            - returnsMenu() void
        }
    }

    namespace service_layer {
        class SaleService {
            <<Líder Técnico>>
            + SaleService(SaleRepository, ClientRepository, SellerRepository, ProductRepository, AccessoryRepository, PromotionService, WarrantyService)
            + registerSale(String code, String clientIdNumber, String sellerIdNumber, List~String~ productIdentifiers, List~String~ accessoryIdentifiers, List~String~ extendedWarrantyProductIds) Sale
            + findByCode(String code) Sale
            + findAll() List~Sale~
            + cancelSale(String code) boolean
            - applyBestPromotion(Sale sale) void
        }
        class ProductService {
            <<Desarrollador 1 · paquete "services">>
            + ProductService(ProductRepository productRepository)
            + registerVideoGame(String identifier, String title, double price, int stock, String platform, String genre, String ageRating) void
            + registerConsole(String identifier, String title, double price, int stock, String brand, String model, String generation) void
            + findByIdentifier(String identifier) Product
            + findAll() List~Product~
            + updateStock(String identifier, int newStock) boolean
            + deleteProduct(String identifier) boolean
            + restoreStock(String identifier, int quantity) boolean
        }
        class ClientService {
            <<Desarrollador 2>>
            + ClientService(ClientRepository clientRepository)
            + registerClient(String name, String idNumber, String phone, String email) void
            + findByIdNumber(String idNumber) Client
            + findAll() List~Client~
            + updateClient(String idNumber, String newName, String newPhone, String newEmail) boolean
            + deleteClient(String idNumber) boolean
        }
        class SellerService {
            <<Desarrollador 2>>
            + SellerService(SellerRepository sellerRepository)
            + registerSeller(String name, String idNumber, String phone, String employeeCode, String shift) void
            + findByIdNumber(String idNumber) Seller
            + findAll() List~Seller~
            + updateSeller(String idNumber, String newName, String newPhone, String newEmployeeCode, String newShift) boolean
            + deleteSeller(String idNumber) boolean
        }
        class AccessoryService {
            <<Desarrollador 1>>
            + AccessoryService(AccessoryRepository accessoryRepository)
            + registerAccessory(Accessory accessory) void
            + findByIdentifier(String identifier) Accessory
            + findAll() List~Accessory~
            + updateStock(String identifier, int newQuantity) boolean
            + hasEnoughStock(String identifier, int requestedQuantity) boolean
            + deleteAccessory(String identifier) boolean
            + findByType(String type) List~Accessory~
            + findCompatibleWithConsoleBrand(String brand) List~Accessory~
            + findAccessoriesCompatibleWith(String productId) List~Accessory~
            + addCompatibleProduct(String accessoryIdentifier, Product product) void
        }
        class PromotionService {
            <<Desarrollador 2 / Líder Técnico>>
            + PromotionService(PromotionRepository promotionRepository)
            + registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) void
            + registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String targetCategory, double percentage) void
            + registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minQuantity, double percentage) void
            + findBestPromotionFor(Sale sale) Promotion
            + listActivePromotions() List~Promotion~
            + listAllPromotions() List~Promotion~
            + findById(String id) Promotion
        }
        class WarrantyService {
            <<Desarrollador 2 / Líder Técnico>>
            + WarrantyService(WarrantyRepository warrantyRepository, SaleRepository saleRepository, ProductService productService)
            + assignBasicWarranty(Product product, Sale sale, LocalDate startDate) BasicWarranty
            + assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) ExtendedWarranty
            + findWarrantyByProduct(String productId, String saleCode) Warranty
            + listAllWarranties() List~Warranty~
            + listActiveWarranties() List~Warranty~
            + listWarrantiesExpiringSoon(int daysAhead) List~Warranty~
            - loadAllResolved() List~Warranty~
        }
        class ReturnService {
            <<Nueva funcionalidad>>
            + ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService)
            + registerReturn(String saleId, List~String~ productIds, String reason) Return
            + viewAllReturns() List~Return~
            + viewReturnsByCustomer(String customerId) List~Return~
            + viewReturnsBySale(String saleId) List~Return~
            + generateMonthlyBalance(int month, int year) double
            - resolveAndValidateProducts(Sale sale, List~String~ productIds) List~Product~
        }
    }

    namespace persistence_layer {
        class SaleRepository {
            <<Interface>>
            <<Líder Técnico>>
            + save(Sale sale) void
            + findByCode(String code) Sale
            + findAll() List~Sale~
            + update(Sale sale) boolean
            + deleteByCode(String code) boolean
        }
        class ProductRepository {
            <<Interface>>
            <<Desarrollador 1>>
            + save(Product product) void
            + findByIdentifier(String identifier) Product
            + findAll() List~Product~
            + update(Product product) boolean
            + deleteByIdentifier(String identifier) boolean
        }
        class ClientRepository {
            <<Interface>>
            <<Desarrollador 2>>
            + save(Client client) void
            + findByIdNumber(String idNumber) Client
            + findAll() List~Client~
            + update(Client client) boolean
            + deleteByIdNumber(String idNumber) boolean
        }
        class SellerRepository {
            <<Interface>>
            <<Desarrollador 2>>
            + save(Seller seller) void
            + findByIdNumber(String idNumber) Seller
            + findAll() List~Seller~
            + update(Seller seller) boolean
            + deleteByIdNumber(String idNumber) boolean
        }
        class AccessoryRepository {
            <<Interface>>
            <<Desarrollador 1>>
            + save(Accessory accessory) void
            + findByIdentifier(String identifier) Accessory
            + findAll() List~Accessory~
            + update(Accessory accessory) boolean
            + deleteByIdentifier(String identifier) boolean
        }
        class PromotionRepository {
            <<Interface>>
            <<Desarrollador 2>>
            + saveAll(List~Promotion~ promotions) void
            + loadAll() List~Promotion~
        }
        class WarrantyRepository {
            <<Interface>>
            <<Desarrollador 2>>
            + saveAll(List~Warranty~ warranties) void
            + loadAll(Function~String,Product~ productResolver, Function~String,Sale~ saleResolver) List~Warranty~
        }
        class ReturnRepository {
            <<Interface>>
            <<Nueva funcionalidad>>
            + saveAll(List~Return~ returns) void
            + loadAll() List~Return~
        }
    }

    namespace model_layer {
        class Person {
            <<Abstract>>
            - String name
            - String idNumber
            - String phone
            + getName() String
            + getIdNumber() String
            + getPhone() String
            + display()* String
        }
        class Client {
            - String email
            - List~Sale~ salesHistory
            + getEmail() String
            + getSalesHistory() List~Sale~
            + addSale(Sale sale) void
            + display() String
        }
        class Seller {
            <<pre-loaded>>
            - String employeeCode
            - String shift
            + getEmployeeCode() String
            + getShift() String
            + display() String
        }
        class Product {
            <<Abstract>>
            - String identifier
            - String title
            - double price
            - int availableQuantity
            + getIdentifier() String
            + getTitle() String
            + getPrice() double
            + getAvailableQuantity() int
            + setAvailableQuantity(int quantity) void
            + getDescription()* String
            + mostrar() void
        }
        class VideoGame {
            - String platform
            - String genre
            - String ageRating
            + getDescription() String
        }
        class Console {
            - String brand
            - String model
            - String generation
            + getDescription() String
        }
        class Accessory {
            <<Abstract>>
            # String brand
            # List~Product~ compatible
            + getBrand() String
            + setBrand(String brand) void
            + getCompatible() List~Product~
            + setCompatible(List~Product~ compatible) void
            + addProduct(Product p)* void
            + getDescription()* String
        }
        class Cable {
            - String ConnectionType
            - int Length
            + getConnectionType() String
            + getLength() int
            + getDescription() String
            + addProduct(Product p) void
        }
        class Controller {
            - boolean alambric
            + isAlambric() boolean
            + getDescription() String
            + addProduct(Product p) void
        }
        class Memory {
            - String MemoryType
            - int Storage
            + getMemoryType() String
            + getStorage() int
            + getDescription() String
            + addProduct(Product p) void
        }
        class Sale {
            - String code
            - LocalDate date
            - Client client
            - Seller seller
            - List~Product~ products
            - double total
            - String appliedPromotionName
            - double discountAmount
            - double warrantyCost
            + getCode() String
            + calculateTotal(List~Product~ products) double
            + canBeReturned() boolean
            + getDate() LocalDate
            + getClient() Client
            + getSeller() Seller
            + getProducts() List~Product~
            + getTotal() double
            + getAppliedPromotionName() String
            + getDiscountAmount() double
            + getWarrantyCost() double
            + Display() String
        }
        class Return {
            <<Nueva funcionalidad>>
            - String id
            - LocalDate date
            - Sale originalSale
            - List~Product~ returnedProducts
            - String reason
            - double refundAmount
            + getId() String
            + getDate() LocalDate
            + getOriginalSale() Sale
            + getReturnedProducts() List~Product~
            + getReason() String
            + getRefundAmount() double
            + calculateRefundAmount() double
            + generateReturnReceipt() String
        }
        class Promotion {
            <<Abstract>>
            <<Desarrollador 2>>
            - String id
            - String name
            - LocalDate startDate
            - LocalDate endDate
            + getId() String
            + getName() String
            + isActive() boolean
            + isActive(LocalDate date) boolean
            + calculateDiscount(List~Product~ products)* double
            + getDescription()* String
        }
        class PercentageDiscount {
            <<Desarrollador 2>>
            - double percentage
            + getPercentage() double
            + calculateDiscount(List~Product~ products) double
            + getDescription() String
        }
        class CategoryDiscount {
            <<Desarrollador 2>>
            - String targetCategory
            - double percentage
            + isValidCategory(String category)$ boolean
            + getTargetCategory() String
            + getPercentage() double
            + calculateDiscount(List~Product~ products) double
            + getDescription() String
        }
        class BulkPurchaseDiscount {
            <<Desarrollador 2>>
            - int minQuantity
            - double percentage
            + getMinQuantity() int
            + getPercentage() double
            + calculateDiscount(List~Product~ products) double
            + getDescription() String
        }
        class Warranty {
            <<Abstract>>
            <<Desarrollador 1>>
            - String id
            - Product product
            - Sale sale
            - LocalDate startDate
            - LocalDate endDate
            + getId() String
            + getProduct() Product
            + getSale() Sale
            + getStartDate() LocalDate
            + getEndDate() LocalDate
            + getDurationInMonths()* int
            + getWarrantyType()* String
            + getAdditionalCost()* double
            + isActive(LocalDate date) boolean
            + generateWarrantyCertificate() String
        }
        class BasicWarranty {
            <<Desarrollador 1>>
            + getDurationInMonths() int
            + getWarrantyType() String
            + getAdditionalCost() double
        }
        class ExtendedWarranty {
            <<Desarrollador 1>>
            + double COST_PERCENTAGE$
            + getDurationInMonths() int
            + getWarrantyType() String
            + getAdditionalCost() double
        }
    }

    %% Dependencias de entrada y UI
    Main --> MenuUI

    %% Dependencias UI -> Service
    MenuUI --> SaleService
    MenuUI --> ProductService
    MenuUI --> ClientService
    MenuUI --> SellerService
    MenuUI --> AccessoryService
    MenuUI --> PromotionService
    MenuUI --> WarrantyService
    MenuUI --> ReturnService

    %% Dependencias Service -> Persistence
    SaleService --> SaleRepository
    SaleService --> ClientRepository
    SaleService --> SellerRepository
    SaleService --> ProductRepository
    SaleService --> AccessoryRepository
    SaleService --> PromotionService
    SaleService --> WarrantyService
    ProductService --> ProductRepository
    ClientService --> ClientRepository
    SellerService --> SellerRepository
    AccessoryService --> AccessoryRepository
    PromotionService --> PromotionRepository
    WarrantyService --> WarrantyRepository
    WarrantyService --> SaleRepository
    WarrantyService --> ProductService
    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService

    %% Dependencias de Service hacia Model (relaciones de uso)
    SaleService --> Sale
    ProductService --> Product
    ClientService --> Client
    SellerService --> Seller
    AccessoryService --> Accessory
    AccessoryService --> Product
    PromotionService --> Promotion
    WarrantyService --> Warranty
    ReturnService --> Return

    %% Dependencias de Persistence hacia Model
    SaleRepository --> Sale
    ProductRepository --> Product
    ClientRepository --> Client
    SellerRepository --> Seller
    AccessoryRepository --> Accessory
    PromotionRepository --> Promotion
    WarrantyRepository --> Warranty
    ReturnRepository --> Return

    %% Relaciones de Herencia (Triángulos)
    Person <|-- Client
    Person <|-- Seller
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Cable
    Accessory <|-- Controller
    Accessory <|-- Memory
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% Relaciones de Asociación de la Venta
    Sale --> Client
    Sale --> Seller
    Sale --> Product
    Warranty --> Product
    Warranty --> Sale
    Return --> Sale
    Return --> Product
```
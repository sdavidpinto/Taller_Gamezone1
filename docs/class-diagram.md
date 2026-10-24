```mermaid
%% ============================================================
%% Diagrama de clases - Sistema integrado GameZone Unicesar
%% Refleja el diseño objetivo del Requerimiento 5 (Integración):
%%   A1 - CategoryDiscount admite "ACCESSORY"            [ya en código]
%%   A2 - WarrantyService sin dependencia circular        [ya en código]
%%   A3 - Flujo unificado de SaleService.registerSale     [reorganización interna, sin cambio de firma]
%%   A4 - ReturnService delega restauración de stock de accesorios en AccessoryService
%%   A5 - Return.calculateRefundAmount es proporcional al descuento de la venta original
%%   A6 - ReturnService expone ventas y devoluciones del mes por separado
%%   A7 - WarrantyService.cancelWarranties anula garantías al devolver una consola
%% Refactor GRASP (Requerimiento 7, Líder Técnico):
%%   V-01 - Sale.getSubtotal() expone el subtotal (Return lo usa en resolveDiscountRatio)
%%   V-02 - SaleService delega el stock en ProductService / AccessoryService
%%          (ya no depende de ProductRepository ni de AccessoryRepository)
%%   V-03 - Product.requiresBasicWarranty() reemplaza el instanceof Console
%%   V-04 - SaleService.registerSale se divide en métodos privados de una sola tarea
%%   V-05 - SaleRepositoryFile.toBlock(Sale) (clase de archivo, fuera de este diagrama)
%%   V-06 - MenuUI delega en SubMenu y sus 8 submenús; helpers de entrada en SubMenu
%% Las clases de validation y exceptions se agregan como capas propias.
%% Nota: el documento de Requerimiento 5 usa el nombre genérico "ConsoleMenu"
%% para referirse a la clase MenuUI de este proyecto.
%% ============================================================
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
            - parseOption(String texto) int
        }
        class SubMenu {
            <<Abstract>>
            + show()* void
            # parseOption(String texto) int
            # askDate(String mensaje) LocalDate
            # askYesNo(String mensaje) Boolean
            # askRequiredText(String mensaje) String
            # askDouble(String mensaje) Double
            # askInt(String mensaje) Integer
        }
        class ClientMenu {
            <<UI>>
            + ClientMenu(ClientService, SaleService)
            + show() void
            - registerClient() void
            - searchClient() void
            - listClients() void
            - updateClient() void
            - deleteClient() void
        }
        class SellerMenu {
            <<UI>>
            + SellerMenu(SellerService, SaleService)
            + show() void
            - registerSeller() void
            - searchSeller() void
            - listSellers() void
            - updateSeller() void
            - deleteSeller() void
        }
        class ProductMenu {
            <<UI>>
            + ProductMenu(ProductService)
            + show() void
            - registerVideoGame() void
            - registerConsole() void
            - searchProduct() void
            - listProducts() void
            - updateStock() void
            - deleteProduct() void
        }
        class SaleMenu {
            <<UI>>
            + SaleMenu(ClientService, SellerService, SaleService)
            + show() void
            - registerSale() void
            - searchSale() void
            - listSales() void
            - cancelSale() void
        }
        class AccessoryMenu {
            <<UI>>
            + AccessoryMenu(ProductService, AccessoryService)
            + show() void
            - registerController() void
            - registerCable() void
            - registerMemory() void
            - listAllAccessories() void
            - listAccessoriesByType() void
            - consultCompatibleAccessories() void
            - addCompatibleProduct() void
            - listCompatibleAccessories() void
        }
        class PromotionMenu {
            <<UI>>
            + PromotionMenu(PromotionService)
            + show() void
            - registerPercentageDiscount() void
            - registerCategoryDiscount() void
            - registerBulkPurchaseDiscount() void
            - listAllPromotions() void
            - listActivePromotions() void
            - mostrarListaPromociones(List~Promotion~ promociones, String mensajeVacio) void
            - searchPromotion() void
        }
        class WarrantyMenu {
            <<UI>>
            + WarrantyMenu(WarrantyService)
            + show() void
            - searchWarranty() void
            - listAllWarranties() void
            - listActiveWarranties() void
            - listWarrantiesExpiringSoon() void
            - displayWarrantyList(List~Warranty~ warranties, String emptyMessage) void
        }
        class ReturnMenu {
            <<UI>>
            + ReturnMenu(SaleService, ReturnService)
            + show() void
            - registerReturn() void
            - listAllReturns() void
            - searchReturnsByCustomer() void
            - searchReturnsBySale() void
            - displayReturnList(List~Return~ devoluciones, String emptyMessage) void
            - monthlyBalance() void
        }
    }

    namespace service_layer {
        class SaleService {
            <<Líder Técnico>>
            + SaleService(SaleRepository, ClientRepository, SellerRepository, ProductService, AccessoryService, PromotionService, WarrantyService)
            + registerSale(String code, String clientIdNumber, String sellerIdNumber, List~String~ productIdentifiers, List~String~ accessoryIdentifiers, List~String~ extendedWarrantyProductIds) Sale
            + findByCode(String code) Sale
            + findAll() List~Sale~
            + cancelSale(String code) boolean
            - applyBestPromotion(Sale sale) void
            - resolveItems(List~String~ productIdentifiers, List~String~ accessoryIdentifiers) List~Product~
            - assignBasicWarranties(Sale sale) void
            - assignExtendedWarranties(Sale sale, List~String~ extendedWarrantyProductIds) double
            - updateInventory(List~Product~ products) void
            - restoreStock(Product p) void
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
            + restoreStock(String accessoryId, int quantity) boolean
            - requireNonBlank(String value, String fieldName) void
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
            - requireNonBlank(String value, String fieldName) void
            - requireUniqueId(String id) void
            - saveNewPromotion(Promotion promotion) void
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
            + cancelWarranties(String productId, String saleId) double
            - loadAllResolved() List~Warranty~
            - generateId() String
            - saveNewWarranty(Warranty warranty) void
        }
        class ReturnService {
            <<Nueva funcionalidad>>
            - int RETURN_WINDOW_DAYS$
            + ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService, AccessoryService accessoryService, WarrantyService warrantyService)
            + registerReturn(String saleId, List~String~ productIds, String reason) Return
            + viewAllReturns() List~Return~
            + viewReturnsByCustomer(String customerId) List~Return~
            + viewReturnsBySale(String saleId) List~Return~
            + generateMonthlyBalance(int month, int year) double
            + calculateMonthlySales(int month, int year) double
            + calculateMonthlyReturns(int month, int year) double
            - resolveAndValidateProducts(Sale sale, List~String~ productIds) List~Product~
            - restoreStockFor(Product item) void
            - generateId() String
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

    %% A5: Return.calculateRefundAmount deja de sumar precios de lista y pasa
    %% a ser proporcional al descuento de la venta original:
    %% precio x (1 - discountAmount / subtotal).
    %% A7: el monto reembolsado incorpora el costo de las garantías canceladas,
    %% devuelto por WarrantyService.cancelWarranties(productId, saleId).
    namespace model_layer {
        class Person {
            <<Abstract>>
            - String name
            - String idNumber
            - String phone
            + getName() String
            + getIdNumber() String
            + getPhone() String
            + setName(String name) void
            + setIdNumber(String idNumber) void
            + setPhone(String phone) void
            + display()* String
        }
        class Client {
            - String email
            - List~Sale~ salesHistory
            + getEmail() String
            + setEmail(String email) void
            + getSalesHistory() List~Sale~
            + addSale(Sale sale) void
            + display() String
        }
        class Seller {
            <<pre-loaded>>
            - String employeeCode
            - String shift
            + getEmployeeCode() String
            + setEmployeeCode(String employeeCode) void
            + getShift() String
            + setShift(String shift) void
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
            + requiresBasicWarranty() boolean
            + getDescription()* String
            + mostrar() void
        }
        class VideoGame {
            - String platform
            - String genre
            - String ageRating
            + getPlatform() String
            + getGenre() String
            + getAgeRating() String
            + getDescription() String
        }
        class Console {
            - String brand
            - String model
            - String generation
            + getBrand() String
            + getModel() String
            + getGeneration() String
            + getDescription() String
            + requiresBasicWarranty() boolean
        }
        class Accessory {
            <<Abstract>>
            # String brand
            # List~Product~ compatible
            + getBrand() String
            + setBrand(String brand) void
            + getCompatible() List~Product~
            + setCompatible(List~Product~ compatible) void
            + getAvailableQuantity() int
            + setAvailableQuantity(int availableQuantity) void
            + mostrar() void
            + addProduct(Product p)* void
            + getDescription()* String
        }
        class Cable {
            - String ConnectionType
            - int Length
            + getConnectionType() String
            + setConnectionType(String ConnectionType) void
            + getLength() int
            + setLength(int Length) void
            + getDescription() String
            + addProduct(Product p) void
        }
        class Controller {
            - boolean alambric
            + isAlambric() boolean
            + setAlambric(boolean alambric) void
            + getDescription() String
            + addProduct(Product p) void
        }
        class Memory {
            - String MemoryType
            - int Storage
            + getMemoryType() String
            + setMemoryType(String MemoryType) void
            + getStorage() int
            + setStorage(int Storage) void
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
            + getSubtotal() double
            + setDate(LocalDate date) void
            + setProducts(List~Product~ products) void
            + setTotal(double total) void
            + setAppliedPromotionName(String appliedPromotionName) void
            + setDiscountAmount(double discountAmount) void
            + setWarrantyCost(double warrantyCost) void
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
            - double warrantyRefundAmount
            + getId() String
            + getDate() LocalDate
            + getOriginalSale() Sale
            + getReturnedProducts() List~Product~
            + getReason() String
            + getRefundAmount() double
            + getWarrantyRefundAmount() double
            + calculateRefundAmount() double
            + addWarrantyRefund(double amount) void
            - resolveDiscountRatio() double
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
            + getStartDate() LocalDate
            + getEndDate() LocalDate
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
            + String VIDEOGAME$
            + String CONSOLE$
            + String ACCESSORY$
            - List~String~ VALID_CATEGORIES$
            - String targetCategory
            - double percentage
            + isValidCategory(String category)$ boolean
            + getTargetCategory() String
            + getPercentage() double
            + calculateDiscount(List~Product~ products) double
            - belongsToTargetCategory(Product product) boolean
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

    %% Capa de validación: clases utilitarias estáticas usadas por los services
    namespace validation_layer {
        class PersonValidator {
            <<Utility>>
            + validatePersonData(String id, String name, String phone)$ void
            + validatePersonData(String id, String name, String email, String phone)$ void
            + validatePersonExists(Person person, String personId)$ void
            + validateUniquePerson(List~Person~ existing, String newId)$ void
            - requireNonBlank(String value, String fieldName)$ void
        }
        class ProductValidator {
            <<Utility>>
            + validateProductData(String id, String title, double price, int stock)$ void
            + validateStockAvailability(Product product, int requestedQuantity)$ void
            + validateProductExists(Product product, String productId)$ void
            - requireNonBlank(String value, String fieldName)$ void
        }
        class SaleValidator {
            <<Utility>>
            + validateSaleData(Client client, Seller seller, List~Product~ products)$ void
            + validateSaleExists(Sale sale, String saleId)$ void
        }
    }

    %% Capa de excepciones: jerarquía propia sobre RuntimeException
    namespace exception_layer {
        class GameZoneException {
            <<Abstract>>
            - String errorCode
            # GameZoneException(String message)
            # GameZoneException(String message, String errorCode)
            + getErrorCode() String
        }
        class BusinessRuleException {
            + BusinessRuleException(String message)
        }
        class InvalidDataException {
            + InvalidDataException(String fieldName, String problem)
        }
        class PersistenceException {
            + PersistenceException(String message, Throwable cause)
        }
        class ResourceNotFoundException {
            + ResourceNotFoundException(String resourceType, String identifier)
        }
    }
    class RuntimeException {
        <<Java>>
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

    %% Dependencias MenuUI -> submenús
    MenuUI --> ClientMenu
    MenuUI --> SellerMenu
    MenuUI --> ProductMenu
    MenuUI --> SaleMenu
    MenuUI --> AccessoryMenu
    MenuUI --> PromotionMenu
    MenuUI --> WarrantyMenu
    MenuUI --> ReturnMenu

    %% Dependencias submenú -> Service
    ClientMenu --> ClientService
    ClientMenu --> SaleService
    SellerMenu --> SellerService
    SellerMenu --> SaleService
    ProductMenu --> ProductService
    SaleMenu --> ClientService
    SaleMenu --> SellerService
    SaleMenu --> SaleService
    AccessoryMenu --> ProductService
    AccessoryMenu --> AccessoryService
    PromotionMenu --> PromotionService
    WarrantyMenu --> WarrantyService
    ReturnMenu --> SaleService
    ReturnMenu --> ReturnService

    %% Dependencias Service -> Persistence
    SaleService --> SaleRepository
    SaleService --> ClientRepository
    SaleService --> SellerRepository
    SaleService --> ProductService
    SaleService --> AccessoryService
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
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService

    %% Dependencias de Service hacia validation y exceptions
    ClientService --> PersonValidator
    SellerService --> PersonValidator
    ProductService --> ProductValidator
    ProductService --> InvalidDataException
    SaleService --> PersonValidator
    SaleService --> ProductValidator
    SaleService --> SaleValidator
    SaleService --> ResourceNotFoundException
    PersonValidator --> BusinessRuleException
    PersonValidator --> InvalidDataException
    PersonValidator --> ResourceNotFoundException
    ProductValidator --> BusinessRuleException
    ProductValidator --> InvalidDataException
    ProductValidator --> ResourceNotFoundException
    SaleValidator --> BusinessRuleException
    SaleValidator --> InvalidDataException
    SaleValidator --> ResourceNotFoundException
    PersonValidator --> Person
    ProductValidator --> Product
    SaleValidator --> Sale
    SaleValidator --> Client
    SaleValidator --> Seller

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
    SubMenu <|-- ClientMenu
    SubMenu <|-- SellerMenu
    SubMenu <|-- ProductMenu
    SubMenu <|-- SaleMenu
    SubMenu <|-- AccessoryMenu
    SubMenu <|-- PromotionMenu
    SubMenu <|-- WarrantyMenu
    SubMenu <|-- ReturnMenu
    RuntimeException <|-- GameZoneException
    GameZoneException <|-- BusinessRuleException
    GameZoneException <|-- InvalidDataException
    GameZoneException <|-- PersistenceException
    GameZoneException <|-- ResourceNotFoundException

    %% Relaciones de Asociación de la Venta
    Sale --> Client
    Sale --> Seller
    Sale --> Product
    Warranty --> Product
    Warranty --> Sale
    Return --> Sale
    Return --> Product
```
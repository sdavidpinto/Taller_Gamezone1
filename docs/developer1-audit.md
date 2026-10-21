# GRASP Audit Report – Developer 1

- **Author:** Edward Carrillo Martinez
- **Role:** Developer 1 (products, accessories, promotions, returns, warranties, validation, exceptions)
- **Branch:** `feature/grasp-audit`
- **Audited state:** `develop` (commit: `ab0fb9680f8dbdc44f4b15b5200e4bb3dfe0a30e`)
- **Code:** Java, layers `ui → services → persistence → model`

---

## 1. Scope

Only code developed by Developer 1 is audited. Problems whose origin is in another member's code are reported to the Leader (section 5), not audited.

- **Products (Workshop 1)**
  - Classes audited: `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductRepositoryFile`, `ProductService`
  - Methods audited: all, except `ProductService.restoreStock` (Leader)
- **Accessories (Req. 1)**
  - Classes audited: `Accessory`, `Cable`, `Controller`, `Memory`
  - Methods audited: all
- **Promotions (Req. 2)**
  - Classes audited: `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
  - Methods audited: all
- **Returns (Req. 3)**
  - Classes audited: `Return`
  - Methods audited: all of `Return`, and `Sale.canBeReturned()` (`model/Sale.java`, lines 128–131)
- **Warranties (Req. 4)**
  - Classes audited: `Warranty`, `BasicWarranty`, `ExtendedWarranty`
  - Methods audited: all
- **Exceptions and validation (Req. 6)**
  - Classes audited: `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`, `ResourceNotFoundException`, `PersonValidator`, `ProductValidator`, `SaleValidator`
  - Methods audited: all

Not audited by me (other owners): `Person`, `Client`, `Seller` and the persistence/service classes of persons, accessories, promotions, returns and warranties (Developer 2); `Sale` (except `canBeReturned`), `SaleRepository`, `SaleService`, `MenuUI`, `Main` and `ProductService.restoreStock` (Leader).

Note on `ProductRepositoryFile`: it is the persistence class of the products module (Team.md, Developer 1, task 6) and does not appear in the Developer 2 scope, so it is audited here.

---

## 2. Verdict summary

- **1. Information Expert:** Violation. Findings: V-04
- **2. Creator:** Correct. Findings: –
- **3. Controller:** Not applicable. Findings: –
- **4. Low Coupling:** Correct. Findings: –
- **5. High Cohesion:** Violation. Findings: V-02, V-03
- **6. Polymorphism:** Violation. Findings: V-01
- **7. Pure Fabrication:** Correct. Findings: –
- **8. Indirection:** Correct. Findings: –
- **9. Protected Variations:** Correct. Findings: –

Four distinct violations (V-01 to V-04). V-04 also conflicts with the strict-encapsulation rule of the course code restrictions, but it is registered once, under Information Expert.

---

## 3. Audit by pattern

### 3.1 Information Expert – Violation

**Uso correcto.** Las clases usan la información que tienen para realizar sus tareas:
- `Promotion.isActive(LocalDate)` (líneas 77–79) y `Warranty.isActive(LocalDate)` (líneas 93–95) comprueban su vigencia con las fechas que almacenan.
- `ExtendedWarranty.getAdditionalCost()` (línea 37) calcula el costo con el precio de su propio producto.
- `Sale.canBeReturned()` (líneas 128–131) decide si el plazo de 30 días sigue abierto con la fecha que la propia venta almacena.
- `Return.calculateRefundAmount()` (líneas 81–91) calcula el reembolso con sus propios productos devueltos.

**Violación: V-04** (la lista de productos compatibles pertenece a `Accessory`, pero su manejo está en las subclases).

### 3.2 Creator – Correct

- `ProductService.registerVideoGame` (línea 30) y `registerConsole` (línea 37) crean el `VideoGame` / `Console` después de validar los datos que reciben y luego lo persisten.
- `ProductRepositoryFile.parseLine` (líneas 228 y 233) construye los objetos porque tiene los datos almacenados necesarios para inicializarlos.
- Los constructores de `Cable`, `Controller` y `Memory` delegan en `Accessory` y esta en `Product` (`super(...)`) para inicializar los datos compartidos.

### 3.3 Controller – Not applicable

Las clases revisadas se ocupan principalmente de datos del dominio, servicios, persistencia o validación. No coordinan directamente los eventos de la interfaz de usuario (eso ocurre en `MenuUI`, código del Líder), por lo que este patrón no aplica a las clases auditadas.

### 3.4 Low Coupling – Correct

`ProductService` depende de la interfaz `ProductRepository` (campo línea 21, constructor línea 23) y no de `ProductRepositoryFile`. Esto mantiene separadas las responsabilidades del servicio y del repositorio.

### 3.5 High Cohesion – Violation

**Violations: V-02, V-03** (see section 4).

### 3.6 Polymorphism – Violation

**Violation: V-01** (see section 4).

### 3.7 Pure Fabrication – Correct

`ProductService` (reglas de registro y stock) y `ProductRepositoryFile` (acceso a archivo) reúnen responsabilidades que no pertenecen a `Product`. `ProductValidator.validateProductData` (líneas 25–34), `validateStockAvailability` (43–49) y `validateProductExists` (58–62) reúnen comprobaciones comunes sin cargarlas a las entidades.

### 3.8 Indirection – Correct

`ProductService` se comunica con los datos mediante la interfaz `ProductRepository` (por ejemplo `findByIdentifier`, línea 42; `save`, línea 31). Esa intermediación evita que el servicio dependa de cómo se guardan los productos.

### 3.9 Protected Variations – Correct

- `Promotion.calculateDiscount` (línea 89) y `getDescription` (línea 96) son los puntos de variación implementados de distinta forma por `PercentageDiscount`, `CategoryDiscount` y `BulkPurchaseDiscount`.
- `Warranty.getDurationInMonths`, `getWarrantyType` y `getAdditionalCost` (líneas 70, 77, 84) protegen la variación entre `BasicWarranty` y `ExtendedWarranty`.
- `Product.getDescription` (línea 72) protege la variación entre tipos de producto.
- `GameZoneException` (clase base abstracta) agrupa los cuatro tipos de excepción del sistema.

---

## 4. Violations

### V-01

- **ID:** V-01
- **Pattern:** Polymorphism
- **Module:** Promotions – Requirement 2 (`CategoryDiscount`)
- **Evidence:** `model/CategoryDiscount.java`, método privado `belongsToTargetCategory(Product)`, líneas 95–105. Usa `instanceof` para identificar si el producto es un `VideoGame`, `Console` o `Accessory`.
- **Explanation:** La clase reconoce los tipos concretos mediante condiciones en lugar de delegar esa identificación a un comportamiento polimórfico.
- **Consequence:** Si se agrega otro tipo de producto, puede ser necesario modificar este método.
- **Proposed solution:** Usar un método común que permita a cada tipo de producto indicar su categoría: en `Product` con una implementación por defecto que no coincide con ninguna categoría (no abstracto) y sobrescrito en `VideoGame`, `Console` y `Accessory`. Aplica Polimorfismo. Debe conservar las constantes `VIDEOGAME`, `CONSOLE`, `ACCESSORY` y el formato de los archivos de promociones. No debe ser abstracto porque `SaleRepositoryFile.SimpleProduct` (línea 299) también extiende `Product` y dejaría de compilar; con el valor por defecto sigue sin pertenecer a ninguna categoría, como ahora.
- **Other developer's code:** No – el método es privado y la corrección queda en `Product` y sus subclases, y en `CategoryDiscount`. `PromotionRepositoryFile` y `PromotionService` (Developer 2) solo usan el constructor, `isValidCategory` y `getTargetCategory()`, que no cambian.

### V-02

- **ID:** V-02
- **Pattern:** High Cohesion
- **Module:** Products – Workshop 1 (`Product`)
- **Evidence:** `model/Product.java`, método `mostrar()`, líneas 77–78. Imprime información del producto en consola. Una búsqueda de `mostrar(` en todo `src/main/java` solo encuentra las dos definiciones (`Product` y `Accessory`); no hay llamadas.
- **Explanation:** Mostrar información por consola no es responsabilidad de la entidad `Product`.
- **Consequence:** La clase mezcla los datos del producto con la presentación.
- **Proposed solution:** Eliminar el método, ya que no se utiliza en el proyecto. Aplica Alta Cohesión.
- **Other developer's code:** No.

### V-03

- **ID:** V-03
- **Pattern:** High Cohesion
- **Module:** Accessories – Requirement 1 (`Accessory`)
- **Evidence:** `model/Accessory.java`, método `mostrar()`, líneas 69–70. Imprime información del accesorio en consola. Sin llamadas en el proyecto (misma búsqueda de V-02).
- **Explanation:** Mostrar información por consola no es responsabilidad de la entidad `Accessory`.
- **Consequence:** La clase mezcla los datos del accesorio con la presentación.
- **Proposed solution:** Eliminar el método, ya que no se utiliza en el proyecto. Aplica Alta Cohesión.
- **Other developer's code:** No.

### V-04

- **ID:** V-04
- **Pattern:** Information Expert (guía). También afecta Alta Cohesión.
- **Module:** Accessories – Requirement 1 (`Accessory`, `Cable`, `Controller`, `Memory`)
- **Evidence:** `model/Accessory.java`: los atributos `brand` y `compatible` son `protected` (líneas 13–14) y `addProduct(Product)` se declara abstracto (línea 58). Las tres subclases lo implementan con el mismo cuerpo: `Cable.addProduct` (líneas 65–72), `Controller.addProduct` (líneas 43–50) y `Memory.addProduct` (líneas 57–64): `if (compatible == null) { compatible = new ArrayList<>(); } if (p != null && !compatible.contains(p)) { compatible.add(p); }`. Las subclases escriben directamente sobre el atributo de la superclase.
- **Explanation:** `Accessory` es quien posee la lista `compatible` (la declara, la inicializa en sus constructores y expone `getCompatible`/`setCompatible`), así que le corresponde decidir cómo se agrega un producto a ella. Hoy esa responsabilidad está repetida en tres subclases que manipulan un atributo ajeno. Como el comportamiento es idéntico, el método abstracto no expresa ninguna variación entre tipos de accesorio.
- **Consequence:** Cambiar la regla (por ejemplo, validar la compatibilidad antes de agregar) exige editar tres clases, y un cuarto tipo de accesorio obliga a copiar el mismo código otra vez. Los atributos `protected` rompen el encapsulamiento estricto que exige el Taller 1.
- **Proposed solution:** Implementar `addProduct` una sola vez en `Accessory` (conservando la creación de la lista si es `null`, la validación `p != null` y la ausencia de duplicados), volver `private` los atributos `brand` y `compatible`, y eliminar el método abstracto y las tres sobrescrituras. Aplica Experto en Información (y Alta Cohesión). No se crea ninguna clase.
- **Other developer's code:** No – `AccessoryService.addCompatibleProduct` (línea 182, Developer 2) sigue llamando a `accessory.addProduct(product)` y `AccessoryRepositoryFile` usa `getCompatible`/`setCompatible`; ninguna firma pública cambia.

---

## 5. Issues reported to the Technical Leader

Ningún hallazgo de este informe requiere modificar código de otro integrante.

1. **Defecto funcional fuera de alcance (ya reportado por Developer 2 como F-01).** `ProductService.findByIdentifier` (líneas
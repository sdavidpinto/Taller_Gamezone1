# Informe de Auditoría SOLID – Desarrollador 1

- **Autor:** Edward Carrillo Martinez
- **Rol:** Desarrollador 1 (productos, accesorios, promociones, devoluciones, garantías, validación, excepciones)
- **Rama:** `feature/solid-audit`
- **Estado auditado:** `develop` 
- **Código:** Java, capas `ui → services → persistence → model`

---

## 1. Alcance

Solo se audita el código desarrollado por el Desarrollador 1. 

- **Productos (Taller 1):** `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductRepositoryFile`, `ProductService`
- **Accesorios (Req. 1):** `Accessory`, `Cable`, `Controller`, `Memory`
- **Promociones (Req. 2):** `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
- **Devoluciones (Req. 3):** `Return`
- **Garantías (Req. 4):** `Warranty`, `BasicWarranty`, `ExtendedWarranty`
- **Excepciones y validación (Req. 6):** `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`, `ResourceNotFoundException`, `PersonValidator`, `ProductValidator`, `SaleValidator`

---

## 2. Resumen de veredictos

- **1. SRP (Responsabilidad Única):** Correcto. Hallazgos: –
- **2. OCP (Abierto/Cerrado):** Correcto. Hallazgos: – (Observación O-01)
- **3. LSP (Sustitución de Liskov):** Correcto. Hallazgos: –
- **4. ISP (Segregación de Interfaces):** Correcto. Hallazgos: –
- **5. DIP (Inversión de Dependencias):** Correcto. Hallazgos: –

Cero violaciones que requieran refactorización. Un hallazgo documentado como observación por restricciones arquitectónicas del equipo.

---

## 3. Auditoría por principio

### 3.1 SRP (Principio de Responsabilidad Única) – Correcto

Las clases tienen una única razón para cambiar, separando claramente las responsabilidades por capas:
- **Modelo:** Las entidades (`Product`, `Accessory`, etc.) solo almacenan datos y comportamiento inherente a ellos.
- **Validación:** Clases como `ProductValidator` centralizan las reglas de negocio y comprobaciones de datos.
- **Persistencia:** `ProductRepositoryFile` se encarga exclusivamente de leer y escribir en el archivo `.txt`.
- **Servicios:** `ProductService` actúa como orquestador, aplicando lógica de negocio pero delegando la persistencia y validación.

### 3.2 OCP (Principio de Abierto/Cerrado) – Correcto

El sistema está diseñado para ser extendido sin modificar código existente mediante el uso de herencia y polimorfismo. Por ejemplo, se pueden agregar nuevos tipos de garantías extendiendo `Warranty` sin alterar `WarrantyService`. 
*(Ver Observación O-01 sobre la categorización de descuentos).*

### 3.3 LSP (Principio de Sustitución de Liskov) – Correcto

Las subclases pueden reemplazar a sus clases base sin romper la aplicación:
- `VideoGame` y `Console` pueden usarse en cualquier lugar donde se requiera un `Product` (ej. en `ProductService.registerProduct`).
- `BasicWarranty` y `ExtendedWarranty` calculan su duración y costo correctamente al ser tratadas como `Warranty`.
- Las excepciones específicas (`BusinessRuleException`, etc.) extienden de `GameZoneException` y son atrapadas adecuadamente por los manejadores genéricos.

### 3.4 ISP (Principio de Segregación de Interfaces) – Correcto

Las interfaces de persistencia están segregadas por entidad (`ProductRepository`, `AccessoryRepository`, `PromotionRepository`). Ningún servicio se ve obligado a depender de métodos que no utiliza. Cada servicio consume solo la interfaz de su dominio correspondiente.

### 3.5 DIP (Principio de Inversión de Dependencias) – Correcto

Los módulos de alto nivel (Servicios) no dependen de módulos de bajo nivel (archivos o bases de datos), sino de abstracciones:
- `ProductService` depende de la interfaz `ProductRepository`, no de la implementación concreta `ProductRepositoryFile`. Esto permite cambiar el almacenamiento en el futuro (ej. a una base de datos SQL) sin tocar la lógica de negocio.

---

## 4. Violaciones

No se reportan violaciones que requieran modificación de código en la fase de refactorización.

---

## 5. Observaciones que no se cuentan como violaciones

- **O-01 (OCP en `CategoryDiscount`)**
  - **Observación:** El método `belongsToTargetCategory(Product)` en `CategoryDiscount.java` utiliza múltiples condicionales con `instanceof` para identificar si el producto es un `VideoGame`, `Console` o `Accessory`. Si la tienda agrega un nuevo tipo de producto en el futuro, esta clase tendría que ser modificada, lo que viola el principio de que debería estar cerrada a la modificación.
  - **Por qué no es violación:** Corregir esto de forma óptima requeriría modificar la clase núcleo `Product` para incluir polimorfismo de categorías. Como el código de `Product` y sus derivadas constituye la frontera del modelo que está siendo modificada en paralelo por el Líder Técnico, alterar esta jerarquía provocaría conflictos de integración (merge conflicts) severos. Por lo tanto, el uso de `instanceof` se tolera temporalmente y se documenta como deuda técnica.
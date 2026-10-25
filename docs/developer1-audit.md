# Informe de Auditoría GRASP – Desarrollador 1

- **Autor:** Edward Carrillo Martinez
- **Rol:** Desarrollador 1 (productos, accesorios, promociones, devoluciones, garantías, validación, excepciones)
- **Rama:** `feature/grasp-audit`
- **Estado auditado:** `develop`
- **Código:** Java, capas `ui → services → persistence → model`

---

## 1. Alcance

Solo se audita el código desarrollado por el Desarrollador 1. Los problemas cuyo origen está en el código de otro integrante se reportan al Líder (sección 5), no se auditan aquí.

- **Productos (Taller 1)**
  - Clases auditadas: `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductRepositoryFile`, `ProductService`
  - Métodos auditados: todos, excepto `ProductService.restoreStock` (Líder)
- **Accesorios (Req. 1)**
  - Clases auditadas: `Accessory`, `Cable`, `Controller`, `Memory`
  - Métodos auditados: todos
- **Promociones (Req. 2)**
  - Clases auditadas: `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
  - Métodos auditados: todos
- **Devoluciones (Req. 3)**
  - Clases auditadas: `Return`
  - Métodos auditados: todos los de `Return`, y `Sale.canBeReturned()` (`model/Sale.java`, líneas 128–131)
- **Garantías (Req. 4)**
  - Clases auditadas: `Warranty`, `BasicWarranty`, `ExtendedWarranty`
  - Métodos auditados: todos
- **Excepciones y validación (Req. 6)**
  - Clases auditadas: `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`, `ResourceNotFoundException`, `PersonValidator`, `ProductValidator`, `SaleValidator`
  - Métodos auditados: todos

No auditado por mí (otros responsables): `Person`, `Client`, `Seller` y las clases de persistencia/servicios de personas, accesorios, promociones, devoluciones y garantías (Desarrollador 2); `Sale` (excepto `canBeReturned`), `SaleRepository`, `SaleService`, `MenuUI`, `Main` y `ProductService.restoreStock` (Líder).

---

## 2. Resumen de veredictos

- **1. Experto en Información:** Correcto. Hallazgos: – (observación O-06)
- **2. Creador:** Correcto. Hallazgos: –
- **3. Controlador:** No aplica. Hallazgos: –
- **4. Bajo Acoplamiento:** Correcto. Hallazgos: –
- **5. Alta Cohesión:** Violación. Hallazgos: V-01, V-02
- **6. Polimorfismo:** Correcto. Hallazgos: – (observación O-05)
- **7. Fabricación Pura:** Correcto. Hallazgos: –
- **8. Indirección:** Correcto. Hallazgos: –
- **9. Variaciones Protegidas:** Correcto. Hallazgos: –

Dos violaciones distintas (V-01, V-02) enfocadas en responsabilidades de presentación dentro de las entidades del modelo.

---

## 3. Auditoría por patrón

### 3.1 Experto en Información – Correcto

Las clases usan su propia información para realizar sus tareas:
- `Promotion.isActive(LocalDate)` (líneas 77–79) y `Warranty.isActive(LocalDate)` (líneas 93–95) comprueban su vigencia con las fechas que almacenan.
- `ExtendedWarranty.getAdditionalCost()` (línea 37) calcula el costo con el precio de su propio producto.
- `Sale.canBeReturned()` (líneas 128–131) decide si el plazo de 30 días sigue abierto con la fecha que la propia venta almacena.
- `Return.calculateRefundAmount()` (líneas 81–91) calcula el reembolso con sus propios productos devueltos.

### 3.2 Creador – Correcto

- `ProductService.registerVideoGame` (línea 30) y `registerConsole` (línea 37) crean los objetos después de validar los datos recibidos y luego los persisten.
- `ProductRepositoryFile.parseLine` (líneas 228, 233) construye los objetos porque tiene los datos almacenados necesarios.
- Los constructores en `Cable`, `Controller` y `Memory` delegan en `Accessory` y `Product` (`super(...)`).

### 3.3 Controlador – No aplica

Estas clases manejan datos del dominio, servicios, persistencia o validación. No coordinan directamente los eventos de la interfaz de usuario (eso lo maneja `MenuUI`, del Líder).

### 3.4 Bajo Acoplamiento – Correcto

`ProductService` depende de la interfaz `ProductRepository` (línea 21), no de `ProductRepositoryFile`.

### 3.5 Alta Cohesión – Violación

**Violaciones: V-01, V-02** (ver sección 4).

### 3.6 Polimorfismo – Correcto

La clase base abstracta `GameZoneException` permite un manejo uniforme de excepciones en todo el sistema sin comprobar tipos concretos. 
*(Ver Observación O-05 para el análisis de `CategoryDiscount`).*

### 3.7 Fabricación Pura – Correcto

`ProductService` y `ProductRepositoryFile` reúnen responsabilidades que no pertenecen a `Product`. `ProductValidator` centraliza comprobaciones comunes sin sobrecargar a las entidades.

### 3.8 Indirección – Correcto

`ProductService` se comunica con los datos mediante la interfaz `ProductRepository`, evitando una dependencia directa de los mecanismos de almacenamiento en archivo.

### 3.9 Variaciones Protegidas – Correcto

- `Promotion.calculateDiscount` (línea 89) protege las variaciones entre los tipos de descuento.
- `Warranty.getAdditionalCost` (línea 84) protege las variaciones entre garantías básicas y extendidas.

---

## 4. Violaciones

### V-01

- **ID:** V-01
- **Patrón:** Alta Cohesión
- **Módulo:** Productos – Taller 1 (`Product`)
- **Evidencia:** `model/Product.java`, método `mostrar()`, líneas 77–78. Imprime información del producto en consola. Una búsqueda de `mostrar(` en todo `src/main/java` no muestra llamadas externas.
- **Explicación:** Mostrar información por consola no es responsabilidad de la entidad `Product`.
- **Consecuencia:** La clase mezcla los datos del producto con la lógica de presentación.
- **Solución propuesta:** Eliminar el método por completo ya que no se utiliza. Aplica Alta Cohesión.
- **Código de otro integrante:** No.

### V-02

- **ID:** V-02
- **Patrón:** Alta Cohesión
- **Módulo:** Accesorios – Requerimiento 1 (`Accessory`)
- **Evidencia:** `model/Accessory.java`, método `mostrar()`, líneas 69–70. Imprime información del accesorio en consola.
- **Explicación:** Mostrar información por consola no es responsabilidad de la entidad `Accessory`.
- **Consecuencia:** La clase mezcla los datos del accesorio con la lógica de presentación.
- **Solución propuesta:** Eliminar el método por completo ya que no se utiliza. Aplica Alta Cohesión.
- **Código de otro integrante:** No.

---

## 5. Problemas reportados al Líder Técnico

1. **Defecto funcional fuera de alcance (reportado por Dev 2 como F-01).** `ProductService.findByIdentifier` (líneas 41–45) lanza `ResourceNotFoundException` cuando el id no es un producto, en lugar de devolver `null`. Corregir esto altera el comportamiento observable, por lo que debe aplazarse.

---

## 6. Observaciones que no se cuentan como violaciones

- **O-05 (Polimorfismo en `CategoryDiscount`)**
  - Observación: `CategoryDiscount.belongsToTargetCategory(Product)` usa `instanceof` para identificar `VideoGame`, `Console` o `Accessory`.
  - Por qué no es violación: La lógica está encapsulada de forma segura dentro de un método `private`. Alterar la jerarquía de `Product` para reemplazar esto requiere cambios estructurales en clases núcleo que están siendo modificadas por otros miembros del equipo, arriesgando conflictos severos de integración.
- **O-06 (Experto en Información en `Accessory`)**
  - Observación: `Cable`, `Controller` y `Memory` duplican la implementación de `addProduct(Product)`, modificando directamente la lista `protected` `compatible` de su superclase `Accessory`.
  - Por qué no es violación: Centralizar esto en `Accessory` mejora el diseño pero modifica la interacción de la firma pública de las clases núcleo del modelo. Para evitar conflictos de fusión con la refactorización concurrente del Líder Técnico, se conserva esta decisión de diseño.
# Informe de Auditoría GRASP - Líder Técnico (versión en español, solo para lectura)


- **Auditor:** Líder Técnico
- **Estado auditado:** rama `develop`, commit `8f1f1d8` 
- **Fase:** 1 - Auditoría (no se modificó código).

## 1. Alcance

Código auditado según la tabla de responsabilidades (sección 4 del Requerimiento 7). Cada método lo audita quien lo desarrolló.

| Módulo (requerimiento) | Clases / métodos auditados |
|---|---|
| Ventas, interfaz y arranque (Taller 1) | `Sale` (todo excepto `canBeReturned`), `SaleRepository`, `SaleRepositoryFile` (ver nota), `SaleService` (`registerSale`, `findByCode`, `findAll`, `cancelSale`), `MenuUI` (el "ConsoleMenu" del requerimiento), `Main` |
| Accesorios (Req. 1) | Manejo de accesorios dentro de `SaleService.registerSale` y `cancelSale`; submenú de accesorios en `MenuUI` |
| Promociones (Req. 2) | `SaleService.applyBestPromotion`; `Sale.appliedPromotionName`, `Sale.discountAmount`, desglose en `Sale.Display()`; submenú de promociones en `MenuUI` |
| Devoluciones (Req. 3) | `ProductService.restoreStock`; submenú de devoluciones en `MenuUI` (`registerReturn`, `monthlyBalance`, listados) |
| Garantías (Req. 4) | Asignación de garantía básica y extendida y `warrantyCost` en `SaleService.registerSale`; `Sale.warrantyCost`; submenú de garantías en `MenuUI` |
| Excepciones y validaciones (Req. 6) | Manejo de excepciones en `MenuUI` (bloques `catch`) |

**Excluido (de otros responsables):** `Sale.canBeReturned` (Desarrollador 1), `ReturnRepositoryFile`, `ReturnService`, `WarrantyService`, validadores y clases de excepción.

**Nota sobre `SaleRepositoryFile`:** la tabla de responsabilidades solo lista `SaleRepository`. La implementación en archivo se escribió junto con la interfaz, así que se audita aquí y se deja señalado para que la consolidación confirme la asignación.

## 2. Resumen de veredictos

| # | Patrón | Veredicto | Hallazgos |
|---|---|---|---|
| 1 | Experto en Información | Violación | V-01, V-02 |
| 2 | Creador | Correcto | - |
| 3 | Controlador | Violación | V-06 |
| 4 | Bajo Acoplamiento | Violación | V-06 (compartido con Controlador) |
| 5 | Alta Cohesión | Violación | V-04 |
| 6 | Polimorfismo | Violación | V-03 |
| 7 | Fabricación Pura | Correcto | - |
| 8 | Indirección | Correcto | - |
| 9 | Variaciones Protegidas | Violación | V-05 |

Cuando un mismo fragmento rompe dos patrones se registra una sola vez, bajo el patrón que guía la corrección, y se referencia desde el otro.

## 3. Violaciones

### V-01 - `Sale` no expone su subtotal y la fórmula está duplicada

- **Patrón:** Experto en Información
- **Módulo:** Promociones (Req. 2) / Garantías (Req. 4), clase `Sale`
- **Evidencia:** `Sale.Display()`, línea 141: `double subtotal = total + discountAmount - warrantyCost;`. La misma aritmética se repite fuera de `Sale`, en `Return.resolveDiscountRatio()` (línea 93).
- **Explicación:** `Sale` tiene `total`, `discountAmount` y `warrantyCost`, así que es el experto en su propio subtotal. Mantiene ese conocimiento privado y obliga a otra clase a reconstruirlo con tres getters.
- **Consecuencia:** si cambia la fórmula del total (por ejemplo, que las garantías entren al descuento), hay que editar `Sale` y `Return` a la vez. Si se olvida una, el reembolso se desvía de la venta sin que nadie lo note.
- **Solución propuesta:** agregar `Sale.getSubtotal()` (valor derivado, sin atributo nuevo) y usarlo en `Display()`. Aplica Experto en Información.
- **Código de otro integrante:** sí. `Return.resolveDiscountRatio` (Desarrollador 1) debe llamar a `getSubtotal()`. Hallazgo de frontera, a unificar en la consolidación.

### V-02 - `SaleService` manipula el inventario directamente

- **Patrón:** Experto en Información (secundario: Bajo Acoplamiento)
- **Módulo:** Ventas (Taller 1), Accesorios (Req. 1)
- **Evidencia:** `SaleService.registerSale`, líneas 118-125 (`setAvailableQuantity(... - 1)` y luego `accessoryRepository.update` o `productRepository.update` según el tipo) y `cancelSale`, líneas 149-163, que reimplementa "leer, sumar 1, actualizar". `ProductService.restoreStock` y `AccessoryService.restoreStock` ya implementan esa última regla.
- **Explicación:** las reglas de stock pertenecen a `ProductService` y `AccessoryService`. El ajuste A3 del Req. 5 (paso 7) ya pide que `registerSale` delegue la actualización de inventario en ellos. `SaleService` además necesita `ProductRepository` y `AccessoryRepository` solo para esto.
- **Consecuencia:** la regla de stock existe en tres lugares. Un cambio como una alerta de stock mínimo hay que hacerlo en todos.
- **Solución propuesta:** delegar en `ProductService.updateStock` / `restoreStock` y `AccessoryService.updateStock` / `restoreStock`. **Comportamiento a preservar:** hoy `cancelSale` omite en silencio un producto que ya no existe en el catálogo (`if (actual != null)`, líneas 152 y 158). La versión delegada debe mantener esa tolerancia.
- **Código de otro integrante:** no se espera cambio en `AccessoryService` (Desarrollador 2), porque ya ofrece `restoreStock` y `updateStock`. Hay que confirmarlo en la consolidación.

### V-03 - Verificación de tipos (`instanceof`) en lugar de polimorfismo

- **Patrón:** Polimorfismo
- **Módulo:** Garantías (Req. 4), Accesorios (Req. 1), Ventas
- **Evidencia:**
  - `SaleService.registerSale`, línea 92: `if (p instanceof Console)` decide quién recibe garantía básica.
  - `SaleService.registerSale`, línea 120, y `cancelSale`, línea 150: `if (p instanceof Accessory)` elige qué repositorio actualizar.
  - `MenuUI.consultCompatibleAccessories`, línea 805: `if (!(producto instanceof Console consola))`.
- **Explicación:** el comportamiento que varía según el tipo de producto se decide con condicionales en quien llama, en vez de que lo decida el propio tipo.
- **Consecuencia:** un nuevo tipo de producto con garantía básica (por ejemplo, una portátil) obliga a editar `SaleService`. Un nuevo lugar de stock obliga a editar las tres ramas.
- **Solución propuesta:** (a) agregar `Product.requiresBasicWarranty()` (por defecto `false`; `Console` devuelve `true`) y quitar la comprobación de `Console`. (b) Después de V-02, el enrutamiento de stock queda centralizado en un solo lugar en vez de repetido; eliminarlo por completo exige una abstracción común de repositorios, que sale de un refactor que preserve el comportamiento. La consolidación debe decidir hasta dónde llegar.
- **Código de otro integrante:** sí, para (a): `Product` y `Console` son del Desarrollador 1. La comprobación en `MenuUI` podría usar una consulta de `ProductService` (Desarrollador 1).

### V-04 - `registerSale` hace demasiadas cosas

- **Patrón:** Alta Cohesión
- **Módulo:** Ventas; integración de Accesorios, Promociones y Garantías
- **Evidencia:** `SaleService.registerSale`, líneas 62-130 (unas 70 líneas). En un solo método valida cliente y vendedor, resuelve productos y accesorios, valida stock, crea la venta, asigna garantías básicas, aplica la promoción, agrega garantías extendidas, calcula el total final, actualiza el inventario y persiste.
- **Explicación:** varias responsabilidades independientes viven en un solo método, lo que dificulta leerlo y cambiar un paso sin tocar el resto.
- **Consecuencia:** cada módulo nuevo (el método ya se reescribió para accesorios, promociones y garantías) aumenta el riesgo de romper el orden de pasos definido en A3.
- **Solución propuesta:** dividir en métodos privados de una sola tarea (`resolveItems`, `assignWarranties`, `updateInventory`) respetando exactamente el orden de A3. No hace falta una clase nueva. Aplica Alta Cohesión.
- **Código de otro integrante:** no.

### V-05 - El formato de persistencia es el texto del recibo

- **Patrón:** Variaciones Protegidas (secundario: Fabricación Pura)
- **Módulo:** Ventas (Taller 1), Promociones (Req. 2)
- **Evidencia:** `SaleRepositoryFile.save`, línea 98, y `rewriteFile`, línea 206, escriben `sale.Display()`. `parseBlock` (líneas 224-293) reconstruye la venta buscando los prefijos `"code:"`, `"Sale date:"`, `"Client:"`, `"Subtotal:"`, `"Discount:"`, `"Warranty cost:"`, `"Total:"` y una expresión regular para las líneas de producto. Un comentario en las líneas 246-249 muestra que el formato ya obligó a un parche cuando se agregó la línea de subtotal.
- **Explicación:** el recibo que ve el usuario y el formato de almacenamiento son el mismo texto. Cambiar la redacción o el diseño del recibo (algo que pide A3) cambia lo que se escribe en disco y puede romper la lectura de datos viejos.
- **Consecuencia:** cualquier cambio en el recibo arriesga corromper la carga de `data/sales.txt`.
- **Solución propuesta:** mover la escritura del bloque a `SaleRepositoryFile` (un `toBlock(Sale)` privado que produzca exactamente el mismo texto de hoy) y dejar `Display()` solo para la interfaz. El contenido del archivo debe quedar idéntico byte a byte. Aplica Variaciones Protegidas mediante un contrato de persistencia estable.
- **Código de otro integrante:** no.

### V-06 - `MenuUI` es un controlador sobredimensionado y acoplado a todos los servicios

- **Patrón:** Controlador (secundarios: Alta Cohesión, Bajo Acoplamiento)
- **Módulo:** Interfaz de todos los módulos (Taller 1, Req. 1-4, Req. 6)
- **Evidencia:**
  - `MenuUI` tiene 1161 líneas y ocho submenús. Su constructor (línea 52) recibe 8 servicios.
  - `registerSale` (líneas 573-628) repite validaciones de negocio: llama dos veces por ciclo a `clientService.findByIdNumber` (líneas 583/586) y a `sellerService.findByIdNumber` (líneas 592/596) para comprobar existencia, aunque `SaleService.registerSale` ya valida ambos.
  - El parseo de listas separadas por comas se repite (líneas 606, 612, 617, 1109).
- **Explicación:** un controlador de caso de uso debe recibir el evento y delegar. Aquí una sola clase atiende todos los módulos y además duplica validación que corresponde a la capa de servicios.
- **Consecuencia:** cada funcionalidad nueva agranda esta clase, y un cambio en una regla de negocio hay que replicarlo en la interfaz.
- **Solución propuesta:** dividir el menú de consola en un controlador por módulo (clientes, vendedores, productos, ventas, accesorios, promociones, garantías, devoluciones) detrás de un menú principal pequeño, y quitar las comprobaciones de existencia de la interfaz (ver F-01 sobre por qué hay que hacerlo sin cambiar el comportamiento). Cada controlador dependería solo de los servicios que necesita.
- **Código de otro integrante:** no (todas son mis clases de interfaz).

## 4. Secciones por patrón

### 4.1 Experto en Información - Violación
Ver V-01 y V-02. Ejemplos correctos: `Sale.calculateTotal` suma precios porque `Sale` posee sus productos; `ProductService.restoreStock` (líneas 66-75) valida y cambia el stock usando solo datos del servicio de productos y su repositorio.

### 4.2 Creador - Correcto
- `SaleService.registerSale`, línea 89 (`new Sale(...)`): el servicio tiene el cliente, el vendedor y la lista de productos para inicializar la venta.
- `Main` crea todos los repositorios y servicios en un solo lugar (raíz de composición).
- `SaleRepositoryFile.parseBlock`, línea 287, reconstruye `Sale` desde datos almacenados, algo aceptable en un repositorio.
- `MenuUI` no crea objetos de dominio.

### 4.3 Controlador - Violación
Ver V-06.

### 4.4 Bajo Acoplamiento - Violación
El único fragmento de mi alcance es el constructor de `MenuUI` con ocho servicios (V-06). Se registra allí para no duplicar el hallazgo. `SaleService` depende de `PromotionService` y `WarrantyService` de forma concreta; se dejó así porque la cantidad de colaboradores no baja si se reemplazan repositorios por servicios (V-02), así que no se declara una violación aparte.

### 4.5 Alta Cohesión - Violación
Ver V-04 (y el tamaño de `MenuUI` en V-06).

### 4.6 Polimorfismo - Violación
Ver V-03.

### 4.7 Fabricación Pura - Correcto
`SaleService` (lógica de caso de uso que no pertenece a `Sale`), `SaleRepository` / `SaleRepositoryFile` (acceso a archivos fuera del modelo) y `Main` son fabricaciones bien ubicadas. El modelo no tiene acceso a archivos. El problema de formato queda registrado en Variaciones Protegidas (V-05).

### 4.8 Indirección - Correcto
`SaleService` depende de la interfaz `SaleRepository`, no de la clase de archivo. En `Main` (líneas 42-48), `SaleRepositoryFile` recibe funciones de búsqueda (`clientRepository::findByIdNumber`, ...) en vez de otros repositorios, lo que evita acoplar persistencia con persistencia.

### 4.9 Variaciones Protegidas - Violación
Ver V-05.

## 5. Observaciones fuera de mi alcance (para la consolidación)

- `ReturnRepositoryFile` (persistencia) importa `SaleService`, `ProductService` y `AccessoryService` (líneas 6-8, 157, 189, 193), contra el sentido ui → service → persistence → model. Origen: Desarrollador 2. La corrección también cambia el cableado en `Main` (mío), como se hizo con garantías en el ajuste A2.
- `ReturnService` repite el enrutamiento `instanceof Accessory` (línea 103) y genera ids con `UUID` (línea 145), igual que `WarrantyService` (línea 59). Origen: Desarrollador 2.
- `Return.resolveDiscountRatio` duplica la fórmula del subtotal (ver V-01). Origen: Desarrollador 1.

## 6. Defectos funcionales encontrados (fuera de alcance, no se corrigen en el Req. 7)

| ID | Evidencia | Efecto |
|---|---|---|
| F-01 | `ClientService.findByIdNumber` (líneas 50-54) ahora lanza `ResourceNotFoundException` (una `RuntimeException`). `MenuUI.registerSale` (líneas 583-586) y `registerReturn` siguen comprobando el resultado con `== null`. El `catch` de la línea 622 solo maneja `IllegalArgumentException \| IllegalStateException`. Mismo desajuste en `searchSale` (líneas 630-633). | Un cliente, vendedor o código de venta inexistente no se informa con un mensaje amable. La excepción puede llegar al usuario sin manejar. Nota: quitar las comprobaciones de la interfaz (V-06) no debe cambiar este comportamiento visible. |
| F-02 | `registerSale` carga cada id con una lectura separada del repositorio (`ProductRepositoryFile.findByIdentifier` relee el archivo) y valida el stock con cantidad 1 por ítem (líneas 70-85). **Confirmado con una prueba:** registrar `["V1", "V1"]` con stock 5 guarda la venta con dos líneas de producto y cobra ambas (total 601.0 por dos ítems de 300.5), pero el stock queda en 4 en vez de 3. | Repetir el mismo identificador pasa la validación de stock cada vez contra el mismo valor inicial, y la segunda escritura pisa a la primera: se cobra una unidad que nunca sale del inventario. El comportamiento es idéntico antes y después del refactor del Req. 7. |
| F-03 | `Sale.total` tiene visibilidad de paquete (`double total;`, línea 26). | Rompe la regla de encapsulamiento estricto. No es un hallazgo GRASP, es trivial de arreglar y se registra aparte. |
| F-04 | `SaleRepositoryFile.save` rechaza un código de venta repetido con `BusinessRuleException`, pero `registerSale` solo llama a `save` como último paso, después de asignar garantías y actualizar el inventario. **Confirmado con una prueba:** tras una venta válida de la consola `K1` (stock 5 → 4, 1 garantía), una segunda `registerSale` con el mismo código es rechazada, pero el stock baja a 3 y se guarda una segunda garantía, mientras solo queda 1 venta almacenada. | Un código repetido rechazado deja el inventario descontado y una garantía huérfana sin venta. La comprobación de código duplicado debería ejecutarse al inicio de `registerSale`, antes de cambiar cualquier estado. El comportamiento es idéntico antes y después del refactor del Req. 7. |

## 7. Corrección a mi revisión preliminar anterior

`SaleRepositoryFile` ya no reconstruye todos los productos como `SimpleProduct`. `parseBlock` primero resuelve el producto o accesorio real por identificador (líneas 262-265) y usa `SimpleProduct` solo como respaldo (líneas 266-270). La preocupación anterior por identificadores incorrectos al recargar quedó, en su mayor parte, resuelta.


# Auditoría SOLID – Líder Técnico (Samuel David Pinto Ortiz)

**Requerimiento 8 – Auditoría y Corrección SOLID · Fase 1**
**Rama:** `feature/solid-audit` · **Archivo:** `docs/solid-audit/leader-audit.md`
**Estado auditado:** `develop` con la corrección GRASP del Requerimiento 7 integrada (commit `3372884`, merge del PR #51).

## Criterio de la auditoría

Una violación se registra solo cuando existe un escenario concreto de cambio que obliga a editar código que ya funciona y ese cambio es previsible. Esta versión del sistema es la **entrega final**: no se agregarán módulos, tipos de producto, promociones ni implementaciones alternativas de servicios o repositorios. Por esa razón los principios que protegen frente al cambio futuro (OCP, ISP, DIP) y la separación de responsabilidades que solo se paga al evolucionar (SRP) no producen violaciones que justifiquen modificar código estable. Las observaciones de diseño se dejan documentadas en la sección 5, sin registrarlas como violaciones.

---

## 1. Alcance

| Módulo / requerimiento | Clases y métodos auditados |
|---|---|
| Ventas, interfaz y arranque (Taller 1) | `Sale`, `SaleRepository`, `SaleRepositoryFile`, `SaleService`, `MenuUI`, `Main` |
| Accesorios (Req. 1) | Integración en `SaleService` (`resolveItems`, `updateInventory`, `restoreStock`) y conexión en `MenuUI`/`AccessoryMenu` |
| Promociones (Req. 2) | `SaleService.applyBestPromotion`, `Sale` (`appliedPromotionName`, `discountAmount`, `Display`), conexión en `MenuUI` |
| Devoluciones (Req. 3) | `ProductService.restoreStock`, conexión en `MenuUI`/`ReturnMenu` |
| Garantías (Req. 4) | `SaleService.assignBasicWarranties`, `assignExtendedWarranties`, `Sale.warrantyCost`, conexión en `MenuUI` |
| Excepciones y validaciones (Req. 6) | Manejo de excepciones en `SaleMenu` y en los demás submenús creados por el líder |
| Corrección GRASP (Req. 7) | `SubMenu`, `ClientMenu`, `SellerMenu`, `ProductMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `WarrantyMenu`, `ReturnMenu`, `Sale.getSubtotal`, `Product.requiresBasicWarranty`, `Console.requiresBasicWarranty`, métodos privados extraídos de `SaleService.registerSale` |

El código de los demás integrantes **no** se audita aquí.

---

## 2. Veredicto por principio (código del líder)

| Principio | Veredicto | Hallazgos |
|---|---|---|
| SRP | Sin violaciones registradas | – |
| OCP | Sin violaciones registradas | – |
| LSP | **Correcto** | – |
| ISP | Sin violaciones registradas | – |
| DIP | **Correcto** | – |

---

## 3. Auditoría por principio

### 3.1 Responsabilidad Única (SRP) – Sin violaciones registradas

- **Dónde se cumple:** cada submenú atiende un solo módulo (`ClientMenu`, `SellerMenu`, `ProductMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `WarrantyMenu`, `ReturnMenu`); `MenuUI` solo enruta el menú principal; `SubMenu` concentra las ayudas de entrada; `Main` solo conecta las piezas; `SaleService` descompone `registerSale` en métodos privados con un propósito cada uno (`resolveItems`, `assignBasicWarranties`, `applyBestPromotion`, `assignExtendedWarranties`, `updateInventory`); `ProductService.restoreStock` tiene un solo propósito.
- **Por qué no se registran violaciones:** las razones de cambio que pueden separar más a `Sale` (formato del recibo) y a `SaleService` (inventario) solo se materializarían si el sistema siguiera evolucionando. En la entrega final no existe un cambio previsible que las active. Ver observaciones O-01 y O-02.

### 3.2 Abierto/Cerrado (OCP) – Sin violaciones registradas

- **Dónde se cumple:** `Product.requiresBasicWarranty()` con la sobrescritura en `Console` (el polimorfismo de GRASP reemplazó una verificación de tipo, de modo que un producto nuevo con garantía básica no exige cambios en `SaleService.assignBasicWarranties`); `Promotion.calculateDiscount` se usa de forma polimórfica en `SaleService.applyBestPromotion`.
- **Por qué no se registran violaciones:** las verificaciones `instanceof Accessory` de `SaleService` y el `switch` de `MenuUI` solo obligarían a editar código si se agregaran nuevos tipos de ítem o nuevos módulos, y el sistema se entrega cerrado con los existentes. Ver observaciones O-03 y O-04.

### 3.3 Sustitución de Liskov (LSP) – **Correcto**

- Cada subclase de `SubMenu` implementa `show()` y `MenuUI` la usa por el tipo base sin condiciones.
- `Console.requiresBasicWarranty()` no endurece el contrato y devuelve un booleano válido; `SaleService` trata cualquier `Product` igual.
- `SaleRepositoryFile` respeta el contrato de `SaleRepository` (`save` falla con duplicados, `findByCode` devuelve `null` si no existe, `deleteByCode` devuelve un booleano).
- Observación: la clase privada `SaleRepositoryFile.SimpleProduct` usa el título como identificador (líneas 269 y 299-312). Se registra como defecto funcional D-03 porque corregirla cambia lo que se escribe en `sales.txt`.

### 3.4 Segregación de Interfaces (ISP) – Sin violaciones registradas

- **Dónde se cumple:** los menús reciben solo los servicios que usan (por ejemplo `ReturnMenu` recibe `SaleService` y `ReturnService`); `SaleService` usa tres de las cinco operaciones de `SaleRepository`.
- **Por qué no se registran violaciones:** `SaleRepository` declara `update`, que ningún cliente usa, y `WarrantyService` recibe la interfaz completa solo para `findByCode`. Dividirla afectaría código del Desarrollador 2 y no aporta nada a un sistema que no tendrá nuevos clientes ni implementaciones. Ver observación O-05.

### 3.5 Inversión de Dependencias (DIP) – **Correcto**

- **Dónde se cumple:** `SaleService` depende de las interfaces `SaleRepository`, `ClientRepository` y `SellerRepository` y las recibe por constructor; `SaleRepositoryFile` recibe sus colaboradores como funciones (`Function`); `Main` es el único lugar que instancia repositorios, servicios y menús concretos (raíz de composición).
- **Dependencias de servicios concretos (aceptadas):** `SaleService` usa `ProductService`, `AccessoryService`, `PromotionService` y `WarrantyService`, y `MenuUI` crea los submenús. Los servicios y menús no tendrán implementaciones alternativas, y la dependencia `ui → service → persistence → model` es la arquitectura por capas del curso.

---

## 4. Tabla de cobertura (líder)

| Clase / método | Auditado por |
|---|---|
| `Sale`, `SaleRepository`, `SaleRepositoryFile`, `SaleService` | Líder |
| `MenuUI`, `SubMenu`, `ClientMenu`, `SellerMenu`, `ProductMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `WarrantyMenu`, `ReturnMenu` | Líder |
| `Main` | Líder |
| `ProductService.restoreStock`, `Product.requiresBasicWarranty`, `Console.requiresBasicWarranty` | Líder |

---

## 5. Observaciones de diseño (no son violaciones; no se modifica código)

| ID | Dónde | Observación |
|---|---|---|
| O-01 | `Sale.Display()` (líneas 135-163) | La clase de dominio arma el texto del recibo que muestran `SaleMenu`, `ClientMenu` y `SellerMenu`; `SaleRepositoryFile.toBlock` mantiene un formato parecido. Cambiar el recibo exigiría editar `Sale`. |
| O-02 | `SaleService.resolveItems`, `updateInventory`, `restoreStock` (129-214) | `SaleService` conoce además cómo se valida, descuenta y restaura el stock de productos y accesorios. |
| O-03 | `SaleService` líneas 189, 196 y 205 | Verificaciones `instanceof Accessory` y dos listas paralelas de identificadores en `registerSale`. |
| O-04 | `MenuUI` | Campos fijos, constructor, texto y `switch` deben editarse para agregar un menú. |
| O-05 | `SaleRepository` | Incluye `update`, que ningún cliente usa; `WarrantyService` recibe la interfaz completa para usar solo `findByCode`. |

Estas observaciones quedan como referencia por si el sistema se extiende en el futuro.

---

## 6. Hallazgos de frontera

Ninguno.

---

## 7. Defectos funcionales fuera de alcance (no se corrigen en este requerimiento)

- **D-01 – Los menús no atrapan las excepciones del dominio.** `SaleMenu.registerSale` atrapa `IllegalArgumentException | IllegalStateException`, pero `BusinessRuleException` (stock insuficiente, código duplicado) y `ResourceNotFoundException` (producto inexistente) extienden `GameZoneException`, una `RuntimeException`. Salen del menú sin ser atrapadas. El mismo patrón `catch (IllegalArgumentException)` aparece en `ClientMenu`, `SellerMenu`, `ProductMenu`, `AccessoryMenu`, `PromotionMenu` y `ReturnMenu`. Hallado leyendo el código; la aplicación no se ejecutó.
- **D-02 – Verificaciones de `null` que nunca se cumplen.** `SaleService.findByCode` lanza `ResourceNotFoundException` en vez de devolver `null`, por lo que `SaleMenu.searchSale` (`s != null ? … : "No se encontró la venta."`), `SaleMenu.cancelSale` (`ok ? … : "No se encontró la venta."`) y el ciclo `findByCode(saleId) == null` de `ReturnMenu.registerReturn` nunca llegan a su rama de "no encontrado".
- **D-03 – `SimpleProduct` usa el título como identificador.** En `SaleRepositoryFile.parseBlock`, cuando un producto ya no existe en el catálogo, `new SimpleProduct(titulo, precio)` guarda el título como identificador. Tras cualquier `update` o `deleteByCode`, `rewriteFile` escribe `[título]` en lugar de `[identificador]` para esa venta, alterando el archivo almacenado.
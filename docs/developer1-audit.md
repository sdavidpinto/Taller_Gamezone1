# Informe de Auditoría GRASP – Desarrollador 2

- **Autor:** Jose Gabriel Quintero Ortiz[cite: 18]
- **Rol:** Desarrollador 2 (módulo de personas y sus extensiones)[cite: 18]
- **Rama:** `feature/grasp-audit`[cite: 18]
- **Estado auditado:** `develop`[cite: 18]
- **Código:** Java, capas `ui → services → persistence → model`[cite: 18]

---

## 1. Alcance

Solo se audita el código desarrollado por el Desarrollador 2[cite: 18]. Los problemas cuyo origen está en código de otro integrante se reportan al Líder (sección 5), no se auditan[cite: 18].

- **Personas (Taller 1)**[cite: 18]
  - Clases auditadas: `Person`, `Client`, `Seller`[cite: 18]
  - Métodos auditados: todos (constructores, accesores, `display`)[cite: 18]
  - Clases auditadas: `ClientRepository`, `ClientRepositoryFile`, `SellerRepository`, `SellerRepositoryFile`[cite: 18]
  - Métodos auditados: todos (`save`, `findByIdNumber`, `findAll`, `update`, `deleteByIdNumber`, `toLine`, `parseLine`, `rewriteFile`)[cite: 18]
  - Clases auditadas: `ClientService`, `SellerService`[cite: 18]
  - Métodos auditados: todos (`register*`, `find*`, `update*`, `delete*`)[cite: 18]
- **Accesorios (Req. 1)**[cite: 18]
  - Clases auditadas: `AccessoryRepository`, `AccessoryRepositoryFile`, `AccessoryService`[cite: 18]
  - Métodos auditados: todos, incluidos `restoreStock`, `findByType`, `addCompatibleProduct`[cite: 18]
- **Promociones (Req. 2)**[cite: 18]
  - Clases auditadas: `PromotionRepository`, `PromotionRepositoryFile`, `PromotionService`[cite: 18]
  - Métodos auditados: todos, incluido `findBestPromotionFor`[cite: 18]
- **Devoluciones (Req. 3)**[cite: 18]
  - Clases auditadas: `ReturnRepository`, `ReturnRepositoryFile`, `ReturnService`[cite: 18]
  - Métodos auditados: todos, incluido el balance mensual (`calculateMonthlySales`, `calculateMonthlyReturns`, `generateMonthlyBalance`)[cite: 18]
- **Garantías (Req. 4)**[cite: 18]
  - Clases auditadas: `WarrantyRepository`, `WarrantyRepositoryFile`, `WarrantyService`[cite: 18]
  - Métodos auditados: todos, incluidos `cancelWarranties` y `findWarrantyByProduct`[cite: 18]
- **Excepciones y validaciones (Req. 6)**[cite: 18]
  - Clases auditadas: uso de excepciones y validadores dentro de los servicios y repositorios anteriores[cite: 18]
  - Métodos auditados: llamadas a `PersonValidator` y excepciones lanzadas por servicios y repositorios[cite: 18]

No auditado por mí (otros responsables): jerarquías `Product`, `Accessory`, `Promotion`, `Warranty`, `Return` y `Sale.canBeReturned` (Desarrollador 1); `Sale`, `SaleRepository`, `SaleService`, `MenuUI`, `Main`, `ProductService.restoreStock` (Líder); paquetes `exceptions` y `validation` (Desarrollador 1)[cite: 18].

---

## 2. Resumen de veredictos

- **1. Experto en Información:** Correcto. Hallazgos: – (observación O-06)[cite: 18]
- **2. Creador:** Correcto. Hallazgos: –[cite: 18]
- **3. Controlador:** Correcto. Hallazgos: –[cite: 18]
- **4. Bajo Acoplamiento:** Violación. Hallazgos: V-01 (patrón guía)[cite: 18]
- **5. Alta Cohesión:** Violación. Hallazgos: V-02[cite: 18]
- **6. Polimorfismo:** Violación. Hallazgos: V-03 (patrón guía)[cite: 18]
- **7. Fabricación Pura:** Correcto. Hallazgos: – (observación O-01)[cite: 18]
- **8. Indirección:** Violación. Hallazgos: V-01 (patrón secundario, no se cuenta dos veces)[cite: 18]
- **9. Variaciones Protegidas:** Violación. Hallazgos: V-03 (patrón secundario, no se cuenta dos veces)[cite: 18]

Se encontraron tres violaciones distintas (V-01 a V-03)[cite: 18]. Un hallazgo que toca dos patrones se registra una sola vez, bajo el patrón guía (ver `docs/grasp-analysis.md`, pregunta 3)[cite: 18].

---

## 3. Auditoría por patrón

### 3.1 Experto en Información – Correcto

- `ReturnService.registerReturn` (línea 70) pregunta a `Sale.canBeReturned()` si el plazo de 30 días sigue abierto en lugar de calcular fechas[cite: 18].
- `PromotionService.findBestPromotionFor` (línea 173) pide a cada `Promotion` su `calculateDiscount(...)` y `listActivePromotions` (línea 137) pregunta `promotion.isActive()`[cite: 18].
- `WarrantyService.cancelWarranties` (línea 108) pide a cada `Warranty` su `getAdditionalCost()`[cite: 18].
- `Client.display()` y `Seller.display()` arman su texto con sus propios atributos[cite: 18].

Observación O-06 (navegación de `ReturnService` por `Return` y `Sale`): se analizó y no se cuenta como violación; ver sección 6[cite: 18].

### 3.2 Creador – Correcto

- `ClientService.registerClient` (línea 39) y `SellerService.registerSeller` (línea 41) crean `Client` / `Seller` tras validar los datos que reciben[cite: 18].
- `PromotionService` crea los tres tipos de descuento (líneas 61, 84, 104) y conserva y persiste la lista (`saveNewPromotion`, líneas 113–117)[cite: 18].
- `WarrantyService.assignBasicWarranty` / `assignExtendedWarranty` (líneas 83, 127) crean la garantía y la registran[cite: 18].
- `ReturnService.registerReturn` (línea 76) crea la `Return` que registra y persiste[cite: 18].
- `ClientRepositoryFile.parseLine` (línea 198), `SellerRepositoryFile.parseLine` (línea 200) y `ReturnRepositoryFile.parseLine` (línea 171) construyen objetos porque tienen los datos almacenados necesarios para inicializarlos[cite: 18].

Ninguna clase crea instancias que deba poseer otra[cite: 18].

### 3.3 Controlador – Correcto

La interfaz (código del Líder, no auditado aquí) envía cada evento del menú a un servicio de este módulo, y esos servicios actúan como coordinadores del caso de uso y delegan el trabajo[cite: 18]:
- `ReturnService.registerReturn` (líneas 65–92) coordina el caso de uso: busca la venta, consulta `Sale.canBeReturned`, resuelve los ítems, restaura stock, cancela garantías y persiste. No hace E/S ni formato[cite: 18].
- `ClientService` / `SellerService` delegan la validación en `PersonValidator` y la persistencia en el repositorio[cite: 18].

### 3.4 Bajo Acoplamiento – Violación

**Uso correcto**
- Todos los servicios reciben **interfaces** de repositorio por constructor (`ClientService` línea 21, `PromotionService` línea 24, `AccessoryService` línea 18, `ReturnService` líneas 41–49)[cite: 18].
- `WarrantyRepositoryFile` no conoce `ProductRepository`, `SaleRepository` ni servicios: `WarrantyRepository.loadAll` (línea 35) recibe dos funciones resolutoras[cite: 18].

**Violación: V-01** (detalle en la sección 4). Además, `ReturnService` importa las subclases concretas `Console` y `Accessory` (líneas 3–4); ese acoplamiento desaparece con V-03, por lo que no se registra aparte[cite: 18].

### 3.5 Alta Cohesión – Violación

**Uso correcto**
- `ClientService`, `SellerService`, `PromotionService`, `AccessoryService` y `WarrantyService` se enfocan cada uno en una entidad[cite: 18].
- `Client`, `Seller` y `Person` solo contienen datos de la persona y su texto de presentación[cite: 18].

**Violación: V-02** (detalle en la sección 4)[cite: 18].

La observación O-02 (balance mensual dentro de `ReturnService`) se analizó para cohesión y no se cuenta como violación; ver sección 6[cite: 18].

### 3.6 Polimorfismo – Violación

**Uso correcto**
- `Person.display()` es abstracto e implementado por `Client` y `Seller`; quien lo llama nunca consulta el tipo[cite: 18].
- `PromotionService.findBestPromotionFor` y `WarrantyService.cancelWarranties` llaman métodos abstractos (`Promotion.calculateDiscount`, `Warranty.getAdditionalCost`) sin preguntar tipos[cite: 18].

**Violación: V-03** (detalle en la sección 4)[cite: 18].

Revisado y no contado: los tres repositorios de archivo (`AccessoryRepositoryFile` líneas 173/183/192 y 232/237/241, `PromotionRepositoryFile` líneas 102/110/119 y 150/153/157, `WarrantyRepositoryFile` líneas 118/120 y 160/162) eligen el formato de texto según el tipo concreto al escribir y según una etiqueta al leer. Ver observación O-03[cite: 18].

### 3.7 Fabricación Pura – Correcto

- `ClientService` / `SellerService` usan `PersonValidator` (`registerClient` líneas 37–38, `updateClient` líneas 78–79, `findByIdNumber` línea 52): las reglas de campos obligatorios, email válido, existencia y unicidad no pertenecen a `Client` ni a `Seller`, por lo que viven en una clase fabricada[cite: 18].
- Los repositorios de archivo asumen la responsabilidad de acceso a archivos que las clases del modelo tienen prohibida[cite: 18].

La observación O-01 (`requireNonBlank` repetido en dos servicios) se analizó aquí y no se cuenta; ver sección 6[cite: 18].

### 3.8 Indirección – Violación

**Uso correcto**
- `WarrantyRepository.loadAll(Function, Function)` (línea 35 de la interfaz y línea 86 de la implementación) es la indirección que desacopla garantías de productos y ventas; su Javadoc explica que rompe la construcción circular que había en `Main`[cite: 18].
- `WarrantyService.loadAllResolved` (líneas 49–51) entrega los resolutores (`productService::findByIdentifier`, `saleRepository::findByCode`)[cite: 18].

**Violación: V-01** (falta el intermediario entre `ReturnRepositoryFile` y los servicios). Se registra una sola vez bajo Bajo Acoplamiento[cite: 18].

### 3.9 Variaciones Protegidas – Violación

**Uso correcto**
- El almacenamiento puede cambiar (archivos hoy, base de datos mañana) sin tocar los servicios, porque cada uno depende de una interfaz `*Repository`[cite: 18].
- Los tipos de promoción quedan protegidos tras `Promotion.calculateDiscount`; los de garantía, tras `Warranty.getAdditionalCost`[cite: 18].

**Violación: V-03** (el punto de variación "qué ítems tienen garantía / qué servicio maneja el stock de un ítem" está atado a las clases `Console` y `Accessory`). Se registra una sola vez bajo Polimorfismo[cite: 18].

---

## 4. Violaciones

### V-01

- **ID:** V-01[cite: 18]
- **Patrón:** Bajo Acoplamiento (guía). También incumple Indirección[cite: 18].
- **Módulo:** Devoluciones – Requerimiento 3 (`ReturnRepositoryFile`)[cite: 18]
- **Evidencia:** `persistence/ReturnRepositoryFile.java`: importa `services.AccessoryService`, `services.ProductService`, `services.SaleService` (líneas 6–8); campos (líneas 27–29); constructor `ReturnRepositoryFile(String, SaleService, ProductService, AccessoryService)` (líneas 39–44); llamadas `saleService.findByCode(saleCode)` (línea 157), `productService.findByIdentifier(itemId)` (línea 189) y `accessoryService.findByIdentifier(itemId)` (línea 193). Cableado en `Main.java` línea 65: `new ReturnRepositoryFile(dataFile("returns.csv"), saleService, productService, accesoryService)`[cite: 18].
- **Explicación:** Una clase de persistencia depende de tres clases concretas de la capa de servicios. Invierte el sentido `ui → services → persistence → model` y ata el repositorio a las firmas exactas de tres servicios, cuando solo necesita "buscar una venta por código", "un producto por id" y "un accesorio por id". `WarrantyRepositoryFile` ya resuelve la misma necesidad con funciones resolutoras[cite: 18].
- **Consecuencia:** El repositorio no se puede construir ni probar sin tres servicios y, a través de `SaleService`, sus repositorios; cualquier cambio en esos servicios (por ejemplo la firma de `findByIdentifier`) rompe la capa de persistencia[cite: 18].
- **Solución propuesta:** Reemplazar los tres campos de servicio por tres funciones resolutoras (`Function<String, Sale>`, `Function<String, Product>`, `Function<String, Accessory>`) recibidas por constructor, la misma técnica de `WarrantyRepository.loadAll`. Aplica Bajo Acoplamiento mediante Indirección. No se crea ninguna clase[cite: 18].
- **Código de otro integrante:** Sí – `Main.java` (Líder): la línea de construcción pasa a `new ReturnRepositoryFile(dataFile("returns.csv"), saleService::findByCode, productService::findByIdentifier, accesoryService::findByIdentifier)`. Una línea[cite: 18].

### V-02

- **ID:** V-02[cite: 18]
- **Patrón:** Alta Cohesión[cite: 18]
- **Módulo:** Personas – Taller 1 (`ClientRepositoryFile`, `SellerRepositoryFile`)[cite: 18]
- **Evidencia:** `ClientRepositoryFile.save`, líneas 65–67: `if (findByIdNumber(client.getIdNumber()) != null) { throw new BusinessRuleException("Ya existe un cliente con ID: " ...); }`. `SellerRepositoryFile.save`, líneas 65–67: la misma regla con el mensaje "Ya existe un vendedor con ID: ...". La misma regla ya se aplica antes de guardar: `ClientService.registerClient` línea 38 y `SellerService.registerSeller` línea 40 llaman `PersonValidator.validateUniquePerson(...)` (`PersonValidator.java` líneas 70–76). Los servicios son los únicos que llaman a estos `save`[cite: 18].
- **Explicación:** `save` solo debería escribir en el archivo. Además decide una regla de negocio ("el id debe ser único") que ya pertenece al servicio y al validador. La responsabilidad está duplicada en dos capas, con mensajes distintos[cite: 18].
- **Consecuencia:** Dos fuentes de verdad: cambiar la regla o su mensaje exige editar tres lugares. La comprobación del repositorio es inalcanzable desde el servicio (el validador siempre actúa primero, por lo que el usuario solo ve el mensaje del servicio), y cada registro lee el archivo completo una vez más (`save` → `findByIdNumber` → `findAll`)[cite: 18].
- **Solución propuesta:** Eliminar la comprobación de unicidad, el import de `BusinessRuleException` y el `@throws` correspondiente en ambos `save`, dejando a `ClientService` / `PersonValidator` como único lugar que aplica la regla. Aplica Alta Cohesión (el repositorio conserva solo persistencia). No se crea ninguna clase[cite: 18].
- **Código de otro integrante:** No[cite: 18].

### V-03

- **ID:** V-03[cite: 18]
- **Patrón:** Polimorfismo (guía). También incumple Variaciones Protegidas[cite: 18].
- **Módulo:** Devoluciones – Requerimiento 3 (`ReturnService`)[cite: 18]
- **Evidencia:** `services/ReturnService.java`, `registerReturn`, líneas 79–84: `restoreStockFor(item); if (item instanceof Console) { warrantyRefund += warrantyService.cancelWarranties(item.getIdentifier(), sale.getCode()); }`. `restoreStockFor`, líneas 102–108: `if (item instanceof Accessory accessory) { accessoryService.restoreStock(...) } else { productService.restoreStock(...) }`. Imports de las clases concretas `model.Console` y `model.Accessory` (líneas 3–4)[cite: 18].
- **Explicación:** El comportamiento cambia según el tipo concreto del ítem y el servicio lo decide con preguntas de tipo. El conocimiento "este ítem tiene garantía" ya existe en `WarrantyService` (hay un registro por producto y venta) y "este ítem es un accesorio" ya lo conoce `AccessoryService` (lo encuentra o no)[cite: 18].
- **Consecuencia:** Un nuevo tipo de producto con garantía, o un nuevo tipo de stock, obliga a editar `ReturnService`; olvidarlo omite en silencio el reembolso de garantía o restaura el stock equivocado[cite: 18].
- **Solución propuesta:** Dejar de preguntar el tipo y consultar al colaborador que posee el conocimiento: (a) cancelar la garantía cuando `warrantyService.findWarrantyByProduct(id, saleCode)` devuelve una garantía; (b) llamar `accessoryService.restoreStock(id, 1)` y, si responde `false` (no es accesorio), restaurar con `productService`. Aplica la intención de Polimorfismo (sin condicionales por tipo) sin agregar métodos a la jerarquía `Product`, que es del Desarrollador 1. No se crea ninguna clase[cite: 18].
- **Código de otro integrante:** No[cite: 18].

---

## 5. Problemas reportados al Líder Técnico

Nacen en código que no audito; el Líder decide en la consolidación cómo clasificarlos[cite: 18].

1. **`Main.java` línea 65 (frontera de V-01).** Es el único cambio fuera de mi código para V-01. Reparto sugerido: el Desarrollador 2 cambia `ReturnRepositoryFile`; el Líder cambia esa línea en la misma sesión, porque mi commit no compila sin ella[cite: 18].
2. **Mismas preguntas de tipo en `SaleService`** (Líder), líneas 92, 120 y 150 (`instanceof Console`, `instanceof Accessory`). Se reporta para su propia auditoría de Polimorfismo[cite: 18].
3. **Defecto funcional fuera de alcance F-01.** Después de guardar una devolución que incluye un accesorio, cualquier consulta que cargue las devoluciones falla (todas, por cliente, por venta y el balance mensual). Evidencia: `ReturnRepositoryFile.resolveItem` (líneas 188–194) llama primero a `productService.findByIdentifier(itemId)`; `ProductService.findByIdentifier` (líneas 41–45, Desarrollador 1) lanza `ResourceNotFoundException` cuando el id no es un producto (`ProductValidator.validateProductExists`) en lugar de devolver `null`, así que nunca se llega al respaldo con `accessoryService` de la línea 193. En la ejecución de referencia del escenario de no regresión, las cinco consultas de devoluciones posteriores a devolver un accesorio muestran `EXC ResourceNotFoundException: ... producto con identificador A1 no fue encontrado`. Corregirlo cambia lo que ve el usuario, por lo que **no se corrige** en este requerimiento[cite: 18].

---

## 6. Observaciones que no se cuentan como violaciones

- **O-01**
  - Observación: `requireNonBlank` es una copia privada en `AccessoryService` (líneas 29–33) y `PromotionService` (líneas 31–35), y ambos lanzan `IllegalArgumentException`, mientras el módulo de personas usa `PersonValidator` y el paquete `exceptions`[cite: 18].
  - Por qué no es violación: Quitar las copias exige un ayudante compartido en `validation` (paquete del Desarrollador 1) o una clase nueva, y pasar a la jerarquía `exceptions` cambiaría el tipo de excepción y, con él, el `catch` de la interfaz que la atiende (`MenuUI` líneas 149–158), alterando el comportamiento observable[cite: 18].
- **O-02**
  - Observación: El balance mensual (`calculateMonthlySales`, `calculateMonthlyReturns`, `generateMonthlyBalance`, líneas 199–239) está en `ReturnService` aunque también lee ventas[cite: 18].
  - Por qué no es violación: El Requerimiento 3 ubica `generateMonthlyBalance` en `ReturnService`, y el balance es el reporte de ventas menos devoluciones que necesita ambos datos; moverlo exigiría una clase nueva y un cambio del requerimiento[cite: 18].
- **O-03**
  - Observación: La escritura y lectura en `AccessoryRepositoryFile`, `PromotionRepositoryFile` y `WarrantyRepositoryFile` elige el formato de línea por tipo concreto (`instanceof` al escribir, etiqueta al leer)[cite: 18].
  - Por qué no es violación: Las clases del modelo no pueden contener lógica de archivos (restricción del Taller 1), así que el formato de texto debe vivir en el repositorio; leer por etiqueta es una fábrica de objetos desde datos almacenados. Reemplazarlo exigiría un serializador nuevo por tipo. Se acepta como compromiso documentado[cite: 18].
- **O-04**
  - Observación: `ReturnRepositoryFile`, `AccessoryRepositoryFile`, `PromotionRepositoryFile` y `WarrantyRepositoryFile` lanzan `RuntimeException` en errores de E/S, mientras `ClientRepositoryFile` y `SellerRepositoryFile` lanzan `PersistenceException`[cite: 18].
  - Por qué no es violación: Es una inconsistencia de la capa de excepciones, no de asignación de responsabilidades; cambiar el tipo cambiaría el manejo visible para el usuario[cite: 18].
- **O-05**
  - Observación: `ReturnService.RETURN_WINDOW_DAYS` (línea 24) está declarada y nunca se usa; la regla de 30 días vive en `Sale.canBeReturned`[cite: 18].
  - Por qué no es violación: Es una constante muerta; ninguna responsabilidad está mal ubicada, y la regla la posee correctamente `Sale`[cite: 18].
- **O-06**
  - Observación: `ReturnService.resolveAndValidateProducts` (líneas 124–129) recorre `sale.getProducts()` para buscar un ítem y `viewReturnsByCustomer` (línea 167) encadena `r.getOriginalSale().getClient().getIdNumber()`[cite: 18].
  - Por qué no es violación: Un mejor reparto de responsabilidad (`Sale.findProduct`, `Return.belongsToCustomer`) exigiría agregar métodos a clases de otros integrantes (`Sale`, `Return`). Son accesos de lectura mediante getters públicos, sin lógica de negocio duplicada que cambie resultados; se deja como mejora futura y no se registra como violación del equipo[cite: 18].
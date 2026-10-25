# Justificación del diseño SOLID – Repositorios, servicios e inyección de dependencias

**Autor:** Desarrollador 2
**Alcance:** módulos de Personas, Accesorios, Promociones, Devoluciones y Garantías (repositorios, servicios y su conexión en `Main`).
**Documentos relacionados:** `docs/solid-audit/developer2-audit.md`, `docs/solid-audit/consolidated-audit.md`, `docs/solid-analysis.md`.

## 1. Propósito

La auditoría SOLID muestra que las capas de persistencia y de servicios ya siguen los principios en la mayoría de los puntos. Este documento deja registrado por qué el equipo **mantiene el diseño actual** de repositorios, servicios e inyección de dependencias, y por qué decidió no agregar más abstracciones donde solo aumentarían la complejidad.

El objetivo que guía la decisión es: **clases pequeñas con pocas responsabilidades, cuyos contratos viven en las interfaces que ya existen en el paquete `persistence`, sin multiplicar interfaces, capas ni indirecciones por sí mismas.**

## 2. Lo que el diseño actual ya logra

- **SRP:** cada repositorio solo lee y escribe su archivo; cada servicio solo aplica las reglas de negocio de su módulo; `Person`, `Client` y `Seller` solo modelan datos.
- **OCP:** se pueden agregar nuevas implementaciones de repositorio (base de datos, memoria) implementando la interfaz existente, sin modificar los servicios.
- **LSP:** `Client` y `Seller` sustituyen a `Person` sin alterar el comportamiento; `display()` es la única operación sobrescrita.
- **ISP:** cada interfaz de repositorio agrupa las operaciones de **una sola entidad** (`save`, `findByIdNumber`, `findAll`, `update`, `deleteByIdNumber`), un contrato CRUD cohesivo.
- **DIP:** `ClientService`, `SellerService`, `PromotionService` y `AccessoryService` dependen de **interfaces** de repositorio; las clases concretas `*RepositoryFile` solo aparecen en `Main`.

## 3. Repositorios

### 3.1 Decisión
Mantener una interfaz por entidad en `persistence` (`ClientRepository`, `SellerRepository`, `SaleRepository`, etc.) con su contrato CRUD completo, y una implementación basada en archivos para cada una.

### 3.2 Justificación
- **Cohesión.** Las cinco operaciones describen un único concepto: "guardar y recuperar esta entidad". Separarlas en interfaces de lectura y escritura (por ejemplo `ClientFinder` más `ClientRepository`) crearía dos archivos por entidad para una distinción que hoy nadie necesita.
- **Una implementación y pocos clientes.** Cada repositorio tiene una sola implementación y se inyecta en un servicio (más `SaleService` para clientes y vendedores). Interfaces adicionales tendrían un único implementador y un único consumidor: más nombres que aprender sin más flexibilidad.
- **Los contratos siguen donde el equipo ya los busca.** Dejar el contrato en la interfaz existente de `persistence` permite encontrar en un solo lugar *qué se puede hacer con una entidad*.
- **Los riesgos reales de acoplamiento ya están resueltos.** `ReturnRepositoryFile` y `WarrantyRepository.loadAll` reciben resolvedores `Function` en lugar de servicios o repositorios. Eso eliminó la dependencia circular `SaleService ↔ WarrantyService` y la dependencia de persistencia hacia servicios, que eran los problemas costosos.

## 4. Servicios

### 4.1 Decisión
Los servicios siguen siendo clases concretas, cada una responsable de las reglas de un módulo, y colaboran directamente entre sí (por ejemplo, `ReturnService` usa `SaleService`, `ProductService`, `AccessoryService` y `WarrantyService`).

### 4.2 Justificación
- **Pocas responsabilidades por clase.** Cada servicio tiene una única razón de cambio a nivel de módulo. `ReturnService` coordina una devolución; no persiste, no valida formatos ni calcula precios.
- **No existen ni se planean implementaciones alternativas.** Una interfaz como `SaleLookup` o `StockRestorer` tendría un solo implementador. La abstracción no sería usada por ninguna segunda implementación, por lo que sería especulativa.
- **Costo y beneficio.** Abstraer los cuatro servicios que usa `ReturnService` implica al menos tres interfaces nuevas, cuatro cláusulas `implements` y cambios en código de otros integrantes (`SaleService`, `ProductService`), a cambio de un beneficio (pruebas unitarias aisladas de `ReturnService`) que el proyecto hoy no aprovecha.
- **Flujo controlado entre capas.** Cada servicio es dueño de la conexión con su propio repositorio. Cuando `ReturnService` necesita una venta, un producto, un accesorio o una garantía, se lo pide al servicio de ese módulo y no lee el repositorio de otro módulo. Así, las reglas y validaciones (por ejemplo `ProductValidator`) se ejecutan en cada acceso y cada repositorio tiene un único punto de entrada. Reemplazar estas dependencias de servicios por abstracciones no mejoraría ese flujo; solo agregaría una indirección encima.
- **Preservación del comportamiento.** El requerimiento prohíbe cambiar el comportamiento observable. Dejar los servicios intactos tiene riesgo cero de regresión en las operaciones del menú.

## 5. Inyección de dependencias

### 5.1 Decisión
Mantener la **inyección por constructor** de forma manual, con `Main` como única raíz de composición.

### 5.2 Justificación
- Las dependencias son explícitas en cada constructor, inmutables (campos `final`) y están visibles en un solo lugar (`Main`), lo que facilita leerlas y auditarlas.
- No se necesita un framework, reflexión ni un localizador de servicios para una aplicación de consola de este tamaño; agregarlos sería más complejo que el problema.
- Las clases concretas se instancian **solo** en `Main`; ningún servicio ni repositorio crea sus colaboradores con `new`.
- Se respeta el sentido de las capas: `ui → service → persistence → model`.

## 6. Hallazgos de la auditoría aceptados como compromisos de diseño

**V-01 (ISP) – se mantiene.** Crear una interfaz con un único método (`findByIdNumber`) solo para satisfacer a un consumidor (`SaleService`) agrega un archivo por entidad sin reutilización real. La interfaz del repositorio ya es el contrato estándar y cohesivo. *Se reconsideraría si* un segundo consumidor necesita solo consultas, o si se requiere una implementación de solo lectura.

**V-02 (ISP) – se mantiene.** Mismo razonamiento: una interfaz de un solo método para un único consumidor (`WarrantyService` usando `findByCode`) es más estructura de la que el problema justifica. *Se reconsideraría si* `SaleRepository` crece más allá del CRUD, o si `WarrantyService` se prueba de forma aislada.

**V-03 (DIP) – se mantiene.** Cada servicio está conectado a su propio repositorio, así que `ReturnService` accede a los datos de otros módulos **a través de sus servicios**, sin saltárselos. Esto da un flujo controlado de la información entre capas (`ui → service → persistence → model`) y mantiene las validaciones y reglas de cada módulo en un solo lugar. *Se reconsideraría si* aparece una segunda implementación de alguno de esos servicios, o si se introducen pruebas unitarias con dobles de prueba.

Estos son **compromisos de diseño**, no defectos de comportamiento: ninguno produce un resultado incorrecto ni una falla visible para el usuario hoy.

## 7. Reglas que seguimos para mantener baja la complejidad

1. Agregar una interfaz solo cuando haya **dos implementaciones** o una **necesidad concreta de pruebas**; nunca "por si acaso".
2. Mantener las interfaces cohesivas alrededor de una entidad o una capacidad.
3. Preferir clases pequeñas con una sola razón de cambio antes que clases grandes divididas en muchas capas.
4. Usar resolvedores funcionales (`Function<String, T>`) para romper ciclos de dependencia en lugar de crear interfaces nuevas.
5. Construir los objetos únicamente en `Main`.

## 8. Conclusión

Los repositorios, los servicios y el esquema de inyección ya entregan los beneficios que busca SOLID: bajo acoplamiento entre capas, persistencia reemplazable y clases pequeñas con un propósito claro. Abstraer más agregaría archivos e indirección sin resolver un problema actual, por lo que el equipo mantiene el diseño vigente y documenta los tres compromisos aceptados con las condiciones explícitas bajo las cuales se reconsiderarían.

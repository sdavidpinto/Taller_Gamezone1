# Diseño del Sistema de Tienda

## Sobre las personas del sistema

**¿Qué atributos son comunes a todas las personas que interactúan con la tienda?**
Todas las personas comparten información básica como nombre, identificación y teléfono de contacto.

**¿Cuáles son propios de cada tipo específico de persona?**
- **Seller:** código de empleado y turno de trabajo asignado.
- **Customer:** correo electrónico y su historial de compras.

**¿Cómo se refleja esta distinción en una jerarquía de clases?**
`Customer` y `Seller` heredan atributos comunes de `Person` y añaden sus propios atributos específicos.

**¿Debería existir una clase que represente a una "persona genérica" sin especificar su rol?**
Sí, una clase abstracta.

**¿Por qué sí o por qué no?**
Para centralizar los atributos y permitir el polimorfismo.

**¿Qué implicación tiene esta decisión sobre la posibilidad de instanciar dicha clase?**
No se puede crear un objeto `Person` directamente, solo se pueden instanciar sus subclases (`Customer`, `Seller`). `Person` es solo un molde base.

---

## Sobre los productos del sistema

**¿Qué características tienen en común todos los productos que comercializa la tienda, independientemente de su tipo?**
Identificador, título, precio y cantidad disponible en inventario.

**¿Qué características son específicas de cada tipo de producto?**
- **VideoGame:** plataforma, género y clasificación de edad.
- **Console:** marca, modelo y generación.

**Cada tipo de producto debe poder presentar una descripción que integre sus características particulares. ¿Cómo debería declararse este comportamiento en la clase base para garantizar que todas las subclases lo implementen de manera propia?**
Como un método abstracto.

**¿Qué mecanismo de la programación orientada a objetos permite esto?**
Polimorfismo.

---

## Sobre las ventas y las relaciones entre entidades

Una venta involucra a un `Customer`, a un `Seller` y a uno o más `Product`.

**¿Qué tipo de relaciones existen entre la clase que representa la Sale y las demás clases del sistema?**
- `Sale` - `Customer`: asociación
- `Sale` - `Seller`: asociación
- `Sale` - `Product`: agregación

**¿Estas relaciones son de herencia, de asociación, de composición o de otro tipo? Justifique.**
`Sale` se relaciona con `Customer` y `Seller` mediante asociación, y con `Product` mediante agregación. No hay herencia ni composición en ninguno de los tres casos, porque en ningún caso las clases relacionadas dependen del ciclo de vida de `Sale` para existir.

**¿Debería la Sale ser responsable de calcular su propio total, o esta responsabilidad debería recaer en otra clase? Argumente su decisión.**
El cálculo del total es una operación simple con datos propios, le corresponde a `Sale`. Las validaciones y reglas sí deberían realizarse en `SaleService`.

---

## Sobre las restricciones del negocio

**¿Cómo se garantiza en el diseño que una Sale no pueda registrarse sin al menos un Product?**
Con validaciones en el constructor.

**¿En qué punto del sistema debería validarse esta regla?**
En la capa de servicios, más exactamente en `SaleService`, antes de que la `Sale` se construya de forma definitiva y se envíe a persistencia.

**¿Cómo se refleja en el diseño la actualización automática del inventario cuando se registra una Sale?**
Se refleja como un flujo de coordinación orquestado por la capa de servicios, no como una responsabilidad de `Sale` ni de `Product` por sí solos.

**¿Qué clases se ven involucradas en esta operación?**
`Sale`, `Product`, `SaleService`, `ProductService`, `ProductRepository`, `SaleRepository`.

---

## Sobre la organización en capas

El sistema debe organizarse en cuatro capas: modelo, persistencia, servicios e interfaz de usuario.

**¿Qué tipo de clases pertenecen a cada capa?**

| Capa | Clases |
|---|---|
| Modelo | `Person`, `Customer`, `Seller`, `Product`, `VideoGame`, `Console`, `Sale` |
| Persistencia | `ProductRepository`, `PersonRepository`, `SaleRepository` |
| Servicios | `ProductService`, `PersonService`, `SaleService` |
| Interfaz de usuario | `MainMenu` |

**¿Qué criterio permite decidir en qué capa debe ubicarse una clase?**
- Si la clase representa un concepto propio del negocio → modelo.
- Si su función es guardar o recuperar datos desde un archivo → persistencia.
- Si aplica reglas de negocio, validaciones y coordina operaciones entre otras clases → servicios.
- Si interactúa directamente con el usuario final → interfaz de usuario.

**¿Por qué la lógica de guardar y recuperar datos de archivos no debe estar dentro de las clases del dominio?**
Porque el dominio (`Product`, `Sale`, etc.) debe representar solo el negocio, no los detalles de cómo se almacena. Mezclarlo viola el principio de responsabilidad única.

**¿Qué problemas se generan cuando estas responsabilidades se mezclan?**
1. Cambiar el formato de archivo obligaría a modificar clases de dominio.
2. Las pruebas se vuelven dependientes de disco.
3. Se rompe la regla de que el modelo no depende de otras capas.
4. El código de almacenamiento queda disperso en vez de centralizado.

**¿Qué dependencias están permitidas entre las capas y cuáles están prohibidas?**

Permitidas:
- `MainMenu` → `Services`
- `Services` → `Model` y `Persistence`
- `Persistence` → `Model`

Prohibidas:
- `MainMenu` → `Persistence` (directo)
- `MainMenu` → `Model` (directo, saltándose servicios)
- `Model` → cualquier otra capa
- `Persistence` → `Services` o `MainMenu`

**Justifique el sentido de las dependencias permitidas.**
- `MainMenu` → `Services`: evita que la interfaz aplique reglas de negocio directamente.
- `Services` → `Model`: necesitan los objetos del dominio para operar sobre ellos.
- `Services` → `Persistence`: son los únicos que deciden cuándo guardar o leer datos.
- `Persistence` → `Model`: necesita saber qué datos guardar y reconstruir.

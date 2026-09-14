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

1. ¿Los accesorios deben integrarse a la jerarquía existente de productos
(extendiendo Product) o deben conformar una jerarquía independiente?
Justifique su decisión considerando la reutilización de código y la coherencia
del modelo.

Para simplificacion de el codigo la marca puede ser tomada como atributo para ser comparado con las consolas eso significa usar un atributo general de mas para ahorrar en la especializacion de las clases de tipo Accessory y facilitar el proceso de busqueda en capaz mas altas

2. ¿Qué atributos son comunes a los tres tipos de accesorios y cuáles son
específicos de cada tipo? ¿Cómo se refleja esta distinción en la jerarquía de
clases del módulo?

Tienen atributos similares a productos con la diferenciacion de usar la marca (atributo brand) para determinar la compatibilidad de las consolas registradas, 


3. La compatibilidad entre un accesorio y una consola es una relación entre dos
entidades del sistema. ¿Cómo se representa esta relación en el diseño y en la
persistencia? ¿La compatibilidad es un atributo del accesorio, de la consola, o
de ambos?

En el sistema compararemos los valores brand para determinar si la compatibilidad es correcta similar a la vida real como son accesorios como cargadores lenovo,iphone,huawei,entre otros.

4. ¿Qué modificaciones son necesarias en la clase de servicio de ventas
(SaleService) para que las ventas puedan incluir accesorios sin romper el
comportamiento existente con videojuegos y consolas?

agregarlas como clases idenpendientes como si fuera un modulo de una persona diferente,es decir agregar un nueva interfaz repository y su implementacion repositoryfile en sale service como explicare debemos cambiar algunos constructores para mantener la inyeccion de dependencias 

cambiar su constructor y agregar un parametro para enviar un Arraylist de tipo Accesory y validar los tipos de brand disponibles mediante foreach de las consolas pedidas o tirar una excepcion si no hay consolas;

5. ¿En qué capa de la arquitectura del sistema deben ubicarse las nuevas clases
del módulo de accesorios? Justifique su decisión con base en las
responsabilidades de cada capa.

Module: Clase abstracta Accesory y sus especializaciones cable,Controller,Memory, es logico que en esta capa esten los modelos y estructura de las clases de las cuales repository buscara para añadir y eliminar de los archivos

Persistence: En esta capa Se crean la clase AccesoryRepositorios Y AccessoryRepositoryFile Para mantener la persistencia en un nuevo archivo de tipo TXT depende de la clase Accesory Y clases hijas para encontrar una estructura Y poder leer Construir y reconstruir los archivos de manera eficiente

Service: En esta capa se crea AccesoryService Para el tratamiento Del CRUD Y de las validaciones y excepciones de La creación de cada 1 de los tipos de accesorios Anteriormente mencionados

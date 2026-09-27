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

1. Las tres promociones tienen reglas de cálculo distintas pero comparten atributos y comportamientos comunes. ¿Cómo se refleja esta situación en el diseño de la jerarquía de clases? ¿Qué mecanismo de la programación orientada a objetos permite que cada tipo de promoción calcule su descuento de forma diferente sin que el resto del sistema tenga que conocer los tipos concretos?

Al tener Atributos y comportamientos comunes el mecanismo de la programación orientada a objetos utilizados en estos casos Sería la herencia desde una clase general con sus atributos compartidos Y métodos abstractos que cada sub clase mencionada va a sobrescribir esto mediante el mecanismo del polimorfismo Y la creación de métodos abstractos en la clase abstracta Que es usada como molde de sus clases hijas


2. La clase base Promotion no puede implementar el método de cálculo de descuento porque cada tipo tiene una lógica diferente. ¿Cómo se declara este método en la clase base y qué garantiza esta declaración respecto a las subclases?

En este caso Promotion es creada como una clase abstracta,esto permite crear métodos abstractos Que cada clase hija o derivada de promoción debe contener debido a qué Los métodos abstractos en el polimorfismo y en la herencia deben obligatoriamente estar en todas las clases hijas de la clase abstracta el cual lo implementa Y este caso puede ser un gran ejemplo de ello 

3. La regla de negocio establece que solo se aplica la promoción con el mayor descuento. ¿En qué clase se ubica esta lógica de selección y por qué esta ubicación es coherente con el principio de arquitectura en capas? ¿Por qué esta lógica NO debe estar en la clase Sale ni en el menú de consola?

seria en promotion services esto debido a que ella es la que tomara los datos dados por PromotionrepositoryFile para buscar y comparar, es coherente por el modelo de capaz manejado en este proyecto porque sale es el molde menuUi lo que ve el usuario y services se encarga de las comparaciones y condiciones para que se manden los datos y no debe estar en las otras capas porque no es su funcion sencillamente y no puede depender de nadie la capa de modelo 

4. ¿Qué modificaciones son necesarias en la clase Sale y en el método genesrateReceipt para que el recibo muestre el descuento aplicado? ¿Estas modificaciones rompen algo del comportamiento existente en el 
sistema?

debería guardar el tipo de promoción aplicada y el descuento. Además de ello, al momento de agregar en el método generar el receipt, bueno, en mi caso se llama display y me repetiré a él de tal manera. Es el subtotal sumar el total más el descuento. El descuento en este caso tendría que ser negativo y se determina con un settotal que lo resta a el total original. Y con ello, dependiendo de lo de las otras clases de derivadas de promoción, hacer los cálculos y agregándola a la parte final del mensaje.

5. Las promociones vigentes se determinan comparando la fecha actual con las fechas de inicio y fin de cada promoción. ¿Dónde se realiza esta validación en la clase Promotion, en el PromotionService, o en ambas? Justifique.

Esta validación se divide entre ambas clases, cada una con una responsabilidad distinta. Promotion expone el método isActive(LocalDate date), que hace la comparación real contra startDate y endDate y devuelve un booleano; ahí vive la lógica de cálculo, porque solo depende de los datos propios de la promoción. También tiene una sobrecarga sin argumentos, isActive(), que simplemente llama a isActive(LocalDate.now()) para validar contra la fecha de hoy.


1. Los dos tipos de garantía tienen atributos comunes (fechas, producto asociado) pero también atributos y comportamientos diferentes (duración, cobertura, costo). ¿Cómo se refleja esta situación en el diseño de la jerarquía de clases? ¿Qué mecanismo de la programación orientada a objetos permite que cada tipo de garantía tenga su propia duración sin duplicar código?

al tener Atributos y comportamientos comunes el mecanismo de la programación orientada a objetos utilizados en estos casos Sería la herencia desde una clase general con sus atributos compartidos Y métodos abstractos que cada sub clase mencionada va a sobrescribir esto mediante el mecanismo del polimorfismo Y la creación de métodos abstractos en la clase abstracta Que es usada como molde de sus clases hijas


2. La regla de negocio establece que solo las consolas generan garantía básica automática, no los videojuegos. ¿En qué capa del sistema se ubica esta decisión y qué mecanismo de Java se usa para verificar el tipo real de un producto? Justifique.

seria en WarrantyService esto debido a que ella es la que tomara los datos dados por WarrantyRepositoryFile para buscar y comparar, es coherente por el modelo de capaz manejado en este proyecto porque sale es el molde menuUi lo que ve el usuario y services se encarga de las comparaciones y condiciones para que se manden los datos y no debe estar en las otras capas porque no es su funcion sencillamente y no puede depender de nadie la capa de modelo.

3. La duración de cada tipo de garantía es distinta (6 meses o 12 meses). ¿Cómo se calcula la fecha de vencimiento en cada subclase? ¿Debería este cálculo hacerse en el constructor de la garantía o en un método separado? Justifique.

Se calcula en el constructor de la clase abstracta Warranty, no en un método separado ni en cada subclase y al momento de mandar la fecha solo se suma a la fecha de inicio con la funcion getDurationinMonths

4. La garantía extendida agrega un costo del 10% del precio del producto al total de la venta. ¿En qué punto del flujo de registro de venta se calcula y aplica este costo adicional? ¿Qué modificaciones son necesarias en el método SaleService.registerSale?

Se calcula y aplica en SaleService.registerSale, en un punto muy específico: después de crear la venta y aplicar la promoción,como añadidos esta un List<String>extendedWarrantyProductIds como nuevo parámetro de registerSale, con los identificadores de los productos para los que el cliente pidió garantía extendida, recorrer esa lista, ubicar cada Product correspondiente dentro de products , invocar warrantyService.assignExtendedWarranty(producto, sale, LocalDate.now()) por cada uno, y acumular el costo adicional de cada garantía creada.

5. La consulta de "garantías próximas a vencer" requiere iterar sobre todas las garantías y filtrar aquellas cuya fecha de fin esté dentro de los próximos 30 días. ¿En qué clase se ubica este método y qué dependencias necesita? ¿Por qué esta ubicación es coherente con la arquitectura en capas?

WarrantyRepository , que ya recibe por constructor. Con eso le alcanza para pedir warrantyRepository.loadAll() y quedarse con la lista completa de garantías. Todo lo demás lo resuelve con LocalDate.now() y comparaciones de fechas, sin hablar con ninguna otra clase.


1. La devolución es una nueva entidad del sistema que hace referencia a una venta existente. ¿Qué tipo de relación existe entre la clase Return y la clase Sale? ¿Esta relación es de herencia, asociación, agregación o composición? Justifique.

Esta relación entre la clase Sale y la clase Return es meramente asociativa debido a que en gran parte lo único que hace Return es apuntar a Sale no es una relación de herencia debido a que no es una venta Y mucho menos la venta se borra si el Return es eliminado por ende es mucho menos es composicion.

2. Una devolución puede contener solamente algunos productos de la venta original, no necesariamente todos. ¿Cómo se representa esta situación en los atributos de la clase Return? ¿Qué se almacena en el atributo de productos devueltos?

Return no vuelve a guardar toda la venta, guarda una referencia a la Sale original (originalSale) más una lista aparte, returnedProducts, con solo los productos que el cliente realmente está devolviendo. Entonces en ese atributo no se guarda todo lo que había en la venta sino nada más el subconjunto elegido, y no hace falta marcar nada dentro de Sale para que la devolución parcial funcione.

3. La regla de negocio establece que solo se pueden registrar devoluciones dentro de los 30 días posteriores a la venta. ¿En qué capa del sistema se ubica esta validación y por qué? ¿Qué mecanismo de Java se usa para calcular la diferencia entre dos fechas?

esto queda dividido entre Sale y ReturnService, esto debido a que Sale es la que tiene el atributo date y puede calcular por sí sola si ya pasó el plazo con su propio método canBeReturned, es coherente por el modelo de capaz manejado en este proyecto porque sale es el molde menuUi lo que ve el usuario y services se encarga de las comparaciones y condiciones para que se manden los datos, entonces la decisión de negocio de rechazar la devolución si ya pasó el plazo no la toma Sale sino ReturnService, que llama canBeReturned() antes de registrar y ahí sí lanza la excepción. El mecanismo que se usa para la diferencia de fechas es ChronoUnit.DAYS.between(), de java.time.temporal.ChronoUnit.

4. La devolución de productos incrementa el stock. ¿Qué método existente en el sistema del Taller 1 se reutiliza para esta operación, y en qué clase se invoca desde el módulo de devoluciones? ¿Por qué es importante reutilizar métodos existentes en lugar de duplicar la lógica de actualización de stock?

se reutiliza restoreStock, que ya estaba en ProductService desde el Taller 1, se invoca desde ReturnService cada vez que se registra una devolución, una vez por cada producto devuelto. es importante reutilizarlo y no volver a escribir la lógica de sumar stock dentro de ReturnService porque si no quedaría la misma responsabilidad duplicada en dos partes del sistema, y si un día cambia cómo se actualiza o se guarda el stock tocaría modificar dos lugares en vez de uno solo.

5. El reporte de balance mensual requiere consolidar información de dos módulos distintos (ventas y devoluciones). ¿En qué clase de servicio se ubica este reporte y por qué esta ubicación es coherente con la arquitectura en capas? ¿Qué dependencias necesita esta clase para poder generarlo?

sería en ReturnService esto debido a que ella es la que tomará los datos dados por SaleService y por su propio ReturnRepository para buscar y comparar, es coherente por el modelo de capaz manejado en este proyecto porque sale es el molde menuUi lo que ve el usuario y services se encarga de las comparaciones y condiciones para que se manden los datos y no debe estar en las otras capas porque no es su función sencillamente y no puede depender de nadie la capa de modelo. Las dependencias que necesita son SaleService, para recorrer todas las ventas del mes con findAll(), y su propio returnRepository, para recorrer las devoluciones del mes de la misma forma.

A1 - Descuento por categoría para accesorios

El problema es que CategoryDiscount solo tenía habilitadas las categorías VIDEOGAME y CONSOLE, entonces con el módulo de accesorios ya integrado la tienda no tenía forma de lanzar una promoción sobre accesorios. La solución quedó en la misma clase CategoryDiscount, agregando ACCESSORY como tercera categoría válida, porque esa clase ya era la dueña de la lista de categorías permitidas y de la lógica que decide si un producto pertenece o no a la categoría objetivo, entonces no tenía sentido mover esa decisión a otra capa. Como Controller, Cable y Memory heredan de Accessory, con un solo chequeo de tipo contra Accessory alcanza para cubrir los tres tipos de accesorio sin repetir lógica. En PromotionService solo se agregó la validación de que la categoría recibida sea una de las tres permitidas, y en MenuUI se ajustó el texto que le muestra al usuario las opciones disponibles al registrar una promoción por categoría.

A2 - Dependencia circular en el módulo de garantías

El problema es el ciclo SaleService, WarrantyService, WarrantyRepository, SaleService, que se genera porque el repositorio de garantías necesitaba resolver la venta asociada a cada garantía durante la carga, y eso impedía construir los objetos en Main mediante inyección por constructor, porque ninguno de los dos puede armarse primero si dependen entre sí. La solución fue sacarle esa responsabilidad al repositorio: WarrantyRepository deja de guardar y de necesitar objetos Sale y Product completos, y solo persiste los identificadores de cada uno en el archivo. Quien resuelve las referencias reales pasó a ser WarrantyService, al momento de leer las garantías, recibiendo por constructor WarrantyRepository, SaleRepository y ProductService y usando esas dos últimas solo para buscar por identificador cuando hace falta. Con eso el repositorio deja de depender de SaleService, y en Main el orden de construcción quedó primero warrantyRepository, luego warrantyService, y recién después saleService, sin que se vuelva a formar el ciclo.

A3 - Flujo unificado del registro de ventas

El problema es que los Requerimientos 1, 2 y 4 modificaban SaleService.registerSale cada uno por su lado, y al integrarlos el orden en que se ejecutan las operaciones cambia el resultado, por ejemplo si el descuento se calcula antes o después de sumar el costo de las garantías extendidas. La solución fue reorganizar registerSale para que siempre siga el mismo orden: primero valida que la venta tenga al menos un ítem y que haya stock, después resuelve cada ítem como producto o accesorio, luego crea la venta y con eso ya tiene el subtotal, después le asigna la garantía básica automática a cada consola, luego consulta la mejor promoción vigente y el descuento se calcula únicamente sobre el subtotal de los ítems sin tocar el costo de garantías, después revisa qué consolas pidieron garantía extendida y suma ese costo aparte, y con eso arma el total final como subtotal menos descuento más costo de garantías extendidas, y solo hasta el final, con la venta ya armada sin errores, actualiza el inventario delegando en ProductService o en AccessoryService según el tipo del ítem. Esto evita que dos módulos distintos compitan por decidir en qué momento se toca el total de la venta.

A4 - Devolución de accesorios

El problema es que ReturnService restauraba el stock invocando únicamente ProductService.restoreStock, y ese método no sabe nada del inventario de accesorios, entonces al devolver un accesorio su stock se quedaba igual. La solución fue que ReturnService reciba también AccessoryService por constructor, y en el momento de restaurar stock revise de qué tipo es cada ítem devuelto: si es un Accessory delega en AccessoryService, si es cualquier otro Product delega en ProductService como ya se hacía. Para que esto funcione hubo que agregar en AccessoryService un método restoreStock equivalente al que ya existía en ProductService desde el Taller 1, y en ReturnRepositoryFile ajustar la carga para que también sepa resolver referencias a accesorios y no solo a productos.

A5 - Reembolso de ventas con descuento

El problema es que Return.calculateRefundAmount sumaba el precio de lista de cada producto devuelto, entonces si la venta original tuvo una promoción aplicada, el cliente terminaba recibiendo más plata de la que realmente pagó. La solución fue que ese cálculo ya no use el precio de lista tal cual, sino que primero calcule qué proporción del subtotal de la venta original fue descontada, y le reste esa misma proporción al precio de cada producto devuelto, entonces el reembolso queda proporcional al descuento real que tuvo la venta. Ese mismo ajuste se reflejó en generateReturnReceipt, que ahora muestra el precio de lista, el descuento proporcional y el monto realmente reembolsado de cada ítem, en vez de mostrar solo el precio original.

A6 - Reporte de balance mensual

El problema es que generateMonthlyBalance solo devolvía el balance neto, y el negocio necesita ver por separado cuánto se vendió y cuánto se devolvió en el mes, además de que con promociones y garantías ya integradas el total de ventas debía calcularse con el total final de cada venta y no con el precio de lista. La solución fue agregar dos métodos nuevos en ReturnService, uno que recorre todas las ventas del mes con SaleService.findAll y suma el total final de cada una, y otro que recorre las devoluciones del mismo mes con el propio ReturnRepository y suma lo reembolsado en cada una, y generateMonthlyBalance quedó igual por fuera, solo que ahora internamente calcula la diferencia entre esos dos métodos en lugar de acumular todo en una sola pasada. En MenuUI se ajustó la opción de balance mensual para mostrar los tres valores por separado en vez de mostrar solo el neto.

A7 - Anulación de garantías al devolver una consola

El problema es que ningún requerimiento anterior definía qué pasaba con la garantía de una consola una vez que esa consola se devolvía, y en el sistema integrado no tiene sentido que una consola devuelta siga teniendo una garantía vigente. La solución fue agregar en WarrantyService un método que recibe el identificador del producto y el código de la venta, recorre todas las garantías, separa las que coinciden con ese producto y esa venta de las que no, guarda solo la lista de las que quedan y devuelve como resultado el costo reembolsable de las garantías que se eliminaron, que da cero si era básica y el costo adicional si era extendida, gracias a que ese valor ya lo sabe calcular cada subclase de Warranty por su cuenta. Del lado de ReturnService, dentro de registerReturn, después de restaurar el stock se recorren los ítems devueltos y por cada uno que sea una Console se invoca ese método nuevo de WarrantyService y el valor que devuelve se le suma al Return mediante un método nuevo que agrega ese monto tanto al reembolso total como a un campo aparte para poder mostrarlo distinto en el recibo. Para que ReturnService pueda hacer esa llamada hubo que agregarle WarrantyService como una dependencia más en su constructor, y por lo tanto también hubo que actualizar la forma en que se arma ReturnService en Main para pasarle ese nuevo parámetro.
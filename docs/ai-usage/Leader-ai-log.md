# Bitácora de Uso de Inteligencia Artificial

**Integrante:** Samuel David Pinto Ortiz
**Código estudiantil:** 1066284305
**Rol:** Líder Técnico
**Módulo asignado:** Ventas

---

## Instrucciones de uso de esta bitácora

Uso de la ia:Consultar la estructura de la capa repositorio para la creacion de archivos,Estructurar la interfaz de usuario con diseños o funcionalidades Repetidas(validaciones repetidas,mensajes de error entre otros) y buscar explicaciones sobre el termino de inyeccion de dependencias

Resumen de la respuesta obtenida: La IA explicó que una clase de persistencia típicamente expone métodos como save(List<Sale> sales) y load() que devuelve List<Sale>, y que internamente se encarga de escribir/leer el archivo (texto plano, CSV o serialización de Java), separando esa lógica de las clases del dominio (model) para no violar la arquitectura en capas. También mencionó que el formato de archivo es libre siempre que los datos se conserven entre ejecuciones.

Decisión tomada: Definí mi propia clase SaleRepository con los métodos save() y load() siguiendo esa estructura general, pero escribí yo mismo la lógica de lectura/escritura y el formato de archivo (CSV) adaptado a los atributos reales de mi clase Sale, en lugar de copiar una implementación completa.

Actualizacion final:

Luego de la implementación de los 8 requerimientos, esta es la actualización del uso de IA por parte de mi persona:

Posterior al uso de la IA para determinar el uso de inyección de dependencias, decidí usar esta misma herramienta para buscar errores, debido a que el programa no funcionaba de la manera correcta. Por ejemplo, en ReturnService, en la implementación a SaleService, el cálculo de la devolución muchas veces se sobrepasaba respecto al valor original, y con ello apareció el problema de manera realista: no se puede cobrar mucho más de lo que originalmente el cliente pagó por la devolución de un objeto.

En su gran mayoría hice refactorizaciones de código y utilicé asistencia en ciertos casos en los cuales desconocía cómo implementar. Por ejemplo, el sistema está originalmente pensado para poder pedir un producto de cada tipo en el servicio, es decir, un único código, pero con eso refactoricé para la parte de SOLID y GRASP.

Por último, usé la IA para los .md de Mermaid, porque no sé usar la herramienta.

## Reflexión final

La ia utilizada por mi parte fue para entender en profundidad la conexion entre capas y la inyeccion de dependencias algunos principios y para no estar escribiendo codigo cuando ya sabia como distribuirlo.



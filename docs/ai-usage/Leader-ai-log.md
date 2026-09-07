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

## Reflexión final

La ia utilizada por mi parte fue para entender en profundidad la conexion entre capas y la inyeccion de dependencias.



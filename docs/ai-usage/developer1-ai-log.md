# Bitacora de Uso de Inteligencia Artificial

**Integrante:** Edward Carrillo Martinez
**Codigo estudiantil:** 1067596194
**Rol:** Desarrollador 1
**Modulos asignados:** Productos, Accesorios, Promociones, Devoluciones, Garantias, Validacion y Excepciones

---

## Instrucciones de uso de esta bitacora

**Uso de la IA:** 
Durante la fase inicial del proyecto, utilice la IA para obtener un paso a paso metodologico sobre el orden recomendado para construir mis clases asignadas. Tambien investigue como implementar correctamente la herencia y el polimorfismo en mi jerarquia de entidades, y pedi asistencia en casos especificos donde desconocia la sintaxis correcta en Java (como el manejo de fechas y la creacion de excepciones). 
Posteriormente, en la fase de refactorizacion (GRASP y SOLID), dialogue con la IA sobre como manejar un problema de diseño complejo que detecte en mis clases de promociones, buscando alternativas para resolverlo sin romper la directriz acordada con mi Lider Tecnico de no causar errores en cascada.

**Resumen de la respuesta obtenida:** 
la IA detallo una secuencia de trabajo iniciando por las clases del modelo, seguidas de la capa de persistencia y finalmente la capa de servicio. En cuanto a mis bloqueos con la sintaxis, me brindo fragmentos de codigo genericos sobre como la libreria `LocalDate` maneja el tiempo, y me explico como aislar la logica de archivos de las reglas de negocio.
En la fase final, tras pronunciar mi preocupacion sobre el riesgo de modificar los condicionales en mi clase de promociones, la IA me explico que forzar un cambio profundo en este momento era peligroso. Me sugirio la alternativa de documentar el hallazgo como una observacion tecnica.

**Decision tomada:** 
Para la construccion del codigo, segui el orden arquitectonico sugerido para estructurar las capas de forma organizada en mi entorno local. Diseñe yo mismo la logica de mis clases y arme mi jerarquia de excepciones, adaptando los pequeños fragmentos sugeridos a mis propios atributos. 
Para la fase de auditoria, decidi priorizar la estabilidad del repositorio del equipo. Limpie yo mismo los errores menores de cohesion, pero decidi dejar el codigo complejo de promociones intacto para evitar conflictos de fusion, adoptando la alternativa de justificarlo formalmente en mi informe.

**Actualizacion final:**

Luego de la implementacion de todos los requerimientos, esta es la actualizacion final de mi uso de IA:

Utilice asistencia en ciertos casos en los cuales desconocia como implementar la logica. Por ejemplo, para las devoluciones y garantias, la IA me asistio enseñandome a usar las librerias de fechas de Java. 

Ademas de esto, el uso de la herramienta evoluciono para apoyarme en el formato profesional de mis conclusiones en Markdown y me apoyo proporcionando la guia de comandos necesarios para gestionar mi repositorio (crear ramas, commits, push) de forma segura.

## Reflexion final

La IA funciono como un excelente tutor para dominar los fundamentos de la Programacion Orientada a Objetos en las primeras etapas, resolver dudas puntuales sobre comandos de Git, y como una caja de resonancia para mis preocupaciones estructurales al final. Me ayudo a encontrar la manera correcta de reportar problemas de diseño sin arriesgar el trabajo del equipo. El diagnostico de errores y la escritura logica dependieron enteramente de mi criterio como responsable de mis modulos.
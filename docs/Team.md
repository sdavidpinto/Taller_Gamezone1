# Diseño del Sistema de Tienda

## Información del equipo

| Integrante | Identificación | Rol | Módulo asignado |
|---|---|---|---|
| Samuel David Pinto Ortiz | 1066284305 | Líder Técnico | Módulo de ventas |
| Jose Gabriel Quintero Ortiz | 1214470820 | Desarrollador 2 | Módulo de personas |
| Edward Carrillo Martinez | 1067596194 | Desarrollador 1 | Módulo de productos |

## Distribución de clases

| Integrante | Clases asignadas |
|---|---|
| Samuel David Pinto Ortiz | `Sale`, `SaleRepository`, `SaleService`, `MainConsoleMenu` |
| Jose Gabriel Quintero Ortiz | `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService` |
| Edward Carrillo Martinez | `Product`, `Console`, `VideoGame`, `ProductRepository`, `ProductService` |

## Estrategia de ramas (Git)

| Integrante | Ramas a utilizar |
|---|---|
| Samuel David Pinto Ortiz | `feature/project-setup`, `feature/docs`, `feature/sale-module`, `feature/ui-module` |
| Jose Gabriel Quintero Ortiz | `feature/person-module` |
| Edward Carrillo Martinez | `feature/product-module` |

---

## Actividades por desarrollador

### Líder Técnico — Samuel David Pinto Ortiz

**Configuración del proyecto**
1. Crear el repositorio del proyecto en GitHub con la configuración inicial (README, `.gitignore`, licencia).
2. Configurar las ramas del proyecto (`main` y `develop`) y activar la protección de las ramas principales.
3. Configurar el proyecto Maven con el `pom.xml` inicial y la estructura de paquetes de las cuatro capas.
4. Elaborar el archivo `TEAM.md` con la información del equipo, los roles asignados y la distribución de clases.

**Módulo de ventas**
5. Implementar la clase del dominio de ventas con sus atributos, constructor y métodos básicos.
6. Implementar el método de cálculo del total de la venta.
7. Implementar la clase de persistencia del módulo de ventas.
8. Implementar la clase de servicio del módulo de ventas con las reglas de validación (mínimo un producto, verificación de stock, actualización de inventario).

**Interfaz de usuario**
9. Implementar la estructura básica de la clase de interfaz de usuario (menú principal).
10. Implementar los submenús de la interfaz de usuario para cada uno de los tres módulos.

**Integración final**
11. Implementar la clase principal de la aplicación con la carga inicial de datos y la inyección de dependencias.
12. Revisar e integrar los Pull Requests de los desarrolladores en la rama de integración.
13. Elaborar el `README.md` final del proyecto con las instrucciones de compilación y ejecución.

### Desarrollador 1 — Edward Carrillo Martinez

**Módulo de productos**
1. Crear la rama feature correspondiente al módulo de productos.
2. Implementar la clase base abstracta de la jerarquía de productos con sus atributos comunes, constructor y métodos comunes.
3. Declarar el método abstracto de descripción que las clases derivadas deberán implementar.
4. Implementar la primera clase derivada (videojuegos) con sus atributos particulares y la implementación del método de descripción.
5. Implementar la segunda clase derivada (consolas) con sus atributos particulares y la implementación del método de descripción.
6. Implementar la clase de persistencia del módulo de productos con los métodos de guardado y carga desde archivos.
7. Implementar la clase de servicio del módulo de productos con los métodos de registro, listado y actualización de stock.

**Cierre**
8. Documentar todas las clases del módulo con JavaDoc en inglés.
9. Solicitar Pull Requests al Líder Técnico para la integración del módulo.

### Desarrollador 2 — Jose Gabriel Quintero Ortiz

**Módulo de personas**
1. Crear la rama feature correspondiente al módulo de personas.
2. Implementar la clase base abstracta de la jerarquía de personas con sus atributos comunes, constructor y métodos comunes.
3. Declarar el método abstracto o de negocio que las clases derivadas deberán implementar según el análisis realizado.
4. Implementar la primera clase derivada (clientes) con sus atributos particulares.
5. Implementar la segunda clase derivada (vendedores) con sus atributos particulares.
6. Implementar la clase de persistencia del módulo de personas con los métodos de guardado y carga desde archivos.
7. Implementar la clase de servicio del módulo de personas con los métodos de registro y listado.

**Cierre**
8. Documentar todas las clases del módulo con JavaDoc en inglés.
9. Solicitar Pull Requests al Líder Técnico para la integración del módulo.

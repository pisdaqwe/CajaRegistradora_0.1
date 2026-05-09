# CajaRegistradora_0.1

**TPV Cafetería** es una aplicación de escritorio desarrollada en **Java Swing** con base de datos **MariaDB**.
El proyecto simula un sistema TPV real para una cafetería, permitiendo gestionar ventas, caja, empleados, fichajes, stock, devoluciones, mermas, informes, auditoría y configuración del sistema.

Este repositorio forma parte de un proyecto final de ciclo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.

---

## Índice

1. [Descripción general](#descripción-general)
2. [Tecnologías utilizadas](#tecnologías-utilizadas)
3. [Módulos principales](#módulos-principales)
4. [Requisitos de instalación](#requisitos-de-instalación)
5. [Instalación de la base de datos](#instalación-de-la-base-de-datos)
6. [Configuración de la aplicación](#configuración-de-la-aplicación)
7. [Ejecución de la aplicación](#ejecución-de-la-aplicación)
8. [Estructura recomendada de entrega](#estructura-recomendada-de-entrega)
9. [Usuarios de prueba](#usuarios-de-prueba)
10. [Errores comunes](#errores-comunes)
11. [Notas sobre seguridad](#notas-sobre-seguridad)
12. [Estado del proyecto](#estado-del-proyecto)

---

## Descripción general

La aplicación permite centralizar la operativa diaria de una cafetería desde un único sistema. No se limita únicamente a registrar ventas, sino que incorpora módulos relacionados entre sí para cubrir un flujo completo de trabajo:

- Inicio de sesión por usuario y PIN.
- Fichaje de empleados.
- Apertura y cierre de sesiones de caja.
- Registro de ventas desde una interfaz tipo TPV.
- Personalización de productos con tamaños, extras y modificaciones.
- Cobro de ventas y generación de tickets.
- Consulta y reimpresión de tickets.
- Gestión de stock, disponibilidad y mermas.
- Registro de devoluciones vinculadas a ventas originales.
- Informes con filtros, tablas, gráficos y exportación a PDF.
- Auditoría de acciones relevantes.
- Configuración visual, idioma y herramientas técnicas.

La aplicación está organizada mediante una arquitectura por capas:

```text
Interfaz gráfica (Swing)
        ↓
Facade
        ↓
Service
        ↓
DAO
        ↓
Base de datos MariaDB
```

Esta separación permite mantener el código más ordenado, facilitar la ampliación del sistema y evitar que la interfaz gráfica dependa directamente de consultas SQL.

---

## Tecnologías utilizadas

| Tecnología / herramienta | Uso principal |
|---|---|
| **Java 17** | Lenguaje principal del proyecto |
| **Java Swing** | Interfaz gráfica de escritorio |
| **MariaDB** | Base de datos relacional |
| **JDBC** | Conexión entre Java y MariaDB |
| **HikariCP** | Pool de conexiones a base de datos |
| **Maven** | Gestión de dependencias y compilación |
| **Jackson** | Serialización y lectura de datos JSON |
| **Apache PDFBox** | Generación de documentos PDF |
| **JFreeChart** | Gráficos para informes |
| **FlatLaf** | Mejora visual del Look & Feel de Swing |
| **Logback + SLF4J** | Registro de logs y errores |
| **Git / GitHub** | Control de versiones |
| **Launch4j** | Generación de ejecutable `.exe` para Windows |
| **Eclipse** | Entorno principal de desarrollo |

---

## Módulos principales

### 1. Gestión de usuarios y roles

- Inicio de sesión mediante **usuario y PIN**.
- Control de roles: **Cajero**, **Encargado**, **Administrador** y **Técnico**.
- Control de usuarios activos e inactivos.
- Redirección posterior al login según rol y estado operativo.
- Acceso restringido a módulos según permisos.

### 2. Fichajes y control horario

- Registro de **entrada y salida** de empleados.
- Control de fichaje abierto o cerrado.
- Asociación del fichaje con usuario y sucursal.
- Consulta de fichajes desde perfiles autorizados.
- Bloqueo de determinadas acciones si el usuario no tiene un estado válido.

### 3. Gestión de caja

- Consulta del estado de las cajas.
- Apertura de sesión de caja asociada a **caja**, **usuario**, **sucursal** e **importe inicial**.
- Validación para evitar sesiones incompatibles.
- Cierre de caja con conteo de efectivo.
- Cálculo de importe esperado, importe contado y desfase.
- Histórico de sesiones de caja.
- Ticket o resumen de cierre.

### 4. Módulo de ventas

La pantalla principal de ventas funciona como un TPV táctil:

- Categorías y subcategorías de productos.
- Grid visual de productos.
- Ticket dinámico.
- Panel de personalización.
- Barra inferior de acciones.

Permite:

- Añadir productos al ticket.
- Seleccionar tamaños.
- Añadir extras.
- Aplicar personalizaciones.
- Añadir notas tipo “Ask Me”.
- Eliminar líneas del ticket.
- Duplicar productos.
- Buscar productos por nombre o SKU.
- Consultar tickets del día.
- Acceder a opciones adicionales según rol.

Validaciones principales:

- No se permite cobrar un ticket vacío.
- No se permite vender productos no disponibles.
- Se valida la sesión de caja antes de registrar ventas.
- El stock se descuenta de forma controlada cuando corresponde.

### 5. Cobro y registro de venta

- Cobro mediante métodos disponibles, como **efectivo** y **tarjeta**.
- Cálculo del total del ticket.
- Registro de venta en base de datos.
- Registro de líneas de venta, extras, pago y ticket JSON.
- Descuento de stock dentro de la operación de venta.
- Confirmación visual al finalizar la operación.

> En la versión actual del proyecto no se contempla el pago mixto como flujo principal. Cada venta se registra con un método de pago.

### 6. Tickets y reimpresión

- Generación de ticket asociado a cada venta.
- Guardado de ticket en formato **JSON** en la base de datos.
- Consulta de tickets del día.
- Reimpresión o visualización posterior del ticket.
- Recuperación de datos desde el JSON guardado.
- Soporte para tickets de venta y tickets de devolución.

### 7. Control de stock, disponibilidad y recetas

- Control de disponibilidad de productos por sucursal.
- Control de disponibilidad de extras.
- Soporte para productos con disponibilidad desactivada, disponibilidad sin control de cantidad o disponibilidad con stock controlado.
- Descuento de stock al vender.
- Reposición de stock en devoluciones cuando corresponde.
- Registro de mermas.
- Control de ingredientes y recetas para preparaciones.
- Registro de movimientos de stock.

### 8. Devoluciones

- Búsqueda de ventas registradas.
- Devoluciones parciales o completas.
- Selección de líneas a devolver.
- Cálculo del importe a reembolsar.
- Asociación de la devolución con la venta original.
- Generación de ticket de devolución.
- Opción de reponer stock cuando corresponde.
- Consulta posterior de tickets de devolución.

### 9. Mermas

- Registro de productos o preparaciones descartadas.
- Selección de producto desde un flujo similar al de ventas.
- Registro de motivo y observaciones.
- Posibilidad de descontar stock según receta.
- Asociación de la merma a usuario y sucursal.
- Registro de movimientos de stock relacionados.

### 10. Informes y estadísticas

El módulo de informes permite consultar información del negocio mediante filtros:

- Rango de fechas.
- Caja.
- Empleado.
- Método de pago.
- Tipo de informe.
- Inclusión de devoluciones según el informe.

Informes incluidos:

- Resumen ejecutivo.
- Ventas por día.
- Ventas por franja horaria.
- Ticket medio.
- Pagos por método.
- Productos más vendidos.
- Extras más vendidos.
- Combos vendidos.
- Descuentos aplicados.
- Devoluciones.
- Ranking de empleados.
- Ventas por caja.
- Ventas por sesión de caja.
- Mermas.
- Movimientos de stock.

Además, permite visualización en tabla, KPIs superiores, gráficos y exportación a PDF.

### 11. Auditoría

- Registro de acciones relevantes.
- Asociación con usuario, fecha y sucursal.
- Almacenamiento de detalles en formato JSON.
- Consulta desde la interfaz de auditoría.
- Útil para revisar cambios importantes o incidencias.

### 12. Configuración y herramientas del sistema

- Configuración de idioma.
- Configuración de apariencia visual.
- Herramientas técnicas.
- Comprobación de conexión con base de datos.
- Configuración de caja terminal.
- Información básica del sistema.

---

## Requisitos de instalación

Para ejecutar el proyecto en un equipo local se necesita:

- **Java 17 o superior**.
- **MariaDB 10 o superior**.
- **Git** si se desea clonar el repositorio.
- **Eclipse** o **IntelliJ IDEA** si se desea abrir el código fuente.
- **Maven** si se desea compilar desde consola.
- Sistema operativo Windows recomendado para la versión entregada con `.exe`.

---

## Instalación de la base de datos

### 1. Instalar MariaDB

Descargar e instalar MariaDB en el equipo.

Durante la instalación se debe recordar la contraseña asignada al usuario `root`, ya que será necesaria para importar la base de datos.

### 2. Crear la base de datos

Abrir una consola de MariaDB o CMD y ejecutar:

```sql
CREATE DATABASE tpv_cafeteria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Importar el script SQL

Desde la carpeta donde esté el archivo SQL de la entrega, ejecutar:

```bash
mariadb -u root -p tpv_cafeteria < tpv_cafeteria_demo.sql
```

También puede usarse:

```bash
mysql -u root -p tpv_cafeteria < tpv_cafeteria_demo.sql
```

Después de ejecutar el comando, MariaDB pedirá la contraseña del usuario `root`.

### 4. Verificar la importación

Entrar en MariaDB:

```bash
mariadb -u root -p
```

Seleccionar la base de datos:

```sql
USE tpv_cafeteria;
SHOW TABLES;
```

Si aparecen las tablas del proyecto, la importación se ha realizado correctamente.

---

## Configuración de la aplicación

La aplicación utiliza un archivo `config.properties` para cargar la configuración principal.

Ejemplo orientativo:

```properties
# Configuración general
app.name=TPV Cafeteria
app.version=0.1.0
app.language=es
app.timezone=Europe/Madrid

# Base de datos
db.url=jdbc:mariadb://localhost:3306/tpv_cafeteria
db.user=root
db.password=TU_PASSWORD
db.driver=org.mariadb.jdbc.Driver

# Pool de conexiones
db.pool.maxSize=10
db.pool.minIdle=2
db.pool.connectionTimeout=30000

# Apariencia
app.laf=cafe

# Caja configurada para este terminal
terminal.id_caja=1
```

> El valor de `db.password` debe adaptarse a la contraseña configurada en MariaDB.
> No se recomienda subir contraseñas reales al repositorio.

---

## Ejecución de la aplicación

### Opción 1: ejecutar desde Eclipse

1. Clonar o descargar el repositorio.
2. Abrir Eclipse.
3. Importar el proyecto como proyecto Maven existente.
4. Revisar el archivo `config.properties`.
5. Comprobar que MariaDB está iniciado.
6. Ejecutar la clase principal de la aplicación.

### Opción 2: ejecutar el `.jar`

Desde consola:

```bash
java -jar TPV_Cafeteria.jar
```

O, si el jar generado tiene el nombre de Maven:

```bash
java -jar CajaRegistradora-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### Opción 3: ejecutar el `.exe`

En la carpeta de entrega:

```text
01_Ejecutable/TPV_Cafeteria.exe
```

Antes de ejecutar el `.exe`, comprobar que:

- La base de datos está importada.
- MariaDB está iniciado.
- El archivo `config.properties` tiene los datos correctos.
- Java está disponible o se incluye un runtime en la carpeta de entrega.

---

## Compilación del proyecto

Para compilar desde consola con Maven:

```bash
mvn clean package
```

El archivo generado aparecerá normalmente en la carpeta:

```text
target/
```

---

## Estructura recomendada de entrega

```text
TPV_Cafeteria_Entrega_Final_Guiada
│
├── 01_Ejecutable
│   ├── TPV_Cafeteria.exe
│   ├── TPV_Cafeteria.jar
│   ├── runtime
│   └── config.properties
│
├── 02_BaseDatos
│   └── tpv_cafeteria_demo.sql
│
├── 03_Manuales
│   ├── Manual_instalacion.pdf
│   ├── Manual_usuario.pdf
│   └── Guia_demo.pdf
│
├── 04_CodigoFuente
│   └── Proyecto Java completo
│
├── 05_CredencialesDemo
│   └── usuarios_demo.txt
│
├── 06_Checklists
│   └── checklist_pruebas.pdf
│
└── 07_Logo
    └── recursos_graficos
```

---

## Usuarios de prueba

La entrega puede incluir un archivo con usuarios de demostración.

| Rol | Usuario | PIN |
|---|---|---|
| Administrador | admin | 1234 |
| Encargado | encargado | 1234 |
| Cajero | cajero | 1234 |
| Técnico | tecnico | 1234 |

> Estos datos son solo un ejemplo. Deben sustituirse por las credenciales reales incluidas en la base de datos demo.

---

## Errores comunes

### Error: no se puede conectar con la base de datos

Comprobar:

- Que MariaDB está iniciado.
- Que la base de datos `tpv_cafeteria` existe.
- Que el usuario y contraseña de `config.properties` son correctos.
- Que el puerto 3306 está disponible.
- Que la URL JDBC es correcta.

### Error: `mariadb` no se reconoce como comando

Significa que MariaDB no está añadido al PATH de Windows.

Soluciones:

1. Añadir MariaDB al PATH.
2. Ejecutar el comando usando la ruta completa, por ejemplo:

```bash
"C:\Program Files\MariaDB 12.2\bin\mariadb.exe" -u root -p tpv_cafeteria < tpv_cafeteria_demo.sql
```

La ruta puede variar según la versión instalada.

### Error: Java no está instalado

Comprobar con:

```bash
java -version
```

Si no aparece Java 17 o superior, instalar una versión compatible o usar la carpeta `runtime` incluida en la entrega si está disponible.

### Error al abrir la aplicación desde el `.exe`

Comprobar:

- Que el `.jar` existe en la misma carpeta si el `.exe` depende de él.
- Que el archivo `config.properties` está presente.
- Que las rutas no contienen caracteres problemáticos.
- Que la base de datos está iniciada.
- Que el antivirus no está bloqueando el ejecutable.

### Error al importar la base de datos

Comprobar:

- Que el archivo `.sql` está en la ruta correcta.
- Que se está ejecutando el comando desde la carpeta adecuada.
- Que la contraseña de MariaDB es correcta.
- Que la base de datos se ha creado antes de importar.

---

## Notas sobre seguridad

- No se deben publicar contraseñas reales en GitHub.
- El archivo `config.properties` debe revisarse antes de subir el proyecto.
- En una instalación real se recomienda usar un usuario de base de datos con permisos limitados.
- Las operaciones críticas deben quedar protegidas por rol.
- El sistema utiliza consultas preparadas mediante `PreparedStatement` para reducir riesgos de inyección SQL.
- Los PIN no deben almacenarse en texto plano.

---

## Estado del proyecto

El proyecto se encuentra en fase final de entrega académica. Incluye los módulos principales de un TPV de cafetería:

- Login.
- Fichajes.
- Caja.
- Ventas.
- Tickets.
- Stock.
- Devoluciones.
- Mermas.
- Informes.
- Auditoría.
- Configuración.
- Herramientas técnicas.

Quedan como posibles mejoras futuras:

- Instalador profesional para Windows.
- Copias de seguridad automáticas.
- Integración directa con impresoras térmicas.
- Integración con datáfonos físicos.
- Panel web de administración.
- Mayor cobertura de pruebas automatizadas.
- Sincronización en la nube para varias sucursales.

---

## Autor

Proyecto desarrollado como trabajo final de ciclo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.

Repositorio:

```text
https://github.com/pisdaqwe/CajaRegistradora_0.1.git
```

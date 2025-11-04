## 🧩 Módulos principales y funcionalidades

### 👤 **1. Gestión de usuarios y roles**
- Inicio de sesión con **usuario y PIN cifrado**.  
- Roles: **Cajero** (operaciones de venta) y **Administrador** (gestión y reportes).  
- Bloqueo tras múltiples intentos fallidos.  
- Control de usuarios activos/inactivos.  

### ⏱️ **2. Fichajes y control horario**
- Cada empleado puede **fichar entrada y salida**.  
- Registro de **hora real y estado del fichaje** (en curso o completado).  
- Reportes de asistencia y control de retrasos.  

### 💼 **3. Apertura y cierre de caja**
- Registro de **importe inicial y final de caja**.  
- Validación de **una sesión abierta por caja**.  
- Cálculo automático de diferencias entre arqueo y efectivo real.  
- Histórico de sesiones por usuario y caja.  

### 🛒 **4. Módulo de ventas**
- Pantalla principal tipo TPV con:
  - Categorías a la izquierda (botones grandes).  
  - Productos al centro (grid visual).  
  - Ticket dinámico a la derecha con totales.  
- Permite:
  - Agregar o eliminar productos del ticket.  
  - Incrementar o disminuir unidades.  
  - Calcular subtotales, IVA y totales en tiempo real.  
- Validaciones:
  - No permitir ventas vacías.  
  - No vender productos sin stock.  

### 💳 **5. Cobro y registro de venta**
- Métodos de pago: **efectivo, tarjeta o mixto**.  
- Cálculo de **cambio automático**.  
- Registro completo en BD:
  - Venta + Detalle + Pago + Ticket JSON.  
- Descuento automático de stock (atómico, seguro).  
- Mensaje de confirmación con número de ticket.  

### 🧾 **6. Generación de tickets**
- Ticket guardado en formato **JSON (en la BD)** y **PDF (local)**.  
- Estructura del JSON:
  - Datos de venta, cajero, caja, productos, totales e IVA.  
- PDFs adaptados a **impresoras térmicas de 80mm (226pt)**.  
- Diseño profesional con logotipo y márgenes.  

### 📦 **7. Control de stock**
- Actualización automática en cada venta o ajuste.  
- Soporte para productos con stock **limitado o ilimitado**.  
- Registro de movimientos en tabla `inventario_mov`:
  - Tipos: venta, ajuste, devolución, desperdicio.  
- Alerta visual si un producto queda sin stock.  

### ⚙️ **8. Panel de administración**
- CRUD completo de:
  - Productos (nombre, categoría, precio, IVA, stock, estado).  
  - Categorías.  
  - Usuarios y roles.  
  - Cajas y sesiones.  
- Validación de datos y actualización inmediata en pantalla.  

### 📊 **9. Reportes y estadísticas**
- Reportes filtrables por:
  - Rango de fechas.  
  - Empleado.  
  - Categoría.  
- Visualización en tabla con totales (IVA, bruto, neto).  
- Exportación a **PDF A4** con logotipo, filtros y pie de totales.  

### 🔁 **10. Reimpresión y auditoría**
- Búsqueda de tickets por:
  - Fecha.  
  - Número de venta.  
  - Empleado.  
- Regeneración del PDF desde el JSON guardado.  
- Auditoría de acciones (ventas anuladas, cierres, ajustes).  

### 🧰 **11. Utilidades y soporte**
- Manejo de fechas (`DateUtil`).  
- Formateo de importes (`MoneyUtil`).  
- Validaciones (`ValidationUtil`).  
- Conversión JSON (`JsonUtil`).  
- Generación PDF (`PdfUtil`).  
- Logs automáticos con contexto de error (usuario/sesión/venta).  

---

## 📦 Base de datos (MariaDB)

### Tablas principales
- `usuarios`, `roles`
- `fichajes`
- `cajas`, `sesiones_caja`
- `categorias`, `productos`
- `ventas`, `venta_items`, `pagos`
- `tickets_json`
- `inventario_mov`
- *(Opcional)* `descuentos`, `auditoria`

Todas las relaciones están normalizadas (3FN) y con **claves foráneas** activas.

---

## 🔐 Seguridad
- PIN cifrado con **SHA-256 + salt**.  
- Permisos según rol (solo el admin puede eliminar o modificar registros críticos).  
- Manejo de errores y excepciones controladas.  
- Validación de entrada para evitar inyecciones SQL (uso de `PreparedStatement`).  

---

## 🖥️ Interfaz de usuario

- Diseño cálido estilo cafetería (colores marrón, crema, verde).  
- Botones grandes y legibles (mínimo 44px de alto).  
- Fuente: **Serif-Monospace** para coherencia visual.  
- Panel principal dividido:
  - Izquierda → Categorías.  
  - Centro → Productos.  
  - Derecha → Ticket dinámico.  
  - Superior → Estado de caja/sesión/usuario.  

---

## 🧪 Pruebas previstas
- **Unitarias:** Servicios de autenticación, venta, stock, PDF, JSON.  
- **Integración:** DAOs conectando a BD remota.  
- **E2E:** Flujos completos (Login → Venta → Cobro → Cierre → Reporte).  
- **Concurrencia:** Ventas simultáneas en el mismo producto.  

---

## 💾 Instalación y despliegue

### Requisitos
- Java 17+  
- MariaDB 10+  
- Conexión TCP (puerto 3306 abierto o SSH tunel)  

### Pasos
1. Crear base de datos `tpv_db`.  
2. Ejecutar script `schema.sql` y `seed.sql`.  
3. Crear usuario `tpv_user` con permisos limitados.  
4. Editar archivo `config.properties` con tus datos.  
5. Ejecutar:# CajaRegistradora_0.1
Caja registradora 

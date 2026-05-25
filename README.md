# AutoFix Pro 🔧

**Sistema de gestión integral para talleres mecánicos pequeños**

AutoFix Pro es una aplicación de escritorio desarrollada en Java que digitaliza y centraliza la gestión diaria de un taller mecánico: citas, clientes, servicios, facturación y control de empleados, todo desde una misma herramienta gratuita y sin dependencia de internet.

---

## ¿Por qué AutoFix Pro?

Más del 70% de los talleres mecánicos en España son microempresas que aún gestionan su trabajo con agendas en papel o mensajes de WhatsApp. Esto provoca solapamiento de citas, pérdida de información y falta de control financiero.

AutoFix Pro nace para cubrir ese hueco: ofrece las funcionalidades de un software profesional sin los costes ni la complejidad de las soluciones del mercado.

---

## Funcionalidades principales

### 🔐 Autenticación y roles
- Login con credenciales seguras
- Dos roles diferenciados: **Administrador** y **Trabajador**
- El administrador tiene acceso completo; el trabajador solo ve sus citas asignadas

### 📅 Gestión de citas
- Creación, edición y cancelación de citas
- Asignación de múltiples servicios por cita
- Estados con código de colores: `Pendiente` · `En Proceso` · `Completada` · `Cancelada`
- Registro del motivo de cancelación y empleado responsable

### 👥 Gestión de clientes
- Alta, edición y baja de clientes
- Búsqueda en tiempo real por nombre
- Historial de vehículos y matrículas por cliente

### 🛠️ Gestión de servicios
- Catálogo de servicios con nombre, descripción, precio y duración estimada
- Activar/desactivar servicios sin perder el historial

### 🧾 Facturación automática en PDF
- Generación automática de factura al completar una cita
- Desglose de base imponible e IVA (21%)
- Numeración única por factura, datos del taller y del cliente
- Guardado automático en `[Usuario]/AutoFixPro/Facturas/`

### 📊 Panel de reportes (solo administrador)
- KPIs en tiempo real: total de citas, ingresos y cancelaciones
- Filtrado por fecha para consultar actividad histórica
- Vista de trabajos activos (pendientes y en proceso)

### 📁 Cierre de periodo e histórico
- Archivado de citas finalizadas sin eliminar datos
- Exportación del resumen del periodo en PDF

### 👤 Gestión de usuarios (solo administrador)
- Alta, edición y baja de empleados
- Identificación visual por rol (azul: administrador, verde: trabajador)
- Protección contra eliminación del último usuario del sistema

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Java** | Lenguaje principal |
| **Java Swing** | Interfaz gráfica de escritorio |
| **MySQL** | Base de datos relacional |
| **JDBC** | Conexión y operaciones con la base de datos |
| **Arquitectura MVC + DAO** | Separación de capas: vista, lógica y persistencia |
| **iTextPDF** | Generación de facturas en PDF |
| **JUnit** | Pruebas unitarias e integración |
| **Maven** | Gestión de dependencias |
| **Git** | Control de versiones |
| **IntelliJ IDEA** | Entorno de desarrollo |

---

## Arquitectura del proyecto

```
src/
└── com/autofix/
    ├── controller/     # Lógica de negocio (MVC)
    ├── dao/            # Acceso a datos (patrón DAO)
    ├── model/          # Entidades: Cliente, Cita, Servicio, Usuario...
    └── view/           # Interfaces gráficas Swing

test/
└── com/autofix/        # 24 tests unitarios (100% passing)
```

### Base de datos — 7 tablas

```sql
usuarios        -- Empleados y administradores del taller
clientes        -- Datos de contacto de los clientes
servicios       -- Catálogo de servicios con precio y duración
citas           -- Reservas con estado, matrícula y vehículo
detalle_citas   -- Relación entre citas y servicios (N:M)
historico_periodos  -- Cabecera de cada cierre de periodo
historico_citas     -- Citas archivadas al cerrar un periodo
```

---

## Tests

La aplicación cuenta con **24 tests automatizados** con JUnit que cubren el ciclo completo de cada módulo:

- ✅ Conexión a base de datos
- ✅ CRUD completo de Clientes
- ✅ CRUD completo de Servicios (con validación de precisión decimal)
- ✅ CRUD completo de Usuarios + autenticación (camino positivo y negativo)
- ✅ CRUD completo de Citas + filtros de estado + cálculo de ingresos

**Resultado: 24/24 tests en verde.**

---

## Descarga

👉 **[AutofixPro.jar — v1.0](https://github.com/Infernus13/AutoFix-Pro/releases/tag/v1.0)**

No necesitas compilar nada. Descarga el JAR, configura la base de datos y ejecútalo directamente.

---

## Instalación y requisitos

### Requisitos previos
- Java JDK 17 o superior
- MySQL Server (o XAMPP)

### Pasos

**1. Descarga el ejecutable**

Descarga `AutofixPro.jar` desde la sección [Releases](https://github.com/Infernus13/AutoFix-Pro/releases/tag/v1.0).

**2. Crea la base de datos**

Importa el script SQL incluido en `/sql/autofix_pro.sql` desde MySQL Workbench o phpMyAdmin. Esto creará automáticamente la base de datos `autofix_pro` con sus 7 tablas y datos de prueba.

**3. Configura la conexión**

Edita el archivo de conexión con tus credenciales de MySQL:
```java
String url = "jdbc:mysql://localhost:3306/autofix_pro";
String user = "tu_usuario";
String password = "tu_contraseña";
```

**4. Ejecuta la aplicación**

```bash
java -jar AutofixPro.jar
```

### Credenciales de prueba
```
Usuario:     admin@autofix.com
Contraseña:  1234
```

---

## Capturas de pantalla

> *Próximamente*

---

## Limitaciones conocidas (v1.0)

- No incluye copias de seguridad automáticas
- Diseñado para red local, sin acceso remoto
- Sin facturación electrónica (Verifactu)
- Sin control de permisos avanzado por módulo

---

## Autor

**Cristian Sánchez Cachinero**
Técnico Superior en Desarrollo de Aplicaciones Multiplataforma — Ilerna, Córdoba
📧 cristiansanc13@gmail.com

---

## Licencia

Proyecto académico de libre uso. Desarrollado como Trabajo de Fin de Ciclo — DAM 2025.

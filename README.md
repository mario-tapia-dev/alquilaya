# AlquilaYa

Sistema de renta de vehículos con API REST e interfaz web, hecho con Spring Boot.

## Descripción de la API

AlquilaYa permite administrar una empresa de renta de vehículos: oficinas, flota, clientes, alquileres y mantenimientos. Ofrece dos formas de uso sobre la misma lógica de negocio:

- **API REST** (`/api/...`) que recibe y devuelve JSON, con autenticación HTTP Basic.
- **Interfaz web** (Thymeleaf) con inicio de sesión, listados y formularios de cada sección, en estilo bosque (verdes y cafés).

**Roles**

| Rol | Acceso |
|---|---|
| `ADMIN` | Todo: vehículos, clientes, oficinas, alquileres y mantenimientos. |
| `OPERADOR` | Solo clientes y alquileres (y lectura de vehículos en la API). |

**Reglas de negocio de los alquileres**

- La fecha de devolución debe ser posterior a la fecha de alquiler.
- Un vehículo no puede tener dos alquileres cuyas fechas se crucen (devolver y recoger el mismo día sí se permite).
- Un vehículo en mantenimiento no se puede alquilar.
- El precio total se calcula solo: tarifa diaria del vehículo × número de días.
- El estado del vehículo (`DISPONIBLE` o `ALQUILADO`) se actualiza automáticamente al crear, editar o eliminar alquileres, al iniciar la app y todos los días a las 00:05. El estado `MANTENIMIENTO` se asigna manualmente.
- No se puede eliminar una oficina con vehículos, ni un vehículo o cliente con alquileres registrados.

## Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC)
- Spring Data JPA / Hibernate
- Spring Security (formulario de login y HTTP Basic, contraseñas con BCrypt)
- Thymeleaf (plantillas HTML) y CSS propio
- Bean Validation (Jakarta Validation)
- MySQL 8 con `mysql-connector-j`
- Maven (incluye Maven Wrapper)

## Pasos de instalación y ejecución

**Requisitos previos:** Java 21, MySQL 8 en ejecución y Git.

1. Clona el repositorio:

   ```bash
   git clone https://github.com/mario-tapia-dev/alquilaya.git
   cd alquilaya
   ```

2. Crea la base de datos en MySQL (las tablas se crean solas al arrancar):

   ```sql
   CREATE DATABASE alquilaya;
   ```

3. Define las [variables de entorno](#variables-de-entorno-necesarias). Ejemplo en Linux/macOS:

   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=tu_contraseña
   ```

   En Windows (PowerShell):

   ```powershell
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="tu_contraseña"
   ```

4. Ejecuta la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows usa `mvnw.cmd spring-boot:run`.

5. Abre `http://localhost:1501` e inicia sesión. Al primer arranque se crean dos usuarios (ver variables de entorno).

**Generar el JAR (opcional):**

```bash
./mvnw clean package -DskipTests
java -jar target/alquilaya-0.0.1-SNAPSHOT.jar
```

> Las pruebas (`mvn test`) necesitan MySQL disponible; por eso se omiten en el empaquetado.

## Variables de entorno necesarias

| Variable | Obligatoria | Valor por defecto | Descripción |
|---|---|---|---|
| `DB_USERNAME` | Sí | — | Usuario de MySQL. |
| `DB_PASSWORD` | Sí | — | Contraseña de MySQL. |
| `DB_URL` | No | `jdbc:mysql://localhost:3306/alquilaya?useSSL=false&serverTimezone=UTC` | URL JDBC de la base de datos. |
| `APP_ADMIN_EMAIL` | No | `admin@alquilaya.com` | Correo del usuario administrador inicial. |
| `APP_ADMIN_PASSWORD` | No | `Admin1234` | Contraseña del administrador inicial. |
| `APP_OPERADOR_EMAIL` | No | `operador@alquilaya.com` | Correo del usuario operador inicial. |
| `APP_OPERADOR_PASSWORD` | No | `Operador123` | Contraseña del operador inicial. |

Ejemplo de archivo `.env` (no lo subas al repositorio):

```env
DB_URL=jdbc:mysql://localhost:3306/alquilaya?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=cambia_esta_contraseña
```

> Cambia las contraseñas por defecto de los usuarios iniciales antes de usar la aplicación fuera de un entorno de pruebas. Los usuarios solo se crean si no existen.

## Listado de endpoints

Todos los endpoints requieren autenticación. En la API se usa **HTTP Basic** con el correo y la contraseña del usuario. Códigos de éxito: `200` (consulta y actualización), `201` (creación) y `204` (eliminación, sin cuerpo).

### API REST

| Método | Ruta | Descripción | Roles |
|---|---|---|---|
| GET | `/api/vehiculos` | Lista los vehículos | ADMIN, OPERADOR |
| GET | `/api/vehiculos/{id}` | Consulta un vehículo | ADMIN, OPERADOR |
| POST | `/api/vehiculos` | Crea un vehículo | ADMIN |
| PUT | `/api/vehiculos/{id}` | Actualiza un vehículo | ADMIN |
| DELETE | `/api/vehiculos/{id}` | Elimina un vehículo | ADMIN |
| GET | `/api/clientes` | Lista los clientes | ADMIN, OPERADOR |
| GET | `/api/clientes/{id}` | Consulta un cliente | ADMIN, OPERADOR |
| POST | `/api/clientes` | Crea un cliente | ADMIN, OPERADOR |
| PUT | `/api/clientes/{id}` | Actualiza un cliente | ADMIN, OPERADOR |
| DELETE | `/api/clientes/{id}` | Elimina un cliente | ADMIN, OPERADOR |
| GET | `/api/oficinas` | Lista las oficinas | ADMIN |
| GET | `/api/oficinas/{id}` | Consulta una oficina | ADMIN |
| POST | `/api/oficinas` | Crea una oficina | ADMIN |
| PUT | `/api/oficinas/{id}` | Actualiza una oficina | ADMIN |
| DELETE | `/api/oficinas/{id}` | Elimina una oficina | ADMIN |
| GET | `/api/alquileres` | Lista los alquileres | ADMIN, OPERADOR |
| GET | `/api/alquileres/{id}` | Consulta un alquiler | ADMIN, OPERADOR |
| POST | `/api/alquileres` | Registra un alquiler | ADMIN, OPERADOR |
| PUT | `/api/alquileres/{id}` | Actualiza un alquiler | ADMIN, OPERADOR |
| DELETE | `/api/alquileres/{id}` | Elimina un alquiler | ADMIN, OPERADOR |
| GET | `/api/mantenimientos` | Lista los mantenimientos | ADMIN |
| GET | `/api/mantenimientos/{id}` | Consulta un mantenimiento | ADMIN |
| POST | `/api/mantenimientos` | Registra un mantenimiento | ADMIN |
| PUT | `/api/mantenimientos/{id}` | Actualiza un mantenimiento | ADMIN |
| DELETE | `/api/mantenimientos/{id}` | Elimina un mantenimiento | ADMIN |
| GET | `/api/admin/prueba` | Verifica el acceso de administrador | ADMIN |
| GET | `/api/operador/prueba` | Verifica el acceso de operador | ADMIN, OPERADOR |

**Campos de cada recurso** (en `POST` y `PUT`)

| Recurso | Campos |
|---|---|
| Oficina | `nombreOficina`, `direccion`, `ciudad`, `region` (todos obligatorios) |
| Vehículo | `marca`, `modelo`, `anio`, `matricula` (única), `tarifaDiaria` (> 0), `estado`, `oficina: { "idOficina": n }` |
| Cliente | `nombre`, `apellido`, `email` (único), `licenciaConducir` (única), `telefono` (opcional) |
| Alquiler | `vehiculo: { "idVehiculo": n }`, `cliente: { "idCliente": n }`, `fechaAlquiler`, `fechaDevolucion` (formato `AAAA-MM-DD`). El `precioTotal` se calcula solo. |
| Mantenimiento | `vehiculo: { "idVehiculo": n }`, `fechaMantenimiento`, `descripcion`, `costo` (opcional, ≥ 0) |

Valores válidos de `estado` en vehículos: `DISPONIBLE`, `ALQUILADO`, `MANTENIMIENTO`.

### Pantallas web

Cada sección sigue el mismo patrón (`{seccion}` = `vehiculos`, `clientes`, `oficinas`, `alquileres` o `mantenimientos`):

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/login` | Inicio de sesión |
| GET | `/` | Inicio con resumen de la flota |
| GET | `/{seccion}` | Listado |
| GET | `/{seccion}/nuevo` | Formulario de alta (`/oficinas/nueva` en oficinas) |
| POST | `/{seccion}` | Guarda el registro nuevo |
| GET | `/{seccion}/{id}/editar` | Formulario de edición |
| POST | `/{seccion}/{id}` | Guarda los cambios |
| POST | `/{seccion}/{id}/eliminar` | Elimina el registro |

Permisos: `clientes` y `alquileres` para ADMIN y OPERADOR; `vehiculos`, `oficinas` y `mantenimientos` solo para ADMIN.

## Ejemplos de peticiones y respuestas

Los ejemplos usan `curl` con el administrador por defecto. El servidor corre en `http://localhost:1501`.

### Crear una oficina

```bash
curl -X POST http://localhost:1501/api/oficinas \
  -u admin@alquilaya.com:Admin1234 \
  -H "Content-Type: application/json" \
  -d '{
    "nombreOficina": "Sucursal Centro",
    "direccion": "Av. Independencia 120",
    "ciudad": "Veracruz",
    "region": "Veracruz"
  }'
```

Respuesta `201 Created`:

```json
{
  "idOficina": 1,
  "nombreOficina": "Sucursal Centro",
  "direccion": "Av. Independencia 120",
  "ciudad": "Veracruz",
  "region": "Veracruz",
  "vehiculos": null
}
```

### Crear un vehículo

```bash
curl -X POST http://localhost:1501/api/vehiculos \
  -u admin@alquilaya.com:Admin1234 \
  -H "Content-Type: application/json" \
  -d '{
    "marca": "Nissan",
    "modelo": "Versa",
    "anio": 2024,
    "matricula": "XYZ-123-A",
    "tarifaDiaria": 850.00,
    "estado": "DISPONIBLE",
    "oficina": { "idOficina": 1 }
  }'
```

Respuesta `201 Created`:

```json
{
  "idVehiculo": 1,
  "marca": "Nissan",
  "modelo": "Versa",
  "anio": 2024,
  "matricula": "XYZ-123-A",
  "tarifaDiaria": 850.00,
  "estado": "DISPONIBLE",
  "oficina": {
    "idOficina": 1,
    "nombreOficina": "Sucursal Centro",
    "direccion": "Av. Independencia 120",
    "ciudad": "Veracruz",
    "region": "Veracruz"
  },
  "alquileres": null,
  "mantenimientos": null
}
```

### Crear un cliente

```bash
curl -X POST http://localhost:1501/api/clientes \
  -u operador@alquilaya.com:Operador123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellido": "López",
    "email": "ana.lopez@example.com",
    "licenciaConducir": "VER-1234567",
    "telefono": "2291234567"
  }'
```

Respuesta `201 Created`:

```json
{
  "idCliente": 1,
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana.lopez@example.com",
  "licenciaConducir": "VER-1234567",
  "telefono": "2291234567",
  "alquileres": null
}
```

### Registrar un alquiler

```bash
curl -X POST http://localhost:1501/api/alquileres \
  -u operador@alquilaya.com:Operador123 \
  -H "Content-Type: application/json" \
  -d '{
    "vehiculo": { "idVehiculo": 1 },
    "cliente": { "idCliente": 1 },
    "fechaAlquiler": "2026-11-01",
    "fechaDevolucion": "2026-11-04"
  }'
```

Respuesta `201 Created` (3 días × $850.00):

```json
{
  "idAlquiler": 1,
  "vehiculo": {
    "idVehiculo": 1,
    "marca": "Nissan",
    "modelo": "Versa",
    "anio": 2024,
    "matricula": "XYZ-123-A",
    "tarifaDiaria": 850.00,
    "estado": "DISPONIBLE",
    "oficina": {
      "idOficina": 1,
      "nombreOficina": "Sucursal Centro",
      "direccion": "Av. Independencia 120",
      "ciudad": "Veracruz",
      "region": "Veracruz"
    },
    "mantenimientos": []
  },
  "cliente": {
    "idCliente": 1,
    "nombre": "Ana",
    "apellido": "López",
    "email": "ana.lopez@example.com",
    "licenciaConducir": "VER-1234567",
    "telefono": "2291234567"
  },
  "fechaAlquiler": "2026-11-01",
  "fechaDevolucion": "2026-11-04",
  "precioTotal": 2550.00
}
```

El vehículo sigue `DISPONIBLE` porque el alquiler empieza en el futuro; pasa a `ALQUILADO` cuando llega la fecha de inicio.

### Consultar un vehículo

```bash
curl http://localhost:1501/api/vehiculos/1 -u operador@alquilaya.com:Operador123
```

Respuesta `200 OK`:

```json
{
  "idVehiculo": 1,
  "marca": "Nissan",
  "modelo": "Versa",
  "anio": 2024,
  "matricula": "XYZ-123-A",
  "tarifaDiaria": 850.00,
  "estado": "DISPONIBLE",
  "oficina": {
    "idOficina": 1,
    "nombreOficina": "Sucursal Centro",
    "direccion": "Av. Independencia 120",
    "ciudad": "Veracruz",
    "region": "Veracruz"
  },
  "alquileres": [
    {
      "idAlquiler": 1,
      "cliente": {
        "idCliente": 1,
        "nombre": "Ana",
        "apellido": "López",
        "email": "ana.lopez@example.com",
        "licenciaConducir": "VER-1234567",
        "telefono": "2291234567"
      },
      "fechaAlquiler": "2026-11-01",
      "fechaDevolucion": "2026-11-04",
      "precioTotal": 2550.00
    }
  ],
  "mantenimientos": []
}
```

### Actualizar un cliente

```bash
curl -X PUT http://localhost:1501/api/clientes/1 \
  -u operador@alquilaya.com:Operador123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana",
    "apellido": "López",
    "email": "ana.lopez@example.com",
    "licenciaConducir": "VER-1234567",
    "telefono": "2299876543"
  }'
```

Respuesta `200 OK`: el cliente con el teléfono actualizado.

```json
{
  "idCliente": 1,
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana.lopez@example.com",
  "licenciaConducir": "VER-1234567",
  "telefono": "2299876543",
  "alquileres": []
}
```

### Eliminar un alquiler

```bash
curl -X DELETE http://localhost:1501/api/alquileres/1 -u operador@alquilaya.com:Operador123
```

Respuesta `204 No Content` (sin cuerpo).

### Ejemplos de errores

Los errores de la API devuelven JSON con el campo `error`.

**Datos inválidos** (`400 Bad Request`), por ejemplo un cliente con correo mal escrito:

```json
{
  "error": "Hay datos inválidos",
  "campos": {
    "email": "El correo no tiene un formato válido"
  }
}
```

**Regla de negocio incumplida** (`400 Bad Request`), por ejemplo un alquiler que se cruza con otro:

```json
{
  "error": "El vehículo ya tiene un alquiler que se cruza con esas fechas"
}
```

Otros mensajes de este tipo: `La fecha de devolución debe ser posterior a la fecha de alquiler`, `El vehículo está en mantenimiento y no se puede alquilar`, `Ya existe un cliente con ese correo`, `No se puede eliminar el vehículo porque tiene alquileres registrados`.

**Recurso inexistente** (`404 Not Found`):

```json
{
  "error": "Vehículo no encontrado con id: 99"
}
```

**Sin credenciales o credenciales incorrectas:** `401 Unauthorized`.
**Rol sin permiso** (por ejemplo, un operador intentando crear una oficina): `403 Forbidden`.

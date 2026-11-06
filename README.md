# Web II — API de productos, favoritos y listas

API REST desarrollada con Spring Boot para consultar un catálogo externo de productos, gestionar favoritos y listas, registrar usuarios y autenticar solicitudes mediante JWT. El proyecto utiliza PostgreSQL para la persistencia y Flyway para versionar los cambios del esquema.

## Funcionalidades

- Consulta de productos mediante la API externa [DummyJSON](https://dummyjson.com).
- CRUD de favoritos persistidos en PostgreSQL.
- Creación y administración de listas de favoritos.
- Registro de usuarios con contraseñas almacenadas mediante hash BCrypt.
- Login con emisión de JWT firmado (HS256).
- Protección de endpoints mediante Spring Security y un filtro Bearer JWT.
- Asignación automática del propietario al crear una lista.
- Autorización por propietario: cada usuario accede a sus listas; `ADMIN` puede acceder a todas.
- Migraciones de base de datos administradas por Flyway.
- Documentación interactiva mediante Swagger UI / OpenAPI.

## Tecnologías

- Java 25.
- Spring Boot 4.1.2.
- Spring MVC.
- Spring Security.
- Spring Data JPA / Hibernate.
- PostgreSQL 17.
- Flyway.
- JJWT 0.13.0.
- BCrypt mediante `PasswordEncoder`.
- Springdoc OpenAPI 3.1.0.
- Maven Wrapper.
- Testcontainers, JUnit y Maven Failsafe para pruebas.

## Requisitos

- JDK 25.
- Docker y Docker Compose, o una instancia local de PostgreSQL 17.
- Windows, macOS o Linux.

## Configuración y ejecución

### 1. Configurar PostgreSQL con Docker

El proyecto incluye `docker-compose.yml`. En la raíz del proyecto, crear un archivo `.env` con las variables que utiliza Docker Compose:

```properties
POSTGRES_DB=webii_tp2
POSTGRES_USER=webii_tp2
POSTGRES_PASSWORD=cambiar_esta_contrasena
```

Levantar la base de datos:

```bash
docker compose up -d
```

El contenedor se llama `demo-postgres`, publica PostgreSQL en el puerto `5432` y utiliza el volumen `demo-postgres-data` para conservar los datos entre reinicios.

Para detener el contenedor:

```bash
docker compose down
```

> `docker compose down -v` también elimina el volumen y, por lo tanto, los datos persistidos. No utilizarlo si se desea conservar la base.

### 2. Configurar las variables de entorno de la aplicación

La aplicación lee estas variables:

| Variable | Propósito | Valor de desarrollo de ejemplo |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/webii_tp2` |
| `DB_USERNAME` | Usuario de PostgreSQL | `webii_tp2` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | La misma definida en `POSTGRES_PASSWORD` |
| `JWT_SECRET` | Clave secreta Base64 para firmar y validar JWT | Generar una clave propia de al menos 32 bytes |
| `JWT_EXPIRATION_MS` | Duración del JWT en milisegundos | `3600000` (una hora) |

El archivo `.env` de la raíz es utilizado por Docker Compose para interpolar las variables del contenedor. Spring Boot **no carga automáticamente** ese archivo; las variables `DB_*` y `JWT_*` también deben estar disponibles en el entorno desde el que se ejecuta la aplicación o configurarse en el entorno de ejecución del IDE.

En PowerShell, configurar la conexión de la aplicación:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/webii_tp2"
$env:DB_USERNAME = "webii_tp2"
$env:DB_PASSWORD = "cambiar_esta_contrasena"
$env:JWT_EXPIRATION_MS = "3600000"
```

Generar una clave JWT aleatoria de 32 bytes codificada en Base64 y asignarla a la sesión actual de PowerShell:

```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$key = New-Object byte[] 32
$rng.GetBytes($key)
$env:JWT_SECRET = [Convert]::ToBase64String($key)
$rng.Dispose()
```

En macOS o Linux, la clave se puede generar con OpenSSL:

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION_MS="3600000"
```

La clave debe mantenerse privada y estable mientras se quieran aceptar los tokens ya emitidos. No subir secretos reales al repositorio. Si se genera una clave nueva, los JWT firmados con la anterior dejan de ser válidos.

### 3. Ejecutar la aplicación

Desde la raíz del proyecto, en la misma terminal donde se configuraron las variables:

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

Al iniciar, Flyway valida y aplica las migraciones pendientes. Hibernate utiliza `ddl-auto=validate`: comprueba que las entidades coincidan con el esquema, pero no crea ni modifica tablas.

### 4. Ejecutar las pruebas

Pruebas unitarias:

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

Para ejecutar también el ciclo de integración configurado con Maven Failsafe:

```bash
./mvnw verify
```

Las pruebas de integración que utilizan Testcontainers requieren que Docker esté disponible.

## API REST

Todas las rutas protegidas requieren el header:

```http
Authorization: Bearer <accessToken>
```

### Autenticación

Las rutas `/api/auth/**` son públicas.

| Método | Ruta | Descripción | Respuestas principales |
|---|---|---|---|
| POST | `/api/auth/registro` | Registra un usuario. El rol inicial se asigna como `USER`. | `201`, `400`, `409` |
| POST | `/api/auth/login` | Valida email y contraseña y devuelve un JWT. | `200`, `400`, `401` |

Ejemplo de registro:

```json
{
  "email": "usuario@example.com",
  "password": "MiPassword123"
}
```

Ejemplo de respuesta del registro:

```json
{
  "id": 1,
  "email": "usuario@example.com",
  "rol": "USER"
}
```

Ejemplo de login:

```json
{
  "email": "usuario@example.com",
  "password": "MiPassword123"
}
```

Respuesta de login:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer"
}
```

El cliente debe conservar el `accessToken` y enviarlo en el header `Authorization` para las operaciones protegidas. El JWT contiene el ID, email y rol del usuario, además de sus fechas de emisión y expiración. Está firmado, no cifrado: su contenido puede leerse, por lo que no debe incluir información secreta.

Las contraseñas no se guardan en texto plano. En el registro se utiliza `PasswordEncoder` con BCrypt; en el login se compara la contraseña recibida con el hash almacenado mediante `matches()`.

### Productos

Las rutas `/api/productos/**` son públicas. Los datos se obtienen desde DummyJSON y se mapean a DTOs propios de esta API.

| Método | Ruta | Descripción | Respuestas principales |
|---|---|---|---|
| GET | `/api/productos?limit=10&skip=0` | Lista una página del catálogo. | `200`, `502` |
| GET | `/api/productos/{id}` | Obtiene un producto por ID. | `200`, `404`, `502` |

`502 Bad Gateway` indica que la aplicación no pudo consumir correctamente el servicio externo.

### Favoritos

Todas las rutas `/api/favoritos/**` requieren un JWT válido.

| Método | Ruta | Descripción | Respuestas principales |
|---|---|---|---|
| GET | `/api/favoritos` | Lista los favoritos. | `200`, `401` |
| POST | `/api/favoritos` | Crea un favorito. | `201`, `400`, `401` |
| GET | `/api/favoritos/{id}` | Obtiene un favorito por ID. | `200`, `401`, `404` |
| PUT | `/api/favoritos/{id}` | Actualiza un favorito. | `200`, `400`, `401`, `404` |
| DELETE | `/api/favoritos/{id}` | Elimina un favorito. | `204`, `401`, `404` |

Ejemplo para crear o actualizar un favorito:

```json
{
  "productoId": 1,
  "listaId": 1,
  "nota": "Para comprar"
}
```

### Listas

Todas las rutas `/api/listas/**` requieren un JWT válido.

| Método | Ruta | Descripción | Respuestas principales |
|---|---|---|---|
| POST | `/api/listas` | Crea una lista y asigna como propietario al usuario autenticado. | `201`, `400`, `401` |
| GET | `/api/listas` | Devuelve las listas del usuario; `ADMIN` obtiene todas. | `200`, `401` |
| GET | `/api/listas/{id}` | Obtiene una lista si pertenece al usuario o si es `ADMIN`. | `200`, `401`, `403`, `404` |
| GET | `/api/listas/{id}/favoritos` | Lista los favoritos de una lista autorizada. | `200`, `401`, `403`, `404` |
| POST | `/api/listas/{origenId}/mover-favoritos` | Mueve los favoritos a la lista destino y elimina la lista de origen. Se deben poder administrar ambas listas. | `204`, `401`, `403`, `404` |
| DELETE | `/api/listas/{id}` | Elimina una lista autorizada si no contiene favoritos. | `204`, `401`, `403`, `404`, `409` |

Ejemplo para crear una lista:

```json
{
  "nombre": "Compras"
}
```

El cliente **no envía `usuarioId`**. El backend obtiene el ID desde la identidad autenticada y lo asigna como propietario.

Ejemplo para mover favoritos:

```json
{
  "listaDestinoId": 2
}
```

La operación de movimiento comprueba la propiedad tanto de la lista de origen como de la lista destino. Si se completa, devuelve `204 No Content` y elimina la lista de origen.

## Autenticación y autorización

### Autenticación con JWT

El flujo de autenticación se implementa con Spring Security y JJWT:

1. El cliente envía email y contraseña a `POST /api/auth/login`.
2. `AuthServiceImpl` busca el usuario mediante `UsuarioRepository` y compara la contraseña con el hash BCrypt mediante `PasswordEncoder.matches()`.
3. Si las credenciales son correctas, `JwtService` genera un JWT firmado con HS256 que contiene el ID, email y rol del usuario.
4. El cliente recibe el token y lo envía como `Authorization: Bearer <token>` en las siguientes solicitudes protegidas.
5. `JwtAuthenticationFilter` valida el token y establece un `UsuarioAutenticado` en el `SecurityContext` de Spring Security.

La aplicación utiliza sesiones `STATELESS`: no mantiene una sesión HTTP del usuario entre solicitudes. Cada solicitud protegida presenta su propio token. La duración predeterminada del JWT es de una hora y se puede modificar con `JWT_EXPIRATION_MS`.

### Rutas públicas y protegidas

`SecurityConfig` define las reglas generales:

- Públicas: `/api/auth/**`, `/api/productos/**`, `/swagger-ui/**`, `/swagger-ui.html` y `/v3/api-docs/**`.
- Protegidas: el resto de las rutas, incluidas `/api/favoritos/**`, `/api/listas/**`, `/health` y `/ping`.

Una solicitud sin autenticación o con un JWT inválido/vencido no puede acceder a una ruta protegida y responde `401 Unauthorized`.

### Roles

El registro público siempre asigna el rol `USER`. El cliente no puede elegir su rol en el body del registro.

El filtro JWT transforma el rol del token en una autoridad de Spring Security con el prefijo `ROLE_`, por ejemplo `ROLE_USER` o `ROLE_ADMIN`. No existe un endpoint público para crear administradores o cambiar roles; la asignación de `ADMIN` debe realizarse mediante un procedimiento administrativo controlado.

Para una prueba local, el rol de un usuario puede cambiarse directamente en PostgreSQL:

```sql
UPDATE usuarios
SET rol = 'ADMIN'
WHERE email = 'admin@example.com';
```

Después de cambiar el rol, el usuario debe iniciar sesión nuevamente para obtener un JWT que contenga el rol actualizado. El token anterior conserva los claims con los que fue emitido.

### ¿Dónde vive la regla de autorización por propietario?

La regla de propiedad de las listas vive en **`ListaServiceImpl`**, no en el filtro JWT ni en el Controller.

- `JwtAuthenticationFilter` se ocupa de autenticar la solicitud: valida el token y deja disponible la identidad (`id`, `email` y `rol`) en el `SecurityContext`.
- `ListaController` obtiene esa identidad mediante `@AuthenticationPrincipal` y pasa el ID y el rol al Service. No decide si el usuario tiene permiso.
- `ListaServiceImpl` aplica la regla de negocio: busca la lista y permite la operación si el usuario es propietario o si su rol es `ADMIN`. La comprobación se centraliza en `obtenerYVerificarPropiedad()` para reutilizar el mismo criterio en las operaciones correspondientes.
- `GlobalExceptionHandler` transforma `AccessDeniedException` en `403 Forbidden` con un cuerpo `ProblemDetail`.

Esta regla pertenece al Service porque expresa una condición del negocio sobre un recurso concreto. El filtro no conoce la propiedad de cada lista: su responsabilidad es validar la identidad del usuario, no consultar las reglas de cada operación. El Controller debe ocuparse del protocolo HTTP y delegar la lógica, no repetir comprobaciones de permisos.

La API diferencia estos resultados:

| Código | Significado |
|---|---|
| `401 Unauthorized` | No se presentó una autenticación válida. |
| `403 Forbidden` | El usuario está autenticado, pero no es propietario de la lista ni tiene rol `ADMIN`. |
| `404 Not Found` | La lista solicitada no existe. |
| `409 Conflict` | No se puede eliminar una lista porque todavía contiene favoritos. |

## Arquitectura y estructura

El proyecto separa HTTP, lógica de negocio, dominio y persistencia. Los Services dependen de interfaces de repositorio (puertos), mientras que los Adapters implementan esos contratos usando Spring Data JPA.

```text
com.example.demo
├── client/
│   └── dummyjson/
│       ├── DummyJsonClient
│       ├── DummyJsonProducto
│       └── DummyJsonProductosResponse
├── config/
│   ├── OpenApiConfig
│   ├── PasswordEncoderConfig
│   ├── RestClientConfig
│   └── SecurityConfig
├── controller/
│   ├── AuthController
│   ├── FavoritoController
│   ├── HealthController
│   ├── ListaController
│   ├── PingController
│   └── ProductoController
├── domain/
│   ├── Favorito
│   ├── Lista
│   └── Usuario
├── dto/
│   ├── auth/
│   ├── favorito/
│   ├── lista/
│   └── producto/
├── entity/
│   ├── FavoritoEntity
│   ├── ListaEntity
│   └── UsuarioEntity
├── exception/
│   ├── ConflictoRecursoException
│   ├── GlobalExceptionHandler
│   ├── RecursoNoEncontradoException
│   └── ServicioExternoException
├── repository/
│   ├── FavoritoRepository / FavoritoJpaRepository / FavoritoRepositoryAdapter
│   ├── ListaRepository / ListaJpaRepository / ListaRepositoryAdapter
│   └── UsuarioRepository / UsuarioJpaRepository / UsuarioRepositoryAdapter
├── security/
│   ├── JwtAuthenticationFilter
│   └── UsuarioAutenticado
└── service/
    ├── AuthService / AuthServiceImpl
    ├── FavoritoService / FavoritoServiceImpl
    ├── JwtService
    ├── ListaService / ListaServiceImpl
    └── ProductoService / ProductoServiceImpl
```

### Responsabilidades principales

- **Controller:** recibe solicitudes HTTP, valida requests y delega en los Services.
- **Service:** implementa las reglas de negocio y coordina las operaciones.
- **Domain:** representa los modelos de negocio sin depender de JPA.
- **Repository:** define los contratos de acceso a datos.
- **JPA Repository:** utiliza Spring Data JPA para ejecutar operaciones sobre las entidades.
- **Repository Adapter:** convierte entre modelos de dominio y entidades JPA.
- **Entity:** representa las tablas y relaciones persistidas.
- **Security:** contiene el filtro JWT y el principal autenticado.
- **Config:** registra y configura componentes de Spring.
- **Exception:** centraliza las excepciones de la aplicación y sus respuestas HTTP.

### Relaciones del modelo

- Un `Usuario` puede tener muchas `Lista`; cada lista referencia a su propietario mediante `usuario_id`.
- Una `Lista` puede contener muchos `Favorito`; cada favorito referencia una lista mediante `lista_id`.
- `Favorito` conserva `productoId` como referencia al producto del catálogo externo; no se persiste una entidad de producto local.

## Persistencia y migraciones

PostgreSQL es la base de datos principal. Flyway administra la evolución del esquema y registra las migraciones ejecutadas en `flyway_schema_history`.

Las migraciones se encuentran en `src/main/resources/db/migration/`:

| Versión | Archivo | Propósito |
|---|---|---|
| V1 | `V1__create_favoritos.sql` | Crea la tabla `favoritos`. |
| V2 | `V2__create_listas.sql` | Crea la tabla `listas`. |
| V3 | `V3__add_lista_id_a_favoritos.sql` | Agrega `lista_id` y la clave foránea hacia `listas(id)`. |
| V4 | `V4__lista_id_obligatorio.sql` | Crea la lista por defecto `Sin clasificar`, asigna los favoritos sin lista y establece `lista_id` como obligatorio. |
| V5 | `V5__create_usuarios.sql` | Crea la tabla `usuarios`, con email único, hash de contraseña y rol. |
| V6 | `V6__add_usuario_id_a_listas.sql` | Agrega `usuario_id`, su clave foránea hacia `usuarios(id)` y un índice para búsquedas por propietario. |

La columna `listas.usuario_id` permite inicialmente valores nulos para conservar listas preexistentes que no tenían propietario. Las listas nuevas se crean desde la API con el usuario autenticado como propietario.

**No modificar migraciones que ya fueron aplicadas en una base compartida o productiva.** Para cambios posteriores, crear una nueva migración versionada. Flyway calcula checksums y mantiene el historial de ejecución.

Consultas útiles para revisar el esquema y el historial:

```sql
SELECT version, description, script, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

```sql
SELECT id, nombre, usuario_id
FROM listas;
```

```sql
SELECT id, email, rol
FROM usuarios;
```

## Swagger / OpenAPI

Swagger UI está disponible en:

`http://localhost:8080/swagger-ui/index.html`

El documento OpenAPI en JSON está disponible en:

`http://localhost:8080/v3/api-docs`

Los Controllers documentan las operaciones y sus respuestas HTTP mediante `@Operation`, `@Tag` y anotaciones repetibles `@ApiResponse`. Las respuestas de error utilizan el esquema `ProblemDetail` con media type `application/problem+json`.

Para ejecutar operaciones protegidas desde un cliente HTTP, enviar el header `Authorization: Bearer <accessToken>` obtenido en el login.

## Notas de implementación

- `spring.jpa.hibernate.ddl-auto=validate`: Hibernate valida las entidades contra el esquema, pero no administra las migraciones.
- `spring.jpa.open-in-view=false`: Open EntityManager in View está desactivado.
- `@Transactional` en el movimiento de favoritos permite que el traslado y la eliminación de la lista de origen se ejecuten como una única transacción.
- `ProblemDetail` es el formato utilizado por `GlobalExceptionHandler` para centralizar los errores de la API.
- La autorización por propietario se implementa en `ListaServiceImpl`; el filtro JWT solo autentica y construye el principal.

# Web II — API de productos, favoritos y listas

API REST desarrollada con Spring Boot. Permite consultar productos de DummyJSON, gestionar favoritos y listas, y autenticar usuarios mediante JWT.

## Funcionalidades

- Consulta de productos externos.
- CRUD de favoritos.
- Creación y gestión de listas.
- Registro e inicio de sesión de usuarios.
- Contraseñas protegidas con BCrypt.
- Autenticación mediante JWT.
- Autorización según propietario y rol.
- Migraciones de base de datos con Flyway.
- Documentación con Swagger/OpenAPI.

## Tecnologías

- Java 25 y Spring Boot 4.1.2.
- Spring MVC, Spring Security y Spring Data JPA.
- PostgreSQL 17 y Flyway.
- JJWT 0.13.0 y BCrypt.
- Springdoc OpenAPI 3.1.0.
- Maven Wrapper, JUnit y Testcontainers.

## Requisitos

- JDK 25.
- Docker y Docker Compose.
- Git.

## Ejecución local

### 1. Configurar la base de datos

Crear un archivo `.env` en la raíz del proyecto:

```env
POSTGRES_DB=webii_tp2
POSTGRES_USER=webii_tp2
POSTGRES_PASSWORD=tu_password
```

Iniciar PostgreSQL:

```bash
docker compose up -d
```

Esto crea el contenedor `demo-postgres`, disponible en el puerto `5432`.

Para detenerlo:

```bash
docker compose down
```

> `docker compose down -v` también elimina el volumen y los datos almacenados.

### 2. Configurar variables de entorno

La aplicación necesita las siguientes variables:

| Variable | Descripción |
|---|---|
| `DB_URL` | URL de conexión JDBC |
| `DB_USERNAME` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |
| `JWT_SECRET` | Clave secreta Base64 de al menos 32 bytes |
| `JWT_EXPIRATION_MS` | Tiempo de expiración del token en milisegundos |

Ejemplo para PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/webii_tp2"
$env:DB_USERNAME="webii_tp2"
$env:DB_PASSWORD="tu_password"
$env:JWT_EXPIRATION_MS="3600000"
```

Para generar una clave JWT:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

El archivo `.env` se utiliza para Docker Compose. Spring Boot no lo carga automáticamente, por lo que las variables deben configurarse en el entorno de ejecución o en el IDE.

### 3. Ejecutar la aplicación

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

Flyway ejecuta las migraciones al iniciar la aplicación y Hibernate valida el esquema existente.

## Tests

Ejecutar los tests unitarios:

```bash
./mvnw test
```

Ejecutar la verificación completa:

```bash
./mvnw verify
```

La segunda opción también ejecuta los tests de integración configurados con Testcontainers. Para estos tests es necesario tener Docker en funcionamiento.

## Integración continua (CI)

El proyecto utiliza GitHub Actions mediante el workflow `.github/workflows/ci.yml`.

Ante cada `push`, se ejecuta:

```bash
./mvnw verify
```

Para consultar los resultados: https://github.com/marcos-coloma/web2-tp2/actions

Un resultado exitoso indica que la compilación y las pruebas finalizaron correctamente. Si falla, los logs permiten identificar el paso que produjo el error.

## Endpoints

### Autenticación

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/auth/registro` | Público |
| POST | `/api/auth/login` | Público |

### Productos

| Método | Endpoint | Acceso |
|---|---|---|
| GET | `/api/productos` | Público |
| GET | `/api/productos/{id}` | Público |

Ejemplo de consulta:

```text
GET /api/productos?limit=10&skip=0
```

### Favoritos

Todos los endpoints requieren autenticación.

| Método | Endpoint |
|---|---|
| GET | `/api/favoritos` |
| POST | `/api/favoritos` |
| GET | `/api/favoritos/{id}` |
| PUT | `/api/favoritos/{id}` |
| DELETE | `/api/favoritos/{id}` |

### Listas

Todos los endpoints requieren autenticación.

| Método | Endpoint |
|---|---|
| POST | `/api/listas` |
| GET | `/api/listas` |
| GET | `/api/listas/{id}` |
| GET | `/api/listas/{id}/favoritos` |
| POST | `/api/listas/{origenId}/mover-favoritos` |
| DELETE | `/api/listas/{id}` |

Para acceder a los endpoints protegidos, enviar el token JWT en el encabezado:

```http
Authorization: Bearer <token>
```

## Documentación de la API

Con la aplicación en ejecución:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Despliegue

El despliegue se realizó en Render utilizando una base de datos PostgreSQL y un servicio web construido desde el `Dockerfile` del repositorio.

Pasos realizados:

1. Crear una base de datos PostgreSQL en Render.
2. Crear un Web Service conectado al repositorio de GitHub.
3. Seleccionar el entorno Docker.
4. Configurar las variables de entorno necesarias para la conexión a la base de datos y JWT.
5. Ejecutar el despliegue.
6. Verificar el funcionamiento mediante `/health` y Swagger.

Render proporciona la variable `PORT`, utilizada por la aplicación para definir el puerto de escucha.

## Persistencia y migraciones

La estructura de la base de datos se administra mediante Flyway.

Las migraciones se encuentran en:

```text
src/main/resources/db/migration/
```

Hibernate utiliza `ddl-auto=validate`, por lo que valida el esquema sin modificarlo automáticamente.

## Arquitectura

```text
src/main/java/com/example/demo/
├── config/       # Configuración general y seguridad
├── controller/   # Endpoints REST
├── dto/          # Objetos de transferencia
├── entity/       # Entidades JPA
├── exception/    # Manejo de excepciones
├── repository/  # Acceso a datos
├── security/    # JWT y autenticación
└── service/      # Lógica de negocio
```

## Justificación de la ubicación de la lógica de autorización

La lógica de autorización se encuentra principalmente en la **capa de servicios**, particularmente en los servicios que gestionan listas, como `ListaServiceImpl`.

Esta capa verifica si el usuario autenticado tiene permiso para realizar una operación sobre un recurso. Por ejemplo, comprueba que una lista pertenezca al usuario que intenta modificarla y contempla el rol `ADMIN`, que puede acceder a recursos de otros usuarios.

Se eligió ubicar esta lógica en los servicios y no en los controladores porque la autorización forma parte de las reglas de negocio. De esta manera, las restricciones se mantienen centralizadas y pueden aplicarse independientemente del endpoint desde el que se invoque la operación.

El controlador se encarga de recibir las solicitudes HTTP, validar los datos de entrada y delegar las operaciones al servicio. No debería contener reglas de autorización específicas del negocio.

Por otro lado, Spring Security y el filtro JWT se encargan de verificar la autenticidad del token y establecer la identidad del usuario autenticado. Las anotaciones de seguridad, como `@SecurityRequirement`, documentan los requisitos de autenticación en Swagger, pero no reemplazan las comprobaciones de autorización realizadas por los servicios.
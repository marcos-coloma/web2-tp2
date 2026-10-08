# TP2 · Persistencia, migraciones y arquitectura hexagonal

Proyecto práctico de Spring Boot para trabajar con persistencia en PostgreSQL, JPA, Flyway, arquitectura por capas y puertos y adapters. Parte del TP1, que incluía un catálogo de productos consumido desde DummyJSON y un CRUD de favoritos en memoria.

## Cómo levantar el proyecto

Requiere Java 25. Usar siempre el wrapper de Maven, no un `mvn` instalado aparte.

### 1. Base de datos

**Opción A — Docker (recomendada)**

```bash
docker compose up -d
```

Levanta PostgreSQL 17 con la base `webii_tp2` y el usuario `webii_tp2`, utilizando el volumen `demo-postgres-data` para conservar los datos entre reinicios del contenedor.

**Opción B — PostgreSQL local**

Instalá PostgreSQL y creá el usuario y la base de datos. Por ejemplo, desde pgAdmin o una sesión de PostgreSQL con permisos suficientes:

```sql
CREATE ROLE webii_tp2 WITH LOGIN PASSWORD 'webii_tp2';
CREATE DATABASE webii_tp2 OWNER webii_tp2;
```

Si el rol o la base ya existen, no hace falta crearlos nuevamente.

Los datos de conexión están en `src/main/resources/application.properties`, en las propiedades `spring.datasource.*`.

### 2. Levantar la aplicación

Desde la raíz del proyecto:

**Windows (PowerShell)**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS/Linux**

```bash
./mvnw spring-boot:run
```

Al iniciar la aplicación, Flyway valida y ejecuta las migraciones pendientes. Si la base y el esquema son compatibles con las entidades JPA, Spring Boot debería iniciar sin errores.

La API queda disponible en `http://localhost:8080`.

Para ejecutar los tests:

```bash
./mvnw test
```

En Windows también se puede usar `.\mvnw.cmd test`.

### 3. Verificar las migraciones

Se puede comprobar el historial de Flyway desde PostgreSQL:

```sql
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

Las migraciones aplicadas deben figurar con `success = true`.

También se puede comprobar que no haya favoritos sin lista:

```sql
SELECT COUNT(*)
FROM favoritos
WHERE lista_id IS NULL;
```

El resultado esperado es `0`.

## Endpoints disponibles

### Productos

| Método | Path | Descripción |
|---|---|---|
| GET | `/health` | Chequeo de salud |
| GET | `/ping` | Devuelve `pong` |
| GET | `/api/productos?limit=&skip=` | Consulta el catálogo de DummyJSON |
| GET | `/api/productos/{id}` | Obtiene un producto por ID; devuelve 404 si no existe |

El catálogo de productos es de solo lectura y consume una API externa.

### Favoritos

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/favoritos` | Lista los favoritos |
| POST | `/api/favoritos` | Crea un favorito |
| GET | `/api/favoritos/{id}` | Obtiene un favorito por ID |
| PUT | `/api/favoritos/{id}` | Actualiza un favorito |
| DELETE | `/api/favoritos/{id}` | Elimina un favorito |

Los favoritos se guardan en PostgreSQL mediante JPA. Para crear o actualizar un favorito, el request incluye `productoId`, `listaId` y `nota`.

### Listas de favoritos

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/listas` | Crea una lista |
| POST | `/api/listas/{origenId}/mover-favoritos` | Mueve los favoritos a otra lista y elimina la lista de origen |
| GET | `/api/listas` | Lista todas las listas |
| GET | `/api/listas/{id}` | Obtiene una lista por ID |
| GET | `/api/listas/{id}/favoritos` | Obtiene los favoritos de una lista |
| DELETE | `/api/listas/{id}` | Elimina una lista vacía |

Las listas y los favoritos se persisten en PostgreSQL. No se permite eliminar una lista que todavía tenga favoritos: la API responde `409 Conflict`. Los recursos inexistentes responden `404 Not Found`.

### Swagger / OpenAPI

La interfaz interactiva está disponible en:

`http://localhost:8080/swagger-ui/index.html`

El documento OpenAPI en formato JSON se encuentra en:

`http://localhost:8080/v3/api-docs`

## Estructura del proyecto

```text
com.example.demo
├── controller/
│   ├── ProductoController
│   ├── FavoritoController
│   └── ListaController
├── service/
│   ├── ProductoService / ProductoServiceImpl
│   ├── FavoritoService / FavoritoServiceImpl
│   └── ListaService / ListaServiceImpl
├── repository/
│   ├── FavoritoRepository
│   ├── FavoritoJpaRepository
│   ├── FavoritoRepositoryAdapter
│   ├── ListaRepository
│   ├── ListaJpaRepository
│   └── ListaRepositoryAdapter
├── entity/
│   ├── FavoritoEntity
│   └── ListaEntity
├── domain/
│   ├── Favorito
│   └── Lista
├── dto/
│   ├── producto/
│   ├── favorito/
│   └── lista/
├── client/
│   └── dummyjson/
├── exception/
│   ├── RecursoNoEncontradoException
│   ├── ServicioExternoException
│   ├── ConflictoRecursoException
│   └── GlobalExceptionHandler
└── config/
    ├── RestClientConfig
    └── OpenApiConfig
```

Los controllers gestionan HTTP, los services contienen la lógica de negocio y los repositorios abstraen el acceso a los datos. Los adapters traducen entre los modelos de dominio y las entidades JPA.

`Favorito` y `Lista` son modelos de dominio; `FavoritoEntity` y `ListaEntity` representan las tablas de la base de datos.

## Persistencia y configuración JPA

El proyecto utiliza Spring Data JPA y Hibernate para acceder a PostgreSQL.

En `application.properties`:

- `spring.jpa.hibernate.ddl-auto=validate`: Hibernate verifica que las entidades sean compatibles con el esquema existente, pero no crea ni modifica las tablas.
- `spring.jpa.open-in-view=false`: desactiva Open EntityManager in View.

Los cambios en el esquema se gestionan mediante Flyway, no mediante actualizaciones automáticas de Hibernate.

## Migraciones de Flyway

Las migraciones están en:

`src/main/resources/db/migration/`

| Migración | Propósito |
|---|---|
| `V1__create_favoritos.sql` | Crea la tabla `favoritos` |
| `V2__create_listas.sql` | Crea la tabla `listas` |
| `V3__add_lista_id_a_favoritos.sql` | Agrega `lista_id` y la clave foránea hacia `listas(id)` |
| `V4__lista_id_obligatorio.sql` | Asigna los favoritos sin lista a una lista por defecto y establece `lista_id` como obligatorio |

Cada migración versionada se ejecuta una sola vez y queda registrada en `flyway_schema_history`.

Las migraciones que ya fueron aplicadas no deben modificarse. Si el esquema necesita evolucionar, se crea una nueva migración para conservar el historial y permitir que otras bases de datos apliquen los mismos cambios.

### Evolución del esquema

La migración V3 permite inicialmente que `lista_id` sea nulo, porque pueden existir favoritos creados antes de incorporar las listas.

La migración V4 crea la lista por defecto `Sin clasificar` si no existe, asigna a ella los favoritos que todavía tienen `lista_id` nulo y recién después establece la restricción `NOT NULL`.

Esto permite adaptar los datos existentes antes de imponer la nueva condición del esquema.

## Migración de memoria a JPA

### Qué se mantuvo

- `FavoritoService` y su implementación.
- `FavoritoController`.
- `FavoritoRepository`, como contrato de acceso a datos.

El service depende de la interfaz `FavoritoRepository`, no de una implementación concreta de persistencia.

### Qué cambió

- Se eliminó `InMemoryFavoritoRepository`.
- Se creó `FavoritoEntity` para representar la tabla `favoritos`.
- Se creó `FavoritoJpaRepository`, basado en `JpaRepository`.
- Se creó `FavoritoRepositoryAdapter`, que implementa el puerto `FavoritoRepository` y traduce entre `Favorito` y `FavoritoEntity`.

La persistencia pudo cambiar de memoria a PostgreSQL sin que el controller tuviera que conocer los detalles de JPA.

## Relación entre listas y favoritos

Una lista puede contener varios favoritos, mientras que cada favorito referencia una lista mediante `listaId`.

En JPA, esta relación se representa con `@ManyToOne` en `FavoritoEntity`, utilizando la columna `lista_id` como clave foránea.

Para consultar los favoritos de una lista se utiliza una consulta derivada del repositorio JPA, evitando mantener una colección bidireccional `@OneToMany` en `ListaEntity`.


### ¿Por qué usamos `@Transactional`?

El método `moverFavoritos` utiliza `@Transactional` para que todas las modificaciones de la operación se realicen como una única transacción.

Esto se relaciona con la **atomicidad**, una de las propiedades ACID: todas las operaciones deben completarse correctamente o, si ocurre un error que provoca rollback, los cambios deben revertirse.

Si quitáramos `@Transactional` y fallara una escritura después de que otra ya se hubiera confirmado, la base de datos podría quedar en un estado inconsistente. Por ejemplo, algunos favoritos podrían haberse movido a la lista destino, pero la lista origen podría no haberse eliminado. También podrían quedar solamente algunos favoritos trasladados si fallara una actualización a mitad del proceso.

Con `@Transactional`, si ocurre un error que provoca rollback, se revierten las modificaciones realizadas dentro de la transacción, evitando que quede aplicada solamente una parte de la operación.



## Dependencias principales

- `spring-boot-starter-webmvc`: endpoints REST.
- `spring-boot-starter-validation`: validación de requests.
- `springdoc-openapi-starter-webmvc-ui`: Swagger / OpenAPI.
- `spring-boot-starter-data-jpa`: Spring Data JPA e integración con Hibernate.
- `postgresql`: driver JDBC de PostgreSQL.
- `spring-boot-starter-flyway` y `flyway-database-postgresql`: integración y migraciones de Flyway.






# Marketplace de Alojamientos — Inversiones LR

Proyecto de curso de Ingeniería de Software 1 (Universidad El Bosque).
Marketplace web de alojamientos temporales con arquitectura orientada a servicios.

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3.4, Maven (multi-módulo) |
| Base de datos | MySQL 8.4 (un esquema por servicio), migraciones con Flyway |
| Frontend | Angular 20 |
| Seguridad | Spring Security + JWT |
| CI | GitHub Actions + SonarCloud |

## Servicios

| Servicio | Puerto | Esquema | Historias |
|---|---|---|---|
| `identity-service` | 8085 | `identity_db` | HU-01 a HU-06, HU-23 |
| `catalog-service` | 8082 | `catalog_db` | HU-07 a HU-15 |
| `booking-service` | 8083 | `booking_db` | HU-16 a HU-20 |
| `backoffice-service` | 8084 | `backoffice_db` | HU-21, HU-22, HU-24 a HU-27 |

Los cuatro servicios contienen funcionalidades de dominio para identidad, catálogo, reservas y backoffice. Registro, login, recuperación, perfil y administración de usuarios consumen Identity; búsqueda, publicación, disponibilidad y moderación consumen Catalog; cotización, pago simulado, reservas, métricas y reseñas verificadas usan Booking con JWT. Auditoría se consulta desde Backoffice. El checkout usa exclusivamente datos de catálogo y reservas del backend; la tarjeta aceptada es un dato de prueba y no produce cobros reales.

### Pago de prueba

El simulador acepta cualquier número ficticio de 16 dígitos, una fecha de vencimiento futura en formato `MM/AA` y un CVV de tres dígitos. El backend persiste el monto, resultado, referencia, marca y últimos cuatro dígitos; no almacena el número completo, CVV ni titular. No se conecta a una pasarela ni se mueve dinero real. No ingreses datos reales de pago.

## Cómo correrlo

Requisitos: JDK 21, Maven 3.9+, Node 22 y Docker.

```bash
# 1. Base de datos
docker compose up -d

# 2. Empaqueta el backend una vez
mvn package

# 3. Ejecuta cada JAR en su propia terminal
java -jar services/identity-service/target/identity-service-0.1.0-SNAPSHOT-exec.jar
java -jar services/catalog-service/target/catalog-service-0.1.0-SNAPSHOT.jar
java -jar services/booking-service/target/booking-service-0.1.0-SNAPSHOT-exec.jar
java -jar services/backoffice-service/target/backoffice-service-0.1.0-SNAPSHOT.jar

# 4. Frontend (el proxy local reenvía las rutas a los servicios)
cd frontend && npm install && npm start
```

Pruebas:

```bash
mvn verify                                   # todos los servicios + cobertura JaCoCo
cd frontend && npm test -- --watch=false --browsers=ChromeHeadless
```

Probar el registro y el login:

```bash
curl -X POST localhost:8085/api/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"ana@example.com","password":"Clave12345","fullName":"Ana Pérez","role":"GUEST"}'

curl -X POST localhost:8085/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"ana@example.com","password":"Clave12345"}'
```

Para crear una cuenta administradora local, inicia Identity con credenciales de bootstrap elegidas por ti (PowerShell):

```powershell
$env:ADMIN_BOOTSTRAP_EMAIL = "admin@travesia.test"
$env:ADMIN_BOOTSTRAP_PASSWORD = "TravesiaAdmin123!"
java -jar services/identity-service/target/identity-service-0.1.0-SNAPSHOT.jar
```

La contraseña debe tener al menos 12 caracteres. El servicio crea el administrador solo si ese correo aún no existe; el registro público nunca permite elegir ADMIN. Estas credenciales son de desarrollo local: cámbialas antes de compartir o desplegar el proyecto. Para crear anfitrión, usa `/registro-anfitrion` en la aplicación.

Las reservas confirmadas pasan a `COMPLETADA` al llegar la fecha de salida, cuando el huésped consulta sus reservas. Entonces puede enviar una calificación de 1 a 5 estrellas y un comentario opcional; cada reserva admite una sola reseña.

## Variables de entorno

| Variable | Uso | Valor por defecto (solo desarrollo) |
|---|---|---|
| `DB_URL` | URL JDBC de Identity | `jdbc:mysql://localhost:3307/identity_db` |
| `CATALOG_DB_URL`, `BOOKING_DB_URL`, `BACKOFFICE_DB_URL` | URL JDBC de cada servicio | `jdbc:mysql://localhost:3307/<servicio>_db` |
| `DB_USER` / `DB_PASSWORD` | Credenciales de MySQL | `app` / `app` |
| `JWT_SECRET` | Clave para firmar tokens (mínimo 32 caracteres) | valor de desarrollo |
| `ADMIN_BOOTSTRAP_EMAIL` / `ADMIN_BOOTSTRAP_PASSWORD` | Crea o sincroniza el administrador de desarrollo al iniciar Identity | sin valor; deshabilitado |

Docker publica MySQL en el puerto local `3307` y el motor escucha en el puerto interno `3306`. Cada servicio usa la misma clave JWT de desarrollo por defecto para validar tokens entre servicios; configura una clave compartida propia fuera de desarrollo.

## Flujo de trabajo

Ver [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) y la matriz en [docs/trazabilidad.md](docs/trazabilidad.md).

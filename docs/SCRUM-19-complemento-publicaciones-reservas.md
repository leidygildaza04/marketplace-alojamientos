# SCRUM-19 — Complemento de publicaciones y reservas

## Decisiones de implementación

Se respeta el modelo entidad-relación del proyecto: `Alojamiento` pertenece a `catalog-service`/`catalog_db` y `Reserva` a `booking-service`/`booking_db`. No se comparten tablas con `identity-service`.

- Antes de desactivar, identity consulta `GET /internal/accounts/{userId}/future-confirmed-bookings` en booking-service. Si `count > 0`, responde HTTP 409 (`FUTURE_BOOKINGS_PENDING`) y no desactiva la cuenta.
- Si no hay reservas futuras confirmadas, identity invoca `POST /internal/accounts/{userId}/pause-listings` en catalog-service. Las publicaciones `ACTIVA` del anfitrión pasan a `PAUSADA`.
- Si uno de los servicios no responde, la operación se detiene con HTTP 503 y no desactiva la cuenta.

## Base de datos

Crear las bases `catalog_db` y `booking_db` y otorgar al usuario de aplicación permisos de creación/lectura/escritura. Variables opcionales: `CATALOG_DB_URL`, `CATALOG_DB_USER`, `CATALOG_DB_PASSWORD`, `BOOKING_DB_URL`, `BOOKING_DB_USER`, `BOOKING_DB_PASSWORD`, `CATALOG_SERVICE_URL`, `BOOKING_SERVICE_URL`.

Las migraciones Flyway crean tablas mínimas iniciales según el ER. En esta base del repositorio los servicios estaban vacíos; antes de integrar otros módulos se deben ampliar estas tablas con los atributos de los flujos de alojamiento y reserva existentes.

## Importante

Los endpoints `/internal/**` son internos y no deben exponerse públicamente sin autenticación servicio-a-servicio o restricción de red. La llamada HTTP y la actualización de identity no forman una transacción distribuida; para producción se recomienda patrón Saga/outbox y reintentos idempotentes.

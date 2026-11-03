# SCRUM-26 — HU-13: Bloqueo automático de periodos reservados

## Resumen
Se incorpora un flujo de confirmación/cancelación de reservas coordinado entre `booking-service` y `catalog-service`.

## Criterios de aceptación
- **CA-13.1:** Booking registra primero una reserva `PENDIENTE`, Catalog reclama atómicamente el periodo y Booking cambia a `CONFIRMADA` después de recibir el bloqueo.
- **CA-13.2:** Catalog ejecuta una actualización condicional `DISPONIBLE -> RESERVADO` dentro de una transacción local. Solo la primera solicitud que actualiza la fila gana; las demás reciben HTTP 409.
- **CA-13.3:** Al cancelar, Booking pasa a `CANCELACION_PENDIENTE`, Catalog libera solo el periodo cuyo `id_reserva` coincide y Booking termina en `CANCELADA`.

## Arquitectura y consistencia
Los servicios utilizan bases de datos independientes (`booking_db` y `catalog_db`). Por lo tanto, no es correcto afirmar que una transacción ACID distribuida única abarca ambas bases. La implementación usa una saga síncrona con compensación: si Catalog bloquea y Booking no puede finalizar la confirmación, Booking intenta liberar el periodo; si falla la liberación durante cancelación, mantiene `CANCELACION_PENDIENTE` y el periodo continúa bloqueado (fail-closed).

La operación atómica en Catalog se realiza mediante un `UPDATE ... WHERE estado = DISPONIBLE` y guarda `id_reserva`; el índice único evita asociar accidentalmente una misma reserva a más de un periodo. La liberación filtra por `id_reserva`, de modo que una cancelación repetida o tardía no libera el periodo de otra reserva.

## Endpoints añadidos
- `POST /api/v1/reservas` — crear y confirmar una reserva (respuesta 201).
- `POST /api/v1/reservas/{reservaId}/cancelar` — cancelar una reserva.
- `POST /internal/alojamientos/{alojamientoId}/periodos-disponibilidad/reservar?reservaId=&fechaInicio=&fechaFin=` — reclamar un periodo; responde 409 cuando ya no está disponible.
- `POST /internal/alojamientos/{alojamientoId}/periodos-disponibilidad/liberar?reservaId=` — liberar el periodo asociado a esa reserva.

## Configuración
- Booking llama a Catalog mediante `services.catalog.base-url`; valor por defecto `http://localhost:8082`.
- Catalog es llamado por Booking en `http://localhost:8082`.
- Aplicar Flyway `V3__vincular_periodo_reserva.sql` en `catalog_db`.

## Cómo probar
```powershell
& "C:pache-maven-3.10.0in\mvn.cmd" -pl services/booking-service -am clean verify
& "C:pache-maven-3.10.0in\mvn.cmd" -pl services/catalog-service -am clean verify
```

## Alcance y limitaciones
El endpoint `POST /api/v1/reservas` introduce el flujo inicial de confirmación porque el módulo revisado no tenía controlador de creación/cancelación. La autorización/autenticación del usuario debe integrarse con el mecanismo de seguridad del proyecto cuando corresponda. La saga cubre fallos síncronos y compensación; para tolerar caídas prolongadas entre los dos servicios se recomienda complementar con outbox/reintentos persistentes.

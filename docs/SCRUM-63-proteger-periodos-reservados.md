# SCRUM-63 — Proteger periodos de disponibilidad asociados a reservas

## Qué cambia

- Se incorpora `alojamientoId` a la entidad `Reserva`, mapeado al campo existente `id_alojamiento` de la tabla `reservas`.
- `booking-service` expone un endpoint interno que informa si un alojamiento tiene una reserva `CONFIRMADA` que coincide con un rango de fechas.
- `catalog-service` consulta ese endpoint antes de editar o eliminar un periodo de disponibilidad.
- Si existe una reserva confirmada, la operación se rechaza con HTTP 409 Conflict.
- Si `booking-service` no está disponible, la operación se rechaza con HTTP 503 Service Unavailable (protección ante fallos: no se modifica ni elimina el periodo sin verificar las reservas).
- Al editar, se comprueba tanto el rango actual del periodo como el rango propuesto.

## Endpoint interno

`GET /internal/alojamientos/{alojamientoId}/reservas-confirmadas/solapamiento?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`

Devuelve `true` cuando existe una reserva confirmada del alojamiento cuyas fechas se cruzan con el rango consultado; en caso contrario devuelve `false`.

## Criterios de aceptación cubiertos por la implementación

- [x] Identificar reservas por alojamiento mediante `id_alojamiento`.
- [x] Considerar únicamente reservas con estado `CONFIRMADA`.
- [x] Evitar editar periodos asociados a reservas confirmadas.
- [x] Evitar eliminar periodos asociados a reservas confirmadas.
- [x] Rechazar operaciones protegidas con HTTP 409.
- [x] Fallar de forma segura con HTTP 503 cuando no se pueda consultar Booking.
- [x] Mantener intacta la validación de fechas y superposiciones implementada en SCRUM-62.
- [x] Añadir pruebas para la consulta de reservas y la protección de edición/eliminación.

## Cómo se probó

Pendiente de ejecutar en el repositorio local:

```powershell
& "C:\apache-maven-3.10.0\bin\mvn.cmd" -pl services/booking-service -am clean verify
& "C:\apache-maven-3.10.0\bin\mvn.cmd" -pl services/catalog-service -am clean verify
```

## Configuración

Catalog utiliza `BOOKING_SERVICE_URL` para indicar la URL base de `booking-service`. Si no se define, usa `http://localhost:8083`.

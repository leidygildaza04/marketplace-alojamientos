# SCRUM-61 — Operaciones de creación, edición y eliminación de periodos

## Historia de Jira

Implementar las operaciones para administrar periodos de disponibilidad asociados a un alojamiento.

## Qué cambia

- Se incorpora la entidad `PeriodoDisponibilidad`, relacionada con `Alojamiento`.
- Se agrega la migración `V2__create_periodos_disponibilidad.sql`.
- Se implementan endpoints para listar, crear, editar y eliminar periodos.
- Los periodos nuevos se crean con estado `DISPONIBLE`.
- Se documentan las operaciones en `docs/api/openapi/disponibilidad.yaml`.

## Endpoints

- `GET /api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad`
- `POST /api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad`
- `PUT /api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad/{periodoId}`
- `DELETE /api/v1/alojamientos/{alojamientoId}/periodos-disponibilidad/{periodoId}`

El cuerpo de creación y edición contiene `fechaInicio` y `fechaFin` en formato ISO `yyyy-MM-dd`.

## Criterios cubiertos

- Crear periodos asociados a un alojamiento existente.
- Consultar los periodos de un alojamiento.
- Editar las fechas de un periodo perteneciente al alojamiento indicado.
- Eliminar un periodo perteneciente al alojamiento indicado.
- Responder `404` cuando no existe el alojamiento o el periodo solicitado.

## Fuera del alcance de esta historia

- Validación de fechas invertidas y detección de periodos superpuestos: SCRUM-62.
- Bloqueo de edición/eliminación de periodos reservados: SCRUM-63.
- Bloqueo automático al confirmar/cancelar reservas: HU-13 / SCRUM-26.

## Cómo se probó

Se agregó `PeriodoDisponibilidadCrudTest` con escenarios de creación, consulta, edición, eliminación y alojamiento inexistente. La ejecución de Maven debe confirmarse en el entorno del proyecto antes de integrar estos cambios.

# SCRUM-62 — Validar fechas y detectar periodos superpuestos

## Historia de Jira

Validar las fechas de los periodos de disponibilidad y evitar cruces entre periodos del mismo alojamiento.

## Qué cambia

- Se exige que las fechas de inicio y fin estén presentes.
- Se valida que la fecha de inicio sea estrictamente anterior a la fecha de fin.
- Se detectan intersecciones con otros periodos asociados al mismo alojamiento al crear o editar.
- Se excluye el propio periodo de la búsqueda de conflictos durante una edición.
- Se responde `400 Bad Request` para fechas ausentes o inválidas y `409 Conflict` para periodos superpuestos.

## Criterios cubiertos

- Rechazar fechas nulas.
- Rechazar fechas iguales o con inicio posterior al fin.
- Rechazar la creación de periodos que se superponen con otro del mismo alojamiento.
- Rechazar la edición que genere una superposición.
- Permitir periodos sin intersección y no comparar periodos de alojamientos distintos.

## Cómo se probó

Se amplía `PeriodoDisponibilidadCrudTest` con escenarios de fechas inválidas, superposición al crear y editar, y periodos no superpuestos. Ejecutar `mvn -pl services/catalog-service -am clean verify` y registrar el resultado antes de integrar.

## Fuera del alcance

El bloqueo de modificación o eliminación de periodos reservados se aborda en SCRUM-63. La coordinación transaccional de reservas corresponde a HU-13 / SCRUM-26.

# Contratos OpenAPI iniciales (SCRUM-48)

## Propósito

Este documento reúne los borradores OpenAPI 3.0.3 de los servicios de Reservas, Disponibilidad y Cotización para servir como base de desarrollo del Sprint 1. Los contratos son una propuesta inicial y deben revisarse con los criterios de aceptación de Jira antes de considerarse definitivos.

## Archivos

| Servicio | Contrato | Operaciones principales |
|---|---|---|
| Reservas | [`reservas.yaml`](../api/openapi/reservas.yaml) | Crear reserva, listar reservas y consultar detalle |
| Disponibilidad | [`disponibilidad.yaml`](../api/openapi/disponibilidad.yaml) | Consultar disponibilidad por alojamiento, fechas y huéspedes |
| Cotización | [`cotizacion.yaml`](../api/openapi/cotizacion.yaml) | Crear cotización y consultar su vigencia |

## Convenciones

- Base de contrato: OpenAPI 3.0.3 y JSON como formato de intercambio.
- Fechas de estadía usan `date` (ISO 8601, `YYYY-MM-DD`); marcas de tiempo usan `date-time`.
- Identificadores de recursos se expresan como UUID en esta propuesta. Deben alinearse con los identificadores reales del modelo y la implementación antes de integrar.
- Las operaciones de Reservas y Cotización requieren bearer JWT como propuesta de seguridad; Disponibilidad se define como consulta pública en este borrador.
- Errores usan un objeto común con `codigo` y `mensaje`, con detalles opcionales donde aplique.
- Los puertos y prefijos `/api/v1` son referencias de diseño, no una afirmación de que las rutas ya estén implementadas.

## Reglas de negocio reflejadas

1. La fecha de fin debe ser posterior a la fecha de inicio.
2. La cantidad de huéspedes debe ser positiva.
3. La reserva se crea a partir de una cotización vigente y la disponibilidad debe revalidarse al confirmar para evitar sobreventa.
4. Una cotización debe indicar total, moneda y fecha de expiración.
5. Las respuestas distinguen errores de solicitud (`400`), autenticación (`401`), recurso inexistente (`404`) y conflicto de disponibilidad o vigencia (`409`).

## Flujo propuesto

1. El cliente consulta disponibilidad para un alojamiento y un rango de fechas.
2. Solicita una cotización con alojamiento, fechas y huéspedes.
3. Consulta el total y la vigencia de la cotización.
4. Envía el identificador de cotización para crear la reserva.
5. El servicio de Reservas revalida vigencia y disponibilidad antes de confirmar.

## Validación

Los archivos se revisaron por estructura y coherencia documental. Antes de publicar como contratos definitivos, validar sintaxis con un validador OpenAPI, confirmar rutas, puertos, autenticación, identificadores y campos con el equipo, y contrastar los endpoints con la implementación existente.

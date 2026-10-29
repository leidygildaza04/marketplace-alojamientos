# 7. Diseño de servicios

Página índice para el diseño técnico de la solución. Las subpáginas documentan la arquitectura propuesta y su correspondencia con el código actual.

## Subpáginas

| Subpágina | Qué contiene | Tarea |
|---|---|---|
| [Arquitectura (C4)](07-1-arquitectura-c4.md) | Contexto, contenedores, doce servicios lógicos, responsabilidades, flujos, trazabilidad y decisiones por confirmar | SCRUM-46 |
| [Modelo de datos](07-2-modelo-entidad-relacion.md) | Entidades, atributos, cardinalidades, restricciones, propiedad de datos y decisiones pendientes | SCRUM-47 |
| Contratos de API | Contratos OpenAPI de cada servicio y colección de pruebas | SCRUM-48 |
| Secuencias | Diagramas de secuencia de los flujos relevantes (por ejemplo, reserva y cancelación) | Por asignar |
| Despliegue | Cómo se despliega la solución | Por asignar |

## Criterio de lectura

La arquitectura C4 distingue entre **servicios lógicos de negocio** y **contenedores desplegables**. En la versión actual, el backend se agrupa en cuatro módulos Spring Boot (`identity-service`, `catalog-service`, `booking-service` y `backoffice-service`). La documentación de SCRUM-46 descompone las responsabilidades en doce servicios lógicos sin afirmar que todos estén desplegados como aplicaciones independientes.

## Servicios desplegables actuales

| Contenedor | Responsabilidad agrupada | Historias según documentación actual |
|---|---|---|
| `identity-service` | Identidad y perfiles | HU-01 a HU-06 y HU-23 |
| `catalog-service` | Alojamientos, búsqueda y disponibilidad | HU-07 a HU-15 |
| `booking-service` | Cotización, reservas, cancelaciones y notificaciones propuestas | HU-16 a HU-20 |
| `backoffice-service` | Reseñas, administración, reportería y auditoría propuestas | HU-21, HU-22 y HU-24 a HU-27 |

Las responsabilidades agrupadas son una asignación inicial de diseño. Los contratos entre servicios, la propiedad definitiva de datos y la trazabilidad detallada de cada historia se precisarán en los Scrums correspondientes y se validarán con el equipo.

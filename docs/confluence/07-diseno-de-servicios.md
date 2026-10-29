# 7. Diseño de servicios

Índice de la documentación técnica del Marketplace de Alojamientos. Las subpáginas conectan la arquitectura lógica con el código y las convenciones de implementación.

## Subpáginas

| Subpágina | Qué contiene | Tarea |
|---|---|---|
| [Arquitectura (C4)](07-1-arquitectura-c4.md) | Contexto, contenedores, doce servicios lógicos, responsabilidades, flujos y decisiones pendientes | SCRUM-46 |
| [Modelo de datos](07-2-modelo-entidad-relacion.md) | Entidades, atributos, cardinalidades, restricciones y propiedad de datos | SCRUM-47 |
| [Contratos OpenAPI](07-3-contratos-openapi.md) | Contratos iniciales de Reservas, Disponibilidad y Cotización | SCRUM-48 |
| [Stack técnico y estructura del repositorio](07-4-stack-tecnico-estructura-repositorio.md) | Tecnologías, estructura, ramas, commits y flujo de contribución | SCRUM-49 |
| Secuencias | Diagramas de secuencia de los flujos relevantes, como reserva y cancelación | Por asignar |
| Despliegue | Procedimiento y arquitectura de despliegue | Por asignar |

## Criterio de lectura

La arquitectura C4 distingue entre **servicios lógicos de negocio** y **contenedores desplegables**. El backend actual se agrupa en cuatro módulos Spring Boot: `identity-service`, `catalog-service`, `booking-service` y `backoffice-service`. La documentación describe doce servicios lógicos sin afirmar que todos estén desplegados como aplicaciones independientes.

El stack y la estructura documentados en SCRUM-49 reflejan la configuración del repositorio y deben actualizarse cuando el equipo apruebe cambios tecnológicos.

## Servicios desplegables actuales

| Contenedor | Responsabilidad agrupada | Historias según documentación actual |
|---|---|---|
| `identity-service` | Identidad y perfiles | HU-01 a HU-06 y HU-23 |
| `catalog-service` | Alojamientos, búsqueda y disponibilidad | HU-07 a HU-15 |
| `booking-service` | Cotización, reservas, cancelaciones y notificaciones propuestas | HU-16 a HU-20 |
| `backoffice-service` | Reseñas, administración, reportería y auditoría propuestas | HU-21, HU-22 y HU-24 a HU-27 |

Las responsabilidades son una asignación inicial de diseño. Los contratos, los límites de datos y la trazabilidad detallada deben validarse con el equipo.

## Correspondencia entre servicios lógicos y módulos

| Servicio lógico | Módulo desplegable propuesto | Procesos |
|---|---|---|
| Identidad y Perfil | `identity-service` | P-01 |
| Alojamientos | `catalog-service` | P-02 |
| Búsqueda | `catalog-service` | P-03 |
| Disponibilidad | `catalog-service` | P-03, P-04 |
| Cotización | `booking-service` | P-03 |
| Reservas | `booking-service` | P-03 |
| Cancelaciones | `booking-service` | P-04 |
| Notificaciones | `booking-service` | Flujos de reservas y cancelaciones |
| Administración | `backoffice-service` | P-05 |
| Auditoría | `backoffice-service` | P-05 |
| Reseñas | `backoffice-service` | P-05 |
| Reportería | `backoffice-service` | P-05 |

## Agrupación desplegable actual

| Módulo desplegable | Servicios lógicos agrupados | Datos principales |
|---|---|---|
| `identity-service` | Identidad y perfil | Usuarios, roles y perfiles |
| `catalog-service` | Alojamientos, Disponibilidad y Búsqueda | Publicaciones, servicios, imágenes y periodos disponibles |
| `booking-service` | Cotización, Reservas, Cancelaciones y Notificaciones | Cotizaciones, reservas, cancelaciones, pagos simulados y avisos |
| `backoffice-service` | Reseñas, Administración, Auditoría y Reportería | Reseñas, eventos de auditoría, moderación y reportes |

Esta agrupación representa las responsabilidades previstas para el MVP. La propiedad de datos, los límites de cada servicio y los diagramas C4 se detallan en las subpáginas correspondientes.

## Contratos OpenAPI iniciales

Consulta [Contratos OpenAPI iniciales (SCRUM-48)](07-3-contratos-openapi.md), donde se documentan los borradores de Reservas, Disponibilidad y Cotización.

Los contratos deben validarse formalmente y contrastarse con los endpoints reales antes de considerarse definitivos.

## Dependencias externas

La integración con Stripe se contempla en modo de prueba desde `booking-service`, según el diseño propuesto. La configuración definitiva y los contratos de integración deben validarse con la implementación existente.

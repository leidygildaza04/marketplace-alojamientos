# 7. Diseño de servicios

> Página nueva (SCRUM-41). Cada subpágina se llena en su tarea de Jira.

## Subpáginas

| Subpágina | Qué contiene | Tarea |
|---|---|---|
| Arquitectura (C4) | [Contexto, contenedores, 12 servicios lógicos, responsabilidades e interacciones](07-1-arquitectura-c4.md) | SCRUM-46 |
| Modelo de datos | Modelo entidad-relación y la responsabilidad de cada servicio sobre sus datos | SCRUM-47 |
| Contratos de API | Contratos OpenAPI de cada servicio y colección de pruebas | SCRUM-48 |
| Secuencias | Diagramas de secuencia de los flujos relevantes (por ejemplo reserva y cancelación) | Por asignar |
| Despliegue | Cómo se despliega la solución | Por asignar |

## Qué debe quedar documentado

- Descomposición de la solución en servicios con responsabilidades claras.
- Relación entre cada servicio, los requerimientos, procesos e historias que atiende.
- Interacciones y dependencias entre servicios en los flujos relevantes.
- Mecanismos de integración con servicios externos, como el proveedor de pagos de prueba.
- Correspondencia entre el diseño documentado y lo que realmente se implementó.

## Servicios que aparecen en los procesos de negocio

Los procesos de negocio se descomponen en 12 servicios lógicos, documentados en [Arquitectura (C4)](07-1-arquitectura-c4.md). El código base del repositorio los agrupa actualmente en cuatro servicios desplegables. La vista lógica no implica que los 12 servicios sean aplicaciones independientes.

| Servicio en los procesos | Servicio en el código base (propuesta) | Procesos donde aparece |
|---|---|---|
| Servicio de Identidad y Perfil | identity-service | P-01 |
| Servicio de Alojamientos | catalog-service | P-02 |
| Servicio de Búsqueda | catalog-service | P-03 |
| Servicio de Disponibilidad | catalog-service | P-03, P-04 |
| Servicio de Cotización | booking-service | P-03 |
| Servicio de Reservas | booking-service | P-03 |
| Servicio de Cancelaciones | booking-service | P-04 |
| Servicio de Administración/Auditoría | backoffice-service | P-05 |
| Servicio de Reseñas | backoffice-service | P-05 |
| Stripe (modo de prueba), proveedor externo | Integración desde booking-service | P-03 |

## Agrupación desplegable actual

| Módulo desplegable | Servicios lógicos agrupados | Datos principales |
|---|---|---|
| `identity-service` | Identidad y perfil | Usuarios, roles y perfiles |
| `catalog-service` | Alojamientos, Disponibilidad y Búsqueda | Publicaciones, servicios, imágenes y periodos disponibles |
| `booking-service` | Cotización, Reservas, Cancelaciones y Notificaciones | Cotizaciones, reservas, cancelaciones, pagos simulados y avisos |
| `backoffice-service` | Reseñas, Administración, Auditoría y Reportería | Reseñas, eventos de auditoría, moderación y reportes |

La tabla es una agrupación de responsabilidades para el MVP. Las dependencias, límites de datos y diagramas C4 se detallan en `07-1-arquitectura-c4.md`.

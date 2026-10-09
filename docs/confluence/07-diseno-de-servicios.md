# 7. Diseño de servicios

> Página nueva (SCRUM-41). Cada subpágina se llena en su tarea de Jira.

## Subpáginas

| Subpágina | Qué contiene | Tarea |
|---|---|---|
| Arquitectura (C4) | Diagrama de contexto y de contenedores, y la descomposición en servicios con sus responsabilidades | SCRUM-46 |
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

Los modelos de la página "4. Procesos de negocio" mencionan nueve servicios y un proveedor externo. El código base del repositorio los agrupa en cuatro servicios desplegables. Esta agrupación es una propuesta que se confirma en SCRUM-46, y la documentación final debe coincidir con lo que se implemente.

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

## Servicios del código base (propuesta)

| Servicio | Historias | Datos que posee |
|---|---|---|
| identity-service | HU-01 a HU-06 y HU-23 | Usuarios, roles y perfiles |
| catalog-service | HU-07 a HU-15 | Publicaciones, servicios (amenities), imágenes y periodos de disponibilidad |
| booking-service | HU-16 a HU-20 | Cotizaciones, reservas, cancelaciones, pagos simulados y notificaciones |
| backoffice-service | HU-21, HU-22 y HU-24 a HU-27 | Reseñas, eventos de auditoría y reportes |

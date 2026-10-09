# 2. Visión, objetivos y alcance (complemento)

> Texto de apoyo para la página "2. Visión, objetivos y alcance" de Confluence. Se compara con lo que ya tiene la página y se agrega lo que falte, en especial los indicadores de éxito, las restricciones, las ambigüedades detectadas y el plan de sprints.

## Visión

### Visión del producto

Ofrecer un marketplace confiable donde anfitriones y huéspedes se encuentren para descubrir, comparar y reservar alojamientos temporales de la región, con información transparente y procesos que se puedan verificar, tanto para huéspedes y anfitriones como para los administradores de la plataforma.

### Contexto

Inversiones LR es una empresa de gestión de inversiones y propiedades, con buena reputación por la calidad y calidez de los alojamientos que administra. Su éxito trajo más turistas nacionales e internacionales que quieren alquilar, y más propietarios que quieren entregar la gestión de sus propiedades. Ese crecimiento destapó problemas en la operación y por eso la empresa decidió cambiar su base tecnológica.

### Problema que se quiere resolver

- La oferta y la demanda crecen y hoy no hay un punto central que las conecte.
- Sin una fuente única de disponibilidad es posible que se confirmen reservas duplicadas sobre el mismo alojamiento.
- Los anfitriones dependen de procesos manuales para publicar y mantener su oferta.
- Es difícil comparar alojamientos y saber el costo total antes de reservar.
- Falta trazabilidad de publicaciones, reservas, cancelaciones y acciones administrativas.

### Objetivos de negocio

1. Conectar la oferta administrada por los anfitriones con la demanda de los huéspedes.
2. Reducir las reservas duplicadas con una fuente central de disponibilidad.
3. Permitir que los anfitriones publiquen y administren su oferta sin procesos manuales.
4. Facilitar el descubrimiento y la comparación con filtros, información de detalle y criterios de disponibilidad.
5. Mantener trazabilidad de publicaciones, reservas, cancelaciones y acciones administrativas.

### Qué gana cada tipo de usuario

| Usuario | Qué gana |
|---|---|
| Huésped | Buscar con filtros, ver el detalle, conocer el total antes de reservar, reservar y cancelar con una política clara |
| Anfitrión | Publicar y administrar sus alojamientos, su disponibilidad, precios, servicios y reglas sin trámites manuales |
| Administrador | Moderar publicaciones, restringir usuarios, consultar auditoría y ver indicadores de la operación |

### Indicadores de éxito del MVP (propuesta)

Estos indicadores salen de los objetivos y del dashboard que pide el enunciado. Están pendientes de confirmar por el equipo.

- Ninguna reserva confirmada se superpone con otra del mismo alojamiento.
- El flujo completo funciona de punta a punta: publicar un alojamiento, buscarlo, consultar su disponibilidad, cotizar y hacer una reserva inmediata.
- El dashboard muestra usuarios, publicaciones activas, reservas por estado, ingresos estimados y ciudades con mayor demanda.
- Toda acción de seguridad o cambio de estado relevante queda registrada y se puede consultar en la auditoría.

## Alcance

### Dentro del alcance (MVP)

| Área | Qué incluye |
|---|---|
| Registro e inicio de sesión | Roles huésped, anfitrión y administrador; protección de rutas; recuperación de contraseña simulada |
| Perfil | Consultar y actualizar datos básicos; activar o desactivar la cuenta |
| Alojamientos | Crear, editar, activar, pausar y consultar publicaciones (título, descripción, ubicación, tipo, capacidad, precio por noche, servicios, reglas e imágenes) |
| Disponibilidad | El anfitrión administra los periodos disponibles; el sistema los consulta y bloquea automáticamente los reservados |
| Búsqueda | Filtrar por ciudad, fechas, capacidad, tipo, rango de precio y servicios; ver el detalle para comparar |
| Cotización | Noches, precio base, limpieza, tarifa de servicio, descuentos, servicios adicionales y total |
| Reserva | Crear la reserva de forma consistente, consultar su estado y el historial |
| Cancelación | Aplicar la política de cancelación de la publicación y liberar el periodo cuando corresponda |
| Notificaciones | Registrar y mostrar notificaciones dentro de la plataforma |
| Reseñas y calificaciones | Calificar y reseñar después de una estancia completada, y consultar las reseñas de una publicación |
| Administración | Bloquear una publicación, restringir un usuario y consultar eventos de auditoría |
| Dashboard | Usuarios, publicaciones activas, reservas por estado, ingresos estimados y ciudades con mayor demanda |
| Reportes | Exportar a PDF o Excel información de usuarios, publicaciones, reservas e ingresos o demanda |
| Trazabilidad | Logs estructurados de autenticación, publicación, reserva, cancelación, reseñas, errores y acciones administrativas |

**Demostración de punta a punta:** publicar un alojamiento → buscarlo → consultar la disponibilidad → cotizar → reserva inmediata.

### Fuera del alcance (funcionalidades opcionales)

- Mapas y geocodificación.
- Mensajería entre huésped y anfitrión.
- Favoritos o listas de alojamientos guardados.
- Contenerización y despliegue automatizado.
- Autenticación multifactor.

Solo se trabajan si el MVP obligatorio, sus pruebas y la sustentación están completos.

### Restricciones

- Arquitectura orientada a servicios, con servicios REST consumidos desde el front.
- Los pagos no son reales: se simulan con un servicio como Stripe en modo de pruebas.
- Herramientas obligatorias: Jira, Confluence, GitHub, Pull Requests con revisión, GitHub Actions y SonarCloud.
- Toda integración a la rama principal pasa por Pull Request, con las pruebas y el Quality Gate en verde.
- El uso de IA debe quedar documentado con capturas (con fecha y hora) y los prompts completos.
- La nota depende de la sustentación de los entregables.

### Stack técnico (propuesto por el equipo)

Java 21 con Spring Boot en el backend, MySQL como base de datos y Angular en el front. Se confirma en la tarea SCRUM-49.

### Ambigüedades detectadas y decisión propuesta

El enunciado tiene algunos puntos que no coinciden entre sí. Esta es la propuesta del equipo, pendiente de confirmar.

| Punto | Qué dice el enunciado | Decisión propuesta |
|---|---|---|
| Reseñas y calificaciones | Aparecen en el alcance del MVP, pero el glosario las describe como funcionalidad opcional | Se tratan como parte del MVP, igual que en el backlog (Sprint 3) |
| Modo de reserva | Las reservas pueden aceptarse de inmediato o requerir aprobación, pero el glosario define la reserva inmediata como obligatoria en el MVP | Solo reserva inmediata en el MVP; la aprobación manual queda como evolución |
| Pagos | Las reglas piden simular pagos con Stripe en pruebas, pero el alcance funcional no lista un módulo de pagos | El pago simulado se integra dentro del flujo de reserva, sin una pantalla de pagos aparte |

### Plan de sprints

| Sprint | Fechas | Objetivo | Puntos |
|---|---|---|---|
| Sprint 0 | 22 sep – 13 oct | Análisis y diseño (Entrega 1) | — |
| Sprint 1 | 14 oct – 27 oct | Identidad, acceso y disponibilidad | 29 |
| Sprint 2 | 28 oct – 10 nov | Publicaciones, búsqueda y reservas | 41 |
| Sprint 3 | 11 nov – 21 nov | Cancelación, reseñas, administración, dashboard y reportes | 33 |

### Entregas del curso

- **Entrega 1** (13 oct): análisis de requerimientos y diseño.
- **Entrega 2**: producto funcional, pruebas e integración continua. Fecha por confirmar.

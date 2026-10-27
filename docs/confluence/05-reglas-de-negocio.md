# 5. Reglas de negocio

Las siguientes reglas de negocio, extraídas del análisis del caso, quedan identificadas con un código (RNxx) para permitir su trazabilidad hacia procesos, historias de usuario y criterios de aceptación.

| ID | Regla | Proceso/área relacionada | Procesos | Servicios que la aplican | Historias de usuario |
|---|---|---|---|---|---|
| RN-01 | Una publicación pertenece a un único ANFITRIÓN; solo el propietario o un ADMINISTRADOR puede modificarla. | Alojamientos | P-02 | Servicio de Alojamientos; Servicio de Identidad y Perfil (rol del usuario) | HU-07, HU-09, HU-10 |
| RN-02 | El ANFITRIÓN administra precio, servicios, reglas, disponibilidad y política de cancelación dentro de las opciones del sistema. | Alojamientos | P-02 | Servicio de Alojamientos; Servicio de Disponibilidad | HU-07, HU-09, HU-12 |
| RN-03 | Estados de publicación: BORRADOR, PENDIENTE_REVISION, ACTIVA, PAUSADA, BLOQUEADA. | Alojamientos | P-02 | Servicio de Alojamientos | HU-07, HU-08, HU-09, HU-10 |
| RN-04 | Solo publicaciones ACTIVAS aparecen en búsquedas y reciben reservas. | Búsqueda / Reserva | P-03 | Servicio de Alojamientos (estado); Servicio de Búsqueda; Servicio de Reservas | HU-08, HU-14, HU-17 |
| RN-05 | Una publicación requiere información mínima completa antes de poder activarse. | Alojamientos | P-02 | Servicio de Alojamientos | HU-08 |
| RN-06 | Capacidad > 0; precio por noche > 0. | Alojamientos | P-02 | Servicio de Alojamientos | HU-07, HU-09 |
| RN-07 | Fecha de salida posterior a la fecha de llegada. | Reserva | P-03 | Servicio de Búsqueda; Servicio de Cotización; Servicio de Reservas | HU-14, HU-16, HU-17 |
| RN-08 | No se confirman dos reservas superpuestas para el mismo alojamiento; la verificación se repite al confirmar, no solo al mostrar resultados. | Disponibilidad / Reserva | P-03 | Servicio de Disponibilidad; Servicio de Reservas | HU-12, HU-13, HU-17 |
| RN-09 | El total = precio base + tarifa de limpieza + tarifa de servicio + tarifas de servicios adicionales, con porcentajes configurables. | Cotización | P-03 | Servicio de Cotización | HU-16 |
| RN-10 | Las reservas pueden ser inmediatas o requerir aprobación, según configuración del anfitrión/administrador (MVP usa reserva inmediata). | Reserva | P-03 | Servicio de Reservas | HU-17 |
| RN-11 | Debe existir al menos una política de cancelación, asociada a la publicación. | Cancelación | P-04 | Servicio de Cancelaciones; Servicio de Alojamientos (política asociada a la publicación) | HU-07, HU-19 |
| RN-12 | Una cancelación registra usuario, fecha, motivo, valor de devolución y estado final. | Cancelación | P-04 | Servicio de Cancelaciones | HU-19 |
| RN-13 | Las operaciones de reserva deben ser idempotentes o equivalente, para evitar duplicados en reintentos. | Reserva | P-03 | Servicio de Reservas | HU-17 |
| RN-14 | Un administrador puede bloquear una publicación por información engañosa; reservas futuras se conservan para auditoría y pasan a revisión manual. | Administración | P-05 (y P-02) | Servicio de Administración/Auditoría; Servicio de Alojamientos (estado BLOQUEADA); Servicio de Reservas (revisión manual) | HU-10, HU-24 |
| RN-15 | Toda acción de seguridad o cambio de estado relevante deja un registro trazable (auditoría). | Trazabilidad | P-05 y P-07 | Servicio de Administración/Auditoría (recibe los eventos de los demás servicios) | HU-24 y las historias que cambian un estado (HU-10, HU-17, HU-19, HU-23) |
| RN-16 | Los pagos no son reales; se usa Stripe en modo prueba para simularlos. | Cotización / Pago | P-03 | Servicio de Cotización; Servicio de Reservas; Stripe (modo de prueba) | HU-16, HU-17 |
| RN-17 | Un huésped solo puede reseñar una reserva completada y asociada al alojamiento evaluado. | Reseñas | P-06 | Servicio de Reseñas | HU-21 |
| RN-18 | La reseña conserva la relación huésped–reserva–alojamiento–fecha–contenido. | Reseñas | P-06 | Servicio de Reseñas | HU-21, HU-22 |
| RN-19 | Las reseñas pueden ser ocultadas por un administrador cuando incumplen políticas. | Moderación | P-05 | Servicio de Reseñas; Servicio de Administración/Auditoría | HU-25 |
| RN-20 | Los reportes exportados corresponden a la información disponible y registran quién los solicitó y cuándo. | Reportes | P-07 | Servicio de Administración/Auditoría | HU-26, HU-27 |

## Cobertura de la trazabilidad

- Las 20 reglas (RN-01 a RN-20) están relacionadas con al menos un proceso de la página 4, al menos un servicio y al menos una historia de usuario de la página 6.
- Las columnas "Procesos", "Servicios que la aplican" e "Historias de usuario" se agregaron para trazar cada regla; el texto de las reglas no cambió.
- Los nombres de los servicios son los que usan los modelos de la página 4. La forma en que se agrupan en servicios desplegables se define en la página 7 (Diseño de servicios).
- Las reglas RN-17, RN-18 y RN-20 se relacionan con los procesos P-06 y P-07 de la página 4.

## Historias sin una regla de negocio específica

Estas historias no se apoyan en una regla numerada. Se rigen por los requerimientos funcionales y por el alcance del MVP: HU-01 a HU-06, HU-11, HU-15, HU-18, HU-20.

## Reglas propuestas (por confirmar)

El enunciado menciona dos condiciones que hoy no tienen código de regla. Se proponen para que las historias de identidad también queden trazadas. No forman parte de la lista aprobada hasta que el equipo las confirme.

| ID | Regla propuesta | Procesos | Servicios que la aplican | Historias de usuario |
|---|---|---|---|---|
| RN-21 | Los roles del sistema son HUÉSPED, ANFITRIÓN y ADMINISTRADOR; cada usuario tiene un rol y solo accede a las rutas permitidas para él. | P-01 | Servicio de Identidad y Perfil | HU-01, HU-02, HU-03 |
| RN-22 | La recuperación de contraseña es simulada. | P-01 | Servicio de Identidad y Perfil | HU-04 |

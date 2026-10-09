# 5. Reglas de negocio

Las siguientes reglas de negocio, extraídas del análisis del caso, quedan identificadas con un código (RNxx) para permitir su trazabilidad hacia procesos, historias de usuario y criterios de aceptación.

| ID | Regla | Proceso/área relacionada |
|---|---|---|
| RN-01 | Una publicación pertenece a un único ANFITRIÓN; solo el propietario o un ADMINISTRADOR puede modificarla. | Alojamientos |
| RN-02 | El ANFITRIÓN administra precio, servicios, reglas, disponibilidad y política de cancelación dentro de las opciones del sistema. | Alojamientos |
| RN-03 | Estados de publicación: BORRADOR, PENDIENTE_REVISION, ACTIVA, PAUSADA, BLOQUEADA. | Alojamientos |
| RN-04 | Solo publicaciones ACTIVAS aparecen en búsquedas y reciben reservas. | Búsqueda / Reserva |
| RN-05 | Una publicación requiere información mínima completa antes de poder activarse. | Alojamientos |
| RN-06 | Capacidad > 0; precio por noche > 0. | Alojamientos |
| RN-07 | Fecha de salida posterior a la fecha de llegada. | Reserva |
| RN-08 | No se confirman dos reservas superpuestas para el mismo alojamiento; la verificación se repite al confirmar, no solo al mostrar resultados. | Disponibilidad / Reserva |
| RN-09 | El total = precio base + tarifa de limpieza + tarifa de servicio + tarifas de servicios adicionales, con porcentajes configurables. | Cotización |
| RN-10 | Las reservas pueden ser inmediatas o requerir aprobación, según configuración del anfitrión/administrador (MVP usa reserva inmediata). | Reserva |
| RN-11 | Debe existir al menos una política de cancelación, asociada a la publicación. | Cancelación |
| RN-12 | Una cancelación registra usuario, fecha, motivo, valor de devolución y estado final. | Cancelación |
| RN-13 | Las operaciones de reserva deben ser idempotentes o equivalente, para evitar duplicados en reintentos. | Reserva |
| RN-14 | Un administrador puede bloquear una publicación por información engañosa; reservas futuras se conservan para auditoría y pasan a revisión manual. | Administración |
| RN-15 | Toda acción de seguridad o cambio de estado relevante deja un registro trazable (auditoría). | Trazabilidad |
| RN-16 | Los pagos no son reales; se usa Stripe en modo prueba para simularlos. | Cotización / Pago |
| RN-17 | Un huésped solo puede reseñar una reserva completada y asociada al alojamiento evaluado. | Reseñas |
| RN-18 | La reseña conserva la relación huésped–reserva–alojamiento–fecha–contenido. | Reseñas |
| RN-19 | Las reseñas pueden ser ocultadas por un administrador cuando incumplen políticas. | Moderación |
| RN-20 | Los reportes exportados corresponden a la información disponible y registran quién los solicitó y cuándo. | Reportes |

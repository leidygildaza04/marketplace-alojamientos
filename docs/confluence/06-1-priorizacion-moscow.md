# Priorización MoSCoW

> Subpágina de "6. Requerimientos" (SCRUM-45). Revisa y confirma la prioridad Must / Should / Could de las 9 épicas y las 27 historias cargadas en el backlog de Jira.

## Cómo se usa MoSCoW en este proyecto

Todas las funcionalidades de la lista pertenecen al MVP obligatorio del enunciado. MoSCoW no decide qué se entrega y qué no: ordena el trabajo y define qué se recorta primero si el tiempo se agota.

| Categoría | Criterio |
|---|---|
| Must have | Necesaria para la demostración de punta a punta (publicar, buscar, consultar disponibilidad, cotizar y reservar) o para evitar reservas superpuestas. |
| Should have | Obligatoria en el alcance, pero no bloquea la demostración. Se entrega después de lo Must. |
| Could have | Aporta valor, pero se puede recortar sin afectar el flujo principal. Es lo primero que se ajusta si falta tiempo. |
| Won't have (esta vez) | Fuera del MVP de esta entrega. |

## Resumen

| Categoría | Historias | Puntos | Porcentaje |
|---|---|---|---|
| Must have | 11 | 50 | 49 % |
| Should have | 10 | 32 | 31 % |
| Could have | 6 | 21 | 20 % |
| Total | 27 | 103 | 100 % |

Lo Must queda por debajo del 50 % de los puntos, así que hay margen para absorber retrasos sin comprometer la demostración.

## Priorización por épica

Los nombres de las épicas están abreviados; hay que ajustarlos al nombre completo que tienen en Jira.

| Épica | MoSCoW | Justificación | Historias |
|---|---|---|---|
| EP-01 Identidad y acceso | Must | Sin cuentas con rol y sesión no hay publicación ni reserva. | Must: HU-01, HU-02, HU-03. Should: HU-04, HU-05. Could: HU-06. |
| EP-02 Gestión de publicaciones | Must | Sin publicaciones activas no hay oferta que descubrir. | Must: HU-07, HU-08, HU-11. Should: HU-09, HU-10. |
| EP-03 Disponibilidad | Must | Es la fuente central que evita reservas duplicadas. | Must: HU-12, HU-13. |
| EP-04 Descubrimiento | Must | Búsqueda y cotización son pasos de la demostración. | Must: HU-14, HU-16. Should: HU-15. |
| EP-05 Reservas | Must | Cierra la demostración con la reserva inmediata. | Must: HU-17. Should: HU-18. |
| EP-06 Cancelaciones | Should | Obligatoria en el alcance, pero se apoya en que existan reservas. | Should: HU-19, HU-20. |
| EP-07 Reseñas y calificaciones | Could | Ocurre después de la estancia y no bloquea el flujo principal. | Could: HU-21, HU-22, HU-25. |
| EP-08 Administración | Should | Moderación y auditoría para la confiabilidad de la plataforma. | Should: HU-23, HU-24 (HU-10 en EP-02). |
| EP-09 Reportería | Could | Indicadores y reportes para la gestión; se alimentan de datos que ya existen. | Could: HU-26, HU-27. |

## Priorización por historia

| HU | Jira | Historia | Épica | Sprint | Puntos | MoSCoW | Justificación |
|---|---|---|---|---|---|---|---|
| HU-01 | SCRUM-14 | Registro con selección de rol | EP-01 | 1 | 5 | Must | Sin cuentas con rol no hay publicación ni reserva; es el primer paso de la demostración. |
| HU-02 | SCRUM-15 | Inicio de sesión | EP-01 | 1 | 3 | Must | Toda acción autenticada depende de iniciar sesión. |
| HU-03 | SCRUM-16 | Protección de rutas por rol | EP-01 | 1 | 3 | Must | Garantiza que solo el anfitrión publique y solo el propietario modifique (RN-01). |
| HU-04 | SCRUM-17 | Recuperación de contraseña simulada | EP-01 | 1 | 3 | Should | Está en el alcance, pero no bloquea el flujo principal. |
| HU-05 | SCRUM-18 | Consultar y actualizar perfil | EP-01 | 1 | 3 | Should | Obligatoria en el alcance; no bloquea la demostración. |
| HU-06 | SCRUM-19 | Activar/desactivar cuenta | EP-01 | 1 | 2 | Could | Gestión del ciclo de vida de la cuenta; se puede recortar sin afectar el flujo principal. |
| HU-07 | SCRUM-20 | Crear publicación | EP-02 | 2 | 5 | Must | Primer paso de la demostración: publicar el alojamiento. |
| HU-08 | SCRUM-21 | Activar publicación | EP-02 | 2 | 3 | Must | Solo las publicaciones ACTIVAS aparecen en búsquedas y reciben reservas (RN-04). |
| HU-09 | SCRUM-22 | Editar/pausar publicación | EP-02 | 2 | 3 | Should | Necesaria para operar la oferta, pero no para la demostración. |
| HU-10 | SCRUM-23 | Bloquear publicación (admin) | EP-02 | 2 | 3 | Should | Moderación por información engañosa (RN-14); no bloquea la demostración. |
| HU-11 | SCRUM-24 | Ver detalle de publicación | EP-02 | 2 | 3 | Must | El huésped necesita el detalle antes de cotizar y reservar. |
| HU-12 | SCRUM-25 | Administrar periodos disponibles | EP-03 | 1 | 5 | Must | Sin periodos disponibles no hay reserva posible (RN-08). |
| HU-13 | SCRUM-26 | Bloqueo automático de periodos reservados | EP-03 | 1 | 5 | Must | Evita reservas superpuestas (RN-08), que es un objetivo central del negocio. |
| HU-14 | SCRUM-27 | Filtrar búsqueda | EP-04 | 2 | 5 | Must | Descubrimiento: paso de la demostración. |
| HU-15 | SCRUM-28 | Comparar resultados | EP-04 | 2 | 3 | Should | Mejora el descubrimiento; se entrega después del filtro. |
| HU-16 | SCRUM-29 | Ver cotización detallada | EP-04 | 2 | 5 | Must | Paso de la demostración y aplica el cálculo del total (RN-09). |
| HU-17 | SCRUM-30 | Confirmar reserva | EP-05 | 2 | 8 | Must | Cierra la demostración; aplica RN-08, RN-10 y RN-13. |
| HU-18 | SCRUM-31 | Consultar estado e historial de reservas | EP-05 | 2 | 3 | Should | El huésped lo necesita, pero no bloquea crear la reserva. |
| HU-19 | SCRUM-32 | Cancelar reserva con política | EP-06 | 3 | 5 | Should | Lo pide el enunciado (RN-11 y RN-12); depende de que exista la reserva. |
| HU-20 | SCRUM-33 | Notificaciones internas | EP-06 | 3 | 3 | Should | Informa los eventos relevantes; no bloquea los flujos. |
| HU-21 | SCRUM-34 | Calificar y reseñar | EP-07 | 3 | 5 | Could | Aporta confianza a la plataforma; ocurre después de la estancia. |
| HU-22 | SCRUM-35 | Consultar reseñas de una publicación | EP-07 | 3 | 2 | Could | Complementa las reseñas; depende de HU-21. |
| HU-23 | SCRUM-36 | Restringir usuario | EP-08 | 3 | 3 | Should | Moderación de usuarios; se apoya en la autenticación. |
| HU-24 | SCRUM-37 | Consultar auditoría | EP-08 | 3 | 3 | Should | Da trazabilidad de las acciones relevantes (RN-15). |
| HU-25 | SCRUM-38 | Ocultar reseña (admin) | EP-07 | 3 | 2 | Could | Moderación de reseñas; depende de HU-21. |
| HU-26 | SCRUM-39 | Dashboard de indicadores | EP-09 | 3 | 5 | Could | Indicadores de gestión; se alimentan de los datos de reservas. |
| HU-27 | SCRUM-40 | Exportar reportes | EP-09 | 3 | 5 | Could | Exportación de reportes; se apoya en los datos del sistema (RN-20). |

## Ajustes de prioridad en Jira

La prioridad de Jira se alinea con MoSCoW: Must = Alta, Should = Media, Could = Baja. Comparando contra el backlog (prioridades vistas el 4 de octubre), 8 historias cambian. Las otras 19 ya estaban bien.

| HU | Jira | Historia | Prioridad actual | Prioridad nueva | MoSCoW |
|---|---|---|---|---|---|
| HU-06 | SCRUM-19 | Activar/desactivar cuenta | Media | Baja | Could |
| HU-09 | SCRUM-22 | Editar/pausar publicación | Alta | Media | Should |
| HU-10 | SCRUM-23 | Bloquear publicación (admin) | Alta | Media | Should |
| HU-18 | SCRUM-31 | Consultar estado e historial de reservas | Alta | Media | Should |
| HU-21 | SCRUM-34 | Calificar y reseñar | Media | Baja | Could |
| HU-22 | SCRUM-35 | Consultar reseñas de una publicación | Media | Baja | Could |
| HU-26 | SCRUM-39 | Dashboard de indicadores | Media | Baja | Could |
| HU-27 | SCRUM-40 | Exportar reportes | Media | Baja | Could |

Antes de cambiarlas, hay que confirmar en Jira que esas sean las prioridades actuales.

## Won't have (esta vez)

| Funcionalidad | Motivo |
|---|---|
| Mapas y geocodificación | Opcional en el enunciado; solo se considera si el MVP completo, sus pruebas y la sustentación están listos. |
| Mensajería entre huésped y anfitrión | Opcional en el enunciado. |
| Favoritos o listas de alojamientos guardados | Opcional en el enunciado. |
| Contenerización y despliegue automatizado | Opcional en el enunciado. |
| Autenticación multifactor | Opcional en el enunciado. |
| Reserva con aprobación del anfitrión | El MVP usa reserva inmediata (RN-10). |

## Dependencias que afectan el orden

| Historia | Depende de | Efecto en la planeación |
|---|---|---|
| HU-12 Administrar periodos disponibles (Sprint 1) | HU-07 Crear publicación (Sprint 2) | En el Sprint 1 se construye el manejo de periodos con una publicación mínima; se completa al llegar HU-07. |
| HU-13 Bloqueo automático de periodos reservados (Sprint 1) | HU-17 Confirmar reserva (Sprint 2) | El bloqueo se termina de probar cuando exista la reserva. |
| HU-16 Ver cotización detallada | HU-11 y HU-12 | Necesita el detalle de la publicación y su disponibilidad. |
| HU-17 Confirmar reserva | HU-08, HU-12 y HU-16 | Es el cierre del flujo de la demostración. |
| HU-19 Cancelar reserva con política | HU-17 | No se puede cancelar sin reserva. |
| HU-21 Calificar y reseñar | HU-17 | Requiere una reserva completada (RN-17). |
| HU-22 y HU-25 | HU-21 | Consultan u ocultan reseñas ya registradas. |
| HU-26 y HU-27 | HU-17 | Se alimentan de los datos de reservas. |

## Decisiones a confirmar con el equipo

1. Tratar las reseñas como Could dentro del MVP. El enunciado las lista en el alcance y a la vez las describe como opcionales en el glosario.
2. Aceptar los ajustes de prioridad de la tabla anterior.
3. Mantener HU-12 y HU-13 en el Sprint 1 aunque dependan de historias del Sprint 2, o moverlas al Sprint 2.

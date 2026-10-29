# SCRUM-47 — Modelo de datos entidad-relación

> Estado: propuesta lógica para revisión. El modelo describe las entidades principales solicitadas por SCRUM-47 y sus cardinalidades. No implica que todas las tablas estén implementadas en la base de datos actual.

## 1. Objetivo y alcance

Definir el modelo entidad-relación del Marketplace de Alojamientos para las entidades Usuario, Alojamiento, PeriodoDisponible, Reserva, Cotización, Cancelación, PolíticaCancelación, Notificación, Reseña y EventoAuditoría. Se indican atributos principales, claves, relaciones, reglas de integridad y propiedad lógica de los datos.

El modelo es lógico y transversal. La arquitectura actual utiliza cuatro bases de datos separadas (`identity_db`, `catalog_db`, `booking_db` y `backoffice_db`). Por ello, las relaciones entre entidades de distintos servicios se representan mediante identificadores y contratos de API/eventos; no deben implementarse como claves foráneas físicas entre bases de datos diferentes.

## 2. Diagrama entidad-relación

```mermaid
erDiagram
    USUARIO ||--o{ ALOJAMIENTO : publica
    ALOJAMIENTO ||--o{ PERIODO_DISPONIBLE : define
    USUARIO ||--o{ RESERVA : realiza
    ALOJAMIENTO ||--o{ RESERVA : recibe
    COTIZACION ||--o| RESERVA : origina
    RESERVA ||--o| CANCELACION : puede_tener
    POLITICA_CANCELACION ||--o{ CANCELACION : determina
    USUARIO ||--o{ NOTIFICACION : recibe
    RESERVA o|--o{ NOTIFICACION : contextualiza
    USUARIO ||--o{ RESENA : escribe
    ALOJAMIENTO ||--o{ RESENA : recibe
    RESERVA ||--o| RESENA : habilita
    USUARIO o|--o{ EVENTO_AUDITORIA : ejecuta

    USUARIO {
      bigint id_usuario PK
      varchar nombre
      varchar correo UK
      varchar hash_contrasena
      varchar telefono
      varchar estado
      datetime fecha_creacion
    }
    ALOJAMIENTO {
      bigint id_alojamiento PK
      bigint id_anfitrion FK_logica
      varchar titulo
      text descripcion
      varchar direccion
      varchar ciudad
      decimal precio_noche
      int capacidad
      varchar estado_publicacion
      datetime fecha_creacion
    }
    PERIODO_DISPONIBLE {
      bigint id_periodo PK
      bigint id_alojamiento FK_logica
      date fecha_inicio
      date fecha_fin
      varchar estado
      decimal precio_noche
    }
    RESERVA {
      bigint id_reserva PK
      bigint id_usuario FK_logica
      bigint id_alojamiento FK_logica
      bigint id_cotizacion FK_logica_UK
      date fecha_entrada
      date fecha_salida
      int huespedes
      decimal total
      varchar estado
      datetime fecha_creacion
    }
    COTIZACION {
      bigint id_cotizacion PK
      bigint id_usuario FK_logica
      bigint id_alojamiento FK_logica
      date fecha_entrada
      date fecha_salida
      int huespedes
      decimal subtotal
      decimal cargos
      decimal total
      datetime expira_en
      varchar estado
    }
    CANCELACION {
      bigint id_cancelacion PK
      bigint id_reserva FK_logica_UK
      bigint id_politica FK_logica
      bigint solicitada_por FK_logica
      varchar motivo
      decimal valor_reembolso
      varchar estado
      datetime fecha_solicitud
    }
    POLITICA_CANCELACION {
      bigint id_politica PK
      varchar nombre
      text descripcion
      int horas_limite
      decimal porcentaje_reembolso
      varchar estado
    }
    NOTIFICACION {
      bigint id_notificacion PK
      bigint id_usuario FK_logica
      bigint id_reserva FK_logica_nullable
      varchar canal
      varchar tipo
      varchar asunto
      text contenido
      varchar estado_envio
      datetime fecha_creacion
      datetime fecha_envio
    }
    RESENA {
      bigint id_resena PK
      bigint id_usuario FK_logica
      bigint id_alojamiento FK_logica
      bigint id_reserva FK_logica_UK
      int calificacion
      text comentario
      varchar estado_moderacion
      datetime fecha_creacion
    }
    EVENTO_AUDITORIA {
      bigint id_evento PK
      bigint id_usuario FK_logica_nullable
      varchar accion
      varchar entidad
      varchar id_entidad
      text detalle
      varchar resultado
      datetime fecha_evento
    }
```

> En Mermaid, las marcas `FK_logica` indican referencias lógicas; no necesariamente son restricciones FK físicas en el mismo esquema.

## 3. Entidades y atributos principales

Los atributos son una propuesta mínima para el modelo inicial. Los tipos pueden adaptarse al motor y a las convenciones definitivas del equipo.

| Entidad | Atributos principales | Propósito y servicio propietario propuesto |
|---|---|---|
| **Usuario** | `id_usuario` (PK), `nombre`, `correo` (único), `hash_contrasena`, `telefono`, `estado`, `fecha_creacion` | Identidad y perfil; `identity-service` / `identity_db`. La contraseña se almacena como hash, nunca en texto plano. |
| **Alojamiento** | `id_alojamiento` (PK), `id_anfitrion` (referencia lógica), `titulo`, `descripcion`, `direccion`, `ciudad`, `precio_noche`, `capacidad`, `estado_publicacion`, `fecha_creacion` | Publicación y administración del alojamiento; `catalog-service` / `catalog_db`. |
| **PeriodoDisponible** | `id_periodo` (PK), `id_alojamiento` (referencia lógica), `fecha_inicio`, `fecha_fin`, `estado`, `precio_noche` | Bloques de fechas disponibles, bloqueadas o reservadas; `catalog-service` / `catalog_db`. |
| **Reserva** | `id_reserva` (PK), `id_usuario`, `id_alojamiento`, `id_cotizacion` (único y opcional según flujo), `fecha_entrada`, `fecha_salida`, `huespedes`, `total`, `estado`, `fecha_creacion` | Reserva confirmada o en proceso; `booking-service` / `booking_db`. |
| **Cotización** | `id_cotizacion` (PK), `id_usuario`, `id_alojamiento`, fechas, `huespedes`, `subtotal`, `cargos`, `total`, `expira_en`, `estado` | Precio calculado para un periodo concreto; `booking-service` / `booking_db`. Debe tener vencimiento para evitar reutilizar precios caducados. |
| **Cancelación** | `id_cancelacion` (PK), `id_reserva` (único), `id_politica`, `solicitada_por`, `motivo`, `valor_reembolso`, `estado`, `fecha_solicitud` | Solicitud y resultado de cancelar una reserva; `booking-service` / `booking_db`. |
| **PolíticaCancelación** | `id_politica` (PK), `nombre`, `descripcion`, `horas_limite`, `porcentaje_reembolso`, `estado` | Reglas que determinan plazos y reembolsos; propuesta de propiedad en `catalog-service` o configuración de negocio. La propiedad final debe confirmarse. |
| **Notificación** | `id_notificacion` (PK), `id_usuario`, `id_reserva` (opcional), `canal`, `tipo`, `asunto`, `contenido`, `estado_envio`, fechas | Registro de avisos de reserva, cancelación y cuenta; responsabilidad lógica de Notificaciones, agrupada inicialmente en `booking-service`. |
| **Reseña** | `id_resena` (PK), `id_usuario`, `id_alojamiento`, `id_reserva` (único), `calificacion`, `comentario`, `estado_moderacion`, `fecha_creacion` | Valoración posterior a una estancia; responsabilidad lógica de Reseñas, agrupada inicialmente en `backoffice-service`. |
| **EventoAuditoría** | `id_evento` (PK), `id_usuario` (opcional), `accion`, `entidad`, `id_entidad`, `detalle`, `resultado`, `fecha_evento` | Evidencia de acciones relevantes y decisiones de moderación; `backoffice-service` / `backoffice_db`. |

## 4. Relaciones y cardinalidades

| Relación | Cardinalidad | Regla de negocio |
|---|---|---|
| Usuario — Alojamiento | Un usuario anfitrión puede publicar muchos alojamientos; cada alojamiento tiene un anfitrión. | El usuario referenciado debe existir y tener permisos para publicar. |
| Alojamiento — PeriodoDisponible | Un alojamiento puede tener muchos periodos; cada periodo corresponde a un alojamiento. | `fecha_inicio` debe ser anterior a `fecha_fin`; no se permiten periodos incompatibles que provoquen doble disponibilidad. |
| Usuario — Reserva | Un usuario puede realizar muchas reservas; cada reserva identifica al huésped que la solicita. | Solo usuarios autenticados y habilitados pueden reservar. |
| Alojamiento — Reserva | Un alojamiento puede aparecer en muchas reservas a lo largo del tiempo; cada reserva corresponde a un alojamiento. | Las fechas de reservas confirmadas no pueden solaparse para la misma unidad reservable. |
| Cotización — Reserva | Una cotización puede no convertirse en reserva o convertirse en una sola reserva. | La reserva debe respetar la cotización vigente o recalcular el precio si expiró. |
| Reserva — Cancelación | Una reserva puede no tener cancelación o tener una solicitud/registro de cancelación. | El estado final debe ser coherente y no permitir múltiples cancelaciones efectivas para la misma reserva. |
| PolíticaCancelación — Cancelación | Una política puede aplicarse a muchas cancelaciones; cada cancelación registra la política aplicada. | Guardar la política aplicada o su versión evita que cambios posteriores alteren el cálculo histórico. |
| Usuario — Notificación | Un usuario puede recibir muchas notificaciones; cada notificación tiene un destinatario. | Registrar estado y fechas de envío para trazabilidad de entrega. |
| Reserva — Notificación | Una reserva puede originar cero o muchas notificaciones; una notificación puede no estar ligada a una reserva. | Por ejemplo, avisos de cuenta no requieren una reserva. |
| Usuario — Reseña | Un usuario puede escribir muchas reseñas; cada reseña tiene un autor. | El usuario debe haber completado una estancia elegible antes de reseñar. |
| Alojamiento — Reseña | Un alojamiento puede recibir muchas reseñas; cada reseña valora un alojamiento. | La calificación debe estar en el rango acordado por el producto (propuesta: 1 a 5). |
| Reserva — Reseña | Una reserva puede habilitar como máximo una reseña; una reseña corresponde a una reserva. | La reserva debe estar completada y pertenecer al autor de la reseña. |
| Usuario — EventoAuditoría | Un usuario puede generar muchos eventos; algunos eventos del sistema pueden no tener usuario asociado. | El evento debe conservar actor, acción, entidad afectada, resultado y fecha. |

## 5. Restricciones e integridad

1. **Claves primarias:** cada entidad tiene un identificador único e inmutable.
2. **Correo único:** `Usuario.correo` debe tener una restricción de unicidad y normalizarse para evitar duplicados por mayúsculas/minúsculas.
3. **Fechas:** `fecha_entrada < fecha_salida`, `fecha_inicio < fecha_fin` y las fechas deben validarse en la zona horaria definida por el sistema.
4. **Valores monetarios:** precios, subtotales, cargos, totales y reembolsos deben ser decimales exactos, no `float`. Deben ser no negativos y calcularse en el backend.
5. **Capacidad:** `Alojamiento.capacidad > 0`; `Reserva.huespedes > 0` y no puede superar la capacidad del alojamiento.
6. **Calificación:** `Resena.calificacion` debe estar entre 1 y 5 si ese es el rango acordado.
7. **Unicidad de negocio:** `Reserva.id_cotizacion`, `Cancelacion.id_reserva` y `Resena.id_reserva` deben ser únicos cuando no sean nulos, según las reglas de conversión, cancelación y reseña indicadas.
8. **Disponibilidad concurrente:** la comprobación y reserva de fechas debe ser atómica para evitar que dos usuarios confirmen el mismo periodo al mismo tiempo.
9. **Auditoría:** los eventos no deben actualizarse como si fueran datos operativos ordinarios; se recomienda restringir modificación y eliminación y controlar acceso a sus detalles.
10. **Privacidad:** no registrar contraseñas, tokens ni datos de pago sensibles en `EventoAuditoria` o `Notificacion`.

## 6. Distribución de datos por base y servicio

La infraestructura documentada en `docker/mysql/init.sql` crea cuatro bases independientes. La asignación propuesta es:

| Base de datos | Entidades propietarias | Módulo desplegable actual |
|---|---|---|
| `identity_db` | Usuario | `identity-service` |
| `catalog_db` | Alojamiento, PeriodoDisponible y, si se aprueba, PolíticaCancelación | `catalog-service` |
| `booking_db` | Cotización, Reserva, Cancelación y Notificación | `booking-service` |
| `backoffice_db` | Reseña y EventoAuditoría | `backoffice-service` |

Esta distribución es la propiedad lógica recomendada, no una afirmación de que todas las tablas ya existan. La implementación debe respetar el principio de que cada servicio modifica sus propios datos. Para referencias entre bases, guardar el identificador externo y validar mediante API/eventos; no crear joins ni claves foráneas físicas entre bases separadas.

## 7. Reglas para evolucionar a esquema físico

- Usar migraciones versionadas para crear o modificar tablas; evitar cambios manuales no reproducibles.
- Definir índices para correo, estado de publicación, ciudad, rangos de fechas, usuario de reserva, alojamiento y fechas de reserva.
- Revisar la estrategia para evitar solapamiento de reservas según el motor de base de datos elegido.
- Definir retención, anonimización y permisos de acceso para datos personales, notificaciones y auditoría.
- Si una política de cancelación cambia con el tiempo, almacenar en la cancelación una copia/versionado de las condiciones aplicadas.
- Acordar si una reseña requiere una reserva completada y cómo se calcula el promedio público.

## 8. Decisiones por confirmar con el equipo

- Motor y convenciones definitivas de nombres, tipos y migraciones por base de datos.
- Propietario definitivo de `PolíticaCancelación` y forma de versionarla.
- Si una reserva puede generarse sin cotización previa; si se exige cotización, hacer `id_cotizacion` obligatorio.
- Política para varias solicitudes de cancelación rechazadas frente a una única cancelación efectiva; si se requiere historial de intentos, separar solicitud e historial.
- Reglas definitivas de disponibilidad, zona horaria, monedas, impuestos y cargos.
- Rango de calificación y condiciones de elegibilidad para publicar reseñas.
- Campos mínimos y política de retención de `EventoAuditoría`.

## 9. Trazabilidad con SCRUM-46 y SCRUM-48

- **SCRUM-46:** la propiedad de datos sigue la agrupación de los doce servicios lógicos en los cuatro módulos desplegables descritos en `07-1-arquitectura-c4.md`.
- **SCRUM-47:** define entidades, atributos principales, relaciones, cardinalidades, restricciones y propiedad lógica.
- **SCRUM-48:** los contratos OpenAPI de Reservas, Disponibilidad y Cotización deberán reutilizar los identificadores y conceptos de este modelo y aclarar qué datos se consultan entre servicios.

## 10. Validación del entregable

- [x] Las diez entidades solicitadas están incluidas.
- [x] Se documentan claves, atributos principales y cardinalidades.
- [x] Se identifican reglas de integridad y decisiones pendientes.
- [x] Se asigna propiedad lógica de datos a las bases/módulos existentes.
- [ ] El equipo valida las decisiones de dominio pendientes.
- [ ] El modelo se contrasta con los criterios completos de Jira y el pipeline después del push.

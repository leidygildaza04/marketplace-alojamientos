# 3. Actores e interesados

Se identificaron los actores que interactúan directamente con el sistema, los interesados que influyen en el proyecto o reciben valor de sus resultados, los responsables de administrar/moderar/soportar la plataforma, los usuarios afectados por decisiones de disponibilidad/reserva/cancelación/publicación, y los interesados relacionados con la operación, medición, auditoría y evolución del producto.

## Resumen de actores e interesados

Cada actor o interesado se justifica con evidencia del contexto del proyecto (enunciado, reglas de negocio y alcance del MVP).

| Actor o interesado | Categoría | Evidencia en el contexto |
|---|---|---|
| Huésped | Actor directo; usuario afectado por decisiones de disponibilidad, reserva, cancelación y publicación | El contexto habla de turistas y arrendatarios que quieren alquilar propiedades; el glosario define al huésped como quien busca, cotiza, reserva y cancela |
| Anfitrión | Actor directo; usuario afectado por decisiones de disponibilidad, reserva, cancelación y publicación | El contexto habla de propietarios que entregan la gestión de sus propiedades; el glosario lo define como quien publica y administra alojamientos |
| Administrador | Actor directo; responsable de administrar y moderar la plataforma | El alcance incluye el rol ADMINISTRADOR, bloquear publicaciones, restringir usuarios y consultar la auditoría (RN-01, RN-14, RN-15, RN-19) |
| Inversiones LR (dirección) | Interesado que influye en el proyecto y recibe valor de sus resultados | El contexto explica que la empresa decidió cambiar su base tecnológica por el crecimiento de huéspedes y oferta; los objetivos de negocio son suyos |
| Área de operación y auditoría de Inversiones LR | Interesado de operación, medición, auditoría y evolución del producto | El alcance incluye dashboard, reportes exportables y trazabilidad; la regla RN-20 exige registrar quién pidió cada exportación y cuándo |
| Proveedor de pagos de prueba (Stripe) | Interesado externo; soporta una parte del flujo de reserva | La regla RN-16 pide simular los pagos con un servicio como Stripe en modo de prueba |
| Equipo de desarrollo del proyecto | Interesado de evolución del producto | El enunciado pide documentación, trazabilidad, informe final y una propuesta de evolución del producto |

## Huésped

| Atributo | Descripción |
|---|---|
| Descripción | Persona natural nacional o internacional que busca, compara, cotiza, reserva y eventualmente cancela alojamientos temporales en la plataforma. |
| Relación con la empresa | Usuario final directo del lado de la demanda; cliente de Inversiones LR. |
| Objetivos / Necesidades | Encontrar alojamientos confiables, comparar precios y condiciones, reservar de forma rápida y segura, y consultar el historial de sus reservas. |
| Preocupaciones | Transparencia del precio total, disponibilidad real (sin sobreventa), claridad de reglas y políticas de cancelación. |
| Responsabilidades | Registrarse con datos veraces, completar el pago simulado, calificar la estancia una vez finalizada. |
| Funcionalidades que podrían afectarlo | Publicación, disponibilidad, búsqueda, cotización, reserva, cancelación, notificaciones, reseñas. |

## Anfitrión

| Atributo | Descripción |
|---|---|
| Descripción | Propietario o encargado de uno o más alojamientos que entrega su gestión a Inversiones LR para ofrecerlos en el marketplace. |
| Relación con la empresa | Usuario final del lado de la oferta; proveedor de inventario para la plataforma. |
| Objetivos / Necesidades | Maximizar la ocupación de sus alojamientos, mantener información y disponibilidad actualizadas sin procesos manuales, recibir reservas sin duplicidad. |
| Preocupaciones | Reservas superpuestas, publicaciones bloqueadas injustamente, falta de visibilidad sobre el estado de sus anuncios. |
| Responsabilidades | Mantener veraz la información de la publicación (precio, servicios, reglas, imágenes), administrar la disponibilidad y la política de cancelación aplicable. |
| Funcionalidades que podrían afectarlo | Publicación, disponibilidad, cotización (tarifas), reserva, cancelación. |

## Administrador

| Atributo | Descripción |
|---|---|
| Descripción | Usuario interno de Inversiones LR con el rol ADMINISTRADOR. Modera la plataforma: revisa publicaciones y reseñas, restringe usuarios y consulta la auditoría, el dashboard y los reportes. |
| Relación con la empresa | Usuario interno que opera y da soporte al marketplace en nombre de Inversiones LR. |
| Objetivos / Necesidades | Mantener la calidad y la confiabilidad de la oferta, actuar rápido ante información engañosa, dejar trazabilidad de cada decisión y conocer los indicadores de la operación. |
| Preocupaciones | Publicaciones o reseñas que incumplen las políticas, decisiones administrativas sin registro, y reservas futuras que quedan afectadas cuando se bloquea una publicación. |
| Responsabilidades | Bloquear publicaciones por información engañosa (RN-14), restringir usuarios, ocultar reseñas que incumplen políticas (RN-19), consultar la auditoría (RN-15) y exportar reportes (RN-20). También puede modificar publicaciones (RN-01). |
| Funcionalidades que podrían afectarlo | Bloquear publicación (HU-10), restringir usuario (HU-23), consultar auditoría (HU-24), ocultar reseña (HU-25), dashboard de indicadores (HU-26), exportar reportes (HU-27) y notificaciones. |

## Inversiones LR (dirección)

| Atributo | Descripción |
|---|---|
| Descripción | Dirección de la empresa que gestiona inversiones y propiedades de estancias cortas y largas. Patrocina el proyecto y decidió cambiar su base tecnológica. |
| Relación con la empresa | Dueña del negocio y del producto; es el cliente del proyecto. |
| Objetivos / Necesidades | Atender el crecimiento de huéspedes y de oferta, sostener su reputación por la calidad y calidez de sus propiedades, conectar oferta y demanda y reducir las reservas duplicadas. |
| Preocupaciones | Perder reputación por reservas duplicadas o información engañosa, depender de procesos manuales que no escalan y no tener trazabilidad de lo que ocurre en la plataforma. |
| Responsabilidades | Definir los objetivos y las políticas del negocio (por ejemplo, los porcentajes de la tarifa de servicio) y validar que el marketplace los cumpla. |
| Funcionalidades que podrían afectarla | Tarifas y porcentajes configurables (RN-09), políticas de cancelación (RN-11), dashboard de indicadores y las decisiones de alcance del MVP. |

## Área de operación y auditoría de Inversiones LR

| Atributo | Descripción |
|---|---|
| Descripción | Personas de Inversiones LR que miden la operación, revisan la trazabilidad y usan la información para mejorar el producto. Acceden al sistema mediante el rol ADMINISTRADOR. |
| Relación con la empresa | Usuario interno que consume información de gestión. |
| Objetivos / Necesidades | Conocer usuarios, publicaciones activas, reservas por estado, ingresos estimados y ciudades con mayor demanda; auditar las acciones de seguridad y los cambios de estado; sustentar decisiones de evolución del producto. |
| Preocupaciones | Reportes que no coincidan con la información del sistema, exportaciones sin registro de quién las pidió y logs incompletos. |
| Responsabilidades | Consultar el dashboard y los reportes, solicitar exportaciones (que quedan registradas) y revisar los eventos de auditoría. |
| Funcionalidades que podrían afectarla | Dashboard de indicadores (HU-26), exportar reportes (HU-27), consultar auditoría (HU-24) y logs estructurados. |

## Proveedor de pagos de prueba (Stripe)

| Atributo | Descripción |
|---|---|
| Descripción | Servicio externo de pagos que se usa en modo de prueba para simular el cobro de las reservas. No mueve dinero real. |
| Relación con la empresa | Proveedor externo de tecnología con el que el marketplace se integra. |
| Objetivos / Necesidades | Recibir las solicitudes de cobro simulado y responder su resultado para que la reserva pueda completarse. |
| Preocupaciones | Disponibilidad de su servicio de pruebas, cambios en su API y que los datos de prueba no se confundan con pagos reales. |
| Responsabilidades | Procesar el pago de prueba y devolver el resultado de la transacción al servicio de reservas. |
| Funcionalidades que podrían afectarlo | Cotización (HU-16), confirmar reserva (HU-17) y el proceso P-03 (RN-16). |

## Equipo de desarrollo del proyecto

| Atributo | Descripción |
|---|---|
| Descripción | Estudiantes del curso Ingeniería de Software 1 que analizan, diseñan, construyen y prueban el MVP: Gabriella Castro, Paulo Alejandro, Yeison Silva y Leidy Gil. |
| Relación con la empresa | Equipo que desarrolla el producto para Inversiones LR en el marco del curso. |
| Objetivos / Necesidades | Entregar un MVP funcional que cumpla el alcance, con pruebas, integración continua, trazabilidad completa y una sustentación sólida. |
| Preocupaciones | Cumplir las fechas de las entregas, mantener la coherencia entre el diseño documentado y lo implementado, mantener estable la rama principal y resolver las ambigüedades del enunciado. |
| Responsabilidades | Especificar las historias de usuario, diseñar los servicios, implementar, revisar los Pull Requests, mantener Jira, Confluence y GitHub, y documentar el uso de IA. |
| Funcionalidades que podrían afectarlo | Todo el producto, además del pipeline de integración continua, SonarCloud y la matriz de trazabilidad. |

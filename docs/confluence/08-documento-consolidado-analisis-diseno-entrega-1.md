# SCRUM-52 — Documento consolidado de análisis y diseño de la Entrega 1

## 1. Objetivo

Consolidar las decisiones de análisis y diseño del Marketplace de Alojamientos, reuniendo la arquitectura del sistema, el modelo de datos y el stack tecnológico documentados durante la semana 2.

## 2. Arquitectura del sistema

El sistema utiliza una arquitectura orientada a servicios REST. La documentación define doce servicios lógicos agrupados en cuatro aplicaciones backend desplegables:

- `identity-service`: funcionalidades relacionadas con la identidad y autenticación.
- `catalog-service`: gestión y consulta del catálogo de alojamientos.
- `booking-service`: funcionalidades relacionadas con las reservas.
- `backoffice-service`: operaciones administrativas del sistema.

Los doce servicios representan responsabilidades de negocio; no significa que cada uno se despliegue como una aplicación independiente.

La arquitectura C4 describe el contexto del sistema, sus contenedores, las responsabilidades de los servicios y sus principales integraciones.

Documento de referencia: [Arquitectura C4](07-1-arquitectura-c4.md).

## 3. Modelo de datos

El modelo entidad-relación representa las entidades principales, sus atributos, cardinalidades y restricciones.

La solución contempla cuatro bases de datos:

- `identity_db`
- `catalog_db`
- `booking_db`
- `backoffice_db`

Como los datos se distribuyen entre diferentes bases de datos, las relaciones entre entidades de distintos servicios se gestionan mediante identificadores y contratos de API o eventos. No se deben asumir claves foráneas físicas entre bases de datos separadas.

Documento de referencia: [Modelo entidad-relación](07-2-modelo-entidad-relacion.md).

## 4. Stack tecnológico y estructura del repositorio

El stack tecnológico y la estructura del proyecto se encuentran documentados en el archivo de referencia correspondiente. La implementación backend se organiza en cuatro módulos Spring Boot, de acuerdo con la agrupación definida en la arquitectura.

Las decisiones tecnológicas deben mantenerse alineadas con la estructura real del repositorio y con los contratos de integración documentados, incluidos los contratos OpenAPI cuando correspondan.

Documento de referencia: [Stack tecnológico y estructura del repositorio](07-4-stack-tecnico-estructura-repositorio.md).

## 5. Relación entre análisis y diseño

La arquitectura establece cómo se distribuyen las responsabilidades del sistema. El modelo de datos define la información que necesita cada área funcional y el stack tecnológico establece las herramientas y la organización utilizadas para implementar la solución.

Estos elementos deben ser coherentes entre sí y con los procesos, requisitos e historias de usuario de la Entrega 1.

## 6. Verificación

- [ ] Revisar la arquitectura C4 y la agrupación de los servicios.
- [ ] Verificar las entidades, atributos y relaciones del modelo de datos.
- [ ] Confirmar el stack tecnológico frente a la estructura real del repositorio.
- [ ] Comprobar que los enlaces a los documentos de referencia funcionan.
- [ ] Revisar que el documento no contradiga las decisiones técnicas existentes.

## 7. Criterios de aceptación

- Existe un documento consolidado de análisis y diseño.
- Se encuentra documentada la arquitectura y la agrupación de servicios.
- Se resumen las bases de datos y los elementos principales del modelo de datos.
- Se referencia el stack tecnológico documentado.
- El contenido se contrasta con los documentos existentes y sus enlaces.
- Los cambios se integran mediante un Pull Request revisado.

## 8. Alcance

Este documento consolida la información disponible en el repositorio. No reemplaza los documentos técnicos detallados ni introduce decisiones tecnológicas nuevas sin revisión del equipo.

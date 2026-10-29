# SCRUM-49. Stack técnico y estructura del repositorio

## Objetivo

Registrar el stack técnico que ya utiliza el repositorio y establecer convenciones para organizar el código, las ramas y los commits. Las decisiones se basan en los archivos de configuración presentes en el proyecto, no en una migración tecnológica.

## Stack técnico identificado

| Capa | Tecnología identificada | Evidencia en el repositorio | Uso |
|---|---|---|---|
| Frontend | Angular 20, TypeScript y RxJS | `frontend/package.json` y configuración de Angular | Aplicación web del marketplace |
| Backend | Java 21 y Spring Boot 3.4.5 | `pom.xml` raíz y POM de los servicios | API y lógica de negocio |
| Construcción backend | Maven, proyecto multi-módulo | `pom.xml` raíz | Compilar y verificar los cuatro servicios |
| Base de datos | MySQL 8.4 | `docker-compose.yml` | Persistencia relacional para el entorno de desarrollo |
| Contenedores de desarrollo | Docker Compose | `docker-compose.yml` | Levantar MySQL e inicializar la base de datos |
| Contratos de API | OpenAPI 3.0.3 en YAML | `docs/api/openapi/` | Documentar Reservas, Disponibilidad y Cotización |
| Control de versiones | Git y GitHub | Repositorio y `docs/CONTRIBUTING.md` | Colaboración por ramas y Pull Requests |
| Integración continua | GitHub Actions | `.github/workflows/ci.yml` | Verificación del backend y frontend en Pull Requests y push a `main` |
| Calidad estática | SonarCloud, configuración prevista | `pom.xml` y workflow CI | Análisis cuando estén configurados el token y los identificadores reales |

### Decisiones y límites

- Se conserva Angular como frontend y Spring Boot como backend porque son las tecnologías que ya están configuradas.
- El backend se organiza como proyecto Maven multi-módulo con `identity-service`, `catalog-service`, `booking-service` y `backoffice-service`.
- Los doce servicios descritos en la arquitectura son servicios lógicos; no se asume que cada uno tenga un despliegue independiente.
- MySQL 8.4 está definido en Docker Compose para desarrollo. La estrategia de bases de datos por servicio y el entorno de producción requieren confirmación del equipo.
- Los contratos OpenAPI son especificaciones iniciales y deben mantenerse alineados con los endpoints implementados.
- SonarCloud requiere completar `sonar.organization`, `sonar.projectKey` y el secreto `SONAR_TOKEN`; no se considera activo hasta verificarlo en GitHub Actions.

## Estructura del repositorio

```text
marketplace-alojamientos/
├── .github/
│   ├── workflows/ci.yml
│   └── pull_request_template.md
├── docs/
│   ├── api/openapi/
│   │   ├── reservas.yaml
│   │   ├── disponibilidad.yaml
│   │   └── cotizacion.yaml
│   ├── confluence/
│   │   └── documentación funcional y técnica
│   ├── CONTRIBUTING.md
│   ├── github-configuracion.md
│   └── trazabilidad.md
├── docker/
│   └── mysql/init.sql
├── frontend/                 # Angular
├── services/
│   ├── identity-service/
│   ├── catalog-service/
│   ├── booking-service/
│   └── backoffice-service/
├── docker-compose.yml
└── pom.xml                   # Maven parent multi-módulo
```

La estructura refleja los directorios de la versión actual. Los archivos y carpetas adicionales pueden incorporarse en historias posteriores sin cambiar estas convenciones.

## Convención de ramas

Crear una rama por historia Jira, con el identificador al inicio y un nombre breve en minúsculas separado por guiones:

```text
SCRUM-49-stack-estructura-repositorio
SCRUM-48-contratos-openapi
```

- Las ramas de trabajo parten de `main` actualizado.
- No hacer `push` directo a `main`.
- Abrir un Pull Request hacia `main` por historia.
- Resolver conflictos y comentarios antes de integrar.

## Convención de commits

Iniciar el mensaje con el código de Jira y describir un cambio coherente:

```text
SCRUM-49 documenta stack técnico y estructura del repositorio
SCRUM-49 aclara convenciones de ramas y commits
```

Cada integrante debe usar su identidad de Git configurada y asociada a su cuenta de GitHub.

## Flujo de contribución

1. Actualizar `main`.
2. Crear una rama por historia.
3. Realizar cambios pequeños y relacionados con la historia.
4. Revisar `git status` y ejecutar las comprobaciones aplicables.
5. Crear el commit y subir la rama.
6. Abrir el Pull Request con la historia Jira en el título.
7. Esperar los checks de GitHub Actions y la revisión del equipo.
8. Integrar en `main` cuando se cumplan las reglas del repositorio.

## Comprobaciones para esta historia

- Revisar que las tecnologías documentadas coincidan con `pom.xml`, `frontend/package.json`, `docker-compose.yml` y `.github/workflows/ci.yml`.
- Revisar que las rutas del árbol del repositorio correspondan con los directorios presentes.
- Después de subir los cambios, comprobar el resultado real de GitHub Actions.

## Pendientes de confirmación del equipo

- Versiones objetivo para producción y política de actualización de dependencias.
- Estrategia final de persistencia y propiedad de datos por servicio.
- Valores reales de SonarCloud y configuración de secretos.
- Reglas de protección de ramas y número mínimo de aprobaciones en GitHub.

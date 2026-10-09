# 1. Introducción (complemento)

> Texto de apoyo para la página "1. Introducción" del espacio de Confluence. Si la página ya tiene alguna de estas secciones, se deja la que ya existe y se agrega solo lo que falte.

## De qué trata el proyecto

Inversiones LR gestiona propiedades de estancias cortas y largas y está creciendo tanto en huéspedes como en propietarios. Para ordenar ese crecimiento, el proyecto construye un marketplace web que conecta anfitriones con huéspedes: publicar, buscar, cotizar, reservar y cancelar alojamientos desde un solo lugar, con la disponibilidad centralizada.

## Estructura del espacio

| # | Página | Qué contiene | Tarea en Jira | Responsable |
|---|---|---|---|---|
| 1 | Introducción | Contexto del proyecto, equipo y herramientas | SCRUM-41 | Gabriella Castro |
| 2 | Visión, objetivos y alcance | Visión, objetivos de negocio, alcance del MVP y plan de sprints | SCRUM-41 | Gabriella Castro |
| 3 | Actores e interesados | Quién usa el sistema y a quién afecta | SCRUM-42 | Gabriella Castro |
| 4 | Procesos de negocio | Modelos BPMN de los procesos críticos | SCRUM-43 | Paulo Alejandro |
| 5 | Reglas de negocio | Reglas de negocio y su trazabilidad | SCRUM-44 | Paulo Alejandro |
| 6 | Requerimientos | Índice de historias de usuario y priorización MoSCoW | SCRUM-45 | Yeison Silva |
| 7 | Diseño de servicios | Arquitectura C4, modelo de datos y contratos OpenAPI | SCRUM-46, 47 y 48 | Yeison Silva y Leidy Gil |
| 8 | Acuerdos del equipo | Decisiones y acuerdos de trabajo | — | Todos |
| 9 | Evidencias de validación | Pruebas, capturas y resultados de revisión | — | Todos |
| 10 | Manual de despliegue | Cómo ejecutar y desplegar el producto | — | Todos |
| 11 | Uso de IA | Evidencias del uso de IA por integrante | — | Todos |

## Equipo

| Integrante | Tareas del sprint 0 |
|---|---|
| Gabriella Castro | SCRUM-41, 42, 49, 53 y 54 |
| Paulo Alejandro | SCRUM-43, 44, 51 y 52 |
| Yeison Silva | SCRUM-45 y 46 |
| Leidy Gil | SCRUM-47, 48 y 50 |

## Herramientas

- **Jira** (proyecto SCRUM): épicas, historias, sprints y seguimiento.
- **Confluence**: esta documentación.
- **GitHub**: repositorio privado `leidygildaza04/marketplace-alojamientos`, con ramas por historia, Pull Requests y GitHub Actions.
- **SonarCloud**: análisis estático dentro del flujo de PR.

## Forma de trabajo

El equipo trabaja con Scrum. Cada historia de Jira se desarrolla en una rama propia que lleva el código de la historia (por ejemplo `SCRUM-14-registro-con-rol`), se integra por Pull Request con al menos una revisión de otro integrante, y la sube la persona asignada en Jira.

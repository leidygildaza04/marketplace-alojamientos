# Configuración de GitHub (SCRUM-50)

Este documento describe cómo está configurado el repositorio del proyecto y qué reglas protegen la rama principal.

## Repositorio

| Dato | Valor |
|---|---|
| Nombre | `marketplace-alojamientos` |
| Propietaria | Leidy Gil (`leidygildaza04`) |
| Visibilidad | Privado |
| Rama principal | `main` |
| Estructura | Monorepo: `services/` (4 servicios Spring Boot), `frontend/` (Angular), `docs/` y `docker/` |

El commit inicial (`19679c6`, "SCRUM-50 estructura inicial del repositorio") se hizo directo sobre `main`, antes de activar la protección. Desde la protección en adelante, todo cambio entra por Pull Request.

## Colaboradores

| Integrante | Usuario de GitHub | Permiso |
|---|---|---|
| Leidy Gil | `leidygildaza04` | Propietaria (Admin) |
| Gabriella Castro | `gabGABY006` | Write |
| Paulo Alejandro | `LeNot-T` | Write |
| Yeison Silva | `AlejandroS17bosque` | Write |

## Protección de la rama `main`

Configurada en *Settings → Branches → Add branch ruleset* (o *Add classic branch protection rule*).

| Regla | Valor |
|---|---|
| Rama protegida | `main` |
| Pull Request obligatorio antes del merge | Sí |
| Aprobaciones requeridas | 1, de una persona distinta a quien abre el PR |
| Checks obligatorios | `Backend (build, pruebas y SonarCloud)` y `Frontend (build y pruebas)` |
| Rama actualizada antes del merge | Sí |
| Force push | Bloqueado |
| Eliminar la rama | Bloqueado |
| Las reglas aplican también a administradores | Sí |

### Cómo se verificó

- [ ] Un `git push` directo a `main` es rechazado.
- [ ] Un PR sin aprobación no se puede unir.
- [ ] Un PR con un check en rojo no se puede unir.

Evidencia: _(agregar capturas con fecha y hora)_.

## Plantilla de Pull Request

El archivo `.github/pull_request_template.md` pide el código de la historia de Jira, los criterios de aceptación cubiertos, cómo se probó y la confirmación de la revisión.

## Flujo de trabajo

Historia Jira → rama → commits → Pull Request → GitHub Actions → pruebas → SonarCloud → revisión → merge. El detalle está en `docs/CONTRIBUTING.md`.

## Pendiente

- Conectar SonarCloud (secreto `SONAR_TOKEN` y datos de organización en el `pom.xml`) para que el Quality Gate cuente como evidencia. Se hace en SCRUM-51.

# Flujo de trabajo

Historia Jira → branch → commits → Pull Request → GitHub Actions → pruebas → SonarCloud Quality Gate → revisión → merge

## Ramas

Una rama por historia, con el código de Jira al inicio:

```
SCRUM-14-registro-con-rol
SCRUM-51-pipeline-github-actions
```

Nunca se hace push directo a `main`.

## Commits

Empiezan con la historia de Jira y describen un cambio coherente:

```
SCRUM-14 agrega endpoint de registro con selección de rol
SCRUM-14 agrega pruebas de correo duplicado
```

Cada integrante usa su propio nombre y el correo asociado a su cuenta de GitHub, para que la autoría de cada historia coincida con la persona asignada en Jira.

## Pull Requests

1. Usar la plantilla del repositorio y poner la historia de Jira en el título.
2. Esperar a que GitHub Actions y el Quality Gate de SonarCloud estén en verde.
3. Conseguir al menos una revisión de otro integrante del equipo.
4. Resolver los comentarios antes de hacer merge.

## Configuración de GitHub

La configuración del repositorio, los colaboradores y la protección de `main` están documentadas en [github-configuracion.md](github-configuracion.md).

Pendiente: agregar el secreto `SONAR_TOKEN` en *Settings → Secrets and variables → Actions* y poner `sonar.organization` y `sonar.projectKey` reales en el `pom.xml` raíz.

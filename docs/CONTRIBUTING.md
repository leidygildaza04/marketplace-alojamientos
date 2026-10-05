# Flujo de trabajo

Historia Jira → branch → commits → Pull Request → GitHub Actions → pruebas → SonarCloud → revisión → merge

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

## Pull Requests

1. Usar la plantilla del repositorio y poner la historia de Jira en el título.
2. Esperar a que GitHub Actions y el Quality Gate de SonarCloud estén en verde.
3. Conseguir al menos una revisión de otro integrante del equipo.
4. Resolver los comentarios antes de hacer merge.

## Configuración pendiente en GitHub

- Proteger `main`: exigir PR, 1 aprobación y que pasen los checks `Backend` y `Frontend`.
- Agregar el secreto `SONAR_TOKEN` en Settings → Secrets and variables → Actions.
- Poner `sonar.organization` y `sonar.projectKey` reales en el `pom.xml` raíz.

# Pipeline base de GitHub Actions — SCRUM-51

## Objetivo
Ejecutar la integración continua del marketplace en cada Pull Request para detectar
errores de compilación o construcción antes de integrar cambios en `main`.

## Workflow
Archivo: `.github/workflows/ci.yml`
Nombre del workflow: `CI`

## Cuándo se ejecuta
- En eventos `pull_request`.
- En `push` a la rama `main`.

## Trabajos

### Backend
- Ejecuta en `ubuntu-latest`.
- Configura Java 21 (Temurin) y caché de Maven.
- Ejecuta `mvn -B verify` desde la raíz para compilar y ejecutar las verificaciones
  definidas por el proyecto Maven multi-módulo.
- Ejecuta el análisis de SonarCloud solo si está configurado el secreto `SONAR_TOKEN`.

### Frontend
- Ejecuta en `ubuntu-latest`, dentro de `frontend/`.
- Configura Node.js 22 y la caché de npm.
- Instala dependencias reproducibles con `npm ci`.
- Ejecuta las pruebas con `npm test -- --watch=false --browsers=ChromeHeadless`.
- Construye la aplicación con `npm run build`.

## Secretos
`SONAR_TOKEN` es opcional para el paso de análisis SonarCloud. El workflow utiliza
`GITHUB_TOKEN` proporcionado por GitHub Actions para la integración con GitHub.
No se deben guardar tokens ni contraseñas directamente en el repositorio.

## Cómo verificar una ejecución
1. Abrir la pestaña **Actions** del repositorio.
2. Seleccionar la ejecución asociada al Pull Request.
3. Confirmar que los trabajos **Backend** y **Frontend** finalizaron correctamente.
4. Si un trabajo falla, abrir el detalle del paso fallido, corregir el error y actualizar
   la rama del Pull Request.

## Estado y alcance
Este archivo documenta el workflow existente como pipeline inicial. Las ejecuciones
anteriores reportadas para SCRUM-46, SCRUM-47 y SCRUM-48 finalizaron correctamente.
La verificación final de esta historia debe quedar respaldada por la ejecución de CI
del Pull Request específico de SCRUM-51.

## Evolución prevista
El pipeline puede ampliarse durante Sprint 1 con más pruebas automatizadas y reglas de
calidad. El análisis SonarCloud ya está contemplado de forma condicional cuando existe
`SONAR_TOKEN`.

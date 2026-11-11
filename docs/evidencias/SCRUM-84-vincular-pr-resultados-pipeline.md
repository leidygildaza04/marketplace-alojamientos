# SCRUM-84 - Vincular el Pull Request y los resultados del pipeline

## Historia de Jira
SCRUM-84 - Vincular el Pull Request y los resultados del pipeline.

Historia principal: SCRUM-30 - HU-17 Confirmar reserva.

## Objetivo
Dejar vinculados y documentados el Pull Request y los resultados
del pipeline de integración continua correspondientes a SCRUM-83.

## Pull Request relacionado

- PR #57: test(SCRUM-83): cubrir concurrencia y pagos rechazados.
- Estado: MERGED.
- Fecha de integración: 10 de noviembre de 2026.
- Rama: SCRUM-83-probar-concurrencia-pagos-rechazados.
- Commit: 36ae43d.

Enlace:
https://github.com/leidygildaza04/marketplace-alojamientos/pull/57

## Ejecución del pipeline

- Workflow: CI.
- Evento: pull_request.
- Ejecución: 42762382422.
- Estado general: SUCCESS.

Enlace de ejecución:
https://github.com/leidygildaza04/marketplace-alojamientos/actions/runs/42762382422

## Resultados verificados

### Backend
- Trabajo: Backend (build, pruebas y SonarCloud).
- Estado: SUCCESS.
- Resultado: BUILD y pruebas completados correctamente.

Enlace:
https://github.com/leidygildaza04/marketplace-alojamientos/actions/runs/42762382422/job/129650892464

### Frontend
- Trabajo: Frontend (build y pruebas).
- Estado: SUCCESS.
- Resultado: compilación y pruebas completadas correctamente.

Enlace:
https://github.com/leidygildaza04/marketplace-alojamientos/actions/runs/42762382422/job/129650892760

### SonarCloud
- Análisis: SonarCloud Code Analysis.
- Estado: SUCCESS.
- Quality Gate: PASSED.
- Incidencias nuevas: 0.
- Security Hotspots: 0.

Enlace:
https://sonarcloud.io/dashboard?id=leidygildaza04_marketplace-alojamientos&pullRequest=57

## Evidencia de integración

El Pull Request #57 fue integrado en la rama main.
La ejecución del pipeline confirmó resultados exitosos para Backend,
Frontend y el análisis de calidad de SonarCloud.

## Alcance de esta subtarea

Esta subtarea documenta la vinculación del Pull Request y los resultados
del pipeline. No modifica código funcional ni configuraciones de CI.

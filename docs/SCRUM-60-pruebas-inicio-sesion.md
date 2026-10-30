# SCRUM-60 — Probar credenciales válidas, incorrectas y cuentas restringidas

## Objetivo
Comprobar las respuestas del inicio de sesión ante credenciales correctas, incorrectas y cuentas no habilitadas.

## Escenarios automatizados
- Credenciales válidas: responde HTTP 200 y entrega token y rol.
- Contraseña incorrecta: responde HTTP 401 con `INVALID_CREDENTIALS`.
- Correo inexistente: responde HTTP 401 con `INVALID_CREDENTIALS`, sin revelar si el correo está registrado.
- Cuenta desactivada con contraseña correcta: responde HTTP 403 con `ACCOUNT_DISABLED`.

## Prueba agregada
Los escenarios están en `Scrum60LoginScenariosTest`. La cuenta desactivada se prepara cambiando su estado a inactivo en el repositorio antes de intentar el inicio de sesión.

## Validación
Ejecutar desde la raíz del repositorio:

```powershell
& "C:\apache-maven-3.10.0\bin\mvn.cmd" -pl services/identity-service -am test
```

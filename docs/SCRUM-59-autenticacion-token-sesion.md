# SCRUM-59 — Implementar autenticación y generación del token de sesión

## Objetivo
Verificar el inicio de sesión y la generación de un token JWT firmado para el usuario autenticado.

## Implementación existente revisada
- `POST /api/auth/login` recibe correo y contraseña.
- `AuthService` normaliza el correo y compara la contraseña con el hash almacenado.
- `JwtService` genera un JWT firmado con identificador del usuario, correo, rol, fecha de emisión y expiración.
- La respuesta contiene el token, el rol y el tiempo de expiración en minutos.
- La seguridad de la API utiliza sesiones sin estado (`STATELESS`) y el filtro valida tokens Bearer.

## Prueba agregada
`Scrum59JwtSessionTest` registra un usuario, inicia sesión y verifica la firma/lectura del JWT, el identificador, correo, rol y vigencia de cinco minutos configurada en el perfil de pruebas.

## Validación
Ejecutar desde la raíz del repositorio:

```powershell
& "C:\apache-maven-3.10.0\bin\mvn.cmd" -pl services/identity-service -am test
```

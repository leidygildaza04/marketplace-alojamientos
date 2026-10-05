# Matriz de trazabilidad

Requisito ↔ historia Jira ↔ criterio de aceptación ↔ branch/commit ↔ PR ↔ prueba/evidencia ↔ release

Se agrega una fila cuando se abre el PR y se completa al hacer el merge.

| Requisito | Historia Jira | Criterio de aceptación | Branch | PR | Prueba / evidencia | Release |
|---|---|---|---|---|---|---|
| Registro con rol (EP-01) | SCRUM-14 (HU-01) | Un usuario se registra como HUÉSPED o ANFITRIÓN; ADMIN no se puede elegir; el correo no se repite | _por definir_ | _por definir_ | `AuthFlowTest` (registro) | _por definir_ |
| Inicio de sesión (EP-01) | SCRUM-15 (HU-02) | Con credenciales válidas se recibe un token con el rol; con credenciales inválidas, 401 | _por definir_ | _por definir_ | `AuthFlowTest` (login) | _por definir_ |
| Protección de rutas (EP-01) | SCRUM-16 (HU-03) | Una ruta protegida responde 401 sin token válido | _por definir_ | _por definir_ | `AuthFlowTest` (ruta protegida) | _por definir_ |

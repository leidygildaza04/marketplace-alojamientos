# SCRUM-17 – Recuperación de contraseña

## Implementación
- `POST /api/auth/password/recovery`: recibe un correo registrado y genera un token aleatorio de 256 bits con vigencia de 30 minutos.
- Para la simulación, el token se devuelve en la respuesta y no se envía correo electrónico.
- Se almacena únicamente el SHA-256 del token en `password_reset_tokens`.
- `POST /api/auth/password/reset`: valida el token, actualiza la contraseña con BCrypt y marca el token como utilizado.
- Los tokens inexistentes, vencidos o utilizados se rechazan.

## Ejemplo de solicitud
```json
{ "email": "usuario@example.com" }
```

## Ejemplo de cambio de contraseña
```json
{ "token": "TOKEN_RECIBIDO", "newPassword": "ClaveNueva456!" }
```

La solicitud de recuperación responde con `token` y `expiresAt` para la simulación académica. En producción, el token debe entregarse por un canal verificado y no exponerse en la respuesta HTTP.

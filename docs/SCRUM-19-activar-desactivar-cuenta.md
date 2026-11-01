# SCRUM-19 – HU-06 Activar/desactivar cuenta

## Implementado en este parche (identity-service)
- `POST /api/users/me/deactivate` requiere JWT y desactiva la cuenta autenticada.
- El inicio de sesión rechaza cuentas desactivadas (`ACCOUNT_DISABLED`).
- El filtro JWT consulta el estado vigente de la cuenta; un token emitido antes de la desactivación deja de autorizar operaciones.
- `POST /api/auth/reactivate` recibe correo y contraseña, valida las credenciales y reactiva la cuenta.
- Cada cambio se registra en `account_status_history` mediante Flyway V2.

## Pendiente de integración para completar los criterios de aceptación
El ZIP de base disponible contiene `catalog-service` y `booking-service` como aplicaciones iniciales con endpoint de salud, sin entidades, repositorios ni contratos de publicaciones o reservas. Por eso este parche no puede pausar publicaciones activas ni consultar reservas futuras confirmadas de forma real. No se simulan esos datos ni se declara CA-06.1/CA-06.2 como completados. Para terminarlos se necesitan los modelos y endpoints reales de esos dos servicios.

## Endpoints
### Desactivar (JWT requerido)
`POST /api/users/me/deactivate`

### Reactivar (sin JWT; credenciales obligatorias)
`POST /api/auth/reactivate`
```json
{
  "email": "usuario@example.com",
  "password": "Clave12345!"
}
```

## Pruebas
`Scrum19AccountLifecycleTest` cubre bloqueo del login, revocación efectiva de tokens ya emitidos, reactivación con credenciales válidas y rechazo de credenciales inválidas.

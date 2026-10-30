# SCRUM-18 – HU-05: Consultar y actualizar perfil

## Descripción
Como usuario autenticado, puedo consultar mis datos básicos y actualizar mi nombre y teléfono para mantener mi información al día.

## Endpoints
- `GET /api/users/me`: consulta id, nombre, correo, teléfono, rol y estado del usuario autenticado.
- `PUT /api/users/me`: actualiza nombre y teléfono del usuario autenticado.

Todas las rutas requieren JWT. El correo y el rol no son editables desde este endpoint. Si se intenta cambiar a un correo que ya pertenece a otra cuenta, se responde `409 EMAIL_ALREADY_USED`; un intento de modificar el rol se rechaza con `403 ROLE_NOT_EDITABLE`.

## Criterios cubiertos
- CA-05.1: consulta de nombre, correo, teléfono y rol.
- CA-05.2: actualización persistente de nombre y teléfono con confirmación en la respuesta.
- CA-05.3: rechazo de correo duplicado y modificación de rol.

## Pruebas
`Scrum18ProfileTest` cubre consulta autenticada, actualización persistente, correo duplicado, intento de cambio de rol, teléfono inválido y acceso sin autenticación.

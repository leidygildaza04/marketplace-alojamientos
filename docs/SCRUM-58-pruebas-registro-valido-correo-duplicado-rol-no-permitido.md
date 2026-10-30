# SCRUM-58 — Probar registro válido, correo duplicado y rol no permitido

## Objetivo

Agregar pruebas de aceptación que comprueben el comportamiento del endpoint `POST /api/auth/register` en los tres escenarios solicitados.

## Cambios incluidos

- Se agrega `RegistrationScenariosTest.java` al servicio `identity-service`.
- Se comprueba que un registro válido cree el usuario en la base de datos de prueba y que la contraseña se guarde codificada con el `PasswordEncoder`, nunca en texto plano.
- Se comprueba que la respuesta de registro no exponga la contraseña ni el hash.
- Se comprueba que un correo duplicado con diferencias entre mayúsculas y minúsculas devuelva HTTP 409 (`EMAIL_ALREADY_USED`).
- Se comprueba que solicitar el rol `ADMIN` devuelva HTTP 403 (`ROLE_NOT_ALLOWED`) y no cree un usuario.

## Cómo probar

Desde la raíz del repositorio ejecutar:

```powershell
mvn -pl services/identity-service -am test
```

Para empaquetar el servicio y ejecutar las pruebas:

```powershell
mvn -pl services/identity-service -am package
```

Las pruebas usan el perfil `test` y la base de datos H2 en memoria configurada en `src/test/resources/application-test.yml`; no requieren modificar la base de datos real.

## Criterios de aceptación

- El registro válido responde HTTP 201 y el usuario queda persistido.
- La contraseña persistida coincide mediante verificación del codificador, pero no es igual al texto original.
- El correo duplicado responde HTTP 409.
- El rol `ADMIN` responde HTTP 403 y no se persiste la cuenta.
- Las pruebas automatizadas terminan correctamente.

# 10. Manual de despliegue

Requisitos: JDK 21, Maven 3.9 o superior, Node 22 y Docker (para MySQL).

1. Clonar el repositorio.
2. Levantar la base de datos con `docker compose up -d`.
3. Ejecutar cada servicio con `mvn -pl services/<servicio> spring-boot:run`.
4. Ejecutar el front con `npm install` y `npm start` dentro de `frontend`.

El README del repositorio tiene el detalle actualizado, incluido el perfil `dev` para probar sin MySQL.

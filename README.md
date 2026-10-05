# Marketplace de Alojamientos — Inversiones LR

Proyecto de curso de Ingeniería de Software 1 (Universidad El Bosque).
Marketplace web de alojamientos temporales con arquitectura orientada a servicios.

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3.4, Maven (multi-módulo) |
| Base de datos | MySQL 8.4 (un esquema por servicio), migraciones con Flyway |
| Frontend | Angular 20 |
| Seguridad | Spring Security + JWT |
| CI | GitHub Actions + SonarCloud |

## Servicios

| Servicio | Puerto | Esquema | Historias |
|---|---|---|---|
| `identity-service` | 8081 | `identity_db` | HU-01 a HU-06, HU-23 |
| `catalog-service` | 8082 | `catalog_db` | HU-07 a HU-15 |
| `booking-service` | 8083 | `booking_db` | HU-16 a HU-20 |
| `backoffice-service` | 8084 | `backoffice_db` | HU-21, HU-22, HU-24 a HU-27 |

Estado actual: `identity-service` tiene registro (HU-01), inicio de sesión (HU-02) y una ruta protegida de ejemplo (`GET /api/users/me`). Los otros tres tienen solo el esqueleto y `/actuator/health`.

## Cómo correrlo

Requisitos: JDK 21, Maven 3.9+, Node 22 y Docker.

```bash
# 1. Base de datos
docker compose up -d

# 2. Backend (cada servicio en su terminal)
mvn -pl services/identity-service spring-boot:run

# 3. Frontend
cd frontend && npm install && npm start
```

Pruebas:

```bash
mvn verify                                   # todos los servicios + cobertura JaCoCo
cd frontend && npm test -- --watch=false --browsers=ChromeHeadless
```

Probar el registro y el login:

```bash
curl -X POST localhost:8081/api/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"ana@example.com","password":"Clave12345","fullName":"Ana Pérez","role":"GUEST"}'

curl -X POST localhost:8081/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"ana@example.com","password":"Clave12345"}'
```

## Variables de entorno

| Variable | Uso | Valor por defecto (solo desarrollo) |
|---|---|---|
| `DB_URL` | URL JDBC del servicio | `jdbc:mysql://localhost:3306/<servicio>_db` |
| `DB_USER` / `DB_PASSWORD` | Credenciales de MySQL | `app` / `app` |
| `JWT_SECRET` | Clave para firmar tokens (mínimo 32 caracteres) | valor de desarrollo |

## Flujo de trabajo

Ver [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) y la matriz en [docs/trazabilidad.md](docs/trazabilidad.md).

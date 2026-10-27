# Frontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.3.37.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Conexión con el backend

El frontend conserva `MarketplaceDataService` como fuente de datos local para poder revisar las vistas sin servidor. La configuración HTTP de Angular está habilitada y `MarketplaceApiService` incluye llamadas tipadas para alojamientos y reservas.

Cuando esté disponible el backend:

1. Ajusta `apiBaseUrl` en `src/environments/environment.ts` a la URL de la API.
2. Alinea las rutas y los nombres de campos de `MarketplaceApiService` con el contrato real del backend. Actualmente se asumen `GET /accommodations`, `GET /accommodations/:id`, `GET /reservations` y `POST /reservations`.
3. Sustituye en cada pantalla el origen `MarketplaceDataService` por las llamadas HTTP y gestiona estados de carga y error.
4. Configura CORS en el backend para permitir el origen del frontend durante desarrollo. Si la API requiere autenticación, acuerda primero el mecanismo con el backend y añade el flujo correspondiente.

Las pantallas aún usan datos locales; no se envían solicitudes a la API hasta conectarlas explícitamente a `MarketplaceApiService`.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

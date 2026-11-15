/** Cambia esta URL cuando el equipo defina dónde se ejecutará el backend. */
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:3000/api',
  identityApiBaseUrl: 'http://localhost:8085',
  identityApiProxyBaseUrl: '/identity-api',
  catalogApiProxyBaseUrl: '/catalog-api',
  bookingApiProxyBaseUrl: '/booking-api',
  backofficeApiProxyBaseUrl: '/backoffice-api',
};

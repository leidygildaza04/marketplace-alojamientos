# SCRUM-80 - Revalidar disponibilidad al confirmar

## Objetivo
Verificar que la disponibilidad del alojamiento se compruebe nuevamente
cuando se intenta confirmar una reserva.

## Implementación verificada
- El controlador interno recibe la solicitud de reserva.
- Se comprueba que el alojamiento exista y esté activo.
- El repositorio ejecuta reservarAtomico para cambiar el periodo
  de DISPONIBLE a RESERVADO únicamente si cumple las condiciones.
- Si el periodo ya está reservado o no cumple las fechas solicitadas,
  la operación responde HTTP 409 Conflict.
- Booking interpreta el conflicto y no confirma la reserva.

## Archivos revisados
- services/catalog-service/src/main/java/co/edu/unbosque/marketplace/catalog/web/PeriodosReservaInternalController.java
- services/catalog-service/src/main/java/co/edu/unbosque/marketplace/catalog/repository/PeriodoDisponibilidadRepository.java
- services/catalog-service/src/test/java/co/edu/unbosque/marketplace/catalog/PeriodosReservaInternalTest.java
- services/booking-service/src/main/java/co/edu/unbosque/marketplace/booking/service/ReservaService.java

## Pruebas ejecutadas

Comando:

mvn -pl services/catalog-service -am -Dtest=PeriodosReservaInternalTest -Dsurefire.failIfNoSpecifiedTests=false test

Resultado:
- Tests run: 6
- Failures: 0
- Errors: 0
- Skipped: 0
- BUILD SUCCESS

## Alcance del PR
Este PR agrega evidencia documental de la implementación y las pruebas
verificadas. No modifica el código funcional porque la revalidación
ya se encontraba implementada.

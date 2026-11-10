# SCRUM-79 - Implementar la confirmación de reserva

## Historia de Jira
SCRUM-79 - Implementar la confirmación de reserva.

Historia principal: SCRUM-30 - HU-17 Confirmar reserva.

## Objetivo
Verificar la implementación del proceso mediante el cual un huésped
puede confirmar una reserva de alojamiento.

## Funcionalidad verificada
- Endpoint POST /api/v1/reservas.
- Recepción de los datos necesarios para crear una reserva.
- Validación de los datos obligatorios y del orden de las fechas.
- Registro inicial de la reserva en estado PENDIENTE.
- Solicitud de bloqueo del periodo al servicio de catálogo.
- Cambio al estado CONFIRMADA cuando el bloqueo del periodo es exitoso.
- Respuesta HTTP 201 Created al confirmar la reserva.

## Pruebas ejecutadas

Comando utilizado:

mvn -pl services/booking-service -am test

Resultado:
- 15 pruebas ejecutadas.
- 0 fallidas.
- 0 errores.
- 0 omitidas.
- BUILD SUCCESS.

## Archivos relacionados con la implementación
- services/booking-service/src/main/java/co/edu/unbosque/marketplace/booking/web/ReservationLifecycleController.java
- services/booking-service/src/main/java/co/edu/unbosque/marketplace/booking/service/ReservaService.java
- services/booking-service/src/main/java/co/edu/unbosque/marketplace/booking/domain/Reserva.java
- services/booking-service/src/test/java/co/edu/unbosque/marketplace/booking/ReservationLifecycleTest.java

## Alcance de esta subtarea
Esta evidencia corresponde a la implementación y verificación del proceso
principal de confirmación de reservas.

La revalidación de disponibilidad, la idempotencia, el pago simulado
y las pruebas de concurrencia se gestionan en las subtareas SCRUM-80,
SCRUM-81, SCRUM-82 y SCRUM-83, respectivamente.


package co.edu.unbosque.marketplace.booking.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import co.edu.unbosque.marketplace.booking.client.CatalogPeriodClient;
import co.edu.unbosque.marketplace.booking.domain.Reserva;
import co.edu.unbosque.marketplace.booking.dto.CrearReservaRequest;
import co.edu.unbosque.marketplace.booking.dto.ReservaResponse;
import co.edu.unbosque.marketplace.booking.repository.ReservaRepository;

/**
 * Coordina la reserva local con el bloqueo atómico del periodo en Catalog.
 * Al ser bases de datos independientes, el flujo usa compensación (saga) y
 * no afirma una transacción ACID distribuida entre ambos servicios.
 */
@Service
public class ReservaService {

    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

    private final ReservaRepository reservas;
    private final CatalogPeriodClient catalog;
    private final TransactionTemplate transactions;
    private final PagoSimuladoService pagoSimulado;

    public ReservaService(
            ReservaRepository reservas,
            CatalogPeriodClient catalog,
            PlatformTransactionManager transactionManager,
            PagoSimuladoService pagoSimulado
    ) {
        this.reservas = reservas;
        this.catalog = catalog;
        this.transactions = new TransactionTemplate(transactionManager);
        this.pagoSimulado = pagoSimulado;
    }

    public ReservaResponse confirmar(
            CrearReservaRequest request,
            String claveIdempotencia) {

        validar(request);

        if (claveIdempotencia == null
                || claveIdempotencia.isBlank()
                || claveIdempotencia.trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La clave de idempotencia es obligatoria y debe tener máximo 100 caracteres"
            );
        }

        String clave = claveIdempotencia.trim();

        // Si la clave ya existe para el usuario, no se crea otra reserva.
        var existente = reservas.findByUserIdAndClaveIdempotencia(
                request.userId(), clave);

        if (existente.isPresent()) {
            return devolverReservaExistente(existente.get(), request);
        }

        Reserva pendiente;

        try {
            pendiente = transactions.execute(status -> reservas.save(
                    new Reserva(
                            request.userId(),
                            request.alojamientoId(),
                            request.checkIn(),
                            request.checkOut(),
                            "PENDIENTE",
                            clave
                    )
            ));

        } catch (DataIntegrityViolationException ex) {
            // La restricción única evita duplicados ante solicitudes simultáneas.
            var duplicada = reservas.findByUserIdAndClaveIdempotencia(
                    request.userId(), clave);

            if (duplicada.isPresent()) {
                return devolverReservaExistente(duplicada.get(), request);
            }

            throw ex;
        }

        if (pendiente == null || pendiente.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible registrar la solicitud de reserva"
            );
        }

        // Primero se bloquea el periodo en Catalog.
        try {
            catalog.reservar(
                    pendiente.getAlojamientoId(),
                    pendiente.getId(),
                    pendiente.getCheckIn(),
                    pendiente.getCheckOut()
            );

        } catch (HttpClientErrorException.Conflict ex) {
            cambiarEstado(pendiente.getId(), "RECHAZADA");

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El periodo ya no está disponible para esas fechas",
                    ex
            );

        } catch (RestClientException ex) {
            cambiarEstado(pendiente.getId(), "RECHAZADA");

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible bloquear el periodo. Intenta nuevamente",
                    ex
            );
        }

        // Después del bloqueo se procesa el pago simulado.
        boolean pagoAprobado = pagoSimulado.procesarPago(
                request.resultadoPago()
        );

        if (!pagoAprobado) {
            try {
                // Si el pago se rechaza, se libera el periodo reservado.
                catalog.liberar(
                        pendiente.getAlojamientoId(),
                        pendiente.getId()
                );

                cambiarEstado(pendiente.getId(), "RECHAZADA");

            } catch (RestClientException ex) {
                cambiarEstado(
                        pendiente.getId(),
                        "COMPENSACION_PENDIENTE"
                );

                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "El pago fue rechazado, pero no fue posible liberar el periodo",
                        ex
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "El pago fue rechazado. La reserva no fue confirmada"
            );
        }

        // Si el pago es aprobado, continúa la confirmación existente.
        try {
            Reserva confirmada = transactions.execute(status -> {
                Reserva actual = reservas.findById(pendiente.getId())
                        .orElseThrow(() -> new IllegalStateException(
                                "La reserva provisional desapareció"
                        ));

                actual.cambiarEstado(ESTADO_CONFIRMADA);
                return reservas.save(actual);
            });

            return ReservaResponse.desde(confirmada);

        } catch (RuntimeException ex) {
            // Intentar liberar el periodo si Booking no pudo confirmar la reserva.
            boolean liberado = false;

            try {
                catalog.liberar(
                        pendiente.getAlojamientoId(),
                        pendiente.getId()
                );
                liberado = true;

            } catch (RestClientException compensacion) {
                ex.addSuppressed(compensacion);
            }

            if (liberado) {
                cambiarEstado(pendiente.getId(), "RECHAZADA");
            } else {
                cambiarEstado(
                        pendiente.getId(),
                        "COMPENSACION_PENDIENTE"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible finalizar la confirmación de la reserva. "
                            + "La compensación requiere revisión",
                    ex
            );
        }
    }

    private ReservaResponse devolverReservaExistente(
            Reserva existente,
            CrearReservaRequest request) {

        boolean mismosDatos =
                Objects.equals(existente.getAlojamientoId(), request.alojamientoId())
                && Objects.equals(existente.getCheckIn(), request.checkIn())
                && Objects.equals(existente.getCheckOut(), request.checkOut());

        if (!mismosDatos) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La clave de idempotencia ya fue utilizada con otros datos"
            );
        }

        return ReservaResponse.desde(existente);
    }

    public ReservaResponse cancelar(Long reservaId) {
        Reserva datos = transactions.execute(status -> {
            Reserva reserva = reservas.findById(reservaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La reserva no existe"
                    ));

            if ("CANCELADA".equals(reserva.getEstado())) {
                return reserva;
            }

            if (!ESTADO_CONFIRMADA.equals(reserva.getEstado())
                    && !"CANCELACION_PENDIENTE".equals(reserva.getEstado())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "La reserva no se encuentra en un estado que permita cancelarla"
                );
            }

            reserva.cambiarEstado("CANCELACION_PENDIENTE");
            return reservas.save(reserva);
        });

        if ("CANCELADA".equals(datos.getEstado())) {
            return ReservaResponse.desde(datos);
        }

        try {
            catalog.liberar(datos.getAlojamientoId(), datos.getId());

        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "La cancelación quedó pendiente porque no se pudo liberar el periodo",
                    ex
            );
        }

        Reserva cancelada = transactions.execute(status -> {
            Reserva actual = reservas.findById(reservaId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La reserva no existe"
                    ));

            actual.cambiarEstado("CANCELADA");
            return reservas.save(actual);
        });

        return ReservaResponse.desde(cancelada);
    }

    /**
     * Conserva las reservas vigentes del alojamiento y las marca
     * para revisión manual después de un bloqueo administrativo.
     */
    public int marcarReservasFuturasEnRevision(Long alojamientoId) {
        return transactions.execute(status -> {
            var reservasVigentes = reservas
                    .findByAlojamientoIdAndEstadoAndCheckOutGreaterThanEqual(
                            alojamientoId,
                            ESTADO_CONFIRMADA,
                            LocalDate.now(ZONA_HORARIA)
                    );

            reservasVigentes.forEach(reserva ->
                    reserva.cambiarEstado("REVISION_MANUAL")
            );

            reservas.saveAll(reservasVigentes);
            return reservasVigentes.size();
        });
    }

    private void cambiarEstado(Long reservaId, String estado) {
        transactions.execute(status -> {
            reservas.findById(reservaId).ifPresent(reserva -> {
                reserva.cambiarEstado(estado);
                reservas.save(reserva);
            });

            return null;
        });
    }

    private void validar(CrearReservaRequest request) {
        if (request == null
                || request.userId() == null
                || request.alojamientoId() == null
                || request.checkIn() == null
                || request.checkOut() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Usuario, alojamiento y fechas son obligatorios"
            );
        }

        if (!request.checkIn().isBefore(request.checkOut())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de entrada debe ser anterior a la fecha de salida"
            );
        }

        if (!"APROBADO".equals(request.resultadoPago())
                && !"RECHAZADO".equals(request.resultadoPago())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El resultado del pago debe ser APROBADO o RECHAZADO"
            );
        }
    }
}

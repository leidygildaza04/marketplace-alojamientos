package co.edu.unbosque.marketplace.booking.service;

import java.time.LocalDate;
import java.time.ZoneId;

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

    public ReservaService(
            ReservaRepository reservas,
            CatalogPeriodClient catalog,
            PlatformTransactionManager transactionManager
    ) {
        this.reservas = reservas;
        this.catalog = catalog;
        this.transactions = new TransactionTemplate(transactionManager);
    }

    public ReservaResponse confirmar(CrearReservaRequest request) {
        validar(request);

        Reserva pendiente = transactions.execute(status -> reservas.save(
                new Reserva(
                        request.userId(),
                        request.alojamientoId(),
                        request.checkIn(),
                        request.checkOut(),
                        "PENDIENTE"
                )
        ));

        if (pendiente == null || pendiente.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible registrar la solicitud de reserva"
            );
        }

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
                // El periodo podría continuar reservado en Catalog.
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
            // Libera únicamente el periodo asociado a esta reserva.
            catalog.liberar(datos.getAlojamientoId(), datos.getId());

        } catch (RestClientException ex) {
            // Se conserva CANCELACION_PENDIENTE para permitir un nuevo intento.
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
    }
}
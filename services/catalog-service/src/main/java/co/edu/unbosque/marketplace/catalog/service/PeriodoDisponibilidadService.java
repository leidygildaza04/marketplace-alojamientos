package co.edu.unbosque.marketplace.catalog.service;

import co.edu.unbosque.marketplace.catalog.client.BookingReservationClient;
import co.edu.unbosque.marketplace.catalog.domain.Alojamiento;
import co.edu.unbosque.marketplace.catalog.domain.PeriodoDisponibilidad;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadRequest;
import co.edu.unbosque.marketplace.catalog.dto.PeriodoDisponibilidadResponse;
import co.edu.unbosque.marketplace.catalog.repository.AlojamientoRepository;
import co.edu.unbosque.marketplace.catalog.repository.PeriodoDisponibilidadRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PeriodoDisponibilidadService {

    private final AlojamientoRepository alojamientos;
    private final PeriodoDisponibilidadRepository periodos;
    private final BookingReservationClient reservas;

    public PeriodoDisponibilidadService(AlojamientoRepository alojamientos,
            PeriodoDisponibilidadRepository periodos, BookingReservationClient reservas) {
        this.alojamientos = alojamientos;
        this.periodos = periodos;
        this.reservas = reservas;
    }

    @Transactional(readOnly = true)
    public List<PeriodoDisponibilidadResponse> listar(Long alojamientoId) {
        verificarAlojamiento(alojamientoId);
        return periodos.findByAlojamientoIdOrderByFechaInicioAsc(alojamientoId)
                .stream().map(PeriodoDisponibilidadResponse::desde).toList();
    }

    public PeriodoDisponibilidadResponse crear(Long alojamientoId, PeriodoDisponibilidadRequest request) {
        Alojamiento alojamiento = verificarAlojamiento(alojamientoId);
        validarFechas(request);
        if (periodos.existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                alojamientoId, request.fechaFin(), request.fechaInicio())) {
            throw conflictoSuperposicion();
        }
        PeriodoDisponibilidad periodo = new PeriodoDisponibilidad(
                alojamiento, request.fechaInicio(), request.fechaFin());
        return PeriodoDisponibilidadResponse.desde(periodos.save(periodo));
    }

    public PeriodoDisponibilidadResponse editar(Long alojamientoId, Long periodoId,
            PeriodoDisponibilidadRequest request) {
        PeriodoDisponibilidad periodo = buscarPeriodo(alojamientoId, periodoId);
        validarPeriodoDisponible(periodo);
        validarFechas(request);
        validarSinReservas(alojamientoId, periodo.getFechaInicio(), periodo.getFechaFin());
        if (!Objects.equals(periodo.getFechaInicio(), request.fechaInicio())
                || !Objects.equals(periodo.getFechaFin(), request.fechaFin())) {
            validarSinReservas(alojamientoId, request.fechaInicio(), request.fechaFin());
        }
        if (periodos.existsByAlojamientoIdAndIdNotAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                alojamientoId, periodoId, request.fechaFin(), request.fechaInicio())) {
            throw conflictoSuperposicion();
        }
        periodo.actualizarFechas(request.fechaInicio(), request.fechaFin());
        return PeriodoDisponibilidadResponse.desde(periodos.save(periodo));
    }

    public void eliminar(Long alojamientoId, Long periodoId) {
        PeriodoDisponibilidad periodo = buscarPeriodo(alojamientoId, periodoId);
        validarPeriodoDisponible(periodo);
        validarSinReservas(alojamientoId, periodo.getFechaInicio(), periodo.getFechaFin());
        periodos.delete(periodo);
    }

    private void validarPeriodoDisponible(PeriodoDisponibilidad periodo) {
        if (periodo.getEstado() == PeriodoDisponibilidad.Estado.RESERVADO
                || periodo.getReservaId() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El periodo está reservado y no puede modificarse ni eliminarse");
        }
    }

    private void validarSinReservas(Long alojamientoId, LocalDate fechaInicio, LocalDate fechaFin) {
        final boolean existe;
        try {
            existe = reservas.existeReservaConfirmada(alojamientoId, fechaInicio, fechaFin);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible verificar las reservas. Intenta nuevamente más tarde.", ex);
        }
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El periodo está asociado a una reserva confirmada y no puede modificarse ni eliminarse");
        }
    }

    private void validarFechas(PeriodoDisponibilidadRequest request) {
        if (request == null || request.fechaInicio() == null || request.fechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y fin son obligatorias");
        }
        if (!request.fechaInicio().isBefore(request.fechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    private ResponseStatusException conflictoSuperposicion() {
        return new ResponseStatusException(HttpStatus.CONFLICT,
                "El periodo se superpone con otro periodo del mismo alojamiento");
    }

    private Alojamiento verificarAlojamiento(Long alojamientoId) {
        return alojamientos.findById(alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El alojamiento no existe"));
    }

    private PeriodoDisponibilidad buscarPeriodo(Long alojamientoId, Long periodoId) {
        verificarAlojamiento(alojamientoId);
        return periodos.findByIdAndAlojamientoId(periodoId, alojamientoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El periodo no existe para este alojamiento"));
    }
}
